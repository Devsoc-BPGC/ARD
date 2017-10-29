package com.macbitsgoa.ard.services;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.macbitsgoa.ard.BuildConfig;
import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.keys.ChatItemKeys;
import com.macbitsgoa.ard.keys.MessageItemKeys;
import com.macbitsgoa.ard.models.ChatsItem;
import com.macbitsgoa.ard.models.MessageItem;
import com.macbitsgoa.ard.types.MessageStatusType;
import com.macbitsgoa.ard.utils.AHC;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import io.realm.Realm;

public class MessagingService extends BaseIntentService {

    private final DatabaseReference messageStatusRef = getRootReference().child(AHC.FDR_CHAT);

    public MessagingService() {
        super(MessagingService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        super.onHandleIntent(intent);
        //TODO: restart service using alarm bc
        AHC.setNextAlarm(this);
        Realm database = Realm.getDefaultInstance();
        DatabaseReference msgRef = getRootReference()
                .child(AHC.FDR_CHAT)
                .child(getUser().getUid())
                .child(ChatItemKeys.PRIVATE_MESSAGES);
        ValueEventListener msgRefVEL = getEventListener();
        msgRef.addValueEventListener(msgRefVEL);
        try {
            Thread.sleep(1000 * 60 * 10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        msgRef.removeEventListener(msgRefVEL);
        database.close();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (getUser() == null) onDestroy();
    }

    public ValueEventListener getEventListener() {
        return new ValueEventListener() {
            Realm database;

            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                database = Realm.getDefaultInstance();

                Queue<DataSnapshot> newChatsQ = new LinkedList<>();
                for (final DataSnapshot childShot : dataSnapshot.getChildren()) {
                    newChatsQ.add(childShot);
                }
                while (!newChatsQ.isEmpty()) {
                    final DataSnapshot newChatDS = newChatsQ.poll();
                    final String senderId = newChatDS
                            .child(ChatItemKeys.SENDER)
                            .child("id").getValue(String.class);
                    final String name = newChatDS
                            .child(ChatItemKeys.SENDER)
                            .child("name").getValue(String.class);
                    final String latest = newChatDS
                            .child(ChatItemKeys.SENDER)
                            .child("latest").getValue(String.class);
                    final String photoUrl = newChatDS
                            .child(ChatItemKeys.SENDER)
                            .child("photoUrl").getValue(String.class);
                    final Date update = newChatDS
                            .child(ChatItemKeys.SENDER)
                            .child("date").getValue(Date.class);


                    if (senderId == null
                            || name == null
                            || latest == null) continue;
                    Log.e("TAG", newChatDS.child(ChatItemKeys.MESSAGE_STATUS)
                            .getChildrenCount() + " ");

                    Queue<DataSnapshot> newMessagesQ = new LinkedList<>();
                    for (final DataSnapshot child : newChatDS
                            .child(ChatItemKeys.MESSAGES)
                            .getChildren()) {
                        newMessagesQ.add(child);
                    }

                    DatabaseReference msgStatusWriteRef;
                    //TODO fix sentMessages value listener
                    msgStatusWriteRef = messageStatusRef
                            .child(senderId)
                            .child(ChatItemKeys.PRIVATE_MESSAGES)
                            .child(getUser().getUid())
                            .child(ChatItemKeys.MESSAGE_STATUS);

                    int newMessageCount = 0;

                    while (!newMessagesQ.isEmpty()) {
                        final DataSnapshot newMessageDS = newMessagesQ.poll();
                        final String messageId = newMessageDS.getKey();
                        final String messageData = newMessageDS.child(MessageItemKeys.DATA).getValue(String.class);
                        final Date messageTime = newMessageDS.child(MessageItemKeys.DATE).getValue(Date.class);

                        if (messageData == null || messageTime == null || messageId == null)
                            continue;

                        MessageItem mi = database.where(MessageItem.class)
                                .equalTo("messageId", messageId)
                                .equalTo("senderId", senderId)
                                .findFirst();
                        database.beginTransaction();
                        if (mi == null) {
                            mi = database.createObject(MessageItem.class, messageId);
                        }
                        mi.setMessageData(messageData);
                        mi.setSenderId(senderId);
                        mi.setMessageTime(messageTime);
                        mi.setMessageRcvdTime(Calendar.getInstance().getTime());
                        mi.setMessageRcvd(true);
                        mi.setMessageStatus(MessageStatusType.MSG_RCVD);
                        database.commitTransaction();

                        newMessageCount++;
                        msgStatusWriteRef
                                .child(messageId)
                                .setValue(MessageStatusType.MSG_RCVD);
                        newChatDS
                                .child(ChatItemKeys.MESSAGES)
                                .child(messageId)
                                .getRef()
                                .removeValue();
                    }

                    Queue<DataSnapshot> messageStatusQ = new LinkedList<>();
                    for (DataSnapshot temp : newChatDS.child(ChatItemKeys.MESSAGE_STATUS).getChildren()) {
                        messageStatusQ.add(temp);
                    }

                    while (!messageStatusQ.isEmpty()) {
                        DataSnapshot messageStatusDS = messageStatusQ.poll();
                        MessageItem mi = database
                                .where(MessageItem.class)
                                .equalTo("messageId", messageStatusDS.getKey())
                                .findFirst();
                        if (mi == null) {
                            Log.e("TAG", "message lost");
                            continue;
                        }
                        database.beginTransaction();
                        mi.setMessageStatus(messageStatusDS.getValue(Integer.class));
                        database.commitTransaction();
                        Log.e("TAG", "removing read status value");
                        newChatDS.child(ChatItemKeys.MESSAGE_STATUS).child(messageStatusDS.getKey()).getRef().removeValue();
                    }

                    ChatsItem ci = database.where(ChatsItem.class)
                            .equalTo("id", senderId)
                            .findFirst();
                    database.beginTransaction();
                    if (ci == null) {
                        ci = database.createObject(ChatsItem.class, senderId);
                    } else {
                        if (update.getTime() >= ci.getUpdate().getTime()) {
                            ci.setLatest(latest);
                            ci.setUpdate(update);
                        }
                    }
                    ci.setName(name);
                    ci.setPhotoUrl(photoUrl);
                    Log.e("TAG", "old value " + ci.getUnreadCount() + " " + newMessageCount );
                    ci.setUnreadCount(ci.getUnreadCount() + newMessageCount);
                    database.commitTransaction();

                    dataSnapshot.child(senderId).child(ChatItemKeys.MESSAGES).getRef().removeValue();
                    dataSnapshot.child(senderId).child(ChatItemKeys.MESSAGE_STATUS).getRef().removeValue();

                    createNotification();
                }
                database.close();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("TAG", databaseError.toString());
                if (database != null && !database.isClosed()) database.close();
            }
        };
    }

    private void createNotification() {
        startService(new Intent(this, NotificationService.class));
    }

    public boolean isForeground(String myPackage) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager.getRunningTasks(1);
        ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
        Log.e("TAG", componentInfo.getPackageName() + " name");
        return componentInfo.getPackageName().equals(myPackage);
    }
}

package com.macbitsgoa.ard.services;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.macbitsgoa.ard.keys.ChatItemKeys;
import com.macbitsgoa.ard.keys.MessageItemKeys;
import com.macbitsgoa.ard.models.ChatsItem;
import com.macbitsgoa.ard.models.MessageItem;
import com.macbitsgoa.ard.types.MessageStatusType;
import com.macbitsgoa.ard.utils.AHC;
import com.macbitsgoa.ard.utils.Actions;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

import io.realm.Realm;

public class MessagingService extends BaseIntentService {

    public static final String TAG = MessagingService.class.getSimpleName();

    private final DatabaseReference messageStatusRef = getRootReference().child(AHC.FDR_CHAT);

    public MessagingService() {
        super(MessagingService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable final Intent intent) {
        super.onHandleIntent(intent);
        Log.d(TAG, "service started");
        //TODO: restart service using alarm bc
        AHC.setNextAlarm(this);
        final Realm database = Realm.getDefaultInstance();
        final DatabaseReference messagesRef = getRootReference()
                .child(AHC.FDR_CHAT)
                .child(getUser().getUid())
                .child(ChatItemKeys.PRIVATE_MESSAGES);
        final ValueEventListener messagesRefVEL = getEventListener();
        messagesRef.addValueEventListener(messagesRefVEL);
        try {
            Thread.sleep(1000 * 60 * 10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        messagesRef.removeEventListener(messagesRefVEL);
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

                final Queue<DataSnapshot> newChatsQ = new LinkedList<>();
                for (final DataSnapshot childShot : dataSnapshot.getChildren()) {
                    newChatsQ.add(childShot);
                }
                while (!newChatsQ.isEmpty()) {
                    final DataSnapshot newChatDS = newChatsQ.poll();
                    final DataSnapshot senderChild = newChatDS.child(ChatItemKeys.SENDER);
                    final String senderId = senderChild
                            .child("id").getValue(String.class);
                    final String name = senderChild
                            .child("name").getValue(String.class);
                    final String latest = senderChild
                            .child("latest").getValue(String.class);
                    final String photoUrl = senderChild
                            .child("photoUrl").getValue(String.class);
                    final Date update = senderChild
                            .child("date").getValue(Date.class);


                    if (senderId == null
                            || name == null
                            || latest == null) continue;

                    final Queue<DataSnapshot> newMessagesQ = new LinkedList<>();
                    for (final DataSnapshot child : newChatDS
                            .child(ChatItemKeys.MESSAGES)
                            .getChildren()) {
                        newMessagesQ.add(child);
                    }

                    final DatabaseReference msgStatusWriteRef;
                    //TODO fix sentMessages value listener
                    msgStatusWriteRef = messageStatusRef
                            .child(senderId)
                            .child(ChatItemKeys.PRIVATE_MESSAGES)
                            .child(getUser().getUid())
                            .child(ChatItemKeys.MESSAGE_STATUS);

                    while (!newMessagesQ.isEmpty()) {
                        final DataSnapshot newMessageDS = newMessagesQ.poll();
                        final String messageId = newMessageDS.getKey();
                        final String messageData = newMessageDS.child(MessageItemKeys.FDR_DATA).getValue(String.class);
                        final Date messageTime = newMessageDS.child(MessageItemKeys.FDR_DATE).getValue(Date.class);

                        if (messageData == null || messageTime == null || messageId == null)
                            continue;

                        MessageItem mi = database.where(MessageItem.class)
                                .equalTo(MessageItemKeys.MESSAGE_ID, messageId)
                                .equalTo(MessageItemKeys.SENDER_ID, senderId)
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

                        msgStatusWriteRef
                                .child(messageId)
                                .setValue(MessageStatusType.MSG_RCVD);
                        newMessageDS.getRef().removeValue();
                        final Intent broadcastIntent = new Intent(Actions.NEW_MESSAGE_ARRIVED);
                        broadcastIntent.putExtra(MessageItemKeys.SENDER_ID, senderId);
                        sendBroadcast(broadcastIntent);
                    }

                    final Queue<DataSnapshot> messageStatusQ = new LinkedList<>();
                    for (final DataSnapshot temp : newChatDS.child(ChatItemKeys.MESSAGE_STATUS).getChildren()) {
                        messageStatusQ.add(temp);
                    }

                    while (!messageStatusQ.isEmpty()) {
                        final DataSnapshot messageStatusDS = messageStatusQ.poll();
                        final MessageItem mi = database
                                .where(MessageItem.class)
                                .equalTo("messageId", messageStatusDS.getKey())
                                .findFirst();
                        if (mi == null) {
                            Log.e(TAG, "Message lost");
                            continue;
                        }
                        database.beginTransaction();
                        mi.setMessageStatus(messageStatusDS.getValue(Integer.class));
                        database.commitTransaction();
                        //Log.e(TAG, "removing read status value");
                        //TODO work on fixing this
                        //newChatDS.child(ChatItemKeys.MESSAGE_STATUS).child(messageStatusDS.getKey()).getRef().removeValue();
                    }

                    final int newMessageCount = database
                            .where(MessageItem.class)
                            .equalTo(MessageItemKeys.SENDER_ID, senderId)
                            .equalTo(MessageItemKeys.MESSAGE_RECEIVED, true)
                            .equalTo(MessageItemKeys.MESSAGE_STATUS, MessageStatusType.MSG_RCVD)
                            .findAll().size();

                    database.beginTransaction();
                    ChatsItem ci = database.where(ChatsItem.class)
                            .equalTo("id", senderId)
                            .findFirst();
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
                    ci.setUnreadCount(newMessageCount);
                    database.commitTransaction();

                    //no new messages from this chat but it is still
                    //lingering in database
                    if (database.where(MessageItem.class)
                            .equalTo(MessageItemKeys.SENDER_ID, senderId)
                            .findAll().isEmpty())
                        database.executeTransaction(r -> {
                            r.where(ChatsItem.class)
                                    .equalTo("id", senderId)
                                    .findFirst()
                                    .deleteFromRealm();
                        });

                    //dataSnapshot.child(senderId).child(ChatItemKeys.MESSAGES).getRef().removeValue();
                    //dataSnapshot.child(senderId).child(ChatItemKeys.MESSAGE_STATUS).getRef().removeValue();

                    createNotification();
                }
                database.close();
            }

            @Override
            public void onCancelled(final DatabaseError databaseError) {
                Log.e(TAG, databaseError.toString());
                if (database != null && !database.isClosed()) database.close();
            }
        };
    }

    private void createNotification() {
        startService(new Intent(this, NotificationService.class));
    }
}

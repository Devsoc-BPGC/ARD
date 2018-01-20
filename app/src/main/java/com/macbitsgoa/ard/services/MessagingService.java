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

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

import io.realm.Realm;

public class MessagingService extends BaseIntentService {

    /**
     * TAG for class.
     */
    public static final String TAG = MessagingService.class.getSimpleName();

    /**
     * Request code for alarm manager.
     */
    public static final int REQUEST_CODE = 107;

    private final DatabaseReference messageStatusRef = getRootReference().child(AHC.FDR_CHAT);


    public MessagingService() {
        super(MessagingService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable final Intent intent) {
        super.onHandleIntent(intent);
        final DatabaseReference messagesRef = getRootReference()
                .child(AHC.FDR_CHAT)
                .child(getUser().getUid())
                .child(ChatItemKeys.PRIVATE_MESSAGES);
        final DatabaseReference sentMessageStatusRef = getRootReference()
                .child(AHC.FDR_CHAT)
                .child(getUser().getUid())
                .child(ChatItemKeys.SENT_STATUS);

        final ValueEventListener messagesRefVEL = getEventListener();
        final ValueEventListener sendListener = getSentMsgStatusVEL();

        messagesRef.addValueEventListener(messagesRefVEL);
        sentMessageStatusRef.addValueEventListener(sendListener);

        try {
            Thread.sleep(1000 * 60 * 5);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        } finally {
            AHC.setNextAlarm(this, MessagingService.class, REQUEST_CODE, 0);
            messagesRef.removeEventListener(messagesRefVEL);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (getUser() == null) onDestroy();
    }

    /**
     * Method to get messages event listener.
     *
     * @return Messages event listener.
     */
    public ValueEventListener getEventListener() {
        return new ValueEventListener() {
            private Realm database;

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
                            .child(ChatItemKeys.FDR_ID).getValue(String.class);
                    final String name = senderChild
                            .child(ChatItemKeys.FDR_NAME).getValue(String.class);
                    final String latest = senderChild
                            .child(ChatItemKeys.FDR_LATEST).getValue(String.class);
                    final String photoUrl = senderChild
                            .child(ChatItemKeys.FDR_PHOTO_URL).getValue(String.class);
                    final Date update = senderChild
                            .child(ChatItemKeys.FDR_DATE).getValue(Date.class);


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
                        final Intent broadcastIntent = new Intent(ChatItemKeys.NEW_MESSAGE_ARRIVED);
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
                                .equalTo(MessageItemKeys.MESSAGE_ID, messageStatusDS.getKey())
                                .findFirst();
                        if (mi == null) {
                            Log.e(TAG, "Message lost");
                            //If message is lost, we can safely remove this value from firebase
                            messageStatusDS.getRef().removeValue();
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
                            .equalTo(ChatItemKeys.DB_ID, senderId)
                            .findFirst();
                    if (ci == null) {
                        ci = database.createObject(ChatsItem.class, senderId);
                    } else {
                        //TODO something wrong here
                        /*
                        01-01 12:17:55.357 25445-25445/com.macbitsgoa.ard.debug:BackgroundServices E/AndroidRuntime: FATAL EXCEPTION: main
                        Process: com.macbitsgoa.ard.debug:BackgroundServices, PID: 25445
                        java.lang.NullPointerException: Attempt to invoke virtual method 'long java.util.Date.getTime()' on a null object reference
                        at com.macbitsgoa.ard.services.MessagingService$1.onDataChange(MessagingService.java:204)
                        */
                        if (false && update.getTime() >= ci.getUpdate().getTime()) {
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
                                    .equalTo(ChatItemKeys.DB_ID, senderId)
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
                Log.e(TAG, "Could not get data " + databaseError.toString());
                if (database != null && !database.isClosed()) {
                    if (database.isInTransaction()) {
                        database.cancelTransaction();
                    }
                    database.close();
                }
            }
        };
    }

    /**
     * Handle sent messages status.
     *
     * @return ValueEventListener object for handling ref updates.
     */
    public ValueEventListener getSentMsgStatusVEL() {
        return new ValueEventListener() {
            private Realm database;

            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                database = Realm.getDefaultInstance();
                for (final DataSnapshot child : dataSnapshot.getChildren()) {
                    final String key = child.getKey();
                    final MessageItem mi = database
                            .where(MessageItem.class)
                            .equalTo(MessageItemKeys.MESSAGE_ID, key)
                            .findFirst();
                    if (mi != null) {
                        database.beginTransaction();
                        if (mi.getMessageStatus() == MessageStatusType.MSG_WAIT) {
                            Log.d(TAG, "msg status updated");
                            mi.setMessageStatus(MessageStatusType.MSG_SENT);
                            child.getRef().removeValue();
                        }
                        database.commitTransaction();
                    } else {
                        Log.e(TAG, "Sent message not in database");
                    }
                }
                database.close();
            }

            @Override
            public void onCancelled(final DatabaseError databaseError) {
                if (database != null && !database.isClosed()) database.close();
            }
        };
    }

    private void createNotification() {
        startService(new Intent(this, NotificationService.class));
    }
}

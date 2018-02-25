package com.macbitsgoa.ard.services;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.macbitsgoa.ard.keys.ChatItemKeys;
import com.macbitsgoa.ard.keys.DocumentItemKeys;
import com.macbitsgoa.ard.keys.MessageItemKeys;
import com.macbitsgoa.ard.models.ChatsItem;
import com.macbitsgoa.ard.models.DocumentItem;
import com.macbitsgoa.ard.models.MessageItem;
import com.macbitsgoa.ard.types.MessageStatusType;
import com.macbitsgoa.ard.utils.AHC;

import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmList;

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
        if (getUser() == null) return;
        final DatabaseReference pmReference = getRootReference()
                .child(AHC.FDR_CHAT)
                .child(getUser().getUid())
                .child(ChatItemKeys.PRIVATE_MESSAGES);
        final DatabaseReference sentMessagesStatusRef = getRootReference()
                .child(AHC.FDR_CHAT)
                .child(getUser().getUid())
                .child(ChatItemKeys.SENT_STATUS);

        final ValueEventListener pmRefListener = getEventListener();
        final ValueEventListener sentMessagesStatusListener = getSentMsgStatusVEL();

        pmReference.addValueEventListener(pmRefListener);
        sentMessagesStatusRef.addValueEventListener(sentMessagesStatusListener);

        try {
            Thread.sleep(1000 * 60);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        } finally {
            AHC.setNextAlarm(this, MessagingService.class, REQUEST_CODE, 3);
            pmReference.removeEventListener(pmRefListener);
        }
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

                for (final DataSnapshot senderSnapshot : dataSnapshot.getChildren()) {
                    final DataSnapshot senderInfoSnapshot = senderSnapshot.child(ChatItemKeys.SENDER);
                    final String senderId = senderInfoSnapshot
                            .child(ChatItemKeys.FDR_ID).getValue(String.class);
                    final String senderName = senderInfoSnapshot
                            .child(ChatItemKeys.FDR_NAME).getValue(String.class);
                    final String latestFromSender = senderInfoSnapshot
                            .child(ChatItemKeys.FDR_LATEST).getValue(String.class);
                    final String senderPhotoUrl = senderInfoSnapshot
                            .child(ChatItemKeys.FDR_PHOTO_URL).getValue(String.class);
                    final Date senderLatestUpdate = senderInfoSnapshot
                            .child(ChatItemKeys.FDR_DATE).getValue(Date.class);

                    if (senderId == null) continue;

                    final DatabaseReference msgStatusWriteRef;
                    //TODO fix sentMessages value listener
                    msgStatusWriteRef = messageStatusRef
                            .child(senderId)
                            .child(ChatItemKeys.PRIVATE_MESSAGES)
                            .child(getUser().getUid())
                            .child(ChatItemKeys.MESSAGE_STATUS);

                    for (final DataSnapshot messageSnapshot : senderSnapshot
                            .child(ChatItemKeys.FDR_MESSAGES).getChildren()) {
                        final String messageId = messageSnapshot.getKey();
                        final String messageData = messageSnapshot.child(MessageItemKeys.FDR_DATA)
                                .getValue(String.class);
                        final Date messageTime = messageSnapshot.child(MessageItemKeys.FDR_DATE)
                                .getValue(Date.class);

                        if (messageData == null || messageTime == null || messageId == null)
                            continue;

                        RealmList<DocumentItem> documentItems = new RealmList<>();
                        if (messageSnapshot.hasChild(MessageItemKeys.FDR_DOCUMENTS)) {
                            DataSnapshot documentsSnapshot = messageSnapshot.child(MessageItemKeys.FDR_DOCUMENTS);
                            for (DataSnapshot documentSnapshot : documentsSnapshot.getChildren()) {
                                DocumentItem di = new DocumentItem();
                                di.setId(documentSnapshot.child(DocumentItemKeys.DOCUMENT_ID).getValue(String.class));
                                di.setRemoteUrl(documentSnapshot.child(DocumentItemKeys.REMOTE_URL).getValue(String.class));
                                di.setMimeType(documentSnapshot.child(DocumentItemKeys.MIME_TYPE).getValue(String.class));
                                di.setRemoteThumbnailUrl(documentSnapshot.child(DocumentItemKeys.REMOTE_THUMBNAIL_URL).getValue(String.class));
                                di.setLocalUri(di.getRemoteUrl());
                                //TODO set local thumbnail and uri
                                //TODO download file service required
                                documentItems.add(di);
                            }
                        }

                        MessageItem mi = new MessageItem();
                        mi.setMessageId(messageId);
                        mi.setMessageData(messageData);
                        mi.setOtherUserId(senderId);
                        mi.setMessageTime(messageTime);
                        mi.setMessageRcvdTime(Calendar.getInstance().getTime());
                        mi.setMessageRcvd(true);
                        mi.setMessageStatus(MessageStatusType.MSG_RCVD);
                        mi.setDocuments(documentItems);
                        database.executeTransaction(r -> {
                            r.insertOrUpdate(mi);
                        });
                        msgStatusWriteRef
                                .child(messageId)
                                .setValue(MessageStatusType.MSG_RCVD);
                        messageSnapshot.getRef().removeValue();
                    }
                    final Intent broadcastIntent = new Intent(ChatItemKeys.NEW_MESSAGE_ARRIVED);
                    broadcastIntent.putExtra(MessageItemKeys.OTHER_USER_ID, senderId);
                    sendBroadcast(broadcastIntent);

                    //Our message status from other user
                    for (final DataSnapshot ourMsgStatusSnapshot : senderSnapshot
                            .child(ChatItemKeys.MESSAGE_STATUS).getChildren()) {
                        final MessageItem mi = database
                                .where(MessageItem.class)
                                .equalTo(MessageItemKeys.MESSAGE_ID, ourMsgStatusSnapshot.getKey())
                                .findFirst();
                        ourMsgStatusSnapshot.getRef().removeValue();
                        if (mi == null) {
                            Log.e(TAG, "Message lost");
                            //If message is lost, we can safely remove this value from firebase
                            continue;
                        }
                        database.beginTransaction();
                        mi.setMessageStatus(ourMsgStatusSnapshot.getValue(Integer.class));
                        database.commitTransaction();
                    }

                    final int newMessageCount = database
                            .where(MessageItem.class)
                            .equalTo(MessageItemKeys.OTHER_USER_ID, senderId)
                            .equalTo(MessageItemKeys.MESSAGE_RECEIVED, true)
                            .equalTo(MessageItemKeys.MESSAGE_STATUS, MessageStatusType.MSG_RCVD)
                            .findAll().size();

                    database.beginTransaction();
                    ChatsItem ci = database.where(ChatsItem.class)
                            .equalTo(ChatItemKeys.DB_ID, senderId)
                            .findFirst();
                    if (ci == null) {
                        ci = database.createObject(ChatsItem.class, senderId);
                    }
                    if (senderLatestUpdate.getTime() > ci.getUpdate().getTime()) {
                        ci.setLatest(latestFromSender);
                        ci.setUpdate(senderLatestUpdate);
                    }
                    ci.setName(senderName);
                    ci.setPhotoUrl(senderPhotoUrl);
                    ci.setUnreadCount(newMessageCount);
                    database.commitTransaction();

                    //no new messages from this chat but it is still
                    //lingering in database
                    if (database.where(MessageItem.class)
                            .equalTo(MessageItemKeys.OTHER_USER_ID, senderId)
                            .findAll().isEmpty())
                        database.executeTransaction(r -> {
                            r.where(ChatsItem.class)
                                    .equalTo(ChatItemKeys.DB_ID, senderId)
                                    .findFirst()
                                    .deleteFromRealm();
                        });

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
                for (final DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    final String key = messageSnapshot.getKey();
                    final MessageItem mi = database
                            .where(MessageItem.class)
                            .equalTo(MessageItemKeys.MESSAGE_ID, key)
                            .findFirst();
                    if (mi != null) {
                        database.beginTransaction();
                        mi.setMessageStatus(MessageStatusType.MSG_SENT);
                        database.commitTransaction();
                    } else {
                        AHC.logd(TAG, "Sent message not in database");
                    }
                    messageSnapshot.getRef().removeValue();
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

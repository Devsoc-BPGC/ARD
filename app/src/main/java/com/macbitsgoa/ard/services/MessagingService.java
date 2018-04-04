package com.macbitsgoa.ard.services;

import android.content.Intent;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
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

public class MessagingService extends BaseJobService {

    /**
     * TAG for class.
     */
    public static final String TAG = MessagingService.class.getSimpleName();

    boolean continueJob1 = true;
    boolean continueJob2 = true;

    JobParameters job;
    Thread serverThread;

    @Override
    public boolean onStartJob(JobParameters job) {
        this.job = job;
        serverThread = new Thread() {
            @Override
            public void run() {
                AHC.logd(TAG, "Thread has started");
                AHC.logd(TAG, "checking for nun user");
                if (getUser() == null) {
                    AHC.logd(TAG, "User was null. Exiting job");
                    jobFinished(job, false);
                }

                AHC.logd(TAG, "Started " + TAG + " job!");

                //Send message status reference for this user
                getRootReference()
                        .child(AHC.FDR_CHAT)
                        .child(getUser().getUid())
                        .child(ChatItemKeys.SENT_STATUS)
                        .addListenerForSingleValueEvent(getSentMsgStatusVEL());

                //Private messages reference for this user
                getRootReference()
                        .child(AHC.FDR_CHAT)
                        .child(getUser().getUid())
                        .child(ChatItemKeys.PRIVATE_MESSAGES)
                        .addListenerForSingleValueEvent(getEventListener());
            }
        };
        serverThread.start();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
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
                AHC.logd(TAG, "Received datasnapshot for messages");
                database = Realm.getDefaultInstance();
                AHC.logd(TAG, "snapshot has " + dataSnapshot.getChildrenCount() + " children");
                for (final DataSnapshot senderSnapshot : dataSnapshot.getChildren()) {
                    final DataSnapshot senderInfoSnapshot = senderSnapshot
                            .child(ChatItemKeys.SENDER);
                    final String senderId = senderSnapshot.getKey();
                    final String senderName = senderInfoSnapshot
                            .child(ChatItemKeys.FDR_NAME).getValue(String.class);
                    final String latestFromSender = senderInfoSnapshot
                            .child(ChatItemKeys.FDR_LATEST).getValue(String.class);
                    final String senderPhotoUrl = senderInfoSnapshot
                            .child(ChatItemKeys.FDR_PHOTO_URL).getValue(String.class);
                    Date senderLatestUpdate = null;
                    try {
                        senderLatestUpdate = senderInfoSnapshot
                                .child(ChatItemKeys.FDR_DATE).getValue(Date.class);
                    } catch (NullPointerException e) {
                        AHC.logd(TAG, "Date was null");
                    }
                    if (senderLatestUpdate == null)
                        senderLatestUpdate = Calendar.getInstance().getTime();

                    //TODO fix sentMessages value listener
                    //Parse messages from sender
                    for (final DataSnapshot messageSnapshot : senderSnapshot
                            .child(ChatItemKeys.FDR_MESSAGES).getChildren()) {
                        final String messageId = messageSnapshot.getKey();
                        final String messageData = messageSnapshot.child(MessageItemKeys.FDR_DATA)
                                .getValue(String.class);
                        final Date messageTime = messageSnapshot.child(MessageItemKeys.FDR_DATE)
                                .getValue(Date.class);

                        if (messageData == null || messageTime == null || messageId == null) {
                            continue;
                        }

                        final DatabaseReference rcvdMessageStatusRef = getRootReference()
                                .child(AHC.FDR_CHAT)
                                .child(senderId)
                                .child(ChatItemKeys.PRIVATE_MESSAGES)
                                .child(getUser().getUid())
                                .child(ChatItemKeys.MESSAGE_STATUS);

                        RealmList<DocumentItem> documentItems = new RealmList<>();
                        if (messageSnapshot.hasChild(MessageItemKeys.FDR_DOCUMENTS)) {
                            DataSnapshot documentsSnapshot = messageSnapshot
                                    .child(MessageItemKeys.FDR_DOCUMENTS);
                            for (DataSnapshot documentSnapshot : documentsSnapshot.getChildren()) {
                                DocumentItem di = new DocumentItem();
                                di.setId(documentSnapshot
                                        .child(DocumentItemKeys.DOCUMENT_ID)
                                        .getValue(String.class));
                                di.setRemoteUrl(documentSnapshot
                                        .child(DocumentItemKeys.REMOTE_URL)
                                        .getValue(String.class));
                                di.setMimeType(documentSnapshot
                                        .child(DocumentItemKeys.MIME_TYPE)
                                        .getValue(String.class));
                                di.setRemoteThumbnailUrl(documentSnapshot
                                        .child(DocumentItemKeys.REMOTE_THUMBNAIL_URL)
                                        .getValue(String.class));
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

                        database.executeTransaction(r -> r.insertOrUpdate(mi));

                        rcvdMessageStatusRef
                                .child(messageId)
                                .setValue(MessageStatusType.MSG_RCVD);
                        messageSnapshot.getRef().removeValue();
                    }

                    final Intent broadcastIntent = new Intent(ChatItemKeys.NEW_MESSAGE_ARRIVED);
                    broadcastIntent.putExtra(MessageItemKeys.OTHER_USER_ID, senderId);
                    sendBroadcast(broadcastIntent);

                    //Our message status from other user
                    AHC.logd(TAG, "Other user has updated their rcvd/read status");
                    AHC.logd(TAG, "Message rcvd/read status available for " + senderSnapshot.child(ChatItemKeys.MESSAGE_STATUS).getChildrenCount());
                    for (final DataSnapshot ourMsgStatusSnapshot : senderSnapshot
                            .child(ChatItemKeys.MESSAGE_STATUS).getChildren()) {
                        final MessageItem mi = database
                                .where(MessageItem.class)
                                .equalTo(MessageItemKeys.MESSAGE_ID, ourMsgStatusSnapshot.getKey())
                                .findFirst();
                        if (mi == null) {
                            AHC.logd(TAG, "Message lost");
                        } else {
                            AHC.logd(TAG, "Update message read ack status of other user for " + mi.getMessageId());
                            database.beginTransaction();
                            mi.setMessageStatus(ourMsgStatusSnapshot.getValue(Integer.class));
                            database.commitTransaction();
                        }
                        ourMsgStatusSnapshot.getRef().removeValue();
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
                    //TODO change to last known message of these users
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
                            .findAll().isEmpty()) {
                        database.executeTransaction(r -> r.where(ChatsItem.class)
                                .equalTo(ChatItemKeys.DB_ID, senderId)
                                .findFirst()
                                .deleteFromRealm());
                    }
                    AHC.startService(MessagingService.this,
                            NotificationService.class, NotificationService.TAG);
                }
                database.close();
                continueJob1 = false;
                AHC.logd(TAG, "continue1 is false, continue2 is " + continueJob2);
                if (!continueJob1 && !continueJob2) {
                    jobFinished(job, false);
                }
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
                continueJob1 = false;
                AHC.logd(TAG, "continue1 is false, continue2 is " + continueJob2);
                if (!continueJob1 && !continueJob2) jobFinished(job, false);
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
                AHC.logd(TAG, "sent messages status updates");
                database = Realm.getDefaultInstance();
                AHC.logd(TAG, "Total " + dataSnapshot.getChildrenCount() + " status updates for sent messages");
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
                continueJob2 = false;
                AHC.logd(TAG, "continue2 is false, continue1 is " + continueJob1);
                if (!continueJob1 && !continueJob2) jobFinished(job, false);
            }

            @Override
            public void onCancelled(final DatabaseError databaseError) {
                if (database != null && !database.isClosed()) {
                    if (database.isInTransaction()) {
                        database.cancelTransaction();
                    }
                    database.close();
                }
                continueJob2 = false;
                AHC.logd(TAG, "continue2 is false, continue1 is " + continueJob1);
                if (!continueJob1 && !continueJob2) jobFinished(job, false);
            }
        };
    }
}

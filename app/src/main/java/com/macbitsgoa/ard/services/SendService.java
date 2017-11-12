package com.macbitsgoa.ard.services;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.macbitsgoa.ard.keys.ChatItemKeys;
import com.macbitsgoa.ard.models.ChatsItem;
import com.macbitsgoa.ard.models.MessageItem;
import com.macbitsgoa.ard.types.MessageStatusType;
import com.macbitsgoa.ard.utils.AHC;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;

/**
 * Service to send information to Firebase.
 *
 * @author Vikramaditya Kukreja
 */

public class SendService extends BaseIntentService {

    public static final String TAG = SendService.class.getSimpleName();

    private ValueEventListener sendListener;

    public SendService() {
        super(SendService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        super.onHandleIntent(intent);
        if (intent == null) return;
        final String messageData = intent.getStringExtra("messageData");
        final String receiverId = intent.getStringExtra("receiverId");
        if (messageData == null || receiverId == null) return;

        notifyStatus(receiverId);


        final Realm database = Realm.getDefaultInstance();
        final DatabaseReference sendMessageRef = getRootReference()
                .child(AHC.FDR_CHAT)
                .child(receiverId)
                .child(ChatItemKeys.PRIVATE_MESSAGES)
                .child(getUser().getUid());
        final DatabaseReference sentMessageStatusRef = getRootReference()
                .child(AHC.FDR_CHAT)
                .child(getUser().getUid())
                .child(ChatItemKeys.SENT_STATUS);

        //Get current system time and include this is in message id;
        final Date messageTime = Calendar.getInstance().getTime();
        final String messageId = "" + messageTime.getTime()
                + messageTime.hashCode()
                + messageData.hashCode();
        Log.e(TAG, "Sending message with id " + messageId);
        final String latestMessage = messageData.substring(0, messageData.length() % 50);

        //Add new message
        final Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("data", messageData);
        messageMap.put("date", messageTime);

        //Update latest sender information
        final Map<String, Object> senderMap = new HashMap<>();
        senderMap.put("id", getUser().getUid());
        senderMap.put("name", getUser().getDisplayName());
        senderMap.put("latest", latestMessage);
        senderMap.put("photoUrl", getUser().getPhotoUrl().toString());
        senderMap.put("date", messageTime);

        final Map<String, Integer> sentStatusMap = new HashMap<>();
        sentStatusMap.put(messageId, MessageStatusType.MSG_SENT);

        sendMessageRef.child(ChatItemKeys.MESSAGES).child(messageId).setValue(messageMap);
        sendMessageRef.child(ChatItemKeys.SENDER).setValue(senderMap);
        sentMessageStatusRef.setValue(sentStatusMap);

        database.executeTransaction(r -> {
            final MessageItem mi = r.createObject(MessageItem.class, messageId);
            mi.setMessageRcvd(false);
            mi.setMessageTime(messageTime);
            mi.setMessageRcvdTime(Calendar.getInstance().getTime());
            mi.setMessageData(messageData);
            mi.setSenderId(receiverId);
            mi.setMessageStatus(MessageStatusType.MSG_WAIT);
        });

        database.executeTransaction(r -> {
            if (receiverId == null) return;
            final ChatsItem ci = r.where(ChatsItem.class).equalTo("id", receiverId).findFirst();
            if (ci != null) {
                ci.setLatest(latestMessage);
                ci.setUpdate(messageTime);
            }
        });
        sendListener = getSentMsgStatusVEL();
        sentMessageStatusRef.addValueEventListener(sendListener);

        Log.e(TAG, "Calling notify service");
        notifyStatus(receiverId);
        //sentMessageStatusRef.removeEventListener(sendListener);
        database.close();
    }

    /**
     * Start {@link NotifyService} to update read status for received messages which have been
     * read but not updated.
     *
     * @param receiverId User id of receiver.
     */
    private void notifyStatus(final String receiverId) {
        //Update any message read status of this receiver user
        final Intent notifyIntent = new Intent(this, NotifyService.class);
        notifyIntent.putExtra("receiverId", receiverId);
        startService(notifyIntent);
    }

    /**
     * Handle onDataSnapshot for {@link #sendListener}.
     *
     * @return ValueEventListener object for hadnling ref updates.
     */
    public ValueEventListener getSentMsgStatusVEL() {
        return new ValueEventListener() {
            Realm database;

            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                database = Realm.getDefaultInstance();
                for (final DataSnapshot child : dataSnapshot.getChildren()) {
                    final String key = child.getKey();
                    final MessageItem mi = database
                            .where(MessageItem.class)
                            .equalTo("messageId", key)
                            .lessThanOrEqualTo("messageStatus", MessageStatusType.MSG_SENT)
                            .findFirst();
                    if (mi != null) {
                        database.beginTransaction();
                        mi.setMessageStatus(MessageStatusType.MSG_SENT);
                        database.commitTransaction();
                    }
                    //TODO still buggy
                    //child.getRef().removeValue();
                }
                database.close();
            }

            @Override
            public void onCancelled(final DatabaseError databaseError) {
                if (database != null && !database.isClosed()) database.close();
            }
        };
    }
}

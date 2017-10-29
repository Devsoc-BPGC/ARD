package com.macbitsgoa.ard.services;

import android.content.Intent;

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

import javax.annotation.Nonnull;

import io.realm.Realm;

/**
 * Service to send information to Firebase.
 *
 * @author Vikramaditya Kukreja
 */

public class SendService extends BaseIntentService {

    ValueEventListener sendListener;

    public SendService() {
        super(SendService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nonnull Intent intent) {
        super.onHandleIntent(intent);
        final String messageData = intent.getStringExtra("messageData");
        final String receiverId = intent.getStringExtra("receiverId");
        if (messageData == null || receiverId == null) return;

        Realm database = Realm.getDefaultInstance();
        DatabaseReference sendMessageRef = getRootReference()
                .child(AHC.FDR_CHAT)
                .child(receiverId)
                .child(ChatItemKeys.PRIVATE_MESSAGES)
                .child(getUser().getUid());
        DatabaseReference sentMessageStatusRef = getRootReference()
                .child(AHC.FDR_CHAT)
                .child(getUser().getUid())
                .child(ChatItemKeys.SENT_STATUS);

        //Get current system time and include this is in message id;
        final Date messageTime = Calendar.getInstance().getTime();
        final String messageId = "" + messageTime.getTime()
                + messageTime.hashCode()
                + messageData.hashCode();
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
            final MessageItem mi = database.createObject(MessageItem.class, messageId);
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
        sentMessageStatusRef.addListenerForSingleValueEvent(sendListener);

        //sentMessageStatusRef.removeEventListener(sendListener);
        database.close();
    }

    public ValueEventListener getSentMsgStatusVEL() {
        return new ValueEventListener() {
            Realm database;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                database = Realm.getDefaultInstance();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String key = child.getKey();
                    MessageItem mi = database
                            .where(MessageItem.class)
                            .equalTo("messageId", key)
                            .lessThan("messageStatus", MessageStatusType.MSG_SENT)
                            .findFirst();
                    if (mi != null) {
                        database.beginTransaction();
                        mi.setMessageStatus(MessageStatusType.MSG_SENT);
                        database.commitTransaction();
                    }
                    child.getRef().removeValue();
                }
                database.close();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (database != null && !database.isClosed()) database.close();
            }
        };
    }

    public ValueEventListener getSentStatusVEL() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }
}

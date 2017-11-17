package com.macbitsgoa.ard.services;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.macbitsgoa.ard.keys.ChatItemKeys;
import com.macbitsgoa.ard.keys.MessageItemKeys;
import com.macbitsgoa.ard.models.ChatsItem;
import com.macbitsgoa.ard.models.MessageItem;
import com.macbitsgoa.ard.types.MessageStatusType;
import com.macbitsgoa.ard.utils.AHC;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * Service to send information to Firebase. This service's only job is to send messages to Firebase
 * It also calls the {@link NotifyService} for old messages. Sent messages listener is in
 * {@link MessagingService}.
 *
 * @author Vikramaditya Kukreja
 */

public class SendService extends BaseIntentService {

    /**
     * TAG for this class.
     */
    public static final String TAG = SendService.class.getSimpleName();

    /**
     * Realm instance.
     */
    private Realm database;

    public SendService() {
        super(SendService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        super.onHandleIntent(intent);
        database = Realm.getDefaultInstance();
        if (intent == null) {
            Log.d(TAG, "Null intent was passed, sending all unsent messages");
            sendAll();
        } else {
            Log.e(TAG, intent.toString());
            final String messageData = intent.getStringExtra(MessageItemKeys.MESSAGE_DATA);
            final String receiverId = intent.getStringExtra(MessageItemKeys.RECEIVER_ID);
            if (messageData == null || receiverId == null) {
                Log.e(TAG, "No extras sent in intent");
                return;
            }
            sendMessage(messageData, receiverId);
        }
        database.close();
    }

    /**
     * If {@link #onHandleIntent(Intent)} has {@code null} intent, then automatically check for
     * any unsent messages.
     */
    private void sendAll() {
        final RealmList<MessageItem> notSentMessages = new RealmList<>();
        notSentMessages.addAll(database.where(MessageItem.class)
                .equalTo(MessageItemKeys.MESSAGE_STATUS, MessageStatusType.MSG_WAIT)
                .findAll());
        for (int i = 0; i < notSentMessages.size(); i++) {
            sendMessage(notSentMessages.get(i));
        }
    }

    /**
     * Method to create a temporary {@link MessageItem} object.
     *
     * @param messageData Message Data to send.
     * @param receiverId  Other user's unique id.
     */
    private void sendMessage(final String messageData, final String receiverId) {

        //Get current system time and include this is in message id
        final Date messageTime = Calendar.getInstance().getTime();
        final String messageId = "" + messageTime.getTime()
                + messageTime.hashCode()
                + messageData.hashCode();

        final MessageItem mi = new MessageItem();
        mi.setMessageId(messageId);
        mi.setMessageRcvd(false);
        mi.setMessageTime(messageTime);
        mi.setMessageRcvdTime(Calendar.getInstance().getTime());
        mi.setMessageData(messageData);
        mi.setSenderId(receiverId);
        mi.setMessageStatus(MessageStatusType.MSG_WAIT);

        sendMessage(mi);
    }

    /**
     * Sends a single message using information from the {@link MessageItem} object.
     *
     * @param mItem message item object to extact information from.
     */
    private void sendMessage(final MessageItem mItem) {
        final String messageId = mItem.getMessageId();
        final String messageData = mItem.getMessageData();
        final String receiverId = mItem.getSenderId();
        final String latestMessage = messageData.substring(0, messageData.length() % 50);
        final Date messageTime = mItem.getMessageTime();

        final DatabaseReference sendMessageRef = getRootReference()
                .child(AHC.FDR_CHAT)
                .child(mItem.getSenderId())
                .child(ChatItemKeys.PRIVATE_MESSAGES)
                .child(getUser().getUid());
        final DatabaseReference sentMessageStatusRef = getRootReference()
                .child(AHC.FDR_CHAT)
                .child(getUser().getUid())
                .child(ChatItemKeys.SENT_STATUS);

        Log.e(TAG, "Sending message with id " + mItem.getMessageId());

        //Add new message
        final Map<String, Object> messageMap = new HashMap<>();
        messageMap.put(MessageItemKeys.FDR_DATA, messageData);
        messageMap.put(MessageItemKeys.FDR_DATE, messageTime);

        //Update latest sender information
        final Map<String, Object> senderMap = new HashMap<>();
        senderMap.put(ChatItemKeys.FDR_ID, getUser().getUid());
        senderMap.put(ChatItemKeys.FDR_NAME, getUser().getDisplayName());
        senderMap.put(ChatItemKeys.FDR_LATEST, latestMessage);
        senderMap.put(ChatItemKeys.FDR_PHOTO_URL, getUser().getPhotoUrl().toString());
        senderMap.put(ChatItemKeys.FDR_DATE, messageTime);

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
            final ChatsItem ci = r.where(ChatsItem.class)
                    .equalTo(ChatItemKeys.DB_ID, receiverId).findFirst();
            if (ci != null) {
                ci.setLatest(latestMessage);
                ci.setUpdate(messageTime);
            }
        });

        Log.e(TAG, "Calling notify service");
        notifyStatus(receiverId);
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
        notifyIntent.putExtra(MessageItemKeys.RECEIVER_ID, receiverId);
        startService(notifyIntent);
    }

}

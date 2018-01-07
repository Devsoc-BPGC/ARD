package com.macbitsgoa.ard.services;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.macbitsgoa.ard.keys.ChatItemKeys;
import com.macbitsgoa.ard.keys.MessageItemKeys;
import com.macbitsgoa.ard.models.ChatsItem;
import com.macbitsgoa.ard.models.MessageItem;
import com.macbitsgoa.ard.types.MessageStatusType;
import com.macbitsgoa.ard.types.MessageType;
import com.macbitsgoa.ard.utils.AHC;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * Service to save documents on cloud and use the generated links as message data.
 */

public class SendDocumentService extends BaseIntentService {

    public static final String TAG = SendDocumentService.class.getSimpleName();

    public SendDocumentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        super.onHandleIntent(intent);
        if (intent == null || intent.getData() == null || !intent.hasExtra(MessageItemKeys.RECEIVER_ID)) {
            Log.d(TAG, "Null intent was passed, sending all unsent messages");
            sendAll();
        } else {
            Log.i(TAG, "Sending document " + intent.toString());
            sendDoucment(intent.getData(), intent.getStringExtra(MessageItemKeys.RECEIVER_ID));
        }
    }

    private String getMessageId(final String messageData) {
        final Date messageTime = Calendar.getInstance().getTime();
        return "" + messageTime.getTime()
                + messageTime.hashCode()
                + messageData.hashCode();
    }

    private void sendDoucment(final Uri data, final String receiverId) {
        final String messageId = getMessageId(data.getPath());
        StorageReference sRef = getStorageRef().child(getUser().getUid()).child(receiverId).child(messageId);
        UploadTask uploadTask = sRef.putFile(data);
        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(exception -> {
            // Handle unsuccessful uploads
        }).addOnSuccessListener(taskSnapshot -> {
            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
            Uri downloadUrl = taskSnapshot.getDownloadUrl();
            sendDocument(downloadUrl.toString(), data, receiverId);
        });
    }

    /**
     * Method to create a temporary {@link MessageItem} object.
     *
     * @param firebaseUrl url in firebase.
     * @param localUri    Uri of local file.
     * @param receiverId  Other user's unique id.
     */
    private void sendDocument(final String firebaseUrl, final Uri localUri, final String receiverId) {

        //Get current system time and include this is in message id
        final Date messageTime = Calendar.getInstance().getTime();
        final String messageId = getMessageId(firebaseUrl);

        final MessageItem mi = new MessageItem();
        mi.setMessageId(messageId);
        mi.setMessageRcvd(false);
        mi.setMessageTime(messageTime);
        mi.setMessageRcvdTime(Calendar.getInstance().getTime());
        mi.setSenderId(receiverId);
        mi.setMessageStatus(MessageStatusType.MSG_WAIT);
        mi.setMessageData(firebaseUrl);
        mi.setMessageType(MessageType.DOCUMENT);
        mi.setMimeType(AHC.getMimeType(this, localUri));
        mi.setLocalUri(localUri.toString());
        sendDocument(mi);
    }

    /**
     * If {@link #onHandleIntent(Intent)} has {@code null} intent, then automatically check for
     * any unsent messages.
     */
    private void sendAll() {
        final RealmList<MessageItem> notSentMessages = new RealmList<>();
        Realm database = Realm.getDefaultInstance();
        notSentMessages.addAll(database.where(MessageItem.class)
                .equalTo(MessageItemKeys.MESSAGE_STATUS, MessageStatusType.MSG_WAIT)
                .equalTo(MessageItemKeys.MESSAGE_TYPE, MessageType.DOCUMENT)
                .findAll());
        for (int i = 0; i < notSentMessages.size(); i++) {
            sendDocument(notSentMessages.get(i));
        }
        database.close();
    }

    /**
     * Sends a single message using information from the {@link MessageItem} object.
     *
     * @param mItem message item object to extact information from.
     */
    private void sendDocument(final MessageItem mItem) {
        final String messageId = mItem.getMessageId();
        final String receiverId = mItem.getSenderId();
        final String latestMessage = "[Document]";
        final Date messageTime = mItem.getMessageTime();

        final Realm database = Realm.getDefaultInstance();

        //First write to local database if not already done so.
        database.executeTransaction(r -> {
            MessageItem mi = r.where(MessageItem.class)
                    .equalTo(MessageItemKeys.MESSAGE_ID, messageId).findFirst();
            if (mi == null) {
                r.copyToRealm(mItem);
            }
        });

        database.executeTransaction(r -> {
            final ChatsItem ci = r.where(ChatsItem.class)
                    .equalTo(ChatItemKeys.DB_ID, receiverId).findFirst();
            if (ci != null) {
                ci.setLatest(latestMessage);
                ci.setUpdate(messageTime);
            }
        });

        if (getUser() == null) return;

        final DatabaseReference sendMessageRef = getRootReference()
                .child(AHC.FDR_CHAT)
                .child(mItem.getSenderId())
                .child(ChatItemKeys.PRIVATE_MESSAGES)
                .child(getUser().getUid());

        if (Log.isLoggable(TAG, Log.DEBUG))
            Log.d(TAG, "Sending message with id " + mItem.getMessageId());

        //Add new message
        final Map<String, Object> messageMap = new HashMap<>();
        messageMap.put(MessageItemKeys.FDR_DATA, mItem.getMessageData());
        messageMap.put(MessageItemKeys.FDR_MIME_TYPE, mItem.getMimeType());
        messageMap.put(MessageItemKeys.MESSAGE_TYPE, mItem.getMessageType());
        messageMap.put(MessageItemKeys.FDR_DATE, messageTime);

        //Update latest sender information
        final Map<String, Object> senderMap = new HashMap<>();
        senderMap.put(ChatItemKeys.FDR_ID, getUser().getUid());
        senderMap.put(ChatItemKeys.FDR_NAME, getUser().getDisplayName());
        senderMap.put(ChatItemKeys.FDR_LATEST, latestMessage);
        senderMap.put(ChatItemKeys.FDR_PHOTO_URL, getUser().getPhotoUrl().toString());
        senderMap.put(ChatItemKeys.FDR_DATE, messageTime);

        sendMessageRef.child(ChatItemKeys.MESSAGES).child(messageId).setValue(messageMap);
        sendMessageRef.child(ChatItemKeys.SENDER).setValue(senderMap);

        database.close();

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

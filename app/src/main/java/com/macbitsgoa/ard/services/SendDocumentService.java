package com.macbitsgoa.ard.services;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.macbitsgoa.ard.keys.ChatItemKeys;
import com.macbitsgoa.ard.keys.DocumentItemKeys;
import com.macbitsgoa.ard.keys.MessageItemKeys;
import com.macbitsgoa.ard.models.ChatsItem;
import com.macbitsgoa.ard.models.DocumentItem;
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

    /**
     * Method to upload document to firebase and prepare its url.
     *
     * @param data       Uri to save on firebase.
     * @param receiverId receiver's id.
     */
    private void sendDoucment(final Uri data, final String receiverId) {
        final String messageId = getMessageId(data.getPath());
        StorageReference sRef = getStorageRef().child(getUser().getUid()).child(receiverId).child(messageId);
        UploadTask uploadTask = sRef.putFile(data);
        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(exception -> {
            // Handle unsuccessful uploads
        }).addOnSuccessListener(taskSnapshot -> {
            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
            Toast.makeText(this, "Document uploaded on server. Sending to user", Toast.LENGTH_SHORT).show();
            Uri downloadUrl = taskSnapshot.getDownloadUrl();
            sendDocument(downloadUrl.toString(), data, receiverId);
        }).addOnProgressListener(taskSnapshot -> {
            if (taskSnapshot.getBytesTransferred() != 0)
                Toast.makeText(SendDocumentService.this,
                        taskSnapshot.getBytesTransferred()
                                + " bytes uploaded", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Method to create a temporary {@link DocumentItem} object.
     *
     * @param firebaseUrl url in firebase.
     * @param localUri    Uri of local file.
     * @param receiverId  Other user's unique id.
     */
    private void sendDocument(final String firebaseUrl, final Uri localUri, final String receiverId) {

        //Get current system time and include this is in message id
        final Date messageTime = Calendar.getInstance().getTime();
        final String messageId = getMessageId(firebaseUrl);

        final DocumentItem dItem = new DocumentItem();
        dItem.setId(messageId);
        dItem.setReceived(false);
        dItem.setActualTime(messageTime);
        dItem.setRcvdSentTime(Calendar.getInstance().getTime());
        dItem.setSenderId(receiverId);
        dItem.setStatus(MessageStatusType.MSG_WAIT);
        dItem.setFirebaseUrl(firebaseUrl);
        dItem.setMimeType(AHC.getMimeType(this, localUri));
        dItem.setLocalUri(localUri.toString());
        dItem.setThumbnailUrl("http://pngimages.net/sites/default/files/document-png-image-65553.png");

        sendDocument(dItem);
    }

    /**
     * If {@link #onHandleIntent(Intent)} has {@code null} intent, then automatically check for
     * any unsent messages.
     */
    private void sendAll() {
        final RealmList<DocumentItem> notSentMessages = new RealmList<>();
        Realm database = Realm.getDefaultInstance();
        notSentMessages.addAll(database.where(DocumentItem.class)
                .equalTo(MessageItemKeys.MESSAGE_STATUS, MessageStatusType.MSG_WAIT)
                .equalTo(MessageItemKeys.MESSAGE_TYPE, MessageType.DOCUMENT)
                .findAll());
        for (int i = 0; i < notSentMessages.size(); i++) {
            sendDocument(notSentMessages.get(i));
        }
        database.close();
    }

    /**
     * Sends a single message using information from the {@link DocumentItem} object.
     *
     * @param documentItem message item object to extact information from.
     */
    private void sendDocument(final DocumentItem documentItem) {
        final String messageId = documentItem.getId();
        final String receiverId = documentItem.getSenderId();
        final String latestMessage = "\uD83D\uDCCE Document";
        final Date messageTime = documentItem.getActualTime();

        final Realm database = Realm.getDefaultInstance();

        //First write to local database if not already done so.
        database.executeTransaction(r -> {
            DocumentItem mi = r.where(DocumentItem.class)
                    .equalTo(DocumentItemKeys.ID, messageId).findFirst();
            if (mi == null) {
                r.copyToRealm(documentItem);
                final ChatsItem ci = r.where(ChatsItem.class)
                        .equalTo(ChatItemKeys.DB_ID, receiverId).findFirst();
                if (ci != null) {
                    ci.setLatest(latestMessage);
                    ci.setUpdate(messageTime);
                }
            }
        });

        if (getUser() == null) return;

        final DatabaseReference sendMessageRef = getRootReference()
                .child(AHC.FDR_CHAT)
                .child(documentItem.getSenderId())
                .child(ChatItemKeys.PRIVATE_MESSAGES)
                .child(getUser().getUid());

        if (Log.isLoggable(TAG, Log.DEBUG))
            Log.d(TAG, "Sending document with id " + documentItem.getId());

        //Add new document
        final Map<String, Object> documentMap = new HashMap<>();
        documentMap.put(DocumentItemKeys.FIREBASE_URL, documentItem.getFirebaseUrl());
        documentMap.put(DocumentItemKeys.MIME_TYPE, documentItem.getMimeType());
        documentMap.put(DocumentItemKeys.ACTUAL_TIME, documentItem.getActualTime());
        documentMap.put(DocumentItemKeys.THUMBNAIL_URL, documentItem.getThumbnailUrl());

        //Update latest sender information
        final Map<String, Object> senderMap = new HashMap<>();
        senderMap.put(ChatItemKeys.FDR_ID, getUser().getUid());
        senderMap.put(ChatItemKeys.FDR_NAME, getUser().getDisplayName());
        senderMap.put(ChatItemKeys.FDR_LATEST, latestMessage);
        senderMap.put(ChatItemKeys.FDR_PHOTO_URL, getUser().getPhotoUrl().toString());
        senderMap.put(ChatItemKeys.FDR_DATE, messageTime);

        sendMessageRef.child(ChatItemKeys.FDR_DOCUMENTS).child(messageId).setValue(documentMap);
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

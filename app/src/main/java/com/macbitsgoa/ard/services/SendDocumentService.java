package com.macbitsgoa.ard.services;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.macbitsgoa.ard.models.MessageItem;
import com.macbitsgoa.ard.types.MessageStatusType;
import com.macbitsgoa.ard.utils.AHC;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.Sort;

/**
 * Service to save documents on cloud and use the generated links as message data.
 */
public class SendDocumentService extends BaseIntentService {

    public static final String TAG = SendDocumentService.class.getSimpleName();
    private Uri fileUri;

    public SendDocumentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        super.onHandleIntent(intent);
        if (intent == null
                || intent.getData() == null
                || !intent.hasExtra(MessageItemKeys.OTHER_USER_ID)) {
            AHC.logd(TAG, "Null intent was passed, sending all unsent documents");
            sendAll();
        } else {
            AHC.logi(TAG, "Sending document " + intent.toString());
            saveDocument(intent.getData(), intent.getStringExtra(MessageItemKeys.OTHER_USER_ID));
        }
    }

    /**
     * Method to upload document to firebase and prepare its url.
     *
     * @param data        Uri to save on firebase.
     * @param otherUserId receiver's id.
     */
    private void saveDocument(final Uri data, final String otherUserId) {
        String documentId = AHC.generateUniqueId(data.getPath());
        String messageId = AHC.generateUniqueId(data.toString());
        writeToDatabase(data, messageId, documentId, otherUserId);
        uploadDocument(data, otherUserId, messageId, documentId);
    }

    /**
     * Method to upload document to firebase and prepare its url.
     *
     * @param data        Uri to save on firebase.
     * @param otherUserId receiver's id.
     * @param messageId   parent message id.
     * @param documentId  document id to use.
     */
    private void uploadDocument(Uri data, String otherUserId, String messageId, String documentId) {
        StorageReference sRef = getStorageRef()
                .child(getUser().getUid())
                .child(otherUserId)
                .child(documentId);
        UploadTask uploadTask = sRef.putFile(data);
        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(exception -> {
            // Handle unsuccessful uploads
            Toast.makeText(this,
                    "Upload failed. Will try again later", Toast.LENGTH_SHORT).show();
        }).addOnSuccessListener(taskSnapshot -> {
            // taskSnapshot.getMetadata() contains file metadata such as size, content-type,
            // and download URL.
            Toast.makeText(this,
                    "Document uploaded on server. Sending to user", Toast.LENGTH_SHORT).show();
            Uri downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
            updateDocumentRemoteUrl(downloadUrl.toString(), messageId);
        }).addOnProgressListener(taskSnapshot -> {
            if (taskSnapshot.getBytesTransferred() != 0)
                Toast.makeText(SendDocumentService.this,
                        taskSnapshot.getBytesTransferred()/1000
                                + " Kbytes uploaded", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Save document information in database.
     *
     * @param data        Uri string of file
     * @param messageId   Parent id to use.
     * @param documentId  Document id.
     * @param otherUserId Other user info.
     */
    private void writeToDatabase(final Uri data, final String messageId,
                                 final String documentId, String otherUserId) {
        Realm database = Realm.getDefaultInstance();
        database.executeTransaction(realm -> {
            //Generate parent message id
            //Create document item to be attached to parent message
            DocumentItem di = realm
                    .createObject(DocumentItem.class, messageId + documentId);
            di.setMimeType(AHC.getMimeType(SendDocumentService.this, data));
            di.setLocalUri(data.toString());

            //Create parent message
            MessageItem mi = realm.createObject(MessageItem.class, messageId);
            mi.addDocument(di);
            mi.setOtherUserId(otherUserId);
        });
        database.executeTransaction(realm -> {
            ChatsItem ci = realm.where(ChatsItem.class).equalTo(ChatItemKeys.DB_ID, otherUserId)
                    .findFirst();
            if (ci == null) {
                ci = realm.createObject(ChatsItem.class, otherUserId);
                //TODO
            }
            ci.setUnreadCount(0);
            ci.setLatest(AHC.DOCUMENT_LITERAL);
            //TODO A little late value. Fix this also
            ci.setUpdate(Calendar.getInstance().getTime());
        });
        database.close();
    }

    /**
     * Method to update {@link DocumentItem} object's remote url.
     *
     * @param firebaseUrl url in firebase.
     * @param messageId   unique message id.
     */
    private void updateDocumentRemoteUrl(final String firebaseUrl, final String messageId) {
        Realm database = Realm.getDefaultInstance();
        database.executeTransaction(realm -> {
            final DocumentItem dItem = realm.where(MessageItem.class)
                    .equalTo(MessageItemKeys.MESSAGE_ID, messageId)
                    .findFirst()
                    .getDocument();
            if (dItem == null) {
                Log.e(TAG, "Document not found. Probable write fail");
                return;
            }
            dItem.setRemoteUrl(firebaseUrl);
            dItem.getParentMessage().setMessageStatus(MessageStatusType.MSG_SENT);
        });
        sendDocument(database
                .where(MessageItem.class)
                .equalTo(MessageItemKeys.MESSAGE_ID, messageId)
                .findFirst());
        database.close();
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
                .isNotEmpty(MessageItemKeys.DB_DOCUMENTS)
                .findAllSorted(MessageItemKeys.DB_MESSAGE_TIME, Sort.ASCENDING));
        for (int i = 0; i < notSentMessages.size(); i++) {
            sendDocument(notSentMessages.get(i));
        }
        database.close();
    }

    /**
     * Sends a single message using information from the {@link DocumentItem} object.
     *
     * @param messageItem message item object to extact information from.
     */
    private void sendDocument(final MessageItem messageItem) {
        if (messageItem == null) return;
        final Realm database = Realm.getDefaultInstance();
        if (getUser() == null) return;
        final DatabaseReference sendMessageRef = getRootReference()
                .child(AHC.FDR_CHAT)
                .child(messageItem.getOtherUserId())
                .child(ChatItemKeys.PRIVATE_MESSAGES)
                .child(getUser().getUid());

        AHC.logd(TAG, "Sending document with message id " + messageItem.getMessageId());

        //Add new document
        final Map<String, Map<String, String>> documentsMap = new HashMap<>();

        final Map<String, String> documentMap = new HashMap<>();
        documentMap.put(DocumentItemKeys.REMOTE_URL, messageItem.getDocument().getRemoteUrl());
        documentMap.put(DocumentItemKeys.MIME_TYPE, messageItem.getDocument().getMimeType());
        documentMap.put(DocumentItemKeys.THUMBNAIL_URL, messageItem.getDocument().getRemoteThumbnailUrl());

        documentsMap.put("0", documentMap);

        final Map<String, Object> messageMap = new HashMap<>();
        messageMap.put(MessageItemKeys.FDR_DATA, messageItem.getMessageData());
        messageMap.put(MessageItemKeys.FDR_DATE, messageItem.getMessageTime());
        messageMap.put(MessageItemKeys.FDR_DOCUMENTS, documentsMap);

        //Update latest sender information
        final Map<String, Object> senderMap = new HashMap<>();
        senderMap.put(ChatItemKeys.FDR_ID, getUser().getUid());
        senderMap.put(ChatItemKeys.FDR_NAME, getUser().getDisplayName());
        senderMap.put(ChatItemKeys.FDR_LATEST, messageItem.getMessageData());
        senderMap.put(ChatItemKeys.FDR_PHOTO_URL, getUser().getPhotoUrl().toString());
        senderMap.put(ChatItemKeys.FDR_DATE, messageItem.getMessageTime());

        sendMessageRef
                .child(ChatItemKeys.FDR_MESSAGES)
                .child(messageItem.getMessageId())
                .setValue(messageMap);
        sendMessageRef
                .child(ChatItemKeys.SENDER)
                .setValue(senderMap);

        database.close();

        AHC.logd(TAG, "Calling notify service");
        notifyStatus(messageItem.getOtherUserId());
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

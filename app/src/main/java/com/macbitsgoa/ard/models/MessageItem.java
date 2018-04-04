package com.macbitsgoa.ard.models;

import com.macbitsgoa.ard.types.MessageStatusType;
import com.macbitsgoa.ard.utils.AHC;

import java.util.Calendar;
import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Message item for chat.
 *
 * @author Vikramaditya Kukreja
 */
public class MessageItem extends RealmObject {

    /**
     * Unique message id.
     */
    @PrimaryKey
    @Required
    private String messageId;

    /**
     * 1 of 4 values to indicate the message status.
     */
    private int messageStatus;

    /**
     * Flag to indicate whether message was sent or received.
     * {@code true} if received, false otherwise.
     */
    private boolean messageRcvd;

    /**
     * Message Data.
     */
    @Required
    private String messageData;

    /**
     * Uid of other user. Similar to {@link UserItem#getUid()}.
     */
    @Required
    private String otherUserId;

    /**
     * Actual message time from sender.
     */
    @Required
    private Date messageTime;

    /**
     * Received time of message on current user's device.
     */
    @Required
    private Date messageRcvdTime;

    /**
     * List of extras if any, otherwise an empty list.
     */
    private RealmList<DocumentItem> documents;

    public MessageItem() {
        setMessageData(AHC.DOCUMENT_LITERAL);
        setMessageStatus(MessageStatusType.MSG_WAIT);
        setMessageTime(Calendar.getInstance().getTime());
        setMessageRcvdTime(getMessageTime());
        setDocuments(new RealmList<>());
        setMessageRcvd(false);
    }

    public MessageItem(String messageId, int messageStatus, boolean messageRcvd, String messageData,
                       String otherUserId, Date messageTime, Date messageRcvdTime,
                       RealmList<DocumentItem> documents) {
        this.messageId = messageId;
        this.messageStatus = messageStatus;
        this.messageRcvd = messageRcvd;
        this.messageData = messageData;
        this.otherUserId = otherUserId;
        this.messageTime = messageTime;
        this.messageRcvdTime = messageRcvdTime;
        this.documents = documents;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(final String messageId) {
        this.messageId = messageId;
    }

    public String getMessageData() {
        return messageData;
    }

    public void setMessageData(final String messageData) {
        this.messageData = messageData;
    }

    public String getOtherUserId() {
        return otherUserId;
    }

    public void setOtherUserId(final String otherUserId) {
        this.otherUserId = otherUserId;
    }

    public Date getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(final Date messageTime) {
        this.messageTime = messageTime;
    }

    public Date getMessageRcvdTime() {
        return messageRcvdTime;
    }

    public void setMessageRcvdTime(final Date messageRcvdTime) {
        this.messageRcvdTime = messageRcvdTime;
    }

    public boolean isMessageRcvd() {
        return messageRcvd;
    }

    public void setMessageRcvd(final boolean messageRcvd) {
        this.messageRcvd = messageRcvd;
    }

    public int getMessageStatus() {
        return messageStatus;
    }

    /**
     * Set new message status. Value will be updated if new status is greater than the
     * current status.
     *
     * @param messageStatus New status to update.
     * @see com.macbitsgoa.ard.types.MessageStatusType.MessageStatus
     */
    public void setMessageStatus(@MessageStatusType.MessageStatus final int messageStatus) {
        if (messageStatus > this.messageStatus)
            this.messageStatus = messageStatus;
    }

    public boolean hasAttachments() {
        return !documents.isEmpty();
    }

    public DocumentItem getDocument() {
        return documents.get(0);
    }

    public RealmList<DocumentItem> getDocuments() {
        return documents;
    }

    public void setDocuments(RealmList<DocumentItem> documents) {
        this.documents = documents;
    }

    public void addDocument(DocumentItem di) {
        documents.add(di);
    }

    @Override
    public String toString() {
        return "MessageItem{"
                + "messageId=" + messageId
                + ", messageStatus=" + messageStatus
                + ", messageRcvd=" + messageRcvd
                + ", messageData=" + messageData
                + ", otherUserId=" + otherUserId
                + ", messageTime=" + messageTime
                + ", messageRcvdTime=" + messageRcvdTime
                + ", documents=" + documents
                + '}';
    }
}

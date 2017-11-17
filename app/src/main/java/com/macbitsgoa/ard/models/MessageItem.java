package com.macbitsgoa.ard.models;

import com.macbitsgoa.ard.types.MessageStatusType;

import java.util.Date;

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
     *
     * @see MessageStatusType#MSG_WAIT
     * @see MessageStatusType#MSG_SENT
     * @see MessageStatusType#MSG_RCVD
     * @see MessageStatusType#MSG_READ
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
    private String senderId;

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

    public MessageItem() {

    }

    public MessageItem(final String messageId, final int messageStatus, final boolean messageRcvd,
                       final String messageData, final String senderId, final Date messageTime,
                       final Date messageRcvdTime) {
        this.messageId = messageId;
        this.messageStatus = messageStatus;
        this.messageRcvd = messageRcvd;
        this.messageData = messageData;
        this.senderId = senderId;
        this.messageTime = messageTime;
        this.messageRcvdTime = messageRcvdTime;
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

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(final String senderId) {
        this.senderId = senderId;
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

    public void setMessageStatus(@MessageStatusType.MessageStatus final int messageStatus) {
        this.messageStatus = messageStatus;
    }

    @Override
    public String toString() {
        return "MessageItem{"
                + "messageId='" + messageId + '\''
                + ", messageStatus=" + messageStatus
                + ", messageRcvd=" + messageRcvd
                + ", messageData='" + messageData + '\''
                + ", senderId='" + senderId + '\''
                + ", messageTime=" + messageTime
                + ", messageRcvdTime=" + messageRcvdTime
                + '}';
    }
}

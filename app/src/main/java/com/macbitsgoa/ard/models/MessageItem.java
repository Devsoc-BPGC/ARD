package com.macbitsgoa.ard.models;

import com.macbitsgoa.ard.types.MessageStatusType;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by vikramaditya on 24/10/17.
 */

public class MessageItem extends RealmObject {
    @PrimaryKey
    @Required
    private String messageId;

    private int messageStatus;

    private boolean messageRcvd;

    @Required
    private String messageData;

    @Required
    private String senderId;

    @Required
    private Date messageTime;

    @Required
    private Date messageRcvdTime;

    public MessageItem() {

    }

    public MessageItem(final int messageStatus, final boolean messageRcvd, final String messageId,
                       final String messageData, final String senderId, final Date messageTime,
                       final Date messageRcvdTime) {
        this.messageStatus = messageStatus;
        this.messageRcvd = messageRcvd;
        this.messageId = messageId;
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
}

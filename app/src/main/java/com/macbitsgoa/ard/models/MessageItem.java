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

    public MessageItem(int messageStatus, boolean messageRcvd, String messageId, String messageData,
                       String senderId, Date messageTime, Date messageRcvdTime, boolean messageRead) {
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

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessageData() {
        return messageData;
    }

    public void setMessageData(String messageData) {
        this.messageData = messageData;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public Date getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(Date messageTime) {
        this.messageTime = messageTime;
    }

    public Date getMessageRcvdTime() {
        return messageRcvdTime;
    }

    public void setMessageRcvdTime(Date messageRcvdTime) {
        this.messageRcvdTime = messageRcvdTime;
    }

    public boolean isMessageRcvd() {
        return messageRcvd;
    }

    public void setMessageRcvd(boolean messageRcvd) {
        this.messageRcvd = messageRcvd;
    }

    public int getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(@MessageStatusType.MessageStatus int messageStatus) {
        this.messageStatus = messageStatus;
    }
}

package com.macbitsgoa.ard.models;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by vikramaditya on 24/10/17.
 */

public class MessageItem extends RealmObject {
    private String messageId;
    private String messageData;
    private String senderId;
    private Date messageTime;
    private boolean rcvd;

    public MessageItem() {
    }

    public MessageItem(String messageId, String messageData, String senderId, Date messageTime, boolean rcvd) {
        this.messageId = messageId;
        this.messageData = messageData;
        this.senderId = senderId;
        this.messageTime = messageTime;
        this.rcvd = rcvd;
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

    public boolean isRcvd() {
        return rcvd;
    }

    public void setRcvd(boolean rcvd) {
        this.rcvd = rcvd;
    }
}

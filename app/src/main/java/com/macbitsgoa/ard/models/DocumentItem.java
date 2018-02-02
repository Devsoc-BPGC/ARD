package com.macbitsgoa.ard.models;

import android.support.annotation.Nullable;

import com.macbitsgoa.ard.types.MessageStatusType;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Class to represent a file document.
 *
 * @author Vikramadiyta Kukreja
 */
public class DocumentItem extends RealmObject {
    @Required
    private Date actualTime;

    @PrimaryKey
    private String id;

    @Nullable
    private String firebaseUrl;

    @Nullable
    private String localUri;

    @Nullable
    private String mimeType;

    private boolean received;

    @Required
    private Date rcvdSentTime;

    @Required
    private String senderId;

    private String thumbnailUrl;

    /**
     * 1 of 4 values to indicate the message status.
     *
     * @see MessageStatusType#MSG_WAIT
     * @see MessageStatusType#MSG_SENT
     * @see MessageStatusType#MSG_RCVD
     * @see MessageStatusType#MSG_READ
     */
    private int status;

    public Date getActualTime() {
        return actualTime;
    }

    public void setActualTime(Date actualTime) {
        this.actualTime = actualTime;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    @Nullable
    public String getFirebaseUrl() {
        return firebaseUrl;
    }

    public void setFirebaseUrl(@Nullable final String firebaseUrl) {
        this.firebaseUrl = firebaseUrl;
    }

    @Nullable
    public String getLocalUri() {
        return localUri;
    }

    public void setLocalUri(@Nullable final String localUri) {
        this.localUri = localUri;
    }

    @Nullable
    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(@Nullable final String mimeType) {
        this.mimeType = mimeType;
    }

    public boolean isReceived() {
        return received;
    }

    public void setReceived(final boolean received) {
        this.received = received;
    }

    public Date getRcvdSentTime() {
        return rcvdSentTime;
    }

    public void setRcvdSentTime(final Date rcvdSentTime) {
        this.rcvdSentTime = rcvdSentTime;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(final String senderId) {
        this.senderId = senderId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(@MessageStatusType.MessageStatus final int status) {
        this.status = status;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
}

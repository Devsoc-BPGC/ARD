package com.macbitsgoa.ard.models;

import com.macbitsgoa.ard.keys.MessageItemKeys;

import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.LinkingObjects;
import io.realm.annotations.PrimaryKey;

/**
 * Class to represent a file document.
 *
 * @author Vikramadiyta Kukreja
 */
public class DocumentItem extends RealmObject {
    /**
     * Id of document.
     */
    @PrimaryKey
    private String id;

    /**
     * Url of remote copy.
     */
    private String remoteUrl;

    /**
     * URI of local image as string.
     */
    private String localUri;

    /**
     * Mime type.
     */
    private String mimeType;

    /**
     * Remote thumbnail url.
     */
    private String remoteThumbnailUrl;

    /**
     * Local thumbnail url.
     */
    private String localThumbnailUri;

    /**
     * Back link to the message owning this document.
     */
    @LinkingObjects(MessageItemKeys.DB_DOCUMENTS)
    private final RealmResults<MessageItem> parentMessages = null;

    public DocumentItem() {
        remoteThumbnailUrl = "";
    }

    public DocumentItem(String id, String remoteUrl, String localUri, String mimeType,
                        String remoteThumbnailUrl, String localThumbnailUri) {
        this.id = id;
        this.remoteUrl = remoteUrl;
        this.localUri = localUri;
        this.mimeType = mimeType;
        this.remoteThumbnailUrl = remoteThumbnailUrl;
        this.localThumbnailUri = localThumbnailUri;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getRemoteUrl() {
        return remoteUrl;
    }

    public void setRemoteUrl(String remoteUrl) {
        this.remoteUrl = remoteUrl;
    }

    public String getLocalUri() {
        return localUri;
    }

    public void setLocalUri(String localUri) {
        this.localUri = localUri;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getRemoteThumbnailUrl() {
        return remoteThumbnailUrl;
    }

    public void setRemoteThumbnailUrl(String remoteThumbnailUrl) {
        this.remoteThumbnailUrl = remoteThumbnailUrl;
    }

    public String getLocalThumbnailUri() {
        return localThumbnailUri;
    }

    public void setLocalThumbnailUri(String localThumbnailUrl) {
        this.localThumbnailUri = localThumbnailUrl;
    }

    public MessageItem getParentMessage() {
        return parentMessages.get(0);
    }

    public RealmResults<MessageItem> getParentMessages() {
        return parentMessages;
    }
}

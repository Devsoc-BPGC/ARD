package com.macbitsgoa.ard.models;

import android.support.annotation.Nullable;

import java.util.Calendar;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ChatsItem extends RealmObject {
    @PrimaryKey
    private String id;
    private String name;
    private String latest;
    private String photoUrl;

    @Nullable
    private Date update;
    private int unreadCount;

    public ChatsItem() {
        this.id = null;
        this.name = "";
        this.latest = "now";
        this.photoUrl = "";
        this.update = Calendar.getInstance().getTime();
        this.unreadCount = 0;
    }

    public ChatsItem(final String id, final String name, final String latest, final String photoUrl,
                     @Nullable final Date update, final int unreadCount) {
        this.id = id;
        this.name = name;
        this.latest = latest;
        this.photoUrl = photoUrl;
        this.update = update;
        this.unreadCount = unreadCount;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getLatest() {
        return latest;
    }

    public void setLatest(final String latest) {
        this.latest = latest;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(final String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Date getUpdate() {
        return update;
    }

    public void setUpdate(final Date update) {
        this.update = update;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(final int unreadCount) {
        this.unreadCount = unreadCount;
    }

    @Override
    public String toString() {
        return "ChatsItem{"
                + "id='" + id + '\''
                + ", name='" + name + '\''
                + ", latest='" + latest + '\''
                + ", photoUrl='" + photoUrl + '\''
                + ", update=" + update
                + ", unreadCount=" + unreadCount
                + '}';
    }
}

package com.macbitsgoa.ard.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Calendar;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ChatsItem extends RealmObject {
    @PrimaryKey
    private String id;

    /**
     * Name of other user.
     */
    @NonNull
    private String name;

    @NonNull
    private String latest;
    private String photoUrl;

    /**
     * Latest message time. Should not be null.
     */
    @NonNull
    private Date update;

    /**
     * Current unread count.
     */
    private int unreadCount;

    public ChatsItem() {
        this.name = "";
        this.latest = "";
        this.photoUrl = "";
        this.update = Calendar.getInstance().getTime();
        this.unreadCount = 0;
    }

    public ChatsItem(final String id, @NonNull final String name,
                     @NonNull final String latest, final String photoUrl,
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

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull final String name) {
        this.name = name;
    }

    @NonNull
    public String getLatest() {
        return latest;
    }

    public void setLatest(@Nullable final String latest) {
        this.latest = latest == null ? "" : latest;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(final String photoUrl) {
        this.photoUrl = photoUrl;
    }

    @NonNull
    public Date getUpdate() {
        return update;
    }

    public void setUpdate(@Nullable final Date update) {
        if (update == null) this.update = Calendar.getInstance().getTime();
        if (update.getTime() > this.update.getTime()) this.update = update;
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
                + "id=" + id
                + ", name=" + name
                + ", latest=" + latest
                + ", photoUrl=" + photoUrl
                + ", update=" + update
                + ", unreadCount=" + unreadCount
                + '}';
    }
}

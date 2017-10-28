package com.macbitsgoa.ard.models;

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

    public ChatsItem(String id, String name, String latest, String photoUrl, Date update, int unreadCount) {
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

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLatest() {
        return latest;
    }

    public void setLatest(String latest) {
        this.latest = latest;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Date getUpdate() {
        return update;
    }

    public void setUpdate(Date update) {
        this.update = update;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    @Override
    public String toString() {
        return "ChatsItem{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", latest='" + latest + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                ", update=" + update +
                ", unreadCount=" + unreadCount +
                '}';
    }
}

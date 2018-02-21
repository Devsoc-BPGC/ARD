package com.macbitsgoa.ard.models;

import android.support.annotation.NonNull;

import java.util.Calendar;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Realm object for posts of type {@link com.macbitsgoa.ard.types.PostType#ANNOUNCEMENT}.
 *
 * @author Vikramaditya Kukreja
 */
public class AnnItem extends RealmObject {

    /**
     * Primary key is the node string in firebase.
     */
    @PrimaryKey
    private String key;

    /**
     * Data is the main content of this type.
     */
    @NonNull
    private String data;

    /**
     * Author name field.
     */
    @NonNull
    private String author;

    /**
     * Read status. This is also closely related to the {@link #date} object.
     * Read status is set to {@code false} iff date changed. This is because any data or author
     * change has to accompany a date change.
     */
    private boolean read;

    /**
     * Variable to store info of whether this announcement was sent as notification or not.
     */
    private boolean notified;

    /**
     * Date of original posting.
     * Update date can also be added later.
     */
    @NonNull
    private Date date;

    public AnnItem() {
        data = "";
        author = "Admin";
        date = Calendar.getInstance().getTime();
        read = false;
        notified = false;
    }


    public AnnItem(final String key, @NonNull final String data, @NonNull final String author,
                   final boolean read, final boolean notified, @NonNull final Date date) {
        this.key = key;
        this.data = data;
        this.author = author;
        this.read = read;
        this.notified = notified;
        this.date = date;
    }

    public String getKey() {
        return key;
    }

    public void setKey(@NonNull final String key) {
        this.key = key;
    }

    @NonNull
    public String getData() {
        return data;
    }

    public void setData(@NonNull final String data) {
        this.data = data;
    }

    @NonNull
    public String getAuthor() {
        return author;
    }

    public void setAuthor(@NonNull final String author) {
        this.author = author;
    }

    @NonNull
    public Date getDate() {
        return date;
    }

    public void setDate(@NonNull final Date date) {
        this.date = date;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(final boolean read) {
        this.read = read;
    }

    public boolean isNotified() {
        return notified;
    }

    public void setNotified(boolean notified) {
        this.notified = notified;
    }

    @Override
    public String toString() {
        return "AnnItem{"
                + "key='" + key + '\''
                + ", data='" + data + '\''
                + ", author='" + author + '\''
                + ", read=" + read
                + ", notified=" + notified
                + ", date=" + date
                + '}';
    }
}

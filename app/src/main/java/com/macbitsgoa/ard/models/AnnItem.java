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
     * Date of original posting.
     * Update date can also be added later.
     */
    @NonNull
    private Date date;

    public AnnItem() {
        data = "";
        author = "Admin";
        date = Calendar.getInstance().getTime();
    }


    public AnnItem(final String key, @NonNull final String data, @NonNull final String author,
                   @NonNull final Date date) {
        this.key = key;
        this.data = data;
        this.author = author;
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

    @Override
    public String toString() {
        return "AnnItem{"
                + "key='" + key + '\''
                + ", data='" + data + '\''
                + ", author='" + author + '\''
                + ", date=" + date
                + '}';
    }
}

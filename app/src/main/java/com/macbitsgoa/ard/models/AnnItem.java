package com.macbitsgoa.ard.models;

import android.support.annotation.NonNull;

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
    private String data;

    /**
     * Author name field.
     */
    private String author;

    /**
     * Date of original posting.
     * Update date can also be added later.
     */
    private Date date;

    public String getKey() {
        return key;
    }

    public void setKey(@NonNull final String key) {
        this.key = key;
    }

    public String getData() {
        return data;
    }

    public void setData(@NonNull final String data) {
        this.data = data;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(@NonNull final String author) {
        this.author = author;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(@NonNull final Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        final String separator = ", ";
        return "Key=" + getKey() + separator + "data=" + data + separator + "author=" + author
                + separator + "date=" + date;
    }
}

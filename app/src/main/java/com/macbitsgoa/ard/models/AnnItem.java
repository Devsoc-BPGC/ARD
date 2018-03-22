package com.macbitsgoa.ard.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.macbitsgoa.ard.utils.AHC;

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

    public void setAuthor(@Nullable final String author) {
        this.author = author;
        if (this.author == null || this.author.length() == 0) {
            this.author = AHC.DEFAULT_AUTHOR;
        }
    }

    @NonNull
    public Date getDate() {
        return date;
    }

    public void setDate(@NonNull final Date date) {
        this.date = date;
    }

    public String toStringVerbose() {
        return "AnnItem{"
                + "key='" + key + '\''
                + ", data='" + data + '\''
                + ", author='" + author + '\''
                + ", date=" + date
                + '}';
    }
}

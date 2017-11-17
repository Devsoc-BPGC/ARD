package com.macbitsgoa.ard.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Calendar;
import java.util.Date;

import io.realm.RealmObject;

/**
 * Class to represent a slideshow image.
 *
 * @author Vikramaditya Kukreja
 */
public class SlideshowItem extends RealmObject {

    /**
     * Url of slideshow image. Cannot be null.
     */
    @NonNull
    private String photoUrl;

    /**
     * Photo title to display.
     */
    @Nullable
    private String photoTitle;
    @NonNull
    private Date photoDate;
    @Nullable
    private String photoDesc;
    @Nullable
    private String photoTag;
    @Nullable
    private String photoTagColor;
    @Nullable
    private String photoTagTextColor;

    public SlideshowItem() {
        //TODO Add a correct permanent url
        photoUrl = "";
        photoDate = Calendar.getInstance().getTime();
    }

    public SlideshowItem(@NonNull final String photoUrl, @Nullable final String photoTitle,
                         @NonNull final Date photoDate, @Nullable final String photoDesc,
                         @Nullable final String photoTag, @Nullable final String photoTagColor,
                         @Nullable final String photoTagTextColor) {
        this.photoUrl = photoUrl;
        this.photoTitle = photoTitle;
        this.photoDate = photoDate;
        this.photoDesc = photoDesc;
        this.photoTag = photoTag;
        this.photoTagColor = photoTagColor;
        this.photoTagTextColor = photoTagTextColor;
    }

    @NonNull
    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(@NonNull final String photoUrl) {
        this.photoUrl = photoUrl;
    }

    @Nullable
    public String getPhotoTitle() {
        return photoTitle;
    }

    public void setPhotoTitle(@Nullable final String photoTitle) {
        this.photoTitle = photoTitle;
    }

    @NonNull
    public Date getPhotoDate() {
        return photoDate;
    }

    public void setPhotoDate(@NonNull final Date photoDate) {
        this.photoDate = photoDate;
    }

    @Nullable
    public String getPhotoDesc() {
        return photoDesc;
    }

    public void setPhotoDesc(@Nullable final String photoDesc) {
        this.photoDesc = photoDesc;
    }

    @Nullable
    public String getPhotoTag() {
        return photoTag;
    }

    public void setPhotoTag(@Nullable final String photoTag) {
        this.photoTag = photoTag;
    }

    @Nullable
    public String getPhotoTagColor() {
        return photoTagColor;
    }

    public void setPhotoTagColor(@Nullable final String photoTagColor) {
        this.photoTagColor = photoTagColor;
    }

    @Nullable
    public String getPhotoTagTextColor() {
        return photoTagTextColor;
    }

    public void setPhotoTagTextColor(@Nullable final String photoTagTextColor) {
        this.photoTagTextColor = photoTagTextColor;
    }
}

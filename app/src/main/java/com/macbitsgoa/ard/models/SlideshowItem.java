package com.macbitsgoa.ard.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Calendar;
import java.util.Date;

/**
 * Class to represent a slideshow image.
 *
 * @author Vikramaditya Kukreja
 */
public class SlideshowItem {

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
        photoUrl = "https://picsum.photos/640/400/?random";
        photoDate = Calendar.getInstance().getTime();
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

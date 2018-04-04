package com.macbitsgoa.ard.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Calendar;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Class to represent a slideshow image.
 *
 * @author Vikramaditya Kukreja
 * @author Rushikesh Jogdand
 */
public class SlideshowItem extends RealmObject {

    /*
     * Note: There is no primary key for this item.
     * Hence, only batch addition/removal should be done
     */

    @PrimaryKey
    private String photoUrl;

    @NonNull
    private String photoTitle;

    @NonNull
    private Date photoDate;

    @NonNull
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
        photoTitle = "";
        photoDesc = "";
    }

    @NonNull
    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(@NonNull String photoUrl) {
        this.photoUrl = photoUrl;
    }

    @NonNull
    public String getPhotoTitle() {
        return photoTitle;
    }

    public void setPhotoTitle(@NonNull String photoTitle) {
        this.photoTitle = photoTitle;
    }

    @NonNull
    public Date getPhotoDate() {
        return photoDate;
    }

    public void setPhotoDate(@NonNull Date photoDate) {
        this.photoDate = photoDate;
    }

    @NonNull
    public String getPhotoDesc() {
        return photoDesc;
    }

    public void setPhotoDesc(@NonNull String photoDesc) {
        this.photoDesc = photoDesc;
    }

    @Nullable
    public String getPhotoTag() {
        return photoTag;
    }

    public void setPhotoTag(@Nullable String photoTag) {
        this.photoTag = photoTag;
    }

    @Nullable
    public String getPhotoTagColor() {
        return photoTagColor;
    }

    public void setPhotoTagColor(@Nullable String photoTagColor) {
        this.photoTagColor = photoTagColor;
    }

    @Nullable
    public String getPhotoTagTextColor() {
        return photoTagTextColor;
    }

    public void setPhotoTagTextColor(@Nullable String photoTagTextColor) {
        this.photoTagTextColor = photoTagTextColor;
    }
}

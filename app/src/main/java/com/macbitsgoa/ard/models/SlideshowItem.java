package com.macbitsgoa.ard.models;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Class to represent a slideshow image.
 *
 * @author Vikramaditya Kukreja
 */
public class SlideshowItem extends RealmObject {
    private String photoUrl;
    private String photoTitle;
    private Date photoDate;
    private String photoDesc;

    public SlideshowItem() {
    }

    public SlideshowItem(String photoUrl, String photoTitle, Date photoDate, String photoDesc) {
        this.photoUrl = photoUrl;
        this.photoTitle = photoTitle;
        this.photoDate = photoDate;
        this.photoDesc = photoDesc;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getPhotoTitle() {
        return photoTitle;
    }

    public void setPhotoTitle(String photoTitle) {
        this.photoTitle = photoTitle;
    }

    public Date getPhotoDate() {
        return photoDate;
    }

    public void setPhotoDate(Date photoDate) {
        this.photoDate = photoDate;
    }

    public String getPhotoDesc() {
        return photoDesc;
    }

    public void setPhotoDesc(String photoDesc) {
        this.photoDesc = photoDesc;
    }
}

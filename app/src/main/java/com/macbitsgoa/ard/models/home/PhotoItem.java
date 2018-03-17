package com.macbitsgoa.ard.models.home;

import android.support.annotation.Nullable;

import io.realm.RealmObject;

/**
 * Class representing a Photo object.
 *
 * @author Vikamaditya Kukreja
 */
public class PhotoItem extends RealmObject {

    /**
     * Image url photoUrl.
     */
    private String photoUrl;

    /**
     * Order of placement in the list.
     */
    private String priority;

    public PhotoItem() {
        priority = "" + Integer.MAX_VALUE;
    }

    @Nullable
    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(final String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(final String priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "PhotoItem{"
                + "photoUrl='" + photoUrl + '\''
                + ", priority=" + priority
                + '}';
    }
}

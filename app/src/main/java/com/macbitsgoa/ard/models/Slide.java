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
 * @author Rushikesh Jogdand
 */
public class Slide extends RealmObject {

    /*
     * Note: There is no primary key for this item.
     * Hence, only batch addition/removal should be done
     */

    @NonNull
    public String photoUrl;
    @Nullable
    public String photoTitle;
    @NonNull
    public Date photoDate;
    @Nullable
    public String photoDesc;
    @Nullable
    public String photoTag;
    @Nullable
    public String photoTagColor;
    @Nullable
    public String photoTagTextColor;

    public Slide() {
        photoUrl = "https://picsum.photos/640/400/?random";
        photoDate = Calendar.getInstance().getTime();
    }
}

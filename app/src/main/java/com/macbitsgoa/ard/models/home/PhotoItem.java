package com.macbitsgoa.ard.models.home;

import android.support.annotation.Nullable;

import io.realm.RealmObject;

/**
 * Created by vikramaditya on 8/11/17.
 */

public class PhotoItem extends RealmObject {

    /**
     * Image url data.
     */
    @Nullable
    private String data;

    public PhotoItem() {
    }

    public PhotoItem(@Nullable final String data) {
        this.data = data;
    }

    @Nullable
    public String getData() {
        return data;
    }

    public void setData(@Nullable final String data) {
        this.data = data;
    }
}

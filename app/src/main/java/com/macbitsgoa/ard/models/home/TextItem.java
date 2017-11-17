package com.macbitsgoa.ard.models.home;

import android.support.annotation.Nullable;

import io.realm.RealmObject;

/**
 * Created by vikramaditya on 8/11/17.
 */

public class TextItem extends RealmObject {

    /**
     * Text data.
     */
    @Nullable
    private String data;

    public TextItem() {
    }

    public TextItem(@Nullable final String data) {
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

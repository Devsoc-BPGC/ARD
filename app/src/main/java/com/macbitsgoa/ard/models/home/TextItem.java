package com.macbitsgoa.ard.models.home;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.realm.RealmObject;

/**
 * Class representing a text item.
 *
 * @author Vikramaditya Kukreja
 */
public class TextItem extends RealmObject {

    /**
     * Text data.
     */
    @Nullable
    private String data;

    /**
     * Order of placement in the list.
     */
    private String priority;

    public TextItem() {
        priority = "" + Integer.MIN_VALUE;
    }

    @NonNull
    public String getData() {
        return data == null ? "" : data;
    }

    public void setData(@Nullable final String data) {
        this.data = data;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(final String priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "TextItem{"
                + "data='" + data + '\''
                + ", priority=" + priority
                + '}';
    }
}

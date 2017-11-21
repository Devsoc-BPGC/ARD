package com.macbitsgoa.ard.models;

import android.support.annotation.NonNull;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Item represents a section of Faq item. eg. PhD, ME, etc.
 *
 * @author Vikramaditya Kukreja
 */
public class FaqSectionItem extends RealmObject {

    /**
     * Faq section key to categorise.
     */
    @PrimaryKey
    private String sectionKey;

    /**
     * Section name.
     */
    @NonNull
    private String sectionTitle;

    /**
     * Ordering priority in lists.
     */
    @NonNull
    private String sectionPriority;

    /**
     * No args constructor. Default value for section is <b>General</b> and priority is <b>"0"</b>.
     */
    public FaqSectionItem() {
        sectionTitle = "General";
        sectionPriority = "0";
    }

    public String getSectionKey() {
        return sectionKey;
    }

    public void setSectionKey(final String sectionKey) {
        this.sectionKey = sectionKey;
    }

    @NonNull
    public String getSectionTitle() {
        return sectionTitle;
    }

    public void setSectionTitle(@NonNull final String sectionTitle) {
        this.sectionTitle = sectionTitle;
    }

    @NonNull
    public String getSectionPriority() {
        return sectionPriority;
    }

    public void setSectionPriority(@NonNull final String sectionPriority) {
        this.sectionPriority = sectionPriority;
    }

    @Override
    public String toString() {
        return "FaqSectionItem{"
                + "sectionKey='" + sectionKey + '\''
                + ", sectionTitle='" + sectionTitle + '\''
                + ", sectionPriority='" + sectionPriority + '\''
                + '}';
    }
}

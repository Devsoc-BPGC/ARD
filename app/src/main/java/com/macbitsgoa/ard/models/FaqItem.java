package com.macbitsgoa.ard.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.macbitsgoa.ard.utils.AHC;

import java.util.Calendar;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Class represting a Faq object.
 * Call using {@code realm.createObject(FaqItem.class, uniqueKey)}. Every faq item has a unique
 * key and in case of a match or conflict, values will be updated.
 *
 * @author Vikramaditya Kukreja
 */

public class FaqItem extends RealmObject {

    /**
     * Unique Id of faq item.
     */
    @PrimaryKey
    private String key;

    /**
     * Name of faq subsection of which this faq is a part of.
     */
    @NonNull
    private String section;

    /**
     * Name of sub section that it belongs to.
     */
    @Required
    @NonNull
    private String subSection;

    /**
     * Origin date of faq.
     */
    @NonNull
    private Date originalDate;

    /**
     * Latest updated Date.
     */
    @NonNull
    private Date updateDate;

    /**
     * String representing faq question.
     */
    @NonNull
    private String question;

    /**
     * String representing faq answer.
     */
    @NonNull
    private String answer;

    /**
     * String representing author info.
     */
    @NonNull
    private String author;

    /**
     * Any extra information that is to be included for later use.
     */
    @NonNull
    private String desc;

    /**
     * No args constructor.
     */
    public FaqItem() {
        section = "General";
        subSection = section;
        originalDate = Calendar.getInstance().getTime();
        updateDate = originalDate;
        question = "";
        answer = "";
        author = AHC.DEFAULT_AUTHOR;
        desc = "";
    }

    public String getKey() {
        return key;
    }

    public void setKey(final String key) {
        this.key = key;
    }

    @NonNull
    public String getSection() {
        return section;
    }

    public void setSection(@NonNull final String section) {
        this.section = section;
    }

    @NonNull
    public Date getOriginalDate() {
        return originalDate;
    }

    public void setOriginalDate(@NonNull final Date originalDate) {
        this.originalDate = originalDate;
    }

    @NonNull
    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(@NonNull final Date updateDate) {
        this.updateDate = updateDate;
    }

    @NonNull
    public String getQuestion() {
        return question;
    }

    public void setQuestion(@NonNull final String question) {
        this.question = question;
    }

    @NonNull
    public String getAnswer() {
        return answer;
    }

    public void setAnswer(@NonNull final String answer) {
        this.answer = answer;
    }

    @NonNull
    public String getAuthor() {
        return author;
    }

    /**
     * Set author name. Can use null value, will be replaced by {@value AHC#DEFAULT_AUTHOR}.
     *
     * @param author Author name as string, nullable.
     */
    public void setAuthor(@Nullable final String author) {
        if (author == null) {
            this.author = AHC.DEFAULT_AUTHOR;
        } else {
            this.author = author;
        }
    }

    @NonNull
    public String getDesc() {
        return desc;
    }

    /**
     * Set description. This is for future use. Can use null value which will be replaced by
     * emoty string.
     *
     * @param desc String to use as description.
     */
    public void setDesc(@Nullable final String desc) {
        if (desc == null) {
            this.desc = "";
        } else {
            this.desc = desc;
        }
    }

    @NonNull
    public String getSubSection() {
        return subSection;
    }

    public void setSubSection(@Nullable final String subSection) {
        if (subSection != null && !subSection.equals("")) {
            this.subSection = subSection;
        }
    }
}

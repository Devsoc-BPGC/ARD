package com.macbitsgoa.ard.keys;

/**
 * Util class for keys that are used for {@link com.macbitsgoa.ard.models.AnnItem} class.
 *
 * @author Vikramaditya Kukreja
 */
public class AnnItemKeys {

    /**
     * Key for <b>primary key</b> field in Realm Database.
     * {@link com.macbitsgoa.ard.models.AnnItem#key}
     */
    public static final String KEY = "key";

    /**
     * This key is used to differentiate a object of AnnItem from HomeItem when passing data
     * to {@link com.macbitsgoa.ard.activities.PostDetailsActivity} via intent.
     */
    public static final String SECONDARY_KEY = "annItem";

    /**
     * Key for {@link com.macbitsgoa.ard.types.PostType#ANNOUNCEMENT} field.
     */
    public static final String TYPE_KEY = "type";

    /**
     * Key for {@link com.macbitsgoa.ard.models.AnnItem#data} field.
     */
    public static final String DATA = "data";

    /**
     * Key for {@link com.macbitsgoa.ard.models.AnnItem#author} field.
     */
    public static final String AUTHOR = "author";

    /**
     * Key for {@link com.macbitsgoa.ard.models.AnnItem#date} field.
     */
    public static final String DATE = "date";

    /**
     * Key for {@link com.macbitsgoa.ard.models.AnnItem#read} field.
     */
    public static final String READ = "read";
}

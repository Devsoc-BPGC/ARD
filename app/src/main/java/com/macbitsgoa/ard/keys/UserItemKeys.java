package com.macbitsgoa.ard.keys;

import com.macbitsgoa.ard.models.UserItem;

/**
 * Keys for {@link UserItem} class and FDR.
 *
 * @author Vikramaditya Kukreja
 */
public class UserItemKeys {

    /**
     * Token key for use with Firebase. Not saved in local database.
     */
    public static final String FDR_TOKEN = "token";

    /**
     * Field {@link UserItem#name} (Name of UserItem).
     */
    public static final String NAME = "name";

    /**
     * Field {@link UserItem#email} (email id of user).
     */
    public static final String EMAIL = "email";

    /**
     * Field {@link UserItem#photoUrl} (photo url of user).
     */
    public static final String PHOTO_URL = "photoUrl";
    /**
     * Phone number of user.
     */
    public static final String PHONE_NUMBER = "phoneNumber";

    /**
     * Field {@link UserItem#photoUrl} (photo url of user).
     */
    public static final String DESC = "desc";
}

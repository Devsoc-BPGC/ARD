package com.macbitsgoa.ard.keys;

/**
 * Keys related to Firebase Messaging.
 *
 * @author Vikramaditya Kukreja
 */
public class FCMKeys {

    /**
     * Key to get action data from remote message data map.
     */
    public static final String ACTION = "action";

    /**
     * Id of message. Not the same as that from Firebase.
     */
    public static final String ID = "id";

    /**
     * Key to detect if clicking on notification opens a uri.
     */
    public static final String ACTION_VIEW = "view";

    /**
     * Uri key. Use along with {@link #ACTION_VIEW}.
     */
    public static final String ACTION_VIEW_URI = "uri";
    public static final String ACTION_VIEW_TITLE = "title";
    public static final String ACTION_VIEW_TEXT = "text";

    public static final String ACTION_SERVICE = "service";

    public static final String ACTION_SERVICE_NAME = "serviceName";
    public static final String ACTION_LIMIT_TO_LAST = "limitToLast";

    /**
     * Delete action is sent.
     */
    public static final String ACTION_DELETE = "delete";

    /**
     * Id or key to delete from realm.
     */
    public static final String ACTION_DELETE_ID = "deleteId";

    /**
     * Action indicating announcement is sent.
     */
    public static final String ACTION_ANNOUNCEMENT = "announcement";
}

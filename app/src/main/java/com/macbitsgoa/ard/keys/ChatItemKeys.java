package com.macbitsgoa.ard.keys;

/**
 * Created by vikramaditya on 29/10/17.
 */

public class ChatItemKeys {

    public static final String MESSAGE_STATUS = "messageStatus";
    public static final String PRIVATE_MESSAGES = "0";
    public static final String MESSAGES = "messages";
    public static final String SENDER = "sender";
    public static final String ONLINE = "online";
    public static final String SENT_STATUS = "sentMessages";

    /**
     * Field name used in database for {@link com.macbitsgoa.ard.models.ChatsItem#name}.
     */
    public static final String DB_NAME = "name";
    public static final String DB_ID = "id";
    public static final String DB_LATEST = "latest";
    public static final String DB_PHOTO_URL = "photoUrl";
    public static final String DB_DATE = "update";

    public static final String FDR_NAME = DB_NAME;
    public static final String FDR_ID = DB_ID;
    public static final String FDR_LATEST = DB_LATEST;
    public static final String FDR_PHOTO_URL = DB_PHOTO_URL;
    public static final String FDR_DOCUMENTS = "documents";

    public static final String FDR_DATE = "date";
    public static final String NOTIFICATION_ACTION = "com.macbitsgoa.ard.notificationService.new";
    public static final String NEW_MESSAGE_ARRIVED = "com.macbitsgoa.ard.messagingService.new";
}

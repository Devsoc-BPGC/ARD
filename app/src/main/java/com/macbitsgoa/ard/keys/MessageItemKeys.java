package com.macbitsgoa.ard.keys;

import com.macbitsgoa.ard.models.MessageItem;

/**
 * Keys used for {@link MessageItem} class.
 *
 * @author Vikramaditya Kukreja
 */
public class MessageItemKeys {
    /**
     * Key for Firebase node value for {@link MessageItem#messageData} field.
     */
    public static final String FDR_DATA = "data";

    /**
     * Key for Firebase node value for {@link MessageItem#messageData} field.
     */
    public static final String FDR_DATE = "date";

    /**
     * Key for Firebase node for storing documents.
     */
    public static final String FDR_DOCUMENTS = "documents";


    public static final String MESSAGE_STATUS = "messageStatus";

    /**
     * Key for {@link MessageItem#messageTime}.
     */
    public static final String DB_MESSAGE_TIME = "messageTime";

    public static final String MESSAGE_RECEIVED = "messageRcvd";
    public static final String RECEIVER_ID = "receiverId";

    /**
     * Key for other user's id.
     *
     * @see MessageItem#otherUserId
     */
    public static final String OTHER_USER_ID = "otherUserId";

    public static final String DB_DOCUMENTS = "documents";

    /**
     * Key for {}
     */
    public static final String MESSAGE_ID = "messageId";
    public static final String MESSAGE_DATA = "messageData";
    public static final String MESSAGE_RECEIVED_TIME = "messageRcvdTime";
}

package com.macbitsgoa.ard.keys;

import com.macbitsgoa.ard.models.MessageItem;

/**
 * Keys used for MessageItem class. Useful for realm and firebase.
 *
 * @author Vikramaditya Kukreja
 * @see MessageItem
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

    /**
     * Key for message status integer.
     *
     * @see MessageItem#messageStatus
     */
    public static final String MESSAGE_STATUS = "messageStatus";

    /**
     * Key for message time.
     *
     * @see MessageItem#messageTime
     */
    public static final String DB_MESSAGE_TIME = "messageTime";

    /**
     * Key for message received boolean.
     *
     * @see MessageItem#messageRcvd
     */
    public static final String MESSAGE_RECEIVED = "messageRcvd";

    /**
     * Key for other user's id.
     *
     * @see MessageItem#otherUserId
     */
    public static final String OTHER_USER_ID = "otherUserId";

    /**
     * Key for documents field.
     *
     * @see MessageItem#documents
     */
    public static final String DB_DOCUMENTS = "documents";

    /**
     * Key for {}
     */
    public static final String MESSAGE_ID = "messageId";

    /**
     * Key for message data field.
     *
     * @see MessageItem#messageData
     */
    public static final String MESSAGE_DATA = "messageData";

    /**
     * Key for message received time.
     *
     * @see MessageItem#messageRcvdTime
     */
    public static final String MESSAGE_RECEIVED_TIME = "messageRcvdTime";
}

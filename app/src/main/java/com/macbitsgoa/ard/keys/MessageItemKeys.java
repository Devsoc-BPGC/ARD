package com.macbitsgoa.ard.keys;

import com.macbitsgoa.ard.models.MessageItem;

/**
 * Keys used for {@link MessageItem} class.
 *
 * @author Vikramaditya Kukreja
 */
public class MessageItemKeys {
    /**
     * Firebase node value for {@link MessageItem#messageData} field.
     */
    public static final String FDR_DATA = "data";

    /**
     * Firebase node value for {@link MessageItem#messageData} field.
     */
    public static final String FDR_DATE = "date";


    public static final String MESSAGE_STATUS = "messageStatus";
    public static final String MESSAGE_RECEIVED = "messageRcvd";
    public static final String SENDER_ID = "senderId";
    public static final String RECEIVER_ID = "receiverId";
    public static final String MESSAGE_ID = "messageId";
    public static final String MESSAGE_DATA = "messageData";
}

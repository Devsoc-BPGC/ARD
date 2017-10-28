package com.macbitsgoa.ard.types;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by vikramaditya on 25/10/17.
 */

public class MessageStatusType {

    /**
     * Only allow fields to be used instead of pure numbers
     */
    @IntDef({MSG_WAIT, MSG_SENT, MSG_RCVD, MSG_READ})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MessageStatus {}


    /**
     * Int value when message is in queue to be sent to receiver.
     */
    public static final int MSG_WAIT = 0;

    /**
     * Int value when message has been sent to server.
     */
    public static final int MSG_SENT = 1;

    /**
     * Int value when message has been received.
     */
    public static final int MSG_RCVD = 2;

    /**
     * Int value when message has been read.
     */
    public static final int MSG_READ = 3;

}

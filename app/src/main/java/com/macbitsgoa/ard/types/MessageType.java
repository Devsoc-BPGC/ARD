package com.macbitsgoa.ard.types;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Constants to define type of message.
 * Posible options are
 * <ul>
 * <li><b>TEXT</b> for text messages </li>
 * <li><b>DOCUMENT</b> for files</li>
 * </ul>
 *
 * @author Vikramaditya Kukreja
 */

public class MessageType {
    /**
     * Int value when text is message.
     */
    public static final int TEXT = 0;
    /**
     * Int value when document is message.
     */
    public static final int DOCUMENT = 1;

    /**
     * Only allow fields to be used instead of pure numbers
     */
    @IntDef({TEXT, DOCUMENT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MessageStatus {
    }

}

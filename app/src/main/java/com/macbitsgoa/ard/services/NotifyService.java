package com.macbitsgoa.ard.services;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.macbitsgoa.ard.keys.ChatItemKeys;
import com.macbitsgoa.ard.keys.MessageItemKeys;
import com.macbitsgoa.ard.models.ChatsItem;
import com.macbitsgoa.ard.models.MessageItem;
import com.macbitsgoa.ard.types.MessageStatusType;
import com.macbitsgoa.ard.utils.AHC;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * Intent service used to notify other user that a message has been read.
 *
 * @author Vikramaditya Kukreja
 */
public class NotifyService extends BaseIntentService {

    /**
     * Tag for this class.
     */
    public static final String TAG = NotifyService.class.getSimpleName();

    public NotifyService() {
        super(NotifyService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable final Intent intent) {
        if (intent == null || getUser() == null) return;
        final String receiverId = intent.getStringExtra("receiverId");
        if (receiverId == null) {
            Log.e(TAG, "Receiver id was null");
            return;
        }

        final Realm database = Realm.getDefaultInstance();
        final RealmList<MessageItem> notifyList = new RealmList<>();
        notifyList.addAll(database
                .where(MessageItem.class)
                .equalTo(MessageItemKeys.SENDER_ID, receiverId)
                .equalTo(MessageItemKeys.MESSAGE_RECEIVED, true)
                .lessThanOrEqualTo(MessageItemKeys.MESSAGE_STATUS, MessageStatusType.MSG_RCVD)
                .findAll());
        Log.e(TAG, "For id " + receiverId + ", messages unread = " + notifyList.size());
        final DatabaseReference readStatusRef = getRootReference()
                .child(AHC.FDR_CHAT)
                .child(receiverId)
                .child(ChatItemKeys.PRIVATE_MESSAGES)
                .child(getUser().getUid())
                .child(ChatItemKeys.MESSAGE_STATUS);

        for (final MessageItem mi : notifyList) {
            readStatusRef.child(mi.getMessageId()).setValue(MessageStatusType.MSG_READ);
            Log.e(TAG, "message read notif sent for " + mi.getMessageId());
            database.executeTransaction(r -> {
                final MessageItem mItem = r
                        .where(MessageItem.class)
                        .equalTo(MessageItemKeys.MESSAGE_ID, mi.getMessageId())
                        .findFirst();
                mItem.setMessageStatus(MessageStatusType.MSG_READ);
            });
        }
        database.executeTransaction(r -> {
            final ChatsItem ci = r
                    .where(ChatsItem.class)
                    .equalTo("id", receiverId)
                    .findFirst();
            Log.e(TAG, "Chat item count set to 0");
            ci.setUnreadCount(0);
        });
        database.close();
    }
}

package com.macbitsgoa.ard.services;

import android.content.Intent;
import android.support.annotation.Nullable;

import com.google.firebase.database.DatabaseReference;
import com.macbitsgoa.ard.keys.ChatItemKeys;
import com.macbitsgoa.ard.models.MessageItem;
import com.macbitsgoa.ard.types.MessageStatusType;
import com.macbitsgoa.ard.utils.AHC;

import io.realm.Realm;

/**
 * Intent service used to notify other user that a message has been read.
 *
 * @author Vikramaditya Kukreja
 */
public class NotifyService extends BaseIntentService {

    public NotifyService() {
        super(NotifyService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null || getUser() == null) return;
        String receiverId = intent.getStringExtra("receiverId");
        String messageId = intent.getStringExtra("messageId");
        if (receiverId == null || messageId == null) return;

        DatabaseReference readRef = getRootReference()
                .child(AHC.FDR_CHAT)
                .child(receiverId)
                .child(ChatItemKeys.PRIVATE_MESSAGES)
                .child(getUser().getUid())
                .child(ChatItemKeys.MESSAGE_STATUS)
                .child(messageId);
        readRef.setValue(MessageStatusType.MSG_READ);
        Realm database = Realm.getDefaultInstance();
        database.executeTransaction(r -> {
            MessageItem mi = r
                    .where(MessageItem.class)
                    .equalTo("messageId", messageId)
                    .equalTo("senderId", receiverId)
                    .findFirst();
            mi.setMessageStatus(MessageStatusType.MSG_READ);
        });
        database.close();
    }
}

package com.macbitsgoa.ard.services;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.activities.ChatActivity;
import com.macbitsgoa.ard.models.ChatsItem;
import com.macbitsgoa.ard.models.MessageItem;
import com.macbitsgoa.ard.types.MessageStatusType;
import com.macbitsgoa.ard.utils.AHC;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by vikramaditya on 30/10/17.
 */

public class NotificationService extends IntentService {


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public NotificationService() {
        super("NotificationService");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public NotificationService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        final Realm database = Realm.getDefaultInstance();
        final RealmResults<MessageItem> unreadMessagesUsers = database.where(MessageItem.class)
                .equalTo("messageRcvd", true)
                .lessThanOrEqualTo("messageStatus", MessageStatusType.MSG_RCVD)
                .distinct("senderId");

        final List<ChatsItem> chatsItems = new ArrayList<>();
        for (MessageItem mi : unreadMessagesUsers) {
            final ChatsItem ci = database.where(ChatsItem.class).equalTo("id", mi.getSenderId()).findFirst();
            if (ci == null) continue;
            //TODO handle null case. is it req?
            chatsItems.add(ci);
        }
        for (ChatsItem ci : chatsItems) {
            RealmResults<MessageItem> unreadMessages = database.where(MessageItem.class)
                    .equalTo("senderId", ci.getId())
                    .equalTo("messageRcvd", true)
                    .lessThanOrEqualTo("messageStatus", MessageStatusType.MSG_RCVD)
                    .findAllSorted(new String[]{"messageRcvdTime", "messageTime"}, new Sort[]{Sort.DESCENDING, Sort.DESCENDING});
            if (unreadMessages.size() == 0) continue;
            Intent piIntent = new Intent(this, ChatActivity.class);
            piIntent.putExtra("title", ci.getName());
            piIntent.putExtra("senderId", ci.getId());
            piIntent.putExtra("photoUrl", ci.getPhotoUrl());
            PendingIntent pi = PendingIntent.getActivity(this, 123, piIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle()
                    .setBigContentTitle(ci.getName())
                    .addLine(AHC.getSimpleTime(unreadMessages.get(0).getMessageTime())
                            + ": "
                            + unreadMessages.get(0).getMessageData())
                    .setSummaryText(unreadMessages.size() + " new message");
            if (unreadMessages.size() > 1)
                inboxStyle = inboxStyle.addLine(AHC.getSimpleTime(unreadMessages.get(1).getMessageTime())
                        + ": "
                        + unreadMessages.get(1).getMessageData())
                        .setSummaryText(unreadMessages.size() + " new messages");
            if (unreadMessages.size() > 2)
                inboxStyle = inboxStyle
                        .addLine(AHC.getSimpleTime(unreadMessages.get(2).getMessageTime())
                                + ": "
                                + unreadMessages.get(2).getMessageData());

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setAutoCancel(true)
                    .setContentIntent(pi)
                    .setContentTitle(ci.getName())
                    .setContentText(unreadMessages.size() + " new " + (unreadMessages.size() > 1 ? "messages" : "message"))
                    .setShowWhen(true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setTicker("New from " + ci.getName())
                    .setStyle(inboxStyle);

            NotificationManagerCompat nmc = NotificationManagerCompat.from(this);
            //nmc.cancel(ci.getId().hashCode());
            nmc.notify(ci.getId().hashCode(), builder.build());
            Intent intentBC = new Intent("cancel.notification." + ci.getId());
            sendBroadcast(intentBC);
        }
        database.close();
    }
}

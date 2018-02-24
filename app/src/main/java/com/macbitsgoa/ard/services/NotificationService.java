package com.macbitsgoa.ard.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.Html;
import android.util.Log;

import com.macbitsgoa.ard.BuildConfig;
import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.activities.AnnActivity;
import com.macbitsgoa.ard.activities.ChatActivity;
import com.macbitsgoa.ard.keys.AnnItemKeys;
import com.macbitsgoa.ard.keys.ChatItemKeys;
import com.macbitsgoa.ard.keys.MessageItemKeys;
import com.macbitsgoa.ard.models.AnnItem;
import com.macbitsgoa.ard.models.ChatsItem;
import com.macbitsgoa.ard.models.MessageItem;
import com.macbitsgoa.ard.types.MessageStatusType;
import com.macbitsgoa.ard.utils.AHC;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Service to show various section updates. Currently chats and announcements are shown.
 * Announcements that have {@link AnnItem#read} status as {@code false} are include.
 * For chats those that have {@link MessageStatusType} as {@code MessageStatusType.MSG_RCVD} or
 * from other senders is shown.
 *
 * @author Vikramaditya Kukreja
 */

public class NotificationService extends BaseIntentService {

    /**
     * TAG for class.
     */
    public static final String TAG = NotificationService.class.getSimpleName();

    /**
     * Request code for alarm manager.
     */
    public static final int RC = 90;

    public static final int ANN_NOTIF_CODE = 193;

    /**
     * Realm database.
     */
    private Realm database;


    /**
     * Notification manager.
     */
    private NotificationManagerCompat nmc;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public NotificationService() {
        super("NotificationService");
    }

    @Override
    protected void onHandleIntent(@Nullable final Intent intent) {
        if (getUser() == null) return;
        database = Realm.getDefaultInstance();
        nmc = NotificationManagerCompat.from(this);
        chatNotifications();
        announcementNotifications();
        database.close();
    }

    /**
     * Genereate notifications for new announcements.
     */
    private void announcementNotifications() {
        final int pIntentCode = 192;

        ApplicationInfo appInfo = null;
        try {
            appInfo = getPackageManager().getApplicationInfo(BuildConfig.APPLICATION_ID, 0);
        } catch (final PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Error in getting installed date info, " + e.toString());
        }
        final String appFile;
        long installedTime = Long.MAX_VALUE;
        if (appInfo != null)
        {
            appFile = appInfo.sourceDir;
            installedTime = new File(appFile).lastModified();
        }

        if (AnnActivity.isActive || installedTime == Long.MAX_VALUE) return;

        final RealmList<AnnItem> annItems = new RealmList<>();
        final Date date = new Date(installedTime);
        Log.e(TAG, "installed date as " + date);
        annItems.addAll(database.where(AnnItem.class)
                .equalTo(AnnItemKeys.READ, false).findAll());
        if (annItems.isEmpty()) return;

        final Intent intent = new Intent(this, AnnActivity.class);
        final PendingIntent pIntent = PendingIntent.getActivity(this,
                pIntentCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        final NotificationCompat.Builder builder
                = new NotificationCompat.Builder(this, "Announcements")
                .setAutoCancel(true)
                .setContentIntent(pIntent)
                .setShowWhen(true)
                .setVibrate(new long[]{Notification.DEFAULT_VIBRATE})
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("New announcement from ARD");

        if (annItems.size() == 1) {
            builder.setContentTitle(annItems.get(0).getData())
                    .setContentText(Html.fromHtml(annItems.get(0).getData()))
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(Html.fromHtml(annItems.get(0).getData()))
                            .setBigContentTitle("ARD Announcement"));

        } else {
            final NotificationCompat.InboxStyle is = new NotificationCompat.InboxStyle()
                    .setBigContentTitle("ARD Announcements");
            for (int i = 0; i < annItems.size(); i++) {
                is.addLine(Html.fromHtml(annItems.get(i).getData()));
            }
            builder.setContentTitle(annItems.size() + " new announcements")
                    .setContentText(Html.fromHtml("Latest: " + annItems.get(0).getData()))
                    .setStyle(is);
        }
        nmc.notify(ANN_NOTIF_CODE, builder.build());
    }

    private void chatNotifications() {
        final RealmResults<MessageItem> unreadMessagesUsers = database.where(MessageItem.class)
                .equalTo(MessageItemKeys.MESSAGE_RECEIVED, true)
                .lessThanOrEqualTo(MessageItemKeys.MESSAGE_STATUS, MessageStatusType.MSG_RCVD)
                .distinct(MessageItemKeys.SENDER_ID);

        final List<ChatsItem> chatsItems = new ArrayList<>();
        for (final MessageItem mi : unreadMessagesUsers) {
            final ChatsItem ci = database
                    .where(ChatsItem.class)
                    .equalTo("id", mi.getSenderId())
                    .findFirst();
            if (ci == null) continue;
            //TODO handle null case. is it req?
            chatsItems.add(ci);
        }
        for (final ChatsItem ci : chatsItems) {
            final RealmResults<MessageItem> unreadMessages = database.where(MessageItem.class)
                    .equalTo(MessageItemKeys.SENDER_ID, ci.getId())
                    .equalTo(MessageItemKeys.MESSAGE_RECEIVED, true)
                    .lessThanOrEqualTo(MessageItemKeys.MESSAGE_STATUS, MessageStatusType.MSG_RCVD)
                    .findAllSorted(new String[]{"messageRcvdTime", "messageTime"},
                            new Sort[]{Sort.DESCENDING, Sort.DESCENDING});
            if (unreadMessages.size() == 0) continue;
            final Intent piIntent = new Intent(this, ChatActivity.class);
            piIntent.putExtra("title", ci.getName());
            piIntent.putExtra(MessageItemKeys.SENDER_ID, ci.getId());
            piIntent.putExtra("photoUrl", ci.getPhotoUrl());

            final PendingIntent pi = PendingIntent
                    .getActivity(this,
                            123,
                            piIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle()
                    .setBigContentTitle(ci.getName())
                    .addLine(AHC.getSimpleTime(unreadMessages.get(0).getMessageTime())
                            + ": "
                            + unreadMessages.get(0).getMessageData())
                    .setSummaryText(unreadMessages.size() + " new message");
            if (unreadMessages.size() > 1)
                inboxStyle = inboxStyle
                        .addLine(AHC.getSimpleTime(unreadMessages.get(1).getMessageTime())
                                + ": "
                                + unreadMessages.get(1).getMessageData())
                        .setSummaryText(unreadMessages.size() + " new messages");
            if (unreadMessages.size() > 2)
                inboxStyle = inboxStyle
                        .addLine(AHC.getSimpleTime(unreadMessages.get(2).getMessageTime())
                                + ": "
                                + unreadMessages.get(2).getMessageData());

            final NotificationCompat.Builder builder
                    = new NotificationCompat.Builder(this, ci.getId())
                    .setAutoCancel(true)
                    .setContentIntent(pi)
                    .setContentTitle(ci.getName())
                    .setContentText(unreadMessages.size()
                            + " new " + (unreadMessages.size() > 1 ? "messages" : "message"))
                    .setShowWhen(true)
                    .setVibrate(new long[]{Notification.DEFAULT_VIBRATE})
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setTicker("New from " + ci.getName())
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setStyle(inboxStyle);

            Log.e(TAG, "Notification id -> "
                    + ci.getId().hashCode());

            //TODO improve not showing notif for current uesr
            if (ChatActivity.visible) {
                if (ChatActivity.otherUserId != null
                        && !ChatActivity.otherUserId.equals(ci.getId())) {
                    nmc.notify(ci.getId().hashCode(), builder.build());
                }
            } else nmc.notify(ci.getId().hashCode(), builder.build());

            final Intent notificationBC = new Intent(ChatItemKeys.NOTIFICATION_ACTION);
            notificationBC.putExtra(MessageItemKeys.SENDER_ID, ci.getId());
            sendBroadcast(notificationBC);
        }
    }
}

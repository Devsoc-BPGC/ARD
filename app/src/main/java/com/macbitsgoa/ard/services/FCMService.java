package com.macbitsgoa.ard.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.keys.AnnItemKeys;
import com.macbitsgoa.ard.keys.FCMKeys;
import com.macbitsgoa.ard.keys.FaqItemKeys;
import com.macbitsgoa.ard.keys.HomeItemKeys;
import com.macbitsgoa.ard.keys.UserItemKeys;
import com.macbitsgoa.ard.models.AnnItem;
import com.macbitsgoa.ard.models.FaqItem;
import com.macbitsgoa.ard.models.UserItem;
import com.macbitsgoa.ard.models.home.HomeItem;
import com.macbitsgoa.ard.utils.AHC;

import java.util.Map;

import io.realm.Realm;

/**
 * FCM service.
 * Supports
 * <ol>
 * <li>Deletion of item (HomeItem/AnnItem) from database</li>
 * <li>Notification that opens a url</li>
 * <li>Starting of Home Service in case of a new news item</li>
 * </ol>
 *
 * @author Vikramaditya Kukreja
 */
public class FCMService extends FirebaseMessagingService {

    /**
     * Tag for this class.
     */
    public static final String TAG = FCMService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        AHC.logd(TAG, "New FCM from " + remoteMessage.getFrom());
        final Map<String, String> data = remoteMessage.getData();
        if (data.size() == 0) {
            AHC.logd(TAG, "No data in FCM message");
            return;
        }
        //Data message
        final String action = data.get(FCMKeys.ACTION);
        AHC.logd(TAG, "Action received from FCM " + action);
        switch (action) {
            case FCMKeys.ACTION_SERVICE: {
                try {
                    scheduleJob(data);
                } catch (ActivityNotFoundException | ClassNotFoundException e) {
                    AHC.logd(TAG, e.toString());
                }
                break;
            }
            case FCMKeys.ACTION_VIEW: {
                createNotification(data);
                break;
            }
            case FCMKeys.ACTION_DELETE: {
                final String id = data.get(FCMKeys.ACTION_DELETE_ID);
                deleteItemFromRealm(id);
                break;
            }
            case FCMKeys.ACTION_ANNOUNCEMENT: {
                Intent intent = new Intent(this, AnnNotifyService.class);
                intent.putExtra(AnnItemKeys.AUTHOR, data.get(AnnItemKeys.AUTHOR));
                intent.putExtra(AnnItemKeys.DATA, data.get(AnnItemKeys.DATA));
                startService(intent);
                startService(new Intent(this, HomeService.class));
                break;
            }
            default: {
                AHC.logd(TAG, "Unrecognised action " + action);
                break;
            }
        }
    }

    private void deleteItemFromRealm(final String id) {
        if (id == null) {
            AHC.logd(TAG, "null id was sent");
            Crashlytics.log(0, TAG, "Null id was sent for deletion from Realm");
            return;
        }
        final Realm database = Realm.getDefaultInstance();
        database.executeTransaction(r -> {
            final HomeItem hi = r.where(HomeItem.class).equalTo(HomeItemKeys.KEY, id).findFirst();
            if (hi != null) {
                AHC.logd(TAG, "Found home item with same id to delete.");
                hi.deleteFromRealm();
            }
            final AnnItem ai = r.where(AnnItem.class).equalTo(AnnItemKeys.KEY, id).findFirst();
            if (ai != null) {
                AHC.logd(TAG, "Found announcement item with same id to delete.");
                ai.deleteFromRealm();
            }
            final FaqItem fi = r.where(FaqItem.class).equalTo(FaqItemKeys.KEY, id).findFirst();
            if (fi != null) {
                AHC.logd(TAG, "Found faq item with same id to delete.");
                fi.deleteFromRealm();
            }
            final UserItem ui = r.where(UserItem.class).equalTo(UserItemKeys.UID, id).findFirst();
            if (ui != null) {
                AHC.logd(TAG, "Found user item with same id to delete.");
                ui.deleteFromRealm();
            }
        });
        database.close();
    }

    private void createNotification(final Map<String, String> data) {
        AHC.logd(TAG, "Received a notification");
        AHC.logd(TAG, data.toString());

        final Uri uri = Uri.parse(data.get(FCMKeys.ACTION_VIEW_URI));
        final String title = data.get(FCMKeys.ACTION_VIEW_TITLE);
        final String text = data.get(FCMKeys.ACTION_VIEW_TEXT);
        final int notificationId = Integer.parseInt(data.get(FCMKeys.ID));

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        AHC.createChannels(nm);
        NotificationCompat.Builder ncb = new NotificationCompat.Builder(this, AHC.ARD)
                .setContentTitle(title)
                .setContentIntent(PendingIntent.getActivity(this, 143, new Intent(Intent.ACTION_VIEW, uri), PendingIntent.FLAG_UPDATE_CURRENT))
                .setAutoCancel(true)
                .setContentText(text)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOnlyAlertOnce(true)
                .setCategory(NotificationCompat.CATEGORY_RECOMMENDATION)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text).setBigContentTitle(title).setSummaryText(AHC.ARD))
                .setSmallIcon(R.mipmap.ic_launcher);

        nm.notify(notificationId, ncb.build());
    }

    private void scheduleJob(Map<String, String> data) throws ClassNotFoundException {
        AHC.logd(TAG, "Action is " + FCMKeys.ACTION_SERVICE);
        String service = data.get(FCMKeys.ACTION_SERVICE_NAME);
        if (!service.contains("Service")) {
            AHC.logd(TAG, "Action does not have a valid service. Found " + service);
            throw new ClassNotFoundException("Not a valid service");
        }

        if (service.equals(HomeService.TAG)) {
            AHC.startService(this, HomeService.class, HomeService.TAG);
        } else if (service.equals(MessagingService.TAG)) {
            AHC.startService(this, MessagingService.class, MessagingService.TAG);
            AHC.logd(TAG, "Messaging service will be started from FCM");
        } else {
            AHC.logd(TAG, "Requested service is not yet supported. Service was " + service);
            throw new ClassNotFoundException("Currently not supporting " + service);
        }
    }
}

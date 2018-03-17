package com.macbitsgoa.ard.services;

import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.macbitsgoa.ard.BuildConfig;
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
        Map<String, String> data = remoteMessage.getData();
        if (data.size() == 0 || !remoteMessage.getFrom().contains(BuildConfig.BUILD_TYPE)) {
            return;
        }
        //Data message
        String action = data.get(FCMKeys.ACTION);
        AHC.logd(TAG, "Action received is " + action);
        switch (action) {
            case FCMKeys.ACTION_SERVICE: {
                try {
                    startService(getIntent(data));
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
            default:
                break;
        }
    }

    private void deleteItemFromRealm(String id) {
        if (id == null) return;
        Realm database = Realm.getDefaultInstance();
        database.executeTransaction(r -> {
            HomeItem hi = r.where(HomeItem.class).equalTo(HomeItemKeys.KEY, id).findFirst();
            if (hi != null) {
                AHC.logd(TAG, "Found home item with same id to delete.");
                hi.deleteFromRealm();
            }
            AnnItem ai = r.where(AnnItem.class).equalTo(AnnItemKeys.KEY, id).findFirst();
            if (ai != null) {
                AHC.logd(TAG, "Found announcement item with same id to delete.");
                ai.deleteFromRealm();
            }
            FaqItem fi = r.where(FaqItem.class).equalTo(FaqItemKeys.KEY, id).findFirst();
            if (fi != null) {
                AHC.logd(TAG, "Found faq item with same id to delete.");
                fi.deleteFromRealm();
            }
            UserItem ui = r.where(UserItem.class).equalTo(UserItemKeys.UID, id).findFirst();
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

        NotificationManagerCompat nmc = NotificationManagerCompat.from(this);
        NotificationCompat.Builder ncb = new NotificationCompat.Builder(this, "FCM view")
                .setContentTitle(title)
                .setContentIntent(PendingIntent.getActivity(this, 143, new Intent(Intent.ACTION_VIEW, uri), PendingIntent.FLAG_UPDATE_CURRENT))
                .setAutoCancel(true)
                .setContentText(text)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOnlyAlertOnce(true)
                .setCategory(NotificationCompat.CATEGORY_RECOMMENDATION)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text).setBigContentTitle(title).setSummaryText("ARD"))
                .setSmallIcon(R.mipmap.ic_launcher);

        nmc.notify(notificationId, ncb.build());
    }

    private Intent getIntent(Map<String, String> data) throws ClassNotFoundException {
        AHC.logd(TAG, "Action is " + FCMKeys.ACTION_SERVICE);
        String service = data.get(FCMKeys.ACTION_SERVICE_NAME);
        if (!service.contains("Service")) {
            AHC.logd(TAG, "Action does not have a valid service. Found " + service);
            throw new ClassNotFoundException("Not a valid service");
        }
        if (service.equals("HomeService")) {
            String ltl = data.get(FCMKeys.ACTION_LIMIT_TO_LAST);
            int limitToLast = ltl == null ? 1 : Integer.parseInt(ltl);
            Intent intent = new Intent(this, HomeService.class);
            intent.putExtra(HomeService.LIMIT_TO_LAST, limitToLast);
            AHC.logd(TAG, "Home Service will be started with ltl = " + limitToLast);
            return intent;
        } else {
            AHC.logd(TAG, "Requested service is not yet supported. Service was " + service);
            throw new ClassNotFoundException("Currently not supporting " + service);
        }
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }
}

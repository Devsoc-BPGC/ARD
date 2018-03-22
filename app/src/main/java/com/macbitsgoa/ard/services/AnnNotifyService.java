package com.macbitsgoa.ard.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.activities.AnnActivity;
import com.macbitsgoa.ard.keys.AnnItemKeys;
import com.macbitsgoa.ard.utils.AHC;

/**
 * Service to notify Announcement data. Intent should contain author and data strings as extras.
 * Use keys {@link AnnItemKeys#AUTHOR} and {@link AnnItemKeys#DATA}.
 *
 * @author Vikramaditya Kukreja.
 */

public class AnnNotifyService extends BaseIntentService {

    /**
     * Tag for this class.
     */
    public static final String TAG = AnnNotifyService.class.getSimpleName();

    /**
     * Creates an IntentService.
     */
    public AnnNotifyService() {
        super(AnnNotifyService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        final String author = intent.getStringExtra(AnnItemKeys.AUTHOR);
        final String data = intent.getStringExtra(AnnItemKeys.DATA);
        final int id = data.hashCode();

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        AHC.createChannels(nm);
        NotificationCompat.Builder ncb = new NotificationCompat.Builder(this, AHC.ARD)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(data)
                .setAutoCancel(true)
                .setContentIntent(PendingIntent
                        .getActivity(this, id,
                                new Intent(this, AnnActivity.class),
                                PendingIntent.FLAG_UPDATE_CURRENT))
                .setContentTitle("New announcement from ARD")
                .setCategory(NotificationCompat.CATEGORY_RECOMMENDATION)
                .setShowWhen(true)
                .setOnlyAlertOnce(true)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                .setColorized(true)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(data)
                        .setBigContentTitle("New announcement from ARD")
                        .setSummaryText(author));
        nm.notify(id, ncb.build());
    }
}

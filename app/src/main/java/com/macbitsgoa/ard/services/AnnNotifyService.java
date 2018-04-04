package com.macbitsgoa.ard.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.text.Html;

import com.firebase.jobdispatcher.JobParameters;
import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.activities.AnnActivity;
import com.macbitsgoa.ard.keys.AnnItemKeys;
import com.macbitsgoa.ard.utils.AHC;

/**
 * Service to notify Announcement data. Intent should contain author and data strings as extras.
 * Use keys {@link AnnItemKeys#AUTHOR} and {@link AnnItemKeys#DATA}. Only shows the latest message
 * as id is contant value. This service also checks if {@link AnnActivity} is running or not before
 * posting the notification.
 *
 * @author Vikramaditya Kukreja
 * @see #NOTIFICATION_ID
 */
public class AnnNotifyService extends BaseJobService {

    /**
     * Tag for this class.
     */
    public static final String TAG = AnnNotifyService.class.getSimpleName();

    /**
     * Id used to notify notification manager. In case of new announcement, old one is removed.
     */
    public static final int NOTIFICATION_ID = 223;

    @Override
    public boolean onStartJob(final JobParameters job) {
        //Don't display notification if AnnActivity is running
        if (AnnActivity.inForeground) {
            return false;
        }

        final Bundle extras = job.getExtras();
        final String author = extras.getString(AnnItemKeys.AUTHOR, AHC.DEFAULT_AUTHOR);
        final String data = extras.getString(AnnItemKeys.DATA, "Announcement");
        final int id = data.hashCode();

        final NotificationManager nm = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        AHC.createChannels(nm);
        final NotificationCompat.Builder ncb = new NotificationCompat.Builder(this, AHC.ARD)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(Html.fromHtml(data))
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
        return false;
    }

    @Override
    public boolean onStopJob(final JobParameters job) {
        return true;
    }
}

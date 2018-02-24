package com.macbitsgoa.ard.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.macbitsgoa.ard.utils.AHC;

/**
 * BroadcastReceiver to start {@link MessagingService} on boot.
 *
 * @author Vikramaditya Kukreja
 */

public class BootBroadcastReceiver extends BroadcastReceiver {
    /**
     * TAG for class.
     */
    public static final String TAG = BootBroadcastReceiver.class.getSimpleName();

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (intent == null) return;
        if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)
                || intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)
                || intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            AHC.logd(TAG, "Starting services, action was " + intent.getAction());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(new Intent(context, ServicesStarter.class));
            } else {
                context.startService(new Intent(context, ServicesStarter.class));
            }
        }
    }
}

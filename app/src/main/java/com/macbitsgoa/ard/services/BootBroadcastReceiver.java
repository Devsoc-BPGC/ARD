package com.macbitsgoa.ard.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

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
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Log.e(TAG, "Starting services");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(new Intent(context, ServicesStarter.class));
            } else {
                context.startService(new Intent(context, ServicesStarter.class));
            }
        }
    }
}

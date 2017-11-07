package com.macbitsgoa.ard.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * BroadcastReceiver to start {@link MessagingService} on boot.
 *
 * @author Vikramaditya Kukreja
 */

public class BootBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) return;
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            // Set the alarm here.
            Log.d("BootBroadcastReceiver", "Starting MessagingService");
            context.startService(new Intent(context, MessagingService.class));
        }
    }
}

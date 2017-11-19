package com.macbitsgoa.ard.services;

import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * All services are started here. Especially those which do not require an intent with extras.
 *
 * @author Vikramaditya Kukreja
 */
public class ServicesStarter extends BaseIntentService {
    /**
     * Default no. of items to download.
     */
    public static final int DEFAULT_LIMIT_TO_LAST = 10;

    public ServicesStarter() {
        super(ServicesStarter.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable final Intent intent) {
        super.onHandleIntent(intent);
        startService(new Intent(this, MessagingService.class));
        startService(new Intent(this, NotificationService.class));
        startService(new Intent(this, SendService.class));

        final Intent homeServiceIntent = new Intent(this, HomeService.class);
        homeServiceIntent.putExtra(HomeService.LIMIT_TO_LAST, DEFAULT_LIMIT_TO_LAST);
        startService(homeServiceIntent);
    }
}

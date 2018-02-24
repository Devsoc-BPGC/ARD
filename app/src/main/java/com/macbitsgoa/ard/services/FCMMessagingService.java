package com.macbitsgoa.ard.services;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.macbitsgoa.ard.utils.AHC;

/**
 * Created by vikramaditya on 22/2/18.
 */

public class FCMMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        AHC.logd("tag", remoteMessage.getData().toString());
    }
}

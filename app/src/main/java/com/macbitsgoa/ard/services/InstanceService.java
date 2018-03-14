package com.macbitsgoa.ard.services;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.macbitsgoa.ard.utils.AHC;

/**
 * Used to update current user token.
 *
 * @author Vikramaditya Kukreja
 */
public class InstanceService extends FirebaseInstanceIdService {
    private static final String TAG = InstanceService.class.getSimpleName();

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        AHC.logd(TAG, "Refreshed token: " + refreshedToken);
        AHC.sendRegistrationToServer(refreshedToken);
        FirebaseMessaging.getInstance().subscribeToTopic(AHC.FDR_USERS);
    }
}

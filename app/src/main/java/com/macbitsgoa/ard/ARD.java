package com.macbitsgoa.ard;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Process;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.macbitsgoa.ard.utils.AHC;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Custom class extending the Application class.
 * This is used for setting up libraries.
 *
 * @author Vikramaditya Kukreja
 */
public class ARD extends Application {

    /**
     * TAG for the class.
     */
    public static final String TAG = ARD.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        if (!getProcessName(Process.myPid()).endsWith("BackgroundServices")) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        } else {
            //Required for Background services
            FirebaseApp.initializeApp(this);
        }
        Realm.init(this);
        final RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .name(AHC.REALM_ARD_DATABASE)
                .schemaVersion(AHC.REALM_ARD_DATABASE_SCHEMA)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }

    /**
     * Method to get process name. Eg. com.macbitsgoa.ard.debug:BackgroundServices
     *
     * @param pID process id.
     * @return Process name.
     */
    private String getProcessName(final int pID) {
        final ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final List l = am.getRunningAppProcesses();
        for (final Object aL : l) {
            final ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) aL;
            if (info.pid == pID) {
                Log.d(TAG, "Id: " + info.pid + " ProcessName: " + info.processName);
                return info.processName;
            }
        }
        return BuildConfig.APPLICATION_ID;
    }
}

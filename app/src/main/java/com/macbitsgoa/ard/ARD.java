package com.macbitsgoa.ard;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;
import com.macbitsgoa.ard.utils.AHC;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Custom class extending the Application class.
 * This is used for setting up libraries.
 *
 * @author Vikramaditya Kukreja
 */
public class ARD extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        Realm.init(this);
        final RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .name(AHC.REALM_ARD_DATABASE)
                .schemaVersion(AHC.REALM_ARD_DATABASE_SCHEMA)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }
}

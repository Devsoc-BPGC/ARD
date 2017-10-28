package com.macbitsgoa.ard.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.macbitsgoa.ard.BuildConfig;

import io.realm.Realm;

/**
 * Created by vikramaditya on 29/10/17.
 */

public class BaseService extends IntentService {
    protected Realm database;

    public BaseService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        database = Realm.getDefaultInstance();
        Log.e("TAG", "service created");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        database.close();
        Log.e("TAG", "service destroyed");
    }

    protected DatabaseReference getRootReference() {
        return FirebaseDatabase.getInstance().getReference().child(BuildConfig.BUILD_TYPE);
    }

    @Nullable
    protected FirebaseUser getUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

}

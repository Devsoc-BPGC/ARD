package com.macbitsgoa.ard.services;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.macbitsgoa.ard.BuildConfig;

/**
 * Base service class with useful methods.
 *
 * @author Vikramaditya Kukreja
 */
@SuppressLint("Registered")
public class BaseIntentService extends IntentService {
    public BaseIntentService(final String name) {
        super(name);
    }

    protected DatabaseReference getRootReference() {
        return FirebaseDatabase.getInstance().getReference().child(BuildConfig.BUILD_TYPE);
    }

    protected StorageReference getStorageRef() {
        return FirebaseStorage.getInstance().getReference().child(BuildConfig.BUILD_TYPE);
    }

    @Nullable
    protected FirebaseUser getUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    protected void onHandleIntent(@Nullable final Intent intent) {

    }
}

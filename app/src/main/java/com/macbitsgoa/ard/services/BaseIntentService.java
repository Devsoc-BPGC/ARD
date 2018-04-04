package com.macbitsgoa.ard.services;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

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

    /**
     * Constructor with name parameter for IntentService.
     *
     * @param name Service name.
     */
    public BaseIntentService(final String name) {
        super(name);
        setIntentRedelivery(true);
    }

    protected void showDebugToast(@NonNull final String message) {
        if (BuildConfig.DEBUG) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    protected DatabaseReference getRootReference() {
        return FirebaseDatabase.getInstance().getReference().child(BuildConfig.BUILD_TYPE);
    }

    StorageReference getStorageRef() {
        return FirebaseStorage.getInstance().getReference().child(BuildConfig.BUILD_TYPE);
    }

    @Nullable
    FirebaseUser getUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    protected void onHandleIntent(@Nullable final Intent intent) {

    }
}

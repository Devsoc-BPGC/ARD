package com.macbitsgoa.ard.services;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.macbitsgoa.ard.BuildConfig;

/**
 * Created by vikramaditya on 20/3/18.
 *
 * @author Vikramaditya Kukreja
 */
@SuppressLint("Registered")
public class BaseJobService extends JobService {

    protected void showDebugToast(@NonNull final String message) {
        if (BuildConfig.DEBUG) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    protected DatabaseReference getRootReference() {
        return FirebaseDatabase.getInstance().getReference().child(BuildConfig.BUILD_TYPE);
    }

    protected StorageReference getStorageRef() {
        return FirebaseStorage.getInstance().getReference().child(BuildConfig.BUILD_TYPE);
    }

    @Nullable
    FirebaseUser getUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public boolean onStartJob(final JobParameters job) {
        return false;
    }

    @Override
    public boolean onStopJob(final JobParameters job) {
        return true;
    }
}

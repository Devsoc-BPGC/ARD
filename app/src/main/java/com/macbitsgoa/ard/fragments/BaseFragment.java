package com.macbitsgoa.ard.fragments;

import android.support.v4.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.macbitsgoa.ard.BuildConfig;

import javax.annotation.Nullable;

import io.realm.Realm;

/**
 * Created by vikramaditya on 26/10/17.
 */

public class BaseFragment extends Fragment {

    protected Realm database;

    protected DatabaseReference getRootReference() {
        return FirebaseDatabase.getInstance().getReference().child(BuildConfig.BUILD_TYPE);
    }

    @Nullable
    protected FirebaseUser getUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public void onStart() {
        super.onStart();
        database = Realm.getDefaultInstance();
    }

    @Override
    public void onStop() {
        super.onStop();
        database.close();
    }
}

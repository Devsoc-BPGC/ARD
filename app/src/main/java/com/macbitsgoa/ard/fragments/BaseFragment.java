package com.macbitsgoa.ard.fragments;

import android.support.v4.app.Fragment;
import android.view.View;

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

public class BaseFragment extends Fragment implements View.OnClickListener {

    protected Realm database;

    protected DatabaseReference getRootReference() {
        return FirebaseDatabase.getInstance().getReference().child(BuildConfig.BUILD_TYPE);
    }

    @Nullable
    protected FirebaseUser getUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    /**
     * Subclasses should call this at the start.
     */
    @Override
    public void onStart() {
        super.onStart();
        database = Realm.getDefaultInstance();
    }

    /**
     * Subclasses should call this method at the end.
     */
    @Override
    public void onStop() {
        super.onStop();
        database.close();
    }

    @Override
    public void onClick(final View v) {

    }
}

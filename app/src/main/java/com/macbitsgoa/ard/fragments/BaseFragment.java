package com.macbitsgoa.ard.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.macbitsgoa.ard.BuildConfig;
import com.macbitsgoa.ard.utils.AHC;

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

    /**
     * Get {@link SharedPreferences} for the app.
     *
     * @return app shared pref {@link AHC#SP_APP} in private mode.
     */
    public SharedPreferences getDefaultSharedPref() {
        return getContext().getSharedPreferences(AHC.SP_APP, Context.MODE_PRIVATE);
    }

    @Nullable
    protected FirebaseUser getUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    /**
     * Subclasses should call super at the start.
     * Realm database member is opened here.
     */
    @Override
    public void onStart() {
        super.onStart();
        database = Realm.getDefaultInstance();
    }

    /**
     * Subclasses should call super method at the end.
     * Realm database member is closed here.
     */
    @Override
    public void onStop() {
        super.onStop();
        database.close();
    }
}

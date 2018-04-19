package com.macbitsgoa.ard.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.IntegerRes;
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
 * Base fragment with useful methods.
 *
 * @author Vikramaditya Kukreja
 */
public class BaseFragment extends Fragment {

    /**
     * Realm database field.
     */
    protected Realm database;

    /**
     * Get root reference depending on build config value.
     *
     * @return Database root reference for current app.
     */
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

    /**
     * Get integer resource.
     *
     * @param resourceId Int resource id.
     * @return int value.
     */
    protected int getInteger(@IntegerRes final int resourceId) {
        return getResources().getInteger(resourceId);
    }

    /**
     * Method to get current user.
     *
     * @return Firebase user. Value is nullable.
     */
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

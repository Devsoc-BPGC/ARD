package com.macbitsgoa.ard.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.IntRange;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.macbitsgoa.ard.BuildConfig;
import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.utils.AHC;

import io.realm.Realm;

/**
 * Base activity with useful methods.
 *
 * @author Vikramaditya Kukreja
 */
@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {

    /**
     * Realm database object. This object is initialised in {@link BaseActivity}'s
     * {@link #onCreate(Bundle)} and closed in {@link BaseActivity}'s {@link #onDestroy()}.
     * Activities that extend this class should call all closing functions before the
     * {@code super.onDestroy()} method to prevent ANR because of closed Realm database.
     */
    Realm database;

    /**
     * Get {@link SharedPreferences} for the app.
     *
     * @return app shared pref {@link AHC#SP_APP} in private mode.
     */
    public SharedPreferences getDefaultSharedPref() {
        return getSharedPreferences(AHC.SP_APP, Context.MODE_PRIVATE);
    }

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (FirebaseAuth.getInstance().getCurrentUser() == null && !(this instanceof AuthActivity)) {
            startActivity(new Intent(this, AuthActivity.class));
        }
        database = Realm.getDefaultInstance();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        database.close();
    }

    /**
     * Get string value from resource.
     *
     * @param stringResource Id of resource.
     * @return String value.
     */
    public String getStringRes(@StringRes final int stringResource) {
        return getResources().getString(stringResource);
    }

    /**
     * Get integer value from resource.
     *
     * @param intResource Id of resource.
     * @return int value.
     */
    public int getIntegerRes(@IntegerRes final int intResource) {
        return getResources().getInteger(intResource);
    }


    /**
     * Returns the root reference for the Firebase Database.
     * It varies depending on the build.
     *
     * @return root reference for the current build type.
     */
    public DatabaseReference getRootReference() {
        return FirebaseDatabase.getInstance().getReference().getRoot().child(BuildConfig.BUILD_TYPE);
    }

    /**
     * Get current Firebase user
     *
     * @return Firebase user. May be null
     */
    @Nullable
    FirebaseUser getUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }


    /**
     * Displays a toast in current activity. In this method the duration
     * supplied is {@link Toast#LENGTH_SHORT} that the toast shows.
     *
     * @param message Message to be displayed.
     */
    void showToast(final String message) {
        showToast(message, Toast.LENGTH_SHORT);
    }

    /**
     * Displays a toast in current activity. The duration can of two types:
     * <ul>
     * <li>{@link Toast#LENGTH_SHORT}</li>
     * <li>{@link Toast#LENGTH_LONG}</li>
     * </ul>
     *
     * @param message   Message that the toast must show.
     * @param toastType Duration for which the toast must be visible.
     */
    private void showToast(final String message,
                           @IntRange(from = Toast.LENGTH_SHORT,
                                   to = Toast.LENGTH_LONG) final int toastType) {
        Toast.makeText(this, message, toastType).show();
    }

    /**
     * Show simple snack. Default duration is {@link Snackbar#LENGTH_SHORT}.
     * Text color is {@link Color#WHITE} and background color is {@link Color#BLACK}.
     *
     * @param message Message to be displayed.
     */
    public void showSnack(@NonNull final String message) {
        showSnack(message, Snackbar.LENGTH_SHORT);
    }

    /**
     * Show simple snack. Default duration is {@link Snackbar#LENGTH_SHORT}.
     * Text color is {@link Color#WHITE} and background color is {@link Color#BLACK}.
     * <p>
     * Length can be defined as
     * <ul>
     * <li>{@link Snackbar#LENGTH_SHORT}</li>
     * <li>{@link Snackbar#LENGTH_LONG}</li>
     * <li>{@link Snackbar#LENGTH_INDEFINITE}</li>
     * </ul>
     *
     * @param message Message to be displayed.
     * @param length  Int value to be used as length.
     */
    private void showSnack(@NonNull final String message, final int length) {
        showSnack(getWindow().getDecorView(), message, length);
    }

    /**
     * Show simple snack. Default duration is {@link Snackbar#LENGTH_SHORT}.
     * Text color is {@link Color#WHITE} and background color is {@link Color#BLACK}.
     * <p>
     * Length can be defined as
     * <ul>
     * <li>{@link Snackbar#LENGTH_SHORT}</li>
     * <li>{@link Snackbar#LENGTH_LONG}</li>
     * <li>{@link Snackbar#LENGTH_INDEFINITE}</li>
     * </ul>
     *
     * @param view    View to be used.
     * @param message Message to be displayed.
     * @param length  Int value to be used as length.
     */
    private void showSnack(@NonNull final View view,
                           @NonNull final String message, final int length) {
        showSnack(message, length, getWindow().getDecorView(), R.color.white, R.color.black);
    }


    /**
     * Show simple snack. Default duration is {@link Snackbar#LENGTH_SHORT}.
     * Text color is {@link Color#WHITE} and background color is {@link Color#BLACK}.
     * <p>
     * Length can be defined as
     * <ul>
     * <li>{@link Snackbar#LENGTH_SHORT}</li>
     * <li>{@link Snackbar#LENGTH_LONG}</li>
     * <li>{@link Snackbar#LENGTH_INDEFINITE}</li>
     * </ul>
     *
     * @param view            View to be used.
     * @param message         Message to be displayed.
     * @param length          Int value to be used as length.
     * @param textColor       Color res for textColor.
     * @param backgroundColor Color res for backgroundColor.
     */
    private void showSnack(@NonNull final String message, final int length,
                           @NonNull final View view, @ColorRes final int textColor,
                           @ColorRes final int backgroundColor) {
        final Snackbar snackbar = Snackbar.make(view, message, length);
        final TextView snackBarText = snackbar.getView()
                .findViewById(R.id.snackbar_text);
        snackBarText.setTextColor(ContextCompat.getColor(this, textColor));
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(this, backgroundColor));
        snackbar.show();
    }

}

package com.macbitsgoa.ard.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.messaging.FirebaseMessaging;
import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.helpers.AuthHelperForGoogle;
import com.macbitsgoa.ard.keys.AuthActivityKeys;
import com.macbitsgoa.ard.keys.UserItemKeys;
import com.macbitsgoa.ard.utils.AHC;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * AuthActivity authenticates user.
 * The backend functions are implemented in {@link AuthHelperForGoogle} class.
 * Currently implemented:
 * 1. Sign in with Google
 *
 * @author Rushikesh Jogdand
 */
public class AuthActivity extends BaseActivity implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener, OnCompleteListener<AuthResult> {

    /**
     * TAG for this activity.
     */
    private static final String TAG = AHC.TAG + ".AuthActivity";

    /**
     * AuthHelperForGoogle instance to handle backend functions.
     */
    public AuthHelperForGoogle mHelper;

    /**
     * Google Sign In Button.
     */
    @BindView(R.id.btn_content_auth_google)
    public Button googleSignInButton;

    /**
     * Google API Client for login purposes.
     */
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        ButterKnife.bind(this);

        googleApiClient = setupGoogleApiClient();
        mHelper = new AuthHelperForGoogle(this, FirebaseAuth.getInstance());

        googleSignInButton.setOnClickListener(this);
    }

    /**
     * Method to setup the googleApiClient.
     *
     * @return GoogleApiClient object for login.
     */
    private GoogleApiClient setupGoogleApiClient() {
        final GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.firebase_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();
        return new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //if (requestCode == AuthActivityKeys.RC_GOOGLE_SIGN_IN) {}
        if (resultCode == Activity.RESULT_CANCELED) {
            googleSignInButton.setClickable(true);
            if (pd != null)
                pd.cancel();
            showToast("Sign in cancelled");
            return;
        }
        mHelper.handleGoogleSignIn(data);
    }

    /**
     * Default response that user will get if anything in google sign in fails.
     * Later we can prompt user to use Guest mode if sign in fails
     */
    public void handleGoogleSignInFailure() {
        showToast(getString(R.string.error_google_sign_in_failed));
        googleSignInButton.setClickable(true);
        if (pd != null)
            pd.cancel();
    }

    @Override
    public void onClick(final View v) {
        //no need to verify id of view as only google sign in currently supported.
        v.setClickable(false);
        launchGoogleSignIn(Auth.GoogleSignInApi.getSignInIntent(googleApiClient));
    }

    @Override
    public void onConnectionFailed(@NonNull final ConnectionResult connectionResult) {
        Log.e(TAG, "Connection Failure " + connectionResult);
        handleGoogleSignInFailure();
        if (pd != null)
            pd.cancel();
    }

    ProgressDialog pd;

    /**
     * Launch Google Sign screen.
     *
     * @param intent Intent generated from GoogleSignInApi.
     */
    public void launchGoogleSignIn(@NonNull final Intent intent) {
        startActivityForResult(intent, AuthActivityKeys.RC_GOOGLE_SIGN_IN);
        pd = ProgressDialog.show(this, "Google sign in", "Signing in to ARD");
        pd.show();
    }

    @Override
    public void onComplete(@NonNull final Task<AuthResult> task) {
        onComplete(task, FirebaseAuth.getInstance());
    }

    /**
     * Extension of {@link #onComplete(Task)} which accepts a {@link FirebaseAuth} object.
     * This also helps in simplifying unit testing.
     *
     * @param task         Task object from interface.
     * @param firebaseAuth Auth object to use.
     */
    public void onComplete(final Task<AuthResult> task, final FirebaseAuth firebaseAuth) {
        if (!task.isSuccessful()) {
            handleGoogleSignInFailure();
            return;
        }
        updateUserInfo(firebaseAuth, getRootReference().child(AHC.FDR_USERS));
        if (pd != null)
            pd.cancel();
        FirebaseMessaging.getInstance().subscribeToTopic(AHC.FDR_USERS);
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }

    /**
     * Send relevant info of current user to firebase.
     *
     * @param firebaseAuth Firebase auth object to use.
     * @param parentRef    Reference to add/update child into.
     * @return boolean true if result was updated successfully, false otherwise.
     */
    public boolean updateUserInfo(@NonNull final FirebaseAuth firebaseAuth,
                                  @NonNull final DatabaseReference parentRef) {
        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            return false;
        }
        final String uid = firebaseUser.getUid();
        final String name = firebaseUser.getDisplayName();
        final String email = firebaseUser.getEmail();
        final String photoUrl;
        final Uri uri = firebaseUser.getPhotoUrl();
        if (uri == null) {
            photoUrl = "";
        } else {
            photoUrl = uri.toString();
        }
        final DatabaseReference userDb = parentRef.child(uid);
        userDb.child(UserItemKeys.NAME).setValue(name);
        userDb.child(UserItemKeys.EMAIL).setValue(email);
        userDb.child(UserItemKeys.PHOTO_URL).setValue(photoUrl);
        return true;
    }
}

package com.macbitsgoa.ard.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
import com.macbitsgoa.ard.BuildConfig;
import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.helpers.AuthHelperForGoogle;
import com.macbitsgoa.ard.keys.AuthActivityKeys;
import com.macbitsgoa.ard.keys.UserItemKeys;
import com.macbitsgoa.ard.utils.AHC;
import com.macbitsgoa.ard.utils.CenterCropDrawable;

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
public class AuthActivity extends BaseActivity implements
        View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener,
        OnCompleteListener<AuthResult>,
        GoogleApiClient.ConnectionCallbacks {

    /**
     * TAG for this activity.
     */
    public static final String TAG = AuthActivity.class.getSimpleName();

    /**
     * Google Sign In Button.
     */
    @BindView(R.id.btn_content_auth_google)
    Button googleSignInButton;

    /**
     * Textview to show current app version.
     */
    @BindView(R.id.tv_activity_auth_version)
    TextView versionTV;

    /**
     * AuthHelperForGoogle instance to handle backend functions.
     */
    private AuthHelperForGoogle mHelper;

    /**
     * Dialog to show when signing in.
     */
    private ProgressDialog pd;

    /**
     * Google API Client for login purposes.
     */
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            final Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        getWindow().setBackgroundDrawable(new CenterCropDrawable(this, R.drawable.auth_bg));
        setContentView(R.layout.activity_auth);
        ButterKnife.bind(this);
        versionTV.setText(BuildConfig.VERSION_NAME);

        pd = ProgressDialog.show(this, "Google sign in", "Signing in to ARD");
        pd.cancel();

        googleApiClient = setupGoogleApiClient();
        mHelper = new AuthHelperForGoogle(this, FirebaseAuth.getInstance());
    }

    /**
     * Method to setup the googleApiClient.
     *
     * @return GoogleApiClient object for login.
     */
    private GoogleApiClient setupGoogleApiClient() {
        final GoogleSignInOptions gso
                = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.firebase_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();
        return new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode,
                                    final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //if (requestCode == AuthActivityKeys.RC_GOOGLE_SIGN_IN) {}
        if (resultCode == Activity.RESULT_CANCELED) {
            handleGoogleSignInFailure("Sign in cancelled");
            return;
        }
        mHelper.handleGoogleSignIn(data);
    }

    /**
     * Default response that user will get if anything in google sign in fails.
     * Later we can prompt user to use Guest mode if sign in fails
     */
    public void handleGoogleSignInFailure() {
        handleGoogleSignInFailure(getString(R.string.error_google_sign_in_failed));
    }

    /**
     * Default response that user will get if anything in google sign in fails.
     * Later we can prompt user to use Guest mode if sign in fails
     *
     * @param message Message to show.
     */
    public void handleGoogleSignInFailure(final String message) {
        pd.cancel();
        showToast(message);
        googleSignInButton.setClickable(true);
    }

    @Override
    public void onClick(final View v) {
        //no need to verify id of view as only google sign in currently supported.
        //disable button clicking
        v.setClickable(false);

        final Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent, AuthActivityKeys.RC_GOOGLE_SIGN_IN);
        pd.show();
    }

    @Override
    public void onComplete(@NonNull final Task<AuthResult> task) {
        if (!task.isSuccessful()) {
            handleGoogleSignInFailure();
            return;
        }
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        //Check if email is a subset of BITS email ids
        if (!firebaseAuth.getCurrentUser()
                .getEmail()
                .matches(getString(R.string.regex_allowed_emails))) {
            handleGoogleSignInFailure("Please use institute email address for signing in");

            //Sign out currently logged in user
            FirebaseAuth.getInstance().signOut();
            Auth.GoogleSignInApi
                    .signOut(googleApiClient)
                    .setResultCallback(status -> googleSignInButton.setClickable(true));
        } else {
            updateUserInfo(firebaseAuth, getRootReference().child(AHC.FDR_USERS));
            pd.cancel();
            final Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    /**
     * Send relevant info of current user to firebase.
     *
     * @param firebaseAuth Firebase auth object to use.
     * @param parentRef    Reference to add/update child into.
     */
    public void updateUserInfo(@NonNull final FirebaseAuth firebaseAuth,
                               @NonNull final DatabaseReference parentRef) {
        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        final String uid = firebaseUser.getUid();
        final String name = firebaseUser.getDisplayName();
        final String email = firebaseUser.getEmail();
        final String phoneNumber = firebaseUser.getPhoneNumber();
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
        userDb.child(UserItemKeys.PHONE_NUMBER).setValue(phoneNumber);
    }

    /**
     * Connect to GoogleApiClient -> SignOut from Google account if Signed in -> enable onClick
     * GoogleApiClient connected.
     *
     * @param bundle ignored.
     */
    @Override
    public void onConnected(@Nullable final Bundle bundle) {
        Auth.GoogleSignInApi
                .signOut(googleApiClient)
                .setResultCallback(status -> googleSignInButton.setOnClickListener(this));
    }

    /**
     * {@link GoogleApiClient#connect()} failed.
     *
     * @param i the cause of failure. One of
     *          {@link GoogleApiClient.ConnectionCallbacks#CAUSE_NETWORK_LOST} or
     *          {@link GoogleApiClient.ConnectionCallbacks#CAUSE_SERVICE_DISCONNECTED}.
     */
    @Override
    public void onConnectionSuspended(final int i) {
        if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
            showToast(getString(R.string.network_lost));
        } else {
            showToast("Service disconnected. Try again");
        }
    }

    @Override
    public void onConnectionFailed(@NonNull final ConnectionResult connectionResult) {
        Log.e(TAG, "Connection Failure " + connectionResult);
        handleGoogleSignInFailure("Connection failed to Google APIs");
    }
}

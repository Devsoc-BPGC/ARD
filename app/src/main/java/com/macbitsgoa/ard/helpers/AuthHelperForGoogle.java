package com.macbitsgoa.ard.helpers;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.macbitsgoa.ard.activities.AuthActivity;

/**
 * Helper class to handle backend functions for {@link AuthActivity}.
 *
 * @author Rushikesh Jogdand
 * @author Vikramaditya Kukreja
 */
@SuppressWarnings("WeakerAccess")
public class AuthHelperForGoogle {

    /**
     * Do when signed in to firebase with credential provided by google.
     */
    public final OnCompleteListener<AuthResult> googleAuthCompleteListener;

    /**
     * Activity that uses this helper.
     */
    private final AuthActivity hostActivity;

    /**
     * FirebaseAuth object to use throughout this class.
     */
    private final FirebaseAuth firebaseAuth;

    /**
     * Constructor that takes in activity and auth object.
     * It also sets up the {@link OnCompleteListener} using the passed
     * activity.
     *
     * @param hostActivity calling activity.
     * @param firebaseAuth Firebase auth object to use.
     */
    public AuthHelperForGoogle(@NonNull final AuthActivity hostActivity,
                               @NonNull final FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
        this.hostActivity = hostActivity;
        this.googleAuthCompleteListener = hostActivity;
    }

    /**
     * Get a reference to FirebaseAuth being used by this class.
     * It returns the auth object that was used to setup this class.
     *
     * @return {@link FirebaseAuth} object used to create this class.
     */
    public FirebaseAuth getFBAuthInstance() {
        return firebaseAuth;
    }

    /**
     * Authenticate using google sign in data.
     *
     * @param data resulting from {@link AuthActivity#launchGoogleSignIn(Intent)}
     */
    public void handleGoogleSignIn(final Intent data) {
        if (data == null) {
            hostActivity.handleGoogleSignInFailure();
            return;
        }
        final GoogleSignInResult result = getSignInResultFromIntent(data);
        if (!result.isSuccess()) {
            hostActivity.handleGoogleSignInFailure();
            return;
        }
        final GoogleSignInAccount account = result.getSignInAccount();
        if (account == null) {
            hostActivity.handleGoogleSignInFailure();
            return;
        }
        signWithCredentials(account);
    }

    /**
     * Pass the {@link GoogleSignInAccount} object to sign in Firebase.
     *
     * @param account GoogleSignInAccount to use.
     */
    public void signWithCredentials(@NonNull final GoogleSignInAccount account) {
        final AuthCredential credential = getCredentials(account);
        getFBAuthInstance().signInWithCredential(credential).addOnCompleteListener(googleAuthCompleteListener);
    }

    /**
     * Get auth credentials from GoogleSignInAccount.
     *
     * @param account Account to use
     * @return {@link AuthCredential} of the account.
     */
    public AuthCredential getCredentials(@NonNull final GoogleSignInAccount account) {
        return GoogleAuthProvider.getCredential(account.getIdToken(), null);
    }

    /**
     * Get sign in data from intent.
     *
     * @param data Intent object to be used.
     * @return GoogleSignInResult object.
     */
    public GoogleSignInResult getSignInResultFromIntent(@NonNull final Intent data) {
        return Auth.GoogleSignInApi.getSignInResultFromIntent(data);
    }
}

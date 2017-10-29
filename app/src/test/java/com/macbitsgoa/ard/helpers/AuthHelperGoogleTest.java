package com.macbitsgoa.ard.helpers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.macbitsgoa.ard.activities.AuthActivity;
import com.macbitsgoa.ard.keys.UserItemKeys;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for {@link AuthHelperForGoogle}.
 *
 * @author Rushikesh Jogdand
 * @author Vikramaditya Kukreja
 */
@RunWith(MockitoJUnitRunner.class)
public class AuthHelperGoogleTest {

    private AuthHelperForGoogle authHelper;

    @Mock
    FirebaseUser firebaseUser;

    @Mock
    FirebaseAuth firebaseAuth;

    @Mock
    DatabaseReference databaseReference;

    @Mock
    AuthActivity authActivity;

    OnCompleteListener<AuthResult> onCompleteListener;

    @Mock
    Task<AuthResult> task;

    @Mock
    Context context;

    @Mock
    GoogleSignInResult gsir;

    @Mock
    GoogleSignInAccount gsia;

    AuthHelperForGoogle authSpy;

    @Before
    public void init() {
        initMocks(this);

        doNothing().when(authActivity).handleGoogleSignInFailure();
        doCallRealMethod().when(authActivity).updateUserInfo(firebaseAuth, databaseReference);

        authHelper = new AuthHelperForGoogle(authActivity, firebaseAuth);
        onCompleteListener = authActivity;

        doReturn("123").when(firebaseUser).getUid();
        doReturn("name").when(firebaseUser).getDisplayName();
        doReturn("email@domain.com").when(firebaseUser).getEmail();
        doReturn(mock(Uri.class)).when(firebaseUser).getPhotoUrl();

        doReturn(firebaseUser).when(firebaseAuth).getCurrentUser();

        doReturn(databaseReference).when(authActivity).getRootReference();
        doReturn(databaseReference).when(databaseReference).child(anyString());

        authSpy = spy(authHelper);
    }

    @Test
    public void testUpdateUserInfo() throws Exception {
        assertTrue(authActivity.updateUserInfo(firebaseAuth, databaseReference));

        verify(databaseReference, times(1)).child("123");
        verify(databaseReference.child(UserItemKeys.NAME), times(1)).setValue("name");
        verify(databaseReference.child(UserItemKeys.EMAIL), times(1)).setValue("email@domain.com");
    }

    @Test
    public void testUpdateUserInfoWithNullPhoto() throws Exception {
        doReturn(null).when(firebaseUser).getPhotoUrl();

        assertTrue(authActivity.updateUserInfo(firebaseAuth, databaseReference));

        verify(databaseReference, times(1)).child("123");
        verify(databaseReference.child(UserItemKeys.NAME), times(1)).setValue("name");
        verify(databaseReference.child(UserItemKeys.EMAIL), times(1)).setValue("email@domain.com");
        verify(databaseReference.child(UserItemKeys.PHOTO_URL), times(1)).setValue("");
    }

    @Test
    public void testNullUser() throws Exception {
        doReturn(null).when(firebaseAuth).getCurrentUser();
        assertFalse(authActivity.updateUserInfo(firebaseAuth, databaseReference));
    }

    @Test
    public void testOnConnectionFailure() throws Exception {
        doCallRealMethod().when(authActivity).onConnectionFailed(new ConnectionResult(ConnectionResult.CANCELED));

        authActivity.onConnectionFailed(new ConnectionResult(ConnectionResult.CANCELED));
        verify(authActivity, times(1)).handleGoogleSignInFailure();
    }

    @Test
    public void testGoogleSignInWithNullData() throws Exception {
        authHelper.handleGoogleSignIn(null);
        verify(authActivity, times(1)).handleGoogleSignInFailure();
    }


    @Test
    public void testIntentLaunch() throws Exception {
        final Intent intent = new Intent();

        doCallRealMethod().when(authActivity).launchGoogleSignIn(any(Intent.class));

        authActivity.launchGoogleSignIn(intent);
        verify(authActivity, times(1)).startActivityForResult(intent, 17);
    }

    @Test
    public void testGoogleSignInWithNoSuccess() throws Exception {
        doReturn(false).when(gsir).isSuccess();
        doReturn(gsir).when(authSpy).getSignInResultFromIntent(any(Intent.class));

        authSpy.handleGoogleSignIn(new Intent());
        verify(authActivity, times(1)).handleGoogleSignInFailure();
    }

    @Test
    public void testGoogleSignInWithSuccessNoAcc() throws Exception {
        when(gsir.isSuccess()).thenReturn(true);
        doReturn(gsir).when(authSpy).getSignInResultFromIntent(any(Intent.class));
        doReturn(null).when(gsir).getSignInAccount();

        authSpy.handleGoogleSignIn(new Intent());
        verify(authActivity, times(1)).handleGoogleSignInFailure();
    }

    @Test
    public void testGoogleSignInWithSuccess() throws Exception {
        when(gsir.isSuccess()).thenReturn(true);
        doReturn(gsir).when(authSpy).getSignInResultFromIntent(any(Intent.class));
        doReturn(gsia).when(gsir).getSignInAccount();

        doNothing().when(authSpy).signWithCredentials(any(GoogleSignInAccount.class));

        authSpy.handleGoogleSignIn(new Intent());
        verify(authActivity, times(0)).handleGoogleSignInFailure();
    }

    @Test
    public void testSignInWithCredentialsInvalidTask() throws Exception {
        final AuthCredential ac = mock(AuthCredential.class);

        doReturn(ac).when(authSpy).getCredentials(any(GoogleSignInAccount.class));

        doReturn(task).when(firebaseAuth).signInWithCredential(any(AuthCredential.class));
        doReturn(task).when(task).addOnCompleteListener(authActivity);

        authSpy.signWithCredentials(gsia);
        verify(task, times(1)).addOnCompleteListener(onCompleteListener);

        doReturn(false).when(task).isSuccessful();

        doCallRealMethod().when(authActivity).onComplete(task, firebaseAuth);

        authActivity.onComplete(task, firebaseAuth);

        verify(authActivity, times(1)).handleGoogleSignInFailure();
    }

    @Test
    public void testSignInWithCredentialsValidTask() throws Exception {
        final AuthCredential ac = mock(AuthCredential.class);

        doReturn(ac).when(authSpy).getCredentials(any(GoogleSignInAccount.class));

        doReturn(task).when(firebaseAuth).signInWithCredential(any(AuthCredential.class));
        doReturn(task).when(task).addOnCompleteListener(authActivity);

        authSpy.signWithCredentials(gsia);
        verify(task, times(1)).addOnCompleteListener(onCompleteListener);

        doReturn(true).when(task).isSuccessful();
        doReturn(true).when(authActivity).updateUserInfo(firebaseAuth, databaseReference);
        doNothing().when(authActivity).startActivity(any(Intent.class));
        doNothing().when(authActivity).finish();

        assertEquals(firebaseAuth, authSpy.getFBAuthInstance());
        assertEquals(firebaseUser, authSpy.getFBAuthInstance().getCurrentUser());
        assertTrue(authActivity.updateUserInfo(firebaseAuth, databaseReference));

        doCallRealMethod().when(authActivity).onComplete(task, firebaseAuth);

        authActivity.onComplete(task, firebaseAuth);
        verify(authActivity, times(1)).startActivity(any(Intent.class));
        verify(authActivity, times(1)).finish();
    }
}

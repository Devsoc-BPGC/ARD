package com.macbitsgoa.ard.activities;

import android.app.Instrumentation;
import android.os.IBinder;
import android.support.test.espresso.Root;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import android.view.WindowManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.macbitsgoa.ard.R;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.anyIntent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

/**
 * UI tests for AuthActivity.
 * @author Rushikesh Jogdand
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class AuthActivityTest {

    @Rule
    public IntentsTestRule<AuthActivity> activityTestRule =
            new IntentsTestRule<>(AuthActivity.class);

    @Ignore
    @Test
    public void testGoogleAccountChooserLaunches() throws Exception {
        intending(anyIntent()).respondWith(new Instrumentation.ActivityResult(RESULT_CANCELED, null));

        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int code = api.isGooglePlayServicesAvailable(activityTestRule.getActivity());
        if (code == ConnectionResult.SUCCESS) {
            onView(withId(R.id.btn_content_auth_google)).perform(click()).check(matches(not(isClickable())));
            intended(hasComponent("com.google.android.gms.auth.api.signin.internal.SignInHubActivity"));
            onView(withText("Sign in cancelled"))
                    .inRoot(withDecorView(not(is(activityTestRule.getActivity().getWindow().getDecorView()))))
                    .check(matches(isDisplayed()));
        }
    }

    @Ignore
    @Test
    public void testOnActivityResultNullResponse() throws Exception {
        intending(anyIntent()).respondWith(new Instrumentation.ActivityResult(RESULT_OK, null));

        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int code = api.isGooglePlayServicesAvailable(activityTestRule.getActivity());
        if (code == ConnectionResult.SUCCESS) {
            onView(withId(R.id.btn_content_auth_google)).perform(click()).check(matches(not(isClickable())));
            intended(hasComponent("com.google.android.gms.auth.api.signin.internal.SignInHubActivity"));
            //Cannot check for multiple toasts in one activity test as mentioned here
            //https://stackoverflow.com/a/38379219/5262677
            onView(withText(R.string.error_google_sign_in_failed))
                    .inRoot(withDecorView(not(is(activityTestRule.getActivity().getWindow().getDecorView()))))
                    .check(matches(isDisplayed()));
        }
    }
}

package com.macbitsgoa.ard.utils;

import android.app.Instrumentation;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.activities.MainActivity;
import com.macbitsgoa.ard.keys.AuthActivityKeys;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.app.Activity.RESULT_CANCELED;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.Intents.times;
import static android.support.test.espresso.intent.matcher.IntentMatchers.anyIntent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;

/**
 * Test for utility "Browser".
 * @author Rushikesh Jogdand
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class BrowserTest {

    @Rule
    public IntentsTestRule<MainActivity> activityTestRule = new IntentsTestRule<>(MainActivity.class, false, false);

    @Before
    public void setup() {
        activityTestRule.launchActivity(new Intent().putExtra(AuthActivityKeys.USE_DEFAULT, false));
    }

    @Test
    public void testNoConfig() {
        String url = "example.com";
        intending(anyIntent()).respondWith(new Instrumentation.ActivityResult(RESULT_CANCELED, null));
        new Browser(activityTestRule.getActivity()).launchUrl(url);
        intended(hasAction(Intent.ACTION_VIEW), times(1));
    }

    @Test
    public void testAllConfig () {
        intending(anyIntent()).respondWith(new Instrumentation.ActivityResult(RESULT_CANCELED, null));
        final String url = "example.com";
        final MainActivity mActivity = activityTestRule.getActivity();
        final Intent mIntent = new Intent();
        PendingIntent mPendingIntent = PendingIntent.getActivity(
                mActivity,
                1,
                mIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        new Browser(mActivity)
                .setToolBarColor(ActivityCompat.getColor(mActivity, R.color.green_50))
                .setCloseButtonIcon(ContextCompat.getDrawable(mActivity, R.mipmap.ic_launcher))
                .setStartAnimation(android.R.anim.fade_in, android.R.anim.fade_out)
                .setExitAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .setUrlBarHiding(false)
                .setShowTitle(false)
                .addMenuItem("Lol", mPendingIntent)
                .addMenuItem("Lol2", mPendingIntent)
                .setShareInMenu(true)
                .setActionButton(
                        ContextCompat.getDrawable(mActivity, R.drawable.ic_faq_24dp),
                        "lorem ipsum",
                        mPendingIntent,
                        true
                )
                .launchUrl(url);

        new Browser(mActivity)
                .setUrlBarHiding(true)
                .setShowTitle(true)
                .setShareInMenu(false)
                .launchUrl(url);

        intended(hasAction(Intent.ACTION_VIEW), times(2));
    }

    @Test
    public void penTest() {
        new Browser(activityTestRule.getActivity()).launchUrl("javascript://nasty.kid()");
        new Browser(activityTestRule.getActivity()).launchUrl("file://secret.db");
        intended(hasAction(Intent.ACTION_VIEW), times(0));
    }
}

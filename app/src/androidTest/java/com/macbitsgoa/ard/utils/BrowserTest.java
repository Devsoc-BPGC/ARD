package com.macbitsgoa.ard.utils;

import android.app.Activity;
import android.app.Instrumentation;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.activities.MainActivity;

import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.Intents.times;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;

/**
 * Test for utility "Browser".
 * @author Rushikesh Jogdand
 */
@RunWith(AndroidJUnit4.class)
public class BrowserTest {

    private Activity mActivity;

    @Rule
    public IntentsTestRule<MainActivity> activityTestRule = new IntentsTestRule<>(MainActivity.class);

    @Before
    public void init() {
        mActivity = activityTestRule.getActivity();
        intending(hasAction(Intent.ACTION_VIEW))
                .respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));
    }

    @Test
    public void testNoConfig() {
        String url = "example.com";
        new Browser(activityTestRule.getActivity()).launchUrl(url);
        intended(hasAction(Intent.ACTION_VIEW), times(1));
    }

    @Test
    public void testAllConfig () {
        String url = "http://bits-pilani.ac.in/";
        Intent mIntent = new Intent(mActivity, MainActivity.class);
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
        new Browser(mActivity).launchUrl("javascript://nasty.kid()");
        new Browser(mActivity).launchUrl("file://secret.db");

        intended(hasAction(Intent.ACTION_VIEW), times(0));
    }
}

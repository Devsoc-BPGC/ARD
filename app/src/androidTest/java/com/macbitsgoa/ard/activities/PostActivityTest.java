package com.macbitsgoa.ard.activities;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.macbitsgoa.ard.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by vikramaditya on 6/26/17.
 */
@RunWith(AndroidJUnit4.class)
public class PostActivityTest {

    @Rule
    public ActivityTestRule<PostActivity> activityTestRule =
            new ActivityTestRule<>(PostActivity.class);

    @Test
    public void testFragmentVisible() {
        onView(withId(R.id.fab_activity_post)).check(matches(isDisplayed()));
        onView(withId(R.id.frame_activity_post)).check(matches(isDisplayed()));
        onView(withId(R.id.fab_activity_post)).check(matches(isDisplayed())).check(matches(isClickable()))
                .perform(click());
        onView(withId(android.support.design.R.id.snackbar_text)).check(matches(isDisplayed()));
    }
}

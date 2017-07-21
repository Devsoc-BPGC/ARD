package com.macbitsgoa.ard.activities;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.macbitsgoa.ard.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.Visibility.VISIBLE;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Instrumentation tests for {@link SettingsActivity}.
 *
 * @author Vikramaditya Kukreja
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class SettingsActivityTest {

    @Rule
    public IntentsTestRule<SettingsActivity> activityTestRule = new IntentsTestRule<>(SettingsActivity.class);

    @Test
    public void testUIVisible() throws Exception {
        onView(withId(R.id.toolbar_activity_settings)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withText("Settings")).check(matches(isDisplayed()));
        onView(withId(R.id.frame_activity_settings)).check(matches(withEffectiveVisibility(VISIBLE)));
    }
}

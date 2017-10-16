package com.macbitsgoa.ard.activities;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.view.GravityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.keys.AuthActivityKeys;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.close;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.matcher.ViewMatchers.Visibility.INVISIBLE;
import static android.support.test.espresso.matcher.ViewMatchers.Visibility.VISIBLE;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.macbitsgoa.ard.THC.childAtPosition;
import static org.hamcrest.Matchers.allOf;

/**
 * Tests for MainActivity
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {

    @Rule
    public IntentsTestRule<MainActivity> activityTestRule =
            new IntentsTestRule<>(MainActivity.class, true, false);

    @Test
    public void testBackPressOnOpenedDrawer() throws Exception {
        activityTestRule.launchActivity(new Intent().putExtra(AuthActivityKeys.USE_DEFAULT, false));
        onView(withId(R.id.drawer_layout))
                .perform(open(GravityCompat.START));
        pressBack();
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(GravityCompat.START)));
    }

    @Test
    public void testOptionsMenuItems() throws Exception {
        activityTestRule.launchActivity(new Intent().putExtra(AuthActivityKeys.USE_DEFAULT, false));
        onView(withText("Settings")).check(doesNotExist());
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
        onView(withText("Settings")).check(matches(isDisplayed())).perform(click());
    }

    @Test
    public void testDrawerItems() throws Exception {
        activityTestRule.launchActivity(new Intent().putExtra(AuthActivityKeys.USE_DEFAULT, false));
        onView(withId(R.id.drawer_layout)).perform(open());
        onView(withText("null")).check(doesNotExist());
        onView(withId(R.id.drawer_layout)).perform(close());
    }

    @Test
    public void testFragmentFrameIsVisible() throws Exception {
        activityTestRule.launchActivity(new Intent().putExtra(AuthActivityKeys.USE_DEFAULT, false));
        onView(withId(R.id.frame_content_main)).check(matches(isDisplayed()));
    }

    @Test
    public void testBottomNavIsDisplayed() throws Exception {
        activityTestRule.launchActivity(new Intent().putExtra(AuthActivityKeys.USE_DEFAULT, false));
        onView(withId(R.id.bottom_nav_activity_main)).check(matches(isDisplayed()));

        onView(allOf(withId(R.id.bottom_nav_home),
                withContentDescription("Home"), isDisplayed()))
                .perform(click());
        onView(withId(R.id.fragment_home_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_faq_layout)).check(doesNotExist());
        onView(withId(R.id.fragment_chat_layout)).check(doesNotExist());

        onView(allOf(withId(R.id.bottom_nav_faq),
                withContentDescription("F.A.Q."), isDisplayed()))
                .perform(click());
        onView(withId(R.id.fragment_faq_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_home_layout)).check(doesNotExist());
        onView(withId(R.id.fragment_chat_layout)).check(doesNotExist());
        onView(withId(R.id.tabLayout_fragment_faq)).check(matches(isDisplayed()));
        onView(withId(R.id.viewPager_fragment_faq)).check(matches(isDisplayed()));
        onView(withText("General")).check(matches(isDisplayed()));


        onView(allOf(withId(R.id.bottom_nav_chat),
                withContentDescription("Chat"), isDisplayed()))
                .perform(click());
        onView(withId(R.id.fragment_chat_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_home_layout)).check(doesNotExist());
        onView(withId(R.id.fragment_faq_layout)).check(doesNotExist());
    }

    @Test
    public void testDrawerHeader() {
        activityTestRule.launchActivity(new Intent().putExtra(AuthActivityKeys.USE_DEFAULT, false));
        ViewInteraction imageView = onView(
                allOf(withId(R.id.nav_drawer_image),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.navigation_header_container),
                                        0),
                                0),
                        isDisplayed()));
        imageView.check(matches(isDisplayed()));

        ViewInteraction textView = onView(
                allOf(withId(R.id.nav_drawer_title),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.navigation_header_container),
                                        0),
                                1),
                        isDisplayed()));
        textView.check(matches(isDisplayed()));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.nav_drawer_subtitle),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.navigation_header_container),
                                        0),
                                2),
                        isDisplayed()));
        textView2.check(matches(isDisplayed()));

        pressBack();
    }

    @Test
    public void testFabButtons() throws Exception {
        activityTestRule.launchActivity(new Intent().putExtra(AuthActivityKeys.USE_DEFAULT, false));

        onView(withId(R.id.view_fragment_home_backdrop)).check(matches(withEffectiveVisibility(INVISIBLE)));
        onView(withId(R.id.fab_fragment_home_announce)).check(matches(withEffectiveVisibility(INVISIBLE)));
        onView(withId(R.id.fab_fragment_home_add)).perform(click());
        onView(withId(R.id.view_fragment_home_backdrop)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.fab_fragment_home_announce)).check(matches(withEffectiveVisibility(VISIBLE)));

        onView(withId(R.id.fab_fragment_home_add)).perform(click());
        onView(withId(R.id.view_fragment_home_backdrop)).check(matches(withEffectiveVisibility(INVISIBLE)));
        onView(withId(R.id.fab_fragment_home_announce)).check(matches(withEffectiveVisibility(INVISIBLE)));

        onView(withId(R.id.fab_fragment_home_add)).perform(click());
        onView(withId(R.id.view_fragment_home_backdrop)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.fab_fragment_home_announce)).check(matches(withEffectiveVisibility(VISIBLE)));

        onView(withId(R.id.view_fragment_home_backdrop)).perform(click());
        onView(withId(R.id.view_fragment_home_backdrop)).check(matches(withEffectiveVisibility(INVISIBLE)));
        onView(withId(R.id.fab_fragment_home_announce)).check(matches(withEffectiveVisibility(INVISIBLE)));

        onView(withId(R.id.fab_fragment_home_add)).perform(click());
        onView(withId(R.id.fab_fragment_home_announce)).perform(click());
        pressBack();
        onView(withId(R.id.fab_fragment_home_announce)).check(matches(withEffectiveVisibility(INVISIBLE)));
    }

    @Test
    public void testFirstLaunchRedirectsToAuth() throws Exception {
        activityTestRule.launchActivity(null);
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int code = api.isGooglePlayServicesAvailable(activityTestRule.getActivity());
        if (code == ConnectionResult.SUCCESS) {
            //not necessary here
        } else {
            pressBack();
        }
        onView(withId(R.id.btn_content_auth_google)).check(matches(isDisplayed()));
    }

    @Test
    public void testFirstLaunchRedirectsToAuth2() throws Exception {
        activityTestRule.launchActivity(new Intent().putExtra(AuthActivityKeys.USE_DEFAULT, true));
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int code = api.isGooglePlayServicesAvailable(activityTestRule.getActivity());
        if (code == ConnectionResult.SUCCESS) {
            //not necessary here
        } else {
            pressBack();
        }
        onView(withId(R.id.btn_content_auth_google)).check(matches(isDisplayed()));
    }
}

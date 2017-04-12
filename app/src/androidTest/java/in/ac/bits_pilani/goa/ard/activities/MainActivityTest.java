package in.ac.bits_pilani.goa.ard.activities;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.DrawerMatchers;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.view.GravityCompat;
import android.util.Log;
import android.view.Gravity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import in.ac.bits_pilani.goa.ard.R;
import in.ac.bits_pilani.goa.ard.utils.AHC;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.Espresso.openContextualActionModeOverflowMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for MainActivity
 */

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Mock
    private Context context;

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Before
    public void init() {
        context = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void testClassName() throws Exception {
        final String[] expected = new String[]{
                "MainActivity",
        };
        assertArrayEquals("Class name error", expected,
                new String[]{activityTestRule.getActivity().getClass().getSimpleName()});
    }

    @Test
    public void testParentActivityName() throws Exception {
        final String[] expected = new String[]{
                "AppCompatActivity",
        };
        assertArrayEquals("Parent class is wrong", expected,
                new String[]{activityTestRule.getActivity()
                        .getClass().getSuperclass().getSimpleName()});
    }

    @Test
    public void testViewIds() throws Exception {
        onView(withId(R.id.toolbar_activity_main))
                .perform(click())
                .check(matches(isDisplayed()));
    }

    @Test
    public void testSnackbarPopup() throws Exception {
        onView(withId(R.id.fab))
                .perform(click());
        onView(withId(android.support.design.R.id.snackbar_text))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testDrawerOpenClose() throws Exception {
        onView(withId(R.id.drawer_layout))
                .perform(DrawerActions.open())
        .check(matches(DrawerMatchers.isOpen()));

        onView(withId(R.id.drawer_layout))
                .perform(DrawerActions.close())
        .check(matches(DrawerMatchers.isClosed()));

        onView(withId(R.id.drawer_layout))
                .perform(DrawerActions.open(Gravity.START))
                .check(matches(DrawerMatchers.isOpen(Gravity.START)));

    }

    @Test
    public void testBackPressOnOpenedDrawer() throws Exception {
        onView(withId(R.id.drawer_layout))
                .perform(DrawerActions.open(GravityCompat.START));
        Espresso.pressBack();
        onView(withId(R.id.drawer_layout))
                .check(matches(DrawerMatchers.isClosed(GravityCompat.START)));
    }

    @Test
    public void testOptionsMenuItems() throws Exception {
        onView(withText("Settings")).check(doesNotExist());
        openActionBarOverflowOrOptionsMenu(context);
        onView(withText("Settings")).check(matches(isDisplayed())).perform(click());
    }

    @Test
    public void testDrawerItems() throws Exception {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withText("Import")).check(matches(isDisplayed())).perform(click());
        onView(withId(R.id.drawer_layout)).check(matches(DrawerMatchers.isClosed()));

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withText("Gallery")).check(matches(isDisplayed())).perform(click());
        onView(withId(R.id.drawer_layout)).check(matches(DrawerMatchers.isClosed()));

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withText("Slideshow")).check(matches(isDisplayed())).perform(click());
        onView(withId(R.id.drawer_layout)).check(matches(DrawerMatchers.isClosed()));

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withText("Manage")).check(matches(isDisplayed())).perform(click());
        onView(withId(R.id.drawer_layout)).check(matches(DrawerMatchers.isClosed()));

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withText("Share")).check(matches(isDisplayed())).perform(click());
        onView(withId(R.id.drawer_layout)).check(matches(DrawerMatchers.isClosed()));

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withText("Send")).check(matches(isDisplayed())).perform(click());
        onView(withId(R.id.drawer_layout)).check(matches(DrawerMatchers.isClosed()));

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withText("null")).check(doesNotExist());

    }

}

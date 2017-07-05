package com.macbitsgoa.ard.fragments;

import android.content.Context;
import android.graphics.Color;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.Checks;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.view.View;
import android.widget.TextView;

import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.activities.PostActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.Visibility.GONE;
import static android.support.test.espresso.matcher.ViewMatchers.Visibility.VISIBLE;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;

/**
 * Created by vikramaditya on 6/30/17.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class HomeAnnFragmentTest {

    @Rule
    public IntentsTestRule<PostActivity> activityTestRule = new IntentsTestRule<>(PostActivity.class);

    private Context context;

    private UiDevice uiDevice;

    @Before
    public void init() {
        context = InstrumentationRegistry.getTargetContext();
        uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    }

    @Test
    public void testLimitCrossed() throws Exception {
        uiDevice.setOrientationNatural();
        //Get input from text file
        final String text = getString("lipsum-big.txt");

        onView(withId(R.id.editText_home_ann_fragment_message)).perform(typeText(text));
        pressBack();

        onView(withId(R.id.textView_home_ann_fragment_counter))
                .check(matches(withText(text.length() + "/" + context.getResources()
                        .getInteger(R.integer.ann_max_char))))
                .check(matches(hasTextColor(Color.RED)));
        onView(withId(R.id.fab_activity_post)).check((matches(withEffectiveVisibility(GONE))));

        uiDevice.setOrientationRight();

        onView(withId(R.id.textView_home_ann_fragment_counter)).
                check(matches(withText(text.length() + "/" + context.getResources()
                        .getInteger(R.integer.ann_max_char))))
                .check(matches(hasTextColor(Color.RED)));
        onView(withId(R.id.fab_activity_post)).check((matches(withEffectiveVisibility(GONE))));
        onView(withId(R.id.editText_home_ann_fragment_message)).check(matches(withText(text)));

        uiDevice.setOrientationNatural();
        uiDevice.setOrientationLeft();
        uiDevice.setOrientationNatural();
    }

    @Test
    public void testLimitNotCrossed() throws Exception {
        //Get input from text file
        final String text = getString("lipsum-small.txt");

        onView(withId(R.id.editText_home_ann_fragment_message)).perform(typeText(text));

        closeSoftKeyboard();

        onView(withId(R.id.textView_home_ann_fragment_counter)).check(matches(withText(text.length() + "/" + context.getResources().getInteger(R.integer.ann_max_char))));
        onView(withId(R.id.fab_activity_post)).check((matches(withEffectiveVisibility(VISIBLE))));

        uiDevice.setOrientationRight();

        onView(withId(R.id.textView_home_ann_fragment_counter)).check(matches(withText(text.length() + "/" + context.getResources().getInteger(R.integer.ann_max_char))));
        onView(withId(R.id.fab_activity_post)).check((matches(withEffectiveVisibility(VISIBLE))));
        onView(withId(R.id.editText_home_ann_fragment_message)).check(matches(withText(text)));

        uiDevice.setOrientationNatural();
    }

    @Test
    public void testPostAnnouncement() throws Exception {
        //Get input from text file
        String text = "";

        onView(withId(R.id.fab_activity_post)).perform(click());
        onView(allOf(withId(android.support.design.R.id.snackbar_text), withText("Please enter message!")))
                .check(matches(isDisplayed()));
        text = getString("lipsum-small.txt");

        onView(withId(R.id.editText_home_ann_fragment_message)).perform(typeText(text));
        pressBack();

        onView(withId(R.id.fab_activity_post)).perform(click());
    }

    /**
     * Returns a string object from an input stream.
     * The resource file should be placed in the same package name as the java file
     * expect it should be inside the <b>resources</b> package and not the java package.
     *
     * @param file File name to be used.
     * @return converted string.
     * @throws Exception thrown exception.
     */
    public String getString(final String file) throws Exception {
        InputStream inputStream = getClass().getResourceAsStream(file);
        final ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString("UTF-8");
    }


    /**
     * Matcher for EditText/TextView color checker.
     *
     * @param color color to match.
     * @return Matcher.
     */
    public static Matcher<View> hasTextColor(final int color) {
        Checks.checkNotNull(color);
        return new BoundedMatcher<View, TextView>(TextView.class) {
            @Override
            public void describeTo(final Description description) {
                description.appendText("with text color: ");
            }

            @Override
            public boolean matchesSafely(final TextView warning) {
                return color == warning.getCurrentTextColor();
            }
        };
    }
}

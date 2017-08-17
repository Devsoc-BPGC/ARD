package com.macbitsgoa.ard;

import android.os.IBinder;
import android.support.test.espresso.Root;
import android.support.test.espresso.intent.Checks;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.TextView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Helper class for useful methods and matchers.
 *
 * @author Vikramaditya Kukreja
 */
public class THC {

    public static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
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

    /**
     * Returns a string object from an input stream.
     * The resource file should be placed in the same package name as the java file
     * expect it should be inside the <b>resources</b> package and not the java package.
     *
     * @param file File name to be used.
     * @return converted string.
     * @throws Exception thrown exception.
     */
    public static String getString(final Class<?> clazz, final String file) throws Exception {
        InputStream inputStream = clazz.getResourceAsStream(file);
        final ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString("UTF-8");
    }

    /**
     * Match toast with this.
     */
    public class ToastMatcher extends TypeSafeMatcher<Root> {

        @Override
        public void describeTo(Description description) {
            description.appendText("is toast");
        }

        @Override
        public boolean matchesSafely(Root root) {
            int type = root.getWindowLayoutParams().get().type;
            if ((type == WindowManager.LayoutParams.TYPE_TOAST)) {
                IBinder windowToken = root.getDecorView().getWindowToken();
                IBinder appToken = root.getDecorView().getApplicationWindowToken();
                if (windowToken == appToken) {
                    //means this window isn't contained by any other windows.
                }
            }
            return false;
        }
    }
}

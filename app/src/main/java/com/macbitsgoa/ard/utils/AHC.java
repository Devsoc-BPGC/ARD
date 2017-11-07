package com.macbitsgoa.ard.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.TypedValue;

import com.macbitsgoa.ard.BuildConfig;
import com.macbitsgoa.ard.services.MessagingService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.annotation.Nonnull;

/**
 * Helper class for ARD.
 *
 * @author Vikramaditya Kukreja
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class AHC {

    /**
     * Application id of app.
     */
    public static final String APPLICATION_ID = "com.macbitsgoa.ard";

    /**
     * Package name of project.
     */
    public static final String PACKAGE_NAME = APPLICATION_ID;

    /**
     * Default Log tag for project.
     */
    public static final String TAG = "MAC_ARD";

    /**
     * Separator used when joining strings.
     */
    public static final String SEPARATOR = ", ";

    /**
     * Name of Realm database.
     */
    public static final String REALM_ARD_DATABASE = "REALM_ARD_DATABASE";

    /**
     * Version of Realm database.
     */
    public static final int REALM_ARD_DATABASE_SCHEMA = BuildConfig.VERSION_CODE;

    /**
     * Fragment title key.
     */
    public static final String FRAGMENT_TITLE_KEY = "key";

    /**
     * Firebase node name of navigation drawer.
     */
    public static final String FDR_NAV_DRAWER = "navDrawer";

    /**
     * Firebase node name of child "title" of navigation drawer.
     */
    public static final String FDR_NAV_DRAWER_TITLE = "navDrawerTitle";

    /**
     * Firebase node name of child "subtitle" of navigation drawer.
     */
    public static final String FDR_NAV_DRAWER_SUBTITLE = "navDrawerSubtitle";

    /**
     * Firebase node name of child "image list" of navigation drawer.
     * Image list is array of URLs (strings) of images used randomly as
     * background image for navigation drawer.
     */
    public static final String FDR_NAV_DRAWER_IMAGE_LIST = "navDrawerImages";

    /**
     * Reference to home fragment node.
     */
    public static final String FDR_HOME = "home";

    /**
     * Reference to extra informations.
     */
    public static final String FDR_EXTRAS = "extra";

    /**
     * Reference to home fragment node.
     */
    public static final String FDR_CHAT = "chats";

    /**
     * Animation multiplier for Fragment Home.
     */
    public static final float ANIMATION_MULTIPLIER = 1.5f;

    /**
     * Firebase directory of users.
     */
    public static final String FDR_USERS = "users";

    /**
     * Firebase directory of users.
     */
    public static final String FDR_ADMINS = "admins";

    /**
     * SharedPreferences file name for the app.
     */
    public static final String SP_APP = "prefs";

    /**
     * Unique code for Alarm receiver.
     */
    public static final int ALARM_RECEIVER_REQUEST_CODE = 49;

    /**
     * Action for alarm receiver.
     */
    public static final String ALARM_RECEIVER_ACTION_UPDATE = "ard.action.alarm";

    /**
     * Method to get pixel value corresponding to input dp.
     *
     * @param context of calling method.
     * @param dp      value to be converted in dp.
     * @return converted value in pixels.
     */
    public static float dpToPx(final Context context, final float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    /**
     * Method to get bitmap from vector drawable.
     *
     * @param drawable Input {@link android.graphics.drawable.Drawable}
     * @param width    The width of resultant bitmap in pixels.
     * @param height   The height of resultant bitmap in pixels.
     * @return converted bitmap.
     */
    public static Bitmap getBitmapFromDrawable(final Drawable drawable, final int width, final int height) {
        final Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);

        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    /**
     * Create a string from a Date object and generate a simple format.
     *
     * @param date Date object to use.
     * @return converted string.
     */
    public static String getSimpleDate(@NonNull final Date date) {
        final SimpleDateFormat sdf = new SimpleDateFormat("dd MMM", Locale.UK);
        return sdf.format(date);
    }

    /**
     * Create a string from a Date object and generate a simple format.
     *
     * @param date Date object to use.
     * @return converted string.
     */
    public static String getSimpleDayAndTime(@Nullable final Date date) {
        return getSimpleTime(date);
    }

    public static String getSimpleDay(@Nullable final Date date) {
        if (date == null) return "";
        long diff = Math.abs(date.getTime() - Calendar.getInstance().getTime().getTime());
        if (diff / (1000 * 60 * 60 * 24) < 1)
            return getSimpleTime(date);
        final SimpleDateFormat sdf = new SimpleDateFormat("E", Locale.UK);
        return sdf.format(date);
    }

    public static String getSimpleTime(@Nonnull final Date date) {
        if (date == null) return "";
        final SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.UK);
        return sdf.format(date);
    }

    /**
     * Setup alarm after delay.
     */
    public static void setNextAlarm(final Context context) {
         /*
        Set alarm for service start.
         */
        Log.e("TAG", "setting next alarm");
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 1);
        //Create a new PendingIntent and add it to the AlarmManager
        final Intent intent = new Intent(context, MessagingService.class);
        PendingIntent pi = PendingIntent.getService(
                context,
                AHC.ALARM_RECEIVER_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        final AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
    }


}

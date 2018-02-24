package com.macbitsgoa.ard.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.TypedValue;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.macbitsgoa.ard.BuildConfig;
import com.macbitsgoa.ard.models.TypeItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
     * Reference to announcements node.
     */
    public static final String FDR_ANN = "announcements";

    /**
     * Reference to home fragment node.
     */
    public static final String FDR_HOME = "home1";

    /**
     * Reference to forum node on firebase.
     */
    public static final String FDR_FORUM = "forum";

    /**
     * Reference to extra informations.
     */
    public static final String FDR_EXTRAS = "extra";

    /**
     * Reference to home fragment node.
     */
    public static final String FDR_CHAT = "chats";

    /**
     * Reference to online ref node.
     */
    public static final String FDR_ONLINE = "online";

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
     * Name of default content poster.
     */
    public static final String DEFAULT_AUTHOR = "Admin";

    /**
     * Action for alarm receiver.
     */
    public static final String ALARM_RECEIVER_ACTION_UPDATE = "ard.action.alarm";

    /**
     * Default latest message set data as "📎 Document".
     */
    public static final String DOCUMENT_LITERAL = "\uD83D\uDCCE Document";

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
        final long diff = Math.abs(date.getTime() - Calendar.getInstance().getTime().getTime());
        if (diff / (1000 * 60 * 60 * 24) < 1)
            return getSimpleTime(date);
        return getSimpleDay(date) + ", " + getSimpleTime(date);
    }

    public static String getSimpleDay(@Nullable final Date date) {
        if (date == null) return "";
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
     *
     * @param context      Context object for use.
     * @param className    Class to start.
     * @param requestCode  Unique code for pending intent.
     * @param delayMinutes Delay value in minutes.
     */
    public static void setNextAlarm(final Context context, final Class className,
                                    final int requestCode, final int delayMinutes) {
        AHC.logd(TAG, "Setting next alarm for given class name " + className.getSimpleName());
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, delayMinutes);
        //Create a new PendingIntent and add it to the AlarmManager
        final Intent intent = new Intent(context, className);
        final PendingIntent pi = PendingIntent.getService(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        final AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
    }

    /**
     * Setup alarm after delay.
     *
     * @param context      Context object for use.
     * @param intent       Intent object for pending intent.
     * @param requestCode  Unique code for pending intent.
     * @param delayMinutes Delay value in minutes.
     */
    public static void setNextAlarm(final Context context, final Intent intent,
                                    final int requestCode, final int delayMinutes) {
        Log.i(TAG, "Setting next alarm for given intent");
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, delayMinutes);
        //Create a new PendingIntent and add it to the AlarmManager
        final PendingIntent pi = PendingIntent.getService(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        final AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
    }

    /**
     * Get screen width.
     *
     * @return width of screen in pixels.
     */
    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    /**
     * Convenience method to add a list of generic object to a {@link TypeItem} list.
     *
     * @param list      List to add to of type {@link TypeItem}.
     * @param additions List of objects to add to list.
     * @param type      Integer value to be used as type. {@link TypeItem#type}.
     * @param <T>       Generic object list for additions list.
     */
    public static <T> void fill(@Nullable List<TypeItem> list,
                                @NonNull final List<T> additions, final int type) {
        if (list == null) list = new ArrayList<>();
        for (final T addition : additions) {
            final TypeItem ti = new TypeItem(addition, type);
            list.add(ti);
        }
    }

    /**
     * Method to return mime type of file as a string.
     *
     * @param context Context object to use.
     * @param uri     Uri of file.
     * @return mime type as string.
     */
    public static String getMimeType(final Context context, final Uri uri) {
        return context.getContentResolver().getType(uri);
    }

    /**
     * Void method to print info logs.
     *
     * @param tag     Tag to use for message.
     * @param message Message to print.
     */
    public static void logi(@NonNull final String tag, @NonNull final String message) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, message);
        }
    }

    /**
     * Void method to print debug logs.
     *
     * @param tag     Tag to use for message.
     * @param message Message to print.
     */
    public static void logd(@NonNull final String tag, @NonNull final String message) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message);
        }
    }

    /**
     * Persist token to firebase database for use with FCM.
     *
     * @param token The current token to be updated on server.
     */
    public static void sendRegistrationToServer(final String token) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null
                || FirebaseAuth.getInstance().getCurrentUser().getUid() == null) return;
        FirebaseDatabase.getInstance()
                .getReference(BuildConfig.BUILD_TYPE)
                .child(FDR_USERS)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("token").setValue(token);
    }

    /**
     * Create a unique id with given String data.
     *
     * @param data String data to use.
     * @return String of unique id.
     */
    public static String generateUniqueId(final String data) {
        final Date time = Calendar.getInstance().getTime();
        return "" + time.getTime()
                + time.hashCode()
                + data.hashCode();
    }
}

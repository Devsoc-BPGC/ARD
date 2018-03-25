package com.macbitsgoa.ard.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.TypedValue;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.macbitsgoa.ard.BuildConfig;
import com.macbitsgoa.ard.keys.UserItemKeys;
import com.macbitsgoa.ard.models.TypeItem;
import com.macbitsgoa.ard.services.AnnNotifyService;

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
     * ARD.
     */
    public static final String ARD = "ARD";

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
    public static String getSimpleDayOrTime(@Nullable final Date date) {
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
     * @param uri     A Uri identifying content (either a list or specific type), using the content:// scheme.
     * @return A MIME type for the content, or null if the URL is invalid or the type is unknown
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
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || user.getUid() == null) return;
        final String userDisplayName = user.getDisplayName();
        final String userPhotoUrl = user.getPhotoUrl().toString();
        final String userEmail = user.getEmail();
        final String userUid = user.getUid();

        final DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference()
                .getRoot()
                .child(BuildConfig.BUILD_TYPE)
                .child(FDR_USERS)
                .child(userUid);

        userRef.child(UserItemKeys.EMAIL).setValue(userEmail);
        userRef.child(UserItemKeys.NAME).setValue(userDisplayName);
        userRef.child(UserItemKeys.PHOTO_URL).setValue(userPhotoUrl);
        userRef.child(UserItemKeys.DESC).setValue("User");
        userRef.child(UserItemKeys.FDR_TOKEN).setValue(token);

        final DatabaseReference adminRef = FirebaseDatabase.getInstance()
                .getReference()
                .getRoot()
                .child(BuildConfig.BUILD_TYPE)
                .child(FDR_ADMINS)
                .child(userUid);

        adminRef.child(UserItemKeys.EMAIL).setValue(userEmail);
        adminRef.child(UserItemKeys.NAME).setValue(userDisplayName);
        adminRef.child(UserItemKeys.PHOTO_URL).setValue(userPhotoUrl);
        adminRef.child(UserItemKeys.FDR_TOKEN).setValue(token);
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

    public static String getImageUrlFromMimeType(@NonNull final String mimeType) {
        AHC.logd(TAG, "Requested url for mime type " + mimeType);
        return "https://ard-bits.firebaseapp.com/assets/icons/" + mimeType + "/icon.png";
    }

    public static void startService(@NonNull final Context context,
                                    @NonNull final Class<? extends JobService> serviceClass,
                                    @NonNull final String tag) {
        startService(context, serviceClass, tag, new Bundle());
    }

    public static void startService(@NonNull final Context context,
                                    @NonNull final Class<? extends JobService> serviceClass,
                                    @NonNull final String tag, @NonNull final Bundle extras) {
        final FirebaseJobDispatcher fjd = getJobDispatcher(context);
        final Job.Builder jobBuilder = fjd.newJobBuilder()
                .setService(serviceClass)
                .setTag(tag)
                .setTrigger(Trigger.NOW)
                .setRecurring(false)
                .setExtras(extras)
                .setReplaceCurrent(false)
                .setLifetime(Lifetime.FOREVER)
                .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR);
        int status = fjd.schedule(jobBuilder.build());
        if (status != FirebaseJobDispatcher.SCHEDULE_RESULT_SUCCESS) {
            Log.e(TAG, "Failed to schedule job for " + tag
                    + "\nStatus code " + status);
        } else {
            AHC.logd(TAG, "Job successfully scheduled for tag " + tag);
        }
    }

    public static FirebaseJobDispatcher getJobDispatcher(final Context context) {
        return new FirebaseJobDispatcher(new GooglePlayDriver(context));
    }

    /**
     * Channel creation for Android O and above.
     *
     * @param nm {@link NotificationManager} object.
     */
    public static void createChannels(final NotificationManager nm) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final int importance = NotificationManager.IMPORTANCE_HIGH;
            final NotificationChannel nc = new NotificationChannel(ARD, ARD, importance);
            nm.createNotificationChannel(nc);
        } else {
            logd(AnnNotifyService.TAG, "Build Version < Android O. " +
                    "Skipping channel creation.");
        }
    }
}

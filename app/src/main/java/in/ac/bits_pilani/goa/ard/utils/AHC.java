package in.ac.bits_pilani.goa.ard.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

/**
 * Helper class for ARD.
 *
 * @author Vikramaditya Kukreja
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class AHC {

    /**
     * Package name of project.
     */
    public static final String PACKAGE_NAME = "in.ac.bits_pilani.goa.ard";

    /**
     * Application id of app.
     */
    public static final String APPLICATION_ID = "com.macbitsgoa.ard";

    /**
     * Default Log tag for project.
     */
    public static final String TAG = "MAC_ARD";

    /**
     * Name of Realm database.
     */
    public static final String REALM_ARD_DATABASE = "REALM_ARD_DATABASE";

    /**
     * Version of Realm database.
     */
    public static final int REALM_ARD_DATABASE_SCHEMA = 0;

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
     * Method to get pixel value corresponding to input dp.
     * @param context of calling method.
     * @param dp value to be converted in dp.
     * @return converted value in pixels.
     */
    public static float dpToPx(final Context context, final float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    /**
     * Method to get bitmap from vector drawable.
     * @param drawable Input {@link android.graphics.drawable.Drawable}
     * @param width The width of resultant bitmap in pixels.
     * @param height The height of resultant bitmap in pixels.
     * @return converted bitmap.
     */
    public static Bitmap getBitmapFromDrawable(final Drawable drawable, final int width, final int height) {
        final Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);

        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}

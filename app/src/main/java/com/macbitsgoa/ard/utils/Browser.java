package com.macbitsgoa.ard.utils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;
import android.support.annotation.AnimRes;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.webkit.URLUtil;
import android.widget.Toast;

import com.macbitsgoa.ard.R;

import static com.macbitsgoa.ard.utils.AHC.dpToPx;
import static com.macbitsgoa.ard.utils.AHC.getBitmapFromDrawable;
import static com.macbitsgoa.ard.utils.BrowserUtil.ACTION_BUTTON_HEIGHT;
import static com.macbitsgoa.ard.utils.BrowserUtil.CLOSE_BUTTON_HEIGHT;
import static com.macbitsgoa.ard.utils.BrowserUtil.CLOSE_BUTTON_WIDTH;

/**
 * Browser opens web links.
 * Includes default customizations which can be overridden.
 *
 * <h1>Usage</h1>
 * <p>
 * To open url with default customizations
 * <pre>
 * {@code
 * new Browser(this).launchUrl("example.com");
 * }
 * </pre>
 * </p>
 * <p>
 * To override values :
 * <pre>
 * {@code
 * new Browser(this).setToolBarColor(ActivityCompat.getColor(R.color.cyan_500), this).launchUrl("example.com");
 * }
 * </pre>
 * </p>
 * @author Rushikesh Jogdand
 */
@SuppressWarnings("WeakerAccess")
public final class Browser {

    /**
     * Host activity.
     */
    private final Activity hostActivity;

    /**
     * CustomTabsIntent Builder object.
     */
    private final CustomTabsIntent.Builder mBuilder;

    /**
     * CustomTabsIntent object.
     */
    private CustomTabsIntent mCustomTabsIntent;

    /**
     * Should url bar hide as the user scrolls down on the page.
     */
    private boolean urlBarHiding;

    /**
     * Whether to include share menu item.
     */
    private boolean shareInMenu;

    /**
     * Constructor initiates parameters to default values.
     * @param hostActivity activity from which wab tab is launched.
     */
    public Browser(final Activity hostActivity) {
        this.hostActivity = hostActivity;
        mBuilder = new CustomTabsIntent.Builder();
        initDefault();
    }

    /**
     * Default customization.
     */
    private void initDefault() {
        final int[] attrs = {R.attr.customTab_toolbar_color};
        final TypedArray ta = hostActivity.obtainStyledAttributes(R.style.AppTheme, attrs);
        setToolBarColor(ta.getColor(0, ActivityCompat.getColor(hostActivity, R.color.colorPrimary)));
        ta.recycle();

        setCloseButtonIcon(ContextCompat.getDrawable(hostActivity, R.drawable.ic_close_24dp));
        setUrlBarHiding(true);
        setShowTitle(true);
        setShareInMenu(true);
    }

    /**
     * Sets toolbar color.
     * @param color The {@link Color}
     * @return current instance.
     */
    public Browser setToolBarColor(@ColorInt final int color) {
        mBuilder.setToolbarColor(color);
        return this;
    }

    /**
     * Sets the Close button icon for the custom tab.
     * @param icon The icon {@link VectorDrawable}
     * @return current instance.
     */
    public Browser setCloseButtonIcon(@NonNull final Drawable icon) {
        final int width = (int) dpToPx(hostActivity, CLOSE_BUTTON_WIDTH);
        final int height = (int) dpToPx(hostActivity, CLOSE_BUTTON_HEIGHT);
        mBuilder.setCloseButtonIcon(getBitmapFromDrawable(icon, width, height));
        return this;
    }


    /**
     * Sets the start animations.
     * @param enterResId Resource ID of the "enter" animation for the browser.
     * @param exitResId Resource ID of the "exit" animation for the application.
     * @return current instance.
     */
    public Browser setStartAnimation(@AnimRes final int enterResId, @AnimRes final int exitResId) {
        mBuilder.setStartAnimations(hostActivity, enterResId, exitResId);
        return this;
    }

    /**
     * Sets the exit animations.
     * @param enterResId Resource ID of the "enter" animation for the application.
     * @param exitResId Resource ID of the "exit" animation for the browser.
     * @return current instance.
     */
    public Browser setExitAnimations(@AnimRes final int enterResId, @AnimRes final int exitResId) {
        mBuilder.setExitAnimations(hostActivity, enterResId, exitResId);
        return this;
    }

    /**
     * Should url bar hide when user scrolls.
     * @param value true to hide.
     * @return current instance.
     */
    public Browser setUrlBarHiding(final boolean value) {
        urlBarHiding = value;
        return this;
    }

    /**
     * Should the title of web-page be shown.
     * @param value true to show.
     * @return current instance.
     */
    public Browser setShowTitle(final boolean value) {
        mBuilder.setShowTitle(value);
        return this;
    }

    /**
     * Adds a menu item.
     * @param label Menu label.
     * @param pendingIntent Pending intent delivered when the menu item is clicked.
     * @return current instance.
     */
    public Browser addMenuItem(@NonNull final String label, @NonNull final PendingIntent pendingIntent) {
        mBuilder.addMenuItem(label, pendingIntent);
        return this;
    }

    /**
     * Whether to add default share item in menu.
     * @param value true to add.
     * @return current instance.
     */
    public Browser setShareInMenu(final boolean value) {
        shareInMenu = value;
        return this;
    }

    /**
     * Sets the action button that is displayed in the Toolbar.
     * @param icon The {@link Drawable} icon.
     *             The width:height ratio of icon should be in range [1, 2].
     * @param description The description for the button. To be used for accessibility.
     * @param pendingIntent pending intent delivered when the button is clicked.
     * @param shouldTint Whether the action button should be tinted.
     * @return current instance.
     */
    public Browser setActionButton(@NonNull final Drawable icon,
                                   @NonNull final String description,
                                   @NonNull final PendingIntent pendingIntent,
                                   final boolean shouldTint) {
        final int height = (int) dpToPx(hostActivity, ACTION_BUTTON_HEIGHT);
        final int width = (icon.getIntrinsicWidth() * height) / icon.getIntrinsicHeight();
        mBuilder.setActionButton(
                getBitmapFromDrawable(icon, width, height),
                description,
                pendingIntent,
                shouldTint
        );
        return this;
    }

    /**
     * Launches url in customized tab.
     * @param url string url.
     */
    public void launchUrl(final String url) {
        final String saneUrl = URLUtil.guessUrl(url);
        if (URLUtil.isNetworkUrl(saneUrl)) {
            initiateIntent();
            mCustomTabsIntent.launchUrl(hostActivity, Uri.parse(saneUrl));
        } else {
            hostActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(hostActivity, "Invalid URL", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * Initiate customTabsIntent.
     */
    private void initiateIntent() {
        if (urlBarHiding) {
            mBuilder.enableUrlBarHiding();
        }
        if (shareInMenu) {
            mBuilder.addDefaultShareMenuItem();
        }

        mCustomTabsIntent = mBuilder.build();
    }
}

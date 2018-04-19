package com.macbitsgoa.ard.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Viewpager that does not allowing swiping by default.
 *
 * @author Vikramaditya Kukreja
 */
public class NoSwipeViewPager extends ViewPager {
    public NoSwipeViewPager(@NonNull final Context context) {
        super(context);
    }

    public NoSwipeViewPager(@NonNull final Context context, @Nullable final AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(final MotionEvent event) {
        // Never allow swiping to switch between pages
        return false;
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        // Never allow swiping to switch between pages
        return false;
    }
}

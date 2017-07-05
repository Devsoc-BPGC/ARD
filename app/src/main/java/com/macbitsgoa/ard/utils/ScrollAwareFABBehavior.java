package com.macbitsgoa.ard.utils;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Behaviour class to change FAB view on scroll.
 *
 * @author Vikramaditya Kukreja
 */
public class ScrollAwareFABBehavior extends FloatingActionButton.Behavior {

    public ScrollAwareFABBehavior(final Context context, final AttributeSet attrs) {
        super();
    }

    @Override
    public boolean onStartNestedScroll(final CoordinatorLayout coordinatorLayout,
                                       final FloatingActionButton child,
                                       final View directTargetChild,
                                       final View target,
                                       final int nestedScrollAxes) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL
                || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target,
                nestedScrollAxes);
    }

    @Override
    public boolean layoutDependsOn(final CoordinatorLayout parent,
                                   final FloatingActionButton child,
                                   final View dependency) {
        return dependency instanceof RecyclerView;
    }

    @Override
    public void onNestedScroll(final CoordinatorLayout coordinatorLayout,
                               final FloatingActionButton child,
                               final View target,
                               final int dxConsumed,
                               final int dyConsumed,
                               final int dxUnconsumed,
                               final int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed,
                dxUnconsumed, dyUnconsumed);

        if (dyConsumed > 0 && child.getVisibility() == View.VISIBLE) {
            child.hide(new FloatingActionButton.OnVisibilityChangedListener() {
                @Override
                public void onHidden(final FloatingActionButton fab) {
                    super.onHidden(fab);
                    fab.setVisibility(View.INVISIBLE);
                }
            });
        }
        if (dyConsumed < 0 && child.getVisibility() != View.VISIBLE) {
            child.show();
        }
    }
}

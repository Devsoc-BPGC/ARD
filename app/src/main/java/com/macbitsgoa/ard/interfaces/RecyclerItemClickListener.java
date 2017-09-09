package com.macbitsgoa.ard.interfaces;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Utility class that listens for item clicks in recyclerview.
 *
 * @author Vikramaditya Kukreja
 */
public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {

    /**
     * On item click listener object for the class.
     *
     * @see OnItemClickListener
     */
    private OnItemClickListener mListener;

    /**
     * Gesture Detector object.
     */
    private GestureDetector mGestureDetector;

    /**
     * Constructor for Item click listener.
     *
     * @param context      Context object. eg. {@code getContext()} or {@code this}.
     * @param recyclerView RecyclerView to attach to.
     * @param listener     pass {@code this} if class implements {@link OnItemClickListener}.
     */
    public RecyclerItemClickListener(final Context context, final RecyclerView recyclerView,
                                     final OnItemClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(final MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(final MotionEvent e) {
                final View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null && mListener != null) {
                    mListener.onLongItemClick(child, recyclerView.getChildAdapterPosition(child));
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(final RecyclerView view, final MotionEvent e) {
        final View childView = view.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
            return true;
        }
        return false;
    }

    @Override
    public void onTouchEvent(final RecyclerView view, final MotionEvent motionEvent) {
        //Not required
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(final boolean disallowIntercept) {
        //Not required
    }
}

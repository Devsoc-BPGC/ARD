package com.macbitsgoa.ard.interfaces;

import android.view.View;

/**
 * On item click listener for Recyclerview.
 *
 * @author Vikramaditya Kukreja
 */
public interface OnItemClickListener {

    void onItemClick(final View view, final int position);

    void onLongItemClick(final View view, final int position);

}

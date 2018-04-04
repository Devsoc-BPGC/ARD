package com.macbitsgoa.ard.interfaces;

import android.view.View;

/**
 * On item click listener for Recyclerview.
 *
 * @author Vikramaditya Kukreja
 */
public interface OnItemClickListener {

    void onItemClick(View view, int position);

    void onLongItemClick(View view, int position);

}

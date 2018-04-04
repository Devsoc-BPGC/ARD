package com.macbitsgoa.ard.interfaces;

import io.realm.Sort;

/**
 * Interface to listen for sort changes in Faq items.
 *
 * @author Vikramaditya Kukreja
 */
public interface SortOrderChangeListener {
    void onSortOrderChanged(Sort newSortOrder);
}

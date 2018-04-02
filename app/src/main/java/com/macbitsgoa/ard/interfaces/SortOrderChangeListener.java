package com.macbitsgoa.ard.interfaces;

import io.realm.Sort;

public interface SortOrderChangeListener {
    void onSortOrderChanged(final Sort newSortOrder);
}

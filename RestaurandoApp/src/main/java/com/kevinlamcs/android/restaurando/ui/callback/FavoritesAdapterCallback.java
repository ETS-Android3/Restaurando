package com.kevinlamcs.android.restaurando.ui.callback;

/**
 * Callback between the favorites adapter and the favorites fragment
 */
public interface FavoritesAdapterCallback {
    void addSubheaderCallback();
    void removeSubheaderCallback();
    void emptyStateVisibilityCallback(boolean isEmpty);
}

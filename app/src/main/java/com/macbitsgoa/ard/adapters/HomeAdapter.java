package com.macbitsgoa.ard.adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.interfaces.AdapterNotificationListener;
import com.macbitsgoa.ard.keys.HomeItemKeys;
import com.macbitsgoa.ard.models.home.HomeItem;
import com.macbitsgoa.ard.viewholders.HomeItemViewHolder;
import com.macbitsgoa.ard.viewholders.ImageViewHolder;

import javax.annotation.Nullable;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Adapter class to display data in HomeFragment.
 *
 * @author Vikramaditya Kukreja
 */
public class HomeAdapter extends BaseAdapter<HomeItemViewHolder> implements
        ImageViewHolder.ImageClickListener,
        OrderedRealmCollectionChangeListener<RealmResults<HomeItem>> {

    /**
     * TAG for class.
     */
    public static final String TAG = HomeAdapter.class.getSimpleName();

    /**
     * List to hold all data.
     */
    private RealmResults<HomeItem> homeItems;

    /**
     * Context for use with glide.
     */
    private final Context context;

    /**
     * Notification listener.
     */
    private final AdapterNotificationListener anl;

    /**
     * Constructor that populates recyclerView.
     *
     * @param context For use with Image downloading and
     *                {@link AdapterNotificationListener}listener.
     */
    public HomeAdapter(@NonNull final Context context) {
        this.context = context;
        if (this.context instanceof AdapterNotificationListener) {
            anl = (AdapterNotificationListener) this.context;
        } else {
            anl = null;
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull final RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        homeItems = database
                .where(HomeItem.class)
                .findAllSortedAsync(HomeItemKeys.DATE, Sort.DESCENDING);
        homeItems.addChangeListener(this);
    }

    @NonNull
    @Override
    public HomeItemViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view = inflater.inflate(R.layout.vh_home_item, parent, false);
        return new HomeItemViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull final HomeItemViewHolder hivh, final int position) {
        hivh.setData(homeItems.get(position));
    }

    @Override
    public int getItemCount() {
        return homeItems.size();
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull final RecyclerView recyclerView) {
        homeItems.removeAllChangeListeners();
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public void onImageClick(final Uri uri) {
        if (uri == null) {
            return;
        }
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
        } catch (final ActivityNotFoundException e) {
            Toast.makeText(context, "Error loading image", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Activity not found");
        }
    }

    @Override
    public void onChange(@NonNull final RealmResults<HomeItem> homeItems,
                         @Nullable final OrderedCollectionChangeSet changeSet) {
        // `null`  means the async query returns the first time.
        if (changeSet == null) {
            notifyDataSetChanged();
            if (anl != null) {
                anl.onAdapterNotified(getItemCount());
            }
            return;
        }
        // For deletions, the adapter has to be notified in reverse order.
        final OrderedCollectionChangeSet.Range[] deletions = changeSet.getDeletionRanges();
        for (int i = deletions.length - 1; i >= 0; i--) {
            final OrderedCollectionChangeSet.Range range = deletions[i];
            notifyItemRangeRemoved(range.startIndex, range.length);
        }

        final OrderedCollectionChangeSet.Range[] insertions = changeSet.getInsertionRanges();
        for (final OrderedCollectionChangeSet.Range range : insertions) {
            notifyItemRangeInserted(range.startIndex, range.length);
        }

        final OrderedCollectionChangeSet.Range[] modifications = changeSet.getChangeRanges();
        for (final OrderedCollectionChangeSet.Range range : modifications) {
            notifyItemRangeChanged(range.startIndex, range.length);
        }
        if (anl != null) {
            anl.onAdapterNotified(getItemCount());
        }
    }
}

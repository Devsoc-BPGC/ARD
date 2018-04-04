package com.macbitsgoa.ard.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.interfaces.AdapterNotificationListener;
import com.macbitsgoa.ard.keys.AnnItemKeys;
import com.macbitsgoa.ard.models.AnnItem;
import com.macbitsgoa.ard.utils.AHC;
import com.macbitsgoa.ard.viewholders.AnnViewHolder;

import javax.annotation.Nullable;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Adapter class to display annItems in HomeFragment.
 *
 * @author Vikramaditya Kukreja
 */
public class AnnAdapter extends BaseAdapter<AnnViewHolder>
        implements OrderedRealmCollectionChangeListener<RealmResults<AnnItem>> {

    /**
     * TAG for class.
     */
    public static final String TAG = AnnAdapter.class.getSimpleName();

    /**
     * List to hold all annItems.
     */
    private RealmResults<AnnItem> annItems;

    /**
     * Listener object for item changes.
     */
    private final AdapterNotificationListener anl;

    /**
     * Constructor to set listener as well.
     *
     * @param context Activity context.
     */
    public AnnAdapter(@NonNull final Context context) {
        if (context instanceof AdapterNotificationListener)
            anl = (AdapterNotificationListener) context;
        else anl = null;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        annItems = database.where(AnnItem.class)
                .findAllSortedAsync(AnnItemKeys.DATE, Sort.DESCENDING);
        annItems.addChangeListener(this);
        if (anl != null) anl.onAdapterNotified(getItemCount());
    }

    @NonNull
    @Override
    public AnnViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view;
        view = inflater.inflate(R.layout.vh_ann_activity_item, parent, false);
        return new AnnViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AnnViewHolder anvh, final int position) {
        final AnnItem ai = annItems.get(position);
        anvh.data.setText(Html.fromHtml(ai.getData()));
        anvh.extras.setText(Html.fromHtml(ai.getAuthor()
                + AHC.SEPARATOR
                + AHC.getSimpleDayOrTime(ai.getDate())));
    }

    @Override
    public int getItemCount() {
        return annItems.size();
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        annItems.removeAllChangeListeners();
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public void onChange(RealmResults<AnnItem> annItems, @Nullable OrderedCollectionChangeSet changeSet) {
        // `null`  means the async query returns the first time.
        if (changeSet == null) {
            notifyDataSetChanged();
            if (anl != null) anl.onAdapterNotified(getItemCount());
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

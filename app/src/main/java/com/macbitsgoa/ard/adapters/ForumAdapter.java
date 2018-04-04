package com.macbitsgoa.ard.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.fragments.BaseFragment;
import com.macbitsgoa.ard.interfaces.AdapterNotificationListener;
import com.macbitsgoa.ard.keys.FaqItemKeys;
import com.macbitsgoa.ard.models.FaqItem;
import com.macbitsgoa.ard.viewholders.FaqViewHolder;

import javax.annotation.Nullable;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Adapter to display forums. Call using {@link ForumAdapter#ForumAdapter(String, BaseFragment)}.
 *
 * @author Vikramaditya Kukreja
 */
public class ForumAdapter extends BaseAdapter<FaqViewHolder>
        implements OrderedRealmCollectionChangeListener<RealmResults<FaqItem>> {

    /**
     * Item list to use as data source.
     */
    private RealmResults<FaqItem> faqItems;

    private final String section;

    private Sort defaultSort;

    private final AdapterNotificationListener anl;
    /**
     * Maintains expanded text info.
     */
    private SparseBooleanArray sba;

    /**
     * Constructor for items of {@link FaqItem} class.
     * It also initialises a sparse boolean array to maintain clicked items info.
     *
     * @param section FaqItem section.
     */
    public ForumAdapter(@NonNull final String section, final BaseFragment bf) {
        this.section = section;
        defaultSort = Sort.DESCENDING;
        if (bf instanceof AdapterNotificationListener) {
            anl = (AdapterNotificationListener) bf;
        } else {
            anl = null;
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        faqItems = database.where(FaqItem.class)
                .equalTo(FaqItemKeys.SECTION, section)
                .findAllSortedAsync(new String[]{FaqItemKeys.SUB_SECTION, FaqItemKeys.UPDATE},
                        new Sort[]{Sort.ASCENDING, defaultSort});
        faqItems.addChangeListener(this);
        sba = new SparseBooleanArray(getItemCount());
    }

    @NonNull
    @Override
    public FaqViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view = inflater.inflate(R.layout.vh_fg_forum_general, parent, false);
        return new FaqViewHolder(view, sba);
    }

    @Override
    public void onBindViewHolder(@NonNull final FaqViewHolder holder, final int position) {
        final FaqItem fi = faqItems.get(position);
        holder.setQuestionTV(fi.getQuestion());
        holder.setAnswerTV(fi.getAnswer());
        if (position > 0) {
            if (faqItems.get(position - 1).getSubSection().equals(fi.getSubSection()))
                holder.hideSubSection();
            else holder.setSubSection(fi.getSubSection());
        } else {
            holder.setSubSection(fi.getSubSection());
        }
    }

    @Override
    public int getItemCount() {
        return faqItems.size();
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        faqItems.removeAllChangeListeners();
        super.onDetachedFromRecyclerView(recyclerView);
    }

    public void sortBy(final String fieldName, final boolean maintainSortOrder) {
        if (maintainSortOrder) {
            faqItems = faqItems.sort(fieldName, defaultSort);
        } else {
            if (defaultSort == Sort.DESCENDING) {
                defaultSort = Sort.ASCENDING;
            } else {
                defaultSort = Sort.DESCENDING;
            }
        }
        faqItems.removeAllChangeListeners();
        faqItems = faqItems.sort(new String[]{FaqItemKeys.SUB_SECTION, fieldName},
                new Sort[]{Sort.ASCENDING, defaultSort});
        sba.clear();
        notifyDataSetChanged();
        if (anl != null) anl.onAdapterNotified(getItemCount());
        faqItems.addChangeListener(this);
    }

    @Override
    public void onChange(@NonNull final RealmResults<FaqItem> faqItems,
                         @Nullable final OrderedCollectionChangeSet changeSet) {
        // `null`  means the async query returns the first time.
        //We are not listening for each and every update but only for the first one.
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

package com.macbitsgoa.ard.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.models.FaqItem;
import com.macbitsgoa.ard.models.TypeItem;
import com.macbitsgoa.ard.viewholders.FaqViewHolder;

import io.realm.RealmResults;

/**
 * Adapter to display forums. Call using {@link ForumAdapter#ForumAdapter(RealmResults)} where
 * list is a {@link RealmResults} object of type {@link TypeItem} class.
 *
 * @author Vikramaditya Kukreja
 */
public class ForumAdapter extends RecyclerView.Adapter<FaqViewHolder> {

    /**
     * Item list to use as data source.
     */
    private RealmResults<FaqItem> items;

    /**
     * Maintains expanded text info.
     */
    private SparseBooleanArray sba;

    /**
     * Constructor for items of {@link RealmResults<FaqItem>} class.
     * It also initialises a sparse boolean array to maintain clicked items info.
     *
     * @param items {@link RealmResults} of {@link FaqItem}.
     */
    public ForumAdapter(@NonNull final RealmResults<FaqItem> items) {
        this.items = items;
        sba = new SparseBooleanArray(getItemCount());
    }

    public void setNewData(@NonNull final RealmResults<FaqItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FaqViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view = inflater.inflate(R.layout.vh_fg_forum_general, parent, false);
        return new FaqViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FaqViewHolder holder, final int position) {
        final FaqItem fi = items.get(position);
        holder.setQuestionTV(fi.getQuestion());
        holder.setAnswerTV(fi.getAnswer(), sba);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}

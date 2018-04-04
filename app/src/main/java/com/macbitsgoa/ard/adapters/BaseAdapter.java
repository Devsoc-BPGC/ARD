package com.macbitsgoa.ard.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import io.realm.Realm;

public class BaseAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    /**
     * Realm database field. It is initialised when recyclerView is attached and closed when
     * detached.
     */
    Realm database;

    @Override
    public void onAttachedToRecyclerView(@NonNull final RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        database = Realm.getDefaultInstance();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull final VH holder, final int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull final RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        database.close();
    }
}

package com.macbitsgoa.ard.adapters;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.models.AnnItem;
import com.macbitsgoa.ard.models.TypeItem;
import com.macbitsgoa.ard.utils.AHC;
import com.macbitsgoa.ard.viewholders.AnnViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter class to display data in HomeFragment.
 *
 * @author Vikramaditya Kukreja
 */
public class HomeAdapter extends RecyclerView.Adapter<ViewHolder> {

    /**
     * List to hold all data.
     */
    private List<TypeItem> data;

    /**
     * Constructor that initialises empty data list of type {@link TypeItem}.
     */
    public HomeAdapter() {
        this.data = new ArrayList<>(0);
    }

    /**
     * Get the current list that is being used by the adapter.
     * This can be used to update the list.
     *
     * @return {@code List<TypeItem>} current data.
     */
    public List<TypeItem> getData() {
        return data;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new AnnViewHolder(inflater.inflate(R.layout.viewholder_home_fragment_ann, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final AnnViewHolder annViewHolder = (AnnViewHolder) holder;
        final AnnItem item = (AnnItem) data.get(position).getData();
        annViewHolder.data.setText(item.getData());
        final String extras = item.getAuthor() + ", " + AHC.getSimpleDate(item.getDate());
        annViewHolder.extras.setText(extras);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(final int position) {
        return data.get(position).getType();
    }
}

package com.macbitsgoa.ard.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.viewholders.DetailsViewHolder;

import java.util.List;

/**
 * Adapter to display details list. Call using {@link DetailsAdapter#DetailsAdapter(List<String>)}.
 *
 * @author Aayush Singla
 * @author Vikramaditya Kukreja
 */
public class DetailsAdapter extends RecyclerView.Adapter<DetailsViewHolder> {

    /**
     * List to store titles of all the options in details list.
     */
    private List<String> itemsList;

    /**
     * Constructor for recyclerView adapter.
     * It also initialises list items to be displayed in the details list.
     */
    public DetailsAdapter(final List<String> itemsList) {
        this.itemsList = itemsList;
    }

    @NonNull
    @Override
    public DetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        final View view = layoutInflater.inflate(R.layout.vh_details_main, parent, false);
        return new DetailsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailsViewHolder holder, int position) {
        holder.text.setText(itemsList.get(position));
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }
}

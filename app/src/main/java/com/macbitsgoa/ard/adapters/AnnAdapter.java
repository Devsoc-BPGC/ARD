package com.macbitsgoa.ard.adapters;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.models.AnnItem;
import com.macbitsgoa.ard.models.TypeItem;
import com.macbitsgoa.ard.utils.AHC;
import com.macbitsgoa.ard.viewholders.AnnViewHolder;

import io.realm.RealmResults;

/**
 * Adapter class to display data in HomeFragment.
 *
 * @author Vikramaditya Kukreja
 */
public class AnnAdapter extends RecyclerView.Adapter<AnnViewHolder> {

    /**
     * TAG for class.
     */
    public static final String TAG = AnnAdapter.class.getSimpleName();

    /**
     * List to hold all data.
     */
    private RealmResults<AnnItem> data;

    /**
     * Constructor that initialises empty data list of type {@link TypeItem}.
     *
     * @param data Data of type {@link RealmResults<AnnItem>}
     */
    public AnnAdapter(@Nullable final RealmResults<AnnItem> data) {
        this.data = data;
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
        final AnnItem ai = data.get(position);
        anvh.data.setText(Html.fromHtml(ai.getData()));
        anvh.extras.setText(Html.fromHtml(ai.getAuthor()
                + AHC.SEPARATOR
                + AHC.getSimpleDayOrTime(ai.getDate())));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}

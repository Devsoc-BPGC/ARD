package com.macbitsgoa.ard.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.macbitsgoa.ard.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Details Item ViewHolder Class for options.
 *
 * @author Aayush Singla
 * @author Vikramaditya Kukreja
 */
public class DetailsViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.tv_vh_details_title)
    public TextView text;

    public DetailsViewHolder(final View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}

package com.macbitsgoa.ard.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.macbitsgoa.ard.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * ViewHolder for {@link com.macbitsgoa.ard.types.PostType#ANNOUNCEMENT}.
 *
 * @author Vikramaditya Kukreja
 */
public class AnnViewHolder extends RecyclerView.ViewHolder {

    /**
     * TAG for class.
     */
    public static final String TAG = AnnViewHolder.class.getSimpleName();

    /**
     * Author of the post.
     */
    @BindView(R.id.textView_viewHolder_ann_extras)
    public TextView extras;

    /**
     * Announcement data.
     */
    @BindView(R.id.textView_viewHolder_ann_data)
    public TextView data;

    /**
     * Constructor for {@link com.macbitsgoa.ard.types.PostType#ANNOUNCEMENT} post.
     *
     * @param itemView inflated view.
     */
    public AnnViewHolder(final View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}

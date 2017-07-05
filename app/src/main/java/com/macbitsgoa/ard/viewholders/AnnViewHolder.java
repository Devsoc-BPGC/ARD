package com.macbitsgoa.ard.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.macbitsgoa.ard.R;

/**
 * ViewHolder for {@link com.macbitsgoa.ard.types.PostType#ANNOUNCEMENT}.
 *
 * @author Vikramaditya Kukreja
 */
public class AnnViewHolder extends RecyclerView.ViewHolder {

    /**
     * Author of the post.
     */
    public TextView extras;

    /**
     * Announcement data.
     */
    public TextView data;

    /**
     * Constructor for {@link com.macbitsgoa.ard.types.PostType#ANNOUNCEMENT} post.
     *
     * @param itemView inflated view.
     */
    public AnnViewHolder(final View itemView) {
        super(itemView);
        data = (TextView) itemView.findViewById(R.id.textView_viewHolder_ann_data);
        extras = (TextView) itemView.findViewById(R.id.textView_viewHolder_ann_extras);
    }
}

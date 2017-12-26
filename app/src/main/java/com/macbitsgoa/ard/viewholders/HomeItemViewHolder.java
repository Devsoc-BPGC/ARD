package com.macbitsgoa.ard.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.macbitsgoa.ard.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Home item view holder class.
 *
 * @author Vikramaditya Kukreja
 */
public class HomeItemViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.imgView_home_item)
    public ImageView imageView;

    @BindView(R.id.tv_vh_home_item_1)
    public TextView textView1;

    @BindView(R.id.tv_vh_home_item_2)
    public TextView textView2;

    /**
     * Status bar that contains useful info. Use this view for visibility work only.
     */
    @BindView(R.id.ll_vh_home_status)
    public LinearLayout statusBar;

    /**
     * Textview that handles # of images in the news.
     */
    @BindView(R.id.tv_vh_home_item_image_count)
    public TextView imageCount;

    /**
     * Textview that handles # of comments.
     */
    @BindView(R.id.tv_vh_home_item_comment_count)
    public TextView commentCount;

    /**
     * Constructor taking item view as param
     *
     * @param itemView item view to use.
     */
    public HomeItemViewHolder(final View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        //TODO Add viewpager with crossfade instead of imageview
    }
}

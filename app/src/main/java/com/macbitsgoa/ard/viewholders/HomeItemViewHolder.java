package com.macbitsgoa.ard.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.macbitsgoa.ard.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by vikramaditya on 15/11/17.
 */

public class HomeItemViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.imgView_home_item)
    public ImageView imageView;

    @BindView(R.id.tv_vh_home_item_1)
    public TextView textView1;

    @BindView(R.id.tv_vh_home_item_2)
    public TextView textView2;

    public HomeItemViewHolder(final View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        //TODO Add viewpager with crossfade instead of imageview
    }
}

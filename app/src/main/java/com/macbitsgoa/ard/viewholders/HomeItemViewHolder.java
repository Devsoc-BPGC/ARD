package com.macbitsgoa.ard.viewholders;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.activities.PostDetailsActivity;
import com.macbitsgoa.ard.keys.HomeItemKeys;
import com.macbitsgoa.ard.models.home.HomeItem;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Home item view holder class.
 *
 * @author Vikramaditya Kukreja
 */
public class HomeItemViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.imgView_home_item)
    ImageView imageView;

    @BindView(R.id.tv_vh_home_item_1)
    TextView textView1;

    @BindView(R.id.tv_vh_home_item_2)
    TextView textView2;

    /**
     * Status bar that contains useful info. Use this view for visibility work only.
     */
    @BindView(R.id.ll_vh_home_status)
    LinearLayout statusBar;

    /**
     * Textview that handles # of images in the news.
     */
    @BindView(R.id.tv_vh_home_item_image_count)
    TextView imageCount;

    /**
     * Textview that handles # of comments.
     */
    @BindView(R.id.tv_vh_home_item_comment_count)
    TextView commentCount;

    private HomeItem hi;

    private final Context context;

    /**
     * Constructor taking item view as param
     *
     * @param itemView item view to use.
     */
    public HomeItemViewHolder(final View itemView, final Context context) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.context = context;
        //TODO Add viewpager with crossfade instead of imageview
        itemView.setOnClickListener(v -> {
            final Intent intent = new Intent(this.context, PostDetailsActivity.class);
            intent.putExtra(HomeItemKeys.KEY, hi.getKey());
            this.context.startActivity(intent);
        });
    }

    public void setData(final HomeItem hi) {
        this.hi = hi;
        if (hi.getImages().size() == 0) {
            imageView.setVisibility(View.GONE);
            statusBar.setVisibility(View.GONE);
        } else {
            imageView.setVisibility(View.VISIBLE);
            statusBar.setVisibility(View.VISIBLE);

            final String numberFormat = "%d";

            commentCount.setText(String.format(Locale.ENGLISH, numberFormat, hi.getTexts().size()));
            imageCount.setText(String.format(Locale.ENGLISH, numberFormat, hi.getImages().size()));
            Glide.with(context)
                    .load(hi.getImages().get(0).getPhotoUrl())
                    .transition(DrawableTransitionOptions.withCrossFade(500))
                    .apply(RequestOptions.centerCropTransform())
                    .into(imageView);

        }
        if (hi.getTexts().size() == 0) {
            textView1.setVisibility(View.GONE);
            textView2.setVisibility(View.GONE);
        } else if (hi.getTexts().size() == 1) {
            textView1.setVisibility(View.VISIBLE);
            textView2.setVisibility(View.GONE);
            textView1.setText(Html.fromHtml(hi.getTexts().get(0).getData()));
        } else {
            textView1.setVisibility(View.VISIBLE);
            textView2.setVisibility(View.VISIBLE);
            textView1.setText(Html.fromHtml(hi.getTexts().get(0).getData()));
            textView2.setText(Html.fromHtml(hi.getTexts().get(1).getData()));
        }
    }
}

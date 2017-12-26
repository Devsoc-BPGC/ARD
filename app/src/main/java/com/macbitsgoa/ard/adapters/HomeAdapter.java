package com.macbitsgoa.ard.adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.models.AnnItem;
import com.macbitsgoa.ard.models.TypeItem;
import com.macbitsgoa.ard.models.home.HomeItem;
import com.macbitsgoa.ard.models.home.PhotoItem;
import com.macbitsgoa.ard.models.home.TextItem;
import com.macbitsgoa.ard.types.HomeType;
import com.macbitsgoa.ard.types.PostType;
import com.macbitsgoa.ard.utils.AHC;
import com.macbitsgoa.ard.viewholders.AnnViewHolder;
import com.macbitsgoa.ard.viewholders.AnnouncementViewHolder;
import com.macbitsgoa.ard.viewholders.HomeItemViewHolder;
import com.macbitsgoa.ard.viewholders.ImageViewHolder;
import com.macbitsgoa.ard.viewholders.TextViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adapter class to display data in HomeFragment.
 *
 * @author Vikramaditya Kukreja
 */
public class HomeAdapter extends RecyclerView.Adapter<ViewHolder> implements ImageViewHolder.ImageClickListener {

    /**
     * TAG for class.
     */
    public static final String TAG = HomeAdapter.class.getSimpleName();

    /**
     * Viewtype value for Announcement.
     */
    public static final int ANNOUNCEMENT_TAB = -1;

    /**
     * List to hold all data.
     */
    private List<TypeItem> data;

    /**
     * Context for use with glide.
     */
    private Context context;

    /**
     * Constructor that initialises empty data list of type {@link TypeItem}.
     *
     * @param data    Data of type {@link TypeItem}
     * @param context For use with Image downloading.
     */
    public HomeAdapter(@Nullable final List<TypeItem> data, @NonNull final Context context) {
        this.data = data;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view;
        switch (viewType) {
            case ANNOUNCEMENT_TAB:
                view = inflater.inflate(R.layout.vh_ann_card_fragment_home, parent, false);
                return new AnnouncementViewHolder(view);
            case PostType.ANNOUNCEMENT:
                view = inflater.inflate(R.layout.vh_ann_activity_item, parent, false);
                return new AnnViewHolder(view);
            case HomeType.HOME_ITEM:
                view = inflater.inflate(R.layout.vh_home_item_1, parent, false);
                return new HomeItemViewHolder(view);
            case HomeType.TEXT_ITEM:
                view = inflater.inflate(R.layout.vh_big_text, parent, false);
                return new TextViewHolder(view, R.id.tv_vh_big_text);
            case HomeType.PHOTO_ITEM:
                view = inflater.inflate(R.layout.vh_big_image, parent, false);
                return new ImageViewHolder(view, context, R.id.imgView_vh_big_image, this);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Object obj = data.get(position).getData();
        switch (holder.getItemViewType()) {
            case ANNOUNCEMENT_TAB: {
                final AnnouncementViewHolder avh = (AnnouncementViewHolder) holder;
                avh.setTextData((ArrayList<String>) obj);
                avh.subtitleTextView.setText("Announcements");
                break;
            }
            case PostType.ANNOUNCEMENT: {
                final AnnViewHolder anvh = (AnnViewHolder) holder;
                final AnnItem ai = (AnnItem) obj;
                anvh.data.setText(Html.fromHtml(ai.getData()));
                anvh.extras.setText(Html.fromHtml(ai.getAuthor()
                        + ", "
                        + AHC.getSimpleDayAndTime(ai.getDate())));
                if (ai.isRead()) anvh.newTag.setVisibility(View.INVISIBLE);
                else anvh.newTag.setVisibility(View.VISIBLE);
                break;
            }
            case HomeType.HOME_ITEM: {
                final HomeItemViewHolder hivh = (HomeItemViewHolder) holder;
                final HomeItem hi = (HomeItem) obj;
                if (hi.getImages().size() == 0) {
                    hivh.imageView.setVisibility(View.GONE);
                    hivh.statusBar.setVisibility(View.GONE);
                } else {
                    hivh.imageView.setVisibility(View.VISIBLE);
                    hivh.statusBar.setVisibility(View.VISIBLE);

                    final String numberFormat = "%d";

                    hivh.commentCount.setText(String.format(Locale.ENGLISH, numberFormat, hi.getTexts().size()));
                    hivh.imageCount.setText(String.format(Locale.ENGLISH, numberFormat, hi.getImages().size()));
                    Glide.with(context)
                            .load(hi.getImages().get(0).getPhotoUrl())
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .apply(RequestOptions.centerCropTransform())
                            .into(hivh.imageView);

                }
                if (hi.getTexts().size() == 0) {
                    hivh.textView1.setVisibility(View.GONE);
                    hivh.textView2.setVisibility(View.GONE);
                } else if (hi.getTexts().size() == 1) {
                    hivh.textView1.setVisibility(View.VISIBLE);
                    hivh.textView2.setVisibility(View.GONE);
                    hivh.textView1.setText(Html.fromHtml(hi.getTexts().get(0).getData()));
                } else {
                    hivh.textView1.setVisibility(View.VISIBLE);
                    hivh.textView2.setVisibility(View.VISIBLE);
                    hivh.textView1.setText(Html.fromHtml(hi.getTexts().get(0).getData()));
                    hivh.textView2.setText(Html.fromHtml(hi.getTexts().get(1).getData()));
                }
                break;
            }
            case HomeType.TEXT_ITEM: {
                final TextViewHolder tvh = (TextViewHolder) holder;
                final TextItem ti = (TextItem) obj;
                tvh.setText(ti.getData());
                break;
            }
            case HomeType.PHOTO_ITEM: {
                final ImageViewHolder imgvh = (ImageViewHolder) holder;
                final PhotoItem pi = (PhotoItem) obj;
                imgvh.setImage(pi.getPhotoUrl());
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public int getItemViewType(final int position) {
        return data.get(position).getType();
    }

    @Override
    public void onImageClick(final String url) {
        if (url == null) return;
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (final ActivityNotFoundException e) {
            Toast.makeText(context, "Error loading image", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Activity not found");
        }
    }
}

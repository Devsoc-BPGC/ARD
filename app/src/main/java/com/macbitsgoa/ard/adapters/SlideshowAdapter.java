package com.macbitsgoa.ard.adapters;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.keys.SlideshowItemKeys;
import com.macbitsgoa.ard.models.SlideshowItem;
import com.macbitsgoa.ard.utils.AHC;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by vikramaditya on 7/11/17.
 */
public class SlideshowAdapter extends PagerAdapter
        implements RealmChangeListener<RealmResults<SlideshowItem>> {

    /**
     * TAG for the class.
     */
    public static final String TAG = SlideshowAdapter.class.getSimpleName();

    /**
     * Realm database.
     */
    private final Realm database;

    /**
     * Realm results for slideshow data.
     */
    private RealmResults<SlideshowItem> slideshowItems;

    public SlideshowAdapter() {
        database = Realm.getDefaultInstance();
        slideshowItems = database
                .where(SlideshowItem.class)
                .findAllSortedAsync(SlideshowItemKeys.PHOTO_DATE, Sort.DESCENDING);
        slideshowItems.addChangeListener(this);
    }

    @SuppressWarnings("OverlyNestedMethod")
    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, final int position) {
        final View itemView = LayoutInflater
                .from(container.getContext())
                .inflate(R.layout.vh_slideshow_fragment_home, container, false);

        if (slideshowItems.size() > 0) {
            final SlideshowItem si = slideshowItems.get(position);
            Glide.with(container.getContext())
                    .load(si.getPhotoUrl())
                    .apply(RequestOptions.placeholderOf(R.drawable.nav_drawer_default_image))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into((ImageView) itemView
                            .findViewById(R.id.imgView_vh_slideshow_fragment_home));

            final TextView photoTitleTV = itemView.findViewById(R.id.tv_vh_slideshow_fg_home_title);
            final TextView photoDescTV = itemView.findViewById(R.id.tv_vh_slideshow_fg_home_desc);
            final CardView tagCV = itemView.findViewById(R.id.cv_vh_slideshow_fg_home_tag);
            final TextView tagTV = itemView.findViewById(R.id.tv_vh_slideshow_fg_home_tag);

            if (TextUtils.isEmpty(si.getPhotoTitle())) {
                photoTitleTV.setVisibility(View.GONE);
            } else {
                photoTitleTV.setVisibility(View.VISIBLE);
                photoTitleTV.setText(Html.fromHtml(si.getPhotoTitle()));
            }

            if (TextUtils.isEmpty(si.getPhotoDesc())) {
                photoDescTV.setVisibility(View.GONE);
            } else {
                photoDescTV.setVisibility(View.VISIBLE);
                photoDescTV.setText(Html.fromHtml(si.getPhotoDesc()));
            }

            if (TextUtils.isEmpty(si.getPhotoTag())) {
                tagCV.setVisibility(View.GONE);
            } else {
                tagCV.setVisibility(View.VISIBLE);
                tagTV.setText(Html.fromHtml(si.getPhotoTag()));
                if (!TextUtils.isEmpty(si.getPhotoTagColor()) && TextUtils.isEmpty(si.getPhotoTagTextColor())) {
                    try {
                        tagCV.setCardBackgroundColor(Color.parseColor(si.getPhotoTagColor()));
                        tagTV.setTextColor(Color.parseColor(si.getPhotoTagTextColor()));
                    } catch (final IllegalArgumentException e) {
                        AHC.logd(TAG, e.getMessage());
                    }
                }
            }
        }
        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(@NonNull final ViewGroup container, final int position,
                            @NonNull final Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return slideshowItems.size() != 0 ? slideshowItems.size() : 1;
    }

    @Override
    public boolean isViewFromObject(@NonNull final View view, @NonNull final Object object) {
        return view == object;
    }

    @Override
    public void onChange(@NonNull final RealmResults<SlideshowItem> slideshowItems) {
        notifyDataSetChanged();
    }

    public void notifyOfDestruction() {
        if (database != null) {
            slideshowItems.removeAllChangeListeners();
            database.close();
        }
    }
}

package com.macbitsgoa.ard.adapters;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.models.SlideshowItem;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by vikramaditya on 7/11/17.
 */
public class SlideshowAdapter extends PagerAdapter {
    private RealmResults<SlideshowItem> slideshowItems;
    private Realm database;

    public SlideshowAdapter() {
        database = Realm.getDefaultInstance();
        slideshowItems = database.where(SlideshowItem.class)
                .findAllSorted("photoDate", Sort.DESCENDING);
        slideshowItems.addChangeListener(slideshowItems -> notifyDataSetChanged());
    }

    public void close() {
        database.close();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View itemView = LayoutInflater
                .from(container.getContext())
                .inflate(R.layout.vh_slideshow_fragment_home, container, false);

        if (slideshowItems.size() == getCount()) {
            Glide.with(container.getContext())
                    .load(slideshowItems.get(position).getPhotoUrl())
                    .into((ImageView) itemView
                            .findViewById(R.id.imgView_vh_slideshow_fragment_home));
            ((TextView) itemView.findViewById(R.id.tv_vh_slideshow_fg_home_title)).setText(slideshowItems.get(position).getPhotoTitle());
            ((TextView) itemView.findViewById(R.id.tv_vh_slideshow_fg_home_desc)).setText(Html.fromHtml(slideshowItems.get(position).getPhotoDesc()));

        }
        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(@NonNull final ViewGroup container, final int position, @NonNull Object object) {
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
}

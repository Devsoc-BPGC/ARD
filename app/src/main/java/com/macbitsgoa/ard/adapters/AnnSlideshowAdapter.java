package com.macbitsgoa.ard.adapters;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.keys.AnnItemKeys;
import com.macbitsgoa.ard.models.AnnItem;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Adapter to display ann slideshow content.
 *
 * @author Vikramaditya Kukreja
 */
public class AnnSlideshowAdapter extends PagerAdapter
        implements RealmChangeListener<RealmResults<AnnItem>> {

    /**
     * Realm database reference.
     */
    private final Realm database;

    /**
     * Realm results for ann data.
     */
    private RealmResults<AnnItem> annItems;

    public AnnSlideshowAdapter() {
        database = Realm.getDefaultInstance();
        this.annItems = database
                .where(AnnItem.class)
                .findAllSortedAsync(AnnItemKeys.DATE, Sort.DESCENDING);
        this.annItems.addChangeListener(this);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, final int position) {
        final View itemView = LayoutInflater
                .from(container.getContext())
                .inflate(R.layout.vh_announcement_slideshow_title, container, false);

        ((TextView) itemView.findViewById(R.id.tv_vh_announcement_title))
                .setText(Html.fromHtml(annItems.get(position).getData()));
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
        return annItems.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull final View view, @NonNull final Object object) {
        return view == object;
    }

    @Override
    public void onChange(@NonNull final RealmResults<AnnItem> annItems) {
        notifyDataSetChanged();
    }

    public void notifyOfDesctruction() {
        annItems.removeAllChangeListeners();
        database.close();
    }
}

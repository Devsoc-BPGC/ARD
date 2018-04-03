package com.macbitsgoa.ard.adapters;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.models.Slide;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Created by vikramaditya on 7/11/17.
 */
public class SlideshowAdapter extends PagerAdapter implements RealmChangeListener<RealmResults<Slide> > {

    /**
     * TAG for the class.
     */
    public static final String TAG = SlideshowAdapter.class.getSimpleName();

    private Realm realm;

    private ArrayList<Slide> slides;

    public SlideshowAdapter() {
        this.slides = new ArrayList<>();
        realm = Realm.getDefaultInstance();
        RealmResults<Slide> slides = realm.where(Slide.class).findAllAsync();
        slides.addChangeListener(this);
    }

    @SuppressWarnings("OverlyNestedMethod")
    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, final int position) {
        final View itemView = LayoutInflater
                .from(container.getContext())
                .inflate(R.layout.vh_slideshow_fragment_home, container, false);

        if (slides.size() > 0) {
            final Slide si = slides.get(position);
            Glide.with(container.getContext())
                    .load(si.photoUrl)
                    .apply(RequestOptions.placeholderOf(R.drawable.nav_drawer_default_image))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into((ImageView) itemView
                            .findViewById(R.id.imgView_vh_slideshow_fragment_home));

            final TextView photoTitleTV = itemView.findViewById(R.id.tv_vh_slideshow_fg_home_title);
            final TextView photoDescTV = itemView.findViewById(R.id.tv_vh_slideshow_fg_home_desc);
            final CardView tagCV = itemView.findViewById(R.id.cv_vh_slideshow_fg_home_tag);
            final TextView tagTV = itemView.findViewById(R.id.tv_vh_slideshow_fg_home_tag);

            if (TextUtils.isEmpty(si.photoTitle)) {
                photoTitleTV.setVisibility(View.GONE);
            } else {
                photoTitleTV.setVisibility(View.VISIBLE);
                photoTitleTV.setText(Html.fromHtml(si.photoTitle));
            }

            if (TextUtils.isEmpty(si.photoDesc)) {
                photoDescTV.setVisibility(View.GONE);
            } else {
                photoDescTV.setVisibility(View.VISIBLE);
                photoDescTV.setText(Html.fromHtml(si.photoDesc));
            }

            if (TextUtils.isEmpty(si.photoTag)) {
                tagCV.setVisibility(View.GONE);
            } else {
                tagCV.setVisibility(View.VISIBLE);
                tagTV.setText(Html.fromHtml(si.photoTag));
                if (si.photoTagColor != null && si.photoTagTextColor != null) {
                    try {
                        tagCV.setCardBackgroundColor(Color.parseColor(si.photoTagColor));
                        tagTV.setTextColor(Color.parseColor(si.photoTagTextColor));
                    } catch (final IllegalArgumentException e) {
                        Log.e(TAG, e.getMessage());
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
        return slides.size() != 0 ? slides.size() : 1;
    }

    @Override
    public boolean isViewFromObject(@NonNull final View view, @NonNull final Object object) {
        return view == object;
    }

    @Override
    public void onChange(@NonNull RealmResults<Slide> slides) {
        this.slides.clear();
        this.slides.addAll(realm.copyFromRealm(slides));
        notifyDataSetChanged();
    }

    public void notifyDestruction() {
        if (realm != null) {
            realm.removeAllChangeListeners();
            realm.close();
        }
    }
}

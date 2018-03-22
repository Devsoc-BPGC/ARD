package com.macbitsgoa.ard.adapters;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.macbitsgoa.ard.R;

import java.util.List;

/**
 * Created by vikramaditya on 14/11/17.
 */

public class AnnSlideshowAdapter extends PagerAdapter {
    private List<String> slideshowItems;

    public AnnSlideshowAdapter(@NonNull final List<String> slideshowItems) {
        this.slideshowItems = slideshowItems;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, final int position) {
        final View itemView = LayoutInflater
                .from(container.getContext())
                .inflate(R.layout.vh_announcement_slideshow_title, container, false);

        ((TextView) itemView.findViewById(R.id.tv_vh_announcement_title))
                .setText(Html.fromHtml(slideshowItems.get(position)));
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
        return slideshowItems.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull final View view, @NonNull final Object object) {
        return view == object;
    }
}

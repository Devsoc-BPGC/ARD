package com.macbitsgoa.ard.viewholders;

import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.adapters.AnnSlideshowAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by vikramaditya on 14/11/17.
 */

public class AnnouncementViewHolder extends RecyclerView.ViewHolder {
    /**
     * Textview for subtitle info.
     */
    @BindView(R.id.tv_vh_announcement_subtitle)
    public TextView subtitleTextView;

    /**
     * Viewpager for title textview slideshow.
     */
    @BindView(R.id.vp_vh_announcement)
    ViewPager viewPager;

    private Handler handler;
    private Runnable runnable;

    public AnnouncementViewHolder(final View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setTextData(final ArrayList<String> data) {
        final AnnSlideshowAdapter adapter = new AnnSlideshowAdapter(data);
        if (handler == null) handler = new Handler();
        if (runnable == null) runnable = () -> {
            if (viewPager == null || adapter == null) return;
            int viewpagerpos = viewPager.getCurrentItem();
            viewpagerpos++;
            viewpagerpos %= adapter.getCount();
            viewPager.setCurrentItem(viewpagerpos);
            handler.postDelayed(runnable, 2500);
        };
        handler.removeCallbacks(runnable);
        viewPager.setAdapter(adapter);
        handler.postDelayed(runnable, 2500);
    }
}

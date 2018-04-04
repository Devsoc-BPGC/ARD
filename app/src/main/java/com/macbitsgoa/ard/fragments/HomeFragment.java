package com.macbitsgoa.ard.fragments;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.vivchar.viewpagerindicator.ViewPagerIndicator;
import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.activities.AnnActivity;
import com.macbitsgoa.ard.adapters.AnnSlideshowAdapter;
import com.macbitsgoa.ard.adapters.HomeAdapter;
import com.macbitsgoa.ard.adapters.SlideshowAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass to display home content.
 *
 * @author Vikramaditya Kukreja
 */
public class HomeFragment extends BaseFragment implements AppBarLayout.OnOffsetChangedListener {

    /**
     * TAG for this class.
     */
    public static final String TAG = HomeFragment.class.getSimpleName();

    /**
     * RecyclerView to display Home content.
     */
    @BindView(R.id.recyclerView_fragment_home)
    public RecyclerView homeRV;

    /**
     * Viewpager indicator.
     */
    @BindView(R.id.ci_fragment_home)
    public ViewPagerIndicator pagerIndicator;

    /**
     * ViewPager for image slideshow.
     */
    @BindView(R.id.vp_fragment_home_slideshow)
    public ViewPager slideshowVP;

    @BindView(R.id.ab_fragment_home)
    AppBarLayout appBarLayout;

    @BindView(R.id.vp_vh_announcement)
    ViewPager annVP;

    @BindView(R.id.nsv_fragment_home)
    NestedScrollView nsv;

    Handler imageSlideshowHandler;
    Runnable imageSlideshowRunnable;
    Handler annSlideshowHandler;
    Runnable annSlideshowRunable;

    /**
     * Unbinder for ButterKnife.
     */
    private Unbinder unbinder;

    /**
     * Slideshow adapter.
     */
    private SlideshowAdapter slideshowAdapter;

    private AnnSlideshowAdapter annAdapter;

    /**
     * {@link View#offsetTopAndBottom(int)} of {@link #appBarLayout}.
     */
    private int appBarOffset = 0;


    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, view);
        homeRV.setHasFixedSize(true);
        homeRV.setLayoutManager(new LinearLayoutManager(getContext()));
        homeRV.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        homeRV.setAdapter(new HomeAdapter(getContext()));

        setupSlideshows();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        appBarLayout.addOnOffsetChangedListener(this);
        scrollToTop();
        //hide app bar if orientation is landscape on starting
        hideAppBar();
        appBarLayout.offsetTopAndBottom(appBarOffset);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        imageSlideshowHandler.removeCallbacks(imageSlideshowRunnable);
        annSlideshowHandler.removeCallbacks(annSlideshowRunable);
        annAdapter.notifyOfDesctruction();
        slideshowAdapter.notifyOfDestruction();
        unbinder.unbind();
    }

    @Override
    public void onOffsetChanged(final AppBarLayout appBarLayout, final int verticalOffset) {
        appBarOffset = verticalOffset;
    }

    private void setupSlideshows() {
        //Image slideshow
        slideshowAdapter = new SlideshowAdapter();
        //Ann slideshow
        annAdapter = new AnnSlideshowAdapter();

        imageSlideshowHandler = new Handler();
        annSlideshowHandler = new Handler();

        imageSlideshowRunnable = () -> {
            int newPos = slideshowVP.getCurrentItem() + 1;
            newPos %= slideshowAdapter.getCount();
            slideshowVP.setCurrentItem(newPos, true);
            imageSlideshowHandler.postDelayed(imageSlideshowRunnable, 5000);
        };
        annSlideshowRunable = () -> {
            if (annAdapter.getCount() == 0) return;
            int newPos = annVP.getCurrentItem() + 1;
            newPos %= annAdapter.getCount();
            annVP.setCurrentItem(newPos, true);
            annSlideshowHandler.postDelayed(annSlideshowRunable, 2500);
        };

        slideshowVP.setAdapter(slideshowAdapter);
        pagerIndicator.setupWithViewPager(slideshowVP);
        annVP.setAdapter(annAdapter);

        imageSlideshowHandler.postDelayed(imageSlideshowRunnable, 5000);
        annSlideshowHandler.postDelayed(annSlideshowRunable, 2500);
    }

    public void scrollToTop() {
        //App crashes on removing this check
        //TODO fix required
        if (nsv != null) nsv.scrollTo(0, 0);
    }

    @OnClick(R.id.itemView_ann_card)
    public void openAnnActivity() {
        startActivity(new Intent(getContext(), AnnActivity.class));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        hideAppBar();
    }

    //function to hide appbar
    private void hideAppBar() {
        final CoordinatorLayout.LayoutParams params =
                (CoordinatorLayout.LayoutParams) nsv.getLayoutParams();

        // Checks the orientation of the screen
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            params.setBehavior(null);
            appBarLayout.setVisibility(View.GONE);
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            params.setBehavior(new AppBarLayout.ScrollingViewBehavior());
            appBarLayout.setVisibility(View.VISIBLE);
        }
        nsv.requestLayout();
    }
}

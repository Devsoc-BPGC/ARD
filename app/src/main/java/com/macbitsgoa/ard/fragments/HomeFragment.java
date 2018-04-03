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
import com.macbitsgoa.ard.keys.AnnItemKeys;
import com.macbitsgoa.ard.models.AnnItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.realm.RealmResults;
import io.realm.Sort;

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

    Handler handler;
    Runnable update;
    Handler annSlideshowHandler;
    Runnable annSlideshowRunable;

    private RealmResults<AnnItem> annItems;

    /**
     * Unbinder for ButterKnife.
     */
    private Unbinder unbinder;

    /**
     * Slideshow adapter.
     */
    private SlideshowAdapter slideshowAdapter;

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

        setupSlideshow();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        appBarLayout.addOnOffsetChangedListener(this);
        scrollToTop();
        //hide app bar if orientation is landscape on starting
        hideAppBar();
        setupAnnouncementSlideshow();
        appBarLayout.offsetTopAndBottom(appBarOffset);
    }

    private void pingBgServices() {
            }

    @Override
    public void onResume() {
        super.onResume();
        pingBgServices();
    }

    @Override
    public void onStop() {
        handler.removeCallbacks(update);
        if (annSlideshowHandler != null && annSlideshowRunable != null) {
            annSlideshowHandler.removeCallbacks(annSlideshowRunable);
        }
        annItems.removeAllChangeListeners();
        pingBgServices();
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onOffsetChanged(final AppBarLayout appBarLayout, final int verticalOffset) {
        appBarOffset = verticalOffset;
    }

    private void setupSlideshow() {
        slideshowAdapter = new SlideshowAdapter();

        handler = new Handler();
        update = () -> {
            if (slideshowVP == null || slideshowAdapter == null) return;
            int newPos = slideshowVP.getCurrentItem() + 1;
            newPos %= slideshowAdapter.getCount();
            slideshowVP.setCurrentItem(newPos, true);
        };

        handler.postDelayed(update, 5000);
        slideshowVP.setAdapter(slideshowAdapter);
        pagerIndicator.setupWithViewPager(slideshowVP);
        pagerIndicator.addOnPageChangeListener(getVopl());
        slideshowVP.addOnPageChangeListener(getVopl());
    }

    @NonNull
    private ViewPager.OnPageChangeListener getVopl() {
        return new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset,
                                       final int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(final int position) {

            }

            @Override
            public void onPageScrollStateChanged(final int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    handler.removeCallbacks(update);
                    handler.postDelayed(update, 5000);
                }
            }
        };
    }

    private void setupAnnouncementSlideshow() {
        annItems = database.where(AnnItem.class).findAllSorted(AnnItemKeys.DATE, Sort.DESCENDING);
        final List<String> annItemsText = new ArrayList<>();
        for (AnnItem ai : annItems) annItemsText.add(ai.getData());
        setTextData(annItemsText);
        annItems.addChangeListener((collection, changeSet) -> {
            annItemsText.clear();
            for (AnnItem ai : annItems) annItemsText.add(ai.getData());
            setTextData(annItemsText);
        });
    }



    public void scrollToTop() {
        //App crashes on removing this check
        //TODO fix required
        if (nsv != null) nsv.scrollTo(0, 0);
    }

    public void setTextData(final List<String> data) {
        if (annVP == null) return;
        final AnnSlideshowAdapter adapter = new AnnSlideshowAdapter(data);
        if (annSlideshowHandler == null) annSlideshowHandler = new Handler();
        if (annSlideshowRunable == null) annSlideshowRunable = () -> {
            if (adapter.getCount() == 0) return;
            int viewpagerpos = annVP.getCurrentItem();
            viewpagerpos++;
            viewpagerpos %= adapter.getCount();
            annVP.setCurrentItem(viewpagerpos);
            annSlideshowHandler.postDelayed(annSlideshowRunable, 2500);
        };
        annVP.setAdapter(adapter);
        annSlideshowHandler.removeCallbacks(annSlideshowRunable);
        annSlideshowHandler.postDelayed(annSlideshowRunable, 2500);
    }

    @OnClick(R.id.ann_card_fragment_home)
    public void openAnnActivity() {
        startActivity(new Intent(getContext(), AnnActivity.class));
    }

    //called everytime orientation changes
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        hideAppBar();
    }

    @Override
    public void onDestroy() {
        if (slideshowAdapter != null) slideshowAdapter.notifyDestruction();
        super.onDestroy();
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

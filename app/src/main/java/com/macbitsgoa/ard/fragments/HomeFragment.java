package com.macbitsgoa.ard.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.vivchar.viewpagerindicator.ViewPagerIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.activities.PostDetailsActivity;
import com.macbitsgoa.ard.adapters.HomeAdapter;
import com.macbitsgoa.ard.adapters.SlideshowAdapter;
import com.macbitsgoa.ard.interfaces.OnItemClickListener;
import com.macbitsgoa.ard.interfaces.RecyclerItemClickListener;
import com.macbitsgoa.ard.keys.HomeItemKeys;
import com.macbitsgoa.ard.keys.SlideshowItemKeys;
import com.macbitsgoa.ard.models.SlideshowItem;
import com.macbitsgoa.ard.models.home.HomeItem;
import com.macbitsgoa.ard.services.HomeService;
import com.macbitsgoa.ard.utils.AHC;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.realm.OrderedCollectionChangeSet;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * A simple {@link Fragment} subclass to display home content.
 *
 * @author Vikramaditya Kukreja
 */
public class HomeFragment extends BaseFragment implements OnItemClickListener,
        AppBarLayout.OnOffsetChangedListener {

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

    /**
     * HomeAdapter object.
     */
    public HomeAdapter homeAdapter;

    @BindView(R.id.ab_fragment_home)
    AppBarLayout appBarLayout;

    Handler handler;
    Runnable update;

    private RealmResults<HomeItem> homeItems;

    /**
     * Unbinder for ButterKnife.
     */
    private Unbinder unbinder;

    /**
     * Slideshow adapter.
     */
    private SlideshowAdapter slideshowAdapter;

    /**
     * Reference to slide show image data.
     */
    //TODO change to fhr home..and change fdr home to home
    private DatabaseReference slideShowRef = getRootReference().child(AHC.FDR_EXTRAS).child("home").child("slideshow");
    private ValueEventListener slideShowVEL;
    private ValueEventListener homeRefVEL;

    private DatabaseReference homeRef = getRootReference().child(AHC.FDR_HOME);

    /**
     * Item touch listener of RecyclerView.
     */
    private RecyclerView.OnItemTouchListener onItemTouchListener;
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
        init();
        return view;
    }

    /**
     * View updates and listeners can be done here.
     */
    private void init() {
        homeRV.setHasFixedSize(true);
        homeRV.setLayoutManager(new LinearLayoutManager(getContext()));
        onItemTouchListener = new RecyclerItemClickListener(getContext(), homeRV, this);
        homeRV.addOnItemTouchListener(onItemTouchListener);
        homeRV.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        appBarLayout.addOnOffsetChangedListener(this);
        database = Realm.getDefaultInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        //adter super call, database is ready
        homeItems = database.where(HomeItem.class).findAllSorted(HomeItemKeys.DATE, Sort.DESCENDING);
        homeItems.addChangeListener((collection, changeSet) -> {
            // `null`  means the async query returns the first time.
            if (changeSet == null) {
                homeAdapter.notifyDataSetChanged();
                return;
            }
            // For deletions, the adapter has to be notified in reverse order.
            OrderedCollectionChangeSet.Range[] deletions = changeSet.getDeletionRanges();
            for (int i = deletions.length - 1; i >= 0; i--) {
                OrderedCollectionChangeSet.Range range = deletions[i];
                homeAdapter.notifyItemRangeRemoved(range.startIndex, range.length);
            }

            OrderedCollectionChangeSet.Range[] insertions = changeSet.getInsertionRanges();
            for (OrderedCollectionChangeSet.Range range : insertions) {
                homeAdapter.notifyItemRangeInserted(range.startIndex, range.length);
            }

            OrderedCollectionChangeSet.Range[] modifications = changeSet.getChangeRanges();
            for (OrderedCollectionChangeSet.Range range : modifications) {
                homeAdapter.notifyItemRangeChanged(range.startIndex, range.length);
            }
            if (homeAdapter.getItemCount() == 0) {
                //emptyTextView.setVisibility(View.VISIBLE);
            } else {
                //emptyTextView.setVisibility(View.GONE);
            }
        });
        homeAdapter = new HomeAdapter(homeItems, getContext());
        homeRV.setAdapter(homeAdapter);
        homeRefVEL = getHomeRefVEL();
        homeRef.addValueEventListener(homeRefVEL);

        appBarLayout.offsetTopAndBottom(appBarOffset);

        setupSlideshow();
    }

    @Override
    public void onStop() {
        handler.removeCallbacks(update);
        slideshowAdapter.close();
        //Remove firebase database listeners
        slideShowRef.removeEventListener(slideShowVEL);
        homeRef.removeEventListener(homeRefVEL);

        //Remove database change listener
        homeItems.removeAllChangeListeners();

        //null the adapter
        homeAdapter = null;
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        homeRV.removeOnItemTouchListener(onItemTouchListener);
        unbinder.unbind();
        database.close();
    }

    @Override
    public void onItemClick(final View view, final int position) {
        final Intent intent = new Intent(getContext(), PostDetailsActivity.class);
        final HomeItem hi = homeItems.get(position);
        intent.putExtra(HomeItemKeys.KEY, hi.getKey());
        startActivity(intent);
    }

    @Override
    public void onLongItemClick(final View view, final int position) {
        //Not required as of now
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

        ViewPager.OnPageChangeListener vopl = new ViewPager.OnPageChangeListener() {
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
                    handler.postDelayed(update, 7500);
                }
            }
        };
        slideshowVP.addOnPageChangeListener(vopl);
        handler.postDelayed(update, 7500);
        slideshowVP.setAdapter(slideshowAdapter);
        slideShowVEL = getSlideShowVEL();
        pagerIndicator.setupWithViewPager(slideshowVP);
        pagerIndicator.addOnPageChangeListener(vopl);
        slideShowRef.addValueEventListener(slideShowVEL);
    }

    private ValueEventListener getSlideShowVEL() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                //If no data is present avoid deleting any existing slideshow image data
                Log.e(TAG, dataSnapshot.toString());
                if (dataSnapshot.getChildrenCount() == 0) return;

                //Delete old values
                database.executeTransaction(r -> database.delete(SlideshowItem.class));
                for (final DataSnapshot cs :
                        dataSnapshot.getChildren()) {
                    if (!cs.hasChild(SlideshowItemKeys.PHOTO_URL)
                            || !cs.hasChild(SlideshowItemKeys.PHOTO_DATE)) continue;
                    database.executeTransaction(r -> {
                        final SlideshowItem ssi = r.createObject(SlideshowItem.class);
                        ssi.setPhotoUrl(cs.child(SlideshowItemKeys.PHOTO_URL).getValue(String.class));
                        ssi.setPhotoDate(cs.child(SlideshowItemKeys.PHOTO_DATE).getValue(Date.class));
                        ssi.setPhotoTitle(cs.child(SlideshowItemKeys.PHOTO_TITLE).getValue(String.class));
                        ssi.setPhotoDesc(cs.child(SlideshowItemKeys.PHOTO_DESC).getValue(String.class));
                        ssi.setPhotoTag(cs.child(SlideshowItemKeys.PHOTO_TAG).getValue(String.class));
                        ssi.setPhotoTagColor(cs.child(SlideshowItemKeys.PHOTO_TAG_COLOR).getValue(String.class));
                        ssi.setPhotoTagTextColor(cs.child(SlideshowItemKeys.PHOTO_TAG_TEXT_COLOR).getValue(String.class));
                    });
                }
            }

            @Override
            public void onCancelled(final DatabaseError databaseError) {
                Log.e(TAG, "Error while getting slidehshow images\n" + databaseError.getDetails());
            }
        };
    }

    private ValueEventListener getHomeRefVEL() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                HomeService.saveHomeSnapshotToRealm(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Database access error." + databaseError.toString());
            }
        };
    }

    public void scrollToTop() {
        if (homeRV != null) homeRV.smoothScrollToPosition(0);
    }
}

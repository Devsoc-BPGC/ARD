package com.macbitsgoa.ard.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.google.android.flexbox.FlexboxItemDecoration;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.lapism.searchview.SearchView;
import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.activities.AnnActivity;
import com.macbitsgoa.ard.activities.PostDetailsActivity;
import com.macbitsgoa.ard.adapters.HomeAdapter;
import com.macbitsgoa.ard.adapters.SlideshowAdapter;
import com.macbitsgoa.ard.interfaces.HomeFragmentListener;
import com.macbitsgoa.ard.interfaces.OnItemClickListener;
import com.macbitsgoa.ard.interfaces.RecyclerItemClickListener;
import com.macbitsgoa.ard.keys.AnnItemKeys;
import com.macbitsgoa.ard.keys.HomeItemKeys;
import com.macbitsgoa.ard.keys.PostKeys;
import com.macbitsgoa.ard.keys.SlideshowItemKeys;
import com.macbitsgoa.ard.models.AnnItem;
import com.macbitsgoa.ard.models.SlideshowItem;
import com.macbitsgoa.ard.models.TypeItem;
import com.macbitsgoa.ard.models.home.HomeItem;
import com.macbitsgoa.ard.types.PostType;
import com.macbitsgoa.ard.utils.AHC;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment can implement the
 * {@link HomeFragmentListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * @author Vikramaditya Kukreja
 */
public class HomeFragment extends BaseFragment implements OnItemClickListener {

    /**
     * TAG for this class.
     */
    public static final String TAG = HomeFragment.class.getSimpleName();

    //----------------------------------------------------------------------------------------------
    /**
     * RecyclerView to display Home content.
     */
    @BindView(R.id.recyclerView_fragment_home)
    public RecyclerView recyclerView;

    /**
     * Viewpager indicator.
     */
    @BindView(R.id.ci_fragment_home)
    public ViewPagerIndicator viewPagerIndicator;
    /**
     * ViewPager for image slideshow.
     */
    @BindView(R.id.vp_fragment_home_slideshow)
    public ViewPager viewPagerSlideShow;

    /**
     * Toolbar search view object.
     */
    @BindView(R.id.search_view_fragment_home)
    SearchView searchView;

    //----------------------------------------------------------------------------------------------

    /**
     * HomeAdapter object.
     */
    public HomeAdapter homeAdapter;

    private List<TypeItem> dataSet;

    /**
     * Unbinder for ButterKnife.
     */
    private Unbinder unbinder;

    private SlideshowAdapter slideshowAdapter;

    /**
     * Reference to node {@link AHC#FDR_HOME} to which listener is attached.
     */
    private DatabaseReference dbRef = getRootReference().child(AHC.FDR_HOME);
    /**
     * Reference to slide show image data.
     */
    //TODO change to fhr home..and change fdr home to home
    private DatabaseReference slideShowRef = getRootReference().child(AHC.FDR_EXTRAS).child("home").child("slideshow");

    private ValueEventListener slideShowEventListener;

    /**
     * EventListener for {@link AHC#FDR_HOME} which is required to remove in {@link #onStop()}.
     */
    private ValueEventListener homeEventListener;

    /**
     * Item touch listener of RecyclerView.
     */
    private RecyclerView.OnItemTouchListener onItemTouchListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param bundle Bundled data for this fragment.
     * @return A new instance of fragment HomeFragment.
     */
    public static HomeFragment newInstance(@Nullable final Bundle bundle) {
        final HomeFragment fragment = new HomeFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

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
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        onItemTouchListener = new RecyclerItemClickListener(getContext(), recyclerView, this);
        recyclerView.addOnItemTouchListener(onItemTouchListener);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        database = Realm.getDefaultInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        dataSet = generateList();
        homeAdapter = new HomeAdapter(dataSet, getContext());
        recyclerView.setAdapter(homeAdapter);

        searchView.hideKeyboard();
        searchView.clearFocus();

        setupSlideshow();

        homeEventListener = getValueEventListener();
        dbRef.addValueEventListener(homeEventListener);
    }

    Handler handler;
    Runnable update;

    private void setupSlideshow() {
        slideshowAdapter = new SlideshowAdapter();

        handler = new Handler();
        update = () -> {
            if (viewPagerSlideShow == null || slideshowAdapter == null) return;
            int newPos = viewPagerSlideShow.getCurrentItem() + 1;
            newPos %= slideshowAdapter.getCount();
            viewPagerSlideShow.setCurrentItem(newPos, true);
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
        viewPagerSlideShow.addOnPageChangeListener(vopl);
        handler.postDelayed(update, 7500);
        viewPagerSlideShow.setAdapter(slideshowAdapter);
        slideShowEventListener = getSlideShowEventListener();
        viewPagerIndicator.setupWithViewPager(viewPagerSlideShow);
        viewPagerIndicator.addOnPageChangeListener(vopl);
        slideShowRef.addValueEventListener(slideShowEventListener);
    }

    /**
     * When fragment is created the list is generated from the realm database.
     * This cannot be called before {@link #onStart()} as database is not ready.
     *
     * @return Generated list from Realm.
     */
    private List<TypeItem> generateList() {
        //Clear existing results
        final List<TypeItem> list = new ArrayList<>();

        //Add announcement tab
        final List<String> anns = new ArrayList<>();
        final RealmResults<AnnItem> annItems = database.where(AnnItem.class)
                .findAllSorted(AnnItemKeys.DATE, Sort.DESCENDING);
        for (final AnnItem annItem : annItems) {
            anns.add(annItem.getData());
        }
        if (anns.size() == 0) anns.add("No announcements");
        list.add(new TypeItem(anns, HomeAdapter.ANNOUNCEMENT_TAB));


        //Add home items
        final RealmResults<HomeItem> homeItems = database.where(HomeItem.class)
                .findAllSorted(HomeItemKeys.DATE, Sort.DESCENDING);
        for (final HomeItem hi : homeItems) {
            list.add(new TypeItem(hi, HomeAdapter.HOME_ITEM));
        }

        return list;
    }

    private ValueEventListener getSlideShowEventListener() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                //If no data is present avoid deleting any existing slideshow image data
                Log.e(TAG, dataSnapshot.toString());
                if (dataSnapshot.getChildrenCount() == 0) return;

                //Delete old values
                database.executeTransaction(r -> {
                    database.delete(SlideshowItem.class);
                });
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

    /**
     * Returns the valueEventListener for the node {@link AHC#FDR_HOME}.
     *
     * @return valueEventListener object that can be later detached in {@link #onStop()}.
     */
    @NonNull
    private ValueEventListener getValueEventListener() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                homeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(final DatabaseError databaseError) {
                Log.e(TAG, "Error in " + AHC.FDR_HOME + " node\n"
                        + databaseError.getDetails());
            }
        };
    }

    @Override
    public void onStop() {
        super.onStop();
        handler.removeCallbacks(update);
        slideshowAdapter.close();
        slideShowRef.removeEventListener(slideShowEventListener);
        dbRef.removeEventListener(homeEventListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        recyclerView.removeOnItemTouchListener(onItemTouchListener);
        homeAdapter = null;
        unbinder.unbind();
        database.close();
    }

    @Override
    public void onItemClick(final View view, final int position) {
        if (position == 0) {
            startActivity(new Intent(getContext(), AnnActivity.class));
        } else {
            final Intent intent = new Intent(getContext(), PostDetailsActivity.class);
            final HomeItem hi = (HomeItem) dataSet.get(position).getData();
            intent.putExtra(HomeItemKeys.KEY, hi.getKey());
            intent.putExtra(PostKeys.TYPE, PostType.HOME_ITEM);
            startActivity(intent);
        }
    }

    @Override
    public void onLongItemClick(final View view, final int position) {
        //Not required as of now
    }
}

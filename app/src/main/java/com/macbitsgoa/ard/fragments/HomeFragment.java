package com.macbitsgoa.ard.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.lapism.searchview.SearchView;
import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.activities.PostDetailsActivity;
import com.macbitsgoa.ard.adapters.HomeAdapter;
import com.macbitsgoa.ard.adapters.SlideshowAdapter;
import com.macbitsgoa.ard.interfaces.HomeFragmentListener;
import com.macbitsgoa.ard.interfaces.OnItemClickListener;
import com.macbitsgoa.ard.interfaces.RecyclerItemClickListener;
import com.macbitsgoa.ard.keys.AnnItemKeys;
import com.macbitsgoa.ard.keys.PostKeys;
import com.macbitsgoa.ard.models.AnnItem;
import com.macbitsgoa.ard.models.SlideshowItem;
import com.macbitsgoa.ard.models.TypeItem;
import com.macbitsgoa.ard.types.PostType;
import com.macbitsgoa.ard.utils.AHC;

import java.util.ArrayList;
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
     * RecyclerView to display Home content.
     */
    @BindView(R.id.recyclerView_fragment_home)
    public RecyclerView recyclerView;

    /**
     * HomeAdapter object.
     */
    public HomeAdapter homeAdapter;

    /**
     * Unbinder for ButterKnife.
     */
    private Unbinder unbinder;

    /**
     * ViewPager for image slideshow
     */
    @BindView(R.id.vp_fragment_home_slideshow)
    public ViewPager viewPagerSlideShow;

    private SlideshowAdapter slideshowAdapter;

    /**
     * Reference to node {@link AHC#FDR_HOME} to which listener is attached.
     */
    private DatabaseReference dbRef = getRootReference().child(AHC.FDR_HOME);

    /**
     * Reference to slide show image data.
     */
    private DatabaseReference slideShowRef = getRootReference().child(AHC.FDR_EXTRAS).child(AHC.FDR_HOME).child("slideshow");
    private ValueEventListener slideShowEventListener;

    @BindView(R.id.search_view_fragment_home)
    SearchView searchView;

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
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
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
        homeAdapter = new HomeAdapter();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(homeAdapter);

        onItemTouchListener = new RecyclerItemClickListener(getContext(), recyclerView, this);
        recyclerView.addOnItemTouchListener(onItemTouchListener);
    }

    @Override
    public void onStart() {
        super.onStart();
        database = Realm.getDefaultInstance();

        searchView.clearFocus();

        setupData(generateList());

        RealmResults<SlideshowItem> slideshowItems = database.where(SlideshowItem.class)
                .findAllSorted("photoDate", Sort.DESCENDING);
        slideshowAdapter = new SlideshowAdapter(slideshowItems);
        slideshowItems.addChangeListener(newSlideshowItems -> slideshowAdapter.notifyDataSetChanged());
        viewPagerSlideShow.setAdapter(slideshowAdapter);

        homeEventListener = getValueEventListener();
        slideShowEventListener = getSlideShowEventListener();
        slideShowRef.addValueEventListener(slideShowEventListener);
        dbRef.addValueEventListener(homeEventListener);
    }

    /**
     * Method to set realm database.
     * If argument is null, default realm database will be used.
     * This method is convenient while testing.
     *
     * @param list List of {@link TypeItem} to be used in adapter.
     */
    //@VisibleForTesting
    public void setupData(@NonNull final List<TypeItem> list) {
        homeAdapter.getData().clear();
        homeAdapter.getData().addAll(list);
        homeAdapter.notifyDataSetChanged();
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

        //Generate Announcement type list
        for (final AnnItem annItem
                : database.where(AnnItem.class).findAllSorted("date", Sort.DESCENDING)) {
            list.add(new TypeItem(annItem, PostType.ANNOUNCEMENT));
        }
        return list;
    }

    private ValueEventListener getSlideShowEventListener() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() <= 0) return;

                database.executeTransaction(r -> {
                    database.delete(SlideshowItem.class);
                });
                for (DataSnapshot childShot :
                        dataSnapshot.getChildren()) {
                    database.executeTransaction(r -> {
                        r.insert(childShot.getValue(SlideshowItem.class));
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
                for (final DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    final AnnItem annItem = childSnapshot.getValue(AnnItem.class);
                    annItem.setKey(childSnapshot.getKey());
                    database.executeTransactionAsync(r -> {
                        r.copyToRealmOrUpdate(annItem);
                    });
                }
                setupData(generateList());
            }

            @Override
            public void onCancelled(final DatabaseError databaseError) {
                throw databaseError.toException();
            }
        };
    }

    @Override
    public void onStop() {
        super.onStop();
        database.close();
        slideShowRef.removeEventListener(slideShowEventListener);
        dbRef.removeEventListener(homeEventListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        recyclerView.removeOnItemTouchListener(onItemTouchListener);
        homeAdapter = null;
        unbinder.unbind();
    }

    @Override
    public void onItemClick(final View view, final int position) {
        final Intent intent = new Intent(getContext(), PostDetailsActivity.class);
        final TypeItem item = homeAdapter.getData().get(position);
        intent.putExtra(PostKeys.TYPE, item.getType());
        //switch (item.getType()) {
        //    case PostType.ANNOUNCEMENT: {
        intent.putExtra(AnnItemKeys.PRIMARY_KEY, ((AnnItem) item.getData()).getKey());
        //        break;
        //    }
        //}
        startActivity(intent);
    }

    @Override
    public void onLongItemClick(final View view, final int position) {
        //Not required as of now
    }
}

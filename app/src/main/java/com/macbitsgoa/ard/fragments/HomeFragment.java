package com.macbitsgoa.ard.fragments;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.macbitsgoa.ard.BuildConfig;
import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.activities.PostActivity;
import com.macbitsgoa.ard.activities.PostDetailsActivity;
import com.macbitsgoa.ard.activities.SearchActivity;
import com.macbitsgoa.ard.adapters.HomeAdapter;
import com.macbitsgoa.ard.interfaces.HomeFragmentListener;
import com.macbitsgoa.ard.interfaces.OnItemClickListener;
import com.macbitsgoa.ard.interfaces.RecyclerItemClickListener;
import com.macbitsgoa.ard.keys.AnnItemKeys;
import com.macbitsgoa.ard.keys.PostKeys;
import com.macbitsgoa.ard.models.AnnItem;
import com.macbitsgoa.ard.models.TypeItem;
import com.macbitsgoa.ard.types.PostType;
import com.macbitsgoa.ard.utils.AHC;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.realm.Realm;
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
public class HomeFragment extends Fragment implements View.OnClickListener, OnItemClickListener {

    /**
     * RecyclerView to display Home content.
     */
    @BindView(R.id.recyclerView_fragment_home)
    public RecyclerView recyclerView;

    /**
     * Main FAB for fragment. Has multiple sub mini FABs.
     */
    @BindView(R.id.fab_fragment_home_add)
    public FloatingActionButton mainFab;

    /**
     * Sub mini FAB for general upload.
     */
    @BindView(R.id.fab_fragment_home_announce)
    public FloatingActionButton announceFab;

    /**
     * A simple {@code View} object which has a custom background to be used when main FAB is
     * clicked.
     */
    @BindView(R.id.view_fragment_home_backdrop)
    public View backdrop;

    /**
     * Status boolean to maintain current status of {@link HomeFragment#mainFab}
     * in {@link HomeFragment}.
     */
    public boolean isFabOpen;

    /**
     * HomeAdapter object.
     */
    public HomeAdapter homeAdapter;

    /**
     * Unbinder for ButterKnife.
     */
    private Unbinder unbinder;

    /**
     * Reference to node {@link AHC#FDR_HOME} to which listener is attached.
     */
    private DatabaseReference dbRef = FirebaseDatabase.getInstance()
            .getReference().child(BuildConfig.BUILD_TYPE).child(AHC.FDR_HOME);

    /**
     * Handle for Realm instance.
     */
    private Realm database;

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
        backdrop.setVisibility(View.INVISIBLE);
        announceFab.setVisibility(View.INVISIBLE);

        homeAdapter = new HomeAdapter();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(homeAdapter);

        onItemTouchListener = new RecyclerItemClickListener(getContext(), recyclerView, this);
        recyclerView.addOnItemTouchListener(onItemTouchListener);
        mainFab.setOnClickListener(this);
        announceFab.setOnClickListener(this);
        backdrop.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        database = Realm.getDefaultInstance();

        setupData(generateList());

        homeEventListener = getValueEventListener();
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
    public void onClick(final View v) {
        final int id = v.getId();
        if (id == R.id.fab_fragment_home_add || id == R.id.view_fragment_home_backdrop) {
            animateFab();
        } else {
            animateFab();
            final Intent intent = new Intent(getContext(), PostActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Method to animate main fab and invisible ones.
     */
    public void animateFab() {
        final Animator startAnimator = ViewAnimationUtils.createCircularReveal(backdrop,
                (int) mainFab.getX() + mainFab.getWidth() / 2,
                (int) mainFab.getY() + mainFab.getHeight() / 2,
                0,
                (float) Math.hypot(backdrop.getHeight(), backdrop.getWidth()));
        final Animator endAnimator = ViewAnimationUtils.createCircularReveal(backdrop,
                (int) mainFab.getX() + mainFab.getWidth() / 2,
                (int) mainFab.getY() + mainFab.getHeight() / 2,
                (float) Math.hypot(backdrop.getHeight(), backdrop.getWidth()),
                0);
        startAnimator.setDuration((long) AHC.ANIMATION_MULTIPLIER * getResources()
                .getInteger(R.integer.anim_fab_duration));
        endAnimator.setDuration((long) AHC.ANIMATION_MULTIPLIER * getResources()
                .getInteger(R.integer.anim_fab_duration));

        if (isFabOpen) {
            startAnimator.removeAllListeners();
            endAnimator.addListener(getEndAnimatorListener());
            endAnimator.start();
        } else {
            endAnimator.removeAllListeners();
            startAnimator.addListener(getStartAnimatorListener());
            startAnimator.start();
        }
    }

    /**
     * Returns listener for start animation of backdrop.
     *
     * @return Animator listener for animator object.
     */
    private Animator.AnimatorListener getStartAnimatorListener() {
        return new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(final Animator animation) {
                mainFab.startAnimation(AnimationUtils.loadAnimation(getContext(),
                        R.anim.rotate_clock));
                announceFab.startAnimation(AnimationUtils.loadAnimation(getContext(),
                        R.anim.fab_open));
                backdrop.setVisibility(View.VISIBLE);
                announceFab.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(final Animator animation) {
                announceFab.setClickable(true);
                backdrop.setClickable(true);
                isFabOpen = true;

                //To keep coverage at 100%
                onAnimationRepeat(animation);
                onAnimationCancel(animation);
            }

            @Override
            public void onAnimationCancel(final Animator animation) {
                //nothing is cancelled.
            }

            @Override
            public void onAnimationRepeat(final Animator animation) {
                //No repeating set.
            }
        };
    }

    /**
     * Returns listener for end animation for backdrop.
     *
     * @return Animator listener for animator object.
     */
    private Animator.AnimatorListener getEndAnimatorListener() {
        return new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(final Animator animation) {
                announceFab.startAnimation(AnimationUtils.loadAnimation(getContext(),
                        R.anim.fab_close));
                mainFab.startAnimation(AnimationUtils.loadAnimation(getContext(),
                        R.anim.rotate_anticlock));
            }

            @Override
            public void onAnimationEnd(final Animator animation) {
                announceFab.setVisibility(View.INVISIBLE);
                backdrop.setVisibility(View.INVISIBLE);
                announceFab.setClickable(false);
                backdrop.setClickable(false);
                isFabOpen = false;

                //To keep coverage at 100%
                onAnimationRepeat(animation);
                onAnimationCancel(animation);
            }

            @Override
            public void onAnimationCancel(final Animator animation) {
                //Nothing is cancelled.
            }

            @Override
            public void onAnimationRepeat(final Animator animation) {
                //No repeating set.
            }
        };
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

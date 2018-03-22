package com.macbitsgoa.ard.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.adapters.AnnAdapter;
import com.macbitsgoa.ard.interfaces.OnItemClickListener;
import com.macbitsgoa.ard.interfaces.RecyclerItemClickListener;
import com.macbitsgoa.ard.keys.AnnItemKeys;
import com.macbitsgoa.ard.models.AnnItem;
import com.macbitsgoa.ard.services.HomeService;
import com.macbitsgoa.ard.utils.AHC;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedCollectionChangeSet;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Activity to show all announcements.
 *
 * @author Vikramaditya Kukreja
 */
public class AnnActivity extends BaseActivity implements OnItemClickListener {

    /**
     * TAG for class.
     */
    public static final String TAG = AnnActivity.class.getSimpleName();

    //----------------------------------------------------------------------------------------------
    /**
     * Toolbar for this activity.
     */
    @BindView(R.id.toolbar_activity_ann)
    Toolbar toolbar;

    /**
     * Recyclerview for displaying announcements.
     */
    @BindView(R.id.rv_activity_ann)
    RecyclerView annRV;

    /**
     * TextView to display info message when recyclerview is empty.
     */
    @BindView(R.id.tv_activity_ann_empty)
    TextView emptyListTV;

    //----------------------------------------------------------------------------------------------
    /**
     * Realm list to get announcement data.
     */
    private RealmResults<AnnItem> anns;

    /**
     * Item touch listener of RecyclerView.
     */
    private RecyclerView.OnItemTouchListener onItemTouchListener;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        AHC.startService(this, HomeService.class, HomeService.TAG);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ann);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener((v) -> onBackPressed());

        annRV.setHasFixedSize(true);
        annRV.setLayoutManager(new LinearLayoutManager(this));
        onItemTouchListener = new RecyclerItemClickListener(this, annRV, this);
        annRV.addOnItemTouchListener(onItemTouchListener);
        annRV.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        //Generate data
        anns = database.where(AnnItem.class)
                .findAllSorted(AnnItemKeys.DATE, Sort.DESCENDING);
        //Init adapter
        final AnnAdapter annAdapter = new AnnAdapter(anns);
        //set adapter
        annRV.setAdapter(annAdapter);

        if (anns.size() == 0) emptyListTV.setVisibility(View.VISIBLE);
        else emptyListTV.setVisibility(View.INVISIBLE);

        //Setup on change listener
        anns.addChangeListener((collection, changeSet) -> {
            //TODO cancel ongoing notifications
            // `null`  means the async query returns the first time.
            if (changeSet == null) {
                annAdapter.notifyDataSetChanged();
                return;
            }
            // For deletions, the adapter has to be notified in reverse order.
            OrderedCollectionChangeSet.Range[] deletions = changeSet.getDeletionRanges();
            for (int i = deletions.length - 1; i >= 0; i--) {
                OrderedCollectionChangeSet.Range range = deletions[i];
                annAdapter.notifyItemRangeRemoved(range.startIndex, range.length);
            }

            OrderedCollectionChangeSet.Range[] insertions = changeSet.getInsertionRanges();
            for (OrderedCollectionChangeSet.Range range : insertions) {
                annAdapter.notifyItemRangeInserted(range.startIndex, range.length);
            }

            OrderedCollectionChangeSet.Range[] modifications = changeSet.getChangeRanges();
            for (OrderedCollectionChangeSet.Range range : modifications) {
                annAdapter.notifyItemRangeChanged(range.startIndex, range.length);
            }
            if (anns.size() == 0) emptyListTV.setVisibility(View.VISIBLE);
            else emptyListTV.setVisibility(View.INVISIBLE);
        });
    }

    /**
     * As {@link #database} is closed automatically in {@link BaseActivity#onDestroy()},
     * {@code super.onDestroy()} is called at the end so any references to {@link #database} are
     * closed.
     */
    @Override
    protected void onDestroy() {
        anns.removeAllChangeListeners();
        annRV.removeOnItemTouchListener(onItemTouchListener);
        super.onDestroy();
    }

    @Override
    public void onItemClick(final View view, final int position) {
        final AnnItem ai = anns.get(position);
        final Intent intent = new Intent(this, PostDetailsActivity.class);
        intent.putExtra("annItem", ai.getKey());
        startActivity(intent);
    }

    @Override
    public void onLongItemClick(final View view, final int position) {
        //not used
    }
}

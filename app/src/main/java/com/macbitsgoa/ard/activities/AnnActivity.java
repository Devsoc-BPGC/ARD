package com.macbitsgoa.ard.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.adapters.HomeAdapter;
import com.macbitsgoa.ard.interfaces.OnItemClickListener;
import com.macbitsgoa.ard.interfaces.RecyclerItemClickListener;
import com.macbitsgoa.ard.keys.AnnItemKeys;
import com.macbitsgoa.ard.models.AnnItem;
import com.macbitsgoa.ard.models.TypeItem;
import com.macbitsgoa.ard.services.AnnNotifyService;
import com.macbitsgoa.ard.types.PostType;
import com.macbitsgoa.ard.utils.AHC;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
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

        //TODO cancel ongoing notifications

        //Generate data
        final List<TypeItem> annItemsList = new ArrayList<>();
        anns = database.where(AnnItem.class)
                .findAllSorted(AnnItemKeys.DATE, Sort.DESCENDING);
        for (final AnnItem ai : anns) {
            annItemsList.add(new TypeItem(ai, PostType.ANNOUNCEMENT));
        }

        if (annItemsList.size() == 0) emptyListTV.setVisibility(View.VISIBLE);
        else emptyListTV.setVisibility(View.INVISIBLE);

        //Init adapter
        final HomeAdapter homeAdapter = new HomeAdapter(annItemsList, this);

        //Setup on change listener
        anns.addChangeListener(annItems -> {
            annItemsList.clear();
            //TODO cancel ongoing notifications
            for (final AnnItem ais : annItems) {
                annItemsList.add(new TypeItem(ais, PostType.ANNOUNCEMENT));
            }
            if (annItemsList.size() == 0) emptyListTV.setVisibility(View.VISIBLE);
            else emptyListTV.setVisibility(View.INVISIBLE);
            homeAdapter.notifyDataSetChanged();
        });

        //set adapter
        annRV.setAdapter(homeAdapter);
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
        if (ai.getKey() == null) {
            AHC.logd(TAG, "Key was null, was item deleted?");
            return;
        }
        final Intent intent = new Intent(this, PostDetailsActivity.class);
        intent.putExtra("annItem", ai.getKey());
        startActivity(intent);
    }

    @Override
    public void onLongItemClick(final View view, final int position) {
        //not used
    }
}

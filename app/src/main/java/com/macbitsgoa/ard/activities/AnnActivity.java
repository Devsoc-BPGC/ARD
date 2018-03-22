package com.macbitsgoa.ard.activities;

import android.app.NotificationManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.adapters.AnnAdapter;
import com.macbitsgoa.ard.interfaces.OnItemClickListener;
import com.macbitsgoa.ard.interfaces.RecyclerItemClickListener;
import com.macbitsgoa.ard.keys.AnnItemKeys;
import com.macbitsgoa.ard.models.AnnItem;
import com.macbitsgoa.ard.services.AnnNotifyService;
import com.macbitsgoa.ard.services.HomeService;
import com.macbitsgoa.ard.utils.AHC;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
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

    /**
     * Realm list to get announcement data.
     */
    private RealmResults<AnnItem> anns;

    AnnAdapter annAdapter;

    private DatabaseReference annRef = getRootReference().child(AHC.FDR_ANN);

    private ValueEventListener annRefVEL;

    private RecyclerView.OnItemTouchListener onItemTouchListener;

    /**
     * Static boolean variable to prevent notification service to show when activity is visible.
     */
    public static boolean inForeground = false;

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

        //Generate data
        anns = database.where(AnnItem.class)
                .findAllSortedAsync(AnnItemKeys.DATE, Sort.DESCENDING);
        annAdapter = new AnnAdapter(anns);
        annRV.setAdapter(annAdapter);

        checkForEmptyList();
        showLongClickTip();

        //Cancel any existing notification
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(AnnNotifyService.NOTIFICATION_ID);

        //Setup on change listener
        anns.addChangeListener(getRealmChangeListener());

        annRefVEL = getAnnRefVEL();
        annRef.addValueEventListener(annRefVEL);
    }

    private void checkForEmptyList() {
        if (anns.size() == 0) emptyListTV.setVisibility(View.VISIBLE);
        else emptyListTV.setVisibility(View.GONE);
    }

    private void showLongClickTip() {
        final String ANN_LONG_CLICK_TIP = "ann_long_click_tip";
        SharedPreferences sp = getDefaultSharedPref();
        if (!sp.contains(ANN_LONG_CLICK_TIP)) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean(ANN_LONG_CLICK_TIP, true);
            showSnack("Long click to copy announcement", Snackbar.LENGTH_LONG);
            editor.apply();
        }
    }

    private OrderedRealmCollectionChangeListener<RealmResults<AnnItem>> getRealmChangeListener() {
        return (collection, changeSet) -> {
            // `null`  means the async query returns the first time.
            if (changeSet == null) {
                annAdapter.notifyDataSetChanged();
                checkForEmptyList();
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
            checkForEmptyList();
        };
    }

    private ValueEventListener getAnnRefVEL() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                HomeService.saveAnnSnapshotToRealm(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Database error");
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        inForeground = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        inForeground = false;
    }

    /**
     * As {@link #database} is closed automatically in {@link BaseActivity#onDestroy()},
     * {@code super.onDestroy()} is called at the end so any references to {@link #database} are
     * closed.
     */
    @Override
    protected void onDestroy() {
        annRef.removeEventListener(annRefVEL);
        anns.removeAllChangeListeners();
        annRV.removeOnItemTouchListener(onItemTouchListener);
        super.onDestroy();
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public void onLongItemClick(View view, int position) {
        //TODO Remove html tags from data
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("ARD Announcement", anns.get(position).getData());
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(this, "Text copied to clipboard", Toast.LENGTH_SHORT).show();
    }
}

package com.macbitsgoa.ard.activities;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.adapters.AnnAdapter;
import com.macbitsgoa.ard.interfaces.AdapterNotificationListener;
import com.macbitsgoa.ard.services.AnnNotifyService;
import com.macbitsgoa.ard.services.HomeService;
import com.macbitsgoa.ard.utils.AHC;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Activity to show all announcements.
 *
 * @author Vikramaditya Kukreja
 */
public class AnnActivity extends BaseActivity implements AdapterNotificationListener {

    /**
     * TAG for class.
     */
    public static final String TAG = AnnActivity.class.getSimpleName();
    /**
     * Static boolean variable to prevent notification service to show when activity is visible.
     */
    public static boolean inForeground = false;
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
    private DatabaseReference annRef = getRootReference().child(AHC.FDR_ANN);
    private ValueEventListener annRefVEL;

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
        annRV.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        //Generate data
        annRV.setAdapter(new AnnAdapter(this));

        //Cancel any existing notification
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(AnnNotifyService.NOTIFICATION_ID);

        annRefVEL = getAnnRefVEL();
        annRef.addValueEventListener(annRefVEL);
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
        super.onDestroy();
    }

    @Override
    public void onAdapterNotified(final int size) {
        if (size == 0) emptyListTV.setVisibility(View.VISIBLE);
        else emptyListTV.setVisibility(View.GONE);
    }
}

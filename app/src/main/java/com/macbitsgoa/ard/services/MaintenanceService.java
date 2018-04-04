package com.macbitsgoa.ard.services;

import android.support.annotation.NonNull;

import com.firebase.jobdispatcher.JobParameters;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.macbitsgoa.ard.keys.AnnItemKeys;
import com.macbitsgoa.ard.keys.FaqItemKeys;
import com.macbitsgoa.ard.keys.HomeItemKeys;
import com.macbitsgoa.ard.models.AnnItem;
import com.macbitsgoa.ard.models.FaqItem;
import com.macbitsgoa.ard.models.home.HomeItem;
import com.macbitsgoa.ard.utils.AHC;

import io.realm.Realm;

/**
 * Delete service for old items.
 *
 * @author Rushikesh Jogdand
 */
public class MaintenanceService extends BaseJobService {

    /**
     * Tag for this class.
     */
    public static final String TAG = MaintenanceService.class.getSimpleName();
    private JobParameters jobParameters;

    @Override
    public boolean onStartJob(JobParameters job) {
        jobParameters = job;
        AHC.logd(TAG, "Starting maintenance service");
        getRootReference()
                .child(AHC.FDR_DELETES)
                .addListenerForSingleValueEvent(getDeletesListener());
        return true;
    }

    @NonNull
    private ValueEventListener getDeletesListener() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null) {
                    AHC.logd(TAG, "No deletes history");
                    jobFinished(jobParameters, false);
                }
                new Thread(() -> {
                    final Realm database = Realm.getDefaultInstance();
                    for (DataSnapshot childDS : dataSnapshot.getChildren()) {
                        final String id = childDS.child("key").getValue(String.class);
                        AHC.logd(TAG, "Delete key " + id + " if present");
                        database.executeTransaction(r -> {
                            final HomeItem hi = r.where(HomeItem.class).equalTo(HomeItemKeys.KEY, id)
                                    .findFirst();
                            if (hi != null) {
                                AHC.logd(TAG, "Found home item with same id to delete.");
                                hi.deleteFromRealm();
                            }
                            final AnnItem ai = r.where(AnnItem.class).equalTo(AnnItemKeys.KEY, id).findFirst();
                            if (ai != null) {
                                AHC.logd(TAG, "Found announcement item with same id to delete.");
                                ai.deleteFromRealm();
                            }
                            final FaqItem fi = r.where(FaqItem.class).equalTo(FaqItemKeys.KEY, id).findFirst();
                            if (fi != null) {
                                AHC.logd(TAG, "Found faq item with same id to delete.");
                                fi.deleteFromRealm();
                            }
                        });
                    }
                    database.close();
                    jobFinished(jobParameters, false);
                }).start();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                AHC.logd(TAG, "Database read access error");
                jobFinished(jobParameters, false);
            }
        };
    }
}

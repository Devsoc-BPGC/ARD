package com.macbitsgoa.ard.services;

import android.content.Intent;
import android.support.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.macbitsgoa.ard.keys.AnnItemKeys;
import com.macbitsgoa.ard.keys.FaqItemKeys;
import com.macbitsgoa.ard.keys.HomeItemKeys;
import com.macbitsgoa.ard.models.AnnItem;
import com.macbitsgoa.ard.models.FaqItem;
import com.macbitsgoa.ard.models.home.HomeItem;
import com.macbitsgoa.ard.utils.AHC;

import io.realm.Realm;

public class DeleteService extends BaseIntentService {

    /**
     * Tag for this class.
     */
    public static final String TAG = DeleteService.class.getSimpleName();

    /**
     * Key field for deleted items.
     */
    public static final String KEY = "key";

    public DeleteService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        super.onHandleIntent(intent);
        final DatabaseReference deletesRef = getRootReference()
                .child(AHC.FDR_DELETES);
        deletesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null) {
                    AHC.logd(TAG, "No deletes history");
                }
                final Realm database = Realm.getDefaultInstance();
                for (DataSnapshot childDS : dataSnapshot.getChildren()) {
                    final String id = childDS.child(KEY).getValue(String.class);
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                AHC.logd(TAG, "Database read access error");
            }
        });
    }
}

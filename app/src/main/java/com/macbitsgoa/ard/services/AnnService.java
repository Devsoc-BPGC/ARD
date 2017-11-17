package com.macbitsgoa.ard.services;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.macbitsgoa.ard.keys.AnnItemKeys;
import com.macbitsgoa.ard.models.AnnItem;
import com.macbitsgoa.ard.utils.AHC;

import java.util.Date;

import io.realm.Realm;

/**
 * Background service to get announcement info.
 *
 * @author Vikramaditya Kukreja
 */
public class AnnService extends BaseIntentService {

    /**
     * TAG for class.
     */
    public static final String TAG = AnnService.class.getSimpleName();

    /**
     * Request code for alarm manager.
     */
    public static final int REQUEST_CODE = 42;

    //----------------------------------------------------------------------------------------------

    /**
     * Childeventlistener for ann items node on Firebase.
     */
    private ChildEventListener annRefCEL;

    public AnnService() {
        super(AnnService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable final Intent intent) {
        final DatabaseReference annRef = getRootReference().child(AHC.FDR_ANN);
        annRefCEL = getListener();
        annRef.addChildEventListener(annRefCEL);
        try {
            Thread.sleep(1000 * 60 * 5);
        } catch (final InterruptedException e) {
            Log.e(TAG, "Interrupt error\n" + e.toString());
        } finally {
            annRef.removeEventListener(annRefCEL);
            AHC.setNextAlarm(this, AnnService.class, REQUEST_CODE, 1);
        }
    }

    /**
     * Method to initialise {@link #annRefCEL}.
     *
     * @return {@link #annRefCEL}.
     */
    public ChildEventListener getListener() {
        return new ChildEventListener() {
            private Realm database;

            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, final String s) {
                if (dataSnapshot.getValue() == null) return;
                database = Realm.getDefaultInstance();

                final String key = dataSnapshot.getKey();
                final String data = dataSnapshot.child(AnnItemKeys.DATA).getValue(String.class);
                final Date date = dataSnapshot.child(AnnItemKeys.DATE).getValue(Date.class);
                final String author = dataSnapshot.child(AnnItemKeys.AUTHOR).getValue(String.class);

                if (data == null || date == null || data.length() == 0) return;

                database.executeTransaction(r -> {
                    AnnItem annItem = r.where(AnnItem.class)
                            .equalTo(AnnItemKeys.KEY, key)
                            .findFirst();
                    if (annItem == null) {
                        annItem = r.createObject(AnnItem.class, key);
                    } else {
                        if (annItem.getDate().getTime() == date.getTime())
                            return;
                    }
                    //Only way an update occurred is if date objecct changed, in which case ann is
                    //now unread
                    annItem.setRead(false);
                    annItem.setAuthor(author == null || author.length() == 0 ? "Admin" : author);
                    annItem.setDate(date);
                    annItem.setData(data);
                    startService(new Intent(AnnService.this, NotificationService.class));
                });

                database.close();
            }

            @Override
            public void onChildChanged(final DataSnapshot dataSnapshot, final String s) {
                onChildAdded(dataSnapshot, s);
            }

            @Override
            public void onChildRemoved(final DataSnapshot dataSnapshot) {
                onChildRemoved(dataSnapshot.getKey());
            }

            void onChildRemoved(final String key) {
                database = Realm.getDefaultInstance();
                database.executeTransaction(r -> {
                    final AnnItem annItem = r
                            .where(AnnItem.class)
                            .equalTo(AnnItemKeys.KEY, key)
                            .findFirst();
                    if (annItem != null) {
                        annItem.deleteFromRealm();
                    } else {
                        Log.e(TAG, "Trying to delete a non existent ann item");
                    }
                });
                database.close();
            }

            @Override
            public void onChildMoved(final DataSnapshot dataSnapshot, final String s) {
                onChildRemoved(s);
                onChildAdded(dataSnapshot, s);
            }

            @Override
            public void onCancelled(final DatabaseError databaseError) {
                Log.e(TAG, "Error in getting announcements\n" + databaseError.getDetails());
                if (database != null && !database.isClosed()) {
                    if (database.isInTransaction()) {
                        database.cancelTransaction();
                    }
                    database.close();
                }
            }
        };
    }
}

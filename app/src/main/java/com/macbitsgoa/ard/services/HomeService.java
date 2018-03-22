package com.macbitsgoa.ard.services;

import android.support.annotation.Nullable;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.macbitsgoa.ard.keys.AnnItemKeys;
import com.macbitsgoa.ard.keys.HomeItemKeys;
import com.macbitsgoa.ard.models.AnnItem;
import com.macbitsgoa.ard.models.home.HomeItem;
import com.macbitsgoa.ard.models.home.PhotoItem;
import com.macbitsgoa.ard.models.home.TextItem;
import com.macbitsgoa.ard.types.HomeType;
import com.macbitsgoa.ard.utils.AHC;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * Service to download home node content from Firebase.
 *
 * @author Vikramaditya Kukreja
 */
public class HomeService extends BaseJobService {

    /**
     * TAG for class.
     */
    public static final String TAG = HomeService.class.getSimpleName();

    boolean completedHome = false;
    boolean completedAnn = false;
    JobParameters job;

    @Override
    public boolean onStartJob(JobParameters job) {
        this.job = job;
        AHC.logd(TAG, "Starting new thread for job");
        new Thread(() -> {
            final DatabaseReference homeRef = getRootReference().child(AHC.FDR_HOME);
            final DatabaseReference annRef = getRootReference().child(AHC.FDR_ANN);
            final ValueEventListener homeCEL = getHomeListener();
            final ValueEventListener annRefCEL = getAnnListener();

            homeRef.addListenerForSingleValueEvent(homeCEL);
            annRef.addListenerForSingleValueEvent(annRefCEL);
        }).start();
        return true;
    }

    /**
     * Listener for Home node in Firebase.
     *
     * @return listener for children.
     */
    private ValueEventListener getHomeListener() {
        //noinspection OverlyLongMethod
        return new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                saveHomeSnapshotToRealm(dataSnapshot);
                completedHome = true;
            }

            @Override
            public void onCancelled(final DatabaseError databaseError) {
                Log.e(TAG, "Error in getting announcements\n" + databaseError.getDetails());
                completedHome = true;
            }
        };
    }

    public static void saveHomeSnapshotToRealm(@Nullable final DataSnapshot dataSnapshot) {
        if (dataSnapshot == null) return;
        final Realm database = Realm.getDefaultInstance();
        for (DataSnapshot child : dataSnapshot.getChildren()) {
            final String key = child.getKey();
            final String author = child.child(HomeItemKeys.AUTHOR).getValue(String.class);
            final Date date = child.child(HomeItemKeys.DATE).getValue(Date.class);
            if (date == null || !child.hasChild(HomeItemKeys.SUB_SECTIONS)) continue;
            database.executeTransaction(r -> {
                HomeItem hi = r
                        .where(HomeItem.class)
                        .equalTo(HomeItemKeys.KEY, key)
                        .findFirst();
                if (hi == null) {
                    hi = r.createObject(HomeItem.class, key);
                    AHC.logd(TAG, "Creating new home item");
                } else if (hi.getDate().getTime() == date.getTime()){
                    return;
                }
                hi.setAuthor(author);
                hi.setDate(date);
                final RealmList<PhotoItem> images = new RealmList<>();
                final RealmList<TextItem> texts = new RealmList<>();
                for (final DataSnapshot subSectionDS
                        : child.child(HomeItemKeys.SUB_SECTIONS).getChildren()) {
                    final int type = subSectionDS.child(HomeItemKeys.TYPE).getValue(Integer.class);
                    final String data = subSectionDS.child(HomeItemKeys.DATA).getValue(String.class);
                    switch (type) {
                        case HomeType.FDR_PHOTO_ITEM:
                            final PhotoItem pi = r.createObject(PhotoItem.class);
                            pi.setPhotoUrl(data);
                            pi.setPriority(subSectionDS.getKey());
                            images.add(pi);
                            break;
                        case HomeType.FDR_TEXT_ITEM:
                            final TextItem ti = r.createObject(TextItem.class);
                            ti.setData(data);
                            ti.setPriority(subSectionDS.getKey());
                            texts.add(ti);
                            break;
                    }
                }
                hi.setImages(images);
                hi.setTexts(texts);
            });
        }
        database.close();
    }


    /**
     * Method to initialise announcement reference listener object.
     *
     * @return ChildEventListener object.
     */
    public ValueEventListener getAnnListener() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                saveAnnSnapshotToRealm(dataSnapshot);
                completedAnn = true;
                checkJobStatus();
            }

            @Override
            public void onCancelled(final DatabaseError databaseError) {
                Log.e(TAG, "Error in getting announcements\n" + databaseError.getDetails());
                completedAnn = true;
            }
        };
    }

    public static void saveAnnSnapshotToRealm(@Nullable final DataSnapshot dataSnapshot) {
        if (dataSnapshot == null) return;
        final Realm database = Realm.getDefaultInstance();
        for (final DataSnapshot child : dataSnapshot.getChildren()) {
            final String key = child.getKey();
            final String data = child.child(AnnItemKeys.DATA).getValue(String.class);
            final Date date = child.child(AnnItemKeys.DATE).getValue(Date.class);
            final String author = child.child(AnnItemKeys.AUTHOR).getValue(String.class);
            if (data == null || date == null || data.length() == 0) {
                continue;
            }
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
                annItem.setAuthor(author);
                annItem.setDate(date);
                annItem.setData(data);
            });
        }
        database.close();
    }

    private void checkJobStatus() {
        AHC.logd(TAG, "Status of jobs is " + completedAnn + " and " + completedHome);
        if (completedAnn && completedHome) jobFinished(job, false);
    }
}

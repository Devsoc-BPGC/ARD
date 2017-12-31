package com.macbitsgoa.ard.services;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
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
public class HomeService extends BaseIntentService {

    /**
     * TAG for class.
     */
    public static final String TAG = HomeService.class.getSimpleName();

    /**
     * Key for intent extra, which denotes how many of the latest news item are to be fetched.
     */
    public static final String LIMIT_TO_LAST = "limitToLast";

    /**
     * Request code for Alarm manager.
     */
    public static final int REQUEST_CODE = 69;

    public HomeService() {
        super(HomeService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable final Intent intent) {
        super.onHandleIntent(intent);
        int limitToLast = -1;
        if (intent != null && intent.hasExtra(LIMIT_TO_LAST)) {
            limitToLast = intent.getIntExtra(LIMIT_TO_LAST, -1);
        }

        final DatabaseReference homeRef = getRootReference().child(AHC.FDR_HOME);
        final DatabaseReference annRef = getRootReference().child(AHC.FDR_ANN);
        final ChildEventListener homeCEL = getHomeListener();
        final ChildEventListener annRefCEL = getAnnListener();

        if (limitToLast != -1) {
            homeRef.limitToLast(limitToLast).addChildEventListener(homeCEL);
        } else {
            homeRef.addChildEventListener(homeCEL);
        }
        annRef.addChildEventListener(annRefCEL);

        try {
            Thread.sleep(1000 * 60 * 5);
        } catch (final InterruptedException e) {
            Log.e(TAG, "Thread sleep interrupted with error\n" + e.toString());
        } finally {
            homeRef.removeEventListener(homeCEL);
            annRef.removeEventListener(annRefCEL);
            AHC.setNextAlarm(this, new Intent(this, HomeService.class), REQUEST_CODE, 0);
        }
    }

    /**
     * Listener for Home node in Firebase.
     *
     * @return listener for children.
     */
    private ChildEventListener getHomeListener() {
        //noinspection OverlyLongMethod
        return new ChildEventListener() {
            private Realm database;

            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, final String s) {
                database = Realm.getDefaultInstance();

                final String key = dataSnapshot.getKey();
                final String author = dataSnapshot.child(HomeItemKeys.AUTHOR).getValue(String.class);
                final Date date = dataSnapshot.child(HomeItemKeys.DATE).getValue(Date.class);

                if (date == null || !dataSnapshot.hasChild(HomeItemKeys.SUB_SECTIONS)) return;

                database.executeTransaction(r -> {
                    HomeItem hi = r
                            .where(HomeItem.class)
                            .equalTo(HomeItemKeys.KEY, key)
                            .findFirst();
                    if (hi == null) {
                        hi = r.createObject(HomeItem.class, key);
                    } else {
                        if (hi.getImages() != null) {
                            hi.getImages().deleteAllFromRealm();
                        }
                        if (hi.getTexts() != null) {
                            hi.getTexts().deleteAllFromRealm();
                        }
                    }
                    hi.setAuthor(author == null
                            || author.length() == 0 ? AHC.DEFAULT_AUTHOR : author);
                    hi.setDate(date);
                    final RealmList<PhotoItem> images = new RealmList<>();
                    final RealmList<TextItem> texts = new RealmList<>();
                    for (final DataSnapshot subSectionDS
                            : dataSnapshot.child(HomeItemKeys.SUB_SECTIONS).getChildren()) {
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
                    final HomeItem hi = r.where(HomeItem.class)
                            .equalTo(HomeItemKeys.KEY, key).findFirst();
                    if (hi == null) {
                        Log.e(TAG, "Home item was not present and thus could not delete");
                    } else {
                        hi.deleteFromRealm();
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


    /**
     * Method to initialise announcement reference listener object.
     *
     * @return ChildEventListener object.
     */
    public ChildEventListener getAnnListener() {
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

                if (data == null || date == null || data.length() == 0) {
                    return;
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
                    //Only way an update occurred is if date objecct changed, in which case ann is
                    //now unread
                    annItem.setRead(false);
                    annItem.setAuthor(author == null || author.length() == 0 ? "Admin" : author);
                    annItem.setDate(date);
                    annItem.setData(data);
                    startService(new Intent(HomeService.this, NotificationService.class));
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

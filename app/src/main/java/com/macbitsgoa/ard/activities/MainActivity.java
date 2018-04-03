package com.macbitsgoa.ard.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.macbitsgoa.ard.BuildConfig;
import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.fragments.BaseFragment;
import com.macbitsgoa.ard.fragments.DetailsFragment;
import com.macbitsgoa.ard.fragments.ForumFragment;
import com.macbitsgoa.ard.fragments.HomeFragment;
import com.macbitsgoa.ard.keys.AnnItemKeys;
import com.macbitsgoa.ard.keys.FaqItemKeys;
import com.macbitsgoa.ard.keys.HomeItemKeys;
import com.macbitsgoa.ard.models.AnnItem;
import com.macbitsgoa.ard.models.FaqItem;
import com.macbitsgoa.ard.models.home.HomeItem;
import com.macbitsgoa.ard.services.ForumService;
import com.macbitsgoa.ard.services.HomeService;
import com.macbitsgoa.ard.types.MainActivityType;
import com.macbitsgoa.ard.utils.AHC;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

/**
 * Main activity of app.
 *
 * @author Vikramaditya Kukreja
 */
public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        BottomNavigationView.OnNavigationItemSelectedListener //,
        /*ChatFragmentListener*/ {

    /**
     * Tag for this class.
     */
    public static final String TAG = MainActivity.class.getSimpleName();

    private static int currentSection = MainActivityType.HOME;
    /**
     * Bottom navigation view.
     */
    @BindView(R.id.bottom_nav_activity_main)
    BottomNavigationView bottomNavigationView;

    /**
     * Key {@link MainActivityType}
     * Value : Access order (lower is more recent).
     */
    HashMap<Integer, Integer> sectionsHistory = new HashMap<>();

    /**
     * Fragment manager used to handle the 3 fragments.
     */
    private FragmentManager fragmentManager;

    /**
     * ForumFragment object.
     */
    private ForumFragment forumFragment;

    /**
     * HomeFragment object.
     */
    private HomeFragment homeFragment;

    /**
     * DetailsFragment object.
     */
    private DetailsFragment detailFragment;

    private DatabaseReference deletesRef;
    private ValueEventListener deleteRefVEL;

    /**
     * ChatFragment object.
     */
    //private ChatFragment chatFragment;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Check if authorised
        if (getUser() == null) {
            AHC.logd(TAG, "Current user null");
            startActivity(new Intent(this, AuthActivity.class));
            finish();
        } else {
            AHC.sendRegistrationToServer(FirebaseInstanceId.getInstance().getToken());
            FirebaseMessaging.getInstance().subscribeToTopic(AHC.FDR_USERS);
            FirebaseMessaging.getInstance().subscribeToTopic(BuildConfig.BUILD_TYPE);
            FirebaseMessaging.getInstance().subscribeToTopic("android");
            FirebaseMessaging.getInstance().subscribeToTopic(getUser().getUid());

            setContentView(R.layout.activity_main);
            init();

            startService(new Intent(this, ForumService.class));
            AHC.startService(this, HomeService.class, HomeService.TAG);
            //AHC.startService(this, MessagingService.class, MessagingService.TAG);

            bottomNavigationView.setOnNavigationItemSelectedListener(this);
        }
    }

    /**
     * All initialisation are done here.
     */
    private void init() {
        ButterKnife.bind(this);

        fragmentManager = getSupportFragmentManager();

        forumFragment = ForumFragment.newInstance(getString(R.string.bottom_nav_forum_activity_main));
        homeFragment = new HomeFragment();
        //chatFragment = ChatFragment.newInstance(getString(R.string.bottom_nav_chat_activity_main));
        detailFragment = DetailsFragment.newInstance();
        launchFragment(currentSection);
        final int menuId;
        if (currentSection == MainActivityType.FORUM) menuId = R.id.bottom_nav_forum;
        else if (currentSection == MainActivityType.HOME) menuId = R.id.bottom_nav_home;
        else /*if (currentSection == MainActivityType.DETAILS)*/ menuId = R.id.bottom_nav_details;
        //else menuId = R.id.bottom_nav_chat;
        bottomNavigationView.setSelectedItemId(menuId);
        bottomNavigationView.getMenu().findItem(menuId).setChecked(true);
        deletesRef = getRootReference()
                .child(AHC.FDR_DELETES);

        deleteRefVEL = getDeletesListener();
        deletesRef.addValueEventListener(deleteRefVEL);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (deletesRef != null && deleteRefVEL != null)
            deletesRef.removeEventListener(deleteRefVEL);
    }

    @NonNull
    private ValueEventListener getDeletesListener() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null) {
                    AHC.logd(TAG, "No deletes history");
                }
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                AHC.logd(TAG, "Database read access error");
            }
        };
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
        final int id = item.getItemId();

        if (id == R.id.bottom_nav_forum) {
            launchFragment(MainActivityType.FORUM);
        } else if (id == R.id.bottom_nav_home) {
            launchFragment(MainActivityType.HOME);
            homeFragment.scrollToTop();
        } else /*if (id == R.id.bottom_nav_details)*/ {
            launchFragment(MainActivityType.DETAILS);
        /*} else {
            launchFragment(MainActivityType.CHAT);*/
        }
        return true;
    }

    private void launchFragment(int section) {
        for (Map.Entry<Integer, Integer> entry : sectionsHistory.entrySet()) {
            entry.setValue(entry.getValue() + 1);
        }
        sectionsHistory.put(currentSection, 0);

        currentSection = section;
        sectionsHistory.remove(currentSection);

        BaseFragment baseFragment;
        if (currentSection == MainActivityType.FORUM) baseFragment = forumFragment;
        else if (currentSection == MainActivityType.HOME) baseFragment = homeFragment;
        else /*if (currentSection == MainActivityType.DETAILS)*/ baseFragment = detailFragment;
        //else baseFragment = chatFragment;
        fragmentManager.beginTransaction().replace(R.id.frame_content_main, baseFragment).commit();
    }

    @Override
    public void onBackPressed() {
        if (sectionsHistory.isEmpty()) {
            // Home should be last section before exit.
            if (currentSection == MainActivityType.HOME) {
                finish();
                return;
            } else {
                sectionsHistory.put(MainActivityType.HOME, 0);
            }
        }

        Map.Entry<Integer, Integer> e = sectionsHistory.entrySet().iterator().next();
        int minVal = e.getValue();
        int lastSection = e.getKey();

        for (Map.Entry<Integer, Integer> entry : sectionsHistory.entrySet()) {
            if (entry.getValue() < minVal) {
                minVal = entry.getValue();
                lastSection = entry.getKey();
            }
        }

        int staleSection = currentSection;

        launchFragment(lastSection);
        final int menuId;
        if (currentSection == MainActivityType.FORUM) menuId = R.id.bottom_nav_forum;
        else if (currentSection == MainActivityType.HOME) menuId = R.id.bottom_nav_home;
        else /*if (currentSection == MainActivityType.DETAILS)*/ menuId = R.id.bottom_nav_details;
        //else menuId = R.id.bottom_nav_chat;
        bottomNavigationView.setSelectedItemId(menuId);
        bottomNavigationView.getMenu().findItem(menuId).setChecked(true);

        sectionsHistory.remove(staleSection);
    }

    /*@Override
    public void updateChatFragment() {

    }*/
}

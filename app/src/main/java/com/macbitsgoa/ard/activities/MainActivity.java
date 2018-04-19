package com.macbitsgoa.ard.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.view.MenuItem;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.macbitsgoa.ard.BuildConfig;
import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.fragments.BaseFragment;
import com.macbitsgoa.ard.fragments.DetailsFragment;
import com.macbitsgoa.ard.fragments.ForumFragment;
import com.macbitsgoa.ard.fragments.HomeFragment;
import com.macbitsgoa.ard.keys.MainActivityKeys;
import com.macbitsgoa.ard.services.ForumService;
import com.macbitsgoa.ard.services.HomeService;
import com.macbitsgoa.ard.services.MaintenanceService;
import com.macbitsgoa.ard.types.MainActivityType;
import com.macbitsgoa.ard.utils.AHC;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Main activity of app.
 *
 * @author Vikramaditya Kukreja
 */
public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        BottomNavigationView.OnNavigationItemSelectedListener {

    /**
     * Tag for this class.
     */
    public static final String TAG = MainActivity.class.getSimpleName();

    /**
     * Bottom navigation view.
     */
    @BindView(R.id.bottom_nav_activity_main)
    BottomNavigationView bottomNavigationView;

    /**
     * int variable storing the currently selected fragment.
     *
     * @see MainActivityType
     */
    private int currentSection;

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

    /**
     * Job dispatcher service.
     */
    private FirebaseJobDispatcher bgServicesDispatcher;

    /**
     * ChatFragment object.
     */
    //private ChatFragment chatFragment;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SharedPreferences sp = getDefaultSharedPref();
        if (!sp.getBoolean(MainActivityKeys.GPS_AVAILABLE, false)) {
            final GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
            final int serviceStatus = googleApiAvailability
                    .isGooglePlayServicesAvailable(this);
            if (serviceStatus == ConnectionResult.SUCCESS) {
                sp.edit().putBoolean(MainActivityKeys.GPS_AVAILABLE, true).apply();
                AHC.logd(TAG, "GPS available");
            } else {
                googleApiAvailability
                        .makeGooglePlayServicesAvailable(this)
                        .addOnSuccessListener(aVoid -> sp
                                .edit()
                                .putBoolean(MainActivityKeys.GPS_AVAILABLE, true)
                                .apply());
            }
        }

        //Check if authorised
        if (getUser() == null) {
            AHC.logd(TAG, "Current user null");
            final Intent intent = new Intent(this, AuthActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
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
            startBgServices();

            showEmailWarning();

            bottomNavigationView.setOnNavigationItemSelectedListener(this);
        }
    }

    /**
     * All initialisation are done here.
     */
    private void init() {
        ButterKnife.bind(this);

        forumFragment = ForumFragment.newInstance(getString(R.string.bottom_nav_forum_activity_main));
        homeFragment = new HomeFragment();
        detailFragment = DetailsFragment.newInstance();

        currentSection = MainActivityType.HOME;
        launchFragment();

        bottomNavigationView.setSelectedItemId(R.id.bottom_nav_home);
    }

    private void showEmailWarning() {
        if (!FirebaseAuth.getInstance()
                .getCurrentUser()
                .getEmail()
                .matches(getStringRes(R.string.regex_allowed_emails))) {
            new AlertDialog.Builder(this)
                    .setTitle("Switch to BITS email")
                    .setMessage("To help us improve this app, please switch to your BITS email account.")
                    .setCancelable(false)
                    .setPositiveButton("Ok",
                            (d, w) -> AHC.signOutApp(MainActivity.this)).show();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
        final int id = item.getItemId();

        if (id == R.id.bottom_nav_forum) {
            currentSection = MainActivityType.FORUM;
        } else if (id == R.id.bottom_nav_home) {
            currentSection = MainActivityType.HOME;
        } else {
            currentSection = MainActivityType.DETAILS;
        }
        launchFragment();
        return true;
    }

    /**
     * Method to launch a fragment when click on bottom nav.
     */
    private void launchFragment() {
        final BaseFragment baseFragment;
        if (currentSection == MainActivityType.FORUM) {
            baseFragment = forumFragment;
        } else if (currentSection == MainActivityType.HOME) {
            baseFragment = homeFragment;
        } else {
            baseFragment = detailFragment;
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_content_main, baseFragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (currentSection != MainActivityType.HOME) {
            bottomNavigationView.setSelectedItemId(R.id.bottom_nav_home);
            currentSection = MainActivityType.HOME;
            launchFragment();
        } else {
            finish();
        }
    }

    /**
     * Job dispatcher method.
     */
    private void startBgServices() {
        bgServicesDispatcher
                = AHC.getJobDispatcher(this);
        final Job maintenanceJob = bgServicesDispatcher.newJobBuilder()
                .setService(MaintenanceService.class)
                .setTag(MaintenanceService.TAG)
                .setReplaceCurrent(true)
                .setTrigger(Trigger.NOW)
                .setLifetime(Lifetime.FOREVER)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .build();
        final Job homeJob = bgServicesDispatcher.newJobBuilder()
                .setService(HomeService.class)
                .setTag(HomeService.TAG)
                .setReplaceCurrent(true)
                .setTrigger(Trigger.NOW)
                .setLifetime(Lifetime.FOREVER)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .build();

        bgServicesDispatcher.schedule(maintenanceJob);
        bgServicesDispatcher.schedule(homeJob);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bgServicesDispatcher != null) {
            //If app is not running we don't need this updated information.
            bgServicesDispatcher.cancel(MaintenanceService.TAG);
        }
    }
}

package com.macbitsgoa.ard.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.fragments.ChatFragment;
import com.macbitsgoa.ard.fragments.FaqFragment;
import com.macbitsgoa.ard.fragments.HomeFragment;
import com.macbitsgoa.ard.interfaces.ChatFragmentListener;
import com.macbitsgoa.ard.interfaces.FaqFragmentListener;
import com.macbitsgoa.ard.interfaces.NavigationDrawerListener;
import com.macbitsgoa.ard.keys.AuthActivityKeys;
import com.macbitsgoa.ard.services.MessagingService;
import com.macbitsgoa.ard.utils.AHC;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Main activity of app.
 *
 * @author Vikramaditya Kukreja
 */
public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        BottomNavigationView.OnNavigationItemSelectedListener,
        FaqFragmentListener,
        ChatFragmentListener {

    /**
     * The title of nav Drawer.
     */
    public static String navDrawerTitleText;

    /**
     * The subtitle of nav Drawer.
     */
    public static String navDrawerSubtitleText;

    /**
     * The array of urls of images used as nav drawer background.
     */
    public static ArrayList<String> navDrawerImageList;

    /**
     * URL of nav drawer background for current instance of app.
     * Chosen randomly from navDrawerImageList when app launches
     * and when corresponding list changes in firebase.
     */
    public static String navDrawerImageURL;

    /**
     * Duration of cross fade animation between in nav drawer background images (in milliseconds).
     */
    public static final int NAV_DRAWER_BACKGROUND_ANIM_DUR = 50;

    /**
     * DrawerLayout for nav drawer.
     */
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    /**
     * Navigation view in drawer.
     */
    @BindView(R.id.nav_view)
    NavigationView navigationView;

    /**
     * Bottom navigation view.
     */
    @BindView(R.id.bottom_nav_activity_main)
    BottomNavigationView bottomNavigationView;

    /**
     * Fragment manager used to handle the 3 fragments.
     */
    private FragmentManager fragmentManager;

    /**
     * FaqFragment object.
     */
    private FaqFragment faqFragment;

    /**
     * HomeFragment object.
     */
    private HomeFragment homeFragment;

    /**
     * ChatFragment object.
     */
    private ChatFragment chatFragment;

    /**
     * Firebase db ref for navigation drawer.
     */
    private DatabaseReference navDrawerDBRef;

    /**
     * navDrawerListener listens for db changes for nav drawer.
     */
    private NavigationDrawerListener navDrawerListener;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        //Check if authorised
        if (!auth(getIntent())) {
            startActivity(new Intent(this, AuthActivity.class));
            finish();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        startService(new Intent(this, MessagingService.class));
        initListeners();
    }

    /**
     * Start {@link AuthActivity} if Firebase user object is null.
     * This also closes the current {@link MainActivity} before launching Auth.
     *
     * @param intent Intent object. Should not be null. See <b>MainActivityTest</b>.
     * @return boolean true if auth is successful, false otherwise.
     */
    public boolean auth(@NonNull final Intent intent) {
        return !intent.getBooleanExtra(AuthActivityKeys.USE_DEFAULT, true)
                || FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    /**
     * All initialisation are done here.
     */
    private void init() {
        ButterKnife.bind(this);

        navDrawerDBRef = getRootReference().child(AHC.FDR_NAV_DRAWER);

        final View headerView = navigationView.getHeaderView(0);
        final ImageView navDrawerImage = ButterKnife.findById(headerView, R.id.nav_drawer_image);
        final TextView navDrawerTitle = ButterKnife.findById(headerView, R.id.nav_drawer_title);
        final TextView navDrawerSubtitle = ButterKnife.findById(headerView, R.id.nav_drawer_subtitle);

        if (navDrawerTitleText != null) {
            navDrawerTitle.setText(navDrawerTitleText);
        }

        if (navDrawerSubtitleText != null) {
            navDrawerSubtitle.setText(navDrawerSubtitleText);
        }

        if (navDrawerImageURL != null) {
            final RequestOptions navDrawerImageOptions = new RequestOptions()
                    .placeholder(getDrawable(R.drawable.nav_drawer_default_image));

            Glide.with(this)
                    .load(navDrawerImageURL)
                    .transition(DrawableTransitionOptions.withCrossFade()
                            .crossFade(NAV_DRAWER_BACKGROUND_ANIM_DUR)
                    )
                    .apply(navDrawerImageOptions)
                    .into(navDrawerImage);
        }

        navDrawerListener = new NavigationDrawerListener(
                this,
                navDrawerTitle,
                navDrawerSubtitle,
                navDrawerImage);

        fragmentManager = getSupportFragmentManager();

        faqFragment = FaqFragment.newInstance(null);
        homeFragment = HomeFragment.newInstance(null);
        chatFragment = ChatFragment.newInstance(getString(R.string.bottom_nav_chat_activity_main));

        bottomNavigationView.setSelectedItemId(R.id.bottom_nav_home);
        fragmentManager.beginTransaction().replace(R.id.frame_content_main, homeFragment,
                getString(R.string.bottom_nav_home_activity_main)).commit();
    }

    /**
     * Initialise listeners.
     */
    private void initListeners() {
        navigationView.setNavigationItemSelectedListener(this);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        navDrawerDBRef.addValueEventListener(navDrawerListener);
    }

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
        // Handle navigation view item clicks here.
        final int id = item.getItemId();

        if (id == R.id.bottom_nav_faq) {
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_content_main, faqFragment)
                    .commit();
            return true;
        } else if (id == R.id.bottom_nav_home) {
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_content_main, homeFragment)
                    .commit();
            return true;
        } else {
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_content_main, chatFragment)
                    .commit();
            return true;
        }

        //Not required until we have an item in Nav drawer.
        //drawer.closeDrawer(GravityCompat.START);
        //return true;
    }

    @Override
    public void updateChatFragment() {

    }

    @Override
    public void updateFaqFragment() {

    }

    @Override
    protected void onDestroy() {
        navDrawerDBRef.removeEventListener(navDrawerListener);
        super.onDestroy();
    }
}

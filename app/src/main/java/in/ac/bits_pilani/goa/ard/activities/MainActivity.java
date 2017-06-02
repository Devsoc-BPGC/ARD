package in.ac.bits_pilani.goa.ard.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.ac.bits_pilani.goa.ard.R;
import in.ac.bits_pilani.goa.ard.fragments.ChatFragment;
import in.ac.bits_pilani.goa.ard.fragments.FaqFragment;
import in.ac.bits_pilani.goa.ard.fragments.HomeFragment;
import in.ac.bits_pilani.goa.ard.interfaces.ChatFragmentListener;
import in.ac.bits_pilani.goa.ard.interfaces.FaqFragmentListener;
import in.ac.bits_pilani.goa.ard.interfaces.HomeFragmentListener;
import in.ac.bits_pilani.goa.ard.utils.AHC;

/**
 * Main activity of app.
 *
 * @author Vikramaditya Kukreja
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        BottomNavigationView.OnNavigationItemSelectedListener,
        FaqFragmentListener,
        HomeFragmentListener,
        ChatFragmentListener {

    /**
     * Toolbar for MainActivity.
     */
    @BindView(R.id.toolbar_activity_main)
    Toolbar toolbar;

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
     * Tag for this activity.
     */
    private final String TAG = AHC.TAG + ".activities." + getClass().getSimpleName();

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

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        initListeners();
    }

    /**
     * All initialisation are done here.
     */
    private void init() {
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        fragmentManager = getSupportFragmentManager();

        faqFragment = FaqFragment.newInstance(getString(R.string.bottom_nav_faq_activity_main));
        homeFragment = HomeFragment.newInstance(getString(R.string.bottom_nav_home_activity_main));
        chatFragment = ChatFragment.newInstance(getString(R.string.bottom_nav_chat_activity_main));

        bottomNavigationView.setSelectedItemId(R.id.bottom_nav_home);
        fragmentManager.beginTransaction().add(R.id.frame_content_main, homeFragment).commit();
    }

    /**
     * Initialise listeners.
     */
    private void initListeners() {
        navigationView.setNavigationItemSelectedListener(this);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        final int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
        // Handle navigation view item clicks here.
        final int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.bottom_nav_faq) {
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_content_main, faqFragment)
                    .commit();
            return true;
        } else if (id == R.id.bottom_nav_home) {
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_content_main, homeFragment)
                    .commit();
            return true;
        } else if (id == R.id.bottom_nav_chat) {
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_content_main, chatFragment)
                    .commit();
            return true;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void updateChatFragment() {

    }

    @Override
    public void updateHomeFragment() {

    }

    @Override
    public void updateFaqFragment() {

    }
}

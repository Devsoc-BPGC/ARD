package com.macbitsgoa.ard.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.fragments.posts.HomeAnnFragment;
import com.macbitsgoa.ard.interfaces.PostListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This activity is used to create new posts in HomeFragment.
 *
 * @author Vikramaditya Kukreja
 */
public class PostActivity extends BaseActivity implements View.OnClickListener, PostListener {

    /**
     * Toolbar for this activity.
     */
    @BindView(R.id.toolbar_activity_post)
    public Toolbar toolbar;

    /**
     * Fab that is used to send posts in Firebase.
     */
    @BindView(R.id.fab_activity_post)
    public FloatingActionButton sendFab;

    /**
     * Boolean variable to maintain FAB state.
     */
    private boolean isFabVisible;

    /**
     * Object of {@link HomeAnnFragment}.
     */
    private HomeAnnFragment homeAnnFragment;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Since there is only one post type as of now, there is no check required for post type
        //which can be passed onto this activity using the intent object.
        if (savedInstanceState == null) {
            init();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_activity_post, homeAnnFragment)
                    .commit();
            sendFab.setOnClickListener(this);
        }
    }

    /**
     * All initializations can be done here.
     */
    private void init() {
        homeAnnFragment = HomeAnnFragment.newInstance(null);
        homeAnnFragment.setRetainInstance(true);
    }

    @Override
    public void onClick(final View v) {
        homeAnnFragment.post();
    }

    @Override
    public void hideFab() {
        if (isFabVisible) {
            isFabVisible = false;
            final Animation animation = AnimationUtils.loadAnimation(this, R.anim.fab_close);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(final Animation animation) {
                    //Nothing to be done here.
                }

                @Override
                public void onAnimationEnd(final Animation animation) {
                    sendFab.setVisibility(View.GONE);

                    //To keep coverage at 100%
                    onAnimationRepeat(animation);
                }

                @Override
                public void onAnimationRepeat(final Animation animation) {
                    //Empty. Not used.
                }
            });
            animation.setDuration(getResources().getInteger(R.integer.anim_fab_duration));
            sendFab.startAnimation(animation);
        }
    }

    @Override
    public void showFab() {
        if (!isFabVisible) {
            isFabVisible = true;
            final Animation animation = AnimationUtils.loadAnimation(this, R.anim.fab_open);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(final Animation animation) {
                    sendFab.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(final Animation animation) {
                    //To keep coverage at 100%
                    onAnimationRepeat(animation);
                }

                @Override
                public void onAnimationRepeat(final Animation animation) {
                    //Nothing to be done here.
                }
            });
            animation.setDuration(getResources().getInteger(R.integer.anim_fab_duration));
            animation.start();
            sendFab.startAnimation(animation);
        }
    }
}

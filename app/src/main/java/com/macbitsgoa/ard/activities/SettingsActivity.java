package com.macbitsgoa.ard.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.fragments.SettingsFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * SettingsActivity for app.
 * This contains a settingsfragment which loads data from preferences file in xml folder
 *
 * @author Vikramaditya Kukreja
 */
public class SettingsActivity extends BaseActivity {

    /**
     * Toolbar mainly for back button use.
     */
    @BindView(R.id.toolbar_activity_settings)
    public Toolbar toolbar;

    /**
     * Settings fragment to be displayed.
     */
    SettingsFragment settingsFragment;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        settingsFragment = new SettingsFragment();
        getFragmentManager()
                .beginTransaction()
                .add(R.id.frame_activity_settings, settingsFragment)
                .commit();
    }
}

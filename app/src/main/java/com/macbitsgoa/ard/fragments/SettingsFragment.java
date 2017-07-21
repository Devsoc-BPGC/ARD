package com.macbitsgoa.ard.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.macbitsgoa.ard.R;

/**
 * Settings Fragment that contains the preferences.xml file.
 *
 * @author Vikramaditya Kukreja
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}

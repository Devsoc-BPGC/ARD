package com.macbitsgoa.ard.adapters;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.macbitsgoa.ard.fragments.forum.GeneralFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewPagerAdapter extending FragmentPagerAdapter that accepts a fragment and title arg.
 *
 * @author Vikramaditya Kukreja
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

    /**
     * List to hold fragments objects.
     */
    private List<Fragment> fragmentList = new ArrayList<>();

    /**
     * List to hold fragment titles.
     */
    private List<String> fragmentTitlesList = new ArrayList<>();

    /**
     * Constructor with args.
     *
     * @param manager Fragment manager obj.
     */
    public ViewPagerAdapter(@NonNull final FragmentManager manager) {
        super(manager);
        addFragment(GeneralFragment.newInstance("0"), "Ph.D.");
        addFragment(GeneralFragment.newInstance("1"), "M.E.");
        addFragment(GeneralFragment.newInstance("2"), "B.E.");
        addFragment(GeneralFragment.newInstance("3"), "Others");
    }

    @Override
    public Fragment getItem(@IntRange(from = 0) final int position) {
        return fragmentList.get(position);
    }

    @Override
    public CharSequence getPageTitle(@IntRange(from = 0) final int position) {
        return fragmentTitlesList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    /**
     * Add a new fragment with the given title.
     *
     * @param fragment Fragment object to display.
     * @param title    Title for the fragment.
     */
    public void addFragment(@NonNull final Fragment fragment, @NonNull final String title) {
        fragmentList.add(fragment);
        fragmentTitlesList.add(title);
    }
}

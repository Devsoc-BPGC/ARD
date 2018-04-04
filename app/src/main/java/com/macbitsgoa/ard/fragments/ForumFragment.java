package com.macbitsgoa.ard.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.adapters.ViewPagerAdapter;
import com.macbitsgoa.ard.services.ForumService;
import com.macbitsgoa.ard.utils.AHC;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ForumFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * @author Vikramaditya Kukreja
 */
public class ForumFragment extends BaseFragment {

    /**
     * TAG for class.
     */
    public static final String TAG = ForumFragment.class.getSimpleName();

    /**
     * Viewpager to display inner fragments.
     */
    @BindView(R.id.viewPager_fragment_forum)
    ViewPager viewPager;

    /**
     * Tab layout used with viewpager.
     */
    @BindView(R.id.tabLayout_fragment_forum)
    TabLayout tabLayout;

    /**
     * Unbinder to remove views.
     */
    private Unbinder unbinder;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param fragmentTitle Name of fragment to use.
     * @return A new instance of fragment ForumFragment.
     */
    public static ForumFragment newInstance(@NonNull final String fragmentTitle) {
        final ForumFragment fragment = new ForumFragment();
        final Bundle args = new Bundle();
        args.putString(AHC.FRAGMENT_TITLE_KEY, fragmentTitle);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_forum, container, false);
        unbinder = ButterKnife.bind(this, view);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(new ViewPagerAdapter(getChildFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);

        getContext().startService(new Intent(getContext(), ForumService.class));
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}

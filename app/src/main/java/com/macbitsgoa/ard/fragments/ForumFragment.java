package com.macbitsgoa.ard.fragments;

import android.content.Context;
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
import com.macbitsgoa.ard.fragments.forum.GeneralFragment;
import com.macbitsgoa.ard.interfaces.ForumFragmentListener;
import com.macbitsgoa.ard.utils.AHC;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment can implement the
 * {@link ForumFragmentListener} interface
 * to handle interaction events.
 * Use the {@link ForumFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * @author Vikramaditya Kukreja
 */
public class ForumFragment extends Fragment {

    /**
     * Viewpager to display inner fragments.
     */
    @BindView(R.id.viewPager_fragment_forum)
    public ViewPager viewPager;

    /**
     * Tab layout used with viewpager.
     */
    @BindView(R.id.tabLayout_fragment_forum)
    public TabLayout tabLayout;

    /**
     * Viewpager adapter for sub sections.
     */
    private ViewPagerAdapter viewPagerAdapter;

    /**
     * Unbinder to remove views.
     */
    private Unbinder unbinder;

    /**
     * Used to communicate with activity.
     */
    private ForumFragmentListener mListener;

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
        mListener.updateForumFragment();
        unbinder = ButterKnife.bind(this, view);
        init();

        return view;
    }

    /**
     * All inits can be done here for easy reading.
     */
    private void init() {
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPagerAdapter.addFragment(GeneralFragment.newInstance(
                getString(R.string.tabLayout_forum_general)),
                getString(R.string.tabLayout_forum_general));
        viewPager.setOffscreenPageLimit(viewPagerAdapter.getCount() - 1);

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewPagerAdapter = null;
        unbinder.unbind();
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        if (context instanceof ForumFragmentListener) {
            mListener = (ForumFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}

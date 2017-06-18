package in.ac.bits_pilani.goa.ard.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import in.ac.bits_pilani.goa.ard.R;
import in.ac.bits_pilani.goa.ard.adapters.ViewPagerAdapter;
import in.ac.bits_pilani.goa.ard.interfaces.FaqFragmentListener;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment can implement the
 * {@link FaqFragmentListener} interface
 * to handle interaction events.
 * Use the {@link FaqFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * @author Vikramaditya Kukreja
 */
public class FaqFragment extends Fragment {

    /**
     * Viewpager to display inner fragments.
     */
    @BindView(R.id.viewPager_fragment_faq)
    public ViewPager viewPager;

    /**
     * Tab layout used with viewpager.
     */
    @BindView(R.id.tabLayout_fragment_faq)
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
    private FaqFragmentListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param args Bundled arg.
     * @return A new instance of fragment FaqFragment.
     */
    public static FaqFragment newInstance(@Nullable final Bundle args) {
        final FaqFragment fragment = new FaqFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_faq, container, false);

        mListener.updateFaqFragment();
        unbinder = ButterKnife.bind(this, view);
        init();

        return view;
    }

    /**
     * All inits can be done here for easy reading.
     */
    private void init() {
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());

        viewPagerAdapter.addFragment(FaqGeneralFragment.newInstance(null),
                getString(R.string.tabLayout_faq_general));

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
        if (context instanceof FaqFragmentListener) {
            mListener = (FaqFragmentListener) context;
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

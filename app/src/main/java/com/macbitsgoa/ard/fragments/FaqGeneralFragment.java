package com.macbitsgoa.ard.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.macbitsgoa.ard.R;

/**
 * General FAQ fragment displayed inside FAQ Fragment.
 * In order to create an instance, simply call {@code newInstance(Bundle args)}
 *
 * @author Vikramaditya Kukreja
 */
public class FaqGeneralFragment extends Fragment {

    /**
     * Use this method to create a new instance of the fragment.
     * It accepts an object of Bundle as a parameter (can be null).
     *
     * @param args Bundle object to be sent.
     * @return A new instance of fragment FaqGeneralFragment.
     */
    public static FaqGeneralFragment newInstance(@Nullable final Bundle args) {
        final FaqGeneralFragment fragment = new FaqGeneralFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_faq_general, container, false);
    }
}

package in.ac.bits_pilani.goa.ard.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.ac.bits_pilani.goa.ard.R;
import in.ac.bits_pilani.goa.ard.interfaces.FaqFragmentListener;
import in.ac.bits_pilani.goa.ard.utils.AHC;

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
     * Tag for this fragment.
     * Should be replaced with Short tag string and suffix with class name.
     */
    private final String TAG = AHC.TAG + ".fragments." + FaqFragment.class.getSimpleName();

    /**
     * Fragment title to be used.
     */
    private String fragmentTitle;

    /**
     * Used to communicate with activity.
     */
    private FaqFragmentListener mListener;

    public FaqFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param fragmentTitle Title for the fragment.
     * @return A new instance of fragment FaqFragment.
     */
    public static FaqFragment newInstance(final String fragmentTitle) {
        final FaqFragment fragment = new FaqFragment();
        final Bundle args = new Bundle();
        args.putString(AHC.FRAGMENT_TITLE_KEY, fragmentTitle);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            fragmentTitle = getArguments().getString(AHC.FRAGMENT_TITLE_KEY);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mListener.updateFaqFragment();
        Log.d(TAG, fragmentTitle);
        return inflater.inflate(R.layout.fragment_faq, container, false);
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

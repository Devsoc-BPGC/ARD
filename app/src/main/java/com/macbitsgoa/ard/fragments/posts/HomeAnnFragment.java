package com.macbitsgoa.ard.fragments.posts;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.fragments.HomePostFragment;
import com.macbitsgoa.ard.interfaces.PostListener;
import com.macbitsgoa.ard.keys.AnnItemKeys;
import com.macbitsgoa.ard.types.PostType;
import com.macbitsgoa.ard.utils.AHC;
import com.macbitsgoa.ard.utils.PostUtil;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Fragment to create an announcement.
 *
 * @author Vikramaditya Kukreja
 */
public class HomeAnnFragment extends HomePostFragment implements TextWatcher {

    /**
     * EditText that the user can utilise for making an announcement.
     */
    @BindView(R.id.editText_home_ann_fragment_message)
    public TextInputEditText userText;

    /**
     * Counter textView to display characters left.
     */
    @BindView(R.id.textView_home_ann_fragment_counter)
    public TextView counterText;

    /**
     * Unbinder to unbind views onDestroy of fragment.
     */
    private Unbinder unbinder;

    /**
     * Maximum characters allowed for announcement text.
     */
    private int maxChars = PostUtil.DEFAULT_MAX_CHARS;

    /**
     * Original color of counter text.
     */
    private int originalColor;

    /**
     * Use this method to create a new instance of
     * {@link HomeAnnFragment} fragment using a bundle object.
     *
     * @param bundle Bundle object to instantiate this instance.
     * @return A new instance of fragment HomeAnnFragment.
     */
    public static HomeAnnFragment newInstance(@Nullable final Bundle bundle) {
        final HomeAnnFragment fragment = new HomeAnnFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_home_ann, container, false);

        unbinder = ButterKnife.bind(this, view);

        originalColor = counterText.getCurrentTextColor();

        userText.addTextChangedListener(this);
        userText.setText("");
        if (savedInstanceState != null) {
            userText.setText(savedInstanceState.getString(AnnItemKeys.DATA_KEY, ""));
        }
        return view;
    }

    @Override
    public void post() {
        super.post();
        final String text = userText.getText().toString().trim();
        if (text.length() == 0) {
            Snackbar.make(getView(), R.string.info_home_ann_fragment_empty_text, Snackbar.LENGTH_SHORT).show();
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle(getString(R.string.info_home_ann_fragment_dialog_title));
        progressDialog.setMessage(getString(R.string.info_home_ann_fragment_dialog_message));
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        final DatabaseReference databaseReference = getRootReference().child(AHC.FDR_HOME);
        final DatabaseReference newC = databaseReference.push();

        final Map<String, Object> map = new HashMap<>();
        map.put(AnnItemKeys.TYPE_KEY, PostType.ANNOUNCEMENT);
        map.put(AnnItemKeys.DATA_KEY, text);
        map.put(AnnItemKeys.AUTHOR_KEY, "debug");
        map.put(AnnItemKeys.DATE_KEY, Calendar.getInstance().getTime());

        newC.setValue(map, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(final DatabaseError error,
                                   final DatabaseReference ref) {
                progressDialog.dismiss();
                if (error != null && getView() != null) {
                    Snackbar.make(getView(), R.string.info_home_ann_fragment_post_error, Snackbar.LENGTH_SHORT).show();
                } else if (error == null && getView() != null) {
                    Snackbar.make(getView(), R.string.info_home_ann_fragment_post_success,
                            Snackbar.LENGTH_SHORT).show();
                    userText.setText("");
                }
            }
        });
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        setPostListener((PostListener) context);
        maxChars = getInteger(R.integer.ann_max_char);
    }

    /**
     * Save the text entered by the user.
     *
     * @param outState Bundle object which contains the saved text.
     */
    @Override
    public void onSaveInstanceState(final Bundle outState) {
        outState.putString(AnnItemKeys.DATA_KEY, userText.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void beforeTextChanged(final CharSequence s, final int start,
                                  final int count, final int after) {
        //Not much to do here.
    }

    @Override
    public void onTextChanged(final CharSequence s, final int start,
                              final int before, final int count) {
        counterText.setText(s.length() + "/" + maxChars);
        if (s.length() > maxChars) {
            counterText.setTextColor(Color.RED);
            postListener.hideFab();
        } else {
            counterText.setTextColor(originalColor);
            postListener.showFab();
        }
    }

    @Override
    public void afterTextChanged(final Editable s) {
        if (counterText.getVisibility() == View.GONE) {
            counterText.setVisibility(View.VISIBLE);
        } else if (s.length() == 0) {
            counterText.setVisibility(View.GONE);
        }
    }
}

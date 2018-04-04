package com.macbitsgoa.ard.fragments.forum;

import android.app.AlertDialog;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.TooltipCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.adapters.ForumAdapter;
import com.macbitsgoa.ard.fragments.BaseFragment;
import com.macbitsgoa.ard.interfaces.AdapterNotificationListener;
import com.macbitsgoa.ard.interfaces.SortOrderChangeListener;
import com.macbitsgoa.ard.keys.FaqItemKeys;
import com.macbitsgoa.ard.utils.AHC;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Sort;

/**
 * General Forum fragment displayed inside Forum Fragment.
 * In order to create an instance, simply call {@code newInstance(String fragmentTitle)}
 *
 * @author Vikramaditya Kukreja
 */
public class GeneralFragment extends BaseFragment implements AdapterNotificationListener,
        SortOrderChangeListener {

    /**
     * TAG for class.
     */
    public static final String TAG = GeneralFragment.class.getSimpleName();

    /**
     * Section key code.
     */
    public static final String SECTION_KEY = "sectionKey";

    /**
     * RecyclerView to display faqs.
     */
    @BindView(R.id.rv_fg_forum_general)
    RecyclerView recyclerView;

    /**
     * Text to show in case of empty adapter.
     */
    @BindView(R.id.tv_fg_forum_general_empty)
    TextView emptyTextView;

    /**
     * Sort order image button.
     */
    @BindView(R.id.imgView_fragment_forum_general_sort_order)
    ImageButton sortOrderImg;

    /**
     * Sort image button.
     */
    @BindView(R.id.imgView_fragment_forum_general_sort)
    ImageButton sortImg;

    /**
     * Adapter for RecyclerView.
     */
    private ForumAdapter forumAdapter;

    /**
     * Refers to "Last modified".
     */
    private int currentSortOrder = 2;

    /**
     * Use this method to create a new instance of the fragment.
     * It accepts a non null String object representing faq section name.
     *
     * @param fragmentId id of faq section to show.
     * @return A new instance of fragment GeneralFragment.
     */
    public static GeneralFragment newInstance(@NonNull final String fragmentId) {
        final GeneralFragment fragment = new GeneralFragment();
        final Bundle args = new Bundle();
        args.putString(SECTION_KEY, fragmentId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_forum_general, container, false);
        ButterKnife.bind(this, view);

        TooltipCompat.setTooltipText(sortImg, "Sort");
        TooltipCompat.setTooltipText(sortOrderImg, "Asc/Desc");

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        forumAdapter = new ForumAdapter(getArguments().getString(SECTION_KEY), this);
        recyclerView.setAdapter(forumAdapter);

        return view;
    }

    /**
     * Sort direction change listener.
     */
    @OnClick(R.id.imgView_fragment_forum_general_sort_order)
    public void onSortDirectionChanged() {
        final String fieldName;
        if (currentSortOrder == 0) {
            fieldName = FaqItemKeys.QUES;
        } else if (currentSortOrder == 1) {
            fieldName = FaqItemKeys.ORIGINAL;
        } else {
            fieldName = FaqItemKeys.UPDATE;
        }
        forumAdapter.sortBy(fieldName, false);
    }

    /**
     * On sort button press.
     */
    @OnClick(R.id.imgView_fragment_forum_general_sort)
    public void onSortPressed() {
        ((Animatable) sortImg.getDrawable()).start();
        final CharSequence[] sortOrders = new CharSequence[]{
                "Alphabetical",
                "Last Created",
                "Last Modified",
        };
        new AlertDialog.Builder(getActivity()).setSingleChoiceItems(sortOrders, currentSortOrder,
                (dialog, which) -> {
                    final String fieldName;
                    if (which == 0) {
                        currentSortOrder = 0;
                        fieldName = FaqItemKeys.QUES;
                    } else if (which == 1) {
                        currentSortOrder = 1;
                        fieldName = FaqItemKeys.ORIGINAL;
                    } else {
                        currentSortOrder = 2;
                        fieldName = FaqItemKeys.UPDATE;
                    }
                    forumAdapter.sortBy(fieldName, true);
                    AHC.logd(TAG, "Notifying faq adapter of sort change");
                }).show();
    }

    @Override
    public void onAdapterNotified(final int size) {
        if (forumAdapter.getItemCount() == 0) {
            emptyTextView.setVisibility(View.VISIBLE);
        } else {
            emptyTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSortOrderChanged(final Sort newSortOrder) {
        if (newSortOrder == Sort.DESCENDING) {
            sortOrderImg.setImageResource(R.drawable.avd_anim_desc);
            ((Animatable) sortOrderImg.getDrawable()).start();
        } else {
            sortOrderImg.setImageResource(R.drawable.avd_anim_asc);
            ((Animatable) sortOrderImg.getDrawable()).start();
        }
    }
}

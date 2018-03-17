package com.macbitsgoa.ard.fragments.forum;

import android.app.AlertDialog;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.adapters.ForumAdapter;
import com.macbitsgoa.ard.fragments.BaseFragment;
import com.macbitsgoa.ard.keys.FaqItemKeys;
import com.macbitsgoa.ard.models.FaqItem;
import com.macbitsgoa.ard.utils.AHC;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * General Forum fragment displayed inside Forum Fragment.
 * In order to create an instance, simply call {@code newInstance(String fragmentTitle)}
 *
 * @author Vikramaditya Kukreja
 */
public class GeneralFragment extends BaseFragment {

    /**
     * TAG for class.
     */
    public static final String TAG = GeneralFragment.class.getSimpleName();

    /**
     * Section key code.
     */
    public static final String SECTION_KEY = "sectionKey";

    /**
     * Recyclerview to display faqs.
     */
    @BindView(R.id.rv_fg_forum_general)
    RecyclerView recyclerView;

    /**
     * Text to show in case of empty adapter.
     */
    @BindView(R.id.tv_fg_forum_general_empty)
    TextView emptyTextView;


    @BindView(R.id.imgView_fragment_forum_general_sort_order)
    ImageView sortOrderImg;

    @BindView(R.id.imgView_fragment_forum_general_sort)
    ImageView sortImg;

    /**
     * Adapter for recyclerview.
     */
    private ForumAdapter forumAdapter;
    private RealmResults<FaqItem> faqItems;

    /**
     * Final list to use for displaying using adapter.
     */
    private List<FaqItem> items;
    Sort sort = Sort.DESCENDING;
    Animatable toDesc, toAsc, sortAnimatable;
    int currentSortOrder = 2;

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
        items = new ArrayList<>();
        forumAdapter = new ForumAdapter(items);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(forumAdapter);
        toDesc = (Animatable) sortOrderImg.getDrawable();
        sortAnimatable = (Animatable) sortImg.getDrawable();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        faqItems = database.where(FaqItem.class)
                .equalTo(FaqItemKeys.SECTION, getArguments().getString(SECTION_KEY))
                .findAllSorted(FaqItemKeys.UPDATE, sort);
        populateAdapter();
    }

    /**
     * Method to generate adapter data and set adapter.
     */
    private void populateAdapter() {
        items.clear();
        items.addAll(faqItems);
        forumAdapter.notifyDataSetChanged();
        if (forumAdapter.getItemCount() == 0) {
            emptyTextView.setVisibility(View.VISIBLE);
        } else {
            emptyTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStop() {
        faqItems.removeAllChangeListeners();
        super.onStop();
    }

    boolean sortOrderDesc = true;

    @OnClick(R.id.imgView_fragment_forum_general_sort_order)
    public void onSortDirectionChanged() {
        String fieldName;
        if (currentSortOrder == 0) {
            fieldName = FaqItemKeys.QUES;
        } else if (currentSortOrder == 1) {
            fieldName = FaqItemKeys.ORIGINAL;
        } else {
            fieldName = FaqItemKeys.UPDATE;
        }
        if (sortOrderDesc) {
            sort = Sort.ASCENDING;
            sortOrderImg.setImageResource(R.drawable.avd_anim_desc);
            toDesc = (Animatable) sortOrderImg.getDrawable();
            toDesc.start();
        } else {
            sort = Sort.DESCENDING;
            sortOrderImg.setImageResource(R.drawable.avd_anim_asc);
            toAsc = (Animatable) sortOrderImg.getDrawable();
            toAsc.start();
        }
        faqItems = faqItems.sort(fieldName, sort);
        populateAdapter();
        sortOrderDesc = !sortOrderDesc;
    }

    @OnClick(R.id.imgView_fragment_forum_general_sort)
    public void onSortPressed() {
        sortAnimatable.start();
        CharSequence[] sortOrders = new CharSequence[]{"Alphabetical", "Last Created", "Last Modified"};
        new AlertDialog.Builder(getActivity())
                .setSingleChoiceItems(sortOrders,
                        currentSortOrder,
                        (dialog, which) -> {
                            if (which == 0) {
                                currentSortOrder = 0;
                                faqItems = faqItems.sort(FaqItemKeys.QUES, sort);
                            } else if (which == 1) {
                                currentSortOrder = 1;
                                faqItems = faqItems.sort(FaqItemKeys.ORIGINAL, sort);
                            } else if (which == 2) {
                                currentSortOrder = 2;
                                faqItems = faqItems.sort(FaqItemKeys.UPDATE, sort);
                            }
                            populateAdapter();
                            AHC.logd(TAG, "Notifying faq adapter of sort change");
                        }).show();
    }
}

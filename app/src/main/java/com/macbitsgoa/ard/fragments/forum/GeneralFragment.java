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
import com.macbitsgoa.ard.keys.FaqItemKeys;
import com.macbitsgoa.ard.models.FaqItem;
import com.macbitsgoa.ard.utils.AHC;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.OrderedRealmCollectionChangeListener;
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
     * RecyclerView to display faqs.
     */
    @BindView(R.id.rv_fg_forum_general)
    RecyclerView recyclerView;

    /**
     * Text to show in case of empty adapter.
     */
    @BindView(R.id.tv_fg_forum_general_empty)
    TextView emptyTextView;


    @BindView(R.id.imgView_fragment_forum_general_sort_order)
    ImageButton sortOrderImg;

    @BindView(R.id.imgView_fragment_forum_general_sort)
    ImageButton sortImg;

    /**
     * Adapter for RecyclerView.
     */
    private ForumAdapter forumAdapter;

    /**
     * Faq items from database.
     */
    private RealmResults<FaqItem> faqItems;

    /**
     * Default sorting order to be used.
     */
    Sort sort = Sort.DESCENDING;


    Animatable toDesc, toAsc, sortAnimatable;

    /**
     * Refers to "Last modified".
     */
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
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        TooltipCompat.setTooltipText(sortImg, "Sort");
        TooltipCompat.setTooltipText(sortOrderImg, "Asc/Desc");
        toDesc = (Animatable) sortOrderImg.getDrawable();
        sortAnimatable = (Animatable) sortImg.getDrawable();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        faqItems = database.where(FaqItem.class)
                .equalTo(FaqItemKeys.SECTION, getArguments().getString(SECTION_KEY))
                .findAllSortedAsync(new String[]{FaqItemKeys.SUB_SECTION, FaqItemKeys.UPDATE},
                        new Sort[]{Sort.ASCENDING, sort});
        forumAdapter = new ForumAdapter(faqItems);
        recyclerView.setAdapter(forumAdapter);
        faqItems.addChangeListener(getChangeListener());
    }

    @NonNull
    private OrderedRealmCollectionChangeListener<RealmResults<FaqItem>> getChangeListener() {
        return (collection, changeSet) -> {
            // `null`  means the async query returns the first time.
            //We are not listening for each and every update but only for the first one.
            if (changeSet == null) {
                forumAdapter.notifyDataSetChanged();
                if (forumAdapter.getItemCount() == 0) {
                    emptyTextView.setVisibility(View.VISIBLE);
                } else {
                    emptyTextView.setVisibility(View.GONE);
                }
                if (forumAdapter.getItemCount() == 0) {
                    emptyTextView.setVisibility(View.VISIBLE);
                } else {
                    emptyTextView.setVisibility(View.GONE);
                }
            }
        };
    }

    @Override
    public void onStop() {
        faqItems.removeAllChangeListeners();
        super.onStop();
    }

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
        if (sort == Sort.DESCENDING) {
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
        sortAndUpdateList(fieldName, sort);
    }

    public void sortAndUpdateList(String fieldname, Sort sort) {
        faqItems.removeAllChangeListeners();
        faqItems = faqItems.sort(new String[]{FaqItemKeys.SUB_SECTION, fieldname},
                new Sort[]{Sort.ASCENDING, sort});
        faqItems.addChangeListener(getChangeListener());
        forumAdapter.setNewData(faqItems);
    }

    @OnClick(R.id.imgView_fragment_forum_general_sort)
    public void onSortPressed() {
        sortAnimatable.start();
        final CharSequence[] sortOrders = new CharSequence[]{
                "Alphabetical",
                "Last Created",
                "Last Modified",
        };
        new AlertDialog.Builder(getActivity())
                .setSingleChoiceItems(sortOrders,
                        currentSortOrder,
                        (dialog, which) -> {
                            String fieldName;
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
                            sortAndUpdateList(fieldName, sort);
                            AHC.logd(TAG, "Notifying faq adapter of sort change");
                        }).show();
    }
}

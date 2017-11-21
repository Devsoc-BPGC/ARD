package com.macbitsgoa.ard.fragments.forum;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.adapters.ForumAdapter;
import com.macbitsgoa.ard.fragments.BaseFragment;
import com.macbitsgoa.ard.models.FaqItem;
import com.macbitsgoa.ard.models.TypeItem;
import com.macbitsgoa.ard.types.ForumType;
import com.macbitsgoa.ard.utils.AHC;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmResults;

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
    public static final String SECTION = "sectionKey";

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

    /**
     * Adapter for recyclerview.
     */
    private ForumAdapter forumAdapter;
    private RealmResults<FaqItem> faqItems;

    /**
     * Final list to use for displaying using adapter.
     */
    private List<TypeItem> items;

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
        args.putString(SECTION, fragmentId);
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
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        faqItems = database.where(FaqItem.class)
                .equalTo("section", getArguments().getString(SECTION)).findAll();
        faqItems.addChangeListener(newResults -> {
            if (forumAdapter != null) {
                populateAdapter();
            }
        });
        populateAdapter();
    }

    /**
     * Method to generate adapter data and set adapter.
     */
    private void populateAdapter() {
        if (items == null)
            items = new ArrayList<>();
        items.clear();
        AHC.fill(items, faqItems, ForumType.FAQ_ITEM);
        if (forumAdapter == null) {
            forumAdapter = new ForumAdapter(items);
            recyclerView.setAdapter(forumAdapter);
        } else {
            forumAdapter.notifyDataSetChanged();
        }
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
}

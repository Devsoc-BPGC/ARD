package com.macbitsgoa.ard.fragments.forum;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.adapters.ForumAdapter;
import com.macbitsgoa.ard.fragments.BaseFragment;
import com.macbitsgoa.ard.keys.FaqItemKeys;
import com.macbitsgoa.ard.models.FaqItem;
import com.macbitsgoa.ard.models.TypeItem;
import com.macbitsgoa.ard.types.ForumType;
import com.macbitsgoa.ard.utils.AHC;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
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

    @BindView(R.id.rv_fg_forum_general)
    RecyclerView recyclerView;

    private DatabaseReference forumRef;
    private ValueEventListener forumRefVEL;
    private ForumAdapter forumAdapter;
    private RealmResults<FaqItem> faqItems;

    /**
     * Use this method to create a new instance of the fragment.
     * It accepts a non null String object representing framnet name.
     *
     * @param fragmentTitle title for fragment.
     * @return A new instance of fragment GeneralFragment.
     */
    public static GeneralFragment newInstance(@NonNull final String fragmentTitle) {
        final GeneralFragment fragment = new GeneralFragment();
        final Bundle args = new Bundle();
        args.putString(AHC.FRAGMENT_TITLE_KEY, fragmentTitle);
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

        faqItems = database.where(FaqItem.class).findAll();
        faqItems.addChangeListener(newResults -> {
            if (forumAdapter != null) forumAdapter.notifyDataSetChanged();
        });
        final List<TypeItem> items = new ArrayList<>();
        AHC.fill(items, faqItems, ForumType.FAQ_ITEM);

        forumAdapter = new ForumAdapter(items);
        recyclerView.setAdapter(forumAdapter);

        forumRef = getRootReference().child(AHC.FDR_FORUM);
        forumRefVEL = getForumVEL();
        forumRef.addValueEventListener(forumRefVEL);
    }

    @Override
    public void onStop() {
        forumRef.removeEventListener(forumRefVEL);
        faqItems.removeAllChangeListeners();
        super.onStop();
    }

    public ValueEventListener getForumVEL() {
        return new ValueEventListener() {
            private Realm database;

            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                //faq children
                database = Realm.getDefaultInstance();
                faqParse(dataSnapshot.child(FaqItemKeys.FDR_FAQ));
                database.close();
            }

            private void faqParse(final DataSnapshot faqSnapshot) {
                for (final DataSnapshot child : faqSnapshot.getChildren()) {
                    final String key = child.getKey();
                    final Date updateDate = child.child(FaqItemKeys.UPDATE).getValue(Date.class);
                    database.executeTransaction(r -> {
                        FaqItem fi = r.where(FaqItem.class)
                                .equalTo(FaqItemKeys.KEY, key).findFirst();
                        if (fi == null) {
                            fi = r.createObject(FaqItem.class, key);
                        } else {
                            if (fi.getUpdateDate().getTime() == updateDate.getTime()) {
                                return;
                            }
                        }
                        fi.setQuestion(child.child(FaqItemKeys.QUES).getValue(String.class));
                        fi.setAnswer(child.child(FaqItemKeys.ANS).getValue(String.class));
                        fi.setAuthor(child.child(FaqItemKeys.AUTHOR).getValue(String.class));
                        fi.setSection(child.child(FaqItemKeys.SECTION).getValue(String.class));
                        fi.setDesc(child.child(FaqItemKeys.DESC).getValue(String.class));
                        fi.setOriginalDate(child.child(FaqItemKeys.ORIGINAL).getValue(Date.class));
                        fi.setUpdateDate(child.child(FaqItemKeys.UPDATE).getValue(Date.class));
                    });
                }
            }

            @Override
            public void onCancelled(final DatabaseError databaseError) {
                Log.e(TAG, "Database read error for forum node\n" + databaseError.toString());
            }
        };
    }
}

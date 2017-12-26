package com.macbitsgoa.ard.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.adapters.ViewPagerAdapter;
import com.macbitsgoa.ard.fragments.forum.GeneralFragment;
import com.macbitsgoa.ard.interfaces.ForumFragmentListener;
import com.macbitsgoa.ard.keys.FaqItemKeys;
import com.macbitsgoa.ard.models.FaqItem;
import com.macbitsgoa.ard.models.FaqSectionItem;
import com.macbitsgoa.ard.utils.AHC;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

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
public class ForumFragment extends BaseFragment {

    /**
     * TAG for class.
     */
    public static final String TAG = ForumFragment.class.getSimpleName();

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
     * Forum reference on Firebase.
     */
    private DatabaseReference forumRef;

    /**
     * {@link #forumRef} listener.
     */
    private ValueEventListener forumRefVEL;

    /**
     * Sections list.
     */
    private RealmResults<FaqSectionItem> sections;

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

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        init();
    }

    /**
     * All inits can be done here for easy reading.
     */
    private void init() {
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        sections = database.where(FaqSectionItem.class)
                .findAllSorted(FaqItemKeys.DB_FAQ_SECTION_PRIORITY, Sort.ASCENDING);
        sections.addChangeListener(faqSectionItems -> {
            viewPagerAdapter.removeAll();
            for (int i = 0; i < faqSectionItems.size(); i++) {
                final String fragmentTitle = faqSectionItems.get(i).getSectionTitle();
                final String fragmentKey = faqSectionItems.get(i).getSectionKey();
                final GeneralFragment gf = GeneralFragment.newInstance(fragmentKey);
                if (database.where(FaqItem.class)
                        .equalTo(FaqItemKeys.SECTION, fragmentKey)
                        .findAll()
                        .size() != 0)
                    viewPagerAdapter.addFragment(gf, fragmentTitle);
            }
            viewPagerAdapter.notifyDataSetChanged();
        });
        for (int i = 0; i < sections.size(); i++) {
            final String fragmentTitle = sections.get(i).getSectionTitle();
            final String fragmentKey = sections.get(i).getSectionKey();
            final GeneralFragment gf = GeneralFragment.newInstance(fragmentKey);
            if (database.where(FaqItem.class)
                    .equalTo(FaqItemKeys.SECTION, fragmentKey)
                    .findAll()
                    .size() != 0)
                viewPagerAdapter.addFragment(gf, fragmentTitle);
        }
        viewPager.setOffscreenPageLimit(viewPagerAdapter.getCount() - 1);

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        forumRef = getRootReference().child(AHC.FDR_FORUM);
        forumRefVEL = getForumVEL();
        forumRef.addValueEventListener(forumRefVEL);
    }

    @Override
    public void onStop() {
        viewPagerAdapter = null;
        forumRef.removeEventListener(forumRefVEL);
        sections.removeAllChangeListeners();
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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

    /**
     * Initialise {@link #forumRefVEL}.
     * Syncs firebase db with local realm db.
     *
     * @return ValueEventListener
     */
    public ValueEventListener getForumVEL() {
        return new ValueEventListener() {
            private Realm database;

            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                //faq children
                database = Realm.getDefaultInstance();
                faqParse(dataSnapshot.child(FaqItemKeys.FDR_FAQ));
                faqSectionParse(dataSnapshot.child(FaqItemKeys.FDR_FAQ_SECTION));
                database.close();
            }

            private void faqSectionParse(final DataSnapshot faqSectionShot) {
                //TODO manage deletes as well
                for (final DataSnapshot childShot : faqSectionShot.getChildren()) {
                    database.executeTransaction(r -> {
                        FaqSectionItem fsi = r.where(FaqSectionItem.class)
                                .equalTo(FaqItemKeys.DB_FAQ_SECTION_KEY, childShot.getKey())
                                .findFirst();
                        if (fsi == null) {
                            fsi = r.createObject(FaqSectionItem.class, childShot.getKey());
                        }
                        fsi.setSectionTitle(childShot
                                .child(FaqItemKeys.FDR_FAQ_SECTION_TITLE).getValue(String.class));
                        fsi.setSectionPriority(childShot
                                .child(FaqItemKeys.FDR_FAQ_SECTION_PRIORITY)
                                .getValue(String.class));
                    });
                }
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
                        } else if (fi.getUpdateDate().getTime() == updateDate.getTime()) {
                            return;
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

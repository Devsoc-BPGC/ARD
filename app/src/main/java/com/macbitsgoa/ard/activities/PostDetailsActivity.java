package com.macbitsgoa.ard.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.adapters.HomeAdapter;
import com.macbitsgoa.ard.keys.AnnItemKeys;
import com.macbitsgoa.ard.keys.HomeItemKeys;
import com.macbitsgoa.ard.models.AnnItem;
import com.macbitsgoa.ard.models.TypeItem;
import com.macbitsgoa.ard.models.home.HomeItem;
import com.macbitsgoa.ard.models.home.PhotoItem;
import com.macbitsgoa.ard.models.home.TextItem;
import com.macbitsgoa.ard.types.HomeType;
import com.macbitsgoa.ard.types.PostType;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Detail activity for Home posts.
 *
 * @author Vikramaditya Kukreja
 */
public class PostDetailsActivity extends BaseActivity {

    /**
     * TAG for class.
     */
    public static final String TAG = PostDetailsActivity.class.getSimpleName();

    /**
     * Toolbar for this activity. Majorly used for back button only.
     */
    @BindView(R.id.toolbar_activity_post_details)
    Toolbar toolbar;

    /**
     * Recyclerview used to display comments.
     */
    @BindView(R.id.rv_content_post_details_post)
    RecyclerView recyclerView;

    /**
     * Textview to display when there is an error.
     */
    @BindView(R.id.tv_content_post_details_empty)
    TextView emptyTextView;

    private List<TypeItem> postItems;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);
        ButterKnife.bind(this);

        //https://developer.android.com/topic/libraries/support-library/preview/emoji-compat.html
        if (getIntent() == null
                || !(getIntent().hasExtra(HomeItemKeys.KEY)
                || getIntent().hasExtra("annItem"))) {
            finish();
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        String key = getIntent().getStringExtra(HomeItemKeys.KEY);

        if (key == null) {
            //Key field is same in both and extra chars are required to separate
            key = getIntent().getStringExtra("annItem");
            postItems = handleAnnItem(key);
        } else {
            postItems = handleHomeItem(key);
        }
        final HomeAdapter homeAdapter = new HomeAdapter(postItems, this);
        recyclerView.setAdapter(homeAdapter);

    }

    private List<TypeItem> handleHomeItem(final String key) {
        //Atmost 1 will be the size of this list
        final HomeItem post = database.where(HomeItem.class).equalTo(HomeItemKeys.KEY, key)
                .findFirst();
        final List<TypeItem> pItems = new ArrayList<>();

        if (post == null) {
            emptyTextView.setVisibility(View.VISIBLE);
        } else {

            int i = 0;
            int j = 0;
            final RealmResults<TextItem> texts = post.getTexts()
                    .sort(HomeItemKeys.PRIORITY, Sort.ASCENDING);
            final RealmResults<PhotoItem> images = post.getImages()
                    .sort(HomeItemKeys.PRIORITY, Sort.ASCENDING);
            while (i < texts.size() && j < images.size()) {
                if (texts.get(i).getPriority().compareTo(images.get(j).getPriority()) <= 0) {
                    pItems.add(new TypeItem(texts.get(i), HomeType.TEXT_ITEM));
                    i++;
                } else {
                    pItems.add(new TypeItem(images.get(j), HomeType.PHOTO_ITEM));
                    j++;
                }
            }

            for (; i < texts.size(); i++)
                pItems.add(new TypeItem(texts.get(i), HomeType.TEXT_ITEM));
            for (; j < images.size(); j++)
                pItems.add(new TypeItem(images.get(j), HomeType.PHOTO_ITEM));

        }
        return pItems;
    }

    private List<TypeItem> handleAnnItem(final String key) {
        final AnnItem ai = database.where(AnnItem.class).equalTo(AnnItemKeys.KEY, key)
                .findFirst();
        final List<TypeItem> pItems = new ArrayList<>();
        if (ai == null) {
            emptyTextView.setVisibility(View.VISIBLE);
        } else {
            pItems.add(new TypeItem(ai, PostType.ANNOUNCEMENT));
        }
        return pItems;
    }
}

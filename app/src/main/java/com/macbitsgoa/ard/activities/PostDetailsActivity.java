package com.macbitsgoa.ard.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.adapters.PostDetailsAdapter;
import com.macbitsgoa.ard.keys.HomeItemKeys;
import com.macbitsgoa.ard.models.TypeItem;
import com.macbitsgoa.ard.models.home.HomeItem;
import com.macbitsgoa.ard.models.home.PhotoItem;
import com.macbitsgoa.ard.models.home.TextItem;
import com.macbitsgoa.ard.types.HomeType;

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
     * RecyclerView used to display post content.
     */
    @BindView(R.id.rv_content_post_details_post)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);
        ButterKnife.bind(this);

        //If opening intent is null, we don't have data to show
        //exit this activity
        if (getIntent() == null
                || !(getIntent().hasExtra(HomeItemKeys.KEY))) {
            finish();
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        final String key = getIntent().getStringExtra(HomeItemKeys.KEY);

        final PostDetailsAdapter pdAdapter = new PostDetailsAdapter(handleHomeItem(key), this);
        recyclerView.setAdapter(pdAdapter);
    }

    /**
     * Method to take over if key is of HomeItem class.
     *
     * @param key Homeitem key to search for
     * @return {@link TypeItem} list to display data.
     */
    private List<TypeItem> handleHomeItem(final String key) {
        //Atmost 1 will be the size of this list
        final HomeItem post = database
                .where(HomeItem.class)
                .equalTo(HomeItemKeys.KEY, key)
                .findFirst();
        final List<TypeItem> pItems = new ArrayList<>();

        if (post == null) {
            showToast("Could not load post");
            finish();
        } else {
            recyclerView.setVisibility(View.VISIBLE);

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
            //add remaining items if any
            for (; i < texts.size(); i++)
                pItems.add(new TypeItem(texts.get(i), HomeType.TEXT_ITEM));
            for (; j < images.size(); j++)
                pItems.add(new TypeItem(images.get(j), HomeType.PHOTO_ITEM));

        }
        return pItems;
    }
}

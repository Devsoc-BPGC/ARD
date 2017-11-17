package com.macbitsgoa.ard.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.keys.AnnItemKeys;
import com.macbitsgoa.ard.keys.PostKeys;
import com.macbitsgoa.ard.models.AnnItem;
import com.macbitsgoa.ard.types.PostType;
import com.macbitsgoa.ard.utils.AHC;

import butterknife.BindView;
import butterknife.ButterKnife;

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
    public Toolbar toolbar;

    /**
     * Recyclerview used to display comments.
     */
    @BindView(R.id.recyclerView_content_post_details_comments)
    public RecyclerView recyclerView;

    /**
     * Frame that is used to display post content.
     */
    @BindView(R.id.frame_content_post_details_post)
    public FrameLayout frameLayoutPost;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);
        ButterKnife.bind(this);

        //https://developer.android.com/topic/libraries/support-library/preview/emoji-compat.html
        handleIntent();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    /**
     * From the data present in the intent extras, inflate views.
     */
    private void handleIntent() {
        final Intent intent = getIntent();
        final int type = intent.getIntExtra(PostKeys.TYPE, Integer.MIN_VALUE);
        switch (type) {
            case PostType.ANNOUNCEMENT: {
                final String key = intent.getStringExtra(AnnItemKeys.KEY);
                final AnnItem item = database.where(AnnItem.class)
                        .equalTo(AnnItemKeys.KEY, key)
                        .findFirst();
                if (item == null) {
                    finish();
                    Log.e(TAG, "Error loading post with key : Not found " + key);
                    Toast.makeText(this, "Error loading post", Toast.LENGTH_SHORT).show();
                }
                final View post = getLayoutInflater()
                        .inflate(R.layout.vh_ann_activity_item, frameLayoutPost);

                final String extras = item.getAuthor() + AHC.SEPARATOR
                        + AHC.getSimpleDate(item.getDate());
                ((TextView) post.findViewById(R.id.textView_viewHolder_ann_data))
                        .setText(Html.fromHtml(item.getData()));
                ((TextView) post.findViewById(R.id.textView_viewHolder_ann_extras))
                        .setText(Html.fromHtml(extras));
                (post.findViewById(R.id.cv_vh_ann_activity_item))
                        .setVisibility(View.INVISIBLE);
                toolbar.setTitle(getString(R.string.text_viewHolder_ann_data_default));
                break;
            }
            case PostType.HOME_ITEM: {

            }

        }
    }
}

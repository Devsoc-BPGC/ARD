package com.macbitsgoa.ard.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.keys.AnnItemKeys;
import com.macbitsgoa.ard.models.AnnItem;
import com.macbitsgoa.ard.utils.AHC;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

/**
 * Detail activity for Home posts.
 *
 * @author Vikramaditya Kukreja
 */
public class PostDetailsActivity extends BaseActivity implements View.OnClickListener {

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

    /**
     * Realm database to extract data.
     */
    private Realm database;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);
        ButterKnife.bind(this);

        //https://developer.android.com/topic/libraries/support-library/preview/emoji-compat.html

        database = Realm.getDefaultInstance();
        handleIntent();

        toolbar.setNavigationOnClickListener(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * From the data present in the intent extras, inflate views.
     */
    private void handleIntent() {
        final Intent intent = getIntent();
        //final int type = intent.getIntExtra(PostKeys.TYPE, -1);
        //switch (type) {
        //    case PostType.ANNOUNCEMENT: {
        final String key = intent.getStringExtra(AnnItemKeys.PRIMARY_KEY);
        final AnnItem item = database.where(AnnItem.class)
                .equalTo(AnnItemKeys.PRIMARY_KEY, key)
                .findFirst();
        if (item == null) {
            return;
        }
        final View post = getLayoutInflater()
                .inflate(R.layout.viewholder_home_fragment_ann, frameLayoutPost);

        final String extras = item.getAuthor() + AHC.SEPARATOR
                + AHC.getSimpleDate(item.getDate());
        ((TextView) post.findViewById(R.id.textView_viewHolder_ann_data))
                .setText(item.getData());
        ((TextView) post.findViewById(R.id.textView_viewHolder_ann_extras))
                .setText(extras);
        toolbar.setTitle(getString(R.string.text_viewHolder_ann_data_default));
        //        break;
        //    }
        //    default: {
        //        break;
        //    }
        //}
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        database.close();
    }

    @Override
    public void onClick(final View v) {
        //Since currently only toolbar has a listener set, id is not checked.
        onBackPressed();
    }
}

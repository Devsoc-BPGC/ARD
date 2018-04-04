package com.macbitsgoa.ard.viewholders;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.activities.ChatActivity;
import com.macbitsgoa.ard.keys.MessageItemKeys;
import com.macbitsgoa.ard.models.UserItem;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by vikramaditya on 29/10/17.
 */

public class NewChatViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.textView_vh_newchat_name)
    TextView name;

    @BindView(R.id.textView_vh_newchat_desc)
    TextView desc;

    @BindView(R.id.imgView_vh_newchat_desc)
    ImageView profile;

    private UserItem ui;
    private Activity activity;

    public NewChatViewHolder(final View itemView, final Activity activity) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.activity = activity;
        itemView.setOnClickListener(v -> {
            final Intent intent = new Intent(activity, ChatActivity.class);
            intent.putExtra("title", ui.getName());
            intent.putExtra(MessageItemKeys.OTHER_USER_ID, ui.getUid());
            intent.putExtra("photoUrl", ui.getPhotoUrl());
            activity.startActivity(intent);
            activity.finish();
        });
    }

    public void setUi(final UserItem ui) {
        this.ui = ui;
        name.setText(ui.getName());
        desc.setText(ui.getDesc());
        Glide.with(activity)
                .load(ui.getPhotoUrl())
                .apply(RequestOptions
                        .circleCropTransform()
                        .error(R.drawable.ic_contact))
                .transition(DrawableTransitionOptions.withCrossFade(300))
                .into(profile);
    }
}

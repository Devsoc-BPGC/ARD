package com.macbitsgoa.ard.viewholders;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.activities.ChatActivity;
import com.macbitsgoa.ard.models.ChatsItem;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatsViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.imageView_vh_cf_chats_dp)
    public ImageView image;

    @BindView(R.id.textView_vh_cf_chats_name)
    public TextView name;

    @BindView(R.id.textView_vh_cf_chats_latest)
    public TextView latest;

    @BindView(R.id.tv_cf_chats_icon_text)
    public TextView update;

    @BindView(R.id.imageView_cf_chats_icon)
    public ImageView updateIcon;

    @BindView(R.id.textView_vh_cf_chats_time)
    public TextView time;

    public ChatsItem item;

    public ChatsViewHolder(View itemView, final Context context) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("title", item.getName());
                intent.putExtra("senderId", item.getId());
                intent.putExtra("photoUrl", item.getPhotoUrl());
                context.startActivity(intent);
            }
        });
    }
}

package com.macbitsgoa.ard.viewholders;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.activities.ChatActivity;
import com.macbitsgoa.ard.models.ChatsItem;

/**
 * Created by vikramaditya on 21/10/17.
 */

public class ChatsViewHolder extends RecyclerView.ViewHolder {
    public ImageView image;
    public TextView name;
    public TextView latest;
    public TextView time;
    public ChatsItem item;

    public ChatsViewHolder(View itemView, final Context context) {
        super(itemView);
        image = (ImageView) itemView.findViewById(R.id.imageView_vh_cf_chats_dp);
        name = (TextView) itemView.findViewById(R.id.textView_vh_cf_chats_name);
        latest = (TextView) itemView.findViewById(R.id.textView_vh_cf_chats_latest);
        time = (TextView) itemView.findViewById(R.id.textView_vh_cf_chats_time);
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

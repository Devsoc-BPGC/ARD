package com.macbitsgoa.ard.viewholders;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.activities.ChatActivity;
import com.macbitsgoa.ard.keys.ChatItemKeys;
import com.macbitsgoa.ard.keys.MessageItemKeys;
import com.macbitsgoa.ard.models.ChatsItem;
import com.macbitsgoa.ard.models.MessageItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

public class ChatsViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener, View.OnLongClickListener {
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

    private Context context;

    public ChatsViewHolder(View itemView, final Context context) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.context = context;
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("title", item.getName());
        intent.putExtra("senderId", item.getId());
        intent.putExtra("photoUrl", item.getPhotoUrl());
        context.startActivity(intent);
    }

    @Override
    public boolean onLongClick(final View v) {
        AlertDialog ad = new AlertDialog.Builder(context)
                .setTitle("Select action")
                .setCancelable(true)
                .setItems(new String[]{"Delete",}, (dialog, which) -> {
                    Realm database = Realm.getDefaultInstance();
                    database.executeTransaction(realm -> {
                        RealmResults<MessageItem> mis = realm.where(MessageItem.class).equalTo(MessageItemKeys.SENDER_ID, item.getId()).findAll();
                        for (MessageItem mi : mis) mi.deleteFromRealm();
                        ChatsItem ci = realm.where(ChatsItem.class).equalTo(ChatItemKeys.DB_ID, item.getId()).findFirst();
                        if (ci == null) return;
                        ci.deleteFromRealm();
                    });
                    database.close();
                })
                .show();
        return true;
    }
}

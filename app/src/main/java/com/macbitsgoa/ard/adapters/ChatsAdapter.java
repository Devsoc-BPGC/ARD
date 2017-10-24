package com.macbitsgoa.ard.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.models.ChatsItem;
import com.macbitsgoa.ard.viewholders.ChatsViewHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.realm.RealmResults;

/**
 * Created by vikramaditya on 21/10/17.
 */

public class ChatsAdapter extends RecyclerView.Adapter<ChatsViewHolder> {

    private RealmResults<ChatsItem> chats;
    private Context context;

    public ChatsAdapter(@Nullable final RealmResults<ChatsItem> chats, @Nonnull Context context) {
        this.chats = chats;
        this.context = context;
    }

    @Override
    public ChatsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.vh_chat_fragment_chats, parent, false);
        return new ChatsViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(ChatsViewHolder holder, int position) {
        holder.item = chats.get(position);
        Glide.with(context)
                .load(holder.item.getPhotoUrl())
                .apply(RequestOptions.circleCropTransform())
                .into(holder.image);
        holder.time.setText(holder.item.getUpdate().toString().substring(0, 4));
        holder.name.setText(holder.item.getName());
        holder.latest.setText(holder.item.getLatest());
    }

    @Override
    public int getItemCount() {
        return chats != null ? chats.size() : 0;
    }
}

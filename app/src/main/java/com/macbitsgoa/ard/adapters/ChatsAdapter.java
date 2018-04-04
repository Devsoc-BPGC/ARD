package com.macbitsgoa.ard.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.models.ChatsItem;
import com.macbitsgoa.ard.utils.AHC;
import com.macbitsgoa.ard.viewholders.ChatsViewHolder;

import javax.annotation.Nonnull;

import io.realm.RealmResults;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsViewHolder> {

    private RealmResults<ChatsItem> chats;
    private Context context;

    public ChatsAdapter(@NonNull final RealmResults<ChatsItem> chats,
                        @Nonnull final Context context) {
        this.chats = chats;
        this.context = context;
    }

    @NonNull
    @Override
    public ChatsViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view = inflater.inflate(R.layout.vh_chat_fragment_chats,
                parent, false);
        return new ChatsViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatsViewHolder holder, final int position) {
        holder.item = chats.get(position);
        Glide.with(context)
                .load(holder.item.getPhotoUrl())
                .apply(RequestOptions
                        .circleCropTransform()
                        .error(R.drawable.ic_contact))
                .transition(DrawableTransitionOptions.withCrossFade(300))
                .into(holder.image);
        holder.time.setText(AHC.getSimpleDayOrTime(holder.item.getUpdate()));
        holder.name.setText(holder.item.getName());
        holder.latest.setText(holder.item.getLatest());
        if (holder.item.getUnreadCount() == 0) {
            holder.update.setVisibility(View.INVISIBLE);
            holder.updateIcon.setVisibility(View.INVISIBLE);
        } else {
            holder.update.setVisibility(View.VISIBLE);
            holder.updateIcon.setVisibility(View.VISIBLE);
            final int updateCount = holder.item.getUnreadCount();
            holder.update.setText(updateCount > 99 ? "99+" : "" + updateCount);
        }
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }
}

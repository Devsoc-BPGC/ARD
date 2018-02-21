package com.macbitsgoa.ard.adapters;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.models.UserItem;
import com.macbitsgoa.ard.viewholders.NewChatViewHolder;
import com.macbitsgoa.ard.viewholders.TextViewHolder;

import java.util.List;

import io.realm.RealmResults;

/**
 * Created by vikramaditya on 29/10/17.
 */

public class NewChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<UserItem> admins;
    private List<UserItem> users;
    private Activity context;

    /**
     * Used for title "Admin" and "User"
     */
    private static final int TEXT = 0;
    /**
     * This user is not to be confused with users/admins.
     * It is a key pointing to the fact that current item is a user, whether admin or not.
     */
    private static final int USER = 1;

    public NewChatAdapter(final List<UserItem> admins,
                          final List<UserItem> users,
                          final Activity context) {
        this.admins = admins;
        this.users = users;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TEXT)
            return new TextViewHolder(inflater.inflate(R.layout.vh_text, parent, false), R.id.tv_vh_text);
        return new NewChatViewHolder(inflater.inflate(R.layout.vh_newchat, parent, false), context);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder generalHolder, final int position) {
        if (generalHolder.getItemViewType() == TEXT) {
            final TextViewHolder holder = (TextViewHolder) generalHolder;
            if (position == admins.size() + 1)
                holder.setText("Users");
            else holder.setText("Admins");
        } else {
            final NewChatViewHolder holder = (NewChatViewHolder) generalHolder;
            UserItem ui;
            if (position <= admins.size()) ui = admins.get(position - 1);
            else ui = users.get(position - admins.size() - 2);
            holder.setUi(ui);
        }
    }

    @Override
    public int getItemCount() {
        int size = 1 + admins.size();
        size += !users.isEmpty()? 1 + users.size() : 0;
        return size;
    }

    @Override
    public int getItemViewType(final int position) {
        if (position == 0 || position == admins.size() + 1) return TEXT;
        else return USER;
    }
}

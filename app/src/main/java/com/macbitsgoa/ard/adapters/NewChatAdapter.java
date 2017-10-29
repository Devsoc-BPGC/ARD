package com.macbitsgoa.ard.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.models.UserItem;
import com.macbitsgoa.ard.viewholders.NewChatViewHolder;
import com.macbitsgoa.ard.viewholders.TextViewHolder;

import io.realm.RealmResults;

/**
 * Created by vikramaditya on 29/10/17.
 */

public class NewChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private RealmResults<UserItem> admins;
    private RealmResults<UserItem> users;
    private Activity context;

    private final int TEXT = 0;
    private final int USER = 1;


    public NewChatAdapter(final RealmResults<UserItem> admins,
                          final RealmResults<UserItem> users,
                          final Activity context) {
        this.admins = admins;
        this.users = users;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TEXT)
            return new TextViewHolder(inflater.inflate(R.layout.vh_text, parent, false), R.id.tv_vh_text);
        return new NewChatViewHolder(inflater.inflate(R.layout.vh_newchat, parent, false), context);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder generalHolder, int position) {
        if (generalHolder.getItemViewType() == TEXT) {
            TextViewHolder holder = (TextViewHolder) generalHolder;
            if (position == admins.size() + 1)
                holder.text.setText("User(s)");
            else holder.text.setText("Admin(s)");
        } else {
            NewChatViewHolder holder = (NewChatViewHolder) generalHolder;
            UserItem ui;
            if (position <= admins.size()) ui = admins.get(position - 1);
            else ui = users.get(position - admins.size() - 2);
            holder.setUi(ui);
        }
    }


    @Override
    public int getItemCount() {
        return 1 + admins.size() + 1 + users.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 || position == admins.size() + 1) return TEXT;
        else return USER;
    }
}

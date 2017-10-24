package com.macbitsgoa.ard.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.models.MessageItem;
import com.macbitsgoa.ard.viewholders.ChatMsgViewHolder;

import io.realm.RealmResults;

public class ChatMsgAdapter extends RecyclerView.Adapter<ChatMsgViewHolder> {

    private final int RECEIVER = 0;
    private final int SENDER = 1;
    private RealmResults<MessageItem> messages;

    public ChatMsgAdapter(final RealmResults<MessageItem> messageItems) {
        this.messages = messageItems;
    }

    @Override
    public ChatMsgViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = null;
        if (viewType == RECEIVER)
            view = inflater.inflate(R.layout.vh_activity_chat_chatmsg_receiver, parent, false);
        else if (viewType == SENDER)
            view = inflater.inflate(R.layout.vh_activity_chat_chatmsg_sender, parent, false);
        return new ChatMsgViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatMsgViewHolder holder, int position) {
        holder.message.setText(messages.get(position).getMessageData());
        holder.time.setText(messages.get(position).getMessageTime().toString().substring(0, 4));
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).isRcvd() ? RECEIVER : SENDER;
    }

    @Override
    public int getItemCount() {
        Log.e("TAG", "size is " + messages.size());
        return messages.size();
    }
}

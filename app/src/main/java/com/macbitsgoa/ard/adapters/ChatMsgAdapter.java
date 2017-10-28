package com.macbitsgoa.ard.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.models.MessageItem;
import com.macbitsgoa.ard.types.MessageStatusType;
import com.macbitsgoa.ard.utils.AHC;
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
        holder.time.setText(AHC.getSimpleDayAndTime(messages.get(position).getMessageTime()));
        holder.message.setText(messages.get(position).getMessageData());
        if (holder.getItemViewType() == RECEIVER) {
            switch (messages.get(position).getMessageStatus()) {
                case MessageStatusType.MSG_READ:
                    holder.status.setImageResource(R.drawable.ic_double_tick);
                    holder.status.setColorFilter(Color.parseColor("#03A9F4"));
                    break;
                case MessageStatusType.MSG_RCVD:
                    holder.status.setImageResource(R.drawable.ic_double_tick);
                    holder.status.setColorFilter(Color.GRAY);
                    break;
                case MessageStatusType.MSG_SENT:
                    holder.status.setImageResource(R.drawable.ic_single_tick);
                    holder.status.setColorFilter(Color.GRAY);
                    break;
                default:
                    holder.status.setImageResource(R.drawable.ic_wait);
                    holder.status.setColorFilter(Color.GRAY);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).isMessageRcvd() ? SENDER : RECEIVER;
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}

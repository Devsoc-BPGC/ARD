package com.macbitsgoa.ard.adapters;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.models.MessageItem;
import com.macbitsgoa.ard.viewholders.ChatMsgViewHolder;

import io.realm.RealmResults;

import static com.macbitsgoa.ard.utils.AHC.getScreenWidth;
import static com.macbitsgoa.ard.viewholders.ChatMsgViewHolder.MAX_WIDTH_FRACTION;

public class ChatMsgAdapter extends RecyclerView.Adapter<ChatMsgViewHolder> {

    public static final int RECEIVER = 0;
    public static final int SENDER = 1;

    private RealmResults<MessageItem> messages;

    public ChatMsgAdapter(final RealmResults<MessageItem> messageItems) {
        this.messages = messageItems;
    }

    @Override
    public ChatMsgViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view = inflater.inflate(R.layout.vh_activity_chat_chatmsg, parent, false);

        final LinearLayout root = view.findViewById(R.id.ll_chatmsg_root);
        final LinearLayout msgBox = view.findViewById(R.id.ll_chatmsg_msg_box);
        if (viewType == RECEIVER) {
            root.setGravity(Gravity.END);
            msgBox.setBackground(ContextCompat.getDrawable(parent.getContext(), R.drawable.bg_chat_rcv));
        } else if (viewType == SENDER) {
            root.setGravity(Gravity.START);
            msgBox.setBackground(ContextCompat.getDrawable(parent.getContext(), R.drawable.bg_chat_sen));
        }

        final TextView messageTv = view.findViewById(R.id.textView_viewHolder_chatmsg_msg);
        final int msgMaxWidth = ((int) (getScreenWidth() * MAX_WIDTH_FRACTION));
        messageTv.setMaxWidth(msgMaxWidth);

        return new ChatMsgViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ChatMsgViewHolder holder, final int position) {
        holder.populate(messages.get(position));
    }

    @Override
    public int getItemViewType(final int position) {
        return messages.get(position).isMessageRcvd() ? SENDER : RECEIVER;
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}

package com.macbitsgoa.ard.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.macbitsgoa.ard.R;

public class ChatMsgViewHolder extends RecyclerView.ViewHolder {
    public TextView time;
    public TextView message;

    public ChatMsgViewHolder(View itemView) {
        super(itemView);
        time = (TextView) itemView.findViewById(R.id.textView_viewHolder_chatmsg_time);
        message = (TextView) itemView.findViewById(R.id.textView_viewHolder_chatmsg_msg);
    }
}

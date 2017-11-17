package com.macbitsgoa.ard.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.macbitsgoa.ard.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatMsgViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.textView_viewHolder_chatmsg_time)
    public TextView time;

    @BindView(R.id.textView_viewHolder_chatmsg_msg)
    public TextView message;

    @BindView(R.id.imgView_vh_activity_chatmsg_tick)
    public ImageView status;

    public ChatMsgViewHolder(final View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}

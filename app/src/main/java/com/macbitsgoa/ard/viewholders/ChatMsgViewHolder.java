package com.macbitsgoa.ard.viewholders;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.adapters.ChatMsgAdapter;
import com.macbitsgoa.ard.models.MessageItem;
import com.macbitsgoa.ard.types.MessageStatusType;
import com.macbitsgoa.ard.utils.AHC;

public class ChatMsgViewHolder extends RecyclerView.ViewHolder {

    /**
     * Maximum width fraction of {@link #message} of screen size.
     */
    public static final float MAX_WIDTH_FRACTION = 0.6f;

    private TextView time;

    private TextView message;

    private ImageView status;

    public ChatMsgViewHolder(final View itemView) {
        super(itemView);
        bindViews(itemView);
    }

    private void bindViews(@NonNull final View view) {
        time = view.findViewById(R.id.textView_viewHolder_chatmsg_time);
        message = view.findViewById(R.id.textView_viewHolder_chatmsg_msg);
        status = view.findViewById(R.id.imgView_vh_activity_chatmsg_tick);
    }

    public void populate(@NonNull final MessageItem messageItem) {
        time.setText(AHC.getSimpleDayAndTime(messageItem.getMessageTime()));
        message.setText(messageItem.getMessageData());

        if (getItemViewType() == ChatMsgAdapter.RECEIVER) {
            final Context context = status.getContext();
            switch (messageItem.getMessageStatus()) {
                case MessageStatusType.MSG_READ:
                    status.setImageResource(R.drawable.ic_double_tick);
                    status.setContentDescription(context.getString(R.string.msg_read));
                    status.setColorFilter(Color.parseColor("#03A9F4"));
                    break;
                case MessageStatusType.MSG_RCVD:
                    status.setImageResource(R.drawable.ic_double_tick);
                    status.setContentDescription(context.getString(R.string.msg_rcvd));
                    status.setColorFilter(Color.GRAY);
                    break;
                case MessageStatusType.MSG_SENT:
                    status.setImageResource(R.drawable.ic_single_tick);
                    status.setContentDescription(context.getString(R.string.msg_sent));
                    status.setColorFilter(Color.GRAY);
                    break;
                default:
                    status.setImageResource(R.drawable.ic_wait);
                    status.setContentDescription(context.getString(R.string.msg_wait));
                    status.setColorFilter(Color.GRAY);
                    break;
            }
        } else if (getItemViewType() == ChatMsgAdapter.SENDER) {
            status.setVisibility(View.GONE);
        }
    }
}

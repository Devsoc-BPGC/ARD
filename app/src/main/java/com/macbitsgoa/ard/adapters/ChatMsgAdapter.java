package com.macbitsgoa.ard.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.models.MessageItem;
import com.macbitsgoa.ard.types.MessageType;
import com.macbitsgoa.ard.utils.AHC;
import com.macbitsgoa.ard.viewholders.ChatMsgViewHolder;
import com.macbitsgoa.ard.viewholders.ImageViewHolder;

import java.security.Permissions;

import io.realm.RealmResults;

import static com.macbitsgoa.ard.viewholders.ChatMsgViewHolder.MAX_WIDTH_FRACTION;

public class ChatMsgAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ImageViewHolder.ImageClickListener {

    public static final int RECEIVER = 0;
    public static final int SENDER = 1;
    public static final int DOCUMENT = 2;

    private RealmResults<MessageItem> messages;

    private Context context;

    public ChatMsgAdapter(final RealmResults<MessageItem> messageItems, final Context context) {
        this.messages = messageItems;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == RECEIVER || viewType == SENDER) {
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
            final int msgMaxWidth = (int) (AHC.getScreenWidth() * MAX_WIDTH_FRACTION);
            messageTv.setMaxWidth(msgMaxWidth);

            return new ChatMsgViewHolder(view);
        } else {
            final View view = inflater.inflate(R.layout.vh_chat_document, parent, false);
            return new ImageViewHolder(view, context, R.id.imgView_vh_chat_document, this);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder.getItemViewType() == RECEIVER || holder.getItemViewType() == SENDER) {
            ((ChatMsgViewHolder) holder).populate(messages.get(position));
        } else {
            if (messages.get(position).getLocalUri() != null)
                ((ImageViewHolder) holder).setImage(Uri.parse(messages.get(position).getLocalUri()));
            else
                ((ImageViewHolder) holder).setImage(Uri.parse(messages.get(position).getMessageData()));
        }

    }

    @Override
    public int getItemViewType(final int position) {
        if (messages.get(position).getMessageType() == MessageType.DOCUMENT) return DOCUMENT;
        return messages.get(position).isMessageRcvd() ? SENDER : RECEIVER;
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public void onImageClick(final Uri uri) {
        //nothing needed as of now
        if (uri == null) {
            Log.e("tag", "null uri for image");
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, AHC.getMimeType(context, uri));
        context.startActivity(intent);
    }
}

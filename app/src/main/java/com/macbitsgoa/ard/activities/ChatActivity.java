package com.macbitsgoa.ard.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.adapters.ChatMsgAdapter;
import com.macbitsgoa.ard.keys.ChatItemKeys;
import com.macbitsgoa.ard.models.ChatsItem;
import com.macbitsgoa.ard.models.MessageItem;
import com.macbitsgoa.ard.services.MessagingService;
import com.macbitsgoa.ard.services.NotifyService;
import com.macbitsgoa.ard.services.SendService;
import com.macbitsgoa.ard.types.MessageStatusType;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedCollectionChangeSet;
import io.realm.RealmResults;
import io.realm.Sort;

public class ChatActivity extends BaseActivity {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    DatabaseReference writeRef = getRootReference().child("chats");
    DatabaseReference onlineStatus = getRootReference().child("online");
    DatabaseReference myStatus = onlineStatus.child(user.getUid());
    DatabaseReference theirStatus;
    DatabaseReference readStatus = writeRef;

    String sessionId;

    @BindView(R.id.recyclerView_activity_chat)
    RecyclerView chatsRV;

    @BindView(R.id.ll_frame_chat_toolbar_icon)
    ImageView icon;

    @BindView(R.id.tv_frame_chat_toolbar_title)
    TextView title;

    @BindView(R.id.tv_frame_chat_toolbar_subtitle)
    TextView subtitle;

    @BindView(R.id.editText_frame_comment_message)
    EditText message;

    @BindView(R.id.fab_frame_comment_send)
    FloatingActionButton sendFab;

    @BindView(R.id.rl_activity_chat_icon)
    RelativeLayout rlIcons;

    @BindView(R.id.rl_activity_chat_updates)
    RelativeLayout rlUpdates;

    @BindView(R.id.tv_activity_chat_number)
    TextView updateNumber;

    private String senderId = null;

    ChatMsgAdapter chatMsgAdapter;

    MessagingService messagingService;

    NotificationManagerCompat nmc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        senderId = getIntent().getStringExtra("senderId");
        if (senderId == null || user == null) return;
        theirStatus = onlineStatus.child(senderId);
        writeRef = writeRef.child(senderId).child(ChatItemKeys.PRIVATE_MESSAGES).child(user.getUid());
        readStatus = writeRef.child(ChatItemKeys.MESSAGE_STATUS);

        ButterKnife.bind(this);
        setupUI();
        nmc = NotificationManagerCompat.from(this);
        startService(new Intent(this, MessagingService.class));
        theirStatus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    subtitle.setVisibility(View.GONE);
                } else {
                    subtitle.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Setup ui window and toolbar.
     */
    private void setupUI() {
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.green_900));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.green_900));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        title.setText(getIntent().getStringExtra("title"));
        Glide.with(this)
                .load(getIntent().getStringExtra("photoUrl"))
                .apply(RequestOptions.circleCropTransform())
                .into(icon);
    }

    /**
     * Update unread message count.
     * //TODO fix counts maybe?
     */
    private void updateCounts() {
        //Remove any unread count if present
        database.executeTransactionAsync(r -> {
            ChatsItem chat = r.where(ChatsItem.class)
                    .equalTo("id", senderId).findFirst();
            if (chat == null) {
                chat = r.createObject(ChatsItem.class, senderId);
                chat.setLatest("");
                chat.setUpdate(null);
                chat.setName(getIntent().getStringExtra("title"));
                chat.setPhotoUrl(getIntent().getStringExtra("photoUrl"));
            }
            chat.setUnreadCount(0);

            RealmResults<MessageItem> unreadMessages = r.where(MessageItem.class)
                    .equalTo("messageRcvd", true)
                    .lessThanOrEqualTo("messageStatus", MessageStatusType.MSG_RCVD)
                    .findAll();
            for (MessageItem mi : unreadMessages) {
                mi.setMessageStatus(MessageStatusType.MSG_READ);
                Intent notifyIntent = new Intent(ChatActivity.this, NotifyService.class);
                notifyIntent.putExtra("receiverId", mi.getSenderId());
                notifyIntent.putExtra("messageId", mi.getMessageId());
                startService(notifyIntent);
            }
        });
    }

    RealmResults<MessageItem> messages;

    @Override
    protected void onStart() {
        super.onStart();
        sessionId = Calendar.getInstance().getTime().toString();
        myStatus = onlineStatus.child(user.getUid()).child(sessionId);
        myStatus.setValue(true);
        myStatus.onDisconnect().removeValue();

        final LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        chatsRV.setLayoutManager(llm);
        chatsRV.setHasFixedSize(true);

        updateCounts();

        messages = database
                .where(MessageItem.class)
                .equalTo("senderId", senderId)
                .findAllSorted("messageRcvdTime", Sort.DESCENDING);
        messages.addChangeListener((messageItems, changeSet) -> {
            // `null`  means the async query returns the first time.
            if (changeSet == null) {
                chatMsgAdapter.notifyDataSetChanged();
                return;
            }

            OrderedCollectionChangeSet.Range[] modifications = changeSet.getChangeRanges();
            for (OrderedCollectionChangeSet.Range range : modifications) {
                //chatMsgAdapter.notifyItemRangeChanged(range.startIndex, range.length);
            }
            OrderedCollectionChangeSet.Range[] additions = changeSet.getInsertionRanges();
            for (OrderedCollectionChangeSet.Range range : modifications) {
                //chatMsgAdapter.notifyItemRangeInserted(range.startIndex, range.length);
            }
            chatMsgAdapter.notifyDataSetChanged();
            //Cancel any ongoing notification from this user
            nmc.cancel(senderId.hashCode());

            //TODO update read status of new messages somewhere


            if (llm.findFirstCompletelyVisibleItemPosition() < 5) {
                chatsRV.scrollToPosition(0);
                rlIcons.setVisibility(View.GONE);
                updateNumber.setText("0");
                rlUpdates.setVisibility(View.GONE);
            }
        });

        chatMsgAdapter = new ChatMsgAdapter(messages);
        chatsRV.setAdapter(chatMsgAdapter);

        //TODO improve this
          /*  chatsRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_SETTLING || newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    LinearLayoutManager llm = (LinearLayoutManager) recyclerView.getLayoutManager();
                    int pos = llm.findFirstCompletelyVisibleItemPosition();
                    if (pos > 5) rlIcons.setVisibility(View.VISIBLE);
                    else {
                        rlIcons.setVisibility(View.GONE);
                        rlUpdates.setVisibility(View.GONE);
                    }
                }

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    LinearLayoutManager llm = (LinearLayoutManager) recyclerView.getLayoutManager();
                    int pos = llm.findFirstCompletelyVisibleItemPosition();
                    if (pos > 5) rlIcons.setVisibility(View.VISIBLE);
                    else {
                        rlIcons.setVisibility(View.GONE);
                        rlUpdates.setVisibility(View.GONE);
                    }
                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    */
    }

    @Override
    protected void onStop() {
        super.onStop();
        messages.removeAllChangeListeners();
        myStatus.removeValue();
    }

    @Override
    protected void onResume() {
        super.onResume();
        nmc.cancel(senderId.hashCode());
    }

    //TODO fix read status
    @Override
    public void onClick(View v) {
        super.onClick(v);
        int viewId = v.getId();
        if (viewId == R.id.ll_frame_chat_toolbar_icons) {
            onBackPressed();
        } else if (viewId == R.id.fab_activity_chat_scroll) {
            chatsRV.scrollToPosition(0);
            rlIcons.setVisibility(View.GONE);
            rlUpdates.setVisibility(View.GONE);
        } else if (viewId == R.id.fab_frame_comment_send) {
            //Get user message from edittext
            final String messageData = message.getText().toString().trim();
            //If length of EditText is 0, do nothing
            if (messageData.length() == 0) return;

            Intent mIntent = new Intent(this, SendService.class);
            mIntent.putExtra("messageData", messageData);
            mIntent.putExtra("receiverId", senderId);
            startService(mIntent);
            updateCounts();

            //Clear EditText after extracting it's value
            message.getText().clear();

            chatsRV.scrollToPosition(0);

            rlUpdates.setVisibility(View.GONE);
            rlIcons.setVisibility(View.GONE);
        }
    }
}

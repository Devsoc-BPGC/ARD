package com.macbitsgoa.ard.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import com.macbitsgoa.ard.models.ChatsItem;
import com.macbitsgoa.ard.models.MessageItem;
import com.macbitsgoa.ard.services.MessagingService;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        if (!getIntent().hasExtra("senderId")) {
            /*
            This activity was somehow launched without the sender id and therefore should be
            finished.
             */
            finish();
        } else {
            senderId = getIntent().getStringExtra("senderId");
            theirStatus = onlineStatus.child(senderId);
            writeRef = writeRef.child(senderId).child("0").child(user.getUid());
            readStatus = writeRef.child("messageStatus");
        }

        ButterKnife.bind(this);

        /*
          Setup up UI window
         */
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.green_900));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.green_900));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        title.setText(getIntent().getStringExtra("title"));
        Glide.with(this)
                .load(getIntent().getStringExtra("photoUrl"))
                .apply(RequestOptions.circleCropTransform())
                .into(icon);

        chatsRV.setHasFixedSize(true);
        final LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        llm.findFirstCompletelyVisibleItemPosition();
        chatsRV.setLayoutManager(llm);

        updateCounts();

        final RealmResults<MessageItem> messages = database.where(MessageItem.class)
                .equalTo("senderId", senderId)
                .findAllSorted("messageRcvdTime", Sort.DESCENDING);
        messages.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<MessageItem>>() {
            @Override
            public void onChange(RealmResults<MessageItem> messageItems, OrderedCollectionChangeSet changeSet) {
                // `null`  means the async query returns the first time.
                if (changeSet == null||true) {
                    chatMsgAdapter.notifyDataSetChanged();
                    return;
                }

                OrderedCollectionChangeSet.Range[] modifications = changeSet.getChangeRanges();
                for (OrderedCollectionChangeSet.Range range : modifications) {
                   // chatMsgAdapter.notifyItemRangeChanged(range.startIndex, range.length);
                }

                if (llm.findFirstCompletelyVisibleItemPosition() == 0) {
                    chatsRV.scrollToPosition(0);
                    rlIcons.setVisibility(View.GONE);
                    updateNumber.setText("0");
                    rlUpdates.setVisibility(View.GONE);
                }

            }
        });


        chatMsgAdapter = new ChatMsgAdapter(messages);
        chatsRV.setAdapter(chatMsgAdapter);
        chatsRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
        ;

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

    private void updateCounts() {
        //Remove any unread count if present
        final ChatsItem chat = database.where(ChatsItem.class)
                .equalTo("id", senderId).findFirst();
        if (chat == null) {
            finish();
        } else {
            database.beginTransaction();
            chat.setUnreadCount(0);
            database.commitTransaction();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        sessionId = Calendar.getInstance().getTime().toString();
        myStatus = onlineStatus.child(user.getUid()).child(sessionId);
        myStatus.setValue(true);
        myStatus.onDisconnect().removeValue();
    }

    @Override
    protected void onStop() {
        super.onStop();
        myStatus.removeValue();
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

            if (messagingService == null || !MessagingService.isInstanceRunning()) {
                startService(new Intent(this, MessagingService.class));
                messagingService = MessagingService.getInstance();
            }
            messagingService.sendMessage(messageData, senderId);

            //Clear EditText after extracting it's value
            message.getText().clear();

            chatMsgAdapter.notifyItemInserted(0);
            chatsRV.scrollToPosition(0);

            rlUpdates.setVisibility(View.GONE);
            rlIcons.setVisibility(View.GONE);
        }
    }

}

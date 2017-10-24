package com.macbitsgoa.ard.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class ChatActivity extends BaseActivity {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    DatabaseReference readRef = getRootReference().child("chats").child(user.getUid());
    DatabaseReference writeRef = getRootReference().child("chats");

    @BindView(R.id.recyclerView_activity_chat)
    RecyclerView chatsRV;

    @BindView(R.id.ll_frame_chat_toolbar_icon)
    ImageView icon;

    @BindView(R.id.tv_frame_chat_toolbar_title)
    TextView title;

    @BindView(R.id.editText_frame_comment_message)
    EditText message;

    @BindView(R.id.fab_frame_comment_send)
    FloatingActionButton sendFab;

    ValueEventListener messagesListener;

    private String senderId = null;

    Realm database;
    ChatMsgAdapter chatMsgAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        if (!getIntent().hasExtra("senderId")) {
            finish();
        } else {
            senderId = getIntent().getStringExtra("senderId");
            readRef = readRef.child("0").child(senderId);
            writeRef = writeRef.child(senderId).child("0").child(user.getUid());
        }

        ButterKnife.bind(this);

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.green_900));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.green_900));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        title.setText(getIntent().getStringExtra("title"));
        Glide.with(this)
                .load(getIntent().getStringExtra("photoUrl"))
                .apply(RequestOptions.circleCropTransform())
                .into(icon);

        chatsRV.setHasFixedSize(true);
        chatsRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));
        database = Realm.getDefaultInstance();
        RealmResults<MessageItem> messages = database.where(MessageItem.class)
                .equalTo("senderId", senderId)
                .findAllSorted("messageTime", Sort.DESCENDING);
        chatMsgAdapter = new ChatMsgAdapter(messages);
        chatsRV.setAdapter(chatMsgAdapter);
        messagesListener = returnMessageListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        readRef.addValueEventListener(messagesListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        readRef.removeEventListener(messagesListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        database.close();
    }

    public void hideKeyboardFrom(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int viewId = v.getId();
        if (viewId == R.id.ll_frame_chat_toolbar_icons) {
            onBackPressed();
        } else if (viewId == R.id.fab_frame_comment_send) {
            hideKeyboardFrom(v);
            String messageData = message.getText().toString().trim();
            if (messageData.length() == 0) return;
            Date messageTime = Calendar.getInstance().getTime();
            String messageId = "" + messageTime.getTime() + messageTime.hashCode() + messageData.hashCode();

            String latestMessage = messageData.substring(0, messageData.length() % 50);

            message.getText().clear();

            //Add new message
            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("data", messageData);
            messageMap.put("date", messageTime);

            //Update latest sender information
            Map<String, Object> senderMap = new HashMap<>();
            senderMap.put("id", user.getUid());
            senderMap.put("name", user.getDisplayName());
            senderMap.put("latest", latestMessage);
            senderMap.put("photoUrl", user.getPhotoUrl().toString());
            senderMap.put("date", messageTime);

            writeRef.child("sender").setValue(senderMap);
            writeRef.child("messages").child(messageId).setValue(messageMap);

            database.beginTransaction();
            MessageItem mi = database.createObject(MessageItem.class);
            mi.setRcvd(false);
            mi.setMessageTime(messageTime);
            mi.setMessageId(messageId);
            mi.setMessageData(messageData);
            mi.setSenderId(senderId);
            database.commitTransaction();

            chatMsgAdapter.notifyItemInserted(0);
            chatsRV.smoothScrollToPosition(0);
        }
    }

    public ValueEventListener returnMessageListener() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ChatsItem ci = database.where(ChatsItem.class)
                        .equalTo("id", senderId)
                        .findFirst();
                Queue<DataSnapshot> shots = new LinkedList<>();
                for (DataSnapshot childShot : dataSnapshot.child("messages").getChildren()) {
                    shots.add(childShot);
                }
                while (!shots.isEmpty()) {
                    DataSnapshot childShot = shots.poll();

                    String messageData = childShot.child("data").getValue(String.class);
                    Date messageTime = childShot.child("date").getValue(Date.class);
                    String messageId = childShot.getKey();
                    MessageItem mi = null;
                    database.beginTransaction();
                    if (mi == null)
                        mi = database.createObject(MessageItem.class);
                    mi.setSenderId(senderId);
                    mi.setMessageData(messageData);
                    mi.setMessageId(messageId);
                    mi.setRcvd(true);
                    mi.setMessageTime(messageTime);
                    database.commitTransaction();

                    database.beginTransaction();
                    ci.setLatest(messageData);
                    ci.setUpdate(messageTime);
                    database.commitTransaction();
                    chatMsgAdapter.notifyItemInserted(0);

                    readRef.child("messages").child(messageId).removeValue();
                }
                //update latest sender info
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }
}

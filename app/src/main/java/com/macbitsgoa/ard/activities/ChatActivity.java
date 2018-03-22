package com.macbitsgoa.ard.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.adapters.ChatMsgAdapter;
import com.macbitsgoa.ard.keys.ChatItemKeys;
import com.macbitsgoa.ard.keys.MessageItemKeys;
import com.macbitsgoa.ard.models.ChatsItem;
import com.macbitsgoa.ard.models.MessageItem;
import com.macbitsgoa.ard.services.MessagingService;
import com.macbitsgoa.ard.services.NotifyService;
import com.macbitsgoa.ard.services.SendDocumentService;
import com.macbitsgoa.ard.services.SendService;
import com.macbitsgoa.ard.utils.AHC;
import com.macbitsgoa.ard.utils.CenterCropDrawable;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.OrderedCollectionChangeSet;
import io.realm.RealmResults;
import io.realm.Sort;

public class ChatActivity extends BaseActivity {

    //----------------------------------------------------------------------------------------------

    /**
     * TAG for class.
     */
    public static final String TAG = ChatActivity.class.getSimpleName();

    /**
     * Request code for selecting a document from Files API to share via chat.
     */
    private static final int DOCUMENT_READ_REQUEST_CODE = 452;

    //----------------------------------------------------------------------------------------------
    public static boolean visible = false;
    public static String otherUserId = null;
    /**
     * Reference to the "chats" node on Firebase.
     */
    DatabaseReference chatsReference = getRootReference().child(AHC.FDR_CHAT);
    /**
     * Reference to "online" node on Firebase.
     */
    DatabaseReference onlineStatus = getRootReference().child(AHC.FDR_ONLINE);
    /**
     * Get current user's online status reference.
     */
    DatabaseReference myStatus = onlineStatus.child(getUser().getUid());
    /**
     * Other user's online status.
     */
    DatabaseReference theirStatus;
    DatabaseReference theirReadStatusRef = chatsReference;
    /**
     * Current session id. Used for online status.
     */
    String sessionId;
    @BindView(R.id.recyclerView_activity_chat)
    RecyclerView chatsRV;
    @BindView(R.id.ll_frame_chat_toolbar_icon)
    ImageView icon;
    @BindView(R.id.imgBtn_frame_comment_doc)
    ImageButton uploadDocImgBtn;
    @BindView(R.id.tv_frame_chat_toolbar_title)
    TextView title;
    @BindView(R.id.tv_frame_chat_toolbar_subtitle)
    TextView subtitle;
    @BindView(R.id.editText_frame_comment_message)
    EditText message;
    @BindView(R.id.fab_frame_comment_send)
    FloatingActionButton sendFab;
    RealmResults<MessageItem> messageItems;

    ChatMsgAdapter chatMsgAdapter;

    NotificationManagerCompat nmc;

    BroadcastReceiver newMessageReceiver;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set screen background
        getWindow().setBackgroundDrawable(new CenterCropDrawable(ContextCompat.getDrawable(this, R.drawable.bg_chat_activity)));
        setContentView(R.layout.activity_chat);

        otherUserId = getIntent().getStringExtra(MessageItemKeys.OTHER_USER_ID);
        if (otherUserId == null) return;
        theirStatus = onlineStatus.child(otherUserId);
        chatsReference = chatsReference.child(otherUserId)
                .child(ChatItemKeys.PRIVATE_MESSAGES)
                .child(getUser().getUid());
        theirReadStatusRef = chatsReference.child(ChatItemKeys.MESSAGE_STATUS);

        ButterKnife.bind(this);
        setupUI();
        nmc = NotificationManagerCompat.from(this);

        AHC.startService(this, MessagingService.class, MessagingService.TAG);
        notifyOfReadStatus();
        //Create the receiver to update read status
        newMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {
                if (intent == null
                        || intent.getAction() == null
                        || intent.getStringExtra(MessageItemKeys.OTHER_USER_ID) == null
                        || !intent.getStringExtra(MessageItemKeys.OTHER_USER_ID).equals(otherUserId)) {
                    return;
                }
                AHC.logd(TAG, "Received action " + intent.getAction());
                switch (intent.getAction()) {
                    case ChatItemKeys.NOTIFICATION_ACTION:
                        //Cancel ongoing notifications for this user
                        AHC.logd(TAG, "Cancelling notification " + otherUserId.hashCode());
                        nmc.cancel(otherUserId.hashCode());
                        break;
                    case ChatItemKeys.NEW_MESSAGE_ARRIVED:
                        //Fire up intent to notify
                        notifyOfReadStatus();
                        break;
                    default:
                        break;
                }
            }
        };

        theirStatus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    subtitle.setVisibility(View.GONE);
                } else {
                    subtitle.setVisibility(View.VISIBLE);
                }
                notifyOfReadStatus();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                AHC.logd(TAG, "Error reading other user's read status of my messages");
                Log.e(TAG, databaseError.toString());
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

        chatsRV.setHasFixedSize(true);
    }

    /**
     * Update unread message count.
     * //TODO fix counts maybe?
     */
    private void updateCounts() {
        //Remove any unread count if present
        database.executeTransactionAsync(r -> {
            ChatsItem chat = r.where(ChatsItem.class)
                    .equalTo(ChatItemKeys.DB_ID, otherUserId).findFirst();
            if (chat == null) {
                chat = r.createObject(ChatsItem.class, otherUserId);
                chat.setLatest("");
                chat.setUpdate(Calendar.getInstance().getTime());
                chat.setName(getIntent().getStringExtra("title"));
                chat.setPhotoUrl(getIntent().getStringExtra("photoUrl"));
            }
            chat.setUnreadCount(0);
            notifyOfReadStatus();
        });
    }

    private void notifyOfReadStatus() {
        final Intent notifyIntent = new Intent(ChatActivity.this, NotifyService.class);
        notifyIntent.putExtra(MessageItemKeys.OTHER_USER_ID, otherUserId);
        startService(notifyIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        visible = true;
        sessionId = Calendar.getInstance().getTime().toString();
        myStatus = onlineStatus.child(getUser().getUid()).child(sessionId);
        myStatus.setValue(true);
        myStatus.onDisconnect().removeValue();

        final LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        chatsRV.setLayoutManager(llm);

        updateCounts();
        messageItems = database
                .where(MessageItem.class)
                .equalTo(MessageItemKeys.OTHER_USER_ID, otherUserId)
                .findAllSortedAsync(MessageItemKeys.MESSAGE_RECEIVED_TIME, Sort.DESCENDING);
        messageItems.addChangeListener((messageItems, changeSet) -> {
            AHC.logd(TAG, "Calling NotifyService in chat change listener");
            notifyOfReadStatus();
            // `null`  means the async query returns the first time.
            if (changeSet == null && chatMsgAdapter != null) {
                chatMsgAdapter.notifyDataSetChanged();
                return;
            }
            // For deletions, the adapter has to be notified in reverse order.
            OrderedCollectionChangeSet.Range[] deletions = changeSet.getDeletionRanges();
            for (int i = deletions.length - 1; i >= 0; i--) {
                OrderedCollectionChangeSet.Range range = deletions[i];
                chatMsgAdapter.notifyItemRangeRemoved(range.startIndex, range.length);
            }

            OrderedCollectionChangeSet.Range[] insertions = changeSet.getInsertionRanges();
            for (OrderedCollectionChangeSet.Range range : insertions) {
                chatMsgAdapter.notifyItemRangeInserted(range.startIndex, range.length);
            }

            OrderedCollectionChangeSet.Range[] modifications = changeSet.getChangeRanges();
            for (OrderedCollectionChangeSet.Range range : modifications) {
                chatMsgAdapter.notifyItemRangeChanged(range.startIndex, range.length);
            }
            //Cancel any ongoing notification from this user
            nmc.cancel(otherUserId.hashCode());
            chatsRV.scrollToPosition(0);
        });

        chatMsgAdapter = new ChatMsgAdapter(messageItems, this);
        chatsRV.setAdapter(chatMsgAdapter);

        final IntentFilter intf = new IntentFilter();
        intf.addAction(ChatItemKeys.NOTIFICATION_ACTION);
        intf.addAction(ChatItemKeys.NEW_MESSAGE_ARRIVED);
        registerReceiver(newMessageReceiver, intf);
    }

    @Override
    protected void onResume() {
        super.onResume();
        nmc.cancel(otherUserId.hashCode());
        Intent intent = new Intent(ChatActivity.this, NotifyService.class);
        intent.putExtra(MessageItemKeys.OTHER_USER_ID, otherUserId);
        startService(intent);
    }

    @Override
    protected void onStop() {
        visible = false;
        super.onStop();
        unregisterReceiver(newMessageReceiver);
        messageItems.removeAllChangeListeners();
        myStatus.removeValue();
    }

    @OnClick(R.id.ll_frame_chat_toolbar_icons)
    public void backPressed() {
        onBackPressed();
    }

    @OnClick(R.id.fab_frame_comment_send)
    public void sendMessage() {
        //Get user message from edittext
        final String messageData = message.getText().toString().trim();
        //If length of EditText is 0, do nothing
        if (messageData.length() == 0) return;

        final Intent mIntent = new Intent(this, SendService.class);
        mIntent.putExtra(MessageItemKeys.MESSAGE_DATA, messageData);
        mIntent.putExtra(MessageItemKeys.OTHER_USER_ID, otherUserId);
        startService(mIntent);
        updateCounts();

        //Clear EditText after extracting it's value
        message.getText().clear();
        //Go to start of chat
        chatsRV.scrollToPosition(0);
    }

    /**
     * Fires an intent to spin up the "file chooser" UI and select an image.
     */
    @OnClick(R.id.imgBtn_frame_comment_doc)
    public void performFileSearch() {
        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("*/*");

        startActivityForResult(intent, DOCUMENT_READ_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode,
                                 final Intent resultData) {

        if (requestCode == DOCUMENT_READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {
                final Uri uri = resultData.getData();
                AHC.logi(TAG, "Uri: " + uri.toString());
                Toast.makeText(this, "File will be uploaded shortly",
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, SendDocumentService.class);
                intent.setData(uri);
                intent.putExtra(MessageItemKeys.OTHER_USER_ID, otherUserId);
                startService(intent);
            } else {
                Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error occurred while getting URI of file for upload "
                        + resultData + " and result code " + resultCode);
            }
        }
    }
}

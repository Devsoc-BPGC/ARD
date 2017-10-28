package com.macbitsgoa.ard.services;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.macbitsgoa.ard.keys.ChatItemKeys;
import com.macbitsgoa.ard.keys.MessageItemKeys;
import com.macbitsgoa.ard.models.ChatsItem;
import com.macbitsgoa.ard.models.MessageItem;
import com.macbitsgoa.ard.types.MessageStatusType;
import com.macbitsgoa.ard.utils.AHC;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import io.realm.Realm;

public class MessagingService extends BaseService {

    private String messageId;
    private DatabaseReference writeRef;
    private Map<String, Object> senderMap;
    private static MessagingService instance = null;

    public MessagingService() {
        super("MessagingService");
    }

    public MessagingService(String name) {
        super(name);
    }

    public static MessagingService getInstance() {
        if (instance == null)
            instance = new MessagingService("MessagingService");
        return instance;
    }

    public static boolean isInstanceRunning() {
        return instance != null;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        super.onHandleIntent(intent);
        //TODO: restart service using alarm bc
        //TODO fix first singup / login
        DatabaseReference msgRef = getRootReference().child(AHC.FDR_CHAT).child(getUser().getUid()).child("0");
        writeRef = getRootReference().child(AHC.FDR_CHAT);
        ValueEventListener msgRefVEL = getEventListener();
        msgRef.addValueEventListener(msgRefVEL);
        try {
            Thread.sleep(1000 * 60 * 60 * 24);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        msgRef.removeEventListener(msgRefVEL);

    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        instance = null;
    }

    public void sendMessage(final String messageData, final String receiverId) {
        database = Realm.getDefaultInstance();
        Log.e("TAG", "sendeing msg");
        writeRef = getRootReference().child(AHC.FDR_CHAT).child(receiverId).child("0").child(getUser().getUid());
        //Get current system time and include this is in message id;
        final Date messageTime = Calendar.getInstance().getTime();
        messageId = "" + messageTime.getTime()
                + messageTime.hashCode()
                + messageData.hashCode();

        final String latestMessage = messageData.substring(0, messageData.length() % 50);

        //Add new message
        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("data", messageData);
        messageMap.put("date", messageTime);

        //Update latest sender information
        senderMap = new HashMap<>();
        senderMap.put("id", getUser().getUid());
        senderMap.put("name", getUser().getDisplayName());
        senderMap.put("latest", latestMessage);
        senderMap.put("photoUrl", getUser().getPhotoUrl().toString());
        senderMap.put("date", messageTime);

        writeRef.child(ChatItemKeys.MESSAGES).child(messageId).setValue(messageMap, getCompletionListener());

        database.beginTransaction();
        MessageItem mi = database.createObject(MessageItem.class, messageId);
        mi.setMessageRcvd(false);
        mi.setMessageTime(messageTime);
        mi.setMessageRcvdTime(Calendar.getInstance().getTime());
        mi.setMessageData(messageData);
        mi.setSenderId(receiverId);
        mi.setMessageStatus(MessageStatusType.MSG_WAIT);
        database.commitTransaction();

        ChatsItem ci = database.where(ChatsItem.class).equalTo("id", receiverId).findFirst();
        if (ci != null) {
            database.beginTransaction();
            ci.setLatest(latestMessage);
            database.commitTransaction();
        }
    }

    private final DatabaseReference messageStatusRef = getRootReference().child(AHC.FDR_CHAT);

    public DatabaseReference.CompletionListener getCompletionListener() {
        return new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                databaseReference.getParent().getParent().child(ChatItemKeys.SENDER).setValue(senderMap);
                if (messageId == null) return;
                database = Realm.getDefaultInstance();
                MessageItem mi = database.where(MessageItem.class)
                        .equalTo("messageId", messageId)
                        .findFirst();
                if (mi == null) return;
                database.beginTransaction();
                mi.setMessageStatus(MessageStatusType.MSG_SENT);
                database.commitTransaction();
                database.close();
            }
        };
    }

    public ValueEventListener getEventListener() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                Log.e("TAG", dataSnapshot.toString());
                if (dataSnapshot.getValue() == null) return;

                Queue<DataSnapshot> newChatsQ = new LinkedList<>();
                for (final DataSnapshot childShot : dataSnapshot.getChildren()) {
                    newChatsQ.add(childShot);
                }
                while (!newChatsQ.isEmpty()) {
                    final DataSnapshot newChatDS = newChatsQ.poll();
                    final String senderId = newChatDS
                            .child(ChatItemKeys.SENDER)
                            .child("id").getValue(String.class);
                    final String name = newChatDS
                            .child(ChatItemKeys.SENDER)
                            .child("name").getValue(String.class);
                    final String latest = newChatDS
                            .child(ChatItemKeys.SENDER)
                            .child("latest").getValue(String.class);
                    final String photoUrl = newChatDS
                            .child(ChatItemKeys.SENDER)
                            .child("photoUrl").getValue(String.class);
                    final Date date = newChatDS
                            .child(ChatItemKeys.SENDER)
                            .child("date").getValue(Date.class);

                    if (senderId == null
                            || name == null
                            || latest == null) continue;
                    Log.e("TAG", newChatDS.child(ChatItemKeys.MESSAGE_STATUS)
                            .getChildrenCount() + " ");

                    Queue<DataSnapshot> newMessagesQ = new LinkedList<>();
                    for (final DataSnapshot child : newChatDS
                            .child(ChatItemKeys.MESSAGES)
                            .getChildren()) {
                        newMessagesQ.add(child);
                    }

                    DatabaseReference msgStatusWriteRef;
                    msgStatusWriteRef = messageStatusRef
                            .child(senderId)
                            .child(ChatItemKeys.PRIVATE_MESSAGES)
                            .child(getUser().getUid())
                            .child(ChatItemKeys.MESSAGE_STATUS);

                    int newMessageCount = 0;

                    while (!newMessagesQ.isEmpty()) {
                        final DataSnapshot newMessageDS = newMessagesQ.poll();
                        final String messageId = newMessageDS.getKey();
                        final String messageData = newMessageDS.child(MessageItemKeys.DATA).getValue(String.class);
                        final Date messageTime = newMessageDS.child(MessageItemKeys.DATE).getValue(Date.class);

                        if (messageData == null || messageTime == null || messageId == null)
                            continue;

                        MessageItem mi = database.where(MessageItem.class)
                                .equalTo("messageId", messageId)
                                .equalTo("senderId", senderId)
                                .findFirst();
                        database.beginTransaction();
                        if (mi == null) {
                            mi = database.createObject(MessageItem.class, messageId);
                        }
                        mi.setMessageData(messageData);
                        mi.setSenderId(senderId);
                        mi.setMessageTime(messageTime);
                        mi.setMessageRcvdTime(Calendar.getInstance().getTime());
                        mi.setMessageRcvd(true);
                        database.commitTransaction();

                        newMessageCount++;
                        msgStatusWriteRef
                                .child(messageId)
                                .setValue(MessageStatusType.MSG_RCVD);
                        dataSnapshot
                                .child(ChatItemKeys.MESSAGES)
                                .child(messageId)
                                .getRef()
                                .removeValue();
                    }

                    Queue<DataSnapshot> messageStatusQ = new LinkedList<>();
                    for (DataSnapshot temp : newChatDS.child(ChatItemKeys.MESSAGE_STATUS).getChildren()) {
                        messageStatusQ.add(temp);
                    }

                    while (!messageStatusQ.isEmpty()) {
                        DataSnapshot messageStatusDS = messageStatusQ.poll();
                        MessageItem mi = database
                                .where(MessageItem.class)
                                .equalTo("messageId", messageStatusDS.getKey())
                                .findFirst();
                        if (mi == null) {
                            Log.e("TAG", "message lost");
                            continue;
                        }
                        database.beginTransaction();
                        mi.setMessageStatus(messageStatusDS.getValue(Integer.class));
                        database.commitTransaction();
                        Log.e("TAG", "removing read status value");
                        newChatDS.child(ChatItemKeys.MESSAGE_STATUS).child(messageStatusDS.getKey()).getRef().removeValue();
                    }

                    ChatsItem ci = database.where(ChatsItem.class)
                            .equalTo("id", senderId)
                            .findFirst();
                    database.beginTransaction();
                    if (ci == null) {
                        ci = database.createObject(ChatsItem.class, senderId);
                    }
                    ci.setLatest(latest);
                    ci.setName(name);
                    ci.setUpdate(date);
                    ci.setPhotoUrl(photoUrl);
                    ci.setUnreadCount(ci.getUnreadCount() + newMessageCount);
                    database.commitTransaction();

                    dataSnapshot.child(senderId).child(ChatItemKeys.MESSAGES).getRef().removeValue();
                    dataSnapshot.child(senderId).child(ChatItemKeys.MESSAGE_STATUS).getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("TAG", databaseError.toString());
            }
        };
    }

}

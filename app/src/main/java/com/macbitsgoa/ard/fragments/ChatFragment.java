package com.macbitsgoa.ard.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.adapters.ChatsAdapter;
import com.macbitsgoa.ard.interfaces.ChatFragmentListener;
import com.macbitsgoa.ard.models.ChatsItem;
import com.macbitsgoa.ard.models.MessageItem;
import com.macbitsgoa.ard.utils.AHC;

import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment can implement the
 * {@link ChatFragmentListener} interface
 * to handle interaction events.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * @author Vikramaditya Kukreja
 */
public class ChatFragment extends Fragment {

    /**
     * Used to communicate with activity.
     */
    private ChatFragmentListener mListener;

    private RecyclerView recyclerView;

    private Realm database;

    private RealmResults<ChatsItem> chats;
    private ValueEventListener chatsListener;
    private ChatsAdapter chatsAdapter;
    private DatabaseReference dbRef;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param fragmentTitle Title for the fragment.
     * @return A new instance of fragment ChatFragment.
     */
    public static ChatFragment newInstance(final String fragmentTitle) {
        final ChatFragment fragment = new ChatFragment();
        final Bundle args = new Bundle();
        args.putString(AHC.FRAGMENT_TITLE_KEY, fragmentTitle);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mListener.updateChatFragment();
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_fragment_chat);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        if (context instanceof ChatFragmentListener) {
            mListener = (ChatFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        dbRef = FirebaseDatabase.getInstance().getReference("debug").child("chats")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("0");

        chatsListener = getEventListener();
        dbRef.addValueEventListener(chatsListener);
        database = Realm.getDefaultInstance();
        chats = database.where(ChatsItem.class).findAllSorted("update", Sort.DESCENDING);
        chatsAdapter = new ChatsAdapter(chats, getContext());
        recyclerView.setAdapter(chatsAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        dbRef.removeEventListener(chatsListener);
        database.close();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public ValueEventListener getEventListener() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                Log.e("TAG", dataSnapshot.toString());
                Queue<DataSnapshot> shots = new LinkedList<>();
                for (final DataSnapshot childShot : dataSnapshot.getChildren()) {
                    shots.add(childShot);
                }
                while (!shots.isEmpty()) {
                    DataSnapshot childShot = shots.poll();
                    final String senderId = childShot.child("sender").child("id").getValue(String.class);

                    final String name = childShot.child("sender").child("name").getValue(String.class);
                    final String latest = childShot.child("sender").child("latest").getValue(String.class);
                    final String photoUrl = childShot.child("sender").child("photoUrl").getValue(String.class);
                    Date date = childShot.child("sender").child("date").getValue(Date.class);
                    ChatsItem ci = database.where(ChatsItem.class).equalTo("id", senderId).findFirst();
                    database.beginTransaction();
                    if (ci == null) {
                        ci = database.createObject(ChatsItem.class, senderId);
                    }
                    ci.setLatest(latest);
                    ci.setName(name);
                    ci.setUpdate(date);
                    ci.setPhotoUrl(photoUrl);
                    database.commitTransaction();


                    Queue<DataSnapshot> childShots = new LinkedList<>();
                    for (final DataSnapshot child : childShot.child("messages").getChildren()) {
                        childShots.add(child);
                    }

                    while (!childShots.isEmpty()) {
                        DataSnapshot child = childShots.poll();
                        final String messageId = child.getKey();
                        final String messageData = child.child("data").getValue(String.class);
                        final Date messageTime = child.child("date").getValue(Date.class);
                        MessageItem mi = database.where(MessageItem.class)
                                .equalTo("messageId", messageId)
                                .equalTo("senderId", senderId)
                                .findFirst();
                        database.beginTransaction();
                        if (mi == null) {
                            mi = database.createObject(MessageItem.class);
                        }
                        mi.setMessageData(messageData);
                        mi.setMessageId(messageId);
                        mi.setSenderId(senderId);
                        mi.setMessageTime(messageTime);
                        mi.setRcvd(true);
                        database.commitTransaction();

                        dataSnapshot.child("messages").child(messageId).getRef().removeValue();
                    }
                    dataSnapshot.child(senderId).getRef().removeValue();
                }
                chatsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("TAG", databaseError.toString());
            }
        };
    }

}

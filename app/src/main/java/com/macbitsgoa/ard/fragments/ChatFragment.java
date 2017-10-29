package com.macbitsgoa.ard.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.activities.NewChatActivity;
import com.macbitsgoa.ard.adapters.ChatsAdapter;
import com.macbitsgoa.ard.interfaces.ChatFragmentListener;
import com.macbitsgoa.ard.keys.ChatItemKeys;
import com.macbitsgoa.ard.models.ChatsItem;
import com.macbitsgoa.ard.models.MessageItem;
import com.macbitsgoa.ard.utils.AHC;

import java.util.Calendar;

import io.realm.RealmList;
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
public class ChatFragment extends BaseFragment {

    /**
     * Used to communicate with {@link com.macbitsgoa.ard.activities.MainActivity}. to notify it
     * of updates.
     */
    private ChatFragmentListener mListener;

    private RecyclerView recyclerView;

    private ChatsAdapter chatsAdapter;

    private DatabaseReference myStatus;

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
        FloatingActionButton newChatFab = (FloatingActionButton) view.findViewById(R.id.fab_fragment_chat);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        newChatFab.setOnClickListener(this);
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

        String sessionId = Calendar.getInstance().getTime().toString();
        myStatus = getRootReference()
                .child(ChatItemKeys.ONLINE)
                .child(getUser().getUid())
                .child(sessionId);
        myStatus.setValue(true);
        myStatus.onDisconnect().removeValue();

        RealmResults<ChatsItem> chats = database.where(ChatsItem.class)
                .findAllSorted("update", Sort.DESCENDING);

        deleteEmptyChats();
        chatsAdapter = new ChatsAdapter(chats, getContext());

        chats.addChangeListener(results -> {
            chatsAdapter.notifyDataSetChanged();
        });

        recyclerView.setAdapter(chatsAdapter);
    }

    /**
     * If any chat item has zero messages then don't include it in this RV
     */
    private void deleteEmptyChats() {
        database.executeTransactionAsync(r -> {
            RealmList<ChatsItem> allChats = new RealmList<>();
            allChats.addAll(r.where(ChatsItem.class).findAll());
            for (ChatsItem cItem : allChats) {
                if (r.where(MessageItem.class).equalTo("senderId", cItem.getId())
                        .findAll().isEmpty())
                    cItem.deleteFromRealm();
            }

        }, () -> {
            if (chatsAdapter != null)
                chatsAdapter.notifyDataSetChanged();
        });
    }


    @Override
    public void onStop() {
        super.onStop();
        myStatus.removeValue();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        //Only fab  is registered
        startActivity(new Intent(getContext(), NewChatActivity.class));
    }
}

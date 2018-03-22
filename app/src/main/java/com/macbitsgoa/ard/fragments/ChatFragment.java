package com.macbitsgoa.ard.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.activities.NewChatActivity;
import com.macbitsgoa.ard.adapters.ChatsAdapter;
import com.macbitsgoa.ard.interfaces.ChatFragmentListener;
import com.macbitsgoa.ard.keys.ChatItemKeys;
import com.macbitsgoa.ard.keys.MessageItemKeys;
import com.macbitsgoa.ard.models.ChatsItem;
import com.macbitsgoa.ard.models.MessageItem;
import com.macbitsgoa.ard.services.MessagingService;
import com.macbitsgoa.ard.services.SendService;
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
     * TextView to show in case of no chats.
     */
    TextView emptyListTV;
    RealmResults<ChatsItem> chats;
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
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        mListener.updateChatFragment();
        final View view = inflater.inflate(R.layout.fragment_chat, container, false);

        emptyListTV = view.findViewById(R.id.tv_fragment_chat_empty);
        recyclerView = view.findViewById(R.id.recyclerView_fragment_chat);
        final FloatingActionButton newChatFab = view.findViewById(R.id.fab_fragment_chat);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        AHC.startService(getContext(), MessagingService.class, MessagingService.TAG);
        getContext().startService(new Intent(getContext(), SendService.class));

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

        final String sessionId = Calendar.getInstance().getTime().toString();
        myStatus = getRootReference()
                .child(ChatItemKeys.ONLINE)
                .child(getUser().getUid())
                .child(sessionId);
        myStatus.setValue(true);
        myStatus.onDisconnect().removeValue();

        chats = database.where(ChatsItem.class)
                .findAllSorted(ChatItemKeys.DB_DATE, Sort.DESCENDING);

        deleteEmptyChats();
        if (chats.size() == 0) {
            emptyListTV.setVisibility(View.VISIBLE);
        } else {
            emptyListTV.setVisibility(View.GONE);
        }

        chatsAdapter = new ChatsAdapter(chats, getContext());

        chats.addChangeListener(results -> {
            if (results.size() == 0) emptyListTV.setVisibility(View.VISIBLE);
            else emptyListTV.setVisibility(View.GONE);
            chatsAdapter.notifyDataSetChanged();
        });

        recyclerView.setAdapter(chatsAdapter);
    }

    /**
     * If any chat item has zero messages then don't include it in this RV
     */
    private void deleteEmptyChats() {
        database.executeTransactionAsync(r -> {
            final RealmList<ChatsItem> allChats = new RealmList<>();
            allChats.addAll(r.where(ChatsItem.class).findAll());
            for (final ChatsItem cItem : allChats) {
                if (r
                        .where(MessageItem.class)
                        .equalTo(MessageItemKeys.OTHER_USER_ID, cItem.getId())
                        .findAll().isEmpty())
                    cItem.deleteFromRealm();
            }
        });
    }

    @Override
    public void onStop() {
        chats.removeAllChangeListeners();
        myStatus.removeValue();
        super.onStop();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(final View v) {
        //Only fab  is registered
        startActivity(new Intent(getContext(), NewChatActivity.class));
    }
}

package com.macbitsgoa.ard.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.adapters.NewChatAdapter;
import com.macbitsgoa.ard.keys.UserItemKeys;
import com.macbitsgoa.ard.models.UserItem;
import com.macbitsgoa.ard.utils.AHC;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Activity to show users to talk to.
 * Database rules prevent non admin users to read other users name/details.
 *
 * @author Vikramaditya Kukreja
 * @author Rushikesh Jogdand
 */
public class NewChatActivity extends BaseActivity {

    /**
     * Tag for this class.
     */
    public static final String TAG = NewChatActivity.class.getSimpleName();
    /**
     * Database reference to admin list in Firebase.
     */
    private final DatabaseReference adminsRef = getRootReference().child(AHC.FDR_ADMINS);

    /**
     * Database reference to user list in Firebase.
     */
    private final DatabaseReference usersRef = getRootReference().child(AHC.FDR_USERS);

    /**
     * Progress loader during downloading information.
     */
    @BindView(R.id.pb_activity_new_chat)
    ProgressBar progressBar;

    /**
     * Recyclerview to display user list.
     */
    @BindView(R.id.rv_activity_new_chat)
    RecyclerView userRV;

    @BindView(R.id.toolbar_activity_new_chat)
    Toolbar toolbar;

    ValueEventListener adminEventListener;
    ValueEventListener usersEventListener;
    List<UserItem> adminsList, usersList;
    NewChatAdapter adapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_chat);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        adminsList = new ArrayList<>();
        usersList = new ArrayList<>();

        adapter = new NewChatAdapter(adminsList, usersList, this);
        userRV.setLayoutManager(new LinearLayoutManager(this));
        userRV.setHasFixedSize(true);
        userRV.setAdapter(adapter);

        adminEventListener = getAdminEventListener();
        usersEventListener = getUsersEventListener();

        adminsRef.addValueEventListener(adminEventListener);
        usersRef.addValueEventListener(usersEventListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adminsRef.removeEventListener(adminEventListener);
        usersRef.removeEventListener(usersEventListener);
    }

    private ValueEventListener getAdminEventListener() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                adminsList.clear();
                for (final DataSnapshot child : dataSnapshot.getChildren()) {
                    final String uid = child.getKey();
                    if (uid == null || uid.equals(getUser().getUid())) continue;
                    final String name = child.child(UserItemKeys.NAME).getValue(String.class);
                    final String email = child.child(UserItemKeys.EMAIL).getValue(String.class);
                    final String photoUrl = child.child(UserItemKeys.PHOTO_URL)
                            .getValue(String.class);
                    final String desc = child.child(UserItemKeys.DESC).getValue(String.class);
                    UserItem ui = new UserItem(uid,
                            name,
                            email,
                            photoUrl,
                            desc);
                    adminsList.add(ui);
                }
                Collections.sort(adminsList);
                AHC.logd(TAG, "Admins are " + adminsList.toString());
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(final DatabaseError databaseError) {
                AHC.logd(TAG, "Database error for admins in "
                        + NewChatActivity.class.getSimpleName());
                AHC.logd(TAG, databaseError.toString());
                Toast.makeText(NewChatActivity.this,
                        "Could not get admin data. Try again later!",
                        Toast.LENGTH_SHORT).show();
            }
        };
    }

    private ValueEventListener getUsersEventListener() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                usersList.clear();
                for (final DataSnapshot child : dataSnapshot.getChildren()) {
                    if (child.getValue() == null) continue;
                    final String uid = child.getKey();
                    if (uid == null || uid.equals(getUser().getUid())) continue;
                    final String name = child.child(UserItemKeys.NAME).getValue(String.class);
                    final String email = child.child(UserItemKeys.EMAIL).getValue(String.class);
                    final String photoUrl = child.child(UserItemKeys.PHOTO_URL)
                            .getValue(String.class);
                    UserItem ui = new UserItem(uid,
                            name,
                            email,
                            photoUrl,
                            "User");
                    usersList.add(ui);
                }
                AHC.logd(TAG, "All users are " + usersList.toString());
                Collections.sort(usersList);
                if (usersList.removeAll(adminsList))
                    AHC.logd(TAG, "Final non admin users are " + usersList.toString());
                else AHC.logd(TAG, "No admins were removed from userlist");
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(final DatabaseError databaseError) {
                AHC.logd(TAG, "Database error for users in "
                        + NewChatActivity.class.getSimpleName());
                AHC.logd(TAG, databaseError.toString());
            }
        };
    }
}

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

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmResults;

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

    //----------------------------------------------------------------------------------------------
    @BindView(R.id.pb_activity_new_chat)
    ProgressBar progressBar;

    @BindView(R.id.rv_activity_new_chat)
    RecyclerView userRV;

    @BindView(R.id.toolbar_activity_new_chat)
    Toolbar toolbar;

    //----------------------------------------------------------------------------------------------
    private final DatabaseReference adminsRef = getRootReference().child(AHC.FDR_ADMINS);
    private final DatabaseReference usersRef = getRootReference().child(AHC.FDR_USERS);

    private NewChatAdapter adapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_chat);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        final RealmResults<UserItem> adminsList = database.where(UserItem.class)
                .equalTo(UserItemKeys.ADMIN, true)
                .notEqualTo(UserItemKeys.UID, getUser().getUid())
                .findAllSorted(UserItemKeys.NAME);
        final RealmResults<UserItem> usersList = database.where(UserItem.class)
                .equalTo(UserItemKeys.ADMIN, false)
                .notEqualTo(UserItemKeys.UID, getUser().getUid())
                .findAllSorted(UserItemKeys.NAME);

        final UserItem thisUser = database.where(UserItem.class)
                .equalTo(UserItemKeys.UID, getUser().getUid()).findFirst();

        boolean isAdmin = false;
        if (thisUser != null && thisUser.isAdmin())
            isAdmin = true;

        adminsList.addChangeListener(adminUsers -> {
            adapter.setAdmin(true);
            adapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
        });
        usersList.addChangeListener(userItems -> {
            adapter.notifyDataSetChanged();
            if (adapter.getItemCount() > 0) {
                progressBar.setIndeterminate(false);
                progressBar.setProgress(100);
                progressBar.setVisibility(View.GONE);
            }
        });
        adapter = new NewChatAdapter(adminsList, usersList, this);
        adapter.setAdmin(isAdmin);
        userRV.setLayoutManager(new LinearLayoutManager(this));
        userRV.setHasFixedSize(true);
        userRV.setAdapter(adapter);

        adminsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                for (final DataSnapshot child : dataSnapshot.getChildren()) {
                    if (child.getValue() == null) continue;
                    final String uid = child.getKey();
                    final String name = child.child(UserItemKeys.NAME).getValue(String.class);
                    final String email = child.child(UserItemKeys.EMAIL).getValue(String.class);
                    final String photoUrl = child.child(UserItemKeys.PHOTO_URL)
                            .getValue(String.class);
                    final String desc = child.child(UserItemKeys.DESC).getValue(String.class);
                    UserItem ui = database
                            .where(UserItem.class).equalTo(UserItemKeys.UID, uid).findFirst();
                    database.beginTransaction();
                    if (ui == null) {
                        ui = database.createObject(UserItem.class, uid);
                    }
                    ui.setAdmin(true);
                    ui.setDesc(desc == null ? "Admin" : desc);
                    ui.setEmail(email == null ? "" : email);
                    ui.setName(name == null ? "" : name);
                    ui.setPhotoUrl(photoUrl == null ? "" : photoUrl);
                    database.commitTransaction();
                }
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
        });
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {

                for (final DataSnapshot child : dataSnapshot.getChildren()) {
                    if (child.getValue() == null) continue;
                    final String uid = child.getKey();
                    final String name = child.child(UserItemKeys.NAME).getValue(String.class);
                    final String email = child.child(UserItemKeys.EMAIL).getValue(String.class);
                    final String photoUrl = child.child(UserItemKeys.PHOTO_URL)
                            .getValue(String.class);
                    UserItem ui = database
                            .where(UserItem.class).equalTo(UserItemKeys.UID, uid).findFirst();
                    database.beginTransaction();
                    if (ui == null) {
                        ui = database.createObject(UserItem.class, uid);
                        ui.setAdmin(false);
                        ui.setDesc("User");
                    }
                    ui.setEmail(email == null ? "" : email);
                    ui.setName(name == null ? "" : name);
                    ui.setPhotoUrl(photoUrl == null ? "" : photoUrl);
                    database.commitTransaction();
                }
            }

            @Override
            public void onCancelled(final DatabaseError databaseError) {
                AHC.logd(TAG, "Database error for users in "
                        + NewChatActivity.class.getSimpleName());
                AHC.logd(TAG, databaseError.toString());
            }
        });
    }
}

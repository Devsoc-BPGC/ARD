package com.macbitsgoa.ard.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.activities.AuthActivity;
import com.macbitsgoa.ard.adapters.DetailsAdapter;
import com.macbitsgoa.ard.interfaces.OnItemClickListener;
import com.macbitsgoa.ard.interfaces.RecyclerItemClickListener;
import com.macbitsgoa.ard.keys.UserItemKeys;
import com.macbitsgoa.ard.utils.AHC;
import com.macbitsgoa.ard.utils.Browser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.realm.Realm;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * @author Aayush Singla
 * @author Vikramaditya Kukreja
 */
public class DetailsFragment extends BaseFragment implements OnItemClickListener {

    /**
     * Tag for this class.
     */
    public static final String TAG = DetailsFragment.class.getSimpleName();

    /**
     * Recycler View to display details items
     */
    @BindView(R.id.recyclerView_fragment_details)
    RecyclerView detailRV;

    /**
     * Reference to user database on firebase.
     */
    private DatabaseReference userRef;

    /**
     * {@link #userRef} listener.
     */
    private ValueEventListener userRefVEL;

    /**
     * Butterknife unbinder.
     */
    private Unbinder unbinder;

    /**
     * User name textview.
     */
    @BindView(R.id.tv_vh_details_name)
    TextView userNameTV;

    /**
     * User desc textview.
     */
    @BindView(R.id.tv_vh_details_desc)
    TextView userDescTV;

    /**
     * User email textview.
     */
    @BindView(R.id.tv_vh_details_email)
    TextView userEmailTV;

    @BindView(R.id.imgView_vh_details_user)
    ImageView userPhotoIV;

    public DetailsFragment() {
        // Required empty public constructor
    }


    public static DetailsFragment newInstance() {
        return new DetailsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_detail, container, false);
        unbinder = ButterKnife.bind(this, view);

        detailRV.setHasFixedSize(true);
        detailRV.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), detailRV, this));
        detailRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
//        detailRV.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        final List<String> itemList = new ArrayList<>();
        itemList.add("About ARD");
        itemList.add("About app");
        itemList.add("Sign Out");

        DetailsAdapter detailsAdapter = new DetailsAdapter(itemList);
        detailRV.setAdapter(detailsAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getUser() == null || getUser().getUid() == null) {
            getActivity().finish();
            return;
        }
        userRef = getRootReference()
                .child(AHC.FDR_USERS)
                .child(getUser().getUid());

        userRefVEL = getData();
        userRef.addValueEventListener(userRefVEL);

        //Set basic data from auth if possible
        if (getUser().getDisplayName() != null) {
            userNameTV.setText(getUser().getDisplayName());
        }
        if (getUser().getEmail() != null) {
            userEmailTV.setText(getUser().getEmail());
        }
        Glide.with(getContext())
                .load(getUser().getPhotoUrl())
                .transition(DrawableTransitionOptions.withCrossFade(500))
                .apply(RequestOptions
                        .circleCropTransform()
                        .error(R.drawable.ic_contact))
                .into(userPhotoIV);
    }

    @Override
    public void onStop() {
        userRef.removeEventListener(userRefVEL);
        userRefVEL = null;
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    private ValueEventListener getData() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot ds) {
                if (ds == null) {
                    AHC.logd(TAG, "User data does not exist");
                    Crashlytics.log("Null datasnapshot in " + TAG);
                }
                final String userName = ds.child(UserItemKeys.NAME).getValue(String.class);
                final String userEmail = ds.child(UserItemKeys.EMAIL).getValue(String.class);
                String userDesc = "User";
                if (ds.hasChild(UserItemKeys.DESC)) {
                    userDesc = ds.child(UserItemKeys.DESC).getValue(String.class);
                }

                //Set this data
                userNameTV.setText(userName);
                userEmailTV.setText(userEmail);
                userDescTV.setText(userDesc);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Database read error for user info");
            }
        };
    }

    @Override
    public void onItemClick(View view, int position) {
        if (position == 0) {
            //About ARD
            new Browser(getActivity()).launchUrl(AHC.ARD_REDIRECT_URL);
            //TODO app closing when opening url
        } else if (position == 1) {
            //About MAC
        } else {
            FirebaseAuth.getInstance().signOut();
            Realm.deleteRealm(Realm.getDefaultConfiguration());
            //TODO cancel all running and scheduled services. also delete shared pref file
            final Intent intent = new Intent(getContext(), AuthActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    }

    @Override
    public void onLongItemClick(View view, int position) {
        //Not used
    }
}

package com.macbitsgoa.ard.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
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
import com.macbitsgoa.ard.services.ForumService;
import com.macbitsgoa.ard.utils.AHC;
import com.macbitsgoa.ard.utils.Browser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.realm.exceptions.RealmException;

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
     * Recycler View to display details items.
     */
    @BindView(R.id.recyclerView_fragment_details)
    RecyclerView detailRV;

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

    /**
     * User photo image view.
     */
    @BindView(R.id.imgView_vh_details_user)
    ImageView userPhotoIV;

    /**
     * {@link #userRef} listener.
     */
    private ValueEventListener userRefVEL;

    /**
     * Reference to user database on firebase.
     */
    private DatabaseReference userRef;

    /**
     * Butterknife unbinder.
     */
    private Unbinder unbinder;

    public DetailsFragment() {
        // Required empty public constructor
    }

    public static DetailsFragment newInstance() {
        return new DetailsFragment();
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_detail, container, false);
        unbinder = ButterKnife.bind(this, view);

        detailRV.setHasFixedSize(true);
        detailRV.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), detailRV, this));
        detailRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        final List<String> itemList = new ArrayList<>();
        itemList.add("About ARD");
        itemList.add("About app");
        itemList.add("Sign Out");

        detailRV.setAdapter(new DetailsAdapter(itemList));

        if (getUser() == null || getUser().getUid() == null) {
            getActivity().finish();
            return view;
        }

        userRef = getRootReference()
                .child(AHC.FDR_ADMINS)
                .child(getUser().getUid());

        userRefVEL = getData();
        userRef.addValueEventListener(userRefVEL);

        userNameTV.setText(getUser().getDisplayName());
        userEmailTV.setText(getUser().getEmail());
        Glide.with(getContext())
                .load(getUser().getPhotoUrl())
                .transition(DrawableTransitionOptions
                        .withCrossFade(getInteger(R.integer.default_glide_load_fade)))
                .apply(RequestOptions
                        .circleCropTransform()
                        .error(R.drawable.ic_contact))
                .into(userPhotoIV);

        return view;
    }

    @Override
    public void onDestroyView() {
        userRef.removeEventListener(userRefVEL);
        userRefVEL = null;
        unbinder.unbind();
        super.onDestroyView();
    }

    /**
     * Method to return a VEL for user desc.
     *
     * @return ValueEventListener
     */
    private ValueEventListener getData() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot ds) {
                //Ds should never be null as the only way a user get this snapshot is if they are in
                //the list. Else we handle it in onCancelled.
                if (ds.hasChild(UserItemKeys.DESC)) {
                    final String userDesc = ds.child(UserItemKeys.DESC).getValue(String.class);
                    userDescTV.setText(userDesc);
                } else {
                    userDescTV.setText(getString(R.string.not_admin_placeholder));
                }
            }

            @Override
            public void onCancelled(final DatabaseError databaseError) {
                AHC.logd(TAG, "Database read error for admin info. User not an admin.");
                userDescTV.setText(getString(R.string.not_admin_placeholder));
            }
        };
    }

    @Override
    public void onItemClick(final View view, final int position) {
        if (position == 0) {
            //About ARD
            new Browser(getActivity()).launchUrl(AHC.ARD_REDIRECT_URL);
        } else if (position == 1) {
            //About MAC
        } else {
            //Delete sp
            getDefaultSharedPref().edit().clear().apply();

            //Stop all intent services if they are running
            getContext().stopService(new Intent(getContext(), ForumService.class));
            //getContext().stopService(new Intent(getContext(), SendService.class));
            //getContext().stopService(new Intent(getContext(), SendDocumentService.class));
            //getContext().stopService(new Intent(getContext(), NotifyService.class));
            //Cancel all job intents
            AHC.getJobDispatcher(getContext()).cancelAll();
            //After services are stopped, log out user
            FirebaseAuth.getInstance().signOut();
            //Reset Realm database
            database.executeTransaction(r -> {
                try {
                    r.deleteAll();
                } catch (RealmException e) {
                    AHC.logd(TAG, e.getMessage());
                }
            });

            //Start Auth activity
            final Intent intent = new Intent(getContext(), AuthActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    public void onLongItemClick(final View view, final int position) {
        //Not used
    }
}

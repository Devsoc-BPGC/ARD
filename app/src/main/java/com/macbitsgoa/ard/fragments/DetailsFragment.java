package com.macbitsgoa.ard.fragments;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * @author Aayush Singla
 * @author Vikramaditya Kukreja
 */
public class DetailsFragment extends BaseFragment implements OnItemClickListener,View.OnClickListener {

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

    @BindView(R.id.tv_about_us)
    TextView aboutUsTV;

    @BindView(R.id.constraint)
    ViewGroup sceneRoot;

    @BindView(R.id.container)
    ViewGroup container;

    @BindView(R.id.tv_fragment_detail_app_info)
    TextView tv_version_name;

    /**
     * Butterknife unbinder.
     */
    private Unbinder unbinder;
    /**
     * link for fb page of mac and id.
     */
    public static String ABOUT_US_FACEBOOK_URL = "https://www.facebook.com/MACBITSGoa";
    public static String ABOUT_US_FACEBOOK_PAGE_ID = "MACBITSGoa";
    /**
     *To check if about mac visible or not.
     */
     Boolean clicked;
    /**
     * view_aboutMac
     */
    View viewClicked;
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
       View view = inflater.inflate(R.layout.fragment_detail, container, false);
        unbinder = ButterKnife.bind(this, view);
        clicked=false;
        detailRV.setHasFixedSize(true);
        detailRV.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), detailRV, this));
        detailRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        final List<String> itemList = new ArrayList<>();
        itemList.add("About ARD");
        //itemList.add("About app");
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


        aboutUsTV.setOnClickListener(this);

        return view;
    }


    @Override
    public void onDestroyView() {
        viewClicked=null;
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
       // } else {
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
                r.deleteAll();
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

    @Override
    public void onClick(View v) {
        switch (v.getTag().toString()) {
            case "playStore":
                Intent googlePlayIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=Mobile+App+Club+-+BITS+Goa"));
                startActivity(googlePlayIntent);
                break;
            case "fb":
                try {
                    Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
                    String facebookUrl = getFacebookPageURL(getContext());
                    facebookIntent.setData(Uri.parse(facebookUrl));
                    startActivity(facebookIntent);
                } catch (ActivityNotFoundException e) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(ABOUT_US_FACEBOOK_URL));
                    Toast.makeText(getContext(), "Opening in browser", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                }

                break;
            case "button":

                if (viewClicked == null) {

                    viewClicked = getLayoutInflater().inflate(R.layout.view_developers, null);
                    inflateDevelopers(viewClicked);
                }

                if (clicked) {
                    sceneRoot.setVisibility(View.VISIBLE);
                    tv_version_name.setVisibility(View.VISIBLE);
                    container.removeView(viewClicked);
                    aboutUsTV.setVisibility(View.VISIBLE);
                    clicked = false;
                } else {
                    sceneRoot.setVisibility(View.GONE);
                    tv_version_name.setVisibility(View.GONE);
                    container.addView(viewClicked);
                    aboutUsTV.setVisibility(View.GONE);
                    clicked = true;
                }

        }
    }
    void inflateDevelopers(View view){

        //finding views
        ImageView imageVikram= view.findViewById(R.id.image_vikram);
        ImageView imageRushikesh= view.findViewById(R.id.image_rushikesh);
        ImageView imageAayush= view.findViewById(R.id.image_aayush);
        FloatingActionButton back=view.findViewById(R.id.fab_back);

        //setting onCLickListeners for all views
        view.findViewById(R.id.image_fb).setOnClickListener(this);
        view.findViewById(R.id.image_play_store).setOnClickListener(this);
        back.setOnClickListener(this);

        //loading developers images into all imageViews
        Glide.with(getContext())
                .load("https://lh4.googleusercontent.com/-0dhUBhZKH94/AAAAAAAAAAI/AAAAAAAAACQ/F7fd4BSFRsY/s96-c/photo.jpg")
                .transition(DrawableTransitionOptions.withCrossFade(500))
                .apply(RequestOptions
                        .circleCropTransform()
                        .error(R.drawable.ic_contact))
                .into(imageVikram);

        Glide.with(getContext())
                .load("https://lh4.googleusercontent.com/-ooZffw6cRtU/AAAAAAAAAAI/AAAAAAAAEf0/27Nk35sCSr8/s96-c/photo.jpg")
                .transition(DrawableTransitionOptions.withCrossFade(500))
                .apply(RequestOptions
                        .circleCropTransform()
                        .error(R.drawable.ic_contact))
                .into(imageRushikesh);

        Glide.with(getContext())
                .load(getUser().getPhotoUrl())
                .transition(DrawableTransitionOptions.withCrossFade(500))
                .apply(RequestOptions
                        .circleCropTransform()
                        .error(R.drawable.ic_contact))
                .into(imageAayush);



    }

    //method to get the right URL to use in the intent
    public String getFacebookPageURL(final Context context) {
         PackageManager packageManager = context.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                return "fb://facewebmodal/f?href=" + ABOUT_US_FACEBOOK_URL;
            } else { //older versions of fb app
                return "fb://page/" + ABOUT_US_FACEBOOK_PAGE_ID;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return ABOUT_US_FACEBOOK_URL; //normal web url
        }
    }

}

package com.macbitsgoa.ard.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.adapters.DetailsAdapter;
import com.macbitsgoa.ard.models.DetailsItem;
import com.macbitsgoa.ard.utils.AHC;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * @author Aayush Singla
 */
public class DetailsFragment extends BaseFragment {
    /**
     * Recycler View to display details items
     */
    @BindView(R.id.recyclerView_fragment_details)
    RecyclerView detailRV;
    /**
     * Object for storing user data
     */
    private DetailsItem detailsItem;
    /**
     * Object of {@link DetailsAdapter} Class for Recycler View
     */
    private DetailsAdapter detailsAdapter;


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
        View view=inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this,view);

        //retrieve user data for recycler view and set adapter to recycler view
        getData();

        detailRV.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        detailRV.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        return view;

    }

    private void getData(){
        getRootReference().child(AHC.FDR_USERS).child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                detailsItem= dataSnapshot.getValue(DetailsItem.class);
                detailsAdapter=new DetailsAdapter(detailsItem,getContext());
                detailRV.setAdapter(detailsAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                    Log.e("TAG","UserData retrieval failed");
            }
        });

    }
}

package com.macbitsgoa.ard.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.activities.BaseActivity;
import com.macbitsgoa.ard.models.Developer;
import com.macbitsgoa.ard.viewholders.DeveloperViewHolder;

import java.util.ArrayList;

/**
 * @author Rushikesh Jogdand.
 */
public class AboutMacAdapter extends RecyclerView.Adapter<DeveloperViewHolder> implements ValueEventListener {

    @SuppressWarnings("WeakerAccess")
    public static final int VT_MAC = 0;
    public static final int VT_DEV = 1;
    private ArrayList<Developer> developers;
    private Activity activity;

    public AboutMacAdapter( BaseActivity activity) {
        this.activity = activity;
        DatabaseReference devRef = activity.getRootReference().child("aboutMAC").child("developers");
        populate();
        devRef.addValueEventListener(this);
        devRef.keepSynced(true);
    }

    @NonNull
    @Override
    public DeveloperViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        if (viewType == VT_MAC) {
            view = inflater.inflate(R.layout.vh_mac_desc, parent, false);
        } else {
            view = inflater.inflate(R.layout.vh_developer, parent, false);
        }
        return new DeveloperViewHolder(view, viewType, activity);
    }

    @Override
    public void onBindViewHolder(@NonNull DeveloperViewHolder holder, int position) {
        if (position != 0) holder.populate(developers.get(position - 1));
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VT_MAC : VT_DEV;
    }

    @Override
    public int getItemCount() {
        return developers.size() + 1;
    }

    private void populate() {
        developers = new ArrayList<>();
        developers.add(new Developer(
                "Vikramaditya Kukreja",
                "",
                "vicky@macbitsgoa.com",
                "https://github.com/kukreja-vikramaditya",
                "https://lh4.googleusercontent.com/-0dhUBhZKH94/AAAAAAAAAAI/AAAAAAAAACQ/F7fd4BSFRsY/s96-c/photo.jpg"));
        developers.add(new Developer(
                "Rushiesh Jogdand",
                "+917083413997",
                "rushikesh@jogdand.com",
                "https://jogdand.com",
                "https://lh4.googleusercontent.com/-ooZffw6cRtU/AAAAAAAAAAI/AAAAAAAAEf0/27Nk35sCSr8/s96-c/photo.jpg"));
        developers.add(new Developer(
                "Aayush Singla",
                "",
                "",
                "https://github.com/aayushsingla",
                ""));
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if (dataSnapshot == null) return;
        developers.clear();
        for (DataSnapshot child :
                dataSnapshot.getChildren()) {
            developers.add(child.getValue(Developer.class));
        }
        notifyDataSetChanged();
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}

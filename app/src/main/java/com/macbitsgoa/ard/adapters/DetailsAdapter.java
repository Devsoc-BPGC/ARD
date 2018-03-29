package com.macbitsgoa.ard.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.models.DetailsItem;
import com.macbitsgoa.ard.viewholders.DetailsViewHolder;
import com.macbitsgoa.ard.viewholders.DetailsViewHolderUser;

import java.util.Vector;



/**
 * Adapter to display details list. Call using {@link DetailsAdapter#DetailsAdapter(DetailsItem, Context)} where
 * detailsItem is a object of type {@link DetailsItem} class.
 * Context is passed from parent Activity.
 * This adapter initialise multiple layouts(user data and options).
 *
 *  @author Aayush Singla
 */

public class DetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    /**
     * Vector list to store titles of all the options in details list.
     */
    private Vector<DetailsItem> detailsItemList=new Vector<>();
    /**
     * Object of {@link DetailsItem} class to store user data for position=0.
     */
    private DetailsItem detailsItem;
    /**
     * Context passed from the Parent Activity.
     */
    private Context mContext;

    /**
     * Constructor for objects of {@link DetailsItem} class.
     * It also initialises list items to be displayed in the details list.
     *
     */
    public DetailsAdapter(DetailsItem detailsItem,Context mContext){
        this.detailsItem=detailsItem;
        this.mContext=mContext;
        detailsItemList.add(new DetailsItem("About ARD","ARD"));
        detailsItemList.add(new DetailsItem("About Mac","MAC"));
        detailsItemList.add(new DetailsItem("Sign Out","LOGOUT"));
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View view;

        if(viewType==0) {
            view = layoutInflater.inflate(R.layout.vh_details_user, parent, false);
            return new DetailsViewHolderUser(view);
        } else {
            view = layoutInflater.inflate(R.layout.vh_details_main, parent, false);
            return new DetailsViewHolder(view,detailsItemList);
        }
    }

    @Override
    public int getItemViewType(int position){
        if(position==0)
            return 0;
        else
            return 1;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if(holder.getItemViewType() != 0){
            DetailsViewHolder detailsViewHolder=(DetailsViewHolder)holder;
            detailsViewHolder.TV_details.setText(detailsItemList.get(position-1).getTitle());
        }else{
            DetailsViewHolderUser detailsViewHolderUser=(DetailsViewHolderUser)holder;
            detailsViewHolderUser.TV_name.setText(detailsItem.getName());
            detailsViewHolderUser.TV_description.setText(detailsItem.getDesc());
            detailsViewHolderUser.TV_email.setText(detailsItem.getEmail());
            Glide.with(mContext)
                    .load(detailsItem.getPhotoUrl())
                    .apply(RequestOptions
                            .circleCropTransform()
                            .error(R.drawable.ic_contact))
                    .transition(DrawableTransitionOptions.withCrossFade(300))
                    .into(detailsViewHolderUser.IV_user);

        }

    }

    @Override
    public int getItemCount() {
        return detailsItemList.size()+1;
    }


}

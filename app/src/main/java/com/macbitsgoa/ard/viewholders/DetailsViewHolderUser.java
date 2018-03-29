package com.macbitsgoa.ard.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.macbitsgoa.ard.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * DetailsItem ViewHolder Class for User data.
 * Used only for position=0.
 * @author Aayush Singla
 */

public class DetailsViewHolderUser extends RecyclerView.ViewHolder {

    @BindView(R.id.tv_details_name)
    public TextView TV_name;
    @BindView(R.id.tv_details_description)
    public TextView TV_description;
    @BindView(R.id.tv_details_email)
    public TextView TV_email;
    @BindView(R.id.imageview_user)
    public ImageView IV_user;

    public DetailsViewHolderUser(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }




}

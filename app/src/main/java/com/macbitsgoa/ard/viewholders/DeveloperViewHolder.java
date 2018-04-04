package com.macbitsgoa.ard.viewholders;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.models.Developer;
import com.macbitsgoa.ard.utils.Browser;

import static com.macbitsgoa.ard.adapters.AboutMacAdapter.VT_DEV;

/**
 * @author Rushikesh Jogdand.
 */
public class DeveloperViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private static String fbUrl;
    private static String playStoreUrl;
    private static String githubUrl;
    private TextView nameTv;
    private TextView phoneTv;
    private TextView emailTv;
    private Developer developer;
    private ImageView avatarIv;
    private Activity activity;

    public DeveloperViewHolder(View itemView, int viewType, Activity activity) {
        super(itemView);
        if (viewType == VT_DEV) {
            nameTv = itemView.findViewById(R.id.tv_vh_developer_name);
            avatarIv = itemView.findViewById(R.id.iv_avatar);
            phoneTv = itemView.findViewById(R.id.tv_vh_developer_phone);
            emailTv = itemView.findViewById(R.id.tv_vh_developer_email);
            itemView.setOnClickListener(this);
        } else {
            fbUrl = activity.getString(R.string.url_mac_fb);
            playStoreUrl = activity.getString(R.string.url_mac_play_store);
            githubUrl = activity.getString(R.string.url_mac_github);
            ImageButton playBtn = itemView.findViewById(R.id.imgBtn_vh_mac_desc_play_store);
            ImageButton githubBtn = itemView.findViewById(R.id.imgBtn_vh_mac_desc_github);
            ImageButton fbBtn = itemView.findViewById(R.id.imgBtn_vh_mac_desc_fb);
            playBtn.setOnClickListener(this);
            githubBtn.setOnClickListener(this);
            fbBtn.setOnClickListener(this);
        }
        this.activity = activity;
    }

    public void populate(Developer developer) {
        this.developer = developer;
        nameTv.setText(developer.name);
        emailTv.setText(developer.email);
        phoneTv.setText(developer.phone);
        Glide.with(avatarIv).load(developer.photoUrl)
                .transition(DrawableTransitionOptions.withCrossFade(500))
                .apply(RequestOptions.circleCropTransform().error(R.drawable.ic_contact))
                .into(avatarIv);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBtn_vh_mac_desc_fb: {
                new Browser(activity).launchUrl(fbUrl);
                break;
            }
            case R.id.imgBtn_vh_mac_desc_play_store: {
                new Browser(activity).launchUrl(playStoreUrl);
                break;
            }
            case R.id.imgBtn_vh_mac_desc_github: {
                new Browser(activity).launchUrl(githubUrl);
                break;
            }
            default: {
                new Browser(activity).launchUrl(developer.web);
                break;
            }
        }
    }
}

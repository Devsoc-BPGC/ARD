package com.macbitsgoa.ard.viewholders;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

/**
 * General image viewholder class with convenience method to set image url.
 * Call using {@link #ImageViewHolder(View v, Context c, int resId)}.
 *
 * @author Vikramaditya Kukreja.
 */
public class ImageViewHolder extends RecyclerView.ViewHolder {
    private ImageView imageView;
    private Context context;

    public ImageViewHolder(@NonNull final View itemView, final Context context,
                           @IdRes final int resourceId) {
        super(itemView);
        this.context = context;
        imageView = itemView.findViewById(resourceId);
    }

    /**
     * Set image url.
     *
     * @param url url string to set as image.
     */
    public void setImage(final String url) {
        Glide.with(context)
                .load(url)
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(RequestOptions.centerCropTransform())
                .into(imageView);
    }

}

package com.macbitsgoa.ard.viewholders;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.macbitsgoa.ard.R;

/**
 * General image viewholder class with convenience method to set image url.
 * Call using {@link #ImageViewHolder(View v, Context c, int resId, ImageClickListener icl)}.
 *
 * @author Vikramaditya Kukreja.
 */
public class ImageViewHolder extends RecyclerView.ViewHolder {
    private ImageView imageView;
    private Context context;

    /**
     * Current url being displayed.
     */
    private Uri uri;

    public ImageViewHolder(@NonNull final View itemView, final Context context,
                           @IdRes final int resourceId, final ImageClickListener imageClickListener) {
        super(itemView);
        this.context = context;
        imageView = itemView.findViewById(resourceId);
        imageView.setOnClickListener(o -> {
            if (imageClickListener != null) imageClickListener.onImageClick(uri);
        });
    }

    /**
     * Set uri for image.
     *
     * @param uri Image uri.
     */
    public void setUri(final String uri) {
        setUri(uri, uri);
    }

    /**
     * Set uri for file.
     *
     * @param url      file uri.
     * @param imageUrl uri of image to use.
     */
    public void setUri(final String url, final String imageUrl) {
        setUri(Uri.parse(url), Uri.parse(imageUrl));
    }

    /**
     * Set image url.
     *
     * @param uri      URI to set as image.
     * @param imageUri Image to set as view
     */
    private void setUri(final Uri uri, final Uri imageUri) {
        setUri(uri, imageUri, RequestOptions.fitCenterTransform());
    }

    /**
     * Set image url.
     *
     * @param uri      URI to open when clicked by user.
     * @param imageUri Image to set as view.
     * @param rqop     Glide requestoptions to use.
     */
    public void setUri(final Uri uri, final Uri imageUri, RequestOptions rqop) {
        this.uri = uri;
        Glide.with(context)
                .load(imageUri)
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(rqop.error(R.drawable.ic_attach_file_24dp))
                .into(imageView);
    }

    public interface ImageClickListener {
        void onImageClick(Uri uri);
    }
}

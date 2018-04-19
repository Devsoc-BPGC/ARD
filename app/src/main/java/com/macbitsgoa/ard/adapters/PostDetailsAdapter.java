package com.macbitsgoa.ard.adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.models.TypeItem;
import com.macbitsgoa.ard.models.home.PhotoItem;
import com.macbitsgoa.ard.models.home.TextItem;
import com.macbitsgoa.ard.types.HomeType;
import com.macbitsgoa.ard.viewholders.ImageViewHolder;
import com.macbitsgoa.ard.viewholders.TextViewHolder;

import java.util.List;

/**
 * Adapter to display {@link com.macbitsgoa.ard.models.home.HomeItem} object. Called from contructor
 * by passing key value to display.
 *
 * @author Vikramaditya Kukreja
 */
public class PostDetailsAdapter extends BaseAdapter<RecyclerView.ViewHolder>
        implements ImageViewHolder.ImageClickListener {

    /**
     * Tag for this class.
     */
    public static final String TAG = PostDetailsAdapter.class.getSimpleName();

    /**
     * Context required for image clicking.
     */
    private final Context context;

    /**
     * Items to display.
     */
    private final List<TypeItem> items;

    public PostDetailsAdapter(List<TypeItem> items, final Context context) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent,
                                                      final int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view;
        switch (viewType) {
            case HomeType.TEXT_ITEM:
                view = inflater.inflate(R.layout.vh_big_text, parent, false);
                return new TextViewHolder(view, R.id.tv_vh_big_text);
            case HomeType.PHOTO_ITEM:
                view = inflater.inflate(R.layout.vh_big_image, parent, false);
                return new ImageViewHolder(view, parent.getContext(), R.id.imgView_vh_big_image, this);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder,
                                 final int position) {
        final Object obj = items.get(position).getData();
        switch (holder.getItemViewType()) {
            case HomeType.TEXT_ITEM: {
                final TextViewHolder tvh = (TextViewHolder) holder;
                final TextItem ti = (TextItem) obj;
                tvh.setText(ti.getData());
                break;
            }
            case HomeType.PHOTO_ITEM: {
                final ImageViewHolder imgvh = (ImageViewHolder) holder;
                final PhotoItem pi = (PhotoItem) obj;
                imgvh.setUri(pi.getPhotoUrl());
                break;
            }
        }
    }

    @Override
    public int getItemViewType(final int position) {
        return items.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onImageClick(final Uri uri) {
        if (uri == null) return;
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
        } catch (final ActivityNotFoundException e) {
            Toast.makeText(context, "Error loading image", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Activity not found");
        }
    }
}

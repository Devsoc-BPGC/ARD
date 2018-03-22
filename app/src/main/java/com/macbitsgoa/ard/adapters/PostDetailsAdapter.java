package com.macbitsgoa.ard.adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.models.AnnItem;
import com.macbitsgoa.ard.models.TypeItem;
import com.macbitsgoa.ard.models.home.PhotoItem;
import com.macbitsgoa.ard.models.home.TextItem;
import com.macbitsgoa.ard.types.HomeType;
import com.macbitsgoa.ard.types.PostType;
import com.macbitsgoa.ard.utils.AHC;
import com.macbitsgoa.ard.viewholders.AnnViewHolder;
import com.macbitsgoa.ard.viewholders.ImageViewHolder;
import com.macbitsgoa.ard.viewholders.TextViewHolder;

import java.util.List;

/**
 * Created by vikramaditya on 22/3/18.
 */

public class PostDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ImageViewHolder.ImageClickListener {

    /**
     * Tag for this class.
     */
    public static final String TAG = PostDetailsAdapter.class.getSimpleName();
    private Context context;
    private List<TypeItem> items;

    public PostDetailsAdapter(List<TypeItem> items, final Context context) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view;
        switch (viewType) {
            case PostType.ANNOUNCEMENT:
                view = inflater.inflate(R.layout.vh_ann_activity_item, parent, false);
                return new AnnViewHolder(view);
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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final Object obj = items.get(position).getData();
        switch (holder.getItemViewType()) {
            case PostType.ANNOUNCEMENT: {
                final AnnViewHolder anvh = (AnnViewHolder) holder;
                final AnnItem ai = (AnnItem) obj;
                anvh.data.setText(Html.fromHtml(ai.getData()));
                anvh.extras.setText(Html.fromHtml(ai.getAuthor()
                        + ", "
                        + AHC.getSimpleDayOrTime(ai.getDate())));
                break;
            }
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
    public int getItemViewType(int position) {
        return items.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onImageClick(Uri uri) {
        if (uri == null) return;
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
        } catch (final ActivityNotFoundException e) {
            Toast.makeText(context, "Error loading image", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Activity not found");
        }
    }
}

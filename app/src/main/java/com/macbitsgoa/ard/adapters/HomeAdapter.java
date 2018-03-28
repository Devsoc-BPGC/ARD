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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.models.home.HomeItem;
import com.macbitsgoa.ard.viewholders.HomeItemViewHolder;
import com.macbitsgoa.ard.viewholders.ImageViewHolder;

import java.util.Locale;

import io.realm.RealmResults;

/**
 * Adapter class to display data in HomeFragment.
 *
 * @author Vikramaditya Kukreja
 */
public class HomeAdapter extends RecyclerView.Adapter<HomeItemViewHolder> implements
        ImageViewHolder.ImageClickListener {

    /**
     * TAG for class.
     */
    public static final String TAG = HomeAdapter.class.getSimpleName();

    /**
     * List to hold all data.
     */
    private RealmResults<HomeItem> data;

    /**
     * Context for use with glide.
     */
    private Context context;

    /**
     * Constructor that initialises empty data list of type {@link RealmResults<HomeItem>}.
     *
     * @param data    Data of type {@link RealmResults<HomeItem>}
     * @param context For use with Image downloading.
     */
    public HomeAdapter(@NonNull final RealmResults<HomeItem> data, @NonNull final Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public HomeItemViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view = inflater.inflate(R.layout.vh_home_item_1, parent, false);
        return new HomeItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final HomeItemViewHolder hivh, final int position) {
        final HomeItem hi = data.get(position);
        if (hi.getImages().size() == 0) {
            hivh.imageView.setVisibility(View.GONE);
            hivh.statusBar.setVisibility(View.GONE);
        } else {
            hivh.imageView.setVisibility(View.VISIBLE);
            hivh.statusBar.setVisibility(View.VISIBLE);

            final String numberFormat = "%d";

            hivh.commentCount.setText(String.format(Locale.ENGLISH, numberFormat, hi.getTexts().size()));
            hivh.imageCount.setText(String.format(Locale.ENGLISH, numberFormat, hi.getImages().size()));
            Glide.with(context)
                    .load(hi.getImages().get(0).getPhotoUrl())
                    .transition(DrawableTransitionOptions.withCrossFade(500))
                    .apply(RequestOptions.centerCropTransform())
                    .into(hivh.imageView);

        }
        if (hi.getTexts().size() == 0) {
            hivh.textView1.setVisibility(View.GONE);
            hivh.textView2.setVisibility(View.GONE);
        } else if (hi.getTexts().size() == 1) {
            hivh.textView1.setVisibility(View.VISIBLE);
            hivh.textView2.setVisibility(View.GONE);
            hivh.textView1.setText(Html.fromHtml(hi.getTexts().get(0).getData()));
        } else {
            hivh.textView1.setVisibility(View.VISIBLE);
            hivh.textView2.setVisibility(View.VISIBLE);
            hivh.textView1.setText(Html.fromHtml(hi.getTexts().get(0).getData()));
            hivh.textView2.setText(Html.fromHtml(hi.getTexts().get(1).getData()));
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
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

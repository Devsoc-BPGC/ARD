package com.macbitsgoa.ard.viewholders;

import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by vikramaditya on 29/10/17.
 */

public class TextViewHolder extends RecyclerView.ViewHolder {
    public TextView text;

    public TextViewHolder(View itemView, @IdRes int resourceId) {
        super(itemView);
        text = (TextView) itemView.findViewById(resourceId);
    }
}

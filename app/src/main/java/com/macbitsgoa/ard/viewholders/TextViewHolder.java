package com.macbitsgoa.ard.viewholders;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

/**
 * Created by vikramaditya on 29/10/17.
 */

public class TextViewHolder extends RecyclerView.ViewHolder {
    private TextView text;

    public TextViewHolder(final View itemView, @IdRes final int resourceId) {
        super(itemView);
        text = itemView.findViewById(resourceId);
        text.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void setText(@NonNull final String data) {
        text.setText(Html.fromHtml(data));
    }
}

package com.macbitsgoa.ard.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

/**
 * Drawable decorator which draws the target drawable similarly to an ImageView with scaleType=centerCrop.
 *
 * <p>Example usage:
 * final Drawable bg = getResources().getDrawable(R.drawable.screen);
 * getWindow().setBackgroundDrawable(new CenterCropDrawable(bg));
 *
 * @author Vikramaditya Kukreja
 */
public class CenterCropDrawable extends Drawable {

    @NonNull
    private final Drawable target;

    public CenterCropDrawable(final Context context, @DrawableRes final int drawableRes) {
        this.target = ContextCompat.getDrawable(context, drawableRes);
    }

    public CenterCropDrawable(@NonNull final Drawable target) {
        this.target = target;
    }

    @Override
    public void setBounds(@NonNull final Rect bounds) {
        super.setBounds(bounds.left, bounds.top, bounds.right, bounds.bottom);
    }

    @Override
    public void setBounds(final int left, final int top, final int right, final int bottom) {
        final RectF sourceRect = new RectF(0, 0, target.getIntrinsicWidth(), target.getIntrinsicHeight());
        final RectF screenRect = new RectF(left, top, right, bottom);

        final Matrix matrix = new Matrix();
        matrix.setRectToRect(screenRect, sourceRect, Matrix.ScaleToFit.CENTER);

        final Matrix inverse = new Matrix();
        matrix.invert(inverse);
        inverse.mapRect(sourceRect);

        target.setBounds(Math.round(sourceRect.left), Math.round(sourceRect.top),
                Math.round(sourceRect.right), Math.round(sourceRect.bottom));

        super.setBounds(left, top, right, bottom);
    }

    @Override
    public void draw(@NonNull final Canvas canvas) {
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.clipRect(getBounds());
        target.draw(canvas);
        canvas.restore();
    }

    @Override
    public void setAlpha(@IntRange(from = 0, to = 255) final int alpha) {
        target.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable final ColorFilter colorFilter) {
        target.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return target.getOpacity();
    }
}

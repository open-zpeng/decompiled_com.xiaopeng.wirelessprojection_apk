package com.xiaopeng.xui.drawable.slider;

import android.graphics.Canvas;
/* loaded from: classes2.dex */
public class XSliderTickMarkDotDr extends XSliderTickMarkDrawableBase {
    private static final float BG_DOC_RADIUS = 2.0f;

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        canvas.drawCircle(this.mHalfWidth, this.mHalfHeight, getProgress() * 2.0f, this.mPaint);
        canvas.drawCircle(this.mHalfWidth, this.mHalfHeight, getProgress() * 2.0f, this.mBlurPaint);
    }
}

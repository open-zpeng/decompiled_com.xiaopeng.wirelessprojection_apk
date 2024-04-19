package com.xiaopeng.xui.drawable.slider;

import android.graphics.Canvas;
/* loaded from: classes2.dex */
public class XSliderTickMarkLineDr extends XSliderTickMarkDrawableBase {
    private static final float halfLineHeight = 5.0f;
    private static final float halfLineWidth = 3.2258065f;
    private static final float slope = 1.55f;

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        int save = canvas.save();
        if (this.mSelected) {
            float progress = this.mHalfWidth - (getProgress() * halfLineWidth);
            float progress2 = this.mHalfWidth + (getProgress() * halfLineWidth);
            float progress3 = this.mHalfHeight + (getProgress() * 5.0f);
            float progress4 = this.mHalfHeight - (getProgress() * 5.0f);
            canvas.drawLine(progress, progress3, progress2, progress4, this.mPaint);
            canvas.drawLine(progress, progress3, progress2, progress4, this.mBlurPaint);
        } else {
            canvas.drawLine(this.mHalfWidth - halfLineWidth, this.mHalfHeight + 5.0f, this.mHalfWidth + halfLineWidth, this.mHalfHeight - 5.0f, this.mPaint);
        }
        canvas.restoreToCount(save);
    }
}

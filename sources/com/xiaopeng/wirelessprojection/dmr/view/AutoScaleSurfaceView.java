package com.xiaopeng.wirelessprojection.dmr.view;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import com.xiaopeng.lib.utils.LogUtils;
/* loaded from: classes2.dex */
public class AutoScaleSurfaceView extends SurfaceView {
    private static final String TAG = "AutoScaleSurfaceView";
    private final Handler mHandler;
    private int mScaleHeight;
    private int mScaleWidth;

    public AutoScaleSurfaceView(Context context) {
        super(context);
        this.mHandler = new Handler(Looper.getMainLooper());
        this.mScaleWidth = 0;
        this.mScaleHeight = 0;
    }

    public AutoScaleSurfaceView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mHandler = new Handler(Looper.getMainLooper());
        this.mScaleWidth = 0;
        this.mScaleHeight = 0;
    }

    public AutoScaleSurfaceView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mHandler = new Handler(Looper.getMainLooper());
        this.mScaleWidth = 0;
        this.mScaleHeight = 0;
    }

    public AutoScaleSurfaceView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mHandler = new Handler(Looper.getMainLooper());
        this.mScaleWidth = 0;
        this.mScaleHeight = 0;
    }

    public void cleanSurface() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            this.mHandler.post(new Runnable() { // from class: com.xiaopeng.wirelessprojection.dmr.view.AutoScaleSurfaceView.1
                @Override // java.lang.Runnable
                public void run() {
                    AutoScaleSurfaceView.this.setVisibility(4);
                }
            });
        } else {
            setVisibility(4);
        }
        LogUtils.d(TAG, "clearSurface()");
    }

    public void createSurface() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            this.mHandler.post(new Runnable() { // from class: com.xiaopeng.wirelessprojection.dmr.view.AutoScaleSurfaceView.2
                @Override // java.lang.Runnable
                public void run() {
                    AutoScaleSurfaceView.this.setVisibility(0);
                }
            });
            invalidate();
        } else {
            setVisibility(0);
            postInvalidate();
        }
        LogUtils.d(TAG, "createSurface()");
    }

    public void setAspectRatio(final int i, final int i2) {
        if (i < 0 || i2 < 0) {
            throw new IllegalArgumentException("Size cannot be negative");
        }
        if (Looper.myLooper() != Looper.getMainLooper()) {
            this.mHandler.post(new Runnable() { // from class: com.xiaopeng.wirelessprojection.dmr.view.AutoScaleSurfaceView.3
                @Override // java.lang.Runnable
                public void run() {
                    AutoScaleSurfaceView.this.mScaleWidth = i;
                    AutoScaleSurfaceView.this.mScaleHeight = i2;
                    AutoScaleSurfaceView.this.requestLayout();
                }
            });
            return;
        }
        this.mScaleWidth = i;
        this.mScaleHeight = i2;
        requestLayout();
    }

    @Override // android.view.SurfaceView, android.view.View
    protected void onMeasure(int i, int i2) {
        int i3;
        super.onMeasure(i, i2);
        int size = View.MeasureSpec.getSize(i);
        int size2 = View.MeasureSpec.getSize(i2);
        LogUtils.d(TAG, "onMeasure(): width = " + size + ", height = " + size2);
        int i4 = this.mScaleWidth;
        if (i4 == 0 || (i3 = this.mScaleHeight) == 0) {
            Log.i("tangrh", "onMeasure: scalewidth and scaleheight is 0");
            setMeasuredDimension(size, size2);
        } else if (size * i3 < size2 * i4) {
            setMeasuredDimension(size, (i3 * size) / i4);
        } else {
            setMeasuredDimension((i4 * size2) / i3, size2);
        }
    }

    @Override // android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        LogUtils.d(TAG, "onLayout(): width = " + (i3 - i) + ", height = " + (i4 - i2));
    }
}

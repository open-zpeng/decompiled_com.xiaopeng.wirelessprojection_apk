package com.xiaopeng.xui.utils;

import android.animation.ValueAnimator;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import com.xiaopeng.xui.view.animation.XBesselCurve3Interpolator;
/* loaded from: classes2.dex */
public class XPressedScaleHelper {
    private float currentValue = 1.0f;
    private boolean isPressed;
    private ValueAnimator valueAnimator;
    private View view;

    public XPressedScaleHelper(View view) {
        this.view = view;
    }

    public void update() {
        this.isPressed = false;
        for (int i : this.view.getDrawableState()) {
            if (i == 16842919) {
                this.isPressed = true;
            }
        }
        if (this.isPressed) {
            float f = this.currentValue;
            if (f != 0.98d) {
                animate(f, 0.98f);
                return;
            }
            return;
        }
        float f2 = this.currentValue;
        if (f2 != 1.0f) {
            animate(f2, 1.0f);
        }
    }

    private void animate(float f, float f2) {
        if (!Looper.getMainLooper().isCurrentThread()) {
            this.view.setScaleX(f2);
            this.view.setScaleY(f2);
            this.currentValue = f2;
            return;
        }
        ValueAnimator valueAnimator = this.valueAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.valueAnimator.removeAllUpdateListeners();
        }
        ValueAnimator ofFloat = ValueAnimator.ofFloat(f, f2);
        this.valueAnimator = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.xiaopeng.xui.utils.-$$Lambda$XPressedScaleHelper$NG4QRyR9YVNty7IM-Q9qHwZ3FeA
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                XPressedScaleHelper.this.lambda$animate$0$XPressedScaleHelper(valueAnimator2);
            }
        });
        if (this.isPressed) {
            this.valueAnimator.setInterpolator(new XBesselCurve3Interpolator(0.4f, 0.0f, 0.2f, 1.0f));
            this.valueAnimator.setDuration(100L);
        } else {
            this.valueAnimator.setInterpolator(new XBesselCurve3Interpolator(0.21f, 0.59f, 0.0f, 1.0f));
            this.valueAnimator.setDuration(200L);
        }
        this.valueAnimator.start();
    }

    public /* synthetic */ void lambda$animate$0$XPressedScaleHelper(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        Log.d("XCheckBoxDrawableV5", "value " + floatValue);
        this.view.setScaleX(floatValue);
        this.view.setScaleY(floatValue);
        this.currentValue = floatValue;
    }
}

package com.xiaopeng.xui.drawable.checkbox;

import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieCompositionFactory;
import com.airbnb.lottie.LottieDrawable;
import com.airbnb.lottie.LottieListener;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.Xui;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes2.dex */
public class XCheckBoxDrawableV5 extends LottieDrawable {
    private int height;
    private boolean lastChecked;
    private ValueAnimator mHeightAnimator;
    private int srcId;
    private int width;
    private float checkedFrom = 0.0f;
    private float checkedTo = 0.5f;
    private float unCheckedFrom = 0.5f;
    private float unCheckedTo = 1.0f;

    @Override // android.graphics.drawable.Drawable
    public boolean isStateful() {
        return true;
    }

    @Override // android.graphics.drawable.Drawable
    public void inflate(Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet, Resources.Theme theme) throws XmlPullParserException, IOException {
        TypedArray obtainAttributes;
        super.inflate(resources, xmlPullParser, attributeSet, theme);
        if (theme != null) {
            obtainAttributes = theme.obtainStyledAttributes(attributeSet, R.styleable.XCheckBoxDrawableV5, 0, 0);
        } else {
            obtainAttributes = resources.obtainAttributes(attributeSet, R.styleable.XCheckBoxDrawableV5);
        }
        this.srcId = obtainAttributes.getResourceId(R.styleable.XCheckBoxDrawableV5_android_src, 0);
        this.width = obtainAttributes.getDimensionPixelSize(R.styleable.XCheckBoxDrawableV5_android_width, 0);
        this.height = obtainAttributes.getDimensionPixelSize(R.styleable.XCheckBoxDrawableV5_android_height, 0);
        obtainAttributes.recycle();
        if (this.srcId != 0) {
            LottieCompositionFactory.fromRawRes(Xui.getContext(), R.raw.x_checkbox_lottie, null).addListener(new LottieListener<LottieComposition>() { // from class: com.xiaopeng.xui.drawable.checkbox.XCheckBoxDrawableV5.2
                @Override // com.airbnb.lottie.LottieListener
                public void onResult(LottieComposition lottieComposition) {
                    XCheckBoxDrawableV5.this.setComposition(lottieComposition);
                    Drawable.Callback callback = XCheckBoxDrawableV5.this.getCallback();
                    if (callback != null) {
                        callback.invalidateDrawable(XCheckBoxDrawableV5.this);
                    }
                }
            }).addFailureListener(new LottieListener<Throwable>() { // from class: com.xiaopeng.xui.drawable.checkbox.XCheckBoxDrawableV5.1
                @Override // com.airbnb.lottie.LottieListener
                public void onResult(Throwable th) {
                    Log.d("XCheckBoxDrawableV5", "error" + th.getMessage());
                }
            });
        }
    }

    @Override // com.airbnb.lottie.LottieDrawable, android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        canvas.translate(getBounds().left, getBounds().top);
        super.draw(canvas);
    }

    @Override // com.airbnb.lottie.LottieDrawable, android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        int i = this.width;
        if (i == 0) {
            return 42;
        }
        return i;
    }

    @Override // com.airbnb.lottie.LottieDrawable, android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        int i = this.height;
        if (i == 0) {
            return 42;
        }
        return i;
    }

    @Override // android.graphics.drawable.Drawable
    protected boolean onStateChange(int[] iArr) {
        boolean z = false;
        for (int i : iArr) {
            if (i == 16842912) {
                z = true;
            }
        }
        if (this.lastChecked != z) {
            this.lastChecked = z;
            if (z) {
                animateColor(this.checkedFrom, this.checkedTo);
            } else {
                animateColor(this.unCheckedFrom, this.unCheckedTo);
            }
        }
        return super.onStateChange(iArr);
    }

    private void animateColor(float f, float f2) {
        if (!Looper.getMainLooper().isCurrentThread()) {
            Log.d("not main thread", "setProgress " + f2);
            setProgress(f2);
            Drawable.Callback callback = getCallback();
            if (callback != null) {
                callback.invalidateDrawable(this);
                return;
            }
            return;
        }
        ValueAnimator valueAnimator = this.mHeightAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.mHeightAnimator.removeAllUpdateListeners();
        }
        ValueAnimator ofFloat = ValueAnimator.ofFloat(f, f2);
        this.mHeightAnimator = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.xiaopeng.xui.drawable.checkbox.-$$Lambda$XCheckBoxDrawableV5$UJdU-wWwUTv2RJlOvmI4WnySscM
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                XCheckBoxDrawableV5.this.lambda$animateColor$0$XCheckBoxDrawableV5(valueAnimator2);
            }
        });
        this.mHeightAnimator.setDuration(200L);
        this.mHeightAnimator.start();
    }

    public /* synthetic */ void lambda$animateColor$0$XCheckBoxDrawableV5(ValueAnimator valueAnimator) {
        setProgress(((Float) valueAnimator.getAnimatedValue()).floatValue());
        Drawable.Callback callback = getCallback();
        if (callback != null) {
            callback.invalidateDrawable(this);
        }
    }
}

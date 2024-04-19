package com.xiaopeng.xui.drawable;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.util.AttributeSet;
import com.xiaopeng.xpui.R;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes2.dex */
public class XStateGradientDrawable extends Drawable {
    private static final int DEFAULT_RADIUS = 8;
    private ColorStateList colorStateList;
    private int duration;
    private int mCurrentColor;
    private int mHeight;
    private ValueAnimator mHeightAnimator;
    private int mRadius;
    private int mValueFrom;
    private int mValueTo;
    private int mWidth;
    private final Paint mFillPaint = new Paint(1);
    private final RectF mRect = new RectF();

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -3;
    }

    @Override // android.graphics.drawable.Drawable
    public boolean isStateful() {
        return true;
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
    }

    @Override // android.graphics.drawable.Drawable
    public void inflate(Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet, Resources.Theme theme) throws XmlPullParserException, IOException {
        TypedArray obtainAttributes;
        super.inflate(resources, xmlPullParser, attributeSet, theme);
        if (theme != null) {
            obtainAttributes = theme.obtainStyledAttributes(attributeSet, R.styleable.XStateGradientDrawable, 0, 0);
        } else {
            obtainAttributes = resources.obtainAttributes(attributeSet, R.styleable.XStateGradientDrawable);
        }
        this.mRadius = obtainAttributes.getDimensionPixelSize(R.styleable.XStateGradientDrawable_android_radius, 8);
        this.mWidth = obtainAttributes.getDimensionPixelSize(R.styleable.XStateGradientDrawable_android_width, 0);
        this.mHeight = obtainAttributes.getDimensionPixelSize(R.styleable.XStateGradientDrawable_android_height, 0);
        this.colorStateList = obtainAttributes.getColorStateList(R.styleable.XStateGradientDrawable_android_color);
        this.duration = obtainAttributes.getInt(R.styleable.XStateGradientDrawable_android_duration, 0);
        obtainAttributes.recycle();
    }

    @Override // android.graphics.drawable.Drawable
    protected void onBoundsChange(Rect rect) {
        super.onBoundsChange(rect);
        this.mRect.set(rect);
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        RectF rectF = this.mRect;
        int i = this.mRadius;
        canvas.drawRoundRect(rectF, i, i, this.mFillPaint);
    }

    @Override // android.graphics.drawable.Drawable
    protected boolean onStateChange(int[] iArr) {
        this.mValueFrom = this.mCurrentColor;
        ColorStateList colorStateList = this.colorStateList;
        int colorForState = colorStateList.getColorForState(iArr, colorStateList.getDefaultColor());
        this.mValueTo = colorForState;
        int i = this.mValueFrom;
        if (i != colorForState) {
            animateColor(i, colorForState);
        }
        return super.onStateChange(iArr);
    }

    private void animateColor(int i, int i2) {
        if (!Looper.getMainLooper().isCurrentThread()) {
            this.mFillPaint.setColor(i2);
            this.mCurrentColor = i2;
            invalidateSelf();
            return;
        }
        ValueAnimator valueAnimator = this.mHeightAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.mHeightAnimator.removeAllUpdateListeners();
        }
        ValueAnimator ofInt = ValueAnimator.ofInt(i, i2);
        this.mHeightAnimator = ofInt;
        ofInt.setEvaluator(new ArgbEvaluator());
        this.mHeightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.xiaopeng.xui.drawable.-$$Lambda$XStateGradientDrawable$dvFPZsQAY_d0CDkScgLf0e6unOI
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                XStateGradientDrawable.this.lambda$animateColor$0$XStateGradientDrawable(valueAnimator2);
            }
        });
        this.mHeightAnimator.setDuration(this.duration);
        this.mHeightAnimator.start();
    }

    public /* synthetic */ void lambda$animateColor$0$XStateGradientDrawable(ValueAnimator valueAnimator) {
        int intValue = ((Integer) valueAnimator.getAnimatedValue()).intValue();
        this.mFillPaint.setColor(intValue);
        this.mCurrentColor = intValue;
        invalidateSelf();
    }
}

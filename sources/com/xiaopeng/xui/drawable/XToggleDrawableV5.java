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
public class XToggleDrawableV5 extends Drawable {
    private static final int DEFAULT_RADIUS = 8;
    private ColorStateList colorIndicatorStateList;
    private ColorStateList colorStateList;
    private ColorStateList colorStrokeStateList;
    private int duration;
    private ValueAnimator mContentAnimator;
    private int mCurrentColor;
    private int mCurrentColorIndicator;
    private int mCurrentColorStroke;
    private int mHeight;
    private ValueAnimator mIndicatorAnimator;
    private int mIndicatorValueFrom;
    private int mIndicatorValueTo;
    private int mRadius;
    private int mStrokeValueFrom;
    private int mStrokeValueTo;
    private int mValueFrom;
    private int mValueTo;
    private int mWidth;
    private final Paint mFillPaint = new Paint(1);
    private final Paint mStrokePaint = new Paint(1);
    private final Paint mIndicatorPaint = new Paint(1);
    private final RectF mRect = new RectF();
    private final RectF mIndicatorRect = new RectF();

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
            obtainAttributes = theme.obtainStyledAttributes(attributeSet, R.styleable.XToggleDrawableV5, 0, 0);
        } else {
            obtainAttributes = resources.obtainAttributes(attributeSet, R.styleable.XToggleDrawableV5);
        }
        this.mRadius = obtainAttributes.getDimensionPixelSize(R.styleable.XToggleDrawableV5_android_radius, 8);
        this.mWidth = obtainAttributes.getDimensionPixelSize(R.styleable.XToggleDrawableV5_android_width, 0);
        this.mHeight = obtainAttributes.getDimensionPixelSize(R.styleable.XToggleDrawableV5_android_height, 0);
        this.colorStateList = obtainAttributes.getColorStateList(R.styleable.XToggleDrawableV5_android_color);
        this.colorIndicatorStateList = obtainAttributes.getColorStateList(R.styleable.XToggleDrawableV5_android_colorSecondary);
        this.colorStrokeStateList = obtainAttributes.getColorStateList(R.styleable.XToggleDrawableV5_android_strokeColor);
        this.duration = obtainAttributes.getInt(R.styleable.XToggleDrawableV5_android_duration, 0);
        obtainAttributes.recycle();
    }

    @Override // android.graphics.drawable.Drawable
    protected void onBoundsChange(Rect rect) {
        super.onBoundsChange(rect);
        this.mRect.set(rect);
        this.mIndicatorRect.set((rect.width() >> 1) - 16, rect.bottom - 8, (rect.width() >> 1) + 16, rect.bottom);
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        RectF rectF = this.mRect;
        int i = this.mRadius;
        canvas.drawRoundRect(rectF, i, i, this.mFillPaint);
        canvas.drawRoundRect(this.mIndicatorRect, 2.0f, 2.0f, this.mIndicatorPaint);
    }

    @Override // android.graphics.drawable.Drawable
    protected boolean onStateChange(int[] iArr) {
        this.mValueFrom = this.mCurrentColor;
        this.mIndicatorValueFrom = this.mCurrentColorIndicator;
        this.mStrokeValueFrom = this.mCurrentColorStroke;
        ColorStateList colorStateList = this.colorStateList;
        this.mValueTo = colorStateList.getColorForState(iArr, colorStateList.getDefaultColor());
        ColorStateList colorStateList2 = this.colorIndicatorStateList;
        this.mIndicatorValueTo = colorStateList2.getColorForState(iArr, colorStateList2.getDefaultColor());
        ColorStateList colorStateList3 = this.colorStrokeStateList;
        this.mStrokeValueTo = colorStateList3.getColorForState(iArr, colorStateList3.getDefaultColor());
        int i = this.mValueFrom;
        int i2 = this.mValueTo;
        if (i != i2) {
            animateColor(i, i2);
        }
        int i3 = this.mIndicatorValueFrom;
        int i4 = this.mIndicatorValueTo;
        if (i3 != i4) {
            animateIndicatorColor(i3, i4);
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
        ValueAnimator valueAnimator = this.mContentAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.mContentAnimator.removeAllUpdateListeners();
        }
        ValueAnimator ofInt = ValueAnimator.ofInt(i, i2);
        this.mContentAnimator = ofInt;
        ofInt.setEvaluator(new ArgbEvaluator());
        this.mContentAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.xiaopeng.xui.drawable.-$$Lambda$XToggleDrawableV5$812RtbmLDguKqEBrFdbIMYR0KF8
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                XToggleDrawableV5.this.lambda$animateColor$0$XToggleDrawableV5(valueAnimator2);
            }
        });
        this.mContentAnimator.setDuration(this.duration);
        this.mContentAnimator.start();
    }

    public /* synthetic */ void lambda$animateColor$0$XToggleDrawableV5(ValueAnimator valueAnimator) {
        int intValue = ((Integer) valueAnimator.getAnimatedValue()).intValue();
        this.mFillPaint.setColor(intValue);
        this.mCurrentColor = intValue;
        invalidateSelf();
    }

    private void animateIndicatorColor(int i, int i2) {
        if (!Looper.getMainLooper().isCurrentThread()) {
            this.mIndicatorPaint.setColor(i2);
            this.mCurrentColorIndicator = i2;
            invalidateSelf();
            return;
        }
        ValueAnimator valueAnimator = this.mIndicatorAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.mIndicatorAnimator.removeAllUpdateListeners();
        }
        ValueAnimator ofInt = ValueAnimator.ofInt(i, i2);
        this.mIndicatorAnimator = ofInt;
        ofInt.setEvaluator(new ArgbEvaluator());
        this.mIndicatorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.xiaopeng.xui.drawable.-$$Lambda$XToggleDrawableV5$C-5i5y7fXH_dwxak48n9bzdhkCc
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                XToggleDrawableV5.this.lambda$animateIndicatorColor$1$XToggleDrawableV5(valueAnimator2);
            }
        });
        this.mIndicatorAnimator.setDuration(this.duration);
        this.mIndicatorAnimator.start();
    }

    public /* synthetic */ void lambda$animateIndicatorColor$1$XToggleDrawableV5(ValueAnimator valueAnimator) {
        int intValue = ((Integer) valueAnimator.getAnimatedValue()).intValue();
        this.mIndicatorPaint.setColor(intValue);
        this.mCurrentColorIndicator = intValue;
        invalidateSelf();
    }
}

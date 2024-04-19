package com.xiaopeng.xui.drawable.slider;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import com.xiaopeng.xpui.R;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes2.dex */
public abstract class XSliderTickMarkDrawableBase extends Drawable {
    private static final int DEFAULT_COLOR = 687800320;
    private static final int MAX_LEVEL = 100;
    protected final Paint mBlurPaint;
    private ColorStateList mColor;
    protected float mHalfHeight;
    protected float mHalfWidth;
    protected final Paint mPaint;
    private final int lineStrokeWidth = 4;
    protected boolean mActivated = false;
    protected boolean mSelected = false;
    protected boolean mEnabled = true;

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

    public XSliderTickMarkDrawableBase() {
        Paint paint = new Paint(1);
        this.mPaint = paint;
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(4.0f);
        Paint paint2 = new Paint(1);
        this.mBlurPaint = paint2;
        paint2.setStyle(Paint.Style.FILL);
        paint2.setStrokeCap(Paint.Cap.ROUND);
        paint2.setStrokeWidth(4.0f);
        setLevel(100);
    }

    public void setColor(ColorStateList colorStateList) {
        this.mColor = colorStateList;
        if (colorStateList != null) {
            this.mPaint.setColor(colorStateList.getDefaultColor());
            this.mBlurPaint.setColor(this.mColor.getDefaultColor());
            return;
        }
        this.mPaint.setColor(DEFAULT_COLOR);
        this.mBlurPaint.setColor(DEFAULT_COLOR);
    }

    @Override // android.graphics.drawable.Drawable
    public void inflate(Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet, Resources.Theme theme) throws IOException, XmlPullParserException {
        super.inflate(resources, xmlPullParser, attributeSet, theme);
        updateAttr(resources, attributeSet, theme, 0);
    }

    @Override // android.graphics.drawable.Drawable
    public void inflate(Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet) throws IOException, XmlPullParserException {
        super.inflate(resources, xmlPullParser, attributeSet);
        updateAttr(resources, attributeSet, null, 0);
    }

    public void updateAttr(Resources resources, AttributeSet attributeSet, Resources.Theme theme, int i) {
        TypedArray obtainAttributes;
        if (theme != null) {
            obtainAttributes = theme.obtainStyledAttributes(attributeSet, R.styleable.SliderTickMark, 0, i);
        } else {
            obtainAttributes = resources.obtainAttributes(attributeSet, R.styleable.SliderTickMark);
        }
        setColor(obtainAttributes.getColorStateList(R.styleable.SliderTickMark_slider_tickMark_color));
        obtainAttributes.recycle();
    }

    @Override // android.graphics.drawable.Drawable
    protected boolean onStateChange(int[] iArr) {
        ColorStateList colorStateList;
        boolean z = false;
        boolean z2 = false;
        boolean z3 = false;
        boolean z4 = false;
        for (int i : iArr) {
            if (i == 16843518) {
                z3 = true;
            } else if (i == 16842913) {
                z4 = true;
            } else if (i == 16842910) {
                z2 = true;
            }
        }
        boolean z5 = z2 != this.mEnabled;
        if (z5) {
            this.mEnabled = z2;
        }
        boolean z6 = z3 != this.mActivated;
        if (z6) {
            this.mActivated = z3;
            this.mPaint.setStrokeWidth(z3 ? 2.0f : 4.0f);
            this.mBlurPaint.setMaskFilter(this.mActivated ? new BlurMaskFilter(4.0f, BlurMaskFilter.Blur.NORMAL) : null);
        }
        boolean z7 = z4 != this.mSelected;
        if (z7) {
            this.mSelected = z4;
            if (!z4) {
                setLevel(100);
            }
        }
        z = (z6 || z7 || z5) ? true : true;
        if (z && (colorStateList = this.mColor) != null) {
            int colorForState = colorStateList.getColorForState(iArr, colorStateList.getDefaultColor());
            this.mPaint.setColor(colorForState);
            this.mBlurPaint.setColor(colorForState);
        }
        return z;
    }

    @Override // android.graphics.drawable.Drawable
    protected void onBoundsChange(Rect rect) {
        super.onBoundsChange(rect);
        this.mHalfWidth = rect.width() / 2.0f;
        this.mHalfHeight = rect.height() / 2.0f;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public float getProgress() {
        return getLevel() / 100.0f;
    }
}

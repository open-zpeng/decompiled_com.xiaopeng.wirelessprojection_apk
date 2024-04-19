package com.xiaopeng.xui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import androidx.core.content.res.ResourcesCompat;
import com.xiaopeng.libtheme.ThemeViewModel;
import com.xiaopeng.xpui.R;
/* loaded from: classes2.dex */
public class XShadowLayout extends XFrameLayout {
    private final RectF mRect;
    private final Paint mShadowPaint;
    private int shadowColor;
    private int shadowColorId;
    private int shadowRadius;
    private int shadowX;
    private int shadowY;
    private int shapeRadius;

    public XShadowLayout(Context context) {
        this(context, null);
    }

    public XShadowLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public XShadowLayout(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public XShadowLayout(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mShadowPaint = new Paint();
        this.mRect = new RectF();
        setWillNotDraw(false);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.XShadowLayout);
        this.shapeRadius = obtainStyledAttributes.getDimensionPixelSize(R.styleable.XShadowLayout_radius, 0);
        this.shadowX = obtainStyledAttributes.getDimensionPixelSize(R.styleable.XShadowLayout_shadow_x, 0);
        this.shadowY = obtainStyledAttributes.getDimensionPixelSize(R.styleable.XShadowLayout_shadow_y, 0);
        this.shadowRadius = obtainStyledAttributes.getDimensionPixelSize(R.styleable.XShadowLayout_shadow_radius, 0);
        int resourceId = obtainStyledAttributes.getResourceId(R.styleable.XShadowLayout_shadow_color, 0);
        this.shadowColorId = resourceId;
        if (resourceId != 0) {
            this.shadowColor = context.getColor(resourceId);
        } else {
            this.shadowColor = obtainStyledAttributes.getColor(R.styleable.XShadowLayout_shadow_color, -7829368);
        }
        obtainStyledAttributes.recycle();
        updatePaint();
        this.mXViewDelegate.getThemeViewModel().addCallback(new ThemeViewModel.OnCallback() { // from class: com.xiaopeng.xui.widget.XShadowLayout.1
            @Override // com.xiaopeng.libtheme.ThemeViewModel.OnCallback
            public void onThemeChanged() {
                if (XShadowLayout.this.shadowColorId != 0) {
                    XShadowLayout xShadowLayout = XShadowLayout.this;
                    xShadowLayout.setShadowColorId(xShadowLayout.shadowColorId);
                    XShadowLayout.this.invalidate();
                }
            }
        });
    }

    private void updatePaint() {
        this.mShadowPaint.setColor(this.shadowColor);
        this.mShadowPaint.setAntiAlias(true);
        if (this.shadowRadius > 0) {
            this.mShadowPaint.setMaskFilter(new BlurMaskFilter(this.shadowRadius + 0.01f, BlurMaskFilter.Blur.NORMAL));
        } else {
            this.mShadowPaint.setMaskFilter(null);
        }
    }

    public void setShadowColorId(int i) {
        this.shadowColorId = i;
        this.shadowColor = ResourcesCompat.getColor(getContext().getResources(), i, getContext().getTheme());
        updatePaint();
        invalidate();
    }

    public void setShadowColor(int i) {
        this.shadowColorId = 0;
        this.shadowColor = i;
        updatePaint();
        invalidate();
    }

    public void setShadowRadius(int i) {
        this.shadowRadius = i;
        updatePaint();
        invalidate();
    }

    public void setShadowX(int i) {
        this.shadowX = i;
        this.mRect.left = i;
        this.mRect.right = i + getWidth();
        updatePaint();
        invalidate();
    }

    public void setShadowY(int i) {
        this.shadowY = i;
        this.mRect.top = i;
        this.mRect.bottom = i + getHeight();
        updatePaint();
        invalidate();
    }

    public void setShapeRadius(int i) {
        this.shapeRadius = i;
        invalidate();
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        this.mRect.left = this.shadowX;
        this.mRect.top = this.shadowY;
        this.mRect.right = (this.shadowX + i3) - i;
        this.mRect.bottom = (this.shadowY + i4) - i2;
    }

    @Override // android.view.View
    public void draw(Canvas canvas) {
        RectF rectF = this.mRect;
        int i = this.shapeRadius;
        canvas.drawRoundRect(rectF, i, i, this.mShadowPaint);
        super.draw(canvas);
    }
}

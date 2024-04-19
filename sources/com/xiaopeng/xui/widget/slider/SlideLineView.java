package com.xiaopeng.xui.widget.slider;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.animation.DecelerateInterpolator;
import androidx.core.content.ContextCompat;
import com.xiaopeng.libtheme.ThemeViewModel;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.drawable.slider.XSliderTickMarkDrawableBase;
import com.xiaopeng.xui.view.XView;
import com.xiaopeng.xui.view.XViewDelegate;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public class SlideLineView extends XView {
    private static final int DEFAULT_LEVEL = 100;
    private static final long DURATION = 800;
    public static final int LINE_WIDTH = 22;
    private ValueAnimator animator;
    private final int desireHeight;
    private final int desireWidth;
    private boolean isSelect;
    private int mColorRes;
    private ColorStateList mTickMarkColor;
    private Drawable mTickMarkDr;
    private int mTickMarkRes;
    protected XViewDelegate mXViewDelegate;
    private float progress;

    public SlideLineView(Context context) {
        this(context, null);
    }

    public SlideLineView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public SlideLineView(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public SlideLineView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.desireWidth = 22;
        this.desireHeight = 40;
        this.progress = 1.0f;
        this.mXViewDelegate = XViewDelegate.create(this, attributeSet, i, i2);
        initView(attributeSet, i2);
        this.mXViewDelegate.getThemeViewModel().setCallback(new ThemeViewModel.OnCallback() { // from class: com.xiaopeng.xui.widget.slider.-$$Lambda$SlideLineView$1LX5XdMjQT47I2nhGmguLLdzrdw
            @Override // com.xiaopeng.libtheme.ThemeViewModel.OnCallback
            public final void onThemeChanged() {
                SlideLineView.this.refreshTickMark();
            }
        });
    }

    public SlideLineView(Context context, boolean z, int i) {
        this(context, null, 0, i);
        setSelect(z, false);
    }

    public void setTickMark(Drawable drawable) {
        Drawable drawable2 = this.mTickMarkDr;
        if (drawable2 != null) {
            drawable2.setCallback(null);
        }
        if (drawable != null) {
            drawable.setCallback(this);
            drawable.setBounds(0, 0, getWidth(), getHeight());
            if (drawable instanceof XSliderTickMarkDrawableBase) {
                ((XSliderTickMarkDrawableBase) drawable).setColor(this.mTickMarkColor);
            }
            drawable.setState(getDrawableState());
        }
        this.mTickMarkDr = drawable;
        if (drawable != null) {
            invalidateDrawable(drawable);
        }
    }

    private void readStyleAttrs(Context context, AttributeSet attributeSet, int i) {
        TypedArray obtainStyledAttributes;
        if (attributeSet != null) {
            obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.SlideLineView, 0, i);
        } else {
            obtainStyledAttributes = context.obtainStyledAttributes(i, R.styleable.SlideLineView);
        }
        if (obtainStyledAttributes.hasValueOrEmpty(R.styleable.SlideLineView_slider_tickMark)) {
            this.mTickMarkRes = obtainStyledAttributes.getResourceId(R.styleable.SlideLineView_slider_tickMark, 0);
        }
        int resourceId = obtainStyledAttributes.getResourceId(R.styleable.SlideLineView_slider_tickMark_color, 0);
        this.mColorRes = resourceId;
        if (resourceId != 0) {
            this.mTickMarkColor = ContextCompat.getColorStateList(context, resourceId);
        }
        obtainStyledAttributes.recycle();
    }

    private void applyStyle() {
        refreshTickMark();
        if (this.animator == null) {
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 2.0f, 1.0f);
            this.animator = ofFloat;
            ofFloat.setDuration(DURATION);
            this.animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.xiaopeng.xui.widget.slider.SlideLineView.1
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    SlideLineView.this.progress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                    SlideLineView.this.mTickMarkDr.setLevel((int) (SlideLineView.this.progress * 100.0f));
                    SlideLineView.this.invalidate();
                }
            });
            this.animator.setInterpolator(new DecelerateInterpolator());
            this.animator.addListener(new Animator.AnimatorListener() { // from class: com.xiaopeng.xui.widget.slider.SlideLineView.2
                @Override // android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animator) {
                }

                @Override // android.animation.Animator.AnimatorListener
                public void onAnimationRepeat(Animator animator) {
                }

                @Override // android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animator) {
                }

                @Override // android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    SlideLineView.this.setActivated(false);
                }
            });
        }
        setEnabled(true);
        invalidate();
    }

    public void setStyle(int i) {
        setStyle(null, i);
    }

    private void setStyle(AttributeSet attributeSet, int i) {
        readStyleAttrs(getContext(), attributeSet, i);
        applyStyle();
    }

    private void initView(AttributeSet attributeSet, int i) {
        setLayerType(1, null);
        setStyle(attributeSet, i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshTickMark() {
        Drawable drawable = this.mTickMarkRes != 0 ? ContextCompat.getDrawable(getContext(), this.mTickMarkRes) : null;
        if (this.mColorRes != 0) {
            this.mTickMarkColor = ContextCompat.getColorStateList(getContext(), this.mColorRes);
        }
        setTickMark(drawable);
    }

    @Override // android.view.View
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        int[] drawableState = getDrawableState();
        Drawable drawable = this.mTickMarkDr;
        boolean z = false;
        if (drawable != null && drawable.isStateful()) {
            z = false | this.mTickMarkDr.setState(drawableState);
        }
        if (z) {
            invalidate();
        }
    }

    @Override // android.view.View
    protected boolean verifyDrawable(Drawable drawable) {
        Drawable drawable2 = this.mTickMarkDr;
        return (drawable2 != null && drawable == drawable2) || super.verifyDrawable(drawable);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.view.XView, android.view.View
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        XViewDelegate xViewDelegate = this.mXViewDelegate;
        if (xViewDelegate != null) {
            xViewDelegate.onConfigurationChanged(configuration);
        }
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        Drawable drawable = this.mTickMarkDr;
        if (drawable != null) {
            drawable.setBounds(0, 0, getWidth(), getHeight());
        }
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Drawable drawable = this.mTickMarkDr;
        if (drawable != null) {
            drawable.draw(canvas);
        }
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        setMeasuredDimension(22, 40);
    }

    public void setSelect(boolean z) {
        setSelect(z, true);
    }

    public void setSelect(boolean z, boolean z2) {
        setSelected(z);
        this.isSelect = z;
        if (!z) {
            setActivated(false);
            this.animator.cancel();
        } else if (z2) {
            this.animator.start();
            setActivated(true);
        } else {
            setActivated(false);
        }
        invalidate();
    }

    public boolean isSelect() {
        return this.isSelect;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.view.XView, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        XViewDelegate xViewDelegate = this.mXViewDelegate;
        if (xViewDelegate != null) {
            xViewDelegate.onAttachedToWindow();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.view.XView, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        XViewDelegate xViewDelegate = this.mXViewDelegate;
        if (xViewDelegate != null) {
            xViewDelegate.onDetachedFromWindow();
        }
    }
}

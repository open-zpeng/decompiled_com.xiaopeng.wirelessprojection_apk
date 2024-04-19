package com.xiaopeng.xui.widget.slider;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import androidx.core.content.ContextCompat;
import com.xiaopeng.vui.commons.IVuiElementListener;
import com.xiaopeng.wirelessprojection.core.utils.TrafficInfoUtils;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.theme.XThemeManager;
import com.xiaopeng.xui.widget.XViewGroup;
import java.math.RoundingMode;
import java.text.DecimalFormat;
/* loaded from: classes2.dex */
public abstract class AbsSlider extends XViewGroup implements IVuiElementListener {
    protected static final int BG_ITEM_MARGIN = 18;
    protected static final int BG_ITEM_SIZE = 30;
    protected static final int BG_ITEM_SIZE_MIN = 3;
    protected static final int CHILDREN_LAYOUT_HEIGHT = 40;
    protected static final int CHILDREN_LAYOUT_WIDTH = 20;
    protected static final int INDICATOR_HOLD_HORIZONTAL = 0;
    protected static final int INDICATOR_HOLD_VERTICAL = 40;
    protected static final int INDICATOR_MARGIN = 16;
    private static final int INDICATOR_OUTER = 7;
    public static final int ORIENTATION_HORIZONTAL = 0;
    public static final int ORIENTATION_VERTICAL = 1;
    private static final String TAG = "AbsSlider";
    protected float accuracy;
    protected float bgItemGap;
    protected float currentUpdateIndex;
    protected int decimal;
    protected DecimalFormat decimalFormat;
    private float desireHeight;
    private final float desireWidth;
    protected int disableAlpha;
    protected boolean dismissPop;
    private final int enableAlpha;
    protected int endIndex;
    protected boolean hidePop;
    IndicatorDrawable indicatorDrawable;
    protected float indicatorValue;
    protected int initIndex;
    protected boolean isNight;
    private int itemCount;
    private Drawable mBgDrawable;
    private int mBgDrawableRes;
    protected float mBgThickness;
    protected float mBgThicknessHalf;
    private int mHeight;
    protected boolean mIsDragging;
    protected int mOrientation;
    private Drawable mProgressDrawable;
    private int mProgressDrawableRes;
    protected float mProgressViewLength;
    protected float mScaledTouchSlop;
    protected int mStep;
    protected int mTextTagSize;
    private Drawable mThumb;
    private int mThumbResId;
    private int mTickMarkStyleRes;
    protected float mTouchDownX;
    protected float mTouchDownY;
    private int mWidth;
    protected String prefixUnit;
    protected int startIndex;
    protected String unit;
    protected int upperLimit;
    protected int workableTotalLength;

    private int resetAlpha(int i, int i2) {
        return (i & 16777215) | (i2 << 24);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setAlphaByEnable(boolean z) {
    }

    public AbsSlider(Context context) {
        this(context, null);
    }

    public AbsSlider(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public AbsSlider(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public AbsSlider(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.enableAlpha = 92;
        this.desireWidth = 644.0f;
        this.disableAlpha = 40;
        this.initIndex = -1;
        this.upperLimit = Integer.MIN_VALUE;
        this.mBgThickness = 32.0f;
        this.mBgThicknessHalf = 32.0f / 2.0f;
        this.accuracy = 1.0f;
        this.dismissPop = false;
        this.mStep = 1;
        this.hidePop = false;
        this.desireHeight = 100.0f;
        this.itemCount = 30;
        this.mOrientation = 0;
        this.mTickMarkStyleRes = R.style.XSliderLine;
        setWillNotDraw(false);
        initView(context, attributeSet, i, i2);
        if (isInEditMode()) {
            return;
        }
        this.isNight = XThemeManager.isNight(getContext());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.widget.XViewGroup, android.view.View
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (!isInEditMode()) {
            this.isNight = XThemeManager.isNight(getContext());
            if (XThemeManager.isThemeChanged(configuration)) {
                IndicatorDrawable indicatorDrawable = this.indicatorDrawable;
                if (indicatorDrawable != null) {
                    indicatorDrawable.refreshUI(getResources(), getContext().getTheme());
                }
                setThumb(this.mThumbResId != 0 ? ContextCompat.getDrawable(getContext(), this.mThumbResId) : null);
                if (this.mBgDrawableRes != 0) {
                    setSliderBackground(ContextCompat.getDrawable(getContext(), this.mBgDrawableRes));
                }
                setSliderProgress(this.mProgressDrawableRes != 0 ? ContextCompat.getDrawable(getContext(), this.mProgressDrawableRes) : null);
            }
        }
        invalidate();
    }

    private void initView(Context context, AttributeSet attributeSet, int i, int i2) {
        this.mScaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, R.styleable.XSlider, i, i2);
        this.mWidth = obtainStyledAttributes.getLayoutDimension(R.styleable.XSlider_android_layout_width, 0);
        int layoutDimension = obtainStyledAttributes.getLayoutDimension(R.styleable.XSlider_android_layout_height, 0);
        this.mHeight = layoutDimension;
        setWorkableTotalLength(this.mWidth, layoutDimension);
        this.unit = obtainStyledAttributes.getString(R.styleable.XSlider_slider_unit);
        this.startIndex = obtainStyledAttributes.getInteger(R.styleable.XSlider_slider_start_index, 0);
        this.mStep = obtainStyledAttributes.getInteger(R.styleable.XSlider_slider_step, 1);
        this.initIndex = obtainStyledAttributes.getInteger(R.styleable.XSlider_slider_init_index, -1);
        this.endIndex = obtainStyledAttributes.getInteger(R.styleable.XSlider_slider_end_index, 100);
        this.upperLimit = obtainStyledAttributes.getInteger(R.styleable.XSlider_slider_upper_limit, Integer.MIN_VALUE);
        this.decimal = obtainStyledAttributes.getInteger(R.styleable.XSlider_slider_index_decimal, 0);
        this.prefixUnit = obtainStyledAttributes.getString(R.styleable.XSlider_slider_unit_prefix);
        this.accuracy = obtainStyledAttributes.getFloat(R.styleable.XSlider_slider_accuracy, 0.0f);
        obtainStyledAttributes.recycle();
        if (this.initIndex == -1) {
            this.initIndex = Math.min(this.startIndex, this.endIndex);
        }
        float f = this.initIndex - this.startIndex;
        this.indicatorValue = f;
        this.mProgressViewLength = calculateProgressView(f);
        if (this.endIndex == this.startIndex) {
            throw new RuntimeException("startIndex = endIndex!!! please check the xml");
        }
        int i3 = this.decimal;
        DecimalFormat decimalFormat = i3 == 0 ? null : i3 == 1 ? new DecimalFormat("0.0") : new DecimalFormat(TrafficInfoUtils.TRAFFIC_INFO_DEFAULT);
        this.decimalFormat = decimalFormat;
        if (decimalFormat != null) {
            decimalFormat.setRoundingMode(RoundingMode.DOWN);
        }
        if (this.accuracy == 0.0f) {
            int i4 = this.decimal;
            this.accuracy = i4 == 0 ? 1.0f : i4 == 1 ? 0.1f : 0.01f;
        }
        setStyle(attributeSet, i2);
    }

    public void setStyle(int i) {
        setStyle(null, i);
    }

    private void setStyle(AttributeSet attributeSet, int i) {
        readStyleAttrs(attributeSet, i);
        applyStyleValues();
        if (this.hidePop) {
            return;
        }
        IndicatorDrawable indicatorDrawable = new IndicatorDrawable();
        this.indicatorDrawable = indicatorDrawable;
        indicatorDrawable.inflateAttr(getResources(), getContext().getTheme(), attributeSet, i);
        this.indicatorDrawable.setState(getDrawableState());
        this.indicatorDrawable.setCallback(this);
        this.indicatorDrawable.updateCenter(filterValidValue(), getPopString(), getSliderLength());
    }

    private void readStyleAttrs(AttributeSet attributeSet, int i) {
        TypedArray obtainStyledAttributes;
        Drawable colorDrawable;
        if (attributeSet != null) {
            obtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, R.styleable.XSlider, 0, i);
        } else {
            obtainStyledAttributes = getContext().obtainStyledAttributes(i, R.styleable.XSlider);
        }
        if (obtainStyledAttributes.hasValueOrEmpty(R.styleable.XSlider_slider_background)) {
            this.mBgDrawableRes = obtainStyledAttributes.getResourceId(R.styleable.XSlider_slider_background, 0);
        }
        if (this.mBgDrawableRes != 0) {
            colorDrawable = ContextCompat.getDrawable(getContext(), this.mBgDrawableRes);
        } else {
            colorDrawable = new ColorDrawable(obtainStyledAttributes.getColor(R.styleable.XSlider_slider_background, -30720));
        }
        setSliderBackground(colorDrawable);
        if (obtainStyledAttributes.hasValueOrEmpty(R.styleable.XSlider_slider_progress_drawable)) {
            this.mProgressDrawableRes = obtainStyledAttributes.getResourceId(R.styleable.XSlider_slider_progress_drawable, 0);
        }
        setSliderProgress(this.mProgressDrawableRes != 0 ? ContextCompat.getDrawable(getContext(), this.mProgressDrawableRes) : null);
        if (obtainStyledAttributes.hasValueOrEmpty(R.styleable.XSlider_slider_hide_pop)) {
            this.hidePop = obtainStyledAttributes.getBoolean(R.styleable.XSlider_slider_hide_pop, false);
        }
        if (obtainStyledAttributes.hasValueOrEmpty(R.styleable.XSlider_slider_dismiss_pop)) {
            this.dismissPop = obtainStyledAttributes.getBoolean(R.styleable.XSlider_slider_dismiss_pop, false);
        }
        if (obtainStyledAttributes.hasValueOrEmpty(R.styleable.XSlider_slider_item_count)) {
            this.itemCount = obtainStyledAttributes.getInteger(R.styleable.XSlider_slider_item_count, 30);
        }
        if (obtainStyledAttributes.hasValueOrEmpty(R.styleable.XSlider_slider_orientation)) {
            this.mOrientation = obtainStyledAttributes.getInt(R.styleable.XSlider_slider_orientation, 0);
        }
        if (obtainStyledAttributes.hasValueOrEmpty(R.styleable.XSlider_slider_thumb)) {
            this.mThumbResId = obtainStyledAttributes.getResourceId(R.styleable.XSlider_slider_thumb, 0);
        }
        setThumb(this.mThumbResId != 0 ? ContextCompat.getDrawable(getContext(), this.mThumbResId) : null);
        if (obtainStyledAttributes.hasValueOrEmpty(R.styleable.XSlider_slider_tickMarkStyle)) {
            this.mTickMarkStyleRes = obtainStyledAttributes.getResourceId(R.styleable.XSlider_slider_tickMarkStyle, R.style.XSliderLine);
        }
        obtainStyledAttributes.recycle();
    }

    private void applyStyleValues() {
        if (this.itemCount < 3) {
            this.itemCount = 3;
        }
        if (this.dismissPop) {
            this.hidePop = true;
            this.desireHeight = 32.0f;
        }
        if (!this.hidePop) {
            this.mTextTagSize = 40;
        } else {
            this.mTextTagSize = 0;
        }
        if (this.mOrientation == 1) {
            setMinimumWidth(0);
            setMinimumHeight(80);
        } else {
            setMinimumWidth(80);
            setMinimumHeight(0);
        }
        super.setBackground(null);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof SlideLineView) {
                ((SlideLineView) childAt).setStyle(this.mTickMarkStyleRes);
            }
        }
        invalidate();
    }

    public void setSliderBackground(Drawable drawable) {
        Drawable drawable2 = this.mBgDrawable;
        if (drawable2 != null) {
            drawable2.setCallback(null);
        }
        if (drawable != null) {
            drawable.setCallback(this);
            drawable.setState(getDrawableState());
        }
        this.mBgDrawable = drawable;
        setBackgroundBounds();
        Drawable drawable3 = this.mBgDrawable;
        if (drawable3 != null) {
            invalidateDrawable(drawable3);
        }
    }

    public void setSliderProgress(Drawable drawable) {
        Drawable drawable2 = this.mProgressDrawable;
        if (drawable2 != null) {
            drawable2.setCallback(null);
        }
        if (drawable != null) {
            drawable.setCallback(this);
            drawable.setState(getDrawableState());
        }
        this.mProgressDrawable = drawable;
        if (drawable != null) {
            invalidateDrawable(drawable);
        }
    }

    public void setThumb(Drawable drawable) {
        Drawable drawable2 = this.mThumb;
        if (drawable2 != null) {
            drawable2.setCallback(null);
        }
        if (drawable != null) {
            drawable.setCallback(this);
            drawable.setState(getDrawableState());
        }
        this.mThumb = drawable;
        if (drawable != null) {
            invalidateDrawable(drawable);
        }
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        int i3;
        super.onMeasure(i, i2);
        if (this.mOrientation == 1) {
            i3 = (int) this.desireHeight;
            if (View.MeasureSpec.getMode(i2) == 1073741824) {
                r1 = getMeasuredHeight();
            }
        } else {
            int measuredWidth = View.MeasureSpec.getMode(i) != Integer.MIN_VALUE ? getMeasuredWidth() : 644;
            r1 = (int) this.desireHeight;
            i3 = measuredWidth;
        }
        this.mWidth = i3;
        this.mHeight = r1;
        setMeasuredDimension(i3, r1);
    }

    private void setBackgroundBounds() {
        int heightExIndicator = (int) ((getHeightExIndicator() / 2.0f) - this.mBgThicknessHalf);
        int widthExIndicator = (int) getWidthExIndicator();
        int heightExIndicator2 = (int) ((getHeightExIndicator() / 2.0f) + this.mBgThicknessHalf);
        Drawable drawable = this.mBgDrawable;
        if (drawable != null) {
            drawable.setBounds(0, heightExIndicator, widthExIndicator, heightExIndicator2);
        }
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        int save = canvas.save();
        if (this.mOrientation == 1) {
            canvas.translate(0.0f, getHeight());
            canvas.rotate(270.0f);
        }
        Drawable drawable = this.mBgDrawable;
        if (drawable != null) {
            drawable.draw(canvas);
        }
        if (this.mProgressDrawable != null) {
            int heightExIndicator = (int) ((getHeightExIndicator() / 2.0f) - this.mBgThicknessHalf);
            Drawable drawable2 = this.mThumb;
            this.mProgressDrawable.setBounds(0, heightExIndicator, (int) (filterValidValue() + (drawable2 != null ? drawable2.getIntrinsicWidth() / 2 : 0) + 7.0f), (int) ((getHeightExIndicator() / 2.0f) + this.mBgThicknessHalf));
            this.mProgressDrawable.draw(canvas);
        }
        canvas.restoreToCount(save);
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        setPadding(0, 0, 0, 0);
        setWorkableTotalLength(i, i2);
        this.bgItemGap = this.workableTotalLength / (this.itemCount - 1);
        for (int i5 = 0; i5 < this.itemCount; i5++) {
            addView(new SlideLineView(getContext(), this.mProgressViewLength > (this.bgItemGap * ((float) i5)) + 16.0f, this.mTickMarkStyleRes));
        }
        IndicatorDrawable indicatorDrawable = this.indicatorDrawable;
        if (indicatorDrawable != null) {
            indicatorDrawable.updateCenter(filterValidValue(), getPopString(), getSliderLength());
        }
        setBackgroundBounds();
        invalidate();
    }

    private void setWorkableTotalLength(int i, int i2) {
        this.workableTotalLength = (this.mOrientation == 0 ? Math.max(i, 0) : Math.max(i2, 0)) - 32;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public float calculateProgressView(float f) {
        return (Math.abs(f / (this.endIndex - this.startIndex)) * this.workableTotalLength) + 16.0f;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        float sliderLength = (getSliderLength() - 36) / (getChildCount() - 1);
        for (int i5 = 0; i5 < getChildCount(); i5++) {
            float f = (i5 * sliderLength) + 18.0f;
            getChildAt(i5).layout((int) (f - 10.0f), (((int) getHeightExIndicator()) / 2) - 20, (int) (f + 10.0f), (((int) getHeightExIndicator()) / 2) + 20);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void invalidateAll() {
        invalidate();
        IndicatorDrawable indicatorDrawable = this.indicatorDrawable;
        if (indicatorDrawable != null) {
            indicatorDrawable.updateCenter(filterValidValue(), getPopString(), getSliderLength());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ViewGroup isInScrollContainer() {
        for (ViewParent parent = getParent(); parent instanceof ViewGroup; parent = parent.getParent()) {
            ViewGroup viewGroup = (ViewGroup) parent;
            if (viewGroup.shouldDelayChildPressedState()) {
                return viewGroup;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void stickIndicator() {
        if (this.mStep == 1) {
            return;
        }
        float f = this.workableTotalLength / (this.endIndex - this.startIndex);
        this.mProgressViewLength = (((int) ((this.mProgressViewLength - 16.0f) / f)) * f) + 16.0f + 0.5f;
    }

    public float getIndicatorValue() {
        return (this.indicatorValue + this.startIndex) * this.mStep;
    }

    public void setAccuracy(float f) {
        this.accuracy = f;
    }

    @Override // android.view.View
    protected boolean verifyDrawable(Drawable drawable) {
        IndicatorDrawable indicatorDrawable;
        Drawable drawable2 = this.mThumb;
        return (drawable2 != null && drawable == drawable2) || ((indicatorDrawable = this.indicatorDrawable) != null && drawable == indicatorDrawable) || super.verifyDrawable(drawable);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        int[] drawableState = getDrawableState();
        IndicatorDrawable indicatorDrawable = this.indicatorDrawable;
        if (indicatorDrawable != null && indicatorDrawable.isStateful() && indicatorDrawable.setState(drawableState)) {
            invalidateDrawable(indicatorDrawable);
        }
        Drawable drawable = this.mThumb;
        if (drawable != null && drawable.isStateful() && this.mThumb.setState(drawableState)) {
            invalidateDrawable(this.mThumb);
        }
        Drawable drawable2 = this.mBgDrawable;
        if (drawable2 != null && drawable2.isStateful() && this.mBgDrawable.setState(drawableState)) {
            invalidateDrawable(this.mBgDrawable);
        }
        Drawable drawable3 = this.mProgressDrawable;
        if (drawable3 != null && drawable3.isStateful() && this.mProgressDrawable.setState(drawableState)) {
            invalidateDrawable(this.mProgressDrawable);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        int save = canvas.save();
        if (this.mOrientation == 1) {
            canvas.translate(0.0f, getHeight());
            canvas.rotate(270.0f);
        }
        super.dispatchDraw(canvas);
        float filterValidValue = filterValidValue();
        if (filterValidValue == 0.0f) {
            return;
        }
        float heightExIndicator = getHeightExIndicator() / 2.0f;
        IndicatorDrawable indicatorDrawable = this.indicatorDrawable;
        if (indicatorDrawable != null) {
            indicatorDrawable.draw(canvas);
        }
        if (isEnabled()) {
            Drawable drawable = this.mThumb;
            if (drawable != null) {
                float intrinsicWidth = filterValidValue - (this.mThumb.getIntrinsicWidth() / 2.0f);
                float intrinsicHeight = heightExIndicator - (drawable.getIntrinsicHeight() / 2.0f);
                Drawable drawable2 = this.mThumb;
                drawable2.setBounds((int) intrinsicWidth, (int) intrinsicHeight, (int) (intrinsicWidth + drawable2.getIntrinsicWidth()), (int) (intrinsicHeight + this.mThumb.getIntrinsicHeight()));
                this.mThumb.draw(canvas);
            }
            canvas.restoreToCount(save);
        }
    }

    public int getOrientation() {
        return this.mOrientation;
    }

    public float getHeightExIndicator() {
        return getSliderThickness() + this.mTextTagSize;
    }

    public float getWidthExIndicator() {
        return getSliderLength();
    }

    public int getSliderLength() {
        return this.mOrientation == 1 ? this.mHeight : this.mWidth;
    }

    public int getSliderThickness() {
        return this.mOrientation == 1 ? this.mWidth : this.mHeight;
    }

    public float getProgressViewLength() {
        return this.mProgressViewLength;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public float filterValidValue() {
        if (this.mProgressViewLength < 16.0f) {
            return 16.0f;
        }
        float sliderLength = (getSliderLength() - 16) - upperLimitDistance();
        float f = this.mProgressViewLength;
        return f > sliderLength ? sliderLength : f;
    }

    private int upperLimitDistance() {
        int i;
        int i2;
        int i3 = this.upperLimit;
        if (i3 != Integer.MIN_VALUE && (i = this.startIndex) < (i2 = this.endIndex) && i <= i3 && i3 <= i2) {
            return ((i2 - i3) * this.workableTotalLength) / (i2 - i);
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String getPopString() {
        if (this.unit == null) {
            this.unit = "";
        }
        if (this.prefixUnit == null) {
            this.prefixUnit = "";
        }
        if (this.decimalFormat == null) {
            if (this.mStep == 1) {
                return this.prefixUnit + (this.startIndex + ((int) this.indicatorValue)) + this.unit;
            }
            return this.prefixUnit + getIndicatorValue() + this.unit;
        }
        return this.prefixUnit + this.decimalFormat.format((this.startIndex + this.indicatorValue) * this.mStep) + this.unit;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.widget.XViewGroup, android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.widget.XViewGroup, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (isInEditMode()) {
            return;
        }
        this.isNight = XThemeManager.isNight(getContext());
    }
}

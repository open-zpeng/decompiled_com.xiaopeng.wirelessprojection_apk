package com.xiaopeng.xui.widget.slider;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import com.xiaopeng.vui.commons.IVuiElementBuilder;
import com.xiaopeng.vui.commons.VuiElementType;
import com.xiaopeng.vui.commons.model.VuiElement;
import com.xiaopeng.vui.commons.model.VuiEvent;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.utils.XLogUtils;
import com.xiaopeng.xui.vui.floatinglayer.VuiFloatingLayerManager;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.json.JSONObject;
/* loaded from: classes2.dex */
public class XSlider extends AbsSlider {
    private static final String TAG = "XSlider";
    private float mVuiInterval;
    private int mVuiMaxValue;
    private int mVuiMinValue;
    private ProgressChangeListener progressChangeListener;
    private SliderProgressListener sliderProgressListener;

    /* loaded from: classes2.dex */
    public interface ProgressChangeListener {
        void onProgressChanged(XSlider xSlider, float f, String str, boolean z);
    }

    /* loaded from: classes2.dex */
    public interface SliderProgressListener {
        void onProgressChanged(XSlider xSlider, float f, String str);

        void onStartTrackingTouch(XSlider xSlider);

        void onStopTrackingTouch(XSlider xSlider);
    }

    public XSlider(Context context) {
        this(context, null);
    }

    public XSlider(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public XSlider(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, R.style.XSlider);
    }

    public XSlider(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mVuiMaxValue = -1;
        this.mVuiMinValue = -1;
        this.mVuiInterval = -1.0f;
        TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, R.styleable.XSlider, i, i2);
        this.mVuiMinValue = obtainStyledAttributes.getInteger(R.styleable.XSlider_slider_vuiMinValue, -1);
        this.mVuiMaxValue = obtainStyledAttributes.getInteger(R.styleable.XSlider_slider_vuiMaxValue, -1);
        this.mVuiInterval = obtainStyledAttributes.getFloat(R.styleable.XSlider_slider_vuiInterval, -1.0f);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (isEnabled()) {
            int action = motionEvent.getAction();
            if (action == 0) {
                ViewGroup isInScrollContainer = isInScrollContainer();
                boolean z = (isInScrollContainer != null && isInScrollContainer.canScrollVertically(1)) || (isInScrollContainer != null && isInScrollContainer.canScrollVertically(-1));
                if (!(getOrientation() != 0 ? !((isInScrollContainer != null && isInScrollContainer.canScrollHorizontally(-1)) || (isInScrollContainer != null && isInScrollContainer.canScrollHorizontally(1))) : !z)) {
                    this.mTouchDownX = motionEvent.getX();
                    this.mTouchDownY = motionEvent.getY();
                } else {
                    this.mIsDragging = true;
                    getParent().requestDisallowInterceptTouchEvent(true);
                    SliderProgressListener sliderProgressListener = this.sliderProgressListener;
                    if (sliderProgressListener != null) {
                        sliderProgressListener.onStartTrackingTouch(this);
                    }
                    this.mProgressViewLength = calculateIndicatorFromTouch(motionEvent);
                    notifyChildren(true, false);
                    invalidateAll();
                }
            } else if (action == 1) {
                if (this.mIsDragging) {
                    this.mIsDragging = false;
                } else {
                    SliderProgressListener sliderProgressListener2 = this.sliderProgressListener;
                    if (sliderProgressListener2 != null) {
                        sliderProgressListener2.onStartTrackingTouch(this);
                    }
                }
                this.mProgressViewLength = calculateIndicatorFromTouch(motionEvent);
                stickIndicator();
                notifyChildren(true, true);
                getParent().requestDisallowInterceptTouchEvent(false);
                SliderProgressListener sliderProgressListener3 = this.sliderProgressListener;
                if (sliderProgressListener3 != null) {
                    sliderProgressListener3.onStopTrackingTouch(this);
                }
                invalidateAll();
            } else if (action != 2) {
                if (action == 3) {
                    if (this.mIsDragging) {
                        this.mIsDragging = false;
                    }
                    invalidateAll();
                }
            } else if (this.mIsDragging) {
                this.mProgressViewLength = calculateIndicatorFromTouch(motionEvent);
                notifyChildren(true, false);
                invalidateAll();
            } else {
                if (getOrientation() != 0 ? Math.abs(motionEvent.getY() - this.mTouchDownY) > this.mScaledTouchSlop : Math.abs(motionEvent.getX() - this.mTouchDownX) > this.mScaledTouchSlop) {
                    this.mIsDragging = true;
                    SliderProgressListener sliderProgressListener4 = this.sliderProgressListener;
                    if (sliderProgressListener4 != null) {
                        sliderProgressListener4.onStartTrackingTouch(this);
                    }
                    this.mProgressViewLength = calculateIndicatorFromTouch(motionEvent);
                    getParent().requestDisallowInterceptTouchEvent(true);
                    notifyChildren(true, false);
                    invalidateAll();
                }
            }
            return true;
        }
        return true;
    }

    private float calculateIndicatorFromTouch(MotionEvent motionEvent) {
        return getOrientation() == 0 ? motionEvent.getX() : getHeight() - motionEvent.getY();
    }

    private void notifyChildren(boolean z, boolean z2) {
        float filterValidValue = filterValidValue();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            SlideLineView slideLineView = (SlideLineView) getChildAt(i);
            if (slideLineView.getX() + (slideLineView.getWidth() / 2.0f) <= filterValidValue()) {
                if (!slideLineView.isSelect()) {
                    slideLineView.setSelect(true);
                }
            } else {
                slideLineView.setSelect(false);
            }
        }
        if (z) {
            this.indicatorValue = ((filterValidValue - 16.0f) / this.workableTotalLength) * (this.endIndex - this.startIndex);
            float down = ((float) down(this.indicatorValue, this.decimal)) + this.startIndex;
            if (this.sliderProgressListener != null) {
                if ((z2 || down >= Math.min(this.currentUpdateIndex + this.accuracy, this.endIndex) || down <= Math.max(this.currentUpdateIndex - this.accuracy, this.startIndex)) && this.currentUpdateIndex != down) {
                    this.sliderProgressListener.onProgressChanged(this, down, this.unit);
                    this.currentUpdateIndex = down;
                    updateVui(this);
                }
            }
        }
    }

    private static double down(double d, int i) {
        if (i < 0) {
            throw new IllegalArgumentException();
        }
        return BigDecimal.valueOf(d).setScale(i, RoundingMode.DOWN).doubleValue();
    }

    @Override // com.xiaopeng.xui.widget.slider.AbsSlider
    public float getIndicatorValue() {
        return (((float) down(this.indicatorValue, this.decimal)) + this.startIndex) * this.mStep;
    }

    @Override // com.xiaopeng.xui.widget.slider.AbsSlider
    public void setAccuracy(float f) {
        this.accuracy = f;
    }

    public void setSliderProgressListener(SliderProgressListener sliderProgressListener) {
        this.sliderProgressListener = sliderProgressListener;
    }

    public void setProgressChangeListener(ProgressChangeListener progressChangeListener) {
        this.progressChangeListener = progressChangeListener;
    }

    public void setCurrentIndex(int i) {
        XLogUtils.d(TAG, "setCurrentIndex " + i + ", " + isAttachedToWindow());
        setCurrentIndex(i, false);
    }

    public void setCurrentIndex(final int i, final boolean z) {
        post(new Runnable() { // from class: com.xiaopeng.xui.widget.slider.-$$Lambda$XSlider$buTwNHKhwuANC8oohl4cISYoEkY
            @Override // java.lang.Runnable
            public final void run() {
                XSlider.this.lambda$setCurrentIndex$0$XSlider(i, z);
            }
        });
    }

    public /* synthetic */ void lambda$setCurrentIndex$0$XSlider(int i, boolean z) {
        ProgressChangeListener progressChangeListener;
        XLogUtils.i(TAG, "setCurrentIndex:" + i + ", fromUser:" + z);
        this.indicatorValue = i - this.startIndex;
        this.mProgressViewLength = calculateProgressView(this.indicatorValue);
        invalidate();
        notifyChildren(false, false);
        this.currentUpdateIndex = i;
        if (this.indicatorDrawable != null) {
            this.indicatorDrawable.updateCenter(filterValidValue(), getPopString(), getSliderLength());
        }
        if (z && (progressChangeListener = this.progressChangeListener) != null) {
            progressChangeListener.onProgressChanged(this, this.indicatorValue + this.startIndex, this.unit, true);
        }
        if (getVuiValue() != null && (getVuiValue() instanceof Float) && ((Float) getVuiValue()).floatValue() == getIndicatorValue()) {
            return;
        }
        updateVui(this);
    }

    @Override // com.xiaopeng.xui.widget.XViewGroup, android.view.View
    public void setEnabled(boolean z) {
        if (!z) {
            this.mIsDragging = false;
        }
        super.setEnabled(z);
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setEnabled(z);
        }
        setAlphaByEnable(z);
        invalidate();
    }

    public void setStartIndex(int i) {
        if (i == this.endIndex) {
            throw new RuntimeException("startIndex = endIndex!!!");
        }
        this.startIndex = i;
        post(new Runnable() { // from class: com.xiaopeng.xui.widget.slider.-$$Lambda$XSlider$turWKyA_2g8hu54RlpBdwwlK0bU
            @Override // java.lang.Runnable
            public final void run() {
                XSlider.this.lambda$setStartIndex$1$XSlider();
            }
        });
    }

    public /* synthetic */ void lambda$setStartIndex$1$XSlider() {
        if (this.indicatorDrawable != null) {
            this.indicatorDrawable.updateCenter(filterValidValue(), getPopString(), getSliderLength());
        }
        invalidate();
    }

    public void setEndIndex(int i) {
        if (this.startIndex == i) {
            throw new RuntimeException("startIndex = endIndex!!!");
        }
        this.endIndex = i;
        post(new Runnable() { // from class: com.xiaopeng.xui.widget.slider.-$$Lambda$XSlider$NlYOEbc-aIhKr4eN3ldsoNkFFy4
            @Override // java.lang.Runnable
            public final void run() {
                XSlider.this.lambda$setEndIndex$2$XSlider();
            }
        });
    }

    public /* synthetic */ void lambda$setEndIndex$2$XSlider() {
        invalidate();
    }

    public void setInitIndex(int i) {
        if (i > this.endIndex) {
            this.initIndex = this.endIndex;
        } else if (i < this.startIndex) {
            this.initIndex = this.startIndex;
        } else {
            this.initIndex = i;
            this.indicatorValue = i - this.startIndex;
            this.mProgressViewLength = calculateProgressView(this.indicatorValue);
            invalidate();
        }
    }

    @Override // com.xiaopeng.vui.commons.IVuiElementListener
    public VuiElement onBuildVuiElement(String str, IVuiElementBuilder iVuiElementBuilder) {
        if (getVuiElementType() == VuiElementType.STATEFULBUTTON) {
            return null;
        }
        setVuiValue(Float.valueOf(getIndicatorValue()));
        if (getVuiProps() != null && getVuiProps().has(VuiConstants.PROPS_SETPROPS) && getVuiProps().getBoolean(VuiConstants.PROPS_SETPROPS)) {
            return null;
        }
        JSONObject vuiProps = getVuiProps();
        if (vuiProps == null) {
            vuiProps = new JSONObject();
        }
        int i = this.mVuiMinValue;
        if (i == -1) {
            i = this.startIndex;
        }
        vuiProps.put(VuiConstants.PROPS_MINVALUE, i);
        int i2 = this.mVuiMaxValue;
        if (i2 == -1) {
            i2 = this.endIndex;
        }
        vuiProps.put(VuiConstants.PROPS_MAXVALUE, i2);
        float f = this.mVuiInterval;
        vuiProps.put(VuiConstants.PROPS_INTERVAL, f != -1.0f ? (int) f : (int) Math.ceil((this.endIndex - this.startIndex) / 10.0d));
        setVuiProps(vuiProps);
        return null;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElementListener
    public boolean onVuiElementEvent(View view, VuiEvent vuiEvent) {
        int round;
        logD("slider onVuiElementEvent");
        boolean z = false;
        if (view == null || getVuiElementType() == VuiElementType.STATEFULBUTTON) {
            return false;
        }
        Double d = (Double) vuiEvent.getEventValue(vuiEvent);
        if (d != null) {
            z = true;
            if (this.mStep == 1) {
                round = (int) Math.ceil(d.doubleValue());
            } else {
                round = (int) Math.round(d.doubleValue() / this.mStep);
            }
            if (round >= this.startIndex && round <= this.endIndex) {
                setCurrentIndex(round, true);
                post(new Runnable() { // from class: com.xiaopeng.xui.widget.slider.-$$Lambda$MSGQSSmVeXQ_7tsqb36Dk_DpkHw
                    @Override // java.lang.Runnable
                    public final void run() {
                        XSlider.this.showVuiFloating();
                    }
                });
            }
        }
        return z;
    }

    public void showVuiFloating() {
        int heightExIndicator = (int) ((getHeightExIndicator() / 2.0f) - (getSliderThickness() / 2));
        int sliderLength = ((int) (getOrientation() == 1 ? getSliderLength() - getProgressViewLength() : getProgressViewLength())) - (getSliderLength() / 2);
        int i = getOrientation() == 1 ? sliderLength : heightExIndicator;
        if (getOrientation() != 1) {
            heightExIndicator = sliderLength;
        }
        VuiFloatingLayerManager.show(this, heightExIndicator, i);
    }

    public float getVuiCurrentIndex() {
        return this.currentUpdateIndex;
    }
}

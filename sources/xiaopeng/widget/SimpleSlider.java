package xiaopeng.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.DecelerateInterpolator;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
/* loaded from: classes3.dex */
public class SimpleSlider extends View {
    public static final int DRAG_MODE_STEP = 0;
    public static final int DRAG_MODE_STEPLESS = 1;
    public static final int DRAG_MODE_STEP_ON_DROP = 2;
    private static final int MAX_LEVEL = 10000;
    private static final int MAX_VALUE = 100;
    private static final int MIN_VALUE = 0;
    private static final int NO_ALPHA = 255;
    public static final int ORIENTATION_HORIZONTAL = 0;
    public static final int ORIENTATION_VERTICAL = 1;
    private static final String TAG = "SimpleSlider";
    public static final int TICK_MARK_STYLE_ALL = 1;
    public static final int TICK_MARK_STYLE_CUSTOM = 2;
    public static final int TICK_MARK_STYLE_PROGRESS = 0;
    public static final int TOUCH_MODE_SEEK = 1;
    public static final int TOUCH_MODE_SLIDE = 0;
    private float dragMax;
    private float dragMin;
    private final List<OnSlideChangeListener> mChangeListenerList;
    private boolean mClipToPadding;
    private float mDisabledAlpha;
    protected int mDragMode;
    private boolean mEnabled;
    private int mMax;
    private boolean mMaxInitialized;
    private int mMin;
    private boolean mMinInitialized;
    private OnSlideChangeListener mOnSlideChangeListener;
    protected int mOrientation;
    private int mProgress;
    private Drawable mProgressDrawable;
    private int mProgressDrawableRes;
    private int mScaledTouchSlop;
    private int mSecondaryProgress;
    private float mSlideScale;
    private Drawable mThumb;
    private int mThumbOffset;
    private int mThumbRes;
    private Drawable mTickMark;
    private float mTickMarkPadding;
    private Set<String> mTickMarkPositions;
    private boolean mTickMarkProgressEnd;
    private int mTickMarkRes;
    private int mTickMarkStyle;
    private TouchEventHandler mTouchEventHandler;
    protected int mTouchMode;
    private final ValueAnimator.AnimatorUpdateListener mUpdateListener;
    private ValueAnimator mValueAnimator;

    /* loaded from: classes3.dex */
    public interface OnSlideChangeListener {
        void onProgressChanged(SimpleSlider simpleSlider, int i, boolean z);

        void onStartTrackingTouch(SimpleSlider simpleSlider);

        void onStopTrackingTouch(SimpleSlider simpleSlider);
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface TickMarkStyle {
    }

    /* loaded from: classes3.dex */
    public interface TouchEventHandler {
        boolean onTouchEvent(MotionEvent motionEvent);
    }

    public SimpleSlider(Context context) {
        this(context, null);
    }

    public SimpleSlider(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.style.SimpleSlider);
    }

    public SimpleSlider(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, R.style.SimpleSlider);
    }

    public SimpleSlider(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mOrientation = 0;
        this.mProgress = 0;
        this.mSecondaryProgress = 0;
        this.mMin = 0;
        this.mMax = 100;
        this.mMaxInitialized = false;
        this.mMinInitialized = false;
        this.mSlideScale = 1.0f;
        this.mProgressDrawableRes = -1;
        this.mTickMarkRes = -1;
        this.mTickMarkStyle = 0;
        this.mTickMarkPadding = 0.0f;
        this.mTickMarkProgressEnd = false;
        this.mThumbRes = -1;
        this.mTouchMode = 0;
        this.mDragMode = 0;
        this.mEnabled = true;
        this.mClipToPadding = true;
        this.mUpdateListener = new ValueAnimator.AnimatorUpdateListener() { // from class: xiaopeng.widget.SimpleSlider.1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                Drawable findProgressDrawable = SimpleSlider.this.findProgressDrawable(16908301);
                if (findProgressDrawable != null) {
                    findProgressDrawable.setLevel((int) (10000.0f * floatValue));
                }
                Drawable drawable = SimpleSlider.this.mThumb;
                if (drawable != null) {
                    SimpleSlider simpleSlider = SimpleSlider.this;
                    simpleSlider.setThumbPos(simpleSlider.getSliderWidth(), drawable, floatValue, Integer.MIN_VALUE);
                    SimpleSlider.this.invalidate();
                }
            }
        };
        this.mChangeListenerList = new ArrayList(4);
        init(context, attributeSet, i, i2);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        setEnabled(this.mEnabled);
    }

    private void init(Context context, AttributeSet attributeSet, int i, int i2) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.SimpleSlider, i, i2);
        this.mProgressDrawableRes = obtainStyledAttributes.getResourceId(R.styleable.SimpleSlider_android_progressDrawable, this.mProgressDrawableRes);
        setProgressDrawable(obtainStyledAttributes.getDrawable(R.styleable.SimpleSlider_android_progressDrawable));
        this.mTickMarkRes = obtainStyledAttributes.getResourceId(R.styleable.SimpleSlider_android_tickMark, this.mTickMarkRes);
        setTickMark(obtainStyledAttributes.getDrawable(R.styleable.SimpleSlider_android_tickMark));
        this.mThumbRes = obtainStyledAttributes.getResourceId(R.styleable.SimpleSlider_android_thumb, this.mThumbRes);
        setThumb(obtainStyledAttributes.getDrawable(R.styleable.SimpleSlider_android_thumb));
        this.mOrientation = obtainStyledAttributes.getInt(R.styleable.SimpleSlider_android_orientation, 0);
        this.mTickMarkStyle = obtainStyledAttributes.getInt(R.styleable.SimpleSlider_ss_tickMarkStyle, this.mTickMarkStyle);
        String string = obtainStyledAttributes.getString(R.styleable.SimpleSlider_ss_tickMarkPositions);
        if (string != null) {
            setTickMartPositions(string);
        }
        this.mTickMarkPadding = obtainStyledAttributes.getDimension(R.styleable.SimpleSlider_ss_tickMark_padding, this.mTickMarkPadding);
        this.mTickMarkProgressEnd = obtainStyledAttributes.getBoolean(R.styleable.SimpleSlider_ss_tickMark_ProgressEnd, this.mTickMarkProgressEnd);
        this.mSlideScale = obtainStyledAttributes.getFloat(R.styleable.SimpleSlider_ss_slideScale, this.mSlideScale);
        this.mEnabled = obtainStyledAttributes.getBoolean(R.styleable.SimpleSlider_android_enabled, this.mEnabled);
        this.mDisabledAlpha = obtainStyledAttributes.getFloat(R.styleable.SimpleSlider_android_disabledAlpha, 0.36f);
        setMin(obtainStyledAttributes.getInt(R.styleable.SimpleSlider_android_min, this.mMin));
        setMax(obtainStyledAttributes.getInt(R.styleable.SimpleSlider_android_max, this.mMax));
        this.dragMin = obtainStyledAttributes.getFloat(R.styleable.SimpleSlider_drag_min, this.mMin);
        float f = obtainStyledAttributes.getFloat(R.styleable.SimpleSlider_drag_max, this.mMax);
        this.dragMax = f;
        if (this.dragMin > f) {
            this.dragMin = f;
        }
        setProgress(obtainStyledAttributes.getInt(R.styleable.SimpleSlider_android_progress, this.mProgress));
        setSecondaryProgress(obtainStyledAttributes.getInt(R.styleable.SimpleSlider_android_secondaryProgress, this.mSecondaryProgress));
        int i3 = obtainStyledAttributes.getInt(R.styleable.SimpleSlider_ss_touchMode, this.mTouchMode);
        this.mTouchMode = i3;
        this.mTouchEventHandler = generateTouchEventHandler(i3);
        this.mDragMode = obtainStyledAttributes.getInt(R.styleable.SimpleSlider_ss_dragMode, this.mDragMode);
        this.mClipToPadding = obtainStyledAttributes.getBoolean(R.styleable.SimpleSlider_android_clipToPadding, true);
        obtainStyledAttributes.recycle();
        this.mScaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    protected TouchEventHandler generateTouchEventHandler(int i) {
        SimpleTouchEventHandler slideHandler;
        if (i == 1) {
            slideHandler = new SeekHandler();
        } else {
            slideHandler = new SlideHandler();
        }
        slideHandler.setSimpleSlider(this);
        return slideHandler;
    }

    @Override // android.view.View
    public void drawableHotspotChanged(float f, float f2) {
        super.drawableHotspotChanged(f, f2);
        Drawable drawable = this.mProgressDrawable;
        if (drawable != null) {
            drawable.setHotspot(f, f2);
        }
    }

    int getScaledTouchSlop() {
        return this.mScaledTouchSlop;
    }

    public int getSliderPaddingStart() {
        if (isVertical()) {
            return getPaddingBottom();
        }
        return getPaddingStart();
    }

    public int getSliderPaddingEnd() {
        if (isVertical()) {
            return getPaddingTop();
        }
        return getPaddingEnd();
    }

    public int getSliderPaddingTop() {
        if (isVertical()) {
            return getPaddingStart();
        }
        return getPaddingTop();
    }

    public int getSliderPaddingBottom() {
        if (isVertical()) {
            return getPaddingEnd();
        }
        return getPaddingBottom();
    }

    public int getSliderWidth() {
        if (isVertical()) {
            return getHeight();
        }
        return getWidth();
    }

    public int getSliderHeight() {
        if (isVertical()) {
            return getWidth();
        }
        return getHeight();
    }

    public void setTickMartPositions(String str) {
        String[] split = str.split("\\D");
        Log.d(TAG, "setTickMartPositions: " + Arrays.toString(split) + ", string:" + str);
        if (split.length != 0) {
            HashSet hashSet = new HashSet(split.length);
            this.mTickMarkPositions = hashSet;
            hashSet.addAll(Arrays.asList(split));
        }
        invalidate();
    }

    public void setTouchEventHandler(TouchEventHandler touchEventHandler) {
        this.mTouchEventHandler = touchEventHandler;
        if (touchEventHandler instanceof SimpleTouchEventHandler) {
            ((SimpleTouchEventHandler) touchEventHandler).setSimpleSlider(this);
        }
    }

    public void refreshVisual() {
        if (this.mProgressDrawableRes != -1) {
            setProgressDrawable(getContext().getDrawable(this.mProgressDrawableRes));
        }
        if (this.mTickMarkRes != -1) {
            setTickMark(getContext().getDrawable(this.mTickMarkRes));
        }
        if (this.mThumbRes != -1) {
            setThumb(getContext().getDrawable(this.mThumbRes));
        }
        int range = getRange();
        setVisualProgress(16908301, range > 0 ? (getProgress() - getMin()) / range : 0.0f);
    }

    public int getMin() {
        return this.mMin;
    }

    public void setMin(int i) {
        int i2;
        boolean z = this.mMaxInitialized;
        if (z && i > (i2 = this.mMax)) {
            i = i2;
        }
        this.mMinInitialized = true;
        if (z && i != this.mMin) {
            this.mMin = i;
            postInvalidate();
            if (this.mProgress < i) {
                this.mProgress = i;
            }
            setProgress(this.mProgress);
            return;
        }
        this.mMin = i;
    }

    public int getMax() {
        return this.mMax;
    }

    public void setMax(int i) {
        int i2;
        boolean z = this.mMinInitialized;
        if (z && i < (i2 = this.mMin)) {
            i = i2;
        }
        this.mMaxInitialized = true;
        if (z && i != this.mMax) {
            this.mMax = i;
            postInvalidate();
            if (this.mProgress > i) {
                this.mProgress = i;
            }
            setProgress(this.mProgress);
            return;
        }
        this.mMax = i;
    }

    public int getRange() {
        return this.mMax - this.mMin;
    }

    public int getProgress() {
        return this.mProgress;
    }

    public void setOnSlideChangeListener(OnSlideChangeListener onSlideChangeListener) {
        this.mOnSlideChangeListener = onSlideChangeListener;
    }

    public void addOnSliderChangeListener(OnSlideChangeListener onSlideChangeListener) {
        if (onSlideChangeListener == null) {
            return;
        }
        this.mChangeListenerList.add(onSlideChangeListener);
    }

    public void removeOnSliderChangeListener(OnSlideChangeListener onSlideChangeListener) {
        if (onSlideChangeListener == null) {
            return;
        }
        this.mChangeListenerList.remove(onSlideChangeListener);
    }

    public void setProgress(int i) {
        float f = this.dragMin;
        if (i < f) {
            i = (int) f;
        }
        float f2 = this.dragMax;
        if (i > f2) {
            i = (int) f2;
        }
        setProgressInternal(i, false, false);
    }

    public void setDragRange(int i, int i2) {
        if (i > i2) {
            return;
        }
        this.dragMin = i;
        this.dragMax = i2;
        if (this.mProgress < i) {
            setProgress(i);
        }
        if (this.mProgress > i2) {
            setProgress(i2);
        }
    }

    public void setSecondaryProgress(int i) {
        int i2 = this.mMin;
        if (i < i2) {
            i = i2;
        }
        int i3 = this.mMax;
        if (i > i3) {
            i = i3;
        }
        if (i != this.mSecondaryProgress) {
            this.mSecondaryProgress = i;
            refreshProgress(16908303, i, false, false);
        }
    }

    public int getOrientation() {
        return this.mOrientation;
    }

    public boolean isVertical() {
        return this.mOrientation == 1;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setProgressInternal(int i, boolean z, boolean z2) {
        refreshProgress(16908301, i, z, z2);
    }

    private void refreshProgress(int i, int i2, boolean z, boolean z2) {
        boolean z3 = i == 16908301;
        if (z3 && z2) {
            animateProgress(i2, z);
            return;
        }
        setVisualProgress(i, getVisualScale(i2));
        if (z3) {
            setProgressAndDispatch(i2, z);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onAnimationFinish(int i, boolean z) {
        ValueAnimator valueAnimator = this.mValueAnimator;
        if (valueAnimator != null) {
            valueAnimator.removeAllListeners();
            this.mValueAnimator.removeAllUpdateListeners();
        }
        if (i >= 0) {
            setProgressAndDispatch(i, z);
        }
    }

    private void setProgressAndDispatch(int i, boolean z) {
        Log.d(TAG, "setProgressInternal, progress:" + i + ", fromUser:" + z);
        int limitProgress = limitProgress(i);
        this.mProgress = limitProgress;
        onProgressChanged(limitProgress, z);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public float getCurrentVisualScale() {
        Drawable findProgressDrawable = findProgressDrawable(16908301);
        if (findProgressDrawable == null) {
            return 0.0f;
        }
        return findProgressDrawable.getLevel() / 10000.0f;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Drawable findProgressDrawable(int i) {
        Drawable drawable = this.mProgressDrawable;
        if (drawable instanceof LayerDrawable) {
            Drawable findDrawableByLayerId = ((LayerDrawable) drawable).findDrawableByLayerId(i);
            return findDrawableByLayerId == null ? this.mProgressDrawable : findDrawableByLayerId;
        }
        return drawable;
    }

    private float getVisualScale(int i) {
        int limitProgress = limitProgress(i);
        int i2 = this.mMax;
        int i3 = this.mMin;
        int i4 = i2 - i3;
        if (i4 > 0) {
            return (limitProgress - i3) / i4;
        }
        return 0.0f;
    }

    private float getVisualScaleFromTouch(float f, int i) {
        return Math.min(Math.max(0.0f, f + (i / (getMax() - getMin()))), 1.0f);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setProgressFromTouch(float f, int i, boolean z, boolean z2, boolean z3) {
        float f2 = i;
        float max = getMax() - getMin();
        float min = (f * max) + f2 + getMin();
        float f3 = this.dragMin;
        if (min < f3) {
            f = ((f3 - getMin()) - f2) / max;
            min = f3;
        }
        float f4 = this.dragMax;
        if (min > f4) {
            f = ((f4 - getMin()) - f2) / max;
            min = f4;
        }
        int i2 = this.mDragMode;
        if (i2 == 1) {
            float visualScaleFromTouch = getVisualScaleFromTouch(f, i);
            setVisualProgress(16908301, visualScaleFromTouch);
            Log.d(TAG, "setProgressFromTouch, scale:" + visualScaleFromTouch + ", floatProgress:" + min + ", current:" + getProgress());
            int limitProgress = limitProgress(Math.round(min));
            if (z2 || (Math.abs(min - getProgress()) >= 1.0f && limitProgress != this.mProgress)) {
                Log.i(TAG, "setProgressFromTouch, SET floatProgress:" + min + ", current:" + getProgress() + ", newProgress:" + limitProgress);
                setProgressAndDispatch(limitProgress, z);
            }
        } else if (i2 == 2) {
            int limitProgress2 = limitProgress(Math.round(min));
            if (z2) {
                setProgressInternal(limitProgress2, z, z3);
                return;
            }
            setVisualProgress(16908301, getVisualScaleFromTouch(f, i));
            if (Math.abs(min - this.mProgress) < 1.0f || limitProgress2 == this.mProgress) {
                return;
            }
            setProgressAndDispatch(limitProgress2, z);
        } else {
            int limitProgress3 = limitProgress(Math.round(min));
            if (limitProgress3 != getProgress()) {
                setProgressInternal(limitProgress3, z, false);
            }
        }
    }

    public float getSlideScale() {
        return this.mSlideScale;
    }

    public void setSlideScale(float f) {
        this.mSlideScale = f;
    }

    public void setProgressDrawable(Drawable drawable) {
        Drawable drawable2 = this.mProgressDrawable;
        if (drawable2 != drawable) {
            if (drawable2 != null) {
                drawable2.setCallback(null);
                unscheduleDrawable(this.mProgressDrawable);
            }
            this.mProgressDrawable = drawable;
            if (drawable != null) {
                drawable.setCallback(this);
                drawable.setLayoutDirection(getLayoutDirection());
                if (drawable.isStateful()) {
                    drawable.setState(getDrawableState());
                }
                updateDrawableBounds(getSliderWidth(), getSliderHeight());
                updateDrawableState();
            }
            refreshProgress(16908301, this.mProgress, false, false);
            refreshProgress(16908303, this.mSecondaryProgress, false, false);
        }
    }

    public void setTickMark(Drawable drawable) {
        Drawable drawable2 = this.mTickMark;
        if (drawable2 != null) {
            drawable2.setCallback(null);
        }
        this.mTickMark = drawable;
        if (drawable != null) {
            drawable.setCallback(this);
            drawable.setLayoutDirection(getLayoutDirection());
            if (drawable.isStateful()) {
                drawable.setState(getDrawableState());
            }
        }
        invalidate();
    }

    public Drawable getTickMark() {
        return this.mTickMark;
    }

    public void setThumb(Drawable drawable) {
        boolean z;
        Drawable drawable2 = this.mThumb;
        if (drawable2 == null || drawable == drawable2) {
            z = false;
        } else {
            drawable2.setCallback(null);
            z = true;
        }
        if (drawable != null) {
            drawable.setCallback(this);
            if (canResolveLayoutDirection()) {
                drawable.setLayoutDirection(getLayoutDirection());
            }
            this.mThumbOffset = drawable.getIntrinsicWidth() / 2;
            if (z && (drawable.getIntrinsicWidth() != this.mThumb.getIntrinsicWidth() || drawable.getIntrinsicHeight() != this.mThumb.getIntrinsicHeight())) {
                requestLayout();
            }
        }
        this.mThumb = drawable;
        invalidate();
        if (z) {
            updateThumbAndTrackPos(getSliderWidth(), getSliderHeight());
            if (drawable == null || !drawable.isStateful()) {
                return;
            }
            drawable.setState(getDrawableState());
        }
    }

    private void updateThumbAndTrackPos(int i, int i2) {
        int i3;
        int i4;
        int sliderPaddingTop = (i2 - getSliderPaddingTop()) - getSliderPaddingBottom();
        Drawable drawable = this.mProgressDrawable;
        Drawable drawable2 = this.mThumb;
        if (drawable.getIntrinsicHeight() > 0) {
            i2 = drawable.getIntrinsicHeight();
        }
        int intrinsicHeight = drawable2 == null ? 0 : drawable2.getIntrinsicHeight();
        if (intrinsicHeight > i2) {
            i4 = (sliderPaddingTop - intrinsicHeight) / 2;
            i3 = ((intrinsicHeight - i2) / 2) + i4;
        } else {
            int i5 = (sliderPaddingTop - i2) / 2;
            int i6 = ((i2 - intrinsicHeight) / 2) + i5;
            i3 = i5;
            i4 = i6;
        }
        if (drawable != null) {
            int sliderPaddingStart = (this.mClipToPadding ? (-getSliderPaddingEnd()) - getSliderPaddingStart() : 0) + i;
            if (!this.mClipToPadding) {
                drawable.setHotspotBounds(getSliderPaddingStart(), 0, getSliderWidth() - getSliderPaddingEnd(), 0);
            }
            drawable.setBounds(0, i3, sliderPaddingStart, i2 + i3);
        }
        if (drawable2 != null) {
            setThumbPos(i, drawable2, getVisualScale(getProgress()), i4);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setThumbPos(int i, Drawable drawable, float f, int i2) {
        int i3;
        int sliderPaddingEnd = (i - getSliderPaddingEnd()) - getSliderPaddingStart();
        int intrinsicWidth = drawable.getIntrinsicWidth();
        int intrinsicHeight = drawable.getIntrinsicHeight();
        int i4 = (sliderPaddingEnd - intrinsicWidth) + (this.mThumbOffset * 2);
        int i5 = (int) ((f * i4) + 0.5f);
        if (i2 == Integer.MIN_VALUE) {
            Rect bounds = drawable.getBounds();
            int i6 = bounds.top;
            i3 = bounds.bottom;
            i2 = i6;
        } else {
            i3 = intrinsicHeight + i2;
        }
        if (getLayoutDirection() == 1) {
            i5 = i4 - i5;
        }
        int i7 = intrinsicWidth + i5;
        Drawable background = getBackground();
        if (background != null) {
            int sliderPaddingStart = getSliderPaddingStart() - this.mThumbOffset;
            int sliderPaddingTop = getSliderPaddingTop();
            background.setHotspotBounds(i5 + sliderPaddingStart, i2 + sliderPaddingTop, sliderPaddingStart + i7, sliderPaddingTop + i3);
        }
        drawable.setBounds(i5, i2, i7, i3);
    }

    private void updateDrawableBounds(int i, int i2) {
        int sliderPaddingEnd = i - (getSliderPaddingEnd() + getSliderPaddingStart());
        int sliderPaddingTop = i2 - (getSliderPaddingTop() + getSliderPaddingBottom());
        Drawable drawable = this.mProgressDrawable;
        if (drawable != null) {
            drawable.setBounds(0, 0, sliderPaddingEnd, sliderPaddingTop);
        }
    }

    private void updateDrawableState() {
        int[] drawableState = getDrawableState();
        Drawable drawable = this.mProgressDrawable;
        if ((drawable == null || !drawable.isStateful()) ? false : drawable.setState(drawableState)) {
            invalidate();
        }
    }

    protected void setVisualProgress(int i, float f) {
        Drawable drawable;
        Drawable findProgressDrawable = findProgressDrawable(i);
        if (findProgressDrawable != null) {
            findProgressDrawable.setLevel((int) (10000.0f * f));
            if (!(i == 16908301) || (drawable = this.mThumb) == null) {
                return;
            }
            setThumbPos(getSliderWidth(), drawable, f, Integer.MIN_VALUE);
            invalidate();
            return;
        }
        invalidate();
    }

    private void animateProgress(final int i, final boolean z) {
        float visualScale = getVisualScale(i);
        float currentVisualScale = getCurrentVisualScale();
        ValueAnimator valueAnimator = this.mValueAnimator;
        if (valueAnimator != null) {
            valueAnimator.removeAllListeners();
            this.mValueAnimator.removeAllUpdateListeners();
            this.mValueAnimator.cancel();
        }
        ValueAnimator ofFloat = ValueAnimator.ofFloat(currentVisualScale, visualScale);
        this.mValueAnimator = ofFloat;
        ofFloat.setDuration(100L);
        this.mValueAnimator.setInterpolator(new DecelerateInterpolator(1.5f));
        this.mValueAnimator.addUpdateListener(this.mUpdateListener);
        this.mValueAnimator.addListener(new Animator.AnimatorListener() { // from class: xiaopeng.widget.SimpleSlider.2
            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationRepeat(Animator animator) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                SimpleSlider.this.onAnimationFinish(i, z);
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator) {
                SimpleSlider.this.onAnimationFinish(i, z);
            }
        });
        this.mValueAnimator.start();
    }

    @Override // android.view.View
    protected void drawableStateChanged() {
        Drawable drawable = this.mProgressDrawable;
        if (drawable != null) {
            drawable.setAlpha(isEnabled() ? 255 : (int) (this.mDisabledAlpha * 255.0f));
        }
        Drawable drawable2 = this.mTickMark;
        if (drawable2 != null) {
            drawable2.setAlpha(isEnabled() ? 255 : (int) (this.mDisabledAlpha * 255.0f));
        }
        super.drawableStateChanged();
        Drawable drawable3 = this.mProgressDrawable;
        if (drawable3 != null && drawable3.isStateful() && drawable3.setState(getDrawableState())) {
            invalidateDrawable(drawable3);
        }
        Drawable drawable4 = this.mThumb;
        if (drawable4 != null && drawable4.isStateful() && drawable4.setState(getDrawableState())) {
            invalidateDrawable(drawable4);
        }
        Drawable drawable5 = this.mTickMark;
        if (drawable5 != null && drawable5.isStateful() && drawable5.setState(getDrawableState())) {
            invalidateDrawable(drawable5);
        }
    }

    @Override // android.view.View
    protected boolean verifyDrawable(Drawable drawable) {
        return drawable == this.mThumb || drawable == this.mTickMark || drawable == this.mProgressDrawable || super.verifyDrawable(drawable);
    }

    @Override // android.view.View
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (isEnabled()) {
            int actionMasked = motionEvent.getActionMasked();
            if (actionMasked == 0) {
                setPressed(true);
            } else if (actionMasked == 1 || actionMasked == 3) {
                setPressed(false);
            }
            return super.dispatchTouchEvent(motionEvent);
        }
        return true;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (isEnabled()) {
            TouchEventHandler touchEventHandler = this.mTouchEventHandler;
            if (touchEventHandler != null) {
                return touchEventHandler.onTouchEvent(motionEvent);
            }
            return super.onTouchEvent(motionEvent);
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void attemptClaimDrag() {
        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        if (isVertical()) {
            i2 = i;
            i = i2;
        }
        updateThumbAndTrackPos(i, i2);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (isVertical()) {
            canvas.translate(0.0f, getHeight());
            canvas.rotate(270.0f);
        }
        drawTrack(canvas);
        drawTickMarks(canvas);
        drawThumb(canvas);
    }

    @Override // android.view.View
    public void onVisibilityAggregated(boolean z) {
        super.onVisibilityAggregated(z);
        if (z) {
            return;
        }
        Drawable drawable = this.mProgressDrawable;
        if (drawable instanceof Animatable) {
            ((Animatable) drawable).stop();
        }
        Drawable drawable2 = this.mProgressDrawable;
        if (drawable2 instanceof LayerDrawable) {
            Drawable findDrawableByLayerId = ((LayerDrawable) drawable2).findDrawableByLayerId(16908301);
            if (findDrawableByLayerId instanceof Animatable) {
                ((Animatable) findDrawableByLayerId).stop();
            }
            Drawable findDrawableByLayerId2 = ((LayerDrawable) this.mProgressDrawable).findDrawableByLayerId(16908288);
            if (findDrawableByLayerId2 instanceof Animatable) {
                ((Animatable) findDrawableByLayerId2).stop();
            }
        }
    }

    protected void drawTrack(Canvas canvas) {
        if (this.mProgressDrawable != null) {
            int save = canvas.save();
            canvas.translate(this.mClipToPadding ? getSliderPaddingStart() : 0.0f, getSliderPaddingTop());
            this.mProgressDrawable.draw(canvas);
            canvas.restoreToCount(save);
        }
    }

    protected void drawTickMarks(Canvas canvas) {
        Drawable tickMark = getTickMark();
        if (tickMark != null) {
            int max = getMax() - getMin();
            if (max > 1) {
                int intrinsicWidth = tickMark.getIntrinsicWidth();
                int intrinsicHeight = tickMark.getIntrinsicHeight();
                int i = intrinsicWidth >= 0 ? intrinsicWidth / 2 : 1;
                int i2 = intrinsicHeight >= 0 ? intrinsicHeight / 2 : 1;
                tickMark.setBounds(-i, -i2, i, i2);
                float sliderWidth = (((getSliderWidth() - getSliderPaddingStart()) - getSliderPaddingEnd()) - (this.mTickMarkPadding * 2.0f)) / max;
                int save = canvas.save();
                canvas.translate(getSliderPaddingStart(), getSliderHeight() >> 1);
                canvas.translate(this.mTickMarkPadding + sliderWidth, 0.0f);
                for (int i3 = 1; i3 < max; i3++) {
                    if (canDrawTickMark(i3)) {
                        tickMark.draw(canvas);
                    }
                    canvas.translate(sliderWidth, 0.0f);
                }
                canvas.restoreToCount(save);
            }
        }
    }

    protected boolean canDrawTickMark(int i) {
        int i2 = this.mTickMarkStyle;
        if (i2 != 0) {
            return i2 != 2 || this.mTickMarkPositions.contains(new StringBuilder().append(i).append("").toString());
        }
        boolean z = this.mTickMarkProgressEnd;
        if (!z || i <= this.mProgress) {
            return z || i < this.mProgress;
        }
        return false;
    }

    protected void drawThumb(Canvas canvas) {
        if (this.mThumb != null) {
            int save = canvas.save();
            canvas.translate(getSliderPaddingStart() - this.mThumbOffset, getSliderPaddingTop());
            this.mThumb.draw(canvas);
            canvas.restoreToCount(save);
        }
    }

    protected void onProgressChanged(int i, boolean z) {
        OnSlideChangeListener onSlideChangeListener = this.mOnSlideChangeListener;
        if (onSlideChangeListener != null) {
            onSlideChangeListener.onProgressChanged(this, i, z);
        }
        for (OnSlideChangeListener onSlideChangeListener2 : this.mChangeListenerList) {
            onSlideChangeListener2.onProgressChanged(this, i, z);
        }
    }

    protected void onStartTrackingTouch() {
        OnSlideChangeListener onSlideChangeListener = this.mOnSlideChangeListener;
        if (onSlideChangeListener != null) {
            onSlideChangeListener.onStartTrackingTouch(this);
        }
        for (OnSlideChangeListener onSlideChangeListener2 : this.mChangeListenerList) {
            onSlideChangeListener2.onStartTrackingTouch(this);
        }
    }

    protected void onStopTrackingTouch() {
        OnSlideChangeListener onSlideChangeListener = this.mOnSlideChangeListener;
        if (onSlideChangeListener != null) {
            onSlideChangeListener.onStopTrackingTouch(this);
        }
        for (OnSlideChangeListener onSlideChangeListener2 : this.mChangeListenerList) {
            onSlideChangeListener2.onStopTrackingTouch(this);
        }
    }

    private int limitProgress(int i) {
        return Math.min(this.mMax, Math.max(i, this.mMin));
    }

    public ViewGroup getScrollingContainer() {
        for (ViewParent parent = getParent(); parent instanceof ViewGroup; parent = parent.getParent()) {
            ViewGroup viewGroup = (ViewGroup) parent;
            if (viewGroup.shouldDelayChildPressedState()) {
                return viewGroup;
            }
        }
        return null;
    }

    /* loaded from: classes3.dex */
    public static abstract class SimpleTouchEventHandler implements TouchEventHandler {
        protected SimpleSlider mSimpleSlider;

        @Override // xiaopeng.widget.SimpleSlider.TouchEventHandler
        public boolean onTouchEvent(MotionEvent motionEvent) {
            return false;
        }

        public void setSimpleSlider(SimpleSlider simpleSlider) {
            this.mSimpleSlider = simpleSlider;
        }

        protected boolean startDragDirectly() {
            ViewGroup scrollingContainer = this.mSimpleSlider.getScrollingContainer();
            boolean z = (scrollingContainer != null && scrollingContainer.canScrollVertically(1)) || (scrollingContainer != null && scrollingContainer.canScrollVertically(-1));
            boolean z2 = (scrollingContainer != null && scrollingContainer.canScrollHorizontally(-1)) || (scrollingContainer != null && scrollingContainer.canScrollHorizontally(1));
            if (this.mSimpleSlider.isVertical()) {
                if (z2) {
                    return false;
                }
            } else if (z) {
                return false;
            }
            return true;
        }

        protected void setProgress(float f, int i, boolean z, boolean z2, boolean z3) {
            SimpleSlider simpleSlider = this.mSimpleSlider;
            if (simpleSlider != null) {
                simpleSlider.setProgressFromTouch(f, i, z, z2, z3);
            }
        }
    }

    /* loaded from: classes3.dex */
    public static class SlideHandler extends SimpleTouchEventHandler {
        private boolean mIsDragging;
        private int mTouchDownProgress;
        private float mTouchDownX;
        private float mTouchDownY;

        @Override // xiaopeng.widget.SimpleSlider.SimpleTouchEventHandler, xiaopeng.widget.SimpleSlider.TouchEventHandler
        public boolean onTouchEvent(MotionEvent motionEvent) {
            super.onTouchEvent(motionEvent);
            return handleSlide(motionEvent);
        }

        private void startDrag(MotionEvent motionEvent) {
            this.mSimpleSlider.attemptClaimDrag();
            this.mTouchDownProgress = this.mSimpleSlider.getProgress();
            this.mIsDragging = true;
            this.mSimpleSlider.onStartTrackingTouch();
            trackTouchEvent(motionEvent);
        }

        private boolean handleSlide(MotionEvent motionEvent) {
            int actionMasked = motionEvent.getActionMasked();
            if (actionMasked == 0) {
                this.mTouchDownX = motionEvent.getX();
                this.mTouchDownY = motionEvent.getY();
                if (startDragDirectly()) {
                    startDrag(motionEvent);
                }
            } else if (actionMasked != 1) {
                if (actionMasked == 2) {
                    if (this.mIsDragging) {
                        trackTouchEvent(motionEvent);
                    } else {
                        if ((this.mSimpleSlider.isVertical() ? Math.abs(motionEvent.getY() - this.mTouchDownY) : Math.abs(motionEvent.getX() - this.mTouchDownX)) > this.mSimpleSlider.getScaledTouchSlop()) {
                            startDrag(motionEvent);
                        }
                    }
                } else if (actionMasked == 3 && this.mIsDragging) {
                    this.mSimpleSlider.onStopTrackingTouch();
                    this.mIsDragging = false;
                }
            } else if (this.mIsDragging) {
                trackTouchEvent(motionEvent);
                this.mSimpleSlider.onStopTrackingTouch();
                this.mIsDragging = false;
            }
            return true;
        }

        private void trackTouchEvent(MotionEvent motionEvent) {
            float round = ((this.mSimpleSlider.isVertical() ? Math.round(this.mTouchDownY - motionEvent.getY()) : Math.round(motionEvent.getX() - this.mTouchDownX)) / ((this.mSimpleSlider.getSliderWidth() - this.mSimpleSlider.getSliderPaddingStart()) - this.mSimpleSlider.getSliderPaddingEnd())) * this.mSimpleSlider.getSlideScale();
            boolean z = motionEvent.getAction() == 3 || motionEvent.getAction() == 1;
            setProgress(round, this.mTouchDownProgress, true, z, z && this.mIsDragging);
        }
    }

    /* loaded from: classes3.dex */
    public static class SeekHandler extends SimpleTouchEventHandler {
        private boolean mIsDragging;
        private float mTouchDownX;
        private float mTouchDownY;

        @Override // xiaopeng.widget.SimpleSlider.SimpleTouchEventHandler, xiaopeng.widget.SimpleSlider.TouchEventHandler
        public boolean onTouchEvent(MotionEvent motionEvent) {
            return handleSeekTouch(motionEvent);
        }

        private boolean handleSeekTouch(MotionEvent motionEvent) {
            int action = motionEvent.getAction();
            if (action != 0) {
                if (action != 1) {
                    if (action == 2) {
                        if (this.mIsDragging) {
                            trackTouchEvent(motionEvent);
                        } else {
                            if ((this.mSimpleSlider.isVertical() ? Math.abs(motionEvent.getY() - this.mTouchDownY) : Math.abs(motionEvent.getX() - this.mTouchDownX)) > this.mSimpleSlider.getScaledTouchSlop()) {
                                onStartTrackingTouch();
                                trackTouchEvent(motionEvent);
                                this.mSimpleSlider.attemptClaimDrag();
                            }
                        }
                    } else if (action == 3 && this.mIsDragging) {
                        onStopTrackingTouch();
                    }
                } else if (this.mIsDragging) {
                    trackTouchEvent(motionEvent);
                    onStopTrackingTouch();
                } else {
                    onStartTrackingTouch();
                    trackTouchEvent(motionEvent);
                    onStopTrackingTouch();
                }
            } else if (!startDragDirectly()) {
                this.mTouchDownX = motionEvent.getX();
                this.mTouchDownY = motionEvent.getY();
            } else {
                onStartTrackingTouch();
                trackTouchEvent(motionEvent);
                this.mSimpleSlider.attemptClaimDrag();
            }
            return true;
        }

        protected void onStartTrackingTouch() {
            this.mIsDragging = true;
            this.mSimpleSlider.onStartTrackingTouch();
        }

        protected void onStopTrackingTouch() {
            this.mIsDragging = false;
            this.mSimpleSlider.onStopTrackingTouch();
        }

        protected void trackTouchEvent(MotionEvent motionEvent) {
            float height = (this.mSimpleSlider.isVertical() ? this.mSimpleSlider.getHeight() - motionEvent.getY() : motionEvent.getX()) / ((this.mSimpleSlider.getSliderWidth() - this.mSimpleSlider.getSliderPaddingStart()) - this.mSimpleSlider.getSliderPaddingEnd());
            boolean z = motionEvent.getAction() == 3 || motionEvent.getAction() == 1;
            setProgress(height, 0, true, z, z && this.mIsDragging);
        }
    }
}

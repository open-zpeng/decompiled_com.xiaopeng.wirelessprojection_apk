package com.xiaopeng.xui.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.ViewPager;
/* loaded from: classes2.dex */
public class XLeftSlideView extends XLinearLayout {
    public static final String TAG = "LeftSlideView";
    private boolean isReCompute;
    private int mAnimDuring;
    private CardView mCardView;
    private View mContentView;
    private Context mContext;
    private int mDelLength;
    private float mInitX;
    private float mInitY;
    private View mMenuView;
    private int mRightCanSlide;
    private OnDelViewStatusChangeLister mStatusChangeLister;
    private int mTouchSlop;
    private ValueAnimator mValueAnimator;
    private ViewGroup mVerticalScrollableView;
    private ViewPager mViewPager;

    /* loaded from: classes2.dex */
    public interface OnDelViewStatusChangeLister {
        void onStatusChange(boolean z);
    }

    public XLeftSlideView(Context context) {
        this(context, null);
    }

    public XLeftSlideView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public XLeftSlideView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mAnimDuring = 200;
        this.mDelLength = 76;
        this.isReCompute = true;
        this.mContext = context;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.widget.XLinearLayout, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        init();
    }

    private void init() {
        initView();
        this.mTouchSlop = ViewConfiguration.get(this.mContext).getScaledTouchSlop();
        setOrientation(0);
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        View view = this.mMenuView;
        if (view != null) {
            this.mDelLength = view.getMeasuredWidth();
        }
        this.mRightCanSlide = this.mDelLength;
    }

    public void addContentView(View view) {
        this.mContentView = view;
        view.setTag("contentView");
        View findViewWithTag = findViewWithTag("contentView");
        if (findViewWithTag != null) {
            removeView(findViewWithTag);
        }
        addView(this.mContentView, new LinearLayout.LayoutParams(-1, -1));
    }

    public void addMenuView(View view) {
        this.mMenuView = view;
        view.setTag("menuView");
        View findViewWithTag = findViewWithTag("menuView");
        if (findViewWithTag != null) {
            removeView(findViewWithTag);
        }
        addView(this.mMenuView, new LinearLayout.LayoutParams(this.mRightCanSlide, -1));
    }

    public void setViewPager(ViewPager viewPager) {
        this.mViewPager = viewPager;
    }

    public void setVerticalScrollableView(ViewGroup viewGroup) {
        this.mVerticalScrollableView = viewGroup;
    }

    public void setCardView(CardView cardView) {
        this.mCardView = cardView;
    }

    public void setStatusChangeLister(OnDelViewStatusChangeLister onDelViewStatusChangeLister) {
        this.mStatusChangeLister = onDelViewStatusChangeLister;
    }

    private void initView() {
        if (getChildCount() == 2) {
            View childAt = getChildAt(0);
            this.mContentView = childAt;
            childAt.setTag("contentView");
            View childAt2 = getChildAt(1);
            this.mMenuView = childAt2;
            childAt2.setTag("menuView");
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:9:0x0026, code lost:
        if (r0 != 3) goto L9;
     */
    @Override // android.view.ViewGroup
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean onInterceptTouchEvent(android.view.MotionEvent r6) {
        /*
            Method dump skipped, instructions count: 259
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaopeng.xui.widget.XLeftSlideView.onInterceptTouchEvent(android.view.MotionEvent):boolean");
    }

    /* JADX WARN: Code restructure failed: missing block: B:9:0x000e, code lost:
        if (r0 != 3) goto L9;
     */
    @Override // android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean onTouchEvent(android.view.MotionEvent r7) {
        /*
            Method dump skipped, instructions count: 294
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaopeng.xui.widget.XLeftSlideView.onTouchEvent(android.view.MotionEvent):boolean");
    }

    private void clearAnim() {
        ValueAnimator valueAnimator = this.mValueAnimator;
        if (valueAnimator == null) {
            return;
        }
        valueAnimator.end();
        this.mValueAnimator.cancel();
        this.mValueAnimator = null;
    }

    private void upAnim() {
        int scrollX = getScrollX();
        int i = this.mRightCanSlide;
        if (scrollX == i || scrollX == 0) {
            OnDelViewStatusChangeLister onDelViewStatusChangeLister = this.mStatusChangeLister;
            if (onDelViewStatusChangeLister != null) {
                onDelViewStatusChangeLister.onStatusChange(scrollX == i);
                return;
            }
            return;
        }
        clearAnim();
        int i2 = this.mRightCanSlide;
        if (scrollX >= i2 / 2) {
            ValueAnimator ofInt = ValueAnimator.ofInt(scrollX, i2);
            this.mValueAnimator = ofInt;
            ofInt.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.xiaopeng.xui.widget.XLeftSlideView.1
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    XLeftSlideView.this.scrollTo(((Integer) valueAnimator.getAnimatedValue()).intValue(), 0);
                }
            });
            this.mValueAnimator.setDuration(this.mAnimDuring);
            this.mValueAnimator.start();
            OnDelViewStatusChangeLister onDelViewStatusChangeLister2 = this.mStatusChangeLister;
            if (onDelViewStatusChangeLister2 != null) {
                onDelViewStatusChangeLister2.onStatusChange(true);
                return;
            }
            return;
        }
        ValueAnimator ofInt2 = ValueAnimator.ofInt(scrollX, 0);
        this.mValueAnimator = ofInt2;
        ofInt2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.xiaopeng.xui.widget.XLeftSlideView.2
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                XLeftSlideView.this.scrollTo(((Integer) valueAnimator.getAnimatedValue()).intValue(), 0);
            }
        });
        this.mValueAnimator.setDuration(this.mAnimDuring);
        this.mValueAnimator.start();
        OnDelViewStatusChangeLister onDelViewStatusChangeLister3 = this.mStatusChangeLister;
        if (onDelViewStatusChangeLister3 != null) {
            onDelViewStatusChangeLister3.onStatusChange(false);
        }
    }

    public void resetDelStatus() {
        int scrollX = getScrollX();
        if (scrollX == 0) {
            return;
        }
        clearAnim();
        ValueAnimator ofInt = ValueAnimator.ofInt(scrollX, 0);
        this.mValueAnimator = ofInt;
        ofInt.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.xiaopeng.xui.widget.XLeftSlideView.3
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                XLeftSlideView.this.scrollTo(((Integer) valueAnimator.getAnimatedValue()).intValue(), 0);
            }
        });
        this.mValueAnimator.setDuration(this.mAnimDuring);
        this.mValueAnimator.start();
    }
}

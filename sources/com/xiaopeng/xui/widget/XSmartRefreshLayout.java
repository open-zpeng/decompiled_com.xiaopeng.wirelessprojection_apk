package com.xiaopeng.xui.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.AbsListView;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.NestedScrollingParent;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ViewCompat;
import androidx.vectordrawable.graphics.drawable.PathInterpolatorCompat;
import com.google.android.exoplayer2.C;
import com.scwang.smart.refresh.layout.api.RefreshComponent;
import com.scwang.smart.refresh.layout.api.RefreshContent;
import com.scwang.smart.refresh.layout.api.RefreshFooter;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshKernel;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.DimensionStatus;
import com.scwang.smart.refresh.layout.constant.RefreshState;
import com.scwang.smart.refresh.layout.constant.SpinnerStyle;
import com.scwang.smart.refresh.layout.kernel.R;
import com.scwang.smart.refresh.layout.listener.DefaultRefreshFooterCreator;
import com.scwang.smart.refresh.layout.listener.DefaultRefreshHeaderCreator;
import com.scwang.smart.refresh.layout.listener.DefaultRefreshInitializer;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnMultiListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.ScrollBoundaryDecider;
import com.scwang.smart.refresh.layout.util.SmartUtil;
import com.scwang.smart.refresh.layout.wrapper.RefreshContentWrapper;
/* loaded from: classes2.dex */
public class XSmartRefreshLayout extends XViewGroup implements RefreshLayout, NestedScrollingParent {
    protected static ViewGroup.MarginLayoutParams sDefaultMarginLP = new ViewGroup.MarginLayoutParams(-1, -1);
    protected static DefaultRefreshFooterCreator sFooterCreator;
    protected static DefaultRefreshHeaderCreator sHeaderCreator;
    protected static DefaultRefreshInitializer sRefreshInitializer;
    protected Runnable animationRunnable;
    protected boolean mAttachedToWindow;
    protected int mCurrentVelocity;
    protected boolean mDisableContentWhenLoading;
    protected boolean mDisableContentWhenRefresh;
    protected char mDragDirection;
    protected float mDragRate;
    protected boolean mEnableAutoLoadMore;
    protected boolean mEnableClipFooterWhenFixedBehind;
    protected boolean mEnableClipHeaderWhenFixedBehind;
    protected boolean mEnableDisallowIntercept;
    protected boolean mEnableFooterFollowWhenNoMoreData;
    protected boolean mEnableFooterTranslationContent;
    protected boolean mEnableHeaderTranslationContent;
    protected boolean mEnableLoadMore;
    protected boolean mEnableLoadMoreWhenContentNotFull;
    protected boolean mEnableNestedScrolling;
    protected boolean mEnableOverScrollBounce;
    protected boolean mEnableOverScrollDrag;
    protected boolean mEnablePreviewInEditMode;
    protected boolean mEnablePureScrollMode;
    protected boolean mEnableRefresh;
    protected boolean mEnableScrollContentWhenLoaded;
    protected boolean mEnableScrollContentWhenRefreshed;
    protected MotionEvent mFalsifyEvent;
    protected int mFixedFooterViewId;
    protected int mFixedHeaderViewId;
    protected int mFloorDuration;
    protected int mFooterBackgroundColor;
    protected int mFooterHeight;
    protected DimensionStatus mFooterHeightStatus;
    protected int mFooterInsetStart;
    protected boolean mFooterLocked;
    protected float mFooterMaxDragRate;
    protected boolean mFooterNeedTouchEventWhenLoading;
    protected boolean mFooterNoMoreData;
    protected boolean mFooterNoMoreDataEffective;
    protected int mFooterTranslationViewId;
    protected float mFooterTriggerRate;
    protected Handler mHandler;
    protected int mHeaderBackgroundColor;
    protected int mHeaderHeight;
    protected DimensionStatus mHeaderHeightStatus;
    protected int mHeaderInsetStart;
    protected float mHeaderMaxDragRate;
    protected boolean mHeaderNeedTouchEventWhenRefreshing;
    protected int mHeaderTranslationViewId;
    protected float mHeaderTriggerRate;
    protected boolean mIsBeingDragged;
    protected RefreshKernel mKernel;
    protected long mLastOpenTime;
    protected int mLastSpinner;
    protected float mLastTouchX;
    protected float mLastTouchY;
    protected OnLoadMoreListener mLoadMoreListener;
    protected boolean mManualFooterTranslationContent;
    protected boolean mManualHeaderTranslationContent;
    protected boolean mManualLoadMore;
    protected int mMaximumVelocity;
    protected int mMinimumVelocity;
    protected NestedScrollingChildHelper mNestedChild;
    protected boolean mNestedInProgress;
    protected NestedScrollingParentHelper mNestedParent;
    protected OnMultiListener mOnMultiListener;
    protected Paint mPaint;
    protected int[] mParentOffsetInWindow;
    protected int[] mPrimaryColors;
    protected int mReboundDuration;
    protected Interpolator mReboundInterpolator;
    protected RefreshContent mRefreshContent;
    protected RefreshComponent mRefreshFooter;
    protected RefreshComponent mRefreshHeader;
    protected OnRefreshListener mRefreshListener;
    protected int mScreenHeightPixels;
    protected ScrollBoundaryDecider mScrollBoundaryDecider;
    protected Scroller mScroller;
    protected int mSpinner;
    protected RefreshState mState;
    protected boolean mSuperDispatchTouchEvent;
    protected int mTotalUnconsumed;
    protected int mTouchSlop;
    protected int mTouchSpinner;
    protected float mTouchX;
    protected float mTouchY;
    protected float mTwoLevelBottomPullUpToCloseRate;
    protected VelocityTracker mVelocityTracker;
    protected boolean mVerticalPermit;
    protected RefreshState mViceState;
    protected ValueAnimator reboundAnimator;

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public ViewGroup getLayout() {
        return this;
    }

    public XSmartRefreshLayout(Context context) {
        this(context, null);
    }

    public XSmartRefreshLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mFloorDuration = 300;
        this.mReboundDuration = 300;
        this.mDragRate = 0.5f;
        this.mDragDirection = 'n';
        this.mFixedHeaderViewId = -1;
        this.mFixedFooterViewId = -1;
        this.mHeaderTranslationViewId = -1;
        this.mFooterTranslationViewId = -1;
        this.mEnableRefresh = true;
        this.mEnableLoadMore = false;
        this.mEnableClipHeaderWhenFixedBehind = true;
        this.mEnableClipFooterWhenFixedBehind = true;
        this.mEnableHeaderTranslationContent = true;
        this.mEnableFooterTranslationContent = true;
        this.mEnableFooterFollowWhenNoMoreData = false;
        this.mEnablePreviewInEditMode = true;
        this.mEnableOverScrollBounce = true;
        this.mEnableOverScrollDrag = false;
        this.mEnableAutoLoadMore = true;
        this.mEnablePureScrollMode = true;
        this.mEnableScrollContentWhenLoaded = true;
        this.mEnableScrollContentWhenRefreshed = true;
        this.mEnableLoadMoreWhenContentNotFull = true;
        this.mEnableNestedScrolling = true;
        this.mDisableContentWhenRefresh = false;
        this.mDisableContentWhenLoading = false;
        this.mFooterNoMoreData = false;
        this.mFooterNoMoreDataEffective = false;
        this.mManualLoadMore = false;
        this.mManualHeaderTranslationContent = false;
        this.mManualFooterTranslationContent = false;
        this.mParentOffsetInWindow = new int[2];
        this.mNestedChild = new NestedScrollingChildHelper(this);
        this.mNestedParent = new NestedScrollingParentHelper(this);
        this.mHeaderHeightStatus = DimensionStatus.DefaultUnNotify;
        this.mFooterHeightStatus = DimensionStatus.DefaultUnNotify;
        this.mHeaderMaxDragRate = 2.5f;
        this.mFooterMaxDragRate = 2.5f;
        this.mHeaderTriggerRate = 1.0f;
        this.mFooterTriggerRate = 1.0f;
        this.mTwoLevelBottomPullUpToCloseRate = 0.16666667f;
        this.mKernel = new RefreshKernelImpl();
        this.mState = RefreshState.None;
        this.mViceState = RefreshState.None;
        this.mLastOpenTime = 0L;
        this.mHeaderBackgroundColor = 0;
        this.mFooterBackgroundColor = 0;
        this.mFooterLocked = false;
        this.mVerticalPermit = false;
        this.mFalsifyEvent = null;
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        this.mHandler = new Handler(Looper.getMainLooper());
        this.mScroller = new Scroller(context);
        this.mVelocityTracker = VelocityTracker.obtain();
        this.mScreenHeightPixels = context.getResources().getDisplayMetrics().heightPixels;
        this.mReboundInterpolator = new SmartUtil(SmartUtil.INTERPOLATOR_VISCOUS_FLUID);
        this.mTouchSlop = viewConfiguration.getScaledTouchSlop();
        this.mMinimumVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
        this.mMaximumVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
        this.mFooterHeight = SmartUtil.dp2px(60.0f);
        this.mHeaderHeight = SmartUtil.dp2px(100.0f);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.SmartRefreshLayout);
        if (!obtainStyledAttributes.hasValue(R.styleable.SmartRefreshLayout_android_clipToPadding)) {
            super.setClipToPadding(false);
        }
        if (!obtainStyledAttributes.hasValue(R.styleable.SmartRefreshLayout_android_clipChildren)) {
            super.setClipChildren(false);
        }
        DefaultRefreshInitializer defaultRefreshInitializer = sRefreshInitializer;
        if (defaultRefreshInitializer != null) {
            defaultRefreshInitializer.initialize(context, this);
        }
        this.mDragRate = obtainStyledAttributes.getFloat(R.styleable.SmartRefreshLayout_srlDragRate, this.mDragRate);
        this.mHeaderMaxDragRate = obtainStyledAttributes.getFloat(R.styleable.SmartRefreshLayout_srlHeaderMaxDragRate, this.mHeaderMaxDragRate);
        this.mFooterMaxDragRate = obtainStyledAttributes.getFloat(R.styleable.SmartRefreshLayout_srlFooterMaxDragRate, this.mFooterMaxDragRate);
        this.mHeaderTriggerRate = obtainStyledAttributes.getFloat(R.styleable.SmartRefreshLayout_srlHeaderTriggerRate, this.mHeaderTriggerRate);
        this.mFooterTriggerRate = obtainStyledAttributes.getFloat(R.styleable.SmartRefreshLayout_srlFooterTriggerRate, this.mFooterTriggerRate);
        this.mEnableRefresh = obtainStyledAttributes.getBoolean(R.styleable.SmartRefreshLayout_srlEnableRefresh, this.mEnableRefresh);
        this.mReboundDuration = obtainStyledAttributes.getInt(R.styleable.SmartRefreshLayout_srlReboundDuration, this.mReboundDuration);
        this.mEnableLoadMore = obtainStyledAttributes.getBoolean(R.styleable.SmartRefreshLayout_srlEnableLoadMore, this.mEnableLoadMore);
        this.mHeaderHeight = obtainStyledAttributes.getDimensionPixelOffset(R.styleable.SmartRefreshLayout_srlHeaderHeight, this.mHeaderHeight);
        this.mFooterHeight = obtainStyledAttributes.getDimensionPixelOffset(R.styleable.SmartRefreshLayout_srlFooterHeight, this.mFooterHeight);
        this.mHeaderInsetStart = obtainStyledAttributes.getDimensionPixelOffset(R.styleable.SmartRefreshLayout_srlHeaderInsetStart, this.mHeaderInsetStart);
        this.mFooterInsetStart = obtainStyledAttributes.getDimensionPixelOffset(R.styleable.SmartRefreshLayout_srlFooterInsetStart, this.mFooterInsetStart);
        this.mDisableContentWhenRefresh = obtainStyledAttributes.getBoolean(R.styleable.SmartRefreshLayout_srlDisableContentWhenRefresh, this.mDisableContentWhenRefresh);
        this.mDisableContentWhenLoading = obtainStyledAttributes.getBoolean(R.styleable.SmartRefreshLayout_srlDisableContentWhenLoading, this.mDisableContentWhenLoading);
        this.mEnableHeaderTranslationContent = obtainStyledAttributes.getBoolean(R.styleable.SmartRefreshLayout_srlEnableHeaderTranslationContent, this.mEnableHeaderTranslationContent);
        this.mEnableFooterTranslationContent = obtainStyledAttributes.getBoolean(R.styleable.SmartRefreshLayout_srlEnableFooterTranslationContent, this.mEnableFooterTranslationContent);
        this.mEnablePreviewInEditMode = obtainStyledAttributes.getBoolean(R.styleable.SmartRefreshLayout_srlEnablePreviewInEditMode, this.mEnablePreviewInEditMode);
        this.mEnableAutoLoadMore = obtainStyledAttributes.getBoolean(R.styleable.SmartRefreshLayout_srlEnableAutoLoadMore, this.mEnableAutoLoadMore);
        this.mEnableOverScrollBounce = obtainStyledAttributes.getBoolean(R.styleable.SmartRefreshLayout_srlEnableOverScrollBounce, this.mEnableOverScrollBounce);
        this.mEnablePureScrollMode = obtainStyledAttributes.getBoolean(R.styleable.SmartRefreshLayout_srlEnablePureScrollMode, this.mEnablePureScrollMode);
        this.mEnableScrollContentWhenLoaded = obtainStyledAttributes.getBoolean(R.styleable.SmartRefreshLayout_srlEnableScrollContentWhenLoaded, this.mEnableScrollContentWhenLoaded);
        this.mEnableScrollContentWhenRefreshed = obtainStyledAttributes.getBoolean(R.styleable.SmartRefreshLayout_srlEnableScrollContentWhenRefreshed, this.mEnableScrollContentWhenRefreshed);
        this.mEnableLoadMoreWhenContentNotFull = obtainStyledAttributes.getBoolean(R.styleable.SmartRefreshLayout_srlEnableLoadMoreWhenContentNotFull, this.mEnableLoadMoreWhenContentNotFull);
        this.mEnableFooterFollowWhenNoMoreData = obtainStyledAttributes.getBoolean(R.styleable.SmartRefreshLayout_srlEnableFooterFollowWhenLoadFinished, this.mEnableFooterFollowWhenNoMoreData);
        this.mEnableFooterFollowWhenNoMoreData = obtainStyledAttributes.getBoolean(R.styleable.SmartRefreshLayout_srlEnableFooterFollowWhenNoMoreData, this.mEnableFooterFollowWhenNoMoreData);
        this.mEnableClipHeaderWhenFixedBehind = obtainStyledAttributes.getBoolean(R.styleable.SmartRefreshLayout_srlEnableClipHeaderWhenFixedBehind, this.mEnableClipHeaderWhenFixedBehind);
        this.mEnableClipFooterWhenFixedBehind = obtainStyledAttributes.getBoolean(R.styleable.SmartRefreshLayout_srlEnableClipFooterWhenFixedBehind, this.mEnableClipFooterWhenFixedBehind);
        this.mEnableOverScrollDrag = obtainStyledAttributes.getBoolean(R.styleable.SmartRefreshLayout_srlEnableOverScrollDrag, this.mEnableOverScrollDrag);
        this.mFixedHeaderViewId = obtainStyledAttributes.getResourceId(R.styleable.SmartRefreshLayout_srlFixedHeaderViewId, this.mFixedHeaderViewId);
        this.mFixedFooterViewId = obtainStyledAttributes.getResourceId(R.styleable.SmartRefreshLayout_srlFixedFooterViewId, this.mFixedFooterViewId);
        this.mHeaderTranslationViewId = obtainStyledAttributes.getResourceId(R.styleable.SmartRefreshLayout_srlHeaderTranslationViewId, this.mHeaderTranslationViewId);
        this.mFooterTranslationViewId = obtainStyledAttributes.getResourceId(R.styleable.SmartRefreshLayout_srlFooterTranslationViewId, this.mFooterTranslationViewId);
        boolean z = obtainStyledAttributes.getBoolean(R.styleable.SmartRefreshLayout_srlEnableNestedScrolling, this.mEnableNestedScrolling);
        this.mEnableNestedScrolling = z;
        this.mNestedChild.setNestedScrollingEnabled(z);
        this.mManualLoadMore = this.mManualLoadMore || obtainStyledAttributes.hasValue(R.styleable.SmartRefreshLayout_srlEnableLoadMore);
        this.mManualHeaderTranslationContent = this.mManualHeaderTranslationContent || obtainStyledAttributes.hasValue(R.styleable.SmartRefreshLayout_srlEnableHeaderTranslationContent);
        this.mManualFooterTranslationContent = this.mManualFooterTranslationContent || obtainStyledAttributes.hasValue(R.styleable.SmartRefreshLayout_srlEnableFooterTranslationContent);
        this.mHeaderHeightStatus = obtainStyledAttributes.hasValue(R.styleable.SmartRefreshLayout_srlHeaderHeight) ? DimensionStatus.XmlLayoutUnNotify : this.mHeaderHeightStatus;
        this.mFooterHeightStatus = obtainStyledAttributes.hasValue(R.styleable.SmartRefreshLayout_srlFooterHeight) ? DimensionStatus.XmlLayoutUnNotify : this.mFooterHeightStatus;
        int color = obtainStyledAttributes.getColor(R.styleable.SmartRefreshLayout_srlAccentColor, 0);
        int color2 = obtainStyledAttributes.getColor(R.styleable.SmartRefreshLayout_srlPrimaryColor, 0);
        if (color2 != 0) {
            if (color != 0) {
                this.mPrimaryColors = new int[]{color2, color};
            } else {
                this.mPrimaryColors = new int[]{color2};
            }
        } else if (color != 0) {
            this.mPrimaryColors = new int[]{0, color};
        }
        if (this.mEnablePureScrollMode && !this.mManualLoadMore && !this.mEnableLoadMore) {
            this.mEnableLoadMore = true;
        }
        obtainStyledAttributes.recycle();
    }

    /* JADX WARN: Removed duplicated region for block: B:32:0x0052  */
    @Override // android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected void onFinishInflate() {
        /*
            r11 = this;
            super.onFinishInflate()
            int r0 = super.getChildCount()
            r1 = 3
            if (r0 > r1) goto L9e
            r2 = -1
            r3 = 0
            r5 = r2
            r4 = r3
            r6 = r4
        Lf:
            r7 = 2
            r8 = 1
            if (r4 >= r0) goto L33
            android.view.View r9 = super.getChildAt(r4)
            boolean r10 = com.scwang.smart.refresh.layout.util.SmartUtil.isContentView(r9)
            if (r10 == 0) goto L24
            if (r6 < r7) goto L21
            if (r4 != r8) goto L24
        L21:
            r5 = r4
            r6 = r7
            goto L30
        L24:
            boolean r7 = r9 instanceof com.scwang.smart.refresh.layout.api.RefreshComponent
            if (r7 != 0) goto L30
            if (r6 >= r8) goto L30
            if (r4 <= 0) goto L2e
            r6 = r8
            goto L2f
        L2e:
            r6 = r3
        L2f:
            r5 = r4
        L30:
            int r4 = r4 + 1
            goto Lf
        L33:
            if (r5 < 0) goto L4d
            com.scwang.smart.refresh.layout.wrapper.RefreshContentWrapper r4 = new com.scwang.smart.refresh.layout.wrapper.RefreshContentWrapper
            android.view.View r6 = super.getChildAt(r5)
            r4.<init>(r6)
            r11.mRefreshContent = r4
            if (r5 != r8) goto L48
            if (r0 != r1) goto L45
            goto L46
        L45:
            r7 = r2
        L46:
            r1 = r3
            goto L4f
        L48:
            if (r0 != r7) goto L4d
            r1 = r2
            r7 = r8
            goto L4f
        L4d:
            r1 = r2
            r7 = r1
        L4f:
            r4 = r3
        L50:
            if (r4 >= r0) goto L9d
            android.view.View r5 = super.getChildAt(r4)
            if (r4 == r1) goto L8b
            if (r4 == r7) goto L65
            if (r1 != r2) goto L65
            com.scwang.smart.refresh.layout.api.RefreshComponent r6 = r11.mRefreshHeader
            if (r6 != 0) goto L65
            boolean r6 = r5 instanceof com.scwang.smart.refresh.layout.api.RefreshHeader
            if (r6 == 0) goto L65
            goto L8b
        L65:
            if (r4 == r7) goto L6d
            if (r7 != r2) goto L9a
            boolean r6 = r5 instanceof com.scwang.smart.refresh.layout.api.RefreshFooter
            if (r6 == 0) goto L9a
        L6d:
            boolean r6 = r11.mEnableLoadMore
            if (r6 != 0) goto L78
            boolean r6 = r11.mManualLoadMore
            if (r6 != 0) goto L76
            goto L78
        L76:
            r6 = r3
            goto L79
        L78:
            r6 = r8
        L79:
            r11.mEnableLoadMore = r6
            boolean r6 = r5 instanceof com.scwang.smart.refresh.layout.api.RefreshFooter
            if (r6 == 0) goto L82
            com.scwang.smart.refresh.layout.api.RefreshFooter r5 = (com.scwang.smart.refresh.layout.api.RefreshFooter) r5
            goto L88
        L82:
            com.scwang.smart.refresh.layout.wrapper.RefreshFooterWrapper r6 = new com.scwang.smart.refresh.layout.wrapper.RefreshFooterWrapper
            r6.<init>(r5)
            r5 = r6
        L88:
            r11.mRefreshFooter = r5
            goto L9a
        L8b:
            boolean r6 = r5 instanceof com.scwang.smart.refresh.layout.api.RefreshHeader
            if (r6 == 0) goto L92
            com.scwang.smart.refresh.layout.api.RefreshHeader r5 = (com.scwang.smart.refresh.layout.api.RefreshHeader) r5
            goto L98
        L92:
            com.scwang.smart.refresh.layout.wrapper.RefreshHeaderWrapper r6 = new com.scwang.smart.refresh.layout.wrapper.RefreshHeaderWrapper
            r6.<init>(r5)
            r5 = r6
        L98:
            r11.mRefreshHeader = r5
        L9a:
            int r4 = r4 + 1
            goto L50
        L9d:
            return
        L9e:
            java.lang.RuntimeException r0 = new java.lang.RuntimeException
            java.lang.String r1 = "最多只支持3个子View，Most only support three sub view"
            r0.<init>(r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaopeng.xui.widget.XSmartRefreshLayout.onFinishInflate():void");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.widget.XViewGroup, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        RefreshComponent refreshComponent;
        DefaultRefreshHeaderCreator defaultRefreshHeaderCreator;
        super.onAttachedToWindow();
        boolean z = true;
        this.mAttachedToWindow = true;
        if (!isInEditMode()) {
            if (this.mRefreshHeader == null && (defaultRefreshHeaderCreator = sHeaderCreator) != null) {
                RefreshHeader createRefreshHeader = defaultRefreshHeaderCreator.createRefreshHeader(getContext(), this);
                if (createRefreshHeader == null) {
                    throw new RuntimeException("DefaultRefreshHeaderCreator can not return null");
                }
                setRefreshHeader(createRefreshHeader);
            }
            if (this.mRefreshFooter == null) {
                DefaultRefreshFooterCreator defaultRefreshFooterCreator = sFooterCreator;
                if (defaultRefreshFooterCreator != null) {
                    RefreshFooter createRefreshFooter = defaultRefreshFooterCreator.createRefreshFooter(getContext(), this);
                    if (createRefreshFooter == null) {
                        throw new RuntimeException("DefaultRefreshFooterCreator can not return null");
                    }
                    setRefreshFooter(createRefreshFooter);
                }
            } else {
                if (!this.mEnableLoadMore && this.mManualLoadMore) {
                    z = false;
                }
                this.mEnableLoadMore = z;
            }
            if (this.mRefreshContent == null) {
                int childCount = getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View childAt = getChildAt(i);
                    RefreshComponent refreshComponent2 = this.mRefreshHeader;
                    if ((refreshComponent2 == null || childAt != refreshComponent2.getView()) && ((refreshComponent = this.mRefreshFooter) == null || childAt != refreshComponent.getView())) {
                        this.mRefreshContent = new RefreshContentWrapper(childAt);
                    }
                }
            }
            if (this.mRefreshContent == null) {
                int dp2px = SmartUtil.dp2px(20.0f);
                TextView textView = new TextView(getContext());
                textView.setTextColor(-39424);
                textView.setGravity(17);
                textView.setTextSize(20.0f);
                textView.setText(R.string.srl_content_empty);
                super.addView(textView, 0, new LayoutParams(-1, -1));
                RefreshContentWrapper refreshContentWrapper = new RefreshContentWrapper(textView);
                this.mRefreshContent = refreshContentWrapper;
                refreshContentWrapper.getView().setPadding(dp2px, dp2px, dp2px, dp2px);
            }
            View findViewById = findViewById(this.mFixedHeaderViewId);
            View findViewById2 = findViewById(this.mFixedFooterViewId);
            this.mRefreshContent.setScrollBoundaryDecider(this.mScrollBoundaryDecider);
            this.mRefreshContent.setEnableLoadMoreWhenContentNotFull(this.mEnableLoadMoreWhenContentNotFull);
            this.mRefreshContent.setUpComponent(this.mKernel, findViewById, findViewById2);
            if (this.mSpinner != 0) {
                notifyStateChanged(RefreshState.None);
                RefreshContent refreshContent = this.mRefreshContent;
                this.mSpinner = 0;
                refreshContent.moveSpinner(0, this.mHeaderTranslationViewId, this.mFooterTranslationViewId);
            }
        }
        int[] iArr = this.mPrimaryColors;
        if (iArr != null) {
            RefreshComponent refreshComponent3 = this.mRefreshHeader;
            if (refreshComponent3 != null) {
                refreshComponent3.setPrimaryColors(iArr);
            }
            RefreshComponent refreshComponent4 = this.mRefreshFooter;
            if (refreshComponent4 != null) {
                refreshComponent4.setPrimaryColors(this.mPrimaryColors);
            }
        }
        RefreshContent refreshContent2 = this.mRefreshContent;
        if (refreshContent2 != null) {
            super.bringChildToFront(refreshContent2.getView());
        }
        RefreshComponent refreshComponent5 = this.mRefreshHeader;
        if (refreshComponent5 != null && refreshComponent5.getSpinnerStyle().front) {
            super.bringChildToFront(this.mRefreshHeader.getView());
        }
        RefreshComponent refreshComponent6 = this.mRefreshFooter;
        if (refreshComponent6 == null || !refreshComponent6.getSpinnerStyle().front) {
            return;
        }
        super.bringChildToFront(this.mRefreshFooter.getView());
    }

    /* JADX WARN: Removed duplicated region for block: B:100:0x020f  */
    /* JADX WARN: Removed duplicated region for block: B:102:0x0215  */
    /* JADX WARN: Removed duplicated region for block: B:112:0x0236  */
    /* JADX WARN: Removed duplicated region for block: B:115:0x024f  */
    /* JADX WARN: Removed duplicated region for block: B:120:0x026f  */
    @Override // android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected void onMeasure(int r18, int r19) {
        /*
            Method dump skipped, instructions count: 871
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaopeng.xui.widget.XSmartRefreshLayout.onMeasure(int, int):void");
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5;
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        getPaddingBottom();
        int childCount = super.getChildCount();
        for (int i6 = 0; i6 < childCount; i6++) {
            View childAt = super.getChildAt(i6);
            if (childAt.getVisibility() != 8 && !"GONE".equals(childAt.getTag(R.id.srl_tag))) {
                RefreshContent refreshContent = this.mRefreshContent;
                boolean z2 = true;
                if (refreshContent != null && refreshContent.getView() == childAt) {
                    boolean z3 = isInEditMode() && this.mEnablePreviewInEditMode && isEnableRefreshOrLoadMore(this.mEnableRefresh) && this.mRefreshHeader != null;
                    View view = this.mRefreshContent.getView();
                    ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                    ViewGroup.MarginLayoutParams marginLayoutParams = layoutParams instanceof ViewGroup.MarginLayoutParams ? (ViewGroup.MarginLayoutParams) layoutParams : sDefaultMarginLP;
                    int i7 = marginLayoutParams.leftMargin + paddingLeft;
                    int i8 = marginLayoutParams.topMargin + paddingTop;
                    int measuredWidth = view.getMeasuredWidth() + i7;
                    int measuredHeight = view.getMeasuredHeight() + i8;
                    if (z3 && isEnableTranslationContent(this.mEnableHeaderTranslationContent, this.mRefreshHeader)) {
                        int i9 = this.mHeaderHeight;
                        i8 += i9;
                        measuredHeight += i9;
                    }
                    view.layout(i7, i8, measuredWidth, measuredHeight);
                }
                RefreshComponent refreshComponent = this.mRefreshHeader;
                if (refreshComponent != null && refreshComponent.getView() == childAt) {
                    boolean z4 = isInEditMode() && this.mEnablePreviewInEditMode && isEnableRefreshOrLoadMore(this.mEnableRefresh);
                    View view2 = this.mRefreshHeader.getView();
                    ViewGroup.LayoutParams layoutParams2 = view2.getLayoutParams();
                    ViewGroup.MarginLayoutParams marginLayoutParams2 = layoutParams2 instanceof ViewGroup.MarginLayoutParams ? (ViewGroup.MarginLayoutParams) layoutParams2 : sDefaultMarginLP;
                    int i10 = marginLayoutParams2.leftMargin;
                    int i11 = marginLayoutParams2.topMargin + this.mHeaderInsetStart;
                    int measuredWidth2 = view2.getMeasuredWidth() + i10;
                    int measuredHeight2 = view2.getMeasuredHeight() + i11;
                    if (!z4 && this.mRefreshHeader.getSpinnerStyle() == SpinnerStyle.Translate) {
                        int i12 = this.mHeaderHeight;
                        i11 -= i12;
                        measuredHeight2 -= i12;
                    }
                    view2.layout(i10, i11, measuredWidth2, measuredHeight2);
                }
                RefreshComponent refreshComponent2 = this.mRefreshFooter;
                if (refreshComponent2 != null && refreshComponent2.getView() == childAt) {
                    z2 = (isInEditMode() && this.mEnablePreviewInEditMode && isEnableRefreshOrLoadMore(this.mEnableLoadMore)) ? false : false;
                    View view3 = this.mRefreshFooter.getView();
                    ViewGroup.LayoutParams layoutParams3 = view3.getLayoutParams();
                    ViewGroup.MarginLayoutParams marginLayoutParams3 = layoutParams3 instanceof ViewGroup.MarginLayoutParams ? (ViewGroup.MarginLayoutParams) layoutParams3 : sDefaultMarginLP;
                    SpinnerStyle spinnerStyle = this.mRefreshFooter.getSpinnerStyle();
                    int i13 = marginLayoutParams3.leftMargin;
                    int measuredHeight3 = (marginLayoutParams3.topMargin + getMeasuredHeight()) - this.mFooterInsetStart;
                    if (this.mFooterNoMoreData && this.mFooterNoMoreDataEffective && this.mEnableFooterFollowWhenNoMoreData && this.mRefreshContent != null && this.mRefreshFooter.getSpinnerStyle() == SpinnerStyle.Translate && isEnableRefreshOrLoadMore(this.mEnableLoadMore)) {
                        View view4 = this.mRefreshContent.getView();
                        ViewGroup.LayoutParams layoutParams4 = view4.getLayoutParams();
                        measuredHeight3 = view4.getMeasuredHeight() + paddingTop + paddingTop + (layoutParams4 instanceof ViewGroup.MarginLayoutParams ? ((ViewGroup.MarginLayoutParams) layoutParams4).topMargin : 0);
                    }
                    if (spinnerStyle == SpinnerStyle.MatchLayout) {
                        measuredHeight3 = marginLayoutParams3.topMargin - this.mFooterInsetStart;
                    } else {
                        if (z2 || spinnerStyle == SpinnerStyle.FixedFront || spinnerStyle == SpinnerStyle.FixedBehind) {
                            i5 = this.mFooterHeight;
                        } else if (spinnerStyle.scale && this.mSpinner < 0) {
                            i5 = Math.max(isEnableRefreshOrLoadMore(this.mEnableLoadMore) ? -this.mSpinner : 0, 0);
                        }
                        measuredHeight3 -= i5;
                    }
                    view3.layout(i13, measuredHeight3, view3.getMeasuredWidth() + i13, view3.getMeasuredHeight() + measuredHeight3);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.widget.XViewGroup, android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mAttachedToWindow = false;
        this.mManualLoadMore = true;
        this.animationRunnable = null;
        ValueAnimator valueAnimator = this.reboundAnimator;
        if (valueAnimator != null) {
            valueAnimator.removeAllListeners();
            this.reboundAnimator.removeAllUpdateListeners();
            this.reboundAnimator.setDuration(0L);
            this.reboundAnimator.cancel();
            this.reboundAnimator = null;
        }
        if (this.mRefreshHeader != null && this.mState == RefreshState.Refreshing) {
            this.mRefreshHeader.onFinish(this, false);
        }
        if (this.mRefreshFooter != null && this.mState == RefreshState.Loading) {
            this.mRefreshFooter.onFinish(this, false);
        }
        if (this.mSpinner != 0) {
            this.mKernel.moveSpinner(0, true);
        }
        if (this.mState != RefreshState.None) {
            notifyStateChanged(RefreshState.None);
        }
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        this.mFooterLocked = false;
    }

    @Override // android.view.ViewGroup
    protected boolean drawChild(Canvas canvas, View view, long j) {
        Paint paint;
        Paint paint2;
        RefreshContent refreshContent = this.mRefreshContent;
        View view2 = refreshContent != null ? refreshContent.getView() : null;
        RefreshComponent refreshComponent = this.mRefreshHeader;
        if (refreshComponent != null && refreshComponent.getView() == view) {
            if (!isEnableRefreshOrLoadMore(this.mEnableRefresh) || (!this.mEnablePreviewInEditMode && isInEditMode())) {
                return true;
            }
            if (view2 != null) {
                int max = Math.max(view2.getTop() + view2.getPaddingTop() + this.mSpinner, view.getTop());
                int i = this.mHeaderBackgroundColor;
                if (i != 0 && (paint2 = this.mPaint) != null) {
                    paint2.setColor(i);
                    if (this.mRefreshHeader.getSpinnerStyle().scale) {
                        max = view.getBottom();
                    } else if (this.mRefreshHeader.getSpinnerStyle() == SpinnerStyle.Translate) {
                        max = view.getBottom() + this.mSpinner;
                    }
                    canvas.drawRect(0.0f, view.getTop(), getWidth(), max, this.mPaint);
                }
                if ((this.mEnableClipHeaderWhenFixedBehind && this.mRefreshHeader.getSpinnerStyle() == SpinnerStyle.FixedBehind) || this.mRefreshHeader.getSpinnerStyle().scale) {
                    canvas.save();
                    canvas.clipRect(view.getLeft(), view.getTop(), view.getRight(), max);
                    boolean drawChild = super.drawChild(canvas, view, j);
                    canvas.restore();
                    return drawChild;
                }
            }
        }
        RefreshComponent refreshComponent2 = this.mRefreshFooter;
        if (refreshComponent2 != null && refreshComponent2.getView() == view) {
            if (!isEnableRefreshOrLoadMore(this.mEnableLoadMore) || (!this.mEnablePreviewInEditMode && isInEditMode())) {
                return true;
            }
            if (view2 != null) {
                int min = Math.min((view2.getBottom() - view2.getPaddingBottom()) + this.mSpinner, view.getBottom());
                int i2 = this.mFooterBackgroundColor;
                if (i2 != 0 && (paint = this.mPaint) != null) {
                    paint.setColor(i2);
                    if (this.mRefreshFooter.getSpinnerStyle().scale) {
                        min = view.getTop();
                    } else if (this.mRefreshFooter.getSpinnerStyle() == SpinnerStyle.Translate) {
                        min = view.getTop() + this.mSpinner;
                    }
                    canvas.drawRect(0.0f, min, getWidth(), view.getBottom(), this.mPaint);
                }
                if ((this.mEnableClipFooterWhenFixedBehind && this.mRefreshFooter.getSpinnerStyle() == SpinnerStyle.FixedBehind) || this.mRefreshFooter.getSpinnerStyle().scale) {
                    canvas.save();
                    canvas.clipRect(view.getLeft(), min, view.getRight(), view.getBottom());
                    boolean drawChild2 = super.drawChild(canvas, view, j);
                    canvas.restore();
                    return drawChild2;
                }
            }
        }
        return super.drawChild(canvas, view, j);
    }

    @Override // android.view.View
    public void computeScroll() {
        this.mScroller.getCurrY();
        if (this.mScroller.computeScrollOffset()) {
            int finalY = this.mScroller.getFinalY();
            if ((finalY < 0 && ((this.mEnableRefresh || this.mEnableOverScrollDrag) && this.mRefreshContent.canRefresh())) || (finalY > 0 && ((this.mEnableLoadMore || this.mEnableOverScrollDrag) && this.mRefreshContent.canLoadMore()))) {
                if (this.mVerticalPermit) {
                    animSpinnerBounce(finalY > 0 ? -this.mScroller.getCurrVelocity() : this.mScroller.getCurrVelocity());
                }
                this.mScroller.forceFinished(true);
                return;
            }
            this.mVerticalPermit = true;
            invalidate();
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:97:0x0114, code lost:
        if (r6 != 3) goto L99;
     */
    @Override // android.view.ViewGroup, android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean dispatchTouchEvent(android.view.MotionEvent r23) {
        /*
            Method dump skipped, instructions count: 885
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaopeng.xui.widget.XSmartRefreshLayout.dispatchTouchEvent(android.view.MotionEvent):boolean");
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public void requestDisallowInterceptTouchEvent(boolean z) {
        View scrollableView = this.mRefreshContent.getScrollableView();
        if ((Build.VERSION.SDK_INT >= 21 || !(scrollableView instanceof AbsListView)) && ViewCompat.isNestedScrollingEnabled(scrollableView)) {
            this.mEnableDisallowIntercept = z;
            super.requestDisallowInterceptTouchEvent(z);
        }
    }

    protected boolean startFlingIfNeed(float f) {
        if (f == 0.0f) {
            f = this.mCurrentVelocity;
        }
        if (Math.abs(f) > this.mMinimumVelocity) {
            if (this.mSpinner * f < 0.0f) {
                if (this.mState == RefreshState.Refreshing || this.mState == RefreshState.Loading || (this.mSpinner < 0 && this.mFooterNoMoreData)) {
                    this.animationRunnable = new FlingRunnable(f).start();
                    return true;
                } else if (this.mState.isReleaseToOpening) {
                    return true;
                }
            }
            if ((f < 0.0f && ((this.mEnableOverScrollBounce && (this.mEnableLoadMore || this.mEnableOverScrollDrag)) || ((this.mState == RefreshState.Loading && this.mSpinner >= 0) || (this.mEnableAutoLoadMore && isEnableRefreshOrLoadMore(this.mEnableLoadMore))))) || (f > 0.0f && ((this.mEnableOverScrollBounce && this.mEnableRefresh) || this.mEnableOverScrollDrag || (this.mState == RefreshState.Refreshing && this.mSpinner <= 0)))) {
                this.mVerticalPermit = false;
                this.mScroller.fling(0, 0, 0, (int) (-f), 0, 0, C.RATE_UNSET_INT, Integer.MAX_VALUE);
                this.mScroller.computeScrollOffset();
                invalidate();
            }
        }
        return false;
    }

    protected boolean interceptAnimatorByAction(int i) {
        if (i == 0) {
            if (this.reboundAnimator != null) {
                if (this.mState.isFinishing || this.mState == RefreshState.TwoLevelReleased || this.mState == RefreshState.RefreshReleased || this.mState == RefreshState.LoadReleased) {
                    return true;
                }
                if (this.mState == RefreshState.PullDownCanceled) {
                    this.mKernel.setState(RefreshState.PullDownToRefresh);
                } else if (this.mState == RefreshState.PullUpCanceled) {
                    this.mKernel.setState(RefreshState.PullUpToLoad);
                }
                this.reboundAnimator.setDuration(0L);
                this.reboundAnimator.cancel();
                this.reboundAnimator = null;
            }
            this.animationRunnable = null;
        }
        return this.reboundAnimator != null;
    }

    protected void notifyStateChanged(RefreshState refreshState) {
        RefreshState refreshState2 = this.mState;
        if (refreshState2 != refreshState) {
            this.mState = refreshState;
            this.mViceState = refreshState;
            RefreshComponent refreshComponent = this.mRefreshHeader;
            RefreshComponent refreshComponent2 = this.mRefreshFooter;
            OnMultiListener onMultiListener = this.mOnMultiListener;
            if (refreshComponent != null) {
                refreshComponent.onStateChanged(this, refreshState2, refreshState);
            }
            if (refreshComponent2 != null) {
                refreshComponent2.onStateChanged(this, refreshState2, refreshState);
            }
            if (onMultiListener != null) {
                onMultiListener.onStateChanged(this, refreshState2, refreshState);
            }
            if (refreshState == RefreshState.LoadFinish) {
                this.mFooterLocked = false;
            }
        } else if (this.mViceState != refreshState2) {
            this.mViceState = refreshState2;
        }
    }

    protected void setStateDirectLoading(boolean z) {
        if (this.mState != RefreshState.Loading) {
            this.mLastOpenTime = System.currentTimeMillis();
            this.mFooterLocked = true;
            notifyStateChanged(RefreshState.Loading);
            OnLoadMoreListener onLoadMoreListener = this.mLoadMoreListener;
            if (onLoadMoreListener != null) {
                if (z) {
                    onLoadMoreListener.onLoadMore(this);
                }
            } else if (this.mOnMultiListener == null) {
                finishLoadMore(2000);
            }
            RefreshComponent refreshComponent = this.mRefreshFooter;
            if (refreshComponent != null) {
                float f = this.mFooterMaxDragRate;
                if (f < 10.0f) {
                    f *= this.mFooterHeight;
                }
                refreshComponent.onStartAnimator(this, this.mFooterHeight, (int) f);
            }
            OnMultiListener onMultiListener = this.mOnMultiListener;
            if (onMultiListener == null || !(this.mRefreshFooter instanceof RefreshFooter)) {
                return;
            }
            if (z) {
                onMultiListener.onLoadMore(this);
            }
            float f2 = this.mFooterMaxDragRate;
            if (f2 < 10.0f) {
                f2 *= this.mFooterHeight;
            }
            this.mOnMultiListener.onFooterStartAnimator((RefreshFooter) this.mRefreshFooter, this.mFooterHeight, (int) f2);
        }
    }

    protected void setStateLoading(final boolean z) {
        AnimatorListenerAdapter animatorListenerAdapter = new AnimatorListenerAdapter() { // from class: com.xiaopeng.xui.widget.XSmartRefreshLayout.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                if (animator == null || animator.getDuration() != 0) {
                    XSmartRefreshLayout.this.setStateDirectLoading(z);
                }
            }
        };
        notifyStateChanged(RefreshState.LoadReleased);
        ValueAnimator animSpinner = this.mKernel.animSpinner(-this.mFooterHeight);
        if (animSpinner != null) {
            animSpinner.addListener(animatorListenerAdapter);
        }
        RefreshComponent refreshComponent = this.mRefreshFooter;
        if (refreshComponent != null) {
            float f = this.mFooterMaxDragRate;
            if (f < 10.0f) {
                f *= this.mFooterHeight;
            }
            refreshComponent.onReleased(this, this.mFooterHeight, (int) f);
        }
        OnMultiListener onMultiListener = this.mOnMultiListener;
        if (onMultiListener != null) {
            RefreshComponent refreshComponent2 = this.mRefreshFooter;
            if (refreshComponent2 instanceof RefreshFooter) {
                float f2 = this.mFooterMaxDragRate;
                if (f2 < 10.0f) {
                    f2 *= this.mFooterHeight;
                }
                onMultiListener.onFooterReleased((RefreshFooter) refreshComponent2, this.mFooterHeight, (int) f2);
            }
        }
        if (animSpinner == null) {
            animatorListenerAdapter.onAnimationEnd(null);
        }
    }

    protected void setStateRefreshing(final boolean z) {
        AnimatorListenerAdapter animatorListenerAdapter = new AnimatorListenerAdapter() { // from class: com.xiaopeng.xui.widget.XSmartRefreshLayout.2
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                if (animator == null || animator.getDuration() != 0) {
                    XSmartRefreshLayout.this.mLastOpenTime = System.currentTimeMillis();
                    XSmartRefreshLayout.this.notifyStateChanged(RefreshState.Refreshing);
                    if (XSmartRefreshLayout.this.mRefreshListener != null) {
                        if (z) {
                            XSmartRefreshLayout.this.mRefreshListener.onRefresh(XSmartRefreshLayout.this);
                        }
                    } else if (XSmartRefreshLayout.this.mOnMultiListener == null) {
                        XSmartRefreshLayout.this.finishRefresh(PathInterpolatorCompat.MAX_NUM_POINTS);
                    }
                    if (XSmartRefreshLayout.this.mRefreshHeader != null) {
                        float f = XSmartRefreshLayout.this.mHeaderMaxDragRate < 10.0f ? XSmartRefreshLayout.this.mHeaderHeight * XSmartRefreshLayout.this.mHeaderMaxDragRate : XSmartRefreshLayout.this.mHeaderMaxDragRate;
                        RefreshComponent refreshComponent = XSmartRefreshLayout.this.mRefreshHeader;
                        XSmartRefreshLayout xSmartRefreshLayout = XSmartRefreshLayout.this;
                        refreshComponent.onStartAnimator(xSmartRefreshLayout, xSmartRefreshLayout.mHeaderHeight, (int) f);
                    }
                    if (XSmartRefreshLayout.this.mOnMultiListener == null || !(XSmartRefreshLayout.this.mRefreshHeader instanceof RefreshHeader)) {
                        return;
                    }
                    if (z) {
                        XSmartRefreshLayout.this.mOnMultiListener.onRefresh(XSmartRefreshLayout.this);
                    }
                    XSmartRefreshLayout.this.mOnMultiListener.onHeaderStartAnimator((RefreshHeader) XSmartRefreshLayout.this.mRefreshHeader, XSmartRefreshLayout.this.mHeaderHeight, (int) (XSmartRefreshLayout.this.mHeaderMaxDragRate < 10.0f ? XSmartRefreshLayout.this.mHeaderHeight * XSmartRefreshLayout.this.mHeaderMaxDragRate : XSmartRefreshLayout.this.mHeaderMaxDragRate));
                }
            }
        };
        notifyStateChanged(RefreshState.RefreshReleased);
        ValueAnimator animSpinner = this.mKernel.animSpinner(this.mHeaderHeight);
        if (animSpinner != null) {
            animSpinner.addListener(animatorListenerAdapter);
        }
        RefreshComponent refreshComponent = this.mRefreshHeader;
        if (refreshComponent != null) {
            float f = this.mHeaderMaxDragRate;
            if (f < 10.0f) {
                f *= this.mHeaderHeight;
            }
            refreshComponent.onReleased(this, this.mHeaderHeight, (int) f);
        }
        OnMultiListener onMultiListener = this.mOnMultiListener;
        if (onMultiListener != null) {
            RefreshComponent refreshComponent2 = this.mRefreshHeader;
            if (refreshComponent2 instanceof RefreshHeader) {
                float f2 = this.mHeaderMaxDragRate;
                if (f2 < 10.0f) {
                    f2 *= this.mHeaderHeight;
                }
                onMultiListener.onHeaderReleased((RefreshHeader) refreshComponent2, this.mHeaderHeight, (int) f2);
            }
        }
        if (animSpinner == null) {
            animatorListenerAdapter.onAnimationEnd(null);
        }
    }

    protected void setViceState(RefreshState refreshState) {
        if (this.mState.isDragging && this.mState.isHeader != refreshState.isHeader) {
            notifyStateChanged(RefreshState.None);
        }
        if (this.mViceState != refreshState) {
            this.mViceState = refreshState;
        }
    }

    protected boolean isEnableTranslationContent(boolean z, RefreshComponent refreshComponent) {
        return z || this.mEnablePureScrollMode || refreshComponent == null || refreshComponent.getSpinnerStyle() == SpinnerStyle.FixedBehind;
    }

    protected boolean isEnableRefreshOrLoadMore(boolean z) {
        return z && !this.mEnablePureScrollMode;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes2.dex */
    public class FlingRunnable implements Runnable {
        int mOffset;
        float mVelocity;
        int mFrame = 0;
        int mFrameDelay = 10;
        float mDamping = 0.98f;
        long mStartTime = 0;
        long mLastTime = AnimationUtils.currentAnimationTimeMillis();

        FlingRunnable(float f) {
            this.mVelocity = f;
            this.mOffset = XSmartRefreshLayout.this.mSpinner;
        }

        /* JADX WARN: Code restructure failed: missing block: B:16:0x0032, code lost:
            if (r0.isEnableRefreshOrLoadMore(r0.mEnableLoadMore) != false) goto L36;
         */
        /* JADX WARN: Code restructure failed: missing block: B:26:0x0056, code lost:
            if (r0.isEnableRefreshOrLoadMore(r0.mEnableLoadMore) != false) goto L50;
         */
        /* JADX WARN: Code restructure failed: missing block: B:28:0x0061, code lost:
            if (r10.this$0.mSpinner >= (-r10.this$0.mFooterHeight)) goto L46;
         */
        /* JADX WARN: Code restructure failed: missing block: B:32:0x0073, code lost:
            if (r10.this$0.mSpinner > r10.this$0.mHeaderHeight) goto L17;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public java.lang.Runnable start() {
            /*
                Method dump skipped, instructions count: 229
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: com.xiaopeng.xui.widget.XSmartRefreshLayout.FlingRunnable.start():java.lang.Runnable");
        }

        @Override // java.lang.Runnable
        public void run() {
            if (XSmartRefreshLayout.this.animationRunnable != this || XSmartRefreshLayout.this.mState.isFinishing) {
                return;
            }
            long currentAnimationTimeMillis = AnimationUtils.currentAnimationTimeMillis();
            float pow = (float) (this.mVelocity * Math.pow(this.mDamping, ((float) (currentAnimationTimeMillis - this.mStartTime)) / (1000.0f / this.mFrameDelay)));
            this.mVelocity = pow;
            float f = pow * ((((float) (currentAnimationTimeMillis - this.mLastTime)) * 1.0f) / 1000.0f);
            if (Math.abs(f) > 1.0f) {
                this.mLastTime = currentAnimationTimeMillis;
                this.mOffset = (int) (this.mOffset + f);
                if (XSmartRefreshLayout.this.mSpinner * this.mOffset > 0) {
                    XSmartRefreshLayout.this.mKernel.moveSpinner(this.mOffset, true);
                    XSmartRefreshLayout.this.mHandler.postDelayed(this, this.mFrameDelay);
                    return;
                }
                XSmartRefreshLayout.this.animationRunnable = null;
                XSmartRefreshLayout.this.mKernel.moveSpinner(0, true);
                SmartUtil.fling(XSmartRefreshLayout.this.mRefreshContent.getScrollableView(), (int) (-this.mVelocity));
                if (!XSmartRefreshLayout.this.mFooterLocked || f <= 0.0f) {
                    return;
                }
                XSmartRefreshLayout.this.mFooterLocked = false;
                return;
            }
            XSmartRefreshLayout.this.animationRunnable = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes2.dex */
    public class BounceRunnable implements Runnable {
        int mSmoothDistance;
        float mVelocity;
        int mFrame = 0;
        int mFrameDelay = 10;
        float mOffset = 0.0f;
        long mLastTime = AnimationUtils.currentAnimationTimeMillis();

        BounceRunnable(float f, int i) {
            this.mVelocity = f;
            this.mSmoothDistance = i;
            XSmartRefreshLayout.this.mHandler.postDelayed(this, this.mFrameDelay);
            if (f > 0.0f) {
                XSmartRefreshLayout.this.mKernel.setState(RefreshState.PullDownToRefresh);
            } else {
                XSmartRefreshLayout.this.mKernel.setState(RefreshState.PullUpToLoad);
            }
        }

        @Override // java.lang.Runnable
        public void run() {
            if (XSmartRefreshLayout.this.animationRunnable != this || XSmartRefreshLayout.this.mState.isFinishing) {
                return;
            }
            if (Math.abs(XSmartRefreshLayout.this.mSpinner) >= Math.abs(this.mSmoothDistance)) {
                if (this.mSmoothDistance != 0) {
                    int i = this.mFrame + 1;
                    this.mFrame = i;
                    this.mVelocity = (float) (this.mVelocity * Math.pow(0.44999998807907104d, i * 2));
                } else {
                    int i2 = this.mFrame + 1;
                    this.mFrame = i2;
                    this.mVelocity = (float) (this.mVelocity * Math.pow(0.8500000238418579d, i2 * 2));
                }
            } else {
                int i3 = this.mFrame + 1;
                this.mFrame = i3;
                this.mVelocity = (float) (this.mVelocity * Math.pow(0.949999988079071d, i3 * 2));
            }
            long currentAnimationTimeMillis = AnimationUtils.currentAnimationTimeMillis();
            float f = this.mVelocity * ((((float) (currentAnimationTimeMillis - this.mLastTime)) * 1.0f) / 1000.0f);
            if (Math.abs(f) >= 1.0f) {
                this.mLastTime = currentAnimationTimeMillis;
                float f2 = this.mOffset + f;
                this.mOffset = f2;
                XSmartRefreshLayout.this.moveSpinnerInfinitely(f2);
                XSmartRefreshLayout.this.mHandler.postDelayed(this, this.mFrameDelay);
                return;
            }
            if (XSmartRefreshLayout.this.mViceState.isDragging && XSmartRefreshLayout.this.mViceState.isHeader) {
                XSmartRefreshLayout.this.mKernel.setState(RefreshState.PullDownCanceled);
            } else if (XSmartRefreshLayout.this.mViceState.isDragging && XSmartRefreshLayout.this.mViceState.isFooter) {
                XSmartRefreshLayout.this.mKernel.setState(RefreshState.PullUpCanceled);
            }
            XSmartRefreshLayout.this.animationRunnable = null;
            if (Math.abs(XSmartRefreshLayout.this.mSpinner) >= Math.abs(this.mSmoothDistance)) {
                XSmartRefreshLayout xSmartRefreshLayout = XSmartRefreshLayout.this;
                xSmartRefreshLayout.animSpinner(this.mSmoothDistance, 0, xSmartRefreshLayout.mReboundInterpolator, Math.min(Math.max((int) SmartUtil.px2dp(Math.abs(XSmartRefreshLayout.this.mSpinner - this.mSmoothDistance)), 30), 100) * 10);
            }
        }
    }

    protected ValueAnimator animSpinner(int i, int i2, Interpolator interpolator, int i3) {
        if (this.mSpinner != i) {
            ValueAnimator valueAnimator = this.reboundAnimator;
            if (valueAnimator != null) {
                valueAnimator.setDuration(0L);
                this.reboundAnimator.cancel();
                this.reboundAnimator = null;
            }
            this.animationRunnable = null;
            ValueAnimator ofInt = ValueAnimator.ofInt(this.mSpinner, i);
            this.reboundAnimator = ofInt;
            ofInt.setDuration(i3);
            this.reboundAnimator.setInterpolator(interpolator);
            this.reboundAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.xiaopeng.xui.widget.XSmartRefreshLayout.3
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    if (animator == null || animator.getDuration() != 0) {
                        XSmartRefreshLayout.this.reboundAnimator = null;
                        if (XSmartRefreshLayout.this.mSpinner == 0 && XSmartRefreshLayout.this.mState != RefreshState.None && !XSmartRefreshLayout.this.mState.isOpening && !XSmartRefreshLayout.this.mState.isDragging) {
                            XSmartRefreshLayout.this.notifyStateChanged(RefreshState.None);
                        } else if (XSmartRefreshLayout.this.mState != XSmartRefreshLayout.this.mViceState) {
                            XSmartRefreshLayout xSmartRefreshLayout = XSmartRefreshLayout.this;
                            xSmartRefreshLayout.setViceState(xSmartRefreshLayout.mState);
                        }
                    }
                }
            });
            this.reboundAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.xiaopeng.xui.widget.XSmartRefreshLayout.4
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    XSmartRefreshLayout.this.mKernel.moveSpinner(((Integer) valueAnimator2.getAnimatedValue()).intValue(), false);
                }
            });
            this.reboundAnimator.setStartDelay(i2);
            this.reboundAnimator.start();
            return this.reboundAnimator;
        }
        return null;
    }

    protected void animSpinnerBounce(float f) {
        if (this.reboundAnimator == null) {
            if (f > 0.0f && (this.mState == RefreshState.Refreshing || this.mState == RefreshState.TwoLevel)) {
                this.animationRunnable = new BounceRunnable(f, this.mHeaderHeight);
            } else if (f < 0.0f && (this.mState == RefreshState.Loading || ((this.mEnableFooterFollowWhenNoMoreData && this.mFooterNoMoreData && this.mFooterNoMoreDataEffective && isEnableRefreshOrLoadMore(this.mEnableLoadMore)) || (this.mEnableAutoLoadMore && !this.mFooterNoMoreData && isEnableRefreshOrLoadMore(this.mEnableLoadMore) && this.mState != RefreshState.Refreshing)))) {
                this.animationRunnable = new BounceRunnable(f, -this.mFooterHeight);
            } else if (this.mSpinner == 0 && this.mEnableOverScrollBounce) {
                this.animationRunnable = new BounceRunnable(f, 0);
            }
        }
    }

    protected void overSpinner() {
        if (this.mState == RefreshState.TwoLevel) {
            if (this.mCurrentVelocity > -1000 && this.mSpinner > getHeight() / 2) {
                ValueAnimator animSpinner = this.mKernel.animSpinner(getHeight());
                if (animSpinner != null) {
                    animSpinner.setDuration(this.mFloorDuration);
                }
            } else if (this.mIsBeingDragged) {
                this.mKernel.finishTwoLevel();
            }
        } else if (this.mState == RefreshState.Loading || (this.mEnableFooterFollowWhenNoMoreData && this.mFooterNoMoreData && this.mFooterNoMoreDataEffective && this.mSpinner < 0 && isEnableRefreshOrLoadMore(this.mEnableLoadMore))) {
            int i = this.mSpinner;
            int i2 = this.mFooterHeight;
            if (i < (-i2)) {
                this.mKernel.animSpinner(-i2);
            } else if (i > 0) {
                this.mKernel.animSpinner(0);
            }
        } else if (this.mState == RefreshState.Refreshing) {
            int i3 = this.mSpinner;
            int i4 = this.mHeaderHeight;
            if (i3 > i4) {
                this.mKernel.animSpinner(i4);
            } else if (i3 < 0) {
                this.mKernel.animSpinner(0);
            }
        } else if (this.mState == RefreshState.PullDownToRefresh) {
            this.mKernel.setState(RefreshState.PullDownCanceled);
        } else if (this.mState == RefreshState.PullUpToLoad) {
            this.mKernel.setState(RefreshState.PullUpCanceled);
        } else if (this.mState == RefreshState.ReleaseToRefresh) {
            this.mKernel.setState(RefreshState.Refreshing);
        } else if (this.mState == RefreshState.ReleaseToLoad) {
            this.mKernel.setState(RefreshState.Loading);
        } else if (this.mState == RefreshState.ReleaseToTwoLevel) {
            this.mKernel.setState(RefreshState.TwoLevelReleased);
        } else if (this.mState == RefreshState.RefreshReleased) {
            if (this.reboundAnimator == null) {
                this.mKernel.animSpinner(this.mHeaderHeight);
            }
        } else if (this.mState == RefreshState.LoadReleased) {
            if (this.reboundAnimator == null) {
                this.mKernel.animSpinner(-this.mFooterHeight);
            }
        } else if (this.mState == RefreshState.LoadFinish || this.mSpinner == 0) {
        } else {
            this.mKernel.animSpinner(0);
        }
    }

    protected void moveSpinnerInfinitely(float f) {
        float f2 = (!this.mNestedInProgress || this.mEnableLoadMoreWhenContentNotFull || f >= 0.0f || this.mRefreshContent.canLoadMore()) ? f : 0.0f;
        if (f2 > this.mScreenHeightPixels * 5 && getTag() == null && getTag(R.id.srl_tag) == null) {
            float f3 = this.mLastTouchY;
            int i = this.mScreenHeightPixels;
            if (f3 < i / 6.0f && this.mLastTouchX < i / 16.0f) {
                Toast.makeText(getContext(), "你这么死拉，臣妾做不到啊！", 0).show();
                setTag(R.id.srl_tag, "你这么死拉，臣妾做不到啊！");
            }
        }
        if (this.mState == RefreshState.TwoLevel && f2 > 0.0f) {
            this.mKernel.moveSpinner(Math.min((int) f2, getMeasuredHeight()), true);
        } else if (this.mState == RefreshState.Refreshing && f2 >= 0.0f) {
            int i2 = this.mHeaderHeight;
            if (f2 < i2) {
                this.mKernel.moveSpinner((int) f2, true);
            } else {
                float f4 = this.mHeaderMaxDragRate;
                if (f4 < 10.0f) {
                    f4 *= i2;
                }
                double d = f4 - i2;
                int max = Math.max((this.mScreenHeightPixels * 4) / 3, getHeight());
                int i3 = this.mHeaderHeight;
                double d2 = max - i3;
                double max2 = Math.max(0.0f, (f2 - i3) * this.mDragRate);
                double d3 = -max2;
                if (d2 == 0.0d) {
                    d2 = 1.0d;
                }
                this.mKernel.moveSpinner(((int) Math.min(d * (1.0d - Math.pow(100.0d, d3 / d2)), max2)) + this.mHeaderHeight, true);
            }
        } else if (f2 < 0.0f && (this.mState == RefreshState.Loading || ((this.mEnableFooterFollowWhenNoMoreData && this.mFooterNoMoreData && this.mFooterNoMoreDataEffective && isEnableRefreshOrLoadMore(this.mEnableLoadMore)) || (this.mEnableAutoLoadMore && !this.mFooterNoMoreData && isEnableRefreshOrLoadMore(this.mEnableLoadMore))))) {
            int i4 = this.mFooterHeight;
            if (f2 > (-i4)) {
                this.mKernel.moveSpinner((int) f2, true);
            } else {
                float f5 = this.mFooterMaxDragRate;
                if (f5 < 10.0f) {
                    f5 *= i4;
                }
                double d4 = f5 - i4;
                int max3 = Math.max((this.mScreenHeightPixels * 4) / 3, getHeight());
                int i5 = this.mFooterHeight;
                double d5 = max3 - i5;
                double d6 = -Math.min(0.0f, (i5 + f2) * this.mDragRate);
                double d7 = -d6;
                if (d5 == 0.0d) {
                    d5 = 1.0d;
                }
                this.mKernel.moveSpinner(((int) (-Math.min(d4 * (1.0d - Math.pow(100.0d, d7 / d5)), d6))) - this.mFooterHeight, true);
            }
        } else if (f2 >= 0.0f) {
            float f6 = this.mHeaderMaxDragRate;
            double d8 = f6 < 10.0f ? this.mHeaderHeight * f6 : f6;
            double max4 = Math.max(this.mScreenHeightPixels / 2, getHeight());
            double max5 = Math.max(0.0f, this.mDragRate * f2);
            double d9 = -max5;
            if (max4 == 0.0d) {
                max4 = 1.0d;
            }
            this.mKernel.moveSpinner((int) Math.min(d8 * (1.0d - Math.pow(100.0d, d9 / max4)), max5), true);
        } else {
            float f7 = this.mFooterMaxDragRate;
            double d10 = f7 < 10.0f ? this.mFooterHeight * f7 : f7;
            double max6 = Math.max(this.mScreenHeightPixels / 2, getHeight());
            double d11 = -Math.min(0.0f, this.mDragRate * f2);
            this.mKernel.moveSpinner((int) (-Math.min(d10 * (1.0d - Math.pow(100.0d, (-d11) / (max6 == 0.0d ? 1.0d : max6))), d11)), true);
        }
        if (!this.mEnableAutoLoadMore || this.mFooterNoMoreData || !isEnableRefreshOrLoadMore(this.mEnableLoadMore) || f2 >= 0.0f || this.mState == RefreshState.Refreshing || this.mState == RefreshState.Loading || this.mState == RefreshState.LoadFinish) {
            return;
        }
        if (this.mDisableContentWhenLoading) {
            this.animationRunnable = null;
            this.mKernel.animSpinner(-this.mFooterHeight);
        }
        setStateDirectLoading(false);
        this.mHandler.postDelayed(new Runnable() { // from class: com.xiaopeng.xui.widget.XSmartRefreshLayout.5
            @Override // java.lang.Runnable
            public void run() {
                if (XSmartRefreshLayout.this.mLoadMoreListener != null) {
                    XSmartRefreshLayout.this.mLoadMoreListener.onLoadMore(XSmartRefreshLayout.this);
                } else if (XSmartRefreshLayout.this.mOnMultiListener == null) {
                    XSmartRefreshLayout.this.finishLoadMore(2000);
                }
                OnMultiListener onMultiListener = XSmartRefreshLayout.this.mOnMultiListener;
                if (onMultiListener != null) {
                    onMultiListener.onLoadMore(XSmartRefreshLayout.this);
                }
            }
        }, this.mReboundDuration);
    }

    @Override // android.view.ViewGroup
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new LayoutParams(getContext(), attributeSet);
    }

    /* loaded from: classes2.dex */
    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        public int backgroundColor;
        public SpinnerStyle spinnerStyle;

        public LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            this.backgroundColor = 0;
            this.spinnerStyle = null;
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.SmartRefreshLayout_Layout);
            this.backgroundColor = obtainStyledAttributes.getColor(R.styleable.SmartRefreshLayout_Layout_layout_srlBackgroundColor, this.backgroundColor);
            if (obtainStyledAttributes.hasValue(R.styleable.SmartRefreshLayout_Layout_layout_srlSpinnerStyle)) {
                this.spinnerStyle = SpinnerStyle.values[obtainStyledAttributes.getInt(R.styleable.SmartRefreshLayout_Layout_layout_srlSpinnerStyle, SpinnerStyle.Translate.ordinal)];
            }
            obtainStyledAttributes.recycle();
        }

        public LayoutParams(int i, int i2) {
            super(i, i2);
            this.backgroundColor = 0;
            this.spinnerStyle = null;
        }
    }

    @Override // android.view.ViewGroup, androidx.core.view.NestedScrollingParent
    public int getNestedScrollAxes() {
        return this.mNestedParent.getNestedScrollAxes();
    }

    @Override // android.view.ViewGroup, android.view.ViewParent, androidx.core.view.NestedScrollingParent
    public boolean onStartNestedScroll(View view, View view2, int i) {
        return (isEnabled() && isNestedScrollingEnabled() && (i & 2) != 0) && (this.mEnableOverScrollDrag || this.mEnableRefresh || this.mEnableLoadMore);
    }

    @Override // android.view.ViewGroup, android.view.ViewParent, androidx.core.view.NestedScrollingParent
    public void onNestedScrollAccepted(View view, View view2, int i) {
        this.mNestedParent.onNestedScrollAccepted(view, view2, i);
        this.mNestedChild.startNestedScroll(i & 2);
        this.mTotalUnconsumed = this.mSpinner;
        this.mNestedInProgress = true;
        interceptAnimatorByAction(0);
    }

    @Override // android.view.ViewGroup, android.view.ViewParent, androidx.core.view.NestedScrollingParent
    public void onNestedPreScroll(View view, int i, int i2, int[] iArr) {
        int i3 = this.mTotalUnconsumed;
        int i4 = 0;
        if (i2 * i3 > 0) {
            if (Math.abs(i2) > Math.abs(this.mTotalUnconsumed)) {
                int i5 = this.mTotalUnconsumed;
                this.mTotalUnconsumed = 0;
                i4 = i5;
            } else {
                this.mTotalUnconsumed -= i2;
                i4 = i2;
            }
            moveSpinnerInfinitely(this.mTotalUnconsumed);
        } else if (i2 > 0 && this.mFooterLocked) {
            int i6 = i3 - i2;
            this.mTotalUnconsumed = i6;
            moveSpinnerInfinitely(i6);
            i4 = i2;
        }
        this.mNestedChild.dispatchNestedPreScroll(i, i2 - i4, iArr, null);
        iArr[1] = iArr[1] + i4;
    }

    @Override // android.view.ViewGroup, android.view.ViewParent, androidx.core.view.NestedScrollingParent
    public void onNestedScroll(View view, int i, int i2, int i3, int i4) {
        ScrollBoundaryDecider scrollBoundaryDecider;
        ViewParent parent;
        ScrollBoundaryDecider scrollBoundaryDecider2;
        boolean dispatchNestedScroll = this.mNestedChild.dispatchNestedScroll(i, i2, i3, i4, this.mParentOffsetInWindow);
        int i5 = i4 + this.mParentOffsetInWindow[1];
        if ((i5 < 0 && ((this.mEnableRefresh || this.mEnableOverScrollDrag) && (this.mTotalUnconsumed != 0 || (scrollBoundaryDecider2 = this.mScrollBoundaryDecider) == null || scrollBoundaryDecider2.canRefresh(this.mRefreshContent.getView())))) || (i5 > 0 && ((this.mEnableLoadMore || this.mEnableOverScrollDrag) && (this.mTotalUnconsumed != 0 || (scrollBoundaryDecider = this.mScrollBoundaryDecider) == null || scrollBoundaryDecider.canLoadMore(this.mRefreshContent.getView()))))) {
            if (this.mViceState == RefreshState.None || this.mViceState.isOpening) {
                this.mKernel.setState(i5 > 0 ? RefreshState.PullUpToLoad : RefreshState.PullDownToRefresh);
                if (!dispatchNestedScroll && (parent = getParent()) != null) {
                    parent.requestDisallowInterceptTouchEvent(true);
                }
            }
            int i6 = this.mTotalUnconsumed - i5;
            this.mTotalUnconsumed = i6;
            moveSpinnerInfinitely(i6);
        }
        if (!this.mFooterLocked || i2 >= 0) {
            return;
        }
        this.mFooterLocked = false;
    }

    @Override // android.view.ViewGroup, android.view.ViewParent, androidx.core.view.NestedScrollingParent
    public boolean onNestedPreFling(View view, float f, float f2) {
        return (this.mFooterLocked && f2 > 0.0f) || startFlingIfNeed(-f2) || this.mNestedChild.dispatchNestedPreFling(f, f2);
    }

    @Override // android.view.ViewGroup, android.view.ViewParent, androidx.core.view.NestedScrollingParent
    public boolean onNestedFling(View view, float f, float f2, boolean z) {
        return this.mNestedChild.dispatchNestedFling(f, f2, z);
    }

    @Override // android.view.ViewGroup, android.view.ViewParent, androidx.core.view.NestedScrollingParent
    public void onStopNestedScroll(View view) {
        this.mNestedParent.onStopNestedScroll(view);
        this.mNestedInProgress = false;
        this.mTotalUnconsumed = 0;
        overSpinner();
        this.mNestedChild.stopNestedScroll();
    }

    @Override // android.view.View
    public void setNestedScrollingEnabled(boolean z) {
        this.mEnableNestedScrolling = z;
        this.mNestedChild.setNestedScrollingEnabled(z);
    }

    @Override // android.view.View
    public boolean isNestedScrollingEnabled() {
        return this.mEnableNestedScrolling && (this.mEnableOverScrollDrag || this.mEnableRefresh || this.mEnableLoadMore);
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout setHeaderHeight(float f) {
        return setHeaderHeightPx(SmartUtil.dp2px(f));
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout setHeaderHeightPx(int i) {
        if (i != this.mHeaderHeight && this.mHeaderHeightStatus.canReplaceWith(DimensionStatus.CodeExact)) {
            this.mHeaderHeight = i;
            if (this.mRefreshHeader != null && this.mAttachedToWindow && this.mHeaderHeightStatus.notified) {
                SpinnerStyle spinnerStyle = this.mRefreshHeader.getSpinnerStyle();
                if (spinnerStyle != SpinnerStyle.MatchLayout && !spinnerStyle.scale) {
                    View view = this.mRefreshHeader.getView();
                    ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                    ViewGroup.MarginLayoutParams marginLayoutParams = layoutParams instanceof ViewGroup.MarginLayoutParams ? (ViewGroup.MarginLayoutParams) layoutParams : sDefaultMarginLP;
                    view.measure(View.MeasureSpec.makeMeasureSpec(view.getMeasuredWidth(), 1073741824), View.MeasureSpec.makeMeasureSpec(Math.max((this.mHeaderHeight - marginLayoutParams.bottomMargin) - marginLayoutParams.topMargin, 0), 1073741824));
                    int i2 = marginLayoutParams.leftMargin;
                    int i3 = (marginLayoutParams.topMargin + this.mHeaderInsetStart) - (spinnerStyle == SpinnerStyle.Translate ? this.mHeaderHeight : 0);
                    view.layout(i2, i3, view.getMeasuredWidth() + i2, view.getMeasuredHeight() + i3);
                }
                float f = this.mHeaderMaxDragRate;
                if (f < 10.0f) {
                    f *= this.mHeaderHeight;
                }
                this.mHeaderHeightStatus = DimensionStatus.CodeExact;
                this.mRefreshHeader.onInitialized(this.mKernel, this.mHeaderHeight, (int) f);
            } else {
                this.mHeaderHeightStatus = DimensionStatus.CodeExactUnNotify;
            }
        }
        return this;
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout setFooterHeight(float f) {
        return setFooterHeightPx(SmartUtil.dp2px(f));
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout setFooterHeightPx(int i) {
        if (i != this.mFooterHeight && this.mFooterHeightStatus.canReplaceWith(DimensionStatus.CodeExact)) {
            this.mFooterHeight = i;
            if (this.mRefreshFooter != null && this.mAttachedToWindow && this.mFooterHeightStatus.notified) {
                SpinnerStyle spinnerStyle = this.mRefreshFooter.getSpinnerStyle();
                if (spinnerStyle != SpinnerStyle.MatchLayout && !spinnerStyle.scale) {
                    View view = this.mRefreshFooter.getView();
                    ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                    ViewGroup.MarginLayoutParams marginLayoutParams = layoutParams instanceof ViewGroup.MarginLayoutParams ? (ViewGroup.MarginLayoutParams) layoutParams : sDefaultMarginLP;
                    view.measure(View.MeasureSpec.makeMeasureSpec(view.getMeasuredWidth(), 1073741824), View.MeasureSpec.makeMeasureSpec(Math.max((this.mFooterHeight - marginLayoutParams.bottomMargin) - marginLayoutParams.topMargin, 0), 1073741824));
                    int i2 = marginLayoutParams.leftMargin;
                    int measuredHeight = ((marginLayoutParams.topMargin + getMeasuredHeight()) - this.mFooterInsetStart) - (spinnerStyle != SpinnerStyle.Translate ? this.mFooterHeight : 0);
                    view.layout(i2, measuredHeight, view.getMeasuredWidth() + i2, view.getMeasuredHeight() + measuredHeight);
                }
                float f = this.mFooterMaxDragRate;
                if (f < 10.0f) {
                    f *= this.mFooterHeight;
                }
                this.mFooterHeightStatus = DimensionStatus.CodeExact;
                this.mRefreshFooter.onInitialized(this.mKernel, this.mFooterHeight, (int) f);
            } else {
                this.mFooterHeightStatus = DimensionStatus.CodeExactUnNotify;
            }
        }
        return this;
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout setHeaderInsetStart(float f) {
        this.mHeaderInsetStart = SmartUtil.dp2px(f);
        return this;
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout setHeaderInsetStartPx(int i) {
        this.mHeaderInsetStart = i;
        return this;
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout setFooterInsetStart(float f) {
        this.mFooterInsetStart = SmartUtil.dp2px(f);
        return this;
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout setFooterInsetStartPx(int i) {
        this.mFooterInsetStart = i;
        return this;
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout setDragRate(float f) {
        this.mDragRate = f;
        return this;
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout setHeaderMaxDragRate(float f) {
        this.mHeaderMaxDragRate = f;
        RefreshComponent refreshComponent = this.mRefreshHeader;
        if (refreshComponent != null && this.mAttachedToWindow) {
            if (f < 10.0f) {
                f *= this.mHeaderHeight;
            }
            refreshComponent.onInitialized(this.mKernel, this.mHeaderHeight, (int) f);
        } else {
            this.mHeaderHeightStatus = this.mHeaderHeightStatus.unNotify();
        }
        return this;
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout setFooterMaxDragRate(float f) {
        this.mFooterMaxDragRate = f;
        RefreshComponent refreshComponent = this.mRefreshFooter;
        if (refreshComponent != null && this.mAttachedToWindow) {
            if (f < 10.0f) {
                f *= this.mFooterHeight;
            }
            refreshComponent.onInitialized(this.mKernel, this.mFooterHeight, (int) f);
        } else {
            this.mFooterHeightStatus = this.mFooterHeightStatus.unNotify();
        }
        return this;
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout setHeaderTriggerRate(float f) {
        this.mHeaderTriggerRate = f;
        return this;
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout setFooterTriggerRate(float f) {
        this.mFooterTriggerRate = f;
        return this;
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout setReboundInterpolator(Interpolator interpolator) {
        this.mReboundInterpolator = interpolator;
        return this;
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout setReboundDuration(int i) {
        this.mReboundDuration = i;
        return this;
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout setEnableLoadMore(boolean z) {
        this.mManualLoadMore = true;
        this.mEnableLoadMore = z;
        return this;
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout setEnableRefresh(boolean z) {
        this.mEnableRefresh = z;
        return this;
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout setEnableHeaderTranslationContent(boolean z) {
        this.mEnableHeaderTranslationContent = z;
        this.mManualHeaderTranslationContent = true;
        return this;
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout setEnableFooterTranslationContent(boolean z) {
        this.mEnableFooterTranslationContent = z;
        this.mManualFooterTranslationContent = true;
        return this;
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout setEnableAutoLoadMore(boolean z) {
        this.mEnableAutoLoadMore = z;
        return this;
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout setEnableOverScrollBounce(boolean z) {
        this.mEnableOverScrollBounce = z;
        return this;
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout setEnablePureScrollMode(boolean z) {
        this.mEnablePureScrollMode = z;
        return this;
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout setEnableScrollContentWhenLoaded(boolean z) {
        this.mEnableScrollContentWhenLoaded = z;
        return this;
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout setEnableScrollContentWhenRefreshed(boolean z) {
        this.mEnableScrollContentWhenRefreshed = z;
        return this;
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout setEnableLoadMoreWhenContentNotFull(boolean z) {
        this.mEnableLoadMoreWhenContentNotFull = z;
        RefreshContent refreshContent = this.mRefreshContent;
        if (refreshContent != null) {
            refreshContent.setEnableLoadMoreWhenContentNotFull(z);
        }
        return this;
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout setEnableOverScrollDrag(boolean z) {
        this.mEnableOverScrollDrag = z;
        return this;
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout setEnableFooterFollowWhenNoMoreData(boolean z) {
        this.mEnableFooterFollowWhenNoMoreData = z;
        return this;
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout setEnableClipHeaderWhenFixedBehind(boolean z) {
        this.mEnableClipHeaderWhenFixedBehind = z;
        return this;
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout setEnableClipFooterWhenFixedBehind(boolean z) {
        this.mEnableClipFooterWhenFixedBehind = z;
        return this;
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout setEnableNestedScroll(boolean z) {
        setNestedScrollingEnabled(z);
        return this;
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout setFixedHeaderViewId(int i) {
        this.mFixedHeaderViewId = i;
        return this;
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout setFixedFooterViewId(int i) {
        this.mFixedFooterViewId = i;
        return this;
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout setHeaderTranslationViewId(int i) {
        this.mHeaderTranslationViewId = i;
        return this;
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout setFooterTranslationViewId(int i) {
        this.mFooterTranslationViewId = i;
        return this;
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout setDisableContentWhenRefresh(boolean z) {
        this.mDisableContentWhenRefresh = z;
        return this;
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout setDisableContentWhenLoading(boolean z) {
        this.mDisableContentWhenLoading = z;
        return this;
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout setRefreshHeader(RefreshHeader refreshHeader) {
        return setRefreshHeader(refreshHeader, 0, 0);
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout setRefreshHeader(RefreshHeader refreshHeader, int i, int i2) {
        RefreshComponent refreshComponent;
        RefreshComponent refreshComponent2 = this.mRefreshHeader;
        if (refreshComponent2 != null) {
            super.removeView(refreshComponent2.getView());
        }
        this.mRefreshHeader = refreshHeader;
        this.mHeaderBackgroundColor = 0;
        this.mHeaderNeedTouchEventWhenRefreshing = false;
        this.mHeaderHeightStatus = DimensionStatus.DefaultUnNotify;
        if (i == 0) {
            i = -1;
        }
        if (i2 == 0) {
            i2 = -2;
        }
        LayoutParams layoutParams = new LayoutParams(i, i2);
        ViewGroup.LayoutParams layoutParams2 = refreshHeader.getView().getLayoutParams();
        if (layoutParams2 instanceof LayoutParams) {
            layoutParams = (LayoutParams) layoutParams2;
        }
        if (this.mRefreshHeader.getSpinnerStyle().front) {
            super.addView(this.mRefreshHeader.getView(), getChildCount(), layoutParams);
        } else {
            super.addView(this.mRefreshHeader.getView(), 0, layoutParams);
        }
        int[] iArr = this.mPrimaryColors;
        if (iArr != null && (refreshComponent = this.mRefreshHeader) != null) {
            refreshComponent.setPrimaryColors(iArr);
        }
        return this;
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout setRefreshFooter(RefreshFooter refreshFooter) {
        return setRefreshFooter(refreshFooter, 0, 0);
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout setRefreshFooter(RefreshFooter refreshFooter, int i, int i2) {
        RefreshComponent refreshComponent;
        RefreshComponent refreshComponent2 = this.mRefreshFooter;
        if (refreshComponent2 != null) {
            super.removeView(refreshComponent2.getView());
        }
        this.mRefreshFooter = refreshFooter;
        this.mFooterLocked = false;
        this.mFooterBackgroundColor = 0;
        this.mFooterNoMoreDataEffective = false;
        this.mFooterNeedTouchEventWhenLoading = false;
        this.mFooterHeightStatus = DimensionStatus.DefaultUnNotify;
        this.mEnableLoadMore = !this.mManualLoadMore || this.mEnableLoadMore;
        if (i == 0) {
            i = -1;
        }
        if (i2 == 0) {
            i2 = -2;
        }
        LayoutParams layoutParams = new LayoutParams(i, i2);
        ViewGroup.LayoutParams layoutParams2 = refreshFooter.getView().getLayoutParams();
        if (layoutParams2 instanceof LayoutParams) {
            layoutParams = (LayoutParams) layoutParams2;
        }
        if (this.mRefreshFooter.getSpinnerStyle().front) {
            super.addView(this.mRefreshFooter.getView(), getChildCount(), layoutParams);
        } else {
            super.addView(this.mRefreshFooter.getView(), 0, layoutParams);
        }
        int[] iArr = this.mPrimaryColors;
        if (iArr != null && (refreshComponent = this.mRefreshFooter) != null) {
            refreshComponent.setPrimaryColors(iArr);
        }
        return this;
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout setRefreshContent(View view) {
        return setRefreshContent(view, 0, 0);
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout setRefreshContent(View view, int i, int i2) {
        RefreshContent refreshContent = this.mRefreshContent;
        if (refreshContent != null) {
            super.removeView(refreshContent.getView());
        }
        if (i == 0) {
            i = -1;
        }
        if (i2 == 0) {
            i2 = -1;
        }
        LayoutParams layoutParams = new LayoutParams(i, i2);
        ViewGroup.LayoutParams layoutParams2 = view.getLayoutParams();
        if (layoutParams2 instanceof LayoutParams) {
            layoutParams = (LayoutParams) layoutParams2;
        }
        super.addView(view, getChildCount(), layoutParams);
        this.mRefreshContent = new RefreshContentWrapper(view);
        if (this.mAttachedToWindow) {
            View findViewById = findViewById(this.mFixedHeaderViewId);
            View findViewById2 = findViewById(this.mFixedFooterViewId);
            this.mRefreshContent.setScrollBoundaryDecider(this.mScrollBoundaryDecider);
            this.mRefreshContent.setEnableLoadMoreWhenContentNotFull(this.mEnableLoadMoreWhenContentNotFull);
            this.mRefreshContent.setUpComponent(this.mKernel, findViewById, findViewById2);
        }
        RefreshComponent refreshComponent = this.mRefreshHeader;
        if (refreshComponent != null && refreshComponent.getSpinnerStyle().front) {
            super.bringChildToFront(this.mRefreshHeader.getView());
        }
        RefreshComponent refreshComponent2 = this.mRefreshFooter;
        if (refreshComponent2 != null && refreshComponent2.getSpinnerStyle().front) {
            super.bringChildToFront(this.mRefreshFooter.getView());
        }
        return this;
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshFooter getRefreshFooter() {
        RefreshComponent refreshComponent = this.mRefreshFooter;
        if (refreshComponent instanceof RefreshFooter) {
            return (RefreshFooter) refreshComponent;
        }
        return null;
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshHeader getRefreshHeader() {
        RefreshComponent refreshComponent = this.mRefreshHeader;
        if (refreshComponent instanceof RefreshHeader) {
            return (RefreshHeader) refreshComponent;
        }
        return null;
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshState getState() {
        return this.mState;
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.mRefreshListener = onRefreshListener;
        return this;
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.mLoadMoreListener = onLoadMoreListener;
        this.mEnableLoadMore = this.mEnableLoadMore || !(this.mManualLoadMore || onLoadMoreListener == null);
        return this;
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout setOnRefreshLoadMoreListener(OnRefreshLoadMoreListener onRefreshLoadMoreListener) {
        this.mRefreshListener = onRefreshLoadMoreListener;
        this.mLoadMoreListener = onRefreshLoadMoreListener;
        this.mEnableLoadMore = this.mEnableLoadMore || !(this.mManualLoadMore || onRefreshLoadMoreListener == null);
        return this;
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout setOnMultiListener(OnMultiListener onMultiListener) {
        this.mOnMultiListener = onMultiListener;
        return this;
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout setPrimaryColors(int... iArr) {
        RefreshComponent refreshComponent = this.mRefreshHeader;
        if (refreshComponent != null) {
            refreshComponent.setPrimaryColors(iArr);
        }
        RefreshComponent refreshComponent2 = this.mRefreshFooter;
        if (refreshComponent2 != null) {
            refreshComponent2.setPrimaryColors(iArr);
        }
        this.mPrimaryColors = iArr;
        return this;
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout setPrimaryColorsId(int... iArr) {
        int[] iArr2 = new int[iArr.length];
        for (int i = 0; i < iArr.length; i++) {
            iArr2[i] = ContextCompat.getColor(getContext(), iArr[i]);
        }
        setPrimaryColors(iArr2);
        return this;
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout setScrollBoundaryDecider(ScrollBoundaryDecider scrollBoundaryDecider) {
        this.mScrollBoundaryDecider = scrollBoundaryDecider;
        RefreshContent refreshContent = this.mRefreshContent;
        if (refreshContent != null) {
            refreshContent.setScrollBoundaryDecider(scrollBoundaryDecider);
        }
        return this;
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout setNoMoreData(boolean z) {
        if (this.mState == RefreshState.Refreshing && z) {
            finishRefreshWithNoMoreData();
        } else if (this.mState == RefreshState.Loading && z) {
            finishLoadMoreWithNoMoreData();
        } else if (this.mFooterNoMoreData != z) {
            this.mFooterNoMoreData = z;
            RefreshComponent refreshComponent = this.mRefreshFooter;
            if (refreshComponent instanceof RefreshFooter) {
                if (((RefreshFooter) refreshComponent).setNoMoreData(z)) {
                    this.mFooterNoMoreDataEffective = true;
                    if (this.mFooterNoMoreData && this.mEnableFooterFollowWhenNoMoreData && this.mSpinner > 0 && this.mRefreshFooter.getSpinnerStyle() == SpinnerStyle.Translate && isEnableRefreshOrLoadMore(this.mEnableLoadMore) && isEnableTranslationContent(this.mEnableRefresh, this.mRefreshHeader)) {
                        this.mRefreshFooter.getView().setTranslationY(this.mSpinner);
                    }
                } else {
                    this.mFooterNoMoreDataEffective = false;
                    new RuntimeException("Footer:" + this.mRefreshFooter + " NoMoreData is not supported.(不支持NoMoreData，请使用[ClassicsFooter]或者[自定义Footer并实现setNoMoreData方法且返回true])").printStackTrace();
                }
            }
        }
        return this;
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout resetNoMoreData() {
        return setNoMoreData(false);
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout finishRefresh() {
        return finishRefresh(true);
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout finishLoadMore() {
        return finishLoadMore(true);
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout finishRefresh(int i) {
        return finishRefresh(i, true, Boolean.FALSE);
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout finishRefresh(boolean z) {
        if (z) {
            return finishRefresh(Math.min(Math.max(0, 300 - ((int) (System.currentTimeMillis() - this.mLastOpenTime))), 300) << 16, true, Boolean.FALSE);
        }
        return finishRefresh(0, false, null);
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout finishRefresh(int i, final boolean z, final Boolean bool) {
        final int i2 = i >> 16;
        int i3 = (i << 16) >> 16;
        Runnable runnable = new Runnable() { // from class: com.xiaopeng.xui.widget.XSmartRefreshLayout.6
            int count = 0;

            @Override // java.lang.Runnable
            public void run() {
                if (this.count == 0) {
                    if (XSmartRefreshLayout.this.mState == RefreshState.None && XSmartRefreshLayout.this.mViceState == RefreshState.Refreshing) {
                        XSmartRefreshLayout.this.mViceState = RefreshState.None;
                    } else if (XSmartRefreshLayout.this.reboundAnimator != null && XSmartRefreshLayout.this.mState.isHeader && (XSmartRefreshLayout.this.mState.isDragging || XSmartRefreshLayout.this.mState == RefreshState.RefreshReleased)) {
                        XSmartRefreshLayout.this.reboundAnimator.setDuration(0L);
                        XSmartRefreshLayout.this.reboundAnimator.cancel();
                        XSmartRefreshLayout.this.reboundAnimator = null;
                        if (XSmartRefreshLayout.this.mKernel.animSpinner(0) == null) {
                            XSmartRefreshLayout.this.notifyStateChanged(RefreshState.None);
                        } else {
                            XSmartRefreshLayout.this.notifyStateChanged(RefreshState.PullDownCanceled);
                        }
                    } else if (XSmartRefreshLayout.this.mState == RefreshState.Refreshing && XSmartRefreshLayout.this.mRefreshHeader != null && XSmartRefreshLayout.this.mRefreshContent != null) {
                        this.count++;
                        XSmartRefreshLayout.this.mHandler.postDelayed(this, i2);
                        XSmartRefreshLayout.this.notifyStateChanged(RefreshState.RefreshFinish);
                        if (bool == Boolean.FALSE) {
                            XSmartRefreshLayout.this.setNoMoreData(false);
                        }
                    }
                    if (bool == Boolean.TRUE) {
                        XSmartRefreshLayout.this.setNoMoreData(true);
                        return;
                    }
                    return;
                }
                int onFinish = XSmartRefreshLayout.this.mRefreshHeader.onFinish(XSmartRefreshLayout.this, z);
                if (XSmartRefreshLayout.this.mOnMultiListener != null && (XSmartRefreshLayout.this.mRefreshHeader instanceof RefreshHeader)) {
                    XSmartRefreshLayout.this.mOnMultiListener.onHeaderFinish((RefreshHeader) XSmartRefreshLayout.this.mRefreshHeader, z);
                }
                if (onFinish < Integer.MAX_VALUE) {
                    if (XSmartRefreshLayout.this.mIsBeingDragged || XSmartRefreshLayout.this.mNestedInProgress) {
                        long currentTimeMillis = System.currentTimeMillis();
                        if (XSmartRefreshLayout.this.mIsBeingDragged) {
                            XSmartRefreshLayout xSmartRefreshLayout = XSmartRefreshLayout.this;
                            xSmartRefreshLayout.mTouchY = xSmartRefreshLayout.mLastTouchY;
                            XSmartRefreshLayout.this.mTouchSpinner = 0;
                            XSmartRefreshLayout.this.mIsBeingDragged = false;
                            XSmartRefreshLayout xSmartRefreshLayout2 = XSmartRefreshLayout.this;
                            XSmartRefreshLayout.super.dispatchTouchEvent(MotionEvent.obtain(currentTimeMillis, currentTimeMillis, 0, xSmartRefreshLayout2.mLastTouchX, (XSmartRefreshLayout.this.mLastTouchY + XSmartRefreshLayout.this.mSpinner) - (XSmartRefreshLayout.this.mTouchSlop * 2), 0));
                            XSmartRefreshLayout xSmartRefreshLayout3 = XSmartRefreshLayout.this;
                            XSmartRefreshLayout.super.dispatchTouchEvent(MotionEvent.obtain(currentTimeMillis, currentTimeMillis, 2, xSmartRefreshLayout3.mLastTouchX, XSmartRefreshLayout.this.mLastTouchY + XSmartRefreshLayout.this.mSpinner, 0));
                        }
                        if (XSmartRefreshLayout.this.mNestedInProgress) {
                            XSmartRefreshLayout.this.mTotalUnconsumed = 0;
                            XSmartRefreshLayout xSmartRefreshLayout4 = XSmartRefreshLayout.this;
                            XSmartRefreshLayout.super.dispatchTouchEvent(MotionEvent.obtain(currentTimeMillis, currentTimeMillis, 1, xSmartRefreshLayout4.mLastTouchX, XSmartRefreshLayout.this.mLastTouchY, 0));
                            XSmartRefreshLayout.this.mNestedInProgress = false;
                            XSmartRefreshLayout.this.mTouchSpinner = 0;
                        }
                    }
                    if (XSmartRefreshLayout.this.mSpinner > 0) {
                        XSmartRefreshLayout xSmartRefreshLayout5 = XSmartRefreshLayout.this;
                        ValueAnimator animSpinner = xSmartRefreshLayout5.animSpinner(0, onFinish, xSmartRefreshLayout5.mReboundInterpolator, XSmartRefreshLayout.this.mReboundDuration);
                        ValueAnimator.AnimatorUpdateListener scrollContentWhenFinished = XSmartRefreshLayout.this.mEnableScrollContentWhenRefreshed ? XSmartRefreshLayout.this.mRefreshContent.scrollContentWhenFinished(XSmartRefreshLayout.this.mSpinner) : null;
                        if (animSpinner == null || scrollContentWhenFinished == null) {
                            return;
                        }
                        animSpinner.addUpdateListener(scrollContentWhenFinished);
                    } else if (XSmartRefreshLayout.this.mSpinner < 0) {
                        XSmartRefreshLayout xSmartRefreshLayout6 = XSmartRefreshLayout.this;
                        xSmartRefreshLayout6.animSpinner(0, onFinish, xSmartRefreshLayout6.mReboundInterpolator, XSmartRefreshLayout.this.mReboundDuration);
                    } else {
                        XSmartRefreshLayout.this.mKernel.moveSpinner(0, false);
                        XSmartRefreshLayout.this.mKernel.setState(RefreshState.None);
                    }
                }
            }
        };
        if (i3 > 0) {
            this.mHandler.postDelayed(runnable, i3);
        } else {
            runnable.run();
        }
        return this;
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout finishRefreshWithNoMoreData() {
        return finishRefresh(Math.min(Math.max(0, 300 - ((int) (System.currentTimeMillis() - this.mLastOpenTime))), 300) << 16, true, Boolean.TRUE);
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout finishLoadMore(int i) {
        return finishLoadMore(i, true, false);
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout finishLoadMore(boolean z) {
        return finishLoadMore(z ? Math.min(Math.max(0, 300 - ((int) (System.currentTimeMillis() - this.mLastOpenTime))), 300) << 16 : 0, z, false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.xiaopeng.xui.widget.XSmartRefreshLayout$7  reason: invalid class name */
    /* loaded from: classes2.dex */
    public class AnonymousClass7 implements Runnable {
        int count = 0;
        final /* synthetic */ int val$more;
        final /* synthetic */ boolean val$noMoreData;
        final /* synthetic */ boolean val$success;

        AnonymousClass7(int i, boolean z, boolean z2) {
            this.val$more = i;
            this.val$noMoreData = z;
            this.val$success = z2;
        }

        @Override // java.lang.Runnable
        public void run() {
            boolean z = true;
            if (this.count == 0) {
                if (XSmartRefreshLayout.this.mState == RefreshState.None && XSmartRefreshLayout.this.mViceState == RefreshState.Loading) {
                    XSmartRefreshLayout.this.mViceState = RefreshState.None;
                } else if (XSmartRefreshLayout.this.reboundAnimator != null && ((XSmartRefreshLayout.this.mState.isDragging || XSmartRefreshLayout.this.mState == RefreshState.LoadReleased) && XSmartRefreshLayout.this.mState.isFooter)) {
                    XSmartRefreshLayout.this.reboundAnimator.setDuration(0L);
                    XSmartRefreshLayout.this.reboundAnimator.cancel();
                    XSmartRefreshLayout.this.reboundAnimator = null;
                    if (XSmartRefreshLayout.this.mKernel.animSpinner(0) == null) {
                        XSmartRefreshLayout.this.notifyStateChanged(RefreshState.None);
                    } else {
                        XSmartRefreshLayout.this.notifyStateChanged(RefreshState.PullUpCanceled);
                    }
                } else if (XSmartRefreshLayout.this.mState == RefreshState.Loading && XSmartRefreshLayout.this.mRefreshFooter != null && XSmartRefreshLayout.this.mRefreshContent != null) {
                    this.count++;
                    XSmartRefreshLayout.this.mHandler.postDelayed(this, this.val$more);
                    XSmartRefreshLayout.this.notifyStateChanged(RefreshState.LoadFinish);
                    return;
                }
                if (this.val$noMoreData) {
                    XSmartRefreshLayout.this.setNoMoreData(true);
                    return;
                }
                return;
            }
            int onFinish = XSmartRefreshLayout.this.mRefreshFooter.onFinish(XSmartRefreshLayout.this, this.val$success);
            if (XSmartRefreshLayout.this.mOnMultiListener != null && (XSmartRefreshLayout.this.mRefreshFooter instanceof RefreshFooter)) {
                XSmartRefreshLayout.this.mOnMultiListener.onFooterFinish((RefreshFooter) XSmartRefreshLayout.this.mRefreshFooter, this.val$success);
            }
            if (onFinish < Integer.MAX_VALUE) {
                if (!this.val$noMoreData || !XSmartRefreshLayout.this.mEnableFooterFollowWhenNoMoreData || XSmartRefreshLayout.this.mSpinner >= 0 || !XSmartRefreshLayout.this.mRefreshContent.canLoadMore()) {
                    z = false;
                }
                final int max = XSmartRefreshLayout.this.mSpinner - (z ? Math.max(XSmartRefreshLayout.this.mSpinner, -XSmartRefreshLayout.this.mFooterHeight) : 0);
                if (XSmartRefreshLayout.this.mIsBeingDragged || XSmartRefreshLayout.this.mNestedInProgress) {
                    long currentTimeMillis = System.currentTimeMillis();
                    if (XSmartRefreshLayout.this.mIsBeingDragged) {
                        XSmartRefreshLayout xSmartRefreshLayout = XSmartRefreshLayout.this;
                        xSmartRefreshLayout.mTouchY = xSmartRefreshLayout.mLastTouchY;
                        XSmartRefreshLayout xSmartRefreshLayout2 = XSmartRefreshLayout.this;
                        xSmartRefreshLayout2.mTouchSpinner = xSmartRefreshLayout2.mSpinner - max;
                        XSmartRefreshLayout.this.mIsBeingDragged = false;
                        int i = XSmartRefreshLayout.this.mEnableFooterTranslationContent ? max : 0;
                        XSmartRefreshLayout xSmartRefreshLayout3 = XSmartRefreshLayout.this;
                        float f = i;
                        XSmartRefreshLayout.super.dispatchTouchEvent(MotionEvent.obtain(currentTimeMillis, currentTimeMillis, 0, xSmartRefreshLayout3.mLastTouchX, XSmartRefreshLayout.this.mLastTouchY + f + (XSmartRefreshLayout.this.mTouchSlop * 2), 0));
                        XSmartRefreshLayout xSmartRefreshLayout4 = XSmartRefreshLayout.this;
                        XSmartRefreshLayout.super.dispatchTouchEvent(MotionEvent.obtain(currentTimeMillis, currentTimeMillis, 2, xSmartRefreshLayout4.mLastTouchX, XSmartRefreshLayout.this.mLastTouchY + f, 0));
                    }
                    if (XSmartRefreshLayout.this.mNestedInProgress) {
                        XSmartRefreshLayout.this.mTotalUnconsumed = 0;
                        XSmartRefreshLayout xSmartRefreshLayout5 = XSmartRefreshLayout.this;
                        XSmartRefreshLayout.super.dispatchTouchEvent(MotionEvent.obtain(currentTimeMillis, currentTimeMillis, 1, xSmartRefreshLayout5.mLastTouchX, XSmartRefreshLayout.this.mLastTouchY, 0));
                        XSmartRefreshLayout.this.mNestedInProgress = false;
                        XSmartRefreshLayout.this.mTouchSpinner = 0;
                    }
                }
                XSmartRefreshLayout.this.mHandler.postDelayed(new Runnable() { // from class: com.xiaopeng.xui.widget.XSmartRefreshLayout.7.1
                    @Override // java.lang.Runnable
                    public void run() {
                        ValueAnimator.AnimatorUpdateListener animatorUpdateListener;
                        ValueAnimator valueAnimator;
                        if (!XSmartRefreshLayout.this.mEnableScrollContentWhenLoaded || max >= 0) {
                            animatorUpdateListener = null;
                        } else {
                            animatorUpdateListener = XSmartRefreshLayout.this.mRefreshContent.scrollContentWhenFinished(XSmartRefreshLayout.this.mSpinner);
                            if (animatorUpdateListener != null) {
                                animatorUpdateListener.onAnimationUpdate(ValueAnimator.ofInt(0, 0));
                            }
                        }
                        AnimatorListenerAdapter animatorListenerAdapter = new AnimatorListenerAdapter() { // from class: com.xiaopeng.xui.widget.XSmartRefreshLayout.7.1.1
                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationEnd(Animator animator) {
                                if (animator == null || animator.getDuration() != 0) {
                                    XSmartRefreshLayout.this.mFooterLocked = false;
                                    if (AnonymousClass7.this.val$noMoreData) {
                                        XSmartRefreshLayout.this.setNoMoreData(true);
                                    }
                                    if (XSmartRefreshLayout.this.mState == RefreshState.LoadFinish) {
                                        XSmartRefreshLayout.this.notifyStateChanged(RefreshState.None);
                                    }
                                }
                            }
                        };
                        if (XSmartRefreshLayout.this.mSpinner > 0) {
                            valueAnimator = XSmartRefreshLayout.this.mKernel.animSpinner(0);
                        } else {
                            if (animatorUpdateListener != null || XSmartRefreshLayout.this.mSpinner == 0) {
                                if (XSmartRefreshLayout.this.reboundAnimator != null) {
                                    XSmartRefreshLayout.this.reboundAnimator.setDuration(0L);
                                    XSmartRefreshLayout.this.reboundAnimator.cancel();
                                    XSmartRefreshLayout.this.reboundAnimator = null;
                                }
                                XSmartRefreshLayout.this.mKernel.moveSpinner(0, false);
                                XSmartRefreshLayout.this.mKernel.setState(RefreshState.None);
                            } else if (AnonymousClass7.this.val$noMoreData && XSmartRefreshLayout.this.mEnableFooterFollowWhenNoMoreData) {
                                if (XSmartRefreshLayout.this.mSpinner >= (-XSmartRefreshLayout.this.mFooterHeight)) {
                                    XSmartRefreshLayout.this.notifyStateChanged(RefreshState.None);
                                } else {
                                    valueAnimator = XSmartRefreshLayout.this.mKernel.animSpinner(-XSmartRefreshLayout.this.mFooterHeight);
                                }
                            } else {
                                valueAnimator = XSmartRefreshLayout.this.mKernel.animSpinner(0);
                            }
                            valueAnimator = null;
                        }
                        if (valueAnimator != null) {
                            valueAnimator.addListener(animatorListenerAdapter);
                        } else {
                            animatorListenerAdapter.onAnimationEnd(null);
                        }
                    }
                }, XSmartRefreshLayout.this.mSpinner < 0 ? onFinish : 0L);
            }
        }
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout finishLoadMore(int i, boolean z, boolean z2) {
        int i2 = i >> 16;
        int i3 = (i << 16) >> 16;
        AnonymousClass7 anonymousClass7 = new AnonymousClass7(i2, z2, z);
        if (i3 > 0) {
            this.mHandler.postDelayed(anonymousClass7, i3);
        } else {
            anonymousClass7.run();
        }
        return this;
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout finishLoadMoreWithNoMoreData() {
        return finishLoadMore(Math.min(Math.max(0, 300 - ((int) (System.currentTimeMillis() - this.mLastOpenTime))), 300) << 16, true, true);
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public RefreshLayout closeHeaderOrFooter() {
        if (this.mState == RefreshState.None && (this.mViceState == RefreshState.Refreshing || this.mViceState == RefreshState.Loading)) {
            this.mViceState = RefreshState.None;
        }
        if (this.mState == RefreshState.Refreshing) {
            finishRefresh();
        } else if (this.mState == RefreshState.Loading) {
            finishLoadMore();
        } else if (this.mKernel.animSpinner(0) == null) {
            notifyStateChanged(RefreshState.None);
        } else if (this.mState.isHeader) {
            notifyStateChanged(RefreshState.PullDownCanceled);
        } else {
            notifyStateChanged(RefreshState.PullUpCanceled);
        }
        return this;
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public boolean autoRefresh() {
        return autoRefresh(this.mAttachedToWindow ? 0 : 400, this.mReboundDuration, (this.mHeaderMaxDragRate + this.mHeaderTriggerRate) / 2.0f, false);
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public boolean autoRefresh(int i) {
        return autoRefresh(i, this.mReboundDuration, (this.mHeaderMaxDragRate + this.mHeaderTriggerRate) / 2.0f, false);
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public boolean autoRefreshAnimationOnly() {
        return autoRefresh(this.mAttachedToWindow ? 0 : 400, this.mReboundDuration, (this.mHeaderMaxDragRate + this.mHeaderTriggerRate) / 2.0f, true);
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public boolean autoRefresh(int i, final int i2, final float f, final boolean z) {
        if (this.mState == RefreshState.None && isEnableRefreshOrLoadMore(this.mEnableRefresh)) {
            Runnable runnable = new Runnable() { // from class: com.xiaopeng.xui.widget.XSmartRefreshLayout.8
                @Override // java.lang.Runnable
                public void run() {
                    if (XSmartRefreshLayout.this.mViceState != RefreshState.Refreshing) {
                        return;
                    }
                    if (XSmartRefreshLayout.this.reboundAnimator != null) {
                        XSmartRefreshLayout.this.reboundAnimator.setDuration(0L);
                        XSmartRefreshLayout.this.reboundAnimator.cancel();
                        XSmartRefreshLayout.this.reboundAnimator = null;
                    }
                    XSmartRefreshLayout xSmartRefreshLayout = XSmartRefreshLayout.this;
                    xSmartRefreshLayout.mLastTouchX = xSmartRefreshLayout.getMeasuredWidth() / 2.0f;
                    XSmartRefreshLayout.this.mKernel.setState(RefreshState.PullDownToRefresh);
                    if (XSmartRefreshLayout.this.mRefreshHeader == null || !XSmartRefreshLayout.this.mRefreshHeader.autoOpen(i2, f, z)) {
                        float f2 = XSmartRefreshLayout.this.mHeaderHeight == 0 ? XSmartRefreshLayout.this.mHeaderTriggerRate : XSmartRefreshLayout.this.mHeaderHeight;
                        float f3 = f;
                        if (f3 < 10.0f) {
                            f3 *= f2;
                        }
                        XSmartRefreshLayout xSmartRefreshLayout2 = XSmartRefreshLayout.this;
                        xSmartRefreshLayout2.reboundAnimator = ValueAnimator.ofInt(xSmartRefreshLayout2.mSpinner, (int) f3);
                        XSmartRefreshLayout.this.reboundAnimator.setDuration(i2);
                        XSmartRefreshLayout.this.reboundAnimator.setInterpolator(new SmartUtil(SmartUtil.INTERPOLATOR_VISCOUS_FLUID));
                        XSmartRefreshLayout.this.reboundAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.xiaopeng.xui.widget.XSmartRefreshLayout.8.1
                            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                if (XSmartRefreshLayout.this.reboundAnimator == null || XSmartRefreshLayout.this.mRefreshHeader == null) {
                                    return;
                                }
                                XSmartRefreshLayout.this.mKernel.moveSpinner(((Integer) valueAnimator.getAnimatedValue()).intValue(), true);
                            }
                        });
                        XSmartRefreshLayout.this.reboundAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.xiaopeng.xui.widget.XSmartRefreshLayout.8.2
                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationEnd(Animator animator) {
                                XSmartRefreshLayout.this.mKernel.onAutoRefreshAnimationEnd(animator, z);
                            }
                        });
                        XSmartRefreshLayout.this.reboundAnimator.start();
                    }
                }
            };
            setViceState(RefreshState.Refreshing);
            if (i > 0) {
                this.mHandler.postDelayed(runnable, i);
                return true;
            }
            runnable.run();
            return true;
        }
        return false;
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public boolean autoLoadMore() {
        return autoLoadMore(0, this.mReboundDuration, (this.mFooterMaxDragRate + this.mFooterTriggerRate) / 2.0f, false);
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public boolean autoLoadMore(int i) {
        return autoLoadMore(i, this.mReboundDuration, (this.mFooterMaxDragRate + this.mFooterTriggerRate) / 2.0f, false);
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public boolean autoLoadMoreAnimationOnly() {
        return autoLoadMore(0, this.mReboundDuration, (this.mFooterMaxDragRate + this.mFooterTriggerRate) / 2.0f, true);
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public boolean autoLoadMore(int i, final int i2, final float f, final boolean z) {
        if (this.mState == RefreshState.None && isEnableRefreshOrLoadMore(this.mEnableLoadMore) && !this.mFooterNoMoreData) {
            Runnable runnable = new Runnable() { // from class: com.xiaopeng.xui.widget.XSmartRefreshLayout.9
                @Override // java.lang.Runnable
                public void run() {
                    if (XSmartRefreshLayout.this.mViceState != RefreshState.Loading) {
                        return;
                    }
                    if (XSmartRefreshLayout.this.reboundAnimator != null) {
                        XSmartRefreshLayout.this.reboundAnimator.setDuration(0L);
                        XSmartRefreshLayout.this.reboundAnimator.cancel();
                        XSmartRefreshLayout.this.reboundAnimator = null;
                    }
                    XSmartRefreshLayout xSmartRefreshLayout = XSmartRefreshLayout.this;
                    xSmartRefreshLayout.mLastTouchX = xSmartRefreshLayout.getMeasuredWidth() / 2.0f;
                    XSmartRefreshLayout.this.mKernel.setState(RefreshState.PullUpToLoad);
                    if (XSmartRefreshLayout.this.mRefreshFooter == null || !XSmartRefreshLayout.this.mRefreshFooter.autoOpen(i2, f, z)) {
                        float f2 = XSmartRefreshLayout.this.mFooterHeight == 0 ? XSmartRefreshLayout.this.mFooterTriggerRate : XSmartRefreshLayout.this.mFooterHeight;
                        float f3 = f;
                        if (f3 < 10.0f) {
                            f3 *= f2;
                        }
                        XSmartRefreshLayout xSmartRefreshLayout2 = XSmartRefreshLayout.this;
                        xSmartRefreshLayout2.reboundAnimator = ValueAnimator.ofInt(xSmartRefreshLayout2.mSpinner, -((int) f3));
                        XSmartRefreshLayout.this.reboundAnimator.setDuration(i2);
                        XSmartRefreshLayout.this.reboundAnimator.setInterpolator(new SmartUtil(SmartUtil.INTERPOLATOR_VISCOUS_FLUID));
                        XSmartRefreshLayout.this.reboundAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.xiaopeng.xui.widget.XSmartRefreshLayout.9.1
                            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                if (XSmartRefreshLayout.this.reboundAnimator == null || XSmartRefreshLayout.this.mRefreshFooter == null) {
                                    return;
                                }
                                XSmartRefreshLayout.this.mKernel.moveSpinner(((Integer) valueAnimator.getAnimatedValue()).intValue(), true);
                            }
                        });
                        XSmartRefreshLayout.this.reboundAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.xiaopeng.xui.widget.XSmartRefreshLayout.9.2
                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationEnd(Animator animator) {
                                XSmartRefreshLayout.this.mKernel.onAutoLoadMoreAnimationEnd(animator, z);
                            }
                        });
                        XSmartRefreshLayout.this.reboundAnimator.start();
                    }
                }
            };
            setViceState(RefreshState.Loading);
            if (i > 0) {
                this.mHandler.postDelayed(runnable, i);
                return true;
            }
            runnable.run();
            return true;
        }
        return false;
    }

    public static void setDefaultRefreshHeaderCreator(DefaultRefreshHeaderCreator defaultRefreshHeaderCreator) {
        sHeaderCreator = defaultRefreshHeaderCreator;
    }

    public static void setDefaultRefreshFooterCreator(DefaultRefreshFooterCreator defaultRefreshFooterCreator) {
        sFooterCreator = defaultRefreshFooterCreator;
    }

    public static void setDefaultRefreshInitializer(DefaultRefreshInitializer defaultRefreshInitializer) {
        sRefreshInitializer = defaultRefreshInitializer;
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public boolean isRefreshing() {
        return this.mState == RefreshState.Refreshing;
    }

    @Override // com.scwang.smart.refresh.layout.api.RefreshLayout
    public boolean isLoading() {
        return this.mState == RefreshState.Loading;
    }

    /* loaded from: classes2.dex */
    public class RefreshKernelImpl implements RefreshKernel {
        public RefreshKernelImpl() {
        }

        @Override // com.scwang.smart.refresh.layout.api.RefreshKernel
        public RefreshLayout getRefreshLayout() {
            return XSmartRefreshLayout.this;
        }

        @Override // com.scwang.smart.refresh.layout.api.RefreshKernel
        public RefreshContent getRefreshContent() {
            return XSmartRefreshLayout.this.mRefreshContent;
        }

        @Override // com.scwang.smart.refresh.layout.api.RefreshKernel
        public RefreshKernel setState(RefreshState refreshState) {
            switch (AnonymousClass10.$SwitchMap$com$scwang$smart$refresh$layout$constant$RefreshState[refreshState.ordinal()]) {
                case 1:
                    if (XSmartRefreshLayout.this.mState != RefreshState.None && XSmartRefreshLayout.this.mSpinner == 0) {
                        XSmartRefreshLayout.this.notifyStateChanged(RefreshState.None);
                        return null;
                    } else if (XSmartRefreshLayout.this.mSpinner != 0) {
                        animSpinner(0);
                        return null;
                    } else {
                        return null;
                    }
                case 2:
                    if (!XSmartRefreshLayout.this.mState.isOpening) {
                        XSmartRefreshLayout xSmartRefreshLayout = XSmartRefreshLayout.this;
                        if (xSmartRefreshLayout.isEnableRefreshOrLoadMore(xSmartRefreshLayout.mEnableRefresh)) {
                            XSmartRefreshLayout.this.notifyStateChanged(RefreshState.PullDownToRefresh);
                            return null;
                        }
                    }
                    XSmartRefreshLayout.this.setViceState(RefreshState.PullDownToRefresh);
                    return null;
                case 3:
                    XSmartRefreshLayout xSmartRefreshLayout2 = XSmartRefreshLayout.this;
                    if (xSmartRefreshLayout2.isEnableRefreshOrLoadMore(xSmartRefreshLayout2.mEnableLoadMore) && !XSmartRefreshLayout.this.mState.isOpening && !XSmartRefreshLayout.this.mState.isFinishing && (!XSmartRefreshLayout.this.mFooterNoMoreData || !XSmartRefreshLayout.this.mEnableFooterFollowWhenNoMoreData || !XSmartRefreshLayout.this.mFooterNoMoreDataEffective)) {
                        XSmartRefreshLayout.this.notifyStateChanged(RefreshState.PullUpToLoad);
                        return null;
                    }
                    XSmartRefreshLayout.this.setViceState(RefreshState.PullUpToLoad);
                    return null;
                case 4:
                    if (!XSmartRefreshLayout.this.mState.isOpening) {
                        XSmartRefreshLayout xSmartRefreshLayout3 = XSmartRefreshLayout.this;
                        if (xSmartRefreshLayout3.isEnableRefreshOrLoadMore(xSmartRefreshLayout3.mEnableRefresh)) {
                            XSmartRefreshLayout.this.notifyStateChanged(RefreshState.PullDownCanceled);
                            setState(RefreshState.None);
                            return null;
                        }
                    }
                    XSmartRefreshLayout.this.setViceState(RefreshState.PullDownCanceled);
                    return null;
                case 5:
                    XSmartRefreshLayout xSmartRefreshLayout4 = XSmartRefreshLayout.this;
                    if (xSmartRefreshLayout4.isEnableRefreshOrLoadMore(xSmartRefreshLayout4.mEnableLoadMore) && !XSmartRefreshLayout.this.mState.isOpening && (!XSmartRefreshLayout.this.mFooterNoMoreData || !XSmartRefreshLayout.this.mEnableFooterFollowWhenNoMoreData || !XSmartRefreshLayout.this.mFooterNoMoreDataEffective)) {
                        XSmartRefreshLayout.this.notifyStateChanged(RefreshState.PullUpCanceled);
                        setState(RefreshState.None);
                        return null;
                    }
                    XSmartRefreshLayout.this.setViceState(RefreshState.PullUpCanceled);
                    return null;
                case 6:
                    if (!XSmartRefreshLayout.this.mState.isOpening) {
                        XSmartRefreshLayout xSmartRefreshLayout5 = XSmartRefreshLayout.this;
                        if (xSmartRefreshLayout5.isEnableRefreshOrLoadMore(xSmartRefreshLayout5.mEnableRefresh)) {
                            XSmartRefreshLayout.this.notifyStateChanged(RefreshState.ReleaseToRefresh);
                            return null;
                        }
                    }
                    XSmartRefreshLayout.this.setViceState(RefreshState.ReleaseToRefresh);
                    return null;
                case 7:
                    XSmartRefreshLayout xSmartRefreshLayout6 = XSmartRefreshLayout.this;
                    if (xSmartRefreshLayout6.isEnableRefreshOrLoadMore(xSmartRefreshLayout6.mEnableLoadMore) && !XSmartRefreshLayout.this.mState.isOpening && !XSmartRefreshLayout.this.mState.isFinishing && (!XSmartRefreshLayout.this.mFooterNoMoreData || !XSmartRefreshLayout.this.mEnableFooterFollowWhenNoMoreData || !XSmartRefreshLayout.this.mFooterNoMoreDataEffective)) {
                        XSmartRefreshLayout.this.notifyStateChanged(RefreshState.ReleaseToLoad);
                        return null;
                    }
                    XSmartRefreshLayout.this.setViceState(RefreshState.ReleaseToLoad);
                    return null;
                case 8:
                    if (!XSmartRefreshLayout.this.mState.isOpening) {
                        XSmartRefreshLayout xSmartRefreshLayout7 = XSmartRefreshLayout.this;
                        if (xSmartRefreshLayout7.isEnableRefreshOrLoadMore(xSmartRefreshLayout7.mEnableRefresh)) {
                            XSmartRefreshLayout.this.notifyStateChanged(RefreshState.ReleaseToTwoLevel);
                            return null;
                        }
                    }
                    XSmartRefreshLayout.this.setViceState(RefreshState.ReleaseToTwoLevel);
                    return null;
                case 9:
                    if (!XSmartRefreshLayout.this.mState.isOpening) {
                        XSmartRefreshLayout xSmartRefreshLayout8 = XSmartRefreshLayout.this;
                        if (xSmartRefreshLayout8.isEnableRefreshOrLoadMore(xSmartRefreshLayout8.mEnableRefresh)) {
                            XSmartRefreshLayout.this.notifyStateChanged(RefreshState.RefreshReleased);
                            return null;
                        }
                    }
                    XSmartRefreshLayout.this.setViceState(RefreshState.RefreshReleased);
                    return null;
                case 10:
                    if (!XSmartRefreshLayout.this.mState.isOpening) {
                        XSmartRefreshLayout xSmartRefreshLayout9 = XSmartRefreshLayout.this;
                        if (xSmartRefreshLayout9.isEnableRefreshOrLoadMore(xSmartRefreshLayout9.mEnableLoadMore)) {
                            XSmartRefreshLayout.this.notifyStateChanged(RefreshState.LoadReleased);
                            return null;
                        }
                    }
                    XSmartRefreshLayout.this.setViceState(RefreshState.LoadReleased);
                    return null;
                case 11:
                    XSmartRefreshLayout.this.setStateRefreshing(true);
                    return null;
                case 12:
                    XSmartRefreshLayout.this.setStateLoading(true);
                    return null;
                default:
                    XSmartRefreshLayout.this.notifyStateChanged(refreshState);
                    return null;
            }
        }

        @Override // com.scwang.smart.refresh.layout.api.RefreshKernel
        public RefreshKernel startTwoLevel(boolean z) {
            if (z) {
                AnimatorListenerAdapter animatorListenerAdapter = new AnimatorListenerAdapter() { // from class: com.xiaopeng.xui.widget.XSmartRefreshLayout.RefreshKernelImpl.1
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator) {
                        if (animator == null || animator.getDuration() != 0) {
                            XSmartRefreshLayout.this.mKernel.setState(RefreshState.TwoLevel);
                        }
                    }
                };
                ValueAnimator animSpinner = animSpinner(XSmartRefreshLayout.this.getMeasuredHeight());
                if (animSpinner != null && animSpinner == XSmartRefreshLayout.this.reboundAnimator) {
                    animSpinner.setDuration(XSmartRefreshLayout.this.mFloorDuration);
                    animSpinner.addListener(animatorListenerAdapter);
                } else {
                    animatorListenerAdapter.onAnimationEnd(null);
                }
            } else if (animSpinner(0) == null) {
                XSmartRefreshLayout.this.notifyStateChanged(RefreshState.None);
            }
            return this;
        }

        @Override // com.scwang.smart.refresh.layout.api.RefreshKernel
        public RefreshKernel finishTwoLevel() {
            if (XSmartRefreshLayout.this.mState == RefreshState.TwoLevel) {
                XSmartRefreshLayout.this.mKernel.setState(RefreshState.TwoLevelFinish);
                if (XSmartRefreshLayout.this.mSpinner == 0) {
                    moveSpinner(0, false);
                    XSmartRefreshLayout.this.notifyStateChanged(RefreshState.None);
                } else {
                    animSpinner(0).setDuration(XSmartRefreshLayout.this.mFloorDuration);
                }
            }
            return this;
        }

        /* JADX WARN: Removed duplicated region for block: B:56:0x00f1  */
        /* JADX WARN: Removed duplicated region for block: B:63:0x0108  */
        @Override // com.scwang.smart.refresh.layout.api.RefreshKernel
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public com.scwang.smart.refresh.layout.api.RefreshKernel moveSpinner(int r19, boolean r20) {
            /*
                Method dump skipped, instructions count: 1121
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: com.xiaopeng.xui.widget.XSmartRefreshLayout.RefreshKernelImpl.moveSpinner(int, boolean):com.scwang.smart.refresh.layout.api.RefreshKernel");
        }

        @Override // com.scwang.smart.refresh.layout.api.RefreshKernel
        public ValueAnimator animSpinner(int i) {
            XSmartRefreshLayout xSmartRefreshLayout = XSmartRefreshLayout.this;
            return xSmartRefreshLayout.animSpinner(i, 0, xSmartRefreshLayout.mReboundInterpolator, XSmartRefreshLayout.this.mReboundDuration);
        }

        @Override // com.scwang.smart.refresh.layout.api.RefreshKernel
        public RefreshKernel requestDrawBackgroundFor(RefreshComponent refreshComponent, int i) {
            if (XSmartRefreshLayout.this.mPaint == null && i != 0) {
                XSmartRefreshLayout.this.mPaint = new Paint();
            }
            if (refreshComponent.equals(XSmartRefreshLayout.this.mRefreshHeader)) {
                XSmartRefreshLayout.this.mHeaderBackgroundColor = i;
            } else if (refreshComponent.equals(XSmartRefreshLayout.this.mRefreshFooter)) {
                XSmartRefreshLayout.this.mFooterBackgroundColor = i;
            }
            return this;
        }

        @Override // com.scwang.smart.refresh.layout.api.RefreshKernel
        public RefreshKernel requestNeedTouchEventFor(RefreshComponent refreshComponent, boolean z) {
            if (refreshComponent.equals(XSmartRefreshLayout.this.mRefreshHeader)) {
                XSmartRefreshLayout.this.mHeaderNeedTouchEventWhenRefreshing = z;
            } else if (refreshComponent.equals(XSmartRefreshLayout.this.mRefreshFooter)) {
                XSmartRefreshLayout.this.mFooterNeedTouchEventWhenLoading = z;
            }
            return this;
        }

        @Override // com.scwang.smart.refresh.layout.api.RefreshKernel
        public RefreshKernel requestDefaultTranslationContentFor(RefreshComponent refreshComponent, boolean z) {
            if (refreshComponent.equals(XSmartRefreshLayout.this.mRefreshHeader)) {
                if (!XSmartRefreshLayout.this.mManualHeaderTranslationContent) {
                    XSmartRefreshLayout.this.mManualHeaderTranslationContent = true;
                    XSmartRefreshLayout.this.mEnableHeaderTranslationContent = z;
                }
            } else if (refreshComponent.equals(XSmartRefreshLayout.this.mRefreshFooter) && !XSmartRefreshLayout.this.mManualFooterTranslationContent) {
                XSmartRefreshLayout.this.mManualFooterTranslationContent = true;
                XSmartRefreshLayout.this.mEnableFooterTranslationContent = z;
            }
            return this;
        }

        @Override // com.scwang.smart.refresh.layout.api.RefreshKernel
        public RefreshKernel requestRemeasureHeightFor(RefreshComponent refreshComponent) {
            if (refreshComponent.equals(XSmartRefreshLayout.this.mRefreshHeader)) {
                if (XSmartRefreshLayout.this.mHeaderHeightStatus.notified) {
                    XSmartRefreshLayout xSmartRefreshLayout = XSmartRefreshLayout.this;
                    xSmartRefreshLayout.mHeaderHeightStatus = xSmartRefreshLayout.mHeaderHeightStatus.unNotify();
                }
            } else if (refreshComponent.equals(XSmartRefreshLayout.this.mRefreshFooter) && XSmartRefreshLayout.this.mFooterHeightStatus.notified) {
                XSmartRefreshLayout xSmartRefreshLayout2 = XSmartRefreshLayout.this;
                xSmartRefreshLayout2.mFooterHeightStatus = xSmartRefreshLayout2.mFooterHeightStatus.unNotify();
            }
            return this;
        }

        @Override // com.scwang.smart.refresh.layout.api.RefreshKernel
        public RefreshKernel requestFloorDuration(int i) {
            XSmartRefreshLayout.this.mFloorDuration = i;
            return this;
        }

        @Override // com.scwang.smart.refresh.layout.api.RefreshKernel
        public RefreshKernel requestFloorBottomPullUpToCloseRate(float f) {
            XSmartRefreshLayout.this.mTwoLevelBottomPullUpToCloseRate = f;
            return this;
        }

        @Override // com.scwang.smart.refresh.layout.api.RefreshKernel
        public RefreshKernel onAutoRefreshAnimationEnd(Animator animator, boolean z) {
            if (animator == null || animator.getDuration() != 0) {
                XSmartRefreshLayout.this.reboundAnimator = null;
                if (XSmartRefreshLayout.this.mRefreshHeader != null) {
                    if (XSmartRefreshLayout.this.mState != RefreshState.ReleaseToRefresh) {
                        setState(RefreshState.ReleaseToRefresh);
                    }
                    XSmartRefreshLayout.this.setStateRefreshing(!z);
                } else {
                    setState(RefreshState.None);
                }
                return this;
            }
            return this;
        }

        @Override // com.scwang.smart.refresh.layout.api.RefreshKernel
        public RefreshKernel onAutoLoadMoreAnimationEnd(Animator animator, boolean z) {
            if (animator == null || animator.getDuration() != 0) {
                XSmartRefreshLayout.this.reboundAnimator = null;
                if (XSmartRefreshLayout.this.mRefreshFooter != null) {
                    if (XSmartRefreshLayout.this.mState != RefreshState.ReleaseToLoad) {
                        setState(RefreshState.ReleaseToLoad);
                    }
                    XSmartRefreshLayout.this.setStateLoading(!z);
                } else {
                    setState(RefreshState.None);
                }
                return this;
            }
            return this;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.xiaopeng.xui.widget.XSmartRefreshLayout$10  reason: invalid class name */
    /* loaded from: classes2.dex */
    public static /* synthetic */ class AnonymousClass10 {
        static final /* synthetic */ int[] $SwitchMap$com$scwang$smart$refresh$layout$constant$RefreshState;

        static {
            int[] iArr = new int[RefreshState.values().length];
            $SwitchMap$com$scwang$smart$refresh$layout$constant$RefreshState = iArr;
            try {
                iArr[RefreshState.None.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$scwang$smart$refresh$layout$constant$RefreshState[RefreshState.PullDownToRefresh.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$scwang$smart$refresh$layout$constant$RefreshState[RefreshState.PullUpToLoad.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$scwang$smart$refresh$layout$constant$RefreshState[RefreshState.PullDownCanceled.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$scwang$smart$refresh$layout$constant$RefreshState[RefreshState.PullUpCanceled.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$scwang$smart$refresh$layout$constant$RefreshState[RefreshState.ReleaseToRefresh.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$scwang$smart$refresh$layout$constant$RefreshState[RefreshState.ReleaseToLoad.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                $SwitchMap$com$scwang$smart$refresh$layout$constant$RefreshState[RefreshState.ReleaseToTwoLevel.ordinal()] = 8;
            } catch (NoSuchFieldError unused8) {
            }
            try {
                $SwitchMap$com$scwang$smart$refresh$layout$constant$RefreshState[RefreshState.RefreshReleased.ordinal()] = 9;
            } catch (NoSuchFieldError unused9) {
            }
            try {
                $SwitchMap$com$scwang$smart$refresh$layout$constant$RefreshState[RefreshState.LoadReleased.ordinal()] = 10;
            } catch (NoSuchFieldError unused10) {
            }
            try {
                $SwitchMap$com$scwang$smart$refresh$layout$constant$RefreshState[RefreshState.Refreshing.ordinal()] = 11;
            } catch (NoSuchFieldError unused11) {
            }
            try {
                $SwitchMap$com$scwang$smart$refresh$layout$constant$RefreshState[RefreshState.Loading.ordinal()] = 12;
            } catch (NoSuchFieldError unused12) {
            }
        }
    }
}

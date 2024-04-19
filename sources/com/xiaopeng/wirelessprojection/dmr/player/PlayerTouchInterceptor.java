package com.xiaopeng.wirelessprojection.dmr.player;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import com.xiaopeng.lib.utils.LogUtils;
import com.xiaopeng.wirelessprojection.dmr.R;
import com.xiaopeng.xui.widget.XFrameLayout;
import com.xiaopeng.xui.widget.XImageView;
import com.xiaopeng.xui.widget.XProgressBar;
/* loaded from: classes2.dex */
public class PlayerTouchInterceptor implements View.OnTouchListener {
    private static final String TAG = "PlayerTouchInterceptor";
    private View mActionView;
    private XImageView mIvAdjustButton;
    private XFrameLayout mLlAdjustIndicatorWrapper;
    private XProgressBar mPbAdjustProgressBar;
    private VideoTouchCallback videoTouchCallback;
    private View viewImpl;
    private float lastTouchEventX = 0.0f;
    private float lastTouchEventY = 0.0f;
    private PlayerTouchType lastTouchType = PlayerTouchType.NONE;
    private float parallaxX = 1.0f;
    private float parallaxYVolume = 4.4f;
    private float parallaxYLight = 4.4f;
    private float ratioThreshold = 0.01f;
    private Rect allowXAlixRange = null;
    private TouchMoveBound allowXAlixMoveBound = new TouchMoveBound(20, 20);
    private Rect allowYAlixRangeLeft = null;
    private Rect allowYAlixRangeRight = null;
    private TouchMoveBound allowYAlixMoveBound = new TouchMoveBound(20, 20);
    private AdjustInfo adjustVolumeInfo = new AdjustInfo();
    private AdjustInfo adjustBrightnessInfo = new AdjustInfo();

    public PlayerTouchInterceptor(View view, View view2, VideoTouchCallback videoTouchCallback) {
        this.viewImpl = view;
        this.mActionView = view2;
        this.videoTouchCallback = videoTouchCallback;
        initInterceptor();
    }

    void initInterceptor() {
        try {
            this.mIvAdjustButton = (XImageView) this.mActionView.findViewById(R.id.adjustIcon);
            this.mLlAdjustIndicatorWrapper = (XFrameLayout) this.mActionView.findViewById(R.id.llAdjustIndicatorWrapper);
            this.mPbAdjustProgressBar = (XProgressBar) this.mActionView.findViewById(R.id.adjustProgressBar);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    void viewSizeChange() {
        this.allowXAlixRange = null;
        this.allowYAlixRangeLeft = null;
        this.allowYAlixRangeRight = null;
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int width = view.getWidth();
        int height = view.getHeight();
        if (width != 0 && height != 0) {
            int actionMasked = motionEvent.getActionMasked();
            if (actionMasked == 0) {
                this.lastTouchEventX = motionEvent.getX();
                this.lastTouchEventY = motionEvent.getY();
                handleTouchDown(width, height);
            } else if (actionMasked == 1) {
                releaseTouchHandler();
                view.performClick();
            } else if (actionMasked == 2) {
                return handlerTouchMove(motionEvent.getX() - this.lastTouchEventX, motionEvent.getY() - this.lastTouchEventY, width, height, motionEvent);
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.xiaopeng.wirelessprojection.dmr.player.PlayerTouchInterceptor$1  reason: invalid class name */
    /* loaded from: classes2.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$xiaopeng$wirelessprojection$dmr$player$PlayerTouchType;

        static {
            int[] iArr = new int[PlayerTouchType.values().length];
            $SwitchMap$com$xiaopeng$wirelessprojection$dmr$player$PlayerTouchType = iArr;
            try {
                iArr[PlayerTouchType.NONE.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$xiaopeng$wirelessprojection$dmr$player$PlayerTouchType[PlayerTouchType.TOUCH_PROGRESS.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$xiaopeng$wirelessprojection$dmr$player$PlayerTouchType[PlayerTouchType.TOUCH_VOLUME.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$xiaopeng$wirelessprojection$dmr$player$PlayerTouchType[PlayerTouchType.TOUCH_LIGHT.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
        }
    }

    private boolean handlerTouchMove(float f, float f2, int i, int i2, MotionEvent motionEvent) {
        int i3 = AnonymousClass1.$SwitchMap$com$xiaopeng$wirelessprojection$dmr$player$PlayerTouchType[this.lastTouchType.ordinal()];
        if (i3 != 1) {
            if (i3 != 3) {
                if (i3 != 4) {
                    return false;
                }
                return touchLight(f, f2, i2, motionEvent);
            }
            return touchVolume(f, f2, i2, motionEvent);
        }
        if (isTouchVolume(f, f2, i2, motionEvent)) {
            this.lastTouchType = PlayerTouchType.TOUCH_VOLUME;
        }
        if (isTouchLight(f, f2, i2, motionEvent)) {
            this.lastTouchType = PlayerTouchType.TOUCH_LIGHT;
        }
        return this.lastTouchType != PlayerTouchType.NONE;
    }

    private void handleTouchDown(int i, int i2) {
        if (this.allowXAlixRange == null) {
            this.allowXAlixRange = new Rect(0, 0, i, i2);
        }
        if (this.allowYAlixRangeLeft == null) {
            int i3 = i2 / 6;
            this.allowYAlixRangeLeft = new Rect(0, i3 * 1, i / 2, i3 * 5);
        }
        if (this.allowYAlixRangeRight == null) {
            int i4 = i2 / 6;
            this.allowYAlixRangeRight = new Rect(i / 2, i4 * 1, i, i4 * 5);
        }
        this.adjustVolumeInfo.available = false;
        this.adjustBrightnessInfo.available = false;
    }

    private boolean isTouchVolume(float f, float f2, int i, MotionEvent motionEvent) {
        Rect rect = this.allowYAlixRangeRight;
        return rect != null && rect.contains((int) motionEvent.getX(), (int) motionEvent.getY()) && Math.abs(f) < ((float) this.allowYAlixMoveBound.getLowBound()) && Math.abs(f2) > ((float) this.allowYAlixMoveBound.getUpBound());
    }

    private boolean isTouchLight(float f, float f2, int i, MotionEvent motionEvent) {
        Rect rect = this.allowYAlixRangeLeft;
        return rect != null && rect.contains((int) motionEvent.getX(), (int) motionEvent.getY()) && Math.abs(f) < ((float) this.allowYAlixMoveBound.getLowBound()) && Math.abs(f2) > ((float) this.allowYAlixMoveBound.getUpBound());
    }

    /* JADX WARN: Code restructure failed: missing block: B:5:0x000e, code lost:
        if (r0 != 4) goto L6;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void releaseTouchHandler() {
        /*
            r2 = this;
            int[] r0 = com.xiaopeng.wirelessprojection.dmr.player.PlayerTouchInterceptor.AnonymousClass1.$SwitchMap$com$xiaopeng$wirelessprojection$dmr$player$PlayerTouchType
            com.xiaopeng.wirelessprojection.dmr.player.PlayerTouchType r1 = r2.lastTouchType
            int r1 = r1.ordinal()
            r0 = r0[r1]
            r1 = 3
            if (r0 == r1) goto L11
            r1 = 4
            if (r0 == r1) goto L14
            goto L17
        L11:
            r2.releaseVolumeTouch()
        L14:
            r2.releaseLightTouch()
        L17:
            com.xiaopeng.wirelessprojection.dmr.player.PlayerTouchType r0 = com.xiaopeng.wirelessprojection.dmr.player.PlayerTouchType.NONE
            r2.lastTouchType = r0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaopeng.wirelessprojection.dmr.player.PlayerTouchInterceptor.releaseTouchHandler():void");
    }

    private boolean touchVolume(float f, float f2, int i, MotionEvent motionEvent) {
        float f3 = (-f2) / i;
        if (Math.abs(f3) > this.ratioThreshold) {
            if (!this.adjustVolumeInfo.available) {
                this.adjustVolumeInfo = this.videoTouchCallback.getVolumeInfo();
            }
            this.adjustVolumeInfo.addIncrease(f3 * this.parallaxYVolume);
            this.videoTouchCallback.changeSystemVolumeImpl(this.adjustVolumeInfo.progress);
            visibleAdjustIndicator(true);
            XImageView xImageView = this.mIvAdjustButton;
            if (xImageView != null) {
                xImageView.setImageResource(R.drawable.ic_video_player_audio);
            }
            XProgressBar xProgressBar = this.mPbAdjustProgressBar;
            if (xProgressBar != null) {
                xProgressBar.setProgress(this.adjustVolumeInfo.progressUI);
                this.mPbAdjustProgressBar.setMax(this.adjustVolumeInfo.maxValueUI);
            }
        }
        return true;
    }

    private void releaseVolumeTouch() {
        visibleAdjustIndicator(false);
    }

    private boolean touchLight(float f, float f2, int i, MotionEvent motionEvent) {
        float f3 = (-f2) / i;
        if (Math.abs(f3) > this.ratioThreshold) {
            if (!this.adjustBrightnessInfo.available) {
                this.adjustBrightnessInfo = this.videoTouchCallback.getBrightnessInfo();
            }
            this.adjustBrightnessInfo.addIncrease(f3 * this.parallaxYLight);
            this.videoTouchCallback.changeBrightnessImpl(this.adjustBrightnessInfo.progress);
            visibleAdjustIndicator(true);
            XImageView xImageView = this.mIvAdjustButton;
            if (xImageView != null) {
                xImageView.setImageResource(R.drawable.icon_video_player_light);
            }
            XProgressBar xProgressBar = this.mPbAdjustProgressBar;
            if (xProgressBar != null) {
                xProgressBar.setProgress(this.adjustBrightnessInfo.progressUI);
                this.mPbAdjustProgressBar.setMax(this.adjustBrightnessInfo.maxValueUI);
            }
        }
        return true;
    }

    private void releaseLightTouch() {
        visibleAdjustIndicator(false);
    }

    private void visibleAdjustIndicator(boolean z) {
        XFrameLayout xFrameLayout = this.mLlAdjustIndicatorWrapper;
        if (xFrameLayout != null) {
            if (z) {
                xFrameLayout.bringToFront();
                if (this.mLlAdjustIndicatorWrapper.getVisibility() == 4) {
                    LogUtils.i(TAG, "visibleAdjustIndicator set visible");
                    this.mLlAdjustIndicatorWrapper.setVisibility(0);
                }
            } else if (xFrameLayout.getVisibility() == 0) {
                LogUtils.i(TAG, "visibleAdjustIndicator set invisible");
                this.mLlAdjustIndicatorWrapper.setVisibility(4);
            }
        }
    }
}

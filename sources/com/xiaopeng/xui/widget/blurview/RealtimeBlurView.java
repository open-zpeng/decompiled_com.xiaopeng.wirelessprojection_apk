package com.xiaopeng.xui.widget.blurview;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.view.XView;
/* loaded from: classes2.dex */
public class RealtimeBlurView extends XView {
    private static int BLUR_IMPL;
    private static int RENDERING_COUNT;
    private static StopException STOP_EXCEPTION = new StopException();
    private Bitmap mBitmapToBlur;
    private final BlurImpl mBlurImpl;
    private float mBlurRadius;
    private Bitmap mBlurredBitmap;
    private Canvas mBlurringCanvas;
    private View mDecorView;
    private boolean mDifferentRoot;
    private boolean mDirty;
    private float mDownsampleFactor;
    private boolean mIsRendering;
    private int mOverlayColor;
    private Paint mPaint;
    private final Rect mRectDst;
    private final Rect mRectSrc;
    private final ViewTreeObserver.OnPreDrawListener preDrawListener;

    static /* synthetic */ int access$608() {
        int i = RENDERING_COUNT;
        RENDERING_COUNT = i + 1;
        return i;
    }

    static /* synthetic */ int access$610() {
        int i = RENDERING_COUNT;
        RENDERING_COUNT = i - 1;
        return i;
    }

    public RealtimeBlurView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mRectSrc = new Rect();
        this.mRectDst = new Rect();
        this.preDrawListener = new ViewTreeObserver.OnPreDrawListener() { // from class: com.xiaopeng.xui.widget.blurview.RealtimeBlurView.1
            @Override // android.view.ViewTreeObserver.OnPreDrawListener
            public boolean onPreDraw() {
                int[] iArr = new int[2];
                Bitmap bitmap = RealtimeBlurView.this.mBlurredBitmap;
                View view = RealtimeBlurView.this.mDecorView;
                if (view != null && RealtimeBlurView.this.isShown() && RealtimeBlurView.this.prepare()) {
                    boolean z = RealtimeBlurView.this.mBlurredBitmap != bitmap;
                    view.getLocationOnScreen(iArr);
                    RealtimeBlurView.this.getLocationOnScreen(iArr);
                    int i = (-iArr[0]) + iArr[0];
                    int i2 = (-iArr[1]) + iArr[1];
                    RealtimeBlurView.this.mBitmapToBlur.eraseColor(RealtimeBlurView.this.mOverlayColor);
                    int save = RealtimeBlurView.this.mBlurringCanvas.save();
                    RealtimeBlurView.this.mIsRendering = true;
                    RealtimeBlurView.access$608();
                    try {
                        RealtimeBlurView.this.mBlurringCanvas.scale((RealtimeBlurView.this.mBitmapToBlur.getWidth() * 1.0f) / RealtimeBlurView.this.getWidth(), (RealtimeBlurView.this.mBitmapToBlur.getHeight() * 1.0f) / RealtimeBlurView.this.getHeight());
                        RealtimeBlurView.this.mBlurringCanvas.translate(-i, -i2);
                        if (view.getBackground() != null) {
                            view.getBackground().draw(RealtimeBlurView.this.mBlurringCanvas);
                        }
                        view.draw(RealtimeBlurView.this.mBlurringCanvas);
                    } catch (StopException unused) {
                    } catch (Throwable th) {
                        RealtimeBlurView.this.mIsRendering = false;
                        RealtimeBlurView.access$610();
                        RealtimeBlurView.this.mBlurringCanvas.restoreToCount(save);
                        throw th;
                    }
                    RealtimeBlurView.this.mIsRendering = false;
                    RealtimeBlurView.access$610();
                    RealtimeBlurView.this.mBlurringCanvas.restoreToCount(save);
                    RealtimeBlurView realtimeBlurView = RealtimeBlurView.this;
                    realtimeBlurView.blur(realtimeBlurView.mBitmapToBlur, RealtimeBlurView.this.mBlurredBitmap);
                    if (z || RealtimeBlurView.this.mDifferentRoot) {
                        RealtimeBlurView.this.invalidate();
                    }
                }
                return true;
            }
        };
        this.mBlurImpl = getBlurImpl();
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.RealtimeBlurView);
        this.mBlurRadius = obtainStyledAttributes.getDimension(R.styleable.RealtimeBlurView_realtimeBlurRadius, TypedValue.applyDimension(1, 10.0f, context.getResources().getDisplayMetrics()));
        this.mDownsampleFactor = obtainStyledAttributes.getFloat(R.styleable.RealtimeBlurView_realtimeDownsampleFactor, 4.0f);
        this.mOverlayColor = obtainStyledAttributes.getColor(R.styleable.RealtimeBlurView_realtimeOverlayColor, 0);
        obtainStyledAttributes.recycle();
        this.mPaint = new Paint();
    }

    protected BlurImpl getBlurImpl() {
        try {
            AndroidStockBlurImpl androidStockBlurImpl = new AndroidStockBlurImpl();
            Bitmap createBitmap = Bitmap.createBitmap(4, 4, Bitmap.Config.ARGB_8888);
            androidStockBlurImpl.prepare(getContext(), createBitmap, 4.0f);
            androidStockBlurImpl.release();
            createBitmap.recycle();
            BLUR_IMPL = 3;
            return new AndroidStockBlurImpl();
        } catch (Throwable unused) {
            return new EmptyBlurImpl();
        }
    }

    public void setBlurRadius(float f) {
        if (this.mBlurRadius != f) {
            this.mBlurRadius = f;
            this.mDirty = true;
            invalidate();
        }
    }

    public void setDownsampleFactor(float f) {
        if (f <= 0.0f) {
            throw new IllegalArgumentException("Downsample factor must be greater than 0.");
        }
        if (this.mDownsampleFactor != f) {
            this.mDownsampleFactor = f;
            this.mDirty = true;
            releaseBitmap();
            invalidate();
        }
    }

    public void setOverlayColor(int i) {
        if (this.mOverlayColor != i) {
            this.mOverlayColor = i;
            invalidate();
        }
    }

    private void releaseBitmap() {
        Bitmap bitmap = this.mBitmapToBlur;
        if (bitmap != null) {
            bitmap.recycle();
            this.mBitmapToBlur = null;
        }
        Bitmap bitmap2 = this.mBlurredBitmap;
        if (bitmap2 != null) {
            bitmap2.recycle();
            this.mBlurredBitmap = null;
        }
    }

    protected void release() {
        releaseBitmap();
        this.mBlurImpl.release();
    }

    protected boolean prepare() {
        Bitmap bitmap;
        float f = this.mBlurRadius;
        if (f == 0.0f) {
            release();
            return false;
        }
        float f2 = this.mDownsampleFactor;
        float f3 = f / f2;
        if (f3 > 25.0f) {
            f2 = (f2 * f3) / 25.0f;
            f3 = 25.0f;
        }
        int width = getWidth();
        int height = getHeight();
        int max = Math.max(1, (int) (width / f2));
        int max2 = Math.max(1, (int) (height / f2));
        boolean z = this.mDirty;
        if (this.mBlurringCanvas == null || (bitmap = this.mBlurredBitmap) == null || bitmap.getWidth() != max || this.mBlurredBitmap.getHeight() != max2) {
            releaseBitmap();
            try {
                Bitmap createBitmap = Bitmap.createBitmap(max, max2, Bitmap.Config.ARGB_8888);
                this.mBitmapToBlur = createBitmap;
                if (createBitmap != null) {
                    this.mBlurringCanvas = new Canvas(this.mBitmapToBlur);
                    Bitmap createBitmap2 = Bitmap.createBitmap(max, max2, Bitmap.Config.ARGB_8888);
                    this.mBlurredBitmap = createBitmap2;
                    if (createBitmap2 == null) {
                        release();
                        return false;
                    }
                    z = true;
                } else {
                    release();
                    return false;
                }
            } catch (OutOfMemoryError unused) {
                release();
                return false;
            } catch (Throwable unused2) {
                release();
                return false;
            }
        }
        if (z) {
            if (!this.mBlurImpl.prepare(getContext(), this.mBitmapToBlur, f3)) {
                return false;
            }
            this.mDirty = false;
        }
        return true;
    }

    protected void blur(Bitmap bitmap, Bitmap bitmap2) {
        this.mBlurImpl.blur(bitmap, bitmap2);
    }

    protected View getActivityDecorView() {
        Context context = getContext();
        for (int i = 0; i < 4 && context != null && !(context instanceof Activity) && (context instanceof ContextWrapper); i++) {
            context = ((ContextWrapper) context).getBaseContext();
        }
        if (context instanceof Activity) {
            return ((Activity) context).getWindow().getDecorView();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.view.XView, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        View activityDecorView = getActivityDecorView();
        this.mDecorView = activityDecorView;
        if (activityDecorView != null) {
            activityDecorView.getViewTreeObserver().addOnPreDrawListener(this.preDrawListener);
            boolean z = this.mDecorView.getRootView() != getRootView();
            this.mDifferentRoot = z;
            if (z) {
                this.mDecorView.postInvalidate();
                return;
            }
            return;
        }
        this.mDifferentRoot = false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.view.XView, android.view.View
    public void onDetachedFromWindow() {
        View view = this.mDecorView;
        if (view != null) {
            view.getViewTreeObserver().removeOnPreDrawListener(this.preDrawListener);
        }
        release();
        super.onDetachedFromWindow();
    }

    @Override // android.view.View
    public void draw(Canvas canvas) {
        if (this.mIsRendering) {
            throw STOP_EXCEPTION;
        }
        if (RENDERING_COUNT > 0) {
            return;
        }
        super.draw(canvas);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBlurredBitmap(canvas, this.mBlurredBitmap, this.mOverlayColor);
    }

    protected void drawBlurredBitmap(Canvas canvas, Bitmap bitmap, int i) {
        if (bitmap != null) {
            this.mRectSrc.right = bitmap.getWidth();
            this.mRectSrc.bottom = bitmap.getHeight();
            this.mRectDst.right = getWidth();
            this.mRectDst.bottom = getHeight();
            canvas.drawBitmap(bitmap, this.mRectSrc, this.mRectDst, (Paint) null);
        }
        this.mPaint.setColor(i);
        canvas.drawRect(this.mRectDst, this.mPaint);
    }

    /* loaded from: classes2.dex */
    private static class StopException extends RuntimeException {
        private StopException() {
        }
    }
}

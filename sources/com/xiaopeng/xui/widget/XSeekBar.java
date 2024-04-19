package com.xiaopeng.xui.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.core.content.res.ResourcesCompat;
import com.xiaopeng.vui.commons.IVuiElementBuilder;
import com.xiaopeng.vui.commons.IVuiElementListener;
import com.xiaopeng.vui.commons.model.VuiElement;
import com.xiaopeng.vui.commons.model.VuiEvent;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.utils.FlavorUtils;
import com.xiaopeng.xui.view.XViewDelegate;
import com.xiaopeng.xui.vui.VuiView;
import com.xiaopeng.xui.vui.floatinglayer.VuiFloatingLayerManager;
/* loaded from: classes2.dex */
public class XSeekBar extends AppCompatSeekBar implements VuiView, IVuiElementListener {
    private Drawable mActionDownProgressDrawable;
    private Drawable mActionDownThumbDrawable;
    private Drawable mActionUpProgressDrawable;
    private Drawable mActionUpThumbDrawable;
    private final long mDelayTime;
    private Runnable mHandleUpChangeRunnable;
    private final Handler mHandler;
    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener;
    protected XViewDelegate mXViewDelegate;

    @Override // com.xiaopeng.vui.commons.IVuiElementListener
    public VuiElement onBuildVuiElement(String str, IVuiElementBuilder iVuiElementBuilder) {
        return null;
    }

    public XSeekBar(Context context) {
        super(context);
        this.mDelayTime = 500L;
        this.mHandler = new Handler(Looper.getMainLooper());
        init();
    }

    public XSeekBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mDelayTime = 500L;
        this.mHandler = new Handler(Looper.getMainLooper());
        this.mXViewDelegate = XViewDelegate.create(this, attributeSet);
        init();
        initVui(this, attributeSet);
    }

    public XSeekBar(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mDelayTime = 500L;
        this.mHandler = new Handler(Looper.getMainLooper());
        this.mXViewDelegate = XViewDelegate.create(this, attributeSet, i, 0);
        init();
        initVui(this, attributeSet);
    }

    private void init() {
        if (FlavorUtils.isV5()) {
            this.mActionDownThumbDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.x_seekbar_action_down_thumb, getContext().getTheme());
            this.mActionDownProgressDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.x_seekbar_progress_action_down_drawable, getContext().getTheme());
        }
        this.mActionUpThumbDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.x_seekbar_thumb, getContext().getTheme());
        this.mActionUpProgressDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.x_seekbar_progress_drawable, getContext().getTheme());
        this.mHandleUpChangeRunnable = new Runnable() { // from class: com.xiaopeng.xui.widget.-$$Lambda$XSeekBar$jzwkPcQ2eMlRARJYRlY_ikjviHU
            @Override // java.lang.Runnable
            public final void run() {
                XSeekBar.this.lambda$init$0$XSeekBar();
            }
        };
        super.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.xiaopeng.xui.widget.XSeekBar.1
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                if (XSeekBar.this.mOnSeekBarChangeListener != null) {
                    XSeekBar.this.mOnSeekBarChangeListener.onProgressChanged(seekBar, i, z);
                }
                XSeekBar xSeekBar = XSeekBar.this;
                xSeekBar.updateVui(xSeekBar);
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (XSeekBar.this.mOnSeekBarChangeListener != null) {
                    XSeekBar.this.mOnSeekBarChangeListener.onStartTrackingTouch(seekBar);
                }
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (XSeekBar.this.mOnSeekBarChangeListener != null) {
                    XSeekBar.this.mOnSeekBarChangeListener.onStopTrackingTouch(seekBar);
                }
            }
        });
    }

    public /* synthetic */ void lambda$init$0$XSeekBar() {
        Drawable drawable = this.mActionUpThumbDrawable;
        if (drawable != null) {
            setThumb(drawable);
        }
        Drawable drawable2 = this.mActionUpProgressDrawable;
        if (drawable2 != null) {
            setProgressDrawable(drawable2);
        }
    }

    @Override // android.widget.AbsSeekBar, android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        Runnable runnable;
        int action = motionEvent.getAction();
        if (action == 0) {
            if (FlavorUtils.isV5()) {
                Runnable runnable2 = this.mHandleUpChangeRunnable;
                if (runnable2 != null) {
                    this.mHandler.removeCallbacks(runnable2);
                }
                Drawable drawable = this.mActionDownThumbDrawable;
                if (drawable != null) {
                    setThumb(drawable);
                }
                Drawable drawable2 = this.mActionDownProgressDrawable;
                if (drawable2 != null) {
                    setProgressDrawable(drawable2);
                }
                invalidate();
            }
        } else if (action == 1 && FlavorUtils.isV5() && (runnable = this.mHandleUpChangeRunnable) != null) {
            this.mHandler.removeCallbacks(runnable);
            this.mHandler.postDelayed(this.mHandleUpChangeRunnable, 500L);
        }
        return super.onTouchEvent(motionEvent);
    }

    @Override // android.widget.SeekBar
    public void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener onSeekBarChangeListener) {
        this.mOnSeekBarChangeListener = onSeekBarChangeListener;
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        XViewDelegate xViewDelegate = this.mXViewDelegate;
        if (xViewDelegate != null) {
            xViewDelegate.onConfigurationChanged(configuration);
            if (FlavorUtils.isV5()) {
                this.mActionDownThumbDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.x_seekbar_action_down_thumb, getContext().getTheme());
                this.mActionDownProgressDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.x_seekbar_progress_action_down_drawable, getContext().getTheme());
            }
            this.mActionUpThumbDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.x_seekbar_thumb, getContext().getTheme());
            this.mActionUpProgressDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.x_seekbar_progress_drawable, getContext().getTheme());
        }
    }

    @Override // android.widget.ProgressBar, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        XViewDelegate xViewDelegate = this.mXViewDelegate;
        if (xViewDelegate != null) {
            xViewDelegate.onAttachedToWindow();
        }
    }

    @Override // android.widget.ProgressBar, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        XViewDelegate xViewDelegate = this.mXViewDelegate;
        if (xViewDelegate != null) {
            xViewDelegate.onDetachedFromWindow();
        }
    }

    @Override // android.view.View
    public void setVisibility(int i) {
        super.setVisibility(i);
        setVuiVisibility(this, i);
    }

    protected void finalize() throws Throwable {
        super.finalize();
        releaseVui();
    }

    @Override // com.xiaopeng.vui.commons.IVuiElementListener
    public boolean onVuiElementEvent(final View view, VuiEvent vuiEvent) {
        final Double d;
        logD("SeekBar onVuiElementEvent");
        if (view == null || (d = (Double) vuiEvent.getEventValue(vuiEvent)) == null) {
            return false;
        }
        post(new Runnable() { // from class: com.xiaopeng.xui.widget.-$$Lambda$XSeekBar$TZZsuPh57RtV-5ijRhOR7YcGjkA
            @Override // java.lang.Runnable
            public final void run() {
                XSeekBar.this.lambda$onVuiElementEvent$1$XSeekBar(d, view);
            }
        });
        return true;
    }

    public /* synthetic */ void lambda$onVuiElementEvent$1$XSeekBar(Double d, View view) {
        if (d.doubleValue() >= 0.0d && d.doubleValue() <= 100.0d) {
            setProgress(d.intValue());
        }
        VuiFloatingLayerManager.show(view, (int) ((((getProgress() * 1.0f) / getMax()) - 0.5d) * getWidth()), 0);
    }

    @Override // android.view.View
    public void setEnabled(boolean z) {
        super.setEnabled(z);
        if (FlavorUtils.isV5()) {
            if (z) {
                setAlpha(1.0f);
            } else {
                setAlpha(0.4f);
            }
        }
    }
}

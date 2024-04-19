package com.xiaopeng.wirelessprojection.dmr.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import com.xiaopeng.wirelessprojection.dmr.R;
import com.xiaopeng.wirelessprojection.dmr.view.AutoScaleSurfaceView;
import com.xiaopeng.xui.view.XView;
import com.xiaopeng.xui.widget.XConstraintLayout;
import com.xiaopeng.xui.widget.XFrameLayout;
import com.xiaopeng.xui.widget.XImageView;
import com.xiaopeng.xui.widget.XLinearLayout;
import com.xiaopeng.xui.widget.XProgressBar;
import com.xiaopeng.xui.widget.XRelativeLayout;
import com.xiaopeng.xui.widget.XTextView;
/* loaded from: classes2.dex */
public abstract class ActivityDlnaRendererBinding extends ViewDataBinding {
    public final XImageView adjustIcon;
    public final XProgressBar adjustProgressBar;
    public final XFrameLayout flContentVideo;
    public final XFrameLayout flFragmentVideoParent;
    public final XFrameLayout flTitleLayout;
    public final XRelativeLayout ibBtnBack;
    public final XRelativeLayout ivAudioCover;
    public final XFrameLayout llAdjustIndicatorWrapper;
    public final AutoScaleSurfaceView svMirror;
    public final XTextView tvVideoTitle;
    public final XConstraintLayout videoLayoutLoading;
    public final XLinearLayout videoLoading;
    public final XFrameLayout xflRendererMain;
    public final XView xvTouchArea;

    /* JADX INFO: Access modifiers changed from: protected */
    public ActivityDlnaRendererBinding(Object obj, View view, int i, XImageView xImageView, XProgressBar xProgressBar, XFrameLayout xFrameLayout, XFrameLayout xFrameLayout2, XFrameLayout xFrameLayout3, XRelativeLayout xRelativeLayout, XRelativeLayout xRelativeLayout2, XFrameLayout xFrameLayout4, AutoScaleSurfaceView autoScaleSurfaceView, XTextView xTextView, XConstraintLayout xConstraintLayout, XLinearLayout xLinearLayout, XFrameLayout xFrameLayout5, XView xView) {
        super(obj, view, i);
        this.adjustIcon = xImageView;
        this.adjustProgressBar = xProgressBar;
        this.flContentVideo = xFrameLayout;
        this.flFragmentVideoParent = xFrameLayout2;
        this.flTitleLayout = xFrameLayout3;
        this.ibBtnBack = xRelativeLayout;
        this.ivAudioCover = xRelativeLayout2;
        this.llAdjustIndicatorWrapper = xFrameLayout4;
        this.svMirror = autoScaleSurfaceView;
        this.tvVideoTitle = xTextView;
        this.videoLayoutLoading = xConstraintLayout;
        this.videoLoading = xLinearLayout;
        this.xflRendererMain = xFrameLayout5;
        this.xvTouchArea = xView;
    }

    public static ActivityDlnaRendererBinding inflate(LayoutInflater layoutInflater, ViewGroup viewGroup, boolean z) {
        return inflate(layoutInflater, viewGroup, z, DataBindingUtil.getDefaultComponent());
    }

    @Deprecated
    public static ActivityDlnaRendererBinding inflate(LayoutInflater layoutInflater, ViewGroup viewGroup, boolean z, Object obj) {
        return (ActivityDlnaRendererBinding) ViewDataBinding.inflateInternal(layoutInflater, R.layout.activity_dlna_renderer, viewGroup, z, obj);
    }

    public static ActivityDlnaRendererBinding inflate(LayoutInflater layoutInflater) {
        return inflate(layoutInflater, DataBindingUtil.getDefaultComponent());
    }

    @Deprecated
    public static ActivityDlnaRendererBinding inflate(LayoutInflater layoutInflater, Object obj) {
        return (ActivityDlnaRendererBinding) ViewDataBinding.inflateInternal(layoutInflater, R.layout.activity_dlna_renderer, null, false, obj);
    }

    public static ActivityDlnaRendererBinding bind(View view) {
        return bind(view, DataBindingUtil.getDefaultComponent());
    }

    @Deprecated
    public static ActivityDlnaRendererBinding bind(View view, Object obj) {
        return (ActivityDlnaRendererBinding) bind(obj, view, R.layout.activity_dlna_renderer);
    }
}

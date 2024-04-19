package com.xiaopeng.wirelessprojection.dmr.databinding;

import android.util.SparseIntArray;
import android.view.View;
import androidx.databinding.DataBindingComponent;
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
public class ActivityDlnaRendererBindingImpl extends ActivityDlnaRendererBinding {
    private static final ViewDataBinding.IncludedLayouts sIncludes = null;
    private static final SparseIntArray sViewsWithIds;
    private long mDirtyFlags;

    @Override // androidx.databinding.ViewDataBinding
    protected boolean onFieldChange(int i, Object obj, int i2) {
        return false;
    }

    @Override // androidx.databinding.ViewDataBinding
    public boolean setVariable(int i, Object obj) {
        return true;
    }

    static {
        SparseIntArray sparseIntArray = new SparseIntArray();
        sViewsWithIds = sparseIntArray;
        sparseIntArray.put(R.id.fl_content_video, 1);
        sparseIntArray.put(R.id.fl_fragment_video_parent, 2);
        sparseIntArray.put(R.id.sv_mirror, 3);
        sparseIntArray.put(R.id.iv_audio_cover, 4);
        sparseIntArray.put(R.id.xv_touch_area, 5);
        sparseIntArray.put(R.id.fl_title_layout, 6);
        sparseIntArray.put(R.id.tv_video_title, 7);
        sparseIntArray.put(R.id.ib_btn_back, 8);
        sparseIntArray.put(R.id.llAdjustIndicatorWrapper, 9);
        sparseIntArray.put(R.id.adjustProgressBar, 10);
        sparseIntArray.put(R.id.adjustIcon, 11);
        sparseIntArray.put(R.id.video_layout_loading, 12);
        sparseIntArray.put(R.id.video_loading, 13);
    }

    public ActivityDlnaRendererBindingImpl(DataBindingComponent dataBindingComponent, View view) {
        this(dataBindingComponent, view, mapBindings(dataBindingComponent, view, 14, sIncludes, sViewsWithIds));
    }

    private ActivityDlnaRendererBindingImpl(DataBindingComponent dataBindingComponent, View view, Object[] objArr) {
        super(dataBindingComponent, view, 0, (XImageView) objArr[11], (XProgressBar) objArr[10], (XFrameLayout) objArr[1], (XFrameLayout) objArr[2], (XFrameLayout) objArr[6], (XRelativeLayout) objArr[8], (XRelativeLayout) objArr[4], (XFrameLayout) objArr[9], (AutoScaleSurfaceView) objArr[3], (XTextView) objArr[7], (XConstraintLayout) objArr[12], (XLinearLayout) objArr[13], (XFrameLayout) objArr[0], (XView) objArr[5]);
        this.mDirtyFlags = -1L;
        this.xflRendererMain.setTag(null);
        setRootTag(view);
        invalidateAll();
    }

    @Override // androidx.databinding.ViewDataBinding
    public void invalidateAll() {
        synchronized (this) {
            this.mDirtyFlags = 1L;
        }
        requestRebind();
    }

    @Override // androidx.databinding.ViewDataBinding
    public boolean hasPendingBindings() {
        synchronized (this) {
            return this.mDirtyFlags != 0;
        }
    }

    @Override // androidx.databinding.ViewDataBinding
    protected void executeBindings() {
        synchronized (this) {
            this.mDirtyFlags = 0L;
        }
    }
}

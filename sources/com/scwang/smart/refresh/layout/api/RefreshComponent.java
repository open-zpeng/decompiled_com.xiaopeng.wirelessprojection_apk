package com.scwang.smart.refresh.layout.api;

import android.view.View;
import com.scwang.smart.refresh.layout.constant.SpinnerStyle;
import com.scwang.smart.refresh.layout.listener.OnStateChangedListener;
/* loaded from: classes2.dex */
public interface RefreshComponent extends OnStateChangedListener {
    boolean autoOpen(int i, float f, boolean z);

    SpinnerStyle getSpinnerStyle();

    View getView();

    boolean isSupportHorizontalDrag();

    int onFinish(RefreshLayout refreshLayout, boolean z);

    void onHorizontalDrag(float f, int i, int i2);

    void onInitialized(RefreshKernel refreshKernel, int i, int i2);

    void onMoving(boolean z, float f, int i, int i2, int i3);

    void onReleased(RefreshLayout refreshLayout, int i, int i2);

    void onStartAnimator(RefreshLayout refreshLayout, int i, int i2);

    void setPrimaryColors(int... iArr);
}

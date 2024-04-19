package com.xiaopeng.xui.widget;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.view.XView;
import com.xiaopeng.xui.widget.blurview.RealtimeBlurView;
import com.xiaopeng.xuimanager.utils.LogUtil;
/* loaded from: classes2.dex */
public class XStateBarView extends FrameLayout {
    float alpha;
    View alphaView;
    float radius;
    RealtimeBlurView realtimeBlurView;
    View[] scrollableView;
    int y;

    public XStateBarView(Context context) {
        this(context, null);
    }

    public XStateBarView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.y = 0;
        XView xView = new XView(context, attributeSet);
        this.alphaView = xView;
        xView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        this.alphaView.setBackgroundResource(R.color.x_color_bg_2);
        this.alphaView.setAlpha(0.0f);
        RealtimeBlurView realtimeBlurView = new RealtimeBlurView(context, attributeSet);
        this.realtimeBlurView = realtimeBlurView;
        realtimeBlurView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        this.realtimeBlurView.setBlurRadius(0.0f);
        addView(this.alphaView);
        addView(this.realtimeBlurView);
    }

    public void bindScrollableView(View... viewArr) {
        this.scrollableView = viewArr;
        if (viewArr == null) {
            updateView(0);
            return;
        }
        for (View view : viewArr) {
            this.y = Math.max(view.getScrollY(), this.y);
            view.setOnScrollChangeListener(new View.OnScrollChangeListener() { // from class: com.xiaopeng.xui.widget.-$$Lambda$XStateBarView$DY7covfoMoqiYjV4MyqouBX1W7A
                @Override // android.view.View.OnScrollChangeListener
                public final void onScrollChange(View view2, int i, int i2, int i3, int i4) {
                    XStateBarView.this.lambda$bindScrollableView$0$XStateBarView(view2, i, i2, i3, i4);
                }
            });
        }
        updateView(this.y);
    }

    public /* synthetic */ void lambda$bindScrollableView$0$XStateBarView(View view, int i, int i2, int i3, int i4) {
        int i5 = 0;
        for (View view2 : this.scrollableView) {
            i5 = Math.max(view2.getScrollY(), i5);
        }
        this.y = i5;
        updateView(i5);
        LogUtil.d("XStateBarView", "scrollY: " + i2);
    }

    private synchronized void getBlurRadius(int i) {
        float f = i / 10.0f;
        if (f < 0.0f) {
            f = 0.0f;
        }
        if (f > 25.0f) {
            f = 25.0f;
        }
        this.radius = f;
        this.alpha = f * 0.03f;
    }

    public void updateView(int i) {
        getBlurRadius(i);
        this.alphaView.setAlpha(this.alpha);
        this.realtimeBlurView.setBlurRadius(this.radius);
        if (i == 0) {
            new Handler().postDelayed(new Runnable() { // from class: com.xiaopeng.xui.widget.-$$Lambda$XStateBarView$UhlTzgJQuzJpt0SV_T7iHNpuwQI
                @Override // java.lang.Runnable
                public final void run() {
                    XStateBarView.this.lambda$updateView$1$XStateBarView();
                }
            }, 10L);
        }
    }

    public /* synthetic */ void lambda$updateView$1$XStateBarView() {
        this.realtimeBlurView.invalidate();
    }
}

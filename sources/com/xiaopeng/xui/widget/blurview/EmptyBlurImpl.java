package com.xiaopeng.xui.widget.blurview;

import android.content.Context;
import android.graphics.Bitmap;
/* loaded from: classes2.dex */
public class EmptyBlurImpl implements BlurImpl {
    @Override // com.xiaopeng.xui.widget.blurview.BlurImpl
    public void blur(Bitmap bitmap, Bitmap bitmap2) {
    }

    @Override // com.xiaopeng.xui.widget.blurview.BlurImpl
    public boolean prepare(Context context, Bitmap bitmap, float f) {
        return false;
    }

    @Override // com.xiaopeng.xui.widget.blurview.BlurImpl
    public void release() {
    }
}

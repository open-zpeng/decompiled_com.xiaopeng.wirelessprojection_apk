package com.xiaopeng.xui.widget.blurview;

import android.content.Context;
import android.graphics.Bitmap;
/* loaded from: classes2.dex */
interface BlurImpl {
    void blur(Bitmap bitmap, Bitmap bitmap2);

    boolean prepare(Context context, Bitmap bitmap, float f);

    void release();
}

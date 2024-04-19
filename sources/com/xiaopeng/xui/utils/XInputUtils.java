package com.xiaopeng.xui.utils;

import android.view.View;
/* loaded from: classes2.dex */
public class XInputUtils {
    public static void ignoreHiddenInput(View view) {
        view.setTag(268435456, 1001);
    }
}

package com.xiaopeng.wirelessprojection.core.utils;

import com.xiaopeng.lib.utils.LogUtils;
import com.xiaopeng.wirelessprojection.core.BaseApp;
import com.xiaopeng.xui.app.XToast;
/* loaded from: classes2.dex */
public class ToastUtils {
    private static final String TAG = "ToastUtils";

    public static void showToast(int i) {
        showToast(BaseApp.getContext().getResources().getString(i));
    }

    public static void showToast(String str) {
        XToast.showShort(str);
        LogUtils.i(TAG, "showToast text=" + str);
    }

    public static void showToast(int i, int i2) {
        showToast(BaseApp.getContext().getResources().getString(i), i2);
    }

    public static void showToast(String str, int i) {
        XToast.showShort(str, i);
        LogUtils.i(TAG, "showToast text=" + str);
    }
}

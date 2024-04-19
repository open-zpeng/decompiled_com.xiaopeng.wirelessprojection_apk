package com.xiaopeng.wirelessprojection.core.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import com.xiaopeng.lib.utils.LogUtils;
import com.xiaopeng.wirelessprojection.core.BaseApp;
/* loaded from: classes2.dex */
public class ScreenUtils {
    private static final int SCREEN_ID_REAR = 0;
    private static final String TAG = "ScreenUtils";

    public static int getApiRouterUid() {
        return 0;
    }

    public static boolean isBackScreen(Context context) {
        return false;
    }

    public static boolean isMainScreen(Context context) {
        return true;
    }

    public static int getWindowWidth(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public static int getWindowHeight(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    public static boolean isCurrentScreen(Context context, int i) {
        return getScreenId(context) == i;
    }

    public static int getScreenId() {
        return getScreenId(BaseApp.getContext());
    }

    public static int getScreenId(Context context) {
        LogUtils.i(TAG, "getScreenId res=0");
        return 0;
    }
}

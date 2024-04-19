package com.xiaopeng.wirelessprojection.core.utils;

import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;
import com.xiaopeng.lib.utils.LogUtils;
/* loaded from: classes2.dex */
public class MediaLightUtils {
    private static final String TAG = "MediaLightUtils";
    private final int MAX_SYSTEM_BRIGHTNESS = 255;

    public static float getActivityBrightness(Activity activity) {
        float f = activity.getWindow().getAttributes().screenBrightness;
        LogUtils.i(TAG, "getActivityBrightness brightnessPercent=" + f);
        if (f == -1.0f) {
            return 0.0f;
        }
        return f;
    }

    public static void setActivityBrightness(float f, Activity activity) {
        Window window = activity.getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.screenBrightness = f;
        window.setAttributes(attributes);
    }
}

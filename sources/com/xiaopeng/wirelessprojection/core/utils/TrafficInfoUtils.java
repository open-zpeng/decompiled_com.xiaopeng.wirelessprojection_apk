package com.xiaopeng.wirelessprojection.core.utils;

import com.xiaopeng.lib.utils.LogUtils;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import org.json.JSONObject;
/* loaded from: classes2.dex */
public class TrafficInfoUtils {
    public static final String TAG = "TrafficInfoUtils";
    public static final String TRAFFIC_INFO_DEFAULT = "0.00";
    public static final String TRAFFIC_INFO_NO_DATA = "-.--";
    public static final int TRAFFIC_STATUS_ERROR = -1;
    public static final int TRAFFIC_STATUS_LOW = 1;
    public static final int TRAFFIC_STATUS_NORMAL = 0;
    public static final int TRAFFIC_STATUS_ZERO = 2;

    public static float getResultGB(float f) {
        return ((f / 1024.0f) / 1024.0f) / 1024.0f;
    }

    public static String getFormattedFromByteLong(long j) {
        return getFormattedFromByteFloat((float) j);
    }

    public static String getFormattedFromByteFloat(float f) {
        LogUtils.i(TAG, "getFormattedFromByteFloat result = " + f);
        DecimalFormat decimalFormat = new DecimalFormat(TRAFFIC_INFO_DEFAULT);
        if (f < 0.0f) {
            f = Math.abs(f);
        }
        decimalFormat.setRoundingMode(RoundingMode.FLOOR);
        String format = decimalFormat.format(getResultGB(f));
        return format.equals(TRAFFIC_INFO_DEFAULT) ? "0 " : format;
    }

    public static String getFormattedFromKB(String str) {
        try {
            JSONObject jSONObject = new JSONObject(str);
            return jSONObject.optBoolean("isSuccess", true) ? getFormattedFromByteFloat(Float.parseFloat(jSONObject.getString("resFlow")) * 1024.0f) : TRAFFIC_INFO_NO_DATA;
        } catch (Exception e) {
            e.printStackTrace();
            return TRAFFIC_INFO_NO_DATA;
        }
    }

    public static int remainFlowStatus(String str) {
        try {
            JSONObject jSONObject = new JSONObject(str);
            boolean z = jSONObject.getBoolean("isLogin");
            String string = jSONObject.getString("resFlow");
            boolean optBoolean = jSONObject.optBoolean("isSuccess", true);
            float resultGB = getResultGB(Float.parseFloat(string) * 1024.0f);
            LogUtils.i(TAG, "remainFlowStatus trafficGB = " + resultGB);
            if (z && optBoolean) {
                if (resultGB <= 0.0f) {
                    return 2;
                }
                return resultGB < 1.0f ? 1 : 0;
            }
            return -1;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}

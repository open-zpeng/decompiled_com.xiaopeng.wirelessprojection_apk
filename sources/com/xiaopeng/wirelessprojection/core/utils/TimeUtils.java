package com.xiaopeng.wirelessprojection.core.utils;

import android.text.TextUtils;
import java.util.Formatter;
import java.util.Locale;
/* loaded from: classes2.dex */
public class TimeUtils {
    public static String getStringTime(long j) {
        long j2 = j / 1000;
        return new Formatter(new StringBuilder(), Locale.US).format("%02d:%02d:%02d", Long.valueOf(j2 / 3600), Long.valueOf((j2 / 60) % 60), Long.valueOf(j2 % 60)).toString();
    }

    public static long getIntTime(String str) {
        if (TextUtils.isEmpty(str)) {
            return 0L;
        }
        String[] split = str.split(":");
        if (split.length < 3) {
            return 0L;
        }
        return ((Integer.parseInt(split[0]) * 3600) + (Integer.parseInt(split[1]) * 60) + Integer.parseInt(split[2])) * 1000;
    }

    public static long adjustValueBoundL(long j, long j2, long j3) {
        return j > j2 ? j2 : Math.max(j, j3);
    }

    public static float adjustValueBoundF(float f, float f2, float f3) {
        return f > f2 ? f2 : Math.max(f, f3);
    }
}

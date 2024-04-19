package com.xiaopeng.lib.utils;

import android.app.ActivityManager;
import android.content.Context;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes2.dex */
public class ContextUtils {
    public static boolean isTopActivity(Context context, Class cls) {
        List<ActivityManager.RunningTaskInfo> runningTasks = ((ActivityManager) context.getSystemService("activity")).getRunningTasks(1);
        return runningTasks != null && runningTasks.size() > 0 && runningTasks.get(0).topActivity.getClassName().equals(cls.getName());
    }

    public static boolean isServiceWorked(Context context, Class cls) {
        ArrayList arrayList = (ArrayList) ((ActivityManager) context.getSystemService("activity")).getRunningServices(100);
        for (int i = 0; i < arrayList.size(); i++) {
            if (((ActivityManager.RunningServiceInfo) arrayList.get(i)).service.getClassName().toString().equals(cls.getName())) {
                return true;
            }
        }
        return false;
    }
}

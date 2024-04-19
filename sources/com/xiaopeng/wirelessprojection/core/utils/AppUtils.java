package com.xiaopeng.wirelessprojection.core.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Process;
import android.text.TextUtils;
import com.xiaopeng.lib.utils.LogUtils;
import com.xiaopeng.wirelessprojection.core.BaseApp;
import com.xiaopeng.wirelessprojection.core.manager.ContentObserverManager;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
/* loaded from: classes2.dex */
public class AppUtils {
    private static final String TAG = "AppUtils";
    private static AtomicReference<String> sVersionName = new AtomicReference<>();

    public static String getCurrentProcessName(Context context) {
        String str;
        int myPid = Process.myPid();
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = ((ActivityManager) context.getSystemService("activity")).getRunningAppProcesses();
        if (runningAppProcesses != null && !runningAppProcesses.isEmpty()) {
            for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
                if (runningAppProcessInfo.pid == myPid) {
                    str = runningAppProcessInfo.processName;
                    break;
                }
            }
        }
        str = "";
        LogUtils.i(TAG, "getCurrentProcessName currentProcessName=" + str);
        return str;
    }

    public static String getCurrentRunningPackageName() {
        ComponentName currentRunningTask = getCurrentRunningTask();
        if (currentRunningTask == null) {
            return null;
        }
        return currentRunningTask.getPackageName();
    }

    public static ComponentName getCurrentRunningTask() {
        List<ActivityManager.RunningTaskInfo> runningTasks;
        ActivityManager.RunningTaskInfo runningTaskInfo;
        ActivityManager activityManager = (ActivityManager) BaseApp.getContext().getSystemService("activity");
        if (activityManager == null || (runningTasks = activityManager.getRunningTasks(1)) == null || runningTasks.isEmpty() || (runningTaskInfo = runningTasks.get(0)) == null) {
            return null;
        }
        return runningTaskInfo.topActivity;
    }

    public static boolean isAppOnForeground() {
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = ((ActivityManager) BaseApp.getContext().getSystemService("activity")).getRunningAppProcesses();
        if (runningAppProcesses == null) {
            return false;
        }
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
            if (runningAppProcessInfo.processName.equals(getCurrentProcessName(BaseApp.getContext())) && runningAppProcessInfo.importance == 100) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAllowUseApp() {
        return (ScreenUtils.isMainScreen(BaseApp.getContext()) && ContentObserverManager.instance().isXpengLabLimitOn() && CarHardwareHelper.instance().isRDNLevel()) ? false : true;
    }

    public static String getCurrentPackageFromXui(Context context) {
        String currentProcessName = getCurrentProcessName(context);
        LogUtils.i(TAG, "getCurrentPackageFromXui res=" + currentProcessName);
        return currentProcessName;
    }

    public static String getVersionName(Context context) {
        String str = sVersionName.get();
        if (TextUtils.isEmpty(str)) {
            PackageInfo packageInfo = null;
            try {
                packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            } catch (Throwable th) {
                th.printStackTrace();
            }
            if (packageInfo != null) {
                str = packageInfo.versionName;
                if (!TextUtils.isEmpty(str)) {
                    sVersionName.set(str);
                }
            }
            return str;
        }
        return str;
    }

    public static int getCurrentUid() {
        int userId = BaseApp.getContext().getUserId();
        LogUtils.i(TAG, "getCurrentUid uid=" + userId);
        return userId;
    }
}

package com.xiaopeng.wirelessprojection.core;

import android.app.Application;
import android.content.Context;
/* loaded from: classes2.dex */
public class BaseApp extends Application {
    private static BaseApp sApplication = null;
    public static boolean sInit = false;

    @Override // android.app.Application
    public void onCreate() {
        sApplication = this;
        super.onCreate();
    }

    protected boolean isAppProcess(String str) {
        String packageName = getPackageName();
        return packageName != null && packageName.equals(str);
    }

    public static Context getContext() {
        return sApplication.getApplicationContext();
    }

    public static boolean isInit() {
        return sInit;
    }
}

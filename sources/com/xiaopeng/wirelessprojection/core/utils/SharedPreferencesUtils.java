package com.xiaopeng.wirelessprojection.core.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import com.xiaopeng.lib.utils.LogUtils;
import com.xiaopeng.wirelessprojection.core.BaseApp;
import java.util.Map;
/* loaded from: classes2.dex */
public class SharedPreferencesUtils {
    public static final String DEFAULT_HOTSPOT_NAME = "小鹏X9–123456";
    public static final String DEFAULT_HOTSPOT_PASSWORD = "mm810975";
    public static final boolean DEFAULT_RESTORE_BTN_SHOWING = false;
    public static final String KEY_HOTSPOT_NAME = "key_hotspot_name";
    public static final String KEY_HOTSPOT_PASSWORD = "key_hotspot_password";
    public static final String KEY_RESTORE_BTN_SHOWING = "key_restore_showing";
    private static final String SP_NAME = "shared_pref";
    private static final String TAG = "SharedPreferencesUtils";
    private static volatile SharedPreferencesUtils instance;
    private SharedPreferences.Editor editor;
    private SharedPreferences sp;

    public static void setRestoreButtonShowing(boolean z) {
    }

    private SharedPreferencesUtils(Context context, String str, int i) {
        str = TextUtils.isEmpty(str) ? context.getPackageName() : str;
        str = TextUtils.isEmpty(str) ? SP_NAME : str;
        Log.v(TAG, "spName=" + str);
        SharedPreferences sharedPreferences = context.getSharedPreferences(str, 0);
        this.sp = sharedPreferences;
        SharedPreferences.Editor edit = sharedPreferences.edit();
        this.editor = edit;
        edit.apply();
    }

    private static void init(Context context) {
        init(context, null);
    }

    private static void init(Context context, String str) {
        if (instance == null) {
            synchronized (SharedPreferencesUtils.class) {
                if (instance == null) {
                    instance = new SharedPreferencesUtils(context.getApplicationContext(), str, 0);
                }
            }
        }
    }

    public static SharedPreferencesUtils getInstance(Context context) {
        init(context);
        return instance;
    }

    public void putString(String str, String str2) {
        this.editor.putString(str, str2).apply();
    }

    public String getString(String str) {
        return getString(str, null);
    }

    public String getString(String str, String str2) {
        try {
            return this.sp.getString(str, str2);
        } catch (Exception e) {
            LogUtils.e(TAG, "getString:" + e.getLocalizedMessage());
            return str2;
        }
    }

    public void putBoolean(String str, boolean z) {
        this.editor.putBoolean(str, z).apply();
    }

    public boolean getBoolean(String str) {
        return getBoolean(str, false);
    }

    public boolean getBoolean(String str, boolean z) {
        try {
            return this.sp.getBoolean(str, z);
        } catch (Exception e) {
            LogUtils.e(TAG, "getBoolean:" + e.getLocalizedMessage());
            return z;
        }
    }

    public Map<String, ?> getAll() {
        return this.sp.getAll();
    }

    public void remove(String str) {
        this.editor.remove(str).apply();
    }

    public boolean contains(String str) {
        return this.sp.contains(str);
    }

    public void clear() {
        this.editor.clear().apply();
    }

    public static void setHotspotName(String str) {
        getInstance(BaseApp.getContext()).putString(KEY_HOTSPOT_NAME, str);
    }

    public static String getHotspotName() {
        return getInstance(BaseApp.getContext()).getString(KEY_HOTSPOT_NAME, DEFAULT_HOTSPOT_NAME);
    }

    public static void setHotspotPassword(String str) {
        getInstance(BaseApp.getContext()).putString(KEY_HOTSPOT_PASSWORD, str);
    }

    public static String getHotspotPassword() {
        return getInstance(BaseApp.getContext()).getString(KEY_HOTSPOT_PASSWORD, DEFAULT_HOTSPOT_PASSWORD);
    }

    public static boolean getRestoreButtonShowing() {
        return getInstance(BaseApp.getContext()).getBoolean(KEY_RESTORE_BTN_SHOWING, false);
    }
}

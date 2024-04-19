package com.xiaopeng.wirelessprojection.core.manager;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import com.xiaopeng.lib.utils.LogUtils;
import com.xiaopeng.lib.utils.ThreadUtils;
import com.xiaopeng.wirelessprojection.core.BaseApp;
import com.xiaopeng.wirelessprojection.core.event.ConnectedClientChangeEvent;
import com.xiaopeng.wirelessprojection.core.event.XpengLabSwitchChangeEvent;
import com.xiaopeng.wirelessprojection.core.utils.EventBusUtils;
import com.xiaopeng.wirelessprojection.core.utils.HotspotUtils;
/* loaded from: classes2.dex */
public class ContentObserverManager {
    public static final String KEY_HOTSPOT_NAME_PASS = "hotspot_name_and_pass";
    public static final String KEY_TETHER_CLIENT_LISTS = "tether_client_lists";
    public static final String KEY_XPENG_LAB_GEAR_LIMIT = "isAppUsedLimitEnable";
    private static final String TAG = "ContentObserverManager";
    public static final int XPENG_LAB_GEAR_LIMIT_ON = 1;

    /* loaded from: classes2.dex */
    private static class Holder {
        static final ContentObserverManager INSTANCE = new ContentObserverManager();

        private Holder() {
        }
    }

    public static ContentObserverManager instance() {
        return Holder.INSTANCE;
    }

    public void init() {
        LogUtils.i(TAG, "init");
        ThreadUtils.postMainThread(new Runnable() { // from class: com.xiaopeng.wirelessprojection.core.manager.-$$Lambda$ContentObserverManager$LjhuM9YJXxQn5RrEFjhcPUj0jhc
            @Override // java.lang.Runnable
            public final void run() {
                ContentObserverManager.this.lambda$init$0$ContentObserverManager();
            }
        });
    }

    public /* synthetic */ void lambda$init$0$ContentObserverManager() {
        registerConnectedClientObserver();
        registerXpengLabObserver();
        registerHotspotObserver();
    }

    private void registerConnectedClientObserver() {
        LogUtils.i(TAG, "registerConnectedClientObserver");
        BaseApp.getContext().getContentResolver().registerContentObserver(Settings.Global.getUriFor(KEY_TETHER_CLIENT_LISTS), false, new ContentObserver(new Handler()) { // from class: com.xiaopeng.wirelessprojection.core.manager.ContentObserverManager.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z, Uri uri) {
                super.onChange(z, uri);
                LogUtils.i(ContentObserverManager.TAG, "onChange uri=" + uri);
                EventBusUtils.post(new ConnectedClientChangeEvent());
            }
        });
    }

    private void registerXpengLabObserver() {
        LogUtils.i(TAG, "registerXpengLabObserver");
        BaseApp.getContext().getContentResolver().registerContentObserver(Settings.System.getUriFor(KEY_XPENG_LAB_GEAR_LIMIT), false, new ContentObserver(new Handler()) { // from class: com.xiaopeng.wirelessprojection.core.manager.ContentObserverManager.2
            @Override // android.database.ContentObserver
            public void onChange(boolean z, Uri uri) {
                super.onChange(z, uri);
                LogUtils.i(ContentObserverManager.TAG, "onChange uri=" + uri);
                EventBusUtils.post(new XpengLabSwitchChangeEvent());
            }
        });
    }

    private void registerHotspotObserver() {
        LogUtils.i(TAG, "registerHotspotObserver");
        BaseApp.getContext().getContentResolver().registerContentObserver(Settings.Global.getUriFor(KEY_HOTSPOT_NAME_PASS), false, new ContentObserver(new Handler()) { // from class: com.xiaopeng.wirelessprojection.core.manager.ContentObserverManager.3
            @Override // android.database.ContentObserver
            public void onChange(boolean z, Uri uri) {
                super.onChange(z, uri);
                LogUtils.i(ContentObserverManager.TAG, "onChange uri=" + uri);
                HotspotUtils.handleHotspotInfoChange();
            }
        });
    }

    public String getSettingsGlobalString(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        String string = Settings.Global.getString(BaseApp.getContext().getContentResolver(), str);
        LogUtils.i(TAG, "getSettingsGlobalString key=" + str + ", res=" + string);
        return string == null ? "" : string;
    }

    public void putSettingsGlobalString(String str, String str2) {
        LogUtils.i(TAG, "putSettingsGlobalString key=" + str + ", value=" + str2);
        if (TextUtils.isEmpty(str) && TextUtils.isEmpty(str2)) {
            return;
        }
        Settings.Global.putString(BaseApp.getContext().getContentResolver(), str, str2);
    }

    public int getSettingsSystemInt(String str) {
        int i = 0;
        if (!TextUtils.isEmpty(str)) {
            try {
                i = Settings.System.getInt(BaseApp.getContext().getContentResolver(), str);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            LogUtils.i(TAG, "getSettingsSystemInt key=" + str + ", res=" + i);
        }
        return i;
    }

    public boolean isXpengLabLimitOn() {
        int settingsSystemInt = getSettingsSystemInt(KEY_XPENG_LAB_GEAR_LIMIT);
        boolean z = settingsSystemInt == 1;
        LogUtils.i(TAG, "isXpengLabLimitOn state=" + settingsSystemInt + ", res=" + z);
        return z;
    }
}

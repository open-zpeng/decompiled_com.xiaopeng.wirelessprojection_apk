package com.xiaopeng.wirelessprojection.core.utils;

import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import com.android.dx.stock.ProxyBuilder;
import com.xiaopeng.lib.utils.LogUtils;
import com.xiaopeng.wirelessprojection.core.BaseApp;
import com.xiaopeng.wirelessprojection.core.event.HotspotInfoChangeEvent;
import com.xiaopeng.wirelessprojection.core.manager.ContentObserverManager;
import com.xiaopeng.wirelessprojection.core.manager.ProtocolManager;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
/* loaded from: classes2.dex */
public class HotspotUtils {
    public static final String HOST_NAME_LIST_EMPTY = "no devices";
    public static final String HOST_NAME_UNKNOWN = "unknown";
    private static final String HOTSPOT_DEFAULT_NAME_PRE = "小鹏P5–";
    private static final String HOTSPOT_DEFAULT_SECURITY = "WPA2_PSK";
    private static final int HOTSPOT_OPEN_DELAY = 500;
    private static final String KEY_HOST_NAME = "net.HOST_NAME";
    public static final int PLAYBACK_STATE_STOP = 3;
    private static final String TAG = "HotspotUtils";
    private static final String TEST_CLIENTS = "Miss的IPhone%小李的IPhone%大胖的IPhone%IPhone%大表哥的IPhone%其他的手机3";
    public static final String WIFI_AP_STATE_CHANGED_ACTION = "android.net.wifi.WIFI_AP_STATE_CHANGED";
    private static String hotspotName = null;
    private static String hotspotPassword = null;
    private static String mHostNames = "";
    private static final WifiManager mWifiManager = (WifiManager) BaseApp.getContext().getSystemService("wifi");
    private static final ConnectivityManager mConnectivityManager = (ConnectivityManager) BaseApp.getContext().getSystemService(ConnectivityManager.class);

    static {
        String[] split = ContentObserverManager.instance().getSettingsGlobalString(ContentObserverManager.KEY_HOTSPOT_NAME_PASS).split("\\|");
        if (split.length < 2) {
            refreshNameAndPass();
            return;
        }
        hotspotName = split[0];
        hotspotPassword = split[1];
    }

    private HotspotUtils() {
    }

    public static String getHotspotPassword() {
        return hotspotPassword;
    }

    public static String refreshHotspotPassword() {
        hotspotPassword = generatePassword();
        ContentObserverManager.instance().putSettingsGlobalString(ContentObserverManager.KEY_HOTSPOT_NAME_PASS, hotspotName + "|" + hotspotPassword);
        return hotspotPassword;
    }

    public static String getHotspotName() {
        return hotspotName;
    }

    private static String generatePassword() {
        return RandomUtils.getRandomNumbers(8);
    }

    private static String generateHotspotName() {
        String str = HOTSPOT_DEFAULT_NAME_PRE + RandomUtils.getRandomNumbers(6);
        hotspotName = str;
        return str;
    }

    private static void refreshNameAndPass() {
        generateHotspotName();
        refreshHotspotPassword();
    }

    public static void handleHotspotInfoChange() {
        String[] split = ContentObserverManager.instance().getSettingsGlobalString(ContentObserverManager.KEY_HOTSPOT_NAME_PASS).split("\\|");
        if (split.length < 2) {
            LogUtils.i(TAG, "illegal nameAndPass");
            refreshNameAndPass();
        } else {
            hotspotName = split[0];
            hotspotPassword = split[1];
        }
        Log.i(TAG, "handleHotspotInfoChange: hotspotName is:" + hotspotName + "password: " + hotspotPassword);
        EventBusUtils.post(new HotspotInfoChangeEvent(hotspotName, hotspotPassword));
    }

    public static boolean openHotspot(final boolean z) {
        closeHotspot();
        if (z) {
            refreshHotspotPassword();
        }
        ThreadUtils.postBackground(new Runnable() { // from class: com.xiaopeng.wirelessprojection.core.utils.-$$Lambda$HotspotUtils$4dxtmM2r8H-LxT4aZKAs3vOWp0I
            @Override // java.lang.Runnable
            public final void run() {
                LogUtils.i(HotspotUtils.TAG, "openHotspot isNew=" + z + ", success=" + HotspotUtils.openHotspot(HotspotUtils.hotspotName, HotspotUtils.hotspotPassword, HotspotUtils.HOTSPOT_DEFAULT_SECURITY));
            }
        }, 500L);
        return true;
    }

    public static boolean needOpenHotspot() {
        boolean z = (isHotspotStartByMe() && isHotspotOpen()) ? false : true;
        LogUtils.i(TAG, "needOpenHotspot res=" + z);
        return z;
    }

    private static boolean openHotspot(String str, String str2, String str3) {
        LogUtils.i(TAG, "openHotspot name=" + str + ", ps=" + str2 + ", security=" + str3);
        try {
            WifiConfiguration wifiConfiguration = new WifiConfiguration();
            wifiConfiguration.allowedKeyManagement.clear();
            wifiConfiguration.SSID = str;
            wifiConfiguration.allowedAuthAlgorithms.set(0);
            wifiConfiguration.hiddenSSID = false;
            Field declaredField = wifiConfiguration.getClass().getDeclaredField("apBand");
            Field declaredField2 = wifiConfiguration.getClass().getDeclaredField("AP_BAND_5GHZ");
            LogUtils.i(TAG, "openHotspot fieldApBand=" + declaredField.get(wifiConfiguration) + ", field5G=" + declaredField2.get(wifiConfiguration));
            declaredField.set(wifiConfiguration, declaredField2.get(wifiConfiguration));
            char c = 65535;
            int hashCode = str3.hashCode();
            if (hashCode != 2402104) {
                if (hashCode == 1196474771 && str3.equals(HOTSPOT_DEFAULT_SECURITY)) {
                    c = 1;
                }
            } else if (str3.equals("NONE")) {
                c = 0;
            }
            if (c == 0) {
                wifiConfiguration.allowedKeyManagement.set(0);
                wifiConfiguration.preSharedKey = "";
            } else if (c == 1) {
                wifiConfiguration.allowedKeyManagement.set(4);
                wifiConfiguration.preSharedKey = str2;
            }
            wifiConfiguration.allowedGroupCiphers.set(2);
            wifiConfiguration.allowedPairwiseCiphers.set(1);
            wifiConfiguration.allowedGroupCiphers.set(3);
            wifiConfiguration.allowedPairwiseCiphers.set(2);
            wifiConfiguration.allowedPairwiseCiphers.set(2);
            WifiManager wifiManager = mWifiManager;
            ((Boolean) wifiManager.getClass().getMethod("setWifiApConfiguration", WifiConfiguration.class).invoke(wifiManager, wifiConfiguration)).booleanValue();
            LogUtils.i(TAG, "setWifiApConfiguration success");
            try {
                Object build = ProxyBuilder.forClass(OnStartTetheringCallbackClass()).dexCache(BaseApp.getContext().getCodeCacheDir()).handler(new InvocationHandler() { // from class: com.xiaopeng.wirelessprojection.core.utils.HotspotUtils.1
                    @Override // java.lang.reflect.InvocationHandler
                    public Object invoke(Object obj, Method method, Object[] objArr) throws Throwable {
                        String name = method.getName();
                        name.hashCode();
                        if (name.equals("onTetheringStarted")) {
                            LogUtils.d(HotspotUtils.TAG, "onTetheringStarted");
                            return null;
                        } else if (name.equals("onTetheringFailed")) {
                            LogUtils.d(HotspotUtils.TAG, "onTetheringFailed");
                            return null;
                        } else {
                            ProxyBuilder.callSuper(obj, method, objArr);
                            return null;
                        }
                    }
                }).build();
                try {
                    ConnectivityManager connectivityManager = mConnectivityManager;
                    Method declaredMethod = connectivityManager.getClass().getDeclaredMethod("startTethering", Integer.TYPE, Boolean.TYPE, OnStartTetheringCallbackClass(), Handler.class);
                    if (declaredMethod == null) {
                        LogUtils.e(TAG, "startTetheringMethod is null");
                    } else {
                        declaredMethod.invoke(connectivityManager, 0, false, build, null);
                        LogUtils.d(TAG, "startTethering success");
                    }
                    return true;
                } catch (Exception e) {
                    LogUtils.e(TAG, "Error in enableTethering:" + e.toString());
                    return false;
                }
            } catch (Exception e2) {
                LogUtils.e(TAG, "dexmaker实现预加载Error:" + e2.toString());
                return false;
            }
        } catch (InvocationTargetException e3) {
            LogUtils.e(TAG, "Error in configuration:" + e3.getTargetException().toString());
            return false;
        } catch (Exception e4) {
            LogUtils.e(TAG, "Error in configuration:" + e4.toString());
            return false;
        }
    }

    public static boolean closeHotspot() {
        ConnectivityManager connectivityManager;
        Method declaredMethod;
        LogUtils.i(TAG, "closeHotspot");
        ProtocolManager.instance().setVideoPlaybackState(3);
        try {
            connectivityManager = mConnectivityManager;
            declaredMethod = connectivityManager.getClass().getDeclaredMethod("stopTethering", Integer.TYPE);
        } catch (Exception e) {
            LogUtils.e(TAG, "stopTethering error: " + e.toString());
        }
        if (declaredMethod == null) {
            LogUtils.e(TAG, "stopTethering Method is null");
            return false;
        }
        declaredMethod.invoke(connectivityManager, 0);
        LogUtils.d(TAG, "stopTethering success");
        return true;
    }

    public static boolean isHotspotOpen() {
        try {
            WifiManager wifiManager = mWifiManager;
            int intValue = ((Integer) wifiManager.getClass().getDeclaredMethod("getWifiApState", new Class[0]).invoke(wifiManager, new Object[0])).intValue();
            int intValue2 = ((Integer) wifiManager.getClass().getDeclaredField("WIFI_AP_STATE_ENABLED").get(wifiManager)).intValue();
            LogUtils.i(TAG, "isHotspotOpen state=%s, WIFI_AP_STATE_ENABLED=%s", Integer.valueOf(intValue), Integer.valueOf(intValue2));
            return intValue == intValue2;
        } catch (Exception e) {
            LogUtils.e(TAG, "isHotspotOpen error: " + e.toString());
            return false;
        }
    }

    public static boolean isHotspotStartByMe() {
        WifiConfiguration wifiApConfiguration = getWifiApConfiguration();
        return wifiApConfiguration != null && hotspotName.equals(wifiApConfiguration.SSID) && hotspotPassword.equals(wifiApConfiguration.preSharedKey);
    }

    public static WifiConfiguration getWifiApConfiguration() {
        try {
            WifiManager wifiManager = mWifiManager;
            return (WifiConfiguration) wifiManager.getClass().getDeclaredMethod("getWifiApConfiguration", new Class[0]).invoke(wifiManager, new Object[0]);
        } catch (Exception e) {
            LogUtils.e(TAG, "getWifiApConfiguration error: " + e.toString());
            return null;
        }
    }

    public static boolean isValidHotspotState() {
        try {
            WifiManager wifiManager = mWifiManager;
            int intValue = ((Integer) wifiManager.getClass().getDeclaredMethod("getWifiApState", new Class[0]).invoke(wifiManager, new Object[0])).intValue();
            Field declaredField = wifiManager.getClass().getDeclaredField("WIFI_AP_STATE_ENABLED");
            Field declaredField2 = wifiManager.getClass().getDeclaredField("WIFI_AP_STATE_DISABLED");
            int intValue2 = ((Integer) declaredField.get(wifiManager)).intValue();
            int intValue3 = ((Integer) declaredField2.get(wifiManager)).intValue();
            LogUtils.i(TAG, "isValidHotspotState state=%s", Integer.valueOf(intValue));
            return intValue == intValue2 || intValue == intValue3;
        } catch (Exception e) {
            LogUtils.e(TAG, "isHotspotOpen error: " + e.toString());
            return false;
        }
    }

    public static String[] getConnectedClients() {
        LogUtils.i(TAG, "getConnectedClients mHostNames=" + mHostNames);
        return TextUtils.isEmpty(mHostNames) ? new String[0] : mHostNames.split("%");
    }

    public static void resetLocalConnectedClients() {
        LogUtils.i(TAG, "resetLocalConnectedClients mHostNames=" + mHostNames);
        mHostNames = "";
    }

    public static boolean hasClientConnecting() {
        boolean z = getConnectedClients().length > 0;
        LogUtils.i(TAG, "hasClientConnecting res=" + z);
        return z;
    }

    public static boolean refreshConnectedClients() {
        String settingsGlobalString = ContentObserverManager.instance().getSettingsGlobalString(ContentObserverManager.KEY_TETHER_CLIENT_LISTS);
        LogUtils.i(TAG, "refreshConnectedClients hostNames=" + settingsGlobalString + ", mHostNames=" + mHostNames);
        if (settingsGlobalString == null) {
            settingsGlobalString = "";
        }
        boolean z = !settingsGlobalString.equals(mHostNames);
        mHostNames = settingsGlobalString;
        return z;
    }

    private static Class OnStartTetheringCallbackClass() {
        try {
            return Class.forName("android.net.ConnectivityManager$OnStartTetheringCallback");
        } catch (ClassNotFoundException e) {
            LogUtils.e(TAG, "OnStartTetheringCallbackClass error: " + e.toString());
            return null;
        }
    }

    private static String getProperty(String str, String str2) {
        try {
            try {
                Class<?> cls = Class.forName("android.os.SystemProperties");
                return (String) cls.getMethod("get", String.class, String.class).invoke(cls, str, str2);
            } catch (Exception e) {
                e.printStackTrace();
                return str2;
            }
        } catch (Throwable unused) {
            return str2;
        }
    }
}

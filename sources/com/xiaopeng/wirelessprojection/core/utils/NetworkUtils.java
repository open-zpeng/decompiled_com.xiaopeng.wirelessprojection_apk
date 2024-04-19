package com.xiaopeng.wirelessprojection.core.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
/* loaded from: classes2.dex */
public class NetworkUtils {
    private static final String NETWORK_TYPE_MOBILE = "Mobile EDGE>";
    private static final String NETWORK_TYPE_OTHERS = "Others>";
    private static final String NETWORK_TYPE_WIFI = "WiFi";
    private static final String UNKNOWN = "<unknown>";
    private static final String WIFI_DISABLED = "热点已关闭";
    private static final String WIFI_NO_CONNECT = "无连接";
    private static final String WIFI_NO_PERMISSION = "无网络权限";

    public static String getWiFiInfoSSID(Context context) {
        WifiManager wifiManager = (WifiManager) getSystemService(context, "wifi");
        if (wifiManager.isWifiEnabled()) {
            WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            return connectionInfo == null ? WIFI_NO_CONNECT : connectionInfo.getSSID().replaceAll("\"", "");
        }
        return WIFI_DISABLED;
    }

    public static String getWiFiInfoIPAddress(Context context) {
        int ipAddress;
        WifiManager wifiManager = (WifiManager) getSystemService(context, "wifi");
        if (wifiManager == null || !wifiManager.isWifiEnabled()) {
            return "";
        }
        WifiInfo connectionInfo = wifiManager.getConnectionInfo();
        return (connectionInfo == null || (ipAddress = connectionInfo.getIpAddress()) == 0) ? UNKNOWN : (ipAddress & 255) + "." + ((ipAddress >> 8) & 255) + "." + ((ipAddress >> 16) & 255) + "." + ((ipAddress >> 24) & 255);
    }

    public static String getActiveNetworkInfo(Context context) {
        NetworkInfo activeNetworkInfo;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(context, "connectivity");
        return (connectivityManager == null || (activeNetworkInfo = connectivityManager.getActiveNetworkInfo()) == null) ? UNKNOWN : activeNetworkInfo.getType() == 1 ? NETWORK_TYPE_WIFI : activeNetworkInfo.getType() == 0 ? NETWORK_TYPE_MOBILE : NETWORK_TYPE_OTHERS;
    }

    private static <T> T getSystemService(Context context, String str) {
        return (T) context.getApplicationContext().getSystemService(str);
    }
}

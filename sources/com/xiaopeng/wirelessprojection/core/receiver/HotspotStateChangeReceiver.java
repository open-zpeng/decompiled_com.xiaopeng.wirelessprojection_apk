package com.xiaopeng.wirelessprojection.core.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.xiaopeng.lib.utils.LogUtils;
import com.xiaopeng.wirelessprojection.core.event.HotspotChangeEvent;
import com.xiaopeng.wirelessprojection.core.utils.EventBusUtils;
import com.xiaopeng.wirelessprojection.core.utils.HotspotUtils;
/* loaded from: classes2.dex */
public class HotspotStateChangeReceiver extends BroadcastReceiver {
    public static final String ACTION_HOTSPOT_CHANGE = "android.net.wifi.WIFI_AP_STATE_CHANGED";
    private boolean mRegistered;

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        LogUtils.i("HotspotStateChangeReceiver", "onReceive intent=" + intent);
        if (HotspotUtils.isValidHotspotState()) {
            EventBusUtils.post(new HotspotChangeEvent());
        }
    }

    public void registerReceiver(Context context) {
        if (this.mRegistered) {
            return;
        }
        context.registerReceiver(this, new IntentFilter("android.net.wifi.WIFI_AP_STATE_CHANGED"));
        this.mRegistered = true;
    }

    public void unregisterReceiver(Context context) {
        if (this.mRegistered) {
            context.unregisterReceiver(this);
            this.mRegistered = false;
        }
    }
}

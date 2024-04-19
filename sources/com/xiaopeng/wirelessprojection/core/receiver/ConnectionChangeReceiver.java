package com.xiaopeng.wirelessprojection.core.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.xiaopeng.lib.utils.LogUtils;
import com.xiaopeng.lib.utils.NetUtils;
import com.xiaopeng.wirelessprojection.core.event.ConnectionChangeEvent;
import com.xiaopeng.wirelessprojection.core.utils.EventBusUtils;
/* loaded from: classes2.dex */
public class ConnectionChangeReceiver extends BroadcastReceiver {
    public static final String ACTION_CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";
    public static boolean sConnected;
    private boolean mRegistered;

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        boolean z = !NetUtils.isNetworkAvailable(context);
        sConnected = !z;
        LogUtils.i("ConnectionChangeReceiver", "onReceive:noConnectivity=" + z + ",intent=" + intent);
        EventBusUtils.post(new ConnectionChangeEvent(!z));
    }

    public void registerReceiver(Context context) {
        if (this.mRegistered) {
            return;
        }
        context.registerReceiver(this, new IntentFilter(ACTION_CONNECTIVITY_CHANGE));
        this.mRegistered = true;
    }

    public void unregisterReceiver(Context context) {
        if (this.mRegistered) {
            context.unregisterReceiver(this);
            this.mRegistered = false;
        }
    }
}

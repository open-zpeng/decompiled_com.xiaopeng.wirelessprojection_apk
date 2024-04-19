package com.xiaopeng.wirelessprojection.core.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.xiaopeng.lib.utils.LogUtils;
import com.xiaopeng.lib.utils.NetUtils;
import com.xiaopeng.wirelessprojection.core.event.TrafficStatusChangeEvent;
import com.xiaopeng.wirelessprojection.core.utils.EventBusUtils;
/* loaded from: classes2.dex */
public class TrafficStatusChangeReceiver extends BroadcastReceiver {
    private static final String TAG = "TrafficStatusChangeReceiver";
    public static final int TRAFFIC_STATUS_AVAILABLE = 1;
    public static final int TRAFFIC_STATUS_RUN_OUT = 0;
    private boolean mRegistered;

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String stringExtra = intent.getStringExtra(NetUtils.TRAFFIC_STATUS_TYPE_KEY);
        LogUtils.i(TAG, "onReceive status=" + stringExtra);
        try {
            EventBusUtils.post(new TrafficStatusChangeEvent(Integer.parseInt(stringExtra) == 1 ? 0 : 1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerReceiver(Context context) {
        if (this.mRegistered) {
            return;
        }
        context.registerReceiver(this, new IntentFilter(NetUtils.TRAFFIC_STATUS_CHAGNE_ACTION));
        this.mRegistered = true;
    }

    public void unregisterReceiver(Context context) {
        if (this.mRegistered) {
            context.unregisterReceiver(this);
            this.mRegistered = false;
        }
    }
}

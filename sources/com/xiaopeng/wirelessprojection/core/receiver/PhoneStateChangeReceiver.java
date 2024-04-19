package com.xiaopeng.wirelessprojection.core.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.xiaopeng.lib.utils.LogUtils;
import com.xiaopeng.wirelessprojection.core.manager.PhoneStatusManager;
/* loaded from: classes2.dex */
public class PhoneStateChangeReceiver extends BroadcastReceiver {
    public static final String ACTION_PHONE_STATE_CHANGE = "android.bluetooth.headsetclient.profile.action.AG_CALL_CHANGED";
    private static final String TAG = "PhoneStateChangeReceiver";
    private boolean mRegistered;

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        LogUtils.d(TAG, " action = BluetoothHeadsetClient.ACTION_CALL_CHANGED");
        PhoneStatusManager.instance().handlePhoneStateChange(intent.getParcelableExtra("android.bluetooth.headsetclient.extra.CALL"));
    }

    public void registerReceiver(Context context) {
        if (this.mRegistered) {
            return;
        }
        context.registerReceiver(this, new IntentFilter(ACTION_PHONE_STATE_CHANGE));
        this.mRegistered = true;
    }

    public void unregisterReceiver(Context context) {
        if (this.mRegistered) {
            context.unregisterReceiver(this);
            this.mRegistered = false;
        }
    }
}

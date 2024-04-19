package com.xiaopeng.wirelessprojection.core.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.xiaopeng.lib.utils.LogUtils;
import com.xiaopeng.wirelessprojection.core.BaseApp;
import com.xiaopeng.wirelessprojection.core.event.VideoRestoreEnableEvent;
import com.xiaopeng.wirelessprojection.core.utils.EventBusUtils;
import com.xiaopeng.wirelessprojection.core.utils.ScreenUtils;
import com.xiaopeng.wirelessprojection.core.utils.SharedPreferencesUtils;
/* loaded from: classes2.dex */
public class VideoRestoreStatusReceiver extends BroadcastReceiver {
    public static final String ACTION_RESTORE_STATUS = "com.xiaopeng.airplay.AIRPLAY_STATE_CHANGED";
    public static final String KEY_SCREEN_ID = "screen_id";
    public static final String KEY_STATUS = "state";
    public static final int RESTORE_STATUS_AVAILABLE = 2;
    private static final String TAG = "VideoRestoreStatusReceiver";
    private boolean mRegistered;

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        LogUtils.i(TAG, "onReceive intent=" + intent);
        if (ACTION_RESTORE_STATUS.equals(intent.getAction())) {
            int intExtra = intent.getIntExtra(KEY_STATUS, 0);
            int intExtra2 = intent.getIntExtra(KEY_SCREEN_ID, 0);
            LogUtils.i(TAG, "onReceive state=" + intExtra + ", screenId=" + intExtra2);
            boolean z = 2 == intExtra;
            if (ScreenUtils.isCurrentScreen(BaseApp.getContext(), intExtra2)) {
                SharedPreferencesUtils.setRestoreButtonShowing(z);
                EventBusUtils.post(new VideoRestoreEnableEvent(z));
            }
        }
    }

    public void registerReceiver(Context context) {
        if (this.mRegistered) {
            return;
        }
        context.registerReceiver(this, new IntentFilter(ACTION_RESTORE_STATUS));
        this.mRegistered = true;
    }

    public void unregisterReceiver(Context context) {
        if (this.mRegistered) {
            context.unregisterReceiver(this);
            this.mRegistered = false;
        }
    }
}

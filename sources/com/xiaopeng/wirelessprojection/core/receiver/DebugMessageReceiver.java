package com.xiaopeng.wirelessprojection.core.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.xiaopeng.lib.utils.LogUtils;
import com.xiaopeng.wirelessprojection.core.event.DebugVideoPlayEvent;
import com.xiaopeng.wirelessprojection.core.utils.EventBusUtils;
/* loaded from: classes2.dex */
public class DebugMessageReceiver extends BroadcastReceiver {
    public static final String ACTION_TEST_PLAY_VIDEO = "wirelessprojection.ACTION_TEST_PLAY";
    private static final String TAG = "DebugMessageReceiver";

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        LogUtils.i(TAG, "onReceive intent=" + intent);
        if (ACTION_TEST_PLAY_VIDEO.equals(intent.getAction())) {
            EventBusUtils.post(new DebugVideoPlayEvent());
        }
    }
}

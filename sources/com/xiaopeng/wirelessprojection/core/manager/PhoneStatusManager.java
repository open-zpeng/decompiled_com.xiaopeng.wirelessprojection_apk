package com.xiaopeng.wirelessprojection.core.manager;

import android.bluetooth.BluetoothHeadsetClientCall;
import com.xiaopeng.lib.utils.LogUtils;
import com.xiaopeng.wirelessprojection.core.event.PhoneCallStateChangeEvent;
import com.xiaopeng.wirelessprojection.core.utils.EventBusUtils;
/* loaded from: classes2.dex */
public class PhoneStatusManager {
    public static final int CALL_STATE_ACTIVE = 0;
    public static final int CALL_STATE_ALERTING = 3;
    public static final int CALL_STATE_DIALING = 2;
    public static final int CALL_STATE_HELD = 1;
    public static final int CALL_STATE_HELD_BY_RESPONSE_AND_HOLD = 6;
    public static final int CALL_STATE_INCOMING = 4;
    public static final int CALL_STATE_TERMINATED = 7;
    public static final int CALL_STATE_WAITING = 5;
    private static final int PHONE_STATUS_IDLE = 0;
    private static final int PHONE_STATUS_ON_LINE = 1;
    private static final String TAG = "PhoneStatusManager";
    private static PhoneStatusManager mInstance = new PhoneStatusManager();
    private int mPhoneStatus = 0;

    public static PhoneStatusManager instance() {
        return mInstance;
    }

    private PhoneStatusManager() {
    }

    public static boolean isIdle() {
        return instance().mPhoneStatus == 0;
    }

    public void handlePhoneStateChange(BluetoothHeadsetClientCall bluetoothHeadsetClientCall) {
        LogUtils.d(TAG, " bluetoothHeadsetClientCall = " + bluetoothHeadsetClientCall);
        int state = bluetoothHeadsetClientCall.getState();
        if (state == 0 || state == 2 || state == 4) {
            notifyCallingEvent();
        } else if (state != 7) {
        } else {
            notifyCallEndEvent();
        }
    }

    private void notifyCallEndEvent() {
        LogUtils.i(TAG, "notifyCallEndEvent");
        this.mPhoneStatus = 0;
        EventBusUtils.post(new PhoneCallStateChangeEvent(false));
    }

    private void notifyCallingEvent() {
        LogUtils.i(TAG, "notifyCallingEvent");
        this.mPhoneStatus = 1;
        EventBusUtils.post(new PhoneCallStateChangeEvent(true));
    }
}

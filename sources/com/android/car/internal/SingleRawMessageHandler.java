package com.android.car.internal;

import android.car.XpDebugLog;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
/* loaded from: classes.dex */
public abstract class SingleRawMessageHandler<EventType> implements Handler.Callback {
    public static final String TAG = "SingleRawMessageHandler";
    private final int mHandledMessageWhat;
    private final Handler mHandler;

    protected abstract void handleEvent(EventType eventtype);

    public SingleRawMessageHandler(Looper looper, int i) {
        this.mHandledMessageWhat = i;
        this.mHandler = new Handler(looper, this);
    }

    public SingleRawMessageHandler(Handler handler, int i) {
        this(handler.getLooper(), i);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // android.os.Handler.Callback
    public boolean handleMessage(Message message) {
        if (message.what == this.mHandledMessageWhat) {
            Object obj = message.obj;
            if (XpDebugLog.CAR_DEBUG) {
                Log.d(TAG, "++handleMessage event: " + obj);
            }
            handleEvent(obj);
            if (XpDebugLog.CAR_DEBUG) {
                Log.d(TAG, "--handleMessage event: " + obj);
                return true;
            }
            return true;
        }
        return true;
    }

    public void sendEvents(EventType eventtype) {
        Handler handler = this.mHandler;
        handler.sendMessage(handler.obtainMessage(this.mHandledMessageWhat, eventtype));
    }
}

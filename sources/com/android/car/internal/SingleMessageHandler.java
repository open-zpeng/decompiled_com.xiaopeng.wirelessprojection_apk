package com.android.car.internal;

import android.car.XpDebugLog;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.util.Pair;
import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public abstract class SingleMessageHandler<EventType> implements Handler.Callback {
    private static final long MAX_DELIVER_MSG_INTERVAL_MS;
    private static final long MAX_DISPATCH_MSG_INTERVAL_MS;
    private static final long MAX_ON_EVENT_COST_INTERVAL_MS;
    public static final String TAG = "SingleMessageHandler";
    private final int mHandledMessageWhat;
    private final Handler mHandler;

    protected abstract void handleEvent(EventType eventtype);

    static {
        MAX_DELIVER_MSG_INTERVAL_MS = (Build.IS_USER ? Duration.ofMillis(1000L) : Duration.ofMillis(500L)).toMillis();
        MAX_DISPATCH_MSG_INTERVAL_MS = Duration.ofMillis(500L).toMillis();
        MAX_ON_EVENT_COST_INTERVAL_MS = Duration.ofMillis(100L).toMillis();
    }

    public SingleMessageHandler(Looper looper, int i) {
        this.mHandledMessageWhat = i;
        this.mHandler = new Handler(looper, this);
    }

    public SingleMessageHandler(Handler handler, int i) {
        this(handler.getLooper(), i);
    }

    @Override // android.os.Handler.Callback
    public boolean handleMessage(Message message) {
        if (message.what == this.mHandledMessageWhat) {
            Pair pair = (Pair) message.obj;
            long longValue = ((Long) pair.first).longValue();
            List<EventType> list = (List) pair.second;
            long uptimeMillis = SystemClock.uptimeMillis();
            long j = uptimeMillis - longValue;
            if (j > MAX_DELIVER_MSG_INTERVAL_MS) {
                Log.w(TAG, "deliver car event msg cost too much time:" + j + " ms");
            }
            list.forEach(new Consumer<EventType>() { // from class: com.android.car.internal.SingleMessageHandler.1
                @Override // java.util.function.Consumer
                public void accept(EventType eventtype) {
                    SingleMessageHandler.this.handleEvent(eventtype);
                }
            });
            long uptimeMillis2 = SystemClock.uptimeMillis() - uptimeMillis;
            if (XpDebugLog.CAR_DEBUG) {
                Log.i(TAG, "handleMessage eventList: " + getListString(list) + " cost " + uptimeMillis2 + " ms");
                return true;
            } else if (Build.IS_USER || uptimeMillis2 <= MAX_DISPATCH_MSG_INTERVAL_MS) {
                return true;
            } else {
                Log.w(TAG, "handleMessage eventList: " + getListString(list) + " cost too much time:" + uptimeMillis2 + " ms");
                return true;
            }
        }
        return true;
    }

    private String getListString(List<EventType> list) {
        if (list == null || list.size() <= 0) {
            return "null";
        }
        StringBuilder append = new StringBuilder(64).append("size[").append(list.size()).append("]: ");
        append.append((String) list.stream().map(new Function() { // from class: com.android.car.internal.-$$Lambda$SingleMessageHandler$AfR_caA29KN-2ZbWVbvipqv5cac
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                String obj2;
                obj2 = obj.toString();
                return obj2;
            }
        }).limit(5L).collect(Collectors.joining(", ")));
        return append.toString();
    }

    public void sendEvents(List<EventType> list) {
        long uptimeMillis = SystemClock.uptimeMillis();
        Pair pair = new Pair(Long.valueOf(uptimeMillis), list);
        Handler handler = this.mHandler;
        handler.sendMessage(handler.obtainMessage(this.mHandledMessageWhat, pair));
        if (Build.IS_USER) {
            return;
        }
        long uptimeMillis2 = SystemClock.uptimeMillis() - uptimeMillis;
        if (uptimeMillis2 > MAX_ON_EVENT_COST_INTERVAL_MS) {
            Log.w(TAG, "sendEvents cost too much time:" + uptimeMillis2 + " ms");
        }
    }
}

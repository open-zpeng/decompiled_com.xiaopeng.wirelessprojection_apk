package com.xiaopeng.wirelessprojection.core.utils;

import com.xiaopeng.lib.utils.LogUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.EventBusException;
/* loaded from: classes2.dex */
public class EventBusUtils {
    private static final String TAG = "EventBusUtils";

    private EventBusUtils() {
    }

    public static void init() {
        try {
            EventBus.builder().sendNoSubscriberEvent(false).logNoSubscriberMessages(false).installDefaultEventBus();
        } catch (EventBusException unused) {
            LogUtils.e(TAG, "Default instance already exists");
        }
    }

    public static void registerSafely(Object obj) {
        if (isRegistered(obj)) {
            return;
        }
        EventBus.getDefault().register(obj);
    }

    public static void unregisterSafely(Object obj) {
        if (isRegistered(obj)) {
            EventBus.getDefault().unregister(obj);
        }
    }

    public static boolean isRegistered(Object obj) {
        return EventBus.getDefault().isRegistered(obj);
    }

    public static void post(Object obj) {
        EventBus.getDefault().post(obj);
    }

    public static void postSticky(Object obj) {
        EventBus.getDefault().postSticky(obj);
    }

    public static boolean removeStickyEvent(Object obj) {
        return EventBus.getDefault().removeStickyEvent(obj);
    }
}

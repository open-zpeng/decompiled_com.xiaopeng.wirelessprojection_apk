package com.xiaopeng.lib.apirouter.server;

import android.os.IBinder;
import android.util.Log;
import android.util.Pair;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
/* loaded from: classes2.dex */
class AutoCodeMatcher {
    private static HashMap<String, Pair<IBinder, String>> mapping;
    private static List<IManifestHandler> sManifestHandlerList = new LinkedList();
    private static volatile boolean sIsManifestInited = false;
    private static final Object sLock = new Object();

    public Pair<IBinder, String> match(String str) {
        synchronized (sLock) {
            if (mapping == null) {
                mapping = ManifestHelperMapping.reflectMapping();
                initManifestHandler();
            }
        }
        HashMap<String, Pair<IBinder, String>> hashMap = mapping;
        Pair<IBinder, String> pair = hashMap == null ? null : hashMap.get(str);
        return pair == null ? new Pair<>(null, null) : pair;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void addManifestHandler(IManifestHandler iManifestHandler) {
        Log.i("AutoCodeMatcher", "addManifestHandler:" + iManifestHandler + ", sIsManifestInited = " + sIsManifestInited);
        synchronized (sLock) {
            if (sIsManifestInited) {
                addManifestHandlerToMap(iManifestHandler.getManifestHelpers());
            } else if (!sManifestHandlerList.contains(iManifestHandler)) {
                sManifestHandlerList.add(iManifestHandler);
            }
        }
        Log.i("AutoCodeMatcher", "addManifestHandler Finish:" + iManifestHandler + ", sIsManifestInited = " + sIsManifestInited);
    }

    private void initManifestHandler() {
        Log.i("AutoCodeMatcher", "initManifestHandler:");
        if (!sManifestHandlerList.isEmpty()) {
            for (IManifestHandler iManifestHandler : sManifestHandlerList) {
                initManifestHandler(iManifestHandler);
            }
        }
        sIsManifestInited = true;
    }

    private void initManifestHandler(IManifestHandler iManifestHandler) {
        if (iManifestHandler == null) {
            return;
        }
        IManifestHelper[] manifestHelpers = iManifestHandler.getManifestHelpers();
        if (manifestHelpers == null || manifestHelpers.length == 0) {
            Log.i("AutoCodeMatcher", "initManifestHandler manifestHelpers is empty, return");
        } else {
            addManifestHandlerToMap(manifestHelpers);
        }
    }

    private static void addManifestHandlerToMap(IManifestHelper[] iManifestHelperArr) {
        Log.i("AutoCodeMatcher", "addManifestHandlerToMap start :" + Arrays.toString(iManifestHelperArr));
        if (mapping == null) {
            mapping = new HashMap<>();
        }
        HashMap<String, Pair<IBinder, String>> hashMap = mapping;
        for (IManifestHelper iManifestHelper : iManifestHelperArr) {
            try {
                HashMap<String, Pair<IBinder, String>> mapping2 = iManifestHelper.getMapping();
                if (mapping2 != null && !mapping2.isEmpty()) {
                    hashMap.putAll(mapping2);
                }
            } catch (Exception e) {
                Log.e("AutoCodeMatcher", "addManifestHandlerToMap:" + iManifestHelper.getClass(), e);
            }
        }
        Log.i("AutoCodeMatcher", "addManifestHandlerToMap end : currentMapping size = " + hashMap.size());
    }
}

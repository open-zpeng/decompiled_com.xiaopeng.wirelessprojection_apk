package com.xiaopeng.wirelessprojection.core.manager;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import com.xiaopeng.lib.utils.LogUtils;
import com.xiaopeng.wirelessprojection.core.BaseApp;
import com.xiaopeng.xuimanager.XUIManager;
import com.xiaopeng.xuimanager.XUIServiceNotConnectedException;
import com.xiaopeng.xuimanager.userscenario.UserScenarioManager;
/* loaded from: classes2.dex */
public class XUIServiceManager {
    private static final String TAG = "XUIServiceManager";
    private UserScenarioManager mUserScenarioManager;
    private XUIManager mXUIManager;
    private Handler xuiHandler = null;

    /* loaded from: classes2.dex */
    private static class Holder {
        static final XUIServiceManager INSTANCE = new XUIServiceManager();

        private Holder() {
        }
    }

    public static XUIServiceManager instance() {
        return Holder.INSTANCE;
    }

    public void init() {
        LogUtils.i(TAG, "init");
        if (this.xuiHandler == null) {
            HandlerThread handlerThread = new HandlerThread(TAG);
            handlerThread.start();
            this.xuiHandler = new Handler(handlerThread.getLooper());
        }
        if (this.mXUIManager == null) {
            this.mXUIManager = XUIManager.createXUIManager(BaseApp.getContext(), new ServiceConnection() { // from class: com.xiaopeng.wirelessprojection.core.manager.XUIServiceManager.1
                @Override // android.content.ServiceConnection
                public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                    LogUtils.e(XUIServiceManager.TAG, "XUI service connected.");
                    XUIServiceManager.this.initUserScenarioManager();
                }

                @Override // android.content.ServiceConnection
                public void onServiceDisconnected(ComponentName componentName) {
                    LogUtils.e(XUIServiceManager.TAG, "XUI service disconnected.");
                }
            });
        }
        this.mXUIManager.connect();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initUserScenarioManager() {
        LogUtils.i(TAG, "initUserScenarioManager");
        try {
            this.mUserScenarioManager = (UserScenarioManager) this.mXUIManager.getXUIServiceManager("userscenario");
        } catch (XUIServiceNotConnectedException e) {
            e.printStackTrace();
        }
    }
}

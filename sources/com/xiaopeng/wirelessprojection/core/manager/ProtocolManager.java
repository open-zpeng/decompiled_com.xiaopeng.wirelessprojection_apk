package com.xiaopeng.wirelessprojection.core.manager;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Surface;
import com.xiaopeng.lib.utils.LogUtils;
import com.xiaopeng.wirelessprojection.core.BaseApp;
import com.xiaopeng.wirelessprojection.core.event.AirplayConnChangeEvent;
import com.xiaopeng.wirelessprojection.core.manager.ProtocolManager;
import com.xiaopeng.wirelessprojection.core.utils.EventBusUtils;
import com.xiaopeng.wirelessprojection.core.utils.ScreenUtils;
import com.xiaopeng.wirelessprojection.core.utils.ThreadUtils;
import com.xpeng.airplay.service.IXpAirplaySession;
import com.xpeng.airplay.service.MediaPlaybackInfo;
import com.xpeng.airplay.service.SessionParams;
import com.xpeng.airplay.service.XpAirplayManager;
import io.reactivex.disposables.Disposable;
import java.util.Arrays;
/* loaded from: classes2.dex */
public class ProtocolManager {
    private static final int SERVICE_USER_ID = 0;
    private static final String TAG = "ProtocolManager";
    private Handler mHandler;
    private HandlerThread mHandlerThread;
    private Disposable mReconnectDisposable;
    private XpAirplayManager mXpAirplayManager;
    private IXpAirplaySession mXpAirplaySession;
    private int mSessionId = -1;
    private AirplayConnectionListener mAirplayConnectionListener = new AirplayConnectionListener();

    /* loaded from: classes2.dex */
    private static class Holder {
        private static final ProtocolManager INSTANCE = new ProtocolManager();

        private Holder() {
        }
    }

    public static ProtocolManager instance() {
        return Holder.INSTANCE;
    }

    public void init(boolean z) {
        LogUtils.i(TAG, "init isFirstInit=" + z);
        if (z) {
            initHandler();
        }
        postChildThread(new Runnable() { // from class: com.xiaopeng.wirelessprojection.core.manager.-$$Lambda$ProtocolManager$CGSNIoYRx92ThQDphFs8dK1b4As
            @Override // java.lang.Runnable
            public final void run() {
                ProtocolManager.this.lambda$init$0$ProtocolManager();
            }
        });
    }

    public /* synthetic */ void lambda$init$0$ProtocolManager() {
        LogUtils.i(TAG, "mXpAirplayManager init");
        XpAirplayManager from = XpAirplayManager.from(BaseApp.getContext());
        this.mXpAirplayManager = from;
        from.bindAirplayService(ScreenUtils.getScreenId(), 0);
        String serverName = this.mXpAirplayManager.getServerName(ScreenUtils.getScreenId(BaseApp.getContext()));
        LogUtils.i(TAG, "mXpAirplayManager serverNameList " + Arrays.toString(this.mXpAirplayManager.getServerNames()));
        LogUtils.i(TAG, "mXpAirplayManager getServerName=" + serverName);
        initSession(ScreenUtils.getScreenId(BaseApp.getContext()));
    }

    public void initHandler() {
        HandlerThread handlerThread = new HandlerThread(TAG);
        this.mHandlerThread = handlerThread;
        handlerThread.start();
        this.mHandler = new Handler(this.mHandlerThread.getLooper());
    }

    private void initSession(SessionParams sessionParams) {
        if (sessionParams != null) {
            LogUtils.i(TAG, "initSession serverName=" + sessionParams.getScreenId());
            int createSession = this.mXpAirplayManager.createSession(sessionParams.getScreenId());
            this.mSessionId = createSession;
            IXpAirplaySession openSession = this.mXpAirplayManager.openSession(createSession);
            this.mXpAirplaySession = openSession;
            this.mAirplayConnectionListener.setTarget(openSession);
            EventBusUtils.post(new AirplayConnChangeEvent());
        }
    }

    private void initSession(int i) {
        LogUtils.i(TAG, "initSession screenId=" + i);
        int i2 = this.mSessionId;
        if (i2 != -1) {
            this.mXpAirplayManager.closeSession(i2);
        }
        int createSession = this.mXpAirplayManager.createSession(i);
        this.mSessionId = createSession;
        IXpAirplaySession openSession = this.mXpAirplayManager.openSession(createSession);
        this.mXpAirplaySession = openSession;
        this.mAirplayConnectionListener.setTarget(openSession);
        Log.i(TAG, "initSession: sessionId is: " + this.mSessionId);
        EventBusUtils.post(new AirplayConnChangeEvent());
    }

    public void checkSessionAvailable() {
        if (this.mXpAirplaySession == null) {
            LogUtils.e(TAG, "checkSessionAvailable mXpAirplaySession null");
            init(false);
        }
    }

    public void postChildThread(Runnable runnable) {
        postChildThread(runnable, 0L);
    }

    public void postChildThread(Runnable runnable, long j) {
        if (this.mHandler == null) {
            LogUtils.e(TAG, "postChildThread handler not init");
            initHandler();
        }
        this.mHandler.postDelayed(runnable, j);
    }

    public Handler getHandler() {
        if (this.mHandler == null) {
            LogUtils.e(TAG, "getHandler handler not init");
            initHandler();
        }
        return this.mHandler;
    }

    public String getServerName() {
        LogUtils.i(TAG, "getServerName");
        try {
            return this.mXpAirplaySession.getServerName();
        } catch (RemoteException | NullPointerException e) {
            e.printStackTrace();
            return "";
        }
    }

    public long getTrafficCostByte() {
        XpAirplayManager xpAirplayManager = this.mXpAirplayManager;
        if (xpAirplayManager != null) {
            long tetheringDataUsage = xpAirplayManager.getTetheringDataUsage();
            LogUtils.i(TAG, "getTrafficCostByte res=" + tetheringDataUsage);
            return tetheringDataUsage;
        }
        return 0L;
    }

    public void setMirrorSurface(Surface surface) {
        LogUtils.i(TAG, "setMirrorSurface surface=" + surface);
        try {
            this.mXpAirplaySession.setMirrorSurface(surface);
        } catch (RemoteException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void setVideoPlaybackState(int i) {
        LogUtils.i(TAG, "setVideoPlaybackState state=" + i);
        try {
            this.mXpAirplaySession.setVideoPlaybackState(i);
        } catch (RemoteException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void setVideoPlaybackInfo(MediaPlaybackInfo mediaPlaybackInfo) {
        LogUtils.i(TAG, "setVideoPlaybackInfo pos=" + mediaPlaybackInfo.getPosition());
        try {
            this.mXpAirplaySession.setMediaPlaybackInfo(mediaPlaybackInfo);
        } catch (RemoteException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    public boolean hasActiveConnection() {
        LogUtils.i(TAG, "hasActiveConnection");
        try {
            return this.mXpAirplaySession.hasActiveConnection();
        } catch (RemoteException | NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void registerAirplayCallbacks(XpAirplayManager.DefaultAirplayCallbacks defaultAirplayCallbacks) {
        LogUtils.i(TAG, "registerAirplayCallbacks " + defaultAirplayCallbacks);
        try {
            this.mXpAirplaySession.registerAirplayCallbacks(defaultAirplayCallbacks);
        } catch (RemoteException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void unregisterAirplayCallbacks(XpAirplayManager.DefaultAirplayCallbacks defaultAirplayCallbacks) {
        LogUtils.i(TAG, "unregisterAirplayCallbacks " + defaultAirplayCallbacks);
        try {
            this.mXpAirplaySession.unregisterAirplayCallbacks(defaultAirplayCallbacks);
        } catch (RemoteException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class AirplayConnectionListener implements IBinder.DeathRecipient {
        private static final String TAG = "AirplayConnectionListener";
        private static final int WAIT_SERVICE_DELAY = 5000;
        private IXpAirplaySession mSession;

        AirplayConnectionListener() {
        }

        public void setTarget(IXpAirplaySession iXpAirplaySession) {
            LogUtils.i(TAG, "setTarget session=" + iXpAirplaySession);
            if (this.mSession != null) {
                unlinkToDeath();
            }
            this.mSession = iXpAirplaySession;
            linkToDeath(iXpAirplaySession);
        }

        public void linkToDeath(IXpAirplaySession iXpAirplaySession) {
            LogUtils.i(TAG, "linkToDeath");
            try {
                iXpAirplaySession.asBinder().linkToDeath(this, 0);
            } catch (RemoteException | NullPointerException e) {
                e.printStackTrace();
            }
        }

        public void unlinkToDeath() {
            LogUtils.i(TAG, "unlinkToDeath");
            try {
                this.mSession.asBinder().unlinkToDeath(this, 0);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            LogUtils.e(TAG, "binderDied");
            ProtocolManager.this.mSessionId = -1;
            if (ProtocolManager.this.mReconnectDisposable != null) {
                ProtocolManager.this.mReconnectDisposable.dispose();
                ProtocolManager.this.mReconnectDisposable = null;
            }
            ProtocolManager.this.mReconnectDisposable = ThreadUtils.postBackground(new AnonymousClass1(), 5000L);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: com.xiaopeng.wirelessprojection.core.manager.ProtocolManager$AirplayConnectionListener$1  reason: invalid class name */
        /* loaded from: classes2.dex */
        public class AnonymousClass1 implements Runnable {
            AnonymousClass1() {
            }

            public /* synthetic */ void lambda$run$0$ProtocolManager$AirplayConnectionListener$1() {
                ProtocolManager.this.init(false);
            }

            @Override // java.lang.Runnable
            public void run() {
                ProtocolManager.this.postChildThread(new Runnable() { // from class: com.xiaopeng.wirelessprojection.core.manager.-$$Lambda$ProtocolManager$AirplayConnectionListener$1$4UagBZ3fdRxVmPz-xMfUyXB5dgs
                    @Override // java.lang.Runnable
                    public final void run() {
                        ProtocolManager.AirplayConnectionListener.AnonymousClass1.this.lambda$run$0$ProtocolManager$AirplayConnectionListener$1();
                    }
                });
            }
        }
    }
}

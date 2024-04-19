package com.xpeng.airplay.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.util.Log;
import com.xpeng.airplay.service.IXpAirplayCallbacks;
import com.xpeng.airplay.service.IXpAirplayManager;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes2.dex */
public class XpAirplayManager {
    public static final int DISCONNECT_REASON_NETWORK_ERROR = 1;
    public static final int DISCONNECT_REASON_UNKNOWN = 0;
    public static final int DISCONNECT_REASON_USER_REQUSET = 2;
    public static final int MEDIA_TYPE_AUDIO = 1;
    public static final int MEDIA_TYPE_NONE = 0;
    public static final int MEDIA_TYPE_VIDEO = 2;
    public static final int PLAYBACK_STATE_COMPLETE = 1;
    public static final int PLAYBACK_STATE_ERROR = 2;
    public static final int PLAYBACK_STATE_GONE = 5;
    public static final int PLAYBACK_STATE_PAUSE = 4;
    public static final int PLAYBACK_STATE_PLAYING = 0;
    public static final int PLAYBACK_STATE_RESUME = 6;
    public static final int PLAYBACK_STATE_STOP = 3;
    public static final int SERVER_TYPE_AIRPLAY = 2;
    public static final int SERVER_TYPE_DLNA = 3;
    public static final int SERVER_TYPE_MIRROR = 1;
    public static final int SERVER_TYPE_NONE = 0;
    private static final long SERVICE_BINDING_TIMEOUT = 10000;
    private static final String TAG = "XpAirplayManager";
    private static final String XP_AIRPLAY_SERVICE_CLASS_NAME = "com.xpeng.airplay.service.XpAirplayService";
    private static final String XP_AIRPLAY_SERVICE_PACKAGE_NAME = "com.xiaopeng.airplay";
    private IXpAirplayManager mAirplayService;
    private Context mContext;
    private Intent mIntent;
    private AirplayServiceConnection mServiceConnection;
    private Object mLock = new Object();
    private boolean mCanUnbindService = false;

    /* loaded from: classes2.dex */
    public static abstract class DefaultAirplayCallbacks extends IXpAirplayCallbacks.Stub {
        public abstract void onAudioProgressUpdated(MediaPlaybackInfo mediaPlaybackInfo);

        public abstract void onClientConnected(int i);

        public abstract void onClientDisconnected(int i, int i2);

        public abstract void onMetaDataUpdated(MediaMetaData mediaMetaData);

        public abstract void onMirrorSizeChanged(int i, int i2);

        public abstract void onMirrorStarted();

        public abstract void onMirrorStopped();

        public abstract void onServerNameUpdated(String str);

        public abstract void onVideoPlayStarted(MediaPlayInfo mediaPlayInfo);

        public abstract void onVideoPlayStopped();

        public abstract void onVideoRateChanged(int i);

        public abstract void onVideoScrubbed(int i);

        public abstract void onVolumeChanged(float f);
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface DisconnectReason {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface MediaType {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface PlaybackState {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface ServerType {
    }

    public static XpAirplayManager from(Context context) {
        return new XpAirplayManager(context);
    }

    private XpAirplayManager(Context context) {
        this.mContext = context;
        Intent intent = new Intent(this.mContext.getPackageName());
        this.mIntent = intent;
        intent.setComponent(new ComponentName(XP_AIRPLAY_SERVICE_PACKAGE_NAME, XP_AIRPLAY_SERVICE_CLASS_NAME));
        this.mServiceConnection = new AirplayServiceConnection();
        warningOnMainThread();
    }

    public String[] getServerNames() {
        Log.d(TAG, "getServerNames()");
        IXpAirplayManager iXpAirplayManager = this.mAirplayService;
        if (iXpAirplayManager != null) {
            try {
                return iXpAirplayManager.getServerNames();
            } catch (RemoteException unused) {
                Log.e(TAG, "fail to get server names");
                return null;
            }
        }
        Log.w(TAG, "AirplayService is not connected");
        return null;
    }

    public String getServerName(int i) {
        Log.d(TAG, "getServerName()");
        IXpAirplayManager iXpAirplayManager = this.mAirplayService;
        if (iXpAirplayManager != null) {
            try {
                return iXpAirplayManager.getServerName(i);
            } catch (RemoteException unused) {
                Log.e(TAG, "fail to get server name for screen id " + i);
                return null;
            }
        }
        return null;
    }

    public int createSession(String str) {
        if (this.mAirplayService != null) {
            try {
                return this.mAirplayService.createSession(new SessionParams(this.mContext.getPackageName(), str));
            } catch (RemoteException unused) {
                Log.e(TAG, "fail to create session for " + str);
                return -1;
            }
        }
        Log.w(TAG, "AirplayService is not connected");
        return -1;
    }

    public int createSession(int i) {
        if (this.mAirplayService != null) {
            try {
                return this.mAirplayService.createSession(new SessionParams(this.mContext.getPackageName(), null, i));
            } catch (RemoteException unused) {
                Log.e(TAG, "fail to create session for " + i);
                return -1;
            }
        }
        Log.w(TAG, "AirplayService is not connected");
        return -1;
    }

    public IXpAirplaySession openSession(int i) {
        IXpAirplayManager iXpAirplayManager = this.mAirplayService;
        if (iXpAirplayManager != null) {
            try {
                return iXpAirplayManager.openSession(i);
            } catch (RemoteException unused) {
                Log.e(TAG, "fail to open session for " + i);
                return null;
            }
        }
        Log.w(TAG, "AirplayService is not connected");
        return null;
    }

    public void closeSession(int i) {
        IXpAirplayManager iXpAirplayManager = this.mAirplayService;
        if (iXpAirplayManager != null) {
            try {
                iXpAirplayManager.closeSession(i);
                return;
            } catch (RemoteException unused) {
                return;
            }
        }
        Log.w(TAG, "AirplayService is not connected");
    }

    public long getTetheringDataUsage() {
        Log.d(TAG, "getTetherDataUsage()");
        IXpAirplayManager iXpAirplayManager = this.mAirplayService;
        if (iXpAirplayManager != null) {
            try {
                return iXpAirplayManager.getTetheringDataUsage();
            } catch (RemoteException unused) {
                Log.e(TAG, "fail to get tether data usage");
                return 0L;
            }
        }
        Log.e(TAG, "AirplayService is not connected");
        return 0L;
    }

    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        unbindServiceLocked();
    }

    public void bindAirplayService(int i, int i2) {
        synchronized (this.mLock) {
            if (this.mAirplayService != null) {
                return;
            }
            bindServiceLocked(i, i2);
        }
    }

    private void bindServiceLocked(int i, int i2) {
        Log.d(TAG, "bindServiceLocked()");
        if (this.mAirplayService != null) {
            return;
        }
        try {
            try {
                if (i == 0) {
                    this.mContext.bindService(this.mIntent, this.mServiceConnection, 1);
                } else {
                    this.mContext.bindServiceAsUser(this.mIntent, this.mServiceConnection, 1, UserHandle.of(i2));
                }
                long uptimeMillis = SystemClock.uptimeMillis();
                while (this.mAirplayService == null) {
                    long uptimeMillis2 = SERVICE_BINDING_TIMEOUT - (SystemClock.uptimeMillis() - uptimeMillis);
                    if (uptimeMillis2 <= 0) {
                        this.mLock.wait(SERVICE_BINDING_TIMEOUT);
                    } else {
                        this.mLock.wait(uptimeMillis2);
                    }
                }
            } catch (InterruptedException unused) {
                Log.d(TAG, "fail to bind service");
            }
        } finally {
            this.mCanUnbindService = true;
            this.mLock.notifyAll();
        }
    }

    private void unbindServiceLocked() {
        Log.d(TAG, "unBindServiceLocked()");
        if (this.mAirplayService == null) {
            return;
        }
        while (!this.mCanUnbindService) {
            try {
                this.mLock.wait();
            } catch (InterruptedException unused) {
            }
        }
        this.mAirplayService = null;
        this.mContext.unbindService(this.mServiceConnection);
    }

    private void warningOnMainThread() {
        if (Thread.currentThread() == this.mContext.getMainLooper().getThread()) {
            Log.w(TAG, "Invoke on main thread, it may be blocked");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public final class AirplayServiceConnection implements ServiceConnection {
        private AirplayServiceConnection() {
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(XpAirplayManager.TAG, "onServiceConnected(): " + componentName);
            synchronized (XpAirplayManager.this.mLock) {
                XpAirplayManager.this.mAirplayService = IXpAirplayManager.Stub.asInterface(iBinder);
                XpAirplayManager.this.mLock.notifyAll();
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(XpAirplayManager.TAG, "onServiceDisconnected(): " + componentName);
            synchronized (XpAirplayManager.this.mLock) {
                if (XpAirplayManager.this.mAirplayService != null) {
                    XpAirplayManager.this.mAirplayService = null;
                    XpAirplayManager.this.mLock.notifyAll();
                }
            }
        }
    }
}

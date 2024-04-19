package com.xiaopeng.wirelessprojection.dmr.manager;

import android.util.Log;
import android.view.Surface;
import com.xiaopeng.lib.utils.LogUtils;
import com.xiaopeng.wirelessprojection.core.event.AirplayConnChangeEvent;
import com.xiaopeng.wirelessprojection.core.event.DebugVideoPlayEvent;
import com.xiaopeng.wirelessprojection.core.event.ServerNameUpdateEvent;
import com.xiaopeng.wirelessprojection.core.interfaces.IRendererPresenter;
import com.xiaopeng.wirelessprojection.core.manager.ProtocolManager;
import com.xiaopeng.wirelessprojection.core.utils.EventBusUtils;
import com.xiaopeng.wirelessprojection.core.utils.ThreadUtils;
import com.xiaopeng.wirelessprojection.dmr.RendererActivity;
import com.xiaopeng.wirelessprojection.dmr.manager.RendererControlManager;
import com.xpeng.airplay.service.MediaMetaData;
import com.xpeng.airplay.service.MediaPlayInfo;
import com.xpeng.airplay.service.MediaPlaybackInfo;
import com.xpeng.airplay.service.XpAirplayManager;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
/* loaded from: classes2.dex */
public class RendererControlManager {
    private static final int INFO_VALID_TIME = 60000;
    public static final int SERVER_TYPE_AIRPLAY = 2;
    public static final int SERVER_TYPE_DLNA = 3;
    public static final int SERVER_TYPE_MIRROR = 1;
    private static final int WAIT_INIT_DELAY = 300;
    private static final int WAIT_INIT_DELAY_SHORT = 200;
    private static final int WAIT_SERVICE_DELAY = 100;
    private IRendererPresenter mPresenter;
    private final String TAG = "RendererControlManager";
    private ProjectionStateCallback mProjectionStateCallback = new ProjectionStateCallback();
    private boolean mClientConnected = false;
    private boolean isMirrorStartStatus = false;
    private int lastMirrorWidth = 0;
    private int lastMirrorHeight = 0;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class Holder {
        private static final RendererControlManager INSTANCE = new RendererControlManager();

        private Holder() {
        }
    }

    public static RendererControlManager instance() {
        return Holder.INSTANCE;
    }

    public void init() {
        LogUtils.i("RendererControlManager", "RendererControlManager init");
        EventBusUtils.registerSafely(this);
    }

    public void setRendererPresenter(IRendererPresenter iRendererPresenter) {
        LogUtils.i("RendererControlManager", "setRendererPresenter presenter=" + iRendererPresenter.toString());
        this.mPresenter = iRendererPresenter;
    }

    public void unregister() {
        LogUtils.i("RendererControlManager", "RendererControlManager unregister");
        setRendererPresenter(null);
        EventBusUtils.unregisterSafely(this);
        ProtocolManager.instance().unregisterAirplayCallbacks(this.mProjectionStateCallback);
    }

    public void setVideoPlaybackState(int i) {
        LogUtils.i("RendererControlManager", "setVideoPlaybackState state=" + i);
        ProtocolManager.instance().setVideoPlaybackState(i);
    }

    public String getServerName() {
        String serverName = ProtocolManager.instance().getServerName();
        LogUtils.i("RendererControlManager", "getServerName name=" + serverName);
        return serverName;
    }

    public long getTrafficCostByte() {
        LogUtils.i("RendererControlManager", "getTrafficCostByte");
        return ProtocolManager.instance().getTrafficCostByte();
    }

    public void setVideoPlaybackInfo(MediaPlaybackInfo mediaPlaybackInfo) {
        LogUtils.i("RendererControlManager", "setVideoPlaybackInfo pos=" + mediaPlaybackInfo.getPosition());
        ProtocolManager.instance().setVideoPlaybackInfo(mediaPlaybackInfo);
    }

    public void setMirrorSurface(Surface surface) {
        LogUtils.i("RendererControlManager", "setMirrorSurface surface=" + surface);
        ProtocolManager.instance().setMirrorSurface(surface);
    }

    public boolean hasActiveConnection() {
        boolean hasActiveConnection = ProtocolManager.instance().hasActiveConnection();
        LogUtils.i("RendererControlManager", "hasActiveConnection res=" + hasActiveConnection);
        return hasActiveConnection;
    }

    public MediaPlayInfo getTestInfo() {
        return new MediaPlayInfo(RendererActivity.TEST_URI_LANDSCAPE, "测试标题", 0.33f, 0);
    }

    public ProjectionStateCallback getTestCallback() {
        return this.mProjectionStateCallback;
    }

    public void testVideoPlay() {
        getTestCallback().onClientConnected(3);
        ThreadUtils.postMainThread(new Runnable() { // from class: com.xiaopeng.wirelessprojection.dmr.manager.-$$Lambda$RendererControlManager$ps5KGBvqtEnlN_1wB95JDeiZh2I
            @Override // java.lang.Runnable
            public final void run() {
                RendererControlManager.this.lambda$testVideoPlay$0$RendererControlManager();
            }
        }, 1000L);
    }

    public /* synthetic */ void lambda$testVideoPlay$0$RendererControlManager() {
        getTestCallback().onVideoPlayStarted(getTestInfo());
    }

    public boolean isClientConnected() {
        LogUtils.i("RendererControlManager", "isClientConnected mClientConnected=" + this.mClientConnected);
        return this.mClientConnected;
    }

    public boolean isMirrorStart() {
        return this.isMirrorStartStatus;
    }

    public void setMirrorStatus(boolean z) {
        this.isMirrorStartStatus = z;
    }

    public int getLastMirrorWidth() {
        return this.lastMirrorWidth;
    }

    public int getLastMirrorHeight() {
        return this.lastMirrorHeight;
    }

    public void setLastMirrorWidth(int i) {
        this.lastMirrorWidth = i;
    }

    public void setLastMirrorHeight(int i) {
        this.lastMirrorHeight = i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setClientConnected(boolean z) {
        this.mClientConnected = z;
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onAirplayConnChangeEvent(AirplayConnChangeEvent airplayConnChangeEvent) {
        LogUtils.i("RendererControlManager", "onAirplayConnChangeEvent");
        ProtocolManager.instance().postChildThread(new Runnable() { // from class: com.xiaopeng.wirelessprojection.dmr.manager.-$$Lambda$RendererControlManager$du44l0cXfCawQ2BimC-Yvm6WaD8
            @Override // java.lang.Runnable
            public final void run() {
                RendererControlManager.this.lambda$onAirplayConnChangeEvent$1$RendererControlManager();
            }
        }, 100L);
    }

    public /* synthetic */ void lambda$onAirplayConnChangeEvent$1$RendererControlManager() {
        ProtocolManager.instance().registerAirplayCallbacks(this.mProjectionStateCallback);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onDebugVideoPlayEvent(DebugVideoPlayEvent debugVideoPlayEvent) {
        LogUtils.i("RendererControlManager", "onDebugVideoPlayEvent");
        testVideoPlay();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class ProjectionStateCallback extends XpAirplayManager.DefaultAirplayCallbacks {
        private final String TAG;

        @Override // com.xpeng.airplay.service.XpAirplayManager.DefaultAirplayCallbacks, com.xpeng.airplay.service.IXpAirplayCallbacks
        public void onAudioProgressUpdated(MediaPlaybackInfo mediaPlaybackInfo) {
        }

        private ProjectionStateCallback() {
            this.TAG = "ProjectionStateCallback";
        }

        @Override // com.xpeng.airplay.service.XpAirplayManager.DefaultAirplayCallbacks, com.xpeng.airplay.service.IXpAirplayCallbacks
        public void onClientConnected(int i) {
            LogUtils.i("ProjectionStateCallback", "onClientConnected requestType=" + i);
            RendererActivity.show();
            RendererControlManager.this.setClientConnected(true);
        }

        @Override // com.xpeng.airplay.service.XpAirplayManager.DefaultAirplayCallbacks, com.xpeng.airplay.service.IXpAirplayCallbacks
        public void onClientDisconnected(int i, int i2) {
            LogUtils.i("ProjectionStateCallback", "onClientDisconnected requestType=" + i + ", reason=" + i2);
            RendererActivity.hide();
            RendererControlManager.this.setClientConnected(false);
        }

        @Override // com.xpeng.airplay.service.XpAirplayManager.DefaultAirplayCallbacks, com.xpeng.airplay.service.IXpAirplayCallbacks
        public void onMirrorStarted() {
            LogUtils.i("ProjectionStateCallback", "onMirrorStarted");
            if (RendererControlManager.this.mPresenter != null) {
                RendererControlManager.this.mPresenter.onMirrorStarted();
            }
        }

        @Override // com.xpeng.airplay.service.XpAirplayManager.DefaultAirplayCallbacks, com.xpeng.airplay.service.IXpAirplayCallbacks
        public void onMirrorStopped() {
            LogUtils.i("ProjectionStateCallback", "onMirrorStopped");
            if (RendererControlManager.this.mPresenter != null) {
                RendererControlManager.this.mPresenter.onMirrorStopped();
            }
        }

        @Override // com.xpeng.airplay.service.XpAirplayManager.DefaultAirplayCallbacks, com.xpeng.airplay.service.IXpAirplayCallbacks
        public void onMirrorSizeChanged(int i, int i2) {
            LogUtils.i("ProjectionStateCallback", "onMirrorSizeChanged w=" + i + ", h=" + i2);
            if (RendererControlManager.this.mPresenter != null) {
                RendererControlManager.this.mPresenter.onMirrorSizeChanged(i, i2);
            }
        }

        @Override // com.xpeng.airplay.service.XpAirplayManager.DefaultAirplayCallbacks, com.xpeng.airplay.service.IXpAirplayCallbacks
        public void onVideoPlayStarted(final MediaPlayInfo mediaPlayInfo) {
            if (mediaPlayInfo == null) {
                LogUtils.e("ProjectionStateCallback", "onVideoPlayStarted info null");
                return;
            }
            LogUtils.i("ProjectionStateCallback", "onVideoPlayStarted info=" + mediaPlayInfo.toString());
            ThreadUtils.postBackground(new Runnable() { // from class: com.xiaopeng.wirelessprojection.dmr.manager.-$$Lambda$RendererControlManager$ProjectionStateCallback$-V4_bDg1r07O_cUIV2Y5sLhbMIY
                @Override // java.lang.Runnable
                public final void run() {
                    RendererControlManager.ProjectionStateCallback.this.lambda$onVideoPlayStarted$0$RendererControlManager$ProjectionStateCallback(mediaPlayInfo);
                }
            }, 200L);
        }

        public /* synthetic */ void lambda$onVideoPlayStarted$0$RendererControlManager$ProjectionStateCallback(MediaPlayInfo mediaPlayInfo) {
            if (RendererControlManager.this.mPresenter != null) {
                if (mediaPlayInfo.getStreamType() != 1 || mediaPlayInfo.getUrl() != null) {
                    RendererControlManager.this.mPresenter.onVideoPlayStarted(mediaPlayInfo);
                    return;
                }
                LogUtils.e("ProjectionStateCallback", "onVideoPlayStarted url null");
                onMirrorStarted();
            }
        }

        @Override // com.xpeng.airplay.service.XpAirplayManager.DefaultAirplayCallbacks, com.xpeng.airplay.service.IXpAirplayCallbacks
        public void onVideoPlayStopped() {
            LogUtils.i("ProjectionStateCallback", "onVideoPlayStopped");
            if (RendererControlManager.this.mPresenter != null) {
                RendererControlManager.this.mPresenter.onVideoPlayStopped();
            }
        }

        @Override // com.xpeng.airplay.service.XpAirplayManager.DefaultAirplayCallbacks, com.xpeng.airplay.service.IXpAirplayCallbacks
        public void onVideoRateChanged(int i) {
            LogUtils.i("ProjectionStateCallback", "onVideoRateChanged rate=" + i);
            if (RendererActivity.instance() == null || !RendererActivity.instance().isAlive() || RendererControlManager.this.mPresenter == null) {
                return;
            }
            RendererControlManager.this.mPresenter.onVideoRateChanged(i);
        }

        @Override // com.xpeng.airplay.service.XpAirplayManager.DefaultAirplayCallbacks, com.xpeng.airplay.service.IXpAirplayCallbacks
        public void onVideoScrubbed(final int i) {
            LogUtils.i("ProjectionStateCallback", "onVideoScrubbed pos=" + i);
            ThreadUtils.postBackground(new Runnable() { // from class: com.xiaopeng.wirelessprojection.dmr.manager.-$$Lambda$RendererControlManager$ProjectionStateCallback$TA_L-W6VjuzljA6ppu2eIQlCCCE
                @Override // java.lang.Runnable
                public final void run() {
                    RendererControlManager.ProjectionStateCallback.this.lambda$onVideoScrubbed$1$RendererControlManager$ProjectionStateCallback(i);
                }
            }, 300L);
        }

        public /* synthetic */ void lambda$onVideoScrubbed$1$RendererControlManager$ProjectionStateCallback(int i) {
            if (RendererControlManager.this.mPresenter != null) {
                RendererControlManager.this.mPresenter.onVideoScrubbed(i);
            }
        }

        @Override // com.xpeng.airplay.service.XpAirplayManager.DefaultAirplayCallbacks, com.xpeng.airplay.service.IXpAirplayCallbacks
        public void onVolumeChanged(float f) {
            LogUtils.i("ProjectionStateCallback", "onVolumeChanged vol=" + f);
            if (RendererControlManager.this.mPresenter != null) {
                RendererControlManager.this.mPresenter.onVolumeChanged(f);
            }
        }

        @Override // com.xpeng.airplay.service.XpAirplayManager.DefaultAirplayCallbacks, com.xpeng.airplay.service.IXpAirplayCallbacks
        public void onMetaDataUpdated(MediaMetaData mediaMetaData) {
            if (mediaMetaData == null) {
                LogUtils.e("ProjectionStateCallback", "onMetaDataUpdated playMetaData null");
                return;
            }
            LogUtils.i("ProjectionStateCallback", "onMetaDataUpdated playMetaData=" + mediaMetaData.toString());
            if (RendererControlManager.this.mPresenter != null) {
                RendererControlManager.this.mPresenter.onMetaDataUpdated(mediaMetaData);
            }
        }

        @Override // com.xpeng.airplay.service.XpAirplayManager.DefaultAirplayCallbacks, com.xpeng.airplay.service.IXpAirplayCallbacks
        public void onServerNameUpdated(String str) {
            Log.i("ProjectionStateCallback", "onServerNameUpdated: ");
            EventBusUtils.post(new ServerNameUpdateEvent());
        }
    }
}

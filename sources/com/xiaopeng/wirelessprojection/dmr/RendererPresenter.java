package com.xiaopeng.wirelessprojection.dmr;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;
import com.xiaopeng.lib.utils.LogUtils;
import com.xiaopeng.wirelessprojection.core.event.PhoneCallStateChangeEvent;
import com.xiaopeng.wirelessprojection.core.event.SingleTapEvent;
import com.xiaopeng.wirelessprojection.core.interfaces.IRendererPresenter;
import com.xiaopeng.wirelessprojection.core.interfaces.IRendererView;
import com.xiaopeng.wirelessprojection.core.utils.EventBusUtils;
import com.xiaopeng.wirelessprojection.dmr.manager.RendererControlManager;
import com.xpeng.airplay.service.MediaMetaData;
import com.xpeng.airplay.service.MediaPlayInfo;
import com.xpeng.airplay.service.MediaPlaybackInfo;
import java.net.URI;
import java.util.concurrent.atomic.AtomicInteger;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
/* loaded from: classes2.dex */
public class RendererPresenter extends Handler implements IRendererPresenter {
    private static final int EVENT_PLAYBACK_STATE_REFRESH = 666;
    private static final int PLAYBACK_REFRESH_INTERVAL = 1000;
    public static final int PLAY_RATE_PAUSE = 0;
    public static final int PLAY_RATE_PLAYING = 1;
    private static final String TAG = "RendererPresenter";
    private Size mMirrorSize;
    private MediaPlaybackInfo mPlaybackInfo;
    private AtomicInteger mPlaybackState;
    private IRendererView mView;

    @Override // com.xiaopeng.wirelessprojection.core.interfaces.IRendererPresenter
    public void onClientConnected() {
    }

    public RendererPresenter(IRendererView iRendererView, Looper looper) {
        super(looper);
        this.mView = iRendererView;
        this.mMirrorSize = new Size(0, 0);
        this.mPlaybackInfo = new MediaPlaybackInfo();
        this.mPlaybackState = new AtomicInteger(3);
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        if (message.what != EVENT_PLAYBACK_STATE_REFRESH) {
            return;
        }
        handleRefreshPlaybackInfo();
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        LogUtils.d(TAG, "surfaceCreated()");
        setMirrorSurface(surfaceHolder.getSurface());
        IRendererView iRendererView = this.mView;
        if (iRendererView == null || !iRendererView.isAlive()) {
            return;
        }
        this.mView.onSurfaceCreated();
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        LogUtils.d(TAG, "surfaceChanged(): format = " + i + ", width = " + i2 + ", height = " + i3);
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        LogUtils.d(TAG, "surfaceDestroyed()");
    }

    @Override // com.xiaopeng.wirelessprojection.core.interfaces.IRendererPresenter
    public void setMirrorSurface(Surface surface) {
        LogUtils.d(TAG, "setMirrorSurface surface=" + surface);
        RendererControlManager.instance().setMirrorSurface(surface);
    }

    @Override // com.xiaopeng.wirelessprojection.core.interfaces.IRendererPresenter
    public void start() {
        EventBusUtils.registerSafely(this);
    }

    @Override // com.xiaopeng.wirelessprojection.core.interfaces.IRendererPresenter
    public void stop() {
        stopRefreshInfo();
        EventBusUtils.unregisterSafely(this);
    }

    @Override // com.xiaopeng.wirelessprojection.core.interfaces.IRendererPresenter
    public String getServerName() {
        return RendererControlManager.instance().getServerName();
    }

    @Override // com.xiaopeng.wirelessprojection.core.interfaces.IRendererPresenter
    public void setVideoPlaybackState(int i) {
        LogUtils.i(TAG, "setVideoPlaybackState state=" + i);
        this.mPlaybackState.set(i);
        RendererControlManager.instance().setVideoPlaybackState(i);
        if (this.mPlaybackState.get() == 2 || this.mPlaybackState.get() == 3 || this.mPlaybackState.get() == 1) {
            removeMessages(EVENT_PLAYBACK_STATE_REFRESH);
        }
    }

    @Override // com.xiaopeng.wirelessprojection.core.interfaces.IRendererPresenter
    public void onMirrorStarted() {
        LogUtils.i(TAG, "onMirrorStarted");
        IRendererView iRendererView = this.mView;
        if (iRendererView != null && iRendererView.isAlive()) {
            Log.i("tangrh", "onMirrorStarted: view alive");
            this.mView.onMirrorStarted();
            return;
        }
        Log.i("tangrh", "onMirrorStarted: view not alive set status");
        RendererControlManager.instance().setMirrorStatus(true);
    }

    @Override // com.xiaopeng.wirelessprojection.core.interfaces.IRendererPresenter
    public void onMirrorStopped() {
        LogUtils.i(TAG, "onMirrorStopped");
        RendererControlManager.instance().setMirrorStatus(false);
        IRendererView iRendererView = this.mView;
        if (iRendererView == null || !iRendererView.isAlive()) {
            return;
        }
        this.mView.onMirrorStopped();
    }

    @Override // com.xiaopeng.wirelessprojection.core.interfaces.IRendererPresenter
    public void onMirrorSizeChanged(int i, int i2) {
        LogUtils.d(TAG, "onMirrorSizeChanged(): w = " + i + ", h = " + i2);
        IRendererView iRendererView = this.mView;
        if (iRendererView != null && iRendererView.isAlive()) {
            if (this.mMirrorSize.getWidth() == i && this.mMirrorSize.getHeight() == i2) {
                return;
            }
            this.mView.onMirrorSizeChanged(i, i2);
            return;
        }
        LogUtils.d(TAG, "onMirrorSizeChanged() live not alive record in manager: w = " + i + ", h = " + i2);
        if (this.mMirrorSize.getWidth() == i && this.mMirrorSize.getHeight() == i2) {
            return;
        }
        RendererControlManager.instance().setLastMirrorHeight(i2);
        RendererControlManager.instance().setLastMirrorWidth(i);
    }

    @Override // com.xiaopeng.wirelessprojection.core.interfaces.IRendererPresenter
    public void onVideoPlayStarted(MediaPlayInfo mediaPlayInfo) {
        LogUtils.i(TAG, "onVideoPlayStarted url=" + mediaPlayInfo.getUrl() + ", title=" + mediaPlayInfo.getTitle());
        try {
            new URI(mediaPlayInfo.getUrl());
            IRendererView iRendererView = this.mView;
            if (iRendererView == null || !iRendererView.isAlive()) {
                return;
            }
            if (this.mPlaybackState.get() == 0 || this.mPlaybackState.get() == 4) {
                this.mView.onVideoPlayInfoUpdated(mediaPlayInfo);
            } else {
                this.mView.onVideoPlay(mediaPlayInfo);
            }
        } catch (Exception unused) {
            LogUtils.e(TAG, "URI can not be null or malformed");
            IRendererView iRendererView2 = this.mView;
            if (iRendererView2 == null || !iRendererView2.isAlive()) {
                return;
            }
            this.mView.onClientDisconnected(3, 0);
        }
    }

    @Override // com.xiaopeng.wirelessprojection.core.interfaces.IRendererPresenter
    public void onVideoPlayStopped() {
        LogUtils.i(TAG, "onVideoPlayStopped");
        IRendererView iRendererView = this.mView;
        if (iRendererView == null || !iRendererView.isAlive()) {
            return;
        }
        this.mView.onVideoStop();
    }

    @Override // com.xiaopeng.wirelessprojection.core.interfaces.IRendererPresenter
    public void onVideoRateChanged(int i) {
        LogUtils.i(TAG, "onVideoRateChanged rate=" + i);
        IRendererView iRendererView = this.mView;
        if (iRendererView == null || !iRendererView.isAlive()) {
            return;
        }
        this.mView.onVideoRate(i);
    }

    @Override // com.xiaopeng.wirelessprojection.core.interfaces.IRendererPresenter
    public void onVideoScrubbed(int i) {
        LogUtils.i(TAG, "onVideoScrubbed pos=" + i);
        IRendererView iRendererView = this.mView;
        if (iRendererView == null || !iRendererView.isAlive()) {
            return;
        }
        this.mView.onVideoScrub(i);
    }

    @Override // com.xiaopeng.wirelessprojection.core.interfaces.IRendererPresenter
    public void onVolumeChanged(float f) {
        LogUtils.i(TAG, "onVolumeChanged vol=" + f);
        IRendererView iRendererView = this.mView;
        if (iRendererView == null || !iRendererView.isAlive()) {
            return;
        }
        this.mView.onVolumeChanged(f);
    }

    @Override // com.xiaopeng.wirelessprojection.core.interfaces.IRendererPresenter
    public void onMetaDataUpdated(MediaMetaData mediaMetaData) {
        LogUtils.i(TAG, "onMetaDataUpdated");
        IRendererView iRendererView = this.mView;
        if (iRendererView == null || !iRendererView.isAlive()) {
            return;
        }
        this.mView.onMetaDataUpdated(mediaMetaData);
    }

    @Override // com.xiaopeng.wirelessprojection.core.interfaces.IRendererPresenter
    public void onSingleTap() {
        LogUtils.i(TAG, "onSingleTap");
        IRendererView iRendererView = this.mView;
        if (iRendererView == null || !iRendererView.isAlive()) {
            return;
        }
        this.mView.onSingleTap();
    }

    @Override // com.xiaopeng.wirelessprojection.core.interfaces.IRendererPresenter
    public void onClientDisconnected() {
        RendererControlManager.instance().setMirrorStatus(false);
        this.mMirrorSize = new Size(0, 0);
        if (hasMessages(EVENT_PLAYBACK_STATE_REFRESH)) {
            removeMessages(EVENT_PLAYBACK_STATE_REFRESH);
        }
        this.mPlaybackInfo.reset();
        this.mPlaybackState.set(3);
    }

    @Override // com.xiaopeng.wirelessprojection.core.interfaces.IRendererPresenter
    public void startRefreshInfo() {
        LogUtils.i(TAG, "startRefreshInfo");
        if (hasMessages(EVENT_PLAYBACK_STATE_REFRESH)) {
            return;
        }
        sendEmptyMessageDelayed(EVENT_PLAYBACK_STATE_REFRESH, 1000L);
    }

    public void stopRefreshInfo() {
        LogUtils.i(TAG, "stopRefreshInfo");
        if (hasMessages(EVENT_PLAYBACK_STATE_REFRESH)) {
            removeMessages(EVENT_PLAYBACK_STATE_REFRESH);
        }
    }

    private void handleRefreshPlaybackInfo() {
        LogUtils.i(TAG, "handleRefreshPlaybackInfo");
        IRendererView iRendererView = this.mView;
        if (iRendererView == null || !iRendererView.isAlive()) {
            return;
        }
        this.mView.getPlaybackInfo(this.mPlaybackInfo);
        LogUtils.i(TAG, "handleRefreshPlaybackInfo mPlaybackInfo=" + this.mPlaybackInfo);
        RendererControlManager.instance().setVideoPlaybackInfo(this.mPlaybackInfo);
        sendEmptyMessageDelayed(EVENT_PLAYBACK_STATE_REFRESH, 1000L);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSingleTapEvent(SingleTapEvent singleTapEvent) {
        LogUtils.i(TAG, "onSingleTapEvent");
        onSingleTap();
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onCallStateChangeEvent(PhoneCallStateChangeEvent phoneCallStateChangeEvent) {
        IRendererView iRendererView;
        if (phoneCallStateChangeEvent == null || (iRendererView = this.mView) == null || !iRendererView.isAlive()) {
            return;
        }
        LogUtils.i(TAG, "onCallStateChangeEvent isCalling=" + phoneCallStateChangeEvent.isCalling());
        this.mView.onCallStateChange(phoneCallStateChangeEvent.isCalling());
    }
}

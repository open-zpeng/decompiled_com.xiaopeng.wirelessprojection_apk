package com.xiaopeng.wirelessprojection.core.interfaces;

import android.view.Surface;
import android.view.SurfaceHolder;
import com.xpeng.airplay.service.MediaMetaData;
import com.xpeng.airplay.service.MediaPlayInfo;
/* loaded from: classes2.dex */
public interface IRendererPresenter extends SurfaceHolder.Callback {
    String getServerName();

    void onClientConnected();

    void onClientDisconnected();

    void onMetaDataUpdated(MediaMetaData mediaMetaData);

    void onMirrorSizeChanged(int i, int i2);

    void onMirrorStarted();

    void onMirrorStopped();

    void onSingleTap();

    void onVideoPlayStarted(MediaPlayInfo mediaPlayInfo);

    void onVideoPlayStopped();

    void onVideoRateChanged(int i);

    void onVideoScrubbed(int i);

    void onVolumeChanged(float f);

    void setMirrorSurface(Surface surface);

    void setVideoPlaybackState(int i);

    void start();

    void startRefreshInfo();

    void stop();
}

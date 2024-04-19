package com.xiaopeng.wirelessprojection.core.interfaces;

import com.xpeng.airplay.service.MediaMetaData;
import com.xpeng.airplay.service.MediaPlayInfo;
import com.xpeng.airplay.service.MediaPlaybackInfo;
/* loaded from: classes2.dex */
public interface IRendererView {
    void getPlaybackInfo(MediaPlaybackInfo mediaPlaybackInfo);

    boolean isAlive();

    void onCallStateChange(boolean z);

    void onClientConnected(int i);

    void onClientDisconnected(int i, int i2);

    void onMetaDataUpdated(MediaMetaData mediaMetaData);

    void onMirrorSizeChanged(int i, int i2);

    void onMirrorStarted();

    void onMirrorStopped();

    void onSingleTap();

    void onSurfaceCreated();

    void onVideoPlay(MediaPlayInfo mediaPlayInfo);

    void onVideoPlayInfoUpdated(MediaPlayInfo mediaPlayInfo);

    void onVideoRate(int i);

    void onVideoScrub(int i);

    void onVideoStop();

    void onVolumeChanged(float f);
}

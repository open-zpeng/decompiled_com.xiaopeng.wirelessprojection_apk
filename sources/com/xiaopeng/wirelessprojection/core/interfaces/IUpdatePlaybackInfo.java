package com.xiaopeng.wirelessprojection.core.interfaces;
/* loaded from: classes2.dex */
public interface IUpdatePlaybackInfo {
    void refreshInfo();

    void updateDuration(double d);

    void updatePlaybackState(int i);

    void updatePosition(double d);

    void updateRate(int i);

    void updateVolume(float f);
}

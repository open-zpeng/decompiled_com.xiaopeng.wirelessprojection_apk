package com.xiaopeng.wirelessprojection.core.interfaces;
/* loaded from: classes2.dex */
public interface IPlayerControl {
    long getDuration();

    long getPosition();

    void pause();

    void play();

    void seek(long j);

    void stop();
}

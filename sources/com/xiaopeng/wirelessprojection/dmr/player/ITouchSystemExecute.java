package com.xiaopeng.wirelessprojection.dmr.player;
/* loaded from: classes2.dex */
public interface ITouchSystemExecute {
    void changeBrightnessImpl(float f);

    void changeSystemVolumeImpl(float f);

    AdjustInfo getBrightnessInfo();

    AdjustInfo getVolumeInfo();
}

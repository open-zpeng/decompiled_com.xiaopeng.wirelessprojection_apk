package com.xiaopeng.wirelessprojection.dmr.player;

import android.app.Activity;
import android.media.AudioManager;
import com.xiaopeng.wirelessprojection.core.BaseApp;
import com.xiaopeng.wirelessprojection.core.event.SingleTapEvent;
import com.xiaopeng.wirelessprojection.core.utils.EventBusUtils;
import com.xiaopeng.wirelessprojection.core.utils.MediaLightUtils;
/* loaded from: classes2.dex */
public class VideoTouchCallback implements IVideoTouchCallback {
    private final int VIDEO_STREAM = 3;
    private final Activity mActivity;

    public VideoTouchCallback(Activity activity) {
        this.mActivity = activity;
    }

    @Override // com.xiaopeng.wirelessprojection.dmr.player.ITouchSystemExecute
    public AdjustInfo getVolumeInfo() {
        AudioManager audioManager = (AudioManager) BaseApp.getContext().getSystemService("audio");
        return new AdjustInfo(audioManager.getStreamMinVolume(3), audioManager.getStreamMaxVolume(3), audioManager.getStreamVolume(3));
    }

    @Override // com.xiaopeng.wirelessprojection.dmr.player.ITouchSystemExecute
    public void changeSystemVolumeImpl(float f) {
        ((AudioManager) BaseApp.getContext().getSystemService("audio")).setStreamVolume(3, (int) f, 16);
    }

    @Override // com.xiaopeng.wirelessprojection.dmr.player.ITouchSystemExecute
    public AdjustInfo getBrightnessInfo() {
        return new AdjustInfo(0.0f, 1.0f, MediaLightUtils.getActivityBrightness(this.mActivity));
    }

    @Override // com.xiaopeng.wirelessprojection.dmr.player.ITouchSystemExecute
    public void changeBrightnessImpl(float f) {
        MediaLightUtils.setActivityBrightness(f, this.mActivity);
    }

    @Override // com.xiaopeng.wirelessprojection.dmr.player.IVideoTouchCallback
    public void onSingleTap() {
        EventBusUtils.post(new SingleTapEvent());
    }
}

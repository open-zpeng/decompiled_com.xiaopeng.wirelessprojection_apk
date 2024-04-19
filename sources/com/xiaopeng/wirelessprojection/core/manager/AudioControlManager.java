package com.xiaopeng.wirelessprojection.core.manager;

import android.media.AudioManager;
import com.xiaopeng.lib.utils.LogUtils;
import com.xiaopeng.wirelessprojection.core.BaseApp;
import com.xiaopeng.wirelessprojection.core.event.AudioFocusChangeEvent;
import com.xiaopeng.wirelessprojection.core.utils.EventBusUtils;
/* loaded from: classes2.dex */
public class AudioControlManager {
    public static final int VIDEO_VOLUME_STREAM = 3;
    private final String TAG;
    private final AudioManager mAudioManager;
    private AudioManager.OnAudioFocusChangeListener mFocusChangeListener;
    private boolean mFocusRequested;

    private void setVideoVolume(float f) {
    }

    /* loaded from: classes2.dex */
    private static class Holder {
        static final AudioControlManager INSTANCE = new AudioControlManager();

        private Holder() {
        }
    }

    public static AudioControlManager instance() {
        return Holder.INSTANCE;
    }

    private AudioControlManager() {
        this.TAG = "AudioControlManager";
        this.mAudioManager = (AudioManager) BaseApp.getContext().getSystemService("audio");
    }

    public void setVolume(int i, int i2) {
        if (i2 > getMaxVolume(i) || i2 < 0) {
            LogUtils.e("AudioControlManager", "illegal volume value=" + i2);
            return;
        }
        LogUtils.i("AudioControlManager", "setVolume stream=" + i + ", volume=" + i2);
        this.mAudioManager.setStreamVolume(i, i2, 5);
    }

    public int getVolume(int i) {
        int streamVolume = this.mAudioManager.getStreamVolume(i);
        LogUtils.i("AudioControlManager", "getVolume stream=" + i + ", currentVolume=" + streamVolume);
        return streamVolume;
    }

    public int getMaxVolume(int i) {
        AudioManager audioManager = this.mAudioManager;
        if (audioManager != null) {
            int streamMaxVolume = audioManager.getStreamMaxVolume(i);
            LogUtils.i("AudioControlManager", "getMaxVolume stream=" + i + ", res=" + streamMaxVolume);
            return streamMaxVolume;
        }
        return 0;
    }

    public float getAdjustVolume(int i) {
        int volume = getVolume(i);
        float streamMaxVolume = (volume * 1.0f) / this.mAudioManager.getStreamMaxVolume(i);
        LogUtils.i("AudioControlManager", "getAdjustVolume currentVolume=" + volume + ", adjustVolume=" + streamMaxVolume);
        return streamMaxVolume;
    }

    public void setAdjustVolume(int i, float f) {
        if (f < 0.0f || f > 1.0f) {
            LogUtils.e("AudioControlManager", "setAdjustVolume illegal adjustVolume=" + f);
            return;
        }
        LogUtils.i("AudioControlManager", "setAdjustVolume stream=" + i + ", adjustVolume=" + f);
        setVolume(i, (int) (f * getMaxVolume(i)));
    }

    public void requestAudioFocus(String str) {
        LogUtils.i("AudioControlManager", "requestAudioFocus:mFocusRequested=" + this.mFocusRequested + ", from=" + str);
        if (this.mFocusRequested || this.mAudioManager == null) {
            return;
        }
        AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() { // from class: com.xiaopeng.wirelessprojection.core.manager.-$$Lambda$AudioControlManager$IC5wG1uEi9aAKRTE2Jq04Xp2Ikw
            @Override // android.media.AudioManager.OnAudioFocusChangeListener
            public final void onAudioFocusChange(int i) {
                AudioControlManager.this.lambda$requestAudioFocus$0$AudioControlManager(i);
            }
        };
        this.mFocusChangeListener = onAudioFocusChangeListener;
        if (this.mAudioManager.requestAudioFocus(onAudioFocusChangeListener, 3, 2) == 0) {
            LogUtils.e("AudioControlManager", "request focus failed!");
        } else {
            LogUtils.i("AudioControlManager", "request focus successful");
        }
        this.mFocusRequested = true;
    }

    public /* synthetic */ void lambda$requestAudioFocus$0$AudioControlManager(int i) {
        LogUtils.i("AudioControlManager", "onAudioFocusChange:========" + i);
        EventBusUtils.post(new AudioFocusChangeEvent(i));
    }

    public void abandonAudioFocus(String str) {
        AudioManager audioManager;
        LogUtils.i("AudioControlManager", "abandonAudioFocus:mFocusRequested=" + this.mFocusRequested + ", from=" + str);
        if (this.mFocusRequested && (audioManager = this.mAudioManager) != null) {
            if (audioManager.abandonAudioFocus(this.mFocusChangeListener) == 0) {
                LogUtils.e("AudioControlManager", "Abandon audio focus failed!");
            } else {
                LogUtils.i("AudioControlManager", "Abandon audio focus successful.");
            }
            this.mFocusChangeListener = null;
            this.mFocusRequested = false;
        }
    }
}

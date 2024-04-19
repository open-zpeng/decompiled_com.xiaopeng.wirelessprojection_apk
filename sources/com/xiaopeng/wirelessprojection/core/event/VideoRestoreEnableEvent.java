package com.xiaopeng.wirelessprojection.core.event;
/* loaded from: classes2.dex */
public class VideoRestoreEnableEvent {
    private final boolean enabled;

    public VideoRestoreEnableEvent(boolean z) {
        this.enabled = z;
    }

    public boolean isEnabled() {
        return this.enabled;
    }
}

package com.xiaopeng.wirelessprojection.core.event;
/* loaded from: classes2.dex */
public class PhoneCallStateChangeEvent {
    private final boolean isCalling;

    public PhoneCallStateChangeEvent(boolean z) {
        this.isCalling = z;
    }

    public boolean isCalling() {
        return this.isCalling;
    }
}

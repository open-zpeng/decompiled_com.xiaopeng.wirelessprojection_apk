package com.xiaopeng.wirelessprojection.core.event;
/* loaded from: classes2.dex */
public class TrafficStatusChangeEvent {
    private final int status;

    public TrafficStatusChangeEvent(int i) {
        this.status = i;
    }

    public int getStatus() {
        return this.status;
    }

    public boolean isRunOut() {
        return this.status == 0;
    }
}

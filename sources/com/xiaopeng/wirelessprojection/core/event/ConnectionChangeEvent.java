package com.xiaopeng.wirelessprojection.core.event;
/* loaded from: classes2.dex */
public class ConnectionChangeEvent {
    public boolean connected;

    public ConnectionChangeEvent(boolean z) {
        this.connected = z;
    }

    public String toString() {
        return "ConnectionChangeEvent{connected=" + this.connected + '}';
    }
}

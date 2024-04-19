package com.xiaopeng.wirelessprojection.core.event;
/* loaded from: classes2.dex */
public class HotspotInfoChangeEvent {
    private final String name;
    private final String password;

    public HotspotInfoChangeEvent(String str, String str2) {
        this.name = str;
        this.password = str2;
    }

    public String getName() {
        return this.name;
    }

    public String getPassword() {
        return this.password;
    }

    public String toString() {
        return "HotspotInfoChangeEvent{name='" + this.name + "', password='" + this.password + "'}";
    }
}

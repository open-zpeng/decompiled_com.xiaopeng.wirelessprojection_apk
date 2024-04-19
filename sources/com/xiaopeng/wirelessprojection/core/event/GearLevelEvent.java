package com.xiaopeng.wirelessprojection.core.event;
/* loaded from: classes2.dex */
public class GearLevelEvent {
    private final int gearLevel;

    public GearLevelEvent(int i) {
        this.gearLevel = i;
    }

    public boolean isRLevel() {
        return this.gearLevel == 3;
    }

    public boolean isPLevel() {
        return this.gearLevel == 4;
    }

    public boolean isDLevel() {
        return this.gearLevel == 1;
    }

    public boolean isNLevel() {
        return this.gearLevel == 2;
    }

    public boolean isRDNLevel() {
        return isRLevel() || isDLevel() || isNLevel();
    }
}

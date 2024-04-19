package com.xiaopeng.wirelessprojection.dmr.player;

import com.xiaopeng.lib.utils.LogUtils;
import com.xiaopeng.wirelessprojection.core.utils.TimeUtils;
/* loaded from: classes2.dex */
public class AdjustInfo {
    private static final String TAG = "AdjustInfo";
    boolean available;
    private float currentValue;
    private int currentValueUI;
    float maxValue;
    int maxValueUI;
    private float minValue;
    private int minValueUI;
    float progress;
    int progressUI;

    public AdjustInfo() {
        this.available = false;
        this.minValue = 0.0f;
        this.maxValue = 0.0f;
        this.currentValue = 0.0f;
        this.minValueUI = 0;
        this.maxValueUI = 0;
        this.currentValueUI = 0;
        this.progress = 0.0f;
        this.progressUI = 0;
    }

    public AdjustInfo(float f, float f2, float f3) {
        this.available = false;
        this.minValue = 0.0f;
        this.maxValue = 0.0f;
        this.currentValue = 0.0f;
        this.minValueUI = 0;
        this.maxValueUI = 0;
        this.currentValueUI = 0;
        this.progress = 0.0f;
        this.progressUI = 0;
        this.available = true;
        this.minValue = f;
        this.maxValue = f2;
        this.currentValue = f3;
        LogUtils.i(TAG, "new AdjustInfo minValue=" + f + ", maxValue=" + f2 + ", currentValue=" + f3);
        absUIValue();
    }

    private void absUIValue() {
        float f = this.minValue;
        if (f < 0.0f) {
            float f2 = 0.0f - f;
            this.minValueUI += ((int) (f + f2)) * 100;
            this.maxValueUI += ((int) (this.maxValue + f2)) * 100;
            this.currentValueUI += ((int) (this.currentValue + f2)) * 100;
            return;
        }
        this.minValueUI += ((int) f) * 100;
        float f3 = 100;
        this.maxValueUI = (int) (this.maxValueUI + (this.maxValue * f3));
        this.currentValueUI = (int) (this.currentValueUI + (this.currentValue * f3));
    }

    public void addIncrease(float f) {
        float f2 = this.currentValue;
        float f3 = this.maxValue;
        this.progress = TimeUtils.adjustValueBoundF(f2 + (f * f3), f3, this.minValue);
        int i = this.maxValueUI;
        this.progressUI = (int) TimeUtils.adjustValueBoundF(this.currentValueUI + (f * i), i, this.minValueUI);
    }

    public double getUIRate() {
        return this.progressUI / this.maxValueUI;
    }
}

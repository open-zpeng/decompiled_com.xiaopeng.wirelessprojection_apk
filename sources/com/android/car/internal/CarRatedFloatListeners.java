package com.android.car.internal;

import android.util.SparseArray;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes.dex */
public class CarRatedFloatListeners<T> {
    private static final float NANOSECOND_PER_SECOND = 1.0E9f;
    private float mUpdateRate;
    private final Map<T, Float> mListenersToRate = new HashMap(4);
    private final Map<T, Long> mListenersUpdateTime = new HashMap(4);
    protected SparseArray<Long> mAreaIdToLastUpdateTime = new SparseArray<>();

    /* JADX INFO: Access modifiers changed from: protected */
    public CarRatedFloatListeners(float f) {
        this.mUpdateRate = -2.14748365E9f;
        this.mUpdateRate = f;
    }

    public boolean contains(T t) {
        return this.mListenersToRate.containsKey(t);
    }

    public float getRate() {
        return this.mUpdateRate;
    }

    public boolean remove(T t) {
        this.mListenersToRate.remove(t);
        this.mListenersUpdateTime.remove(t);
        if (this.mListenersToRate.isEmpty()) {
            return false;
        }
        Float f = (Float) Collections.max(this.mListenersToRate.values());
        if (Float.compare(f.floatValue(), this.mUpdateRate) != 0) {
            this.mUpdateRate = f.floatValue();
            return true;
        }
        return false;
    }

    public boolean isEmpty() {
        return this.mListenersToRate.isEmpty();
    }

    public boolean addAndUpdateRate(T t, float f) {
        Float put = this.mListenersToRate.put(t, Float.valueOf(f));
        this.mListenersUpdateTime.put(t, 0L);
        if (this.mUpdateRate < f) {
            this.mUpdateRate = f;
            return true;
        } else if (put == null || Float.compare(put.floatValue(), this.mUpdateRate) != 0) {
            return false;
        } else {
            Float f2 = (Float) Collections.max(this.mListenersToRate.values());
            if (Float.compare(f2.floatValue(), this.mUpdateRate) != 0) {
                this.mUpdateRate = f2.floatValue();
                return true;
            }
            return false;
        }
    }

    public boolean needUpdateForSelectedListener(T t, long j) {
        Long l = this.mListenersUpdateTime.get(t);
        Float f = this.mListenersToRate.get(t);
        if (l != null && f != null) {
            if (f.floatValue() == 0.0f) {
                return true;
            }
            if (l.longValue() <= j) {
                this.mListenersUpdateTime.put(t, Long.valueOf(j + Float.valueOf(NANOSECOND_PER_SECOND / f.floatValue()).longValue()));
                return true;
            }
        }
        return false;
    }

    public boolean needUpdateForAreaId(int i, long j) {
        if (j >= this.mAreaIdToLastUpdateTime.get(i, 0L).longValue()) {
            this.mAreaIdToLastUpdateTime.put(i, Long.valueOf(j));
            return true;
        }
        return false;
    }

    public Collection<T> getListeners() {
        return this.mListenersToRate.keySet();
    }
}

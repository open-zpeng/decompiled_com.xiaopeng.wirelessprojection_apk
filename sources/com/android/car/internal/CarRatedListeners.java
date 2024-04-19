package com.android.car.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes.dex */
public class CarRatedListeners<EventListenerType> {
    private int mUpdateRate;
    private final Map<EventListenerType, Integer> mListenersToRate = new HashMap(4);
    protected long mLastUpdateTime = -1;

    /* JADX INFO: Access modifiers changed from: protected */
    public CarRatedListeners(int i) {
        this.mUpdateRate = i;
    }

    public boolean contains(EventListenerType eventlistenertype) {
        return this.mListenersToRate.containsKey(eventlistenertype);
    }

    public int getRate() {
        return this.mUpdateRate;
    }

    public boolean remove(EventListenerType eventlistenertype) {
        this.mListenersToRate.remove(eventlistenertype);
        if (this.mListenersToRate.isEmpty()) {
            return false;
        }
        Integer num = (Integer) Collections.min(this.mListenersToRate.values());
        if (num.intValue() != this.mUpdateRate) {
            this.mUpdateRate = num.intValue();
            return true;
        }
        return false;
    }

    public boolean isEmpty() {
        return this.mListenersToRate.isEmpty();
    }

    public boolean addAndUpdateRate(EventListenerType eventlistenertype, int i) {
        Integer put = this.mListenersToRate.put(eventlistenertype, Integer.valueOf(i));
        if (this.mUpdateRate > i) {
            this.mUpdateRate = i;
            return true;
        } else if (put == null || put.intValue() != this.mUpdateRate) {
            return false;
        } else {
            this.mUpdateRate = ((Integer) Collections.min(this.mListenersToRate.values())).intValue();
            return false;
        }
    }

    public Collection<EventListenerType> getListeners() {
        return this.mListenersToRate.keySet();
    }
}

package com.xiaopeng.wirelessprojection.dmr.player;

import android.view.GestureDetector;
import android.view.MotionEvent;
/* loaded from: classes2.dex */
public class PlayerTapInterceptor extends GestureDetector.SimpleOnGestureListener {
    private VideoTouchCallback videoTouchCallback;

    public PlayerTapInterceptor(VideoTouchCallback videoTouchCallback) {
        this.videoTouchCallback = videoTouchCallback;
    }

    @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnDoubleTapListener
    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
        this.videoTouchCallback.onSingleTap();
        return true;
    }
}

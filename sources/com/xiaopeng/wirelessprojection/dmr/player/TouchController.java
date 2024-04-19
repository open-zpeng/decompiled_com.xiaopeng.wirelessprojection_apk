package com.xiaopeng.wirelessprojection.dmr.player;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
/* loaded from: classes2.dex */
public class TouchController {
    private View mActionView;
    private VideoTouchCallback mVideoTouchCallback;
    private View mView;
    private GestureDetector tapInterceptor;
    private PlayerTouchInterceptor touchInterceptor;

    public TouchController() {
    }

    public TouchController(View view, View view2, VideoTouchCallback videoTouchCallback) {
        this.mView = view;
        this.mActionView = view2;
        this.mVideoTouchCallback = videoTouchCallback;
        this.touchInterceptor = new PlayerTouchInterceptor(view, view2, videoTouchCallback);
        this.tapInterceptor = new GestureDetector(view.getContext(), new PlayerTapInterceptor(videoTouchCallback));
        try {
            view.setOnTouchListener(new View.OnTouchListener() { // from class: com.xiaopeng.wirelessprojection.dmr.player.-$$Lambda$TouchController$YlPAWVYwuabebroZzrOJziLdMAM
                @Override // android.view.View.OnTouchListener
                public final boolean onTouch(View view3, MotionEvent motionEvent) {
                    return TouchController.this.lambda$new$0$TouchController(view3, motionEvent);
                }
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public /* synthetic */ boolean lambda$new$0$TouchController(View view, MotionEvent motionEvent) {
        return this.touchInterceptor.onTouch(view, motionEvent) || this.tapInterceptor.onTouchEvent(motionEvent);
    }
}

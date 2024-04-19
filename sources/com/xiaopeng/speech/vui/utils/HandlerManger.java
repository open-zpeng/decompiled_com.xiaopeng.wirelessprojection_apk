package com.xiaopeng.speech.vui.utils;

import android.os.Handler;
import android.os.Looper;
/* loaded from: classes2.dex */
public class HandlerManger {
    private static final String TAG = "HandlerManger";
    private static HandlerManger mHandlerManager = new HandlerManger();
    private RecyclerViewUpdateSceneListener mListener;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Runnable mRun = new Runnable() { // from class: com.xiaopeng.speech.vui.utils.HandlerManger.1
        @Override // java.lang.Runnable
        public void run() {
            if (HandlerManger.this.mListener != null) {
                LogUtils.i(HandlerManger.TAG, "onUpdateScene ==  ");
                HandlerManger.this.mListener.onUpdateScene();
            }
        }
    };

    /* loaded from: classes2.dex */
    public interface RecyclerViewUpdateSceneListener {
        void onUpdateScene();
    }

    private HandlerManger() {
    }

    public static HandlerManger getInstance() {
        return mHandlerManager;
    }

    public void submit(RecyclerViewUpdateSceneListener recyclerViewUpdateSceneListener, long j) {
        if (recyclerViewUpdateSceneListener == null) {
            return;
        }
        this.mListener = recyclerViewUpdateSceneListener;
        if (this.mHandler.hasCallbacks(this.mRun)) {
            LogUtils.i(TAG, "removeCallbacks === ");
            this.mHandler.removeCallbacks(this.mRun);
        }
        this.mHandler.postDelayed(this.mRun, j);
    }
}

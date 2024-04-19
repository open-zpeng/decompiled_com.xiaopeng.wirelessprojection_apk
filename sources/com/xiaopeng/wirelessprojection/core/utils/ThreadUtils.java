package com.xiaopeng.wirelessprojection.core.utils;

import android.os.HandlerThread;
import com.xiaopeng.wirelessprojection.core.interfaces.Source;
import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;
/* loaded from: classes2.dex */
public class ThreadUtils {
    private static Scheduler sWorkerScheduler;
    private static HandlerThread sWorkerThread;

    static {
        HandlerThread handlerThread = new HandlerThread("workerThread");
        sWorkerThread = handlerThread;
        handlerThread.start();
        sWorkerScheduler = AndroidSchedulers.from(sWorkerThread.getLooper());
    }

    public static Disposable postWorker(final Runnable runnable) {
        return Rx2Utils.getFlowable(new Source<Boolean>() { // from class: com.xiaopeng.wirelessprojection.core.utils.ThreadUtils.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // com.xiaopeng.wirelessprojection.core.interfaces.Source
            public Boolean call() throws Exception {
                runnable.run();
                return true;
            }
        }).subscribeOn(sWorkerScheduler).subscribe();
    }

    public static Disposable postBackground(final Runnable runnable) {
        if (runnable == null) {
            return null;
        }
        return Rx2Utils.getFlowableOnIo(new Source<Boolean>() { // from class: com.xiaopeng.wirelessprojection.core.utils.ThreadUtils.2
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // com.xiaopeng.wirelessprojection.core.interfaces.Source
            public Boolean call() throws Exception {
                runnable.run();
                return true;
            }
        }).subscribe();
    }

    public static Disposable postBackground(final Runnable runnable, long j) {
        if (runnable == null) {
            return null;
        }
        return Flowable.timer(j, TimeUnit.MILLISECONDS).observeOn(Schedulers.io()).subscribe(new Consumer<Long>() { // from class: com.xiaopeng.wirelessprojection.core.utils.ThreadUtils.3
            @Override // io.reactivex.functions.Consumer
            public void accept(Long l) throws Exception {
                runnable.run();
            }
        });
    }

    public static Disposable postMainThread(final Runnable runnable) {
        if (runnable == null) {
            return null;
        }
        return Rx2Utils.getFlowableOnMain(new Source<Boolean>() { // from class: com.xiaopeng.wirelessprojection.core.utils.ThreadUtils.4
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // com.xiaopeng.wirelessprojection.core.interfaces.Source
            public Boolean call() throws Exception {
                runnable.run();
                return true;
            }
        }).subscribe();
    }

    public static Disposable postMainThread(final Runnable runnable, long j) {
        if (runnable == null) {
            return null;
        }
        return Flowable.timer(j, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Long>() { // from class: com.xiaopeng.wirelessprojection.core.utils.ThreadUtils.5
            @Override // io.reactivex.functions.Consumer
            public void accept(Long l) throws Exception {
                runnable.run();
            }
        });
    }

    public static Scheduler getWorkerScheduler() {
        return sWorkerScheduler;
    }
}

package com.xiaopeng.wirelessprojection.core.utils;

import com.xiaopeng.lib.utils.LogUtils;
import com.xiaopeng.wirelessprojection.core.interfaces.Source;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.CheckReturnValue;
import io.reactivex.annotations.SchedulerSupport;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
/* loaded from: classes2.dex */
public class Rx2Utils {
    public static final Consumer<Throwable> EMPTY_THROWABLE_CONSUMER = new Consumer<Throwable>() { // from class: com.xiaopeng.wirelessprojection.core.utils.Rx2Utils.2
        public String toString() {
            return "ThrowableConsumer";
        }

        @Override // io.reactivex.functions.Consumer
        public void accept(Throwable th) throws Exception {
            if (th == null) {
                th = new NullPointerException("onError called with null. Null values are generally not allowed in 2.x operators and sources.");
            }
            LogUtils.e("Rx2Util", "emptyThrowable accept", th);
        }
    };

    public static void init() {
        RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() { // from class: com.xiaopeng.wirelessprojection.core.utils.Rx2Utils.1
            @Override // io.reactivex.functions.Consumer
            public void accept(Throwable th) throws Exception {
                LogUtils.e("Rx2Util", "errorHandler accept", th);
            }
        });
    }

    public static Consumer<Throwable> emptyThrowable() {
        return EMPTY_THROWABLE_CONSUMER;
    }

    @CheckReturnValue
    public static <T> Flowable<T> getFlowableOnIo(Source<T> source) {
        return getFlowable(source).subscribeOn(Schedulers.io());
    }

    @CheckReturnValue
    public static <T> Flowable<T> getFlowableOnMain(Source<T> source) {
        return getFlowable(source).subscribeOn(AndroidSchedulers.mainThread());
    }

    @SchedulerSupport("none")
    @CheckReturnValue
    public static <T> Flowable<T> getFlowable(Source<T> source) {
        return getFlowable(source, BackpressureStrategy.BUFFER);
    }

    @SchedulerSupport("none")
    @CheckReturnValue
    public static <T> Flowable<T> getFlowable(final Source<T> source, BackpressureStrategy backpressureStrategy) {
        return Flowable.create(new FlowableOnSubscribe<T>() { // from class: com.xiaopeng.wirelessprojection.core.utils.Rx2Utils.3
            /* JADX WARN: Multi-variable type inference failed */
            @Override // io.reactivex.FlowableOnSubscribe
            public void subscribe(FlowableEmitter<T> flowableEmitter) {
                try {
                    if (!flowableEmitter.isCancelled()) {
                        Object call = Source.this.call();
                        if (call == null) {
                            LogUtils.e("Rx2Util", "getFlowable:" + Source.this.toString());
                            throw new RuntimeException("getFlowable call() return null:" + Source.this.toString());
                        }
                        flowableEmitter.onNext(call);
                    }
                    if (flowableEmitter.isCancelled()) {
                        return;
                    }
                    flowableEmitter.onComplete();
                } catch (Exception e) {
                    flowableEmitter.tryOnError(Exceptions.propagate(e));
                } catch (Throwable th) {
                    flowableEmitter.tryOnError(Exceptions.propagate(th));
                }
            }
        }, backpressureStrategy);
    }

    @CheckReturnValue
    public static <T> Observable<T> getObservableOnIo(Source<T> source) {
        return getObservable(source).subscribeOn(Schedulers.io());
    }

    @CheckReturnValue
    public static <T> Observable<T> getObservableOnMain(Source<T> source) {
        return getObservable(source).subscribeOn(AndroidSchedulers.mainThread());
    }

    @SchedulerSupport("none")
    @CheckReturnValue
    public static <T> Observable<T> getObservable(final Source<T> source) {
        return Observable.create(new ObservableOnSubscribe<T>() { // from class: com.xiaopeng.wirelessprojection.core.utils.Rx2Utils.4
            /* JADX WARN: Multi-variable type inference failed */
            @Override // io.reactivex.ObservableOnSubscribe
            public void subscribe(ObservableEmitter<T> observableEmitter) throws Exception {
                try {
                    if (!observableEmitter.isDisposed()) {
                        Object call = Source.this.call();
                        if (call == null) {
                            LogUtils.e("Rx2Util", "getObservable:" + Source.this.toString());
                            throw new RuntimeException("getObservable call() return null:" + Source.this.toString());
                        }
                        observableEmitter.onNext(call);
                    }
                    if (observableEmitter.isDisposed()) {
                        return;
                    }
                    observableEmitter.onComplete();
                } catch (Exception e) {
                    observableEmitter.tryOnError(Exceptions.propagate(e));
                }
            }
        });
    }
}

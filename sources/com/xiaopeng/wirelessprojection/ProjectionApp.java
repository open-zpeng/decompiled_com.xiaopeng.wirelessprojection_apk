package com.xiaopeng.wirelessprojection;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import com.xiaopeng.lib.framework.carcontrollermodule.CarControllerModuleEntry;
import com.xiaopeng.lib.framework.module.Module;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICarControllerService;
import com.xiaopeng.lib.utils.LogUtils;
import com.xiaopeng.wirelessprojection.core.BaseApp;
import com.xiaopeng.wirelessprojection.core.event.GearLevelEvent;
import com.xiaopeng.wirelessprojection.core.event.IgStatusOffEvent;
import com.xiaopeng.wirelessprojection.core.event.XpengLabSwitchChangeEvent;
import com.xiaopeng.wirelessprojection.core.manager.ActivityManager;
import com.xiaopeng.wirelessprojection.core.manager.ContentObserverManager;
import com.xiaopeng.wirelessprojection.core.manager.ProtocolManager;
import com.xiaopeng.wirelessprojection.core.manager.XUIServiceManager;
import com.xiaopeng.wirelessprojection.core.receiver.ConnectionChangeReceiver;
import com.xiaopeng.wirelessprojection.core.receiver.HomeKeyReceiver;
import com.xiaopeng.wirelessprojection.core.receiver.HotspotStateChangeReceiver;
import com.xiaopeng.wirelessprojection.core.receiver.PhoneStateChangeReceiver;
import com.xiaopeng.wirelessprojection.core.receiver.TrafficStatusChangeReceiver;
import com.xiaopeng.wirelessprojection.core.receiver.VideoRestoreStatusReceiver;
import com.xiaopeng.wirelessprojection.core.utils.AppUtils;
import com.xiaopeng.wirelessprojection.core.utils.CarHardwareHelper;
import com.xiaopeng.wirelessprojection.core.utils.EventBusUtils;
import com.xiaopeng.wirelessprojection.core.utils.HotspotUtils;
import com.xiaopeng.wirelessprojection.core.utils.ThreadUtils;
import com.xiaopeng.wirelessprojection.core.utils.ToastUtils;
import com.xiaopeng.wirelessprojection.core.utils.TrafficInfoUtils;
import com.xiaopeng.wirelessprojection.dmr.manager.RendererControlManager;
import com.xiaopeng.xui.Xui;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
/* loaded from: classes2.dex */
public class ProjectionApp extends BaseApp {
    private static final String TAG = "ProjectionApp";
    private ConnectionChangeReceiver mConnectionChangeReceiver = new ConnectionChangeReceiver();
    private PhoneStateChangeReceiver mPhoneStateChangeReceiver = new PhoneStateChangeReceiver();
    private HomeKeyReceiver mHomeKeyReceiver = new HomeKeyReceiver();
    private TrafficStatusChangeReceiver mTrafficStatusChangeReceiver = new TrafficStatusChangeReceiver();
    private HotspotStateChangeReceiver mHotspotStateChangeReceiver = new HotspotStateChangeReceiver();
    private VideoRestoreStatusReceiver mVideoRestoreStatusReceiver = new VideoRestoreStatusReceiver();
    private boolean isHomeBackGround = false;

    @Override // com.xiaopeng.wirelessprojection.core.BaseApp, android.app.Application
    public void onCreate() {
        String currentProcessName = AppUtils.getCurrentProcessName(this);
        String versionName = AppUtils.getVersionName(this);
        LogUtils.i(TAG, "<<<< ProjectionApp.onCreate()... currentProcessName:" + currentProcessName);
        LogUtils.i(TAG, "<<<< versionName:" + versionName);
        super.onCreate();
        initApp();
    }

    private void initApp() {
        long currentTimeMillis = System.currentTimeMillis();
        Xui.init(this);
        EventBusUtils.init();
        EventBusUtils.registerSafely(this);
        registerModule();
        ProtocolManager.instance().init(true);
        RendererControlManager.instance().init();
        ContentObserverManager.instance().init();
        XUIServiceManager.instance().init();
        this.mConnectionChangeReceiver.registerReceiver(this);
        this.mPhoneStateChangeReceiver.registerReceiver(this);
        this.mHomeKeyReceiver.registerReceiver(this);
        this.mTrafficStatusChangeReceiver.registerReceiver(this);
        this.mHotspotStateChangeReceiver.registerReceiver(this);
        this.mVideoRestoreStatusReceiver.registerReceiver(this);
        registerActivityLifecycleCallbacks(new CustomActivityLifecycleCallbacks());
        BaseApp.sInit = true;
        LogUtils.i(TAG, "initApp: cost " + (System.currentTimeMillis() - currentTimeMillis) + "ms");
    }

    private void registerModule() {
        Module.register(CarControllerModuleEntry.class, new CarControllerModuleEntry(getApplicationContext()));
        Module.get(CarControllerModuleEntry.class).get(ProjectionApp.class);
    }

    private void onCarServiceConnected() {
        LogUtils.i(TAG, "onCarServiceConnected");
        CarHardwareHelper.instance().init();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ICarControllerService.ConnectCarEventMsg connectCarEventMsg) {
        if (connectCarEventMsg.getData().booleanValue()) {
            LogUtils.i(TAG, "car service is connected!");
            onCarServiceConnected();
            return;
        }
        CarHardwareHelper.instance().setServiceConnected(false);
        LogUtils.e(TAG, "car service is disconnected!");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onXpengLabSwitchEvent(XpengLabSwitchChangeEvent xpengLabSwitchChangeEvent) {
        judgeFinishAll("Lab");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGearLevelEvent(GearLevelEvent gearLevelEvent) {
        judgeFinishAll("Gear");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onIgOffEvent(IgStatusOffEvent igStatusOffEvent) {
        ActivityManager.instance().finishAllActivity("ProjectionAppigOff");
    }

    private void judgeFinishAll(String str) {
        if (AppUtils.isAllowUseApp()) {
            return;
        }
        if (str.equals("Gear")) {
            ActivityManager.instance().finishAllActivity("GEAR");
        } else {
            ActivityManager.instance().finishAllActivity(TAG);
        }
    }

    @Override // android.app.Application
    public void onTerminate() {
        LogUtils.i(TAG, "<<<< ProjectionApp.onTerminate()...");
        this.mConnectionChangeReceiver.unregisterReceiver(this);
        this.mPhoneStateChangeReceiver.unregisterReceiver(this);
        this.mHomeKeyReceiver.unregisterReceiver(this);
        this.mTrafficStatusChangeReceiver.unregisterReceiver(this);
        this.mHotspotStateChangeReceiver.unregisterReceiver(this);
        this.mVideoRestoreStatusReceiver.unregisterReceiver(this);
        RendererControlManager.instance().unregister();
        super.onTerminate();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public class CustomActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
        CustomActivityLifecycleCallbacks() {
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public void onActivityCreated(Activity activity, Bundle bundle) {
            LogUtils.i(ProjectionApp.TAG, "onActivityCreated:" + activity.getClass().getSimpleName());
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public void onActivityStarted(Activity activity) {
            LogUtils.d(ProjectionApp.TAG, "onActivityStarted:" + activity.getClass().getSimpleName());
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public void onActivityResumed(Activity activity) {
            LogUtils.d(ProjectionApp.TAG, "onActivityResumed:" + activity.getClass().getSimpleName());
            if (activity.getClass().getSimpleName().contains("HomeActivity")) {
                LogUtils.d(ProjectionApp.TAG, "onActivityResumed: HomeActivity");
                ProjectionApp.this.isHomeBackGround = false;
            }
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public void onActivityPaused(Activity activity) {
            LogUtils.d(ProjectionApp.TAG, "onActivityPaused:" + activity.getClass().getSimpleName());
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public void onActivityStopped(Activity activity) {
            LogUtils.d(ProjectionApp.TAG, "onActivityStopped:" + activity.getClass().getSimpleName());
            if (activity.getClass().getSimpleName().contains("HomeActivity")) {
                LogUtils.d(ProjectionApp.TAG, "onActivityStopped: HomeActivity");
                ProjectionApp.this.isHomeBackGround = true;
                if (ActivityManager.instance().getActivitySize() <= 1) {
                    ProjectionApp.this.closeHotSpot();
                }
            }
            if (activity.getClass().getSimpleName().contains("RendererActivity") && ProjectionApp.this.isHomeBackGround) {
                LogUtils.d(ProjectionApp.TAG, "onActivityStopped: RendererActivity");
                ProjectionApp.this.closeHotSpot();
            }
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
            LogUtils.d(ProjectionApp.TAG, "onActivitySaveInstanceState:" + activity.getClass().getSimpleName());
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public void onActivityDestroyed(Activity activity) {
            LogUtils.d(ProjectionApp.TAG, "onActivityDestroyed:" + activity.getClass().getSimpleName());
            if (ProjectionApp.this.isHomeBackGround && activity.getClass().getSimpleName().contains("RendererActivity")) {
                LogUtils.d(ProjectionApp.TAG, "onActivityDestroyed: RendererActivity");
                ProjectionApp.this.closeHotSpot();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void closeHotSpot() {
        HotspotUtils.closeHotspot();
        final String formattedFromByteLong = TrafficInfoUtils.getFormattedFromByteLong(RendererControlManager.instance().getTrafficCostByte());
        ThreadUtils.postMainThread(new Runnable() { // from class: com.xiaopeng.wirelessprojection.ProjectionApp.1
            @Override // java.lang.Runnable
            public void run() {
                ToastUtils.showToast(ProjectionApp.this.getString(R.string.traffic_toast_cost, new Object[]{formattedFromByteLong}));
            }
        });
    }
}

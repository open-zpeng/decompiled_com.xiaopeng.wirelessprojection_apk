package com.xiaopeng.wirelessprojection.home;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.wifi.WifiClient;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.RequestCallback;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.IInputController;
import com.xiaopeng.lib.utils.LogUtils;
import com.xiaopeng.libtheme.ThemeManager;
import com.xiaopeng.wirelessprojection.core.BaseActivity;
import com.xiaopeng.wirelessprojection.core.BaseApp;
import com.xiaopeng.wirelessprojection.core.apirouter.IpcRouterService;
import com.xiaopeng.wirelessprojection.core.event.AppGoBackgroundEvent;
import com.xiaopeng.wirelessprojection.core.event.ConnectedClientChangeEvent;
import com.xiaopeng.wirelessprojection.core.event.ConnectionChangeEvent;
import com.xiaopeng.wirelessprojection.core.event.DayNightChangeEvent;
import com.xiaopeng.wirelessprojection.core.event.HomeKeyPressEvent;
import com.xiaopeng.wirelessprojection.core.event.HotspotChangeEvent;
import com.xiaopeng.wirelessprojection.core.event.HotspotInfoChangeEvent;
import com.xiaopeng.wirelessprojection.core.event.ServerNameUpdateEvent;
import com.xiaopeng.wirelessprojection.core.event.TrafficInfoResultEvent;
import com.xiaopeng.wirelessprojection.core.event.TrafficStatusChangeEvent;
import com.xiaopeng.wirelessprojection.core.event.VideoRestoreEnableEvent;
import com.xiaopeng.wirelessprojection.core.manager.ProtocolManager;
import com.xiaopeng.wirelessprojection.core.utils.EventBusUtils;
import com.xiaopeng.wirelessprojection.core.utils.HotspotUtils;
import com.xiaopeng.wirelessprojection.core.utils.SharedPreferencesUtils;
import com.xiaopeng.wirelessprojection.core.utils.ThreadUtils;
import com.xiaopeng.wirelessprojection.core.utils.ToastUtils;
import com.xiaopeng.wirelessprojection.core.utils.TrafficInfoUtils;
import com.xiaopeng.wirelessprojection.core.view.CustomXTextView;
import com.xiaopeng.wirelessprojection.dmr.RendererActivity;
import com.xiaopeng.wirelessprojection.dmr.manager.RendererControlManager;
import com.xiaopeng.wirelessprojection.home.view.CustomDialogContainer;
import com.xiaopeng.xui.app.XDialog;
import com.xiaopeng.xui.app.XDialogInterface;
import com.xiaopeng.xui.view.XView;
import com.xiaopeng.xui.widget.XButton;
import com.xiaopeng.xui.widget.XConstraintLayout;
import com.xiaopeng.xui.widget.XSwitch;
import com.xiaopeng.xui.widget.XTextView;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
/* loaded from: classes2.dex */
public class HomeActivity extends BaseActivity {
    private static Disposable mCloseHotspotDisposable;
    private static HomeActivity sInstance;
    private XButton mBtnVideoRestore;
    private XConstraintLayout mClHotspotPwDetail;
    private CustomDialogContainer mCustomDialogContainer;
    private Disposable mDisposable;
    private XSwitch mSwitchHotspot;
    private XTextView mTvEduContent;
    private XTextView mTvHotspotClients;
    private XTextView mTvHotspotName;
    private XView mTvHotspotPwRefresh;
    private XButton mTvTrafficBuy;
    private XTextView mTvTrafficCost;
    private CustomXTextView mTvTrafficRemain;
    private CustomXTextView mTvTrafficTips;
    private XTextView mTvTrafficWifiDetail;
    private final String TAG = "HomeActivity";
    private final String REFRESH_PASSWORD_SUCCESS = "热点密码已刷新，请重新连接";
    private int mTipState = -1;
    private final int WAIT_SERVICE_DELAY = 200;
    private final int WAIT_HOTSPOT_DELAY = 1000;
    private final int REFRESH_CLIENTS_DELAY = 10000;
    private final int TRAFFIC_INFO_DELAY = IInputController.KEYCODE_KNOB_WIND_SPD_UP;
    private final int DELAY_30_SECONDS = 30000;
    private final Observer<Long> observer = new Observer<Long>() { // from class: com.xiaopeng.wirelessprojection.home.HomeActivity.1
        @Override // io.reactivex.Observer
        public void onComplete() {
        }

        @Override // io.reactivex.Observer
        public void onError(Throwable th) {
        }

        @Override // io.reactivex.Observer
        public void onSubscribe(Disposable disposable) {
            if (HomeActivity.this.mDisposable == null) {
                HomeActivity.this.mDisposable = disposable;
            }
        }

        @Override // io.reactivex.Observer
        public void onNext(Long l) {
            LogUtils.i("HomeActivity", "update flow info after a min");
            HomeActivity.this.updateFlowInfo();
        }
    };
    private final WifiManager mWifiManager = (WifiManager) BaseApp.getContext().getSystemService("wifi");
    private final WifiManager.SoftApCallback mSoftApCallBack = new WifiManager.SoftApCallback() { // from class: com.xiaopeng.wirelessprojection.home.HomeActivity.2
        public void onNumClientsChanged(int i) {
        }

        public void onStateChanged(int i, int i2) {
        }

        public void onClientsUpdated(List<WifiClient> list) {
            super.onClientsUpdated(list);
            HomeActivity.this.refreshConnectedClientsByNum(list.size());
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHomeKeyPressEvent(HomeKeyPressEvent homeKeyPressEvent) {
    }

    private void createFlowRefresh() {
        Observable.interval(60L, TimeUnit.SECONDS).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this.observer);
    }

    public static final HomeActivity instance() {
        return sInstance;
    }

    public static void show() {
        Context context = BaseApp.getContext();
        Intent intent = new Intent(context, HomeActivity.class);
        intent.addFlags(335560704);
        context.startActivity(intent);
    }

    public static void hide() {
        HomeActivity homeActivity = sInstance;
        if (homeActivity == null || homeActivity.isFinishing()) {
            return;
        }
        sInstance.finish();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.wirelessprojection.core.BaseActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        LogUtils.i("HomeActivity", "onCreate");
        setContentView(R.layout.activity_home_xos_5);
        requestPermissions();
        EventBusUtils.registerSafely(this);
        this.mWifiManager.registerSoftApCallback(this.mSoftApCallBack, getMainThreadHandler());
        this.mSwitchHotspot = (XSwitch) findViewById(R.id.s_hotspot_switch);
        this.mTvHotspotName = (XTextView) findViewById(R.id.tv_hotspot_name_info);
        this.mTvHotspotPwRefresh = (XView) findViewById(R.id.tv_hotspot_pass_refresh);
        this.mClHotspotPwDetail = (XConstraintLayout) findViewById(R.id.cl_hotspot_pass_detail);
        this.mTvHotspotClients = (XTextView) findViewById(R.id.tv_hotspot_devices);
        this.mBtnVideoRestore = (XButton) findViewById(R.id.btn_video_play_restore);
        this.mTvTrafficBuy = (XButton) findViewById(R.id.btn_traffic_buy);
        this.mTvTrafficRemain = (CustomXTextView) findViewById(R.id.tv_traffic_remain_detail);
        this.mTvTrafficCost = (XTextView) findViewById(R.id.tv_traffic_cost_detail);
        this.mTvTrafficTips = (CustomXTextView) findViewById(R.id.tv_traffic_tips);
        this.mTvTrafficWifiDetail = (XTextView) findViewById(R.id.tv_traffic_info_tip_wifi_detail);
        this.mTvEduContent = (XTextView) findViewById(R.id.tv_hotspot_projection_content);
        releaseDisposable();
        XSwitch xSwitch = this.mSwitchHotspot;
        if (xSwitch != null) {
            xSwitch.setChecked(true, false);
            this.mSwitchHotspot.setOnInterceptListener(new XSwitch.OnInterceptListener() { // from class: com.xiaopeng.wirelessprojection.home.-$$Lambda$HomeActivity$SEle6ss69uAeGg9MZAhu_zbE1dA
                @Override // com.xiaopeng.xui.widget.XSwitch.OnInterceptListener
                public final boolean onInterceptCheck(View view, boolean z) {
                    return HomeActivity.this.lambda$onCreate$0$HomeActivity(view, z);
                }
            });
        }
        setHotspotInfo(HotspotUtils.getHotspotName(), HotspotUtils.getHotspotPassword());
        XView xView = this.mTvHotspotPwRefresh;
        if (xView != null) {
            xView.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.wirelessprojection.home.-$$Lambda$HomeActivity$k2iwWqX565itAZ2XhYGbizcWIqY
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    HomeActivity.this.lambda$onCreate$1$HomeActivity(view);
                }
            });
        }
        XButton xButton = this.mTvTrafficBuy;
        if (xButton != null) {
            xButton.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.wirelessprojection.home.-$$Lambda$HomeActivity$m6fUfyK_661kX7BvlNNvnmy0f6U
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    HomeActivity.this.lambda$onCreate$2$HomeActivity(view);
                }
            });
        }
        XTextView xTextView = this.mTvTrafficWifiDetail;
        if (xTextView != null) {
            xTextView.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.wirelessprojection.home.-$$Lambda$HomeActivity$W_rtbhqr_3cyYFefv5FD2vVL18k
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    HomeActivity.this.lambda$onCreate$3$HomeActivity(view);
                }
            });
        }
        XButton xButton2 = this.mBtnVideoRestore;
        if (xButton2 != null) {
            xButton2.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.wirelessprojection.home.-$$Lambda$HomeActivity$TeDC12P23kOgjd5MyWVKYwB0hO4
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    HomeActivity.this.lambda$onCreate$4$HomeActivity(view);
                }
            });
        }
    }

    public /* synthetic */ boolean lambda$onCreate$0$HomeActivity(View view, boolean z) {
        boolean z2;
        LogUtils.i("HomeActivity", "mSwitchHotspot onInterceptCheck b=" + z);
        this.mSwitchHotspot.setClickable(false);
        if (this.mSwitchHotspot.isFromUser()) {
            LogUtils.i("HomeActivity", "mSwitchHotspot from user");
            if (z) {
                z2 = HotspotUtils.openHotspot(false);
            } else {
                z2 = HotspotUtils.closeHotspot();
            }
        } else {
            z2 = true;
        }
        this.mSwitchHotspot.setClickable(true);
        return !z2;
    }

    public /* synthetic */ void lambda$onCreate$1$HomeActivity(View view) {
        showRefreshPassDialog();
    }

    public /* synthetic */ void lambda$onCreate$2$HomeActivity(View view) {
        sendBuyFlow();
    }

    public /* synthetic */ void lambda$onCreate$3$HomeActivity(View view) {
        openWifiDetail();
    }

    public /* synthetic */ void lambda$onCreate$4$HomeActivity(View view) {
        RendererActivity.show();
        setRestoreButtonShowing(false, "OnClickListener");
        SharedPreferencesUtils.setRestoreButtonShowing(false);
    }

    @Override // com.xiaopeng.wirelessprojection.core.BaseActivity
    public int getRootLayoutId() {
        return R.id.xfl_home_main;
    }

    @Override // com.xiaopeng.wirelessprojection.core.BaseActivity
    public void makeActivityVisible() {
        getWindow().setStatusBarColor(getColor(R.color.home_main_bg));
        super.makeActivityVisible();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.wirelessprojection.core.BaseActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStart() {
        super.onStart();
        LogUtils.i("HomeActivity", "onStart");
        if (sInstance != this) {
            sInstance = this;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.wirelessprojection.core.BaseActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        LogUtils.i("HomeActivity", "onResume");
        refreshConnectedClients(true);
        if (HotspotUtils.needOpenHotspot()) {
            HotspotUtils.openHotspot(false);
        }
        updateFlowInfo();
        createFlowRefresh();
        ProtocolManager.instance().postChildThread(new Runnable() { // from class: com.xiaopeng.wirelessprojection.home.-$$Lambda$HomeActivity$KTmi3cBYf89C7rTskZLoUP_Kaqg
            @Override // java.lang.Runnable
            public final void run() {
                HomeActivity.this.updateRestoreButton();
            }
        }, 200L);
        ProtocolManager.instance().postChildThread(new Runnable() { // from class: com.xiaopeng.wirelessprojection.home.-$$Lambda$HomeActivity$yp0Bj7LBESmmVKo_mqWDKwJLnfM
            @Override // java.lang.Runnable
            public final void run() {
                HomeActivity.this.updateDeviceName();
            }
        }, 1000L);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onPause() {
        super.onPause();
        Disposable disposable = this.mDisposable;
        if (disposable != null) {
            disposable.dispose();
            this.mDisposable = null;
        }
        LogUtils.d("HomeActivity", "onPause");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStop() {
        super.onStop();
        LogUtils.d("HomeActivity", "onStop");
        Disposable disposable = this.mDisposable;
        if (disposable != null) {
            disposable.dispose();
            this.mDisposable = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.wirelessprojection.core.BaseActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        LogUtils.i("HomeActivity", "onDestroy");
        EventBusUtils.unregisterSafely(this);
        if (sInstance == this) {
            sInstance = null;
        }
    }

    private void requestPermissions() {
        PermissionX.init(this).permissions("android.permission.READ_EXTERNAL_STORAGE", "android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION").request(new RequestCallback() { // from class: com.xiaopeng.wirelessprojection.home.-$$Lambda$HomeActivity$H67FPew4W4iZR1EKunY-MP2XKpE
            @Override // com.permissionx.guolindev.callback.RequestCallback
            public final void onResult(boolean z, List list, List list2) {
                HomeActivity.this.lambda$requestPermissions$5$HomeActivity(z, list, list2);
            }
        });
    }

    public /* synthetic */ void lambda$requestPermissions$5$HomeActivity(boolean z, List list, List list2) {
        if (z) {
            LogUtils.i("HomeActivity", "All permissions are granted");
        } else {
            LogUtils.i("HomeActivity", "These permissions are denied: " + list2.toString());
        }
    }

    private void resetWifiInfo() {
        LogUtils.i("HomeActivity", "resetWifiInfo");
    }

    private void showRefreshPassDialog() {
        new XDialog(this).setTitle(BaseApp.getContext().getString(R.string.hotspot_notify_title)).setMessage(BaseApp.getContext().getString(R.string.hotspot_notify_content)).setPositiveButton(BaseApp.getContext().getString(R.string.hotspot_notify_confirm), new XDialogInterface.OnClickListener() { // from class: com.xiaopeng.wirelessprojection.home.-$$Lambda$HomeActivity$Q-vbRNpS2piiX_uNNJhj2HIDGWo
            @Override // com.xiaopeng.xui.app.XDialogInterface.OnClickListener
            public final void onClick(XDialog xDialog, int i) {
                HomeActivity.this.lambda$showRefreshPassDialog$6$HomeActivity(xDialog, i);
            }
        }).setNegativeButton(BaseApp.getContext().getString(R.string.hotspot_notify_cancel), new XDialogInterface.OnClickListener() { // from class: com.xiaopeng.wirelessprojection.home.-$$Lambda$HomeActivity$OTRoqsHsLs6dGsFHFJP8ubJCxpo
            @Override // com.xiaopeng.xui.app.XDialogInterface.OnClickListener
            public final void onClick(XDialog xDialog, int i) {
                HomeActivity.this.lambda$showRefreshPassDialog$7$HomeActivity(xDialog, i);
            }
        }).setCloseVisibility(true).show();
    }

    public /* synthetic */ void lambda$showRefreshPassDialog$6$HomeActivity(XDialog xDialog, int i) {
        LogUtils.d("HomeActivity", "confirm refresh");
        xDialog.setNegativeButtonEnable(false);
        refreshHotspotPassword();
    }

    public /* synthetic */ void lambda$showRefreshPassDialog$7$HomeActivity(XDialog xDialog, int i) {
        LogUtils.d("HomeActivity", "cancel refresh");
        xDialog.setPositiveButtonEnable(false);
    }

    private void refreshHotspotPassword() {
        boolean openHotspot = HotspotUtils.openHotspot(true);
        if (openHotspot) {
            runOnUiThread(new Runnable() { // from class: com.xiaopeng.wirelessprojection.home.-$$Lambda$HomeActivity$GKmCdcXMDP0hhede-XjN_v2O5y4
                @Override // java.lang.Runnable
                public final void run() {
                    HomeActivity.this.lambda$refreshHotspotPassword$8$HomeActivity();
                }
            });
        }
        LogUtils.i("HomeActivity", "refreshHotspotPassword success=" + openHotspot);
    }

    public /* synthetic */ void lambda$refreshHotspotPassword$8$HomeActivity() {
        updatePasswordArea(HotspotUtils.getHotspotPassword());
        ToastUtils.showToast("热点密码已刷新，请重新连接");
    }

    private void updatePasswordArea(String str) {
        try {
            if (!TextUtils.isEmpty(str) && str.length() >= 8) {
                ((TextView) findViewById(R.id.tv_hotspot_pass_0)).setText(String.valueOf(str.charAt(0)));
                ((TextView) findViewById(R.id.tv_hotspot_pass_1)).setText(String.valueOf(str.charAt(1)));
                ((TextView) findViewById(R.id.tv_hotspot_pass_2)).setText(String.valueOf(str.charAt(2)));
                ((TextView) findViewById(R.id.tv_hotspot_pass_3)).setText(String.valueOf(str.charAt(3)));
                ((TextView) findViewById(R.id.tv_hotspot_pass_4)).setText(String.valueOf(str.charAt(4)));
                ((TextView) findViewById(R.id.tv_hotspot_pass_5)).setText(String.valueOf(str.charAt(5)));
                ((TextView) findViewById(R.id.tv_hotspot_pass_6)).setText(String.valueOf(str.charAt(6)));
                ((TextView) findViewById(R.id.tv_hotspot_pass_7)).setText(String.valueOf(str.charAt(7)));
                return;
            }
            LogUtils.e("HomeActivity", "updatePasswordArea pass illegal");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshConnectedClients(boolean z) {
        LogUtils.i("HomeActivity", "refreshConnectedClients forceClose=" + z);
        if (HotspotUtils.refreshConnectedClients() || z) {
            String[] connectedClients = HotspotUtils.getConnectedClients();
            LogUtils.i("HomeActivity", "refreshConnectedClients clients=" + Arrays.toString(connectedClients));
            if (this.mTvHotspotClients != null) {
                Resources resources = BaseApp.getContext().getResources();
                if (connectedClients.length == 0) {
                    this.mTvHotspotClients.setText(resources.getString(R.string.hotspot_no_device));
                } else {
                    this.mTvHotspotClients.setText(resources.getString(R.string.hotspot_device_connected, Integer.valueOf(connectedClients.length)));
                }
            }
        }
    }

    private void updateHotspotArea(boolean z) {
        LogUtils.i("HomeActivity", "updateHotspotArea enable=" + z);
        XTextView xTextView = this.mTvHotspotName;
        if (xTextView != null && this.mTvHotspotPwRefresh != null && this.mClHotspotPwDetail != null) {
            xTextView.setEnabled(z);
            this.mClHotspotPwDetail.setEnabled(z);
            this.mTvHotspotPwRefresh.setEnabled(z);
            this.mTvHotspotPwRefresh.setAlpha(0.4f);
            if (z) {
                this.mTvHotspotName.setAlpha(1.0f);
                this.mClHotspotPwDetail.setAlpha(1.0f);
                this.mTvHotspotPwRefresh.setAlpha(1.0f);
            } else {
                this.mTvHotspotName.setAlpha(0.4f);
                this.mClHotspotPwDetail.setAlpha(0.4f);
                this.mTvHotspotPwRefresh.setAlpha(0.4f);
            }
        }
        if (z) {
            return;
        }
        setRestoreButtonShowing(false, "updateHotspotArea");
        SharedPreferencesUtils.setRestoreButtonShowing(false);
    }

    public void dismissWindow() {
        LogUtils.e("HomeActivity", "dismiss home window mIsShowing");
        if (isFinishing()) {
            return;
        }
        finish();
    }

    private void setRestoreButtonShowing(final boolean z, String str) {
        LogUtils.i("HomeActivity", "setRestoreButtonShowing isShowing=" + z + ", from=" + str);
        ThreadUtils.postMainThread(new Runnable() { // from class: com.xiaopeng.wirelessprojection.home.-$$Lambda$HomeActivity$RVzmYOlSRoiLii4LLnTwKvHEll8
            @Override // java.lang.Runnable
            public final void run() {
                HomeActivity.this.lambda$setRestoreButtonShowing$9$HomeActivity(z);
            }
        });
    }

    public /* synthetic */ void lambda$setRestoreButtonShowing$9$HomeActivity(boolean z) {
        XButton xButton = this.mBtnVideoRestore;
        if (xButton != null) {
            xButton.setVisibility(z ? 0 : 8);
        }
    }

    private void setHotspotInfo(String str, String str2) {
        LogUtils.i("HomeActivity", "setHotspotInfo name=" + str + ", pass=" + str2);
        if (this.mTvHotspotName != null) {
            this.mTvHotspotName.setText(BaseApp.getContext().getResources().getString(R.string.wifi_hotspot_switch_with_name, str));
            updatePasswordArea(str2);
        }
    }

    private void sendBuyFlow() {
        LogUtils.i("HomeActivity", "sendBuyFlow");
        IpcRouterService.sendOpenBuyFlow("HomeActivity");
    }

    private void openWifiDetail() {
        LogUtils.i("HomeActivity", "openWifiDetail");
        if (this.mCustomDialogContainer == null) {
            this.mCustomDialogContainer = new CustomDialogContainer(this);
        }
        if (this.mCustomDialogContainer.isShowing()) {
            return;
        }
        this.mCustomDialogContainer.showWifiDetailDialog();
    }

    private void handleTrafficStatusChange(int i) {
        LogUtils.i("HomeActivity", "handleTrafficStatusChange status=" + i);
        if (i == 0) {
            onFlowRunOut();
        } else if (i != 1) {
        } else {
            queryFlowRemain();
        }
    }

    private void queryFlowRemain() {
        LogUtils.i("HomeActivity", "queryFlowRemain");
        IpcRouterService.sendQuery(IpcRouterService.EVENT_FLOW_REMAIN, "com.xiaopeng.wirelessprojection", "com.xiaopeng.caraccount", IpcRouterService.CALLBACK_FLOW_REMAIN);
    }

    private void onFlowRunOut() {
        LogUtils.i("HomeActivity", "onFlowRunOut");
        try {
            this.mTvTrafficRemain.setText(BaseApp.getContext().getResources().getString(R.string.traffic_info_remain_with_unit, TrafficInfoUtils.TRAFFIC_INFO_DEFAULT));
            this.mTvTrafficRemain.setTextColorRef(R.color.x_color_red);
            this.mTvTrafficTips.setTextColorRef(R.color.x_color_red);
            this.mTvTrafficTips.setText(BaseApp.getContext().getString(R.string.traffic_info_tip_zero));
            this.mTipState = 2;
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void onFlowStatusLow(String str) {
        LogUtils.i("HomeActivity", "onFlowStatusLow restFlow=" + str);
        try {
            this.mTvTrafficRemain.setText(BaseApp.getContext().getResources().getString(R.string.traffic_info_remain_with_unit, str));
            this.mTvTrafficRemain.setTextColorRef(R.color.x_color_red);
            this.mTvTrafficTips.setTextColorRef(R.color.x_color_red);
            this.mTvTrafficTips.setText(BaseApp.getContext().getString(R.string.traffic_info_tip_low));
            this.mTipState = 1;
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void onFlowStatusNormal(String str) {
        LogUtils.i("HomeActivity", "onFlowStatusNormal");
        try {
            this.mTvTrafficRemain.setText(BaseApp.getContext().getResources().getString(R.string.traffic_info_remain_with_unit, str));
            this.mTvTrafficRemain.setTextColorRef(R.color.x_color_text_1);
            this.mTvTrafficTips.setTextColorRef(R.color.x_color_text_3);
            this.mTvTrafficTips.setText(BaseApp.getContext().getString(R.string.traffic_info_tip_normal));
            this.mTipState = 0;
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateFlowInfo() {
        queryFlowRemain();
        updateFlowCost(false);
    }

    private void updateFlowCost(final boolean z) {
        ThreadUtils.postMainThread(new Runnable() { // from class: com.xiaopeng.wirelessprojection.home.-$$Lambda$HomeActivity$SrhkofgVHgwwV_1VMe0FSiDXXZs
            @Override // java.lang.Runnable
            public final void run() {
                HomeActivity.this.lambda$updateFlowCost$11$HomeActivity(z);
            }
        }, z ? 0 : IInputController.KEYCODE_KNOB_WIND_SPD_UP);
    }

    public /* synthetic */ void lambda$updateFlowCost$11$HomeActivity(boolean z) {
        final String formattedFromByteLong = TrafficInfoUtils.getFormattedFromByteLong(RendererControlManager.instance().getTrafficCostByte());
        LogUtils.i("HomeActivity", "updateFlowCost cost=" + formattedFromByteLong + " GB, withToast=" + z);
        if (this.mTvTrafficCost != null) {
            this.mTvTrafficCost.setText(BaseApp.getContext().getResources().getString(R.string.traffic_info_remain_with_unit, formattedFromByteLong));
        }
        if (z) {
            runOnUiThread(new Runnable() { // from class: com.xiaopeng.wirelessprojection.home.-$$Lambda$HomeActivity$HHaUofjZKYnkTBMQCFt1JXwnV8A
                @Override // java.lang.Runnable
                public final void run() {
                    ToastUtils.showToast(BaseApp.getContext().getString(R.string.traffic_toast_cost, formattedFromByteLong));
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDeviceName() {
        final String serverName = RendererControlManager.instance().getServerName();
        Log.i("HomeActivity", "updateDeviceName: serverName:" + serverName);
        if (this.mTvEduContent != null) {
            ThreadUtils.postMainThread(new Runnable() { // from class: com.xiaopeng.wirelessprojection.home.-$$Lambda$HomeActivity$1L4Ll73XoWipY6iKzHJRL2DJOW8
                @Override // java.lang.Runnable
                public final void run() {
                    HomeActivity.this.lambda$updateDeviceName$12$HomeActivity(serverName);
                }
            });
        }
    }

    public /* synthetic */ void lambda$updateDeviceName$12$HomeActivity(String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        this.mTvEduContent.setText(BaseApp.getContext().getString(R.string.hotspot_projection_step_content, str));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateRestoreButton() {
        setRestoreButtonShowing(RendererControlManager.instance().hasActiveConnection(), "updateRestoreButton");
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (ThemeManager.isThemeChanged(configuration)) {
            int i = this.mTipState;
            if (i == 2 || i == 1) {
                this.mTvTrafficRemain.setTextColorRef(R.color.x_color_red);
                this.mTvTrafficTips.setTextColorRef(R.color.x_color_red);
                return;
            }
            this.mTvTrafficRemain.setTextColorRef(R.color.x_color_text_1);
            this.mTvTrafficTips.setTextColorRef(R.color.x_color_text_3);
        }
    }

    private void releaseDisposable() {
        if (mCloseHotspotDisposable != null) {
            LogUtils.i("HomeActivity", "releaseDisposable mCloseHotspotDisposable not null");
            mCloseHotspotDisposable.dispose();
            mCloseHotspotDisposable = null;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnectionChangeEvent(ConnectionChangeEvent connectionChangeEvent) {
        LogUtils.i("HomeActivity", "onConnectionChangeEvent");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVideoRestoreEnableEvent(VideoRestoreEnableEvent videoRestoreEnableEvent) {
        LogUtils.i("HomeActivity", "onVideoRestoreEnableEvent");
        if (videoRestoreEnableEvent != null) {
            setRestoreButtonShowing(videoRestoreEnableEvent.isEnabled(), "VideoRestoreEnableEvent");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHotspotInfoChangeEvent(HotspotInfoChangeEvent hotspotInfoChangeEvent) {
        LogUtils.i("HomeActivity", "onHotspotInfoChangeEvent event=" + hotspotInfoChangeEvent.toString());
        setHotspotInfo(hotspotInfoChangeEvent.getName(), hotspotInfoChangeEvent.getPassword());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnectedClientChangeEvent(ConnectedClientChangeEvent connectedClientChangeEvent) {
        LogUtils.i("HomeActivity", "onConnectedClientChangeEvent");
        refreshConnectedClients(false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTrafficStatusChangeEvent(TrafficStatusChangeEvent trafficStatusChangeEvent) {
        LogUtils.i("HomeActivity", "onTrafficStatusChangeEvent");
        if (trafficStatusChangeEvent != null) {
            handleTrafficStatusChange(trafficStatusChangeEvent.getStatus());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDayNightChangeEvent(DayNightChangeEvent dayNightChangeEvent) {
        LogUtils.i("HomeActivity", "onDayNightChangeEvent");
        updateFlowInfo();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTrafficInfoResultEvent(TrafficInfoResultEvent trafficInfoResultEvent) {
        if (trafficInfoResultEvent != null) {
            String data = trafficInfoResultEvent.getData();
            LogUtils.i("HomeActivity", "onTrafficInfoResultEvent data=" + data);
            int remainFlowStatus = TrafficInfoUtils.remainFlowStatus(data);
            if (remainFlowStatus == -1) {
                onFlowStatusNormal(TrafficInfoUtils.TRAFFIC_INFO_NO_DATA);
            } else if (remainFlowStatus == 0) {
                onFlowStatusNormal(TrafficInfoUtils.getFormattedFromKB(data));
            } else if (remainFlowStatus == 1) {
                onFlowStatusLow(TrafficInfoUtils.getFormattedFromKB(data));
            } else if (remainFlowStatus != 2) {
            } else {
                onFlowRunOut();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAppGoBackgroundEvent(AppGoBackgroundEvent appGoBackgroundEvent) {
        LogUtils.i("HomeActivity", "onAppGoBackgroundEvent");
        if (HotspotUtils.isHotspotOpen()) {
            if (RendererControlManager.instance().isClientConnected()) {
                return;
            }
            int i = HotspotUtils.hasClientConnecting() ? 30000 : 0;
            releaseDisposable();
            mCloseHotspotDisposable = ThreadUtils.postBackground(new Runnable() { // from class: com.xiaopeng.wirelessprojection.home.-$$Lambda$HomeActivity$Y0n70xMD6OhXmJPC8Jy4YM7jHRg
                @Override // java.lang.Runnable
                public final void run() {
                    HomeActivity.this.lambda$onAppGoBackgroundEvent$13$HomeActivity();
                }
            }, i);
            return;
        }
        runOnUiThread(new Runnable() { // from class: com.xiaopeng.wirelessprojection.home.-$$Lambda$HomeActivity$tUQhg5xrFal1oDKw4CljDWK5BII
            @Override // java.lang.Runnable
            public final void run() {
                ToastUtils.showToast(BaseApp.getContext().getString(R.string.traffic_toast_quit));
            }
        });
        hide();
    }

    public /* synthetic */ void lambda$onAppGoBackgroundEvent$13$HomeActivity() {
        updateFlowCost(true);
        HotspotUtils.closeHotspot();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHotspotChangeEvent(HotspotChangeEvent hotspotChangeEvent) {
        final boolean isHotspotOpen = HotspotUtils.isHotspotOpen();
        LogUtils.i("HomeActivity", "onHotspotChangeEvent isOpen=" + isHotspotOpen);
        if (this.mSwitchHotspot != null) {
            ThreadUtils.postMainThread(new Runnable() { // from class: com.xiaopeng.wirelessprojection.home.-$$Lambda$HomeActivity$dKQ68aBGK43Ru5dA4uR7uzwRRJ4
                @Override // java.lang.Runnable
                public final void run() {
                    HomeActivity.this.lambda$onHotspotChangeEvent$15$HomeActivity(isHotspotOpen);
                }
            });
        }
        refreshConnectedClients(false);
    }

    public /* synthetic */ void lambda$onHotspotChangeEvent$15$HomeActivity(boolean z) {
        if (z != this.mSwitchHotspot.isChecked()) {
            this.mSwitchHotspot.setChecked(z);
        }
        if (!z) {
            refreshConnectedClientsByNum(0);
        }
        updateHotspotArea(z);
        updateFlowInfo();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateServerName(ServerNameUpdateEvent serverNameUpdateEvent) {
        LogUtils.i("HomeActivity", "onUpdateServerName-------");
        updateDeviceName();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshConnectedClientsByNum(int i) {
        LogUtils.i("HomeActivity", "refreshConnectedClients by num clients=" + i);
        if (this.mTvHotspotClients != null) {
            Resources resources = BaseApp.getContext().getResources();
            if (i == 0) {
                this.mTvHotspotClients.setText(resources.getString(R.string.hotspot_no_device));
            } else {
                this.mTvHotspotClients.setText(resources.getString(R.string.hotspot_device_connected, Integer.valueOf(i)));
            }
        }
    }
}

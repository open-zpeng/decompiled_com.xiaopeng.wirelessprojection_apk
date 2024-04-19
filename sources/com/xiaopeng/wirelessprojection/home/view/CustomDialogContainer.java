package com.xiaopeng.wirelessprojection.home.view;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import com.xiaopeng.lib.utils.LogUtils;
import com.xiaopeng.wirelessprojection.home.R;
import com.xiaopeng.xui.app.XDialog;
import com.xiaopeng.xui.app.XDialogInterface;
/* loaded from: classes2.dex */
public class CustomDialogContainer {
    private static final String ACTION_CONNECT_WIFI = "com.xiaopeng.intent.action.POPUP_WLAN";
    private static final String TAG = "CustomDialogContainer";
    private Context mContext;
    private CustomWifiDetailLayout mCustomWifiDetailLayout;
    private XDialog mDialog;

    public CustomDialogContainer(Context context) {
        LogUtils.i(TAG, "CustomDialogContainer build");
        this.mContext = context;
        this.mDialog = new XDialog(this.mContext, R.style.WifiDialogView).setTitle(R.string.dialog_wifi_detail_title).setMessage((CharSequence) null).setPositiveButton(R.string.dialog_wifi_detail_button, new XDialogInterface.OnClickListener() { // from class: com.xiaopeng.wirelessprojection.home.view.-$$Lambda$CustomDialogContainer$-dsLtV4vaB3tw4IZ2CZ8Pev7vCc
            @Override // com.xiaopeng.xui.app.XDialogInterface.OnClickListener
            public final void onClick(XDialog xDialog, int i) {
                CustomDialogContainer.this.lambda$new$0$CustomDialogContainer(xDialog, i);
            }
        }).setCloseVisibility(true);
        CustomWifiDetailLayout customWifiDetailLayout = new CustomWifiDetailLayout(this.mContext, this.mDialog.getContentView());
        this.mCustomWifiDetailLayout = customWifiDetailLayout;
        this.mDialog.setCustomView((View) customWifiDetailLayout, false);
    }

    public /* synthetic */ void lambda$new$0$CustomDialogContainer(XDialog xDialog, int i) {
        LogUtils.d(TAG, "confirm");
        xDialog.setPositiveButtonEnable(false);
        sendConnectWifi();
    }

    public boolean isShowing() {
        XDialog xDialog = this.mDialog;
        if (xDialog != null) {
            return xDialog.getDialog().isShowing();
        }
        LogUtils.e(TAG, "isShowing mDialog == null");
        return false;
    }

    public void showWifiDetailDialog() {
        LogUtils.i(TAG, "showWifiDetailDialog");
        XDialog xDialog = this.mDialog;
        if (xDialog != null) {
            xDialog.setPositiveButtonEnable(true);
            this.mDialog.show();
        }
    }

    private void sendConnectWifi() {
        LogUtils.i(TAG, "sendConnectWifi");
        Intent intent = new Intent(ACTION_CONNECT_WIFI);
        intent.addFlags(268435456);
        this.mContext.startActivity(intent);
    }
}

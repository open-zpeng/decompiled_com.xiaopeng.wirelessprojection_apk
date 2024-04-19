package com.xiaopeng.wirelessprojection.core.apirouter;

import android.net.Uri;
import android.os.RemoteException;
import android.text.TextUtils;
import androidx.core.app.NotificationCompat;
import com.xiaopeng.lib.apirouter.ApiRouter;
import com.xiaopeng.lib.apirouter.server.IServicePublisher;
import com.xiaopeng.lib.utils.LogUtils;
import com.xiaopeng.speech.overall.SpeechResult;
import com.xiaopeng.wirelessprojection.core.BaseApp;
import com.xiaopeng.wirelessprojection.core.event.TrafficInfoResultEvent;
import com.xiaopeng.wirelessprojection.core.utils.AppUtils;
import com.xiaopeng.wirelessprojection.core.utils.EventBusUtils;
import com.xiaopeng.wirelessprojection.core.utils.ScreenUtils;
import com.xiaopeng.wirelessprojection.core.utils.ThreadUtils;
import org.json.JSONObject;
/* loaded from: classes2.dex */
public class IpcRouterService implements IServicePublisher {
    public static final String CALLBACK_FLOW_REMAIN = "onFlowRemainQueryResult";
    public static final String CAR_ACCOUNT_PACKAGE = "com.xiaopeng.caraccount";
    public static final String EVENT_FLOW_BUY = "projection.event.flow.buy";
    public static final String EVENT_FLOW_REMAIN = "projection.query.flow.remain";
    public static final String EVENT_SPEECH_OPEN_APP = "wireless.app.driver.support.open";
    public static final String RECEIVER_PACKAGE_NAME = "receiverPackageName";
    public static final String SENDER_PACKAGE_NAME = "senderPackageName";
    public static final String TAG = "IpcRouterService";
    public static final String WIRELESS_PROJECTION_PACKAGE = "com.xiaopeng.wirelessprojection";

    public static void sendData(int i, JSONObject jSONObject, String str) {
        if (jSONObject == null) {
            try {
                jSONObject = new JSONObject();
            } catch (Exception e) {
                LogUtils.e(TAG, e);
                return;
            }
        }
        jSONObject.put(SENDER_PACKAGE_NAME, BaseApp.getContext().getPackageName());
        LogUtils.i(TAG, "id = " + i + " bunder = " + jSONObject);
        ApiRouter.route(new Uri.Builder().authority(str + ".IpcRouterService").path("onReceiverData").appendQueryParameter("id", String.valueOf(i)).appendQueryParameter("bundle", jSONObject.toString()).build());
    }

    public static void sendQuery(String str, String str2, String str3, String str4) {
        try {
            LogUtils.i(TAG, "sendQuery event = " + str + ", callback = " + str4 + ", target = " + str3);
            String str5 = getAuthorityPrefix() + "com.xiaopeng.wirelessprojection.IpcRouterService";
            LogUtils.i(TAG, "sendQuery auth = " + str5);
            ApiRouter.route(new Uri.Builder().authority(str3 + ".IpcRouterService").path("onQuery").appendQueryParameter(NotificationCompat.CATEGORY_EVENT, str).appendQueryParameter("data", str2).appendQueryParameter("callback", new Uri.Builder().authority(str5).path(str4).build().toString()).build());
        } catch (Exception e) {
            LogUtils.e(TAG, e);
        }
    }

    public static void sendEvent(String str, String str2, String str3) {
        try {
            LogUtils.i(TAG, "sendEvent event = " + str + ", target = " + str3 + ", data=" + str2);
            ApiRouter.route(new Uri.Builder().authority(str3 + ".IpcRouterService").path("onEvent").appendQueryParameter(NotificationCompat.CATEGORY_EVENT, str).appendQueryParameter("data", str2).build());
        } catch (Exception e) {
            LogUtils.e(TAG, e);
        }
    }

    public static void sendOpenBuyFlow(String str) {
        LogUtils.i(TAG, "sendOpenBuyFlow from = " + str);
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("pkgName", "com.xiaopeng.wirelessprojection");
            jSONObject.put("screenId", ScreenUtils.getScreenId());
            sendEvent(EVENT_FLOW_BUY, jSONObject.toString(), "com.xiaopeng.caraccount");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onFlowRemainQueryResult(String str) {
        LogUtils.i(TAG, "onFlowRemainQueryResult data = " + str);
        EventBusUtils.post(new TrafficInfoResultEvent(str));
    }

    public void onQuery(final String str, String str2, final String str3) {
        LogUtils.i(TAG, "onQuery : event = " + str + " data = " + str2 + " callback = " + str3);
        ThreadUtils.postWorker(new Runnable() { // from class: com.xiaopeng.wirelessprojection.core.apirouter.-$$Lambda$IpcRouterService$GdFdoQvKB2taacsoE3J433FPojs
            @Override // java.lang.Runnable
            public final void run() {
                IpcRouterService.lambda$onQuery$0(str, str3);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$onQuery$0(String str, String str2) {
        str.hashCode();
        if (!str.equals(EVENT_SPEECH_OPEN_APP)) {
            if (str.equals(EVENT_FLOW_REMAIN)) {
                try {
                    ApiRouter.route(Uri.parse(str2).buildUpon().appendQueryParameter("data", "17598807").build());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        } else if (TextUtils.isEmpty(str2)) {
        } else {
            boolean isAllowUseApp = AppUtils.isAllowUseApp();
            LogUtils.i(TAG, "callback : " + str2);
            try {
                ApiRouter.route(Uri.parse(str2).buildUpon().appendQueryParameter("result", new SpeechResult(str, Boolean.valueOf(isAllowUseApp)).toString()).build());
            } catch (Exception e2) {
                LogUtils.i(TAG, "remote exception : " + e2.getMessage());
                e2.printStackTrace();
            }
        }
    }

    private static String getAuthorityPrefix() {
        return AppUtils.getCurrentUid() + "@";
    }
}

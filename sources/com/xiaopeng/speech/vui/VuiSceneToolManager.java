package com.xiaopeng.speech.vui;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewRootImpl;
import android.widget.ListView;
import android.widget.ScrollView;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import com.google.gson.Gson;
import com.xiaopeng.lib.apirouter.ApiRouter;
import com.xiaopeng.lib.apirouter.ClientConstants;
import com.xiaopeng.speech.apirouter.Utils;
import com.xiaopeng.speech.vui.cache.VuiDisplayLocationInfoTestCache;
import com.xiaopeng.speech.vui.cache.VuiSceneBuildTestCache;
import com.xiaopeng.speech.vui.cache.VuiSceneCache;
import com.xiaopeng.speech.vui.cache.VuiSceneRemoveTestCache;
import com.xiaopeng.speech.vui.cache.VuiSceneTestCache;
import com.xiaopeng.speech.vui.cache.VuiSceneTestCacheFactory;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import com.xiaopeng.speech.vui.listener.IXpVuiSceneListener;
import com.xiaopeng.speech.vui.model.VuiEventInfo;
import com.xiaopeng.speech.vui.model.VuiFeedback;
import com.xiaopeng.speech.vui.model.VuiScene;
import com.xiaopeng.speech.vui.model.VuiSceneInfo;
import com.xiaopeng.speech.vui.utils.LogUtils;
import com.xiaopeng.speech.vui.utils.ResourceUtil;
import com.xiaopeng.speech.vui.utils.VuiUtils;
import com.xiaopeng.vui.commons.IVuiElement;
import com.xiaopeng.vui.commons.IVuiElementChangedListener;
import com.xiaopeng.vui.commons.IVuiSceneListener;
import com.xiaopeng.vui.commons.VuiAction;
import com.xiaopeng.vui.commons.VuiElementType;
import com.xiaopeng.vui.commons.VuiFeedbackType;
import com.xiaopeng.vui.commons.model.VuiElement;
import com.xiaopeng.wirelessprojection.core.receiver.VideoRestoreStatusReceiver;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes2.dex */
public class VuiSceneToolManager {
    private static int SEND_UPLOAD_MESSAGE = 1;
    public static final int TYPE_ADD = 2;
    public static final int TYPE_BUILD = 0;
    public static final int TYPE_DISPLAY_LOCATION = 5;
    public static final int TYPE_REMOVE = 3;
    public static final int TYPE_UPDATE = 1;
    public static final int TYPE_UPDATEATTR = 4;
    private static final int VUI_RETRY_MAX_COUNT = 3;
    private static final String VUI_SCENE_AUTHORITY = "com.xiaopeng.speech.vuiscene";
    private static final int VUI_UPDATE_FAILED_ERRO_CODE = -500;
    private final String TAG;
    Map<String, Integer> feedbackInfo;
    private String mActiveSceneId;
    private Handler mApiRouterHandler;
    private HandlerThread mApiRouterThread;
    private Binder mBinder;
    private Context mContext;
    private Handler mHandler;
    private boolean mIsInSpeech;
    private String mObserver;
    private String mPackageName;
    private String mPackageVersion;
    private String mProcessName;
    private VuiBroadCastReceiver mReceiver;
    private HandlerThread mThread;
    private ConcurrentHashMap<String, VuiSceneInfo> mVuiSceneInfoMap;
    private ConcurrentHashMap<String, VuiSceneInfo> mVuiSubSceneInfoMap;
    private List<String> sceneIds;
    private VuiEngineImpl vuiEngine;
    private static final Uri VUI_SCENE_URI = Uri.parse("content://com.xiaopeng.speech.vuiscene/scene");
    private static final Uri VUI_SCENE_DELETE_URI = Uri.parse("content://com.xiaopeng.speech.vuiscene/scene/delete/");
    private static int REMOVE_FEED_BACK = 2;

    /* JADX INFO: Access modifiers changed from: private */
    public String getAuthority() {
        return VuiConstants.VUI_SCENE_THIRD_AUTHORITY;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean hasProcessFeature() {
        return true;
    }

    public void addVuiSceneListener(String str, View view, IVuiSceneListener iVuiSceneListener, IVuiElementChangedListener iVuiElementChangedListener, boolean z) {
    }

    public void removeVuiSceneListener(String str, boolean z, boolean z2, IVuiSceneListener iVuiSceneListener) {
    }

    public void setInSpeech(boolean z) {
        this.mIsInSpeech = z;
        if (z) {
            sendSceneData(null);
        }
    }

    public void setEngine(VuiEngineImpl vuiEngineImpl) {
        this.vuiEngine = vuiEngineImpl;
    }

    public boolean isInSpeech() {
        return this.mIsInSpeech;
    }

    private VuiSceneToolManager() {
        this.TAG = "VuiSceneToolManager";
        this.mIsInSpeech = true;
        this.mBinder = null;
        this.mReceiver = null;
        this.feedbackInfo = new HashMap();
        this.mProcessName = null;
        this.sceneIds = new ArrayList();
        this.mVuiSceneInfoMap = new ConcurrentHashMap<>();
        this.mVuiSubSceneInfoMap = new ConcurrentHashMap<>();
        if (Utils.checkApkExist(VuiConstants.VUI_SCENE_THIRD_APP)) {
            lazyInitThread();
        }
    }

    public static final VuiSceneToolManager instance() {
        return Holder.Instance;
    }

    public void subscribe(String str) {
        if (!Utils.isCorrectObserver(this.mPackageName, str)) {
            LogUtils.e("VuiSceneToolManager", "注册observer不合法,observer是app的包名加observer的类名组成");
            return;
        }
        this.mObserver = str;
        if (VuiUtils.canUseVuiFeature()) {
            subscribe(false);
            registerReceiver();
        }
    }

    private void lazyInitThread() {
        if (this.mThread == null) {
            HandlerThread handlerThread = new HandlerThread("VuiSceneToolManager-Thread");
            this.mThread = handlerThread;
            handlerThread.start();
            this.mHandler = new VuiSceneHandler(this.mThread.getLooper());
        }
        if (this.mApiRouterThread == null) {
            HandlerThread handlerThread2 = new HandlerThread("VuiSceneToolManager-Apirouter-Thread");
            this.mApiRouterThread = handlerThread2;
            handlerThread2.start();
            this.mApiRouterHandler = new Handler(this.mApiRouterThread.getLooper());
        }
    }

    public void setFeatureState(final boolean z) {
        String str;
        try {
            Handler handler = this.mApiRouterHandler;
            if (handler != null) {
                handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiSceneToolManager.1
                    @Override // java.lang.Runnable
                    public void run() {
                        if (VuiUtils.canUseVuiFeature()) {
                            try {
                                if (VuiSceneToolManager.this.hasProcessFeature()) {
                                    ApiRouter.route(new Uri.Builder().authority(VuiSceneToolManager.this.getAuthority()).path("setFeatureState").appendQueryParameter("isEnable", "" + z).build());
                                }
                            } catch (Exception e) {
                                LogUtils.e("VuiSceneToolManager", "subscribe e:" + e.fillInStackTrace());
                            }
                        }
                    }
                });
            }
            if (VuiUtils.isFeatureDisabled() != z) {
                if (z) {
                    VuiSceneBuildTestCache vuiSceneBuildTestCache = (VuiSceneBuildTestCache) VuiSceneTestCacheFactory.instance().getSceneCache(VuiSceneTestCacheFactory.CacheType.BUILD.getType());
                    if (VuiEngineImpl.mActiveSceneIds.size() > 0 && (str = this.mPackageName) != null && str.equals(getTopRunningPackageName())) {
                        handlerActiveScene(VuiEngineImpl.mActiveSceneIds.get(VuiConstants.SCREEN_DISPLAY_LF), VuiConstants.SCREEN_DISPLAY_LF);
                        handlerActiveScene(VuiEngineImpl.mActiveSceneIds.get(VuiConstants.SCREEN_DISPLAY_RF), VuiConstants.SCREEN_DISPLAY_RF);
                    }
                    if (VuiEngineImpl.mLeftPopPanelStack.size() > 0) {
                        Iterator<String> it = VuiEngineImpl.mLeftPopPanelStack.iterator();
                        while (it.hasNext()) {
                            handlerActiveScene(it.next(), VuiConstants.SCREEN_DISPLAY_LF);
                        }
                    }
                    if (VuiEngineImpl.mRightPopPanelStack.size() > 0) {
                        Iterator<String> it2 = VuiEngineImpl.mRightPopPanelStack.iterator();
                        while (it2.hasNext()) {
                            handlerActiveScene(it2.next(), VuiConstants.SCREEN_DISPLAY_RF);
                        }
                        return;
                    }
                    return;
                }
                handleAllSceneCache(true);
                handleSceneDataInfo();
            }
        } catch (Exception unused) {
        }
    }

    private void handlerActiveScene(String str, String str2) {
        IVuiSceneListener vuiSceneListener;
        VuiSceneBuildTestCache vuiSceneBuildTestCache = (VuiSceneBuildTestCache) VuiSceneTestCacheFactory.instance().getSceneCache(VuiSceneTestCacheFactory.CacheType.BUILD.getType());
        if (TextUtils.isEmpty(str)) {
            return;
        }
        List<VuiElement> cache = vuiSceneBuildTestCache.getCache(str);
        if ((cache == null || cache.isEmpty()) && (vuiSceneListener = VuiSceneManager.instance().getVuiSceneListener(str)) != null) {
            vuiSceneListener.onBuildScene();
        }
        enterScene(str, this.mPackageName, true, str2);
    }

    public void storeFeedbackInfo(int i, String str, String str2) {
        LogUtils.logInfo("VuiSceneToolManager", "storeFeedbackInfo:" + str2 + ",soundArea:" + i);
        this.feedbackInfo.put(str2, Integer.valueOf(i));
        Message obtainMessage = this.mHandler.obtainMessage();
        obtainMessage.what = REMOVE_FEED_BACK;
        obtainMessage.obj = str2;
        this.mHandler.sendMessageDelayed(obtainMessage, 1000L);
    }

    public void vuiFeedBack(final String str, final String str2) {
        if (this.mApiRouterHandler == null || !this.feedbackInfo.containsKey(str)) {
            return;
        }
        final int intValue = this.feedbackInfo.get(str).intValue();
        this.feedbackInfo.remove(str);
        LogUtils.logInfo("VuiSceneToolManager", "vuiFeedBack:" + str + ",soundArea:" + intValue + ",content:" + str2);
        this.mApiRouterHandler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiSceneToolManager.2
            @Override // java.lang.Runnable
            public void run() {
                Uri.Builder builder = new Uri.Builder();
                builder.authority(VuiSceneToolManager.this.getAuthority()).path("vuiSoundAreaFeedback").appendQueryParameter(VuiConstants.SCENE_PACKAGE_NAME, VuiSceneToolManager.this.mPackageName).appendQueryParameter("packageVersion", VuiSceneToolManager.this.mPackageVersion).appendQueryParameter("resourceName", str).appendQueryParameter(VideoRestoreStatusReceiver.KEY_STATUS, "-1").appendQueryParameter("type", VuiFeedbackType.TTS.getType()).appendQueryParameter(ClientConstants.BINDER.SCHEME, str2).appendQueryParameter("soundArea", "" + intValue);
                try {
                    LogUtils.logDebug("VuiSceneToolManager", "vuiSoundAreaFeedback ");
                    String str3 = (String) ApiRouter.route(builder.build());
                    LogUtils.logInfo("VuiSceneToolManager", "vuiSoundAreaFeedback success");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void updateListIndexState() {
        VuiEngineImpl vuiEngineImpl = this.vuiEngine;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.updateListIndexState();
        }
    }

    public void onVuiQueryCallBack(final String str, final String str2, final String str3) {
        Handler handler;
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(str2) || (handler = this.mApiRouterHandler) == null) {
            return;
        }
        handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiSceneToolManager.3
            @Override // java.lang.Runnable
            public void run() {
                LogUtils.logInfo("VuiSceneToolManager", "onVuiQueryCallBack:" + str + ",event:" + str2 + ",result:" + str3);
                Uri.Builder builder = new Uri.Builder();
                builder.authority(VuiSceneToolManager.this.getAuthority()).path("onVuiQueryCallBack").appendQueryParameter(VuiConstants.SCENE_ID, str).appendQueryParameter(NotificationCompat.CATEGORY_EVENT, str2).appendQueryParameter("result", str3);
                try {
                    LogUtils.logDebug("VuiSceneToolManager", "onVuiQueryCallBack ");
                    String str4 = (String) ApiRouter.route(builder.build());
                    LogUtils.logInfo("VuiSceneToolManager", "onVuiQueryCallBack success");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void exitScene(final String str, final String str2, final boolean z, final String str3, boolean z2) {
        if (str == null || str2 == null || !VuiUtils.canUseVuiFeature()) {
            return;
        }
        this.mApiRouterHandler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiSceneToolManager.4
            @Override // java.lang.Runnable
            public void run() {
                try {
                    if (z) {
                        Uri.Builder builder = new Uri.Builder();
                        if (!VuiUtils.isUseDisplayLocationPlatForm()) {
                            builder.authority(VuiSceneToolManager.this.getAuthority()).path("exitScene").appendQueryParameter(VuiConstants.SCENE_ID, str).appendQueryParameter(VuiConstants.SCENE_PACKAGE_NAME, str2).appendQueryParameter("packageVersion", VuiSceneToolManager.this.mPackageVersion);
                        } else {
                            builder.authority(VuiSceneToolManager.this.getAuthority()).path("exitDisplayLocationScene").appendQueryParameter(VuiConstants.SCENE_ID, str).appendQueryParameter(VuiConstants.SCENE_PACKAGE_NAME, str2).appendQueryParameter("packageVersion", VuiSceneToolManager.this.mPackageVersion).appendQueryParameter("displayLocation", str3);
                        }
                        LogUtils.logDebug("VuiSceneToolManager", "exitScene-------------- " + str + ",displayLocation:" + str3);
                        ApiRouter.route(builder.build());
                        LogUtils.logDebug("VuiSceneToolManager", "exitScene---success---------- " + str);
                    }
                } catch (Exception e) {
                    LogUtils.e("VuiSceneToolManager", "exitScene--e: " + e.fillInStackTrace());
                    Uri.Builder builder2 = new Uri.Builder();
                    if (VuiUtils.isUseDisplayLocationPlatForm()) {
                        builder2.authority(VuiSceneToolManager.this.getAuthority()).path("exitScene").appendQueryParameter(VuiConstants.SCENE_ID, str).appendQueryParameter(VuiConstants.SCENE_PACKAGE_NAME, str2).appendQueryParameter("packageVersion", VuiSceneToolManager.this.mPackageVersion);
                    }
                    try {
                        ApiRouter.route(builder2.build());
                    } catch (Exception unused) {
                    }
                }
            }
        });
    }

    public boolean isNaviTop() {
        try {
            Uri.Builder builder = new Uri.Builder();
            builder.authority(getAuthority()).path("isNaviTop");
            return ((Boolean) ApiRouter.route(builder.build())).booleanValue();
        } catch (RemoteException e) {
            e.printStackTrace();
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public class VuiSceneHandler extends Handler {
        public VuiSceneHandler() {
        }

        public VuiSceneHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what != VuiSceneToolManager.SEND_UPLOAD_MESSAGE) {
                if (message.what == VuiSceneToolManager.REMOVE_FEED_BACK) {
                    String str = (String) message.obj;
                    if (VuiSceneToolManager.this.feedbackInfo.containsKey(str)) {
                        VuiSceneToolManager.this.feedbackInfo.remove(str);
                        return;
                    }
                    return;
                }
                return;
            }
            int i = message.arg1;
            boolean z = message.arg2 == 1;
            if (i == 2) {
                VuiSceneToolManager.this.addSceneElementGroup((VuiScene) message.obj, z);
            } else if (i == 0) {
                VuiSceneToolManager.this.buildScene((VuiScene) message.obj, z, true);
            } else if (i == 1) {
                VuiSceneToolManager.this.updateDynamicScene((VuiScene) message.obj, z);
            } else if (i == 4) {
                VuiSceneToolManager.this.updateSceneElementAttr((VuiScene) message.obj, z);
            } else if (i == 5) {
                VuiSceneToolManager.this.updateDisplayLocation((VuiScene) message.obj, z);
            } else {
                String str2 = (String) message.obj;
                int indexOf = str2.indexOf(",");
                VuiSceneToolManager.this.removeSceneElementGroup(str2.substring(0, indexOf), str2.substring(indexOf + 1), z);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDisplayLocation(final VuiScene vuiScene, final boolean z) {
        if (VuiUtils.isMultiScreenPlatForm()) {
            LogUtils.i("VuiSceneToolManager", "updateDisplayLocation  =======   ");
            Handler handler = this.mApiRouterHandler;
            if (handler != null) {
                handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiSceneToolManager.5
                    @Override // java.lang.Runnable
                    public void run() {
                        VuiScene vuiScene2;
                        String sceneId;
                        if (VuiUtils.cannotUpload() || (vuiScene2 = vuiScene) == null || (sceneId = vuiScene2.getSceneId()) == null) {
                            return;
                        }
                        VuiDisplayLocationInfoTestCache vuiDisplayLocationInfoTestCache = (VuiDisplayLocationInfoTestCache) VuiSceneTestCacheFactory.instance().getSceneCache(VuiSceneTestCacheFactory.CacheType.DISPLAY_LOCATION.getType());
                        if (z) {
                            vuiDisplayLocationInfoTestCache.setCache(sceneId, vuiScene.getDisplayLocation());
                        }
                        String vuiSceneConvertToString = VuiUtils.vuiSceneConvertToString(vuiScene);
                        LogUtils.i("VuiSceneToolManager", "updateDisplayLocation  =======   " + vuiSceneConvertToString);
                        if (VuiSceneToolManager.this.isUploadScene(sceneId)) {
                            vuiDisplayLocationInfoTestCache.setSendState(true);
                            Uri.Builder builder = new Uri.Builder();
                            builder.authority(VuiSceneToolManager.this.getAuthority()).path("updateDisplayLocation").appendQueryParameter(VuiConstants.SCENE_ID, sceneId).appendQueryParameter(VuiConstants.SCENE_PACKAGE_NAME, vuiScene.getPackageName()).appendQueryParameter("packageVersion", vuiScene.getVersion()).appendQueryParameter("sceneData", vuiSceneConvertToString);
                            try {
                                LogUtils.logDebug("VuiSceneToolManager", " updateDisplayLocation to CarSpeech" + sceneId);
                                String str = (String) ApiRouter.route(builder.build());
                                vuiDisplayLocationInfoTestCache.setSendState(false);
                                LogUtils.logInfo("VuiSceneToolManager", "updateDisplayLocation to CarSpeech success" + sceneId + ",result:" + str);
                                if (!TextUtils.isEmpty(str) && vuiDisplayLocationInfoTestCache != null && vuiScene.getDisplayLocation().equals(vuiDisplayLocationInfoTestCache.getDisplayCache(sceneId))) {
                                    vuiDisplayLocationInfoTestCache.removeDisplayCache(sceneId);
                                }
                            } catch (RemoteException e) {
                                e.printStackTrace();
                                LogUtils.e("VuiSceneToolManager", "updateDisplayLocation " + e.fillInStackTrace());
                                vuiDisplayLocationInfoTestCache.setSendState(false);
                            }
                            if ("user".equals(Build.TYPE) || LogUtils.getLogLevel() > LogUtils.LOG_DEBUG_LEVEL) {
                                return;
                            }
                            LogUtils.logDebug("VuiSceneToolManager", "updateDisplayLocation " + VuiUtils.vuiSceneConvertToString(vuiScene));
                        }
                    }
                });
            }
        }
    }

    private void registerReceiver() {
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiSceneToolManager.6
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        if (VuiUtils.canUseVuiFeature()) {
                            IntentFilter intentFilter = new IntentFilter();
                            intentFilter.addAction(VuiConstants.INTENT_ACTION_TEST_TOOL_START);
                            VuiSceneToolManager.this.mReceiver = new VuiBroadCastReceiver();
                            ((Application) Class.forName("android.app.ActivityThread").getMethod("currentApplication", new Class[0]).invoke(null, null)).registerReceiver(VuiSceneToolManager.this.mReceiver, intentFilter);
                        }
                    } catch (Exception e) {
                        LogUtils.e("VuiSceneToolManager", "registerReceiver e:" + e.getMessage());
                    }
                }
            });
        }
    }

    public void handleSceneDataInfo() {
        if (VuiEngineImpl.mActiveSceneIds.size() > 0) {
            String str = VuiEngineImpl.mActiveSceneIds.get(VuiConstants.SCREEN_DISPLAY_RF);
            String str2 = VuiEngineImpl.mActiveSceneIds.get(VuiConstants.SCREEN_DISPLAY_LF);
            if (!TextUtils.isEmpty(str2)) {
                enterScene(str2, this.mPackageName, true, VuiConstants.SCREEN_DISPLAY_LF);
            }
            if (!TextUtils.isEmpty(str)) {
                enterScene(str, this.mPackageName, true, VuiConstants.SCREEN_DISPLAY_RF);
            }
        }
        if (VuiEngineImpl.mLeftPopPanelStack.size() > 0) {
            Iterator<String> it = VuiEngineImpl.mLeftPopPanelStack.iterator();
            while (it.hasNext()) {
                String next = it.next();
                if (!TextUtils.isEmpty(next)) {
                    enterScene(next, this.mPackageName, true, VuiConstants.SCREEN_DISPLAY_LF);
                }
            }
        }
        if (VuiEngineImpl.mRightPopPanelStack.size() > 0) {
            Iterator<String> it2 = VuiEngineImpl.mRightPopPanelStack.iterator();
            while (it2.hasNext()) {
                String next2 = it2.next();
                if (!TextUtils.isEmpty(next2)) {
                    enterScene(next2, this.mPackageName, true, VuiConstants.SCREEN_DISPLAY_RF);
                }
            }
        }
    }

    public void handleAllSceneCache(boolean z) {
        try {
            if (this.sceneIds == null) {
                return;
            }
            for (int i = 0; i < this.sceneIds.size(); i++) {
                String str = this.sceneIds.get(i);
                if (z) {
                    VuiSceneTestCacheFactory.instance().removeAllCache(str);
                    VuiSceneInfo vuiSceneInfo = this.mVuiSceneInfoMap.get(str);
                    if (vuiSceneInfo != null) {
                        vuiSceneInfo.reset(false);
                        this.mVuiSceneInfoMap.put(str, vuiSceneInfo);
                    }
                } else {
                    ((VuiSceneBuildTestCache) VuiSceneTestCacheFactory.instance().getSceneCache(VuiSceneTestCacheFactory.CacheType.BUILD.getType())).setUploadedState(str, false);
                    VuiSceneTestCacheFactory.instance().removeOtherCache(str);
                }
            }
        } catch (Exception e) {
            LogUtils.e("VuiSceneToolManager", "handleAllSceneCache e:" + e.getMessage());
        }
    }

    public void sendBroadCast() {
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiSceneToolManager.7
                @Override // java.lang.Runnable
                public void run() {
                    if (VuiSceneToolManager.this.mBinder == null) {
                        VuiSceneToolManager.this.mBinder = new Binder();
                    }
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    intent.setAction("com.xiaopeng.speech.vuiengine.start");
                    intent.setPackage(VuiConstants.VUI_SCENE_APP);
                    bundle.putBinder("client", VuiSceneToolManager.this.mBinder);
                    bundle.putString(ClientConstants.ALIAS.P_NAME, VuiSceneToolManager.this.mPackageName);
                    bundle.putString("version", VuiSceneToolManager.this.mPackageVersion);
                    if (VuiSceneToolManager.this.hasProcessFeature()) {
                        bundle.putString("processName", VuiSceneToolManager.this.getProcessName());
                    }
                    intent.putExtra("bundle", bundle);
                    VuiSceneToolManager.this.mContext.sendBroadcast(intent);
                }
            });
        }
    }

    public void subscribe(final boolean z) {
        Handler handler = this.mApiRouterHandler;
        if (handler != null) {
            handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiSceneToolManager.8
                @Override // java.lang.Runnable
                public void run() {
                    if (VuiUtils.canUseVuiFeature()) {
                        LogUtils.logInfo("VuiSceneToolManager", "subscribe：" + VuiSceneToolManager.this.mObserver);
                        new String[]{"command://scene.control"};
                        try {
                            if (VuiSceneToolManager.this.hasProcessFeature()) {
                                ApiRouter.route(new Uri.Builder().authority(VuiSceneToolManager.this.getAuthority()).path("subscribeProcessTest").appendQueryParameter("observer", VuiSceneToolManager.this.mObserver).appendQueryParameter("param", "").appendQueryParameter(VuiConstants.SCENE_PACKAGE_NAME, VuiSceneToolManager.this.mPackageName).appendQueryParameter("packageVersion", VuiSceneToolManager.this.mPackageVersion).appendQueryParameter("sceneList", VuiSceneToolManager.this.sceneIds.toString()).appendQueryParameter("processName", VuiSceneToolManager.this.getProcessName()).appendQueryParameter("carType", VuiUtils.getVuiCarType()).appendQueryParameter("hardWardId", VuiUtils.getHwVersion()).build());
                            }
                            if (z) {
                                VuiSceneToolManager.this.handleSceneDataInfo();
                            }
                        } catch (Exception e) {
                            LogUtils.e("VuiSceneToolManager", "subscribe e:" + e.fillInStackTrace());
                            VuiSceneToolManager.this.subscribe(z);
                        }
                    }
                }
            });
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:49:0x00a3 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public java.lang.String getProcessName() {
        /*
            r6 = this;
            java.lang.String r0 = "main"
            java.lang.String r1 = r6.mProcessName
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto Ld
            java.lang.String r0 = r6.mProcessName
            return r0
        Ld:
            r1 = 0
            java.io.File r2 = new java.io.File     // Catch: java.lang.Throwable -> L8b java.lang.Exception -> L8d
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L8b java.lang.Exception -> L8d
            r3.<init>()     // Catch: java.lang.Throwable -> L8b java.lang.Exception -> L8d
            java.lang.String r4 = "/proc/"
            java.lang.StringBuilder r3 = r3.append(r4)     // Catch: java.lang.Throwable -> L8b java.lang.Exception -> L8d
            int r4 = android.os.Process.myPid()     // Catch: java.lang.Throwable -> L8b java.lang.Exception -> L8d
            java.lang.StringBuilder r3 = r3.append(r4)     // Catch: java.lang.Throwable -> L8b java.lang.Exception -> L8d
            java.lang.String r4 = "/cmdline"
            java.lang.StringBuilder r3 = r3.append(r4)     // Catch: java.lang.Throwable -> L8b java.lang.Exception -> L8d
            java.lang.String r3 = r3.toString()     // Catch: java.lang.Throwable -> L8b java.lang.Exception -> L8d
            r2.<init>(r3)     // Catch: java.lang.Throwable -> L8b java.lang.Exception -> L8d
            java.io.BufferedReader r3 = new java.io.BufferedReader     // Catch: java.lang.Throwable -> L8b java.lang.Exception -> L8d
            java.io.FileReader r4 = new java.io.FileReader     // Catch: java.lang.Throwable -> L8b java.lang.Exception -> L8d
            r4.<init>(r2)     // Catch: java.lang.Throwable -> L8b java.lang.Exception -> L8d
            r3.<init>(r4)     // Catch: java.lang.Throwable -> L8b java.lang.Exception -> L8d
            java.lang.String r1 = r3.readLine()     // Catch: java.lang.Exception -> L86 java.lang.Throwable -> L9f
            java.lang.String r1 = r1.trim()     // Catch: java.lang.Exception -> L86 java.lang.Throwable -> L9f
            java.lang.String r2 = r6.mPackageName     // Catch: java.lang.Exception -> L84 java.lang.Throwable -> L9f
            boolean r2 = r1.startsWith(r2)     // Catch: java.lang.Exception -> L84 java.lang.Throwable -> L9f
            if (r2 == 0) goto L52
            java.lang.String r2 = r6.mPackageName     // Catch: java.lang.Exception -> L84 java.lang.Throwable -> L9f
            java.lang.String r4 = ""
            java.lang.String r1 = r1.replace(r2, r4)     // Catch: java.lang.Exception -> L84 java.lang.Throwable -> L9f
        L52:
            boolean r2 = android.text.TextUtils.isEmpty(r1)     // Catch: java.lang.Exception -> L84 java.lang.Throwable -> L9f
            if (r2 == 0) goto L59
            goto L5e
        L59:
            r0 = 1
            java.lang.String r0 = r1.substring(r0)     // Catch: java.lang.Exception -> L84 java.lang.Throwable -> L9f
        L5e:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch: java.lang.Exception -> L86 java.lang.Throwable -> L9f
            r1.<init>()     // Catch: java.lang.Exception -> L86 java.lang.Throwable -> L9f
            java.lang.StringBuilder r1 = r1.append(r0)     // Catch: java.lang.Exception -> L86 java.lang.Throwable -> L9f
            java.lang.String r2 = ",pid_"
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch: java.lang.Exception -> L86 java.lang.Throwable -> L9f
            int r2 = android.os.Process.myPid()     // Catch: java.lang.Exception -> L86 java.lang.Throwable -> L9f
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch: java.lang.Exception -> L86 java.lang.Throwable -> L9f
            java.lang.String r1 = r1.toString()     // Catch: java.lang.Exception -> L86 java.lang.Throwable -> L9f
            r6.mProcessName = r1     // Catch: java.lang.Exception -> L86 java.lang.Throwable -> L9f
            r3.close()     // Catch: java.lang.Exception -> L7f
            goto L83
        L7f:
            r0 = move-exception
            r0.printStackTrace()
        L83:
            return r1
        L84:
            r0 = move-exception
            goto L91
        L86:
            r1 = move-exception
            r5 = r1
            r1 = r0
            r0 = r5
            goto L91
        L8b:
            r0 = move-exception
            goto La1
        L8d:
            r2 = move-exception
            r3 = r1
            r1 = r0
            r0 = r2
        L91:
            r0.printStackTrace()     // Catch: java.lang.Throwable -> L9f
            if (r3 == 0) goto L9e
            r3.close()     // Catch: java.lang.Exception -> L9a
            goto L9e
        L9a:
            r0 = move-exception
            r0.printStackTrace()
        L9e:
            return r1
        L9f:
            r0 = move-exception
            r1 = r3
        La1:
            if (r1 == 0) goto Lab
            r1.close()     // Catch: java.lang.Exception -> La7
            goto Lab
        La7:
            r1 = move-exception
            r1.printStackTrace()
        Lab:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaopeng.speech.vui.VuiSceneToolManager.getProcessName():java.lang.String");
    }

    public void setProcessName(String str) {
        this.mProcessName = str + ",pid_" + Process.myPid();
    }

    public void unSubscribe() {
        Handler handler;
        if (VuiUtils.canUseVuiFeature() && (handler = this.mHandler) != null) {
            handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiSceneToolManager.9
                @Override // java.lang.Runnable
                public void run() {
                    VuiSceneToolManager.this.unsubscribe();
                }
            });
        }
    }

    public void unsubscribe() {
        if (TextUtils.isEmpty(this.mObserver)) {
            LogUtils.e("VuiSceneToolManager", "mObserver == null");
            return;
        }
        Handler handler = this.mApiRouterHandler;
        if (handler != null) {
            handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiSceneToolManager.10
                @Override // java.lang.Runnable
                public void run() {
                    if (VuiSceneToolManager.this.hasProcessFeature()) {
                        try {
                            ApiRouter.route(new Uri.Builder().authority(VuiSceneToolManager.this.getAuthority()).path("unsubscribeProcess").appendQueryParameter("observer", VuiSceneToolManager.this.mObserver).appendQueryParameter(VuiConstants.SCENE_PACKAGE_NAME, VuiSceneToolManager.this.mPackageName).appendQueryParameter("packageVersion", VuiSceneToolManager.this.mPackageVersion).appendQueryParameter("processName", VuiSceneToolManager.this.getProcessName()).build());
                            return;
                        } catch (Throwable th) {
                            th.printStackTrace();
                            LogUtils.e("VuiSceneToolManager", "unsubscribe e:" + th.getMessage());
                            return;
                        }
                    }
                    try {
                        ApiRouter.route(new Uri.Builder().authority(VuiSceneToolManager.this.getAuthority()).path("unsubscribe").appendQueryParameter("observer", VuiSceneToolManager.this.mObserver).appendQueryParameter(VuiConstants.SCENE_PACKAGE_NAME, VuiSceneToolManager.this.mPackageName).appendQueryParameter("packageVersion", VuiSceneToolManager.this.mPackageVersion).build());
                    } catch (Throwable th2) {
                        th2.printStackTrace();
                        LogUtils.e("VuiSceneToolManager", "unsubscribe e:" + th2.getMessage());
                    }
                }
            });
        }
    }

    public synchronized void sendSceneData(final String str) {
        try {
            this.mHandler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiSceneToolManager.11
                @Override // java.lang.Runnable
                public void run() {
                    if (VuiUtils.cannotUpload()) {
                        return;
                    }
                    String str2 = str;
                    if (!TextUtils.isEmpty(str2)) {
                        VuiSceneToolManager.this.checkUploadScene(str2);
                        return;
                    }
                    String str3 = VuiEngineImpl.mActiveSceneIds.get(VuiConstants.SCREEN_DISPLAY_LF);
                    String str4 = VuiEngineImpl.mActiveSceneIds.get(VuiConstants.SCREEN_DISPLAY_RF);
                    LogUtils.i("VuiSceneToolManager", "sendSceneData   ========   " + str3 + "   ::::  " + str4);
                    if (!TextUtils.isEmpty(str3)) {
                        VuiSceneToolManager.this.checkUploadScene(str3);
                    }
                    if (!TextUtils.isEmpty(str4)) {
                        VuiSceneToolManager.this.checkUploadScene(str4);
                    }
                    if (VuiEngineImpl.mLeftPopPanelStack.size() > 0) {
                        Iterator<String> it = VuiEngineImpl.mLeftPopPanelStack.iterator();
                        while (it.hasNext()) {
                            String next = it.next();
                            if (!TextUtils.isEmpty(next)) {
                                VuiSceneToolManager.this.checkUploadScene(next);
                            }
                        }
                    }
                    if (VuiEngineImpl.mRightPopPanelStack.size() > 0) {
                        Iterator<String> it2 = VuiEngineImpl.mRightPopPanelStack.iterator();
                        while (it2.hasNext()) {
                            String next2 = it2.next();
                            if (!TextUtils.isEmpty(next2)) {
                                VuiSceneToolManager.this.checkUploadScene(next2);
                            }
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.fillInStackTrace();
            LogUtils.e("VuiSceneToolManager", "sendSceneData e:" + e.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkUploadScene(String str) {
        VuiSceneTestCache sceneCache;
        int fusionType;
        VuiDisplayLocationInfoTestCache vuiDisplayLocationInfoTestCache;
        List<VuiElement> cache;
        if (!isUploadScene(str) || (sceneCache = VuiSceneTestCacheFactory.instance().getSceneCache(VuiSceneTestCacheFactory.CacheType.UPDATE.getType())) == null || (fusionType = sceneCache.getFusionType(str)) == VuiSceneTestCacheFactory.CacheType.DEFAULT.getType()) {
            return;
        }
        VuiScene newVuiScene = VuiUtils.getNewVuiScene(str, System.currentTimeMillis(), this.vuiEngine.isAccessibilityScene(), this.mPackageName, this.mPackageVersion);
        if (fusionType == VuiSceneTestCacheFactory.CacheType.UPDATE.getType()) {
            newVuiScene.setElements(sceneCache.getCache(str));
            sendSceneData(1, false, newVuiScene, false);
        } else if (fusionType == VuiSceneTestCacheFactory.CacheType.BUILD.getType()) {
            VuiSceneTestCache sceneCache2 = VuiSceneTestCacheFactory.instance().getSceneCache(VuiSceneTestCacheFactory.CacheType.BUILD.getType());
            if (sceneCache2 == null || (cache = sceneCache2.getCache(str)) == null || cache.isEmpty()) {
                return;
            }
            newVuiScene.setElements(sceneCache2.getCache(str));
            sendSceneData(0, false, newVuiScene, false);
        } else if (fusionType == VuiSceneTestCacheFactory.CacheType.REMOVE.getType()) {
            VuiSceneRemoveTestCache vuiSceneRemoveTestCache = (VuiSceneRemoveTestCache) VuiSceneTestCacheFactory.instance().getSceneCache(VuiSceneTestCacheFactory.CacheType.REMOVE.getType());
            if (vuiSceneRemoveTestCache != null) {
                sendSceneData(3, false, str + "," + vuiSceneRemoveTestCache.getRemoveCache(str).toString().replace("[", "").replace("]", ""), false);
            }
        } else if (fusionType != VuiSceneTestCacheFactory.CacheType.DISPLAY_LOCATION.getType() || (vuiDisplayLocationInfoTestCache = (VuiDisplayLocationInfoTestCache) VuiSceneTestCacheFactory.instance().getSceneCache(VuiSceneTestCacheFactory.CacheType.DISPLAY_LOCATION.getType())) == null) {
        } else {
            String displayCache = vuiDisplayLocationInfoTestCache.getDisplayCache(newVuiScene.getSceneId());
            if (TextUtils.isEmpty(displayCache)) {
                return;
            }
            newVuiScene.setDisplayLocation(displayCache);
            updateDisplayLocation(newVuiScene, true);
        }
    }

    public void sendSceneData(int i, boolean z, Object obj) {
        sendSceneData(i, z, obj, false);
    }

    public void sendSceneData(int i, boolean z, Object obj, boolean z2) {
        Message obtainMessage = this.mHandler.obtainMessage();
        obtainMessage.arg1 = i;
        obtainMessage.what = SEND_UPLOAD_MESSAGE;
        obtainMessage.arg2 = z ? 1 : 0;
        obtainMessage.obj = obj;
        this.mHandler.sendMessage(obtainMessage);
    }

    /* loaded from: classes2.dex */
    private static class Holder {
        private static final VuiSceneToolManager Instance = new VuiSceneToolManager();

        private Holder() {
        }
    }

    public void buildScene(final VuiScene vuiScene, final boolean z, final boolean z2) {
        Handler handler = this.mApiRouterHandler;
        if (handler != null) {
            handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiSceneToolManager.12
                @Override // java.lang.Runnable
                public void run() {
                    VuiScene vuiScene2;
                    String sceneId;
                    VuiDisplayLocationInfoTestCache vuiDisplayLocationInfoTestCache;
                    if (VuiUtils.cannotUpload() || (vuiScene2 = vuiScene) == null || vuiScene2.getElements() == null || vuiScene.getElements().size() < 0 || (sceneId = vuiScene.getSceneId()) == null) {
                        return;
                    }
                    VuiSceneTestCache sceneCache = VuiSceneTestCacheFactory.instance().getSceneCache(VuiSceneTestCacheFactory.CacheType.BUILD.getType());
                    if (z || z2 || !sceneCache.getUploadedState(sceneId)) {
                        if (sceneCache != null && z) {
                            List<VuiElement> cache = sceneCache.getCache(sceneId);
                            if (cache != null && !cache.isEmpty()) {
                                vuiScene.setElements(sceneCache.getUpdateFusionCache(sceneId, vuiScene.getElements(), false));
                            }
                            sceneCache.setCache(sceneId, vuiScene.getElements());
                            if (!VuiConstants.SCREEN_DISPLAY_LF.equals(vuiScene.getDisplayLocation()) && (vuiDisplayLocationInfoTestCache = (VuiDisplayLocationInfoTestCache) VuiSceneTestCacheFactory.instance().getSceneCache(VuiSceneTestCacheFactory.CacheType.DISPLAY_LOCATION.getType())) != null) {
                                vuiDisplayLocationInfoTestCache.setCache(vuiScene.getSceneId(), vuiScene.getDisplayLocation());
                            }
                        }
                        String vuiSceneConvertToString = VuiUtils.vuiSceneConvertToString(vuiScene);
                        VuiSceneInfo sceneInfo = VuiSceneManager.instance().getSceneInfo(sceneId);
                        if (sceneInfo != null && sceneInfo.isWholeScene() && sceneInfo.isFull()) {
                            sceneInfo.setLastAddStr(null);
                            sceneInfo.setLastUpdateStr(null);
                            LogUtils.logDebug("VuiSceneToolManager", "build full_scene_info:" + vuiSceneConvertToString);
                            if (VuiSceneToolManager.this.isUploadScene(sceneId)) {
                                Uri.Builder builder = new Uri.Builder();
                                builder.authority(VuiSceneToolManager.this.getAuthority()).path("buildScene").appendQueryParameter(VuiConstants.SCENE_ID, sceneId).appendQueryParameter(VuiConstants.SCENE_PACKAGE_NAME, vuiScene.getPackageName()).appendQueryParameter("packageVersion", vuiScene.getVersion()).appendQueryParameter("sceneData", vuiSceneConvertToString);
                                try {
                                    LogUtils.logDebug("VuiSceneToolManager", " send buildScene to test" + sceneId);
                                    if (sceneCache != null) {
                                        sceneCache.setUploadedState(sceneId, false);
                                    }
                                    String str = (String) ApiRouter.route(builder.build());
                                    if (!TextUtils.isEmpty(str) && sceneCache != null) {
                                        sceneCache.setUploadedState(sceneId, true);
                                    }
                                    LogUtils.logInfo("VuiSceneToolManager", " send buildScene to test success" + sceneId + ",result:" + str);
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                    if (sceneCache != null) {
                                        sceneCache.setUploadedState(sceneId, false);
                                    }
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    public void addSceneElement(final VuiScene vuiScene, final String str) {
        Handler handler = this.mApiRouterHandler;
        if (handler != null) {
            handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiSceneToolManager.13
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        if (VuiUtils.cannotUpload()) {
                            return;
                        }
                        String vuiSceneConvertToString = VuiUtils.vuiSceneConvertToString(vuiScene);
                        Uri.Builder builder = new Uri.Builder();
                        builder.authority(VuiSceneToolManager.this.getAuthority()).path("addSceneElement").appendQueryParameter(VuiConstants.SCENE_ID, vuiScene.getSceneId()).appendQueryParameter("parentId", str).appendQueryParameter(VuiConstants.SCENE_PACKAGE_NAME, vuiScene.getPackageName()).appendQueryParameter("packageVersion", vuiScene.getVersion()).appendQueryParameter("sceneData", vuiSceneConvertToString);
                        LogUtils.logDebug("VuiSceneToolManager", "addSceneElement to CarSpeech " + vuiScene.getSceneId());
                        LogUtils.logInfo("VuiSceneToolManager", "addSceneElement to CarSpeech success" + vuiScene.getSceneId() + ",result:" + ((String) ApiRouter.route(builder.build())));
                    } catch (Exception e) {
                        e.printStackTrace();
                        LogUtils.e("VuiSceneToolManager", "addSceneElement e:" + e.getMessage());
                    }
                }
            });
        }
    }

    public void addSceneElementGroup(final VuiScene vuiScene, boolean z) {
        Handler handler = this.mApiRouterHandler;
        if (handler != null) {
            handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiSceneToolManager.14
                @Override // java.lang.Runnable
                public void run() {
                    VuiScene vuiScene2;
                    String sceneId;
                    VuiSceneRemoveTestCache vuiSceneRemoveTestCache;
                    List<VuiElement> elements;
                    List<VuiElement> list;
                    if (VuiUtils.cannotUpload() || (vuiScene2 = vuiScene) == null || (sceneId = vuiScene2.getSceneId()) == null) {
                        return;
                    }
                    VuiSceneTestCache sceneCache = VuiSceneTestCacheFactory.instance().getSceneCache(VuiSceneTestCacheFactory.CacheType.UPDATE.getType());
                    VuiSceneTestCache sceneCache2 = VuiSceneTestCacheFactory.instance().getSceneCache(VuiSceneTestCacheFactory.CacheType.BUILD.getType());
                    int type = VuiSceneTestCacheFactory.CacheType.DEFAULT.getType();
                    if (sceneCache != null) {
                        type = sceneCache.getFusionType(sceneId);
                        if (type == VuiSceneTestCacheFactory.CacheType.UPDATE.getType()) {
                            list = sceneCache.getUpdateFusionCache(sceneId, vuiScene.getElements(), false);
                        } else {
                            if (type == VuiSceneTestCacheFactory.CacheType.BUILD.getType()) {
                                if (sceneCache2 != null) {
                                    list = sceneCache2.getFusionCache(sceneId, vuiScene.getElements(), false);
                                }
                            } else if (type == VuiSceneTestCacheFactory.CacheType.REMOVE.getType() && (vuiSceneRemoveTestCache = (VuiSceneRemoveTestCache) VuiSceneTestCacheFactory.instance().getSceneCache(VuiSceneTestCacheFactory.CacheType.REMOVE.getType())) != null && (elements = vuiScene.getElements()) != null && elements.size() == 1) {
                                vuiSceneRemoveTestCache.deleteRemoveIdFromCache(sceneId, vuiScene.getElements().get(0).id);
                            }
                            list = null;
                        }
                        if (list != null) {
                            vuiScene.setElements(list);
                        }
                    }
                    if (VuiSceneToolManager.this.isUploadScene(sceneId)) {
                        if (type == VuiSceneTestCacheFactory.CacheType.BUILD.getType()) {
                            VuiSceneToolManager.this.sendBuildCacheInOther(sceneId, vuiScene, null);
                            return;
                        }
                        String vuiSceneConvertToString = VuiUtils.vuiSceneConvertToString(vuiScene);
                        Uri.Builder builder = new Uri.Builder();
                        builder.authority(VuiSceneToolManager.this.getAuthority()).path("addSceneElementGroup").appendQueryParameter(VuiConstants.SCENE_ID, sceneId).appendQueryParameter(VuiConstants.SCENE_PACKAGE_NAME, vuiScene.getPackageName()).appendQueryParameter("packageVersion", vuiScene.getVersion()).appendQueryParameter("sceneData", vuiSceneConvertToString);
                        try {
                            LogUtils.logDebug("VuiSceneToolManager", "addSceneElementGroup to CarSpeech " + sceneId);
                            String str = (String) ApiRouter.route(builder.build());
                            LogUtils.logInfo("VuiSceneToolManager", "addSceneElementGroup to CarSpeech success " + sceneId + ",result:" + str);
                            if (TextUtils.isEmpty(str)) {
                                if (sceneCache != null) {
                                    sceneCache.setCache(sceneId, vuiScene.getElements());
                                }
                            } else if (type == VuiSceneTestCacheFactory.CacheType.UPDATE.getType()) {
                                sceneCache.removeCache(sceneId);
                            }
                        } catch (RemoteException e) {
                            LogUtils.e("VuiSceneToolManager", "addSceneElementGroup " + e.fillInStackTrace());
                            e.printStackTrace();
                            if (sceneCache != null) {
                                sceneCache.setCache(sceneId, vuiScene.getElements());
                            }
                        }
                        if (sceneCache2 != null) {
                            List<VuiElement> fusionCache = sceneCache2.getFusionCache(sceneId, vuiScene.getElements(), false);
                            if (fusionCache != null) {
                                sceneCache2.setCache(sceneId, fusionCache);
                            }
                            vuiScene.setElements(fusionCache);
                        }
                        if ("user".equals(Build.TYPE) || LogUtils.getLogLevel() > LogUtils.LOG_DEBUG_LEVEL) {
                            return;
                        }
                        LogUtils.logDebug("VuiSceneToolManager", "addSceneElementGroup full_scene_info:" + VuiUtils.vuiSceneConvertToString(vuiScene));
                        return;
                    }
                    if (type != VuiSceneTestCacheFactory.CacheType.BUILD.getType()) {
                        if (sceneCache != null) {
                            sceneCache.setCache(sceneId, vuiScene.getElements());
                        }
                        if (sceneCache2 != null) {
                            List<VuiElement> fusionCache2 = sceneCache2.getFusionCache(sceneId, vuiScene.getElements(), false);
                            if (fusionCache2 != null) {
                                sceneCache2.setCache(sceneId, fusionCache2);
                            }
                            vuiScene.setElements(fusionCache2);
                        }
                    } else if (sceneCache2 != null) {
                        sceneCache2.setCache(sceneId, vuiScene.getElements());
                    }
                    if ("user".equals(Build.TYPE) || LogUtils.getLogLevel() > LogUtils.LOG_DEBUG_LEVEL) {
                        return;
                    }
                    LogUtils.logDebug("VuiSceneToolManager", "addSceneElementGroup from full_scene_info:" + VuiUtils.vuiSceneConvertToString(vuiScene));
                }
            });
        }
    }

    public void removeSceneElementGroup(final String str, final String str2, final boolean z) {
        Handler handler = this.mApiRouterHandler;
        if (handler != null) {
            handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiSceneToolManager.15
                /* JADX WARN: Removed duplicated region for block: B:41:0x015c  */
                /* JADX WARN: Removed duplicated region for block: B:73:0x022f  */
                @Override // java.lang.Runnable
                /*
                    Code decompiled incorrectly, please refer to instructions dump.
                    To view partially-correct add '--show-bad-code' argument
                */
                public void run() {
                    /*
                        Method dump skipped, instructions count: 750
                        To view this dump add '--comments-level debug' option
                    */
                    throw new UnsupportedOperationException("Method not decompiled: com.xiaopeng.speech.vui.VuiSceneToolManager.AnonymousClass15.run():void");
                }
            });
        }
    }

    public void vuiFeedBack(final View view, final VuiFeedback vuiFeedback) {
        Handler handler = this.mApiRouterHandler;
        if (handler != null) {
            handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiSceneToolManager.16
                @Override // java.lang.Runnable
                public void run() {
                    View view2;
                    if (vuiFeedback == null || (view2 = view) == null) {
                        return;
                    }
                    String str = null;
                    if (view2 != null && view2.getId() != -1 && view.getId() != 0) {
                        str = VuiUtils.getResourceName(view.getId());
                    }
                    if (VuiUtils.isUseDisplayLocationPlatForm()) {
                        VuiSceneToolManager.this.vuiFeedBack(str, vuiFeedback.content);
                        return;
                    }
                    Uri.Builder builder = new Uri.Builder();
                    builder.authority(VuiSceneToolManager.this.getAuthority()).path("vuiFeedback").appendQueryParameter(VuiConstants.SCENE_PACKAGE_NAME, VuiSceneToolManager.this.mPackageName).appendQueryParameter("packageVersion", VuiSceneToolManager.this.mPackageVersion).appendQueryParameter("resourceName", str).appendQueryParameter(VideoRestoreStatusReceiver.KEY_STATUS, "" + vuiFeedback.state).appendQueryParameter("type", vuiFeedback.getFeedbackType().getType()).appendQueryParameter(ClientConstants.BINDER.SCHEME, vuiFeedback.content);
                    try {
                        LogUtils.logDebug("VuiSceneToolManager", "vuiFeedBack ");
                        String str2 = (String) ApiRouter.route(builder.build());
                        LogUtils.logInfo("VuiSceneToolManager", "vuiFeedBack success");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void vuiFeedBack(final String str, final VuiFeedback vuiFeedback) {
        Handler handler = this.mApiRouterHandler;
        if (handler != null) {
            handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiSceneToolManager.17
                @Override // java.lang.Runnable
                public void run() {
                    if (vuiFeedback == null || TextUtils.isEmpty(str)) {
                        return;
                    }
                    String str2 = "";
                    if (VuiUtils.isUseDisplayLocationPlatForm()) {
                        int intValue = Integer.valueOf(str).intValue();
                        if (intValue != -1 && intValue != 0) {
                            str2 = VuiUtils.getResourceName(intValue);
                        }
                        VuiSceneToolManager.this.vuiFeedBack(str2, vuiFeedback.content);
                        return;
                    }
                    Uri.Builder builder = new Uri.Builder();
                    builder.authority(VuiSceneToolManager.this.getAuthority()).path("vuiFeedback").appendQueryParameter(VuiConstants.SCENE_PACKAGE_NAME, VuiSceneToolManager.this.mPackageName).appendQueryParameter("packageVersion", VuiSceneToolManager.this.mPackageVersion).appendQueryParameter("resourceName", "").appendQueryParameter(VideoRestoreStatusReceiver.KEY_STATUS, "" + vuiFeedback.state).appendQueryParameter("type", vuiFeedback.getFeedbackType().getType()).appendQueryParameter(ClientConstants.BINDER.SCHEME, vuiFeedback.content);
                    try {
                        LogUtils.logDebug("VuiSceneToolManager", "vuiFeedBack ");
                        String str3 = (String) ApiRouter.route(builder.build());
                        LogUtils.logInfo("VuiSceneToolManager", "vuiFeedBack success");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void updateSceneElementAttr(final VuiScene vuiScene, boolean z) {
        Handler handler = this.mApiRouterHandler;
        if (handler != null) {
            handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiSceneToolManager.18
                @Override // java.lang.Runnable
                public void run() {
                    VuiScene vuiScene2;
                    String sceneId;
                    List<VuiElement> fusionCache;
                    if (VuiUtils.cannotUpload() || (vuiScene2 = vuiScene) == null || vuiScene2.getElements() == null || vuiScene.getElements().size() < 0 || (sceneId = vuiScene.getSceneId()) == null) {
                        return;
                    }
                    VuiSceneTestCache sceneCache = VuiSceneTestCacheFactory.instance().getSceneCache(VuiSceneTestCacheFactory.CacheType.BUILD.getType());
                    VuiSceneTestCache sceneCache2 = VuiSceneTestCacheFactory.instance().getSceneCache(VuiSceneTestCacheFactory.CacheType.UPDATE.getType());
                    int type = VuiSceneTestCacheFactory.CacheType.DEFAULT.getType();
                    if (sceneCache2 != null) {
                        type = sceneCache2.getFusionType(sceneId);
                        if (type == VuiSceneTestCacheFactory.CacheType.UPDATE.getType()) {
                            fusionCache = sceneCache2.getUpdateFusionCache(sceneId, vuiScene.getElements(), true);
                        } else {
                            fusionCache = (type != VuiSceneTestCacheFactory.CacheType.BUILD.getType() || sceneCache == null) ? null : sceneCache.getFusionCache(sceneId, vuiScene.getElements(), true);
                        }
                        if (fusionCache != null) {
                            vuiScene.setElements(fusionCache);
                        }
                    }
                    String vuiSceneConvertToString = VuiUtils.vuiSceneConvertToString(vuiScene);
                    if (VuiSceneToolManager.this.isUploadScene(sceneId)) {
                        if (type == VuiSceneTestCacheFactory.CacheType.BUILD.getType()) {
                            VuiSceneToolManager.this.sendBuildCacheInOther(sceneId, vuiScene, null);
                            return;
                        }
                        Uri.Builder builder = new Uri.Builder();
                        builder.authority(VuiSceneToolManager.this.getAuthority()).path("updateScene").appendQueryParameter(VuiConstants.SCENE_ID, sceneId).appendQueryParameter(VuiConstants.SCENE_PACKAGE_NAME, vuiScene.getPackageName()).appendQueryParameter("packageVersion", vuiScene.getVersion()).appendQueryParameter("sceneData", vuiSceneConvertToString);
                        try {
                            LogUtils.logDebug("VuiSceneToolManager", " updateSceneElementAttr to CarSpeech" + sceneId);
                            String str = (String) ApiRouter.route(builder.build());
                            LogUtils.logInfo("VuiSceneToolManager", "updateSceneElementAttr to CarSpeech success" + sceneId + ",result:" + str);
                            if (TextUtils.isEmpty(str)) {
                                if (sceneCache2 != null) {
                                    sceneCache2.setCache(sceneId, vuiScene.getElements());
                                }
                            } else if (sceneCache2 != null) {
                                sceneCache2.removeCache(sceneId);
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                            LogUtils.e("VuiSceneToolManager", "updateSceneElementAttr " + e.fillInStackTrace());
                            if (sceneCache2 != null) {
                                sceneCache2.setCache(sceneId, vuiScene.getElements());
                            }
                        }
                        if (sceneCache != null) {
                            List<VuiElement> fusionCache2 = sceneCache.getFusionCache(sceneId, vuiScene.getElements(), true);
                            if (fusionCache2 != null) {
                                sceneCache.setCache(sceneId, fusionCache2);
                            }
                            vuiScene.setElements(fusionCache2);
                        }
                        if ("user".equals(Build.TYPE) || LogUtils.getLogLevel() > LogUtils.LOG_DEBUG_LEVEL) {
                            return;
                        }
                        LogUtils.logDebug("VuiSceneToolManager", "updateSceneElementAttr " + VuiUtils.vuiSceneConvertToString(vuiScene));
                        return;
                    }
                    if (type != VuiSceneTestCacheFactory.CacheType.BUILD.getType()) {
                        if (sceneCache2 != null) {
                            sceneCache2.setCache(sceneId, ((VuiScene) new Gson().fromJson(vuiSceneConvertToString, (Class<Object>) VuiScene.class)).getElements());
                        }
                        if (sceneCache != null) {
                            List<VuiElement> fusionCache3 = sceneCache.getFusionCache(sceneId, vuiScene.getElements(), true);
                            if (fusionCache3 != null) {
                                sceneCache.setCache(sceneId, fusionCache3);
                            }
                            vuiScene.setElements(fusionCache3);
                        }
                    } else if (sceneCache != null) {
                        sceneCache.setCache(sceneId, vuiScene.getElements());
                    }
                    if ("user".equals(Build.TYPE) || LogUtils.getLogLevel() > LogUtils.LOG_DEBUG_LEVEL) {
                        return;
                    }
                    LogUtils.logDebug("VuiSceneToolManager", "updateSceneElementAttr cache" + VuiUtils.vuiSceneConvertToString(vuiScene));
                }
            });
        }
    }

    public void updateDynamicScene(final VuiScene vuiScene, final boolean z) {
        Handler handler = this.mApiRouterHandler;
        if (handler != null) {
            handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiSceneToolManager.19
                @Override // java.lang.Runnable
                public void run() {
                    VuiScene vuiScene2;
                    String sceneId;
                    List<VuiElement> cache;
                    if (VuiUtils.cannotUpload() || (vuiScene2 = vuiScene) == null || vuiScene2.getElements() == null || vuiScene.getElements().size() < 0 || (sceneId = vuiScene.getSceneId()) == null || VuiSceneManager.instance().getSceneInfo(sceneId) == null) {
                        return;
                    }
                    VuiSceneTestCache sceneCache = VuiSceneTestCacheFactory.instance().getSceneCache(VuiSceneTestCacheFactory.CacheType.BUILD.getType());
                    VuiSceneTestCache sceneCache2 = VuiSceneTestCacheFactory.instance().getSceneCache(VuiSceneTestCacheFactory.CacheType.UPDATE.getType());
                    if (!z) {
                        if (VuiSceneToolManager.this.isUploadScene(sceneId) && sceneCache2.getFusionType(sceneId) == VuiSceneTestCacheFactory.CacheType.UPDATE.getType()) {
                            String vuiSceneConvertToString = VuiUtils.vuiSceneConvertToString(vuiScene);
                            Uri.Builder builder = new Uri.Builder();
                            builder.authority(VuiSceneToolManager.this.getAuthority()).path("updateScene").appendQueryParameter(VuiConstants.SCENE_ID, sceneId).appendQueryParameter(VuiConstants.SCENE_PACKAGE_NAME, vuiScene.getPackageName()).appendQueryParameter("packageVersion", vuiScene.getVersion()).appendQueryParameter("sceneData", vuiSceneConvertToString);
                            try {
                                LogUtils.logDebug("VuiSceneToolManager", " updateScene to CarSpeech " + sceneId);
                                String str = (String) ApiRouter.route(builder.build());
                                if (TextUtils.isEmpty(str)) {
                                    if (sceneCache2 != null) {
                                        sceneCache2.setCache(sceneId, vuiScene.getElements());
                                    }
                                } else if (sceneCache2 != null) {
                                    sceneCache2.removeCache(sceneId);
                                }
                                LogUtils.logInfo("VuiSceneToolManager", " updateScene to CarSpeech success" + sceneId + ",result:" + str);
                                return;
                            } catch (RemoteException e) {
                                e.printStackTrace();
                                LogUtils.e("VuiSceneToolManager", "updateScene " + e.fillInStackTrace());
                                if (sceneCache2 != null) {
                                    sceneCache2.setCache(sceneId, vuiScene.getElements());
                                    return;
                                }
                                return;
                            }
                        }
                        return;
                    }
                    int type = VuiSceneTestCacheFactory.CacheType.DEFAULT.getType();
                    if (sceneCache2 != null) {
                        type = sceneCache2.getFusionType(sceneId);
                        if (type == VuiSceneTestCacheFactory.CacheType.UPDATE.getType()) {
                            cache = sceneCache2.getUpdateFusionCache(sceneId, vuiScene.getElements(), false);
                        } else {
                            cache = (type != VuiSceneTestCacheFactory.CacheType.BUILD.getType() || sceneCache == null) ? null : sceneCache.getCache(sceneId);
                        }
                        if (cache != null) {
                            vuiScene.setElements(cache);
                        }
                    }
                    if (VuiSceneToolManager.this.isUploadScene(sceneId)) {
                        if (type == VuiSceneTestCacheFactory.CacheType.BUILD.getType()) {
                            VuiSceneToolManager.this.sendBuildCacheInOther(sceneId, vuiScene, null);
                            return;
                        }
                        String vuiSceneConvertToString2 = VuiUtils.vuiSceneConvertToString(vuiScene);
                        Uri.Builder builder2 = new Uri.Builder();
                        builder2.authority(VuiSceneToolManager.this.getAuthority()).path("updateScene").appendQueryParameter(VuiConstants.SCENE_ID, sceneId).appendQueryParameter(VuiConstants.SCENE_PACKAGE_NAME, vuiScene.getPackageName()).appendQueryParameter("packageVersion", vuiScene.getVersion()).appendQueryParameter("sceneData", vuiSceneConvertToString2);
                        try {
                            LogUtils.logDebug("VuiSceneToolManager", " updateScene to CarSpeech" + sceneId);
                            String str2 = (String) ApiRouter.route(builder2.build());
                            LogUtils.logInfo("VuiSceneToolManager", "updateScene to CarSpeech success" + sceneId + ",result:" + str2);
                            if (TextUtils.isEmpty(str2)) {
                                if (sceneCache2 != null) {
                                    sceneCache2.setCache(sceneId, vuiScene.getElements());
                                }
                            } else if (sceneCache2 != null) {
                                sceneCache2.removeCache(sceneId);
                            }
                        } catch (RemoteException e2) {
                            e2.printStackTrace();
                            LogUtils.e("VuiSceneToolManager", "updateScene " + e2.fillInStackTrace());
                            if (sceneCache2 != null) {
                                sceneCache2.setCache(sceneId, vuiScene.getElements());
                            }
                        }
                    } else if (type == VuiSceneTestCacheFactory.CacheType.BUILD.getType() || sceneCache2 == null) {
                    } else {
                        sceneCache2.setCache(sceneId, vuiScene.getElements());
                    }
                }
            });
        }
    }

    public String enterScene(final String str, final String str2, boolean z, final String str3) {
        if (str != null && str2 != null) {
            try {
            } catch (Exception e) {
                LogUtils.e("VuiSceneToolManager", "enterScene--------------e: " + e.fillInStackTrace());
                e.printStackTrace();
            }
            if (!VuiUtils.canUseVuiFeature()) {
                return "";
            }
            if (z) {
                if (isUploadScene(str) && !VuiUtils.cannotUpload()) {
                    sendSceneData(str);
                }
                this.mApiRouterHandler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiSceneToolManager.20
                    @Override // java.lang.Runnable
                    public void run() {
                        String str4;
                        try {
                            if (VuiSceneToolManager.this.hasProcessFeature()) {
                                Uri.Builder builder = new Uri.Builder();
                                if (VuiUtils.isUseDisplayLocationPlatForm()) {
                                    builder.authority(VuiSceneToolManager.this.getAuthority()).path("enterDisplayLocationScene").appendQueryParameter(VuiConstants.SCENE_ID, str).appendQueryParameter("displayLocation", str3).appendQueryParameter(VuiConstants.SCENE_PACKAGE_NAME, str2).appendQueryParameter("packageVersion", VuiSceneToolManager.this.mPackageVersion).appendQueryParameter("processName", VuiSceneToolManager.this.getProcessName());
                                    LogUtils.logDebug("VuiSceneToolManager", "enterScene:  sceneId:" + str + ",dispalyLocation:" + str3);
                                    str4 = (String) ApiRouter.route(builder.build());
                                } else {
                                    builder.authority(VuiSceneToolManager.this.getAuthority()).path("enterProcessScene").appendQueryParameter(VuiConstants.SCENE_ID, str).appendQueryParameter(VuiConstants.SCENE_PACKAGE_NAME, str2).appendQueryParameter("packageVersion", VuiSceneToolManager.this.mPackageVersion).appendQueryParameter("processName", VuiSceneToolManager.this.getProcessName());
                                    LogUtils.logDebug("VuiSceneToolManager", "enterScene: sceneId:" + str);
                                    str4 = (String) ApiRouter.route(builder.build());
                                }
                                if (TextUtils.isEmpty(str4) && VuiSceneToolManager.this.mIsInSpeech) {
                                    VuiSceneToolManager.this.mIsInSpeech = false;
                                }
                                LogUtils.logDebug("VuiSceneToolManager", "enterScene: sceneId success:" + str + ",result:" + str4);
                                return;
                            }
                            Uri.Builder builder2 = new Uri.Builder();
                            builder2.authority(VuiSceneToolManager.this.getAuthority()).path("enterScene").appendQueryParameter(VuiConstants.SCENE_ID, str).appendQueryParameter(VuiConstants.SCENE_PACKAGE_NAME, str2).appendQueryParameter("packageVersion", VuiSceneToolManager.this.mPackageVersion);
                            LogUtils.logDebug("VuiSceneToolManager", "enterScene: sceneId:" + str);
                            if (TextUtils.isEmpty((String) ApiRouter.route(builder2.build())) && VuiSceneToolManager.this.mIsInSpeech) {
                                VuiSceneToolManager.this.mIsInSpeech = false;
                            }
                            LogUtils.logDebug("VuiSceneToolManager", "enterScene: sceneId success:" + str);
                        } catch (Exception e2) {
                            LogUtils.e("VuiSceneToolManager", "enterScene--------------e: " + e2.fillInStackTrace());
                        }
                    }
                });
            }
            if (VuiUtils.cannotUpload()) {
            }
        }
        return "";
    }

    public void exitScene(String str, String str2, boolean z, String str3) {
        exitScene(str, str2, z, str3, false);
    }

    public void destroyScene(final String str) {
        Handler handler;
        if (str == null || (handler = this.mApiRouterHandler) == null) {
            return;
        }
        handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiSceneToolManager.21
            @Override // java.lang.Runnable
            public void run() {
                VuiSceneBuildTestCache vuiSceneBuildTestCache = (VuiSceneBuildTestCache) VuiSceneTestCacheFactory.instance().getSceneCache(VuiSceneTestCacheFactory.CacheType.BUILD.getType());
                if (vuiSceneBuildTestCache != null && vuiSceneBuildTestCache.getUploadedState(str) && !VuiUtils.cannotUpload()) {
                    try {
                        Uri.Builder builder = new Uri.Builder();
                        if (!VuiSceneToolManager.this.hasProcessFeature()) {
                            builder.authority(VuiSceneToolManager.this.getAuthority()).path("destroyScene").appendQueryParameter("sceneIds", str).appendQueryParameter(VuiConstants.SCENE_PACKAGE_NAME, VuiSceneToolManager.this.mPackageName).appendQueryParameter("packageVersion", VuiSceneToolManager.this.mPackageVersion);
                        } else {
                            builder.authority(VuiSceneToolManager.this.getAuthority()).path("destroyProcessScene").appendQueryParameter("sceneIds", str).appendQueryParameter(VuiConstants.SCENE_PACKAGE_NAME, VuiSceneToolManager.this.mPackageName).appendQueryParameter("packageVersion", VuiSceneToolManager.this.mPackageVersion).appendQueryParameter("processName", VuiSceneToolManager.this.getProcessName());
                        }
                        LogUtils.logDebug("VuiSceneToolManager", "destroyScene-------------- " + str);
                        ApiRouter.route(builder.build());
                        LogUtils.logDebug("VuiSceneToolManager", "destroyScene--------------success " + str);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                LogUtils.logDebug("VuiSceneToolManager", "destory removeAllCache--------------" + str);
                VuiSceneTestCacheFactory.instance().removeAllCache(str);
            }
        });
    }

    public void cleanExpiredSceneTime(final String str) {
        Handler handler;
        if (str == null || (handler = this.mApiRouterHandler) == null) {
            return;
        }
        handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiSceneToolManager.22
            @Override // java.lang.Runnable
            public void run() {
                Uri.Builder builder = new Uri.Builder();
                builder.authority(VuiSceneToolManager.this.getAuthority()).path("cleanExpiredSceneTime").appendQueryParameter(VuiConstants.SCENE_ID, str);
                LogUtils.logDebug("VuiSceneToolManager", "cleanExpiredSceneTime-------------- " + str);
                try {
                    ApiRouter.route(builder.build());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void initSubSceneInfo(String str, String str2) {
        LogUtils.d("VuiSceneToolManager", "initSubSceneInfo subSceneId:" + str + ",sceneId:" + str2);
        VuiSceneInfo vuiSceneInfo = new VuiSceneInfo();
        if (this.mVuiSubSceneInfoMap.containsKey(str)) {
            vuiSceneInfo = this.mVuiSubSceneInfoMap.get(str);
        }
        vuiSceneInfo.setWholeScene(false);
        vuiSceneInfo.setWholeSceneId(str2);
        this.mVuiSubSceneInfoMap.put(str, vuiSceneInfo);
    }

    public void setmPackageName(String str) {
        this.mPackageName = str;
    }

    public void setmPackageVersion(String str) {
        this.mPackageVersion = str;
    }

    public String getmPackageName() {
        return this.mPackageName;
    }

    public String getmPackageVersion() {
        return this.mPackageVersion;
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    private String getTopRunningPackageName() {
        List<ActivityManager.RunningTaskInfo> runningTasks = ((ActivityManager) this.mContext.getSystemService("activity")).getRunningTasks(1);
        if (runningTasks == null || runningTasks.size() <= 0) {
            return null;
        }
        return runningTasks.get(0).topActivity.getPackageName();
    }

    public String checkScrollSubViewIsVisible(String str, String str2) {
        if (str == null || str2 == null || VuiUtils.cannotUpload()) {
            return "";
        }
        try {
            JSONObject jSONObject = new JSONObject(str2);
            JSONArray optJSONArray = jSONObject.optJSONArray(VuiConstants.SCENE_ELEMENTS);
            if (optJSONArray != null || optJSONArray.length() > 0) {
                JSONObject jSONObject2 = null;
                JSONArray jSONArray = new JSONArray();
                if (!VuiUtils.is3DApp(VuiUtils.getPackageNameFromSceneId(str))) {
                    for (int i = 0; i < optJSONArray.length(); i++) {
                        JSONObject optJSONObject = optJSONArray.optJSONObject(i);
                        if (optJSONObject != null) {
                            String optString = optJSONObject.optString("elementId");
                            String optString2 = optJSONObject.optString("scrollViewId");
                            JSONObject jSONObject3 = new JSONObject();
                            jSONObject3.put("elementId", optString);
                            VuiEventInfo findView = findView(str, optString);
                            VuiEventInfo findView2 = findView(str, optString2);
                            if (findView != null && findView.hitView != null) {
                                if (findView2 != null && findView2.hitView != null) {
                                    if (findView2.hitView instanceof ScrollView) {
                                        Rect rect = new Rect();
                                        findView2.hitView.getHitRect(rect);
                                        if (findView.hitView.getLocalVisibleRect(rect)) {
                                            jSONObject3.put(VuiConstants.ELEMENT_VISIBLE, true);
                                        } else {
                                            jSONObject3.put(VuiConstants.ELEMENT_VISIBLE, false);
                                        }
                                    }
                                } else {
                                    jSONObject3.put(VuiConstants.ELEMENT_VISIBLE, true);
                                }
                            }
                            jSONObject2 = jSONObject3;
                        }
                        jSONArray.put(jSONObject2);
                    }
                }
                jSONObject.put(VuiConstants.SCENE_ELEMENTS, jSONArray);
                return String.valueOf(jSONObject);
            }
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getElementState(String str, String str2) {
        VuiElement vuiElementById;
        ViewPager viewPager;
        JSONObject vuiProps;
        if (str == null || str2 == null) {
            return null;
        }
        if (VuiUtils.cannotUpload() || (vuiElementById = VuiSceneTestCacheFactory.instance().getSceneCache(VuiSceneTestCacheFactory.CacheType.BUILD.getType()).getVuiElementById(str, str2)) == null) {
            return null;
        }
        boolean is3DApp = VuiUtils.is3DApp(VuiUtils.getPackageNameFromSceneId(str));
        IVuiSceneListener vuiSceneListener = VuiSceneManager.instance().getVuiSceneListener(str);
        VuiEventInfo findView = (VuiUtils.isThirdApp(VuiUtils.getPackageNameFromSceneId(str)) || is3DApp || (vuiSceneListener != null && (vuiSceneListener instanceof IXpVuiSceneListener))) ? null : findView(str, str2);
        String str3 = "VuiSceneToolManager";
        if (VuiElementType.RECYCLEVIEW.getType().equals(vuiElementById.getType())) {
            if (findView != null && findView.hitView != null && (findView.hitView instanceof RecyclerView)) {
                RecyclerView recyclerView = (RecyclerView) findView.hitView;
                if ((recyclerView instanceof IVuiElement) && (vuiProps = ((IVuiElement) recyclerView).getVuiProps()) != null && vuiProps.has(VuiConstants.PROPS_DISABLETPL)) {
                    try {
                        if (vuiProps.getBoolean(VuiConstants.PROPS_DISABLETPL)) {
                            JSONObject jSONObject = new JSONObject();
                            jSONObject.put(VuiConstants.PROPS_SCROLLUP, true);
                            jSONObject.put(VuiConstants.PROPS_SCROLLDOWN, true);
                            return jSONObject.toString();
                        }
                    } catch (Exception unused) {
                    }
                }
                boolean canScrollVertically = recyclerView.canScrollVertically(-1);
                boolean canScrollVertically2 = recyclerView.canScrollVertically(1);
                boolean canScrollHorizontally = recyclerView.canScrollHorizontally(-1);
                boolean canScrollHorizontally2 = recyclerView.canScrollHorizontally(1);
                try {
                    JSONObject jSONObject2 = new JSONObject();
                    try {
                        if (((IVuiElement) recyclerView).getVuiAction().equals(VuiAction.SCROLLBYY.getName())) {
                            jSONObject2.put(VuiConstants.PROPS_SCROLLUP, canScrollVertically);
                            jSONObject2.put(VuiConstants.PROPS_SCROLLDOWN, canScrollVertically2);
                        } else {
                            jSONObject2.put(VuiConstants.PROPS_SCROLLLEFT, canScrollHorizontally);
                            jSONObject2.put(VuiConstants.PROPS_SCROLLRIGHT, canScrollHorizontally2);
                        }
                        str3 = "VuiSceneToolManager";
                        LogUtils.logInfo(str3, "getElementState jsonObject: " + jSONObject2.toString() + ",sceneId:" + str + ",elementId:" + str2);
                        return jSONObject2.toString();
                    } catch (JSONException e) {
                        e = e;
                        str3 = "VuiSceneToolManager";
                        LogUtils.e(str3, "getElementState e:" + e.getMessage());
                        return null;
                    }
                } catch (JSONException e2) {
                    e = e2;
                }
            } else if (vuiElementById.getProps() != null) {
                return vuiElementById.getProps().toString();
            } else {
                try {
                    JSONObject jSONObject3 = new JSONObject();
                    jSONObject3.put(VuiConstants.PROPS_SCROLLUP, true);
                    jSONObject3.put(VuiConstants.PROPS_SCROLLDOWN, true);
                    LogUtils.logInfo("VuiSceneToolManager", "getElementState jsonObject: " + jSONObject3.toString() + ",sceneId:" + str + ",elementId:" + str2);
                    return jSONObject3.toString();
                } catch (JSONException e3) {
                    LogUtils.e("VuiSceneToolManager", "getElementState e:" + e3.getMessage());
                    return null;
                }
            }
        } else if (VuiElementType.VIEWPAGER.getType().equals(vuiElementById.getType())) {
            if (VuiUtils.getExtraPage(vuiElementById) != -1) {
                try {
                    JSONObject jSONObject4 = new JSONObject();
                    jSONObject4.put(VuiConstants.PROPS_SCROLLLEFT, true);
                    jSONObject4.put(VuiConstants.PROPS_SCROLLRIGHT, true);
                    LogUtils.logInfo("VuiSceneToolManager", "getElementState jsonObject: " + jSONObject4.toString() + ",sceneId:" + str + ",elementId:" + str2);
                    return jSONObject4.toString();
                } catch (JSONException e4) {
                    LogUtils.e("VuiSceneToolManager", "getElementState e:" + e4.getMessage());
                    return null;
                }
            } else if (findView == null || findView.hitView == null) {
                return null;
            } else {
                if (!(findView.hitView instanceof ViewPager)) {
                    viewPager = VuiUtils.findViewPager(findView.hitView);
                } else {
                    viewPager = (ViewPager) findView.hitView;
                }
                if (viewPager != null) {
                    try {
                        boolean canScrollHorizontally3 = viewPager.canScrollHorizontally(-1);
                        boolean canScrollHorizontally4 = viewPager.canScrollHorizontally(1);
                        JSONObject jSONObject5 = new JSONObject();
                        jSONObject5.put(VuiConstants.PROPS_SCROLLLEFT, canScrollHorizontally3);
                        jSONObject5.put(VuiConstants.PROPS_SCROLLRIGHT, canScrollHorizontally4);
                        LogUtils.logInfo("VuiSceneToolManager", "getElementState jsonObject: " + jSONObject5.toString() + ",sceneId:" + str + ",elementId:" + str2);
                        return jSONObject5.toString();
                    } catch (JSONException e5) {
                        LogUtils.e("VuiSceneToolManager", "getElementState e:" + e5.getMessage());
                        return null;
                    }
                }
                return null;
            }
        } else {
            boolean z = false;
            if (VuiElementType.SCROLLVIEW.getType().equals(vuiElementById.getType())) {
                if (vuiElementById == null || vuiElementById.getActions() == null) {
                    return null;
                }
                ArrayList arrayList = new ArrayList(vuiElementById.actions.entrySet());
                if (arrayList.isEmpty()) {
                    return null;
                }
                if (VuiAction.SCROLLBYY.getName().equals(((Map.Entry) arrayList.get(0)).getKey())) {
                    if (findView == null || findView.hitView == null) {
                        return null;
                    }
                    if (findView.hitView instanceof ScrollView) {
                        View childAt = ((ViewGroup) findView.hitView).getChildAt(0);
                        if (childAt == null) {
                            return null;
                        }
                        try {
                            JSONObject jSONObject6 = new JSONObject();
                            int scrollY = findView.hitView.getScrollY();
                            jSONObject6.put(VuiConstants.PROPS_SCROLLUP, scrollY != 0);
                            if (scrollY + findView.hitView.getHeight() != childAt.getMeasuredHeight()) {
                                z = true;
                            }
                            jSONObject6.put(VuiConstants.PROPS_SCROLLDOWN, z);
                            LogUtils.logInfo("VuiSceneToolManager", "getElementState jsonObject: " + jSONObject6.toString() + ",sceneId:" + str + ",elementId:" + str2);
                            return jSONObject6.toString();
                        } catch (Exception e6) {
                            e6.printStackTrace();
                            LogUtils.e("VuiSceneToolManager", "getElementState e:" + e6.getMessage());
                            return null;
                        }
                    }
                    Rect rect = new Rect();
                    findView.hitView.getGlobalVisibleRect(rect);
                    try {
                        JSONObject jSONObject7 = new JSONObject();
                        int scrollY2 = findView.hitView.getScrollY();
                        jSONObject7.put(VuiConstants.PROPS_SCROLLUP, scrollY2 != 0);
                        if (scrollY2 + rect.height() < findView.hitView.getMeasuredHeight()) {
                            z = true;
                        }
                        jSONObject7.put(VuiConstants.PROPS_SCROLLDOWN, z);
                        LogUtils.logInfo("VuiSceneToolManager", "getElementState jsonObject: " + jSONObject7.toString() + ",sceneId:" + str + ",elementId:" + str2);
                        return jSONObject7.toString();
                    } catch (Exception e7) {
                        e7.printStackTrace();
                        LogUtils.e("VuiSceneToolManager", "getElementState e:" + e7.getMessage());
                        return null;
                    }
                } else if (!VuiAction.SCROLLBYX.getName().equals(((Map.Entry) arrayList.get(0)).getKey()) || findView == null || findView.hitView == null || (findView.hitView instanceof ScrollView)) {
                    return null;
                } else {
                    View view = (View) findView.hitView.getParent();
                    if (view.getWidth() < findView.hitView.getWidth()) {
                        try {
                            JSONObject jSONObject8 = new JSONObject();
                            int scrollX = view.getScrollX();
                            LogUtils.e("VuiSceneToolManager", "view width:" + findView.hitView.getWidth() + ",parent:" + view.getWidth() + ",scrollX:" + scrollX);
                            jSONObject8.put(VuiConstants.PROPS_SCROLLLEFT, scrollX != 0);
                            if (scrollX + view.getWidth() < findView.hitView.getWidth()) {
                                z = true;
                            }
                            jSONObject8.put(VuiConstants.PROPS_SCROLLRIGHT, z);
                            LogUtils.logInfo("VuiSceneToolManager", "getElementState jsonObject: " + jSONObject8.toString() + ",sceneId:" + str + ",elementId:" + str2);
                            return jSONObject8.toString();
                        } catch (Exception e8) {
                            e8.printStackTrace();
                            LogUtils.e("VuiSceneToolManager", "getElementState e:" + e8.getMessage());
                            return null;
                        }
                    }
                    return null;
                }
            }
            if (findView != null && findView.hitView != null) {
                vuiElementById.setEnabled(findView.hitView.isEnabled() ? null : false);
            }
            String json = new Gson().toJson(vuiElementById);
            LogUtils.logInfo("VuiSceneToolManager", "getElementState:  result:  " + json);
            return json;
        }
    }

    public VuiEventInfo findView(String str, String str2) {
        View hitView;
        if (str != null && str2 != null) {
            try {
                VuiEventInfo findViewFromSceneInfo = findViewFromSceneInfo(str, str2);
                if (findViewFromSceneInfo != null) {
                    LogUtils.logDebug("VuiSceneToolManager", "findViewFromSceneInfo elementId:" + str2 + ",view:" + findViewFromSceneInfo.hitView + ",sceneId:" + findViewFromSceneInfo.sceneId);
                    return findViewFromSceneInfo;
                }
                VuiEventInfo findRootView = findRootView(str, str2);
                LogUtils.logDebug("VuiSceneToolManager", "findView elementId:" + str2 + ",rootView:" + (findRootView != null ? findRootView.hitView : null) + ",sceneId:" + (findRootView != null ? findRootView.sceneId : ""));
                if (findRootView != null && findRootView.hitView != null) {
                    return new VuiEventInfo(getHitView(findRootView.hitView, str2), findRootView.sceneId);
                }
                VuiSceneInfo sceneInfo = VuiSceneManager.instance().getSceneInfo(str);
                LogUtils.logInfo("VuiSceneToolManager", "findView view by rootview");
                View rootView = sceneInfo == null ? null : sceneInfo.getRootView();
                if (rootView != null) {
                    View hitView2 = getHitView(rootView, str2);
                    if (hitView2 == null) {
                        List<String> subSceneList = sceneInfo.getSubSceneList();
                        int size = subSceneList == null ? 0 : subSceneList.size();
                        for (int i = 0; i < size; i++) {
                            String str3 = subSceneList.get(i);
                            VuiSceneInfo sceneInfo2 = TextUtils.isEmpty(str3) ? null : VuiSceneManager.instance().getSceneInfo(str3);
                            View rootView2 = sceneInfo2 == null ? null : sceneInfo2.getRootView();
                            if (rootView2 != null && (hitView = getHitView(rootView2, str2)) != null) {
                                return new VuiEventInfo(hitView, str3);
                            }
                        }
                        return findViewFromSceneInfo;
                    }
                    return new VuiEventInfo(hitView2, str);
                }
                return findViewFromSceneInfo;
            } catch (Exception e) {
                LogUtils.e("VuiSceneToolManager", "findView e:" + e.getMessage());
            }
        }
        return null;
    }

    private View getHitView(View view, String str) {
        if (view != null) {
            View findViewWithTag = view.findViewWithTag(str);
            if (findViewWithTag == null) {
                View findViewWithId = findViewWithId(str, view);
                if (findViewWithId != null) {
                    LogUtils.logDebug("VuiSceneToolManager", "findViewWithId:   Tag====  " + findViewWithId.getTag());
                    return findViewWithId;
                }
                LogUtils.e("VuiSceneToolManager", "findViewWithId  View is null");
                return findViewWithId;
            }
            return findViewWithTag;
        }
        return null;
    }

    private VuiEventInfo findViewFromSceneInfo(String str, String str2) {
        VuiSceneInfo sceneInfo;
        List<SoftReference<View>> notChildrenViewList;
        if (str != null && str2 != null && (sceneInfo = VuiSceneManager.instance().getSceneInfo(str)) != null && sceneInfo.isContainNotChildrenView() && (notChildrenViewList = sceneInfo.getNotChildrenViewList()) != null) {
            for (int i = 0; i < notChildrenViewList.size(); i++) {
                SoftReference<View> softReference = notChildrenViewList.get(i);
                if (softReference != null && softReference.get() != null) {
                    View findViewWithTag = softReference.get().findViewWithTag(str2);
                    if (findViewWithTag != null) {
                        return new VuiEventInfo(findViewWithTag, str);
                    }
                    View findViewWithId = findViewWithId(str2, softReference.get());
                    if (findViewWithId != null) {
                        LogUtils.logDebug("VuiSceneToolManager", "findViewWithId:   Tag====  " + findViewWithId.getTag());
                        return new VuiEventInfo(findViewWithId, str);
                    }
                }
            }
        }
        return null;
    }

    private VuiEventInfo findRootView(String str, String str2) {
        VuiSceneInfo sceneInfo;
        VuiEventInfo vuiEventInfo = null;
        if (str == null || str2 == null || (sceneInfo = VuiSceneManager.instance().getSceneInfo(str)) == null) {
            return null;
        }
        LogUtils.logDebug("VuiSceneToolManager", "findRootView idList:" + sceneInfo.getIdList());
        if (sceneInfo.getIdList() != null && sceneInfo.getIdList().contains(str2)) {
            return new VuiEventInfo(VuiSceneManager.instance().getRootView(str), str);
        }
        List<String> subSceneList = sceneInfo.getSubSceneList();
        if (subSceneList != null) {
            LogUtils.logDebug("VuiSceneToolManager", "findRootView subSceneList:" + subSceneList);
        }
        if (subSceneList != null) {
            int size = subSceneList.size();
            for (int i = 0; i < size; i++) {
                vuiEventInfo = findRootView(subSceneList.get(i), str2);
                if (vuiEventInfo != null) {
                    return vuiEventInfo;
                }
            }
            return vuiEventInfo;
        }
        return null;
    }

    public View findViewWithId(String str, View view) {
        String substring;
        LogUtils.logInfo("VuiSceneToolManager", "findViewWithId  ===  " + str);
        if (view == null || str == null) {
            return view;
        }
        if (str.indexOf("_") != -1) {
            String substring2 = str.substring(0, str.indexOf("_"));
            if (TextUtils.isEmpty(substring2)) {
                return null;
            }
            if (substring2.length() > 4) {
                String[] split = str.split("_");
                if (split.length <= 2) {
                    substring = split[1];
                } else {
                    substring = str.substring(str.indexOf("_", 1) + 1, str.indexOf("_", str.indexOf("_") + 1));
                }
                if (substring.length() < 4) {
                    int id = ResourceUtil.getId(this.mContext, substring2);
                    LogUtils.logInfo("VuiSceneToolManager", "findViewWithId view tag: " + view.findViewById(id).getTag());
                    View listView = getListView(view.findViewById(id));
                    if (listView == null) {
                        return null;
                    }
                    if (listView instanceof RecyclerView) {
                        view = ((RecyclerView) listView).getLayoutManager().findViewByPosition(Integer.valueOf(substring).intValue()).findViewById(id);
                    }
                    return split.length <= 2 ? view : findViewWithId(str.substring(str.indexOf("_", str.indexOf("_") + 1) + 1), view);
                }
                return findViewWithId(str.substring(str.indexOf("_") + 1), view.findViewById(ResourceUtil.getId(this.mContext, substring2)));
            }
            return findViewWithId(str.substring(str.indexOf("_") + 1), view);
        }
        return view.findViewById(ResourceUtil.getId(this.mContext, str));
    }

    private View getListView(View view) {
        if (view == null || (view instanceof ListView) || (view instanceof RecyclerView)) {
            return view;
        }
        if (view.getParent() == null) {
            return null;
        }
        return view.getParent() instanceof ViewRootImpl ? view : getListView((View) view.getParent());
    }

    private View getScrollView(View view) {
        if (view == null || (view instanceof ListView) || (view instanceof ScrollView)) {
            return view;
        }
        if (view.getParent() == null) {
            return null;
        }
        return view.getParent() instanceof ViewRootImpl ? view : getScrollView((View) view.getParent());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isUploadScene(String str) {
        LogUtils.logDebug("VuiSceneToolManager", "isUploadScene sceneId:" + str + ",getTopRunningPackageName:" + getTopRunningPackageName() + ",mIsInSpeech:" + this.mIsInSpeech + ",VuiEngine.mLFActiveSceneId:" + VuiEngineImpl.mActiveSceneIds.get(VuiConstants.SCREEN_DISPLAY_LF) + " VuiEngine.mRFActiveSceneId: " + VuiEngineImpl.mActiveSceneIds.get(VuiConstants.SCREEN_DISPLAY_RF) + ",mPackageName" + this.mPackageName);
        String str2 = this.mPackageName;
        if (str2 == null || str == null) {
            return false;
        }
        if (VuiConstants.SYSTEMUI.equals(str2)) {
            return true;
        }
        if (this.mIsInSpeech && (VuiEngineImpl.mLeftPopPanelStack.contains(str) || VuiEngineImpl.mRightPopPanelStack.contains(str))) {
            return true;
        }
        if (this.mIsInSpeech && VuiEngineImpl.mActiveSceneIds.containsValue(str) && (str.endsWith("Dialog") || str.endsWith("dialog"))) {
            return true;
        }
        if (this.mIsInSpeech && VuiEngineImpl.mActiveSceneIds.containsValue(str) && VuiConstants.VUI_SCENE_APP.equals(this.mPackageName)) {
            return true;
        }
        if ((this.mIsInSpeech && VuiEngineImpl.mActiveSceneIds.containsValue(str) && (VuiConstants.SETTINS.equals(this.mPackageName) || VuiConstants.CARCONTROL.equals(this.mPackageName) || VuiConstants.CHARGE.equals(this.mPackageName) || VuiConstants.UNITY.equals(this.mPackageName))) || "com.xiaopeng.caraccount".equals(this.mPackageName)) {
            return true;
        }
        return this.mIsInSpeech && this.mPackageName.equals(getTopRunningPackageName()) && VuiEngineImpl.mActiveSceneIds.containsValue(str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendBuildCacheInOther(String str, VuiScene vuiScene, VuiSceneCache vuiSceneCache) {
        VuiSceneInfo sceneInfo = VuiSceneManager.instance().getSceneInfo(str);
        if (sceneInfo == null || !sceneInfo.isBuildComplete()) {
            return;
        }
        buildScene(vuiScene, false, false);
    }
}

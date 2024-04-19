package com.xiaopeng.speech.vui;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.android.internal.policy.DecorView;
import com.xiaopeng.speech.apirouter.Utils;
import com.xiaopeng.speech.vui.cache.VuiDisplayLocationInfoCache;
import com.xiaopeng.speech.vui.cache.VuiSceneBuildCache;
import com.xiaopeng.speech.vui.cache.VuiSceneBuildTestCache;
import com.xiaopeng.speech.vui.cache.VuiSceneCache;
import com.xiaopeng.speech.vui.cache.VuiSceneCacheFactory;
import com.xiaopeng.speech.vui.cache.VuiSceneTestCacheFactory;
import com.xiaopeng.speech.vui.constants.Foo;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import com.xiaopeng.speech.vui.listener.IUnityVuiSceneListener;
import com.xiaopeng.speech.vui.listener.IUnityVuiStateListener;
import com.xiaopeng.speech.vui.listener.IVuiEventListener;
import com.xiaopeng.speech.vui.model.VuiFeedback;
import com.xiaopeng.speech.vui.model.VuiScene;
import com.xiaopeng.speech.vui.model.VuiSceneInfo;
import com.xiaopeng.speech.vui.model.VuiSceneState;
import com.xiaopeng.speech.vui.task.TaskDispatcher;
import com.xiaopeng.speech.vui.task.TaskWrapper;
import com.xiaopeng.speech.vui.utils.LogUtils;
import com.xiaopeng.speech.vui.utils.SceneMergeUtils;
import com.xiaopeng.speech.vui.utils.VuiUtils;
import com.xiaopeng.speech.vui.vuiengine.BuildConfig;
import com.xiaopeng.speech.vui.vuiengine.R;
import com.xiaopeng.view.SharedDisplayListener;
import com.xiaopeng.view.WindowManagerFactory;
import com.xiaopeng.vui.commons.IVuiElement;
import com.xiaopeng.vui.commons.IVuiElementChangedListener;
import com.xiaopeng.vui.commons.IVuiElementListener;
import com.xiaopeng.vui.commons.IVuiSceneListener;
import com.xiaopeng.vui.commons.VuiPriority;
import com.xiaopeng.vui.commons.model.VuiElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes2.dex */
public class VuiEngineImpl {
    private static final String TAG = "VuiEngine";
    private static final String VUI_SCENE_TAG = "PopPanel";
    private EventDispatcher eventDispatcher;
    public boolean isAccessibilityScene;
    private List<String> mBackNaviSceneStack;
    private Context mContext;
    private Handler mDispatherHandler;
    private HandlerThread mDispatherThread;
    private List<String> mEnterSceneIds;
    private Handler mHandler;
    private List<String> mLFEnterSceneStack;
    private Handler mMainHandler;
    private String mPackageName;
    private String mPackageVersion;
    private List<String> mRFEnterSceneStack;
    private Resources mResources;
    private HandlerThread mThread;
    private UpdateElementAttrRun mUpdateElementAttrRun;
    private UpdateSceneRun mUpdateSceneRun;
    private WindowManagerFactory mWindowFactory;
    private Handler mainHandler;
    private List<String> mainThreadSceneList;
    private Map<String, String> querySceneIdMap;
    private TaskDispatcher taskStructure;
    private List<IUnityVuiStateListener> vuiStateListeners;
    public static final ConcurrentMap<String, String> mActiveSceneIds = new ConcurrentHashMap();
    public static String mSceneIdPrefix = null;
    public static ConcurrentSkipListSet<String> mLeftPopPanelStack = new ConcurrentSkipListSet<>();
    public static ConcurrentSkipListSet<String> mRightPopPanelStack = new ConcurrentSkipListSet<>();

    public boolean isAccessibilityScene() {
        return this.isAccessibilityScene;
    }

    public VuiEngineImpl(Context context, boolean z) {
        this.mainThreadSceneList = Arrays.asList("MainMusicConcentration");
        this.mMainHandler = new Handler(Looper.getMainLooper());
        this.mEnterSceneIds = Collections.synchronizedList(new ArrayList());
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.mLFEnterSceneStack = new ArrayList();
        this.mRFEnterSceneStack = new ArrayList();
        this.mBackNaviSceneStack = new ArrayList();
        this.isAccessibilityScene = false;
        this.mUpdateElementAttrRun = null;
        this.mUpdateSceneRun = null;
        this.vuiStateListeners = new ArrayList();
        this.querySceneIdMap = new HashMap();
        LogUtils.logInfo(TAG, BuildConfig.BUILD_VERSION);
        if (VuiUtils.canUseVuiFeature()) {
            this.mContext = context;
            Foo.setContext(context);
            lazyInitThread();
            this.mResources = this.mContext.getResources();
            this.mPackageName = context.getApplicationInfo().packageName;
            VuiSceneManager.instance().setmPackageName(this.mPackageName);
            VuiSceneManager.instance().setContext(this.mContext);
            VuiSceneManager.instance().setEngine(this);
            VuiSceneToolManager.instance().setmPackageName(this.mPackageName);
            VuiSceneToolManager.instance().setContext(this.mContext);
            VuiSceneToolManager.instance().setEngine(this);
            this.eventDispatcher = new EventDispatcher(this.mContext, z);
            this.taskStructure = new TaskDispatcher();
            try {
                this.mPackageVersion = context.getPackageManager().getPackageInfo(this.mPackageName, 0).versionName;
                VuiSceneManager.instance().setmPackageVersion(this.mPackageVersion);
                VuiSceneToolManager.instance().setmPackageVersion(this.mPackageVersion);
                mSceneIdPrefix = this.mPackageName + "-" + this.mPackageVersion;
                if (VuiConstants.UNITY.equals(this.mPackageName) || !Utils.isXpDevice()) {
                    return;
                }
                WindowManagerFactory.create(Foo.getContext()).registerSharedListener(new SharedDisplayListener() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.1
                    public void onChanged(String str, int i) throws RemoteException {
                        super.onChanged(str, i);
                        LogUtils.i(VuiEngineImpl.TAG, "onChanged packageName=" + str + ",mPackageName:" + VuiEngineImpl.this.mPackageName + ",sharedId:" + i + ",mRFEnterSceneStack=" + VuiEngineImpl.this.mRFEnterSceneStack + "，mLFEnterSceneStack" + VuiEngineImpl.this.mLFEnterSceneStack);
                        if (str.equals(VuiEngineImpl.this.mPackageName)) {
                            int i2 = 0;
                            if (i == 0) {
                                while (i2 < VuiEngineImpl.this.mRFEnterSceneStack.size()) {
                                    String str2 = (String) VuiEngineImpl.this.mRFEnterSceneStack.get(i2);
                                    if (!TextUtils.isEmpty(str2)) {
                                        VuiEngineImpl.this.updateDisplayLocation(str2.replace(VuiEngineImpl.mSceneIdPrefix + "-", ""), VuiConstants.SCREEN_DISPLAY_LF);
                                    }
                                    i2++;
                                }
                            } else if (i == 1) {
                                while (i2 < VuiEngineImpl.this.mLFEnterSceneStack.size()) {
                                    String str3 = (String) VuiEngineImpl.this.mLFEnterSceneStack.get(i2);
                                    if (!TextUtils.isEmpty(str3)) {
                                        VuiEngineImpl.this.updateDisplayLocation(str3.replace(VuiEngineImpl.mSceneIdPrefix + "-", ""), VuiConstants.SCREEN_DISPLAY_RF);
                                    }
                                    i2++;
                                }
                            }
                        }
                    }
                });
            } catch (Throwable th) {
                th.printStackTrace();
                LogUtils.e(TAG, "VuiEngine init e:" + th.getMessage());
            }
        }
    }

    public VuiEngineImpl(Context context, boolean z, boolean z2) {
        this.mainThreadSceneList = Arrays.asList("MainMusicConcentration");
        this.mMainHandler = new Handler(Looper.getMainLooper());
        this.mEnterSceneIds = Collections.synchronizedList(new ArrayList());
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.mLFEnterSceneStack = new ArrayList();
        this.mRFEnterSceneStack = new ArrayList();
        this.mBackNaviSceneStack = new ArrayList();
        this.isAccessibilityScene = false;
        this.mUpdateElementAttrRun = null;
        this.mUpdateSceneRun = null;
        this.vuiStateListeners = new ArrayList();
        this.querySceneIdMap = new HashMap();
        LogUtils.logInfo(TAG, BuildConfig.BUILD_VERSION);
        if (VuiUtils.canUseVuiFeature()) {
            this.mContext = context;
            Foo.setContext(context);
            lazyInitThread();
            this.mResources = this.mContext.getResources();
            this.mPackageName = context.getApplicationInfo().packageName;
            VuiSceneManager.instance().setmPackageName(this.mPackageName);
            VuiSceneManager.instance().setContext(this.mContext);
            VuiSceneManager.instance().setEngine(this);
            VuiSceneToolManager.instance().setmPackageName(this.mPackageName);
            VuiSceneToolManager.instance().setContext(this.mContext);
            VuiSceneToolManager.instance().setEngine(this);
            this.eventDispatcher = new EventDispatcher(this.mContext, z);
            this.taskStructure = new TaskDispatcher();
            try {
                this.mPackageVersion = context.getPackageManager().getPackageInfo(this.mPackageName, 0).versionName;
                VuiSceneToolManager.instance().setmPackageVersion(this.mPackageVersion);
                mSceneIdPrefix = this.mPackageName + "-" + this.mPackageVersion;
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.e(TAG, "VuiEngine init e:" + e.getMessage());
            }
            this.isAccessibilityScene = z2;
        }
    }

    private void lazyInitThread() {
        if (this.mThread == null) {
            HandlerThread handlerThread = new HandlerThread("VuiEngine-Thread");
            this.mThread = handlerThread;
            handlerThread.start();
            this.mHandler = new Handler(this.mThread.getLooper());
        }
        if (this.mDispatherThread == null) {
            HandlerThread handlerThread2 = new HandlerThread("VuiEngine-Disptcher-Thread");
            this.mDispatherThread = handlerThread2;
            handlerThread2.start();
            this.mDispatherHandler = new Handler(this.mDispatherThread.getLooper());
        }
    }

    public void enterScene(String str, boolean z) {
        enterScene(str, VuiUtils.getSourceDisplayLocation(), z);
    }

    public void enterScene(final String str, final String str2, final boolean z) {
        Handler handler;
        if (VuiUtils.canUseVuiFeature()) {
            if (TextUtils.isEmpty(str2)) {
                str2 = VuiConstants.SCREEN_DISPLAY_LF;
            }
            if (str == null || (handler = this.mHandler) == null) {
                return;
            }
            handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.2
                /* JADX WARN: Removed duplicated region for block: B:113:0x032b A[Catch: Exception -> 0x056b, TryCatch #0 {Exception -> 0x056b, blocks: (B:3:0x000a, B:5:0x0022, B:7:0x003b, B:10:0x0055, B:12:0x005d, B:14:0x0083, B:17:0x0091, B:19:0x009d, B:22:0x00b0, B:24:0x00b5, B:26:0x00d1, B:28:0x00dd, B:29:0x00e6, B:31:0x00ee, B:34:0x00f7, B:36:0x0103, B:51:0x015b, B:38:0x010e, B:40:0x011a, B:42:0x012c, B:44:0x0132, B:46:0x0138, B:47:0x0143, B:49:0x0148, B:50:0x0152, B:53:0x0178, B:55:0x0186, B:58:0x018f, B:60:0x01c7, B:63:0x01d3, B:65:0x01db, B:67:0x01e3, B:68:0x020e, B:70:0x0216, B:71:0x021b, B:73:0x0225, B:75:0x022d, B:77:0x0235, B:78:0x0260, B:80:0x0268, B:81:0x026d, B:83:0x0277, B:85:0x027f, B:87:0x028b, B:93:0x02b2, B:95:0x02da, B:97:0x02e2, B:100:0x02eb, B:102:0x02f3, B:105:0x0302, B:107:0x030a, B:113:0x032b, B:115:0x033a, B:119:0x0355, B:116:0x0344, B:118:0x034c, B:120:0x035e, B:123:0x03a0, B:125:0x03ac, B:126:0x040b, B:128:0x0417, B:130:0x0423, B:131:0x0441, B:132:0x0448, B:134:0x0450, B:136:0x045c, B:138:0x04bf, B:140:0x04cb, B:142:0x04d7, B:144:0x04e3, B:145:0x04fb, B:147:0x0528, B:149:0x0534, B:151:0x0540, B:152:0x055d, B:108:0x0315, B:110:0x031d, B:88:0x0295, B:90:0x029d, B:92:0x02a9, B:153:0x0563), top: B:158:0x000a }] */
                /* JADX WARN: Removed duplicated region for block: B:123:0x03a0 A[Catch: Exception -> 0x056b, TRY_ENTER, TryCatch #0 {Exception -> 0x056b, blocks: (B:3:0x000a, B:5:0x0022, B:7:0x003b, B:10:0x0055, B:12:0x005d, B:14:0x0083, B:17:0x0091, B:19:0x009d, B:22:0x00b0, B:24:0x00b5, B:26:0x00d1, B:28:0x00dd, B:29:0x00e6, B:31:0x00ee, B:34:0x00f7, B:36:0x0103, B:51:0x015b, B:38:0x010e, B:40:0x011a, B:42:0x012c, B:44:0x0132, B:46:0x0138, B:47:0x0143, B:49:0x0148, B:50:0x0152, B:53:0x0178, B:55:0x0186, B:58:0x018f, B:60:0x01c7, B:63:0x01d3, B:65:0x01db, B:67:0x01e3, B:68:0x020e, B:70:0x0216, B:71:0x021b, B:73:0x0225, B:75:0x022d, B:77:0x0235, B:78:0x0260, B:80:0x0268, B:81:0x026d, B:83:0x0277, B:85:0x027f, B:87:0x028b, B:93:0x02b2, B:95:0x02da, B:97:0x02e2, B:100:0x02eb, B:102:0x02f3, B:105:0x0302, B:107:0x030a, B:113:0x032b, B:115:0x033a, B:119:0x0355, B:116:0x0344, B:118:0x034c, B:120:0x035e, B:123:0x03a0, B:125:0x03ac, B:126:0x040b, B:128:0x0417, B:130:0x0423, B:131:0x0441, B:132:0x0448, B:134:0x0450, B:136:0x045c, B:138:0x04bf, B:140:0x04cb, B:142:0x04d7, B:144:0x04e3, B:145:0x04fb, B:147:0x0528, B:149:0x0534, B:151:0x0540, B:152:0x055d, B:108:0x0315, B:110:0x031d, B:88:0x0295, B:90:0x029d, B:92:0x02a9, B:153:0x0563), top: B:158:0x000a }] */
                /* JADX WARN: Removed duplicated region for block: B:132:0x0448 A[Catch: Exception -> 0x056b, TryCatch #0 {Exception -> 0x056b, blocks: (B:3:0x000a, B:5:0x0022, B:7:0x003b, B:10:0x0055, B:12:0x005d, B:14:0x0083, B:17:0x0091, B:19:0x009d, B:22:0x00b0, B:24:0x00b5, B:26:0x00d1, B:28:0x00dd, B:29:0x00e6, B:31:0x00ee, B:34:0x00f7, B:36:0x0103, B:51:0x015b, B:38:0x010e, B:40:0x011a, B:42:0x012c, B:44:0x0132, B:46:0x0138, B:47:0x0143, B:49:0x0148, B:50:0x0152, B:53:0x0178, B:55:0x0186, B:58:0x018f, B:60:0x01c7, B:63:0x01d3, B:65:0x01db, B:67:0x01e3, B:68:0x020e, B:70:0x0216, B:71:0x021b, B:73:0x0225, B:75:0x022d, B:77:0x0235, B:78:0x0260, B:80:0x0268, B:81:0x026d, B:83:0x0277, B:85:0x027f, B:87:0x028b, B:93:0x02b2, B:95:0x02da, B:97:0x02e2, B:100:0x02eb, B:102:0x02f3, B:105:0x0302, B:107:0x030a, B:113:0x032b, B:115:0x033a, B:119:0x0355, B:116:0x0344, B:118:0x034c, B:120:0x035e, B:123:0x03a0, B:125:0x03ac, B:126:0x040b, B:128:0x0417, B:130:0x0423, B:131:0x0441, B:132:0x0448, B:134:0x0450, B:136:0x045c, B:138:0x04bf, B:140:0x04cb, B:142:0x04d7, B:144:0x04e3, B:145:0x04fb, B:147:0x0528, B:149:0x0534, B:151:0x0540, B:152:0x055d, B:108:0x0315, B:110:0x031d, B:88:0x0295, B:90:0x029d, B:92:0x02a9, B:153:0x0563), top: B:158:0x000a }] */
                @Override // java.lang.Runnable
                /*
                    Code decompiled incorrectly, please refer to instructions dump.
                    To view partially-correct add '--show-bad-code' argument
                */
                public void run() {
                    /*
                        Method dump skipped, instructions count: 1396
                        To view this dump add '--comments-level debug' option
                    */
                    throw new UnsupportedOperationException("Method not decompiled: com.xiaopeng.speech.vui.VuiEngineImpl.AnonymousClass2.run():void");
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isChangeActiveScene(String str, Context context, List<String> list) {
        for (int i = 0; i < list.size(); i++) {
            String str2 = list.get(i);
            IVuiSceneListener vuiSceneListener = VuiSceneManager.instance().getVuiSceneListener(str2);
            if (!TextUtils.isEmpty(str2) && (str2.endsWith("Dialog") || str2.endsWith("dialog"))) {
                list.add(i, str);
                return false;
            }
            if (vuiSceneListener instanceof Dialog) {
                Context context2 = getContext(str2);
                LogUtils.logDebug(TAG, "enterScene stackContext:" + context2);
                if (context2 != null && (context2.equals(context) || ((context instanceof Activity) && ((context2 instanceof Service) || (context2 instanceof Application))))) {
                    LogUtils.logInfo(TAG, "enterScene has same context:" + i);
                    list.add(i, str);
                    return false;
                }
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x00d8, code lost:
        if (r5.equals(r12) != false) goto L22;
     */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x00db, code lost:
        if (r11 == false) goto L22;
     */
    /* JADX WARN: Code restructure failed: missing block: B:29:0x00de, code lost:
        if (r11 == false) goto L22;
     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x00e1, code lost:
        r3 = true;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void handlerEnterScene(java.lang.String r10, boolean r11, java.lang.String r12) {
        /*
            Method dump skipped, instructions count: 302
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaopeng.speech.vui.VuiEngineImpl.handlerEnterScene(java.lang.String, boolean, java.lang.String):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Context getContext(String str) {
        View rootView = VuiSceneManager.instance().getRootView(str);
        if (rootView != null) {
            Context context = rootView != null ? rootView.getContext() : null;
            if (rootView instanceof DecorView) {
                return context;
            }
            if (context != null && (context instanceof ContextWrapper)) {
                context = getDialogOwnContext(context);
            }
            if (context != null && (context instanceof Application) && !VuiConstants.MUSIC.equals(this.mPackageName)) {
                View findViewById = rootView != null ? rootView.findViewById(16908290) : null;
                if (findViewById != null && (findViewById instanceof ViewGroup)) {
                    return getDialogOwnContext(((ViewGroup) findViewById).getChildAt(0).getContext());
                }
            }
            return context;
        }
        return null;
    }

    private Context getDialogOwnContext(Context context) {
        if ((context instanceof Activity) || (context instanceof Service) || (context instanceof Application)) {
            return context;
        }
        if (context instanceof ContextWrapper) {
            context = ((ContextWrapper) context).getBaseContext();
        }
        return getDialogOwnContext(context);
    }

    public String getSceneUnqiueId(String str) {
        return mSceneIdPrefix + "-" + str;
    }

    public void exitDupScene(String str, boolean z, IVuiSceneListener iVuiSceneListener) {
        if (iVuiSceneListener == null || TextUtils.isEmpty(str)) {
            return;
        }
        exitScene(iVuiSceneListener.toString() + "-" + str, z);
    }

    public void enterDupScene(String str, boolean z, IVuiSceneListener iVuiSceneListener) {
        if (iVuiSceneListener == null || TextUtils.isEmpty(str)) {
            return;
        }
        enterScene(iVuiSceneListener.toString() + "-" + str, z);
    }

    public void exitScene(String str, boolean z) {
        exitScene(str, VuiUtils.getSourceDisplayLocation(), z, null);
    }

    public void exitScene(final String str, String str2, final boolean z, final IVuiSceneListener iVuiSceneListener) {
        if (!VuiUtils.canUseVuiFeature() || str == null || this.mHandler == null) {
            return;
        }
        if (TextUtils.isEmpty(str2)) {
            str2 = VuiConstants.SCREEN_DISPLAY_LF;
        }
        final String str3 = str2;
        this.mHandler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.4
            @Override // java.lang.Runnable
            public void run() {
                try {
                    String sceneUnqiueId = VuiEngineImpl.this.getSceneUnqiueId(str);
                    VuiSceneInfo sceneInfo = VuiSceneManager.instance().getSceneInfo(sceneUnqiueId);
                    if (iVuiSceneListener != null && sceneInfo != null && sceneInfo.getListener() != null && !iVuiSceneListener.equals(sceneInfo.getListener())) {
                        LogUtils.w(VuiEngineImpl.TAG, "要退出的场景和目前持有的场景数据不一致");
                    } else if (str.endsWith(VuiEngineImpl.VUI_SCENE_TAG)) {
                        if (VuiConstants.SCREEN_DISPLAY_LF.equals(str3)) {
                            if (VuiEngineImpl.mLeftPopPanelStack.contains(sceneUnqiueId)) {
                                VuiEngineImpl.mLeftPopPanelStack.remove(sceneUnqiueId);
                                LogUtils.i(VuiEngineImpl.TAG, "exitScene:" + sceneUnqiueId + ",mLeftPopPanelStack:" + VuiEngineImpl.mLeftPopPanelStack + " mRightPopPanelStack:" + VuiEngineImpl.mRightPopPanelStack + "," + VuiEngineImpl.mActiveSceneIds);
                                VuiSceneManager.instance().exitScene(sceneUnqiueId, VuiEngineImpl.this.mPackageName, z, str3);
                                VuiSceneToolManager.instance().exitScene(sceneUnqiueId, VuiEngineImpl.this.mPackageName, z, str3);
                            }
                        } else if (VuiConstants.SCREEN_DISPLAY_RF.equals(str3) && VuiEngineImpl.mRightPopPanelStack.contains(sceneUnqiueId)) {
                            VuiEngineImpl.mRightPopPanelStack.remove(sceneUnqiueId);
                            LogUtils.i(VuiEngineImpl.TAG, "exitScene:" + sceneUnqiueId + ",mLeftPopPanelStack:" + VuiEngineImpl.mLeftPopPanelStack + " mRightPopPanelStack:" + VuiEngineImpl.mRightPopPanelStack + "," + VuiEngineImpl.mActiveSceneIds);
                            VuiSceneManager.instance().exitScene(sceneUnqiueId, VuiEngineImpl.this.mPackageName, z, str3);
                            VuiSceneToolManager.instance().exitScene(sceneUnqiueId, VuiEngineImpl.this.mPackageName, z, str3);
                        }
                    } else {
                        if (VuiConstants.SCREEN_DISPLAY_LF.equals(str3)) {
                            VuiEngineImpl.this.mLFEnterSceneStack.remove(sceneUnqiueId);
                        } else if (!VuiConstants.SCREEN_DISPLAY_RF.equals(str3)) {
                            return;
                        } else {
                            VuiEngineImpl.this.mRFEnterSceneStack.remove(sceneUnqiueId);
                        }
                        if (VuiEngineImpl.this.mBackNaviSceneStack.contains(sceneUnqiueId)) {
                            VuiEngineImpl.this.mBackNaviSceneStack.remove(sceneUnqiueId);
                        }
                        LogUtils.i(VuiEngineImpl.TAG, "exitScene:" + sceneUnqiueId + ",mLFEnterSceneStack:" + VuiEngineImpl.this.mLFEnterSceneStack + " mRFEnterSceneStack:" + VuiEngineImpl.this.mRFEnterSceneStack + "," + VuiEngineImpl.mActiveSceneIds);
                        VuiSceneManager.instance().exitScene(sceneUnqiueId, VuiEngineImpl.this.mPackageName, z, str3);
                        VuiSceneToolManager.instance().exitScene(sceneUnqiueId, VuiEngineImpl.this.mPackageName, z, str3);
                        if (VuiEngineImpl.mActiveSceneIds.containsValue(sceneUnqiueId)) {
                            if (VuiConstants.SCREEN_DISPLAY_LF.equals(str3)) {
                                if (VuiEngineImpl.this.mLFEnterSceneStack.size() != 0) {
                                    String str4 = (String) VuiEngineImpl.this.mLFEnterSceneStack.get(VuiEngineImpl.this.mLFEnterSceneStack.size() - 1);
                                    VuiEngineImpl.mActiveSceneIds.put(VuiConstants.SCREEN_DISPLAY_LF, str4);
                                    VuiEngineImpl.this.handlerEnterScene(str4, z, str3);
                                } else {
                                    VuiEngineImpl.mActiveSceneIds.put(VuiConstants.SCREEN_DISPLAY_LF, "");
                                }
                            } else if (VuiConstants.SCREEN_DISPLAY_RF.equals(str3)) {
                                if (VuiEngineImpl.this.mRFEnterSceneStack.size() != 0) {
                                    String str5 = (String) VuiEngineImpl.this.mRFEnterSceneStack.get(VuiEngineImpl.this.mRFEnterSceneStack.size() - 1);
                                    VuiEngineImpl.mActiveSceneIds.put(VuiConstants.SCREEN_DISPLAY_RF, str5);
                                    VuiEngineImpl.this.handlerEnterScene(str5, z, str3);
                                } else {
                                    VuiEngineImpl.mActiveSceneIds.put(VuiConstants.SCREEN_DISPLAY_RF, "");
                                }
                            }
                        }
                        Log.d(VuiEngineImpl.TAG, "exitScene:" + sceneUnqiueId + ",mLFEnterSceneStack:" + VuiEngineImpl.this.mLFEnterSceneStack + " mRFEnterSceneStack:" + VuiEngineImpl.this.mRFEnterSceneStack + "," + VuiEngineImpl.mActiveSceneIds);
                        if (VuiEngineImpl.this.mLFEnterSceneStack.size() != 0 || VuiEngineImpl.this.mBackNaviSceneStack.size() <= 0) {
                            return;
                        }
                        for (int i = 0; i < VuiEngineImpl.this.mBackNaviSceneStack.size(); i++) {
                            VuiEngineImpl.this.mLFEnterSceneStack.add((String) VuiEngineImpl.this.mBackNaviSceneStack.get(i));
                        }
                        String str6 = (String) VuiEngineImpl.this.mLFEnterSceneStack.get(VuiEngineImpl.this.mLFEnterSceneStack.size() - 1);
                        VuiEngineImpl.mActiveSceneIds.put(VuiConstants.SCREEN_DISPLAY_LF, str6);
                        VuiEngineImpl.this.handlerEnterScene(str6, z, VuiConstants.SCREEN_DISPLAY_LF);
                        VuiEngineImpl.this.mBackNaviSceneStack.clear();
                    }
                } catch (Exception e) {
                    LogUtils.e(VuiEngineImpl.TAG, "e:" + e.fillInStackTrace());
                }
            }
        });
    }

    public void buildScene(String str, View view, List<Integer> list, IVuiElementListener iVuiElementListener, List<String> list2, boolean z, ISceneCallbackHandler iSceneCallbackHandler) {
        buildScene(str, view, list, iVuiElementListener, list2, z, iSceneCallbackHandler, VuiConstants.SCREEN_DISPLAY_LF, (IVuiSceneListener) null);
    }

    public void buildScene(final String str, final View view, final List<Integer> list, final IVuiElementListener iVuiElementListener, final List<String> list2, final boolean z, final ISceneCallbackHandler iSceneCallbackHandler, final String str2, final IVuiSceneListener iVuiSceneListener) {
        if (VuiUtils.cannotUpload()) {
            return;
        }
        if (view == null || str == null) {
            return;
        }
        Handler handler = this.mHandler;
        if (handler == null) {
            return;
        }
        handler.postDelayed(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.5
            @Override // java.lang.Runnable
            public void run() {
                try {
                    String sceneUnqiueId = VuiEngineImpl.this.getSceneUnqiueId(str);
                    VuiSceneInfo sceneInfo = VuiSceneManager.instance().getSceneInfo(sceneUnqiueId);
                    if (iVuiSceneListener != null && sceneInfo != null && sceneInfo.getListener() != null && !iVuiSceneListener.equals(sceneInfo.getListener())) {
                        LogUtils.w(VuiEngineImpl.TAG, "要build的场景和目前持有的场景数据不一致");
                    } else if (VuiSceneManager.instance().getVuiSceneState(sceneUnqiueId) == VuiSceneState.INIT.getState() && z) {
                        LogUtils.e(VuiEngineImpl.TAG, str + "场景数据的创建必须在场景被激活后");
                    } else {
                        LogUtils.logDebug(VuiEngineImpl.TAG, "buildScene:" + str);
                        List list3 = null;
                        if (list2 != null) {
                            list3 = new ArrayList();
                            list3.addAll(list2);
                            int size = list3.size();
                            for (int i = 0; i < size; i++) {
                                String str3 = (String) list3.get(i);
                                list3.remove(str3);
                                list3.add(i, VuiEngineImpl.this.getSceneUnqiueId(str3));
                            }
                        }
                        VuiEngineImpl.this.taskStructure.dispatchTask(VuiEngineImpl.this.structureViewWrapper(sceneUnqiueId, TaskDispatcher.TaskType.BUILD, list, iVuiElementListener, list3, Arrays.asList(view), z, iSceneCallbackHandler, str2, iVuiSceneListener));
                    }
                } catch (Exception e) {
                    LogUtils.e(VuiEngineImpl.TAG, "e:" + e.fillInStackTrace());
                }
            }
        }, 200L);
    }

    public void buildScene(String str, List<View> list, List<Integer> list2, IVuiElementListener iVuiElementListener, List<String> list3, boolean z, ISceneCallbackHandler iSceneCallbackHandler) {
        buildScene(str, list, list2, iVuiElementListener, list3, z, iSceneCallbackHandler, VuiConstants.SCREEN_DISPLAY_LF, (IVuiSceneListener) null);
    }

    public void buildScene(final String str, final List<View> list, final List<Integer> list2, final IVuiElementListener iVuiElementListener, final List<String> list3, final boolean z, final ISceneCallbackHandler iSceneCallbackHandler, final String str2, final IVuiSceneListener iVuiSceneListener) {
        if (VuiUtils.cannotUpload() || str == null || this.mMainHandler == null) {
            return;
        }
        if (list == null || list.isEmpty()) {
            View rootView = VuiSceneManager.instance().getRootView(getSceneUnqiueId(str));
            if (rootView != null) {
                buildScene(str, rootView, list2, iVuiElementListener, list3, z, iSceneCallbackHandler, str2, iVuiSceneListener);
            }
        } else if (list.size() == 1) {
            buildScene(str, list.get(0), list2, iVuiElementListener, list3, z, iSceneCallbackHandler, str2, iVuiSceneListener);
        } else {
            this.mHandler.postDelayed(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.6
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        String sceneUnqiueId = VuiEngineImpl.this.getSceneUnqiueId(str);
                        VuiSceneInfo sceneInfo = VuiSceneManager.instance().getSceneInfo(sceneUnqiueId);
                        if (iVuiSceneListener != null && sceneInfo != null && sceneInfo.getListener() != null && !iVuiSceneListener.equals(sceneInfo.getListener())) {
                            LogUtils.w(VuiEngineImpl.TAG, "要build的场景和目前持有的场景数据不一致");
                        } else if (VuiSceneManager.instance().getVuiSceneState(sceneUnqiueId) == VuiSceneState.INIT.getState() && z) {
                            LogUtils.e(VuiEngineImpl.TAG, str + "场景数据的创建必须在场景被激活后");
                        } else {
                            LogUtils.logDebug(VuiEngineImpl.TAG, "buildScene:" + str);
                            List list4 = null;
                            if (list3 != null) {
                                list4 = new ArrayList();
                                list4.addAll(list3);
                                int size = list4.size();
                                for (int i = 0; i < size; i++) {
                                    String str3 = (String) list4.get(i);
                                    list4.remove(str3);
                                    list4.add(i, VuiEngineImpl.this.getSceneUnqiueId(str3));
                                }
                            }
                            VuiEngineImpl.this.taskStructure.dispatchTask(VuiEngineImpl.this.structureViewWrapper(sceneUnqiueId, TaskDispatcher.TaskType.BUILD, list2, iVuiElementListener, list4, list, z, iSceneCallbackHandler, str2, iVuiSceneListener));
                        }
                    } catch (Exception e) {
                        LogUtils.e(VuiEngineImpl.TAG, e.fillInStackTrace());
                    }
                }
            }, 200L);
        }
    }

    public void updateElementAttribute(final String str, final List<View> list) {
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.7
                @Override // java.lang.Runnable
                public void run() {
                    if (VuiUtils.cannotUpload() || list == null || TextUtils.isEmpty(str)) {
                        return;
                    }
                    LogUtils.logDebug(VuiEngineImpl.TAG, "updateElementAttribute");
                    if (VuiEngineImpl.this.mUpdateElementAttrRun != null) {
                        if (str.equals(VuiEngineImpl.this.mUpdateElementAttrRun.getSceneId())) {
                            VuiEngineImpl.this.mHandler.removeCallbacks(VuiEngineImpl.this.mUpdateElementAttrRun);
                            HashSet hashSet = new HashSet(VuiEngineImpl.this.mUpdateElementAttrRun.getUpdateViews());
                            hashSet.addAll(list);
                            VuiEngineImpl.this.mUpdateElementAttrRun.setUpdateViews(new ArrayList(hashSet));
                            VuiEngineImpl.this.mHandler.postDelayed(VuiEngineImpl.this.mUpdateElementAttrRun, 50L);
                            return;
                        }
                        VuiEngineImpl.this.mUpdateElementAttrRun.run();
                        VuiEngineImpl.this.mUpdateElementAttrRun = new UpdateElementAttrRun();
                        VuiEngineImpl.this.mUpdateElementAttrRun.setSceneId(str);
                        VuiEngineImpl.this.mUpdateElementAttrRun.setUpdateViews(list);
                        VuiEngineImpl.this.mHandler.postDelayed(VuiEngineImpl.this.mUpdateElementAttrRun, 50L);
                        return;
                    }
                    VuiEngineImpl.this.mUpdateElementAttrRun = new UpdateElementAttrRun();
                    VuiEngineImpl.this.mUpdateElementAttrRun.setSceneId(str);
                    VuiEngineImpl.this.mUpdateElementAttrRun.setUpdateViews(list);
                    VuiEngineImpl.this.mHandler.postDelayed(VuiEngineImpl.this.mUpdateElementAttrRun, 50L);
                }
            });
        }
    }

    public void setUpdateElementValue(final String str, final String str2, final Object obj) {
        if (VuiUtils.cannotUpload()) {
            return;
        }
        try {
            if (!TextUtils.isEmpty(str) && obj != null) {
                this.mHandler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.8
                    @Override // java.lang.Runnable
                    public void run() {
                        String sceneUnqiueId = VuiEngineImpl.this.getSceneUnqiueId(str);
                        if (VuiSceneManager.instance().canUpdateScene(sceneUnqiueId)) {
                            VuiScene newVuiScene = VuiUtils.getNewVuiScene(sceneUnqiueId, System.currentTimeMillis(), VuiEngineImpl.this.isAccessibilityScene, VuiEngineImpl.this.mPackageName, VuiEngineImpl.this.mPackageVersion);
                            VuiSceneCache sceneCache = VuiSceneCacheFactory.instance().getSceneCache(VuiSceneCacheFactory.CacheType.BUILD.getType());
                            if (sceneCache != null) {
                                LogUtils.i(VuiEngineImpl.TAG, "newSceneId：" + sceneUnqiueId + "，elementId：" + str2);
                                VuiElement vuiElementById = sceneCache.getVuiElementById(sceneUnqiueId, str2);
                                LogUtils.i(VuiEngineImpl.TAG, "targetElement：" + vuiElementById);
                                if (vuiElementById != null) {
                                    vuiElementById.setValues(obj);
                                    LogUtils.i(VuiEngineImpl.TAG, "targetElement：" + vuiElementById);
                                    List<VuiElement> asList = Arrays.asList(vuiElementById);
                                    LogUtils.i(VuiEngineImpl.TAG, "resultElement：" + asList);
                                    newVuiScene.setElements(asList);
                                    List<VuiElement> fusionCache = sceneCache.getFusionCache(sceneUnqiueId, asList, false);
                                    sceneCache.setCache(sceneUnqiueId, fusionCache);
                                    if (!"user".equals(Build.TYPE) && LogUtils.getLogLevel() <= LogUtils.LOG_DEBUG_LEVEL) {
                                        VuiScene newVuiScene2 = VuiUtils.getNewVuiScene(sceneUnqiueId, System.currentTimeMillis(), VuiEngineImpl.this.isAccessibilityScene, VuiEngineImpl.this.mPackageName, VuiEngineImpl.this.mPackageVersion);
                                        newVuiScene2.setElements(fusionCache);
                                        LogUtils.logDebug(VuiEngineImpl.TAG, "updateSceneTask full_scene_info" + VuiUtils.vuiSceneConvertToString(newVuiScene2));
                                    }
                                    VuiSceneManager.instance().sendSceneData(1, true, newVuiScene);
                                }
                            }
                        }
                    }
                });
            }
        } catch (Exception unused) {
        }
    }

    public void setUpdateElementVisible(final String str, final String str2, final boolean z) {
        if (VuiUtils.cannotUpload() || TextUtils.isEmpty(str)) {
            return;
        }
        this.mHandler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.9
            @Override // java.lang.Runnable
            public void run() {
                VuiElement vuiElementById;
                String sceneUnqiueId = VuiEngineImpl.this.getSceneUnqiueId(str);
                if (VuiSceneManager.instance().canUpdateScene(sceneUnqiueId)) {
                    VuiScene newVuiScene = VuiUtils.getNewVuiScene(sceneUnqiueId, System.currentTimeMillis(), VuiEngineImpl.this.isAccessibilityScene, VuiEngineImpl.this.mPackageName, VuiEngineImpl.this.mPackageVersion);
                    VuiSceneCache sceneCache = VuiSceneCacheFactory.instance().getSceneCache(VuiSceneCacheFactory.CacheType.BUILD.getType());
                    if (sceneCache == null || (vuiElementById = sceneCache.getVuiElementById(sceneUnqiueId, str2)) != null) {
                        return;
                    }
                    vuiElementById.setVisible(Boolean.valueOf(z));
                    List<VuiElement> asList = Arrays.asList(vuiElementById);
                    newVuiScene.setElements(asList);
                    List<VuiElement> fusionCache = sceneCache.getFusionCache(sceneUnqiueId, asList, false);
                    sceneCache.setCache(sceneUnqiueId, fusionCache);
                    if (!"user".equals(Build.TYPE) && LogUtils.getLogLevel() <= LogUtils.LOG_DEBUG_LEVEL) {
                        VuiScene newVuiScene2 = VuiUtils.getNewVuiScene(sceneUnqiueId, System.currentTimeMillis(), VuiEngineImpl.this.isAccessibilityScene, VuiEngineImpl.this.mPackageName, VuiEngineImpl.this.mPackageVersion);
                        newVuiScene2.setElements(fusionCache);
                        LogUtils.logDebug(VuiEngineImpl.TAG, "updateSceneTask full_scene_info" + VuiUtils.vuiSceneConvertToString(newVuiScene2));
                    }
                    VuiSceneManager.instance().sendSceneData(1, true, newVuiScene);
                }
            }
        });
    }

    public void vuiFeedback(String str, String str2) {
        if (VuiUtils.cannotUpload()) {
            return;
        }
        VuiSceneManager.instance().vuiFeedBack(str, str2);
        VuiSceneToolManager.instance().vuiFeedBack(str, str2);
    }

    public void addVuiStateChangeListener(IUnityVuiStateListener iUnityVuiStateListener) {
        if (this.vuiStateListeners.contains(iUnityVuiStateListener)) {
            return;
        }
        this.vuiStateListeners.add(iUnityVuiStateListener);
    }

    public void removeVuiStateChangeListener(IUnityVuiStateListener iUnityVuiStateListener) {
        if (this.vuiStateListeners.contains(iUnityVuiStateListener)) {
            this.vuiStateListeners.remove(iUnityVuiStateListener);
        }
    }

    public void updateListIndexState() {
        if (this.vuiStateListeners != null) {
            for (int i = 0; i < this.vuiStateListeners.size(); i++) {
                IUnityVuiStateListener iUnityVuiStateListener = this.vuiStateListeners.get(i);
                if (iUnityVuiStateListener != null) {
                    if (isInSpeech()) {
                        iUnityVuiStateListener.onShowVuiListIndex();
                    } else {
                        iUnityVuiStateListener.onHideVuiListIndex();
                    }
                }
            }
        }
    }

    private void checkScrollSubViewIsInsight(String str, JSONArray jSONArray) {
        if (str == null || jSONArray == null || VuiUtils.cannotUpload()) {
            return;
        }
        try {
            IUnityVuiSceneListener iUnityVuiSceneListener = (IUnityVuiSceneListener) VuiSceneManager.instance().getVuiSceneListener(str);
            if (iUnityVuiSceneListener == null) {
                return;
            }
            if (jSONArray != null || jSONArray.length() > 0) {
                JSONObject jSONObject = new JSONObject();
                for (int i = 0; i < jSONArray.length(); i++) {
                    JSONObject optJSONObject = jSONArray.optJSONObject(i);
                    if (optJSONObject != null) {
                        String optString = optJSONObject.optString("scrollViewId");
                        JSONArray jSONArray2 = new JSONArray();
                        if (jSONObject.has(optString)) {
                            jSONArray2 = jSONObject.getJSONArray(optString);
                        }
                        jSONArray2.put(optJSONObject.optString("elementId"));
                        jSONObject.put(optString, jSONArray2);
                    }
                }
                LogUtils.i(TAG, "checkScrollSubViewIsInsight:" + jSONObject.toString());
                if (jSONObject.length() > 0) {
                    iUnityVuiSceneListener.checkSubElementsIsInsight(jSONObject.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onVuiQuery(String str, String str2) {
        IUnityVuiSceneListener iUnityVuiSceneListener;
        IUnityVuiSceneListener iUnityVuiSceneListener2;
        try {
            JSONObject jSONObject = new JSONObject(str2);
            String optString = jSONObject.optString(VuiConstants.SCENE_ID, "");
            if (VuiUtils.is3DApp(VuiUtils.getPackageNameFromSceneId(optString))) {
                if ("vui.scrollview.childview.visible".equals(str)) {
                    checkScrollSubViewIsInsight(optString, jSONObject.optJSONArray(VuiConstants.SCENE_ELEMENTS));
                    return;
                }
                int i = 0;
                if ("vui.element.enabled".equals(str)) {
                    String optString2 = jSONObject.optString("elementId", "");
                    VuiSceneInfo sceneInfo = VuiSceneManager.instance().getSceneInfo(optString);
                    if (sceneInfo != null) {
                        List<String> subSceneList = sceneInfo.getSubSceneList();
                        LogUtils.logInfo(TAG, "subSceneIds:" + subSceneList);
                        if (subSceneList != null) {
                            while (i < subSceneList.size()) {
                                String str3 = subSceneList.get(i);
                                if (((VuiSceneBuildCache) VuiSceneCacheFactory.instance().getSceneCache(VuiSceneCacheFactory.CacheType.BUILD.getType())).getVuiElementById(str3, optString2) != null && (iUnityVuiSceneListener2 = (IUnityVuiSceneListener) VuiSceneManager.instance().getVuiSceneListener(str3)) != null) {
                                    this.querySceneIdMap.put(str3, optString);
                                    iUnityVuiSceneListener2.getElementEnabled(optString2);
                                    return;
                                }
                                i++;
                            }
                        }
                    }
                    IUnityVuiSceneListener iUnityVuiSceneListener3 = (IUnityVuiSceneListener) VuiSceneManager.instance().getVuiSceneListener(optString);
                    if (iUnityVuiSceneListener3 != null) {
                        iUnityVuiSceneListener3.getElementEnabled(optString2);
                    }
                } else if ("vui.element.scroll.state".equals(str)) {
                    String optString3 = jSONObject.optString("elementId", "");
                    String optString4 = jSONObject.optString(VuiConstants.EVENT_VALUE_DIRECTION, "");
                    VuiSceneInfo sceneInfo2 = VuiSceneManager.instance().getSceneInfo(optString);
                    if (sceneInfo2 != null) {
                        List<String> subSceneList2 = sceneInfo2.getSubSceneList();
                        LogUtils.logInfo(TAG, "subSceneIds:" + subSceneList2);
                        if (subSceneList2 != null) {
                            while (i < subSceneList2.size()) {
                                String str4 = subSceneList2.get(i);
                                if (((VuiSceneBuildCache) VuiSceneCacheFactory.instance().getSceneCache(VuiSceneCacheFactory.CacheType.BUILD.getType())).getVuiElementById(str4, optString3) != null && (iUnityVuiSceneListener = (IUnityVuiSceneListener) VuiSceneManager.instance().getVuiSceneListener(str4)) != null) {
                                    this.querySceneIdMap.put(str4, optString);
                                    iUnityVuiSceneListener.getScrollViewState(optString3, optString4);
                                    return;
                                }
                                i++;
                            }
                        }
                    }
                    IUnityVuiSceneListener iUnityVuiSceneListener4 = (IUnityVuiSceneListener) VuiSceneManager.instance().getVuiSceneListener(optString);
                    if (iUnityVuiSceneListener4 != null) {
                        iUnityVuiSceneListener4.getScrollViewState(optString3, optString4);
                    }
                }
            }
        } catch (JSONException unused) {
        }
    }

    public void onVuiQueryCallBack(String str, String str2, String str3) {
        LogUtils.d(TAG, "onVuiQueryCallBack:" + str + ",event:" + str2);
        String sceneUnqiueId = getSceneUnqiueId(str);
        if (this.querySceneIdMap.containsKey(sceneUnqiueId)) {
            this.querySceneIdMap.remove(sceneUnqiueId);
            sceneUnqiueId = this.querySceneIdMap.get(sceneUnqiueId);
        }
        if (getJSONType(str3)) {
            try {
                JSONObject jSONObject = new JSONObject(str3);
                jSONObject.put(VuiConstants.SCENE_ID, sceneUnqiueId);
                VuiSceneManager.instance().onVuiQueryCallBack(sceneUnqiueId, str2, jSONObject.toString());
                VuiSceneToolManager.instance().onVuiQueryCallBack(sceneUnqiueId, str2, jSONObject.toString());
                return;
            } catch (JSONException unused) {
                return;
            }
        }
        VuiSceneManager.instance().onVuiQueryCallBack(sceneUnqiueId, str2, str3);
        VuiSceneToolManager.instance().onVuiQueryCallBack(sceneUnqiueId, str2, str3);
    }

    /* loaded from: classes2.dex */
    class UpdateElementAttrRun implements Runnable {
        private String sceneId;
        private List<View> updateViews;

        UpdateElementAttrRun() {
        }

        public String getSceneId() {
            return this.sceneId;
        }

        public void setSceneId(String str) {
            this.sceneId = str;
        }

        public List<View> getUpdateViews() {
            return this.updateViews;
        }

        public void setUpdateViews(List<View> list) {
            this.updateViews = list;
        }

        @Override // java.lang.Runnable
        public void run() {
            VuiEngineImpl.this.mHandler.removeCallbacks(VuiEngineImpl.this.mUpdateElementAttrRun);
            VuiEngineImpl.this.mUpdateElementAttrRun = null;
            String sceneUnqiueId = VuiEngineImpl.this.getSceneUnqiueId(this.sceneId);
            if (VuiSceneManager.instance().canUpdateScene(sceneUnqiueId)) {
                LogUtils.logDebug(VuiEngineImpl.TAG, "updateSceneElementAttribute:" + this.sceneId);
                VuiEngineImpl.this.taskStructure.dispatchTask(VuiEngineImpl.this.structureViewWrapper(sceneUnqiueId, TaskDispatcher.TaskType.UPDATEATTRIBUTE, this.updateViews));
            }
        }
    }

    /* loaded from: classes2.dex */
    class UpdateSceneRun implements Runnable {
        private String sceneId;
        private List<View> updateViews;

        UpdateSceneRun() {
        }

        public String getSceneId() {
            return this.sceneId;
        }

        public void setSceneId(String str) {
            this.sceneId = str;
        }

        public List<View> getUpdateViews() {
            return this.updateViews;
        }

        public void setUpdateViews(List<View> list) {
            this.updateViews = list;
        }

        @Override // java.lang.Runnable
        public void run() {
            VuiEngineImpl.this.mHandler.removeCallbacks(VuiEngineImpl.this.mUpdateSceneRun);
            VuiEngineImpl.this.mUpdateSceneRun = null;
            String sceneUnqiueId = VuiEngineImpl.this.getSceneUnqiueId(this.sceneId);
            if (VuiSceneManager.instance().canUpdateScene(sceneUnqiueId)) {
                LogUtils.logDebug(VuiEngineImpl.TAG, "updateScene:" + this.sceneId);
                VuiEngineImpl.this.taskStructure.dispatchTask(VuiEngineImpl.this.structureViewWrapper(sceneUnqiueId, TaskDispatcher.TaskType.UPDATE, this.updateViews));
            }
        }
    }

    public void updateRecyclerViewItemView(String str, List<View> list, RecyclerView recyclerView) {
        if (VuiUtils.cannotUpload() || list == null || TextUtils.isEmpty(str)) {
            return;
        }
        String sceneUnqiueId = getSceneUnqiueId(str);
        if (VuiSceneManager.instance().canUpdateScene(sceneUnqiueId)) {
            LogUtils.logInfo(TAG, "updateRecyclerViewItemView:" + str);
            this.taskStructure.dispatchTask(structureViewWrapper(sceneUnqiueId, TaskDispatcher.TaskType.UPDATERECYCLEVIEWITEM, list, recyclerView));
        }
    }

    public void updateScene(final String str, final View view) {
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.10
                @Override // java.lang.Runnable
                public void run() {
                    if (VuiUtils.cannotUpload() || view == null || TextUtils.isEmpty(str)) {
                        return;
                    }
                    LogUtils.logDebug(VuiEngineImpl.TAG, "updateScene");
                    if (VuiEngineImpl.this.mUpdateSceneRun != null) {
                        if (str.equals(VuiEngineImpl.this.mUpdateSceneRun.getSceneId())) {
                            VuiEngineImpl.this.mHandler.removeCallbacks(VuiEngineImpl.this.mUpdateSceneRun);
                            HashSet hashSet = new HashSet(VuiEngineImpl.this.mUpdateSceneRun.getUpdateViews());
                            hashSet.add(view);
                            VuiEngineImpl.this.mUpdateSceneRun.setUpdateViews(new ArrayList(hashSet));
                            VuiEngineImpl.this.mHandler.postDelayed(VuiEngineImpl.this.mUpdateSceneRun, 50L);
                            return;
                        }
                        VuiEngineImpl.this.mUpdateSceneRun.run();
                        VuiEngineImpl.this.mUpdateSceneRun = new UpdateSceneRun();
                        VuiEngineImpl.this.mUpdateSceneRun.setSceneId(str);
                        VuiEngineImpl.this.mUpdateSceneRun.setUpdateViews(Arrays.asList(view));
                        VuiEngineImpl.this.mHandler.postDelayed(VuiEngineImpl.this.mUpdateSceneRun, 50L);
                        return;
                    }
                    VuiEngineImpl.this.mUpdateSceneRun = new UpdateSceneRun();
                    VuiEngineImpl.this.mUpdateSceneRun.setSceneId(str);
                    VuiEngineImpl.this.mUpdateSceneRun.setUpdateViews(Arrays.asList(view));
                    VuiEngineImpl.this.mHandler.postDelayed(VuiEngineImpl.this.mUpdateSceneRun, 50L);
                }
            });
        }
    }

    public void updateScene(final String str, final List<View> list) {
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.11
                @Override // java.lang.Runnable
                public void run() {
                    if (VuiUtils.cannotUpload() || list == null || TextUtils.isEmpty(str)) {
                        return;
                    }
                    LogUtils.logDebug(VuiEngineImpl.TAG, "updateScene");
                    try {
                        String sceneUnqiueId = VuiEngineImpl.this.getSceneUnqiueId(str);
                        if (VuiSceneManager.instance().canUpdateScene(sceneUnqiueId)) {
                            LogUtils.logDebug(VuiEngineImpl.TAG, "updateScene:" + str);
                            VuiEngineImpl.this.taskStructure.dispatchTask(VuiEngineImpl.this.structureViewWrapper(sceneUnqiueId, TaskDispatcher.TaskType.UPDATE, (List<Integer>) null, (IVuiElementListener) null, list));
                        }
                    } catch (Exception e) {
                        LogUtils.e(VuiEngineImpl.TAG, e.fillInStackTrace());
                    }
                }
            });
        }
    }

    public void updateScene(final String str, final List<View> list, final List<Integer> list2, final IVuiElementListener iVuiElementListener) {
        if (VuiUtils.cannotUpload() || list == null || str == null) {
            return;
        }
        if (list.size() == 1) {
            updateScene(str, list.get(0), list2, iVuiElementListener);
        } else {
            this.mHandler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.12
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        String sceneUnqiueId = VuiEngineImpl.this.getSceneUnqiueId(str);
                        if (VuiSceneManager.instance().canUpdateScene(sceneUnqiueId)) {
                            LogUtils.logDebug(VuiEngineImpl.TAG, "updateScene:" + str);
                            VuiEngineImpl.this.taskStructure.dispatchTask(VuiEngineImpl.this.structureViewWrapper(sceneUnqiueId, TaskDispatcher.TaskType.UPDATE, list2, iVuiElementListener, list));
                        }
                    } catch (Exception e) {
                        LogUtils.e(VuiEngineImpl.TAG, e.fillInStackTrace());
                    }
                }
            });
        }
    }

    public void updateScene(final String str, final View view, final List<Integer> list, final IVuiElementListener iVuiElementListener) {
        if (VuiUtils.cannotUpload() || view == null || str == null) {
            return;
        }
        this.mHandler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.13
            @Override // java.lang.Runnable
            public void run() {
                try {
                    if (VuiEngineImpl.this.mainThreadSceneList.contains(str) && (view instanceof RecyclerView)) {
                        VuiEngineImpl.this.mMainHandler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.13.1
                            @Override // java.lang.Runnable
                            public void run() {
                                String sceneUnqiueId = VuiEngineImpl.this.getSceneUnqiueId(str);
                                if (VuiSceneManager.instance().canUpdateScene(sceneUnqiueId)) {
                                    LogUtils.logDebug(VuiEngineImpl.TAG, "updateScene:" + str);
                                    ArrayList arrayList = new ArrayList();
                                    arrayList.add(view);
                                    VuiEngineImpl.this.taskStructure.dispatchTask(VuiEngineImpl.this.structureViewWrapper(sceneUnqiueId, TaskDispatcher.TaskType.UPDATE, list, iVuiElementListener, arrayList));
                                }
                            }
                        });
                    } else {
                        String sceneUnqiueId = VuiEngineImpl.this.getSceneUnqiueId(str);
                        if (!VuiSceneManager.instance().canUpdateScene(sceneUnqiueId)) {
                            return;
                        }
                        LogUtils.logDebug(VuiEngineImpl.TAG, "updateScene:" + str);
                        ArrayList arrayList = new ArrayList();
                        arrayList.add(view);
                        VuiEngineImpl.this.taskStructure.dispatchTask(VuiEngineImpl.this.structureViewWrapper(sceneUnqiueId, TaskDispatcher.TaskType.UPDATE, list, iVuiElementListener, arrayList));
                    }
                } catch (Exception e) {
                    LogUtils.e(VuiEngineImpl.TAG, e.fillInStackTrace());
                }
            }
        });
    }

    public void handleNewRootviewToScene(final String str, final List<View> list, final VuiPriority vuiPriority) {
        Handler handler;
        if (VuiUtils.cannotUpload() || (handler = this.mHandler) == null) {
            return;
        }
        handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.14
            @Override // java.lang.Runnable
            public void run() {
                String str2;
                if (list == null || (str2 = str) == null) {
                    return;
                }
                String sceneUnqiueId = VuiEngineImpl.this.getSceneUnqiueId(str2);
                if (VuiSceneManager.instance().canUpdateScene(sceneUnqiueId)) {
                    LogUtils.logDebug(VuiEngineImpl.TAG, "handleNewRootviewToScene:" + str);
                    VuiEngineImpl.this.taskStructure.dispatchTask(VuiEngineImpl.this.structureViewWrapper(sceneUnqiueId, vuiPriority, TaskDispatcher.TaskType.ADD, (List<View>) list, true));
                }
            }
        });
    }

    public void removeOtherRootViewFromScene(final String str, final List<View> list) {
        Handler handler;
        if (VuiUtils.cannotUpload() || (handler = this.mHandler) == null) {
            return;
        }
        handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.15
            @Override // java.lang.Runnable
            public void run() {
                String str2 = str;
                if (str2 == null) {
                    return;
                }
                String sceneUnqiueId = VuiEngineImpl.this.getSceneUnqiueId(str2);
                if (VuiSceneManager.instance().canUpdateScene(sceneUnqiueId)) {
                    LogUtils.logDebug(VuiEngineImpl.TAG, "removeOtherRootViewFromScene:" + str);
                    VuiEngineImpl.this.taskStructure.dispatchTask(VuiEngineImpl.this.structureViewWrapper(sceneUnqiueId, TaskDispatcher.TaskType.REMOVE, list));
                }
            }
        });
    }

    public void removeOtherRootViewFromScene(final String str) {
        Handler handler;
        if (VuiUtils.cannotUpload() || (handler = this.mHandler) == null) {
            return;
        }
        handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.16
            @Override // java.lang.Runnable
            public void run() {
                String str2 = str;
                if (str2 == null) {
                    return;
                }
                String sceneUnqiueId = VuiEngineImpl.this.getSceneUnqiueId(str2);
                if (VuiSceneManager.instance().canUpdateScene(sceneUnqiueId)) {
                    LogUtils.logDebug(VuiEngineImpl.TAG, "removeOtherRootViewFromScene:" + str);
                    VuiEngineImpl.this.taskStructure.dispatchTask(VuiEngineImpl.this.structureViewWrapper(sceneUnqiueId, TaskDispatcher.TaskType.REMOVE, null));
                }
            }
        });
    }

    public void addSceneElementGroup(final View view, final String str, final VuiPriority vuiPriority, final IVuiSceneListener iVuiSceneListener) {
        Handler handler;
        if (VuiUtils.cannotUpload() || view == null || str == null || (handler = this.mHandler) == null) {
            return;
        }
        handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.17
            @Override // java.lang.Runnable
            public void run() {
                String sceneUnqiueId = VuiEngineImpl.this.getSceneUnqiueId(str);
                if (VuiSceneManager.instance().canUpdateScene(sceneUnqiueId)) {
                    LogUtils.logDebug(VuiEngineImpl.TAG, "addSceneElementGroup:" + str);
                    VuiEngineImpl.this.taskStructure.dispatchTask(VuiEngineImpl.this.structureViewWrapper(sceneUnqiueId, (String) null, vuiPriority, iVuiSceneListener, view));
                }
            }
        });
    }

    public void addSceneElement(final View view, final String str, final String str2) {
        Handler handler;
        if (VuiUtils.cannotUpload() || view == null || str2 == null || (handler = this.mHandler) == null) {
            return;
        }
        handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.18
            @Override // java.lang.Runnable
            public void run() {
                String sceneUnqiueId = VuiEngineImpl.this.getSceneUnqiueId(str2);
                if (VuiSceneManager.instance().canUpdateScene(sceneUnqiueId)) {
                    LogUtils.logDebug(VuiEngineImpl.TAG, "addSceneElement:" + str2);
                    VuiEngineImpl.this.taskStructure.dispatchTask(VuiEngineImpl.this.structureViewWrapper(sceneUnqiueId, str, (VuiPriority) null, (IVuiSceneListener) null, view));
                }
            }
        });
    }

    public void removeSceneElementGroup(final String str, final String str2, final IVuiSceneListener iVuiSceneListener) {
        Handler handler;
        if (VuiUtils.cannotUpload() || str2 == null || str == null || (handler = this.mHandler) == null) {
            return;
        }
        handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.19
            @Override // java.lang.Runnable
            public void run() {
                String sceneUnqiueId = VuiEngineImpl.this.getSceneUnqiueId(str2);
                if (VuiSceneManager.instance().canUpdateScene(sceneUnqiueId)) {
                    LogUtils.logDebug(VuiEngineImpl.TAG, "removeSceneElementGroup:" + str2);
                    VuiEngineImpl.this.taskStructure.dispatchTask(VuiEngineImpl.this.structureViewWrapper(sceneUnqiueId, TaskDispatcher.TaskType.REMOVE, str, iVuiSceneListener));
                }
            }
        });
    }

    public void removeVuiElement(final String str, final String str2) {
        if (VuiUtils.cannotUpload() || TextUtils.isEmpty(str) || TextUtils.isEmpty(str2)) {
            return;
        }
        this.mHandler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.20
            @Override // java.lang.Runnable
            public void run() {
                if (VuiSceneManager.instance().canUpdateScene(VuiEngineImpl.this.getSceneUnqiueId(str))) {
                    LogUtils.logInfo(VuiEngineImpl.TAG, "removeVuiElement:" + str + ",elementId:" + str2);
                    VuiSceneManager.instance().sendSceneData(3, true, str + "," + str2);
                    VuiSceneToolManager.instance().sendSceneData(3, true, str + "," + str2);
                }
            }
        });
    }

    public void dispatchVuiEvent(final String str, final String str2) {
        Handler handler = this.mDispatherHandler;
        if (handler == null) {
            return;
        }
        handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.21
            @Override // java.lang.Runnable
            public void run() {
                if (VuiUtils.canUseVuiFeature()) {
                    LogUtils.logDebug(VuiEngineImpl.TAG, "dispatchVuiEvent:" + str);
                    if (str.equals(VuiConstants.VUI_FEATURE_DISABLE)) {
                        VuiUtils.disableVuiFeature();
                        VuiSceneManager.instance().setFeatureState(false);
                        VuiSceneToolManager.instance().setFeatureState(false);
                        LogUtils.logInfo(VuiEngineImpl.TAG, "disableVuiFeature:" + VuiEngineImpl.this.vuiStateListeners.size());
                        if (VuiEngineImpl.this.isInSpeech()) {
                            if (VuiEngineImpl.this.vuiStateListeners != null) {
                                for (int i = 0; i < VuiEngineImpl.this.vuiStateListeners.size(); i++) {
                                    IUnityVuiStateListener iUnityVuiStateListener = (IUnityVuiStateListener) VuiEngineImpl.this.vuiStateListeners.get(i);
                                    if (iUnityVuiStateListener != null) {
                                        iUnityVuiStateListener.onHideVuiListIndex();
                                    }
                                }
                            }
                            VuiEngineImpl.this.onVuiStateChanged();
                        }
                    } else if (str.equals(VuiConstants.VUI_FEATURE_ENABLE)) {
                        VuiUtils.enableVuiFeature();
                        VuiSceneManager.instance().setFeatureState(true);
                        VuiSceneToolManager.instance().setFeatureState(true);
                        LogUtils.logInfo(VuiEngineImpl.TAG, "enableVuiFeature:");
                    } else if (str.equals(VuiConstants.DM_START_EVENT)) {
                        VuiSceneManager.instance().setInSpeech(true);
                        VuiEngineImpl.this.sendVuiStateChangedEvent(true);
                    } else if (str.equals(VuiConstants.DM_END_EVENT)) {
                        VuiSceneManager.instance().setInSpeech(false);
                        VuiEngineImpl.this.sendVuiStateChangedEvent(false);
                    } else if (str.equals(VuiConstants.REBUILD_EVENT) || str.equals(VuiConstants.SCENE_REBUILD_EVENT)) {
                        if (VuiUtils.cannotUpload() || TextUtils.isEmpty(str2) || !VuiUtils.getPackageNameFromSceneId(str2).equals(VuiEngineImpl.this.mPackageName)) {
                            return;
                        }
                        List<VuiElement> cache = ((VuiSceneBuildCache) VuiSceneCacheFactory.instance().getSceneCache(VuiSceneCacheFactory.CacheType.BUILD.getType())).getCache(str2);
                        if (cache == null || cache.isEmpty()) {
                            IVuiSceneListener vuiSceneListener = VuiSceneManager.instance().getVuiSceneListener(str2);
                            if (vuiSceneListener != null) {
                                vuiSceneListener.onBuildScene();
                                return;
                            }
                            return;
                        }
                        VuiScene newVuiScene = VuiUtils.getNewVuiScene(str2, System.currentTimeMillis(), VuiEngineImpl.this.isAccessibilityScene, VuiEngineImpl.this.mPackageName, VuiEngineImpl.this.mPackageVersion);
                        newVuiScene.setElements(cache);
                        VuiSceneCacheFactory.instance().removeAllCache(str2);
                        VuiSceneTestCacheFactory.instance().removeAllCache(str2);
                        VuiSceneManager.instance().sendSceneData(0, true, newVuiScene);
                        VuiSceneToolManager.instance().sendSceneData(0, true, newVuiScene);
                    } else if (!VuiUtils.cannotUpload() && !TextUtils.isEmpty(str2)) {
                        try {
                            VuiEngineImpl.this.eventDispatcher.dispatch(str, str2);
                        } catch (Throwable th) {
                            th.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onVuiStateChanged() {
        final IVuiSceneListener vuiSceneListener;
        final IVuiSceneListener vuiSceneListener2;
        try {
            List<String> list = this.mLFEnterSceneStack;
            for (int i = 0; i < list.size(); i++) {
                String str = list.get(i);
                if (!TextUtils.isEmpty(str) && (vuiSceneListener2 = VuiSceneManager.instance().getVuiSceneListener(str)) != null) {
                    this.mainHandler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.22
                        @Override // java.lang.Runnable
                        public void run() {
                            vuiSceneListener2.onVuiStateChanged();
                        }
                    });
                }
            }
            List<String> list2 = this.mRFEnterSceneStack;
            for (int i2 = 0; i2 < list2.size(); i2++) {
                String str2 = list2.get(i2);
                if (!TextUtils.isEmpty(str2) && (vuiSceneListener = VuiSceneManager.instance().getVuiSceneListener(str2)) != null) {
                    this.mainHandler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.23
                        @Override // java.lang.Runnable
                        public void run() {
                            vuiSceneListener.onVuiStateChanged();
                        }
                    });
                }
            }
        } catch (Exception unused) {
            LogUtils.e(TAG, "sendVuiStateChangedEvent error");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendVuiStateChangedEvent(boolean z) {
        if (VuiUtils.cannotUpload()) {
            return;
        }
        updateListIndexState();
        onVuiStateChanged();
    }

    public String getElementState(String str, String str2) {
        if (VuiUtils.cannotUpload()) {
            return null;
        }
        LogUtils.logDebug(TAG, "getElementState:" + str + ",elementId:" + str2);
        if (getJSONType(str2)) {
            return VuiSceneManager.instance().checkScrollSubViewIsVisible(str, str2);
        }
        return VuiSceneManager.instance().getElementState(str, str2);
    }

    public boolean getJSONType(String str) {
        if (!TextUtils.isEmpty(str)) {
            String trim = str.trim();
            if (trim.startsWith("{") && trim.endsWith("}")) {
                return true;
            }
            if (trim.startsWith("[") && trim.endsWith("]")) {
                return true;
            }
        }
        return false;
    }

    public void vuiFeedback(View view, VuiFeedback vuiFeedback) {
        if (VuiUtils.cannotUpload()) {
            return;
        }
        VuiSceneManager.instance().vuiFeedBack(view, vuiFeedback);
        VuiSceneToolManager.instance().vuiFeedBack(view, vuiFeedback);
    }

    public void vuiFeedback(View view, String str) {
        if (VuiUtils.cannotUpload()) {
            return;
        }
        VuiFeedback build = new VuiFeedback.Builder().content(str).build();
        VuiSceneManager.instance().vuiFeedBack(view, build);
        VuiSceneToolManager.instance().vuiFeedBack(view, build);
    }

    public void vuiFeedback(String str, VuiFeedback vuiFeedback) {
        if (VuiUtils.cannotUpload()) {
            return;
        }
        VuiSceneManager.instance().vuiFeedBack(str, vuiFeedback);
        VuiSceneToolManager.instance().vuiFeedBack(str, vuiFeedback);
    }

    public void subscribe(final String str) {
        Handler handler;
        if (VuiUtils.canUseVuiFeature() && (handler = this.mHandler) != null) {
            handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.24
                @Override // java.lang.Runnable
                public void run() {
                    LogUtils.logInfo(VuiEngineImpl.TAG, "subscribe:" + str);
                    VuiSceneManager.instance().subscribe(str);
                    VuiSceneToolManager.instance().subscribe(str);
                }
            });
        }
    }

    public void subscribeVuiFeature() {
        Handler handler;
        if (VuiUtils.canUseVuiFeature() && (handler = this.mHandler) != null) {
            handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.25
                @Override // java.lang.Runnable
                public void run() {
                    LogUtils.logInfo(VuiEngineImpl.TAG, "subscribeVuiFeature");
                    VuiSceneManager.instance().subscribeVuiFeature();
                }
            });
        }
    }

    public void unSubscribeVuiFeature() {
        Handler handler;
        if (VuiUtils.canUseVuiFeature() && (handler = this.mHandler) != null) {
            handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.26
                @Override // java.lang.Runnable
                public void run() {
                    LogUtils.logInfo(VuiEngineImpl.TAG, "subscribeVuiFeature");
                    VuiSceneManager.instance().unSubscribeVuiFeature();
                }
            });
        }
    }

    public void unSubscribe() {
        Handler handler;
        if (VuiUtils.canUseVuiFeature() && (handler = this.mHandler) != null) {
            handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.27
                @Override // java.lang.Runnable
                public void run() {
                    LogUtils.logInfo(VuiEngineImpl.TAG, "unSubscribe");
                    VuiSceneManager.instance().unSubscribe();
                    VuiSceneToolManager.instance().unSubscribe();
                }
            });
        }
    }

    public void addVuiSceneListener(String str, View view, IVuiSceneListener iVuiSceneListener, IVuiElementChangedListener iVuiElementChangedListener, boolean z) {
        addVuiSceneListener(str, view, iVuiSceneListener, iVuiElementChangedListener, z, false);
    }

    public void addDupVuiSceneListener(String str, View view, IVuiSceneListener iVuiSceneListener, IVuiElementChangedListener iVuiElementChangedListener, boolean z) {
        addVuiSceneListener(str, view, iVuiSceneListener, iVuiElementChangedListener, z, true);
    }

    public void addVuiSceneListener(final String str, final View view, final IVuiSceneListener iVuiSceneListener, final IVuiElementChangedListener iVuiElementChangedListener, final boolean z, final boolean z2) {
        Handler handler;
        if (!VuiUtils.canUseVuiFeature() || (handler = this.mHandler) == null || str == null) {
            return;
        }
        handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.28
            @Override // java.lang.Runnable
            public void run() {
                LogUtils.logDebug(VuiEngineImpl.TAG, "addVuiSceneListener :" + str);
                String str2 = str;
                if (z2) {
                    str2 = iVuiSceneListener.toString() + "-" + str;
                }
                String sceneUnqiueId = VuiEngineImpl.this.getSceneUnqiueId(str2);
                VuiSceneManager.instance().addVuiSceneListener(sceneUnqiueId, view, iVuiSceneListener, iVuiElementChangedListener, z);
                VuiSceneToolManager.instance().addVuiSceneListener(sceneUnqiueId, view, iVuiSceneListener, iVuiElementChangedListener, z);
            }
        });
    }

    public void removeDupVuiSceneListener(String str, IVuiSceneListener iVuiSceneListener, boolean z) {
        if (iVuiSceneListener == null || TextUtils.isEmpty(str)) {
            return;
        }
        removeVuiSceneListener(iVuiSceneListener.toString() + "-" + str, iVuiSceneListener, z);
    }

    public void removeVuiSceneListener(final String str, final IVuiSceneListener iVuiSceneListener, final boolean z) {
        Handler handler;
        if (VuiUtils.canUseVuiFeature() && (handler = this.mHandler) != null) {
            handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.29
                @Override // java.lang.Runnable
                public void run() {
                    String str2 = str;
                    if (str2 == null) {
                        return;
                    }
                    String sceneUnqiueId = VuiEngineImpl.this.getSceneUnqiueId(str2);
                    VuiSceneInfo sceneInfo = VuiSceneManager.instance().getSceneInfo(sceneUnqiueId);
                    boolean z2 = false;
                    if (sceneInfo != null && sceneInfo.isWholeScene()) {
                        z2 = true;
                    }
                    if (iVuiSceneListener != null && sceneInfo != null && sceneInfo.getListener() != null && !iVuiSceneListener.equals(sceneInfo.getListener())) {
                        LogUtils.w(VuiEngineImpl.TAG, "要销毁的场景和目前持有的场景数据不一致");
                        return;
                    }
                    if (!VuiEngineImpl.mActiveSceneIds.containsValue(sceneUnqiueId)) {
                        if (VuiEngineImpl.this.mLFEnterSceneStack.contains(sceneUnqiueId)) {
                            VuiEngineImpl.this.mLFEnterSceneStack.remove(sceneUnqiueId);
                        } else if (VuiEngineImpl.this.mRFEnterSceneStack.contains(sceneUnqiueId)) {
                            VuiEngineImpl.this.mRFEnterSceneStack.remove(sceneUnqiueId);
                        } else if (VuiEngineImpl.mRightPopPanelStack.contains(sceneUnqiueId)) {
                            VuiEngineImpl.mRightPopPanelStack.remove(sceneUnqiueId);
                            VuiEngineImpl.this.exitScene(str, VuiConstants.SCREEN_DISPLAY_RF, true, iVuiSceneListener);
                        } else if (VuiEngineImpl.mLeftPopPanelStack.contains(sceneUnqiueId)) {
                            VuiEngineImpl.mLeftPopPanelStack.remove(sceneUnqiueId);
                            VuiEngineImpl.this.exitScene(str, VuiConstants.SCREEN_DISPLAY_LF, true, iVuiSceneListener);
                        }
                    } else {
                        VuiEngineImpl.this.exitScene(str, sceneInfo != null ? sceneInfo.isWholeScene() : true);
                    }
                    LogUtils.logDebug(VuiEngineImpl.TAG, "removeVuiSceneListener :" + str + ",isupload:" + z2 + ",keepCache:" + z);
                    VuiEngineImpl.this.taskStructure.removeTask(sceneUnqiueId);
                    VuiSceneManager.instance().removeVuiSceneListener(sceneUnqiueId, z2, z, iVuiSceneListener);
                    VuiSceneToolManager.instance().removeVuiSceneListener(sceneUnqiueId, z2, z, iVuiSceneListener);
                }
            });
        }
    }

    public void setVuiElementTag(View view, String str) {
        if (!VuiUtils.canUseVuiFeature() || view == null) {
            return;
        }
        String str2 = "4657_" + str;
        view.setTag(str2);
        if (view instanceof IVuiElement) {
            ((IVuiElement) view).setVuiElementId(str2);
        }
    }

    public String getVuiElementTag(View view) {
        if (!VuiUtils.canUseVuiFeature() || view == null) {
            return null;
        }
        String str = (String) view.getTag();
        if (str.startsWith("4657")) {
            return str;
        }
        return null;
    }

    public void setVuiElementUnSupportTag(View view, boolean z) {
        if (!VuiUtils.canUseVuiFeature() || view == null) {
            return;
        }
        view.setTag(R.id.vuiElementUnSupport, Boolean.valueOf(z));
    }

    public void setVuiCustomDisableControlTag(View view) {
        if (!VuiUtils.canUseVuiFeature() || view == null) {
            return;
        }
        view.setTag(R.id.customDisableControl, true);
    }

    public void setHasFeedBackTxtByViewDisable(View view, String str) {
        if (!VuiUtils.canUseVuiFeature() || view == null) {
            return;
        }
        view.setTag(R.id.customDisableFeedbackTTS, str);
    }

    public void setVuiCustomDisableFeedbackTag(View view) {
        if (!VuiUtils.canUseVuiFeature() || view == null) {
            return;
        }
        view.setTag(R.id.customDisableFeedback, true);
    }

    public void setVuiElementDefaultAction(View view, String str, Object obj) {
        if (!VuiUtils.canUseVuiFeature() || view == null || str == null || obj == null) {
            return;
        }
        try {
            JSONObject jSONObject = new JSONObject();
            VuiUtils.generateElementValueJSON(jSONObject, str, obj);
            view.setTag(R.id.vuiElementDefaultAction, jSONObject);
        } catch (Exception e) {
            LogUtils.e(TAG, e.fillInStackTrace());
        }
    }

    public void setVuiStatefulButtonClick(View view) {
        if (!VuiUtils.canUseVuiFeature() || view == null) {
            return;
        }
        view.setTag(R.id.vuiStatefulButtonClick, true);
    }

    public void disableChildVuiAttrWhenInvisible(View view) {
        if (!VuiUtils.canUseVuiFeature() || view == null) {
            return;
        }
        view.setTag(R.id.disableChildVuiAttrsWhenInvisible, true);
    }

    public void setVuiLabelUnSupportText(View... viewArr) {
        if (!VuiUtils.canUseVuiFeature() || viewArr == null) {
            return;
        }
        for (View view : viewArr) {
            view.setTag(R.id.vuiLabelUnSupportText, true);
        }
    }

    public void setVuiElementVisibleTag(View view, boolean z) {
        if (!VuiUtils.canUseVuiFeature() || view == null) {
            return;
        }
        view.setTag(R.id.vuiElementVisible, Boolean.valueOf(z));
    }

    public Boolean getVuiElementVisibleTag(View view) {
        Boolean bool;
        if (!VuiUtils.canUseVuiFeature() || view == null || (bool = (Boolean) view.getTag(R.id.vuiElementVisible)) == null) {
            return null;
        }
        return bool;
    }

    public void disableVuiFeature() {
        LogUtils.logInfo(TAG, "user disable feature");
        VuiUtils.userSetFeatureState(true);
    }

    public void enableVuiFeature() {
        LogUtils.logInfo(TAG, "user enable feature");
        VuiUtils.userSetFeatureState(false);
    }

    public boolean isVuiFeatureDisabled() {
        return VuiUtils.cannotUpload();
    }

    public boolean isInSpeech() {
        return VuiSceneManager.instance().isInSpeech();
    }

    public String getVuiElementId(String str, int i, String str2) {
        if (str != null) {
            str2 = str + "_" + str2;
        }
        return i != -1 ? str2 + "_" + i : str2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public List<TaskWrapper> structureViewWrapper(String str, TaskDispatcher.TaskType taskType, String str2, IVuiSceneListener iVuiSceneListener) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new TaskWrapper((View) null, str, taskType, iVuiSceneListener, str2));
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public List<TaskWrapper> structureViewWrapper(String str, VuiPriority vuiPriority, TaskDispatcher.TaskType taskType, List<View> list, boolean z) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new TaskWrapper(str, vuiPriority, taskType, list, z));
        return arrayList;
    }

    private List<TaskWrapper> structureViewWrapper(String str, TaskDispatcher.TaskType taskType, List<Integer> list, IVuiElementListener iVuiElementListener, List<String> list2, List<View> list3, boolean z, ISceneCallbackHandler iSceneCallbackHandler) {
        ArrayList arrayList = new ArrayList();
        if (list3 != null && list3.size() != 0) {
            if (list3.size() == 1) {
                arrayList.add(new TaskWrapper(list3.get(0), str, list3.get(0).getId(), taskType, list, iVuiElementListener, list2, z, iSceneCallbackHandler));
            } else {
                arrayList.add(new TaskWrapper(list3, str, taskType, list, iVuiElementListener, list2, z, iSceneCallbackHandler));
            }
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public List<TaskWrapper> structureViewWrapper(String str, TaskDispatcher.TaskType taskType, List<Integer> list, IVuiElementListener iVuiElementListener, List<String> list2, List<View> list3, boolean z, ISceneCallbackHandler iSceneCallbackHandler, String str2, IVuiSceneListener iVuiSceneListener) {
        ArrayList arrayList = new ArrayList();
        if (list3 != null && list3.size() != 0) {
            if (list3.size() == 1) {
                arrayList.add(new TaskWrapper(list3.get(0), str, list3.get(0).getId(), taskType, list, iVuiElementListener, list2, z, iSceneCallbackHandler, str2, iVuiSceneListener));
            } else {
                arrayList.add(new TaskWrapper(list3, str, taskType, list, iVuiElementListener, list2, z, iSceneCallbackHandler, str2, iVuiSceneListener));
            }
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public List<TaskWrapper> structureViewWrapper(String str, TaskDispatcher.TaskType taskType, List<Integer> list, IVuiElementListener iVuiElementListener, List<View> list2) {
        ArrayList arrayList = new ArrayList();
        if (list2 != null && !list2.isEmpty()) {
            if (list2.size() == 1) {
                View view = list2.get(0);
                if (view.getId() != -1) {
                    arrayList.add(new TaskWrapper(view, str, view.getId(), taskType, list, iVuiElementListener));
                } else {
                    arrayList.add(new TaskWrapper(view, str, taskType, list, iVuiElementListener));
                }
            } else {
                arrayList.add(new TaskWrapper(list2, str, taskType, list, iVuiElementListener));
            }
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public List<TaskWrapper> structureViewWrapper(String str, TaskDispatcher.TaskType taskType, List<View> list) {
        ArrayList arrayList = new ArrayList();
        if (list != null && list.size() == 1) {
            View view = list.get(0);
            if (view.getId() != -1) {
                arrayList.add(new TaskWrapper(view, view.getId(), str, taskType));
            } else {
                arrayList.add(new TaskWrapper(str, taskType, view));
            }
        } else {
            arrayList.add(new TaskWrapper(str, taskType, list));
        }
        return arrayList;
    }

    private List<TaskWrapper> structureViewWrapper(String str, TaskDispatcher.TaskType taskType, List<View> list, RecyclerView recyclerView) {
        ArrayList arrayList = new ArrayList();
        if (list != null && list.size() == 1) {
            View view = list.get(0);
            if (view.getId() != -1) {
                arrayList.add(new TaskWrapper(view, view.getId(), str, taskType, recyclerView));
            } else {
                arrayList.add(new TaskWrapper(str, taskType, view, recyclerView));
            }
        } else {
            arrayList.add(new TaskWrapper(str, taskType, list, recyclerView));
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public List<TaskWrapper> structureViewWrapper(String str, String str2, VuiPriority vuiPriority, IVuiSceneListener iVuiSceneListener, View... viewArr) {
        ArrayList arrayList = new ArrayList();
        for (View view : viewArr) {
            if (view != null) {
                if (vuiPriority == null) {
                    arrayList.add(new TaskWrapper(view, str, view.getId(), TaskDispatcher.TaskType.ADD, str2));
                } else {
                    arrayList.add(new TaskWrapper(view, str, view.getId(), TaskDispatcher.TaskType.ADD, vuiPriority, iVuiSceneListener));
                }
            }
        }
        return arrayList;
    }

    public VuiScene createVuiScene(String str, long j) {
        return new VuiScene.Builder().sceneId(getSceneUnqiueId(str)).appVersion(this.mPackageVersion).packageName(this.mPackageName).timestamp(j).build();
    }

    public void setLoglevel(int i) {
        LogUtils.setLogLevel(i);
    }

    public void addVuiEventListener(final String str, final IVuiEventListener iVuiEventListener) {
        Handler handler;
        if (!VuiUtils.canUseVuiFeature() || (handler = this.mHandler) == null || str == null) {
            return;
        }
        handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.30
            @Override // java.lang.Runnable
            public void run() {
                LogUtils.logDebug(VuiEngineImpl.TAG, "addVuiEventListener :" + str);
                VuiSceneManager.instance().addVuiEventListener(VuiEngineImpl.this.getSceneUnqiueId(str), iVuiEventListener);
            }
        });
    }

    public void disableViewVuiMode() {
        LogUtils.logInfo(TAG, "user disable view's vui mode");
        VuiUtils.userDisableViewMode();
    }

    public void setExecuteVirtualTag(View view, String str) {
        if (!VuiUtils.canUseVuiFeature() || view == null) {
            return;
        }
        if (TextUtils.isEmpty(str)) {
            view.setTag(R.id.executeVirtualId, VuiConstants.VIRTUAL_LIST_ID);
        } else {
            view.setTag(R.id.executeVirtualId, "10000_" + str);
        }
    }

    public void setVirtualResourceNameTag(View view, String str) {
        if (!VuiUtils.canUseVuiFeature() || view == null || TextUtils.isEmpty(str)) {
            return;
        }
        view.setTag(R.id.virtualResourceName, str);
    }

    public void setCustomDoActionTag(View view) {
        if (!VuiUtils.canUseVuiFeature() || view == null) {
            return;
        }
        view.setTag(R.id.customDoAction, true);
    }

    public void setProcessName(String str) {
        VuiSceneManager.instance().setProcessName(str);
        VuiSceneToolManager.instance().setProcessName(str);
    }

    public void init(String str) {
        subscribe(this.mPackageName + "." + str);
    }

    public void addVuiSceneListener(String str, IVuiSceneListener iVuiSceneListener) {
        addVuiSceneListener(str, null, iVuiSceneListener, null, true);
    }

    public void updateDisplayLocation(final String str, final String str2) {
        if (VuiUtils.cannotUpload()) {
            return;
        }
        LogUtils.i(TAG, "updateDisplayLocation");
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(str2)) {
            return;
        }
        this.mHandler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.31
            @Override // java.lang.Runnable
            public void run() {
                String sceneUnqiueId = VuiEngineImpl.this.getSceneUnqiueId(str);
                VuiDisplayLocationInfoCache vuiDisplayLocationInfoCache = (VuiDisplayLocationInfoCache) VuiSceneCacheFactory.instance().getSceneCache(VuiSceneCacheFactory.CacheType.DISPLAY_LOCATION.getType());
                vuiDisplayLocationInfoCache.setCache(sceneUnqiueId, str2);
                if (VuiEngineImpl.this.mLFEnterSceneStack.contains(sceneUnqiueId) && VuiConstants.SCREEN_DISPLAY_RF.equals(str2)) {
                    VuiEngineImpl.this.enterScene(str, str2, true);
                }
                if (VuiEngineImpl.this.mRFEnterSceneStack.contains(sceneUnqiueId) && VuiConstants.SCREEN_DISPLAY_LF.equals(str2)) {
                    VuiEngineImpl.this.enterScene(str, str2, true);
                }
                if (!VuiSceneManager.instance().canUpdateScene(sceneUnqiueId)) {
                    vuiDisplayLocationInfoCache.removeDisplayCache(sceneUnqiueId);
                    return;
                }
                VuiScene newVuiScene = VuiUtils.getNewVuiScene(sceneUnqiueId, System.currentTimeMillis(), VuiEngineImpl.this.isAccessibilityScene, VuiEngineImpl.this.mPackageName, VuiEngineImpl.this.mPackageVersion);
                newVuiScene.setDisplayLocation(str2);
                LogUtils.i(VuiEngineImpl.TAG, "updateDisplayLocation ===    " + str2);
                VuiSceneManager.instance().sendSceneData(5, false, newVuiScene);
                VuiSceneToolManager.instance().sendSceneData(5, false, newVuiScene);
            }
        });
    }

    public void setBuildElements(String str) {
        if (VuiUtils.cannotUpload()) {
            return;
        }
        try {
            VuiScene stringConvertToVuiScene = VuiUtils.stringConvertToVuiScene(str);
            String sceneId = stringConvertToVuiScene.getSceneId();
            List<VuiElement> elements = stringConvertToVuiScene.getElements();
            if (elements == null) {
                elements = new ArrayList<>();
            }
            if (TextUtils.isEmpty(sceneId)) {
                return;
            }
            JSONObject jSONObject = new JSONObject(str);
            boolean z = jSONObject.getBoolean("isMainScene");
            JSONArray jSONArray = jSONObject.getJSONArray("subSceneIds");
            ArrayList arrayList = new ArrayList();
            for (int i = 0; i < jSONArray.length(); i++) {
                arrayList.add(getSceneUnqiueId(jSONArray.getString(i)));
            }
            LogUtils.logInfo(TAG, "sceneId:" + sceneId + "isMainScene:" + z + ",subSceneIds:" + arrayList);
            setBuildElements(stringConvertToVuiScene.getSceneId(), elements, z, arrayList);
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    public void setBuildElements(String str, List<VuiElement> list) {
        if (list == null) {
            list = new ArrayList<>();
        }
        setBuildElements(str, list, true, null);
    }

    public void setBuildElements(final String str, final List<VuiElement> list, final boolean z, final List<String> list2) {
        if (VuiUtils.cannotUpload() || TextUtils.isEmpty(str)) {
            return;
        }
        this.mHandler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.32
            @Override // java.lang.Runnable
            public void run() {
                int i;
                try {
                    String sceneUnqiueId = VuiEngineImpl.this.getSceneUnqiueId(str);
                    int vuiSceneState = VuiSceneManager.instance().getVuiSceneState(sceneUnqiueId);
                    if (vuiSceneState == VuiSceneState.INIT.getState()) {
                        LogUtils.e(VuiEngineImpl.TAG, str + "场景数据的创建必须在场景被激活后");
                        return;
                    }
                    long currentTimeMillis = System.currentTimeMillis();
                    VuiScene newVuiScene = VuiUtils.getNewVuiScene(sceneUnqiueId, currentTimeMillis, VuiEngineImpl.this.isAccessibilityScene, VuiEngineImpl.this.mPackageName, VuiEngineImpl.this.mPackageVersion);
                    newVuiScene.setElements(list);
                    VuiSceneBuildCache vuiSceneBuildCache = (VuiSceneBuildCache) VuiSceneCacheFactory.instance().getSceneCache(VuiSceneCacheFactory.CacheType.BUILD.getType());
                    if (vuiSceneBuildCache != null) {
                        vuiSceneBuildCache.setCache(sceneUnqiueId, list);
                        vuiSceneBuildCache.setDisplayLocation(sceneUnqiueId, VuiUtils.getDisplayLocation(newVuiScene.getDisplayLocation()));
                    }
                    VuiSceneBuildTestCache vuiSceneBuildTestCache = (VuiSceneBuildTestCache) VuiSceneTestCacheFactory.instance().getSceneCache(VuiSceneCacheFactory.CacheType.BUILD.getType());
                    if (vuiSceneBuildTestCache != null) {
                        vuiSceneBuildTestCache.setCache(sceneUnqiueId, list);
                        vuiSceneBuildTestCache.setDisplayLocation(sceneUnqiueId, VuiUtils.getDisplayLocation(newVuiScene.getDisplayLocation()));
                    }
                    VuiSceneInfo sceneInfo = VuiSceneManager.instance().getSceneInfo(sceneUnqiueId);
                    if (sceneInfo != null && !z) {
                        sceneInfo.setBuild(true);
                    }
                    if (z) {
                        List list3 = list2;
                        if (list3 == null || list3.isEmpty()) {
                            VuiEngineImpl.this.setMainSceneBuildElements(sceneInfo, newVuiScene);
                        } else {
                            for (int i2 = 0; i2 < list2.size(); i2++) {
                                String str2 = (String) list2.get(i2);
                                if (VuiSceneManager.instance().getVuiSceneListener(str2) == null) {
                                    if (sceneInfo != null) {
                                        sceneInfo.updateAddSubSceneNum();
                                    }
                                    VuiSceneManager.instance().initSubSceneInfo(str2, sceneUnqiueId);
                                } else {
                                    if (vuiSceneBuildCache != null) {
                                        List<VuiElement> cache = vuiSceneBuildCache.getCache(str2);
                                        LogUtils.logDebug(VuiEngineImpl.TAG, "buildElement:" + cache);
                                        if (cache != null && !cache.isEmpty()) {
                                            if (sceneInfo != null) {
                                                sceneInfo.updateAddSubSceneNum();
                                            }
                                            list.addAll(cache);
                                        } else {
                                            VuiSceneInfo sceneInfo2 = VuiSceneManager.instance().getSceneInfo(str2);
                                            if (sceneInfo2 != null && sceneInfo != null) {
                                                if (sceneInfo2.isBuild()) {
                                                    sceneInfo.updateAddSubSceneNum();
                                                } else {
                                                    sceneInfo2.getState();
                                                    if (vuiSceneState == VuiSceneState.ACTIVE.getState()) {
                                                        LogUtils.logInfo(VuiEngineImpl.TAG, "build subScene");
                                                        IVuiSceneListener listener = sceneInfo2.getListener();
                                                        if (listener != null) {
                                                            listener.onBuildScene();
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    VuiSceneManager.instance().setWholeSceneId(str2, sceneUnqiueId);
                                }
                            }
                            VuiSceneManager.instance().addSubSceneIds(sceneUnqiueId, list2);
                            if (sceneInfo != null && sceneInfo.isFull()) {
                                VuiEngineImpl.this.setMainSceneBuildElements(sceneInfo, newVuiScene);
                            }
                        }
                    } else {
                        List<String> wholeSceneId = sceneInfo != null ? sceneInfo.getWholeSceneId() : null;
                        if (wholeSceneId != null) {
                            int size = wholeSceneId.size();
                            for (int i3 = 0; i3 < size; i3 = i + 1) {
                                String str3 = wholeSceneId.get(i3);
                                VuiSceneInfo sceneInfo3 = VuiSceneManager.instance().getSceneInfo(str3);
                                if (sceneInfo3 != null && sceneInfo3.isFull()) {
                                    i = i3;
                                    VuiScene newVuiScene2 = VuiUtils.getNewVuiScene(str3, currentTimeMillis, VuiEngineImpl.this.isAccessibilityScene, VuiEngineImpl.this.mPackageName, VuiEngineImpl.this.mPackageVersion);
                                    newVuiScene2.setElements(list);
                                    sceneInfo3.setBuildComplete(true);
                                    sceneInfo3.setBuild(true);
                                    VuiEngineImpl.this.setSubSceneElementToCache(str3, vuiSceneBuildCache, list);
                                    VuiSceneManager.instance().sendSceneData(1, true, newVuiScene2);
                                    VuiSceneToolManager.instance().sendSceneData(1, true, newVuiScene2);
                                } else {
                                    i = i3;
                                    if (sceneInfo3 != null && !sceneInfo3.isFull()) {
                                        sceneInfo3.updateAddSubSceneNum();
                                        List<VuiElement> subSceneElementToCache = VuiEngineImpl.this.setSubSceneElementToCache(str3, vuiSceneBuildCache, list);
                                        if (sceneInfo3.isFull()) {
                                            sceneInfo3.setBuildComplete(true);
                                            sceneInfo3.setBuild(true);
                                            LogUtils.logInfo(VuiEngineImpl.TAG, str3 + " full scene build completed ");
                                            if (VuiUtils.isActiveSceneId(str3)) {
                                                VuiScene newVuiScene3 = VuiUtils.getNewVuiScene(str3, currentTimeMillis, VuiEngineImpl.this.isAccessibilityScene, VuiEngineImpl.this.mPackageName, VuiEngineImpl.this.mPackageVersion);
                                                newVuiScene3.setElements(subSceneElementToCache);
                                                VuiSceneManager.instance().sendSceneData(0, false, newVuiScene3);
                                                VuiSceneToolManager.instance().sendSceneData(0, false, newVuiScene3);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    VuiSceneManager.instance().setIsWholeScene(sceneUnqiueId, z);
                } catch (Throwable unused) {
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public List<VuiElement> setSubSceneElementToCache(String str, VuiSceneCache vuiSceneCache, List<VuiElement> list) {
        List<VuiElement> cache = vuiSceneCache.getCache(str);
        if (cache == null) {
            cache = new ArrayList<>();
        }
        if (cache.contains(list.get(0))) {
            return cache;
        }
        List<VuiElement> merge = SceneMergeUtils.merge(cache, list, false);
        vuiSceneCache.setCache(str, merge);
        if (!"user".equals(Build.TYPE) && LogUtils.getLogLevel() <= LogUtils.LOG_DEBUG_LEVEL && !VuiUtils.isActiveSceneId(str)) {
            VuiScene newVuiScene = VuiUtils.getNewVuiScene(str, System.currentTimeMillis(), this.isAccessibilityScene, this.mPackageName, this.mPackageVersion);
            newVuiScene.setElements(merge);
            LogUtils.logDebug(TAG, "buildScene full_scene_info:" + VuiUtils.vuiSceneConvertToString(newVuiScene));
        }
        return merge;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setMainSceneBuildElements(VuiSceneInfo vuiSceneInfo, VuiScene vuiScene) {
        if (!"user".equals(Build.TYPE) && LogUtils.getLogLevel() <= LogUtils.LOG_DEBUG_LEVEL) {
            LogUtils.logDebug(TAG, "BuildSceneTask full_scene_info" + VuiUtils.vuiSceneConvertToString(vuiScene));
        }
        if (vuiSceneInfo != null) {
            vuiSceneInfo.setBuild(true);
            vuiSceneInfo.setBuildComplete(true);
        }
        VuiSceneManager.instance().sendSceneData(0, false, vuiScene);
        VuiSceneToolManager.instance().sendSceneData(0, false, vuiScene);
    }

    public void setUpdateElements(String str, List<VuiElement> list) {
        setUpdateElements(str, list, VuiConstants.SCREEN_DISPLAY_LF);
    }

    public void setUpdateElements(final String str, final String str2, int i) {
        if (VuiUtils.cannotUpload() || TextUtils.isEmpty(str) || TextUtils.isEmpty(str2)) {
            return;
        }
        this.mHandler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.33
            /* JADX WARN: Multi-variable type inference failed */
            /* JADX WARN: Type inference failed for: r2v11 */
            /* JADX WARN: Type inference failed for: r2v12, types: [boolean, int] */
            /* JADX WARN: Type inference failed for: r2v13 */
            @Override // java.lang.Runnable
            public void run() {
                ?? r2;
                VuiScene newVuiScene;
                try {
                    LogUtils.d(VuiEngineImpl.TAG, "sceneId:" + str + ",data:" + str2);
                    VuiElement stringConvertToVuiElement = VuiUtils.stringConvertToVuiElement(str2);
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(stringConvertToVuiElement);
                    String sceneUnqiueId = VuiEngineImpl.this.getSceneUnqiueId(str);
                    if (VuiSceneManager.instance().canUpdateScene(sceneUnqiueId)) {
                        long currentTimeMillis = System.currentTimeMillis();
                        VuiSceneInfo sceneInfo = VuiSceneManager.instance().getSceneInfo(sceneUnqiueId);
                        if (sceneInfo == null) {
                            return;
                        }
                        VuiScene newVuiScene2 = VuiUtils.getNewVuiScene(sceneUnqiueId, currentTimeMillis, VuiEngineImpl.this.isAccessibilityScene, VuiEngineImpl.this.mPackageName, VuiEngineImpl.this.mPackageVersion);
                        VuiSceneCache sceneCache = VuiSceneCacheFactory.instance().getSceneCache(VuiSceneCacheFactory.CacheType.BUILD.getType());
                        if (sceneCache != null) {
                            ArrayList arrayList2 = new ArrayList();
                            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                                VuiElement vuiElement = (VuiElement) arrayList.get(i2);
                                VuiElement vuiElementById = sceneCache.getVuiElementById(sceneUnqiueId, vuiElement.getId());
                                if (vuiElementById == null || !vuiElementById.equals(vuiElement)) {
                                    if (vuiElement.getElements() == null && vuiElementById != null && vuiElementById.getElements() != null) {
                                        vuiElement.setElements(vuiElementById.getElements());
                                    }
                                    arrayList2.add(vuiElement);
                                }
                            }
                            if (arrayList2.isEmpty()) {
                                return;
                            }
                            newVuiScene2.setElements(arrayList2);
                            List<VuiElement> fusionCache = sceneCache.getFusionCache(sceneUnqiueId, arrayList2, false);
                            sceneCache.setCache(sceneUnqiueId, fusionCache);
                            if (sceneInfo.isWholeScene()) {
                                if ("user".equals(Build.TYPE) || LogUtils.getLogLevel() > LogUtils.LOG_DEBUG_LEVEL) {
                                    r2 = 1;
                                } else {
                                    r2 = 1;
                                    VuiUtils.getNewVuiScene(sceneUnqiueId, currentTimeMillis, VuiEngineImpl.this.isAccessibilityScene, VuiEngineImpl.this.mPackageName, VuiEngineImpl.this.mPackageVersion).setElements(fusionCache);
                                    LogUtils.logDebug(VuiEngineImpl.TAG, "updateSceneTask full_scene_info" + VuiUtils.vuiSceneConvertToString(newVuiScene));
                                }
                                VuiSceneManager.instance().sendSceneData(r2, r2, newVuiScene2);
                                VuiSceneToolManager.instance().sendSceneData(r2, r2, newVuiScene2);
                                return;
                            }
                            if (sceneCache != null) {
                                sceneCache.setCache(sceneUnqiueId, sceneCache.getFusionCache(sceneUnqiueId, arrayList, false));
                            }
                            List<String> wholeSceneId = sceneInfo.getWholeSceneId();
                            int size = wholeSceneId == null ? 0 : wholeSceneId.size();
                            if (size > 0) {
                                String activeWholeSceneId = VuiEngineImpl.this.getActiveWholeSceneId(wholeSceneId);
                                if (!TextUtils.isEmpty(activeWholeSceneId)) {
                                    LogUtils.logDebug(VuiEngineImpl.TAG, "updateScene wholeSceneIds:" + wholeSceneId);
                                    VuiScene newVuiScene3 = VuiUtils.getNewVuiScene(activeWholeSceneId, currentTimeMillis, VuiEngineImpl.this.isAccessibilityScene, VuiEngineImpl.this.mPackageName, VuiEngineImpl.this.mPackageVersion);
                                    newVuiScene3.setElements(arrayList);
                                    VuiEngineImpl.this.setWholeSceneCache(activeWholeSceneId, sceneCache, arrayList);
                                    VuiSceneManager.instance().sendSceneData(1, true, newVuiScene3);
                                    VuiSceneToolManager.instance().sendSceneData(1, true, newVuiScene3);
                                }
                                for (int i3 = 0; i3 < size; i3++) {
                                    String str3 = wholeSceneId.get(i3);
                                    if (!TextUtils.isEmpty(str3) && !VuiUtils.isActiveSceneId(str3)) {
                                        VuiScene newVuiScene4 = VuiUtils.getNewVuiScene(str3, currentTimeMillis, VuiEngineImpl.this.isAccessibilityScene, VuiEngineImpl.this.mPackageName, VuiEngineImpl.this.mPackageVersion);
                                        newVuiScene4.setElements(arrayList);
                                        VuiEngineImpl.this.setWholeSceneCache(str3, sceneCache, arrayList);
                                        VuiSceneManager.instance().sendSceneData(1, true, newVuiScene4);
                                        VuiSceneToolManager.instance().sendSceneData(1, true, newVuiScene4);
                                    }
                                }
                            }
                        }
                    }
                } catch (Throwable th) {
                    th.printStackTrace();
                }
            }
        });
    }

    public void setUpdateElements(final String str, final List<VuiElement> list, final String str2) {
        if (VuiUtils.cannotUpload() || TextUtils.isEmpty(str) || list == null || list.isEmpty()) {
            return;
        }
        this.mHandler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.34
            /* JADX WARN: Multi-variable type inference failed */
            /* JADX WARN: Type inference failed for: r2v4 */
            /* JADX WARN: Type inference failed for: r2v5, types: [boolean, int] */
            /* JADX WARN: Type inference failed for: r2v6 */
            @Override // java.lang.Runnable
            public void run() {
                ?? r2;
                VuiScene newVuiScene;
                try {
                    String sceneUnqiueId = VuiEngineImpl.this.getSceneUnqiueId(str);
                    if (VuiSceneManager.instance().canUpdateScene(sceneUnqiueId)) {
                        long currentTimeMillis = System.currentTimeMillis();
                        VuiSceneInfo sceneInfo = VuiSceneManager.instance().getSceneInfo(sceneUnqiueId);
                        if (sceneInfo == null) {
                            return;
                        }
                        VuiScene newVuiScene2 = VuiUtils.getNewVuiScene(sceneUnqiueId, currentTimeMillis, VuiEngineImpl.this.isAccessibilityScene, VuiEngineImpl.this.mPackageName, VuiEngineImpl.this.mPackageVersion);
                        VuiSceneCache sceneCache = VuiSceneCacheFactory.instance().getSceneCache(VuiSceneCacheFactory.CacheType.BUILD.getType());
                        if (sceneCache != null) {
                            ArrayList arrayList = new ArrayList();
                            for (int i = 0; i < list.size(); i++) {
                                VuiElement vuiElement = (VuiElement) list.get(i);
                                VuiElement vuiElementById = sceneCache.getVuiElementById(sceneUnqiueId, vuiElement.getId());
                                if (vuiElementById == null || !vuiElementById.equals(vuiElement)) {
                                    if (vuiElement.getElements() == null && vuiElementById != null && vuiElementById.getElements() != null) {
                                        vuiElement.setElements(vuiElementById.getElements());
                                    }
                                    arrayList.add(vuiElement);
                                }
                            }
                            if (arrayList.isEmpty()) {
                                return;
                            }
                            newVuiScene2.setElements(arrayList);
                            newVuiScene2.setDisplayLocation(str2);
                            List<VuiElement> fusionCache = sceneCache.getFusionCache(sceneUnqiueId, arrayList, false);
                            sceneCache.setCache(sceneUnqiueId, fusionCache);
                            if (sceneInfo.isWholeScene()) {
                                if ("user".equals(Build.TYPE) || LogUtils.getLogLevel() > LogUtils.LOG_DEBUG_LEVEL) {
                                    r2 = 1;
                                } else {
                                    r2 = 1;
                                    VuiUtils.getNewVuiScene(sceneUnqiueId, currentTimeMillis, VuiEngineImpl.this.isAccessibilityScene, VuiEngineImpl.this.mPackageName, VuiEngineImpl.this.mPackageVersion).setElements(fusionCache);
                                    newVuiScene2.setDisplayLocation(str2);
                                    LogUtils.logDebug(VuiEngineImpl.TAG, "updateSceneTask full_scene_info" + VuiUtils.vuiSceneConvertToString(newVuiScene));
                                }
                                VuiSceneManager.instance().sendSceneData(r2, r2, newVuiScene2);
                                VuiSceneToolManager.instance().sendSceneData(r2, r2, newVuiScene2);
                                return;
                            }
                            if (sceneCache != null) {
                                sceneCache.setCache(sceneUnqiueId, sceneCache.getFusionCache(sceneUnqiueId, list, false));
                            }
                            List<String> wholeSceneId = sceneInfo.getWholeSceneId();
                            int size = wholeSceneId == null ? 0 : wholeSceneId.size();
                            if (size > 0) {
                                String activeWholeSceneId = VuiEngineImpl.this.getActiveWholeSceneId(wholeSceneId);
                                if (!TextUtils.isEmpty(activeWholeSceneId)) {
                                    LogUtils.logDebug(VuiEngineImpl.TAG, "updateScene wholeSceneIds:" + wholeSceneId);
                                    VuiScene newVuiScene3 = VuiUtils.getNewVuiScene(activeWholeSceneId, currentTimeMillis, VuiEngineImpl.this.isAccessibilityScene, VuiEngineImpl.this.mPackageName, VuiEngineImpl.this.mPackageVersion);
                                    newVuiScene3.setElements(list);
                                    newVuiScene3.setDisplayLocation(str2);
                                    VuiEngineImpl.this.setWholeSceneCache(activeWholeSceneId, sceneCache, list);
                                    VuiSceneManager.instance().sendSceneData(1, true, newVuiScene3);
                                    VuiSceneToolManager.instance().sendSceneData(1, true, newVuiScene3);
                                }
                                for (int i2 = 0; i2 < size; i2++) {
                                    String str3 = wholeSceneId.get(i2);
                                    if (!TextUtils.isEmpty(str3) && !VuiUtils.isActiveSceneId(str3)) {
                                        VuiScene newVuiScene4 = VuiUtils.getNewVuiScene(str3, currentTimeMillis, VuiEngineImpl.this.isAccessibilityScene, VuiEngineImpl.this.mPackageName, VuiEngineImpl.this.mPackageVersion);
                                        newVuiScene4.setElements(list);
                                        newVuiScene4.setDisplayLocation(str2);
                                        VuiEngineImpl.this.setWholeSceneCache(str3, sceneCache, list);
                                        VuiSceneManager.instance().sendSceneData(1, true, newVuiScene4);
                                        VuiSceneToolManager.instance().sendSceneData(1, true, newVuiScene4);
                                    }
                                }
                            }
                        }
                    }
                } catch (Throwable unused) {
                    LogUtils.e(VuiEngineImpl.TAG, "setUpdateElements exception");
                }
            }
        });
    }

    public String getActiveWholeSceneId(List<String> list) {
        if (list == null) {
            return "";
        }
        ConcurrentMap<String, String> concurrentMap = mActiveSceneIds;
        String str = concurrentMap.get(VuiConstants.SCREEN_DISPLAY_LF);
        String str2 = concurrentMap.get(VuiConstants.SCREEN_DISPLAY_RF);
        return (TextUtils.isEmpty(str) || !list.contains(str)) ? (TextUtils.isEmpty(str2) || !list.contains(str2)) ? "" : str2 : str;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setWholeSceneCache(String str, VuiSceneCache vuiSceneCache, List<VuiElement> list) {
        if (vuiSceneCache != null) {
            List<VuiElement> fusionCache = vuiSceneCache.getFusionCache(str, list, false);
            vuiSceneCache.setCache(str, fusionCache);
            if ("user".equals(Build.TYPE) || LogUtils.getLogLevel() > LogUtils.LOG_DEBUG_LEVEL) {
                return;
            }
            VuiScene newVuiScene = VuiUtils.getNewVuiScene(str, System.currentTimeMillis(), this.isAccessibilityScene, this.mPackageName, this.mPackageVersion);
            newVuiScene.setElements(fusionCache);
            LogUtils.logDebug(TAG, "updateSceneTask full_scene_info" + VuiUtils.vuiSceneConvertToString(newVuiScene));
        }
    }

    public VuiElement getVuiElement(String str, String str2) {
        VuiSceneCache sceneCache;
        if (VuiUtils.cannotUpload() || TextUtils.isEmpty(str2) || TextUtils.isEmpty(str) || (sceneCache = VuiSceneCacheFactory.instance().getSceneCache(VuiSceneCacheFactory.CacheType.BUILD.getType())) == null) {
            return null;
        }
        return sceneCache.getVuiElementById(getSceneUnqiueId(str), str2);
    }

    public VuiScene getVuiScene(String str) {
        List<VuiElement> cache;
        if (VuiUtils.cannotUpload() || TextUtils.isEmpty(str)) {
            return null;
        }
        VuiScene createVuiScene = createVuiScene(str, System.currentTimeMillis());
        VuiSceneCache sceneCache = VuiSceneCacheFactory.instance().getSceneCache(VuiSceneCacheFactory.CacheType.BUILD.getType());
        if (sceneCache != null && (cache = sceneCache.getCache(createVuiScene.getSceneId())) != null && cache.isEmpty()) {
            createVuiScene.setElements(cache);
        }
        return createVuiScene;
    }

    public boolean isSpeechShowNumber() {
        if (VuiUtils.cannotUpload()) {
            return false;
        }
        return VuiSceneManager.instance().isInSpeech();
    }

    public Map<String, String> getActiveSceneId() {
        return mActiveSceneIds;
    }
}

package com.xiaopeng.xui.vui;

import android.text.TextUtils;
import android.view.View;
import androidx.lifecycle.Lifecycle;
import com.xiaopeng.vui.commons.IVuiElementChangedListener;
import com.xiaopeng.vui.commons.IVuiSceneListener;
import com.xiaopeng.vui.commons.VuiUpdateType;
import com.xiaopeng.vui.commons.model.VuiEvent;
import com.xiaopeng.xui.Xui;
import com.xiaopeng.xui.vui.floatinglayer.VuiFloatingLayerManager;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes2.dex */
public class VuiSceneHelper implements IVuiSceneListener {
    private View mRootView;
    private String mSceneId;
    private IVuiEventInterceptListener mVuiEventInterceptListener;
    private List<View> mBuildViewList = null;
    private boolean isMainScene = true;
    private List<String> mSubSceneList = null;
    private boolean isCustomBuildScene = false;
    public IVuiElementChangedListener mListener = new IVuiElementChangedListener() { // from class: com.xiaopeng.xui.vui.-$$Lambda$VuiSceneHelper$lT7VdXu2172wJnBQbvufgRIKbZk
        @Override // com.xiaopeng.vui.commons.IVuiElementChangedListener
        public final void onVuiElementChaned(View view, VuiUpdateType vuiUpdateType) {
            VuiSceneHelper.this.lambda$new$0$VuiSceneHelper(view, vuiUpdateType);
        }
    };

    public void setVuiEventInterceptListener(IVuiEventInterceptListener iVuiEventInterceptListener) {
        this.mVuiEventInterceptListener = iVuiEventInterceptListener;
    }

    private String getSceneId() {
        return this.mSceneId;
    }

    private List<View> getBuildViews() {
        if (this.mBuildViewList == null) {
            ArrayList arrayList = new ArrayList();
            this.mBuildViewList = arrayList;
            arrayList.add(this.mRootView);
        }
        return this.mBuildViewList;
    }

    @Override // com.xiaopeng.vui.commons.IVuiSceneListener
    public boolean onInterceptVuiEvent(View view, VuiEvent vuiEvent) {
        IVuiEventInterceptListener iVuiEventInterceptListener = this.mVuiEventInterceptListener;
        if (iVuiEventInterceptListener != null) {
            return iVuiEventInterceptListener.onInterceptVuiEvent(view, vuiEvent);
        }
        if (view != null) {
            VuiFloatingLayerManager.show(view);
            return false;
        }
        return false;
    }

    @Override // com.xiaopeng.vui.commons.IVuiSceneListener
    public void onVuiEvent(View view, VuiEvent vuiEvent) {
        IVuiEventInterceptListener iVuiEventInterceptListener = this.mVuiEventInterceptListener;
        if (iVuiEventInterceptListener != null) {
            iVuiEventInterceptListener.onVuiEvent(view, vuiEvent);
        } else if (view != null) {
            VuiFloatingLayerManager.show(view);
        }
    }

    public void init(Lifecycle lifecycle, String str, View view) {
        this.mSceneId = str;
        this.mRootView = view;
        if (!Xui.isVuiEnable() || Xui.getVuiEngine() == null) {
            return;
        }
        Xui.getVuiEngine().initScene(lifecycle, str, view, this, this.mListener);
    }

    public void buildScene() {
        if (TextUtils.isEmpty(getSceneId()) || !Xui.isVuiEnable() || Xui.getVuiEngine() == null) {
            return;
        }
        Xui.getVuiEngine().buildScene(getSceneId(), getBuildViews(), this.mSubSceneList, this.isMainScene);
    }

    public /* synthetic */ void lambda$new$0$VuiSceneHelper(View view, VuiUpdateType vuiUpdateType) {
        if (VuiUpdateType.UPDATE_VIEW.equals(vuiUpdateType) && Xui.getVuiEngine() != null) {
            Xui.getVuiEngine().updateScene(getSceneId(), view);
        } else {
            Xui.getVuiEngine().updateElementAttribute(getSceneId(), view);
        }
    }

    public void setBuildViewList(List<View> list) {
        this.mBuildViewList = list;
    }

    public void onDestroy() {
        List<View> list = this.mBuildViewList;
        if (list != null) {
            list.clear();
        }
        this.mBuildViewList = null;
        this.mRootView = null;
        this.mSceneId = null;
        List<String> list2 = this.mSubSceneList;
        if (list2 != null) {
            list2.clear();
        }
        this.mSubSceneList = null;
    }

    public void setMainScene(boolean z) {
        this.isMainScene = z;
    }

    @Override // com.xiaopeng.vui.commons.IVuiSceneListener
    public void onBuildScene() {
        if (isCustomBuildScene()) {
            return;
        }
        buildScene();
    }

    @Override // com.xiaopeng.vui.commons.IVuiSceneListener
    public void onVuiStateChanged() {
        IVuiEventInterceptListener iVuiEventInterceptListener = this.mVuiEventInterceptListener;
        if (iVuiEventInterceptListener != null) {
            iVuiEventInterceptListener.onVuiStateChanged();
        }
    }

    public void setSubSceneIds(List<String> list) {
        this.mSubSceneList = list;
    }

    public void setCustomBuildScene(boolean z) {
        this.isCustomBuildScene = z;
    }

    private boolean isCustomBuildScene() {
        return this.isCustomBuildScene;
    }

    /* loaded from: classes2.dex */
    public interface IVuiEventInterceptListener {
        default void onVuiStateChanged() {
        }

        default void onVuiEvent(View view, VuiEvent vuiEvent) {
            if (view != null) {
                VuiFloatingLayerManager.show(view);
            }
        }

        default boolean onInterceptVuiEvent(View view, VuiEvent vuiEvent) {
            if (view != null) {
                VuiFloatingLayerManager.show(view);
                return false;
            }
            return false;
        }
    }
}

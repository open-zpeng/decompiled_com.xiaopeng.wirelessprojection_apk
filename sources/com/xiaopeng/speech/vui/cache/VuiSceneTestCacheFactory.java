package com.xiaopeng.speech.vui.cache;
/* loaded from: classes2.dex */
public class VuiSceneTestCacheFactory {
    private static final String TAG = "VuiSceneTestCacheFactory";
    private VuiSceneBuildTestCache mBuildCache;
    private VuiDisplayLocationInfoTestCache mDisplayLocationCache;
    private VuiSceneRemoveTestCache mRemoveCache;
    private VuiSceneTestCache mSceneCache;
    private VuiSceneUpdateTestCache mUpdateCache;

    /* loaded from: classes2.dex */
    public enum CacheType {
        BUILD(0),
        UPDATE(1),
        ADD(2),
        REMOVE(3),
        DEFAULT(4),
        DISPLAY_LOCATION(5);
        
        private int type;

        CacheType(int i) {
            this.type = i;
        }

        public int getType() {
            return this.type;
        }
    }

    private VuiSceneTestCacheFactory() {
        this.mBuildCache = null;
        this.mUpdateCache = null;
        this.mRemoveCache = null;
        this.mSceneCache = null;
    }

    public static final VuiSceneTestCacheFactory instance() {
        return Holder.Instance;
    }

    /* loaded from: classes2.dex */
    private static class Holder {
        private static final VuiSceneTestCacheFactory Instance = new VuiSceneTestCacheFactory();

        private Holder() {
        }
    }

    public VuiSceneTestCache getSceneCache(int i) {
        if (i == CacheType.BUILD.getType()) {
            if (this.mBuildCache == null) {
                this.mBuildCache = new VuiSceneBuildTestCache();
            }
            return this.mBuildCache;
        } else if (i == CacheType.UPDATE.getType()) {
            if (this.mUpdateCache == null) {
                this.mUpdateCache = new VuiSceneUpdateTestCache();
            }
            return this.mUpdateCache;
        } else if (i == CacheType.REMOVE.getType()) {
            if (this.mRemoveCache == null) {
                this.mRemoveCache = new VuiSceneRemoveTestCache();
            }
            return this.mRemoveCache;
        } else if (i == CacheType.DISPLAY_LOCATION.getType()) {
            if (this.mDisplayLocationCache == null) {
                this.mDisplayLocationCache = new VuiDisplayLocationInfoTestCache();
            }
            return this.mDisplayLocationCache;
        } else {
            if (this.mSceneCache == null) {
                this.mSceneCache = new VuiSceneTestCache();
            }
            return this.mSceneCache;
        }
    }

    public void removeAllCache(String str) {
        if (str == null) {
            return;
        }
        VuiSceneBuildTestCache vuiSceneBuildTestCache = this.mBuildCache;
        if (vuiSceneBuildTestCache != null) {
            vuiSceneBuildTestCache.removeCache(str);
            this.mBuildCache.removeUploadState(str);
            this.mBuildCache.removeDisplayLocation(str);
        }
        VuiSceneUpdateTestCache vuiSceneUpdateTestCache = this.mUpdateCache;
        if (vuiSceneUpdateTestCache != null) {
            vuiSceneUpdateTestCache.removeCache(str);
        }
        VuiSceneRemoveTestCache vuiSceneRemoveTestCache = this.mRemoveCache;
        if (vuiSceneRemoveTestCache != null) {
            vuiSceneRemoveTestCache.removeCache(str);
        }
        VuiDisplayLocationInfoTestCache vuiDisplayLocationInfoTestCache = this.mDisplayLocationCache;
        if (vuiDisplayLocationInfoTestCache != null) {
            vuiDisplayLocationInfoTestCache.removeDisplayCache(str);
        }
    }

    public void removeOtherCache(String str) {
        if (str == null) {
            return;
        }
        VuiSceneUpdateTestCache vuiSceneUpdateTestCache = this.mUpdateCache;
        if (vuiSceneUpdateTestCache != null) {
            vuiSceneUpdateTestCache.removeCache(str);
        }
        VuiSceneRemoveTestCache vuiSceneRemoveTestCache = this.mRemoveCache;
        if (vuiSceneRemoveTestCache != null) {
            vuiSceneRemoveTestCache.removeCache(str);
        }
        VuiDisplayLocationInfoTestCache vuiDisplayLocationInfoTestCache = this.mDisplayLocationCache;
        if (vuiDisplayLocationInfoTestCache != null) {
            vuiDisplayLocationInfoTestCache.removeDisplayCache(str);
        }
    }
}

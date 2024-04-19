package com.xiaopeng.wirelessprojection.core.manager;

import com.xiaopeng.lib.utils.LogUtils;
import com.xiaopeng.wirelessprojection.core.BaseActivity;
import com.xiaopeng.wirelessprojection.core.R;
import com.xiaopeng.wirelessprojection.core.event.AppGoBackgroundEvent;
import com.xiaopeng.wirelessprojection.core.utils.AppUtils;
import com.xiaopeng.wirelessprojection.core.utils.CarHardwareHelper;
import com.xiaopeng.wirelessprojection.core.utils.EventBusUtils;
import com.xiaopeng.wirelessprojection.core.utils.ScreenUtils;
import com.xiaopeng.wirelessprojection.core.utils.ToastUtils;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes2.dex */
public class ActivityManager {
    private static final String TAG = "ActivityManager";
    private static List<BaseActivity> activityStack = new ArrayList();

    /* loaded from: classes2.dex */
    private static class Holder {
        private static final ActivityManager Instance = new ActivityManager();

        private Holder() {
        }
    }

    public static final ActivityManager instance() {
        return Holder.Instance;
    }

    private ActivityManager() {
    }

    public void addActivity(BaseActivity baseActivity) {
        if (activityStack == null) {
            activityStack = new ArrayList();
        }
        LogUtils.i(TAG, "addActivity name=" + baseActivity.getLocalClassName());
        activityStack.add(baseActivity);
    }

    public int getActivitySize() {
        List<BaseActivity> list = activityStack;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public void removeActivity(BaseActivity baseActivity) {
        if (baseActivity != null) {
            LogUtils.i(TAG, "removeActivity name=" + baseActivity.getLocalClassName());
            activityStack.remove(baseActivity);
            reportAppBackground();
        }
    }

    public void finishAllActivity(String str) {
        LogUtils.i(TAG, "finishAllActivity from=" + str);
        List<BaseActivity> list = activityStack;
        if (list != null) {
            if (list.size() > 0 && str.equals("GEAR")) {
                if (CarHardwareHelper.instance().isCarH93()) {
                    ToastUtils.showToast(R.string.toast_gear_limit_h93, ScreenUtils.getScreenId());
                } else {
                    ToastUtils.showToast(R.string.toast_gear_limit);
                }
            }
            int size = activityStack.size();
            for (int i = 0; i < size; i++) {
                if (activityStack.get(i) != null) {
                    LogUtils.i(TAG, "finishAllActivity index=" + i + ", name=" + activityStack.get(i).getLocalClassName());
                    activityStack.get(i).finish();
                }
            }
            activityStack.clear();
            reportAppBackground();
        }
    }

    private void reportAppBackground() {
        LogUtils.i(TAG, "reportAppBackground activityStack size=" + activityStack.size());
        List<BaseActivity> list = activityStack;
        if ((list == null || list.size() == 0) && AppUtils.isAllowUseApp()) {
            EventBusUtils.post(new AppGoBackgroundEvent());
        }
    }
}

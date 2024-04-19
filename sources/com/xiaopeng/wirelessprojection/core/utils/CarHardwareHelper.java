package com.xiaopeng.wirelessprojection.core.utils;

import android.car.Car;
import com.xiaopeng.lib.framework.carcontrollermodule.CarControllerModuleEntry;
import com.xiaopeng.lib.framework.module.Module;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.IEventMsg;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.ILifeCycle;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.v2extend.IMcuController;
import com.xiaopeng.lib.utils.LogUtils;
import com.xiaopeng.wirelessprojection.core.event.GearLevelEvent;
import com.xiaopeng.wirelessprojection.core.event.IgStatusOffEvent;
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
/* loaded from: classes2.dex */
public class CarHardwareHelper {
    private static final String TAG = "CarHardwareHelper";
    private static int sLastGear = -1;
    private boolean mIsServiceConnected;

    /* loaded from: classes2.dex */
    private static class Holder {
        private static final CarHardwareHelper Instance = new CarHardwareHelper();

        private Holder() {
        }
    }

    public static final CarHardwareHelper instance() {
        return Holder.Instance;
    }

    private CarHardwareHelper() {
        this.mIsServiceConnected = false;
    }

    public void init() {
        ThreadUtils.postWorker(new Runnable() { // from class: com.xiaopeng.wirelessprojection.core.utils.-$$Lambda$CarHardwareHelper$2hEvlW7oAHvvGvMIOLYIYp24IME
            @Override // java.lang.Runnable
            public final void run() {
                CarHardwareHelper.this.initImmediate();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initImmediate() {
        registerVcu();
        registMcu();
        sLastGear = getLevel();
        EventBusUtils.registerSafely(this);
        setServiceConnected(true);
    }

    private void registerVcu() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(IVcuController.CarGearLevelEventMsg.class);
        registerCanEventMsg(IVcuController.class, arrayList);
    }

    private void registMcu() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(IMcuController.McuIgStatusEventMsg.class);
        registerCanEventMsg(com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController.class, arrayList);
    }

    public <T extends ILifeCycle> void registerCanEventMsg(Class<T> cls, List<Class<? extends IEventMsg>> list) {
        try {
            ((ILifeCycle) Module.get(CarControllerModuleEntry.class).get(cls)).registerCanEventMsg(list);
        } catch (Throwable th) {
            LogUtils.e(TAG, "registerCanEventMsg exception:" + th);
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.POSTING)
    public void onReceiveGearLevelEvent(IVcuController.CarGearLevelEventMsg carGearLevelEventMsg) {
        int intValue = carGearLevelEventMsg.getData().intValue();
        sLastGear = intValue;
        LogUtils.i(TAG, "onReceiveVcuEvent : gearLevel = %d", Integer.valueOf(intValue));
        EventBus.getDefault().post(new GearLevelEvent(intValue));
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.POSTING)
    public void onIGStatusEvent(IMcuController.McuIgStatusEventMsg mcuIgStatusEventMsg) {
        int intValue = mcuIgStatusEventMsg.getData().intValue();
        LogUtils.i(TAG, "onIGStatusEvent : ig Status is " + intValue);
        if (intValue == 0) {
            EventBus.getDefault().post(new IgStatusOffEvent());
        }
    }

    public int getLevel() {
        try {
            return ((IVcuController) Module.get(CarControllerModuleEntry.class).get(IVcuController.class)).getGearLever();
        } catch (Throwable th) {
            LogUtils.e(TAG, "get Level exception : " + th.getLocalizedMessage());
            return sLastGear;
        }
    }

    public boolean isRDNLevel() {
        boolean z;
        try {
            IVcuController iVcuController = (IVcuController) Module.get(CarControllerModuleEntry.class).get(IVcuController.class);
            boolean z2 = iVcuController.getGearLever() == 3;
            boolean z3 = iVcuController.getGearLever() == 1;
            boolean z4 = iVcuController.getGearLever() == 2;
            if (!z2 && !z3 && !z4) {
                z = false;
                LogUtils.i(TAG, "get isRDNLevel res=" + z);
                return z;
            }
            z = true;
            LogUtils.i(TAG, "get isRDNLevel res=" + z);
            return z;
        } catch (Throwable th) {
            LogUtils.e(TAG, "get isRDNLevel exception : " + th.getLocalizedMessage());
            int i = sLastGear;
            return i == 3 || i == 1 || i == 2;
        }
    }

    public boolean isServiceConnected() {
        return this.mIsServiceConnected;
    }

    public void setServiceConnected(boolean z) {
        LogUtils.i(TAG, "setServiceConnected isConnected=" + z);
        this.mIsServiceConnected = z;
    }

    public String getCduType() {
        try {
            String xpCduType = Car.getXpCduType();
            LogUtils.i(TAG, "cdu_type:" + xpCduType);
            return xpCduType;
        } catch (Throwable th) {
            LogUtils.e(TAG, "getCduType exception:" + th);
            return null;
        }
    }

    public boolean isCarH93() {
        return "QB".equals(getCduType());
    }

    public boolean isCarD55() {
        return "Q3".equals(getCduType());
    }

    public boolean isCarE38() {
        String cduType = getCduType();
        return "Q7".equals(cduType) || "Q7A".equals(cduType);
    }
}

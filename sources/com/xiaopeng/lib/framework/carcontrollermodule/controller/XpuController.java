package com.xiaopeng.lib.framework.carcontrollermodule.controller;

import android.car.Car;
import android.car.CarNotConnectedException;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.xpu.CarXpuManager;
import android.util.Log;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.IEventMsg;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.v2extend.IXpuController;
import java.util.List;
/* loaded from: classes2.dex */
public class XpuController extends AbstractController implements IXpuController {
    private static final String TAG = "XpuController";
    private CarXpuManager.CarXpuEventCallback mCarXpuEventCallback;
    private CarXpuManager mCarXpuManager;

    public XpuController(Car car) {
        super(car);
        this.mCarXpuEventCallback = null;
    }

    @Override // com.xiaopeng.lib.framework.carcontrollermodule.controller.AbstractController
    public void initCarManager(Car car) {
        this.mCarApiClient = car;
        try {
            this.mCarXpuManager = (CarXpuManager) car.getCarManager(Car.XP_XPU_SERVICE);
        } catch (CarNotConnectedException | IllegalStateException unused) {
            Log.e(TAG, "Car not connected");
        }
    }

    @Override // com.xiaopeng.lib.framework.carcontrollermodule.controller.AbstractController
    public void initPropertyTypeMap() {
        this.mPropertyTypeMap.put(557856775, IXpuController.NedcSwitchEventMsg.class);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ILifeCycle
    public void registerCanEventMsg(List<Class<? extends IEventMsg>> list) {
        if (this.mCarXpuEventCallback == null) {
            this.mCarXpuEventCallback = new CarXpuManager.CarXpuEventCallback() { // from class: com.xiaopeng.lib.framework.carcontrollermodule.controller.XpuController.1
                @Override // android.car.hardware.CarEcuManager.CarEcuEventCallback
                public void onChangeEvent(CarPropertyValue carPropertyValue) {
                    XpuController xpuController = XpuController.this;
                    xpuController.postEventBusMsg(xpuController.mPropertyTypeMap.get(Integer.valueOf(carPropertyValue.getPropertyId())), carPropertyValue);
                }

                @Override // android.car.hardware.CarEcuManager.CarEcuEventCallback
                public void onErrorEvent(int i, int i2) {
                    Log.e(XpuController.TAG, "propertyId = " + i + " zone = " + i2);
                }
            };
        }
        try {
            this.mCarXpuManager.registerPropCallback(convertRegisterPropertyList(list), this.mCarXpuEventCallback);
        } catch (Exception unused) {
            Log.e(TAG, "Car not connected");
        }
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ILifeCycle
    public void unregisterCanEventMsg(List<Class<? extends IEventMsg>> list) {
        if (this.mCarXpuEventCallback == null) {
            return;
        }
        try {
            this.mCarXpuManager.unregisterPropCallback(convertUnregisterPropertyList(list), this.mCarXpuEventCallback);
        } catch (CarNotConnectedException e) {
            e.printStackTrace();
        }
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.v2extend.IXpuController
    public void setNedcSwitch(int i) throws Exception {
        this.mCarXpuManager.setNedcSwitch(i);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.v2extend.IXpuController
    public int getNedcSwitchStatus() throws Exception {
        return this.mCarXpuManager.getNedcSwitchStatus();
    }
}

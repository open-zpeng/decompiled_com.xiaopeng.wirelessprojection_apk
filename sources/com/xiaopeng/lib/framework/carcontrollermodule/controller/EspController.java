package com.xiaopeng.lib.framework.carcontrollermodule.controller;

import android.car.Car;
import android.car.CarNotConnectedException;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.esp.CarEspManager;
import android.util.Log;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.IEspController;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.IEventMsg;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController;
import java.util.List;
/* loaded from: classes2.dex */
public class EspController extends AbstractController implements IEspController {
    private static final String TAG = "EspController";
    private CarEspManager.CarEspEventCallback mCarEspEventCallback;
    private CarEspManager mCarEspManager;

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IEspController
    public boolean getAvhFault() throws Exception {
        return false;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IEspController
    public boolean getEspFault() throws Exception {
        return false;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IEspController
    public boolean getHDC() throws Exception {
        return false;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IEspController
    public boolean getHdcFault() throws Exception {
        return false;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IEspController
    public boolean isAbsFault() throws Exception {
        return false;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IEspController
    public boolean isAvhFault() throws Exception {
        return false;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IEspController
    public boolean isEpbWarningLampOn() throws Exception {
        return false;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IEspController
    public boolean isEpsWarningLampOn() throws Exception {
        return false;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IEspController
    public boolean isEspFault() throws Exception {
        return false;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IEspController
    public boolean isHdcFault() throws Exception {
        return false;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IEspController
    public void setHDC(boolean z) throws Exception {
    }

    public EspController(Car car) {
        super(car);
        this.mCarEspEventCallback = null;
    }

    @Override // com.xiaopeng.lib.framework.carcontrollermodule.controller.AbstractController
    public void initCarManager(Car car) {
        this.mCarApiClient = car;
        try {
            this.mCarEspManager = (CarEspManager) car.getCarManager(Car.XP_ESP_SERVICE);
        } catch (CarNotConnectedException | IllegalStateException unused) {
            Log.e(TAG, "Car not connected");
        }
    }

    @Override // com.xiaopeng.lib.framework.carcontrollermodule.controller.AbstractController
    public void initPropertyTypeMap() {
        this.mPropertyTypeMap.put(557851651, IEspController.AVHEventMsg.class);
        this.mPropertyTypeMap.put(557851650, IEspController.ESPEventMsg.class);
        this.mPropertyTypeMap.put(557851656, IEspController.HDCEventMsg.class);
        this.mPropertyTypeMap.put(559948801, IVcuController.CarSpeedEventMsg.class);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ILifeCycle
    public void registerCanEventMsg(List<Class<? extends IEventMsg>> list) {
        if (this.mCarEspEventCallback == null) {
            this.mCarEspEventCallback = new CarEspManager.CarEspEventCallback() { // from class: com.xiaopeng.lib.framework.carcontrollermodule.controller.EspController.1
                @Override // android.car.hardware.CarEcuManager.CarEcuEventCallback
                public void onChangeEvent(CarPropertyValue carPropertyValue) {
                    EspController espController = EspController.this;
                    espController.postEventBusMsg(espController.mPropertyTypeMap.get(Integer.valueOf(carPropertyValue.getPropertyId())), carPropertyValue);
                }

                @Override // android.car.hardware.CarEcuManager.CarEcuEventCallback
                public void onErrorEvent(int i, int i2) {
                    Log.e(getClass().getSimpleName(), "propertyId = " + i + " zone = " + i2);
                }
            };
        }
        try {
            this.mCarEspManager.registerPropCallback(convertRegisterPropertyList(list), this.mCarEspEventCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ILifeCycle
    public void unregisterCanEventMsg(List<Class<? extends IEventMsg>> list) {
        if (this.mCarEspEventCallback == null) {
            return;
        }
        try {
            this.mCarEspManager.unregisterPropCallback(convertUnregisterPropertyList(list), this.mCarEspEventCallback);
        } catch (CarNotConnectedException e) {
            e.printStackTrace();
        }
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IEspController
    public boolean getESP() throws Exception {
        return this.mCarEspManager.getEsp() == 1;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IEspController
    public void setESP(boolean z) throws Exception {
        this.mCarEspManager.setEsp(z ? 1 : 0);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IEspController
    public boolean getAVH() throws Exception {
        return this.mCarEspManager.getAvh() == 1;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IEspController
    public void setAVH(boolean z) throws Exception {
        this.mCarEspManager.setAvh(z ? 1 : 0);
    }
}

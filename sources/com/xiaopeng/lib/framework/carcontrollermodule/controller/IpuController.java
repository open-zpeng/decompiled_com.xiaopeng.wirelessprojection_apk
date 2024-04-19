package com.xiaopeng.lib.framework.carcontrollermodule.controller;

import android.car.Car;
import android.car.CarNotConnectedException;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.ipu.CarIpuManager;
import android.util.Log;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.IEventMsg;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.IIpuController;
import java.util.List;
/* loaded from: classes2.dex */
public class IpuController extends AbstractController implements IIpuController {
    private static final String TAG = "IpuController";
    private CarIpuManager.CarIpuEventCallback mCarIpuEventCallback;
    private CarIpuManager mCarIpuManager;

    public IpuController(Car car) {
        super(car);
        this.mCarIpuEventCallback = null;
    }

    @Override // com.xiaopeng.lib.framework.carcontrollermodule.controller.AbstractController
    public void initCarManager(Car car) {
        this.mCarApiClient = car;
        try {
            this.mCarIpuManager = (CarIpuManager) car.getCarManager(Car.XP_IPU_SERVICE);
        } catch (CarNotConnectedException | IllegalStateException unused) {
            Log.e(TAG, "Car not connected");
        }
    }

    @Override // com.xiaopeng.lib.framework.carcontrollermodule.controller.AbstractController
    public void initPropertyTypeMap() {
        this.mPropertyTypeMap.put(557847103, IIpuController.IpuFailStInfoEventMsg.class);
        this.mPropertyTypeMap.put(557853191, IIpuController.CtrlVoltEventMsg.class);
        this.mPropertyTypeMap.put(557853185, IIpuController.CtrlCurrEventMsg.class);
        this.mPropertyTypeMap.put(557853190, IIpuController.CtrlTempEventMsg.class);
        this.mPropertyTypeMap.put(557853188, IIpuController.MotorTempEventMsg.class);
        this.mPropertyTypeMap.put(559950341, IIpuController.TorqueEventMsg.class);
        this.mPropertyTypeMap.put(557853187, IIpuController.RollSpeedEventMsg.class);
        this.mPropertyTypeMap.put(557853186, IIpuController.MotorStatusEventMsg.class);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ILifeCycle
    public void registerCanEventMsg(List<Class<? extends IEventMsg>> list) {
        if (this.mCarIpuEventCallback == null) {
            this.mCarIpuEventCallback = new CarIpuManager.CarIpuEventCallback() { // from class: com.xiaopeng.lib.framework.carcontrollermodule.controller.IpuController.1
                @Override // android.car.hardware.CarEcuManager.CarEcuEventCallback
                public void onChangeEvent(CarPropertyValue carPropertyValue) {
                    IpuController ipuController = IpuController.this;
                    ipuController.postEventBusMsg(ipuController.mPropertyTypeMap.get(Integer.valueOf(carPropertyValue.getPropertyId())), carPropertyValue);
                }

                @Override // android.car.hardware.CarEcuManager.CarEcuEventCallback
                public void onErrorEvent(int i, int i2) {
                    Log.e(getClass().getSimpleName(), "propertyId = " + i + " zone = " + i2);
                }
            };
        }
        try {
            this.mCarIpuManager.registerPropCallback(convertRegisterPropertyList(list), this.mCarIpuEventCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ILifeCycle
    public void unregisterCanEventMsg(List<Class<? extends IEventMsg>> list) {
        if (this.mCarIpuEventCallback == null) {
            return;
        }
        try {
            this.mCarIpuManager.unregisterPropCallback(convertUnregisterPropertyList(list), this.mCarIpuEventCallback);
        } catch (CarNotConnectedException e) {
            e.printStackTrace();
        }
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IIpuController
    public int getIpuFailStInfo() throws Exception {
        return this.mCarIpuManager.getIpuFailStInfo();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IIpuController
    public int getCtrlVolt() throws Exception {
        return this.mCarIpuManager.getCtrlVolt();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IIpuController
    public int getCtrlCurr() throws Exception {
        return this.mCarIpuManager.getCtrlCurr();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IIpuController
    public int getCtrlTemp() throws Exception {
        return this.mCarIpuManager.getCtrlTemp();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IIpuController
    public int getMotorTemp() throws Exception {
        return this.mCarIpuManager.getMotorTemp();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IIpuController
    public float getTorque() throws Exception {
        return this.mCarIpuManager.getTorque();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IIpuController
    public int getRollSpeed() throws Exception {
        return this.mCarIpuManager.getRollSpeed();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IIpuController
    public int getMotorStatus() throws Exception {
        return this.mCarIpuManager.getMotorStatus();
    }
}

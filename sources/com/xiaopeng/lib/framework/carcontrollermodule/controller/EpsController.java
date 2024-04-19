package com.xiaopeng.lib.framework.carcontrollermodule.controller;

import android.car.Car;
import android.car.CarNotConnectedException;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.eps.CarEpsManager;
import android.util.Log;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.IEpsController;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.IEventMsg;
import java.util.List;
/* loaded from: classes2.dex */
public class EpsController extends AbstractController implements IEpsController {
    private static final String TAG = "EpsController";
    private CarEpsManager.CarEpsEventCallback mCarEpsEventCallback;
    private CarEpsManager mCarEpsManager;

    public EpsController(Car car) {
        super(car);
        this.mCarEpsEventCallback = null;
    }

    @Override // com.xiaopeng.lib.framework.carcontrollermodule.controller.AbstractController
    public void initCarManager(Car car) {
        this.mCarApiClient = car;
        try {
            this.mCarEpsManager = (CarEpsManager) car.getCarManager(Car.XP_EPS_SERVICE);
        } catch (CarNotConnectedException | IllegalStateException unused) {
            Log.e(TAG, "Car not connected");
        }
    }

    @Override // com.xiaopeng.lib.framework.carcontrollermodule.controller.AbstractController
    public void initPropertyTypeMap() {
        this.mPropertyTypeMap.put(557851653, IEpsController.SteeringWheelEPSEventMsg.class);
        this.mPropertyTypeMap.put(559948806, IEpsController.SteeringAngleEventMsg.class);
        this.mPropertyTypeMap.put(557851655, IEpsController.SteeringAngleSpdEventMsg.class);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ILifeCycle
    public void registerCanEventMsg(List<Class<? extends IEventMsg>> list) {
        if (this.mCarEpsEventCallback == null) {
            this.mCarEpsEventCallback = new CarEpsManager.CarEpsEventCallback() { // from class: com.xiaopeng.lib.framework.carcontrollermodule.controller.EpsController.1
                @Override // android.car.hardware.CarEcuManager.CarEcuEventCallback
                public void onChangeEvent(CarPropertyValue carPropertyValue) {
                    EpsController epsController = EpsController.this;
                    epsController.postEventBusMsg(epsController.mPropertyTypeMap.get(Integer.valueOf(carPropertyValue.getPropertyId())), carPropertyValue);
                }

                @Override // android.car.hardware.CarEcuManager.CarEcuEventCallback
                public void onErrorEvent(int i, int i2) {
                    Log.e(getClass().getSimpleName(), "propertyId = " + i + " zone = " + i2);
                }
            };
        }
        try {
            this.mCarEpsManager.registerPropCallback(convertRegisterPropertyList(list), this.mCarEpsEventCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ILifeCycle
    public void unregisterCanEventMsg(List<Class<? extends IEventMsg>> list) {
        if (this.mCarEpsEventCallback == null) {
            return;
        }
        try {
            this.mCarEpsManager.unregisterPropCallback(convertUnregisterPropertyList(list), this.mCarEpsEventCallback);
        } catch (CarNotConnectedException e) {
            e.printStackTrace();
        }
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IEpsController
    public int getSteeringWheelEPS() throws Exception {
        return this.mCarEpsManager.getSteeringWheelEPS();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IEpsController
    public void setSteeringWheelEPS(int i) throws Exception {
        this.mCarEpsManager.setSteeringWheelEPS(i);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IEpsController
    public float getSteeringAngle() throws Exception {
        return this.mCarEpsManager.getSteeringAngle();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IEpsController
    public float getSteeringAngleSpd() throws Exception {
        return this.mCarEpsManager.getSteeringAngleSpd();
    }
}

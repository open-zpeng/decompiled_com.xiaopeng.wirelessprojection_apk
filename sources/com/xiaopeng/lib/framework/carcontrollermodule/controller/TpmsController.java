package com.xiaopeng.lib.framework.carcontrollermodule.controller;

import android.car.Car;
import android.car.CarNotConnectedException;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.tpms.CarTpmsManager;
import android.util.Log;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.IEventMsg;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.ITpmsController;
import java.util.List;
/* loaded from: classes2.dex */
public class TpmsController extends AbstractController implements ITpmsController {
    private static final String TAG = "TpmsController";
    private CarTpmsManager.CarTpmsEventCallback mCarTpmsEventCallback;
    private CarTpmsManager mCarTpmsManager;

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ITpmsController
    public int[] getAllTirePerssureSensorStatus() throws Exception {
        return new int[0];
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ITpmsController
    public int[] getAllTirePressureWarnings() throws Exception {
        return new int[0];
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ITpmsController
    public int[] getAllTireTemperatureWarnings() throws Exception {
        return new int[0];
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ITpmsController
    public int getTirePressure() throws Exception {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ITpmsController
    public float[] getTirePressureAll() throws Exception {
        return null;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ITpmsController
    public boolean isAbnormalTirePressure() throws Exception {
        return false;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ITpmsController
    public boolean isTirePressureSystemFault() throws Exception {
        return false;
    }

    public TpmsController(Car car) {
        super(car);
        this.mCarTpmsEventCallback = null;
    }

    @Override // com.xiaopeng.lib.framework.carcontrollermodule.controller.AbstractController
    public void initCarManager(Car car) {
        this.mCarApiClient = car;
        try {
            this.mCarTpmsManager = (CarTpmsManager) car.getCarManager(Car.XP_TPMS_SERVICE);
        } catch (CarNotConnectedException | IllegalStateException unused) {
            Log.e(TAG, "Car not connected");
        }
    }

    @Override // com.xiaopeng.lib.framework.carcontrollermodule.controller.AbstractController
    public void initPropertyTypeMap() {
        this.mPropertyTypeMap.put(557850113, ITpmsController.TirePressureEventMsg.class);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ILifeCycle
    public void registerCanEventMsg(List<Class<? extends IEventMsg>> list) {
        if (this.mCarTpmsEventCallback == null) {
            this.mCarTpmsEventCallback = new CarTpmsManager.CarTpmsEventCallback() { // from class: com.xiaopeng.lib.framework.carcontrollermodule.controller.TpmsController.1
                @Override // android.car.hardware.CarEcuManager.CarEcuEventCallback
                public void onChangeEvent(CarPropertyValue carPropertyValue) {
                    TpmsController tpmsController = TpmsController.this;
                    tpmsController.postEventBusMsg(tpmsController.mPropertyTypeMap.get(Integer.valueOf(carPropertyValue.getPropertyId())), carPropertyValue);
                }

                @Override // android.car.hardware.CarEcuManager.CarEcuEventCallback
                public void onErrorEvent(int i, int i2) {
                    Log.e(getClass().getSimpleName(), "propertyId = " + i + " zone = " + i2);
                }
            };
        }
        try {
            this.mCarTpmsManager.registerPropCallback(convertRegisterPropertyList(list), this.mCarTpmsEventCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ILifeCycle
    public void unregisterCanEventMsg(List<Class<? extends IEventMsg>> list) {
        if (this.mCarTpmsEventCallback == null) {
            return;
        }
        try {
            this.mCarTpmsManager.unregisterPropCallback(convertUnregisterPropertyList(list), this.mCarTpmsEventCallback);
        } catch (CarNotConnectedException e) {
            e.printStackTrace();
        }
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ITpmsController
    public void calibrateTirePressure() throws Exception {
        this.mCarTpmsManager.calibrateTirePressure();
    }
}

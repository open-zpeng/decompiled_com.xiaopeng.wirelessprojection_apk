package com.xiaopeng.lib.framework.carcontrollermodule.controller;

import android.car.Car;
import android.car.CarNotConnectedException;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.dcdc.CarDcdcManager;
import android.util.Log;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.IDcdcController;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.IEventMsg;
import java.util.List;
/* loaded from: classes2.dex */
public class DcdcController extends AbstractController implements IDcdcController {
    private static final String TAG = "DcdcController";
    private CarDcdcManager.CarDcdcEventCallback mCarDcdcEventCallback;
    private CarDcdcManager mCarDcdcManager;

    public DcdcController(Car car) {
        super(car);
        this.mCarDcdcEventCallback = null;
    }

    @Override // com.xiaopeng.lib.framework.carcontrollermodule.controller.AbstractController
    public void initCarManager(Car car) {
        this.mCarApiClient = car;
        try {
            this.mCarDcdcManager = (CarDcdcManager) car.getCarManager(Car.XP_DCDC_SERVICE);
        } catch (CarNotConnectedException | IllegalStateException unused) {
            Log.e(TAG, "Car not connected");
        }
    }

    @Override // com.xiaopeng.lib.framework.carcontrollermodule.controller.AbstractController
    public void initPropertyTypeMap() {
        this.mPropertyTypeMap.put(557854721, IDcdcController.DcdcFailStInfoEventMsg.class);
        this.mPropertyTypeMap.put(557854722, IDcdcController.DcdcStatusEventMsg.class);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ILifeCycle
    public void registerCanEventMsg(List<Class<? extends IEventMsg>> list) {
        if (this.mCarDcdcEventCallback == null) {
            this.mCarDcdcEventCallback = new CarDcdcManager.CarDcdcEventCallback() { // from class: com.xiaopeng.lib.framework.carcontrollermodule.controller.DcdcController.1
                @Override // android.car.hardware.CarEcuManager.CarEcuEventCallback
                public void onChangeEvent(CarPropertyValue carPropertyValue) {
                    DcdcController dcdcController = DcdcController.this;
                    dcdcController.postEventBusMsg(dcdcController.mPropertyTypeMap.get(Integer.valueOf(carPropertyValue.getPropertyId())), carPropertyValue);
                }

                @Override // android.car.hardware.CarEcuManager.CarEcuEventCallback
                public void onErrorEvent(int i, int i2) {
                    Log.e(getClass().getSimpleName(), "propertyId = " + i + " zone = " + i2);
                }
            };
        }
        try {
            this.mCarDcdcManager.registerPropCallback(convertRegisterPropertyList(list), this.mCarDcdcEventCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ILifeCycle
    public void unregisterCanEventMsg(List<Class<? extends IEventMsg>> list) {
        if (this.mCarDcdcEventCallback == null) {
            return;
        }
        try {
            this.mCarDcdcManager.unregisterPropCallback(convertUnregisterPropertyList(list), this.mCarDcdcEventCallback);
        } catch (CarNotConnectedException e) {
            e.printStackTrace();
        }
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IDcdcController
    public int getDcdcFailStInfo() throws Exception {
        return this.mCarDcdcManager.getDcdcFailStInfo();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IDcdcController
    public int getDcdcStatus() throws Exception {
        return this.mCarDcdcManager.getDcdcStatus();
    }
}

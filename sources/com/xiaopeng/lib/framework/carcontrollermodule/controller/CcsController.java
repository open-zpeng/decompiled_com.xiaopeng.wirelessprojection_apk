package com.xiaopeng.lib.framework.carcontrollermodule.controller;

import android.car.Car;
import android.car.CarNotConnectedException;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.ccs.CarCcsManager;
import android.util.Log;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICcsController;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.IEventMsg;
import java.util.List;
/* loaded from: classes2.dex */
public class CcsController extends AbstractController implements ICcsController {
    private static final String TAG = "CcsController";
    private CarCcsManager.CarCcsEventCallback mCarCcsEventCallback;
    private CarCcsManager mCarCcsManager;

    public CcsController(Car car) {
        super(car);
        this.mCarCcsEventCallback = null;
    }

    @Override // com.xiaopeng.lib.framework.carcontrollermodule.controller.AbstractController
    public void initCarManager(Car car) {
        this.mCarApiClient = car;
        try {
            this.mCarCcsManager = (CarCcsManager) car.getCarManager(Car.XP_CCS_SERVICE);
        } catch (CarNotConnectedException | IllegalStateException unused) {
            Log.e(TAG, "Car not connected");
        }
    }

    @Override // com.xiaopeng.lib.framework.carcontrollermodule.controller.AbstractController
    public void initPropertyTypeMap() {
        this.mPropertyTypeMap.put(557854723, ICcsController.CcsFaultInfoEventMsg.class);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ILifeCycle
    public void registerCanEventMsg(List<Class<? extends IEventMsg>> list) {
        if (this.mCarCcsEventCallback == null) {
            this.mCarCcsEventCallback = new CarCcsManager.CarCcsEventCallback() { // from class: com.xiaopeng.lib.framework.carcontrollermodule.controller.CcsController.1
                @Override // android.car.hardware.CarEcuManager.CarEcuEventCallback
                public void onChangeEvent(CarPropertyValue carPropertyValue) {
                    CcsController ccsController = CcsController.this;
                    ccsController.postEventBusMsg(ccsController.mPropertyTypeMap.get(Integer.valueOf(carPropertyValue.getPropertyId())), carPropertyValue);
                }

                @Override // android.car.hardware.CarEcuManager.CarEcuEventCallback
                public void onErrorEvent(int i, int i2) {
                    Log.e(getClass().getSimpleName(), "propertyId = " + i + " zone = " + i2);
                }
            };
        }
        try {
            this.mCarCcsManager.registerPropCallback(convertRegisterPropertyList(list), this.mCarCcsEventCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ILifeCycle
    public void unregisterCanEventMsg(List<Class<? extends IEventMsg>> list) {
        if (this.mCarCcsEventCallback == null) {
            return;
        }
        try {
            this.mCarCcsManager.unregisterPropCallback(convertUnregisterPropertyList(list), this.mCarCcsEventCallback);
        } catch (CarNotConnectedException e) {
            e.printStackTrace();
        }
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICcsController
    public int getCcsFaultInfo() throws Exception {
        return this.mCarCcsManager.getCcsFaultInfo();
    }
}

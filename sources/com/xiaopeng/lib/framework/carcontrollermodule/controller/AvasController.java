package com.xiaopeng.lib.framework.carcontrollermodule.controller;

import android.car.Car;
import android.car.CarNotConnectedException;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.avas.CarAvasManager;
import android.util.Log;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.IAvasController;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.IEventMsg;
import java.util.List;
/* loaded from: classes2.dex */
public class AvasController extends AbstractController implements IAvasController {
    private static final String TAG = "BcmController";
    private CarAvasManager.CarAvasEventCallback mCarAvasEventCallback;
    private CarAvasManager mCarAvasManager;

    @Override // com.xiaopeng.lib.framework.carcontrollermodule.controller.AbstractController
    public void initPropertyTypeMap() {
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IAvasController
    public boolean isFault() throws Exception {
        return false;
    }

    public AvasController(Car car) {
        super(car);
        this.mCarAvasEventCallback = null;
    }

    @Override // com.xiaopeng.lib.framework.carcontrollermodule.controller.AbstractController
    public void initCarManager(Car car) {
        this.mCarApiClient = car;
        try {
            this.mCarAvasManager = (CarAvasManager) car.getCarManager(Car.XP_AVAS_SERVICE);
        } catch (CarNotConnectedException | IllegalStateException unused) {
            Log.e(TAG, "Car not connected");
        }
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ILifeCycle
    public void registerCanEventMsg(List<Class<? extends IEventMsg>> list) {
        if (this.mCarAvasEventCallback == null) {
            this.mCarAvasEventCallback = new CarAvasManager.CarAvasEventCallback() { // from class: com.xiaopeng.lib.framework.carcontrollermodule.controller.AvasController.1
                @Override // android.car.hardware.CarEcuManager.CarEcuEventCallback
                public void onChangeEvent(CarPropertyValue carPropertyValue) {
                    AvasController avasController = AvasController.this;
                    avasController.postEventBusMsg(avasController.mPropertyTypeMap.get(Integer.valueOf(carPropertyValue.getPropertyId())), carPropertyValue);
                }

                @Override // android.car.hardware.CarEcuManager.CarEcuEventCallback
                public void onErrorEvent(int i, int i2) {
                    Log.e(AvasController.TAG, "propertyId = " + i + " zone = " + i2);
                }
            };
        }
        try {
            this.mCarAvasManager.registerPropCallback(convertRegisterPropertyList(list), this.mCarAvasEventCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ILifeCycle
    public void unregisterCanEventMsg(List<Class<? extends IEventMsg>> list) {
        if (this.mCarAvasEventCallback == null) {
            return;
        }
        try {
            this.mCarAvasManager.unregisterPropCallback(convertUnregisterPropertyList(list), this.mCarAvasEventCallback);
        } catch (CarNotConnectedException e) {
            e.printStackTrace();
        }
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IAvasController
    public void setAnalogSoundEffect(int i) throws Exception {
        this.mCarAvasManager.setAvasLowSpeedSound(i);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IAvasController
    public void setAnalogSoundEnable(boolean z) throws Exception {
        this.mCarAvasManager.setAvasLowSpeedSoundSwitch(z ? 1 : 0);
    }
}

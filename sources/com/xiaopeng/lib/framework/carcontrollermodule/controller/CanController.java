package com.xiaopeng.lib.framework.carcontrollermodule.controller;

import android.car.Car;
import android.car.CarNotConnectedException;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.can.CarCanManager;
import android.util.Log;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICanController;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.IEventMsg;
import java.util.List;
/* loaded from: classes2.dex */
public class CanController extends AbstractController implements ICanController {
    private static final String TAG = "CanController";
    private CarCanManager.CarCanEventCallback mCarCanEventCallback;
    private CarCanManager mCarCanManager;

    @Override // com.xiaopeng.lib.framework.carcontrollermodule.controller.AbstractController
    public void initPropertyTypeMap() {
    }

    public CanController(Car car) {
        super(car);
        this.mCarCanEventCallback = null;
    }

    @Override // com.xiaopeng.lib.framework.carcontrollermodule.controller.AbstractController
    public void initCarManager(Car car) {
        this.mCarApiClient = car;
        try {
            this.mCarCanManager = (CarCanManager) car.getCarManager(Car.XP_CAN_SERVICE);
        } catch (CarNotConnectedException | IllegalStateException unused) {
            Log.e(TAG, "Car not connected");
        }
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ILifeCycle
    public void registerCanEventMsg(List<Class<? extends IEventMsg>> list) {
        if (this.mCarCanEventCallback == null) {
            this.mCarCanEventCallback = new CarCanManager.CarCanEventCallback() { // from class: com.xiaopeng.lib.framework.carcontrollermodule.controller.CanController.1
                @Override // android.car.hardware.CarEcuManager.CarEcuEventCallback
                public void onChangeEvent(CarPropertyValue carPropertyValue) {
                    CanController canController = CanController.this;
                    canController.postEventBusMsg(canController.mPropertyTypeMap.get(Integer.valueOf(carPropertyValue.getPropertyId())), carPropertyValue);
                }

                @Override // android.car.hardware.CarEcuManager.CarEcuEventCallback
                public void onErrorEvent(int i, int i2) {
                    Log.e(getClass().getSimpleName(), "propertyId = " + i + " zone = " + i2);
                }
            };
        }
        try {
            this.mCarCanManager.registerPropCallback(convertRegisterPropertyList(list), this.mCarCanEventCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ILifeCycle
    public void unregisterCanEventMsg(List<Class<? extends IEventMsg>> list) {
        if (this.mCarCanEventCallback == null) {
            return;
        }
        try {
            this.mCarCanManager.unregisterPropCallback(convertUnregisterPropertyList(list), this.mCarCanEventCallback);
        } catch (CarNotConnectedException e) {
            e.printStackTrace();
        }
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICanController
    public void sendCanDataSync() throws Exception {
        throw new IllegalAccessException("not support any more!");
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICanController
    public byte[] getCanRawData() throws Exception {
        throw new IllegalAccessException("not support any more!");
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICanController
    public void setAdasMeta(byte[] bArr) throws Exception {
        this.mCarCanManager.setAdasMeta(bArr);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICanController
    public void setAdasStub(byte[] bArr) throws Exception {
        this.mCarCanManager.setAdasStub(bArr);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICanController
    public void setAdasProfShort(byte[] bArr) throws Exception {
        this.mCarCanManager.setAdasProfShort(bArr);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICanController
    public void setAdasSegment(byte[] bArr) throws Exception {
        this.mCarCanManager.setAdasSegment(bArr);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICanController
    public void setAdasPosition(byte[] bArr) throws Exception {
        this.mCarCanManager.setAdasPosition(bArr);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICanController
    public void setAdasProfLong(byte[] bArr) throws Exception {
        this.mCarCanManager.setAdasProfLong(bArr);
    }
}

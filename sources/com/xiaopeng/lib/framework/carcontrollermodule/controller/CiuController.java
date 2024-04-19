package com.xiaopeng.lib.framework.carcontrollermodule.controller;

import android.car.Car;
import android.car.CarNotConnectedException;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.ciu.CarCiuManager;
import android.util.Log;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.IEventMsg;
import java.util.List;
/* loaded from: classes2.dex */
public class CiuController extends AbstractController implements ICiuController {
    private static final String TAG = "CiuController";
    private CarCiuManager.CarCiuEventCallback mCarCiuEventCallback;
    private CarCiuManager mCarCiuManager;

    private void sendCommand(int i, int i2) throws Exception {
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController
    public int getCiuAutoLockSt() throws Exception {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController
    public boolean getCiuCarWash() throws Exception {
        return false;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController
    public int getCiuConfigurationActive() throws Exception {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController
    public int getCiuDelayOff() throws Exception {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController
    public int getCiuDeliveryUploadMode() throws Exception {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController
    public int getCiuRainSw() throws Exception {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController
    public int getCiuValid() throws Exception {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController
    public int getDVRFormatStatus() throws Exception {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController
    public int getDVRStatus() throws Exception {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController
    public int getDeleteResult() throws Exception {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController
    public int getDistractionLevel() throws Exception {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController
    public int getDistractionStatus() throws Exception {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController
    public int getDmsState() throws Exception {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController
    public int getDvrLockFB() throws Exception {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController
    public int getDvrMode() throws Exception {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController
    public int getErrorType() throws Exception {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController
    public int getFaceAction() throws Exception {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController
    public int getFaceIdPrimalStatus() throws Exception {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController
    public int getFaceIdStatus() throws Exception {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController
    public int getFaceIdSw() throws Exception {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController
    public boolean getFaceShield() throws Exception {
        return false;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController
    public int getFatIgStatus() throws Exception {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController
    public int getFatigueLevel() throws Exception {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController
    public int getFatigueStatus() throws Exception {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController
    public int getLightIntensity() throws Exception {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController
    public int getSdStatus() throws Exception {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController
    public int getSdcardStatus() throws Exception {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController
    public int getUid() throws Exception {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.carcontrollermodule.controller.AbstractController
    public void initPropertyTypeMap() {
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController
    public boolean isDvrEnable() throws Exception {
        return false;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController
    public void photoProcess() throws Exception {
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController
    public void setCiuCarWash(boolean z) throws Exception {
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController
    public void setCiuConfigurationActive(int i) throws Exception {
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController
    public void setCiuDelayOff(int i) throws Exception {
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController
    public void setCiuDeliveryUploadMode(int i) throws Exception {
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController
    public void setCiuRainSw(int i) throws Exception {
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController
    public void setDeleteFaceId(int i) throws Exception {
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController
    public void setDeleteMulti(int i, int i2) throws Exception {
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController
    public void setDistractionStatus(int i) throws Exception {
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController
    public void setDmsMode(int i) throws Exception {
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController
    public void setDmsStatus(int i) throws Exception {
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController
    public void setDmsStatus(int i, int i2, int i3, int i4) throws Exception {
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController
    public void setDvrEnable(boolean z) throws Exception {
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController
    public void setDvrLockMode(int i) throws Exception {
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController
    public void setDvrMode(int i) throws Exception {
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController
    public void setFaceActionRequest(int i) throws Exception {
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController
    public void setFaceIdMode(int i) throws Exception {
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController
    public void setFaceIdSw(int i) throws Exception {
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController
    public void setFatIgStatus(int i) throws Exception {
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController
    public void setFatigueStatus(int i) throws Exception {
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController
    public void setFirmFaceCancel(int i) throws Exception {
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController
    public void setFormatMode(int i) throws Exception {
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController
    public void setNotifyCiuAutoLightStatus(boolean z) throws Exception {
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController
    public void setRegHint(int i) throws Exception {
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController
    public void setRegisterRequestMulti(int i, int i2, int i3) throws Exception {
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController
    public void setStartRegFlag(int i) throws Exception {
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController
    public void setStartRegFlow(int i) throws Exception {
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController
    public void setUid(int i) throws Exception {
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController
    public void setVideoOutputMode(int i) throws Exception {
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController
    public void setVideoTimeLenMode(int i) throws Exception {
    }

    public CiuController(Car car) {
        super(car);
        this.mCarCiuEventCallback = null;
    }

    @Override // com.xiaopeng.lib.framework.carcontrollermodule.controller.AbstractController
    public void initCarManager(Car car) {
        this.mCarApiClient = car;
        try {
            this.mCarCiuManager = (CarCiuManager) car.getCarManager(Car.XP_CIU_SERVICE);
        } catch (CarNotConnectedException unused) {
            Log.e(TAG, "Car not connected");
        }
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ILifeCycle
    public void registerCanEventMsg(List<Class<? extends IEventMsg>> list) {
        if (this.mCarCiuEventCallback == null) {
            this.mCarCiuEventCallback = new CarCiuManager.CarCiuEventCallback() { // from class: com.xiaopeng.lib.framework.carcontrollermodule.controller.CiuController.1
                @Override // android.car.hardware.CarEcuManager.CarEcuEventCallback
                public void onChangeEvent(CarPropertyValue carPropertyValue) {
                    CiuController ciuController = CiuController.this;
                    ciuController.postEventBusMsg(ciuController.mPropertyTypeMap.get(Integer.valueOf(carPropertyValue.getPropertyId())), carPropertyValue);
                }

                @Override // android.car.hardware.CarEcuManager.CarEcuEventCallback
                public void onErrorEvent(int i, int i2) {
                    Log.e(CiuController.TAG, "propertyId = " + i + " zone = " + i2);
                }
            };
        }
        try {
            this.mCarCiuManager.registerPropCallback(convertRegisterPropertyList(list), this.mCarCiuEventCallback);
        } catch (Exception unused) {
            Log.e(getClass().getSimpleName(), "Car not connected");
        }
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ILifeCycle
    public void unregisterCanEventMsg(List<Class<? extends IEventMsg>> list) {
        if (this.mCarCiuEventCallback == null) {
            return;
        }
        try {
            this.mCarCiuManager.unregisterPropCallback(convertUnregisterPropertyList(list), this.mCarCiuEventCallback);
        } catch (CarNotConnectedException e) {
            e.printStackTrace();
        }
    }
}

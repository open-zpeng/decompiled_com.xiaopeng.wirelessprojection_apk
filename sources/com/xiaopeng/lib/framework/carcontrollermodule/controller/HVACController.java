package com.xiaopeng.lib.framework.carcontrollermodule.controller;

import android.car.Car;
import android.car.CarNotConnectedException;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.hvac.CarHvacManager;
import android.util.Log;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.IEventMsg;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.IHVACController;
import java.util.List;
/* loaded from: classes2.dex */
public class HVACController extends AbstractController implements IHVACController {
    private static final String TAG = "HVACController";
    private CarHvacManager.CarHvacEventCallback mCarHVACEventCallback;
    private CarHvacManager mCarHVACManager;

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IHVACController
    public int[] getCompressorErrorInfo() throws Exception {
        return new int[0];
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IHVACController
    public boolean getHVACEcon() throws Exception {
        return false;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IHVACController
    public float getOutsideAirTemp() throws Exception {
        return 0.0f;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IHVACController
    public int getPtcError() throws Exception {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IHVACController
    public int getTempPTCStatus() throws Exception {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IHVACController
    public boolean isError() throws Exception {
        return false;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IHVACController
    public void setHVACEcon(boolean z) throws Exception {
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IHVACController
    public void setTempPTCStatus(int i) throws Exception {
    }

    public HVACController(Car car) {
        super(car);
        this.mCarHVACEventCallback = null;
    }

    @Override // com.xiaopeng.lib.framework.carcontrollermodule.controller.AbstractController
    public void initCarManager(Car car) {
        this.mCarApiClient = car;
        try {
            this.mCarHVACManager = (CarHvacManager) car.getCarManager(Car.HVAC_SERVICE);
        } catch (Exception unused) {
            Log.e(TAG, "Car not connected");
        }
    }

    @Override // com.xiaopeng.lib.framework.carcontrollermodule.controller.AbstractController
    public void initPropertyTypeMap() {
        this.mPropertyTypeMap.put(557849130, IHVACController.HVACPowerModeEventMsg.class);
        this.mPropertyTypeMap.put(557849127, IHVACController.HVACTempACModeEventMsg.class);
        this.mPropertyTypeMap.put(358614275, IHVACController.HVACTempDriverValueEventMsg.class);
        this.mPropertyTypeMap.put(559946242, IHVACController.HVACTempPsnValueEventMsg.class);
        this.mPropertyTypeMap.put(557849129, IHVACController.HVACAutoModeEventMsg.class);
        this.mPropertyTypeMap.put(557849091, IHVACController.HVACAutoModeBlowLevelEventMsg.class);
        this.mPropertyTypeMap.put(356517128, IHVACController.HVACCirculationModeEventMsg.class);
        this.mPropertyTypeMap.put(557849126, IHVACController.HVACFrontDefrostModeEventMsg.class);
        this.mPropertyTypeMap.put(356517121, IHVACController.HVACWindBlowModeEventMsg.class);
        this.mPropertyTypeMap.put(356517120, IHVACController.HVACWindSpeedLevelEventMsg.class);
        this.mPropertyTypeMap.put(557849089, IHVACController.HVACQualityPurgeModeEventMsg.class);
        this.mPropertyTypeMap.put(557849094, IHVACController.HVACQualityOutsideStatusEventMsg.class);
        this.mPropertyTypeMap.put(557849093, IHVACController.HVACQualityOutsideLevelEventMsg.class);
        this.mPropertyTypeMap.put(559946285, IHVACController.HVACInnerTempEventMsg.class);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ILifeCycle
    public void registerCanEventMsg(List<Class<? extends IEventMsg>> list) {
        if (this.mCarHVACEventCallback == null) {
            this.mCarHVACEventCallback = new CarHvacManager.CarHvacEventCallback() { // from class: com.xiaopeng.lib.framework.carcontrollermodule.controller.HVACController.1
                @Override // android.car.hardware.CarEcuManager.CarEcuEventCallback
                public void onChangeEvent(CarPropertyValue carPropertyValue) {
                    HVACController hVACController = HVACController.this;
                    hVACController.postEventBusMsg(hVACController.mPropertyTypeMap.get(Integer.valueOf(carPropertyValue.getPropertyId())), carPropertyValue);
                }

                @Override // android.car.hardware.CarEcuManager.CarEcuEventCallback
                public void onErrorEvent(int i, int i2) {
                    Log.e(HVACController.TAG, "onErrorEvent arg1 = " + i + " arg2 = " + i2);
                }
            };
        }
        try {
            this.mCarHVACManager.registerPropCallback(convertRegisterPropertyList(list), this.mCarHVACEventCallback);
        } catch (Exception unused) {
            Log.e(TAG, "Car not connected");
        }
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ILifeCycle
    public void unregisterCanEventMsg(List<Class<? extends IEventMsg>> list) {
        if (this.mCarHVACEventCallback == null) {
            return;
        }
        try {
            this.mCarHVACManager.unregisterPropCallback(convertRegisterPropertyList(list), this.mCarHVACEventCallback);
        } catch (CarNotConnectedException e) {
            e.printStackTrace();
        }
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IHVACController
    @Deprecated
    public void setHVACPowerMode() throws Exception {
        throw new IllegalAccessException("not support");
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IHVACController
    public void setHVACPowerMode(boolean z) throws Exception {
        this.mCarHVACManager.setHvacPowerMode(z ? 1 : 0);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IHVACController
    public boolean getHVACPowerMode() throws Exception {
        return this.mCarHVACManager.getHvacPowerMode() == 1;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IHVACController
    public void setHVACTempACMode() throws Exception {
        throw new IllegalAccessException("not support");
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IHVACController
    public void setHVACTempACMode(boolean z) throws Exception {
        this.mCarHVACManager.setHvacTempAcMode(z ? 1 : 0);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IHVACController
    public boolean getHVACTempACMode() throws Exception {
        return this.mCarHVACManager.getHvacTempAcMode() == 1;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IHVACController
    public void setHVACTempSyncMode() throws Exception {
        throw new IllegalAccessException("not support");
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IHVACController
    public void setHVACTempSyncMode(boolean z) throws Exception {
        throw new IllegalAccessException("not support");
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IHVACController
    public boolean getHVACTempSyncMode() throws Exception {
        return this.mCarHVACManager.getHvacTempSyncMode() == 1;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IHVACController
    public void setHVACTempDriverValue(float f) throws Exception {
        this.mCarHVACManager.setHvacTempDriverValue(f);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IHVACController
    public void setHVACTempDriverUp() throws Exception {
        throw new IllegalAccessException("not support");
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IHVACController
    public void setHVACTempDriverUp(float f) throws Exception {
        this.mCarHVACManager.setHvacTempDriverUp(f);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IHVACController
    public void setHVACTempDriverDown() throws Exception {
        throw new IllegalAccessException("not support");
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IHVACController
    public void setHVACTempDriverDown(float f) throws Exception {
        this.mCarHVACManager.setHvacTempDriverDown(f);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IHVACController
    public float getHVACTempDriverValue() throws Exception {
        return this.mCarHVACManager.getHvacTempDriverValue();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IHVACController
    public void setHVACTempPsnValue(float f) throws Exception {
        this.mCarHVACManager.setHvacTempPsnValue(f);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IHVACController
    public void setHVACTempPsnUp() throws Exception {
        throw new IllegalAccessException("not support");
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IHVACController
    public void setHVACTempPsnUp(float f) throws Exception {
        this.mCarHVACManager.setHvacTempPsnUp(f);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IHVACController
    public void setHVACTempPsnDown() throws Exception {
        throw new IllegalAccessException("not support");
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IHVACController
    public void setHVACTempPsnDown(float f) throws Exception {
        this.mCarHVACManager.setHvacTempPsnDown(f);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IHVACController
    public float getHVACTempPsnValue() throws Exception {
        return this.mCarHVACManager.getHvacTempPsnValue();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IHVACController
    public void setHVACAutoMode() throws Exception {
        throw new IllegalAccessException("not support");
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IHVACController
    public void setHVACAutoMode(boolean z) throws Exception {
        this.mCarHVACManager.setHvacAutoMode(z ? 1 : 0);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IHVACController
    public boolean getHVACAutoMode() throws Exception {
        return this.mCarHVACManager.getHvacAutoMode() == 1;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IHVACController
    public void setHVACAutoModeBlowLevel(int i) throws Exception {
        this.mCarHVACManager.setHvacAutoModeBlowLevel(i);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IHVACController
    public int getHVACAutoModeBlowLevel() throws Exception {
        return this.mCarHVACManager.getHvacAutoModeBlowLevel();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IHVACController
    public void setHVACCirculationMode() throws Exception {
        throw new IllegalAccessException("not support");
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IHVACController
    public void setHVACCirculationMode(int i) throws Exception {
        this.mCarHVACManager.setHvacCirculationMode(i);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IHVACController
    public int getHVACCirculationMode() throws Exception {
        return this.mCarHVACManager.getHvacCirculationMode();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IHVACController
    public void setHVACFrontDefrostMode() throws Exception {
        throw new IllegalAccessException("not support");
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IHVACController
    public void setHVACFrontDefrostMode(boolean z) throws Exception {
        this.mCarHVACManager.setHvacFrontDefrostMode(z ? 1 : 0);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IHVACController
    public boolean getHVACFrontDefrostMode() throws Exception {
        return this.mCarHVACManager.getHVACFrontDefrostMode() == 1;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IHVACController
    public void setHVACWindBlowMode(int i) throws Exception {
        this.mCarHVACManager.setHvacWindBlowMode(i);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IHVACController
    public int getHVACWindBlowMode() throws Exception {
        return this.mCarHVACManager.getHvacWindBlowMode();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IHVACController
    public void setHVACWindSpeedLevel(int i) throws Exception {
        this.mCarHVACManager.setHvacWindSpeedLevel(i);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IHVACController
    public void setHVACWindSpeedUp() throws Exception {
        throw new IllegalAccessException("not support");
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IHVACController
    public void setHVACWindSpeedUp(int i) throws Exception {
        this.mCarHVACManager.setHvacWindSpeedUp(i);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IHVACController
    public void setHVACWindSpeedDown() throws Exception {
        throw new IllegalAccessException("not support");
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IHVACController
    public void setHVACWindSpeedDown(int i) throws Exception {
        this.mCarHVACManager.setHvacWindSpeedDown(i);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IHVACController
    public int getHVACWindSpeedLevel() throws Exception {
        return this.mCarHVACManager.getHvacWindSpeedLevel();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IHVACController
    public void setHVACQualityPurgeMode() throws Exception {
        throw new IllegalAccessException("not support");
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IHVACController
    public void setHVACQualityPurgeMode(boolean z) throws Exception {
        this.mCarHVACManager.setHvacQualityPurgeMode(z ? 1 : 0);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IHVACController
    public boolean getHVACQualityPurgeMode() throws Exception {
        return this.mCarHVACManager.getHvacQualityPurgeMode() == 1;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IHVACController
    public int getHVACQualityInnerPM25Value() throws Exception {
        return this.mCarHVACManager.getHvacQualityInnerPm25Value();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IHVACController
    public int getHVACQualityOutsideStatus() throws Exception {
        return this.mCarHVACManager.getHvacQualityOutsideStatus();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IHVACController
    public int getHVACQualityOutsideLevel() throws Exception {
        return this.mCarHVACManager.getHvacQualityOutsideLevel();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IHVACController
    public float getHVACInnerTemp() throws Exception {
        return this.mCarHVACManager.getHvacInnerTemp();
    }
}

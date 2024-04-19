package com.xiaopeng.lib.framework.carcontrollermodule.controller;

import android.car.Car;
import android.car.CarNotConnectedException;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.esp.CarEspManager;
import android.car.hardware.vcu.CarVcuManager;
import android.util.Log;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.IEventMsg;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController;
import java.util.List;
/* loaded from: classes2.dex */
public class VcuController extends AbstractController implements IVcuController {
    private static final String TAG = "VcuController";
    private CarEspManager mCarEspManager;
    private CarVcuManager.CarVcuEventCallback mCarVcuEventCallback;
    private CarVcuManager mCarVcuManager;

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController
    public float getAverageVehConsume100km() throws Exception {
        return 0.0f;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController
    public int[] getBatteryPtcErrorInfo() throws Exception {
        return new int[0];
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController
    public int getCcsWorkError() throws Exception {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController
    public int getChargeSpeedLevel() throws Exception {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController
    public int[] getChargingMaxSoc() throws Exception {
        return null;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController
    public int getDriverOverride() throws Exception {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController
    public int getEnergyRecycle() throws Exception {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController
    public int getGearWarningInfo() throws Exception {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController
    public int[] getTcmFailReason() throws Exception {
        return new int[0];
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController
    public boolean isAgsError() throws Exception {
        return false;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController
    public boolean isBatteryOverheadtingWarning() throws Exception {
        return false;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController
    public boolean isBatteryOverheating() throws Exception {
        return false;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController
    public boolean isBmsError() throws Exception {
        return false;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController
    public boolean isBmsScoLow() throws Exception {
        return false;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController
    public boolean isDcdcError() throws Exception {
        return false;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController
    public boolean isEbsError() throws Exception {
        return false;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController
    public boolean isElectricMotorSystemOverheating() throws Exception {
        return false;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController
    public boolean isElectricVacuumPumpError() throws Exception {
        return false;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController
    public boolean isHvCutoff() throws Exception {
        return false;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController
    public boolean isHvRelayAdhesion() throws Exception {
        return false;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController
    public boolean isLowBatteryVoltage() throws Exception {
        return false;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController
    public boolean isPowerSystemErrorLampOn() throws Exception {
        return false;
    }

    public VcuController(Car car) {
        super(car);
        this.mCarVcuEventCallback = null;
    }

    @Override // com.xiaopeng.lib.framework.carcontrollermodule.controller.AbstractController
    public void initCarManager(Car car) {
        this.mCarApiClient = car;
        try {
            this.mCarVcuManager = (CarVcuManager) car.getCarManager(Car.XP_VCU_SERVICE);
            this.mCarEspManager = (CarEspManager) car.getCarManager(Car.XP_ESP_SERVICE);
        } catch (CarNotConnectedException | IllegalStateException unused) {
            Log.e(TAG, "Car not connected");
        }
    }

    @Override // com.xiaopeng.lib.framework.carcontrollermodule.controller.AbstractController
    public void initPropertyTypeMap() {
        this.mPropertyTypeMap.put(557847082, IVcuController.DrivingModeEventMsg.class);
        this.mPropertyTypeMap.put(557847041, IVcuController.EnergyRecycleLevelEventMsg.class);
        this.mPropertyTypeMap.put(557847045, IVcuController.CarGearLevelEventMsg.class);
        this.mPropertyTypeMap.put(557847072, IVcuController.ChargeErrorEventMsg.class);
        this.mPropertyTypeMap.put(557847080, IVcuController.ChargeGunEventMsg.class);
        this.mPropertyTypeMap.put(557847081, IVcuController.ChargeStatusEventMsg.class);
        this.mPropertyTypeMap.put(557847057, IVcuController.AvailableDrivingDistanceEventMsg.class);
        this.mPropertyTypeMap.put(557847042, IVcuController.ElectriPercentEventMsg.class);
        this.mPropertyTypeMap.put(557847049, IVcuController.ColdWarningTipsEventMsg.class);
        this.mPropertyTypeMap.put(559944226, IVcuController.HvacConsumeEventMsg.class);
        this.mPropertyTypeMap.put(557847050, IVcuController.BatteryWarmmingEventMsg.class);
        this.mPropertyTypeMap.put(557847084, IVcuController.ChargeCompleteTimeEventMsg.class);
        this.mPropertyTypeMap.put(557847071, IVcuController.AcInputStatusEvnetMsg.class);
        this.mPropertyTypeMap.put(559944219, IVcuController.AverageVehConusmeEventMsg.class);
        this.mPropertyTypeMap.put(1, IVcuController.ChargeLimitEventMsg.class);
        this.mPropertyTypeMap.put(559944225, IVcuController.SohEventMsg.class);
        this.mPropertyTypeMap.put(559944228, IVcuController.BatteryMinTemperatureEventMsg.class);
        this.mPropertyTypeMap.put(557847083, IVcuController.SupDebugInfoEventMsg.class);
        this.mPropertyTypeMap.put(557847070, IVcuController.ErhDebugInfoEventMsg.class);
        this.mPropertyTypeMap.put(557847078, IVcuController.EbsBatterySocEventMsg.class);
        this.mPropertyTypeMap.put(559944229, IVcuController.RawCarSpeedEventMsg.class);
        this.mPropertyTypeMap.put(557847079, IVcuController.PureDriveModeFeedbackEventMsg.class);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ILifeCycle
    public void registerCanEventMsg(List<Class<? extends IEventMsg>> list) {
        if (this.mCarVcuEventCallback == null) {
            this.mCarVcuEventCallback = new CarVcuManager.CarVcuEventCallback() { // from class: com.xiaopeng.lib.framework.carcontrollermodule.controller.VcuController.1
                @Override // android.car.hardware.CarEcuManager.CarEcuEventCallback
                public void onChangeEvent(CarPropertyValue carPropertyValue) {
                    VcuController vcuController = VcuController.this;
                    vcuController.postEventBusMsg(vcuController.mPropertyTypeMap.get(Integer.valueOf(carPropertyValue.getPropertyId())), carPropertyValue);
                }

                @Override // android.car.hardware.CarEcuManager.CarEcuEventCallback
                public void onErrorEvent(int i, int i2) {
                    Log.e(VcuController.TAG, "propertyId = " + i + " zone = " + i2);
                }
            };
        }
        try {
            this.mCarVcuManager.registerPropCallback(convertRegisterPropertyList(list), this.mCarVcuEventCallback);
        } catch (Exception unused) {
            Log.e(getClass().getSimpleName(), "Car not connected");
        }
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ILifeCycle
    public void unregisterCanEventMsg(List<Class<? extends IEventMsg>> list) {
        if (this.mCarVcuEventCallback == null) {
            return;
        }
        try {
            this.mCarVcuManager.unregisterPropCallback(convertUnregisterPropertyList(list), this.mCarVcuEventCallback);
        } catch (CarNotConnectedException e) {
            e.printStackTrace();
        }
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController
    public int getDriveMode() throws Exception {
        return this.mCarVcuManager.getDrivingMode();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController
    public void setEnergyRecycle(int i) throws Exception {
        this.mCarVcuManager.setEnergyRecycleLevel(i);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController
    public int getStallState() throws Exception {
        return this.mCarVcuManager.getStallState();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController
    public void startCharge(int i) throws Exception {
        throw new IllegalAccessException("not support anymore");
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController
    public void stopACCharge(int i) throws Exception {
        throw new IllegalAccessException("not support anymore");
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController
    public void stopDCCharging() throws Exception {
        throw new IllegalAccessException("not support anymore");
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController
    public void setBestCharging() throws Exception {
        throw new IllegalAccessException("not support anymore");
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController
    public void setFullyCharging() throws Exception {
        throw new IllegalAccessException("not support anymore");
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController
    public void setChargingLimit(int i) throws Exception {
        throw new IllegalAccessException("not support anymore");
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController
    public int getChargingError() throws Exception {
        return this.mCarVcuManager.getChargingError();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController
    public int getChargingGunStatus() throws Exception {
        return this.mCarVcuManager.getChargingGunStatus();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController
    public int getChargeStatus() throws Exception {
        return this.mCarVcuManager.getChargeStatus();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController
    public int getMileageNumber() throws Exception {
        return this.mCarVcuManager.getAvalibleDrivingDistance();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController
    public float getElectricityPercent() throws Exception {
        return this.mCarVcuManager.getElectricityPercent() / 100;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController
    public boolean getColdWarningTips() throws Exception {
        return this.mCarVcuManager.getColdWarningTips() == 1;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController
    public float getHvacConsume() throws Exception {
        return this.mCarVcuManager.getHvacConsume();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController
    public boolean getBatteryWarmingStatus() throws Exception {
        return this.mCarVcuManager.getBatteryWarmingStatus() == 1;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController
    public float getChargingCompleteTime() throws Exception {
        return this.mCarVcuManager.getChargeCompleteTime();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController
    public float getACChargingCurrent() throws Exception {
        throw new IllegalAccessException("not support");
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController
    public float getACChargingVolt() throws Exception {
        throw new IllegalAccessException("not support");
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController
    public float getDCChargingCurrent() throws Exception {
        throw new IllegalAccessException("not support");
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController
    public float getDCChargingVolt() throws Exception {
        throw new IllegalAccessException("not support");
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController
    public boolean getACInputStatus() throws Exception {
        return this.mCarVcuManager.getAcInputStatus() == 1;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController
    public float getAverageVehConsume() throws Exception {
        return this.mCarVcuManager.getAverageVehConsume();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController
    public float getCarSpeed() throws Exception {
        return this.mCarEspManager.getCarSpeed();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController
    public float getSOH() throws Exception {
        return this.mCarVcuManager.getSoh();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController
    public int getMaxMileage(int i) throws Exception {
        return this.mCarVcuManager.getAvalibleDrivingDistance();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController
    public float getBatteryMinTemperature() throws Exception {
        return this.mCarVcuManager.getBatteryMinTemperature();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController
    public int getGearLever() throws Exception {
        return this.mCarVcuManager.getStallState();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController
    public int getSystemReady() throws Exception {
        return this.mCarVcuManager.getEvSysReady();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController
    public int getVcuSupDebugInfo() throws Exception {
        return this.mCarVcuManager.getVcuSupDebugInfo();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController
    public int getVcuErhDebubInfo() throws Exception {
        return this.mCarVcuManager.getVcuErhDebugInfo();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController
    public int getEbsBatterySoc() throws Exception {
        return this.mCarVcuManager.getEbsBatterySoc();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController
    public int getDriveTravel() throws Exception {
        throw new IllegalAccessException("not support");
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController
    public int getBreakTravel() throws Exception {
        throw new IllegalAccessException("not support");
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController
    public boolean getBatteryCoolStatus() throws Exception {
        throw new IllegalAccessException("getBatteryCoolStatus not support");
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController
    public float getRawCarSpeed() throws Exception {
        return this.mCarVcuManager.getRawCarSpeed();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController
    public int getPureDriveModeFeedback() throws Exception {
        return this.mCarVcuManager.getPureDriveModeFeedback();
    }
}

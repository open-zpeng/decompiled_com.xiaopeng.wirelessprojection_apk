package com.xiaopeng.lib.framework.carcontrollermodule.controller;

import android.car.Car;
import android.car.CarNotConnectedException;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.bcm.CarBcmManager;
import android.util.Log;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.IEventMsg;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.v2extend.IBcmController;
import java.util.List;
/* loaded from: classes2.dex */
public class BcmController extends AbstractController implements IBcmController {
    private static final int INTERVAL_SEND_WINDOWS_COMMAND = 300;
    private static final int STATE_SEAT = 1;
    private static final int STATE_UNSEAT = 0;
    private static final String TAG = "BcmController";
    private CarBcmManager.CarBcmEventCallback mCarBcmEventCallback;
    private CarBcmManager mCarBcmManager;

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public int getBcmFrontWiperOutputStatus() throws Exception {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public boolean getChargeGunLockSt() throws Exception {
        return false;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public boolean getFrontBonnetStatus() throws Exception {
        return false;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public int[] getMsmErrorInfo() throws Exception {
        return new int[0];
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public boolean getPollingOpenCfg() throws Exception {
        return false;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public Integer getRearViewAutoDownCfg() throws Exception {
        return null;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public int getSeatFrHeatLevel() throws Exception {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public int getWinLockStatus() throws Exception {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public int getWiperIntermittentMode() throws Exception {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public boolean isAirbagFault() throws Exception {
        return false;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public boolean isHighBeamFail() throws Exception {
        return false;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public boolean isLeftTurnLampFail() throws Exception {
        return false;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public boolean isLowBeamFail() throws Exception {
        return false;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public boolean isRightTurnLampFail() throws Exception {
        return false;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public boolean isSystemError() throws Exception {
        return false;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public boolean isWasherFluidWarning() throws Exception {
        return false;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public void setBackWindows(boolean z) {
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public void setLeftBackWindow(boolean z) throws Exception {
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public void setPollingOpenCfg(boolean z) throws Exception {
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public void setRearViewAutoDownCfg(int i) throws Exception {
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public void setRightBackWindow(boolean z) throws Exception {
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public void setSeatFrHeatLevel(int i) throws Exception {
    }

    public BcmController(Car car) {
        super(car);
        this.mCarBcmEventCallback = null;
    }

    @Override // com.xiaopeng.lib.framework.carcontrollermodule.controller.AbstractController
    public void initCarManager(Car car) {
        this.mCarApiClient = car;
        try {
            this.mCarBcmManager = (CarBcmManager) car.getCarManager(Car.XP_BCM_SERVICE);
        } catch (CarNotConnectedException | IllegalStateException unused) {
            Log.e(TAG, "Car not connected");
        }
    }

    @Override // com.xiaopeng.lib.framework.carcontrollermodule.controller.AbstractController
    public void initPropertyTypeMap() {
        this.mPropertyTypeMap.put(557849627, IBcmController.LightMeHomeEventMsg.class);
        this.mPropertyTypeMap.put(557849602, IBcmController.RearFogLampEventMsg.class);
        this.mPropertyTypeMap.put(289410561, IBcmController.FarLampEventMsg.class);
        this.mPropertyTypeMap.put(557849712, IBcmController.InternalLightEventMsg.class);
        this.mPropertyTypeMap.put(557849631, IBcmController.EmergencyBrakeWarningEventMsg.class);
        this.mPropertyTypeMap.put(557849647, IBcmController.AtwsStateEventMsg.class);
        this.mPropertyTypeMap.put(557849628, IBcmController.DriveAutoLockEventMsg.class);
        this.mPropertyTypeMap.put(557849629, IBcmController.ParkingAutoUnlockEventMsg.class);
        this.mPropertyTypeMap.put(557849609, IBcmController.DoorLockStateEventMsg.class);
        this.mPropertyTypeMap.put(557849610, IBcmController.TrunkStateEventMsg.class);
        this.mPropertyTypeMap.put(557849601, IBcmController.ChairWelcomeModeEventMsg.class);
        this.mPropertyTypeMap.put(557849635, IBcmController.ElectricSeatBeltEventMsg.class);
        this.mPropertyTypeMap.put(557849636, IBcmController.RearSeatBeltWarningEventMsg.class);
        this.mPropertyTypeMap.put(557849630, IBcmController.UnlockResponseEventMsg.class);
        this.mPropertyTypeMap.put(557915161, IBcmController.DoorsStateEventMsg.class);
        this.mPropertyTypeMap.put(560012320, IBcmController.WindowsStateEventMsg.class);
        this.mPropertyTypeMap.put(557849633, IBcmController.NearLampStateEventMsg.class);
        this.mPropertyTypeMap.put(557849626, IBcmController.LocationLampStateEventMsg.class);
        this.mPropertyTypeMap.put(557849637, IBcmController.BCMBackDefrostModeEventMsg.class);
        this.mPropertyTypeMap.put(557849665, IBcmController.BCMBackMirrorHeatModeEventMsg.class);
        this.mPropertyTypeMap.put(557849638, IBcmController.BCMSeatHeatLevelEventMsg.class);
        this.mPropertyTypeMap.put(356517139, IBcmController.BCMSeatBlowLevelEventMsg.class);
        this.mPropertyTypeMap.put(557849607, IBcmController.DriveSeatStateEventMsg.class);
        this.mPropertyTypeMap.put(557849644, IBcmController.WelcomeModeBackStatusEventMsg.class);
        this.mPropertyTypeMap.put(557849641, IBcmController.FrontBonnetStatusEventMsg.class);
        this.mPropertyTypeMap.put(557849648, IBcmController.DriverBeltWarningEventMsgV2.class);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ILifeCycle
    public void registerCanEventMsg(List<Class<? extends IEventMsg>> list) {
        if (this.mCarBcmEventCallback == null) {
            this.mCarBcmEventCallback = new CarBcmManager.CarBcmEventCallback() { // from class: com.xiaopeng.lib.framework.carcontrollermodule.controller.BcmController.1
                @Override // android.car.hardware.CarEcuManager.CarEcuEventCallback
                public void onChangeEvent(CarPropertyValue carPropertyValue) {
                    Log.d(BcmController.TAG, "onChangeEvent bcm = " + carPropertyValue.toString());
                    BcmController bcmController = BcmController.this;
                    bcmController.postEventBusMsg(bcmController.mPropertyTypeMap.get(Integer.valueOf(carPropertyValue.getPropertyId())), carPropertyValue);
                }

                @Override // android.car.hardware.CarEcuManager.CarEcuEventCallback
                public void onErrorEvent(int i, int i2) {
                    Log.e(getClass().getSimpleName(), "propertyId = " + i + " zone = " + i2);
                }
            };
        }
        try {
            this.mCarBcmManager.registerPropCallback(convertRegisterPropertyList(list), this.mCarBcmEventCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ILifeCycle
    public void unregisterCanEventMsg(List<Class<? extends IEventMsg>> list) {
        if (this.mCarBcmEventCallback == null) {
            return;
        }
        try {
            this.mCarBcmManager.unregisterPropCallback(convertUnregisterPropertyList(list), this.mCarBcmEventCallback);
        } catch (CarNotConnectedException e) {
            e.printStackTrace();
        }
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public void setLightMeHome(boolean z) throws Exception {
        if (z) {
            this.mCarBcmManager.setLightMeHome(3);
        } else {
            this.mCarBcmManager.setLightMeHome(1);
        }
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public int getLightMeHome() throws Exception {
        return this.mCarBcmManager.getLightMeHome();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public boolean getRearFogLamp() throws Exception {
        return this.mCarBcmManager.getRearFogLamp() == 1;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public void setChargingPortUnlock() throws Exception {
        this.mCarBcmManager.setChargePortUnlock(0, 0);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public void setChargeGunLock() throws Exception {
        throw new IllegalAccessException("not support any more!");
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public boolean getChargePortStatus() throws Exception {
        this.mCarBcmManager.getChargePortStatus(0);
        return false;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public void setRearFogLamp(boolean z) throws Exception {
        this.mCarBcmManager.setRearFogLamp(z ? 1 : 0);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public void setRearViewMirror(int i) throws Exception {
        this.mCarBcmManager.setRearViewMirror(i);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public void setHeadLampGroup(int i) throws Exception {
        this.mCarBcmManager.setHeadLampGroup(i);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public int getHeadLampGroup() throws Exception {
        return this.mCarBcmManager.getHeadLampGroup();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public boolean getFarLampState() throws Exception {
        return this.mCarBcmManager.getFarLampState() == 1;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public void setInternalLight(boolean z) throws Exception {
        this.mCarBcmManager.setInternalLight(z ? 1 : 0);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public boolean getInternalLight() throws Exception {
        return this.mCarBcmManager.getInternalLight() == 1;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public void setEmergencyBrakeWarning(boolean z) throws Exception {
        this.mCarBcmManager.setEmergencyBrakeWarning(z ? 1 : 0);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public boolean getEmergencyBrakeWarning() throws Exception {
        return this.mCarBcmManager.getEmergencyBrakeWarning() == 1;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public void setAllWindowManualOrAuto(int i) throws Exception {
        this.mCarBcmManager.setAllWindowManualOrAuto(i);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public int getATWSState() throws Exception {
        return this.mCarBcmManager.getAtwsState();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public int getOled() throws Exception {
        throw new IllegalAccessException("not support any more!");
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public void setOled(int i) throws Exception {
        throw new IllegalAccessException("not support any more!");
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    @Deprecated
    public void setDriverWindowAuto(boolean z) throws Exception {
        throw new IllegalAccessException("not support any more!");
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public void setCopilotWindowAuto(boolean z) throws Exception {
        throw new IllegalAccessException("not support any more!");
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public void setDriveAutoLock(boolean z) throws Exception {
        this.mCarBcmManager.setDriveAutoLock(z ? 1 : 0);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public int[] getChairDirection() throws Exception {
        throw new IllegalAccessException("not support any more!");
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public boolean getDriveAutoLock() throws Exception {
        return this.mCarBcmManager.getDriveAutoLock() == 1;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public void setParkingAutoUnlock(boolean z) throws Exception {
        this.mCarBcmManager.setParkingAutoUnlock(z ? 1 : 0);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public boolean getParkingAutoUnlock() throws Exception {
        return this.mCarBcmManager.getParkingAutoUnlock() == 1;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public void setHazardLight(boolean z) throws Exception {
        this.mCarBcmManager.setHazardLight(z ? 1 : 0);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public void setDoorLock() throws Exception {
        this.mCarBcmManager.setDoorLock(1);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public void setDoorUnlocked() throws Exception {
        this.mCarBcmManager.setDoorLock(0);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public boolean getDoorLockState() throws Exception {
        return this.mCarBcmManager.getDoorLockState() == 1;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public void setTrunk(int i) throws Exception {
        this.mCarBcmManager.setTrunk(i);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public int getTrunk() throws Exception {
        return this.mCarBcmManager.getTrunk();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public void setWiperInterval(int i) throws Exception {
        this.mCarBcmManager.setWiperInterval(i);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public void setChairSlowlyAhead(int i) throws Exception {
        throw new IllegalAccessException("not support any more!");
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public void setChairSlowlyBack(int i) throws Exception {
        throw new IllegalAccessException("not support any more!");
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public void setChairSlowlyEnd(int i) throws Exception {
        throw new IllegalAccessException("not support any more!");
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public void setChairPositionStart(int i, int i2, int i3) throws Exception {
        throw new IllegalAccessException("not support any more!");
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public void setChairPositionEnd() throws Exception {
        throw new IllegalAccessException("not support any more!");
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public boolean getChairWelcomeMode() throws Exception {
        return this.mCarBcmManager.getChairWelcomeMode() == 1;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public void setChairWelcomeMode(boolean z) throws Exception {
        this.mCarBcmManager.setChairWelcomeMode(z ? 1 : 0);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public boolean getElectricSeatBelt() throws Exception {
        return this.mCarBcmManager.getElectricSeatBelt() == 1;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public void setElectricSeatBelt(boolean z) throws Exception {
        this.mCarBcmManager.setElectricSeatBelt(z ? 1 : 0);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public boolean getRearSeatBeltWarning() throws Exception {
        return this.mCarBcmManager.getRearSeatBeltWarning() == 1;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public void setRearSeatBeltWarning(boolean z) throws Exception {
        this.mCarBcmManager.setRearSeatBeltWarning(z ? 1 : 0);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public void setUnlockResponse(int i) throws Exception {
        this.mCarBcmManager.setUnlockResponse(i);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public int getUnlockResponse() throws Exception {
        return this.mCarBcmManager.getUnlockResponse();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public void setWheelDefinedButton(int i) throws Exception {
        throw new IllegalAccessException("not support any more!");
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public int[] getDoorsState() throws Exception {
        return this.mCarBcmManager.getDoorsState();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public float[] getWindowsState() throws Exception {
        return this.mCarBcmManager.getWindowsState();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public int[] getChairLocationValue() throws Exception {
        throw new IllegalAccessException("not support any more!");
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public void setBCMBackDefrostMode() throws Exception {
        throw new IllegalAccessException("not support any more!");
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public void setBCMBackDefrostMode(boolean z) throws Exception {
        this.mCarBcmManager.setBcmBackDefrostMode(z ? 1 : 0);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public boolean getBCMBackDefrostMode() throws Exception {
        return this.mCarBcmManager.getBcmBackDefrostMode() == 1;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public void setBCMBackMirrorHeatMode() throws Exception {
        throw new IllegalAccessException("not support any more!");
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public void setBCMBackMirrorHeatMode(boolean z) throws Exception {
        this.mCarBcmManager.setBcmBackMirrorHeatMode(z ? 1 : 0);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public boolean getBCMBackMirrorHeatMode() throws Exception {
        return this.mCarBcmManager.getBcmBackMirrorHeatMode() == 1;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public void setBCMSeatHeatLevel() throws Exception {
        throw new IllegalAccessException("not support any more!");
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public void setBCMSeatHeatLevel(int i) throws Exception {
        this.mCarBcmManager.setBcmSeatHeatLevel(i);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public int getBCMSeatHeatLevel() throws Exception {
        return this.mCarBcmManager.getBcmSeatHeatLevel();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public void setBCMSeatBlowLevel() throws Exception {
        throw new IllegalAccessException("not support any more!");
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public void setBCMSeatBlowLevel(int i) throws Exception {
        this.mCarBcmManager.setBcmSeatBlowLevel(i);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public int getBCMSeatBlowLevel() throws Exception {
        return this.mCarBcmManager.getBcmSeatBlowLevel();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public boolean getLocationLampState() throws Exception {
        return this.mCarBcmManager.getLocationLampState() == 1;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public boolean getNearLampState() throws Exception {
        return this.mCarBcmManager.getNearLampState() == 1;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public int getDriveSeatState() throws Exception {
        return this.mCarBcmManager.getDriverOnSeat();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public int getBCMIgStatus() throws Exception {
        throw new IllegalAccessException("not support any more!");
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public void setFactoryOledData(byte[] bArr) throws Exception {
        throw new IllegalAccessException("not support any more!");
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public void setFactoryOledDisplayMode(int i) throws Exception {
        throw new IllegalAccessException("not support any more!");
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public boolean getSeatErrorState() throws Exception {
        return this.mCarBcmManager.getSeatErrorState() == 1;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public void setVentilate() throws Exception {
        this.mCarBcmManager.setVentilate();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public boolean getWelcomeModeBackStatus() throws Exception {
        return this.mCarBcmManager.getWelcomeModeBackStatus() == 1;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public void setFrontWindows(final boolean z) {
        new Thread(new Runnable() { // from class: com.xiaopeng.lib.framework.carcontrollermodule.controller.BcmController.2
            @Override // java.lang.Runnable
            public void run() {
                try {
                    BcmController.this.setDriverWindowAuto(z);
                    Thread.sleep(300L);
                    BcmController.this.setCopilotWindowAuto(z);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController
    public boolean getDriverBeltWarning() throws Exception {
        return this.mCarBcmManager.getDriverBeltWarning() == 1;
    }
}

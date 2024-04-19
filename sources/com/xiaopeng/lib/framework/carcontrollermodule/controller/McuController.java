package com.xiaopeng.lib.framework.carcontrollermodule.controller;

import android.car.Car;
import android.car.CarNotConnectedException;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.mcu.CarMcuManager;
import android.util.Log;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.IEventMsg;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.v2extend.IMcuController;
import java.util.List;
/* loaded from: classes2.dex */
public class McuController extends AbstractController implements IMcuController {
    public static final String CAR_STAGE_A = "A";
    public static final String CAR_STAGE_B = "B";
    public static final String CAR_STAGE_C = "C";
    public static final String CAR_STAGE_D = "D";
    public static final String CAR_TYPE_D10 = "D10";
    public static final String CAR_TYPE_D20 = "D20";
    public static final String CAR_TYPE_D21 = "D21";
    public static final String CAR_TYPE_E28 = "E28";
    public static final String CAR_TYPE_E36 = "E36";
    private static final String KEY_HARDWARE_VERSION = "ro.boot.hw_version";
    private static final String KEY_SOFTWARE_VERSION_PROPERTY = "ro.build.display.id";
    private static final String TAG = "McuController";
    private CarMcuManager.CarMcuEventCallback mCarMcuEventCallback;
    private CarMcuManager mCarMcuManager;

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public int getAutoPowerOffMode() throws Exception {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public int[] getDtcReportEV() throws Exception {
        return new int[0];
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public String getFactoryDisplayTypeMsgToMcu() throws Exception {
        return "";
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public int[] getFactoryDugReqMsgToMcu() throws Exception {
        return new int[0];
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public int getFactoryPmSilentMsgToMcu() throws Exception {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public int[] getFactoryPwrDebugMsgToMcu() throws Exception {
        return new int[0];
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public int[] getIgStatus() throws Exception {
        return new int[0];
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public int getMcu4GErrorStatus() throws Exception {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public int getMcuChargeComplete() throws Exception {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public byte[] getMcuFaultInfo() throws Exception {
        return new byte[0];
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public int getMcuRequestSavingQxdmLog() throws Exception {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public int getMcuTheftState() {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public int getMcuVentilateState() throws Exception {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public int getPmStatus() throws Exception {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public int getPowerOffCountdownNotice() throws Exception {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public String getRoadAttribute(int i, int i2) throws Exception {
        return null;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public int[] getShockInfo() throws Exception {
        return new int[0];
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public int getShockSwMode() throws Exception {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public void sendToMcuAppReadCfg(int i, int[] iArr) throws Exception {
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public void sendToMcuAppWriteCfg(int i, int i2, int i3) throws Exception {
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public void setAutoPowerOff(int i) throws Exception {
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public void setChairSlowlyMove(int[] iArr) throws Exception {
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public void setLampCommand(int i) throws Exception {
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public void setPowerOffCountdownAction(int i) throws Exception {
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public void setRearFogCommand(int i) throws Exception {
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public void setSeatControlCommand(int i, int i2) throws Exception {
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public void setShockHandleCmd(int i) throws Exception {
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public void setShockSwMode(int i) throws Exception {
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public void setShockValue2Mcu(int i) throws Exception {
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public void setWindowsCommand(int i) throws Exception {
    }

    public McuController(Car car) {
        super(car);
        this.mCarMcuEventCallback = null;
    }

    @Override // com.xiaopeng.lib.framework.carcontrollermodule.controller.AbstractController
    public void initCarManager(Car car) {
        this.mCarApiClient = car;
        try {
            this.mCarMcuManager = (CarMcuManager) car.getCarManager(Car.XP_MCU_SERVICE);
        } catch (CarNotConnectedException | IllegalStateException unused) {
            Log.e(TAG, "Car not connected");
        }
    }

    @Override // com.xiaopeng.lib.framework.carcontrollermodule.controller.AbstractController
    public void initPropertyTypeMap() {
        this.mPropertyTypeMap.put(356516106, IMcuController.FactoryPwrDebugMsgToMcuEventMsg.class);
        this.mPropertyTypeMap.put(356516106, IMcuController.FactoryDugReqMsgToMcuEventMsg.class);
        this.mPropertyTypeMap.put(554701854, IMcuController.FactoryDisplayTypeMsgToMcuEventMsg.class);
        this.mPropertyTypeMap.put(356516106, IMcuController.FactoryMcuBmsMsgToMcuEventMsg.class);
        this.mPropertyTypeMap.put(356516106, IMcuController.FactorySecretKeyToMcuEventMsg.class);
        this.mPropertyTypeMap.put(356516106, IMcuController.FactoryTestMsgToMcuEventMsg.class);
        this.mPropertyTypeMap.put(557913151, IMcuController.McuPsuOtaEventMsg.class);
        this.mPropertyTypeMap.put(356516106, IMcuController.McuOta1EventMsg.class);
        this.mPropertyTypeMap.put(356516106, IMcuController.BurnProgressEventMsg.class);
        this.mPropertyTypeMap.put(356516106, IMcuController.BurnFinishEventMsg.class);
        this.mPropertyTypeMap.put(356516106, IMcuController.BurnErrorEventMsg.class);
        this.mPropertyTypeMap.put(356516106, IMcuController.ChargeInfoEventMsg.class);
        this.mPropertyTypeMap.put(356516106, IMcuController.McuGpsInfoEventMsg.class);
        this.mPropertyTypeMap.put(560993287, IMcuController.McuFaultInfoEventMsg.class);
        this.mPropertyTypeMap.put(356516106, IMcuController.McuTheftStateEventMsg.class);
        this.mPropertyTypeMap.put(356516106, IMcuController.McuReset4gEventMsg.class);
        this.mPropertyTypeMap.put(356516106, IMcuController.McuAckPwrDebugEventMsg.class);
        this.mPropertyTypeMap.put(560993311, IMcuController.McuGsensorOffsetEventMsg.class);
        this.mPropertyTypeMap.put(557847561, IMcuController.McuIgStatusEventMsg.class);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ILifeCycle
    public void registerCanEventMsg(List<Class<? extends IEventMsg>> list) {
        if (this.mCarMcuEventCallback == null) {
            this.mCarMcuEventCallback = new CarMcuManager.CarMcuEventCallback() { // from class: com.xiaopeng.lib.framework.carcontrollermodule.controller.McuController.1
                @Override // android.car.hardware.CarEcuManager.CarEcuEventCallback
                public void onChangeEvent(CarPropertyValue carPropertyValue) {
                    McuController mcuController = McuController.this;
                    mcuController.postEventBusMsg(mcuController.mPropertyTypeMap.get(Integer.valueOf(carPropertyValue.getPropertyId())), carPropertyValue);
                }

                @Override // android.car.hardware.CarEcuManager.CarEcuEventCallback
                public void onErrorEvent(int i, int i2) {
                    Log.e(getClass().getSimpleName(), "propertyId = " + i + " zone = " + i2);
                }
            };
        }
        try {
            this.mCarMcuManager.registerPropCallback(convertRegisterPropertyList(list), this.mCarMcuEventCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ILifeCycle
    public void unregisterCanEventMsg(List<Class<? extends IEventMsg>> list) {
        if (this.mCarMcuEventCallback == null) {
            return;
        }
        try {
            this.mCarMcuManager.unregisterPropCallback(convertUnregisterPropertyList(list), this.mCarMcuEventCallback);
        } catch (CarNotConnectedException e) {
            e.printStackTrace();
        }
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public void setDriveMode(int i) throws Exception {
        this.mCarMcuManager.setDriveMode(i);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public void setHorn(boolean z) throws Exception {
        this.mCarMcuManager.setHorn(z ? 1 : 0);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public void setFlash(boolean z) throws Exception {
        this.mCarMcuManager.setFlash(z ? 1 : 0);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public void sendFactoryTestMsgToMcu(int[] iArr) throws Exception {
        this.mCarMcuManager.sendFactoryTestMsgToMcu(iArr);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public void sendFactoryPwrDebugMsgToMcu(int[] iArr) throws Exception {
        this.mCarMcuManager.sendFactoryPwrDebugMsgToMcu(iArr);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public int getHardwareVersion() {
        try {
            return this.mCarMcuManager.getHardwareVersion();
        } catch (Exception unused) {
            return 1;
        }
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public void sendFactoryDugReqMsgToMcu(int[] iArr) throws Exception {
        this.mCarMcuManager.sendFactoryDugReqMsgToMcu(iArr);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public void sendFactoryDisplayTypeMsgToMcu(int i) throws Exception {
        this.mCarMcuManager.sendFactoryDisplayTypeMsgToMcu(i);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public void sendFactoryPmSilentMsgToMcu(int i) throws Exception {
        this.mCarMcuManager.sendFactoryPmSilentMsgToMcu(i);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public void sendFactoryMcuBmsMsgToMcu(int i) throws Exception {
        this.mCarMcuManager.sendFactoryMcuBmsMsgToMcu(i);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public int getFactoryMcuBmsMsgToMcu() throws Exception {
        return this.mCarMcuManager.getIntProperty(356516106, 0);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public void sendFactorySecretKeyToMcu(byte[] bArr) throws Exception {
        this.mCarMcuManager.sendFactorySecretKeyToMcu(bArr);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public void sendOta1MsgToMcu(int[] iArr) throws Exception {
        this.mCarMcuManager.sendOta1MsgToMcu(iArr);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public void sendPsuOtaMsgToMcu(int[] iArr) throws Exception {
        this.mCarMcuManager.sendPsuOtaMsgToMcu(iArr);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public void sendGpsInfoMsgToMcu(int[] iArr) throws Exception {
        this.mCarMcuManager.sendGpsInfoMsgToMcu(iArr);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public void updateMcu(String str) throws Exception {
        this.mCarMcuManager.updateMcuBin(str);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public int getCpuTemperature() throws Exception {
        return this.mCarMcuManager.getIntProperty(559944801, 0);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public String getMcuHardwareId() throws Exception {
        return this.mCarMcuManager.getMcuHardwareId();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public void setMqttLogInfo(String str, String str2, String str3, String str4) throws Exception {
        this.mCarMcuManager.setMqttLogInfo(str, str2, str3, str4);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public void setIgOn() throws Exception {
        this.mCarMcuManager.setIgOn();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public void setIgOff() throws Exception {
        this.mCarMcuManager.setIgOff();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public void setIgHeartBeat() throws Exception {
        this.mCarMcuManager.setIgHeartBeat();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public void setTheftHeartBeatOn() throws Exception {
        this.mCarMcuManager.setTheftHeartBeatOn();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public void setTheftHeartBeatOff() throws Exception {
        this.mCarMcuManager.setTheftHeartBeatOff();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public void setMcuIsWakeUpByPhone(boolean z) throws Exception {
        this.mCarMcuManager.setMcuIsWakeUpByPhone(z ? 1 : 0);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public void sendRequestWakeToMcu(int i) throws Exception {
        this.mCarMcuManager.sendRequestWakeToMcu(i);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public void sendDiagnoseMsgToMcu(int[] iArr) throws Exception {
        this.mCarMcuManager.sendDiagnoseMsgToMcu(iArr);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public void sendReset4gMsgToMcu(int[] iArr) throws Exception {
        this.mCarMcuManager.sendReset4gMsgToMcu(iArr);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public void sendResetModemMsgToMcu(int i) throws Exception {
        this.mCarMcuManager.sendResetModemMsgToMcu(i);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public String getHardwareCarType() throws Exception {
        return this.mCarMcuManager.getHardwareCarType();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public String getHardwareCarStage() throws Exception {
        return this.mCarMcuManager.getHardwareCarStage();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public int[] getGSensorOffset() throws Exception {
        return this.mCarMcuManager.getGSensorOffset();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public void setRepairMode(boolean z) throws Exception {
        this.mCarMcuManager.setRepairMode(z ? 1 : 0);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public void sendChargeCompleteTime2Mcu(int i) throws Exception {
        this.mCarMcuManager.setIntProperty(356516106, 0, i);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController
    public String getXpCduType() throws Exception {
        return this.mCarMcuManager.getXpCduType();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.v2extend.IMcuController
    public int getIgStatusFromMcu() throws Exception {
        return this.mCarMcuManager.getIgStatusFromMcu();
    }
}

package com.xiaopeng.lib.framework.carcontrollermodule.controller;

import android.car.Car;
import android.car.CarNotConnectedException;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.scu.CarScuManager;
import android.util.Log;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.IEventMsg;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.v2extend.IScuController;
import java.util.List;
/* loaded from: classes2.dex */
public class ScuController extends AbstractController implements IScuController {
    private static final String TAG = "ScuController";
    private CarScuManager.CarScuEventCallback mCarScuEventCallback;
    private CarScuManager mCarScuManager;

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public int getAccExitReason() throws Exception {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public int getAccLkaWarning() throws Exception {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public int getAebAlarmSwitchState() throws Exception {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public byte[] getAltimeter() throws Exception {
        return new byte[0];
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public boolean getAssLineChanged() throws Exception {
        return false;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public int getAutoRoadTips() throws Exception {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public int[] getFrontCameraFault() throws Exception {
        return new int[0];
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public int getFrontMmRadarFault() throws Exception {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public int getIcmAlarmFaultState() throws Exception {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public boolean getKeyPark() throws Exception {
        return false;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public int getLccExitReason() throws Exception {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public int getModeIndex() throws Exception {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public float[] getParkingProcessPath() throws Exception {
        return new float[0];
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public int[] getRearMmRadarFault() throws Exception {
        return new int[0];
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public int getRoadVoiceTips() throws Exception {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public int[] getScu322LogData() throws Exception {
        return new int[0];
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public int[] getScu3FDExtendLogData() throws Exception {
        return new int[0];
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public int getScuModeIndex() throws Exception {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public int getScuSteerWaringLvl() throws Exception {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public byte[] getSlotTheta() throws Exception {
        return new byte[0];
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public byte[] getTargetParkingPosition() throws Exception {
        return new byte[0];
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public void setAssLineChanged(int i) throws Exception {
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public void setDetailRoadClass(int i) throws Exception {
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public void setDoorOpenWarningSwitch(int i) throws Exception {
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public void setKeyPark(boolean z) throws Exception {
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public void setLaneMiddleAssist(int i) throws Exception {
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public void setLocalWeather(int i, int i2, int i3, int i4) throws Exception {
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public void setRoadAttributes(int i, int i2) throws Exception {
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public void setScuDmsMode(int i) throws Exception {
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public void setScuOtaTagStatus(int i) throws Exception {
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public void setScuRoadAttr(int i) throws Exception {
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public void setSeatBeltReq(int i) throws Exception {
    }

    public ScuController(Car car) {
        super(car);
        this.mCarScuEventCallback = null;
    }

    @Override // com.xiaopeng.lib.framework.carcontrollermodule.controller.AbstractController
    public void initCarManager(Car car) {
        this.mCarApiClient = car;
        try {
            this.mCarScuManager = (CarScuManager) car.getCarManager(Car.XP_SCU_SERVICE);
        } catch (CarNotConnectedException | IllegalStateException unused) {
            Log.e(TAG, "Car not connected");
        }
    }

    @Override // com.xiaopeng.lib.framework.carcontrollermodule.controller.AbstractController
    public void initPropertyTypeMap() {
        this.mPropertyTypeMap.put(557852161, IScuController.FrontCollisionSecurityEventMsg.class);
        this.mPropertyTypeMap.put(557852173, IScuController.IntelligentSpeedLimitEventMsg.class);
        this.mPropertyTypeMap.put(557852169, IScuController.LaneChangeAssistEventMsg.class);
        this.mPropertyTypeMap.put(557852202, IScuController.SideReversingWarningEventMsg.class);
        this.mPropertyTypeMap.put(557852162, IScuController.LaneDepartureWarningEventMsg.class);
        this.mPropertyTypeMap.put(557852165, IScuController.BlindAreaDetectionWarningEventMsg.class);
        this.mPropertyTypeMap.put(557852177, IScuController.RadarWarningVoiceStatusEventMsg.class);
        this.mPropertyTypeMap.put(557917783, IScuController.FactoryScuTest322EventMsg.class);
        this.mPropertyTypeMap.put(557917784, IScuController.FactoryScuTest3FDEventMsg.class);
        this.mPropertyTypeMap.put(557917785, IScuController.FactoryScuTest3FEEventMsg.class);
        this.mPropertyTypeMap.put(557852187, IScuController.ScuOperationTipUpdateEventMsg.class);
        this.mPropertyTypeMap.put(557852207, IScuController.ScuSuperParkActiveResponeEventMsg.class);
        this.mPropertyTypeMap.put(560014910, IScuController.V2ScuSlot1UpdateEventMsg.class);
        this.mPropertyTypeMap.put(560014894, IScuController.V2ScuSlot2UpdateEventMsg.class);
        this.mPropertyTypeMap.put(560014903, IScuController.V2ScuSlot3UpdateEventMsg.class);
        this.mPropertyTypeMap.put(560014899, IScuController.V2ScuSlot4UpdateEventMsg.class);
        this.mPropertyTypeMap.put(560014909, IScuController.V2ScuSlot5UpdateEventMsg.class);
        this.mPropertyTypeMap.put(557917763, IScuController.V2SlotThetaEventMsg.class);
        this.mPropertyTypeMap.put(557917739, IScuController.ScuSensorFeature1UpdateEventMsg.class);
        this.mPropertyTypeMap.put(557917750, IScuController.ScuSensorFeature2UpdateEventMsg.class);
        this.mPropertyTypeMap.put(560014938, IScuController.V2ScuAvmBox1UpdateEventMsg.class);
        this.mPropertyTypeMap.put(560014892, IScuController.V2ScuAvmBox2UpdateEventMsg.class);
        this.mPropertyTypeMap.put(560014911, IScuController.ScuLocationUpdateEventMsg.class);
        this.mPropertyTypeMap.put(560014912, IScuController.ScuLocation2UpdateEventMsg.class);
        this.mPropertyTypeMap.put(557852239, IScuController.ScuReversingDisplayActiveEventMsg.class);
        this.mPropertyTypeMap.put(557852230, IScuController.ScuNearestEnableRadarEventMsg.class);
        this.mPropertyTypeMap.put(557917770, IScuController.V2FrontRadarLevelEventMsg.class);
        this.mPropertyTypeMap.put(557917774, IScuController.V2TailRadarLevelEventMsg.class);
        this.mPropertyTypeMap.put(557917769, IScuController.V2FrontRadarFaultStEventMsg.class);
        this.mPropertyTypeMap.put(557917773, IScuController.V2TailRadarFaultStEventMsg.class);
        this.mPropertyTypeMap.put(560014876, IScuController.V2FrontRadarDataEventMsg.class);
        this.mPropertyTypeMap.put(560014877, IScuController.V2TailRadarDataEventMsg.class);
        this.mPropertyTypeMap.put(557852241, IScuController.ScuFrontMinDistanceEventMsg.class);
        this.mPropertyTypeMap.put(557852242, IScuController.ScuRearMinDistanceEventMsg.class);
        this.mPropertyTypeMap.put(557852205, IScuController.ScuAutoParkErrorCodeEventMsg.class);
        this.mPropertyTypeMap.put(557917782, IScuController.ScuLDWWarningEventMsg.class);
        this.mPropertyTypeMap.put(557917779, IScuController.ScuBSDWarningEventMsg.class);
        this.mPropertyTypeMap.put(557917780, IScuController.ScuRCTAWarningEventMsg.class);
        this.mPropertyTypeMap.put(557917781, IScuController.ScuLKAWarningEventMsg.class);
        this.mPropertyTypeMap.put(557852235, IScuController.ScuModeIndexEventMsg.class);
        this.mPropertyTypeMap.put(560014945, IScuController.ParkingProcessPathEventMsg.class);
        this.mPropertyTypeMap.put(557917764, IScuController.V2TargetParkingPositionEventMsg.class);
        this.mPropertyTypeMap.put(560014914, IScuController.V2AltimeterEventMsg.class);
        this.mPropertyTypeMap.put(557852240, IScuController.RearMirrorCtrl.class);
        this.mPropertyTypeMap.put(557852265, IScuController.AssLineChangedEventMsg.class);
        this.mPropertyTypeMap.put(560014956, IScuController.AllParklotDataEventMsg.class);
        this.mPropertyTypeMap.put(560014942, IScuController.SensorDataUpdateEventMsg.class);
        this.mPropertyTypeMap.put(560014946, IScuController.LeftAvmDataUpdateEventMsg.class);
        this.mPropertyTypeMap.put(560014947, IScuController.RightAvmDataUpdateEventMsg.class);
        this.mPropertyTypeMap.put(560014948, IScuController.SlotForPatkUpdateEventMsg.class);
        this.mPropertyTypeMap.put(557852255, IScuController.ErrorTipsUpdateEventMsg.class);
        this.mPropertyTypeMap.put(557852272, IScuController.FsdSwitchEventMsg.class);
        this.mPropertyTypeMap.put(557852369, IScuController.ParkByMemorySwEventMsg.class);
        this.mPropertyTypeMap.put(557852184, IScuController.AutoParkEventMsg.class);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ILifeCycle
    public void registerCanEventMsg(List<Class<? extends IEventMsg>> list) {
        if (this.mCarScuEventCallback == null) {
            this.mCarScuEventCallback = new CarScuManager.CarScuEventCallback() { // from class: com.xiaopeng.lib.framework.carcontrollermodule.controller.ScuController.1
                @Override // android.car.hardware.CarEcuManager.CarEcuEventCallback
                public void onChangeEvent(CarPropertyValue carPropertyValue) {
                    ScuController scuController = ScuController.this;
                    scuController.postEventBusMsg(scuController.mPropertyTypeMap.get(Integer.valueOf(carPropertyValue.getPropertyId())), carPropertyValue);
                }

                @Override // android.car.hardware.CarEcuManager.CarEcuEventCallback
                public void onErrorEvent(int i, int i2) {
                    Log.e(ScuController.TAG, "propertyId = " + i + " zone = " + i2);
                    ScuController scuController = ScuController.this;
                    scuController.postErrorEventBusMsg(scuController.mPropertyTypeMap.get(Integer.valueOf(i)), i2);
                }
            };
        }
        try {
            this.mCarScuManager.registerPropCallback(convertRegisterPropertyList(list), this.mCarScuEventCallback);
        } catch (Exception unused) {
            Log.e(TAG, "Car not connected");
        }
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ILifeCycle
    public void unregisterCanEventMsg(List<Class<? extends IEventMsg>> list) {
        if (this.mCarScuEventCallback == null) {
            return;
        }
        try {
            this.mCarScuManager.unregisterPropCallback(convertUnregisterPropertyList(list), this.mCarScuEventCallback);
        } catch (CarNotConnectedException e) {
            e.printStackTrace();
        }
    }

    private static int bytesToInt(byte[] bArr, int i) {
        return ((bArr[i + 3] & 255) << 24) | (bArr[i] & 255) | ((bArr[i + 1] & 255) << 8) | ((bArr[i + 2] & 255) << 16);
    }

    private static float byte2float(byte[] bArr, int i) {
        return Float.intBitsToFloat((int) ((bArr[i + 3] << 24) | (((int) ((((int) ((bArr[i + 0] & 255) | (bArr[i + 1] << 8))) & 65535) | (bArr[i + 2] << 16))) & 16777215)));
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public int getFrontCollisionSecurity() throws Exception {
        return this.mCarScuManager.getFrontCollisionSecurity();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public void setFrontCollisionSecurity(int i) throws Exception {
        this.mCarScuManager.setFrontCollisionSecurity(i);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public int getIntelligentSpeedLimit() throws Exception {
        return this.mCarScuManager.getIntelligentSpeedLimit();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public void setIntelligentSpeedLimit(int i) throws Exception {
        this.mCarScuManager.setIntelligentSpeedLimit(i);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public int getLaneChangeAssist() throws Exception {
        return this.mCarScuManager.getLaneChangeAssist();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public void setLaneChangeAssist(int i) throws Exception {
        this.mCarScuManager.setLaneChangeAssist(i);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public int getSideReversingWarning() throws Exception {
        return this.mCarScuManager.getSideReversingWarning();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public void setSideReversingWarning(int i) throws Exception {
        this.mCarScuManager.setSideReversingWarning(i);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public int getLaneDepartureWarning() throws Exception {
        return this.mCarScuManager.getLaneDepartureWarning();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public void setLaneDepartureWarning(int i) throws Exception {
        this.mCarScuManager.setLaneDepartureWarning(i);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public int getBlindAreaDetectionWarning() throws Exception {
        return this.mCarScuManager.getBlindAreaDetectionWarning();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public void setBlindAreaDetectionWarning(int i) throws Exception {
        this.mCarScuManager.setBlindAreaDetectionWarning(i);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public boolean getRadarWarningVoiceStatus() throws Exception {
        return this.mCarScuManager.getRadarWarningVoiceStatus() == 1;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public void setRadarWarningVoiceStatus(boolean z) throws Exception {
        this.mCarScuManager.setRadarWarningVoiceStatus(z ? 1 : 0);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public void setLocationInfo(float f, float f2, float f3, float f4, float f5, long j) throws Exception {
        this.mCarScuManager.setLocationInfo(f, f2, f3, f4, f5, j);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public void setAutoPilotLocationInfo(float f, float f2, float f3, float f4, float f5, float f6, long j) throws Exception {
        this.mCarScuManager.setAutoPilotLocationInfo(f, f2, f3, f4, f5, f6, j);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public void setFactoryScuTest(int i) throws Exception {
        this.mCarScuManager.setFactoryScuTest(i);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public int getParkStatus() throws Exception {
        return this.mCarScuManager.getParkingStatus();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public void setParkLotChoseIndex2Scu(int i) throws Exception {
        this.mCarScuManager.setParkLotChoseIndex2Scu(i);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public void setParkLotRecvIndex2Scu(int i) throws Exception {
        this.mCarScuManager.setParkLotRecvIndex2Scu(i);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public void setSuperParkMode(boolean z) throws Exception {
        this.mCarScuManager.setSuperParkMode(z ? 1 : 0);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public void setAutoParkInState(int i) throws Exception {
        this.mCarScuManager.setAutoParkInState(i);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public void setAutoParkOutState(int i) throws Exception {
        this.mCarScuManager.setAutoParkOutState(i);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public float[] getFrontRadarData() throws Exception {
        return this.mCarScuManager.getFrontRadarData();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public float[] getTailRadarData() throws Exception {
        return this.mCarScuManager.getTailRadarData();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public int[] getFrontRadarLevel() throws Exception {
        return this.mCarScuManager.getFrontRadarLevel();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public int[] getTailRadarLevel() throws Exception {
        return this.mCarScuManager.getTailRadarLevel();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public int[] getFrontRadarFaultSt() throws Exception {
        return this.mCarScuManager.getFrontRadarFaultSt();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public int[] getTailRadarFaultSt() throws Exception {
        return this.mCarScuManager.getTailRadarFaultSt();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public int getNearestEnableRadar() throws Exception {
        return this.mCarScuManager.getNearestEnableRadar();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public int getParkingOperationTips() throws Exception {
        return this.mCarScuManager.getScuOperationTips();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public int getAutoParkErrorCode() throws Exception {
        return this.mCarScuManager.getAutoParkErrorCode();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public void setPhoneAPButton(int i) throws Exception {
        this.mCarScuManager.setPhoneApButton(i);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public void setPhoneSMButton(int i) throws Exception {
        this.mCarScuManager.setPhoneSmButton(i);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public float[] getMileageExtraParams() throws Exception {
        return this.mCarScuManager.getMileageExtraParams();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public int getScuFrontMinDistance() throws Exception {
        return this.mCarScuManager.getFrontRadarDistance();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public int getScuRearMinDistance() throws Exception {
        return this.mCarScuManager.getTailRadarDistance();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public void setComonHomeSlotId(int i) throws Exception {
        this.mCarScuManager.setCommonHomeSlotID(i);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public void setFreeParking1Data(float f, float f2, float f3, int i, int i2, float f4, float f5) throws Exception {
        this.mCarScuManager.setFreeParking1Data(f, f2, f3, i, i2, f4, f5);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public void setFreeParking2Data(float f, float f2, float f3, int i, int i2, float f4, float f5) throws Exception {
        this.mCarScuManager.setFreeParking1Data(f, f2, f3, i, i2, f4, f5);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public void setAutoParkSwitch(int i) throws Exception {
        this.mCarScuManager.setAutoParkSwitch(i);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController
    public int getAutoParkSwitch() throws Exception {
        return this.mCarScuManager.getAutoParkSwitch();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.v2extend.IScuController
    public float[] getAltimeterV2() throws Exception {
        return this.mCarScuManager.getAltimeter();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.v2extend.IScuController
    public int[] getTargetParkingPositionV2() throws Exception {
        return this.mCarScuManager.getTargetParkingPosition();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.v2extend.IScuController
    public int getFsdSwitchState() throws Exception {
        return this.mCarScuManager.getFsdSwitchState();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.v2extend.IScuController
    public void setFsdSwitch(int i) throws Exception {
        this.mCarScuManager.setFsdSwitch(i);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.v2extend.IScuController
    public void setParkByMemorySw(int i) throws Exception {
        this.mCarScuManager.setParkByMemorySw(i);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.v2extend.IScuController
    public int getParkByMemorySwSt() throws Exception {
        return this.mCarScuManager.getParkByMemorySwSt();
    }
}

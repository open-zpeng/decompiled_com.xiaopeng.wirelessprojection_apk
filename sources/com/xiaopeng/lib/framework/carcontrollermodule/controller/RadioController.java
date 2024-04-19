package com.xiaopeng.lib.framework.carcontrollermodule.controller;

import android.car.Car;
import android.car.CarNotConnectedException;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.radio.CarRadioManager;
import android.util.Log;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.IEventMsg;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.IRadioController;
import java.util.List;
/* loaded from: classes2.dex */
public class RadioController extends AbstractController implements IRadioController {
    private static final String TAG = "RadioController";
    private CarRadioManager.CarRadioEventCallback mCarRadioEventCallback;
    private CarRadioManager mCarRadioManager;

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IRadioController
    public String getAudioDspInfo() throws Exception {
        return null;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IRadioController
    public int getRadioBand() throws Exception {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IRadioController
    public String getRadioInfo() throws Exception {
        return null;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IRadioController
    public int[] getRadioVolumePercent() throws Exception {
        return null;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IRadioController
    public boolean getTunerPowerStatus() throws Exception {
        return false;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IRadioController
    public int getXSoundType() throws Exception {
        return 0;
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IRadioController
    public void setXSoundType(int i) throws Exception {
    }

    public RadioController(Car car) {
        super(car);
        this.mCarRadioEventCallback = null;
    }

    @Override // com.xiaopeng.lib.framework.carcontrollermodule.controller.AbstractController
    public void initCarManager(Car car) {
        try {
            this.mCarRadioManager = (CarRadioManager) car.getCarManager(Car.XP_RADIO_SERVICE);
        } catch (Exception unused) {
            Log.e(TAG, "Car not connected");
        }
    }

    @Override // com.xiaopeng.lib.framework.carcontrollermodule.controller.AbstractController
    public void initPropertyTypeMap() {
        this.mPropertyTypeMap.put(Integer.valueOf((int) CarRadioManager.ID_RADIO_RADIO_STATUS), IRadioController.RadioInfoEventMsg.class);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ILifeCycle
    public void registerCanEventMsg(List<Class<? extends IEventMsg>> list) {
        if (this.mCarRadioEventCallback == null) {
            this.mCarRadioEventCallback = new CarRadioManager.CarRadioEventCallback() { // from class: com.xiaopeng.lib.framework.carcontrollermodule.controller.RadioController.1
                @Override // android.car.hardware.CarEcuManager.CarEcuEventCallback
                public void onChangeEvent(CarPropertyValue carPropertyValue) {
                    RadioController radioController = RadioController.this;
                    radioController.postEventBusMsg(radioController.mPropertyTypeMap.get(Integer.valueOf(carPropertyValue.getPropertyId())), carPropertyValue);
                }

                @Override // android.car.hardware.CarEcuManager.CarEcuEventCallback
                public void onErrorEvent(int i, int i2) {
                    Log.e(RadioController.TAG, "propertyId = " + i + " zone = " + i2);
                }
            };
        }
        try {
            this.mCarRadioManager.registerPropCallback(convertRegisterPropertyList(list), this.mCarRadioEventCallback);
        } catch (Exception unused) {
            Log.e(TAG, "Car not connected");
        }
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ILifeCycle
    public void unregisterCanEventMsg(List<Class<? extends IEventMsg>> list) {
        if (this.mCarRadioEventCallback == null) {
            return;
        }
        try {
            this.mCarRadioManager.unregisterPropCallback(convertUnregisterPropertyList(list), this.mCarRadioEventCallback);
        } catch (CarNotConnectedException e) {
            e.printStackTrace();
        }
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IRadioController
    public void setPowerOnTuner() throws Exception {
        this.mCarRadioManager.setPowerOnTunner();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IRadioController
    public void setPowerOffTuner() throws Exception {
        this.mCarRadioManager.setPowerOffTunner();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IRadioController
    public void setFmVolume(int i, int i2) throws Exception {
        this.mCarRadioManager.setFmVolume(i, i2);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IRadioController
    public void setExhibitionModeVolume(int i) throws Exception {
        this.mCarRadioManager.setCarExhibitionModeVol(i);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IRadioController
    public void setRadioSearchUp() throws Exception {
        this.mCarRadioManager.setRadioSearchStationUp();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IRadioController
    public void setRadioSearchDown() throws Exception {
        this.mCarRadioManager.setRadioSearchStationDown();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IRadioController
    public void setStartFullBandScan() throws Exception {
        this.mCarRadioManager.setStartFullBandScan();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IRadioController
    public void setStopFullBandScan() throws Exception {
        this.mCarRadioManager.setStopFullBandScan();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IRadioController
    public void setRadioBand(int i) throws Exception {
        this.mCarRadioManager.setRadioBand(i);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IRadioController
    public void setRadioVolumePercent(int i, int i2) throws Exception {
        this.mCarRadioManager.setRadioVolumePercent(i, i2);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IRadioController
    public void setRadioVolumeAutoFocus(int i) throws Exception {
        this.mCarRadioManager.setRadioVolumeAutoFocus(i);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IRadioController
    public int getRadioVolumeAutoFocus() throws Exception {
        return this.mCarRadioManager.getRadioVolumeAutoFocus();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IRadioController
    public void setRadioFrequency(int i, int i2) throws Exception {
        this.mCarRadioManager.setRadioFrequency(i, i2);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IRadioController
    public int[] getRadioFrequency() throws Exception {
        return this.mCarRadioManager.getRadioFrequency();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IRadioController
    public void setMainAudioMode(int i, int i2) throws Exception {
        this.mCarRadioManager.setAudioMode(i, i2);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IRadioController
    public int[] getAudioMode() throws Exception {
        return this.mCarRadioManager.getAudioMode();
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IRadioController
    public void setAudioGEQParams(int i, int i2, int i3, int i4) throws Exception {
        this.mCarRadioManager.setAudioGEQParams(i, i2, i3, i4);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IRadioController
    public void setAudioBalanceFader(int i, int i2) throws Exception {
        this.mCarRadioManager.setAudioBalanceFader(i, i2);
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.IRadioController
    public void enableAudioDpsInfoCallback() throws Exception {
        this.mCarRadioManager.setAudioParameters();
    }
}

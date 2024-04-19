package com.xiaopeng.lib.framework.carcontrollermodule.controller;

import android.car.Car;
import android.car.CarNotConnectedException;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.input.CarInputManager;
import android.util.Log;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.IEventMsg;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.IInputController;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.v2extend.IInputController;
import java.util.HashMap;
import java.util.List;
/* loaded from: classes2.dex */
public class InputController extends AbstractController implements IInputController {
    private static final String TAG = "InputController";
    private CarInputManager.CarInputEventCallback mCarInputEventCallback;
    private CarInputManager mCarInputManager;
    protected HashMap<Integer, Integer> mKeyCodeMap;

    public InputController(Car car) {
        super(car);
        this.mCarInputEventCallback = null;
    }

    @Override // com.xiaopeng.lib.framework.carcontrollermodule.controller.AbstractController
    public void initCarManager(Car car) {
        this.mCarApiClient = car;
        try {
            this.mCarInputManager = (CarInputManager) car.getCarManager(Car.XP_INPUT_SERVICE);
        } catch (Exception unused) {
            Log.e(TAG, "Car not connected");
        }
    }

    @Override // com.xiaopeng.lib.framework.carcontrollermodule.controller.AbstractController
    public void initPropertyTypeMap() {
        if (this.mKeyCodeMap == null) {
            this.mKeyCodeMap = new HashMap<>();
        }
        this.mPropertyTypeMap.put(507, IInputController.InputAudioSwitchEventMsgV2.class);
        this.mPropertyTypeMap.put(508, IInputController.InputAudioSwitchEventMsgV2.class);
        this.mPropertyTypeMap.put(509, IInputController.InputAudioSwitchEventMsgV2.class);
        this.mPropertyTypeMap.put(510, IInputController.InputAudioSwitchEventMsgV2.class);
        this.mPropertyTypeMap.put(Integer.valueOf((int) com.xiaopeng.lib.framework.moduleinterface.carcontroller.IInputController.KEYCODE_KNOB_USB_MUSIC), IInputController.InputAudioSwitchEventMsgV2.class);
        this.mPropertyTypeMap.put(Integer.valueOf((int) com.xiaopeng.lib.framework.moduleinterface.carcontroller.IInputController.KEYCODE_KNOB_TALKING_BOOK), IInputController.InputAudioSwitchEventMsgV2.class);
        this.mKeyCodeMap.put(557851171, 507);
        this.mKeyCodeMap.put(557851172, 508);
        this.mKeyCodeMap.put(557851173, 509);
        this.mKeyCodeMap.put(557851174, 510);
        this.mKeyCodeMap.put(557851177, Integer.valueOf((int) com.xiaopeng.lib.framework.moduleinterface.carcontroller.IInputController.KEYCODE_KNOB_USB_MUSIC));
        this.mKeyCodeMap.put(557851178, Integer.valueOf((int) com.xiaopeng.lib.framework.moduleinterface.carcontroller.IInputController.KEYCODE_KNOB_TALKING_BOOK));
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ILifeCycle
    public void registerCanEventMsg(List<Class<? extends IEventMsg>> list) {
        if (this.mCarInputEventCallback == null) {
            this.mCarInputEventCallback = new CarInputManager.CarInputEventCallback() { // from class: com.xiaopeng.lib.framework.carcontrollermodule.controller.InputController.1
                @Override // android.car.hardware.CarEcuManager.CarEcuEventCallback
                public void onChangeEvent(CarPropertyValue carPropertyValue) {
                    int propertyId = carPropertyValue.getPropertyId();
                    if (InputController.this.mKeyCodeMap.containsKey(Integer.valueOf(propertyId))) {
                        IInputController.InputKeyEvent inputKeyEvent = new IInputController.InputKeyEvent(InputController.this.mKeyCodeMap.get(Integer.valueOf(propertyId)).intValue(), carPropertyValue.getValue());
                        CarPropertyValue carPropertyValue2 = new CarPropertyValue(inputKeyEvent.getCode(), inputKeyEvent);
                        InputController inputController = InputController.this;
                        inputController.postEventBusMsg(inputController.mPropertyTypeMap.get(Integer.valueOf(carPropertyValue2.getPropertyId())), carPropertyValue2);
                    }
                }

                @Override // android.car.hardware.CarEcuManager.CarEcuEventCallback
                public void onErrorEvent(int i, int i2) {
                    Log.e(InputController.TAG, "propertyId = " + i + " zone = " + i2);
                }
            };
        }
        try {
            this.mCarInputManager.registerCallback(this.mCarInputEventCallback);
        } catch (Exception unused) {
            Log.e(TAG, "Car not connected");
        }
    }

    @Override // com.xiaopeng.lib.framework.moduleinterface.carcontroller.ILifeCycle
    public void unregisterCanEventMsg(List<Class<? extends IEventMsg>> list) {
        CarInputManager.CarInputEventCallback carInputEventCallback = this.mCarInputEventCallback;
        if (carInputEventCallback == null) {
            return;
        }
        try {
            this.mCarInputManager.unregisterCallback(carInputEventCallback);
        } catch (CarNotConnectedException e) {
            e.printStackTrace();
        }
    }
}

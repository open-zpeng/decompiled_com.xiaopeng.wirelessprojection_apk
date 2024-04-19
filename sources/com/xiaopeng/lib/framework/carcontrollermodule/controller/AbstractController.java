package com.xiaopeng.lib.framework.carcontrollermodule.controller;

import android.car.Car;
import android.car.hardware.CarPropertyValue;
import android.util.Log;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.IError;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.IEventMsg;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.greenrobot.eventbus.EventBus;
/* loaded from: classes2.dex */
public abstract class AbstractController {
    private static final String TAG = "AbstractController";
    protected Car mCarApiClient;
    protected HashMap<Integer, Class<? extends IError>> mPropertyErrorMap = new HashMap<>();
    protected HashMap<Integer, Class<? extends IEventMsg>> mPropertyTypeMap = new HashMap<>();
    protected ConcurrentHashMap<Integer, CarPropertyValue> mCarPropertyMap = new ConcurrentHashMap<>();

    public abstract void initCarManager(Car car);

    public abstract void initPropertyTypeMap();

    public AbstractController(Car car) {
        initCarManager(car);
        initPropertyTypeMap();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Removed duplicated region for block: B:30:0x00b8  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void postEventBusMsg(java.lang.Class<? extends com.xiaopeng.lib.framework.moduleinterface.carcontroller.IEventMsg> r7, android.car.hardware.CarPropertyValue r8) {
        /*
            Method dump skipped, instructions count: 367
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaopeng.lib.framework.carcontrollermodule.controller.AbstractController.postEventBusMsg(java.lang.Class, android.car.hardware.CarPropertyValue):void");
    }

    private Integer convertPropertyId(Class<? extends IEventMsg> cls) {
        for (Map.Entry<Integer, Class<? extends IEventMsg>> entry : this.mPropertyTypeMap.entrySet()) {
            if (cls.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    private List<Integer> convertPropertyList(List<Class<? extends IEventMsg>> list) {
        ArrayList arrayList = new ArrayList();
        if (list != null && list.size() > 0) {
            for (Class<? extends IEventMsg> cls : list) {
                Integer convertPropertyId = convertPropertyId(cls);
                if (convertPropertyId != null) {
                    arrayList.add(convertPropertyId);
                }
            }
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public List<Integer> convertRegisterPropertyList(List<Class<? extends IEventMsg>> list) {
        List<Integer> convertPropertyList = convertPropertyList(list);
        Log.d(getClass().getSimpleName(), "register msgMap ：\n" + Arrays.toString(convertPropertyMap(list).entrySet().toArray()));
        return convertPropertyList;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public List<Integer> convertUnregisterPropertyList(List<Class<? extends IEventMsg>> list) {
        List<Integer> convertPropertyList = convertPropertyList(list);
        Log.d(getClass().getSimpleName(), "unregister msgMap ：\n" + Arrays.toString(convertPropertyMap(list).entrySet().toArray()));
        return convertPropertyList;
    }

    private HashMap<Integer, Class<? extends IEventMsg>> convertPropertyMap(List<Class<? extends IEventMsg>> list) {
        HashMap<Integer, Class<? extends IEventMsg>> hashMap = new HashMap<>();
        if (list != null && list.size() > 0) {
            for (Class<? extends IEventMsg> cls : list) {
                Integer convertPropertyId = convertPropertyId(cls);
                if (convertPropertyId != null) {
                    hashMap.put(convertPropertyId, cls);
                }
            }
        }
        return hashMap;
    }

    private HashMap<String, Class<? extends IEventMsg>> convertHalMap(List<Class<? extends IEventMsg>> list) {
        HashMap<String, Class<? extends IEventMsg>> hashMap = new HashMap<>();
        if (list != null && list.size() > 0) {
            for (Class<? extends IEventMsg> cls : list) {
                Integer convertPropertyId = convertPropertyId(cls);
                if (convertPropertyId != null) {
                    hashMap.put(Integer.toHexString(convertPropertyId.intValue()), cls);
                }
            }
        }
        return hashMap;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void postErrorEventBusMsg(Class<? extends IEventMsg> cls, int i) {
        if (cls == null) {
            Log.i(getClass().getSimpleName(), new StringBuffer().append(" postErrorEventBusMsg").toString() + ", id not put in mPropertyTypeMap");
            return;
        }
        try {
            Class<?> cls2 = Class.forName(cls.getName() + "$Error");
            if (cls2 != null) {
                IError iError = (IError) cls2.newInstance();
                iError.setCode(i);
                EventBus.getDefault().postSticky(iError);
                Log.v(getClass().getSimpleName(), new StringBuffer().append(" postErrorEventBusMsg").append(", msg = ").append(cls2.getSimpleName()).append(", errorCode = ").append(i).toString());
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Log.e(getClass().getSimpleName(), e.getMessage());
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
            Log.e(getClass().getSimpleName(), e2.getMessage());
        } catch (InstantiationException e3) {
            e3.printStackTrace();
            Log.e(getClass().getSimpleName(), e3.getMessage());
        }
    }
}

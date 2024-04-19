package com.xiaopeng.lib.framework.carcontrollermodule;

import android.car.Car;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import com.xiaopeng.lib.framework.carcontrollermodule.controller.AvasController;
import com.xiaopeng.lib.framework.carcontrollermodule.controller.AvmController;
import com.xiaopeng.lib.framework.carcontrollermodule.controller.BcmController;
import com.xiaopeng.lib.framework.carcontrollermodule.controller.BmsController;
import com.xiaopeng.lib.framework.carcontrollermodule.controller.CanController;
import com.xiaopeng.lib.framework.carcontrollermodule.controller.CcsController;
import com.xiaopeng.lib.framework.carcontrollermodule.controller.CiuController;
import com.xiaopeng.lib.framework.carcontrollermodule.controller.DcdcController;
import com.xiaopeng.lib.framework.carcontrollermodule.controller.EpsController;
import com.xiaopeng.lib.framework.carcontrollermodule.controller.EspController;
import com.xiaopeng.lib.framework.carcontrollermodule.controller.HVACController;
import com.xiaopeng.lib.framework.carcontrollermodule.controller.IcmController;
import com.xiaopeng.lib.framework.carcontrollermodule.controller.InputController;
import com.xiaopeng.lib.framework.carcontrollermodule.controller.IpuController;
import com.xiaopeng.lib.framework.carcontrollermodule.controller.McuController;
import com.xiaopeng.lib.framework.carcontrollermodule.controller.RadioController;
import com.xiaopeng.lib.framework.carcontrollermodule.controller.ScuController;
import com.xiaopeng.lib.framework.carcontrollermodule.controller.TpmsController;
import com.xiaopeng.lib.framework.carcontrollermodule.controller.VcuController;
import com.xiaopeng.lib.framework.carcontrollermodule.controller.XpuController;
import com.xiaopeng.lib.framework.module.IModuleEntry;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.IAvasController;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.IAvmController;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBcmController;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.IBmsController;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICanController;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICarControllerService;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICcsController;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.ICiuController;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.IDcdcController;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.IEpsController;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.IEspController;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.IHVACController;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.IIcmController;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.IInputController;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.IIpuController;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.IMcuController;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.IRadioController;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.IScuController;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.ITpmsController;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.IVcuController;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.v2extend.IXpuController;
import org.greenrobot.eventbus.EventBus;
/* loaded from: classes2.dex */
public class CarControllerModuleEntry implements IModuleEntry {
    public static boolean sPrintLog = false;
    private volatile AvasController mAvasController;
    private volatile AvmController mAvmController;
    private volatile BcmController mBcmController;
    private volatile BmsController mBmsController;
    private volatile CanController mCanController;
    private Car mCarApiClient;
    private volatile CcsController mCcsController;
    private volatile CiuController mCiuController;
    private Context mContext;
    private volatile DcdcController mDcdcController;
    private volatile EpsController mEpsController;
    private volatile EspController mEspController;
    private volatile HVACController mHVACController;
    private HandlerThread mHandlerThread;
    private volatile IcmController mIcmController;
    private volatile InputController mInputController;
    private volatile IpuController mIpuController;
    private volatile boolean mIsInited = false;
    private volatile McuController mMcuController;
    private volatile RadioController mRadioController;
    private volatile ScuController mScuController;
    private volatile TpmsController mTpmsController;
    private volatile VcuController mVcuController;
    private volatile XpuController mXpuController;

    public CarControllerModuleEntry(Context context) {
        this.mContext = context;
    }

    private void connectCarWhenInit() {
        if (this.mIsInited) {
            return;
        }
        synchronized (this) {
            if (!this.mIsInited) {
                HandlerThread handlerThread = new HandlerThread("CarControllerModule");
                this.mHandlerThread = handlerThread;
                handlerThread.start();
                Car createCar = Car.createCar(this.mContext, new ServiceConnection() { // from class: com.xiaopeng.lib.framework.carcontrollermodule.CarControllerModuleEntry.1
                    @Override // android.content.ServiceConnection
                    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                        ICarControllerService.ConnectCarEventMsg connectCarEventMsg = new ICarControllerService.ConnectCarEventMsg();
                        connectCarEventMsg.setData(true);
                        CarControllerModuleEntry.this.clearAllControllers();
                        EventBus.getDefault().post(connectCarEventMsg);
                    }

                    @Override // android.content.ServiceConnection
                    public void onServiceDisconnected(ComponentName componentName) {
                        ICarControllerService.ConnectCarEventMsg connectCarEventMsg = new ICarControllerService.ConnectCarEventMsg();
                        connectCarEventMsg.setData(false);
                        EventBus.getDefault().post(connectCarEventMsg);
                    }
                }, new Handler(this.mHandlerThread.getLooper()));
                this.mCarApiClient = createCar;
                createCar.connect();
                this.mIsInited = true;
            }
        }
    }

    @Override // com.xiaopeng.lib.framework.module.IModuleEntry
    public Object get(Class cls) {
        connectCarWhenInit();
        if (cls == IBcmController.class) {
            if (this.mBcmController == null) {
                synchronized (this) {
                    if (this.mBcmController == null) {
                        this.mBcmController = new BcmController(this.mCarApiClient);
                    }
                }
            }
            return this.mBcmController;
        } else if (cls == IVcuController.class) {
            if (this.mVcuController == null) {
                synchronized (this) {
                    if (this.mVcuController == null) {
                        this.mVcuController = new VcuController(this.mCarApiClient);
                    }
                }
            }
            get(EspController.class);
            return this.mVcuController;
        } else if (cls == IHVACController.class) {
            if (this.mHVACController == null) {
                synchronized (this) {
                    if (this.mHVACController == null) {
                        this.mHVACController = new HVACController(this.mCarApiClient);
                    }
                }
            }
            return this.mHVACController;
        } else if (cls == IAvasController.class) {
            if (this.mAvasController == null) {
                synchronized (this) {
                    if (this.mAvasController == null) {
                        this.mAvasController = new AvasController(this.mCarApiClient);
                    }
                }
            }
            return this.mAvasController;
        } else if (cls == IAvmController.class) {
            if (this.mAvmController == null) {
                synchronized (this) {
                    if (this.mAvmController == null) {
                        this.mAvmController = new AvmController(this.mCarApiClient);
                    }
                }
            }
            return this.mAvmController;
        } else if (cls == IBmsController.class) {
            if (this.mBmsController == null) {
                synchronized (this) {
                    if (this.mBmsController == null) {
                        this.mBmsController = new BmsController(this.mCarApiClient);
                    }
                }
            }
            return this.mBmsController;
        } else if (cls == IEpsController.class) {
            if (this.mEpsController == null) {
                synchronized (this) {
                    if (this.mEpsController == null) {
                        this.mEpsController = new EpsController(this.mCarApiClient);
                    }
                }
            }
            return this.mEpsController;
        } else if (cls == IEspController.class) {
            if (this.mEspController == null) {
                synchronized (this) {
                    if (this.mEspController == null) {
                        this.mEspController = new EspController(this.mCarApiClient);
                    }
                }
            }
            return this.mEspController;
        } else if (cls == IIcmController.class) {
            if (this.mIcmController == null) {
                synchronized (this) {
                    if (this.mIcmController == null) {
                        this.mIcmController = new IcmController(this.mCarApiClient);
                    }
                }
            }
            return this.mIcmController;
        } else if (cls == IMcuController.class) {
            if (this.mMcuController == null) {
                synchronized (this) {
                    if (this.mMcuController == null) {
                        this.mMcuController = new McuController(this.mCarApiClient);
                    }
                }
            }
            return this.mMcuController;
        } else if (cls == IRadioController.class) {
            if (this.mRadioController == null) {
                synchronized (this) {
                    if (this.mRadioController == null) {
                        this.mRadioController = new RadioController(this.mCarApiClient);
                    }
                }
            }
            return this.mRadioController;
        } else if (cls == IScuController.class) {
            if (this.mScuController == null) {
                synchronized (this) {
                    if (this.mScuController == null) {
                        this.mScuController = new ScuController(this.mCarApiClient);
                    }
                }
            }
            return this.mScuController;
        } else if (cls == ITpmsController.class) {
            if (this.mTpmsController == null) {
                synchronized (this) {
                    if (this.mTpmsController == null) {
                        this.mTpmsController = new TpmsController(this.mCarApiClient);
                    }
                }
            }
            return this.mTpmsController;
        } else if (cls == IInputController.class) {
            if (this.mInputController == null) {
                synchronized (this) {
                    if (this.mInputController == null) {
                        this.mInputController = new InputController(this.mCarApiClient);
                    }
                }
            }
            return this.mInputController;
        } else if (cls == ICanController.class) {
            if (this.mCanController == null) {
                synchronized (this) {
                    if (this.mCanController == null) {
                        this.mCanController = new CanController(this.mCarApiClient);
                    }
                }
            }
            return this.mCanController;
        } else if (cls == IIpuController.class) {
            if (this.mIpuController == null) {
                synchronized (this) {
                    if (this.mIpuController == null) {
                        this.mIpuController = new IpuController(this.mCarApiClient);
                    }
                }
            }
            return this.mIpuController;
        } else if (cls == ICcsController.class) {
            if (this.mCcsController == null) {
                synchronized (this) {
                    if (this.mCcsController == null) {
                        this.mCcsController = new CcsController(this.mCarApiClient);
                    }
                }
            }
            return this.mCcsController;
        } else if (cls == IDcdcController.class) {
            if (this.mDcdcController == null) {
                synchronized (this) {
                    if (this.mDcdcController == null) {
                        this.mDcdcController = new DcdcController(this.mCarApiClient);
                    }
                }
            }
            return this.mDcdcController;
        } else if (cls == ICiuController.class) {
            if (this.mCiuController == null) {
                synchronized (this) {
                    if (this.mCiuController == null) {
                        this.mCiuController = new CiuController(this.mCarApiClient);
                    }
                }
            }
            return this.mCiuController;
        } else if (cls == IXpuController.class) {
            if (this.mXpuController == null) {
                synchronized (this) {
                    if (this.mXpuController == null) {
                        this.mXpuController = new XpuController(this.mCarApiClient);
                    }
                }
            }
            return this.mXpuController;
        } else {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearAllControllers() {
        synchronized (this) {
            this.mVcuController = null;
            this.mHVACController = null;
            this.mAvasController = null;
            this.mAvmController = null;
            this.mBmsController = null;
            this.mEpsController = null;
            this.mEspController = null;
            this.mIcmController = null;
            this.mMcuController = null;
            this.mRadioController = null;
            this.mScuController = null;
            this.mTpmsController = null;
            this.mInputController = null;
            this.mCanController = null;
            this.mIpuController = null;
            this.mCcsController = null;
            this.mDcdcController = null;
            this.mCiuController = null;
            this.mXpuController = null;
        }
    }

    public static void setPrintLog(boolean z) {
        sPrintLog = z;
    }

    public Car getCarApiClient() throws Exception {
        if (!this.mIsInited) {
            throw new Exception("CarServices is not connected");
        }
        return this.mCarApiClient;
    }
}

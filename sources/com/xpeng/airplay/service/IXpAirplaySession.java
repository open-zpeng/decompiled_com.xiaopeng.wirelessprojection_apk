package com.xpeng.airplay.service;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.view.Surface;
import com.xpeng.airplay.service.IXpAirplayCallbacks;
/* loaded from: classes2.dex */
public interface IXpAirplaySession extends IInterface {

    /* loaded from: classes2.dex */
    public static class Default implements IXpAirplaySession {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // com.xpeng.airplay.service.IXpAirplaySession
        public String getServerName() throws RemoteException {
            return null;
        }

        @Override // com.xpeng.airplay.service.IXpAirplaySession
        public boolean hasActiveConnection() throws RemoteException {
            return false;
        }

        @Override // com.xpeng.airplay.service.IXpAirplaySession
        public void registerAirplayCallbacks(IXpAirplayCallbacks iXpAirplayCallbacks) throws RemoteException {
        }

        @Override // com.xpeng.airplay.service.IXpAirplaySession
        public void saveSystemVolume(int i) throws RemoteException {
        }

        @Override // com.xpeng.airplay.service.IXpAirplaySession
        public void setMediaPlaybackInfo(MediaPlaybackInfo mediaPlaybackInfo) throws RemoteException {
        }

        @Override // com.xpeng.airplay.service.IXpAirplaySession
        public void setMirrorSurface(Surface surface) throws RemoteException {
        }

        @Override // com.xpeng.airplay.service.IXpAirplaySession
        public void setVideoPlaybackState(int i) throws RemoteException {
        }

        @Override // com.xpeng.airplay.service.IXpAirplaySession
        public void unregisterAirplayCallbacks(IXpAirplayCallbacks iXpAirplayCallbacks) throws RemoteException {
        }
    }

    String getServerName() throws RemoteException;

    boolean hasActiveConnection() throws RemoteException;

    void registerAirplayCallbacks(IXpAirplayCallbacks iXpAirplayCallbacks) throws RemoteException;

    void saveSystemVolume(int i) throws RemoteException;

    void setMediaPlaybackInfo(MediaPlaybackInfo mediaPlaybackInfo) throws RemoteException;

    void setMirrorSurface(Surface surface) throws RemoteException;

    void setVideoPlaybackState(int i) throws RemoteException;

    void unregisterAirplayCallbacks(IXpAirplayCallbacks iXpAirplayCallbacks) throws RemoteException;

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IXpAirplaySession {
        private static final String DESCRIPTOR = "com.xpeng.airplay.service.IXpAirplaySession";
        static final int TRANSACTION_getServerName = 1;
        static final int TRANSACTION_hasActiveConnection = 2;
        static final int TRANSACTION_registerAirplayCallbacks = 3;
        static final int TRANSACTION_saveSystemVolume = 8;
        static final int TRANSACTION_setMediaPlaybackInfo = 7;
        static final int TRANSACTION_setMirrorSurface = 5;
        static final int TRANSACTION_setVideoPlaybackState = 6;
        static final int TRANSACTION_unregisterAirplayCallbacks = 4;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IXpAirplaySession asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            if (queryLocalInterface != null && (queryLocalInterface instanceof IXpAirplaySession)) {
                return (IXpAirplaySession) queryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i == 1598968902) {
                parcel2.writeString(DESCRIPTOR);
                return true;
            }
            switch (i) {
                case 1:
                    parcel.enforceInterface(DESCRIPTOR);
                    String serverName = getServerName();
                    parcel2.writeNoException();
                    parcel2.writeString(serverName);
                    return true;
                case 2:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean hasActiveConnection = hasActiveConnection();
                    parcel2.writeNoException();
                    parcel2.writeInt(hasActiveConnection ? 1 : 0);
                    return true;
                case 3:
                    parcel.enforceInterface(DESCRIPTOR);
                    registerAirplayCallbacks(IXpAirplayCallbacks.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 4:
                    parcel.enforceInterface(DESCRIPTOR);
                    unregisterAirplayCallbacks(IXpAirplayCallbacks.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 5:
                    parcel.enforceInterface(DESCRIPTOR);
                    setMirrorSurface(parcel.readInt() != 0 ? (Surface) Surface.CREATOR.createFromParcel(parcel) : null);
                    return true;
                case 6:
                    parcel.enforceInterface(DESCRIPTOR);
                    setVideoPlaybackState(parcel.readInt());
                    return true;
                case 7:
                    parcel.enforceInterface(DESCRIPTOR);
                    setMediaPlaybackInfo(parcel.readInt() != 0 ? MediaPlaybackInfo.CREATOR.createFromParcel(parcel) : null);
                    return true;
                case 8:
                    parcel.enforceInterface(DESCRIPTOR);
                    saveSystemVolume(parcel.readInt());
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public static class Proxy implements IXpAirplaySession {
            public static IXpAirplaySession sDefaultImpl;
            private IBinder mRemote;

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // com.xpeng.airplay.service.IXpAirplaySession
            public String getServerName() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(1, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getServerName();
                    }
                    obtain2.readException();
                    return obtain2.readString();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.xpeng.airplay.service.IXpAirplaySession
            public boolean hasActiveConnection() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(2, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().hasActiveConnection();
                    }
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.xpeng.airplay.service.IXpAirplaySession
            public void registerAirplayCallbacks(IXpAirplayCallbacks iXpAirplayCallbacks) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iXpAirplayCallbacks != null ? iXpAirplayCallbacks.asBinder() : null);
                    if (!this.mRemote.transact(3, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().registerAirplayCallbacks(iXpAirplayCallbacks);
                    } else {
                        obtain2.readException();
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.xpeng.airplay.service.IXpAirplaySession
            public void unregisterAirplayCallbacks(IXpAirplayCallbacks iXpAirplayCallbacks) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iXpAirplayCallbacks != null ? iXpAirplayCallbacks.asBinder() : null);
                    if (!this.mRemote.transact(4, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().unregisterAirplayCallbacks(iXpAirplayCallbacks);
                    } else {
                        obtain2.readException();
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.xpeng.airplay.service.IXpAirplaySession
            public void setMirrorSurface(Surface surface) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (surface != null) {
                        obtain.writeInt(1);
                        surface.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (this.mRemote.transact(5, obtain, null, 1) || Stub.getDefaultImpl() == null) {
                        return;
                    }
                    Stub.getDefaultImpl().setMirrorSurface(surface);
                } finally {
                    obtain.recycle();
                }
            }

            @Override // com.xpeng.airplay.service.IXpAirplaySession
            public void setVideoPlaybackState(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (this.mRemote.transact(6, obtain, null, 1) || Stub.getDefaultImpl() == null) {
                        return;
                    }
                    Stub.getDefaultImpl().setVideoPlaybackState(i);
                } finally {
                    obtain.recycle();
                }
            }

            @Override // com.xpeng.airplay.service.IXpAirplaySession
            public void setMediaPlaybackInfo(MediaPlaybackInfo mediaPlaybackInfo) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (mediaPlaybackInfo != null) {
                        obtain.writeInt(1);
                        mediaPlaybackInfo.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (this.mRemote.transact(7, obtain, null, 1) || Stub.getDefaultImpl() == null) {
                        return;
                    }
                    Stub.getDefaultImpl().setMediaPlaybackInfo(mediaPlaybackInfo);
                } finally {
                    obtain.recycle();
                }
            }

            @Override // com.xpeng.airplay.service.IXpAirplaySession
            public void saveSystemVolume(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (this.mRemote.transact(8, obtain, null, 1) || Stub.getDefaultImpl() == null) {
                        return;
                    }
                    Stub.getDefaultImpl().saveSystemVolume(i);
                } finally {
                    obtain.recycle();
                }
            }
        }

        public static boolean setDefaultImpl(IXpAirplaySession iXpAirplaySession) {
            if (Proxy.sDefaultImpl != null || iXpAirplaySession == null) {
                return false;
            }
            Proxy.sDefaultImpl = iXpAirplaySession;
            return true;
        }

        public static IXpAirplaySession getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}

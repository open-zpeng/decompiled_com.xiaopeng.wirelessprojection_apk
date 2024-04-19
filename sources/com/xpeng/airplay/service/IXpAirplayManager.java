package com.xpeng.airplay.service;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.xpeng.airplay.service.IXpAirplaySession;
/* loaded from: classes2.dex */
public interface IXpAirplayManager extends IInterface {

    /* loaded from: classes2.dex */
    public static class Default implements IXpAirplayManager {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // com.xpeng.airplay.service.IXpAirplayManager
        public void closeSession(int i) throws RemoteException {
        }

        @Override // com.xpeng.airplay.service.IXpAirplayManager
        public int createSession(SessionParams sessionParams) throws RemoteException {
            return 0;
        }

        @Override // com.xpeng.airplay.service.IXpAirplayManager
        public String getServerName(int i) throws RemoteException {
            return null;
        }

        @Override // com.xpeng.airplay.service.IXpAirplayManager
        public String[] getServerNames() throws RemoteException {
            return null;
        }

        @Override // com.xpeng.airplay.service.IXpAirplayManager
        public long getTetheringDataUsage() throws RemoteException {
            return 0L;
        }

        @Override // com.xpeng.airplay.service.IXpAirplayManager
        public IXpAirplaySession openSession(int i) throws RemoteException {
            return null;
        }
    }

    void closeSession(int i) throws RemoteException;

    int createSession(SessionParams sessionParams) throws RemoteException;

    String getServerName(int i) throws RemoteException;

    String[] getServerNames() throws RemoteException;

    long getTetheringDataUsage() throws RemoteException;

    IXpAirplaySession openSession(int i) throws RemoteException;

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IXpAirplayManager {
        private static final String DESCRIPTOR = "com.xpeng.airplay.service.IXpAirplayManager";
        static final int TRANSACTION_closeSession = 5;
        static final int TRANSACTION_createSession = 3;
        static final int TRANSACTION_getServerName = 2;
        static final int TRANSACTION_getServerNames = 1;
        static final int TRANSACTION_getTetheringDataUsage = 6;
        static final int TRANSACTION_openSession = 4;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IXpAirplayManager asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            if (queryLocalInterface != null && (queryLocalInterface instanceof IXpAirplayManager)) {
                return (IXpAirplayManager) queryLocalInterface;
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
                    String[] serverNames = getServerNames();
                    parcel2.writeNoException();
                    parcel2.writeStringArray(serverNames);
                    return true;
                case 2:
                    parcel.enforceInterface(DESCRIPTOR);
                    String serverName = getServerName(parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeString(serverName);
                    return true;
                case 3:
                    parcel.enforceInterface(DESCRIPTOR);
                    int createSession = createSession(parcel.readInt() != 0 ? SessionParams.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    parcel2.writeInt(createSession);
                    return true;
                case 4:
                    parcel.enforceInterface(DESCRIPTOR);
                    IXpAirplaySession openSession = openSession(parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeStrongBinder(openSession != null ? openSession.asBinder() : null);
                    return true;
                case 5:
                    parcel.enforceInterface(DESCRIPTOR);
                    closeSession(parcel.readInt());
                    parcel2.writeNoException();
                    return true;
                case 6:
                    parcel.enforceInterface(DESCRIPTOR);
                    long tetheringDataUsage = getTetheringDataUsage();
                    parcel2.writeNoException();
                    parcel2.writeLong(tetheringDataUsage);
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public static class Proxy implements IXpAirplayManager {
            public static IXpAirplayManager sDefaultImpl;
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

            @Override // com.xpeng.airplay.service.IXpAirplayManager
            public String[] getServerNames() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(1, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getServerNames();
                    }
                    obtain2.readException();
                    return obtain2.createStringArray();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.xpeng.airplay.service.IXpAirplayManager
            public String getServerName(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (!this.mRemote.transact(2, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getServerName(i);
                    }
                    obtain2.readException();
                    return obtain2.readString();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.xpeng.airplay.service.IXpAirplayManager
            public int createSession(SessionParams sessionParams) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (sessionParams != null) {
                        obtain.writeInt(1);
                        sessionParams.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (!this.mRemote.transact(3, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().createSession(sessionParams);
                    }
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.xpeng.airplay.service.IXpAirplayManager
            public IXpAirplaySession openSession(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (!this.mRemote.transact(4, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().openSession(i);
                    }
                    obtain2.readException();
                    return IXpAirplaySession.Stub.asInterface(obtain2.readStrongBinder());
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.xpeng.airplay.service.IXpAirplayManager
            public void closeSession(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (!this.mRemote.transact(5, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().closeSession(i);
                    } else {
                        obtain2.readException();
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.xpeng.airplay.service.IXpAirplayManager
            public long getTetheringDataUsage() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(6, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getTetheringDataUsage();
                    }
                    obtain2.readException();
                    return obtain2.readLong();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public static boolean setDefaultImpl(IXpAirplayManager iXpAirplayManager) {
            if (Proxy.sDefaultImpl != null || iXpAirplayManager == null) {
                return false;
            }
            Proxy.sDefaultImpl = iXpAirplayManager;
            return true;
        }

        public static IXpAirplayManager getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}

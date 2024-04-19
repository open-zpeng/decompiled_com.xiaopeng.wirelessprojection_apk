package com.xpeng.airplay.service;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
/* loaded from: classes2.dex */
public interface IXpAirplayCallbacks extends IInterface {

    /* loaded from: classes2.dex */
    public static class Default implements IXpAirplayCallbacks {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // com.xpeng.airplay.service.IXpAirplayCallbacks
        public void onAudioProgressUpdated(MediaPlaybackInfo mediaPlaybackInfo) throws RemoteException {
        }

        @Override // com.xpeng.airplay.service.IXpAirplayCallbacks
        public void onClientConnected(int i) throws RemoteException {
        }

        @Override // com.xpeng.airplay.service.IXpAirplayCallbacks
        public void onClientDisconnected(int i, int i2) throws RemoteException {
        }

        @Override // com.xpeng.airplay.service.IXpAirplayCallbacks
        public void onMetaDataUpdated(MediaMetaData mediaMetaData) throws RemoteException {
        }

        @Override // com.xpeng.airplay.service.IXpAirplayCallbacks
        public void onMirrorSizeChanged(int i, int i2) throws RemoteException {
        }

        @Override // com.xpeng.airplay.service.IXpAirplayCallbacks
        public void onMirrorStarted() throws RemoteException {
        }

        @Override // com.xpeng.airplay.service.IXpAirplayCallbacks
        public void onMirrorStopped() throws RemoteException {
        }

        @Override // com.xpeng.airplay.service.IXpAirplayCallbacks
        public void onServerNameUpdated(String str) throws RemoteException {
        }

        @Override // com.xpeng.airplay.service.IXpAirplayCallbacks
        public void onVideoPlayStarted(MediaPlayInfo mediaPlayInfo) throws RemoteException {
        }

        @Override // com.xpeng.airplay.service.IXpAirplayCallbacks
        public void onVideoPlayStopped() throws RemoteException {
        }

        @Override // com.xpeng.airplay.service.IXpAirplayCallbacks
        public void onVideoRateChanged(int i) throws RemoteException {
        }

        @Override // com.xpeng.airplay.service.IXpAirplayCallbacks
        public void onVideoScrubbed(int i) throws RemoteException {
        }

        @Override // com.xpeng.airplay.service.IXpAirplayCallbacks
        public void onVolumeChanged(float f) throws RemoteException {
        }
    }

    void onAudioProgressUpdated(MediaPlaybackInfo mediaPlaybackInfo) throws RemoteException;

    void onClientConnected(int i) throws RemoteException;

    void onClientDisconnected(int i, int i2) throws RemoteException;

    void onMetaDataUpdated(MediaMetaData mediaMetaData) throws RemoteException;

    void onMirrorSizeChanged(int i, int i2) throws RemoteException;

    void onMirrorStarted() throws RemoteException;

    void onMirrorStopped() throws RemoteException;

    void onServerNameUpdated(String str) throws RemoteException;

    void onVideoPlayStarted(MediaPlayInfo mediaPlayInfo) throws RemoteException;

    void onVideoPlayStopped() throws RemoteException;

    void onVideoRateChanged(int i) throws RemoteException;

    void onVideoScrubbed(int i) throws RemoteException;

    void onVolumeChanged(float f) throws RemoteException;

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IXpAirplayCallbacks {
        private static final String DESCRIPTOR = "com.xpeng.airplay.service.IXpAirplayCallbacks";
        static final int TRANSACTION_onAudioProgressUpdated = 13;
        static final int TRANSACTION_onClientConnected = 1;
        static final int TRANSACTION_onClientDisconnected = 2;
        static final int TRANSACTION_onMetaDataUpdated = 11;
        static final int TRANSACTION_onMirrorSizeChanged = 5;
        static final int TRANSACTION_onMirrorStarted = 3;
        static final int TRANSACTION_onMirrorStopped = 4;
        static final int TRANSACTION_onServerNameUpdated = 12;
        static final int TRANSACTION_onVideoPlayStarted = 6;
        static final int TRANSACTION_onVideoPlayStopped = 7;
        static final int TRANSACTION_onVideoRateChanged = 8;
        static final int TRANSACTION_onVideoScrubbed = 9;
        static final int TRANSACTION_onVolumeChanged = 10;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IXpAirplayCallbacks asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            if (queryLocalInterface != null && (queryLocalInterface instanceof IXpAirplayCallbacks)) {
                return (IXpAirplayCallbacks) queryLocalInterface;
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
                    onClientConnected(parcel.readInt());
                    return true;
                case 2:
                    parcel.enforceInterface(DESCRIPTOR);
                    onClientDisconnected(parcel.readInt(), parcel.readInt());
                    return true;
                case 3:
                    parcel.enforceInterface(DESCRIPTOR);
                    onMirrorStarted();
                    return true;
                case 4:
                    parcel.enforceInterface(DESCRIPTOR);
                    onMirrorStopped();
                    return true;
                case 5:
                    parcel.enforceInterface(DESCRIPTOR);
                    onMirrorSizeChanged(parcel.readInt(), parcel.readInt());
                    return true;
                case 6:
                    parcel.enforceInterface(DESCRIPTOR);
                    onVideoPlayStarted(parcel.readInt() != 0 ? MediaPlayInfo.CREATOR.createFromParcel(parcel) : null);
                    return true;
                case 7:
                    parcel.enforceInterface(DESCRIPTOR);
                    onVideoPlayStopped();
                    return true;
                case 8:
                    parcel.enforceInterface(DESCRIPTOR);
                    onVideoRateChanged(parcel.readInt());
                    return true;
                case 9:
                    parcel.enforceInterface(DESCRIPTOR);
                    onVideoScrubbed(parcel.readInt());
                    return true;
                case 10:
                    parcel.enforceInterface(DESCRIPTOR);
                    onVolumeChanged(parcel.readFloat());
                    return true;
                case 11:
                    parcel.enforceInterface(DESCRIPTOR);
                    onMetaDataUpdated(parcel.readInt() != 0 ? MediaMetaData.CREATOR.createFromParcel(parcel) : null);
                    return true;
                case 12:
                    parcel.enforceInterface(DESCRIPTOR);
                    onServerNameUpdated(parcel.readString());
                    return true;
                case 13:
                    parcel.enforceInterface(DESCRIPTOR);
                    onAudioProgressUpdated(parcel.readInt() != 0 ? MediaPlaybackInfo.CREATOR.createFromParcel(parcel) : null);
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public static class Proxy implements IXpAirplayCallbacks {
            public static IXpAirplayCallbacks sDefaultImpl;
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

            @Override // com.xpeng.airplay.service.IXpAirplayCallbacks
            public void onClientConnected(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (this.mRemote.transact(1, obtain, null, 1) || Stub.getDefaultImpl() == null) {
                        return;
                    }
                    Stub.getDefaultImpl().onClientConnected(i);
                } finally {
                    obtain.recycle();
                }
            }

            @Override // com.xpeng.airplay.service.IXpAirplayCallbacks
            public void onClientDisconnected(int i, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    if (this.mRemote.transact(2, obtain, null, 1) || Stub.getDefaultImpl() == null) {
                        return;
                    }
                    Stub.getDefaultImpl().onClientDisconnected(i, i2);
                } finally {
                    obtain.recycle();
                }
            }

            @Override // com.xpeng.airplay.service.IXpAirplayCallbacks
            public void onMirrorStarted() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(3, obtain, null, 1) || Stub.getDefaultImpl() == null) {
                        return;
                    }
                    Stub.getDefaultImpl().onMirrorStarted();
                } finally {
                    obtain.recycle();
                }
            }

            @Override // com.xpeng.airplay.service.IXpAirplayCallbacks
            public void onMirrorStopped() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(4, obtain, null, 1) || Stub.getDefaultImpl() == null) {
                        return;
                    }
                    Stub.getDefaultImpl().onMirrorStopped();
                } finally {
                    obtain.recycle();
                }
            }

            @Override // com.xpeng.airplay.service.IXpAirplayCallbacks
            public void onMirrorSizeChanged(int i, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    if (this.mRemote.transact(5, obtain, null, 1) || Stub.getDefaultImpl() == null) {
                        return;
                    }
                    Stub.getDefaultImpl().onMirrorSizeChanged(i, i2);
                } finally {
                    obtain.recycle();
                }
            }

            @Override // com.xpeng.airplay.service.IXpAirplayCallbacks
            public void onVideoPlayStarted(MediaPlayInfo mediaPlayInfo) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (mediaPlayInfo != null) {
                        obtain.writeInt(1);
                        mediaPlayInfo.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (this.mRemote.transact(6, obtain, null, 1) || Stub.getDefaultImpl() == null) {
                        return;
                    }
                    Stub.getDefaultImpl().onVideoPlayStarted(mediaPlayInfo);
                } finally {
                    obtain.recycle();
                }
            }

            @Override // com.xpeng.airplay.service.IXpAirplayCallbacks
            public void onVideoPlayStopped() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(7, obtain, null, 1) || Stub.getDefaultImpl() == null) {
                        return;
                    }
                    Stub.getDefaultImpl().onVideoPlayStopped();
                } finally {
                    obtain.recycle();
                }
            }

            @Override // com.xpeng.airplay.service.IXpAirplayCallbacks
            public void onVideoRateChanged(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (this.mRemote.transact(8, obtain, null, 1) || Stub.getDefaultImpl() == null) {
                        return;
                    }
                    Stub.getDefaultImpl().onVideoRateChanged(i);
                } finally {
                    obtain.recycle();
                }
            }

            @Override // com.xpeng.airplay.service.IXpAirplayCallbacks
            public void onVideoScrubbed(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (this.mRemote.transact(9, obtain, null, 1) || Stub.getDefaultImpl() == null) {
                        return;
                    }
                    Stub.getDefaultImpl().onVideoScrubbed(i);
                } finally {
                    obtain.recycle();
                }
            }

            @Override // com.xpeng.airplay.service.IXpAirplayCallbacks
            public void onVolumeChanged(float f) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeFloat(f);
                    if (this.mRemote.transact(10, obtain, null, 1) || Stub.getDefaultImpl() == null) {
                        return;
                    }
                    Stub.getDefaultImpl().onVolumeChanged(f);
                } finally {
                    obtain.recycle();
                }
            }

            @Override // com.xpeng.airplay.service.IXpAirplayCallbacks
            public void onMetaDataUpdated(MediaMetaData mediaMetaData) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (mediaMetaData != null) {
                        obtain.writeInt(1);
                        mediaMetaData.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (this.mRemote.transact(11, obtain, null, 1) || Stub.getDefaultImpl() == null) {
                        return;
                    }
                    Stub.getDefaultImpl().onMetaDataUpdated(mediaMetaData);
                } finally {
                    obtain.recycle();
                }
            }

            @Override // com.xpeng.airplay.service.IXpAirplayCallbacks
            public void onServerNameUpdated(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (this.mRemote.transact(12, obtain, null, 1) || Stub.getDefaultImpl() == null) {
                        return;
                    }
                    Stub.getDefaultImpl().onServerNameUpdated(str);
                } finally {
                    obtain.recycle();
                }
            }

            @Override // com.xpeng.airplay.service.IXpAirplayCallbacks
            public void onAudioProgressUpdated(MediaPlaybackInfo mediaPlaybackInfo) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (mediaPlaybackInfo != null) {
                        obtain.writeInt(1);
                        mediaPlaybackInfo.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (this.mRemote.transact(13, obtain, null, 1) || Stub.getDefaultImpl() == null) {
                        return;
                    }
                    Stub.getDefaultImpl().onAudioProgressUpdated(mediaPlaybackInfo);
                } finally {
                    obtain.recycle();
                }
            }
        }

        public static boolean setDefaultImpl(IXpAirplayCallbacks iXpAirplayCallbacks) {
            if (Proxy.sDefaultImpl != null || iXpAirplayCallbacks == null) {
                return false;
            }
            Proxy.sDefaultImpl = iXpAirplayCallbacks;
            return true;
        }

        public static IXpAirplayCallbacks getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}

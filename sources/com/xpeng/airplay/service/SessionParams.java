package com.xpeng.airplay.service;

import android.os.Parcel;
import android.os.Parcelable;
/* loaded from: classes2.dex */
public class SessionParams implements Parcelable {
    public static final Parcelable.Creator<SessionParams> CREATOR = new Parcelable.Creator<SessionParams>() { // from class: com.xpeng.airplay.service.SessionParams.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public SessionParams createFromParcel(Parcel parcel) {
            return new SessionParams(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public SessionParams[] newArray(int i) {
            return new SessionParams[i];
        }
    };
    private String mPkgName;
    private int mScreenId;
    private String mServerName;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public SessionParams(String str, String str2) {
        this.mPkgName = str;
        this.mServerName = str2;
    }

    public SessionParams(String str, String str2, int i) {
        this.mPkgName = str;
        this.mServerName = str2;
        this.mScreenId = i;
    }

    private SessionParams(Parcel parcel) {
        this.mPkgName = parcel.readString();
        this.mServerName = parcel.readString();
        this.mScreenId = parcel.readInt();
    }

    public void setPackgeName(String str) {
        this.mPkgName = str;
    }

    public String getPackageName() {
        return this.mPkgName;
    }

    public void setServerName(String str) {
        this.mServerName = str;
    }

    public String getServerName() {
        return this.mServerName;
    }

    public int getScreenId() {
        return this.mScreenId;
    }

    public void setScreenId(int i) {
        this.mScreenId = i;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mPkgName);
        parcel.writeString(this.mServerName);
        parcel.writeInt(this.mScreenId);
    }

    public int hashCode() {
        return (this.mPkgName.hashCode() * 131) + (this.mServerName.hashCode() * 31) + this.mScreenId;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        SessionParams sessionParams = (SessionParams) obj;
        return sessionParams.mPkgName.equals(this.mPkgName) && sessionParams.mServerName.equals(this.mServerName) && sessionParams.mScreenId == this.mScreenId;
    }

    public String toString() {
        return "SessionParams { PkgName: " + this.mPkgName + ", ServerName: " + this.mServerName + ", ScreenId: " + this.mScreenId + "}";
    }
}

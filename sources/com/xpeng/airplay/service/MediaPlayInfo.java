package com.xpeng.airplay.service;

import android.os.Parcel;
import android.os.Parcelable;
/* loaded from: classes2.dex */
public class MediaPlayInfo implements Parcelable {
    public static final Parcelable.Creator<MediaPlayInfo> CREATOR = new Parcelable.Creator<MediaPlayInfo>() { // from class: com.xpeng.airplay.service.MediaPlayInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public MediaPlayInfo createFromParcel(Parcel parcel) {
            MediaPlayInfo mediaPlayInfo = new MediaPlayInfo();
            mediaPlayInfo.setType(parcel.readInt());
            mediaPlayInfo.setUrl(parcel.readString());
            mediaPlayInfo.setTitle(parcel.readString());
            mediaPlayInfo.setVolume(parcel.readFloat());
            mediaPlayInfo.setPosition(parcel.readInt());
            mediaPlayInfo.setStreamType(parcel.readInt());
            return mediaPlayInfo;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public MediaPlayInfo[] newArray(int i) {
            return new MediaPlayInfo[i];
        }
    };
    private int pos;
    private int streamType;
    private String title;
    private int type;
    private String url;
    private float volume;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public MediaPlayInfo() {
        this.url = "";
        this.title = "";
        this.volume = 0.0f;
        this.pos = 0;
        this.streamType = 0;
    }

    public MediaPlayInfo(String str, String str2, float f, int i) {
        this.url = str;
        this.title = str2;
        this.volume = f;
        this.pos = i;
    }

    public void copy(MediaPlayInfo mediaPlayInfo) {
        this.url = mediaPlayInfo.url;
        this.title = mediaPlayInfo.title;
        this.volume = mediaPlayInfo.volume;
        this.pos = mediaPlayInfo.pos;
        this.streamType = mediaPlayInfo.streamType;
    }

    public void setType(int i) {
        this.type = i;
    }

    public int getType() {
        return this.type;
    }

    public void setUrl(String str) {
        this.url = str;
    }

    public void setTitle(String str) {
        this.title = str;
    }

    public void setVolume(float f) {
        this.volume = f;
    }

    public void setPosition(int i) {
        this.pos = i;
    }

    public void setStreamType(int i) {
        this.streamType = i;
    }

    public String getUrl() {
        return this.url;
    }

    public String getTitle() {
        return this.title;
    }

    public float getVolume() {
        return this.volume;
    }

    public int getPosition() {
        return this.pos;
    }

    public int getStreamType() {
        return this.streamType;
    }

    public String toString() {
        return "MediaPlayInfo { url = " + this.url + ",, type = " + this.type + ",title = " + this.title + ", volume = " + this.volume + ", position = " + this.pos + ",streamType = " + this.streamType + "}";
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.type);
        parcel.writeString(this.url);
        parcel.writeString(this.title);
        parcel.writeFloat(this.volume);
        parcel.writeInt(this.pos);
        parcel.writeInt(this.streamType);
    }

    public void readFromParcel(Parcel parcel) {
        this.type = parcel.readInt();
        this.url = parcel.readString();
        this.title = parcel.readString();
        this.volume = parcel.readFloat();
        this.pos = parcel.readInt();
        this.streamType = parcel.readInt();
    }
}

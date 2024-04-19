package com.xpeng.airplay.service;

import android.os.Parcel;
import android.os.Parcelable;
/* loaded from: classes2.dex */
public class MediaPlaybackInfo implements Parcelable {
    public static final Parcelable.Creator<MediaPlaybackInfo> CREATOR = new Parcelable.Creator<MediaPlaybackInfo>() { // from class: com.xpeng.airplay.service.MediaPlaybackInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public MediaPlaybackInfo createFromParcel(Parcel parcel) {
            MediaPlaybackInfo mediaPlaybackInfo = new MediaPlaybackInfo();
            mediaPlaybackInfo.setDuration(parcel.readDouble());
            mediaPlaybackInfo.setPosition(parcel.readDouble());
            mediaPlaybackInfo.setRate(parcel.readInt());
            mediaPlaybackInfo.setVolume(parcel.readFloat());
            return mediaPlaybackInfo;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public MediaPlaybackInfo[] newArray(int i) {
            return new MediaPlaybackInfo[i];
        }
    };
    private double duration;
    private double position;
    private int rate;
    private float vol;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public MediaPlaybackInfo() {
        reset();
    }

    public void setDuration(double d) {
        this.duration = d;
    }

    public void setPosition(double d) {
        this.position = d;
    }

    public void setRate(int i) {
        this.rate = i;
    }

    public void setVolume(float f) {
        this.vol = f;
    }

    public double getDuration() {
        return this.duration;
    }

    public double getPosition() {
        return this.position;
    }

    public int getRate() {
        return this.rate;
    }

    public float getVolume() {
        return this.vol;
    }

    public String toString() {
        return "MediaPlaybackInfo { duration = " + this.duration + ",rate = " + this.rate + ",position = " + this.position + ",volume = " + this.vol + "}";
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(this.duration);
        parcel.writeDouble(this.position);
        parcel.writeInt(this.rate);
        parcel.writeFloat(this.vol);
    }

    public void readFromParcel(Parcel parcel) {
        this.duration = parcel.readDouble();
        this.position = parcel.readDouble();
        this.rate = parcel.readInt();
        this.vol = parcel.readFloat();
    }

    public void copy(MediaPlaybackInfo mediaPlaybackInfo) {
        this.duration = mediaPlaybackInfo.duration;
        this.position = mediaPlaybackInfo.position;
        this.rate = mediaPlaybackInfo.rate;
        this.vol = mediaPlaybackInfo.vol;
    }

    public void reset() {
        this.duration = 0.0d;
        this.position = 0.0d;
        this.rate = 0;
        this.vol = 0.0f;
    }
}

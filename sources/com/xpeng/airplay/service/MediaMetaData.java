package com.xpeng.airplay.service;

import android.os.Parcel;
import android.os.Parcelable;
/* loaded from: classes2.dex */
public class MediaMetaData implements Parcelable {
    public static final Parcelable.Creator<MediaMetaData> CREATOR = new Parcelable.Creator<MediaMetaData>() { // from class: com.xpeng.airplay.service.MediaMetaData.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public MediaMetaData createFromParcel(Parcel parcel) {
            return new MediaMetaData(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public MediaMetaData[] newArray(int i) {
            return new MediaMetaData[i];
        }
    };
    private String mAlbum;
    private String mArtist;
    private int mMediaType;
    private String mTitle;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public MediaMetaData(String str, String str2, String str3, int i) {
        this.mTitle = str;
        this.mArtist = str2;
        this.mAlbum = str3;
        this.mMediaType = i;
    }

    protected MediaMetaData(Parcel parcel) {
        this.mTitle = parcel.readString();
        this.mArtist = parcel.readString();
        this.mAlbum = parcel.readString();
        this.mMediaType = parcel.readInt();
    }

    public String getTitle() {
        return this.mTitle;
    }

    public void setTitle(String str) {
        this.mTitle = str;
    }

    public String getArtist() {
        return this.mArtist;
    }

    public void setArtist(String str) {
        this.mArtist = str;
    }

    public String getAlbum() {
        return this.mAlbum;
    }

    public void setAlbum(String str) {
        this.mAlbum = str;
    }

    public int getMediaType() {
        return this.mMediaType;
    }

    public void setMediaType(int i) {
        this.mMediaType = i;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mTitle);
        parcel.writeString(this.mArtist);
        parcel.writeString(this.mAlbum);
        parcel.writeInt(this.mMediaType);
    }

    public String toString() {
        return "MediaMetaData {title = " + this.mTitle + ", artist = " + this.mArtist + ",album = " + this.mAlbum + ", media type = " + this.mMediaType + "}";
    }
}

package com.android.dex;

import com.android.dex.Dex;
import com.android.dex.util.ByteArrayByteInput;
import com.android.dex.util.ByteInput;
/* loaded from: classes.dex */
public final class EncodedValue implements Comparable<EncodedValue> {
    private final byte[] data;

    public EncodedValue(byte[] bArr) {
        this.data = bArr;
    }

    public ByteInput asByteInput() {
        return new ByteArrayByteInput(this.data);
    }

    public byte[] getBytes() {
        return this.data;
    }

    public void writeTo(Dex.Section section) {
        section.write(this.data);
    }

    @Override // java.lang.Comparable
    public int compareTo(EncodedValue encodedValue) {
        int min = Math.min(this.data.length, encodedValue.data.length);
        for (int i = 0; i < min; i++) {
            byte[] bArr = this.data;
            byte b = bArr[i];
            byte[] bArr2 = encodedValue.data;
            if (b != bArr2[i]) {
                return (bArr[i] & 255) - (bArr2[i] & 255);
            }
        }
        return this.data.length - encodedValue.data.length;
    }

    public String toString() {
        return Integer.toHexString(this.data[0] & 255) + "...(" + this.data.length + ")";
    }
}

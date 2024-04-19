package com.android.dex;

import com.android.dex.util.ByteInput;
import java.io.UTFDataFormatException;
/* loaded from: classes.dex */
public final class Mutf8 {
    private Mutf8() {
    }

    public static String decode(ByteInput byteInput, char[] cArr) throws UTFDataFormatException {
        int i;
        int i2 = 0;
        while (true) {
            char readByte = (char) (byteInput.readByte() & 255);
            if (readByte == 0) {
                return new String(cArr, 0, i2);
            }
            cArr[i2] = readByte;
            if (readByte < 128) {
                i2++;
            } else {
                if ((readByte & 224) == 192) {
                    int readByte2 = byteInput.readByte() & 255;
                    if ((readByte2 & 192) != 128) {
                        throw new UTFDataFormatException("bad second byte");
                    }
                    i = i2 + 1;
                    cArr[i2] = (char) (((readByte & 31) << 6) | (readByte2 & 63));
                } else if ((readByte & 240) == 224) {
                    int readByte3 = byteInput.readByte() & 255;
                    int readByte4 = byteInput.readByte() & 255;
                    if ((readByte3 & 192) != 128 || (readByte4 & 192) != 128) {
                        break;
                    }
                    i = i2 + 1;
                    cArr[i2] = (char) (((readByte & 15) << 12) | ((readByte3 & 63) << 6) | (readByte4 & 63));
                } else {
                    throw new UTFDataFormatException("bad byte");
                }
                i2 = i;
            }
        }
        throw new UTFDataFormatException("bad second or third byte");
    }

    private static long countBytes(String str, boolean z) throws UTFDataFormatException {
        int length = str.length();
        long j = 0;
        for (int i = 0; i < length; i++) {
            char charAt = str.charAt(i);
            j += (charAt == 0 || charAt > 127) ? charAt <= 2047 ? 2L : 3L : 1L;
            if (z && j > 65535) {
                throw new UTFDataFormatException("String more than 65535 UTF bytes long");
            }
        }
        return j;
    }

    public static void encode(byte[] bArr, int i, String str) {
        int i2;
        int length = str.length();
        for (int i3 = 0; i3 < length; i3++) {
            char charAt = str.charAt(i3);
            if (charAt != 0 && charAt <= 127) {
                i2 = i + 1;
                bArr[i] = (byte) charAt;
            } else if (charAt <= 2047) {
                int i4 = i + 1;
                bArr[i] = (byte) (((charAt >> 6) & 31) | 192);
                i = i4 + 1;
                bArr[i4] = (byte) ((charAt & '?') | 128);
            } else {
                int i5 = i + 1;
                bArr[i] = (byte) (((charAt >> '\f') & 15) | 224);
                int i6 = i5 + 1;
                bArr[i5] = (byte) (((charAt >> 6) & 63) | 128);
                i2 = i6 + 1;
                bArr[i6] = (byte) ((charAt & '?') | 128);
            }
            i = i2;
        }
    }

    public static byte[] encode(String str) throws UTFDataFormatException {
        byte[] bArr = new byte[(int) countBytes(str, true)];
        encode(bArr, 0, str);
        return bArr;
    }
}

package com.android.dex;

import com.android.dex.util.ByteInput;
import com.android.dex.util.ByteOutput;
/* loaded from: classes.dex */
public final class Leb128 {
    public static int unsignedLeb128Size(int i) {
        int i2 = i >> 7;
        int i3 = 0;
        while (i2 != 0) {
            i2 >>= 7;
            i3++;
        }
        return i3 + 1;
    }

    private Leb128() {
    }

    public static int readSignedLeb128(ByteInput byteInput) {
        int i;
        int i2 = 0;
        int i3 = -1;
        int i4 = 0;
        do {
            int readByte = byteInput.readByte() & 255;
            i2 |= (readByte & 127) << (i4 * 7);
            i3 <<= 7;
            i4++;
            i = readByte & 128;
            if (i != 128) {
                break;
            }
        } while (i4 < 5);
        if (i != 128) {
            return ((i3 >> 1) & i2) != 0 ? i2 | i3 : i2;
        }
        throw new DexException("invalid LEB128 sequence");
    }

    public static int readUnsignedLeb128(ByteInput byteInput) {
        int i;
        int i2 = 0;
        int i3 = 0;
        do {
            int readByte = byteInput.readByte() & 255;
            i2 |= (readByte & 127) << (i3 * 7);
            i3++;
            i = readByte & 128;
            if (i != 128) {
                break;
            }
        } while (i3 < 5);
        if (i != 128) {
            return i2;
        }
        throw new DexException("invalid LEB128 sequence");
    }

    public static void writeUnsignedLeb128(ByteOutput byteOutput, int i) {
        while (true) {
            int i2 = i;
            i >>>= 7;
            if (i != 0) {
                byteOutput.writeByte((byte) ((i2 & 127) | 128));
            } else {
                byteOutput.writeByte((byte) (i2 & 127));
                return;
            }
        }
    }

    public static void writeSignedLeb128(ByteOutput byteOutput, int i) {
        int i2 = i >> 7;
        int i3 = (Integer.MIN_VALUE & i) == 0 ? 0 : -1;
        boolean z = true;
        while (true) {
            int i4 = i2;
            int i5 = i;
            i = i4;
            if (!z) {
                return;
            }
            z = (i == i3 && (i & 1) == ((i5 >> 6) & 1)) ? false : true;
            byteOutput.writeByte((byte) ((i5 & 127) | (z ? 128 : 0)));
            i2 = i >> 7;
        }
    }
}

package com.android.dx.util;

import com.android.dex.util.ByteOutput;
/* loaded from: classes.dex */
public interface Output extends ByteOutput {
    void alignTo(int i);

    void assertCursor(int i);

    int getCursor();

    void write(ByteArray byteArray);

    void write(byte[] bArr);

    void write(byte[] bArr, int i, int i2);

    @Override // com.android.dex.util.ByteOutput
    void writeByte(int i);

    void writeInt(int i);

    void writeLong(long j);

    void writeShort(int i);

    int writeSleb128(int i);

    int writeUleb128(int i);

    void writeZeroes(int i);
}

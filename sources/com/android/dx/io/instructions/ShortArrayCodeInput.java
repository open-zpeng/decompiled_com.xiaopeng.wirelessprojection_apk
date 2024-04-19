package com.android.dx.io.instructions;

import java.io.EOFException;
import java.util.Objects;
import kotlin.UShort;
/* loaded from: classes.dex */
public final class ShortArrayCodeInput extends BaseCodeCursor implements CodeInput {
    private final short[] array;

    public ShortArrayCodeInput(short[] sArr) {
        Objects.requireNonNull(sArr, "array == null");
        this.array = sArr;
    }

    @Override // com.android.dx.io.instructions.CodeInput
    public boolean hasMore() {
        return cursor() < this.array.length;
    }

    @Override // com.android.dx.io.instructions.CodeInput
    public int read() throws EOFException {
        try {
            short s = this.array[cursor()];
            advance(1);
            return s & UShort.MAX_VALUE;
        } catch (ArrayIndexOutOfBoundsException unused) {
            throw new EOFException();
        }
    }

    @Override // com.android.dx.io.instructions.CodeInput
    public int readInt() throws EOFException {
        return read() | (read() << 16);
    }

    @Override // com.android.dx.io.instructions.CodeInput
    public long readLong() throws EOFException {
        return read() | (read() << 16) | (read() << 32) | (read() << 48);
    }
}

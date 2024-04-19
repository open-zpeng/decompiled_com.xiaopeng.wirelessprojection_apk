package com.android.dx.io.instructions;

import com.android.dx.io.IndexType;
/* loaded from: classes.dex */
public final class ZeroRegisterDecodedInstruction extends DecodedInstruction {
    @Override // com.android.dx.io.instructions.DecodedInstruction
    public int getRegisterCount() {
        return 0;
    }

    public ZeroRegisterDecodedInstruction(InstructionCodec instructionCodec, int i, int i2, IndexType indexType, int i3, long j) {
        super(instructionCodec, i, i2, indexType, i3, j);
    }

    @Override // com.android.dx.io.instructions.DecodedInstruction
    public DecodedInstruction withIndex(int i) {
        return new ZeroRegisterDecodedInstruction(getFormat(), getOpcode(), i, getIndexType(), getTarget(), getLiteral());
    }
}

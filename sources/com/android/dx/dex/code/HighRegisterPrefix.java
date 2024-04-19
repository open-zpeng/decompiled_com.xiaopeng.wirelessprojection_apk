package com.android.dx.dex.code;

import com.android.dx.rop.code.RegisterSpec;
import com.android.dx.rop.code.RegisterSpecList;
import com.android.dx.rop.code.SourcePosition;
import com.android.dx.util.AnnotatedOutput;
/* loaded from: classes.dex */
public final class HighRegisterPrefix extends VariableSizeInsn {
    private SimpleInsn[] insns;

    @Override // com.android.dx.dex.code.DalvInsn
    protected String argString() {
        return null;
    }

    public HighRegisterPrefix(SourcePosition sourcePosition, RegisterSpecList registerSpecList) {
        super(sourcePosition, registerSpecList);
        if (registerSpecList.size() == 0) {
            throw new IllegalArgumentException("registers.size() == 0");
        }
        this.insns = null;
    }

    @Override // com.android.dx.dex.code.DalvInsn
    public int codeSize() {
        calculateInsnsIfNecessary();
        int i = 0;
        for (SimpleInsn simpleInsn : this.insns) {
            i += simpleInsn.codeSize();
        }
        return i;
    }

    @Override // com.android.dx.dex.code.DalvInsn
    public void writeTo(AnnotatedOutput annotatedOutput) {
        calculateInsnsIfNecessary();
        for (SimpleInsn simpleInsn : this.insns) {
            simpleInsn.writeTo(annotatedOutput);
        }
    }

    private void calculateInsnsIfNecessary() {
        if (this.insns != null) {
            return;
        }
        RegisterSpecList registers = getRegisters();
        int size = registers.size();
        this.insns = new SimpleInsn[size];
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            RegisterSpec registerSpec = registers.get(i2);
            this.insns[i2] = moveInsnFor(registerSpec, i);
            i += registerSpec.getCategory();
        }
    }

    @Override // com.android.dx.dex.code.DalvInsn
    public DalvInsn withRegisters(RegisterSpecList registerSpecList) {
        return new HighRegisterPrefix(getPosition(), registerSpecList);
    }

    @Override // com.android.dx.dex.code.DalvInsn
    protected String listingString0(boolean z) {
        RegisterSpecList registers = getRegisters();
        int size = registers.size();
        StringBuilder sb = new StringBuilder(100);
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            RegisterSpec registerSpec = registers.get(i2);
            SimpleInsn moveInsnFor = moveInsnFor(registerSpec, i);
            if (i2 != 0) {
                sb.append('\n');
            }
            sb.append(moveInsnFor.listingString0(z));
            i += registerSpec.getCategory();
        }
        return sb.toString();
    }

    private static SimpleInsn moveInsnFor(RegisterSpec registerSpec, int i) {
        return DalvInsn.makeMove(SourcePosition.NO_INFO, RegisterSpec.make(i, registerSpec.getType()), registerSpec);
    }
}

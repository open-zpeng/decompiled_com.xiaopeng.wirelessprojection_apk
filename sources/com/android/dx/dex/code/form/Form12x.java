package com.android.dx.dex.code.form;

import com.android.dx.dex.code.DalvInsn;
import com.android.dx.dex.code.InsnFormat;
import com.android.dx.dex.code.SimpleInsn;
import com.android.dx.rop.code.RegisterSpec;
import com.android.dx.rop.code.RegisterSpecList;
import com.android.dx.util.AnnotatedOutput;
import java.util.BitSet;
/* loaded from: classes.dex */
public final class Form12x extends InsnFormat {
    public static final InsnFormat THE_ONE = new Form12x();

    @Override // com.android.dx.dex.code.InsnFormat
    public int codeSize() {
        return 1;
    }

    @Override // com.android.dx.dex.code.InsnFormat
    public String insnCommentString(DalvInsn dalvInsn, boolean z) {
        return "";
    }

    private Form12x() {
    }

    @Override // com.android.dx.dex.code.InsnFormat
    public String insnArgString(DalvInsn dalvInsn) {
        RegisterSpecList registers = dalvInsn.getRegisters();
        int size = registers.size();
        return registers.get(size - 2).regString() + ", " + registers.get(size - 1).regString();
    }

    @Override // com.android.dx.dex.code.InsnFormat
    public boolean isCompatible(DalvInsn dalvInsn) {
        RegisterSpec registerSpec;
        RegisterSpec registerSpec2;
        if (dalvInsn instanceof SimpleInsn) {
            RegisterSpecList registers = dalvInsn.getRegisters();
            int size = registers.size();
            if (size == 2) {
                registerSpec = registers.get(0);
                registerSpec2 = registers.get(1);
            } else if (size != 3) {
                return false;
            } else {
                registerSpec = registers.get(1);
                registerSpec2 = registers.get(2);
                if (registerSpec.getReg() != registers.get(0).getReg()) {
                    return false;
                }
            }
            return unsignedFitsInNibble(registerSpec.getReg()) && unsignedFitsInNibble(registerSpec2.getReg());
        }
        return false;
    }

    @Override // com.android.dx.dex.code.InsnFormat
    public BitSet compatibleRegs(DalvInsn dalvInsn) {
        RegisterSpecList registers = dalvInsn.getRegisters();
        BitSet bitSet = new BitSet(2);
        int reg = registers.get(0).getReg();
        int reg2 = registers.get(1).getReg();
        int size = registers.size();
        if (size == 2) {
            bitSet.set(0, unsignedFitsInNibble(reg));
            bitSet.set(1, unsignedFitsInNibble(reg2));
        } else if (size == 3) {
            if (reg != reg2) {
                bitSet.set(0, false);
                bitSet.set(1, false);
            } else {
                boolean unsignedFitsInNibble = unsignedFitsInNibble(reg2);
                bitSet.set(0, unsignedFitsInNibble);
                bitSet.set(1, unsignedFitsInNibble);
            }
            bitSet.set(2, unsignedFitsInNibble(registers.get(2).getReg()));
        } else {
            throw new AssertionError();
        }
        return bitSet;
    }

    @Override // com.android.dx.dex.code.InsnFormat
    public void writeTo(AnnotatedOutput annotatedOutput, DalvInsn dalvInsn) {
        RegisterSpecList registers = dalvInsn.getRegisters();
        int size = registers.size();
        write(annotatedOutput, opcodeUnit(dalvInsn, makeByte(registers.get(size - 2).getReg(), registers.get(size - 1).getReg())));
    }
}

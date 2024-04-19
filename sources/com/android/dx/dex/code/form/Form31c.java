package com.android.dx.dex.code.form;

import com.android.dx.dex.code.CstInsn;
import com.android.dx.dex.code.DalvInsn;
import com.android.dx.dex.code.InsnFormat;
import com.android.dx.rop.code.RegisterSpec;
import com.android.dx.rop.code.RegisterSpecList;
import com.android.dx.rop.cst.Constant;
import com.android.dx.rop.cst.CstFieldRef;
import com.android.dx.rop.cst.CstString;
import com.android.dx.rop.cst.CstType;
import com.android.dx.util.AnnotatedOutput;
import java.util.BitSet;
/* loaded from: classes.dex */
public final class Form31c extends InsnFormat {
    public static final InsnFormat THE_ONE = new Form31c();

    @Override // com.android.dx.dex.code.InsnFormat
    public int codeSize() {
        return 3;
    }

    private Form31c() {
    }

    @Override // com.android.dx.dex.code.InsnFormat
    public String insnArgString(DalvInsn dalvInsn) {
        return dalvInsn.getRegisters().get(0).regString() + ", " + dalvInsn.cstString();
    }

    @Override // com.android.dx.dex.code.InsnFormat
    public String insnCommentString(DalvInsn dalvInsn, boolean z) {
        return z ? dalvInsn.cstComment() : "";
    }

    @Override // com.android.dx.dex.code.InsnFormat
    public boolean isCompatible(DalvInsn dalvInsn) {
        RegisterSpec registerSpec;
        if (dalvInsn instanceof CstInsn) {
            RegisterSpecList registers = dalvInsn.getRegisters();
            int size = registers.size();
            if (size == 1) {
                registerSpec = registers.get(0);
            } else if (size != 2) {
                return false;
            } else {
                registerSpec = registers.get(0);
                if (registerSpec.getReg() != registers.get(1).getReg()) {
                    return false;
                }
            }
            if (unsignedFitsInByte(registerSpec.getReg())) {
                Constant constant = ((CstInsn) dalvInsn).getConstant();
                return (constant instanceof CstType) || (constant instanceof CstFieldRef) || (constant instanceof CstString);
            }
            return false;
        }
        return false;
    }

    @Override // com.android.dx.dex.code.InsnFormat
    public BitSet compatibleRegs(DalvInsn dalvInsn) {
        RegisterSpecList registers = dalvInsn.getRegisters();
        int size = registers.size();
        BitSet bitSet = new BitSet(size);
        boolean unsignedFitsInByte = unsignedFitsInByte(registers.get(0).getReg());
        if (size == 1) {
            bitSet.set(0, unsignedFitsInByte);
        } else if (registers.get(0).getReg() == registers.get(1).getReg()) {
            bitSet.set(0, unsignedFitsInByte);
            bitSet.set(1, unsignedFitsInByte);
        }
        return bitSet;
    }

    @Override // com.android.dx.dex.code.InsnFormat
    public void writeTo(AnnotatedOutput annotatedOutput, DalvInsn dalvInsn) {
        RegisterSpecList registers = dalvInsn.getRegisters();
        write(annotatedOutput, opcodeUnit(dalvInsn, registers.get(0).getReg()), ((CstInsn) dalvInsn).getIndex());
    }
}

package com.android.dx.dex.code.form;

import com.android.dx.dex.code.CstInsn;
import com.android.dx.dex.code.DalvInsn;
import com.android.dx.dex.code.InsnFormat;
import com.android.dx.rop.code.RegisterSpecList;
import com.android.dx.rop.cst.Constant;
import com.android.dx.rop.cst.CstLiteralBits;
import com.android.dx.util.AnnotatedOutput;
import java.util.BitSet;
/* loaded from: classes.dex */
public final class Form21h extends InsnFormat {
    public static final InsnFormat THE_ONE = new Form21h();

    @Override // com.android.dx.dex.code.InsnFormat
    public int codeSize() {
        return 2;
    }

    private Form21h() {
    }

    @Override // com.android.dx.dex.code.InsnFormat
    public String insnArgString(DalvInsn dalvInsn) {
        return dalvInsn.getRegisters().get(0).regString() + ", " + literalBitsString((CstLiteralBits) ((CstInsn) dalvInsn).getConstant());
    }

    @Override // com.android.dx.dex.code.InsnFormat
    public String insnCommentString(DalvInsn dalvInsn, boolean z) {
        return literalBitsComment((CstLiteralBits) ((CstInsn) dalvInsn).getConstant(), dalvInsn.getRegisters().get(0).getCategory() == 1 ? 32 : 64);
    }

    @Override // com.android.dx.dex.code.InsnFormat
    public boolean isCompatible(DalvInsn dalvInsn) {
        RegisterSpecList registers = dalvInsn.getRegisters();
        if ((dalvInsn instanceof CstInsn) && registers.size() == 1 && unsignedFitsInByte(registers.get(0).getReg())) {
            Constant constant = ((CstInsn) dalvInsn).getConstant();
            if (constant instanceof CstLiteralBits) {
                CstLiteralBits cstLiteralBits = (CstLiteralBits) constant;
                return registers.get(0).getCategory() == 1 ? (cstLiteralBits.getIntBits() & 65535) == 0 : (cstLiteralBits.getLongBits() & 281474976710655L) == 0;
            }
            return false;
        }
        return false;
    }

    @Override // com.android.dx.dex.code.InsnFormat
    public BitSet compatibleRegs(DalvInsn dalvInsn) {
        RegisterSpecList registers = dalvInsn.getRegisters();
        BitSet bitSet = new BitSet(1);
        bitSet.set(0, unsignedFitsInByte(registers.get(0).getReg()));
        return bitSet;
    }

    @Override // com.android.dx.dex.code.InsnFormat
    public void writeTo(AnnotatedOutput annotatedOutput, DalvInsn dalvInsn) {
        int longBits;
        RegisterSpecList registers = dalvInsn.getRegisters();
        CstLiteralBits cstLiteralBits = (CstLiteralBits) ((CstInsn) dalvInsn).getConstant();
        if (registers.get(0).getCategory() == 1) {
            longBits = cstLiteralBits.getIntBits() >>> 16;
        } else {
            longBits = (int) (cstLiteralBits.getLongBits() >>> 48);
        }
        write(annotatedOutput, opcodeUnit(dalvInsn, registers.get(0).getReg()), (short) longBits);
    }
}

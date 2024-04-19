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
public final class Form11n extends InsnFormat {
    public static final InsnFormat THE_ONE = new Form11n();

    @Override // com.android.dx.dex.code.InsnFormat
    public int codeSize() {
        return 1;
    }

    private Form11n() {
    }

    @Override // com.android.dx.dex.code.InsnFormat
    public String insnArgString(DalvInsn dalvInsn) {
        return dalvInsn.getRegisters().get(0).regString() + ", " + literalBitsString((CstLiteralBits) ((CstInsn) dalvInsn).getConstant());
    }

    @Override // com.android.dx.dex.code.InsnFormat
    public String insnCommentString(DalvInsn dalvInsn, boolean z) {
        return literalBitsComment((CstLiteralBits) ((CstInsn) dalvInsn).getConstant(), 4);
    }

    @Override // com.android.dx.dex.code.InsnFormat
    public boolean isCompatible(DalvInsn dalvInsn) {
        RegisterSpecList registers = dalvInsn.getRegisters();
        if ((dalvInsn instanceof CstInsn) && registers.size() == 1 && unsignedFitsInNibble(registers.get(0).getReg())) {
            Constant constant = ((CstInsn) dalvInsn).getConstant();
            if (constant instanceof CstLiteralBits) {
                CstLiteralBits cstLiteralBits = (CstLiteralBits) constant;
                return cstLiteralBits.fitsInInt() && signedFitsInNibble(cstLiteralBits.getIntBits());
            }
            return false;
        }
        return false;
    }

    @Override // com.android.dx.dex.code.InsnFormat
    public BitSet compatibleRegs(DalvInsn dalvInsn) {
        RegisterSpecList registers = dalvInsn.getRegisters();
        BitSet bitSet = new BitSet(1);
        bitSet.set(0, unsignedFitsInNibble(registers.get(0).getReg()));
        return bitSet;
    }

    @Override // com.android.dx.dex.code.InsnFormat
    public void writeTo(AnnotatedOutput annotatedOutput, DalvInsn dalvInsn) {
        write(annotatedOutput, opcodeUnit(dalvInsn, makeByte(dalvInsn.getRegisters().get(0).getReg(), ((CstLiteralBits) ((CstInsn) dalvInsn).getConstant()).getIntBits() & 15)));
    }
}

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
public final class Form22s extends InsnFormat {
    public static final InsnFormat THE_ONE = new Form22s();

    @Override // com.android.dx.dex.code.InsnFormat
    public int codeSize() {
        return 2;
    }

    private Form22s() {
    }

    @Override // com.android.dx.dex.code.InsnFormat
    public String insnArgString(DalvInsn dalvInsn) {
        RegisterSpecList registers = dalvInsn.getRegisters();
        return registers.get(0).regString() + ", " + registers.get(1).regString() + ", " + literalBitsString((CstLiteralBits) ((CstInsn) dalvInsn).getConstant());
    }

    @Override // com.android.dx.dex.code.InsnFormat
    public String insnCommentString(DalvInsn dalvInsn, boolean z) {
        return literalBitsComment((CstLiteralBits) ((CstInsn) dalvInsn).getConstant(), 16);
    }

    @Override // com.android.dx.dex.code.InsnFormat
    public boolean isCompatible(DalvInsn dalvInsn) {
        RegisterSpecList registers = dalvInsn.getRegisters();
        if ((dalvInsn instanceof CstInsn) && registers.size() == 2 && unsignedFitsInNibble(registers.get(0).getReg()) && unsignedFitsInNibble(registers.get(1).getReg())) {
            Constant constant = ((CstInsn) dalvInsn).getConstant();
            if (constant instanceof CstLiteralBits) {
                CstLiteralBits cstLiteralBits = (CstLiteralBits) constant;
                return cstLiteralBits.fitsInInt() && signedFitsInShort(cstLiteralBits.getIntBits());
            }
            return false;
        }
        return false;
    }

    @Override // com.android.dx.dex.code.InsnFormat
    public BitSet compatibleRegs(DalvInsn dalvInsn) {
        RegisterSpecList registers = dalvInsn.getRegisters();
        BitSet bitSet = new BitSet(2);
        bitSet.set(0, unsignedFitsInNibble(registers.get(0).getReg()));
        bitSet.set(1, unsignedFitsInNibble(registers.get(1).getReg()));
        return bitSet;
    }

    @Override // com.android.dx.dex.code.InsnFormat
    public void writeTo(AnnotatedOutput annotatedOutput, DalvInsn dalvInsn) {
        RegisterSpecList registers = dalvInsn.getRegisters();
        write(annotatedOutput, opcodeUnit(dalvInsn, makeByte(registers.get(0).getReg(), registers.get(1).getReg())), (short) ((CstLiteralBits) ((CstInsn) dalvInsn).getConstant()).getIntBits());
    }
}

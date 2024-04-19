package com.android.dx.dex.code.form;

import com.android.dx.dex.code.DalvInsn;
import com.android.dx.dex.code.InsnFormat;
import com.android.dx.dex.code.TargetInsn;
import com.android.dx.rop.code.RegisterSpecList;
import com.android.dx.util.AnnotatedOutput;
import java.util.BitSet;
/* loaded from: classes.dex */
public final class Form22t extends InsnFormat {
    public static final InsnFormat THE_ONE = new Form22t();

    @Override // com.android.dx.dex.code.InsnFormat
    public int codeSize() {
        return 2;
    }

    private Form22t() {
    }

    @Override // com.android.dx.dex.code.InsnFormat
    public String insnArgString(DalvInsn dalvInsn) {
        RegisterSpecList registers = dalvInsn.getRegisters();
        return registers.get(0).regString() + ", " + registers.get(1).regString() + ", " + branchString(dalvInsn);
    }

    @Override // com.android.dx.dex.code.InsnFormat
    public String insnCommentString(DalvInsn dalvInsn, boolean z) {
        return branchComment(dalvInsn);
    }

    @Override // com.android.dx.dex.code.InsnFormat
    public boolean isCompatible(DalvInsn dalvInsn) {
        RegisterSpecList registers = dalvInsn.getRegisters();
        if ((dalvInsn instanceof TargetInsn) && registers.size() == 2 && unsignedFitsInNibble(registers.get(0).getReg()) && unsignedFitsInNibble(registers.get(1).getReg())) {
            TargetInsn targetInsn = (TargetInsn) dalvInsn;
            if (targetInsn.hasTargetOffset()) {
                return branchFits(targetInsn);
            }
            return true;
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
    public boolean branchFits(TargetInsn targetInsn) {
        int targetOffset = targetInsn.getTargetOffset();
        return targetOffset != 0 && signedFitsInShort(targetOffset);
    }

    @Override // com.android.dx.dex.code.InsnFormat
    public void writeTo(AnnotatedOutput annotatedOutput, DalvInsn dalvInsn) {
        RegisterSpecList registers = dalvInsn.getRegisters();
        write(annotatedOutput, opcodeUnit(dalvInsn, makeByte(registers.get(0).getReg(), registers.get(1).getReg())), (short) ((TargetInsn) dalvInsn).getTargetOffset());
    }
}

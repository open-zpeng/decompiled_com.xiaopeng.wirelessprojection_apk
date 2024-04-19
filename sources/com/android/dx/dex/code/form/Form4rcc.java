package com.android.dx.dex.code.form;

import com.android.dx.dex.code.DalvInsn;
import com.android.dx.dex.code.InsnFormat;
import com.android.dx.dex.code.MultiCstInsn;
import com.android.dx.rop.code.RegisterSpecList;
import com.android.dx.rop.cst.CstMethodRef;
import com.android.dx.rop.cst.CstProtoRef;
import com.android.dx.util.AnnotatedOutput;
/* loaded from: classes.dex */
public final class Form4rcc extends InsnFormat {
    public static final InsnFormat THE_ONE = new Form4rcc();

    @Override // com.android.dx.dex.code.InsnFormat
    public int codeSize() {
        return 4;
    }

    private Form4rcc() {
    }

    @Override // com.android.dx.dex.code.InsnFormat
    public String insnArgString(DalvInsn dalvInsn) {
        return regRangeString(dalvInsn.getRegisters()) + ", " + dalvInsn.cstString();
    }

    @Override // com.android.dx.dex.code.InsnFormat
    public String insnCommentString(DalvInsn dalvInsn, boolean z) {
        return z ? dalvInsn.cstComment() : "";
    }

    @Override // com.android.dx.dex.code.InsnFormat
    public boolean isCompatible(DalvInsn dalvInsn) {
        if (dalvInsn instanceof MultiCstInsn) {
            MultiCstInsn multiCstInsn = (MultiCstInsn) dalvInsn;
            int index = multiCstInsn.getIndex(0);
            int index2 = multiCstInsn.getIndex(1);
            if (unsignedFitsInShort(index) && unsignedFitsInShort(index2) && (multiCstInsn.getConstant(0) instanceof CstMethodRef) && (multiCstInsn.getConstant(1) instanceof CstProtoRef)) {
                RegisterSpecList registers = multiCstInsn.getRegisters();
                int size = registers.size();
                if (size == 0) {
                    return true;
                }
                return unsignedFitsInByte(registers.getWordCount()) && unsignedFitsInShort(size) && unsignedFitsInShort(registers.get(0).getReg()) && isRegListSequential(registers);
            }
            return false;
        }
        return false;
    }

    @Override // com.android.dx.dex.code.InsnFormat
    public void writeTo(AnnotatedOutput annotatedOutput, DalvInsn dalvInsn) {
        MultiCstInsn multiCstInsn = (MultiCstInsn) dalvInsn;
        short index = (short) multiCstInsn.getIndex(0);
        short index2 = (short) multiCstInsn.getIndex(1);
        RegisterSpecList registers = dalvInsn.getRegisters();
        write(annotatedOutput, opcodeUnit(dalvInsn, registers.getWordCount()), index, registers.size() > 0 ? (short) registers.get(0).getReg() : (short) 0, index2);
    }
}

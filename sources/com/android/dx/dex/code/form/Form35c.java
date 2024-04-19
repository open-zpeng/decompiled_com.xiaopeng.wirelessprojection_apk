package com.android.dx.dex.code.form;

import com.android.dx.dex.code.CstInsn;
import com.android.dx.dex.code.DalvInsn;
import com.android.dx.dex.code.InsnFormat;
import com.android.dx.rop.code.RegisterSpec;
import com.android.dx.rop.code.RegisterSpecList;
import com.android.dx.rop.cst.Constant;
import com.android.dx.rop.cst.CstCallSiteRef;
import com.android.dx.rop.cst.CstMethodRef;
import com.android.dx.rop.cst.CstType;
import com.android.dx.rop.type.Type;
import com.android.dx.util.AnnotatedOutput;
import java.util.BitSet;
/* loaded from: classes.dex */
public final class Form35c extends InsnFormat {
    private static final int MAX_NUM_OPS = 5;
    public static final InsnFormat THE_ONE = new Form35c();

    @Override // com.android.dx.dex.code.InsnFormat
    public int codeSize() {
        return 3;
    }

    private Form35c() {
    }

    @Override // com.android.dx.dex.code.InsnFormat
    public String insnArgString(DalvInsn dalvInsn) {
        return regListString(explicitize(dalvInsn.getRegisters())) + ", " + dalvInsn.cstString();
    }

    @Override // com.android.dx.dex.code.InsnFormat
    public String insnCommentString(DalvInsn dalvInsn, boolean z) {
        return z ? dalvInsn.cstComment() : "";
    }

    @Override // com.android.dx.dex.code.InsnFormat
    public boolean isCompatible(DalvInsn dalvInsn) {
        if (dalvInsn instanceof CstInsn) {
            CstInsn cstInsn = (CstInsn) dalvInsn;
            if (unsignedFitsInShort(cstInsn.getIndex())) {
                Constant constant = cstInsn.getConstant();
                return ((constant instanceof CstMethodRef) || (constant instanceof CstType) || (constant instanceof CstCallSiteRef)) && wordCount(cstInsn.getRegisters()) >= 0;
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
        for (int i = 0; i < size; i++) {
            RegisterSpec registerSpec = registers.get(i);
            bitSet.set(i, unsignedFitsInNibble((registerSpec.getReg() + registerSpec.getCategory()) - 1));
        }
        return bitSet;
    }

    @Override // com.android.dx.dex.code.InsnFormat
    public void writeTo(AnnotatedOutput annotatedOutput, DalvInsn dalvInsn) {
        int index = ((CstInsn) dalvInsn).getIndex();
        RegisterSpecList explicitize = explicitize(dalvInsn.getRegisters());
        int size = explicitize.size();
        write(annotatedOutput, opcodeUnit(dalvInsn, makeByte(size > 4 ? explicitize.get(4).getReg() : 0, size)), (short) index, codeUnit(size > 0 ? explicitize.get(0).getReg() : 0, size > 1 ? explicitize.get(1).getReg() : 0, size > 2 ? explicitize.get(2).getReg() : 0, size > 3 ? explicitize.get(3).getReg() : 0));
    }

    private static int wordCount(RegisterSpecList registerSpecList) {
        RegisterSpec registerSpec;
        int size = registerSpecList.size();
        if (size > 5) {
            return -1;
        }
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            i += registerSpecList.get(i2).getCategory();
            if (!unsignedFitsInNibble((registerSpec.getReg() + registerSpec.getCategory()) - 1)) {
                return -1;
            }
        }
        if (i <= 5) {
            return i;
        }
        return -1;
    }

    private static RegisterSpecList explicitize(RegisterSpecList registerSpecList) {
        int wordCount = wordCount(registerSpecList);
        int size = registerSpecList.size();
        if (wordCount == size) {
            return registerSpecList;
        }
        RegisterSpecList registerSpecList2 = new RegisterSpecList(wordCount);
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            RegisterSpec registerSpec = registerSpecList.get(i2);
            registerSpecList2.set(i, registerSpec);
            if (registerSpec.getCategory() == 2) {
                registerSpecList2.set(i + 1, RegisterSpec.make(registerSpec.getReg() + 1, Type.VOID));
                i += 2;
            } else {
                i++;
            }
        }
        registerSpecList2.setImmutable();
        return registerSpecList2;
    }
}

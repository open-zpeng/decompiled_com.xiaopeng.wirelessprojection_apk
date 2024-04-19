package com.android.dx.dex.code.form;

import com.android.dx.dex.code.DalvInsn;
import com.android.dx.dex.code.InsnFormat;
import com.android.dx.dex.code.TargetInsn;
import com.android.dx.util.AnnotatedOutput;
/* loaded from: classes.dex */
public final class Form10t extends InsnFormat {
    public static final InsnFormat THE_ONE = new Form10t();

    @Override // com.android.dx.dex.code.InsnFormat
    public int codeSize() {
        return 1;
    }

    private Form10t() {
    }

    @Override // com.android.dx.dex.code.InsnFormat
    public String insnArgString(DalvInsn dalvInsn) {
        return branchString(dalvInsn);
    }

    @Override // com.android.dx.dex.code.InsnFormat
    public String insnCommentString(DalvInsn dalvInsn, boolean z) {
        return branchComment(dalvInsn);
    }

    @Override // com.android.dx.dex.code.InsnFormat
    public boolean isCompatible(DalvInsn dalvInsn) {
        if ((dalvInsn instanceof TargetInsn) && dalvInsn.getRegisters().size() == 0) {
            TargetInsn targetInsn = (TargetInsn) dalvInsn;
            if (targetInsn.hasTargetOffset()) {
                return branchFits(targetInsn);
            }
            return true;
        }
        return false;
    }

    @Override // com.android.dx.dex.code.InsnFormat
    public boolean branchFits(TargetInsn targetInsn) {
        int targetOffset = targetInsn.getTargetOffset();
        return targetOffset != 0 && signedFitsInByte(targetOffset);
    }

    @Override // com.android.dx.dex.code.InsnFormat
    public void writeTo(AnnotatedOutput annotatedOutput, DalvInsn dalvInsn) {
        write(annotatedOutput, opcodeUnit(dalvInsn, ((TargetInsn) dalvInsn).getTargetOffset() & 255));
    }
}

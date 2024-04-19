package com.android.dx.rop.code;

import com.android.dx.rop.code.Insn;
import com.android.dx.rop.type.StdTypeList;
import com.android.dx.rop.type.Type;
import com.android.dx.rop.type.TypeList;
import com.android.dx.util.IntList;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import java.util.Objects;
/* loaded from: classes.dex */
public final class SwitchInsn extends Insn {
    private final IntList cases;

    @Override // com.android.dx.rop.code.Insn
    public boolean contentEquals(Insn insn) {
        return false;
    }

    public SwitchInsn(Rop rop, SourcePosition sourcePosition, RegisterSpec registerSpec, RegisterSpecList registerSpecList, IntList intList) {
        super(rop, sourcePosition, registerSpec, registerSpecList);
        if (rop.getBranchingness() != 5) {
            throw new IllegalArgumentException("bogus branchingness");
        }
        Objects.requireNonNull(intList, "cases == null");
        this.cases = intList;
    }

    @Override // com.android.dx.rop.code.Insn
    public String getInlineString() {
        return this.cases.toString();
    }

    @Override // com.android.dx.rop.code.Insn
    public TypeList getCatches() {
        return StdTypeList.EMPTY;
    }

    @Override // com.android.dx.rop.code.Insn
    public void accept(Insn.Visitor visitor) {
        visitor.visitSwitchInsn(this);
    }

    @Override // com.android.dx.rop.code.Insn
    public Insn withAddedCatch(Type type) {
        throw new UnsupportedOperationException(VuiConstants.PROPS_UNSUPPORTED);
    }

    @Override // com.android.dx.rop.code.Insn
    public Insn withRegisterOffset(int i) {
        return new SwitchInsn(getOpcode(), getPosition(), getResult().withOffset(i), getSources().withOffset(i), this.cases);
    }

    @Override // com.android.dx.rop.code.Insn
    public Insn withNewRegisters(RegisterSpec registerSpec, RegisterSpecList registerSpecList) {
        return new SwitchInsn(getOpcode(), getPosition(), registerSpec, registerSpecList, this.cases);
    }

    public IntList getCases() {
        return this.cases;
    }
}

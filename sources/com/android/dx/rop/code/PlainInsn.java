package com.android.dx.rop.code;

import com.android.dx.rop.code.Insn;
import com.android.dx.rop.cst.Constant;
import com.android.dx.rop.cst.CstInteger;
import com.android.dx.rop.type.StdTypeList;
import com.android.dx.rop.type.Type;
import com.android.dx.rop.type.TypeBearer;
import com.android.dx.rop.type.TypeList;
import com.xiaopeng.speech.vui.constants.VuiConstants;
/* loaded from: classes.dex */
public final class PlainInsn extends Insn {
    public PlainInsn(Rop rop, SourcePosition sourcePosition, RegisterSpec registerSpec, RegisterSpecList registerSpecList) {
        super(rop, sourcePosition, registerSpec, registerSpecList);
        int branchingness = rop.getBranchingness();
        if (branchingness == 5 || branchingness == 6) {
            throw new IllegalArgumentException("opcode with invalid branchingness: " + rop.getBranchingness());
        }
        if (registerSpec != null && rop.getBranchingness() != 1) {
            throw new IllegalArgumentException("can't mix branchingness with result");
        }
    }

    public PlainInsn(Rop rop, SourcePosition sourcePosition, RegisterSpec registerSpec, RegisterSpec registerSpec2) {
        this(rop, sourcePosition, registerSpec, RegisterSpecList.make(registerSpec2));
    }

    @Override // com.android.dx.rop.code.Insn
    public TypeList getCatches() {
        return StdTypeList.EMPTY;
    }

    @Override // com.android.dx.rop.code.Insn
    public void accept(Insn.Visitor visitor) {
        visitor.visitPlainInsn(this);
    }

    @Override // com.android.dx.rop.code.Insn
    public Insn withAddedCatch(Type type) {
        throw new UnsupportedOperationException(VuiConstants.PROPS_UNSUPPORTED);
    }

    @Override // com.android.dx.rop.code.Insn
    public Insn withRegisterOffset(int i) {
        return new PlainInsn(getOpcode(), getPosition(), getResult().withOffset(i), getSources().withOffset(i));
    }

    @Override // com.android.dx.rop.code.Insn
    public Insn withSourceLiteral() {
        RegisterSpecList sources = getSources();
        int size = sources.size();
        if (size == 0) {
            return this;
        }
        TypeBearer typeBearer = sources.get(size - 1).getTypeBearer();
        if (!typeBearer.isConstant()) {
            TypeBearer typeBearer2 = sources.get(0).getTypeBearer();
            if (size == 2 && typeBearer2.isConstant()) {
                Constant constant = (Constant) typeBearer2;
                RegisterSpecList withoutFirst = sources.withoutFirst();
                return new PlainCstInsn(Rops.ropFor(getOpcode().getOpcode(), getResult(), withoutFirst, constant), getPosition(), getResult(), withoutFirst, constant);
            }
            return this;
        }
        Constant constant2 = (Constant) typeBearer;
        RegisterSpecList withoutLast = sources.withoutLast();
        try {
            int opcode = getOpcode().getOpcode();
            if (opcode == 15 && (constant2 instanceof CstInteger)) {
                opcode = 14;
                constant2 = CstInteger.make(-((CstInteger) constant2).getValue());
            }
            Constant constant3 = constant2;
            return new PlainCstInsn(Rops.ropFor(opcode, getResult(), withoutLast, constant3), getPosition(), getResult(), withoutLast, constant3);
        } catch (IllegalArgumentException unused) {
            return this;
        }
    }

    @Override // com.android.dx.rop.code.Insn
    public Insn withNewRegisters(RegisterSpec registerSpec, RegisterSpecList registerSpecList) {
        return new PlainInsn(getOpcode(), getPosition(), registerSpec, registerSpecList);
    }
}

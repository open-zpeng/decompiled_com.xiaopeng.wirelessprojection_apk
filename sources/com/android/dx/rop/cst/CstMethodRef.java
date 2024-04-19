package com.android.dx.rop.cst;
/* loaded from: classes.dex */
public final class CstMethodRef extends CstBaseMethodRef {
    @Override // com.android.dx.rop.cst.Constant
    public String typeName() {
        return "method";
    }

    public CstMethodRef(CstType cstType, CstNat cstNat) {
        super(cstType, cstNat);
    }
}

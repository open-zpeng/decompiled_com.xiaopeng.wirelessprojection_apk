package com.android.dx.rop.cst;
/* loaded from: classes.dex */
public final class CstInterfaceMethodRef extends CstBaseMethodRef {
    private CstMethodRef methodRef;

    @Override // com.android.dx.rop.cst.Constant
    public String typeName() {
        return "ifaceMethod";
    }

    public CstInterfaceMethodRef(CstType cstType, CstNat cstNat) {
        super(cstType, cstNat);
        this.methodRef = null;
    }

    public CstMethodRef toMethodRef() {
        if (this.methodRef == null) {
            this.methodRef = new CstMethodRef(getDefiningClass(), getNat());
        }
        return this.methodRef;
    }
}

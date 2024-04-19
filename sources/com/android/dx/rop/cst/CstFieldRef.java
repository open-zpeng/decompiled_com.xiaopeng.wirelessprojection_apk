package com.android.dx.rop.cst;

import com.android.dx.rop.type.Type;
/* loaded from: classes.dex */
public final class CstFieldRef extends CstMemberRef {
    @Override // com.android.dx.rop.cst.Constant
    public String typeName() {
        return "field";
    }

    public static CstFieldRef forPrimitiveType(Type type) {
        return new CstFieldRef(CstType.forBoxedPrimitiveType(type), CstNat.PRIMITIVE_TYPE_NAT);
    }

    public CstFieldRef(CstType cstType, CstNat cstNat) {
        super(cstType, cstNat);
    }

    @Override // com.android.dx.rop.type.TypeBearer
    public Type getType() {
        return getNat().getFieldType();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.dx.rop.cst.CstMemberRef, com.android.dx.rop.cst.Constant
    public int compareTo0(Constant constant) {
        int compareTo0 = super.compareTo0(constant);
        return compareTo0 != 0 ? compareTo0 : getNat().getDescriptor().compareTo((Constant) ((CstFieldRef) constant).getNat().getDescriptor());
    }
}

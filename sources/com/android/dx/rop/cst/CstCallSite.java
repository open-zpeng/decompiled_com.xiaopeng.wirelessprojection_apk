package com.android.dx.rop.cst;

import com.android.dx.cf.code.BootstrapMethodArgumentsList;
import com.android.dx.rop.cst.CstArray;
import com.android.dx.rop.type.Prototype;
import java.util.Objects;
/* loaded from: classes.dex */
public final class CstCallSite extends CstArray {
    @Override // com.android.dx.rop.cst.CstArray, com.android.dx.rop.cst.Constant
    public boolean isCategory2() {
        return false;
    }

    @Override // com.android.dx.rop.cst.CstArray, com.android.dx.rop.cst.Constant
    public String typeName() {
        return "call site";
    }

    public static CstCallSite make(CstMethodHandle cstMethodHandle, CstNat cstNat, BootstrapMethodArgumentsList bootstrapMethodArgumentsList) {
        Objects.requireNonNull(cstMethodHandle, "bootstrapMethodHandle == null");
        Objects.requireNonNull(cstNat, "nat == null");
        CstArray.List list = new CstArray.List(bootstrapMethodArgumentsList.size() + 3);
        list.set(0, cstMethodHandle);
        list.set(1, cstNat.getName());
        list.set(2, new CstProtoRef(Prototype.fromDescriptor(cstNat.getDescriptor().getString())));
        if (bootstrapMethodArgumentsList != null) {
            for (int i = 0; i < bootstrapMethodArgumentsList.size(); i++) {
                list.set(i + 3, bootstrapMethodArgumentsList.get(i));
            }
        }
        list.setImmutable();
        return new CstCallSite(list);
    }

    private CstCallSite(CstArray.List list) {
        super(list);
    }

    @Override // com.android.dx.rop.cst.CstArray
    public boolean equals(Object obj) {
        if (obj instanceof CstCallSite) {
            return getList().equals(((CstCallSite) obj).getList());
        }
        return false;
    }

    @Override // com.android.dx.rop.cst.CstArray
    public int hashCode() {
        return getList().hashCode();
    }

    @Override // com.android.dx.rop.cst.CstArray, com.android.dx.rop.cst.Constant
    protected int compareTo0(Constant constant) {
        return getList().compareTo(((CstCallSite) constant).getList());
    }

    @Override // com.android.dx.rop.cst.CstArray
    public String toString() {
        return getList().toString("call site{", ", ", "}");
    }

    @Override // com.android.dx.rop.cst.CstArray, com.android.dx.util.ToHuman
    public String toHuman() {
        return getList().toHuman("{", ", ", "}");
    }
}

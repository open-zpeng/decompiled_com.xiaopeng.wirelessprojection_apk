package com.android.dx.rop.cst;

import com.android.dx.rop.type.Type;
import java.util.Objects;
/* loaded from: classes.dex */
public final class CstNat extends Constant {
    public static final CstNat PRIMITIVE_TYPE_NAT = new CstNat(new CstString("TYPE"), new CstString("Ljava/lang/Class;"));
    private final CstString descriptor;
    private final CstString name;

    @Override // com.android.dx.rop.cst.Constant
    public boolean isCategory2() {
        return false;
    }

    @Override // com.android.dx.rop.cst.Constant
    public String typeName() {
        return "nat";
    }

    public CstNat(CstString cstString, CstString cstString2) {
        Objects.requireNonNull(cstString, "name == null");
        Objects.requireNonNull(cstString2, "descriptor == null");
        this.name = cstString;
        this.descriptor = cstString2;
    }

    public boolean equals(Object obj) {
        if (obj instanceof CstNat) {
            CstNat cstNat = (CstNat) obj;
            return this.name.equals(cstNat.name) && this.descriptor.equals(cstNat.descriptor);
        }
        return false;
    }

    public int hashCode() {
        return (this.name.hashCode() * 31) ^ this.descriptor.hashCode();
    }

    @Override // com.android.dx.rop.cst.Constant
    protected int compareTo0(Constant constant) {
        CstNat cstNat = (CstNat) constant;
        int compareTo = this.name.compareTo((Constant) cstNat.name);
        return compareTo != 0 ? compareTo : this.descriptor.compareTo((Constant) cstNat.descriptor);
    }

    public String toString() {
        return "nat{" + toHuman() + '}';
    }

    public CstString getName() {
        return this.name;
    }

    public CstString getDescriptor() {
        return this.descriptor;
    }

    @Override // com.android.dx.util.ToHuman
    public String toHuman() {
        return this.name.toHuman() + ':' + this.descriptor.toHuman();
    }

    public Type getFieldType() {
        return Type.intern(this.descriptor.getString());
    }

    public final boolean isInstanceInit() {
        return this.name.getString().equals("<init>");
    }

    public final boolean isClassInit() {
        return this.name.getString().equals("<clinit>");
    }
}

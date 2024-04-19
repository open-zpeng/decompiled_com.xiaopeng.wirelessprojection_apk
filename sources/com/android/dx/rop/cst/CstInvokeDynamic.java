package com.android.dx.rop.cst;

import com.android.dx.rop.type.Prototype;
import com.android.dx.rop.type.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/* loaded from: classes.dex */
public final class CstInvokeDynamic extends Constant {
    private final int bootstrapMethodIndex;
    private CstCallSite callSite;
    private CstType declaringClass;
    private final CstNat nat;
    private final Prototype prototype;
    private final List<CstCallSiteRef> references = new ArrayList();

    @Override // com.android.dx.rop.cst.Constant
    public boolean isCategory2() {
        return false;
    }

    @Override // com.android.dx.rop.cst.Constant
    public String typeName() {
        return "InvokeDynamic";
    }

    public static CstInvokeDynamic make(int i, CstNat cstNat) {
        return new CstInvokeDynamic(i, cstNat);
    }

    private CstInvokeDynamic(int i, CstNat cstNat) {
        this.bootstrapMethodIndex = i;
        this.nat = cstNat;
        this.prototype = Prototype.fromDescriptor(cstNat.getDescriptor().toHuman());
    }

    public CstCallSiteRef addReference() {
        CstCallSiteRef cstCallSiteRef = new CstCallSiteRef(this, this.references.size());
        this.references.add(cstCallSiteRef);
        return cstCallSiteRef;
    }

    public List<CstCallSiteRef> getReferences() {
        return this.references;
    }

    public String toString() {
        return toHuman();
    }

    @Override // com.android.dx.util.ToHuman
    public String toHuman() {
        CstType cstType = this.declaringClass;
        return "InvokeDynamic(" + (cstType != null ? cstType.toHuman() : "Unknown") + ":" + this.bootstrapMethodIndex + ", " + this.nat.toHuman() + ")";
    }

    @Override // com.android.dx.rop.cst.Constant
    protected int compareTo0(Constant constant) {
        CstInvokeDynamic cstInvokeDynamic = (CstInvokeDynamic) constant;
        int compare = Integer.compare(this.bootstrapMethodIndex, cstInvokeDynamic.getBootstrapMethodIndex());
        if (compare != 0) {
            return compare;
        }
        int compareTo = this.nat.compareTo((Constant) cstInvokeDynamic.getNat());
        if (compareTo != 0) {
            return compareTo;
        }
        int compareTo2 = this.declaringClass.compareTo((Constant) cstInvokeDynamic.getDeclaringClass());
        return compareTo2 != 0 ? compareTo2 : this.callSite.compareTo((Constant) cstInvokeDynamic.getCallSite());
    }

    public int getBootstrapMethodIndex() {
        return this.bootstrapMethodIndex;
    }

    public CstNat getNat() {
        return this.nat;
    }

    public Prototype getPrototype() {
        return this.prototype;
    }

    public Type getReturnType() {
        return this.prototype.getReturnType();
    }

    public void setDeclaringClass(CstType cstType) {
        if (this.declaringClass != null) {
            throw new IllegalArgumentException("already added declaring class");
        }
        Objects.requireNonNull(cstType, "declaringClass == null");
        this.declaringClass = cstType;
    }

    public CstType getDeclaringClass() {
        return this.declaringClass;
    }

    public void setCallSite(CstCallSite cstCallSite) {
        if (this.callSite != null) {
            throw new IllegalArgumentException("already added call site");
        }
        Objects.requireNonNull(cstCallSite, "callSite == null");
        this.callSite = cstCallSite;
    }

    public CstCallSite getCallSite() {
        return this.callSite;
    }
}

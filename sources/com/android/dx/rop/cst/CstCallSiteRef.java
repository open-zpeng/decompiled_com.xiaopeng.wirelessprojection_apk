package com.android.dx.rop.cst;

import com.android.dx.rop.type.Prototype;
import com.android.dx.rop.type.Type;
import java.util.Objects;
/* loaded from: classes.dex */
public class CstCallSiteRef extends Constant {
    private final int id;
    private final CstInvokeDynamic invokeDynamic;

    @Override // com.android.dx.rop.cst.Constant
    public boolean isCategory2() {
        return false;
    }

    @Override // com.android.dx.rop.cst.Constant
    public String typeName() {
        return "CallSiteRef";
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CstCallSiteRef(CstInvokeDynamic cstInvokeDynamic, int i) {
        Objects.requireNonNull(cstInvokeDynamic, "invokeDynamic == null");
        this.invokeDynamic = cstInvokeDynamic;
        this.id = i;
    }

    @Override // com.android.dx.rop.cst.Constant
    protected int compareTo0(Constant constant) {
        CstCallSiteRef cstCallSiteRef = (CstCallSiteRef) constant;
        int compareTo = this.invokeDynamic.compareTo((Constant) cstCallSiteRef.invokeDynamic);
        return compareTo != 0 ? compareTo : Integer.compare(this.id, cstCallSiteRef.id);
    }

    @Override // com.android.dx.util.ToHuman
    public String toHuman() {
        return getCallSite().toHuman();
    }

    public String toString() {
        return getCallSite().toString();
    }

    public Prototype getPrototype() {
        return this.invokeDynamic.getPrototype();
    }

    public Type getReturnType() {
        return this.invokeDynamic.getReturnType();
    }

    public CstCallSite getCallSite() {
        return this.invokeDynamic.getCallSite();
    }
}

package com.android.dx;

import com.android.dx.rop.cst.CstMethodRef;
import com.android.dx.rop.cst.CstNat;
import com.android.dx.rop.cst.CstString;
import com.android.dx.rop.type.Prototype;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.IInputController;
import java.util.List;
/* loaded from: classes.dex */
public final class MethodId<D, R> {
    final CstMethodRef constant;
    final TypeId<D> declaringType;
    final String name;
    final CstNat nat;
    final TypeList parameters;
    final TypeId<R> returnType;

    /* JADX INFO: Access modifiers changed from: package-private */
    public MethodId(TypeId<D> typeId, TypeId<R> typeId2, String str, TypeList typeList) {
        if (typeId == null || typeId2 == null || str == null || typeList == null) {
            throw null;
        }
        this.declaringType = typeId;
        this.returnType = typeId2;
        this.name = str;
        this.parameters = typeList;
        CstNat cstNat = new CstNat(new CstString(str), new CstString(descriptor(false)));
        this.nat = cstNat;
        this.constant = new CstMethodRef(typeId.constant, cstNat);
    }

    public TypeId<D> getDeclaringType() {
        return this.declaringType;
    }

    public TypeId<R> getReturnType() {
        return this.returnType;
    }

    public boolean isConstructor() {
        return this.name.equals("<init>");
    }

    public boolean isStaticInitializer() {
        return this.name.equals("<clinit>");
    }

    public String getName() {
        return this.name;
    }

    public List<TypeId<?>> getParameters() {
        return this.parameters.asList();
    }

    String descriptor(boolean z) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        if (z) {
            sb.append(this.declaringType.name);
        }
        for (TypeId<?> typeId : this.parameters.types) {
            sb.append(typeId.name);
        }
        sb.append(")");
        sb.append(this.returnType.name);
        return sb.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Prototype prototype(boolean z) {
        return Prototype.intern(descriptor(z));
    }

    public boolean equals(Object obj) {
        if (obj instanceof MethodId) {
            MethodId methodId = (MethodId) obj;
            if (methodId.declaringType.equals(this.declaringType) && methodId.name.equals(this.name) && methodId.parameters.equals(this.parameters) && methodId.returnType.equals(this.returnType)) {
                return true;
            }
        }
        return false;
    }

    public int hashCode() {
        return ((((((IInputController.KEYCODE_KNOB_TALKING_BOOK + this.declaringType.hashCode()) * 31) + this.name.hashCode()) * 31) + this.parameters.hashCode()) * 31) + this.returnType.hashCode();
    }

    public String toString() {
        return this.declaringType + "." + this.name + "(" + this.parameters + ")";
    }
}

package com.android.dx;

import com.android.dx.rop.cst.CstFieldRef;
import com.android.dx.rop.cst.CstNat;
import com.android.dx.rop.cst.CstString;
/* loaded from: classes.dex */
public final class FieldId<D, V> {
    final CstFieldRef constant;
    final TypeId<D> declaringType;
    final String name;
    final CstNat nat;
    final TypeId<V> type;

    /* JADX INFO: Access modifiers changed from: package-private */
    public FieldId(TypeId<D> typeId, TypeId<V> typeId2, String str) {
        if (typeId == null || typeId2 == null || str == null) {
            throw null;
        }
        this.declaringType = typeId;
        this.type = typeId2;
        this.name = str;
        CstNat cstNat = new CstNat(new CstString(str), new CstString(typeId2.name));
        this.nat = cstNat;
        this.constant = new CstFieldRef(typeId.constant, cstNat);
    }

    public TypeId<D> getDeclaringType() {
        return this.declaringType;
    }

    public TypeId<V> getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public boolean equals(Object obj) {
        if (obj instanceof FieldId) {
            FieldId fieldId = (FieldId) obj;
            if (fieldId.declaringType.equals(this.declaringType) && fieldId.name.equals(this.name)) {
                return true;
            }
        }
        return false;
    }

    public int hashCode() {
        return this.declaringType.hashCode() + (this.name.hashCode() * 37);
    }

    public String toString() {
        return this.declaringType + "." + this.name;
    }
}

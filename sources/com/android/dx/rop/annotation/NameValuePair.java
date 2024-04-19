package com.android.dx.rop.annotation;

import com.android.dx.rop.cst.Constant;
import com.android.dx.rop.cst.CstString;
import java.util.Objects;
/* loaded from: classes.dex */
public final class NameValuePair implements Comparable<NameValuePair> {
    private final CstString name;
    private final Constant value;

    public NameValuePair(CstString cstString, Constant constant) {
        Objects.requireNonNull(cstString, "name == null");
        Objects.requireNonNull(constant, "value == null");
        this.name = cstString;
        this.value = constant;
    }

    public String toString() {
        return this.name.toHuman() + ":" + this.value;
    }

    public int hashCode() {
        return (this.name.hashCode() * 31) + this.value.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof NameValuePair) {
            NameValuePair nameValuePair = (NameValuePair) obj;
            return this.name.equals(nameValuePair.name) && this.value.equals(nameValuePair.value);
        }
        return false;
    }

    @Override // java.lang.Comparable
    public int compareTo(NameValuePair nameValuePair) {
        int compareTo = this.name.compareTo((Constant) nameValuePair.name);
        return compareTo != 0 ? compareTo : this.value.compareTo(nameValuePair.value);
    }

    public CstString getName() {
        return this.name;
    }

    public Constant getValue() {
        return this.value;
    }
}

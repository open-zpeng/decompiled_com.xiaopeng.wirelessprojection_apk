package com.android.dx.rop.cst;
/* loaded from: classes.dex */
public abstract class CstLiteralBits extends TypedConstant {
    public abstract boolean fitsInInt();

    public abstract int getIntBits();

    public abstract long getLongBits();

    public boolean fitsIn16Bits() {
        if (fitsInInt()) {
            int intBits = getIntBits();
            return ((short) intBits) == intBits;
        }
        return false;
    }

    public boolean fitsIn8Bits() {
        if (fitsInInt()) {
            int intBits = getIntBits();
            return ((byte) intBits) == intBits;
        }
        return false;
    }
}

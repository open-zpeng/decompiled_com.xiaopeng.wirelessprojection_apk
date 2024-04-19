package com.android.dx.rop.cst;

import com.android.dx.rop.type.Type;
import com.android.dx.util.Hex;
/* loaded from: classes.dex */
public final class CstLong extends CstLiteral64 {
    public static final CstLong VALUE_0 = make(0);
    public static final CstLong VALUE_1 = make(1);

    @Override // com.android.dx.rop.cst.Constant
    public String typeName() {
        return "long";
    }

    public static CstLong make(long j) {
        return new CstLong(j);
    }

    private CstLong(long j) {
        super(j);
    }

    public String toString() {
        long longBits = getLongBits();
        return "long{0x" + Hex.u8(longBits) + " / " + longBits + '}';
    }

    @Override // com.android.dx.rop.type.TypeBearer
    public Type getType() {
        return Type.LONG;
    }

    @Override // com.android.dx.util.ToHuman
    public String toHuman() {
        return Long.toString(getLongBits());
    }

    public long getValue() {
        return getLongBits();
    }
}

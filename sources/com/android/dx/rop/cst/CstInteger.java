package com.android.dx.rop.cst;

import com.android.dx.rop.type.Type;
import com.android.dx.util.Hex;
/* loaded from: classes.dex */
public final class CstInteger extends CstLiteral32 {
    private static final CstInteger[] cache = new CstInteger[511];
    public static final CstInteger VALUE_M1 = make(-1);
    public static final CstInteger VALUE_0 = make(0);
    public static final CstInteger VALUE_1 = make(1);
    public static final CstInteger VALUE_2 = make(2);
    public static final CstInteger VALUE_3 = make(3);
    public static final CstInteger VALUE_4 = make(4);
    public static final CstInteger VALUE_5 = make(5);

    @Override // com.android.dx.rop.cst.Constant
    public String typeName() {
        return "int";
    }

    public static CstInteger make(int i) {
        CstInteger[] cstIntegerArr = cache;
        int length = (Integer.MAX_VALUE & i) % cstIntegerArr.length;
        CstInteger cstInteger = cstIntegerArr[length];
        if (cstInteger == null || cstInteger.getValue() != i) {
            CstInteger cstInteger2 = new CstInteger(i);
            cstIntegerArr[length] = cstInteger2;
            return cstInteger2;
        }
        return cstInteger;
    }

    private CstInteger(int i) {
        super(i);
    }

    public String toString() {
        int intBits = getIntBits();
        return "int{0x" + Hex.u4(intBits) + " / " + intBits + '}';
    }

    @Override // com.android.dx.rop.type.TypeBearer
    public Type getType() {
        return Type.INT;
    }

    @Override // com.android.dx.util.ToHuman
    public String toHuman() {
        return Integer.toString(getIntBits());
    }

    public int getValue() {
        return getIntBits();
    }
}

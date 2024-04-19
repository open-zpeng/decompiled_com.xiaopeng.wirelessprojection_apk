package com.android.dx.rop.cst;

import androidx.constraintlayout.core.motion.utils.TypedValues;
import com.android.dx.rop.type.Type;
import com.android.dx.util.Hex;
/* loaded from: classes.dex */
public final class CstFloat extends CstLiteral32 {
    public static final CstFloat VALUE_0 = make(Float.floatToIntBits(0.0f));
    public static final CstFloat VALUE_1 = make(Float.floatToIntBits(1.0f));
    public static final CstFloat VALUE_2 = make(Float.floatToIntBits(2.0f));

    @Override // com.android.dx.rop.cst.Constant
    public String typeName() {
        return TypedValues.Custom.S_FLOAT;
    }

    public static CstFloat make(int i) {
        return new CstFloat(i);
    }

    private CstFloat(int i) {
        super(i);
    }

    public String toString() {
        int intBits = getIntBits();
        return "float{0x" + Hex.u4(intBits) + " / " + Float.intBitsToFloat(intBits) + '}';
    }

    @Override // com.android.dx.rop.type.TypeBearer
    public Type getType() {
        return Type.FLOAT;
    }

    @Override // com.android.dx.util.ToHuman
    public String toHuman() {
        return Float.toString(Float.intBitsToFloat(getIntBits()));
    }

    public float getValue() {
        return Float.intBitsToFloat(getIntBits());
    }
}

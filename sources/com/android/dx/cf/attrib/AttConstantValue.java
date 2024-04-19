package com.android.dx.cf.attrib;

import com.android.dx.rop.cst.CstDouble;
import com.android.dx.rop.cst.CstFloat;
import com.android.dx.rop.cst.CstInteger;
import com.android.dx.rop.cst.CstLong;
import com.android.dx.rop.cst.CstString;
import com.android.dx.rop.cst.TypedConstant;
import java.util.Objects;
/* loaded from: classes.dex */
public final class AttConstantValue extends BaseAttribute {
    public static final String ATTRIBUTE_NAME = "ConstantValue";
    private final TypedConstant constantValue;

    @Override // com.android.dx.cf.iface.Attribute
    public int byteLength() {
        return 8;
    }

    public AttConstantValue(TypedConstant typedConstant) {
        super(ATTRIBUTE_NAME);
        if (!(typedConstant instanceof CstString) && !(typedConstant instanceof CstInteger) && !(typedConstant instanceof CstLong) && !(typedConstant instanceof CstFloat) && !(typedConstant instanceof CstDouble)) {
            Objects.requireNonNull(typedConstant, "constantValue == null");
            throw new IllegalArgumentException("bad type for constantValue");
        } else {
            this.constantValue = typedConstant;
        }
    }

    public TypedConstant getConstantValue() {
        return this.constantValue;
    }
}

package com.android.dx.dex.code;

import com.android.dx.rop.code.RegisterSpecList;
import com.android.dx.rop.code.SourcePosition;
import com.android.dx.util.AnnotatedOutput;
import com.xiaopeng.speech.vui.constants.VuiConstants;
/* loaded from: classes.dex */
public abstract class ZeroSizeInsn extends DalvInsn {
    @Override // com.android.dx.dex.code.DalvInsn
    public final int codeSize() {
        return 0;
    }

    @Override // com.android.dx.dex.code.DalvInsn
    public final void writeTo(AnnotatedOutput annotatedOutput) {
    }

    public ZeroSizeInsn(SourcePosition sourcePosition) {
        super(Dops.SPECIAL_FORMAT, sourcePosition, RegisterSpecList.EMPTY);
    }

    @Override // com.android.dx.dex.code.DalvInsn
    public final DalvInsn withOpcode(Dop dop) {
        throw new RuntimeException(VuiConstants.PROPS_UNSUPPORTED);
    }

    @Override // com.android.dx.dex.code.DalvInsn
    public DalvInsn withRegisterOffset(int i) {
        return withRegisters(getRegisters().withOffset(i));
    }
}

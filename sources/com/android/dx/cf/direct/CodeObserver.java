package com.android.dx.cf.direct;

import com.android.dx.cf.code.ByteOps;
import com.android.dx.cf.code.BytecodeArray;
import com.android.dx.cf.code.SwitchList;
import com.android.dx.cf.iface.ParseObserver;
import com.android.dx.rop.cst.Constant;
import com.android.dx.rop.cst.CstDouble;
import com.android.dx.rop.cst.CstFloat;
import com.android.dx.rop.cst.CstInteger;
import com.android.dx.rop.cst.CstKnownNull;
import com.android.dx.rop.cst.CstLong;
import com.android.dx.rop.cst.CstType;
import com.android.dx.rop.type.Type;
import com.android.dx.util.ByteArray;
import com.android.dx.util.Hex;
import com.xiaopeng.wirelessprojection.dmr.RendererActivity;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes.dex */
public class CodeObserver implements BytecodeArray.Visitor {
    private final ByteArray bytes;
    private final ParseObserver observer;

    @Override // com.android.dx.cf.code.BytecodeArray.Visitor
    public int getPreviousOffset() {
        return -1;
    }

    @Override // com.android.dx.cf.code.BytecodeArray.Visitor
    public void setPreviousOffset(int i) {
    }

    public CodeObserver(ByteArray byteArray, ParseObserver parseObserver) {
        Objects.requireNonNull(byteArray, "bytes == null");
        Objects.requireNonNull(parseObserver, "observer == null");
        this.bytes = byteArray;
        this.observer = parseObserver;
    }

    @Override // com.android.dx.cf.code.BytecodeArray.Visitor
    public void visitInvalid(int i, int i2, int i3) {
        this.observer.parsed(this.bytes, i2, i3, header(i2));
    }

    @Override // com.android.dx.cf.code.BytecodeArray.Visitor
    public void visitNoArgs(int i, int i2, int i3, Type type) {
        this.observer.parsed(this.bytes, i2, i3, header(i2));
    }

    @Override // com.android.dx.cf.code.BytecodeArray.Visitor
    public void visitLocal(int i, int i2, int i3, int i4, Type type, int i5) {
        String str;
        String u1 = i3 <= 3 ? Hex.u1(i4) : Hex.u2(i4);
        boolean z = i3 == 1;
        String str2 = "";
        if (i == 132) {
            str = ", #" + (i3 <= 3 ? Hex.s1(i5) : Hex.s2(i5));
        } else {
            str = "";
        }
        if (type.isCategory2()) {
            str2 = (z ? "," : " //") + " category-2";
        }
        this.observer.parsed(this.bytes, i2, i3, header(i2) + (z ? " // " : RendererActivity.DEFAULT_TITLE) + u1 + str + str2);
    }

    @Override // com.android.dx.cf.code.BytecodeArray.Visitor
    public void visitConstant(int i, int i2, int i3, Constant constant, int i4) {
        String str;
        if (constant instanceof CstKnownNull) {
            visitNoArgs(i, i2, i3, null);
        } else if (constant instanceof CstInteger) {
            visitLiteralInt(i, i2, i3, i4);
        } else if (constant instanceof CstLong) {
            visitLiteralLong(i, i2, i3, ((CstLong) constant).getValue());
        } else if (constant instanceof CstFloat) {
            visitLiteralFloat(i, i2, i3, ((CstFloat) constant).getIntBits());
        } else if (constant instanceof CstDouble) {
            visitLiteralDouble(i, i2, i3, ((CstDouble) constant).getLongBits());
        } else {
            if (i4 == 0) {
                str = "";
            } else if (i == 197) {
                str = ", " + Hex.u1(i4);
            } else {
                str = ", " + Hex.u2(i4);
            }
            this.observer.parsed(this.bytes, i2, i3, header(i2) + RendererActivity.DEFAULT_TITLE + constant + str);
        }
    }

    @Override // com.android.dx.cf.code.BytecodeArray.Visitor
    public void visitBranch(int i, int i2, int i3, int i4) {
        this.observer.parsed(this.bytes, i2, i3, header(i2) + RendererActivity.DEFAULT_TITLE + (i3 <= 3 ? Hex.u2(i4) : Hex.u4(i4)));
    }

    @Override // com.android.dx.cf.code.BytecodeArray.Visitor
    public void visitSwitch(int i, int i2, int i3, SwitchList switchList, int i4) {
        int size = switchList.size();
        StringBuilder sb = new StringBuilder((size * 20) + 100);
        sb.append(header(i2));
        if (i4 != 0) {
            sb.append(" // padding: " + Hex.u4(i4));
        }
        sb.append('\n');
        for (int i5 = 0; i5 < size; i5++) {
            sb.append("  ");
            sb.append(Hex.s4(switchList.getValue(i5)));
            sb.append(": ");
            sb.append(Hex.u2(switchList.getTarget(i5)));
            sb.append('\n');
        }
        sb.append("  default: ");
        sb.append(Hex.u2(switchList.getDefaultTarget()));
        this.observer.parsed(this.bytes, i2, i3, sb.toString());
    }

    @Override // com.android.dx.cf.code.BytecodeArray.Visitor
    public void visitNewarray(int i, int i2, CstType cstType, ArrayList<Constant> arrayList) {
        this.observer.parsed(this.bytes, i, i2, header(i) + (i2 == 1 ? " // " : RendererActivity.DEFAULT_TITLE) + cstType.getClassType().getComponentType().toHuman());
    }

    private String header(int i) {
        int unsignedByte = this.bytes.getUnsignedByte(i);
        String opName = ByteOps.opName(unsignedByte);
        if (unsignedByte == 196) {
            opName = opName + RendererActivity.DEFAULT_TITLE + ByteOps.opName(this.bytes.getUnsignedByte(i + 1));
        }
        return Hex.u2(i) + ": " + opName;
    }

    private void visitLiteralInt(int i, int i2, int i3, int i4) {
        String str;
        String str2 = i3 == 1 ? " // " : RendererActivity.DEFAULT_TITLE;
        int unsignedByte = this.bytes.getUnsignedByte(i2);
        if (i3 == 1 || unsignedByte == 16) {
            str = "#" + Hex.s1(i4);
        } else if (unsignedByte == 17) {
            str = "#" + Hex.s2(i4);
        } else {
            str = "#" + Hex.s4(i4);
        }
        this.observer.parsed(this.bytes, i2, i3, header(i2) + str2 + str);
    }

    private void visitLiteralLong(int i, int i2, int i3, long j) {
        String s8;
        String str = i3 == 1 ? " // " : " #";
        if (i3 == 1) {
            s8 = Hex.s1((int) j);
        } else {
            s8 = Hex.s8(j);
        }
        this.observer.parsed(this.bytes, i2, i3, header(i2) + str + s8);
    }

    private void visitLiteralFloat(int i, int i2, int i3, int i4) {
        this.observer.parsed(this.bytes, i2, i3, header(i2) + (i3 != 1 ? " #" + Hex.u4(i4) : "") + " // " + Float.intBitsToFloat(i4));
    }

    private void visitLiteralDouble(int i, int i2, int i3, long j) {
        this.observer.parsed(this.bytes, i2, i3, header(i2) + (i3 != 1 ? " #" + Hex.u8(j) : "") + " // " + Double.longBitsToDouble(j));
    }
}

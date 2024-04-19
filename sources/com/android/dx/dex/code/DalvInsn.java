package com.android.dx.dex.code;

import com.android.dx.rop.code.RegisterSpec;
import com.android.dx.rop.code.RegisterSpecList;
import com.android.dx.rop.code.SourcePosition;
import com.android.dx.ssa.RegisterMapper;
import com.android.dx.util.AnnotatedOutput;
import com.android.dx.util.Hex;
import com.android.dx.util.TwoColumnOutput;
import com.xiaopeng.wirelessprojection.dmr.RendererActivity;
import java.util.BitSet;
import java.util.Objects;
/* loaded from: classes.dex */
public abstract class DalvInsn {
    private int address;
    private final Dop opcode;
    private final SourcePosition position;
    private final RegisterSpecList registers;

    protected abstract String argString();

    public abstract int codeSize();

    protected abstract String listingString0(boolean z);

    public abstract DalvInsn withOpcode(Dop dop);

    public abstract DalvInsn withRegisterOffset(int i);

    public abstract DalvInsn withRegisters(RegisterSpecList registerSpecList);

    public abstract void writeTo(AnnotatedOutput annotatedOutput);

    public static SimpleInsn makeMove(SourcePosition sourcePosition, RegisterSpec registerSpec, RegisterSpec registerSpec2) {
        Dop dop;
        boolean z = registerSpec.getCategory() == 1;
        boolean isReference = registerSpec.getType().isReference();
        int reg = registerSpec.getReg();
        if ((registerSpec2.getReg() | reg) < 16) {
            if (isReference) {
                dop = Dops.MOVE_OBJECT;
            } else {
                dop = z ? Dops.MOVE : Dops.MOVE_WIDE;
            }
        } else if (reg < 256) {
            if (isReference) {
                dop = Dops.MOVE_OBJECT_FROM16;
            } else {
                dop = z ? Dops.MOVE_FROM16 : Dops.MOVE_WIDE_FROM16;
            }
        } else if (isReference) {
            dop = Dops.MOVE_OBJECT_16;
        } else {
            dop = z ? Dops.MOVE_16 : Dops.MOVE_WIDE_16;
        }
        return new SimpleInsn(dop, sourcePosition, RegisterSpecList.make(registerSpec, registerSpec2));
    }

    public DalvInsn(Dop dop, SourcePosition sourcePosition, RegisterSpecList registerSpecList) {
        Objects.requireNonNull(dop, "opcode == null");
        Objects.requireNonNull(sourcePosition, "position == null");
        Objects.requireNonNull(registerSpecList, "registers == null");
        this.address = -1;
        this.opcode = dop;
        this.position = sourcePosition;
        this.registers = registerSpecList;
    }

    public final String toString() {
        boolean z;
        StringBuilder sb = new StringBuilder(100);
        sb.append(identifierString());
        sb.append(' ');
        sb.append(this.position);
        sb.append(": ");
        sb.append(this.opcode.getName());
        if (this.registers.size() != 0) {
            sb.append(this.registers.toHuman(RendererActivity.DEFAULT_TITLE, ", ", null));
            z = true;
        } else {
            z = false;
        }
        String argString = argString();
        if (argString != null) {
            if (z) {
                sb.append(',');
            }
            sb.append(' ');
            sb.append(argString);
        }
        return sb.toString();
    }

    public final boolean hasAddress() {
        return this.address >= 0;
    }

    public final int getAddress() {
        int i = this.address;
        if (i >= 0) {
            return i;
        }
        throw new RuntimeException("address not yet known");
    }

    public final Dop getOpcode() {
        return this.opcode;
    }

    public final SourcePosition getPosition() {
        return this.position;
    }

    public final RegisterSpecList getRegisters() {
        return this.registers;
    }

    public final boolean hasResult() {
        return this.opcode.hasResult();
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v0, types: [boolean] */
    /* JADX WARN: Type inference failed for: r0v1, types: [int] */
    /* JADX WARN: Type inference failed for: r0v2, types: [int] */
    /* JADX WARN: Type inference failed for: r4v1, types: [com.android.dx.rop.code.RegisterSpecList] */
    /* JADX WARN: Type inference failed for: r6v0, types: [java.util.BitSet] */
    public final int getMinimumRegisterRequirement(BitSet bitSet) {
        ?? hasResult = hasResult();
        int size = this.registers.size();
        int i = 0;
        int category = (hasResult == 0 || bitSet.get(0)) ? 0 : this.registers.get(0).getCategory();
        while (hasResult < size) {
            if (!bitSet.get(hasResult)) {
                i += this.registers.get(hasResult).getCategory();
            }
            hasResult++;
        }
        return Math.max(i, category);
    }

    public DalvInsn getLowRegVersion() {
        return withRegisters(this.registers.withExpandedRegisters(0, hasResult(), null));
    }

    public DalvInsn expandedPrefix(BitSet bitSet) {
        RegisterSpecList registerSpecList = this.registers;
        boolean z = bitSet.get(0);
        if (hasResult()) {
            bitSet.set(0);
        }
        RegisterSpecList subset = registerSpecList.subset(bitSet);
        if (hasResult()) {
            bitSet.set(0, z);
        }
        if (subset.size() == 0) {
            return null;
        }
        return new HighRegisterPrefix(this.position, subset);
    }

    public DalvInsn expandedSuffix(BitSet bitSet) {
        if (!hasResult() || bitSet.get(0)) {
            return null;
        }
        RegisterSpec registerSpec = this.registers.get(0);
        return makeMove(this.position, registerSpec, registerSpec.withReg(0));
    }

    public DalvInsn expandedVersion(BitSet bitSet) {
        return withRegisters(this.registers.withExpandedRegisters(0, hasResult(), bitSet));
    }

    public final String identifierString() {
        int i = this.address;
        return i != -1 ? String.format("%04x", Integer.valueOf(i)) : Hex.u4(System.identityHashCode(this));
    }

    public final String listingString(String str, int i, boolean z) {
        String listingString0 = listingString0(z);
        if (listingString0 == null) {
            return null;
        }
        String str2 = str + identifierString() + ": ";
        int length = str2.length();
        return TwoColumnOutput.toString(str2, length, "", listingString0, i == 0 ? listingString0.length() : i - length);
    }

    public final void setAddress(int i) {
        if (i < 0) {
            throw new IllegalArgumentException("address < 0");
        }
        this.address = i;
    }

    public final int getNextAddress() {
        return getAddress() + codeSize();
    }

    public DalvInsn withMapper(RegisterMapper registerMapper) {
        return withRegisters(registerMapper.map(getRegisters()));
    }

    public String cstString() {
        throw new UnsupportedOperationException("Not supported.");
    }

    public String cstComment() {
        throw new UnsupportedOperationException("Not supported.");
    }
}

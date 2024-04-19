package com.android.dx.ssa;

import com.android.dx.rop.code.Insn;
import com.android.dx.rop.code.LocalItem;
import com.android.dx.rop.code.RegisterSpec;
import com.android.dx.rop.code.RegisterSpecList;
import com.android.dx.rop.code.Rop;
import com.android.dx.rop.code.SourcePosition;
import com.android.dx.rop.type.Type;
import com.android.dx.rop.type.TypeBearer;
import com.android.dx.ssa.SsaInsn;
import com.android.dx.util.Hex;
import com.xiaopeng.wirelessprojection.dmr.RendererActivity;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes.dex */
public final class PhiInsn extends SsaInsn {
    private final ArrayList<Operand> operands;
    private final int ropResultReg;
    private RegisterSpecList sources;

    /* loaded from: classes.dex */
    public interface Visitor {
        void visitPhiInsn(PhiInsn phiInsn);
    }

    @Override // com.android.dx.ssa.SsaInsn
    public boolean canThrow() {
        return false;
    }

    @Override // com.android.dx.ssa.SsaInsn
    public Rop getOpcode() {
        return null;
    }

    @Override // com.android.dx.ssa.SsaInsn
    public Insn getOriginalRopInsn() {
        return null;
    }

    @Override // com.android.dx.ssa.SsaInsn
    public boolean isPhiOrMove() {
        return true;
    }

    public PhiInsn(RegisterSpec registerSpec, SsaBasicBlock ssaBasicBlock) {
        super(registerSpec, ssaBasicBlock);
        this.operands = new ArrayList<>();
        this.ropResultReg = registerSpec.getReg();
    }

    public PhiInsn(int i, SsaBasicBlock ssaBasicBlock) {
        super(RegisterSpec.make(i, Type.VOID), ssaBasicBlock);
        this.operands = new ArrayList<>();
        this.ropResultReg = i;
    }

    @Override // com.android.dx.ssa.SsaInsn
    /* renamed from: clone */
    public PhiInsn mo14clone() {
        throw new UnsupportedOperationException("can't clone phi");
    }

    public void updateSourcesToDefinitions(SsaMethod ssaMethod) {
        Iterator<Operand> it = this.operands.iterator();
        while (it.hasNext()) {
            Operand next = it.next();
            next.regSpec = next.regSpec.withType(ssaMethod.getDefinitionForRegister(next.regSpec.getReg()).getResult().getType());
        }
        this.sources = null;
    }

    public void changeResultType(TypeBearer typeBearer, LocalItem localItem) {
        setResult(RegisterSpec.makeLocalOptional(getResult().getReg(), typeBearer, localItem));
    }

    public int getRopResultReg() {
        return this.ropResultReg;
    }

    public void addPhiOperand(RegisterSpec registerSpec, SsaBasicBlock ssaBasicBlock) {
        this.operands.add(new Operand(registerSpec, ssaBasicBlock.getIndex(), ssaBasicBlock.getRopLabel()));
        this.sources = null;
    }

    public void removePhiRegister(RegisterSpec registerSpec) {
        ArrayList arrayList = new ArrayList();
        Iterator<Operand> it = this.operands.iterator();
        while (it.hasNext()) {
            Operand next = it.next();
            if (next.regSpec.getReg() == registerSpec.getReg()) {
                arrayList.add(next);
            }
        }
        this.operands.removeAll(arrayList);
        this.sources = null;
    }

    public int predBlockIndexForSourcesIndex(int i) {
        return this.operands.get(i).blockIndex;
    }

    @Override // com.android.dx.ssa.SsaInsn
    public RegisterSpecList getSources() {
        RegisterSpecList registerSpecList = this.sources;
        if (registerSpecList != null) {
            return registerSpecList;
        }
        if (this.operands.size() == 0) {
            return RegisterSpecList.EMPTY;
        }
        int size = this.operands.size();
        this.sources = new RegisterSpecList(size);
        for (int i = 0; i < size; i++) {
            this.sources.set(i, this.operands.get(i).regSpec);
        }
        this.sources.setImmutable();
        return this.sources;
    }

    @Override // com.android.dx.ssa.SsaInsn
    public boolean isRegASource(int i) {
        Iterator<Operand> it = this.operands.iterator();
        while (it.hasNext()) {
            if (it.next().regSpec.getReg() == i) {
                return true;
            }
        }
        return false;
    }

    public boolean areAllOperandsEqual() {
        if (this.operands.size() == 0) {
            return true;
        }
        int reg = this.operands.get(0).regSpec.getReg();
        Iterator<Operand> it = this.operands.iterator();
        while (it.hasNext()) {
            if (reg != it.next().regSpec.getReg()) {
                return false;
            }
        }
        return true;
    }

    @Override // com.android.dx.ssa.SsaInsn
    public final void mapSourceRegisters(RegisterMapper registerMapper) {
        Iterator<Operand> it = this.operands.iterator();
        while (it.hasNext()) {
            Operand next = it.next();
            RegisterSpec registerSpec = next.regSpec;
            next.regSpec = registerMapper.map(registerSpec);
            if (registerSpec != next.regSpec) {
                getBlock().getParent().onSourceChanged(this, registerSpec, next.regSpec);
            }
        }
        this.sources = null;
    }

    @Override // com.android.dx.ssa.SsaInsn
    public Insn toRopInsn() {
        throw new IllegalArgumentException("Cannot convert phi insns to rop form");
    }

    public List<SsaBasicBlock> predBlocksForReg(int i, SsaMethod ssaMethod) {
        ArrayList arrayList = new ArrayList();
        Iterator<Operand> it = this.operands.iterator();
        while (it.hasNext()) {
            Operand next = it.next();
            if (next.regSpec.getReg() == i) {
                arrayList.add(ssaMethod.getBlocks().get(next.blockIndex));
            }
        }
        return arrayList;
    }

    @Override // com.android.dx.ssa.SsaInsn
    public boolean hasSideEffect() {
        return Optimizer.getPreserveLocals() && getLocalAssignment() != null;
    }

    @Override // com.android.dx.ssa.SsaInsn
    public void accept(SsaInsn.Visitor visitor) {
        visitor.visitPhiInsn(this);
    }

    @Override // com.android.dx.util.ToHuman
    public String toHuman() {
        return toHumanWithInline(null);
    }

    protected final String toHumanWithInline(String str) {
        StringBuilder sb = new StringBuilder(80);
        sb.append(SourcePosition.NO_INFO);
        sb.append(": phi");
        if (str != null) {
            sb.append("(");
            sb.append(str);
            sb.append(")");
        }
        RegisterSpec result = getResult();
        if (result == null) {
            sb.append(" .");
        } else {
            sb.append(RendererActivity.DEFAULT_TITLE);
            sb.append(result.toHuman());
        }
        sb.append(" <-");
        int size = getSources().size();
        if (size == 0) {
            sb.append(" .");
        } else {
            for (int i = 0; i < size; i++) {
                sb.append(RendererActivity.DEFAULT_TITLE);
                sb.append(this.sources.get(i).toHuman() + "[b=" + Hex.u2(this.operands.get(i).ropLabel) + "]");
            }
        }
        return sb.toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class Operand {
        public final int blockIndex;
        public RegisterSpec regSpec;
        public final int ropLabel;

        public Operand(RegisterSpec registerSpec, int i, int i2) {
            this.regSpec = registerSpec;
            this.blockIndex = i;
            this.ropLabel = i2;
        }
    }
}

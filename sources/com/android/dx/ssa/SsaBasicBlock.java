package com.android.dx.ssa;

import com.android.dx.rop.code.BasicBlock;
import com.android.dx.rop.code.BasicBlockList;
import com.android.dx.rop.code.Insn;
import com.android.dx.rop.code.InsnList;
import com.android.dx.rop.code.PlainInsn;
import com.android.dx.rop.code.RegisterSpec;
import com.android.dx.rop.code.RegisterSpecList;
import com.android.dx.rop.code.RopMethod;
import com.android.dx.rop.code.Rops;
import com.android.dx.rop.code.SourcePosition;
import com.android.dx.ssa.PhiInsn;
import com.android.dx.ssa.SsaInsn;
import com.android.dx.util.Hex;
import com.android.dx.util.IntList;
import com.android.dx.util.IntSet;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
/* loaded from: classes.dex */
public final class SsaBasicBlock {
    public static final Comparator<SsaBasicBlock> LABEL_COMPARATOR = new LabelComparator();
    private final int index;
    private IntSet liveIn;
    private IntSet liveOut;
    private final SsaMethod parent;
    private BitSet predecessors;
    private final int ropLabel;
    private BitSet successors;
    private int primarySuccessor = -1;
    private int movesFromPhisAtEnd = 0;
    private int movesFromPhisAtBeginning = 0;
    private final ArrayList<SsaInsn> insns = new ArrayList<>();
    private IntList successorList = new IntList();
    private final ArrayList<SsaBasicBlock> domChildren = new ArrayList<>();

    /* loaded from: classes.dex */
    public interface Visitor {
        void visitBlock(SsaBasicBlock ssaBasicBlock, SsaBasicBlock ssaBasicBlock2);
    }

    public SsaBasicBlock(int i, int i2, SsaMethod ssaMethod) {
        this.parent = ssaMethod;
        this.index = i;
        this.ropLabel = i2;
        this.predecessors = new BitSet(ssaMethod.getBlocks().size());
        this.successors = new BitSet(ssaMethod.getBlocks().size());
    }

    public static SsaBasicBlock newFromRop(RopMethod ropMethod, int i, SsaMethod ssaMethod) {
        BasicBlockList blocks = ropMethod.getBlocks();
        BasicBlock basicBlock = blocks.get(i);
        SsaBasicBlock ssaBasicBlock = new SsaBasicBlock(i, basicBlock.getLabel(), ssaMethod);
        InsnList insns = basicBlock.getInsns();
        ssaBasicBlock.insns.ensureCapacity(insns.size());
        int size = insns.size();
        for (int i2 = 0; i2 < size; i2++) {
            ssaBasicBlock.insns.add(new NormalSsaInsn(insns.get(i2), ssaBasicBlock));
        }
        ssaBasicBlock.predecessors = SsaMethod.bitSetFromLabelList(blocks, ropMethod.labelToPredecessors(basicBlock.getLabel()));
        ssaBasicBlock.successors = SsaMethod.bitSetFromLabelList(blocks, basicBlock.getSuccessors());
        IntList indexListFromLabelList = SsaMethod.indexListFromLabelList(blocks, basicBlock.getSuccessors());
        ssaBasicBlock.successorList = indexListFromLabelList;
        if (indexListFromLabelList.size() != 0) {
            int primarySuccessor = basicBlock.getPrimarySuccessor();
            ssaBasicBlock.primarySuccessor = primarySuccessor < 0 ? -1 : blocks.indexOfLabel(primarySuccessor);
        }
        return ssaBasicBlock;
    }

    public void addDomChild(SsaBasicBlock ssaBasicBlock) {
        this.domChildren.add(ssaBasicBlock);
    }

    public ArrayList<SsaBasicBlock> getDomChildren() {
        return this.domChildren;
    }

    public void addPhiInsnForReg(int i) {
        this.insns.add(0, new PhiInsn(i, this));
    }

    public void addPhiInsnForReg(RegisterSpec registerSpec) {
        this.insns.add(0, new PhiInsn(registerSpec, this));
    }

    public void addInsnToHead(Insn insn) {
        SsaInsn makeFromRop = SsaInsn.makeFromRop(insn, this);
        this.insns.add(getCountPhiInsns(), makeFromRop);
        this.parent.onInsnAdded(makeFromRop);
    }

    public void replaceLastInsn(Insn insn) {
        if (insn.getOpcode().getBranchingness() == 1) {
            throw new IllegalArgumentException("last insn must branch");
        }
        ArrayList<SsaInsn> arrayList = this.insns;
        SsaInsn makeFromRop = SsaInsn.makeFromRop(insn, this);
        ArrayList<SsaInsn> arrayList2 = this.insns;
        arrayList2.set(arrayList2.size() - 1, makeFromRop);
        this.parent.onInsnRemoved(arrayList.get(arrayList.size() - 1));
        this.parent.onInsnAdded(makeFromRop);
    }

    public void forEachPhiInsn(PhiInsn.Visitor visitor) {
        int size = this.insns.size();
        for (int i = 0; i < size; i++) {
            SsaInsn ssaInsn = this.insns.get(i);
            if (!(ssaInsn instanceof PhiInsn)) {
                return;
            }
            visitor.visitPhiInsn((PhiInsn) ssaInsn);
        }
    }

    public void removeAllPhiInsns() {
        this.insns.subList(0, getCountPhiInsns()).clear();
    }

    private int getCountPhiInsns() {
        int size = this.insns.size();
        int i = 0;
        while (i < size && (this.insns.get(i) instanceof PhiInsn)) {
            i++;
        }
        return i;
    }

    public ArrayList<SsaInsn> getInsns() {
        return this.insns;
    }

    public List<SsaInsn> getPhiInsns() {
        return this.insns.subList(0, getCountPhiInsns());
    }

    public int getIndex() {
        return this.index;
    }

    public int getRopLabel() {
        return this.ropLabel;
    }

    public String getRopLabelString() {
        return Hex.u2(this.ropLabel);
    }

    public BitSet getPredecessors() {
        return this.predecessors;
    }

    public BitSet getSuccessors() {
        return this.successors;
    }

    public IntList getSuccessorList() {
        return this.successorList;
    }

    public int getPrimarySuccessorIndex() {
        return this.primarySuccessor;
    }

    public int getPrimarySuccessorRopLabel() {
        return this.parent.blockIndexToRopLabel(this.primarySuccessor);
    }

    public SsaBasicBlock getPrimarySuccessor() {
        if (this.primarySuccessor < 0) {
            return null;
        }
        return this.parent.getBlocks().get(this.primarySuccessor);
    }

    public IntList getRopLabelSuccessorList() {
        IntList intList = new IntList(this.successorList.size());
        int size = this.successorList.size();
        for (int i = 0; i < size; i++) {
            intList.add(this.parent.blockIndexToRopLabel(this.successorList.get(i)));
        }
        return intList;
    }

    public SsaMethod getParent() {
        return this.parent;
    }

    public SsaBasicBlock insertNewPredecessor() {
        SsaBasicBlock makeNewGotoBlock = this.parent.makeNewGotoBlock();
        makeNewGotoBlock.predecessors = this.predecessors;
        makeNewGotoBlock.successors.set(this.index);
        makeNewGotoBlock.successorList.add(this.index);
        makeNewGotoBlock.primarySuccessor = this.index;
        BitSet bitSet = new BitSet(this.parent.getBlocks().size());
        this.predecessors = bitSet;
        bitSet.set(makeNewGotoBlock.index);
        for (int nextSetBit = makeNewGotoBlock.predecessors.nextSetBit(0); nextSetBit >= 0; nextSetBit = makeNewGotoBlock.predecessors.nextSetBit(nextSetBit + 1)) {
            this.parent.getBlocks().get(nextSetBit).replaceSuccessor(this.index, makeNewGotoBlock.index);
        }
        return makeNewGotoBlock;
    }

    public SsaBasicBlock insertNewSuccessor(SsaBasicBlock ssaBasicBlock) {
        SsaBasicBlock makeNewGotoBlock = this.parent.makeNewGotoBlock();
        if (!this.successors.get(ssaBasicBlock.index)) {
            throw new RuntimeException("Block " + ssaBasicBlock.getRopLabelString() + " not successor of " + getRopLabelString());
        }
        makeNewGotoBlock.predecessors.set(this.index);
        makeNewGotoBlock.successors.set(ssaBasicBlock.index);
        makeNewGotoBlock.successorList.add(ssaBasicBlock.index);
        makeNewGotoBlock.primarySuccessor = ssaBasicBlock.index;
        for (int size = this.successorList.size() - 1; size >= 0; size--) {
            if (this.successorList.get(size) == ssaBasicBlock.index) {
                this.successorList.set(size, makeNewGotoBlock.index);
            }
        }
        int i = this.primarySuccessor;
        int i2 = ssaBasicBlock.index;
        if (i == i2) {
            this.primarySuccessor = makeNewGotoBlock.index;
        }
        this.successors.clear(i2);
        this.successors.set(makeNewGotoBlock.index);
        ssaBasicBlock.predecessors.set(makeNewGotoBlock.index);
        ssaBasicBlock.predecessors.set(this.index, this.successors.get(ssaBasicBlock.index));
        return makeNewGotoBlock;
    }

    public void replaceSuccessor(int i, int i2) {
        if (i == i2) {
            return;
        }
        this.successors.set(i2);
        if (this.primarySuccessor == i) {
            this.primarySuccessor = i2;
        }
        for (int size = this.successorList.size() - 1; size >= 0; size--) {
            if (this.successorList.get(size) == i) {
                this.successorList.set(size, i2);
            }
        }
        this.successors.clear(i);
        this.parent.getBlocks().get(i2).predecessors.set(this.index);
        this.parent.getBlocks().get(i).predecessors.clear(this.index);
    }

    public void removeSuccessor(int i) {
        int i2 = 0;
        for (int size = this.successorList.size() - 1; size >= 0; size--) {
            if (this.successorList.get(size) == i) {
                i2 = size;
            } else {
                this.primarySuccessor = this.successorList.get(size);
            }
        }
        this.successorList.removeIndex(i2);
        this.successors.clear(i);
        this.parent.getBlocks().get(i).predecessors.clear(this.index);
    }

    public void exitBlockFixup(SsaBasicBlock ssaBasicBlock) {
        if (this != ssaBasicBlock && this.successorList.size() == 0) {
            this.successors.set(ssaBasicBlock.index);
            this.successorList.add(ssaBasicBlock.index);
            this.primarySuccessor = ssaBasicBlock.index;
            ssaBasicBlock.predecessors.set(this.index);
        }
    }

    public void addMoveToEnd(RegisterSpec registerSpec, RegisterSpec registerSpec2) {
        if (this.successors.cardinality() > 1) {
            throw new IllegalStateException("Inserting a move to a block with multiple successors");
        }
        if (registerSpec.getReg() == registerSpec2.getReg()) {
            return;
        }
        ArrayList<SsaInsn> arrayList = this.insns;
        NormalSsaInsn normalSsaInsn = (NormalSsaInsn) arrayList.get(arrayList.size() - 1);
        if (normalSsaInsn.getResult() != null || normalSsaInsn.getSources().size() > 0) {
            int nextSetBit = this.successors.nextSetBit(0);
            while (nextSetBit >= 0) {
                this.parent.getBlocks().get(nextSetBit).addMoveToBeginning(registerSpec, registerSpec2);
                nextSetBit = this.successors.nextSetBit(nextSetBit + 1);
            }
            return;
        }
        NormalSsaInsn normalSsaInsn2 = new NormalSsaInsn(new PlainInsn(Rops.opMove(registerSpec.getType()), SourcePosition.NO_INFO, registerSpec, RegisterSpecList.make(registerSpec2)), this);
        ArrayList<SsaInsn> arrayList2 = this.insns;
        arrayList2.add(arrayList2.size() - 1, normalSsaInsn2);
        this.movesFromPhisAtEnd++;
    }

    public void addMoveToBeginning(RegisterSpec registerSpec, RegisterSpec registerSpec2) {
        if (registerSpec.getReg() == registerSpec2.getReg()) {
            return;
        }
        this.insns.add(getCountPhiInsns(), new NormalSsaInsn(new PlainInsn(Rops.opMove(registerSpec.getType()), SourcePosition.NO_INFO, registerSpec, RegisterSpecList.make(registerSpec2)), this));
        this.movesFromPhisAtBeginning++;
    }

    private static void setRegsUsed(BitSet bitSet, RegisterSpec registerSpec) {
        bitSet.set(registerSpec.getReg());
        if (registerSpec.getCategory() > 1) {
            bitSet.set(registerSpec.getReg() + 1);
        }
    }

    private static boolean checkRegUsed(BitSet bitSet, RegisterSpec registerSpec) {
        int reg = registerSpec.getReg();
        int category = registerSpec.getCategory();
        if (bitSet.get(reg)) {
            return true;
        }
        return category == 2 && bitSet.get(reg + 1);
    }

    private void scheduleUseBeforeAssigned(List<SsaInsn> list) {
        BitSet bitSet = new BitSet(this.parent.getRegCount());
        BitSet bitSet2 = new BitSet(this.parent.getRegCount());
        int size = list.size();
        int i = 0;
        while (i < size) {
            for (int i2 = i; i2 < size; i2++) {
                setRegsUsed(bitSet, list.get(i2).getSources().get(0));
                setRegsUsed(bitSet2, list.get(i2).getResult());
            }
            int i3 = i;
            int i4 = i3;
            while (i3 < size) {
                if (!checkRegUsed(bitSet, list.get(i3).getResult())) {
                    Collections.swap(list, i3, i4);
                    i4++;
                }
                i3++;
            }
            if (i == i4) {
                SsaInsn ssaInsn = null;
                int i5 = i4;
                while (true) {
                    if (i5 >= size) {
                        break;
                    }
                    SsaInsn ssaInsn2 = list.get(i5);
                    if (checkRegUsed(bitSet, ssaInsn2.getResult()) && checkRegUsed(bitSet2, ssaInsn2.getSources().get(0))) {
                        Collections.swap(list, i4, i5);
                        ssaInsn = ssaInsn2;
                        break;
                    }
                    i5++;
                }
                RegisterSpec result = ssaInsn.getResult();
                RegisterSpec withReg = result.withReg(this.parent.borrowSpareRegister(result.getCategory()));
                i = i4 + 1;
                list.add(i4, new NormalSsaInsn(new PlainInsn(Rops.opMove(result.getType()), SourcePosition.NO_INFO, withReg, ssaInsn.getSources()), this));
                list.set(i, new NormalSsaInsn(new PlainInsn(Rops.opMove(result.getType()), SourcePosition.NO_INFO, result, RegisterSpecList.make(withReg)), this));
                size = list.size();
            } else {
                i = i4;
            }
            bitSet.clear();
            bitSet2.clear();
        }
    }

    public void addLiveOut(int i) {
        if (this.liveOut == null) {
            this.liveOut = SetFactory.makeLivenessSet(this.parent.getRegCount());
        }
        this.liveOut.add(i);
    }

    public void addLiveIn(int i) {
        if (this.liveIn == null) {
            this.liveIn = SetFactory.makeLivenessSet(this.parent.getRegCount());
        }
        this.liveIn.add(i);
    }

    public IntSet getLiveInRegs() {
        if (this.liveIn == null) {
            this.liveIn = SetFactory.makeLivenessSet(this.parent.getRegCount());
        }
        return this.liveIn;
    }

    public IntSet getLiveOutRegs() {
        if (this.liveOut == null) {
            this.liveOut = SetFactory.makeLivenessSet(this.parent.getRegCount());
        }
        return this.liveOut;
    }

    public boolean isExitBlock() {
        return this.index == this.parent.getExitBlockIndex();
    }

    public void scheduleMovesFromPhis() {
        int i = this.movesFromPhisAtBeginning;
        if (i > 1) {
            scheduleUseBeforeAssigned(this.insns.subList(0, i));
            if (this.insns.get(this.movesFromPhisAtBeginning).isMoveException()) {
                throw new RuntimeException("Unexpected: moves from phis before move-exception");
            }
        }
        if (this.movesFromPhisAtEnd > 1) {
            ArrayList<SsaInsn> arrayList = this.insns;
            scheduleUseBeforeAssigned(arrayList.subList((arrayList.size() - this.movesFromPhisAtEnd) - 1, this.insns.size() - 1));
        }
        this.parent.returnSpareRegisters();
    }

    public void forEachInsn(SsaInsn.Visitor visitor) {
        int size = this.insns.size();
        for (int i = 0; i < size; i++) {
            this.insns.get(i).accept(visitor);
        }
    }

    public String toString() {
        return "{" + this.index + ":" + Hex.u2(this.ropLabel) + '}';
    }

    /* loaded from: classes.dex */
    public static final class LabelComparator implements Comparator<SsaBasicBlock> {
        @Override // java.util.Comparator
        public int compare(SsaBasicBlock ssaBasicBlock, SsaBasicBlock ssaBasicBlock2) {
            int i = ssaBasicBlock.ropLabel;
            int i2 = ssaBasicBlock2.ropLabel;
            if (i < i2) {
                return -1;
            }
            return i > i2 ? 1 : 0;
        }
    }
}

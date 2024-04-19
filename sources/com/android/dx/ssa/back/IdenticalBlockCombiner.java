package com.android.dx.ssa.back;

import com.android.dx.rop.code.BasicBlock;
import com.android.dx.rop.code.BasicBlockList;
import com.android.dx.rop.code.RopMethod;
import com.android.dx.util.IntList;
import java.util.BitSet;
/* loaded from: classes.dex */
public class IdenticalBlockCombiner {
    private final BasicBlockList blocks;
    private final BasicBlockList newBlocks;
    private final RopMethod ropMethod;

    public IdenticalBlockCombiner(RopMethod ropMethod) {
        this.ropMethod = ropMethod;
        BasicBlockList blocks = ropMethod.getBlocks();
        this.blocks = blocks;
        this.newBlocks = blocks.getMutableCopy();
    }

    public RopMethod process() {
        int size = this.blocks.size();
        BitSet bitSet = new BitSet(this.blocks.getMaxLabel());
        for (int i = 0; i < size; i++) {
            BasicBlock basicBlock = this.blocks.get(i);
            if (!bitSet.get(basicBlock.getLabel())) {
                IntList labelToPredecessors = this.ropMethod.labelToPredecessors(basicBlock.getLabel());
                int size2 = labelToPredecessors.size();
                for (int i2 = 0; i2 < size2; i2++) {
                    int i3 = labelToPredecessors.get(i2);
                    BasicBlock labelToBlock = this.blocks.labelToBlock(i3);
                    if (!bitSet.get(i3) && labelToBlock.getSuccessors().size() <= 1 && labelToBlock.getFirstInsn().getOpcode().getOpcode() != 55) {
                        IntList intList = new IntList();
                        for (int i4 = i2 + 1; i4 < size2; i4++) {
                            int i5 = labelToPredecessors.get(i4);
                            BasicBlock labelToBlock2 = this.blocks.labelToBlock(i5);
                            if (labelToBlock2.getSuccessors().size() == 1 && compareInsns(labelToBlock, labelToBlock2)) {
                                intList.add(i5);
                                bitSet.set(i5);
                            }
                        }
                        combineBlocks(i3, intList);
                    }
                }
            }
        }
        for (int i6 = size - 1; i6 >= 0; i6--) {
            if (bitSet.get(this.newBlocks.get(i6).getLabel())) {
                this.newBlocks.set(i6, (BasicBlock) null);
            }
        }
        this.newBlocks.shrinkToFit();
        this.newBlocks.setImmutable();
        return new RopMethod(this.newBlocks, this.ropMethod.getFirstLabel());
    }

    private static boolean compareInsns(BasicBlock basicBlock, BasicBlock basicBlock2) {
        return basicBlock.getInsns().contentEquals(basicBlock2.getInsns());
    }

    private void combineBlocks(int i, IntList intList) {
        int size = intList.size();
        for (int i2 = 0; i2 < size; i2++) {
            int i3 = intList.get(i2);
            IntList labelToPredecessors = this.ropMethod.labelToPredecessors(this.blocks.labelToBlock(i3).getLabel());
            int size2 = labelToPredecessors.size();
            for (int i4 = 0; i4 < size2; i4++) {
                replaceSucc(this.newBlocks.labelToBlock(labelToPredecessors.get(i4)), i3, i);
            }
        }
    }

    private void replaceSucc(BasicBlock basicBlock, int i, int i2) {
        IntList mutableCopy = basicBlock.getSuccessors().mutableCopy();
        mutableCopy.set(mutableCopy.indexOf(i), i2);
        int primarySuccessor = basicBlock.getPrimarySuccessor();
        if (primarySuccessor != i) {
            i2 = primarySuccessor;
        }
        mutableCopy.setImmutable();
        BasicBlock basicBlock2 = new BasicBlock(basicBlock.getLabel(), basicBlock.getInsns(), mutableCopy, i2);
        BasicBlockList basicBlockList = this.newBlocks;
        basicBlockList.set(basicBlockList.indexOfLabel(basicBlock.getLabel()), basicBlock2);
    }
}

package com.android.dx.ssa;

import com.android.dx.rop.code.RegisterSpec;
import com.android.dx.rop.code.RopMethod;
import com.android.dx.ssa.DomFront;
import com.android.dx.util.IntIterator;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
/* loaded from: classes.dex */
public class SsaConverter {
    public static final boolean DEBUG = false;

    public static SsaMethod convertToSsaMethod(RopMethod ropMethod, int i, boolean z) {
        SsaMethod newFromRopMethod = SsaMethod.newFromRopMethod(ropMethod, i, z);
        edgeSplit(newFromRopMethod);
        placePhiFunctions(newFromRopMethod, LocalVariableExtractor.extract(newFromRopMethod), 0);
        new SsaRenamer(newFromRopMethod).run();
        newFromRopMethod.makeExitBlock();
        return newFromRopMethod;
    }

    public static void updateSsaMethod(SsaMethod ssaMethod, int i) {
        placePhiFunctions(ssaMethod, LocalVariableExtractor.extract(ssaMethod), i);
        new SsaRenamer(ssaMethod, i).run();
    }

    public static SsaMethod testEdgeSplit(RopMethod ropMethod, int i, boolean z) {
        SsaMethod newFromRopMethod = SsaMethod.newFromRopMethod(ropMethod, i, z);
        edgeSplit(newFromRopMethod);
        return newFromRopMethod;
    }

    public static SsaMethod testPhiPlacement(RopMethod ropMethod, int i, boolean z) {
        SsaMethod newFromRopMethod = SsaMethod.newFromRopMethod(ropMethod, i, z);
        edgeSplit(newFromRopMethod);
        placePhiFunctions(newFromRopMethod, LocalVariableExtractor.extract(newFromRopMethod), 0);
        return newFromRopMethod;
    }

    private static void edgeSplit(SsaMethod ssaMethod) {
        edgeSplitPredecessors(ssaMethod);
        edgeSplitMoveExceptionsAndResults(ssaMethod);
        edgeSplitSuccessors(ssaMethod);
    }

    private static void edgeSplitPredecessors(SsaMethod ssaMethod) {
        ArrayList<SsaBasicBlock> blocks = ssaMethod.getBlocks();
        for (int size = blocks.size() - 1; size >= 0; size--) {
            SsaBasicBlock ssaBasicBlock = blocks.get(size);
            if (nodeNeedsUniquePredecessor(ssaBasicBlock)) {
                ssaBasicBlock.insertNewPredecessor();
            }
        }
    }

    private static boolean nodeNeedsUniquePredecessor(SsaBasicBlock ssaBasicBlock) {
        return ssaBasicBlock.getPredecessors().cardinality() > 1 && ssaBasicBlock.getSuccessors().cardinality() > 1;
    }

    private static void edgeSplitMoveExceptionsAndResults(SsaMethod ssaMethod) {
        ArrayList<SsaBasicBlock> blocks = ssaMethod.getBlocks();
        for (int size = blocks.size() - 1; size >= 0; size--) {
            SsaBasicBlock ssaBasicBlock = blocks.get(size);
            if (!ssaBasicBlock.isExitBlock() && ssaBasicBlock.getPredecessors().cardinality() > 1 && ssaBasicBlock.getInsns().get(0).isMoveException()) {
                BitSet bitSet = (BitSet) ssaBasicBlock.getPredecessors().clone();
                for (int nextSetBit = bitSet.nextSetBit(0); nextSetBit >= 0; nextSetBit = bitSet.nextSetBit(nextSetBit + 1)) {
                    blocks.get(nextSetBit).insertNewSuccessor(ssaBasicBlock).getInsns().add(0, ssaBasicBlock.getInsns().get(0).mo14clone());
                }
                ssaBasicBlock.getInsns().remove(0);
            }
        }
    }

    private static void edgeSplitSuccessors(SsaMethod ssaMethod) {
        ArrayList<SsaBasicBlock> blocks = ssaMethod.getBlocks();
        for (int size = blocks.size() - 1; size >= 0; size--) {
            SsaBasicBlock ssaBasicBlock = blocks.get(size);
            BitSet bitSet = (BitSet) ssaBasicBlock.getSuccessors().clone();
            for (int nextSetBit = bitSet.nextSetBit(0); nextSetBit >= 0; nextSetBit = bitSet.nextSetBit(nextSetBit + 1)) {
                SsaBasicBlock ssaBasicBlock2 = blocks.get(nextSetBit);
                if (needsNewSuccessor(ssaBasicBlock, ssaBasicBlock2)) {
                    ssaBasicBlock.insertNewSuccessor(ssaBasicBlock2);
                }
            }
        }
    }

    private static boolean needsNewSuccessor(SsaBasicBlock ssaBasicBlock, SsaBasicBlock ssaBasicBlock2) {
        ArrayList<SsaInsn> insns = ssaBasicBlock.getInsns();
        SsaInsn ssaInsn = insns.get(insns.size() - 1);
        if (ssaBasicBlock.getSuccessors().cardinality() <= 1 || ssaBasicBlock2.getPredecessors().cardinality() <= 1) {
            return (ssaInsn.getResult() != null || ssaInsn.getSources().size() > 0) && ssaBasicBlock2.getPredecessors().cardinality() > 1;
        }
        return true;
    }

    private static void placePhiFunctions(SsaMethod ssaMethod, LocalVariableInfo localVariableInfo, int i) {
        ArrayList<SsaBasicBlock> blocks = ssaMethod.getBlocks();
        int size = blocks.size();
        int regCount = ssaMethod.getRegCount() - i;
        DomFront.DomInfo[] run = new DomFront(ssaMethod).run();
        BitSet[] bitSetArr = new BitSet[regCount];
        BitSet[] bitSetArr2 = new BitSet[regCount];
        for (int i2 = 0; i2 < regCount; i2++) {
            bitSetArr[i2] = new BitSet(size);
            bitSetArr2[i2] = new BitSet(size);
        }
        int size2 = blocks.size();
        for (int i3 = 0; i3 < size2; i3++) {
            Iterator<SsaInsn> it = blocks.get(i3).getInsns().iterator();
            while (it.hasNext()) {
                RegisterSpec result = it.next().getResult();
                if (result != null && result.getReg() - i >= 0) {
                    bitSetArr[result.getReg() - i].set(i3);
                }
            }
        }
        for (int i4 = 0; i4 < regCount; i4++) {
            BitSet bitSet = (BitSet) bitSetArr[i4].clone();
            while (true) {
                int nextSetBit = bitSet.nextSetBit(0);
                if (nextSetBit >= 0) {
                    bitSet.clear(nextSetBit);
                    IntIterator it2 = run[nextSetBit].dominanceFrontiers.iterator();
                    while (it2.hasNext()) {
                        int next = it2.next();
                        if (!bitSetArr2[i4].get(next)) {
                            bitSetArr2[i4].set(next);
                            int i5 = i4 + i;
                            RegisterSpec registerSpec = localVariableInfo.getStarts(next).get(i5);
                            if (registerSpec == null) {
                                blocks.get(next).addPhiInsnForReg(i5);
                            } else {
                                blocks.get(next).addPhiInsnForReg(registerSpec);
                            }
                            if (!bitSetArr[i4].get(next)) {
                                bitSet.set(next);
                            }
                        }
                    }
                }
            }
        }
    }
}

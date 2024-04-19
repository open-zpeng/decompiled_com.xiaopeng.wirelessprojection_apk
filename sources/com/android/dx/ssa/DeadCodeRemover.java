package com.android.dx.ssa;

import com.android.dx.rop.code.RegisterSpec;
import com.android.dx.rop.code.RegisterSpecList;
import com.android.dx.ssa.SsaInsn;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;
/* loaded from: classes.dex */
public class DeadCodeRemover {
    private final int regCount;
    private final SsaMethod ssaMeth;
    private final ArrayList<SsaInsn>[] useList;
    private final BitSet worklist;

    public static void process(SsaMethod ssaMethod) {
        new DeadCodeRemover(ssaMethod).run();
    }

    private DeadCodeRemover(SsaMethod ssaMethod) {
        this.ssaMeth = ssaMethod;
        int regCount = ssaMethod.getRegCount();
        this.regCount = regCount;
        this.worklist = new BitSet(regCount);
        this.useList = ssaMethod.getUseListCopy();
    }

    private void run() {
        pruneDeadInstructions();
        HashSet hashSet = new HashSet();
        this.ssaMeth.forEachInsn(new NoSideEffectVisitor(this.worklist));
        while (true) {
            int nextSetBit = this.worklist.nextSetBit(0);
            if (nextSetBit >= 0) {
                this.worklist.clear(nextSetBit);
                if (this.useList[nextSetBit].size() == 0 || isCircularNoSideEffect(nextSetBit, null)) {
                    SsaInsn definitionForRegister = this.ssaMeth.getDefinitionForRegister(nextSetBit);
                    if (!hashSet.contains(definitionForRegister)) {
                        RegisterSpecList sources = definitionForRegister.getSources();
                        int size = sources.size();
                        for (int i = 0; i < size; i++) {
                            RegisterSpec registerSpec = sources.get(i);
                            this.useList[registerSpec.getReg()].remove(definitionForRegister);
                            if (!hasSideEffect(this.ssaMeth.getDefinitionForRegister(registerSpec.getReg()))) {
                                this.worklist.set(registerSpec.getReg());
                            }
                        }
                        hashSet.add(definitionForRegister);
                    }
                }
            } else {
                this.ssaMeth.deleteInsns(hashSet);
                return;
            }
        }
    }

    private void pruneDeadInstructions() {
        HashSet hashSet = new HashSet();
        BitSet computeReachability = this.ssaMeth.computeReachability();
        ArrayList<SsaBasicBlock> blocks = this.ssaMeth.getBlocks();
        int i = 0;
        while (true) {
            int nextClearBit = computeReachability.nextClearBit(i);
            if (nextClearBit < blocks.size()) {
                SsaBasicBlock ssaBasicBlock = blocks.get(nextClearBit);
                i = nextClearBit + 1;
                for (int i2 = 0; i2 < ssaBasicBlock.getInsns().size(); i2++) {
                    SsaInsn ssaInsn = ssaBasicBlock.getInsns().get(i2);
                    RegisterSpecList sources = ssaInsn.getSources();
                    int size = sources.size();
                    if (size != 0) {
                        hashSet.add(ssaInsn);
                    }
                    for (int i3 = 0; i3 < size; i3++) {
                        this.useList[sources.get(i3).getReg()].remove(ssaInsn);
                    }
                    RegisterSpec result = ssaInsn.getResult();
                    if (result != null) {
                        Iterator<SsaInsn> it = this.useList[result.getReg()].iterator();
                        while (it.hasNext()) {
                            SsaInsn next = it.next();
                            if (next instanceof PhiInsn) {
                                ((PhiInsn) next).removePhiRegister(result);
                            }
                        }
                    }
                }
            } else {
                this.ssaMeth.deleteInsns(hashSet);
                return;
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:18:0x0040  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean isCircularNoSideEffect(int r5, java.util.BitSet r6) {
        /*
            r4 = this;
            r0 = 1
            if (r6 == 0) goto La
            boolean r1 = r6.get(r5)
            if (r1 == 0) goto La
            return r0
        La:
            java.util.ArrayList<com.android.dx.ssa.SsaInsn>[] r1 = r4.useList
            r1 = r1[r5]
            java.util.Iterator r1 = r1.iterator()
        L12:
            boolean r2 = r1.hasNext()
            r3 = 0
            if (r2 == 0) goto L26
            java.lang.Object r2 = r1.next()
            com.android.dx.ssa.SsaInsn r2 = (com.android.dx.ssa.SsaInsn) r2
            boolean r2 = hasSideEffect(r2)
            if (r2 == 0) goto L12
            return r3
        L26:
            if (r6 != 0) goto L2f
            java.util.BitSet r6 = new java.util.BitSet
            int r1 = r4.regCount
            r6.<init>(r1)
        L2f:
            r6.set(r5)
            java.util.ArrayList<com.android.dx.ssa.SsaInsn>[] r1 = r4.useList
            r5 = r1[r5]
            java.util.Iterator r5 = r5.iterator()
        L3a:
            boolean r1 = r5.hasNext()
            if (r1 == 0) goto L57
            java.lang.Object r1 = r5.next()
            com.android.dx.ssa.SsaInsn r1 = (com.android.dx.ssa.SsaInsn) r1
            com.android.dx.rop.code.RegisterSpec r1 = r1.getResult()
            if (r1 == 0) goto L56
            int r1 = r1.getReg()
            boolean r1 = r4.isCircularNoSideEffect(r1, r6)
            if (r1 != 0) goto L3a
        L56:
            return r3
        L57:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.dx.ssa.DeadCodeRemover.isCircularNoSideEffect(int, java.util.BitSet):boolean");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean hasSideEffect(SsaInsn ssaInsn) {
        if (ssaInsn == null) {
            return true;
        }
        return ssaInsn.hasSideEffect();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class NoSideEffectVisitor implements SsaInsn.Visitor {
        BitSet noSideEffectRegs;

        public NoSideEffectVisitor(BitSet bitSet) {
            this.noSideEffectRegs = bitSet;
        }

        @Override // com.android.dx.ssa.SsaInsn.Visitor
        public void visitMoveInsn(NormalSsaInsn normalSsaInsn) {
            if (DeadCodeRemover.hasSideEffect(normalSsaInsn)) {
                return;
            }
            this.noSideEffectRegs.set(normalSsaInsn.getResult().getReg());
        }

        @Override // com.android.dx.ssa.SsaInsn.Visitor
        public void visitPhiInsn(PhiInsn phiInsn) {
            if (DeadCodeRemover.hasSideEffect(phiInsn)) {
                return;
            }
            this.noSideEffectRegs.set(phiInsn.getResult().getReg());
        }

        @Override // com.android.dx.ssa.SsaInsn.Visitor
        public void visitNonMoveInsn(NormalSsaInsn normalSsaInsn) {
            RegisterSpec result = normalSsaInsn.getResult();
            if (DeadCodeRemover.hasSideEffect(normalSsaInsn) || result == null) {
                return;
            }
            this.noSideEffectRegs.set(result.getReg());
        }
    }
}

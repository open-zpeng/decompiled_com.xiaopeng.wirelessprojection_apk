package com.android.dx.ssa;

import com.android.dx.rop.code.CstInsn;
import com.android.dx.rop.code.Insn;
import com.android.dx.rop.code.PlainInsn;
import com.android.dx.rop.code.RegisterSpec;
import com.android.dx.rop.code.RegisterSpecList;
import com.android.dx.rop.code.Rops;
import com.android.dx.rop.cst.Constant;
import com.android.dx.rop.cst.CstInteger;
import com.android.dx.rop.cst.TypedConstant;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
/* loaded from: classes.dex */
public class SCCP {
    private static final int CONSTANT = 1;
    private static final int TOP = 0;
    private static final int VARYING = 2;
    private final ArrayList<SsaInsn> branchWorklist;
    private final ArrayList<SsaBasicBlock> cfgPhiWorklist;
    private final ArrayList<SsaBasicBlock> cfgWorklist;
    private final BitSet executableBlocks;
    private final Constant[] latticeConstants;
    private final int[] latticeValues;
    private final int regCount;
    private final SsaMethod ssaMeth;
    private final ArrayList<SsaInsn> ssaWorklist;
    private final ArrayList<SsaInsn> varyingWorklist;

    private static String latticeValName(int i) {
        return i != 0 ? i != 1 ? i != 2 ? "UNKNOWN" : "VARYING" : "CONSTANT" : "TOP";
    }

    private SCCP(SsaMethod ssaMethod) {
        this.ssaMeth = ssaMethod;
        int regCount = ssaMethod.getRegCount();
        this.regCount = regCount;
        this.latticeValues = new int[regCount];
        this.latticeConstants = new Constant[regCount];
        this.cfgWorklist = new ArrayList<>();
        this.cfgPhiWorklist = new ArrayList<>();
        this.executableBlocks = new BitSet(ssaMethod.getBlocks().size());
        this.ssaWorklist = new ArrayList<>();
        this.varyingWorklist = new ArrayList<>();
        this.branchWorklist = new ArrayList<>();
        for (int i = 0; i < this.regCount; i++) {
            this.latticeValues[i] = 0;
            this.latticeConstants[i] = null;
        }
    }

    public static void process(SsaMethod ssaMethod) {
        new SCCP(ssaMethod).run();
    }

    private void addBlockToWorklist(SsaBasicBlock ssaBasicBlock) {
        if (!this.executableBlocks.get(ssaBasicBlock.getIndex())) {
            this.cfgWorklist.add(ssaBasicBlock);
            this.executableBlocks.set(ssaBasicBlock.getIndex());
            return;
        }
        this.cfgPhiWorklist.add(ssaBasicBlock);
    }

    private void addUsersToWorklist(int i, int i2) {
        if (i2 == 2) {
            for (SsaInsn ssaInsn : this.ssaMeth.getUseListForRegister(i)) {
                this.varyingWorklist.add(ssaInsn);
            }
            return;
        }
        for (SsaInsn ssaInsn2 : this.ssaMeth.getUseListForRegister(i)) {
            this.ssaWorklist.add(ssaInsn2);
        }
    }

    private boolean setLatticeValueTo(int i, int i2, Constant constant) {
        if (i2 != 1) {
            int[] iArr = this.latticeValues;
            if (iArr[i] != i2) {
                iArr[i] = i2;
                return true;
            }
            return false;
        } else if (this.latticeValues[i] == i2 && this.latticeConstants[i].equals(constant)) {
            return false;
        } else {
            this.latticeValues[i] = i2;
            this.latticeConstants[i] = constant;
            return true;
        }
    }

    private void simulatePhi(PhiInsn phiInsn) {
        int reg = phiInsn.getResult().getReg();
        int i = 2;
        if (this.latticeValues[reg] == 2) {
            return;
        }
        RegisterSpecList sources = phiInsn.getSources();
        Constant constant = null;
        int size = sources.size();
        int i2 = 0;
        int i3 = 0;
        while (true) {
            if (i2 >= size) {
                i = i3;
                break;
            }
            int predBlockIndexForSourcesIndex = phiInsn.predBlockIndexForSourcesIndex(i2);
            int reg2 = sources.get(i2).getReg();
            int i4 = this.latticeValues[reg2];
            if (this.executableBlocks.get(predBlockIndexForSourcesIndex)) {
                if (i4 != 1) {
                    i = i4;
                    break;
                } else if (constant == null) {
                    constant = this.latticeConstants[reg2];
                    i3 = 1;
                } else if (!this.latticeConstants[reg2].equals(constant)) {
                    break;
                }
            }
            i2++;
        }
        if (setLatticeValueTo(reg, i, constant)) {
            addUsersToWorklist(reg, i);
        }
    }

    private void simulateBlock(SsaBasicBlock ssaBasicBlock) {
        Iterator<SsaInsn> it = ssaBasicBlock.getInsns().iterator();
        while (it.hasNext()) {
            SsaInsn next = it.next();
            if (next instanceof PhiInsn) {
                simulatePhi((PhiInsn) next);
            } else {
                simulateStmt(next);
            }
        }
    }

    private void simulatePhiBlock(SsaBasicBlock ssaBasicBlock) {
        Iterator<SsaInsn> it = ssaBasicBlock.getInsns().iterator();
        while (it.hasNext()) {
            SsaInsn next = it.next();
            if (!(next instanceof PhiInsn)) {
                return;
            }
            simulatePhi((PhiInsn) next);
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:28:0x0078, code lost:
        if (r1 > 0) goto L41;
     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x007b, code lost:
        if (r1 <= 0) goto L41;
     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x007e, code lost:
        if (r1 >= 0) goto L41;
     */
    /* JADX WARN: Code restructure failed: missing block: B:34:0x0081, code lost:
        if (r1 < 0) goto L41;
     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x0084, code lost:
        if (r1 != 0) goto L41;
     */
    /* JADX WARN: Code restructure failed: missing block: B:38:0x0087, code lost:
        if (r1 == 0) goto L41;
     */
    /* JADX WARN: Code restructure failed: missing block: B:39:0x0089, code lost:
        r0 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:40:0x008b, code lost:
        r0 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:41:0x008c, code lost:
        r1 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:51:0x00b5, code lost:
        if (r1 > r2) goto L41;
     */
    /* JADX WARN: Code restructure failed: missing block: B:53:0x00b8, code lost:
        if (r1 <= r2) goto L41;
     */
    /* JADX WARN: Code restructure failed: missing block: B:55:0x00bb, code lost:
        if (r1 >= r2) goto L41;
     */
    /* JADX WARN: Code restructure failed: missing block: B:57:0x00be, code lost:
        if (r1 < r2) goto L41;
     */
    /* JADX WARN: Code restructure failed: missing block: B:59:0x00c1, code lost:
        if (r1 != r2) goto L41;
     */
    /* JADX WARN: Code restructure failed: missing block: B:61:0x00c4, code lost:
        if (r1 == r2) goto L41;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v11, types: [com.android.dx.rop.cst.Constant[]] */
    /* JADX WARN: Type inference failed for: r2v12 */
    /* JADX WARN: Type inference failed for: r5v8, types: [com.android.dx.rop.cst.Constant[]] */
    /* JADX WARN: Type inference failed for: r6v4 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void simulateBranch(com.android.dx.ssa.SsaInsn r10) {
        /*
            Method dump skipped, instructions count: 316
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.dx.ssa.SCCP.simulateBranch(com.android.dx.ssa.SsaInsn):void");
    }

    private Constant simulateMath(SsaInsn ssaInsn, int i) {
        Constant constant;
        int i2;
        int i3;
        Insn originalRopInsn = ssaInsn.getOriginalRopInsn();
        int opcode = ssaInsn.getOpcode().getOpcode();
        RegisterSpecList sources = ssaInsn.getSources();
        boolean z = false;
        int reg = sources.get(0).getReg();
        Constant constant2 = this.latticeValues[reg] != 1 ? null : this.latticeConstants[reg];
        if (sources.size() == 1) {
            constant = ((CstInsn) originalRopInsn).getConstant();
        } else {
            int reg2 = sources.get(1).getReg();
            constant = this.latticeValues[reg2] != 1 ? null : this.latticeConstants[reg2];
        }
        if (constant2 == null || constant == null || i != 6) {
            return null;
        }
        int value = ((CstInteger) constant2).getValue();
        int value2 = ((CstInteger) constant).getValue();
        switch (opcode) {
            case 14:
                i2 = value + value2;
                i3 = i2;
                break;
            case 15:
                if (sources.size() != 1) {
                    i2 = value - value2;
                    i3 = i2;
                    break;
                } else {
                    i3 = value2 - value;
                    break;
                }
            case 16:
                i2 = value * value2;
                i3 = i2;
                break;
            case 17:
                if (value2 != 0) {
                    i2 = value / value2;
                    i3 = i2;
                    break;
                }
                i3 = 0;
                z = true;
                break;
            case 18:
                if (value2 != 0) {
                    i2 = value % value2;
                    i3 = i2;
                    break;
                }
                i3 = 0;
                z = true;
                break;
            case 19:
            default:
                throw new RuntimeException("Unexpected op");
            case 20:
                i3 = value & value2;
                break;
            case 21:
                i3 = value | value2;
                break;
            case 22:
                i3 = value ^ value2;
                break;
            case 23:
                i3 = value << value2;
                break;
            case 24:
                i3 = value >> value2;
                break;
            case 25:
                i3 = value >>> value2;
                break;
        }
        if (z) {
            return null;
        }
        return CstInteger.make(i3);
    }

    /* JADX WARN: Code restructure failed: missing block: B:26:0x0069, code lost:
        if (r6 != null) goto L23;
     */
    /* JADX WARN: Removed duplicated region for block: B:38:0x00a6  */
    /* JADX WARN: Removed duplicated region for block: B:40:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void simulateStmt(com.android.dx.ssa.SsaInsn r9) {
        /*
            r8 = this;
            com.android.dx.rop.code.Insn r0 = r9.getOriginalRopInsn()
            com.android.dx.rop.code.Rop r1 = r0.getOpcode()
            int r1 = r1.getBranchingness()
            r2 = 1
            if (r1 != r2) goto L19
            com.android.dx.rop.code.Rop r1 = r0.getOpcode()
            boolean r1 = r1.isCallLike()
            if (r1 == 0) goto L1c
        L19:
            r8.simulateBranch(r9)
        L1c:
            com.android.dx.rop.code.Rop r1 = r9.getOpcode()
            int r1 = r1.getOpcode()
            com.android.dx.rop.code.RegisterSpec r3 = r9.getResult()
            r4 = 0
            if (r3 != 0) goto L4b
            r3 = 17
            if (r1 == r3) goto L35
            r3 = 18
            if (r1 != r3) goto L34
            goto L35
        L34:
            return
        L35:
            com.android.dx.ssa.SsaBasicBlock r3 = r9.getBlock()
            com.android.dx.ssa.SsaBasicBlock r3 = r3.getPrimarySuccessor()
            java.util.ArrayList r3 = r3.getInsns()
            java.lang.Object r3 = r3.get(r4)
            com.android.dx.ssa.SsaInsn r3 = (com.android.dx.ssa.SsaInsn) r3
            com.android.dx.rop.code.RegisterSpec r3 = r3.getResult()
        L4b:
            int r5 = r3.getReg()
            r6 = 0
            r7 = 2
            if (r1 == r7) goto L80
            r4 = 5
            if (r1 == r4) goto L79
            r0 = 56
            if (r1 == r0) goto L6c
            switch(r1) {
                case 14: goto L61;
                case 15: goto L61;
                case 16: goto L61;
                case 17: goto L61;
                case 18: goto L61;
                default: goto L5d;
            }
        L5d:
            switch(r1) {
                case 20: goto L61;
                case 21: goto L61;
                case 22: goto L61;
                case 23: goto L61;
                case 24: goto L61;
                case 25: goto L61;
                default: goto L60;
            }
        L60:
            goto L9f
        L61:
            int r0 = r3.getBasicType()
            com.android.dx.rop.cst.Constant r6 = r8.simulateMath(r9, r0)
            if (r6 == 0) goto L9f
            goto La0
        L6c:
            int[] r9 = r8.latticeValues
            r0 = r9[r5]
            if (r0 != r2) goto L9f
            r2 = r9[r5]
            com.android.dx.rop.cst.Constant[] r9 = r8.latticeConstants
            r6 = r9[r5]
            goto La0
        L79:
            com.android.dx.rop.code.CstInsn r0 = (com.android.dx.rop.code.CstInsn) r0
            com.android.dx.rop.cst.Constant r6 = r0.getConstant()
            goto La0
        L80:
            com.android.dx.rop.code.RegisterSpecList r0 = r9.getSources()
            int r0 = r0.size()
            if (r0 != r2) goto L9f
            com.android.dx.rop.code.RegisterSpecList r9 = r9.getSources()
            com.android.dx.rop.code.RegisterSpec r9 = r9.get(r4)
            int r9 = r9.getReg()
            int[] r0 = r8.latticeValues
            r2 = r0[r9]
            com.android.dx.rop.cst.Constant[] r0 = r8.latticeConstants
            r6 = r0[r9]
            goto La0
        L9f:
            r2 = r7
        La0:
            boolean r9 = r8.setLatticeValueTo(r5, r2, r6)
            if (r9 == 0) goto La9
            r8.addUsersToWorklist(r5, r2)
        La9:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.dx.ssa.SCCP.simulateStmt(com.android.dx.ssa.SsaInsn):void");
    }

    private void run() {
        addBlockToWorklist(this.ssaMeth.getEntryBlock());
        while (true) {
            if (!this.cfgWorklist.isEmpty() || !this.cfgPhiWorklist.isEmpty() || !this.ssaWorklist.isEmpty() || !this.varyingWorklist.isEmpty()) {
                while (!this.cfgWorklist.isEmpty()) {
                    simulateBlock(this.cfgWorklist.remove(this.cfgWorklist.size() - 1));
                }
                while (!this.cfgPhiWorklist.isEmpty()) {
                    simulatePhiBlock(this.cfgPhiWorklist.remove(this.cfgPhiWorklist.size() - 1));
                }
                while (!this.varyingWorklist.isEmpty()) {
                    SsaInsn remove = this.varyingWorklist.remove(this.varyingWorklist.size() - 1);
                    if (this.executableBlocks.get(remove.getBlock().getIndex())) {
                        if (remove instanceof PhiInsn) {
                            simulatePhi((PhiInsn) remove);
                        } else {
                            simulateStmt(remove);
                        }
                    }
                }
                while (!this.ssaWorklist.isEmpty()) {
                    SsaInsn remove2 = this.ssaWorklist.remove(this.ssaWorklist.size() - 1);
                    if (this.executableBlocks.get(remove2.getBlock().getIndex())) {
                        if (remove2 instanceof PhiInsn) {
                            simulatePhi((PhiInsn) remove2);
                        } else {
                            simulateStmt(remove2);
                        }
                    }
                }
            } else {
                replaceConstants();
                replaceBranches();
                return;
            }
        }
    }

    private void replaceConstants() {
        for (int i = 0; i < this.regCount; i++) {
            if (this.latticeValues[i] == 1 && (this.latticeConstants[i] instanceof TypedConstant)) {
                SsaInsn definitionForRegister = this.ssaMeth.getDefinitionForRegister(i);
                if (!definitionForRegister.getResult().getTypeBearer().isConstant()) {
                    definitionForRegister.setResult(definitionForRegister.getResult().withType((TypedConstant) this.latticeConstants[i]));
                    for (SsaInsn ssaInsn : this.ssaMeth.getUseListForRegister(i)) {
                        if (!ssaInsn.isPhiOrMove()) {
                            NormalSsaInsn normalSsaInsn = (NormalSsaInsn) ssaInsn;
                            RegisterSpecList sources = ssaInsn.getSources();
                            int indexOfRegister = sources.indexOfRegister(i);
                            normalSsaInsn.changeOneSource(indexOfRegister, sources.get(indexOfRegister).withType((TypedConstant) this.latticeConstants[i]));
                        }
                    }
                }
            }
        }
    }

    private void replaceBranches() {
        Iterator<SsaInsn> it = this.branchWorklist.iterator();
        while (it.hasNext()) {
            SsaInsn next = it.next();
            SsaBasicBlock block = next.getBlock();
            int size = block.getSuccessorList().size();
            int i = -1;
            for (int i2 = 0; i2 < size; i2++) {
                int i3 = block.getSuccessorList().get(i2);
                if (!this.executableBlocks.get(i3)) {
                    i = i3;
                }
            }
            if (size == 2 && i != -1) {
                block.replaceLastInsn(new PlainInsn(Rops.GOTO, next.getOriginalRopInsn().getPosition(), (RegisterSpec) null, RegisterSpecList.EMPTY));
                block.removeSuccessor(i);
            }
        }
    }
}

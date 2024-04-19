package com.android.dx.ssa;

import com.android.dx.rop.code.Exceptions;
import com.android.dx.rop.code.FillArrayDataInsn;
import com.android.dx.rop.code.Insn;
import com.android.dx.rop.code.PlainCstInsn;
import com.android.dx.rop.code.PlainInsn;
import com.android.dx.rop.code.RegisterSpec;
import com.android.dx.rop.code.RegisterSpecList;
import com.android.dx.rop.code.Rop;
import com.android.dx.rop.code.Rops;
import com.android.dx.rop.code.ThrowingCstInsn;
import com.android.dx.rop.code.ThrowingInsn;
import com.android.dx.rop.cst.Constant;
import com.android.dx.rop.cst.CstLiteralBits;
import com.android.dx.rop.cst.CstMethodRef;
import com.android.dx.rop.cst.CstNat;
import com.android.dx.rop.cst.CstString;
import com.android.dx.rop.cst.CstType;
import com.android.dx.rop.cst.TypedConstant;
import com.android.dx.rop.cst.Zeroes;
import com.android.dx.rop.type.StdTypeList;
import com.android.dx.rop.type.Type;
import com.android.dx.rop.type.TypeBearer;
import com.android.dx.ssa.SsaBasicBlock;
import com.android.dx.ssa.SsaInsn;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;
/* loaded from: classes.dex */
public class EscapeAnalysis {
    private final ArrayList<EscapeSet> latticeValues = new ArrayList<>();
    private final int regCount;
    private final SsaMethod ssaMeth;

    /* loaded from: classes.dex */
    public enum EscapeState {
        TOP,
        NONE,
        METHOD,
        INTER,
        GLOBAL
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class EscapeSet {
        ArrayList<EscapeSet> childSets;
        EscapeState escape;
        ArrayList<EscapeSet> parentSets;
        BitSet regSet;
        boolean replaceableArray;

        EscapeSet(int i, int i2, EscapeState escapeState) {
            BitSet bitSet = new BitSet(i2);
            this.regSet = bitSet;
            bitSet.set(i);
            this.escape = escapeState;
            this.childSets = new ArrayList<>();
            this.parentSets = new ArrayList<>();
            this.replaceableArray = false;
        }
    }

    private EscapeAnalysis(SsaMethod ssaMethod) {
        this.ssaMeth = ssaMethod;
        this.regCount = ssaMethod.getRegCount();
    }

    private int findSetIndex(RegisterSpec registerSpec) {
        int i = 0;
        while (i < this.latticeValues.size() && !this.latticeValues.get(i).regSet.get(registerSpec.getReg())) {
            i++;
        }
        return i;
    }

    private SsaInsn getInsnForMove(SsaInsn ssaInsn) {
        ArrayList<SsaInsn> insns = this.ssaMeth.getBlocks().get(ssaInsn.getBlock().getPredecessors().nextSetBit(0)).getInsns();
        return insns.get(insns.size() - 1);
    }

    private SsaInsn getMoveForInsn(SsaInsn ssaInsn) {
        return this.ssaMeth.getBlocks().get(ssaInsn.getBlock().getSuccessors().nextSetBit(0)).getInsns().get(0);
    }

    private void addEdge(EscapeSet escapeSet, EscapeSet escapeSet2) {
        if (!escapeSet2.parentSets.contains(escapeSet)) {
            escapeSet2.parentSets.add(escapeSet);
        }
        if (escapeSet.childSets.contains(escapeSet2)) {
            return;
        }
        escapeSet.childSets.add(escapeSet2);
    }

    private void replaceNode(EscapeSet escapeSet, EscapeSet escapeSet2) {
        Iterator<EscapeSet> it = escapeSet2.parentSets.iterator();
        while (it.hasNext()) {
            EscapeSet next = it.next();
            next.childSets.remove(escapeSet2);
            next.childSets.add(escapeSet);
            escapeSet.parentSets.add(next);
        }
        Iterator<EscapeSet> it2 = escapeSet2.childSets.iterator();
        while (it2.hasNext()) {
            EscapeSet next2 = it2.next();
            next2.parentSets.remove(escapeSet2);
            next2.parentSets.add(escapeSet);
            escapeSet.childSets.add(next2);
        }
    }

    public static void process(SsaMethod ssaMethod) {
        new EscapeAnalysis(ssaMethod).run();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void processInsn(SsaInsn ssaInsn) {
        int opcode = ssaInsn.getOpcode().getOpcode();
        RegisterSpec result = ssaInsn.getResult();
        if (opcode == 56 && result.getTypeBearer().getBasicType() == 9) {
            processRegister(result, processMoveResultPseudoInsn(ssaInsn));
        } else if (opcode == 3 && result.getTypeBearer().getBasicType() == 9) {
            EscapeSet escapeSet = new EscapeSet(result.getReg(), this.regCount, EscapeState.NONE);
            this.latticeValues.add(escapeSet);
            processRegister(result, escapeSet);
        } else if (opcode == 55 && result.getTypeBearer().getBasicType() == 9) {
            EscapeSet escapeSet2 = new EscapeSet(result.getReg(), this.regCount, EscapeState.NONE);
            this.latticeValues.add(escapeSet2);
            processRegister(result, escapeSet2);
        }
    }

    private EscapeSet processMoveResultPseudoInsn(SsaInsn ssaInsn) {
        EscapeSet escapeSet;
        RegisterSpec result = ssaInsn.getResult();
        SsaInsn insnForMove = getInsnForMove(ssaInsn);
        int opcode = insnForMove.getOpcode().getOpcode();
        if (opcode != 5) {
            if (opcode != 38 && opcode != 45) {
                if (opcode != 46) {
                    switch (opcode) {
                        case 40:
                            break;
                        case 41:
                        case 42:
                            if (insnForMove.getSources().get(0).getTypeBearer().isConstant()) {
                                escapeSet = new EscapeSet(result.getReg(), this.regCount, EscapeState.NONE);
                                escapeSet.replaceableArray = true;
                                break;
                            } else {
                                escapeSet = new EscapeSet(result.getReg(), this.regCount, EscapeState.GLOBAL);
                                break;
                            }
                        case 43:
                            break;
                        default:
                            return null;
                    }
                } else {
                    escapeSet = new EscapeSet(result.getReg(), this.regCount, EscapeState.GLOBAL);
                }
                this.latticeValues.add(escapeSet);
                return escapeSet;
            }
            RegisterSpec registerSpec = insnForMove.getSources().get(0);
            int findSetIndex = findSetIndex(registerSpec);
            if (findSetIndex != this.latticeValues.size()) {
                EscapeSet escapeSet2 = this.latticeValues.get(findSetIndex);
                escapeSet2.regSet.set(result.getReg());
                return escapeSet2;
            }
            if (registerSpec.getType() == Type.KNOWN_NULL) {
                escapeSet = new EscapeSet(result.getReg(), this.regCount, EscapeState.NONE);
            } else {
                escapeSet = new EscapeSet(result.getReg(), this.regCount, EscapeState.GLOBAL);
            }
            this.latticeValues.add(escapeSet);
            return escapeSet;
        }
        escapeSet = new EscapeSet(result.getReg(), this.regCount, EscapeState.NONE);
        this.latticeValues.add(escapeSet);
        return escapeSet;
    }

    private void processRegister(RegisterSpec registerSpec, EscapeSet escapeSet) {
        ArrayList<RegisterSpec> arrayList = new ArrayList<>();
        arrayList.add(registerSpec);
        while (!arrayList.isEmpty()) {
            RegisterSpec remove = arrayList.remove(arrayList.size() - 1);
            for (SsaInsn ssaInsn : this.ssaMeth.getUseListForRegister(remove.getReg())) {
                if (ssaInsn.getOpcode() == null) {
                    processPhiUse(ssaInsn, escapeSet, arrayList);
                } else {
                    processUse(remove, ssaInsn, escapeSet, arrayList);
                }
            }
        }
    }

    private void processPhiUse(SsaInsn ssaInsn, EscapeSet escapeSet, ArrayList<RegisterSpec> arrayList) {
        int findSetIndex = findSetIndex(ssaInsn.getResult());
        if (findSetIndex != this.latticeValues.size()) {
            EscapeSet escapeSet2 = this.latticeValues.get(findSetIndex);
            if (escapeSet2 != escapeSet) {
                escapeSet.replaceableArray = false;
                escapeSet.regSet.or(escapeSet2.regSet);
                if (escapeSet.escape.compareTo(escapeSet2.escape) < 0) {
                    escapeSet.escape = escapeSet2.escape;
                }
                replaceNode(escapeSet, escapeSet2);
                this.latticeValues.remove(findSetIndex);
                return;
            }
            return;
        }
        escapeSet.regSet.set(ssaInsn.getResult().getReg());
        arrayList.add(ssaInsn.getResult());
    }

    private void processUse(RegisterSpec registerSpec, SsaInsn ssaInsn, EscapeSet escapeSet, ArrayList<RegisterSpec> arrayList) {
        int opcode = ssaInsn.getOpcode().getOpcode();
        if (opcode == 2) {
            escapeSet.regSet.set(ssaInsn.getResult().getReg());
            arrayList.add(ssaInsn.getResult());
            return;
        }
        if (opcode != 33 && opcode != 35) {
            if (opcode == 43 || opcode == 7 || opcode == 8) {
                if (escapeSet.escape.compareTo(EscapeState.METHOD) < 0) {
                    escapeSet.escape = EscapeState.METHOD;
                    return;
                }
                return;
            } else if (opcode != 38) {
                if (opcode == 39) {
                    if (!ssaInsn.getSources().get(2).getTypeBearer().isConstant()) {
                        escapeSet.replaceableArray = false;
                    }
                } else {
                    switch (opcode) {
                        case 47:
                            break;
                        case 48:
                            escapeSet.escape = EscapeState.GLOBAL;
                            return;
                        case 49:
                        case 50:
                        case 51:
                        case 52:
                        case 53:
                            break;
                        default:
                            return;
                    }
                }
                if (ssaInsn.getSources().get(0).getTypeBearer().getBasicType() != 9) {
                    return;
                }
                escapeSet.replaceableArray = false;
                RegisterSpecList sources = ssaInsn.getSources();
                if (sources.get(0).getReg() == registerSpec.getReg()) {
                    int findSetIndex = findSetIndex(sources.get(1));
                    if (findSetIndex != this.latticeValues.size()) {
                        EscapeSet escapeSet2 = this.latticeValues.get(findSetIndex);
                        addEdge(escapeSet2, escapeSet);
                        if (escapeSet.escape.compareTo(escapeSet2.escape) < 0) {
                            escapeSet.escape = escapeSet2.escape;
                            return;
                        }
                        return;
                    }
                    return;
                }
                int findSetIndex2 = findSetIndex(sources.get(0));
                if (findSetIndex2 != this.latticeValues.size()) {
                    EscapeSet escapeSet3 = this.latticeValues.get(findSetIndex2);
                    addEdge(escapeSet, escapeSet3);
                    if (escapeSet3.escape.compareTo(escapeSet.escape) < 0) {
                        escapeSet3.escape = escapeSet.escape;
                        return;
                    }
                    return;
                }
                return;
            } else if (ssaInsn.getSources().get(1).getTypeBearer().isConstant()) {
                return;
            } else {
                escapeSet.replaceableArray = false;
                return;
            }
        }
        escapeSet.escape = EscapeState.INTER;
    }

    private void scalarReplacement() {
        Iterator<EscapeSet> it = this.latticeValues.iterator();
        while (it.hasNext()) {
            EscapeSet next = it.next();
            if (next.replaceableArray && next.escape == EscapeState.NONE) {
                int nextSetBit = next.regSet.nextSetBit(0);
                SsaInsn definitionForRegister = this.ssaMeth.getDefinitionForRegister(nextSetBit);
                SsaInsn insnForMove = getInsnForMove(definitionForRegister);
                int intBits = ((CstLiteralBits) insnForMove.getSources().get(0).getTypeBearer()).getIntBits();
                ArrayList<RegisterSpec> arrayList = new ArrayList<>(intBits);
                HashSet<SsaInsn> hashSet = new HashSet<>();
                replaceDef(definitionForRegister, insnForMove, intBits, arrayList);
                hashSet.add(insnForMove);
                hashSet.add(definitionForRegister);
                for (SsaInsn ssaInsn : this.ssaMeth.getUseListForRegister(nextSetBit)) {
                    replaceUse(ssaInsn, insnForMove, arrayList, hashSet);
                    hashSet.add(ssaInsn);
                }
                this.ssaMeth.deleteInsns(hashSet);
                this.ssaMeth.onInsnsChanged();
                SsaConverter.updateSsaMethod(this.ssaMeth, this.regCount);
                movePropagate();
            }
        }
    }

    private void replaceDef(SsaInsn ssaInsn, SsaInsn ssaInsn2, int i, ArrayList<RegisterSpec> arrayList) {
        Type type = ssaInsn.getResult().getType();
        for (int i2 = 0; i2 < i; i2++) {
            Constant zeroFor = Zeroes.zeroFor(type.getComponentType());
            RegisterSpec make = RegisterSpec.make(this.ssaMeth.makeNewSsaReg(), (TypedConstant) zeroFor);
            arrayList.add(make);
            insertPlainInsnBefore(ssaInsn, RegisterSpecList.EMPTY, make, 5, zeroFor);
        }
    }

    private void replaceUse(SsaInsn ssaInsn, SsaInsn ssaInsn2, ArrayList<RegisterSpec> arrayList, HashSet<SsaInsn> hashSet) {
        int size = arrayList.size();
        int opcode = ssaInsn.getOpcode().getOpcode();
        if (opcode == 34) {
            TypeBearer typeBearer = ssaInsn2.getSources().get(0).getTypeBearer();
            SsaInsn moveForInsn = getMoveForInsn(ssaInsn);
            insertPlainInsnBefore(moveForInsn, RegisterSpecList.EMPTY, moveForInsn.getResult(), 5, (Constant) typeBearer);
            hashSet.add(moveForInsn);
        } else if (opcode == 57) {
            ArrayList<Constant> initValues = ((FillArrayDataInsn) ssaInsn.getOriginalRopInsn()).getInitValues();
            for (int i = 0; i < size; i++) {
                RegisterSpec make = RegisterSpec.make(arrayList.get(i).getReg(), (TypeBearer) initValues.get(i));
                insertPlainInsnBefore(ssaInsn, RegisterSpecList.EMPTY, make, 5, initValues.get(i));
                arrayList.set(i, make);
            }
        } else if (opcode == 38) {
            SsaInsn moveForInsn2 = getMoveForInsn(ssaInsn);
            RegisterSpecList sources = ssaInsn.getSources();
            int intBits = ((CstLiteralBits) sources.get(1).getTypeBearer()).getIntBits();
            if (intBits < size) {
                RegisterSpec registerSpec = arrayList.get(intBits);
                insertPlainInsnBefore(moveForInsn2, RegisterSpecList.make(registerSpec), registerSpec.withReg(moveForInsn2.getResult().getReg()), 2, null);
            } else {
                insertExceptionThrow(moveForInsn2, sources.get(1), hashSet);
                hashSet.add(moveForInsn2.getBlock().getInsns().get(2));
            }
            hashSet.add(moveForInsn2);
        } else if (opcode == 39) {
            RegisterSpecList sources2 = ssaInsn.getSources();
            int intBits2 = ((CstLiteralBits) sources2.get(2).getTypeBearer()).getIntBits();
            if (intBits2 < size) {
                RegisterSpec registerSpec2 = sources2.get(0);
                RegisterSpec withReg = registerSpec2.withReg(arrayList.get(intBits2).getReg());
                insertPlainInsnBefore(ssaInsn, RegisterSpecList.make(registerSpec2), withReg, 2, null);
                arrayList.set(intBits2, withReg.withSimpleType());
                return;
            }
            insertExceptionThrow(ssaInsn, sources2.get(2), hashSet);
        }
    }

    private void movePropagate() {
        for (int i = 0; i < this.ssaMeth.getRegCount(); i++) {
            SsaInsn definitionForRegister = this.ssaMeth.getDefinitionForRegister(i);
            if (definitionForRegister != null && definitionForRegister.getOpcode() != null && definitionForRegister.getOpcode().getOpcode() == 2) {
                ArrayList<SsaInsn>[] useListCopy = this.ssaMeth.getUseListCopy();
                final RegisterSpec registerSpec = definitionForRegister.getSources().get(0);
                final RegisterSpec result = definitionForRegister.getResult();
                if (registerSpec.getReg() >= this.regCount || result.getReg() >= this.regCount) {
                    RegisterMapper registerMapper = new RegisterMapper() { // from class: com.android.dx.ssa.EscapeAnalysis.1
                        @Override // com.android.dx.ssa.RegisterMapper
                        public int getNewRegisterCount() {
                            return EscapeAnalysis.this.ssaMeth.getRegCount();
                        }

                        @Override // com.android.dx.ssa.RegisterMapper
                        public RegisterSpec map(RegisterSpec registerSpec2) {
                            return registerSpec2.getReg() == result.getReg() ? registerSpec : registerSpec2;
                        }
                    };
                    Iterator<SsaInsn> it = useListCopy[result.getReg()].iterator();
                    while (it.hasNext()) {
                        it.next().mapSourceRegisters(registerMapper);
                    }
                }
            }
        }
    }

    private void run() {
        this.ssaMeth.forEachBlockDepthFirstDom(new SsaBasicBlock.Visitor() { // from class: com.android.dx.ssa.EscapeAnalysis.2
            @Override // com.android.dx.ssa.SsaBasicBlock.Visitor
            public void visitBlock(SsaBasicBlock ssaBasicBlock, SsaBasicBlock ssaBasicBlock2) {
                ssaBasicBlock.forEachInsn(new SsaInsn.Visitor() { // from class: com.android.dx.ssa.EscapeAnalysis.2.1
                    @Override // com.android.dx.ssa.SsaInsn.Visitor
                    public void visitMoveInsn(NormalSsaInsn normalSsaInsn) {
                    }

                    @Override // com.android.dx.ssa.SsaInsn.Visitor
                    public void visitPhiInsn(PhiInsn phiInsn) {
                    }

                    @Override // com.android.dx.ssa.SsaInsn.Visitor
                    public void visitNonMoveInsn(NormalSsaInsn normalSsaInsn) {
                        EscapeAnalysis.this.processInsn(normalSsaInsn);
                    }
                });
            }
        });
        Iterator<EscapeSet> it = this.latticeValues.iterator();
        while (it.hasNext()) {
            EscapeSet next = it.next();
            if (next.escape != EscapeState.NONE) {
                Iterator<EscapeSet> it2 = next.childSets.iterator();
                while (it2.hasNext()) {
                    EscapeSet next2 = it2.next();
                    if (next.escape.compareTo(next2.escape) > 0) {
                        next2.escape = next.escape;
                    }
                }
            }
        }
        scalarReplacement();
    }

    private void insertExceptionThrow(SsaInsn ssaInsn, RegisterSpec registerSpec, HashSet<SsaInsn> hashSet) {
        CstType cstType = new CstType(Exceptions.TYPE_ArrayIndexOutOfBoundsException);
        insertThrowingInsnBefore(ssaInsn, RegisterSpecList.EMPTY, null, 40, cstType);
        SsaBasicBlock block = ssaInsn.getBlock();
        SsaBasicBlock insertNewSuccessor = block.insertNewSuccessor(block.getPrimarySuccessor());
        SsaInsn ssaInsn2 = insertNewSuccessor.getInsns().get(0);
        RegisterSpec make = RegisterSpec.make(this.ssaMeth.makeNewSsaReg(), cstType);
        insertPlainInsnBefore(ssaInsn2, RegisterSpecList.EMPTY, make, 56, null);
        SsaBasicBlock insertNewSuccessor2 = insertNewSuccessor.insertNewSuccessor(insertNewSuccessor.getPrimarySuccessor());
        SsaInsn ssaInsn3 = insertNewSuccessor2.getInsns().get(0);
        insertThrowingInsnBefore(ssaInsn3, RegisterSpecList.make(make, registerSpec), null, 52, new CstMethodRef(cstType, new CstNat(new CstString("<init>"), new CstString("(I)V"))));
        hashSet.add(ssaInsn3);
        SsaBasicBlock insertNewSuccessor3 = insertNewSuccessor2.insertNewSuccessor(insertNewSuccessor2.getPrimarySuccessor());
        SsaInsn ssaInsn4 = insertNewSuccessor3.getInsns().get(0);
        insertThrowingInsnBefore(ssaInsn4, RegisterSpecList.make(make), null, 35, null);
        insertNewSuccessor3.replaceSuccessor(insertNewSuccessor3.getPrimarySuccessorIndex(), this.ssaMeth.getExitBlock().getIndex());
        hashSet.add(ssaInsn4);
    }

    private void insertPlainInsnBefore(SsaInsn ssaInsn, RegisterSpecList registerSpecList, RegisterSpec registerSpec, int i, Constant constant) {
        Rop ropFor;
        Insn plainCstInsn;
        Insn originalRopInsn = ssaInsn.getOriginalRopInsn();
        if (i == 56) {
            ropFor = Rops.opMoveResultPseudo(registerSpec.getType());
        } else {
            ropFor = Rops.ropFor(i, registerSpec, registerSpecList, constant);
        }
        Rop rop = ropFor;
        if (constant == null) {
            plainCstInsn = new PlainInsn(rop, originalRopInsn.getPosition(), registerSpec, registerSpecList);
        } else {
            plainCstInsn = new PlainCstInsn(rop, originalRopInsn.getPosition(), registerSpec, registerSpecList, constant);
        }
        NormalSsaInsn normalSsaInsn = new NormalSsaInsn(plainCstInsn, ssaInsn.getBlock());
        ArrayList<SsaInsn> insns = ssaInsn.getBlock().getInsns();
        insns.add(insns.lastIndexOf(ssaInsn), normalSsaInsn);
        this.ssaMeth.onInsnAdded(normalSsaInsn);
    }

    private void insertThrowingInsnBefore(SsaInsn ssaInsn, RegisterSpecList registerSpecList, RegisterSpec registerSpec, int i, Constant constant) {
        Insn throwingCstInsn;
        Insn originalRopInsn = ssaInsn.getOriginalRopInsn();
        Rop ropFor = Rops.ropFor(i, registerSpec, registerSpecList, constant);
        if (constant == null) {
            throwingCstInsn = new ThrowingInsn(ropFor, originalRopInsn.getPosition(), registerSpecList, StdTypeList.EMPTY);
        } else {
            throwingCstInsn = new ThrowingCstInsn(ropFor, originalRopInsn.getPosition(), registerSpecList, StdTypeList.EMPTY, constant);
        }
        NormalSsaInsn normalSsaInsn = new NormalSsaInsn(throwingCstInsn, ssaInsn.getBlock());
        ArrayList<SsaInsn> insns = ssaInsn.getBlock().getInsns();
        insns.add(insns.lastIndexOf(ssaInsn), normalSsaInsn);
        this.ssaMeth.onInsnAdded(normalSsaInsn);
    }
}

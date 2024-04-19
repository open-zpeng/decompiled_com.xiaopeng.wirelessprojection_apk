package com.android.dx.ssa.back;

import com.android.dx.rop.code.CstInsn;
import com.android.dx.rop.code.LocalItem;
import com.android.dx.rop.code.RegisterSpec;
import com.android.dx.rop.code.RegisterSpecList;
import com.android.dx.rop.code.Rop;
import com.android.dx.rop.cst.CstInteger;
import com.android.dx.ssa.InterferenceRegisterMapper;
import com.android.dx.ssa.NormalSsaInsn;
import com.android.dx.ssa.Optimizer;
import com.android.dx.ssa.PhiInsn;
import com.android.dx.ssa.RegisterMapper;
import com.android.dx.ssa.SsaInsn;
import com.android.dx.ssa.SsaMethod;
import com.android.dx.util.IntIterator;
import com.android.dx.util.IntSet;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
/* loaded from: classes.dex */
public class FirstFitLocalCombiningAllocator extends RegisterAllocator {
    private static final boolean DEBUG = false;
    private final ArrayList<NormalSsaInsn> invokeRangeInsns;
    private final Map<LocalItem, ArrayList<RegisterSpec>> localVariables;
    private final InterferenceRegisterMapper mapper;
    private final boolean minimizeRegisters;
    private final ArrayList<NormalSsaInsn> moveResultPseudoInsns;
    private final int paramRangeEnd;
    private final ArrayList<PhiInsn> phiInsns;
    private final BitSet reservedRopRegs;
    private final BitSet ssaRegsMapped;
    private final BitSet usedRopRegs;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public enum Alignment {
        EVEN { // from class: com.android.dx.ssa.back.FirstFitLocalCombiningAllocator.Alignment.1
            @Override // com.android.dx.ssa.back.FirstFitLocalCombiningAllocator.Alignment
            int nextClearBit(BitSet bitSet, int i) {
                int nextClearBit = bitSet.nextClearBit(i);
                while (!FirstFitLocalCombiningAllocator.isEven(nextClearBit)) {
                    nextClearBit = bitSet.nextClearBit(nextClearBit + 1);
                }
                return nextClearBit;
            }
        },
        ODD { // from class: com.android.dx.ssa.back.FirstFitLocalCombiningAllocator.Alignment.2
            @Override // com.android.dx.ssa.back.FirstFitLocalCombiningAllocator.Alignment
            int nextClearBit(BitSet bitSet, int i) {
                int nextClearBit = bitSet.nextClearBit(i);
                while (FirstFitLocalCombiningAllocator.isEven(nextClearBit)) {
                    nextClearBit = bitSet.nextClearBit(nextClearBit + 1);
                }
                return nextClearBit;
            }
        },
        UNSPECIFIED { // from class: com.android.dx.ssa.back.FirstFitLocalCombiningAllocator.Alignment.3
            @Override // com.android.dx.ssa.back.FirstFitLocalCombiningAllocator.Alignment
            int nextClearBit(BitSet bitSet, int i) {
                return bitSet.nextClearBit(i);
            }
        };

        abstract int nextClearBit(BitSet bitSet, int i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isEven(int i) {
        return (i & 1) == 0;
    }

    @Override // com.android.dx.ssa.back.RegisterAllocator
    public boolean wantsParamsMovedHigh() {
        return true;
    }

    public FirstFitLocalCombiningAllocator(SsaMethod ssaMethod, InterferenceGraph interferenceGraph, boolean z) {
        super(ssaMethod, interferenceGraph);
        this.ssaRegsMapped = new BitSet(ssaMethod.getRegCount());
        this.mapper = new InterferenceRegisterMapper(interferenceGraph, ssaMethod.getRegCount());
        this.minimizeRegisters = z;
        int paramWidth = ssaMethod.getParamWidth();
        this.paramRangeEnd = paramWidth;
        BitSet bitSet = new BitSet(paramWidth * 2);
        this.reservedRopRegs = bitSet;
        bitSet.set(0, paramWidth);
        this.usedRopRegs = new BitSet(paramWidth * 2);
        this.localVariables = new TreeMap();
        this.moveResultPseudoInsns = new ArrayList<>();
        this.invokeRangeInsns = new ArrayList<>();
        this.phiInsns = new ArrayList<>();
    }

    @Override // com.android.dx.ssa.back.RegisterAllocator
    public RegisterMapper allocateRegisters() {
        analyzeInstructions();
        handleLocalAssociatedParams();
        handleUnassociatedParameters();
        handleInvokeRangeInsns();
        handleLocalAssociatedOther();
        handleCheckCastResults();
        handlePhiInsns();
        handleNormalUnassociated();
        return this.mapper;
    }

    private void printLocalVars() {
        System.out.println("Printing local vars");
        for (Map.Entry<LocalItem, ArrayList<RegisterSpec>> entry : this.localVariables.entrySet()) {
            StringBuilder sb = new StringBuilder();
            sb.append('{');
            sb.append(' ');
            Iterator<RegisterSpec> it = entry.getValue().iterator();
            while (it.hasNext()) {
                sb.append('v');
                sb.append(it.next().getReg());
                sb.append(' ');
            }
            sb.append('}');
            System.out.printf("Local: %s Registers: %s\n", entry.getKey(), sb);
        }
    }

    private void handleLocalAssociatedParams() {
        for (ArrayList<RegisterSpec> arrayList : this.localVariables.values()) {
            int size = arrayList.size();
            int i = -1;
            int i2 = 0;
            int i3 = 0;
            while (true) {
                if (i3 >= size) {
                    break;
                }
                RegisterSpec registerSpec = arrayList.get(i3);
                int parameterIndexForReg = getParameterIndexForReg(registerSpec.getReg());
                if (parameterIndexForReg >= 0) {
                    i2 = registerSpec.getCategory();
                    addMapping(registerSpec, parameterIndexForReg);
                    i = parameterIndexForReg;
                    break;
                }
                i3++;
                i = parameterIndexForReg;
            }
            if (i >= 0) {
                tryMapRegs(arrayList, i, i2, true);
            }
        }
    }

    private int getParameterIndexForReg(int i) {
        Rop opcode;
        SsaInsn definitionForRegister = this.ssaMeth.getDefinitionForRegister(i);
        if (definitionForRegister == null || (opcode = definitionForRegister.getOpcode()) == null || opcode.getOpcode() != 3) {
            return -1;
        }
        return ((CstInteger) ((CstInsn) definitionForRegister.getOriginalRopInsn()).getConstant()).getValue();
    }

    private void handleLocalAssociatedOther() {
        for (ArrayList<RegisterSpec> arrayList : this.localVariables.values()) {
            int i = this.paramRangeEnd;
            boolean z = false;
            do {
                int size = arrayList.size();
                int i2 = 1;
                for (int i3 = 0; i3 < size; i3++) {
                    RegisterSpec registerSpec = arrayList.get(i3);
                    int category = registerSpec.getCategory();
                    if (!this.ssaRegsMapped.get(registerSpec.getReg()) && category > i2) {
                        i2 = category;
                    }
                }
                int findRopRegForLocal = findRopRegForLocal(i, i2);
                if (canMapRegs(arrayList, findRopRegForLocal)) {
                    z = tryMapRegs(arrayList, findRopRegForLocal, i2, true);
                }
                i = findRopRegForLocal + 1;
            } while (!z);
        }
    }

    private boolean tryMapRegs(ArrayList<RegisterSpec> arrayList, int i, int i2, boolean z) {
        Iterator<RegisterSpec> it = arrayList.iterator();
        boolean z2 = false;
        while (it.hasNext()) {
            RegisterSpec next = it.next();
            if (!this.ssaRegsMapped.get(next.getReg())) {
                boolean tryMapReg = tryMapReg(next, i, i2);
                z2 = !tryMapReg || z2;
                if (tryMapReg && z) {
                    markReserved(i, next.getCategory());
                }
            }
        }
        return !z2;
    }

    private boolean tryMapReg(RegisterSpec registerSpec, int i, int i2) {
        if (registerSpec.getCategory() > i2 || this.ssaRegsMapped.get(registerSpec.getReg()) || !canMapReg(registerSpec, i)) {
            return false;
        }
        addMapping(registerSpec, i);
        return true;
    }

    private void markReserved(int i, int i2) {
        this.reservedRopRegs.set(i, i2 + i, true);
    }

    private boolean rangeContainsReserved(int i, int i2) {
        for (int i3 = i; i3 < i + i2; i3++) {
            if (this.reservedRopRegs.get(i3)) {
                return true;
            }
        }
        return false;
    }

    private boolean isThisPointerReg(int i) {
        return i == 0 && !this.ssaMeth.isStatic();
    }

    private Alignment getAlignment(int i) {
        Alignment alignment = Alignment.UNSPECIFIED;
        if (i == 2) {
            if (isEven(this.paramRangeEnd)) {
                return Alignment.EVEN;
            }
            return Alignment.ODD;
        }
        return alignment;
    }

    private int findNextUnreservedRopReg(int i, int i2) {
        return findNextUnreservedRopReg(i, i2, getAlignment(i2));
    }

    private int findNextUnreservedRopReg(int i, int i2, Alignment alignment) {
        int nextClearBit = alignment.nextClearBit(this.reservedRopRegs, i);
        while (true) {
            int i3 = 1;
            while (i3 < i2 && !this.reservedRopRegs.get(nextClearBit + i3)) {
                i3++;
            }
            if (i3 == i2) {
                return nextClearBit;
            }
            nextClearBit = alignment.nextClearBit(this.reservedRopRegs, nextClearBit + i3);
        }
    }

    private int findRopRegForLocal(int i, int i2) {
        Alignment alignment = getAlignment(i2);
        int nextClearBit = alignment.nextClearBit(this.usedRopRegs, i);
        while (true) {
            int i3 = 1;
            while (i3 < i2 && !this.usedRopRegs.get(nextClearBit + i3)) {
                i3++;
            }
            if (i3 == i2) {
                return nextClearBit;
            }
            nextClearBit = alignment.nextClearBit(this.usedRopRegs, nextClearBit + i3);
        }
    }

    private void handleUnassociatedParameters() {
        int regCount = this.ssaMeth.getRegCount();
        for (int i = 0; i < regCount; i++) {
            if (!this.ssaRegsMapped.get(i)) {
                int parameterIndexForReg = getParameterIndexForReg(i);
                RegisterSpec definitionSpecForSsaReg = getDefinitionSpecForSsaReg(i);
                if (parameterIndexForReg >= 0) {
                    addMapping(definitionSpecForSsaReg, parameterIndexForReg);
                }
            }
        }
    }

    private void handleInvokeRangeInsns() {
        Iterator<NormalSsaInsn> it = this.invokeRangeInsns.iterator();
        while (it.hasNext()) {
            adjustAndMapSourceRangeRange(it.next());
        }
    }

    private void handleCheckCastResults() {
        Iterator<NormalSsaInsn> it = this.moveResultPseudoInsns.iterator();
        while (it.hasNext()) {
            NormalSsaInsn next = it.next();
            RegisterSpec result = next.getResult();
            int reg = result.getReg();
            BitSet predecessors = next.getBlock().getPredecessors();
            if (predecessors.cardinality() == 1) {
                ArrayList<SsaInsn> insns = this.ssaMeth.getBlocks().get(predecessors.nextSetBit(0)).getInsns();
                SsaInsn ssaInsn = insns.get(insns.size() - 1);
                if (ssaInsn.getOpcode().getOpcode() == 43) {
                    RegisterSpec registerSpec = ssaInsn.getSources().get(0);
                    int reg2 = registerSpec.getReg();
                    int category = registerSpec.getCategory();
                    boolean z = this.ssaRegsMapped.get(reg);
                    boolean z2 = this.ssaRegsMapped.get(reg2);
                    if ((!z2) & z) {
                        z2 = tryMapReg(registerSpec, this.mapper.oldToNew(reg), category);
                    }
                    if ((!z) & z2) {
                        z = tryMapReg(result, this.mapper.oldToNew(reg2), category);
                    }
                    if (!z || !z2) {
                        int findNextUnreservedRopReg = findNextUnreservedRopReg(this.paramRangeEnd, category);
                        ArrayList<RegisterSpec> arrayList = new ArrayList<>(2);
                        arrayList.add(result);
                        arrayList.add(registerSpec);
                        while (!tryMapRegs(arrayList, findNextUnreservedRopReg, category, false)) {
                            findNextUnreservedRopReg = findNextUnreservedRopReg(findNextUnreservedRopReg + 1, category);
                        }
                    }
                    boolean z3 = ssaInsn.getOriginalRopInsn().getCatches().size() != 0;
                    int oldToNew = this.mapper.oldToNew(reg);
                    if (oldToNew != this.mapper.oldToNew(reg2) && !z3) {
                        ((NormalSsaInsn) ssaInsn).changeOneSource(0, insertMoveBefore(ssaInsn, registerSpec));
                        addMapping(ssaInsn.getSources().get(0), oldToNew);
                    }
                }
            }
        }
    }

    private void handlePhiInsns() {
        Iterator<PhiInsn> it = this.phiInsns.iterator();
        while (it.hasNext()) {
            processPhiInsn(it.next());
        }
    }

    private void handleNormalUnassociated() {
        RegisterSpec definitionSpecForSsaReg;
        int regCount = this.ssaMeth.getRegCount();
        for (int i = 0; i < regCount; i++) {
            if (!this.ssaRegsMapped.get(i) && (definitionSpecForSsaReg = getDefinitionSpecForSsaReg(i)) != null) {
                int category = definitionSpecForSsaReg.getCategory();
                int findNextUnreservedRopReg = findNextUnreservedRopReg(this.paramRangeEnd, category);
                while (!canMapReg(definitionSpecForSsaReg, findNextUnreservedRopReg)) {
                    findNextUnreservedRopReg = findNextUnreservedRopReg(findNextUnreservedRopReg + 1, category);
                }
                addMapping(definitionSpecForSsaReg, findNextUnreservedRopReg);
            }
        }
    }

    private boolean canMapRegs(ArrayList<RegisterSpec> arrayList, int i) {
        Iterator<RegisterSpec> it = arrayList.iterator();
        while (it.hasNext()) {
            RegisterSpec next = it.next();
            if (!this.ssaRegsMapped.get(next.getReg()) && !canMapReg(next, i)) {
                return false;
            }
        }
        return true;
    }

    private boolean canMapReg(RegisterSpec registerSpec, int i) {
        return (spansParamRange(i, registerSpec.getCategory()) || this.mapper.interferes(registerSpec, i)) ? false : true;
    }

    private boolean spansParamRange(int i, int i2) {
        int i3 = this.paramRangeEnd;
        return i < i3 && i + i2 > i3;
    }

    private void analyzeInstructions() {
        this.ssaMeth.forEachInsn(new SsaInsn.Visitor() { // from class: com.android.dx.ssa.back.FirstFitLocalCombiningAllocator.1
            @Override // com.android.dx.ssa.SsaInsn.Visitor
            public void visitMoveInsn(NormalSsaInsn normalSsaInsn) {
                processInsn(normalSsaInsn);
            }

            @Override // com.android.dx.ssa.SsaInsn.Visitor
            public void visitPhiInsn(PhiInsn phiInsn) {
                processInsn(phiInsn);
            }

            @Override // com.android.dx.ssa.SsaInsn.Visitor
            public void visitNonMoveInsn(NormalSsaInsn normalSsaInsn) {
                processInsn(normalSsaInsn);
            }

            private void processInsn(SsaInsn ssaInsn) {
                RegisterSpec localAssignment = ssaInsn.getLocalAssignment();
                if (localAssignment != null) {
                    LocalItem localItem = localAssignment.getLocalItem();
                    ArrayList arrayList = (ArrayList) FirstFitLocalCombiningAllocator.this.localVariables.get(localItem);
                    if (arrayList == null) {
                        arrayList = new ArrayList();
                        FirstFitLocalCombiningAllocator.this.localVariables.put(localItem, arrayList);
                    }
                    arrayList.add(localAssignment);
                }
                if (ssaInsn instanceof NormalSsaInsn) {
                    if (ssaInsn.getOpcode().getOpcode() == 56) {
                        FirstFitLocalCombiningAllocator.this.moveResultPseudoInsns.add((NormalSsaInsn) ssaInsn);
                    } else if (Optimizer.getAdvice().requiresSourcesInOrder(ssaInsn.getOriginalRopInsn().getOpcode(), ssaInsn.getSources())) {
                        FirstFitLocalCombiningAllocator.this.invokeRangeInsns.add((NormalSsaInsn) ssaInsn);
                    }
                } else if (ssaInsn instanceof PhiInsn) {
                    FirstFitLocalCombiningAllocator.this.phiInsns.add((PhiInsn) ssaInsn);
                }
            }
        });
    }

    private void addMapping(RegisterSpec registerSpec, int i) {
        int reg = registerSpec.getReg();
        if (this.ssaRegsMapped.get(reg) || !canMapReg(registerSpec, i)) {
            throw new RuntimeException("attempt to add invalid register mapping");
        }
        int category = registerSpec.getCategory();
        this.mapper.addMapping(registerSpec.getReg(), i, category);
        this.ssaRegsMapped.set(reg);
        this.usedRopRegs.set(i, category + i);
    }

    private void adjustAndMapSourceRangeRange(NormalSsaInsn normalSsaInsn) {
        int findRangeAndAdjust = findRangeAndAdjust(normalSsaInsn);
        RegisterSpecList sources = normalSsaInsn.getSources();
        int size = sources.size();
        int i = 0;
        while (i < size) {
            RegisterSpec registerSpec = sources.get(i);
            int reg = registerSpec.getReg();
            int category = registerSpec.getCategory();
            int i2 = findRangeAndAdjust + category;
            if (!this.ssaRegsMapped.get(reg)) {
                LocalItem localItemForReg = getLocalItemForReg(reg);
                addMapping(registerSpec, findRangeAndAdjust);
                if (localItemForReg != null) {
                    markReserved(findRangeAndAdjust, category);
                    ArrayList<RegisterSpec> arrayList = this.localVariables.get(localItemForReg);
                    int size2 = arrayList.size();
                    for (int i3 = 0; i3 < size2; i3++) {
                        RegisterSpec registerSpec2 = arrayList.get(i3);
                        if (-1 == sources.indexOfRegister(registerSpec2.getReg())) {
                            tryMapReg(registerSpec2, findRangeAndAdjust, category);
                        }
                    }
                }
            }
            i++;
            findRangeAndAdjust = i2;
        }
    }

    private int findRangeAndAdjust(NormalSsaInsn normalSsaInsn) {
        int oldToNew;
        BitSet bitSet;
        int fitPlanForRange;
        RegisterSpecList sources = normalSsaInsn.getSources();
        int size = sources.size();
        int[] iArr = new int[size];
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            iArr[i2] = sources.get(i2).getCategory();
            i += iArr[i2];
        }
        int i3 = Integer.MIN_VALUE;
        BitSet bitSet2 = null;
        int i4 = -1;
        int i5 = 0;
        for (int i6 = 0; i6 < size; i6++) {
            int reg = sources.get(i6).getReg();
            if (i6 != 0) {
                i5 -= iArr[i6 - 1];
            }
            if (this.ssaRegsMapped.get(reg) && (oldToNew = this.mapper.oldToNew(reg) + i5) >= 0 && !spansParamRange(oldToNew, i) && (fitPlanForRange = fitPlanForRange(oldToNew, normalSsaInsn, iArr, (bitSet = new BitSet(size)))) >= 0) {
                int cardinality = fitPlanForRange - bitSet.cardinality();
                if (cardinality > i3) {
                    i3 = cardinality;
                    i4 = oldToNew;
                    bitSet2 = bitSet;
                }
                if (fitPlanForRange == i) {
                    break;
                }
            }
        }
        if (i4 == -1) {
            bitSet2 = new BitSet(size);
            i4 = findAnyFittingRange(normalSsaInsn, i, iArr, bitSet2);
        }
        for (int nextSetBit = bitSet2.nextSetBit(0); nextSetBit >= 0; nextSetBit = bitSet2.nextSetBit(nextSetBit + 1)) {
            normalSsaInsn.changeOneSource(nextSetBit, insertMoveBefore(normalSsaInsn, sources.get(nextSetBit)));
        }
        return i4;
    }

    private int findAnyFittingRange(NormalSsaInsn normalSsaInsn, int i, int[] iArr, BitSet bitSet) {
        Alignment alignment = Alignment.UNSPECIFIED;
        int i2 = 0;
        int i3 = 0;
        int i4 = 0;
        for (int i5 : iArr) {
            if (i5 == 2) {
                if (isEven(i4)) {
                    i3++;
                } else {
                    i2++;
                }
                i4 += 2;
            } else {
                i4++;
            }
        }
        if (i2 > i3) {
            if (isEven(this.paramRangeEnd)) {
                alignment = Alignment.ODD;
            } else {
                alignment = Alignment.EVEN;
            }
        } else if (i3 > 0) {
            if (isEven(this.paramRangeEnd)) {
                alignment = Alignment.EVEN;
            } else {
                alignment = Alignment.ODD;
            }
        }
        int i6 = this.paramRangeEnd;
        while (true) {
            int findNextUnreservedRopReg = findNextUnreservedRopReg(i6, i, alignment);
            if (fitPlanForRange(findNextUnreservedRopReg, normalSsaInsn, iArr, bitSet) >= 0) {
                return findNextUnreservedRopReg;
            }
            i6 = findNextUnreservedRopReg + 1;
            bitSet.clear();
        }
    }

    private int fitPlanForRange(int i, NormalSsaInsn normalSsaInsn, int[] iArr, BitSet bitSet) {
        RegisterSpecList sources = normalSsaInsn.getSources();
        int size = sources.size();
        RegisterSpecList ssaSetToSpecs = ssaSetToSpecs(normalSsaInsn.getBlock().getLiveOutRegs());
        BitSet bitSet2 = new BitSet(this.ssaMeth.getRegCount());
        int i2 = 0;
        for (int i3 = 0; i3 < size; i3++) {
            RegisterSpec registerSpec = sources.get(i3);
            int reg = registerSpec.getReg();
            int i4 = iArr[i3];
            if (i3 != 0) {
                i += iArr[i3 - 1];
            }
            if (!this.ssaRegsMapped.get(reg) || this.mapper.oldToNew(reg) != i) {
                if (!rangeContainsReserved(i, i4)) {
                    if (this.ssaRegsMapped.get(reg) || !canMapReg(registerSpec, i) || bitSet2.get(reg)) {
                        if (!this.mapper.areAnyPinned(ssaSetToSpecs, i, i4) && !this.mapper.areAnyPinned(sources, i, i4)) {
                            bitSet.set(i3);
                            bitSet2.set(reg);
                        }
                    }
                }
                return -1;
            }
            i2 += i4;
            bitSet2.set(reg);
        }
        return i2;
    }

    RegisterSpecList ssaSetToSpecs(IntSet intSet) {
        RegisterSpecList registerSpecList = new RegisterSpecList(intSet.elements());
        IntIterator it = intSet.iterator();
        int i = 0;
        while (it.hasNext()) {
            registerSpecList.set(i, getDefinitionSpecForSsaReg(it.next()));
            i++;
        }
        return registerSpecList;
    }

    private LocalItem getLocalItemForReg(int i) {
        for (Map.Entry<LocalItem, ArrayList<RegisterSpec>> entry : this.localVariables.entrySet()) {
            Iterator<RegisterSpec> it = entry.getValue().iterator();
            while (it.hasNext()) {
                if (it.next().getReg() == i) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    private void processPhiInsn(PhiInsn phiInsn) {
        RegisterSpec result = phiInsn.getResult();
        int reg = result.getReg();
        int category = result.getCategory();
        RegisterSpecList sources = phiInsn.getSources();
        int size = sources.size();
        ArrayList<RegisterSpec> arrayList = new ArrayList<>();
        Multiset multiset = new Multiset(size + 1);
        if (this.ssaRegsMapped.get(reg)) {
            multiset.add(this.mapper.oldToNew(reg));
        } else {
            arrayList.add(result);
        }
        for (int i = 0; i < size; i++) {
            RegisterSpec result2 = this.ssaMeth.getDefinitionForRegister(sources.get(i).getReg()).getResult();
            int reg2 = result2.getReg();
            if (this.ssaRegsMapped.get(reg2)) {
                multiset.add(this.mapper.oldToNew(reg2));
            } else {
                arrayList.add(result2);
            }
        }
        for (int i2 = 0; i2 < multiset.getSize(); i2++) {
            tryMapRegs(arrayList, multiset.getAndRemoveHighestCount(), category, false);
        }
        int findNextUnreservedRopReg = findNextUnreservedRopReg(this.paramRangeEnd, category);
        while (!tryMapRegs(arrayList, findNextUnreservedRopReg, category, false)) {
            findNextUnreservedRopReg = findNextUnreservedRopReg(findNextUnreservedRopReg + 1, category);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class Multiset {
        private final int[] count;
        private final int[] reg;
        private int size = 0;

        public Multiset(int i) {
            this.reg = new int[i];
            this.count = new int[i];
        }

        public void add(int i) {
            int i2 = 0;
            while (true) {
                int i3 = this.size;
                if (i2 < i3) {
                    if (this.reg[i2] == i) {
                        int[] iArr = this.count;
                        iArr[i2] = iArr[i2] + 1;
                        return;
                    }
                    i2++;
                } else {
                    this.reg[i3] = i;
                    this.count[i3] = 1;
                    this.size = i3 + 1;
                    return;
                }
            }
        }

        public int getAndRemoveHighestCount() {
            int i = -1;
            int i2 = -1;
            int i3 = 0;
            for (int i4 = 0; i4 < this.size; i4++) {
                int[] iArr = this.count;
                if (i3 < iArr[i4]) {
                    int i5 = this.reg[i4];
                    i3 = iArr[i4];
                    i2 = i5;
                    i = i4;
                }
            }
            this.count[i] = 0;
            return i2;
        }

        public int getSize() {
            return this.size;
        }
    }
}

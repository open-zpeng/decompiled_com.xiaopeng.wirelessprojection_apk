package com.android.dx.cf.code;

import com.android.dx.cf.code.ByteCatchList;
import com.android.dx.cf.code.LocalVariableList;
import com.android.dx.cf.iface.MethodList;
import com.android.dx.dex.DexOptions;
import com.android.dx.rop.code.BasicBlock;
import com.android.dx.rop.code.BasicBlockList;
import com.android.dx.rop.code.Insn;
import com.android.dx.rop.code.InsnList;
import com.android.dx.rop.code.PlainCstInsn;
import com.android.dx.rop.code.PlainInsn;
import com.android.dx.rop.code.RegisterSpec;
import com.android.dx.rop.code.RegisterSpecList;
import com.android.dx.rop.code.Rop;
import com.android.dx.rop.code.RopMethod;
import com.android.dx.rop.code.Rops;
import com.android.dx.rop.code.SourcePosition;
import com.android.dx.rop.code.ThrowingCstInsn;
import com.android.dx.rop.code.ThrowingInsn;
import com.android.dx.rop.code.TranslationAdvice;
import com.android.dx.rop.cst.CstInteger;
import com.android.dx.rop.cst.CstType;
import com.android.dx.rop.type.StdTypeList;
import com.android.dx.rop.type.Type;
import com.android.dx.rop.type.TypeList;
import com.android.dx.util.Bits;
import com.android.dx.util.Hex;
import com.android.dx.util.IntList;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
/* loaded from: classes.dex */
public final class Ropper {
    private static final int PARAM_ASSIGNMENT = -1;
    private static final int RETURN = -2;
    private static final int SPECIAL_LABEL_COUNT = 7;
    private static final int SYNCH_CATCH_1 = -6;
    private static final int SYNCH_CATCH_2 = -7;
    private static final int SYNCH_RETURN = -3;
    private static final int SYNCH_SETUP_1 = -4;
    private static final int SYNCH_SETUP_2 = -5;
    private final ByteBlockList blocks;
    private final CatchInfo[] catchInfos;
    private final ExceptionSetupLabelAllocator exceptionSetupLabelAllocator;
    private boolean hasSubroutines;
    private final RopperMachine machine;
    private final int maxLabel;
    private final int maxLocals;
    private final ConcreteMethod method;
    private final ArrayList<BasicBlock> result;
    private final ArrayList<IntList> resultSubroutines;
    private final Simulator sim;
    private final Frame[] startFrames;
    private final Subroutine[] subroutines;
    private boolean synchNeedsExceptionHandler;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class CatchInfo {
        private final Map<Type, ExceptionHandlerSetup> setups;

        private CatchInfo() {
            this.setups = new HashMap();
        }

        ExceptionHandlerSetup getSetup(Type type) {
            ExceptionHandlerSetup exceptionHandlerSetup = this.setups.get(type);
            if (exceptionHandlerSetup == null) {
                ExceptionHandlerSetup exceptionHandlerSetup2 = new ExceptionHandlerSetup(type, Ropper.this.exceptionSetupLabelAllocator.getNextLabel());
                this.setups.put(type, exceptionHandlerSetup2);
                return exceptionHandlerSetup2;
            }
            return exceptionHandlerSetup;
        }

        Collection<ExceptionHandlerSetup> getSetups() {
            return this.setups.values();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ExceptionHandlerSetup {
        private Type caughtType;
        private int label;

        ExceptionHandlerSetup(Type type, int i) {
            this.caughtType = type;
            this.label = i;
        }

        Type getCaughtType() {
            return this.caughtType;
        }

        public int getLabel() {
            return this.label;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class Subroutine {
        private BitSet callerBlocks;
        private BitSet retBlocks;
        private int startBlock;

        Subroutine(int i) {
            this.startBlock = i;
            this.retBlocks = new BitSet(Ropper.this.maxLabel);
            this.callerBlocks = new BitSet(Ropper.this.maxLabel);
            Ropper.this.hasSubroutines = true;
        }

        Subroutine(Ropper ropper, int i, int i2) {
            this(i);
            addRetBlock(i2);
        }

        int getStartBlock() {
            return this.startBlock;
        }

        void addRetBlock(int i) {
            this.retBlocks.set(i);
        }

        void addCallerBlock(int i) {
            this.callerBlocks.set(i);
        }

        IntList getSuccessors() {
            IntList intList = new IntList(this.callerBlocks.size());
            int nextSetBit = this.callerBlocks.nextSetBit(0);
            while (nextSetBit >= 0) {
                intList.add(Ropper.this.labelToBlock(nextSetBit).getSuccessors().get(0));
                nextSetBit = this.callerBlocks.nextSetBit(nextSetBit + 1);
            }
            intList.setImmutable();
            return intList;
        }

        void mergeToSuccessors(Frame frame, int[] iArr) {
            int nextSetBit = this.callerBlocks.nextSetBit(0);
            while (nextSetBit >= 0) {
                int i = Ropper.this.labelToBlock(nextSetBit).getSuccessors().get(0);
                Frame subFrameForLabel = frame.subFrameForLabel(this.startBlock, nextSetBit);
                if (subFrameForLabel != null) {
                    Ropper.this.mergeAndWorkAsNecessary(i, -1, null, subFrameForLabel, iArr);
                } else {
                    Bits.set(iArr, nextSetBit);
                }
                nextSetBit = this.callerBlocks.nextSetBit(nextSetBit + 1);
            }
        }
    }

    public static RopMethod convert(ConcreteMethod concreteMethod, TranslationAdvice translationAdvice, MethodList methodList, DexOptions dexOptions) {
        try {
            Ropper ropper = new Ropper(concreteMethod, translationAdvice, methodList, dexOptions);
            ropper.doit();
            return ropper.getRopMethod();
        } catch (SimException e) {
            e.addContext("...while working on method " + concreteMethod.getNat().toHuman());
            throw e;
        }
    }

    private Ropper(ConcreteMethod concreteMethod, TranslationAdvice translationAdvice, MethodList methodList, DexOptions dexOptions) {
        Objects.requireNonNull(concreteMethod, "method == null");
        Objects.requireNonNull(translationAdvice, "advice == null");
        this.method = concreteMethod;
        ByteBlockList identifyBlocks = BasicBlocker.identifyBlocks(concreteMethod);
        this.blocks = identifyBlocks;
        int maxLabel = identifyBlocks.getMaxLabel();
        this.maxLabel = maxLabel;
        int maxLocals = concreteMethod.getMaxLocals();
        this.maxLocals = maxLocals;
        RopperMachine ropperMachine = new RopperMachine(this, concreteMethod, translationAdvice, methodList);
        this.machine = ropperMachine;
        this.sim = new Simulator(ropperMachine, concreteMethod, dexOptions);
        Frame[] frameArr = new Frame[maxLabel];
        this.startFrames = frameArr;
        this.subroutines = new Subroutine[maxLabel];
        this.result = new ArrayList<>((identifyBlocks.size() * 2) + 10);
        this.resultSubroutines = new ArrayList<>((identifyBlocks.size() * 2) + 10);
        this.catchInfos = new CatchInfo[maxLabel];
        this.synchNeedsExceptionHandler = false;
        frameArr[0] = new Frame(maxLocals, concreteMethod.getMaxStack());
        this.exceptionSetupLabelAllocator = new ExceptionSetupLabelAllocator();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getFirstTempStackReg() {
        int normalRegCount = getNormalRegCount();
        return isSynchronized() ? normalRegCount + 1 : normalRegCount;
    }

    private int getSpecialLabel(int i) {
        return this.maxLabel + this.method.getCatches().size() + (~i);
    }

    private int getMinimumUnreservedLabel() {
        return this.maxLabel + this.method.getCatches().size() + 7;
    }

    private int getAvailableLabel() {
        int minimumUnreservedLabel = getMinimumUnreservedLabel();
        Iterator<BasicBlock> it = this.result.iterator();
        while (it.hasNext()) {
            int label = it.next().getLabel();
            if (label >= minimumUnreservedLabel) {
                minimumUnreservedLabel = label + 1;
            }
        }
        return minimumUnreservedLabel;
    }

    private boolean isSynchronized() {
        return (this.method.getAccessFlags() & 32) != 0;
    }

    private boolean isStatic() {
        return (this.method.getAccessFlags() & 8) != 0;
    }

    private int getNormalRegCount() {
        return this.maxLocals + this.method.getMaxStack();
    }

    private RegisterSpec getSynchReg() {
        int normalRegCount = getNormalRegCount();
        if (normalRegCount < 1) {
            normalRegCount = 1;
        }
        return RegisterSpec.make(normalRegCount, Type.OBJECT);
    }

    private int labelToResultIndex(int i) {
        int size = this.result.size();
        for (int i2 = 0; i2 < size; i2++) {
            if (this.result.get(i2).getLabel() == i) {
                return i2;
            }
        }
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public BasicBlock labelToBlock(int i) {
        int labelToResultIndex = labelToResultIndex(i);
        if (labelToResultIndex < 0) {
            throw new IllegalArgumentException("no such label " + Hex.u2(i));
        }
        return this.result.get(labelToResultIndex);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addBlock(BasicBlock basicBlock, IntList intList) {
        Objects.requireNonNull(basicBlock, "block == null");
        this.result.add(basicBlock);
        intList.throwIfMutable();
        this.resultSubroutines.add(intList);
    }

    private boolean addOrReplaceBlock(BasicBlock basicBlock, IntList intList) {
        boolean z;
        Objects.requireNonNull(basicBlock, "block == null");
        int labelToResultIndex = labelToResultIndex(basicBlock.getLabel());
        if (labelToResultIndex < 0) {
            z = false;
        } else {
            removeBlockAndSpecialSuccessors(labelToResultIndex);
            z = true;
        }
        this.result.add(basicBlock);
        intList.throwIfMutable();
        this.resultSubroutines.add(intList);
        return z;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean addOrReplaceBlockNoDelete(BasicBlock basicBlock, IntList intList) {
        boolean z;
        Objects.requireNonNull(basicBlock, "block == null");
        int labelToResultIndex = labelToResultIndex(basicBlock.getLabel());
        if (labelToResultIndex < 0) {
            z = false;
        } else {
            this.result.remove(labelToResultIndex);
            this.resultSubroutines.remove(labelToResultIndex);
            z = true;
        }
        this.result.add(basicBlock);
        intList.throwIfMutable();
        this.resultSubroutines.add(intList);
        return z;
    }

    private void removeBlockAndSpecialSuccessors(int i) {
        int minimumUnreservedLabel = getMinimumUnreservedLabel();
        IntList successors = this.result.get(i).getSuccessors();
        int size = successors.size();
        this.result.remove(i);
        this.resultSubroutines.remove(i);
        for (int i2 = 0; i2 < size; i2++) {
            int i3 = successors.get(i2);
            if (i3 >= minimumUnreservedLabel) {
                int labelToResultIndex = labelToResultIndex(i3);
                if (labelToResultIndex < 0) {
                    throw new RuntimeException("Invalid label " + Hex.u2(i3));
                }
                removeBlockAndSpecialSuccessors(labelToResultIndex);
            }
        }
    }

    private RopMethod getRopMethod() {
        int size = this.result.size();
        BasicBlockList basicBlockList = new BasicBlockList(size);
        for (int i = 0; i < size; i++) {
            basicBlockList.set(i, this.result.get(i));
        }
        basicBlockList.setImmutable();
        return new RopMethod(basicBlockList, getSpecialLabel(-1));
    }

    private void doit() {
        int[] makeBitSet = Bits.makeBitSet(this.maxLabel);
        Bits.set(makeBitSet, 0);
        addSetupBlocks();
        setFirstFrame();
        while (true) {
            int findFirst = Bits.findFirst(makeBitSet, 0);
            if (findFirst < 0) {
                break;
            }
            Bits.clear(makeBitSet, findFirst);
            try {
                processBlock(this.blocks.labelToBlock(findFirst), this.startFrames[findFirst], makeBitSet);
            } catch (SimException e) {
                e.addContext("...while working on block " + Hex.u2(findFirst));
                throw e;
            }
        }
        addReturnBlock();
        addSynchExceptionHandlerBlock();
        addExceptionSetupBlocks();
        if (this.hasSubroutines) {
            inlineSubroutines();
        }
    }

    private void setFirstFrame() {
        this.startFrames[0].initializeWithParameters(this.method.getEffectiveDescriptor().getParameterTypes());
        this.startFrames[0].setImmutable();
    }

    private void processBlock(ByteBlock byteBlock, Frame frame, int[] iArr) {
        IntList intList;
        Subroutine subroutine;
        int i;
        IntList intList2;
        int i2;
        int primarySuccessorIndex;
        int i3;
        SourcePosition position;
        IntList intList3;
        int i4;
        ByteCatchList catches = byteBlock.getCatches();
        this.machine.startBlock(catches.toRopCatchList());
        Frame copy = frame.copy();
        this.sim.simulate(byteBlock, copy);
        copy.setImmutable();
        int extraBlockCount = this.machine.getExtraBlockCount();
        ArrayList<Insn> insns = this.machine.getInsns();
        int size = insns.size();
        int size2 = catches.size();
        IntList successors = byteBlock.getSuccessors();
        int i5 = 1;
        if (this.machine.hasJsr()) {
            int i6 = successors.get(1);
            Subroutine[] subroutineArr = this.subroutines;
            if (subroutineArr[i6] == null) {
                subroutineArr[i6] = new Subroutine(i6);
            }
            this.subroutines[i6].addCallerBlock(byteBlock.getLabel());
            intList = successors;
            subroutine = this.subroutines[i6];
            i = 1;
        } else {
            if (this.machine.hasRet()) {
                int subroutineAddress = this.machine.getReturnAddress().getSubroutineAddress();
                Subroutine[] subroutineArr2 = this.subroutines;
                if (subroutineArr2[subroutineAddress] == null) {
                    subroutineArr2[subroutineAddress] = new Subroutine(this, subroutineAddress, byteBlock.getLabel());
                } else {
                    subroutineArr2[subroutineAddress].addRetBlock(byteBlock.getLabel());
                }
                IntList successors2 = this.subroutines[subroutineAddress].getSuccessors();
                this.subroutines[subroutineAddress].mergeToSuccessors(copy, iArr);
                i = successors2.size();
                intList = successors2;
            } else if (this.machine.wereCatchesUsed()) {
                intList = successors;
                i = size2;
            } else {
                intList = successors;
                subroutine = null;
                i = 0;
            }
            subroutine = null;
        }
        int size3 = intList.size();
        int i7 = i;
        while (i7 < size3) {
            try {
                int i8 = i7;
                int i9 = size3;
                IntList intList4 = intList;
                int i10 = i5;
                mergeAndWorkAsNecessary(intList.get(i7), byteBlock.getLabel(), subroutine, copy, iArr);
                i7 = i8 + 1;
                i5 = i10;
                intList = intList4;
                size3 = i9;
            } catch (SimException e) {
                e.addContext("...while merging to block " + Hex.u2(i4));
                throw e;
            }
        }
        int i11 = size3;
        IntList intList5 = intList;
        int i12 = i5;
        if (i11 == 0 && this.machine.returns()) {
            intList2 = IntList.makeImmutable(getSpecialLabel(-2));
            i2 = i12;
        } else {
            intList2 = intList5;
            i2 = i11;
        }
        if (i2 == 0) {
            primarySuccessorIndex = -1;
        } else {
            primarySuccessorIndex = this.machine.getPrimarySuccessorIndex();
            if (primarySuccessorIndex >= 0) {
                primarySuccessorIndex = intList2.get(primarySuccessorIndex);
            }
        }
        int i13 = primarySuccessorIndex;
        int i14 = (isSynchronized() && this.machine.canThrow()) ? i12 : 0;
        if (i14 == 0 && size2 == 0) {
            i3 = i13;
        } else {
            IntList intList6 = new IntList(i2);
            int i15 = 0;
            int i16 = 0;
            while (i16 < size2) {
                ByteCatchList.Item item = catches.get(i16);
                CstType exceptionClass = item.getExceptionClass();
                int handlerPc = item.getHandlerPc();
                int i17 = i15 | (exceptionClass == CstType.OBJECT ? i12 : 0);
                try {
                    IntList intList7 = intList6;
                    int i18 = i13;
                    int i19 = i16;
                    mergeAndWorkAsNecessary(handlerPc, byteBlock.getLabel(), null, copy.makeExceptionHandlerStartFrame(exceptionClass), iArr);
                    CatchInfo catchInfo = this.catchInfos[handlerPc];
                    if (catchInfo == null) {
                        catchInfo = new CatchInfo();
                        this.catchInfos[handlerPc] = catchInfo;
                    }
                    intList7.add(catchInfo.getSetup(exceptionClass.getClassType()).getLabel());
                    i16 = i19 + 1;
                    intList6 = intList7;
                    i15 = i17;
                    i13 = i18;
                    i12 = 1;
                } catch (SimException e2) {
                    e2.addContext("...while merging exception to block " + Hex.u2(handlerPc));
                    throw e2;
                }
            }
            IntList intList8 = intList6;
            int i20 = i13;
            if (i14 != 0 && i15 == 0) {
                intList8.add(getSpecialLabel(SYNCH_CATCH_1));
                this.synchNeedsExceptionHandler = true;
                for (int i21 = (size - extraBlockCount) - 1; i21 < size; i21++) {
                    Insn insn = insns.get(i21);
                    if (insn.canThrow()) {
                        insns.set(i21, insn.withAddedCatch(Type.OBJECT));
                    }
                }
            }
            i3 = i20;
            if (i3 >= 0) {
                intList8.add(i3);
            }
            intList8.setImmutable();
            intList2 = intList8;
        }
        int indexOf = intList2.indexOf(i3);
        int i22 = i3;
        while (extraBlockCount > 0) {
            size--;
            Insn insn2 = insns.get(size);
            boolean z = insn2.getOpcode().getBranchingness() == 1;
            InsnList insnList = new InsnList(z ? 2 : 1);
            insnList.set(0, insn2);
            if (z) {
                insnList.set(1, new PlainInsn(Rops.GOTO, insn2.getPosition(), (RegisterSpec) null, RegisterSpecList.EMPTY));
                intList3 = IntList.makeImmutable(i22);
            } else {
                intList3 = intList2;
            }
            insnList.setImmutable();
            int availableLabel = getAvailableLabel();
            addBlock(new BasicBlock(availableLabel, insnList, intList3, i22), copy.getSubroutines());
            intList2 = intList2.mutableCopy();
            intList2.set(indexOf, availableLabel);
            intList2.setImmutable();
            extraBlockCount--;
            i22 = availableLabel;
        }
        Insn insn3 = size == 0 ? null : insns.get(size - 1);
        if (insn3 == null || insn3.getOpcode().getBranchingness() == 1) {
            if (insn3 == null) {
                position = SourcePosition.NO_INFO;
            } else {
                position = insn3.getPosition();
            }
            insns.add(new PlainInsn(Rops.GOTO, position, (RegisterSpec) null, RegisterSpecList.EMPTY));
            size++;
        }
        InsnList insnList2 = new InsnList(size);
        for (int i23 = 0; i23 < size; i23++) {
            insnList2.set(i23, insns.get(i23));
        }
        insnList2.setImmutable();
        addOrReplaceBlock(new BasicBlock(byteBlock.getLabel(), insnList2, intList2, i22), copy.getSubroutines());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void mergeAndWorkAsNecessary(int i, int i2, Subroutine subroutine, Frame frame, int[] iArr) {
        Frame mergeWith;
        Frame[] frameArr = this.startFrames;
        Frame frame2 = frameArr[i];
        if (frame2 == null) {
            if (subroutine != null) {
                frameArr[i] = frame.makeNewSubroutineStartFrame(i, i2);
            } else {
                frameArr[i] = frame;
            }
            Bits.set(iArr, i);
            return;
        }
        if (subroutine != null) {
            mergeWith = frame2.mergeWithSubroutineCaller(frame, subroutine.getStartBlock(), i2);
        } else {
            mergeWith = frame2.mergeWith(frame);
        }
        if (mergeWith != frame2) {
            this.startFrames[i] = mergeWith;
            Bits.set(iArr, i);
        }
    }

    private void addSetupBlocks() {
        InsnList insnList;
        RegisterSpec makeLocalOptional;
        LocalVariableList localVariables = this.method.getLocalVariables();
        int i = 0;
        SourcePosition makeSourcePosistion = this.method.makeSourcePosistion(0);
        StdTypeList parameterTypes = this.method.getEffectiveDescriptor().getParameterTypes();
        int size = parameterTypes.size();
        InsnList insnList2 = new InsnList(size + 1);
        int i2 = 0;
        int i3 = 0;
        while (i2 < size) {
            Type type = parameterTypes.get(i2);
            LocalVariableList.Item pcAndIndexToLocal = localVariables.pcAndIndexToLocal(i, i3);
            if (pcAndIndexToLocal == null) {
                makeLocalOptional = RegisterSpec.make(i3, type);
            } else {
                makeLocalOptional = RegisterSpec.makeLocalOptional(i3, type, pcAndIndexToLocal.getLocalItem());
            }
            insnList2.set(i2, new PlainCstInsn(Rops.opMoveParam(type), makeSourcePosistion, makeLocalOptional, RegisterSpecList.EMPTY, CstInteger.make(i3)));
            i3 += type.getCategory();
            i2++;
            i = 0;
        }
        insnList2.set(size, new PlainInsn(Rops.GOTO, makeSourcePosistion, (RegisterSpec) null, RegisterSpecList.EMPTY));
        insnList2.setImmutable();
        boolean isSynchronized = isSynchronized();
        int specialLabel = isSynchronized ? getSpecialLabel(-4) : 0;
        addBlock(new BasicBlock(getSpecialLabel(-1), insnList2, IntList.makeImmutable(specialLabel), specialLabel), IntList.EMPTY);
        if (isSynchronized) {
            RegisterSpec synchReg = getSynchReg();
            if (isStatic()) {
                ThrowingCstInsn throwingCstInsn = new ThrowingCstInsn(Rops.CONST_OBJECT, makeSourcePosistion, RegisterSpecList.EMPTY, StdTypeList.EMPTY, this.method.getDefiningClass());
                insnList = new InsnList(1);
                insnList.set(0, throwingCstInsn);
            } else {
                InsnList insnList3 = new InsnList(2);
                insnList3.set(0, new PlainCstInsn(Rops.MOVE_PARAM_OBJECT, makeSourcePosistion, synchReg, RegisterSpecList.EMPTY, CstInteger.VALUE_0));
                insnList3.set(1, new PlainInsn(Rops.GOTO, makeSourcePosistion, (RegisterSpec) null, RegisterSpecList.EMPTY));
                insnList = insnList3;
            }
            int specialLabel2 = getSpecialLabel(-5);
            insnList.setImmutable();
            addBlock(new BasicBlock(specialLabel, insnList, IntList.makeImmutable(specialLabel2), specialLabel2), IntList.EMPTY);
            InsnList insnList4 = new InsnList(isStatic() ? 2 : 1);
            if (isStatic()) {
                insnList4.set(0, new PlainInsn(Rops.opMoveResultPseudo(synchReg), makeSourcePosistion, synchReg, RegisterSpecList.EMPTY));
            }
            insnList4.set(isStatic() ? 1 : 0, new ThrowingInsn(Rops.MONITOR_ENTER, makeSourcePosistion, RegisterSpecList.make(synchReg), StdTypeList.EMPTY));
            insnList4.setImmutable();
            addBlock(new BasicBlock(specialLabel2, insnList4, IntList.makeImmutable(0), 0), IntList.EMPTY);
        }
    }

    private void addReturnBlock() {
        RegisterSpecList make;
        Rop returnOp = this.machine.getReturnOp();
        if (returnOp == null) {
            return;
        }
        SourcePosition returnPosition = this.machine.getReturnPosition();
        int specialLabel = getSpecialLabel(-2);
        if (isSynchronized()) {
            InsnList insnList = new InsnList(1);
            insnList.set(0, new ThrowingInsn(Rops.MONITOR_EXIT, returnPosition, RegisterSpecList.make(getSynchReg()), StdTypeList.EMPTY));
            insnList.setImmutable();
            int specialLabel2 = getSpecialLabel(-3);
            addBlock(new BasicBlock(specialLabel, insnList, IntList.makeImmutable(specialLabel2), specialLabel2), IntList.EMPTY);
            specialLabel = specialLabel2;
        }
        InsnList insnList2 = new InsnList(1);
        TypeList sources = returnOp.getSources();
        if (sources.size() == 0) {
            make = RegisterSpecList.EMPTY;
        } else {
            make = RegisterSpecList.make(RegisterSpec.make(0, sources.getType(0)));
        }
        insnList2.set(0, new PlainInsn(returnOp, returnPosition, (RegisterSpec) null, make));
        insnList2.setImmutable();
        addBlock(new BasicBlock(specialLabel, insnList2, IntList.EMPTY, -1), IntList.EMPTY);
    }

    private void addSynchExceptionHandlerBlock() {
        if (this.synchNeedsExceptionHandler) {
            SourcePosition makeSourcePosistion = this.method.makeSourcePosistion(0);
            RegisterSpec make = RegisterSpec.make(0, Type.THROWABLE);
            InsnList insnList = new InsnList(2);
            insnList.set(0, new PlainInsn(Rops.opMoveException(Type.THROWABLE), makeSourcePosistion, make, RegisterSpecList.EMPTY));
            insnList.set(1, new ThrowingInsn(Rops.MONITOR_EXIT, makeSourcePosistion, RegisterSpecList.make(getSynchReg()), StdTypeList.EMPTY));
            insnList.setImmutable();
            int specialLabel = getSpecialLabel(SYNCH_CATCH_2);
            addBlock(new BasicBlock(getSpecialLabel(SYNCH_CATCH_1), insnList, IntList.makeImmutable(specialLabel), specialLabel), IntList.EMPTY);
            InsnList insnList2 = new InsnList(1);
            insnList2.set(0, new ThrowingInsn(Rops.THROW, makeSourcePosistion, RegisterSpecList.make(make), StdTypeList.EMPTY));
            insnList2.setImmutable();
            addBlock(new BasicBlock(specialLabel, insnList2, IntList.EMPTY, -1), IntList.EMPTY);
        }
    }

    private void addExceptionSetupBlocks() {
        int length = this.catchInfos.length;
        for (int i = 0; i < length; i++) {
            CatchInfo catchInfo = this.catchInfos[i];
            if (catchInfo != null) {
                for (ExceptionHandlerSetup exceptionHandlerSetup : catchInfo.getSetups()) {
                    SourcePosition position = labelToBlock(i).getFirstInsn().getPosition();
                    InsnList insnList = new InsnList(2);
                    insnList.set(0, new PlainInsn(Rops.opMoveException(exceptionHandlerSetup.getCaughtType()), position, RegisterSpec.make(this.maxLocals, exceptionHandlerSetup.getCaughtType()), RegisterSpecList.EMPTY));
                    insnList.set(1, new PlainInsn(Rops.GOTO, position, (RegisterSpec) null, RegisterSpecList.EMPTY));
                    insnList.setImmutable();
                    addBlock(new BasicBlock(exceptionHandlerSetup.getLabel(), insnList, IntList.makeImmutable(i), i), this.startFrames[i].getSubroutines());
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isSubroutineCaller(BasicBlock basicBlock) {
        IntList successors = basicBlock.getSuccessors();
        if (successors.size() < 2) {
            return false;
        }
        int i = successors.get(1);
        Subroutine[] subroutineArr = this.subroutines;
        return i < subroutineArr.length && subroutineArr[i] != null;
    }

    private void inlineSubroutines() {
        final IntList intList = new IntList(4);
        forEachNonSubBlockDepthFirst(0, new BasicBlock.Visitor() { // from class: com.android.dx.cf.code.Ropper.1
            @Override // com.android.dx.rop.code.BasicBlock.Visitor
            public void visitBlock(BasicBlock basicBlock) {
                if (Ropper.this.isSubroutineCaller(basicBlock)) {
                    intList.add(basicBlock.getLabel());
                }
            }
        });
        int availableLabel = getAvailableLabel();
        ArrayList arrayList = new ArrayList(availableLabel);
        for (int i = 0; i < availableLabel; i++) {
            arrayList.add(null);
        }
        for (int i2 = 0; i2 < this.result.size(); i2++) {
            BasicBlock basicBlock = this.result.get(i2);
            if (basicBlock != null) {
                arrayList.set(basicBlock.getLabel(), this.resultSubroutines.get(i2));
            }
        }
        int size = intList.size();
        for (int i3 = 0; i3 < size; i3++) {
            new SubroutineInliner(new LabelAllocator(getAvailableLabel()), arrayList).inlineSubroutineCalledFrom(labelToBlock(intList.get(i3)));
        }
        deleteUnreachableBlocks();
    }

    private void deleteUnreachableBlocks() {
        final IntList intList = new IntList(this.result.size());
        this.resultSubroutines.clear();
        forEachNonSubBlockDepthFirst(getSpecialLabel(-1), new BasicBlock.Visitor() { // from class: com.android.dx.cf.code.Ropper.2
            @Override // com.android.dx.rop.code.BasicBlock.Visitor
            public void visitBlock(BasicBlock basicBlock) {
                intList.add(basicBlock.getLabel());
            }
        });
        intList.sort();
        for (int size = this.result.size() - 1; size >= 0; size--) {
            if (intList.indexOf(this.result.get(size).getLabel()) < 0) {
                this.result.remove(size);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class LabelAllocator {
        int nextAvailableLabel;

        LabelAllocator(int i) {
            this.nextAvailableLabel = i;
        }

        int getNextLabel() {
            int i = this.nextAvailableLabel;
            this.nextAvailableLabel = i + 1;
            return i;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ExceptionSetupLabelAllocator extends LabelAllocator {
        int maxSetupLabel;

        ExceptionSetupLabelAllocator() {
            super(Ropper.this.maxLabel);
            this.maxSetupLabel = Ropper.this.maxLabel + Ropper.this.method.getCatches().size();
        }

        @Override // com.android.dx.cf.code.Ropper.LabelAllocator
        int getNextLabel() {
            if (this.nextAvailableLabel >= this.maxSetupLabel) {
                throw new IndexOutOfBoundsException();
            }
            int i = this.nextAvailableLabel;
            this.nextAvailableLabel = i + 1;
            return i;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class SubroutineInliner {
        private final LabelAllocator labelAllocator;
        private final ArrayList<IntList> labelToSubroutines;
        private final HashMap<Integer, Integer> origLabelToCopiedLabel = new HashMap<>();
        private int subroutineStart;
        private int subroutineSuccessor;
        private final BitSet workList;

        SubroutineInliner(LabelAllocator labelAllocator, ArrayList<IntList> arrayList) {
            this.workList = new BitSet(Ropper.this.maxLabel);
            this.labelAllocator = labelAllocator;
            this.labelToSubroutines = arrayList;
        }

        void inlineSubroutineCalledFrom(BasicBlock basicBlock) {
            this.subroutineSuccessor = basicBlock.getSuccessors().get(0);
            int i = basicBlock.getSuccessors().get(1);
            this.subroutineStart = i;
            int mapOrAllocateLabel = mapOrAllocateLabel(i);
            int nextSetBit = this.workList.nextSetBit(0);
            while (nextSetBit >= 0) {
                this.workList.clear(nextSetBit);
                int intValue = this.origLabelToCopiedLabel.get(Integer.valueOf(nextSetBit)).intValue();
                copyBlock(nextSetBit, intValue);
                Ropper ropper = Ropper.this;
                if (ropper.isSubroutineCaller(ropper.labelToBlock(nextSetBit))) {
                    new SubroutineInliner(this.labelAllocator, this.labelToSubroutines).inlineSubroutineCalledFrom(Ropper.this.labelToBlock(intValue));
                }
                nextSetBit = this.workList.nextSetBit(0);
            }
            Ropper.this.addOrReplaceBlockNoDelete(new BasicBlock(basicBlock.getLabel(), basicBlock.getInsns(), IntList.makeImmutable(mapOrAllocateLabel), mapOrAllocateLabel), this.labelToSubroutines.get(basicBlock.getLabel()));
        }

        private void copyBlock(int i, int i2) {
            IntList intList;
            BasicBlock labelToBlock = Ropper.this.labelToBlock(i);
            IntList successors = labelToBlock.getSuccessors();
            int i3 = -1;
            if (!Ropper.this.isSubroutineCaller(labelToBlock)) {
                Subroutine subroutineFromRetBlock = Ropper.this.subroutineFromRetBlock(i);
                if (subroutineFromRetBlock != null) {
                    if (subroutineFromRetBlock.startBlock != this.subroutineStart) {
                        throw new RuntimeException("ret instruction returns to label " + Hex.u2(subroutineFromRetBlock.startBlock) + " expected: " + Hex.u2(this.subroutineStart));
                    }
                    intList = IntList.makeImmutable(this.subroutineSuccessor);
                    i3 = this.subroutineSuccessor;
                } else {
                    int primarySuccessor = labelToBlock.getPrimarySuccessor();
                    int size = successors.size();
                    IntList intList2 = new IntList(size);
                    for (int i4 = 0; i4 < size; i4++) {
                        int i5 = successors.get(i4);
                        int mapOrAllocateLabel = mapOrAllocateLabel(i5);
                        intList2.add(mapOrAllocateLabel);
                        if (primarySuccessor == i5) {
                            i3 = mapOrAllocateLabel;
                        }
                    }
                    intList2.setImmutable();
                    intList = intList2;
                }
            } else {
                intList = IntList.makeImmutable(mapOrAllocateLabel(successors.get(0)), successors.get(1));
            }
            Ropper.this.addBlock(new BasicBlock(i2, Ropper.this.filterMoveReturnAddressInsns(labelToBlock.getInsns()), intList, i3), this.labelToSubroutines.get(i2));
        }

        private boolean involvedInSubroutine(int i, int i2) {
            IntList intList = this.labelToSubroutines.get(i);
            return intList != null && intList.size() > 0 && intList.top() == i2;
        }

        private int mapOrAllocateLabel(int i) {
            Integer num = this.origLabelToCopiedLabel.get(Integer.valueOf(i));
            if (num != null) {
                return num.intValue();
            }
            if (involvedInSubroutine(i, this.subroutineStart)) {
                int nextLabel = this.labelAllocator.getNextLabel();
                this.workList.set(i);
                this.origLabelToCopiedLabel.put(Integer.valueOf(i), Integer.valueOf(nextLabel));
                while (this.labelToSubroutines.size() <= nextLabel) {
                    this.labelToSubroutines.add(null);
                }
                ArrayList<IntList> arrayList = this.labelToSubroutines;
                arrayList.set(nextLabel, arrayList.get(i));
                return nextLabel;
            }
            return i;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Subroutine subroutineFromRetBlock(int i) {
        for (int length = this.subroutines.length - 1; length >= 0; length--) {
            Subroutine[] subroutineArr = this.subroutines;
            if (subroutineArr[length] != null) {
                Subroutine subroutine = subroutineArr[length];
                if (subroutine.retBlocks.get(i)) {
                    return subroutine;
                }
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public InsnList filterMoveReturnAddressInsns(InsnList insnList) {
        int size = insnList.size();
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            if (insnList.get(i2).getOpcode() != Rops.MOVE_RETURN_ADDRESS) {
                i++;
            }
        }
        if (i == size) {
            return insnList;
        }
        InsnList insnList2 = new InsnList(i);
        int i3 = 0;
        for (int i4 = 0; i4 < size; i4++) {
            Insn insn = insnList.get(i4);
            if (insn.getOpcode() != Rops.MOVE_RETURN_ADDRESS) {
                insnList2.set(i3, insn);
                i3++;
            }
        }
        insnList2.setImmutable();
        return insnList2;
    }

    private void forEachNonSubBlockDepthFirst(int i, BasicBlock.Visitor visitor) {
        forEachNonSubBlockDepthFirst0(labelToBlock(i), visitor, new BitSet(this.maxLabel));
    }

    private void forEachNonSubBlockDepthFirst0(BasicBlock basicBlock, BasicBlock.Visitor visitor, BitSet bitSet) {
        int labelToResultIndex;
        visitor.visitBlock(basicBlock);
        bitSet.set(basicBlock.getLabel());
        IntList successors = basicBlock.getSuccessors();
        int size = successors.size();
        for (int i = 0; i < size; i++) {
            int i2 = successors.get(i);
            if (!bitSet.get(i2) && ((!isSubroutineCaller(basicBlock) || i <= 0) && (labelToResultIndex = labelToResultIndex(i2)) >= 0)) {
                forEachNonSubBlockDepthFirst0(this.result.get(labelToResultIndex), visitor, bitSet);
            }
        }
    }
}

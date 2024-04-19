package com.android.dx.command.dump;

import com.android.dx.cf.code.ConcreteMethod;
import com.android.dx.cf.code.Ropper;
import com.android.dx.cf.iface.Member;
import com.android.dx.cf.iface.Method;
import com.android.dx.rop.code.AccessFlags;
import com.android.dx.rop.code.DexTranslationAdvice;
import com.android.dx.rop.code.RopMethod;
import com.android.dx.ssa.Optimizer;
import com.android.dx.ssa.SsaBasicBlock;
import com.android.dx.ssa.SsaInsn;
import com.android.dx.ssa.SsaMethod;
import com.android.dx.util.ByteArray;
import com.android.dx.util.Hex;
import com.android.dx.util.IntList;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
/* loaded from: classes.dex */
public class SsaDumper extends BlockDumper {
    public static void dump(byte[] bArr, PrintStream printStream, String str, Args args) {
        new SsaDumper(bArr, printStream, str, args).dump();
    }

    private SsaDumper(byte[] bArr, PrintStream printStream, String str, Args args) {
        super(bArr, printStream, str, true, args);
    }

    @Override // com.android.dx.command.dump.BlockDumper, com.android.dx.command.dump.BaseDumper, com.android.dx.cf.iface.ParseObserver
    public void endParsingMember(ByteArray byteArray, int i, String str, String str2, Member member) {
        if (!(member instanceof Method) || !shouldDumpMethod(str) || (member.getAccessFlags() & 1280) != 0) {
            return;
        }
        ConcreteMethod concreteMethod = new ConcreteMethod((Method) member, this.classFile, true, true);
        DexTranslationAdvice dexTranslationAdvice = DexTranslationAdvice.THE_ONE;
        RopMethod convert = Ropper.convert(concreteMethod, dexTranslationAdvice, this.classFile.getMethods(), this.dexOptions);
        SsaMethod ssaMethod = null;
        boolean isStatic = AccessFlags.isStatic(concreteMethod.getAccessFlags());
        int computeParamWidth = computeParamWidth(concreteMethod, isStatic);
        if (this.args.ssaStep == null) {
            ssaMethod = Optimizer.debugNoRegisterAllocation(convert, computeParamWidth, isStatic, true, dexTranslationAdvice, EnumSet.allOf(Optimizer.OptionalStep.class));
        } else if ("edge-split".equals(this.args.ssaStep)) {
            ssaMethod = Optimizer.debugEdgeSplit(convert, computeParamWidth, isStatic, true, dexTranslationAdvice);
        } else if ("phi-placement".equals(this.args.ssaStep)) {
            ssaMethod = Optimizer.debugPhiPlacement(convert, computeParamWidth, isStatic, true, dexTranslationAdvice);
        } else if ("renaming".equals(this.args.ssaStep)) {
            ssaMethod = Optimizer.debugRenaming(convert, computeParamWidth, isStatic, true, dexTranslationAdvice);
        } else if ("dead-code".equals(this.args.ssaStep)) {
            ssaMethod = Optimizer.debugDeadCodeRemover(convert, computeParamWidth, isStatic, true, dexTranslationAdvice);
        }
        StringBuilder sb = new StringBuilder(2000);
        sb.append("first ");
        sb.append(Hex.u2(ssaMethod.blockIndexToRopLabel(ssaMethod.getEntryBlockIndex())));
        sb.append('\n');
        ArrayList arrayList = (ArrayList) ssaMethod.getBlocks().clone();
        Collections.sort(arrayList, SsaBasicBlock.LABEL_COMPARATOR);
        Iterator it = arrayList.iterator();
        while (true) {
            if (it.hasNext()) {
                SsaBasicBlock ssaBasicBlock = (SsaBasicBlock) it.next();
                sb.append("block ").append(Hex.u2(ssaBasicBlock.getRopLabel())).append('\n');
                BitSet predecessors = ssaBasicBlock.getPredecessors();
                for (int nextSetBit = predecessors.nextSetBit(0); nextSetBit >= 0; nextSetBit = predecessors.nextSetBit(nextSetBit + 1)) {
                    sb.append("  pred ");
                    sb.append(Hex.u2(ssaMethod.blockIndexToRopLabel(nextSetBit)));
                    sb.append('\n');
                }
                sb.append("  live in:" + ssaBasicBlock.getLiveInRegs());
                sb.append("\n");
                Iterator<SsaInsn> it2 = ssaBasicBlock.getInsns().iterator();
                while (it2.hasNext()) {
                    sb.append("  ");
                    sb.append(it2.next().toHuman());
                    sb.append('\n');
                }
                if (ssaBasicBlock.getSuccessors().cardinality() == 0) {
                    sb.append("  returns\n");
                } else {
                    int primarySuccessorRopLabel = ssaBasicBlock.getPrimarySuccessorRopLabel();
                    IntList ropLabelSuccessorList = ssaBasicBlock.getRopLabelSuccessorList();
                    int size = ropLabelSuccessorList.size();
                    for (int i2 = 0; i2 < size; i2++) {
                        sb.append("  next ");
                        sb.append(Hex.u2(ropLabelSuccessorList.get(i2)));
                        if (size != 1 && primarySuccessorRopLabel == ropLabelSuccessorList.get(i2)) {
                            sb.append(" *");
                        }
                        sb.append('\n');
                    }
                }
                sb.append("  live out:" + ssaBasicBlock.getLiveOutRegs());
                sb.append("\n");
            } else {
                this.suppressDump = false;
                parsed(byteArray, 0, byteArray.size(), sb.toString());
                this.suppressDump = true;
                return;
            }
        }
    }
}

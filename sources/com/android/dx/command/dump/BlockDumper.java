package com.android.dx.command.dump;

import com.android.dx.cf.code.BasicBlocker;
import com.android.dx.cf.code.ByteBlock;
import com.android.dx.cf.code.ByteBlockList;
import com.android.dx.cf.code.ByteCatchList;
import com.android.dx.cf.code.BytecodeArray;
import com.android.dx.cf.code.ConcreteMethod;
import com.android.dx.cf.code.Ropper;
import com.android.dx.cf.direct.CodeObserver;
import com.android.dx.cf.direct.DirectClassFile;
import com.android.dx.cf.direct.StdAttributeFactory;
import com.android.dx.cf.iface.Member;
import com.android.dx.cf.iface.Method;
import com.android.dx.rop.code.AccessFlags;
import com.android.dx.rop.code.BasicBlock;
import com.android.dx.rop.code.BasicBlockList;
import com.android.dx.rop.code.DexTranslationAdvice;
import com.android.dx.rop.code.InsnList;
import com.android.dx.rop.code.RopMethod;
import com.android.dx.rop.cst.CstType;
import com.android.dx.ssa.Optimizer;
import com.android.dx.util.ByteArray;
import com.android.dx.util.Hex;
import com.android.dx.util.IntList;
import com.xiaopeng.wirelessprojection.dmr.RendererActivity;
import java.io.PrintStream;
/* loaded from: classes.dex */
public class BlockDumper extends BaseDumper {
    protected DirectClassFile classFile;
    private boolean first;
    private final boolean optimize;
    private final boolean rop;
    protected boolean suppressDump;

    public static void dump(byte[] bArr, PrintStream printStream, String str, boolean z, Args args) {
        new BlockDumper(bArr, printStream, str, z, args).dump();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BlockDumper(byte[] bArr, PrintStream printStream, String str, boolean z, Args args) {
        super(bArr, printStream, str, args);
        this.rop = z;
        this.classFile = null;
        this.suppressDump = true;
        this.first = true;
        this.optimize = args.optimize;
    }

    public void dump() {
        ByteArray byteArray = new ByteArray(getBytes());
        DirectClassFile directClassFile = new DirectClassFile(byteArray, getFilePath(), getStrictParse());
        this.classFile = directClassFile;
        directClassFile.setAttributeFactory(StdAttributeFactory.THE_ONE);
        this.classFile.getMagic();
        DirectClassFile directClassFile2 = new DirectClassFile(byteArray, getFilePath(), getStrictParse());
        directClassFile2.setAttributeFactory(StdAttributeFactory.THE_ONE);
        directClassFile2.setObserver(this);
        directClassFile2.getMagic();
    }

    @Override // com.android.dx.command.dump.BaseDumper, com.android.dx.cf.iface.ParseObserver
    public void changeIndent(int i) {
        if (this.suppressDump) {
            return;
        }
        super.changeIndent(i);
    }

    @Override // com.android.dx.command.dump.BaseDumper, com.android.dx.cf.iface.ParseObserver
    public void parsed(ByteArray byteArray, int i, int i2, String str) {
        if (this.suppressDump) {
            return;
        }
        super.parsed(byteArray, i, i2, str);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean shouldDumpMethod(String str) {
        return this.args.method == null || this.args.method.equals(str);
    }

    @Override // com.android.dx.command.dump.BaseDumper, com.android.dx.cf.iface.ParseObserver
    public void startParsingMember(ByteArray byteArray, int i, String str, String str2) {
        if (str2.indexOf(40) >= 0 && shouldDumpMethod(str)) {
            this.suppressDump = false;
            if (this.first) {
                this.first = false;
            } else {
                parsed(byteArray, i, 0, "\n");
            }
            parsed(byteArray, i, 0, "method " + str + RendererActivity.DEFAULT_TITLE + str2);
            this.suppressDump = true;
        }
    }

    @Override // com.android.dx.command.dump.BaseDumper, com.android.dx.cf.iface.ParseObserver
    public void endParsingMember(ByteArray byteArray, int i, String str, String str2, Member member) {
        if ((member instanceof Method) && shouldDumpMethod(str) && (member.getAccessFlags() & 1280) == 0) {
            ConcreteMethod concreteMethod = new ConcreteMethod((Method) member, this.classFile, true, true);
            if (this.rop) {
                ropDump(concreteMethod);
            } else {
                regularDump(concreteMethod);
            }
        }
    }

    private void regularDump(ConcreteMethod concreteMethod) {
        IntList successors;
        ByteCatchList.Item item;
        CstType exceptionClass;
        BytecodeArray code = concreteMethod.getCode();
        ByteArray bytes = code.getBytes();
        ByteBlockList identifyBlocks = BasicBlocker.identifyBlocks(concreteMethod);
        int size = identifyBlocks.size();
        CodeObserver codeObserver = new CodeObserver(bytes, this);
        this.suppressDump = false;
        int i = 0;
        int i2 = 0;
        while (i < size) {
            ByteBlock byteBlock = identifyBlocks.get(i);
            int start = byteBlock.getStart();
            int end = byteBlock.getEnd();
            if (i2 < start) {
                parsed(bytes, i2, start - i2, "dead code " + Hex.u2(i2) + ".." + Hex.u2(start));
            }
            parsed(bytes, start, 0, "block " + Hex.u2(byteBlock.getLabel()) + ": " + Hex.u2(start) + ".." + Hex.u2(end));
            changeIndent(1);
            while (start < end) {
                int parseInstruction = code.parseInstruction(start, codeObserver);
                codeObserver.setPreviousOffset(start);
                start += parseInstruction;
            }
            int size2 = byteBlock.getSuccessors().size();
            if (size2 == 0) {
                parsed(bytes, end, 0, "returns");
            } else {
                for (int i3 = 0; i3 < size2; i3++) {
                    parsed(bytes, end, 0, "next " + Hex.u2(successors.get(i3)));
                }
            }
            ByteCatchList catches = byteBlock.getCatches();
            int size3 = catches.size();
            for (int i4 = 0; i4 < size3; i4++) {
                parsed(bytes, end, 0, "catch " + (catches.get(i4).getExceptionClass() == CstType.OBJECT ? "<any>" : exceptionClass.toHuman()) + " -> " + Hex.u2(item.getHandlerPc()));
            }
            changeIndent(-1);
            i++;
            i2 = end;
        }
        int size4 = bytes.size();
        if (i2 < size4) {
            parsed(bytes, i2, size4 - i2, "dead code " + Hex.u2(i2) + ".." + Hex.u2(size4));
        }
        this.suppressDump = true;
    }

    private void ropDump(ConcreteMethod concreteMethod) {
        DexTranslationAdvice dexTranslationAdvice = DexTranslationAdvice.THE_ONE;
        ByteArray bytes = concreteMethod.getCode().getBytes();
        RopMethod convert = Ropper.convert(concreteMethod, dexTranslationAdvice, this.classFile.getMethods(), this.dexOptions);
        StringBuilder sb = new StringBuilder(2000);
        if (this.optimize) {
            boolean isStatic = AccessFlags.isStatic(concreteMethod.getAccessFlags());
            convert = Optimizer.optimize(convert, computeParamWidth(concreteMethod, isStatic), isStatic, true, dexTranslationAdvice);
        }
        BasicBlockList blocks = convert.getBlocks();
        int[] labelsInOrder = blocks.getLabelsInOrder();
        sb.append("first " + Hex.u2(convert.getFirstLabel()) + "\n");
        for (int i : labelsInOrder) {
            BasicBlock basicBlock = blocks.get(blocks.indexOfLabel(i));
            sb.append("block ");
            sb.append(Hex.u2(i));
            sb.append("\n");
            IntList labelToPredecessors = convert.labelToPredecessors(i);
            int size = labelToPredecessors.size();
            for (int i2 = 0; i2 < size; i2++) {
                sb.append("  pred ");
                sb.append(Hex.u2(labelToPredecessors.get(i2)));
                sb.append("\n");
            }
            InsnList insns = basicBlock.getInsns();
            int size2 = insns.size();
            for (int i3 = 0; i3 < size2; i3++) {
                insns.get(i3);
                sb.append("  ");
                sb.append(insns.get(i3).toHuman());
                sb.append("\n");
            }
            IntList successors = basicBlock.getSuccessors();
            int size3 = successors.size();
            if (size3 == 0) {
                sb.append("  returns\n");
            } else {
                int primarySuccessor = basicBlock.getPrimarySuccessor();
                for (int i4 = 0; i4 < size3; i4++) {
                    int i5 = successors.get(i4);
                    sb.append("  next ");
                    sb.append(Hex.u2(i5));
                    if (size3 != 1 && i5 == primarySuccessor) {
                        sb.append(" *");
                    }
                    sb.append("\n");
                }
            }
        }
        this.suppressDump = false;
        parsed(bytes, 0, bytes.size(), sb.toString());
        this.suppressDump = true;
    }
}

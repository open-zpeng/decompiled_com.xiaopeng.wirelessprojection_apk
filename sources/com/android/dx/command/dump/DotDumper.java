package com.android.dx.command.dump;

import com.android.dx.cf.code.ConcreteMethod;
import com.android.dx.cf.code.Ropper;
import com.android.dx.cf.direct.DirectClassFile;
import com.android.dx.cf.direct.StdAttributeFactory;
import com.android.dx.cf.iface.Member;
import com.android.dx.cf.iface.Method;
import com.android.dx.cf.iface.ParseObserver;
import com.android.dx.dex.DexOptions;
import com.android.dx.rop.code.AccessFlags;
import com.android.dx.rop.code.BasicBlock;
import com.android.dx.rop.code.BasicBlockList;
import com.android.dx.rop.code.DexTranslationAdvice;
import com.android.dx.rop.code.RopMethod;
import com.android.dx.ssa.Optimizer;
import com.android.dx.util.ByteArray;
import com.android.dx.util.Hex;
import com.android.dx.util.IntList;
import com.xiaopeng.wirelessprojection.dmr.RendererActivity;
/* loaded from: classes.dex */
public class DotDumper implements ParseObserver {
    private final Args args;
    private final byte[] bytes;
    private DirectClassFile classFile;
    private final DexOptions dexOptions = new DexOptions();
    private final String filePath;
    private final boolean optimize;
    private final boolean strictParse;

    @Override // com.android.dx.cf.iface.ParseObserver
    public void changeIndent(int i) {
    }

    @Override // com.android.dx.cf.iface.ParseObserver
    public void parsed(ByteArray byteArray, int i, int i2, String str) {
    }

    @Override // com.android.dx.cf.iface.ParseObserver
    public void startParsingMember(ByteArray byteArray, int i, String str, String str2) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void dump(byte[] bArr, String str, Args args) {
        new DotDumper(bArr, str, args).run();
    }

    DotDumper(byte[] bArr, String str, Args args) {
        this.bytes = bArr;
        this.filePath = str;
        this.strictParse = args.strictParse;
        this.optimize = args.optimize;
        this.args = args;
    }

    private void run() {
        ByteArray byteArray = new ByteArray(this.bytes);
        DirectClassFile directClassFile = new DirectClassFile(byteArray, this.filePath, this.strictParse);
        this.classFile = directClassFile;
        directClassFile.setAttributeFactory(StdAttributeFactory.THE_ONE);
        this.classFile.getMagic();
        DirectClassFile directClassFile2 = new DirectClassFile(byteArray, this.filePath, this.strictParse);
        directClassFile2.setAttributeFactory(StdAttributeFactory.THE_ONE);
        directClassFile2.setObserver(this);
        directClassFile2.getMagic();
    }

    protected boolean shouldDumpMethod(String str) {
        return this.args.method == null || this.args.method.equals(str);
    }

    @Override // com.android.dx.cf.iface.ParseObserver
    public void endParsingMember(ByteArray byteArray, int i, String str, String str2, Member member) {
        int i2;
        if ((member instanceof Method) && shouldDumpMethod(str)) {
            ConcreteMethod concreteMethod = new ConcreteMethod((Method) member, this.classFile, true, true);
            DexTranslationAdvice dexTranslationAdvice = DexTranslationAdvice.THE_ONE;
            RopMethod convert = Ropper.convert(concreteMethod, dexTranslationAdvice, this.classFile.getMethods(), this.dexOptions);
            if (this.optimize) {
                boolean isStatic = AccessFlags.isStatic(concreteMethod.getAccessFlags());
                convert = Optimizer.optimize(convert, BaseDumper.computeParamWidth(concreteMethod, isStatic), isStatic, true, dexTranslationAdvice);
            }
            System.out.println("digraph " + str + "{");
            System.out.println("\tfirst -> n" + Hex.u2(convert.getFirstLabel()) + ";");
            BasicBlockList blocks = convert.getBlocks();
            int size = blocks.size();
            int i3 = 0;
            int i4 = 0;
            while (i4 < size) {
                BasicBlock basicBlock = blocks.get(i4);
                int label = basicBlock.getLabel();
                IntList successors = basicBlock.getSuccessors();
                if (successors.size() == 0) {
                    System.out.println("\tn" + Hex.u2(label) + " -> returns;");
                } else if (successors.size() == 1) {
                    System.out.println("\tn" + Hex.u2(label) + " -> n" + Hex.u2(successors.get(i3)) + ";");
                } else {
                    System.out.print("\tn" + Hex.u2(label) + " -> {");
                    for (int i5 = i3; i5 < successors.size(); i5++) {
                        if (successors.get(i5) != basicBlock.getPrimarySuccessor()) {
                            System.out.print(" n" + Hex.u2(i2) + RendererActivity.DEFAULT_TITLE);
                        }
                    }
                    System.out.println("};");
                    System.out.println("\tn" + Hex.u2(label) + " -> n" + Hex.u2(basicBlock.getPrimarySuccessor()) + " [label=\"primary\"];");
                }
                i4++;
                i3 = 0;
            }
            System.out.println("}");
        }
    }
}

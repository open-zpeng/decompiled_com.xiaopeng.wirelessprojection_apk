package com.android.dx.dex.code;

import com.android.dex.util.ExceptionWithContext;
import com.android.dx.rop.cst.Constant;
import com.android.dx.rop.cst.CstBaseMethodRef;
import com.android.dx.rop.cst.CstCallSiteRef;
import com.android.dx.rop.cst.CstProtoRef;
import com.android.dx.util.AnnotatedOutput;
import com.android.dx.util.FixedSizeList;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
/* loaded from: classes.dex */
public final class DalvInsnList extends FixedSizeList {
    private final int regCount;

    public static DalvInsnList makeImmutable(ArrayList<DalvInsn> arrayList, int i) {
        int size = arrayList.size();
        DalvInsnList dalvInsnList = new DalvInsnList(size, i);
        for (int i2 = 0; i2 < size; i2++) {
            dalvInsnList.set(i2, arrayList.get(i2));
        }
        dalvInsnList.setImmutable();
        return dalvInsnList;
    }

    public DalvInsnList(int i, int i2) {
        super(i);
        this.regCount = i2;
    }

    public DalvInsn get(int i) {
        return (DalvInsn) get0(i);
    }

    public void set(int i, DalvInsn dalvInsn) {
        set0(i, dalvInsn);
    }

    public int codeSize() {
        int size = size();
        if (size == 0) {
            return 0;
        }
        return get(size - 1).getNextAddress();
    }

    public void writeTo(AnnotatedOutput annotatedOutput) {
        int cursor = annotatedOutput.getCursor();
        int size = size();
        if (annotatedOutput.annotates()) {
            boolean isVerbose = annotatedOutput.isVerbose();
            for (int i = 0; i < size; i++) {
                DalvInsn dalvInsn = (DalvInsn) get0(i);
                int codeSize = dalvInsn.codeSize() * 2;
                String listingString = (codeSize != 0 || isVerbose) ? dalvInsn.listingString("  ", annotatedOutput.getAnnotationWidth(), true) : null;
                if (listingString != null) {
                    annotatedOutput.annotate(codeSize, listingString);
                } else if (codeSize != 0) {
                    annotatedOutput.annotate(codeSize, "");
                }
            }
        }
        for (int i2 = 0; i2 < size; i2++) {
            DalvInsn dalvInsn2 = (DalvInsn) get0(i2);
            try {
                dalvInsn2.writeTo(annotatedOutput);
            } catch (RuntimeException e) {
                throw ExceptionWithContext.withContext(e, "...while writing " + dalvInsn2);
            }
        }
        int cursor2 = (annotatedOutput.getCursor() - cursor) / 2;
        if (cursor2 != codeSize()) {
            throw new RuntimeException("write length mismatch; expected " + codeSize() + " but actually wrote " + cursor2);
        }
    }

    public int getRegistersSize() {
        return this.regCount;
    }

    public int getOutsSize() {
        int wordCount;
        int size = size();
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            DalvInsn dalvInsn = (DalvInsn) get0(i2);
            if (dalvInsn instanceof CstInsn) {
                Constant constant = ((CstInsn) dalvInsn).getConstant();
                if (constant instanceof CstBaseMethodRef) {
                    wordCount = ((CstBaseMethodRef) constant).getParameterWordCount(dalvInsn.getOpcode().getFamily() == 113);
                } else {
                    wordCount = constant instanceof CstCallSiteRef ? ((CstCallSiteRef) constant).getPrototype().getParameterTypes().getWordCount() : 0;
                }
            } else if (!(dalvInsn instanceof MultiCstInsn)) {
                continue;
            } else if (dalvInsn.getOpcode().getFamily() != 250) {
                throw new RuntimeException("Expecting invoke-polymorphic");
            } else {
                wordCount = ((CstProtoRef) ((MultiCstInsn) dalvInsn).getConstant(1)).getPrototype().getParameterTypes().getWordCount() + 1;
            }
            if (wordCount > i) {
                i = wordCount;
            }
        }
        return i;
    }

    /* JADX WARN: Removed duplicated region for block: B:11:0x0026 A[Catch: IOException -> 0x0030, TryCatch #0 {IOException -> 0x0030, blocks: (B:4:0x000d, B:11:0x0026, B:12:0x0029, B:9:0x001e, B:13:0x002c), top: B:18:0x000d }] */
    /* JADX WARN: Removed duplicated region for block: B:22:0x0029 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void debugPrint(java.io.Writer r5, java.lang.String r6, boolean r7) {
        /*
            r4 = this;
            com.android.dx.util.IndentingWriter r0 = new com.android.dx.util.IndentingWriter
            r1 = 0
            r0.<init>(r5, r1, r6)
            int r5 = r4.size()
            r6 = r1
        Lb:
            if (r6 >= r5) goto L2c
            java.lang.Object r2 = r4.get0(r6)     // Catch: java.io.IOException -> L30
            com.android.dx.dex.code.DalvInsn r2 = (com.android.dx.dex.code.DalvInsn) r2     // Catch: java.io.IOException -> L30
            int r3 = r2.codeSize()     // Catch: java.io.IOException -> L30
            if (r3 != 0) goto L1e
            if (r7 == 0) goto L1c
            goto L1e
        L1c:
            r2 = 0
            goto L24
        L1e:
            java.lang.String r3 = ""
            java.lang.String r2 = r2.listingString(r3, r1, r7)     // Catch: java.io.IOException -> L30
        L24:
            if (r2 == 0) goto L29
            r0.write(r2)     // Catch: java.io.IOException -> L30
        L29:
            int r6 = r6 + 1
            goto Lb
        L2c:
            r0.flush()     // Catch: java.io.IOException -> L30
            return
        L30:
            r5 = move-exception
            java.lang.RuntimeException r6 = new java.lang.RuntimeException
            r6.<init>(r5)
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.dx.dex.code.DalvInsnList.debugPrint(java.io.Writer, java.lang.String, boolean):void");
    }

    public void debugPrint(OutputStream outputStream, String str, boolean z) {
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
        debugPrint(outputStreamWriter, str, z);
        try {
            outputStreamWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

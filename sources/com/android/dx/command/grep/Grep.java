package com.android.dx.command.grep;

import com.android.dex.ClassData;
import com.android.dex.ClassDef;
import com.android.dex.Dex;
import com.android.dex.EncodedValueReader;
import com.android.dx.io.CodeReader;
import com.android.dx.io.instructions.DecodedInstruction;
import com.xiaopeng.wirelessprojection.dmr.RendererActivity;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
/* loaded from: classes.dex */
public final class Grep {
    private final CodeReader codeReader;
    private int count;
    private ClassDef currentClass;
    private ClassData.Method currentMethod;
    private final Dex dex;
    private final PrintWriter out;
    private final Set<Integer> stringIds;

    public Grep(Dex dex, Pattern pattern, PrintWriter printWriter) {
        CodeReader codeReader = new CodeReader();
        this.codeReader = codeReader;
        this.count = 0;
        this.dex = dex;
        this.out = printWriter;
        this.stringIds = getStringIds(dex, pattern);
        codeReader.setStringVisitor(new CodeReader.Visitor() { // from class: com.android.dx.command.grep.Grep.1
            @Override // com.android.dx.io.CodeReader.Visitor
            public void visit(DecodedInstruction[] decodedInstructionArr, DecodedInstruction decodedInstruction) {
                Grep.this.encounterString(decodedInstruction.getIndex());
            }
        });
    }

    private void readArray(EncodedValueReader encodedValueReader) {
        int readArray = encodedValueReader.readArray();
        for (int i = 0; i < readArray; i++) {
            int peek = encodedValueReader.peek();
            if (peek == 23) {
                encounterString(encodedValueReader.readString());
            } else if (peek == 28) {
                readArray(encodedValueReader);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void encounterString(int i) {
        if (this.stringIds.contains(Integer.valueOf(i))) {
            this.out.println(location() + RendererActivity.DEFAULT_TITLE + this.dex.strings().get(i));
            this.count++;
        }
    }

    private String location() {
        String str = this.dex.typeNames().get(this.currentClass.getTypeIndex());
        return this.currentMethod != null ? str + "." + this.dex.strings().get(this.dex.methodIds().get(this.currentMethod.getMethodIndex()).getNameIndex()) : str;
    }

    public int grep() {
        ClassData.Method[] allMethods;
        for (ClassDef classDef : this.dex.classDefs()) {
            this.currentClass = classDef;
            this.currentMethod = null;
            if (classDef.getClassDataOffset() != 0) {
                ClassData readClassData = this.dex.readClassData(classDef);
                int staticValuesOffset = classDef.getStaticValuesOffset();
                if (staticValuesOffset != 0) {
                    readArray(new EncodedValueReader(this.dex.open(staticValuesOffset)));
                }
                for (ClassData.Method method : readClassData.allMethods()) {
                    this.currentMethod = method;
                    if (method.getCodeOffset() != 0) {
                        this.codeReader.visitAll(this.dex.readCode(method).getInstructions());
                    }
                }
            }
        }
        this.currentClass = null;
        this.currentMethod = null;
        return this.count;
    }

    private Set<Integer> getStringIds(Dex dex, Pattern pattern) {
        HashSet hashSet = new HashSet();
        int i = 0;
        for (String str : dex.strings()) {
            if (pattern.matcher(str).find()) {
                hashSet.add(Integer.valueOf(i));
            }
            i++;
        }
        return hashSet;
    }
}

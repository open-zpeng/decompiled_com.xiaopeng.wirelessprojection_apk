package com.android.dx.command.findusages;

import com.android.dex.ClassData;
import com.android.dex.ClassDef;
import com.android.dex.Dex;
import com.android.dex.FieldId;
import com.android.dex.MethodId;
import com.android.dx.io.CodeReader;
import com.android.dx.io.OpcodeInfo;
import com.android.dx.io.instructions.DecodedInstruction;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
/* loaded from: classes.dex */
public final class FindUsages {
    private final CodeReader codeReader = new CodeReader();
    private ClassDef currentClass;
    private ClassData.Method currentMethod;
    private final Dex dex;
    private final Set<Integer> fieldIds;
    private final Set<Integer> methodIds;
    private final PrintWriter out;

    public FindUsages(final Dex dex, String str, String str2, final PrintWriter printWriter) {
        this.dex = dex;
        this.out = printWriter;
        HashSet<Integer> hashSet = new HashSet();
        HashSet hashSet2 = new HashSet();
        Pattern compile = Pattern.compile(str);
        Pattern compile2 = Pattern.compile(str2);
        List<String> strings = dex.strings();
        for (int i = 0; i < strings.size(); i++) {
            String str3 = strings.get(i);
            if (compile.matcher(str3).matches()) {
                hashSet.add(Integer.valueOf(i));
            }
            if (compile2.matcher(str3).matches()) {
                hashSet2.add(Integer.valueOf(i));
            }
        }
        if (hashSet.isEmpty() || hashSet2.isEmpty()) {
            this.fieldIds = null;
            this.methodIds = null;
            return;
        }
        this.methodIds = new HashSet();
        this.fieldIds = new HashSet();
        for (Integer num : hashSet) {
            int binarySearch = Collections.binarySearch(dex.typeIds(), Integer.valueOf(num.intValue()));
            if (binarySearch >= 0) {
                this.methodIds.addAll(getMethodIds(dex, hashSet2, binarySearch));
                this.fieldIds.addAll(getFieldIds(dex, hashSet2, binarySearch));
            }
        }
        this.codeReader.setFieldVisitor(new CodeReader.Visitor() { // from class: com.android.dx.command.findusages.FindUsages.1
            @Override // com.android.dx.io.CodeReader.Visitor
            public void visit(DecodedInstruction[] decodedInstructionArr, DecodedInstruction decodedInstruction) {
                int index = decodedInstruction.getIndex();
                if (FindUsages.this.fieldIds.contains(Integer.valueOf(index))) {
                    printWriter.println(FindUsages.this.location() + ": field reference " + dex.fieldIds().get(index) + " (" + OpcodeInfo.getName(decodedInstruction.getOpcode()) + ")");
                }
            }
        });
        this.codeReader.setMethodVisitor(new CodeReader.Visitor() { // from class: com.android.dx.command.findusages.FindUsages.2
            @Override // com.android.dx.io.CodeReader.Visitor
            public void visit(DecodedInstruction[] decodedInstructionArr, DecodedInstruction decodedInstruction) {
                int index = decodedInstruction.getIndex();
                if (FindUsages.this.methodIds.contains(Integer.valueOf(index))) {
                    printWriter.println(FindUsages.this.location() + ": method reference " + dex.methodIds().get(index) + " (" + OpcodeInfo.getName(decodedInstruction.getOpcode()) + ")");
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String location() {
        String str = this.dex.typeNames().get(this.currentClass.getTypeIndex());
        return this.currentMethod != null ? str + "." + this.dex.strings().get(this.dex.methodIds().get(this.currentMethod.getMethodIndex()).getNameIndex()) : str;
    }

    public void findUsages() {
        ClassData.Method[] allMethods;
        int methodIndex;
        int fieldIndex;
        if (this.fieldIds == null || this.methodIds == null) {
            return;
        }
        for (ClassDef classDef : this.dex.classDefs()) {
            this.currentClass = classDef;
            this.currentMethod = null;
            if (classDef.getClassDataOffset() != 0) {
                ClassData readClassData = this.dex.readClassData(classDef);
                for (ClassData.Field field : readClassData.allFields()) {
                    if (this.fieldIds.contains(Integer.valueOf(field.getFieldIndex()))) {
                        this.out.println(location() + " field declared " + this.dex.fieldIds().get(fieldIndex));
                    }
                }
                for (ClassData.Method method : readClassData.allMethods()) {
                    this.currentMethod = method;
                    if (this.methodIds.contains(Integer.valueOf(method.getMethodIndex()))) {
                        this.out.println(location() + " method declared " + this.dex.methodIds().get(methodIndex));
                    }
                    if (method.getCodeOffset() != 0) {
                        this.codeReader.visitAll(this.dex.readCode(method).getInstructions());
                    }
                }
            }
        }
        this.currentClass = null;
        this.currentMethod = null;
    }

    private Set<Integer> getFieldIds(Dex dex, Set<Integer> set, int i) {
        HashSet hashSet = new HashSet();
        int i2 = 0;
        for (FieldId fieldId : dex.fieldIds()) {
            if (set.contains(Integer.valueOf(fieldId.getNameIndex())) && i == fieldId.getDeclaringClassIndex()) {
                hashSet.add(Integer.valueOf(i2));
            }
            i2++;
        }
        return hashSet;
    }

    private Set<Integer> getMethodIds(Dex dex, Set<Integer> set, int i) {
        Set<Integer> findAssignableTypes = findAssignableTypes(dex, i);
        HashSet hashSet = new HashSet();
        int i2 = 0;
        for (MethodId methodId : dex.methodIds()) {
            if (set.contains(Integer.valueOf(methodId.getNameIndex())) && findAssignableTypes.contains(Integer.valueOf(methodId.getDeclaringClassIndex()))) {
                hashSet.add(Integer.valueOf(i2));
            }
            i2++;
        }
        return hashSet;
    }

    private Set<Integer> findAssignableTypes(Dex dex, int i) {
        HashSet hashSet = new HashSet();
        hashSet.add(Integer.valueOf(i));
        for (ClassDef classDef : dex.classDefs()) {
            if (hashSet.contains(Integer.valueOf(classDef.getSupertypeIndex()))) {
                hashSet.add(Integer.valueOf(classDef.getTypeIndex()));
            } else {
                short[] interfaces = classDef.getInterfaces();
                int length = interfaces.length;
                int i2 = 0;
                while (true) {
                    if (i2 >= length) {
                        break;
                    } else if (hashSet.contains(Integer.valueOf(interfaces[i2]))) {
                        hashSet.add(Integer.valueOf(classDef.getTypeIndex()));
                        break;
                    } else {
                        i2++;
                    }
                }
            }
        }
        return hashSet;
    }
}

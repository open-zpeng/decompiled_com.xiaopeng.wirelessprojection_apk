package com.android.dx.cf.code;

import com.android.dx.cf.attrib.AttCode;
import com.android.dx.cf.attrib.AttLineNumberTable;
import com.android.dx.cf.attrib.AttLocalVariableTable;
import com.android.dx.cf.attrib.AttLocalVariableTypeTable;
import com.android.dx.cf.iface.AttributeList;
import com.android.dx.cf.iface.ClassFile;
import com.android.dx.cf.iface.Method;
import com.android.dx.rop.code.SourcePosition;
import com.android.dx.rop.cst.CstNat;
import com.android.dx.rop.cst.CstString;
import com.android.dx.rop.cst.CstType;
import com.android.dx.rop.type.Prototype;
/* loaded from: classes.dex */
public final class ConcreteMethod implements Method {
    private final AttCode attCode;
    private final ClassFile classFile;
    private final LineNumberList lineNumbers;
    private final LocalVariableList localVariables;
    private final Method method;

    public ConcreteMethod(Method method, ClassFile classFile, boolean z, boolean z2) {
        this.method = method;
        this.classFile = classFile;
        AttCode attCode = (AttCode) method.getAttributes().findFirst(AttCode.ATTRIBUTE_NAME);
        this.attCode = attCode;
        AttributeList attributes = attCode.getAttributes();
        LineNumberList lineNumberList = LineNumberList.EMPTY;
        if (z) {
            for (AttLineNumberTable attLineNumberTable = (AttLineNumberTable) attributes.findFirst(AttLineNumberTable.ATTRIBUTE_NAME); attLineNumberTable != null; attLineNumberTable = (AttLineNumberTable) attributes.findNext(attLineNumberTable)) {
                lineNumberList = LineNumberList.concat(lineNumberList, attLineNumberTable.getLineNumbers());
            }
        }
        this.lineNumbers = lineNumberList;
        LocalVariableList localVariableList = LocalVariableList.EMPTY;
        if (z2) {
            for (AttLocalVariableTable attLocalVariableTable = (AttLocalVariableTable) attributes.findFirst(AttLocalVariableTable.ATTRIBUTE_NAME); attLocalVariableTable != null; attLocalVariableTable = (AttLocalVariableTable) attributes.findNext(attLocalVariableTable)) {
                localVariableList = LocalVariableList.concat(localVariableList, attLocalVariableTable.getLocalVariables());
            }
            LocalVariableList localVariableList2 = LocalVariableList.EMPTY;
            for (AttLocalVariableTypeTable attLocalVariableTypeTable = (AttLocalVariableTypeTable) attributes.findFirst(AttLocalVariableTypeTable.ATTRIBUTE_NAME); attLocalVariableTypeTable != null; attLocalVariableTypeTable = (AttLocalVariableTypeTable) attributes.findNext(attLocalVariableTypeTable)) {
                localVariableList2 = LocalVariableList.concat(localVariableList2, attLocalVariableTypeTable.getLocalVariables());
            }
            if (localVariableList2.size() != 0) {
                localVariableList = LocalVariableList.mergeDescriptorsAndSignatures(localVariableList, localVariableList2);
            }
        }
        this.localVariables = localVariableList;
    }

    public CstString getSourceFile() {
        return this.classFile.getSourceFile();
    }

    public final boolean isDefaultOrStaticInterfaceMethod() {
        return ((this.classFile.getAccessFlags() & 512) == 0 || getNat().isClassInit()) ? false : true;
    }

    public final boolean isStaticMethod() {
        return (getAccessFlags() & 8) != 0;
    }

    @Override // com.android.dx.cf.iface.Member
    public CstNat getNat() {
        return this.method.getNat();
    }

    @Override // com.android.dx.cf.iface.Member
    public CstString getName() {
        return this.method.getName();
    }

    @Override // com.android.dx.cf.iface.Member
    public CstString getDescriptor() {
        return this.method.getDescriptor();
    }

    @Override // com.android.dx.cf.iface.Member
    public int getAccessFlags() {
        return this.method.getAccessFlags();
    }

    @Override // com.android.dx.cf.iface.Member, com.android.dx.cf.iface.HasAttribute
    public AttributeList getAttributes() {
        return this.method.getAttributes();
    }

    @Override // com.android.dx.cf.iface.Member
    public CstType getDefiningClass() {
        return this.method.getDefiningClass();
    }

    @Override // com.android.dx.cf.iface.Method
    public Prototype getEffectiveDescriptor() {
        return this.method.getEffectiveDescriptor();
    }

    public int getMaxStack() {
        return this.attCode.getMaxStack();
    }

    public int getMaxLocals() {
        return this.attCode.getMaxLocals();
    }

    public BytecodeArray getCode() {
        return this.attCode.getCode();
    }

    public ByteCatchList getCatches() {
        return this.attCode.getCatches();
    }

    public LineNumberList getLineNumbers() {
        return this.lineNumbers;
    }

    public LocalVariableList getLocalVariables() {
        return this.localVariables;
    }

    public SourcePosition makeSourcePosistion(int i) {
        return new SourcePosition(getSourceFile(), i, this.lineNumbers.pcToLine(i));
    }
}

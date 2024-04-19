package com.android.dx.dex.file;

import com.android.dx.rop.cst.Constant;
import com.android.dx.rop.cst.CstType;
import com.android.dx.rop.type.Type;
import com.android.dx.rop.type.TypeList;
import com.android.dx.util.AnnotatedOutput;
import com.android.dx.util.Hex;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.TreeMap;
/* loaded from: classes.dex */
public final class ClassDefsSection extends UniformItemSection {
    private final TreeMap<Type, ClassDefItem> classDefs;
    private ArrayList<ClassDefItem> orderedDefs;

    public ClassDefsSection(DexFile dexFile) {
        super("class_defs", dexFile, 4);
        this.classDefs = new TreeMap<>();
        this.orderedDefs = null;
    }

    @Override // com.android.dx.dex.file.Section
    public Collection<? extends Item> items() {
        ArrayList<ClassDefItem> arrayList = this.orderedDefs;
        return arrayList != null ? arrayList : this.classDefs.values();
    }

    @Override // com.android.dx.dex.file.UniformItemSection
    public IndexedItem get(Constant constant) {
        Objects.requireNonNull(constant, "cst == null");
        throwIfNotPrepared();
        ClassDefItem classDefItem = this.classDefs.get(((CstType) constant).getClassType());
        if (classDefItem != null) {
            return classDefItem;
        }
        throw new IllegalArgumentException("not found");
    }

    public void writeHeaderPart(AnnotatedOutput annotatedOutput) {
        throwIfNotPrepared();
        int size = this.classDefs.size();
        int fileOffset = size == 0 ? 0 : getFileOffset();
        if (annotatedOutput.annotates()) {
            annotatedOutput.annotate(4, "class_defs_size: " + Hex.u4(size));
            annotatedOutput.annotate(4, "class_defs_off:  " + Hex.u4(fileOffset));
        }
        annotatedOutput.writeInt(size);
        annotatedOutput.writeInt(fileOffset);
    }

    public void add(ClassDefItem classDefItem) {
        try {
            Type classType = classDefItem.getThisClass().getClassType();
            throwIfPrepared();
            if (this.classDefs.get(classType) != null) {
                throw new IllegalArgumentException("already added: " + classType);
            }
            this.classDefs.put(classType, classDefItem);
        } catch (NullPointerException unused) {
            throw new NullPointerException("clazz == null");
        }
    }

    @Override // com.android.dx.dex.file.UniformItemSection
    protected void orderItems() {
        int size = this.classDefs.size();
        this.orderedDefs = new ArrayList<>(size);
        int i = 0;
        for (Type type : this.classDefs.keySet()) {
            i = orderItems0(type, i, size - i);
        }
    }

    private int orderItems0(Type type, int i, int i2) {
        ClassDefItem classDefItem = this.classDefs.get(type);
        if (classDefItem == null || classDefItem.hasIndex()) {
            return i;
        }
        if (i2 < 0) {
            throw new RuntimeException("class circularity with " + type);
        }
        int i3 = i2 - 1;
        CstType superclass = classDefItem.getSuperclass();
        if (superclass != null) {
            i = orderItems0(superclass.getClassType(), i, i3);
        }
        TypeList interfaces = classDefItem.getInterfaces();
        int size = interfaces.size();
        for (int i4 = 0; i4 < size; i4++) {
            i = orderItems0(interfaces.getType(i4), i, i3);
        }
        classDefItem.setIndex(i);
        this.orderedDefs.add(classDefItem);
        return i + 1;
    }
}

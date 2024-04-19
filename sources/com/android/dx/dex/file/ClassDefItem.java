package com.android.dx.dex.file;

import com.android.dx.rop.annotation.Annotations;
import com.android.dx.rop.annotation.AnnotationsList;
import com.android.dx.rop.code.AccessFlags;
import com.android.dx.rop.cst.Constant;
import com.android.dx.rop.cst.CstArray;
import com.android.dx.rop.cst.CstFieldRef;
import com.android.dx.rop.cst.CstMethodRef;
import com.android.dx.rop.cst.CstString;
import com.android.dx.rop.cst.CstType;
import com.android.dx.rop.type.StdTypeList;
import com.android.dx.rop.type.TypeList;
import com.android.dx.util.AnnotatedOutput;
import com.android.dx.util.Hex;
import com.android.dx.util.Writers;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes.dex */
public final class ClassDefItem extends IndexedItem {
    private final int accessFlags;
    private AnnotationsDirectoryItem annotationsDirectory;
    private final ClassDataItem classData;
    private TypeListItem interfaces;
    private final CstString sourceFile;
    private EncodedArrayItem staticValuesItem;
    private final CstType superclass;
    private final CstType thisClass;

    @Override // com.android.dx.dex.file.Item
    public int writeSize() {
        return 32;
    }

    public ClassDefItem(CstType cstType, int i, CstType cstType2, TypeList typeList, CstString cstString) {
        Objects.requireNonNull(cstType, "thisClass == null");
        Objects.requireNonNull(typeList, "interfaces == null");
        this.thisClass = cstType;
        this.accessFlags = i;
        this.superclass = cstType2;
        this.interfaces = typeList.size() == 0 ? null : new TypeListItem(typeList);
        this.sourceFile = cstString;
        this.classData = new ClassDataItem(cstType);
        this.staticValuesItem = null;
        this.annotationsDirectory = new AnnotationsDirectoryItem();
    }

    @Override // com.android.dx.dex.file.Item
    public ItemType itemType() {
        return ItemType.TYPE_CLASS_DEF_ITEM;
    }

    @Override // com.android.dx.dex.file.Item
    public void addContents(DexFile dexFile) {
        TypeIdsSection typeIds = dexFile.getTypeIds();
        MixedItemSection byteData = dexFile.getByteData();
        MixedItemSection wordData = dexFile.getWordData();
        MixedItemSection typeLists = dexFile.getTypeLists();
        StringIdsSection stringIds = dexFile.getStringIds();
        typeIds.intern(this.thisClass);
        if (!this.classData.isEmpty()) {
            dexFile.getClassData().add(this.classData);
            CstArray staticValuesConstant = this.classData.getStaticValuesConstant();
            if (staticValuesConstant != null) {
                this.staticValuesItem = (EncodedArrayItem) byteData.intern(new EncodedArrayItem(staticValuesConstant));
            }
        }
        CstType cstType = this.superclass;
        if (cstType != null) {
            typeIds.intern(cstType);
        }
        TypeListItem typeListItem = this.interfaces;
        if (typeListItem != null) {
            this.interfaces = (TypeListItem) typeLists.intern(typeListItem);
        }
        CstString cstString = this.sourceFile;
        if (cstString != null) {
            stringIds.intern(cstString);
        }
        if (this.annotationsDirectory.isEmpty()) {
            return;
        }
        if (this.annotationsDirectory.isInternable()) {
            this.annotationsDirectory = (AnnotationsDirectoryItem) wordData.intern(this.annotationsDirectory);
        } else {
            wordData.add(this.annotationsDirectory);
        }
    }

    @Override // com.android.dx.dex.file.Item
    public void writeTo(DexFile dexFile, AnnotatedOutput annotatedOutput) {
        TypeList list;
        boolean annotates = annotatedOutput.annotates();
        TypeIdsSection typeIds = dexFile.getTypeIds();
        int indexOf = typeIds.indexOf(this.thisClass);
        CstType cstType = this.superclass;
        int indexOf2 = cstType == null ? -1 : typeIds.indexOf(cstType);
        int absoluteOffsetOr0 = OffsettedItem.getAbsoluteOffsetOr0(this.interfaces);
        int absoluteOffset = this.annotationsDirectory.isEmpty() ? 0 : this.annotationsDirectory.getAbsoluteOffset();
        int indexOf3 = this.sourceFile != null ? dexFile.getStringIds().indexOf(this.sourceFile) : -1;
        int absoluteOffset2 = this.classData.isEmpty() ? 0 : this.classData.getAbsoluteOffset();
        int absoluteOffsetOr02 = OffsettedItem.getAbsoluteOffsetOr0(this.staticValuesItem);
        if (annotates) {
            annotatedOutput.annotate(0, indexString() + ' ' + this.thisClass.toHuman());
            annotatedOutput.annotate(4, "  class_idx:           " + Hex.u4(indexOf));
            annotatedOutput.annotate(4, "  access_flags:        " + AccessFlags.classString(this.accessFlags));
            StringBuilder append = new StringBuilder().append("  superclass_idx:      ").append(Hex.u4(indexOf2)).append(" // ");
            CstType cstType2 = this.superclass;
            annotatedOutput.annotate(4, append.append(cstType2 == null ? "<none>" : cstType2.toHuman()).toString());
            annotatedOutput.annotate(4, "  interfaces_off:      " + Hex.u4(absoluteOffsetOr0));
            if (absoluteOffsetOr0 != 0) {
                int size = this.interfaces.getList().size();
                for (int i = 0; i < size; i++) {
                    annotatedOutput.annotate(0, "    " + list.getType(i).toHuman());
                }
            }
            StringBuilder append2 = new StringBuilder().append("  source_file_idx:     ").append(Hex.u4(indexOf3)).append(" // ");
            CstString cstString = this.sourceFile;
            annotatedOutput.annotate(4, append2.append(cstString != null ? cstString.toHuman() : "<none>").toString());
            annotatedOutput.annotate(4, "  annotations_off:     " + Hex.u4(absoluteOffset));
            annotatedOutput.annotate(4, "  class_data_off:      " + Hex.u4(absoluteOffset2));
            annotatedOutput.annotate(4, "  static_values_off:   " + Hex.u4(absoluteOffsetOr02));
        }
        annotatedOutput.writeInt(indexOf);
        annotatedOutput.writeInt(this.accessFlags);
        annotatedOutput.writeInt(indexOf2);
        annotatedOutput.writeInt(absoluteOffsetOr0);
        annotatedOutput.writeInt(indexOf3);
        annotatedOutput.writeInt(absoluteOffset);
        annotatedOutput.writeInt(absoluteOffset2);
        annotatedOutput.writeInt(absoluteOffsetOr02);
    }

    public CstType getThisClass() {
        return this.thisClass;
    }

    public int getAccessFlags() {
        return this.accessFlags;
    }

    public CstType getSuperclass() {
        return this.superclass;
    }

    public TypeList getInterfaces() {
        TypeListItem typeListItem = this.interfaces;
        if (typeListItem == null) {
            return StdTypeList.EMPTY;
        }
        return typeListItem.getList();
    }

    public CstString getSourceFile() {
        return this.sourceFile;
    }

    public void addStaticField(EncodedField encodedField, Constant constant) {
        this.classData.addStaticField(encodedField, constant);
    }

    public void addInstanceField(EncodedField encodedField) {
        this.classData.addInstanceField(encodedField);
    }

    public void addDirectMethod(EncodedMethod encodedMethod) {
        this.classData.addDirectMethod(encodedMethod);
    }

    public void addVirtualMethod(EncodedMethod encodedMethod) {
        this.classData.addVirtualMethod(encodedMethod);
    }

    public ArrayList<EncodedMethod> getMethods() {
        return this.classData.getMethods();
    }

    public void setClassAnnotations(Annotations annotations, DexFile dexFile) {
        this.annotationsDirectory.setClassAnnotations(annotations, dexFile);
    }

    public void addFieldAnnotations(CstFieldRef cstFieldRef, Annotations annotations, DexFile dexFile) {
        this.annotationsDirectory.addFieldAnnotations(cstFieldRef, annotations, dexFile);
    }

    public void addMethodAnnotations(CstMethodRef cstMethodRef, Annotations annotations, DexFile dexFile) {
        this.annotationsDirectory.addMethodAnnotations(cstMethodRef, annotations, dexFile);
    }

    public void addParameterAnnotations(CstMethodRef cstMethodRef, AnnotationsList annotationsList, DexFile dexFile) {
        this.annotationsDirectory.addParameterAnnotations(cstMethodRef, annotationsList, dexFile);
    }

    public Annotations getMethodAnnotations(CstMethodRef cstMethodRef) {
        return this.annotationsDirectory.getMethodAnnotations(cstMethodRef);
    }

    public AnnotationsList getParameterAnnotations(CstMethodRef cstMethodRef) {
        return this.annotationsDirectory.getParameterAnnotations(cstMethodRef);
    }

    public void debugPrint(Writer writer, boolean z) {
        PrintWriter printWriterFor = Writers.printWriterFor(writer);
        printWriterFor.println(getClass().getName() + " {");
        printWriterFor.println("  accessFlags: " + Hex.u2(this.accessFlags));
        printWriterFor.println("  superclass: " + this.superclass);
        StringBuilder append = new StringBuilder().append("  interfaces: ");
        TypeListItem typeListItem = this.interfaces;
        if (typeListItem == null) {
            typeListItem = "<none>";
        }
        printWriterFor.println(append.append(typeListItem).toString());
        StringBuilder append2 = new StringBuilder().append("  sourceFile: ");
        CstString cstString = this.sourceFile;
        printWriterFor.println(append2.append(cstString != null ? cstString.toQuoted() : "<none>").toString());
        this.classData.debugPrint(writer, z);
        this.annotationsDirectory.debugPrint(printWriterFor);
        printWriterFor.println("}");
    }
}

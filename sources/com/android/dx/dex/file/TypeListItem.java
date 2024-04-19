package com.android.dx.dex.file;

import com.android.dx.rop.type.StdTypeList;
import com.android.dx.rop.type.Type;
import com.android.dx.rop.type.TypeList;
import com.android.dx.util.AnnotatedOutput;
import com.android.dx.util.Hex;
import com.xiaopeng.speech.vui.constants.VuiConstants;
/* loaded from: classes.dex */
public final class TypeListItem extends OffsettedItem {
    private static final int ALIGNMENT = 4;
    private static final int ELEMENT_SIZE = 2;
    private static final int HEADER_SIZE = 4;
    private final TypeList list;

    public TypeListItem(TypeList typeList) {
        super(4, (typeList.size() * 2) + 4);
        this.list = typeList;
    }

    public int hashCode() {
        return StdTypeList.hashContents(this.list);
    }

    @Override // com.android.dx.dex.file.Item
    public ItemType itemType() {
        return ItemType.TYPE_TYPE_LIST;
    }

    @Override // com.android.dx.dex.file.Item
    public void addContents(DexFile dexFile) {
        TypeIdsSection typeIds = dexFile.getTypeIds();
        int size = this.list.size();
        for (int i = 0; i < size; i++) {
            typeIds.intern(this.list.getType(i));
        }
    }

    @Override // com.android.dx.dex.file.OffsettedItem
    public String toHuman() {
        throw new RuntimeException(VuiConstants.PROPS_UNSUPPORTED);
    }

    public TypeList getList() {
        return this.list;
    }

    @Override // com.android.dx.dex.file.OffsettedItem
    protected void writeTo0(DexFile dexFile, AnnotatedOutput annotatedOutput) {
        TypeIdsSection typeIds = dexFile.getTypeIds();
        int size = this.list.size();
        if (annotatedOutput.annotates()) {
            annotatedOutput.annotate(0, offsetString() + " type_list");
            annotatedOutput.annotate(4, "  size: " + Hex.u4(size));
            for (int i = 0; i < size; i++) {
                Type type = this.list.getType(i);
                annotatedOutput.annotate(2, "  " + Hex.u2(typeIds.indexOf(type)) + " // " + type.toHuman());
            }
        }
        annotatedOutput.writeInt(size);
        for (int i2 = 0; i2 < size; i2++) {
            annotatedOutput.writeShort(typeIds.indexOf(this.list.getType(i2)));
        }
    }

    @Override // com.android.dx.dex.file.OffsettedItem
    protected int compareTo0(OffsettedItem offsettedItem) {
        return StdTypeList.compareContents(this.list, ((TypeListItem) offsettedItem).list);
    }
}

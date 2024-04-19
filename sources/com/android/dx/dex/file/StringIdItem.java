package com.android.dx.dex.file;

import com.android.dx.rop.cst.Constant;
import com.android.dx.rop.cst.CstString;
import com.android.dx.util.AnnotatedOutput;
import com.android.dx.util.Hex;
import java.util.Objects;
/* loaded from: classes.dex */
public final class StringIdItem extends IndexedItem implements Comparable {
    private StringDataItem data;
    private final CstString value;

    @Override // com.android.dx.dex.file.Item
    public int writeSize() {
        return 4;
    }

    public StringIdItem(CstString cstString) {
        Objects.requireNonNull(cstString, "value == null");
        this.value = cstString;
        this.data = null;
    }

    public boolean equals(Object obj) {
        if (obj instanceof StringIdItem) {
            return this.value.equals(((StringIdItem) obj).value);
        }
        return false;
    }

    public int hashCode() {
        return this.value.hashCode();
    }

    @Override // java.lang.Comparable
    public int compareTo(Object obj) {
        return this.value.compareTo((Constant) ((StringIdItem) obj).value);
    }

    @Override // com.android.dx.dex.file.Item
    public ItemType itemType() {
        return ItemType.TYPE_STRING_ID_ITEM;
    }

    @Override // com.android.dx.dex.file.Item
    public void addContents(DexFile dexFile) {
        if (this.data == null) {
            MixedItemSection stringData = dexFile.getStringData();
            StringDataItem stringDataItem = new StringDataItem(this.value);
            this.data = stringDataItem;
            stringData.add(stringDataItem);
        }
    }

    @Override // com.android.dx.dex.file.Item
    public void writeTo(DexFile dexFile, AnnotatedOutput annotatedOutput) {
        int absoluteOffset = this.data.getAbsoluteOffset();
        if (annotatedOutput.annotates()) {
            annotatedOutput.annotate(0, indexString() + ' ' + this.value.toQuoted(100));
            annotatedOutput.annotate(4, "  string_data_off: " + Hex.u4(absoluteOffset));
        }
        annotatedOutput.writeInt(absoluteOffset);
    }

    public CstString getValue() {
        return this.value;
    }

    public StringDataItem getData() {
        return this.data;
    }
}

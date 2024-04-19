package com.android.dx.dex.file;

import com.android.dx.rop.cst.Constant;
import com.android.dx.rop.cst.CstNat;
import com.android.dx.rop.cst.CstString;
import com.android.dx.util.AnnotatedOutput;
import com.android.dx.util.Hex;
import java.util.Collection;
import java.util.Objects;
import java.util.TreeMap;
/* loaded from: classes.dex */
public final class StringIdsSection extends UniformItemSection {
    private final TreeMap<CstString, StringIdItem> strings;

    public StringIdsSection(DexFile dexFile) {
        super("string_ids", dexFile, 4);
        this.strings = new TreeMap<>();
    }

    @Override // com.android.dx.dex.file.Section
    public Collection<? extends Item> items() {
        return this.strings.values();
    }

    @Override // com.android.dx.dex.file.UniformItemSection
    public IndexedItem get(Constant constant) {
        Objects.requireNonNull(constant, "cst == null");
        throwIfNotPrepared();
        StringIdItem stringIdItem = this.strings.get((CstString) constant);
        if (stringIdItem != null) {
            return stringIdItem;
        }
        throw new IllegalArgumentException("not found");
    }

    public void writeHeaderPart(AnnotatedOutput annotatedOutput) {
        throwIfNotPrepared();
        int size = this.strings.size();
        int fileOffset = size == 0 ? 0 : getFileOffset();
        if (annotatedOutput.annotates()) {
            annotatedOutput.annotate(4, "string_ids_size: " + Hex.u4(size));
            annotatedOutput.annotate(4, "string_ids_off:  " + Hex.u4(fileOffset));
        }
        annotatedOutput.writeInt(size);
        annotatedOutput.writeInt(fileOffset);
    }

    public StringIdItem intern(String str) {
        return intern(new StringIdItem(new CstString(str)));
    }

    public StringIdItem intern(CstString cstString) {
        return intern(new StringIdItem(cstString));
    }

    public synchronized StringIdItem intern(StringIdItem stringIdItem) {
        if (stringIdItem == null) {
            throw new NullPointerException("string == null");
        }
        throwIfPrepared();
        CstString value = stringIdItem.getValue();
        StringIdItem stringIdItem2 = this.strings.get(value);
        if (stringIdItem2 != null) {
            return stringIdItem2;
        }
        this.strings.put(value, stringIdItem);
        return stringIdItem;
    }

    public synchronized void intern(CstNat cstNat) {
        intern(cstNat.getName());
        intern(cstNat.getDescriptor());
    }

    public int indexOf(CstString cstString) {
        Objects.requireNonNull(cstString, "string == null");
        throwIfNotPrepared();
        StringIdItem stringIdItem = this.strings.get(cstString);
        if (stringIdItem == null) {
            throw new IllegalArgumentException("not found");
        }
        return stringIdItem.getIndex();
    }

    @Override // com.android.dx.dex.file.UniformItemSection
    protected void orderItems() {
        int i = 0;
        for (StringIdItem stringIdItem : this.strings.values()) {
            stringIdItem.setIndex(i);
            i++;
        }
    }
}

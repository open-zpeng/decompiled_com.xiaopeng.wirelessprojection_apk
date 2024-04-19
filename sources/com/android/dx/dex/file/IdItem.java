package com.android.dx.dex.file;

import com.android.dx.rop.cst.CstType;
import java.util.Objects;
/* loaded from: classes.dex */
public abstract class IdItem extends IndexedItem {
    private final CstType type;

    public IdItem(CstType cstType) {
        Objects.requireNonNull(cstType, "type == null");
        this.type = cstType;
    }

    @Override // com.android.dx.dex.file.Item
    public void addContents(DexFile dexFile) {
        dexFile.getTypeIds().intern(this.type);
    }

    public final CstType getDefiningClass() {
        return this.type;
    }
}

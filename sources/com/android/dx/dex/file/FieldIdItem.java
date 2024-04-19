package com.android.dx.dex.file;

import com.android.dx.rop.cst.CstFieldRef;
/* loaded from: classes.dex */
public final class FieldIdItem extends MemberIdItem {
    @Override // com.android.dx.dex.file.MemberIdItem
    protected String getTypoidName() {
        return "type_idx";
    }

    public FieldIdItem(CstFieldRef cstFieldRef) {
        super(cstFieldRef);
    }

    @Override // com.android.dx.dex.file.Item
    public ItemType itemType() {
        return ItemType.TYPE_FIELD_ID_ITEM;
    }

    @Override // com.android.dx.dex.file.MemberIdItem, com.android.dx.dex.file.IdItem, com.android.dx.dex.file.Item
    public void addContents(DexFile dexFile) {
        super.addContents(dexFile);
        dexFile.getTypeIds().intern(getFieldRef().getType());
    }

    public CstFieldRef getFieldRef() {
        return (CstFieldRef) getRef();
    }

    @Override // com.android.dx.dex.file.MemberIdItem
    protected int getTypoidIdx(DexFile dexFile) {
        return dexFile.getTypeIds().indexOf(getFieldRef().getType());
    }
}

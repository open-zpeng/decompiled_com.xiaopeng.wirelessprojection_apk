package com.android.dx.dex.file;

import com.android.dx.rop.cst.CstString;
import com.android.dx.rop.type.Prototype;
import com.android.dx.rop.type.StdTypeList;
import com.android.dx.rop.type.Type;
import com.android.dx.util.AnnotatedOutput;
import com.android.dx.util.Hex;
import java.util.Objects;
/* loaded from: classes.dex */
public final class ProtoIdItem extends IndexedItem {
    private TypeListItem parameterTypes;
    private final Prototype prototype;
    private final CstString shortForm;

    @Override // com.android.dx.dex.file.Item
    public int writeSize() {
        return 12;
    }

    public ProtoIdItem(Prototype prototype) {
        Objects.requireNonNull(prototype, "prototype == null");
        this.prototype = prototype;
        this.shortForm = makeShortForm(prototype);
        StdTypeList parameterTypes = prototype.getParameterTypes();
        this.parameterTypes = parameterTypes.size() == 0 ? null : new TypeListItem(parameterTypes);
    }

    private static CstString makeShortForm(Prototype prototype) {
        StdTypeList parameterTypes = prototype.getParameterTypes();
        int size = parameterTypes.size();
        StringBuilder sb = new StringBuilder(size + 1);
        sb.append(shortFormCharFor(prototype.getReturnType()));
        for (int i = 0; i < size; i++) {
            sb.append(shortFormCharFor(parameterTypes.getType(i)));
        }
        return new CstString(sb.toString());
    }

    private static char shortFormCharFor(Type type) {
        char charAt = type.getDescriptor().charAt(0);
        if (charAt == '[') {
            return 'L';
        }
        return charAt;
    }

    @Override // com.android.dx.dex.file.Item
    public ItemType itemType() {
        return ItemType.TYPE_PROTO_ID_ITEM;
    }

    @Override // com.android.dx.dex.file.Item
    public void addContents(DexFile dexFile) {
        StringIdsSection stringIds = dexFile.getStringIds();
        TypeIdsSection typeIds = dexFile.getTypeIds();
        MixedItemSection typeLists = dexFile.getTypeLists();
        typeIds.intern(this.prototype.getReturnType());
        stringIds.intern(this.shortForm);
        TypeListItem typeListItem = this.parameterTypes;
        if (typeListItem != null) {
            this.parameterTypes = (TypeListItem) typeLists.intern(typeListItem);
        }
    }

    @Override // com.android.dx.dex.file.Item
    public void writeTo(DexFile dexFile, AnnotatedOutput annotatedOutput) {
        int indexOf = dexFile.getStringIds().indexOf(this.shortForm);
        int indexOf2 = dexFile.getTypeIds().indexOf(this.prototype.getReturnType());
        int absoluteOffsetOr0 = OffsettedItem.getAbsoluteOffsetOr0(this.parameterTypes);
        if (annotatedOutput.annotates()) {
            StringBuilder sb = new StringBuilder();
            sb.append(this.prototype.getReturnType().toHuman());
            sb.append(" proto(");
            StdTypeList parameterTypes = this.prototype.getParameterTypes();
            int size = parameterTypes.size();
            for (int i = 0; i < size; i++) {
                if (i != 0) {
                    sb.append(", ");
                }
                sb.append(parameterTypes.getType(i).toHuman());
            }
            sb.append(")");
            annotatedOutput.annotate(0, indexString() + ' ' + sb.toString());
            annotatedOutput.annotate(4, "  shorty_idx:      " + Hex.u4(indexOf) + " // " + this.shortForm.toQuoted());
            annotatedOutput.annotate(4, "  return_type_idx: " + Hex.u4(indexOf2) + " // " + this.prototype.getReturnType().toHuman());
            annotatedOutput.annotate(4, "  parameters_off:  " + Hex.u4(absoluteOffsetOr0));
        }
        annotatedOutput.writeInt(indexOf);
        annotatedOutput.writeInt(indexOf2);
        annotatedOutput.writeInt(absoluteOffsetOr0);
    }
}

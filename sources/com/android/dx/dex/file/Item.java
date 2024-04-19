package com.android.dx.dex.file;

import com.android.dx.util.AnnotatedOutput;
/* loaded from: classes.dex */
public abstract class Item {
    public abstract void addContents(DexFile dexFile);

    public abstract ItemType itemType();

    public abstract int writeSize();

    public abstract void writeTo(DexFile dexFile, AnnotatedOutput annotatedOutput);

    public final String typeName() {
        return itemType().toHuman();
    }
}

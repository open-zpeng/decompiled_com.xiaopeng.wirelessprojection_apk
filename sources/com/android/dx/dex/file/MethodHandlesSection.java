package com.android.dx.dex.file;

import com.android.dx.rop.cst.Constant;
import com.android.dx.rop.cst.CstMethodHandle;
import java.util.Collection;
import java.util.Objects;
import java.util.TreeMap;
/* loaded from: classes.dex */
public final class MethodHandlesSection extends UniformItemSection {
    private final TreeMap<CstMethodHandle, MethodHandleItem> methodHandles;

    public MethodHandlesSection(DexFile dexFile) {
        super("method_handles", dexFile, 8);
        this.methodHandles = new TreeMap<>();
    }

    @Override // com.android.dx.dex.file.UniformItemSection
    public IndexedItem get(Constant constant) {
        Objects.requireNonNull(constant, "cst == null");
        throwIfNotPrepared();
        MethodHandleItem methodHandleItem = this.methodHandles.get((CstMethodHandle) constant);
        if (methodHandleItem != null) {
            return methodHandleItem;
        }
        throw new IllegalArgumentException("not found");
    }

    @Override // com.android.dx.dex.file.UniformItemSection
    protected void orderItems() {
        int i = 0;
        for (MethodHandleItem methodHandleItem : this.methodHandles.values()) {
            methodHandleItem.setIndex(i);
            i++;
        }
    }

    @Override // com.android.dx.dex.file.Section
    public Collection<? extends Item> items() {
        return this.methodHandles.values();
    }

    public void intern(CstMethodHandle cstMethodHandle) {
        Objects.requireNonNull(cstMethodHandle, "methodHandle == null");
        throwIfPrepared();
        if (this.methodHandles.get(cstMethodHandle) == null) {
            this.methodHandles.put(cstMethodHandle, new MethodHandleItem(cstMethodHandle));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int indexOf(CstMethodHandle cstMethodHandle) {
        return this.methodHandles.get(cstMethodHandle).getIndex();
    }
}

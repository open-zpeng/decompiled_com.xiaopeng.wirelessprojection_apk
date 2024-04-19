package com.android.dx.dex.file;

import com.android.dx.dex.file.OffsettedItem;
import com.android.dx.util.AnnotatedOutput;
import com.android.dx.util.Hex;
import com.xiaopeng.wirelessprojection.dmr.RendererActivity;
import java.util.List;
import java.util.Objects;
/* loaded from: classes.dex */
public final class UniformListItem<T extends OffsettedItem> extends OffsettedItem {
    private static final int HEADER_SIZE = 4;
    private final ItemType itemType;
    private final List<T> items;

    public UniformListItem(ItemType itemType, List<T> list) {
        super(getAlignment(list), writeSize(list));
        Objects.requireNonNull(itemType, "itemType == null");
        this.items = list;
        this.itemType = itemType;
    }

    private static int getAlignment(List<? extends OffsettedItem> list) {
        try {
            return Math.max(4, list.get(0).getAlignment());
        } catch (IndexOutOfBoundsException unused) {
            throw new IllegalArgumentException("items.size() == 0");
        } catch (NullPointerException unused2) {
            throw new NullPointerException("items == null");
        }
    }

    private static int writeSize(List<? extends OffsettedItem> list) {
        return (list.size() * list.get(0).writeSize()) + getAlignment(list);
    }

    @Override // com.android.dx.dex.file.Item
    public ItemType itemType() {
        return this.itemType;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(100);
        sb.append(getClass().getName());
        sb.append(this.items);
        return sb.toString();
    }

    @Override // com.android.dx.dex.file.Item
    public void addContents(DexFile dexFile) {
        for (T t : this.items) {
            t.addContents(dexFile);
        }
    }

    @Override // com.android.dx.dex.file.OffsettedItem
    public final String toHuman() {
        StringBuilder sb = new StringBuilder(100);
        sb.append("{");
        boolean z = true;
        for (T t : this.items) {
            if (z) {
                z = false;
            } else {
                sb.append(", ");
            }
            sb.append(t.toHuman());
        }
        sb.append("}");
        return sb.toString();
    }

    public final List<T> getItems() {
        return this.items;
    }

    @Override // com.android.dx.dex.file.OffsettedItem
    protected void place0(Section section, int i) {
        int headerSize = i + headerSize();
        int i2 = -1;
        boolean z = true;
        int i3 = -1;
        for (T t : this.items) {
            int writeSize = t.writeSize();
            if (z) {
                z = false;
                i3 = t.getAlignment();
                i2 = writeSize;
            } else if (writeSize != i2) {
                throw new UnsupportedOperationException("item size mismatch");
            } else {
                if (t.getAlignment() != i3) {
                    throw new UnsupportedOperationException("item alignment mismatch");
                }
            }
            headerSize = t.place(section, headerSize) + writeSize;
        }
    }

    @Override // com.android.dx.dex.file.OffsettedItem
    protected void writeTo0(DexFile dexFile, AnnotatedOutput annotatedOutput) {
        int size = this.items.size();
        if (annotatedOutput.annotates()) {
            annotatedOutput.annotate(0, offsetString() + RendererActivity.DEFAULT_TITLE + typeName());
            annotatedOutput.annotate(4, "  size: " + Hex.u4(size));
        }
        annotatedOutput.writeInt(size);
        for (T t : this.items) {
            t.writeTo(dexFile, annotatedOutput);
        }
    }

    private int headerSize() {
        return getAlignment();
    }
}

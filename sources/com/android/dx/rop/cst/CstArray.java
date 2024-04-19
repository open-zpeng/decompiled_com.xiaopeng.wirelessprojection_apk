package com.android.dx.rop.cst;

import com.android.dx.util.FixedSizeList;
import java.util.Objects;
/* loaded from: classes.dex */
public class CstArray extends Constant {
    private final List list;

    @Override // com.android.dx.rop.cst.Constant
    public boolean isCategory2() {
        return false;
    }

    @Override // com.android.dx.rop.cst.Constant
    public String typeName() {
        return "array";
    }

    public CstArray(List list) {
        Objects.requireNonNull(list, "list == null");
        list.throwIfMutable();
        this.list = list;
    }

    public boolean equals(Object obj) {
        if (obj instanceof CstArray) {
            return this.list.equals(((CstArray) obj).list);
        }
        return false;
    }

    public int hashCode() {
        return this.list.hashCode();
    }

    @Override // com.android.dx.rop.cst.Constant
    protected int compareTo0(Constant constant) {
        return this.list.compareTo(((CstArray) constant).list);
    }

    public String toString() {
        return this.list.toString("array{", ", ", "}");
    }

    @Override // com.android.dx.util.ToHuman
    public String toHuman() {
        return this.list.toHuman("{", ", ", "}");
    }

    public List getList() {
        return this.list;
    }

    /* loaded from: classes.dex */
    public static final class List extends FixedSizeList implements Comparable<List> {
        public List(int i) {
            super(i);
        }

        @Override // java.lang.Comparable
        public int compareTo(List list) {
            int size = size();
            int size2 = list.size();
            int i = size < size2 ? size : size2;
            for (int i2 = 0; i2 < i; i2++) {
                int compareTo = ((Constant) get0(i2)).compareTo((Constant) list.get0(i2));
                if (compareTo != 0) {
                    return compareTo;
                }
            }
            if (size < size2) {
                return -1;
            }
            return size > size2 ? 1 : 0;
        }

        public Constant get(int i) {
            return (Constant) get0(i);
        }

        public void set(int i, Constant constant) {
            set0(i, constant);
        }
    }
}

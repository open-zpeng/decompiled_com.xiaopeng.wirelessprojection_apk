package com.android.dx.cf.attrib;

import com.android.dx.rop.cst.CstString;
import com.android.dx.rop.cst.CstType;
import com.android.dx.util.FixedSizeList;
import java.util.Objects;
/* loaded from: classes.dex */
public final class InnerClassList extends FixedSizeList {
    public InnerClassList(int i) {
        super(i);
    }

    public Item get(int i) {
        return (Item) get0(i);
    }

    public void set(int i, CstType cstType, CstType cstType2, CstString cstString, int i2) {
        set0(i, new Item(cstType, cstType2, cstString, i2));
    }

    /* loaded from: classes.dex */
    public static class Item {
        private final int accessFlags;
        private final CstType innerClass;
        private final CstString innerName;
        private final CstType outerClass;

        public Item(CstType cstType, CstType cstType2, CstString cstString, int i) {
            Objects.requireNonNull(cstType, "innerClass == null");
            this.innerClass = cstType;
            this.outerClass = cstType2;
            this.innerName = cstString;
            this.accessFlags = i;
        }

        public CstType getInnerClass() {
            return this.innerClass;
        }

        public CstType getOuterClass() {
            return this.outerClass;
        }

        public CstString getInnerName() {
            return this.innerName;
        }

        public int getAccessFlags() {
            return this.accessFlags;
        }
    }
}

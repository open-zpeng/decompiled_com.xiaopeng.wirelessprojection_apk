package com.android.dx.util;

import java.util.NoSuchElementException;
/* loaded from: classes.dex */
public class BitIntSet implements IntSet {
    int[] bits;

    public BitIntSet(int i) {
        this.bits = Bits.makeBitSet(i);
    }

    @Override // com.android.dx.util.IntSet
    public void add(int i) {
        ensureCapacity(i);
        Bits.set(this.bits, i, true);
    }

    private void ensureCapacity(int i) {
        if (i >= Bits.getMax(this.bits)) {
            int[] makeBitSet = Bits.makeBitSet(Math.max(i + 1, Bits.getMax(this.bits) * 2));
            int[] iArr = this.bits;
            System.arraycopy(iArr, 0, makeBitSet, 0, iArr.length);
            this.bits = makeBitSet;
        }
    }

    @Override // com.android.dx.util.IntSet
    public void remove(int i) {
        if (i < Bits.getMax(this.bits)) {
            Bits.set(this.bits, i, false);
        }
    }

    @Override // com.android.dx.util.IntSet
    public boolean has(int i) {
        return i < Bits.getMax(this.bits) && Bits.get(this.bits, i);
    }

    @Override // com.android.dx.util.IntSet
    public void merge(IntSet intSet) {
        if (intSet instanceof BitIntSet) {
            BitIntSet bitIntSet = (BitIntSet) intSet;
            ensureCapacity(Bits.getMax(bitIntSet.bits) + 1);
            Bits.or(this.bits, bitIntSet.bits);
        } else if (intSet instanceof ListIntSet) {
            ListIntSet listIntSet = (ListIntSet) intSet;
            int size = listIntSet.ints.size();
            if (size > 0) {
                ensureCapacity(listIntSet.ints.get(size - 1));
            }
            for (int i = 0; i < listIntSet.ints.size(); i++) {
                Bits.set(this.bits, listIntSet.ints.get(i), true);
            }
        } else {
            IntIterator it = intSet.iterator();
            while (it.hasNext()) {
                add(it.next());
            }
        }
    }

    @Override // com.android.dx.util.IntSet
    public int elements() {
        return Bits.bitCount(this.bits);
    }

    @Override // com.android.dx.util.IntSet
    public IntIterator iterator() {
        return new IntIterator() { // from class: com.android.dx.util.BitIntSet.1
            private int idx;

            {
                this.idx = Bits.findFirst(BitIntSet.this.bits, 0);
            }

            @Override // com.android.dx.util.IntIterator
            public boolean hasNext() {
                return this.idx >= 0;
            }

            @Override // com.android.dx.util.IntIterator
            public int next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                int i = this.idx;
                this.idx = Bits.findFirst(BitIntSet.this.bits, this.idx + 1);
                return i;
            }
        };
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        int findFirst = Bits.findFirst(this.bits, 0);
        boolean z = true;
        while (findFirst >= 0) {
            if (!z) {
                sb.append(", ");
            }
            sb.append(findFirst);
            findFirst = Bits.findFirst(this.bits, findFirst + 1);
            z = false;
        }
        sb.append('}');
        return sb.toString();
    }
}

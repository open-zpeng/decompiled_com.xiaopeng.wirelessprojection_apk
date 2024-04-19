package com.android.dex;
/* loaded from: classes.dex */
public final class TypeList implements Comparable<TypeList> {
    public static final TypeList EMPTY = new TypeList(null, Dex.EMPTY_SHORT_ARRAY);
    private final Dex dex;
    private final short[] types;

    public TypeList(Dex dex, short[] sArr) {
        this.dex = dex;
        this.types = sArr;
    }

    public short[] getTypes() {
        return this.types;
    }

    /* JADX WARN: Code restructure failed: missing block: B:13:0x0025, code lost:
        return com.android.dex.util.Unsigned.compare(r1.length, r6.types.length);
     */
    @Override // java.lang.Comparable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public int compareTo(com.android.dex.TypeList r6) {
        /*
            r5 = this;
            r0 = 0
        L1:
            short[] r1 = r5.types
            int r2 = r1.length
            if (r0 >= r2) goto L1d
            short[] r2 = r6.types
            int r3 = r2.length
            if (r0 >= r3) goto L1d
            short r3 = r1[r0]
            short r4 = r2[r0]
            if (r3 == r4) goto L1a
            short r6 = r1[r0]
            short r0 = r2[r0]
            int r6 = com.android.dex.util.Unsigned.compare(r6, r0)
            return r6
        L1a:
            int r0 = r0 + 1
            goto L1
        L1d:
            int r0 = r1.length
            short[] r6 = r6.types
            int r6 = r6.length
            int r6 = com.android.dex.util.Unsigned.compare(r0, r6)
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.dex.TypeList.compareTo(com.android.dex.TypeList):int");
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        int length = this.types.length;
        for (int i = 0; i < length; i++) {
            Dex dex = this.dex;
            sb.append(dex != null ? dex.typeNames().get(this.types[i]) : Short.valueOf(this.types[i]));
        }
        sb.append(")");
        return sb.toString();
    }
}

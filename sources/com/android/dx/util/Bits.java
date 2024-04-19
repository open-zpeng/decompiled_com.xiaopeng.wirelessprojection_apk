package com.android.dx.util;
/* loaded from: classes.dex */
public final class Bits {
    private Bits() {
    }

    public static int[] makeBitSet(int i) {
        return new int[(i + 31) >> 5];
    }

    public static int getMax(int[] iArr) {
        return iArr.length * 32;
    }

    public static boolean get(int[] iArr, int i) {
        return (iArr[i >> 5] & (1 << (i & 31))) != 0;
    }

    public static void set(int[] iArr, int i, boolean z) {
        int i2 = i >> 5;
        int i3 = 1 << (i & 31);
        if (z) {
            iArr[i2] = i3 | iArr[i2];
            return;
        }
        iArr[i2] = (~i3) & iArr[i2];
    }

    public static void set(int[] iArr, int i) {
        int i2 = i >> 5;
        iArr[i2] = (1 << (i & 31)) | iArr[i2];
    }

    public static void clear(int[] iArr, int i) {
        int i2 = i >> 5;
        iArr[i2] = (~(1 << (i & 31))) & iArr[i2];
    }

    public static boolean isEmpty(int[] iArr) {
        for (int i : iArr) {
            if (i != 0) {
                return false;
            }
        }
        return true;
    }

    public static int bitCount(int[] iArr) {
        int i = 0;
        for (int i2 : iArr) {
            i += Integer.bitCount(i2);
        }
        return i;
    }

    public static boolean anyInRange(int[] iArr, int i, int i2) {
        int findFirst = findFirst(iArr, i);
        return findFirst >= 0 && findFirst < i2;
    }

    public static int findFirst(int[] iArr, int i) {
        int findFirst;
        int length = iArr.length;
        int i2 = i & 31;
        for (int i3 = i >> 5; i3 < length; i3++) {
            int i4 = iArr[i3];
            if (i4 != 0 && (findFirst = findFirst(i4, i2)) >= 0) {
                return (i3 << 5) + findFirst;
            }
            i2 = 0;
        }
        return -1;
    }

    public static int findFirst(int i, int i2) {
        int numberOfTrailingZeros = Integer.numberOfTrailingZeros(i & (~((1 << i2) - 1)));
        if (numberOfTrailingZeros == 32) {
            return -1;
        }
        return numberOfTrailingZeros;
    }

    public static void or(int[] iArr, int[] iArr2) {
        for (int i = 0; i < iArr2.length; i++) {
            iArr[i] = iArr[i] | iArr2[i];
        }
    }

    public static String toHuman(int[] iArr) {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        int length = iArr.length * 32;
        boolean z = false;
        for (int i = 0; i < length; i++) {
            if (get(iArr, i)) {
                if (z) {
                    sb.append(',');
                }
                sb.append(i);
                z = true;
            }
        }
        sb.append('}');
        return sb.toString();
    }
}

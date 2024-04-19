package com.android.dx.util;
/* loaded from: classes.dex */
public final class HexParser {
    private HexParser() {
    }

    public static byte[] parse(String str) {
        String substring;
        int indexOf;
        int length = str.length();
        int i = length / 2;
        byte[] bArr = new byte[i];
        int i2 = 0;
        int i3 = 0;
        int i4 = 0;
        while (i3 < length) {
            int indexOf2 = str.indexOf(10, i3);
            if (indexOf2 < 0) {
                indexOf2 = length;
            }
            int indexOf3 = str.indexOf(35, i3);
            if (indexOf3 >= 0 && indexOf3 < indexOf2) {
                substring = str.substring(i3, indexOf3);
            } else {
                substring = str.substring(i3, indexOf2);
            }
            int i5 = indexOf2 + 1;
            int indexOf4 = substring.indexOf(58);
            if (indexOf4 != -1 && ((indexOf = substring.indexOf(34)) == -1 || indexOf >= indexOf4)) {
                String trim = substring.substring(i2, indexOf4).trim();
                substring = substring.substring(indexOf4 + 1);
                if (Integer.parseInt(trim, 16) != i4) {
                    throw new RuntimeException("bogus offset marker: " + trim);
                }
            }
            int length2 = substring.length();
            int i6 = i2;
            int i7 = i6;
            int i8 = -1;
            while (i6 < length2) {
                char charAt = substring.charAt(i6);
                if (i7 != 0) {
                    if (charAt == '\"') {
                        i7 = 0;
                    } else {
                        bArr[i4] = (byte) charAt;
                        i4++;
                    }
                } else if (charAt > ' ') {
                    if (charAt != '\"') {
                        int digit = Character.digit(charAt, 16);
                        if (digit == -1) {
                            throw new RuntimeException("bogus digit character: \"" + charAt + "\"");
                        }
                        if (i8 == -1) {
                            i8 = digit;
                        } else {
                            bArr[i4] = (byte) ((i8 << 4) | digit);
                            i4++;
                            i8 = -1;
                        }
                    } else if (i8 != -1) {
                        throw new RuntimeException("spare digit around offset " + Hex.u4(i4));
                    } else {
                        i7 = 1;
                    }
                }
                i6++;
            }
            if (i8 != -1) {
                throw new RuntimeException("spare digit around offset " + Hex.u4(i4));
            }
            if (i7 != 0) {
                throw new RuntimeException("unterminated quote around offset " + Hex.u4(i4));
            }
            i3 = i5;
            i2 = 0;
        }
        if (i4 < i) {
            byte[] bArr2 = new byte[i4];
            System.arraycopy(bArr, 0, bArr2, 0, i4);
            return bArr2;
        }
        return bArr;
    }
}

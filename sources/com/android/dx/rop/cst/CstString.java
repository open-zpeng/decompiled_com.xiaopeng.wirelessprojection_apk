package com.android.dx.rop.cst;

import com.android.dx.rop.type.Type;
import com.android.dx.util.ByteArray;
import com.android.dx.util.Hex;
import java.util.Objects;
import kotlin.text.Typography;
/* loaded from: classes.dex */
public final class CstString extends TypedConstant {
    public static final CstString EMPTY_STRING = new CstString("");
    private final ByteArray bytes;
    private final String string;

    @Override // com.android.dx.rop.cst.Constant
    public boolean isCategory2() {
        return false;
    }

    @Override // com.android.dx.rop.cst.Constant
    public String typeName() {
        return "utf8";
    }

    public static byte[] stringToUtf8Bytes(String str) {
        int length = str.length();
        byte[] bArr = new byte[length * 3];
        int i = 0;
        for (int i2 = 0; i2 < length; i2++) {
            char charAt = str.charAt(i2);
            if (charAt != 0 && charAt < 128) {
                bArr[i] = (byte) charAt;
                i++;
            } else if (charAt < 2048) {
                bArr[i] = (byte) (((charAt >> 6) & 31) | 192);
                bArr[i + 1] = (byte) ((charAt & '?') | 128);
                i += 2;
            } else {
                bArr[i] = (byte) (((charAt >> '\f') & 15) | 224);
                bArr[i + 1] = (byte) (((charAt >> 6) & 63) | 128);
                bArr[i + 2] = (byte) ((charAt & '?') | 128);
                i += 3;
            }
        }
        byte[] bArr2 = new byte[i];
        System.arraycopy(bArr, 0, bArr2, 0, i);
        return bArr2;
    }

    public static String utf8BytesToString(ByteArray byteArray) {
        char c;
        int size = byteArray.size();
        char[] cArr = new char[size];
        int i = 0;
        int i2 = 0;
        while (size > 0) {
            int unsignedByte = byteArray.getUnsignedByte(i2);
            switch (unsignedByte >> 4) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                    size--;
                    if (unsignedByte != 0) {
                        c = (char) unsignedByte;
                        i2++;
                        break;
                    } else {
                        return throwBadUtf8(unsignedByte, i2);
                    }
                case 8:
                case 9:
                case 10:
                case 11:
                default:
                    return throwBadUtf8(unsignedByte, i2);
                case 12:
                case 13:
                    size -= 2;
                    if (size < 0) {
                        return throwBadUtf8(unsignedByte, i2);
                    }
                    int i3 = i2 + 1;
                    int unsignedByte2 = byteArray.getUnsignedByte(i3);
                    if ((unsignedByte2 & 192) != 128) {
                        return throwBadUtf8(unsignedByte2, i3);
                    }
                    int i4 = ((unsignedByte & 31) << 6) | (unsignedByte2 & 63);
                    if (i4 == 0 || i4 >= 128) {
                        c = (char) i4;
                        i2 += 2;
                        break;
                    } else {
                        return throwBadUtf8(unsignedByte2, i3);
                    }
                case 14:
                    size -= 3;
                    if (size < 0) {
                        return throwBadUtf8(unsignedByte, i2);
                    }
                    int i5 = i2 + 1;
                    int unsignedByte3 = byteArray.getUnsignedByte(i5);
                    int i6 = unsignedByte3 & 192;
                    if (i6 != 128) {
                        return throwBadUtf8(unsignedByte3, i5);
                    }
                    int i7 = i2 + 2;
                    int unsignedByte4 = byteArray.getUnsignedByte(i7);
                    if (i6 == 128) {
                        int i8 = ((unsignedByte & 15) << 12) | ((unsignedByte3 & 63) << 6) | (unsignedByte4 & 63);
                        if (i8 >= 2048) {
                            c = (char) i8;
                            i2 += 3;
                            break;
                        } else {
                            return throwBadUtf8(unsignedByte4, i7);
                        }
                    } else {
                        return throwBadUtf8(unsignedByte4, i7);
                    }
            }
            cArr[i] = c;
            i++;
        }
        return new String(cArr, 0, i);
    }

    private static String throwBadUtf8(int i, int i2) {
        throw new IllegalArgumentException("bad utf-8 byte " + Hex.u1(i) + " at offset " + Hex.u4(i2));
    }

    public CstString(String str) {
        Objects.requireNonNull(str, "string == null");
        this.string = str.intern();
        this.bytes = new ByteArray(stringToUtf8Bytes(str));
    }

    public CstString(ByteArray byteArray) {
        Objects.requireNonNull(byteArray, "bytes == null");
        this.bytes = byteArray;
        this.string = utf8BytesToString(byteArray).intern();
    }

    public boolean equals(Object obj) {
        if (obj instanceof CstString) {
            return this.string.equals(((CstString) obj).string);
        }
        return false;
    }

    public int hashCode() {
        return this.string.hashCode();
    }

    @Override // com.android.dx.rop.cst.Constant
    protected int compareTo0(Constant constant) {
        return this.string.compareTo(((CstString) constant).string);
    }

    public String toString() {
        return "string{\"" + toHuman() + "\"}";
    }

    @Override // com.android.dx.util.ToHuman
    public String toHuman() {
        int length = this.string.length();
        StringBuilder sb = new StringBuilder((length * 3) / 2);
        int i = 0;
        while (i < length) {
            char charAt = this.string.charAt(i);
            if (charAt >= ' ' && charAt < 127) {
                if (charAt == '\'' || charAt == '\"' || charAt == '\\') {
                    sb.append('\\');
                }
                sb.append(charAt);
            } else if (charAt > 127) {
                sb.append("\\u");
                sb.append(Character.forDigit(charAt >> '\f', 16));
                sb.append(Character.forDigit((charAt >> '\b') & 15, 16));
                sb.append(Character.forDigit((charAt >> 4) & 15, 16));
                sb.append(Character.forDigit(charAt & 15, 16));
            } else if (charAt == '\t') {
                sb.append("\\t");
            } else if (charAt == '\n') {
                sb.append("\\n");
            } else if (charAt == '\r') {
                sb.append("\\r");
            } else {
                char charAt2 = i < length + (-1) ? this.string.charAt(i + 1) : (char) 0;
                boolean z = charAt2 >= '0' && charAt2 <= '7';
                sb.append('\\');
                for (int i2 = 6; i2 >= 0; i2 -= 3) {
                    char c = (char) (((charAt >> i2) & 7) + 48);
                    if (c != '0' || z) {
                        sb.append(c);
                        z = true;
                    }
                }
                if (!z) {
                    sb.append('0');
                }
            }
            i++;
        }
        return sb.toString();
    }

    public String toQuoted() {
        return Typography.quote + toHuman() + Typography.quote;
    }

    public String toQuoted(int i) {
        String str;
        String human = toHuman();
        if (human.length() <= i - 2) {
            str = "";
        } else {
            human = human.substring(0, i - 5);
            str = "...";
        }
        return Typography.quote + human + str + Typography.quote;
    }

    public String getString() {
        return this.string;
    }

    public ByteArray getBytes() {
        return this.bytes;
    }

    public int getUtf8Size() {
        return this.bytes.size();
    }

    public int getUtf16Size() {
        return this.string.length();
    }

    @Override // com.android.dx.rop.type.TypeBearer
    public Type getType() {
        return Type.STRING;
    }
}

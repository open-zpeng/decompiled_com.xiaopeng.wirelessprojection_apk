package com.android.dex;
/* loaded from: classes.dex */
public final class DexFormat {
    public static final int API_CONST_METHOD_HANDLE = 28;
    public static final int API_CURRENT = 28;
    public static final int API_DEFINE_INTERFACE_METHODS = 24;
    public static final int API_INVOKE_INTERFACE_METHODS = 24;
    public static final int API_INVOKE_STATIC_INTERFACE_METHODS = 21;
    public static final int API_METHOD_HANDLES = 26;
    public static final int API_NO_EXTENDED_OPCODES = 13;
    public static final String DEX_IN_JAR_NAME = "classes.dex";
    public static final int ENDIAN_TAG = 305419896;
    public static final String MAGIC_PREFIX = "dex\n";
    public static final String MAGIC_SUFFIX = "\u0000";
    public static final int MAX_MEMBER_IDX = 65535;
    public static final int MAX_TYPE_IDX = 65535;
    public static final String VERSION_CURRENT = "039";
    public static final String VERSION_FOR_API_13 = "035";
    public static final String VERSION_FOR_API_24 = "037";
    public static final String VERSION_FOR_API_26 = "038";
    public static final String VERSION_FOR_API_28 = "039";

    private DexFormat() {
    }

    public static int magicToApi(byte[] bArr) {
        if (bArr.length == 8 && bArr[0] == 100 && bArr[1] == 101 && bArr[2] == 120 && bArr[3] == 10 && bArr[7] == 0) {
            String str = "" + ((char) bArr[4]) + ((char) bArr[5]) + ((char) bArr[6]);
            if (str.equals(VERSION_FOR_API_13)) {
                return 13;
            }
            if (str.equals(VERSION_FOR_API_24)) {
                return 24;
            }
            if (str.equals(VERSION_FOR_API_26)) {
                return 26;
            }
            if (str.equals("039") || str.equals("039")) {
                return 28;
            }
        }
        return -1;
    }

    public static String apiToMagic(int i) {
        String str = "039";
        if (i < 28 && i < 28) {
            str = i >= 26 ? VERSION_FOR_API_26 : i >= 24 ? VERSION_FOR_API_24 : VERSION_FOR_API_13;
        }
        return MAGIC_PREFIX + str + MAGIC_SUFFIX;
    }

    public static boolean isSupportedDexMagic(byte[] bArr) {
        return magicToApi(bArr) > 0;
    }
}

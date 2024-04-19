package com.xiaopeng.lib.utils.crypt;

import android.text.TextUtils;
import android.util.Base64;
import com.google.common.base.Ascii;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
/* loaded from: classes2.dex */
public class AESUtils {
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r3v0, types: [java.io.File] */
    /* JADX WARN: Type inference failed for: r3v1 */
    /* JADX WARN: Type inference failed for: r3v10 */
    /* JADX WARN: Type inference failed for: r3v11, types: [java.io.OutputStream] */
    /* JADX WARN: Type inference failed for: r3v12 */
    /* JADX WARN: Type inference failed for: r3v13 */
    /* JADX WARN: Type inference failed for: r3v14 */
    /* JADX WARN: Type inference failed for: r3v15 */
    /* JADX WARN: Type inference failed for: r3v16 */
    /* JADX WARN: Type inference failed for: r3v17 */
    /* JADX WARN: Type inference failed for: r3v18, types: [java.io.OutputStream, java.io.FileOutputStream] */
    /* JADX WARN: Type inference failed for: r3v2, types: [java.io.OutputStream] */
    /* JADX WARN: Type inference failed for: r3v20 */
    /* JADX WARN: Type inference failed for: r3v21 */
    /* JADX WARN: Type inference failed for: r3v22 */
    /* JADX WARN: Type inference failed for: r3v23 */
    /* JADX WARN: Type inference failed for: r3v4 */
    /* JADX WARN: Type inference failed for: r3v5 */
    /* JADX WARN: Type inference failed for: r3v6 */
    /* JADX WARN: Type inference failed for: r3v7, types: [java.io.IOException] */
    /* JADX WARN: Type inference failed for: r3v8, types: [java.io.OutputStream] */
    /* JADX WARN: Type inference failed for: r3v9 */
    public static boolean decrypt(File e, File file, String str) throws Exception {
        FileInputStream fileInputStream = null;
        try {
            try {
                try {
                    Cipher initAESCipher = initAESCipher(str, 2);
                    FileInputStream fileInputStream2 = new FileInputStream((File) e);
                    try {
                        e = new FileOutputStream(file);
                        try {
                            CipherOutputStream cipherOutputStream = new CipherOutputStream(e, initAESCipher);
                            byte[] bArr = new byte[1048576];
                            while (true) {
                                int read = fileInputStream2.read(bArr);
                                if (read < 0) {
                                    break;
                                }
                                cipherOutputStream.write(bArr, 0, read);
                            }
                            cipherOutputStream.close();
                            try {
                                fileInputStream2.close();
                            } catch (IOException e2) {
                                e2.printStackTrace();
                            }
                            try {
                                e.close();
                            } catch (IOException e3) {
                                e3.printStackTrace();
                            }
                            return true;
                        } catch (Exception e4) {
                            e = e4;
                            fileInputStream = fileInputStream2;
                            e = e;
                            e.printStackTrace();
                            if (fileInputStream != null) {
                                try {
                                    fileInputStream.close();
                                } catch (IOException e5) {
                                    e5.printStackTrace();
                                }
                            }
                            if (e != 0) {
                                e.close();
                                e = e;
                            }
                            return false;
                        } catch (OutOfMemoryError e6) {
                            e = e6;
                            fileInputStream = fileInputStream2;
                            e = e;
                            e.printStackTrace();
                            if (fileInputStream != null) {
                                try {
                                    fileInputStream.close();
                                } catch (IOException e7) {
                                    e7.printStackTrace();
                                }
                            }
                            if (e != 0) {
                                e.close();
                                e = e;
                            }
                            return false;
                        } catch (Throwable th) {
                            th = th;
                            fileInputStream = fileInputStream2;
                            if (fileInputStream != null) {
                                try {
                                    fileInputStream.close();
                                } catch (IOException e8) {
                                    e8.printStackTrace();
                                }
                            }
                            if (e != 0) {
                                try {
                                    e.close();
                                } catch (IOException e9) {
                                    e9.printStackTrace();
                                }
                            }
                            throw th;
                        }
                    } catch (Exception e10) {
                        e = e10;
                        e = 0;
                    } catch (OutOfMemoryError e11) {
                        e = e11;
                        e = 0;
                    } catch (Throwable th2) {
                        th = th2;
                        e = 0;
                    }
                } catch (IOException e12) {
                    e = e12;
                    e.printStackTrace();
                    return false;
                }
            } catch (Exception e13) {
                e = e13;
                e = 0;
            } catch (OutOfMemoryError e14) {
                e = e14;
                e = 0;
            } catch (Throwable th3) {
                th = th3;
                e = 0;
            }
        } catch (Throwable th4) {
            th = th4;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r4v0, types: [java.io.File] */
    /* JADX WARN: Type inference failed for: r4v1 */
    /* JADX WARN: Type inference failed for: r4v10 */
    /* JADX WARN: Type inference failed for: r4v11, types: [java.io.OutputStream] */
    /* JADX WARN: Type inference failed for: r4v12 */
    /* JADX WARN: Type inference failed for: r4v13 */
    /* JADX WARN: Type inference failed for: r4v14 */
    /* JADX WARN: Type inference failed for: r4v15 */
    /* JADX WARN: Type inference failed for: r4v16 */
    /* JADX WARN: Type inference failed for: r4v17 */
    /* JADX WARN: Type inference failed for: r4v18, types: [java.io.OutputStream, java.io.FileOutputStream] */
    /* JADX WARN: Type inference failed for: r4v2, types: [java.io.OutputStream] */
    /* JADX WARN: Type inference failed for: r4v20 */
    /* JADX WARN: Type inference failed for: r4v21 */
    /* JADX WARN: Type inference failed for: r4v22 */
    /* JADX WARN: Type inference failed for: r4v23 */
    /* JADX WARN: Type inference failed for: r4v4 */
    /* JADX WARN: Type inference failed for: r4v5 */
    /* JADX WARN: Type inference failed for: r4v6 */
    /* JADX WARN: Type inference failed for: r4v7, types: [java.io.IOException] */
    /* JADX WARN: Type inference failed for: r4v8, types: [java.io.OutputStream] */
    /* JADX WARN: Type inference failed for: r4v9 */
    public static boolean encrypt(File e, File file, String str) {
        FileInputStream fileInputStream = null;
        try {
            try {
                try {
                    Cipher initAESCipher = initAESCipher(str, 1);
                    FileInputStream fileInputStream2 = new FileInputStream((File) e);
                    try {
                        e = new FileOutputStream(file);
                        try {
                            CipherOutputStream cipherOutputStream = new CipherOutputStream(e, initAESCipher);
                            byte[] bArr = new byte[1048576];
                            while (true) {
                                int read = fileInputStream2.read(bArr);
                                if (read < 0) {
                                    break;
                                }
                                cipherOutputStream.write(bArr, 0, read);
                            }
                            cipherOutputStream.close();
                            try {
                                fileInputStream2.close();
                            } catch (IOException e2) {
                                e2.printStackTrace();
                            }
                            try {
                                e.close();
                            } catch (IOException e3) {
                                e3.printStackTrace();
                            }
                            return true;
                        } catch (Exception e4) {
                            e = e4;
                            fileInputStream = fileInputStream2;
                            e = e;
                            e.printStackTrace();
                            if (fileInputStream != null) {
                                try {
                                    fileInputStream.close();
                                } catch (IOException e5) {
                                    e5.printStackTrace();
                                }
                            }
                            if (e != 0) {
                                e.close();
                                e = e;
                            }
                            return false;
                        } catch (OutOfMemoryError e6) {
                            e = e6;
                            fileInputStream = fileInputStream2;
                            e = e;
                            e.printStackTrace();
                            if (fileInputStream != null) {
                                try {
                                    fileInputStream.close();
                                } catch (IOException e7) {
                                    e7.printStackTrace();
                                }
                            }
                            if (e != 0) {
                                e.close();
                                e = e;
                            }
                            return false;
                        } catch (Throwable th) {
                            th = th;
                            fileInputStream = fileInputStream2;
                            if (fileInputStream != null) {
                                try {
                                    fileInputStream.close();
                                } catch (IOException e8) {
                                    e8.printStackTrace();
                                }
                            }
                            if (e != 0) {
                                try {
                                    e.close();
                                } catch (IOException e9) {
                                    e9.printStackTrace();
                                }
                            }
                            throw th;
                        }
                    } catch (Exception e10) {
                        e = e10;
                        e = 0;
                    } catch (OutOfMemoryError e11) {
                        e = e11;
                        e = 0;
                    } catch (Throwable th2) {
                        th = th2;
                        e = 0;
                    }
                } catch (IOException e12) {
                    e = e12;
                    e.printStackTrace();
                    return false;
                }
            } catch (Exception e13) {
                e = e13;
                e = 0;
            } catch (OutOfMemoryError e14) {
                e = e14;
                e = 0;
            } catch (Throwable th3) {
                th = th3;
                e = 0;
            }
        } catch (Throwable th4) {
            th = th4;
        }
    }

    public static boolean encrypt(InputStream inputStream, File file, String str) {
        Cipher initAESCipher;
        FileOutputStream fileOutputStream;
        FileOutputStream fileOutputStream2 = null;
        try {
            try {
                initAESCipher = initAESCipher(str, 1);
                fileOutputStream = new FileOutputStream(file);
            } catch (Exception e) {
                e = e;
            }
        } catch (Throwable th) {
            th = th;
        }
        try {
            CipherOutputStream cipherOutputStream = new CipherOutputStream(fileOutputStream, initAESCipher);
            byte[] bArr = new byte[1048576];
            while (true) {
                int read = inputStream.read(bArr);
                if (read < 0) {
                    break;
                }
                cipherOutputStream.write(bArr, 0, read);
            }
            cipherOutputStream.flush();
            cipherOutputStream.close();
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
            try {
                fileOutputStream.close();
            } catch (IOException e3) {
                e3.printStackTrace();
            }
            return true;
        } catch (Exception e4) {
            e = e4;
            fileOutputStream2 = fileOutputStream;
            e.printStackTrace();
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e5) {
                    e5.printStackTrace();
                }
            }
            if (fileOutputStream2 != null) {
                try {
                    fileOutputStream2.close();
                } catch (IOException e6) {
                    e6.printStackTrace();
                }
            }
            return false;
        } catch (Throwable th2) {
            th = th2;
            fileOutputStream2 = fileOutputStream;
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e7) {
                    e7.printStackTrace();
                }
            }
            if (fileOutputStream2 != null) {
                try {
                    fileOutputStream2.close();
                } catch (IOException e8) {
                    e8.printStackTrace();
                }
            }
            throw th;
        }
    }

    public static String encrypt(String str, String str2) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(str2.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            byte[] bytes = str.getBytes("utf-8");
            cipher.init(1, secretKeySpec);
            return parseByte2HexStr(cipher.doFinal(bytes));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r6v0, types: [java.lang.CharSequence, java.lang.String] */
    /* JADX WARN: Type inference failed for: r6v1 */
    /* JADX WARN: Type inference failed for: r6v10, types: [java.io.FileOutputStream] */
    /* JADX WARN: Type inference failed for: r6v2 */
    /* JADX WARN: Type inference failed for: r6v3 */
    /* JADX WARN: Type inference failed for: r6v4, types: [java.io.FileOutputStream] */
    /* JADX WARN: Type inference failed for: r6v7, types: [java.io.FileOutputStream] */
    /* JADX WARN: Type inference failed for: r6v8 */
    /* JADX WARN: Type inference failed for: r6v9 */
    /* JADX WARN: Type inference failed for: r7v0, types: [java.lang.CharSequence, java.lang.String] */
    /* JADX WARN: Type inference failed for: r7v1 */
    /* JADX WARN: Type inference failed for: r7v2 */
    /* JADX WARN: Type inference failed for: r7v3 */
    /* JADX WARN: Type inference failed for: r7v4, types: [java.io.FileInputStream] */
    /* JADX WARN: Type inference failed for: r7v5, types: [java.io.FileInputStream] */
    /* JADX WARN: Type inference failed for: r7v7, types: [java.io.FileInputStream, java.io.InputStream] */
    public static boolean encryptFile(String str, String str2, String str3) {
        CipherInputStream cipherInputStream;
        if (TextUtils.isEmpty(str2) || TextUtils.isEmpty(str3)) {
            return false;
        }
        CipherInputStream cipherInputStream2 = null;
        try {
            try {
                File file = new File((String) str3);
                if (!file.exists()) {
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdir();
                    }
                    file.createNewFile();
                }
                Cipher initAESCipher = initAESCipher(str, 1);
                str3 = new FileInputStream((String) str2);
                try {
                    str2 = new FileOutputStream(file);
                    try {
                        cipherInputStream = new CipherInputStream(str3, initAESCipher);
                    } catch (Exception e) {
                        e = e;
                    }
                    try {
                        byte[] bArr = new byte[1024];
                        while (true) {
                            int read = cipherInputStream.read(bArr);
                            if (read == -1) {
                                break;
                            }
                            str2.write(bArr, 0, read);
                        }
                        str2.flush();
                        str2.getFD().sync();
                        try {
                            cipherInputStream.close();
                        } catch (IOException e2) {
                            e2.printStackTrace();
                        }
                        try {
                            str2.close();
                        } catch (IOException e3) {
                            e3.printStackTrace();
                        }
                        try {
                            str3.close();
                        } catch (IOException e4) {
                            e4.printStackTrace();
                        }
                        return true;
                    } catch (Exception e5) {
                        e = e5;
                        cipherInputStream2 = cipherInputStream;
                        e.printStackTrace();
                        if (cipherInputStream2 != null) {
                            try {
                                cipherInputStream2.close();
                            } catch (IOException e6) {
                                e6.printStackTrace();
                            }
                        }
                        if (str2 != 0) {
                            try {
                                str2.close();
                            } catch (IOException e7) {
                                e7.printStackTrace();
                            }
                        }
                        if (str3 != 0) {
                            try {
                                str3.close();
                                return false;
                            } catch (IOException e8) {
                                e8.printStackTrace();
                                return false;
                            }
                        }
                        return false;
                    } catch (Throwable th) {
                        th = th;
                        cipherInputStream2 = cipherInputStream;
                        if (cipherInputStream2 != null) {
                            try {
                                cipherInputStream2.close();
                            } catch (IOException e9) {
                                e9.printStackTrace();
                            }
                        }
                        if (str2 != 0) {
                            try {
                                str2.close();
                            } catch (IOException e10) {
                                e10.printStackTrace();
                            }
                        }
                        if (str3 != 0) {
                            try {
                                str3.close();
                            } catch (IOException e11) {
                                e11.printStackTrace();
                            }
                        }
                        throw th;
                    }
                } catch (Exception e12) {
                    e = e12;
                    str2 = 0;
                } catch (Throwable th2) {
                    th = th2;
                    str2 = 0;
                }
            } catch (Throwable th3) {
                th = th3;
            }
        } catch (Exception e13) {
            e = e13;
            str2 = 0;
            str3 = 0;
        } catch (Throwable th4) {
            th = th4;
            str2 = 0;
            str3 = 0;
        }
    }

    public static String parseByte2HexStr(byte[] bArr) {
        if (bArr == null) {
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer(bArr.length * 2);
        for (int i = 0; i < bArr.length; i++) {
            stringBuffer.append("0123456789ABCDEF".charAt((bArr[i] >> 4) & 15)).append("0123456789ABCDEF".charAt(bArr[i] & Ascii.SI));
        }
        return stringBuffer.toString();
    }

    public static byte[] parseHexStr2Byte(String str) {
        int length = str.length() / 2;
        byte[] bArr = new byte[length];
        for (int i = 0; i < length; i++) {
            int i2 = i * 2;
            bArr[i] = Integer.valueOf(str.substring(i2, i2 + 2), 16).byteValue();
        }
        return bArr;
    }

    public static String decrypt(String str, String str2) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(str2.getBytes(Charset.forName("UTF-8")), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(2, secretKeySpec);
            return new String(cipher.doFinal(parseHexStr2Byte(str)));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Cipher initAESCipher(String str, int i) {
        Cipher cipher = null;
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(str.getBytes(), "AES");
            cipher = Cipher.getInstance("AES");
            cipher.init(i, secretKeySpec);
            return cipher;
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            return cipher;
        } catch (NoSuchAlgorithmException e2) {
            e2.printStackTrace();
            return cipher;
        } catch (NoSuchPaddingException e3) {
            e3.printStackTrace();
            return cipher;
        }
    }

    public static String encryptWithBase64(String str, String str2) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(str2.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            byte[] bytes = str.getBytes("utf-8");
            cipher.init(1, secretKeySpec);
            return Base64.encodeToString(cipher.doFinal(bytes), 0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String decryptWithBase64(String str, String str2) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(str2.getBytes(Charset.forName("UTF-8")), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(2, secretKeySpec);
            return new String(cipher.doFinal(Base64.decode(str, 0)));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

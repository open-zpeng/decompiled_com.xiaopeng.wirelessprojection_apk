package com.xiaopeng.lib.utils;

import android.text.TextUtils;
import android.util.Base64;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.compress.archivers.tar.TarConstants;
/* loaded from: classes2.dex */
public class ZipUtils {
    private static byte[] ZIP_HEADER_1 = {80, TarConstants.LF_GNUTYPE_LONGLINK, 3, 4};
    private static byte[] ZIP_HEADER_2 = {80, TarConstants.LF_GNUTYPE_LONGLINK, 5, 6};
    private static int ZIP_HELADER_LENGTH = 4;

    /* JADX WARN: Code restructure failed: missing block: B:15:0x002b, code lost:
        if (java.util.Arrays.equals(com.xiaopeng.lib.utils.ZipUtils.ZIP_HEADER_2, r1) != false) goto L20;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static boolean isArchiveFile(java.io.File r4) {
        /*
            r0 = 0
            if (r4 != 0) goto L4
            return r0
        L4:
            boolean r1 = r4.isDirectory()
            if (r1 == 0) goto Lb
            return r0
        Lb:
            r1 = 0
            java.io.FileInputStream r2 = new java.io.FileInputStream     // Catch: java.lang.Throwable -> L36
            r2.<init>(r4)     // Catch: java.lang.Throwable -> L36
            int r4 = com.xiaopeng.lib.utils.ZipUtils.ZIP_HELADER_LENGTH     // Catch: java.lang.Throwable -> L33
            byte[] r1 = new byte[r4]     // Catch: java.lang.Throwable -> L33
            int r4 = r2.read(r1, r0, r4)     // Catch: java.lang.Throwable -> L33
            int r3 = com.xiaopeng.lib.utils.ZipUtils.ZIP_HELADER_LENGTH     // Catch: java.lang.Throwable -> L33
            if (r4 != r3) goto L2f
            byte[] r4 = com.xiaopeng.lib.utils.ZipUtils.ZIP_HEADER_1     // Catch: java.lang.Throwable -> L33
            boolean r4 = java.util.Arrays.equals(r4, r1)     // Catch: java.lang.Throwable -> L33
            if (r4 != 0) goto L2d
            byte[] r4 = com.xiaopeng.lib.utils.ZipUtils.ZIP_HEADER_2     // Catch: java.lang.Throwable -> L33
            boolean r4 = java.util.Arrays.equals(r4, r1)     // Catch: java.lang.Throwable -> L33
            if (r4 == 0) goto L2f
        L2d:
            r4 = 1
            r0 = r4
        L2f:
            r2.close()     // Catch: java.io.IOException -> L3f
            goto L3f
        L33:
            r4 = move-exception
            r1 = r2
            goto L37
        L36:
            r4 = move-exception
        L37:
            r4.printStackTrace()     // Catch: java.lang.Throwable -> L40
            if (r1 == 0) goto L3f
            r1.close()     // Catch: java.io.IOException -> L3f
        L3f:
            return r0
        L40:
            r4 = move-exception
            if (r1 == 0) goto L46
            r1.close()     // Catch: java.io.IOException -> L46
        L46:
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaopeng.lib.utils.ZipUtils.isArchiveFile(java.io.File):boolean");
    }

    public static String compressForGzip(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            GZIPOutputStream gZIPOutputStream = new GZIPOutputStream(byteArrayOutputStream);
            gZIPOutputStream.write(str.getBytes());
            gZIPOutputStream.close();
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.flush();
            byteArrayOutputStream.close();
            return compressForBase64(byteArray);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public static String decompressForGzip(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        byte[] decompressForBase64 = decompressForBase64(str);
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(decompressForBase64);
            GZIPInputStream gZIPInputStream = new GZIPInputStream(byteArrayInputStream);
            byte[] bArr = new byte[1024];
            while (true) {
                int read = gZIPInputStream.read(bArr, 0, 1024);
                if (read > 0) {
                    byteArrayOutputStream.write(bArr, 0, read);
                } else {
                    gZIPInputStream.close();
                    byteArrayInputStream.close();
                    byteArrayOutputStream.close();
                    return byteArrayOutputStream.toString();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String compressForBase64(byte[] bArr) {
        return compressForBase64(bArr, 0);
    }

    public static String compressForBase64(byte[] bArr, int i) {
        if (bArr == null || bArr.length == 0) {
            return null;
        }
        return Base64.encodeToString(bArr, i);
    }

    public static byte[] decompressForBase64(String str) {
        return decompressForBase64(str, 0);
    }

    public static byte[] decompressForBase64(String str, int i) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        return Base64.decode(str, i);
    }

    public static byte[] compressForUpload(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            GZIPOutputStream gZIPOutputStream = new GZIPOutputStream(byteArrayOutputStream);
            gZIPOutputStream.write(str.getBytes());
            gZIPOutputStream.close();
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.flush();
            byteArrayOutputStream.close();
            return byteArray;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public static String decompressForUpload(byte[] bArr) {
        if (bArr != null && bArr.length != 0) {
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bArr);
                GZIPInputStream gZIPInputStream = new GZIPInputStream(byteArrayInputStream);
                byte[] bArr2 = new byte[1024];
                while (true) {
                    int read = gZIPInputStream.read(bArr2, 0, 1024);
                    if (read > 0) {
                        byteArrayOutputStream.write(bArr2, 0, read);
                    } else {
                        gZIPInputStream.close();
                        byteArrayInputStream.close();
                        byteArrayOutputStream.close();
                        return byteArrayOutputStream.toString();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static boolean zip(String str, String str2) throws IOException {
        File file;
        ZipOutputStream zipOutputStream;
        ZipOutputStream zipOutputStream2 = null;
        try {
            try {
                File file2 = new File(str2);
                file = new File(str);
                zipOutputStream = new ZipOutputStream(new FileOutputStream(file2));
            } catch (Throwable unused) {
            }
        } catch (Exception e) {
            e = e;
        }
        try {
            if (file.isFile()) {
                zipFileOrDirectory(zipOutputStream, file, "");
            } else {
                File[] listFiles = file.listFiles();
                if (listFiles != null) {
                    for (File file3 : listFiles) {
                        zipFileOrDirectory(zipOutputStream, file3, "");
                    }
                }
            }
            zipOutputStream.flush();
            FileUtils.closeQuietly(zipOutputStream);
            return true;
        } catch (Exception e2) {
            e = e2;
            zipOutputStream2 = zipOutputStream;
            e.printStackTrace();
            FileUtils.closeQuietly(zipOutputStream2);
            return false;
        } catch (Throwable unused2) {
            zipOutputStream2 = zipOutputStream;
            FileUtils.closeQuietly(zipOutputStream2);
            return false;
        }
    }

    public static void zipFileOrDirectory(ZipOutputStream zipOutputStream, File file, String str) throws IOException {
        zipFileOrDirectory(zipOutputStream, file, str, true);
    }

    public static void zipFileOrDirectory(ZipOutputStream zipOutputStream, File file, String str, boolean z) throws IOException {
        FileInputStream fileInputStream;
        int i;
        BufferedInputStream bufferedInputStream;
        BufferedInputStream bufferedInputStream2 = null;
        try {
            try {
            } catch (Throwable th) {
                th = th;
            }
        } catch (Exception e) {
            e = e;
            fileInputStream = null;
        } catch (Throwable th2) {
            th = th2;
            fileInputStream = null;
        }
        if (!file.isDirectory()) {
            byte[] bArr = new byte[4096];
            fileInputStream = new FileInputStream(file);
            try {
                bufferedInputStream = new BufferedInputStream(fileInputStream);
            } catch (Exception e2) {
                e = e2;
            }
            try {
                zipOutputStream.putNextEntry(new ZipEntry(str + file.getName()));
                while (true) {
                    int read = bufferedInputStream.read(bArr);
                    if (read == -1) {
                        break;
                    }
                    zipOutputStream.write(bArr, 0, read);
                }
                zipOutputStream.flush();
                zipOutputStream.closeEntry();
                bufferedInputStream2 = bufferedInputStream;
            } catch (Exception e3) {
                e = e3;
                bufferedInputStream2 = bufferedInputStream;
                e.printStackTrace();
                FileUtils.closeQuietly(bufferedInputStream2);
                FileUtils.closeQuietly(fileInputStream);
            } catch (Throwable th3) {
                th = th3;
                bufferedInputStream2 = bufferedInputStream;
                FileUtils.closeQuietly(bufferedInputStream2);
                FileUtils.closeQuietly(fileInputStream);
                throw th;
            }
            FileUtils.closeQuietly(bufferedInputStream2);
            FileUtils.closeQuietly(fileInputStream);
        }
        File[] listFiles = file.listFiles();
        if (listFiles != null && listFiles.length > 0) {
            for (File file2 : listFiles) {
                zipFileOrDirectory(zipOutputStream, file2, str + file.getName() + "/", z);
            }
        } else if (!z) {
            zipOutputStream.putNextEntry(new ZipEntry(file.getAbsolutePath() + "/"));
            zipOutputStream.closeEntry();
        }
        fileInputStream = null;
        FileUtils.closeQuietly(bufferedInputStream2);
        FileUtils.closeQuietly(fileInputStream);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r3v1 */
    /* JADX WARN: Type inference failed for: r3v10, types: [java.io.BufferedInputStream, java.io.Closeable] */
    /* JADX WARN: Type inference failed for: r3v4, types: [java.io.Closeable] */
    /* JADX WARN: Type inference failed for: r3v6 */
    /* JADX WARN: Type inference failed for: r3v9 */
    /* JADX WARN: Type inference failed for: r9v0, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r9v10 */
    /* JADX WARN: Type inference failed for: r9v12 */
    /* JADX WARN: Type inference failed for: r9v14, types: [java.io.Closeable, java.io.BufferedOutputStream] */
    /* JADX WARN: Type inference failed for: r9v2 */
    /* JADX WARN: Type inference failed for: r9v4 */
    /* JADX WARN: Type inference failed for: r9v6 */
    /* JADX WARN: Type inference failed for: r9v7, types: [java.io.Closeable] */
    /* JADX WARN: Type inference failed for: r9v8, types: [java.io.Closeable] */
    public static File zipFile(String str, String str2) {
        ZipOutputStream zipOutputStream;
        FileInputStream fileInputStream;
        FileInputStream fileInputStream2;
        ?? r3;
        File file = new File(str2);
        try {
            try {
                zipOutputStream = new ZipOutputStream(new FileOutputStream(file));
            } catch (Throwable th) {
                th = th;
            }
            try {
                fileInputStream = new FileInputStream((String) str);
                try {
                    zipOutputStream.putNextEntry(new ZipEntry(new File((String) str).getName()));
                    str = new BufferedOutputStream(zipOutputStream);
                } catch (Exception e) {
                    e = e;
                    str = 0;
                    r3 = 0;
                } catch (Throwable th2) {
                    th = th2;
                    fileInputStream2 = null;
                    th = th;
                    str = fileInputStream2;
                    FileUtils.closeQuietly(zipOutputStream);
                    FileUtils.closeQuietly(fileInputStream);
                    FileUtils.closeQuietly(str);
                    FileUtils.closeQuietly(fileInputStream2);
                    throw th;
                }
            } catch (Exception e2) {
                e = e2;
                str = 0;
                fileInputStream = null;
                r3 = fileInputStream;
                e.printStackTrace();
                FileUtils.closeQuietly(zipOutputStream);
                FileUtils.closeQuietly(fileInputStream);
                FileUtils.closeQuietly(str);
                FileUtils.closeQuietly(r3);
                return null;
            } catch (Throwable th3) {
                th = th3;
                fileInputStream = null;
                fileInputStream2 = fileInputStream;
                th = th;
                str = fileInputStream2;
                FileUtils.closeQuietly(zipOutputStream);
                FileUtils.closeQuietly(fileInputStream);
                FileUtils.closeQuietly(str);
                FileUtils.closeQuietly(fileInputStream2);
                throw th;
            }
            try {
                r3 = new BufferedInputStream(fileInputStream);
                try {
                    byte[] bArr = new byte[1024];
                    while (true) {
                        int read = r3.read(bArr, 0, 1024);
                        if (read != -1) {
                            str.write(bArr, 0, read);
                        } else {
                            str.flush();
                            zipOutputStream.flush();
                            FileUtils.closeQuietly(zipOutputStream);
                            FileUtils.closeQuietly(fileInputStream);
                            FileUtils.closeQuietly(str);
                            FileUtils.closeQuietly(r3);
                            return file;
                        }
                    }
                } catch (Exception e3) {
                    e = e3;
                    e.printStackTrace();
                    FileUtils.closeQuietly(zipOutputStream);
                    FileUtils.closeQuietly(fileInputStream);
                    FileUtils.closeQuietly(str);
                    FileUtils.closeQuietly(r3);
                    return null;
                }
            } catch (Exception e4) {
                e = e4;
                r3 = 0;
            } catch (Throwable th4) {
                fileInputStream2 = null;
                th = th4;
                FileUtils.closeQuietly(zipOutputStream);
                FileUtils.closeQuietly(fileInputStream);
                FileUtils.closeQuietly(str);
                FileUtils.closeQuietly(fileInputStream2);
                throw th;
            }
        } catch (Exception e5) {
            e = e5;
            str = 0;
            zipOutputStream = null;
            fileInputStream = null;
        } catch (Throwable th5) {
            th = th5;
            zipOutputStream = null;
            fileInputStream = null;
        }
    }

    public static File zipMultiFiles(String str, List<String> list) throws IOException {
        return zipMultiFiles(str, list, true);
    }

    public static File zipMultiFiles(String str, List<String> list, boolean z) throws IOException {
        try {
            return zipMultiFilesWithThrow(str, list, z);
        } catch (Exception e) {
            e.printStackTrace();
            return new File(str);
        }
    }

    public static File zipMultiFilesWithThrow(String str, List<String> list) throws IOException {
        return zipMultiFilesWithThrow(str, list, true);
    }

    public static File zipMultiFilesWithThrow(String str, List<String> list, boolean z) throws IOException {
        FileOutputStream fileOutputStream;
        File file = new File(str);
        if (file.exists()) {
            file.delete();
        }
        ZipOutputStream zipOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(new File(str));
            try {
                ZipOutputStream zipOutputStream2 = new ZipOutputStream(fileOutputStream);
                try {
                    for (String str2 : list) {
                        File file2 = new File(str2);
                        String parent = file2.getParent();
                        if (parent == null) {
                            parent = "";
                        }
                        zipFileOrDirectory(zipOutputStream2, file2, parent + "/", z);
                    }
                    zipOutputStream2.flush();
                    zipOutputStream2.closeEntry();
                    FileUtils.closeQuietly(zipOutputStream2);
                    FileUtils.closeQuietly(fileOutputStream);
                    return new File(str);
                } catch (Throwable th) {
                    th = th;
                    zipOutputStream = zipOutputStream2;
                    FileUtils.closeQuietly(zipOutputStream);
                    FileUtils.closeQuietly(fileOutputStream);
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (Throwable th3) {
            th = th3;
            fileOutputStream = null;
        }
    }

    public static String compressForGzipAndBase64NoWrap(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            GZIPOutputStream gZIPOutputStream = new GZIPOutputStream(byteArrayOutputStream);
            gZIPOutputStream.write(str.getBytes());
            gZIPOutputStream.close();
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.flush();
            byteArrayOutputStream.close();
            return compressForBase64(byteArray, 2);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public static String decompressForGzipAndBase64NoWrap(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        byte[] decompressForBase64 = decompressForBase64(str, 2);
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(decompressForBase64);
            GZIPInputStream gZIPInputStream = new GZIPInputStream(byteArrayInputStream);
            byte[] bArr = new byte[1024];
            while (true) {
                int read = gZIPInputStream.read(bArr, 0, 1024);
                if (read > 0) {
                    byteArrayOutputStream.write(bArr, 0, read);
                } else {
                    gZIPInputStream.close();
                    byteArrayInputStream.close();
                    byteArrayOutputStream.close();
                    return byteArrayOutputStream.toString();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

package com.android.dx;

import com.xiaopeng.lib.apirouter.ClientConstants;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
/* loaded from: classes.dex */
class AppDataDirGuesser {
    public static final int PER_USER_RANGE = 100000;

    public File guess() {
        try {
            ClassLoader guessSuitableClassLoader = guessSuitableClassLoader();
            Class<?> cls = Class.forName("dalvik.system.PathClassLoader");
            cls.cast(guessSuitableClassLoader);
            File[] guessPath = guessPath(getPathFromThisClassLoader(guessSuitableClassLoader, cls));
            if (guessPath.length > 0) {
                return guessPath[0];
            }
            return null;
        } catch (ClassCastException | ClassNotFoundException unused) {
            return null;
        }
    }

    private ClassLoader guessSuitableClassLoader() {
        return AppDataDirGuesser.class.getClassLoader();
    }

    private String getPathFromThisClassLoader(ClassLoader classLoader, Class<?> cls) {
        try {
            Field declaredField = cls.getDeclaredField(ClientConstants.ALIAS.PATH);
            declaredField.setAccessible(true);
            return (String) declaredField.get(classLoader);
        } catch (ClassCastException | IllegalAccessException | NoSuchFieldException unused) {
            return processClassLoaderString(classLoader.toString());
        }
    }

    static String processClassLoaderString(String str) {
        if (str.contains("DexPathList")) {
            return processClassLoaderString43OrLater(str);
        }
        return processClassLoaderString42OrEarlier(str);
    }

    private static String processClassLoaderString42OrEarlier(String str) {
        int lastIndexOf = str.lastIndexOf(91);
        if (lastIndexOf != -1) {
            str = str.substring(lastIndexOf + 1);
        }
        int indexOf = str.indexOf(93);
        return indexOf == -1 ? str : str.substring(0, indexOf);
    }

    private static String processClassLoaderString43OrLater(String str) {
        int indexOf = str.indexOf("DexPathList") + 11;
        if (str.length() > indexOf + 4) {
            String substring = str.substring(indexOf);
            int indexOf2 = substring.indexOf(93);
            if (substring.charAt(0) == '[' && substring.charAt(1) == '[' && indexOf2 >= 0) {
                String[] split = substring.substring(2, indexOf2).split(",");
                for (int i = 0; i < split.length; i++) {
                    int indexOf3 = split[i].indexOf(34);
                    int lastIndexOf = split[i].lastIndexOf(34);
                    if (indexOf3 > 0 && indexOf3 < lastIndexOf) {
                        split[i] = split[i].substring(indexOf3 + 1, lastIndexOf);
                    }
                }
                StringBuilder sb = new StringBuilder();
                for (String str2 : split) {
                    if (sb.length() > 0) {
                        sb.append(':');
                    }
                    sb.append(str2);
                }
                return sb.toString();
            }
            return str;
        }
        return str;
    }

    File[] guessPath(String str) {
        String[] splitPathList;
        int lastIndexOf;
        int lastIndexOf2;
        int lastIndexOf3;
        int indexOf;
        ArrayList arrayList = new ArrayList();
        for (String str2 : splitPathList(str)) {
            if (str2.startsWith("/data/app/") && (lastIndexOf = str2.lastIndexOf(".apk")) == str2.length() - 4 && (lastIndexOf2 = str2.lastIndexOf("/", lastIndexOf)) != 9 && (lastIndexOf3 = str2.lastIndexOf("/", lastIndexOf2 - 1)) != -1 && (indexOf = str2.indexOf("-", lastIndexOf3)) != -1) {
                String substring = str2.substring(lastIndexOf3 + 1, indexOf);
                File writeableDirectory = getWriteableDirectory("/data/data/" + substring);
                if (writeableDirectory == null) {
                    writeableDirectory = guessUserDataDirectory(substring);
                }
                if (writeableDirectory != null) {
                    File file = new File(writeableDirectory, "cache");
                    if ((fileOrDirExists(file) || file.mkdir()) && isWriteableDirectory(file)) {
                        arrayList.add(file);
                    }
                }
            }
        }
        return (File[]) arrayList.toArray(new File[arrayList.size()]);
    }

    static String[] splitPathList(String str) {
        if (str.startsWith("dexPath=")) {
            int indexOf = str.indexOf(44);
            str = indexOf == -1 ? str.substring(8) : str.substring(8, indexOf);
        }
        return str.split(":");
    }

    boolean fileOrDirExists(File file) {
        return file.exists();
    }

    boolean isWriteableDirectory(File file) {
        return file.isDirectory() && file.canWrite();
    }

    Integer getProcessUid() {
        try {
            return (Integer) Class.forName("android.os.Process").getMethod("myUid", new Class[0]).invoke(null, new Object[0]);
        } catch (Exception unused) {
            return null;
        }
    }

    File guessUserDataDirectory(String str) {
        Integer processUid = getProcessUid();
        if (processUid == null) {
            return null;
        }
        return getWriteableDirectory(String.format("/data/user/%d/%s", Integer.valueOf(processUid.intValue() / 100000), str));
    }

    private File getWriteableDirectory(String str) {
        File file = new File(str);
        if (isWriteableDirectory(file)) {
            return file;
        }
        return null;
    }
}

package com.android.multidex;

import com.android.dx.cf.attrib.AttRuntimeVisibleAnnotations;
import com.android.dx.cf.direct.DirectClassFile;
import com.android.dx.cf.iface.Attribute;
import com.android.dx.cf.iface.FieldList;
import com.android.dx.cf.iface.HasAttribute;
import com.android.dx.cf.iface.MethodList;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.zip.ZipFile;
/* loaded from: classes.dex */
public class MainDexListBuilder {
    private static final String CLASS_EXTENSION = ".class";
    private static final String DISABLE_ANNOTATION_RESOLUTION_WORKAROUND = "--disable-annotation-resolution-workaround";
    private static final String EOL;
    private static final int STATUS_ERROR = 1;
    private static final String USAGE_MESSAGE;
    private Set<String> filesToKeep = new HashSet();

    static {
        String property = System.getProperty("line.separator");
        EOL = property;
        USAGE_MESSAGE = "Usage:" + property + property + "Short version: Don't use this." + property + property + "Slightly longer version: This tool is used by mainDexClasses script to build" + property + "the main dex list." + property;
    }

    public static void main(String[] strArr) {
        int i = 0;
        boolean z = true;
        while (i < strArr.length - 2) {
            if (strArr[i].equals(DISABLE_ANNOTATION_RESOLUTION_WORKAROUND)) {
                z = false;
            } else {
                System.err.println("Invalid option " + strArr[i]);
                printUsage();
                System.exit(1);
            }
            i++;
        }
        if (strArr.length - i != 2) {
            printUsage();
            System.exit(1);
        }
        try {
            printList(new MainDexListBuilder(z, strArr[i], strArr[i + 1]).getMainDexList());
        } catch (IOException e) {
            System.err.println("A fatal error occured: " + e.getMessage());
            System.exit(1);
        }
    }

    public MainDexListBuilder(boolean z, String str, String str2) throws IOException {
        Path path;
        ZipFile zipFile = null;
        try {
            try {
                ZipFile zipFile2 = new ZipFile(str);
                try {
                    path = new Path(str2);
                    try {
                        ClassReferenceListBuilder classReferenceListBuilder = new ClassReferenceListBuilder(path);
                        classReferenceListBuilder.addRoots(zipFile2);
                        Iterator<String> it = classReferenceListBuilder.getClassNames().iterator();
                        while (it.hasNext()) {
                            this.filesToKeep.add(it.next() + CLASS_EXTENSION);
                        }
                        if (z) {
                            keepAnnotated(path);
                        }
                        try {
                            zipFile2.close();
                        } catch (IOException unused) {
                        }
                        for (ClassPathElement classPathElement : path.elements) {
                            try {
                                classPathElement.close();
                            } catch (IOException unused2) {
                            }
                        }
                    } catch (Throwable th) {
                        th = th;
                        zipFile = zipFile2;
                        try {
                            zipFile.close();
                        } catch (IOException unused3) {
                        }
                        if (path != null) {
                            for (ClassPathElement classPathElement2 : path.elements) {
                                try {
                                    classPathElement2.close();
                                } catch (IOException unused4) {
                                }
                            }
                        }
                        throw th;
                    }
                } catch (Throwable th2) {
                    th = th2;
                    path = null;
                }
            } catch (IOException e) {
                throw new IOException("\"" + str + "\" can not be read as a zip archive. (" + e.getMessage() + ")", e);
            }
        } catch (Throwable th3) {
            th = th3;
            path = null;
        }
    }

    public Set<String> getMainDexList() {
        return this.filesToKeep;
    }

    private static void printUsage() {
        System.err.print(USAGE_MESSAGE);
    }

    private static void printList(Set<String> set) {
        for (String str : set) {
            System.out.println(str);
        }
    }

    private void keepAnnotated(Path path) throws FileNotFoundException {
        for (ClassPathElement classPathElement : path.getElements()) {
            for (String str : classPathElement.list()) {
                if (str.endsWith(CLASS_EXTENSION)) {
                    DirectClassFile directClassFile = path.getClass(str);
                    if (hasRuntimeVisibleAnnotation(directClassFile)) {
                        this.filesToKeep.add(str);
                    } else {
                        MethodList methods = directClassFile.getMethods();
                        int i = 0;
                        int i2 = 0;
                        while (true) {
                            if (i2 < methods.size()) {
                                if (hasRuntimeVisibleAnnotation(methods.get(i2))) {
                                    this.filesToKeep.add(str);
                                    break;
                                }
                                i2++;
                            } else {
                                FieldList fields = directClassFile.getFields();
                                while (true) {
                                    if (i >= fields.size()) {
                                        break;
                                    } else if (hasRuntimeVisibleAnnotation(fields.get(i))) {
                                        this.filesToKeep.add(str);
                                        break;
                                    } else {
                                        i++;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean hasRuntimeVisibleAnnotation(HasAttribute hasAttribute) {
        Attribute findFirst = hasAttribute.getAttributes().findFirst(AttRuntimeVisibleAnnotations.ATTRIBUTE_NAME);
        return findFirst != null && ((AttRuntimeVisibleAnnotations) findFirst).getAnnotations().size() > 0;
    }
}

package com.android.dx;

import com.android.dex.DexFormat;
import com.android.dx.dex.DexOptions;
import com.android.dx.dex.code.RopTranslator;
import com.android.dx.dex.file.ClassDefItem;
import com.android.dx.dex.file.DexFile;
import com.android.dx.dex.file.EncodedField;
import com.android.dx.dex.file.EncodedMethod;
import com.android.dx.rop.code.RopMethod;
import com.android.dx.rop.cst.CstString;
import com.android.dx.rop.cst.CstType;
import com.android.dx.rop.type.StdTypeList;
import com.xiaopeng.wirelessprojection.dmr.RendererActivity;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
/* loaded from: classes.dex */
public final class DexMaker {
    private static boolean didWarnBlacklistedMethods;
    private static boolean didWarnNonBaseDexClassLoader;
    private boolean markAsTrusted;
    private DexFile outputDex;
    private ClassLoader sharedClassLoader;
    private final Map<TypeId<?>, TypeDeclaration> types = new LinkedHashMap();

    /* JADX INFO: Access modifiers changed from: package-private */
    public TypeDeclaration getTypeDeclaration(TypeId<?> typeId) {
        TypeDeclaration typeDeclaration = this.types.get(typeId);
        if (typeDeclaration == null) {
            TypeDeclaration typeDeclaration2 = new TypeDeclaration(typeId);
            this.types.put(typeId, typeDeclaration2);
            return typeDeclaration2;
        }
        return typeDeclaration;
    }

    public void declare(TypeId<?> typeId, String str, int i, TypeId<?> typeId2, TypeId<?>... typeIdArr) {
        TypeDeclaration typeDeclaration = getTypeDeclaration(typeId);
        if ((i & (-5138)) != 0) {
            throw new IllegalArgumentException("Unexpected flag: " + Integer.toHexString(i));
        }
        if (typeDeclaration.declared) {
            throw new IllegalStateException("already declared: " + typeId);
        }
        typeDeclaration.declared = true;
        typeDeclaration.flags = i;
        typeDeclaration.supertype = typeId2;
        typeDeclaration.sourceFile = str;
        typeDeclaration.interfaces = new TypeList(typeIdArr);
    }

    public Code declare(MethodId<?, ?> methodId, int i) {
        TypeDeclaration typeDeclaration = getTypeDeclaration(methodId.declaringType);
        if (typeDeclaration.methods.containsKey(methodId)) {
            throw new IllegalStateException("already declared: " + methodId);
        }
        if ((i & (-5504)) != 0) {
            throw new IllegalArgumentException("Unexpected flag: " + Integer.toHexString(i));
        }
        if ((i & 32) != 0) {
            i = (i & (-33)) | 131072;
        }
        if (methodId.isConstructor() || methodId.isStaticInitializer()) {
            i |= 65536;
        }
        MethodDeclaration methodDeclaration = new MethodDeclaration(methodId, i);
        typeDeclaration.methods.put(methodId, methodDeclaration);
        return methodDeclaration.code;
    }

    public void declare(FieldId<?, ?> fieldId, int i, Object obj) {
        TypeDeclaration typeDeclaration = getTypeDeclaration(fieldId.declaringType);
        if (typeDeclaration.fields.containsKey(fieldId)) {
            throw new IllegalStateException("already declared: " + fieldId);
        }
        if ((i & (-4320)) != 0) {
            throw new IllegalArgumentException("Unexpected flag: " + Integer.toHexString(i));
        }
        if ((i & 8) == 0 && obj != null) {
            throw new IllegalArgumentException("staticValue is non-null, but field is not static");
        }
        typeDeclaration.fields.put(fieldId, new FieldDeclaration(fieldId, i, obj));
    }

    public byte[] generate() {
        if (this.outputDex == null) {
            DexOptions dexOptions = new DexOptions();
            dexOptions.minSdkVersion = 13;
            this.outputDex = new DexFile(dexOptions);
        }
        for (TypeDeclaration typeDeclaration : this.types.values()) {
            this.outputDex.add(typeDeclaration.toClassDefItem());
        }
        try {
            return this.outputDex.toDex(null, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateFileName() {
        Set<TypeId<?>> keySet = this.types.keySet();
        int size = keySet.size();
        int[] iArr = new int[size];
        int i = 0;
        for (TypeId<?> typeId : keySet) {
            TypeDeclaration typeDeclaration = getTypeDeclaration(typeId);
            Set keySet2 = typeDeclaration.methods.keySet();
            if (typeDeclaration.supertype != null) {
                iArr[i] = (((typeDeclaration.supertype.hashCode() * 31) + typeDeclaration.interfaces.hashCode()) * 31) + keySet2.hashCode();
                i++;
            }
        }
        Arrays.sort(iArr);
        int i2 = 1;
        for (int i3 = 0; i3 < size; i3++) {
            i2 = (i2 * 31) + iArr[i3];
        }
        return "Generated_" + i2 + ".jar";
    }

    public void setSharedClassLoader(ClassLoader classLoader) {
        this.sharedClassLoader = classLoader;
    }

    public void markAsTrusted() {
        this.markAsTrusted = true;
    }

    private ClassLoader generateClassLoader(File file, File file2, ClassLoader classLoader) {
        try {
            try {
                ClassLoader classLoader2 = this.sharedClassLoader;
                boolean z = classLoader2 != null;
                if (classLoader == null) {
                    classLoader = classLoader2 != null ? classLoader2 : null;
                }
                Class<?> cls = Class.forName("dalvik.system.BaseDexClassLoader");
                if (z && !cls.isAssignableFrom(classLoader.getClass())) {
                    if (!classLoader.getClass().getName().equals("java.lang.BootClassLoader") && !didWarnNonBaseDexClassLoader) {
                        System.err.println("Cannot share classloader as shared classloader '" + classLoader + "' is not a subclass of '" + cls + "'");
                        didWarnNonBaseDexClassLoader = true;
                    }
                    z = false;
                }
                if (this.markAsTrusted) {
                    try {
                        if (z) {
                            classLoader.getClass().getMethod("addDexPath", String.class, Boolean.TYPE).invoke(classLoader, file.getPath(), true);
                            return classLoader;
                        }
                        return (ClassLoader) cls.getConstructor(String.class, File.class, String.class, ClassLoader.class, Boolean.TYPE).newInstance(file.getPath(), file2.getAbsoluteFile(), null, classLoader, true);
                    } catch (InvocationTargetException e) {
                        if (e.getCause() instanceof SecurityException) {
                            if (!didWarnBlacklistedMethods) {
                                System.err.println("Cannot allow to call blacklisted super methods. This might break spying on system classes." + e.getCause());
                                didWarnBlacklistedMethods = true;
                            }
                        } else {
                            throw e;
                        }
                    }
                }
                if (z) {
                    classLoader.getClass().getMethod("addDexPath", String.class).invoke(classLoader, file.getPath());
                    return classLoader;
                }
                return (ClassLoader) Class.forName("dalvik.system.DexClassLoader").getConstructor(String.class, String.class, String.class, ClassLoader.class).newInstance(file.getPath(), file2.getAbsolutePath(), null, classLoader);
            } catch (InvocationTargetException e2) {
                throw new RuntimeException(e2.getCause());
            }
        } catch (ClassNotFoundException e3) {
            throw new UnsupportedOperationException("load() requires a Dalvik VM", e3);
        } catch (IllegalAccessException unused) {
            throw new AssertionError();
        } catch (InstantiationException unused2) {
            throw new AssertionError();
        } catch (NoSuchMethodException unused3) {
            throw new AssertionError();
        }
    }

    public ClassLoader generateAndLoad(ClassLoader classLoader, File file) throws IOException {
        if (file == null) {
            String property = System.getProperty("dexmaker.dexcache");
            if (property != null) {
                file = new File(property);
            } else {
                file = new AppDataDirGuesser().guess();
                if (file == null) {
                    throw new IllegalArgumentException("dexcache == null (and no default could be found; consider setting the 'dexmaker.dexcache' system property)");
                }
            }
        }
        File file2 = new File(file, generateFileName());
        if (file2.exists()) {
            return generateClassLoader(file2, file, classLoader);
        }
        byte[] generate = generate();
        file2.createNewFile();
        JarOutputStream jarOutputStream = new JarOutputStream(new BufferedOutputStream(new FileOutputStream(file2)));
        try {
            JarEntry jarEntry = new JarEntry(DexFormat.DEX_IN_JAR_NAME);
            jarEntry.setSize(generate.length);
            jarOutputStream.putNextEntry(jarEntry);
            jarOutputStream.write(generate);
            jarOutputStream.closeEntry();
            jarOutputStream.close();
            return generateClassLoader(file2, file, classLoader);
        } catch (Throwable th) {
            jarOutputStream.close();
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DexFile getDexFile() {
        if (this.outputDex == null) {
            DexOptions dexOptions = new DexOptions();
            dexOptions.minSdkVersion = 13;
            this.outputDex = new DexFile(dexOptions);
        }
        return this.outputDex;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class TypeDeclaration {
        private ClassDefItem classDefItem;
        private boolean declared;
        private int flags;
        private TypeList interfaces;
        private String sourceFile;
        private TypeId<?> supertype;
        private final TypeId<?> type;
        private final Map<FieldId, FieldDeclaration> fields = new LinkedHashMap();
        private final Map<MethodId, MethodDeclaration> methods = new LinkedHashMap();

        TypeDeclaration(TypeId<?> typeId) {
            this.type = typeId;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public ClassDefItem toClassDefItem() {
            if (!this.declared) {
                throw new IllegalStateException("Undeclared type " + this.type + " declares members: " + this.fields.keySet() + RendererActivity.DEFAULT_TITLE + this.methods.keySet());
            }
            DexOptions dexOptions = new DexOptions();
            dexOptions.minSdkVersion = 13;
            CstType cstType = this.type.constant;
            if (this.classDefItem == null) {
                this.classDefItem = new ClassDefItem(cstType, this.flags, this.supertype.constant, this.interfaces.ropTypes, new CstString(this.sourceFile));
                for (MethodDeclaration methodDeclaration : this.methods.values()) {
                    EncodedMethod encodedMethod = methodDeclaration.toEncodedMethod(dexOptions);
                    if (methodDeclaration.isDirect()) {
                        this.classDefItem.addDirectMethod(encodedMethod);
                    } else {
                        this.classDefItem.addVirtualMethod(encodedMethod);
                    }
                }
                for (FieldDeclaration fieldDeclaration : this.fields.values()) {
                    EncodedField encodedField = fieldDeclaration.toEncodedField();
                    if (!fieldDeclaration.isStatic()) {
                        this.classDefItem.addInstanceField(encodedField);
                    } else {
                        this.classDefItem.addStaticField(encodedField, Constants.getConstant(fieldDeclaration.staticValue));
                    }
                }
            }
            return this.classDefItem;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class FieldDeclaration {
        private final int accessFlags;
        final FieldId<?, ?> fieldId;
        private final Object staticValue;

        FieldDeclaration(FieldId<?, ?> fieldId, int i, Object obj) {
            if ((i & 8) == 0 && obj != null) {
                throw new IllegalArgumentException("instance fields may not have a value");
            }
            this.fieldId = fieldId;
            this.accessFlags = i;
            this.staticValue = obj;
        }

        EncodedField toEncodedField() {
            return new EncodedField(this.fieldId.constant, this.accessFlags);
        }

        public boolean isStatic() {
            return (this.accessFlags & 8) != 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class MethodDeclaration {
        private final Code code = new Code(this);
        private final int flags;
        final MethodId<?, ?> method;

        public MethodDeclaration(MethodId<?, ?> methodId, int i) {
            this.method = methodId;
            this.flags = i;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public boolean isStatic() {
            return (this.flags & 8) != 0;
        }

        boolean isDirect() {
            return (this.flags & 65546) != 0;
        }

        EncodedMethod toEncodedMethod(DexOptions dexOptions) {
            int i = this.flags;
            if ((i & 1024) != 0 || (i & 256) != 0) {
                return new EncodedMethod(this.method.constant, this.flags, null, StdTypeList.EMPTY);
            }
            return new EncodedMethod(this.method.constant, this.flags, RopTranslator.translate(new RopMethod(this.code.toBasicBlocks(), 0), 1, null, this.code.paramSize(), dexOptions), StdTypeList.EMPTY);
        }
    }
}

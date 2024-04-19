package com.android.dx.stock;

import com.android.dx.Code;
import com.android.dx.Comparison;
import com.android.dx.DexMaker;
import com.android.dx.Label;
import com.android.dx.Local;
import com.android.dx.MethodId;
import com.android.dx.TypeId;
import com.android.dx.rop.code.RegisterSpec;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.IInputController;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
/* loaded from: classes.dex */
public final class ProxyBuilder<T> {
    private static final String FIELD_NAME_HANDLER = "$__handler";
    private static final String FIELD_NAME_METHODS = "$__methodArray";
    private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_BOXED;
    private static final Map<Class<?>, MethodId<?, ?>> PRIMITIVE_TO_UNBOX_METHOD;
    private static final Map<TypeId<?>, MethodId<?, ?>> PRIMITIVE_TYPE_TO_UNBOX_METHOD;
    public static final int VERSION = 1;
    private static final Map<ProxiedClass<?>, Class<?>> generatedProxyClasses = Collections.synchronizedMap(new HashMap());
    private final Class<T> baseClass;
    private File dexCache;
    private InvocationHandler handler;
    private boolean markTrusted;
    private Method[] methods;
    private boolean sharedClassLoader;
    private ClassLoader parentClassLoader = ProxyBuilder.class.getClassLoader();
    private Class<?>[] constructorArgTypes = new Class[0];
    private Object[] constructorArgValues = new Object[0];
    private List<Class<?>> interfaces = new ArrayList();

    static {
        HashMap hashMap = new HashMap();
        PRIMITIVE_TO_BOXED = hashMap;
        hashMap.put(Boolean.TYPE, Boolean.class);
        hashMap.put(Integer.TYPE, Integer.class);
        hashMap.put(Byte.TYPE, Byte.class);
        hashMap.put(Long.TYPE, Long.class);
        hashMap.put(Short.TYPE, Short.class);
        hashMap.put(Float.TYPE, Float.class);
        hashMap.put(Double.TYPE, Double.class);
        hashMap.put(Character.TYPE, Character.class);
        PRIMITIVE_TYPE_TO_UNBOX_METHOD = new HashMap();
        for (Map.Entry entry : hashMap.entrySet()) {
            TypeId<?> typeId = TypeId.get((Class) entry.getKey());
            TypeId typeId2 = TypeId.get((Class) entry.getValue());
            PRIMITIVE_TYPE_TO_UNBOX_METHOD.put(typeId, typeId2.getMethod(typeId2, "valueOf", typeId));
        }
        HashMap hashMap2 = new HashMap();
        hashMap2.put(Boolean.TYPE, TypeId.get(Boolean.class).getMethod(TypeId.BOOLEAN, "booleanValue", new TypeId[0]));
        hashMap2.put(Integer.TYPE, TypeId.get(Integer.class).getMethod(TypeId.INT, "intValue", new TypeId[0]));
        hashMap2.put(Byte.TYPE, TypeId.get(Byte.class).getMethod(TypeId.BYTE, "byteValue", new TypeId[0]));
        hashMap2.put(Long.TYPE, TypeId.get(Long.class).getMethod(TypeId.LONG, "longValue", new TypeId[0]));
        hashMap2.put(Short.TYPE, TypeId.get(Short.class).getMethod(TypeId.SHORT, "shortValue", new TypeId[0]));
        hashMap2.put(Float.TYPE, TypeId.get(Float.class).getMethod(TypeId.FLOAT, "floatValue", new TypeId[0]));
        hashMap2.put(Double.TYPE, TypeId.get(Double.class).getMethod(TypeId.DOUBLE, "doubleValue", new TypeId[0]));
        hashMap2.put(Character.TYPE, TypeId.get(Character.class).getMethod(TypeId.CHAR, "charValue", new TypeId[0]));
        PRIMITIVE_TO_UNBOX_METHOD = hashMap2;
    }

    private ProxyBuilder(Class<T> cls) {
        this.baseClass = cls;
    }

    public static <T> ProxyBuilder<T> forClass(Class<T> cls) {
        return new ProxyBuilder<>(cls);
    }

    public ProxyBuilder<T> parentClassLoader(ClassLoader classLoader) {
        this.parentClassLoader = classLoader;
        return this;
    }

    public ProxyBuilder<T> handler(InvocationHandler invocationHandler) {
        this.handler = invocationHandler;
        return this;
    }

    public ProxyBuilder<T> dexCache(File file) {
        File file2 = new File(file, RegisterSpec.PREFIX + Integer.toString(1));
        this.dexCache = file2;
        file2.mkdir();
        return this;
    }

    public ProxyBuilder<T> implementing(Class<?>... clsArr) {
        List<Class<?>> list = this.interfaces;
        for (Class<?> cls : clsArr) {
            if (!cls.isInterface()) {
                throw new IllegalArgumentException("Not an interface: " + cls.getName());
            }
            if (!list.contains(cls)) {
                list.add(cls);
            }
        }
        return this;
    }

    public ProxyBuilder<T> constructorArgValues(Object... objArr) {
        this.constructorArgValues = objArr;
        return this;
    }

    public ProxyBuilder<T> constructorArgTypes(Class<?>... clsArr) {
        this.constructorArgTypes = clsArr;
        return this;
    }

    public ProxyBuilder<T> onlyMethods(Method[] methodArr) {
        this.methods = methodArr;
        return this;
    }

    public ProxyBuilder<T> withSharedClassLoader() {
        this.sharedClassLoader = true;
        return this;
    }

    public ProxyBuilder<T> markTrusted() {
        this.markTrusted = true;
        return this;
    }

    public T build() throws IOException {
        check(this.handler != null, "handler == null");
        check(this.constructorArgTypes.length == this.constructorArgValues.length, "constructorArgValues.length != constructorArgTypes.length");
        try {
            try {
                T newInstance = buildProxyClass().getConstructor(this.constructorArgTypes).newInstance(this.constructorArgValues);
                setInvocationHandler(newInstance, this.handler);
                return newInstance;
            } catch (IllegalAccessException e) {
                throw new AssertionError(e);
            } catch (InstantiationException e2) {
                throw new AssertionError(e2);
            } catch (InvocationTargetException e3) {
                throw launderCause(e3);
            }
        } catch (NoSuchMethodException unused) {
            throw new IllegalArgumentException("No constructor for " + this.baseClass.getName() + " with parameter types " + Arrays.toString(this.constructorArgTypes));
        }
    }

    public Class<? extends T> buildProxyClass() throws IOException {
        ClassLoader classLoader;
        ClassLoader generateAndLoad;
        if (this.sharedClassLoader) {
            classLoader = this.baseClass.getClassLoader();
        } else {
            classLoader = this.parentClassLoader;
        }
        ProxiedClass<?> proxiedClass = new ProxiedClass<>(this.baseClass, this.interfaces, classLoader, this.sharedClassLoader);
        Map<ProxiedClass<?>, Class<?>> map = generatedProxyClasses;
        Class<? extends T> cls = (Class<? extends T>) map.get(proxiedClass);
        if (cls != null) {
            return cls;
        }
        DexMaker dexMaker = new DexMaker();
        String methodNameForProxyOf = getMethodNameForProxyOf(this.baseClass, this.interfaces);
        TypeId<?> typeId = TypeId.get("L" + methodNameForProxyOf + ";");
        TypeId<?> typeId2 = TypeId.get(this.baseClass);
        generateConstructorsAndFields(dexMaker, typeId, typeId2, this.baseClass);
        Method[] methodArr = this.methods;
        if (methodArr == null) {
            methodArr = getMethodsToProxyRecursive();
        }
        Arrays.sort(methodArr, new Comparator<Method>() { // from class: com.android.dx.stock.ProxyBuilder.1
            @Override // java.util.Comparator
            public int compare(Method method, Method method2) {
                return (method.getDeclaringClass() + method.getName() + Arrays.toString(method.getParameterTypes()) + method.getReturnType()).compareTo(method2.getDeclaringClass() + method2.getName() + Arrays.toString(method2.getParameterTypes()) + method2.getReturnType());
            }
        });
        generateCodeForAllMethods(dexMaker, typeId, methodArr, typeId2);
        dexMaker.declare(typeId, methodNameForProxyOf + ".generated", 1, typeId2, getInterfacesAsTypeIds());
        if (this.sharedClassLoader) {
            dexMaker.setSharedClassLoader(classLoader);
        }
        if (this.markTrusted) {
            dexMaker.markAsTrusted();
        }
        if (this.sharedClassLoader) {
            generateAndLoad = dexMaker.generateAndLoad(null, this.dexCache);
        } else {
            generateAndLoad = dexMaker.generateAndLoad(this.parentClassLoader, this.dexCache);
        }
        try {
            Class<? extends T> loadClass = loadClass(generateAndLoad, methodNameForProxyOf);
            setMethodsStaticField(loadClass, methodArr);
            map.put(proxiedClass, loadClass);
            return loadClass;
        } catch (ClassNotFoundException e) {
            throw new AssertionError(e);
        } catch (IllegalAccessError e2) {
            throw new UnsupportedOperationException("cannot proxy inaccessible class " + this.baseClass, e2);
        }
    }

    private Class<? extends T> loadClass(ClassLoader classLoader, String str) throws ClassNotFoundException {
        return (Class<? extends T>) classLoader.loadClass(str);
    }

    private static RuntimeException launderCause(InvocationTargetException invocationTargetException) {
        Throwable cause = invocationTargetException.getCause();
        if (cause instanceof Error) {
            throw ((Error) cause);
        }
        if (cause instanceof RuntimeException) {
            throw ((RuntimeException) cause);
        }
        throw new UndeclaredThrowableException(cause);
    }

    private static void setMethodsStaticField(Class<?> cls, Method[] methodArr) {
        try {
            Field declaredField = cls.getDeclaredField(FIELD_NAME_METHODS);
            declaredField.setAccessible(true);
            declaredField.set(null, methodArr);
        } catch (IllegalAccessException e) {
            throw new AssertionError(e);
        } catch (NoSuchFieldException e2) {
            throw new AssertionError(e2);
        }
    }

    public static InvocationHandler getInvocationHandler(Object obj) {
        try {
            Field declaredField = obj.getClass().getDeclaredField(FIELD_NAME_HANDLER);
            declaredField.setAccessible(true);
            return (InvocationHandler) declaredField.get(obj);
        } catch (IllegalAccessException e) {
            throw new AssertionError(e);
        } catch (NoSuchFieldException e2) {
            throw new IllegalArgumentException("Not a valid proxy instance", e2);
        }
    }

    public static void setInvocationHandler(Object obj, InvocationHandler invocationHandler) {
        try {
            Field declaredField = obj.getClass().getDeclaredField(FIELD_NAME_HANDLER);
            declaredField.setAccessible(true);
            declaredField.set(obj, invocationHandler);
        } catch (IllegalAccessException e) {
            throw new AssertionError(e);
        } catch (NoSuchFieldException e2) {
            throw new IllegalArgumentException("Not a valid proxy instance", e2);
        }
    }

    public static boolean isProxyClass(Class<?> cls) {
        try {
            cls.getDeclaredField(FIELD_NAME_HANDLER);
            return true;
        } catch (NoSuchFieldException unused) {
            return false;
        }
    }

    private static void throwAbstractMethodError(Code code, Method method, Local<String> local, Local<AbstractMethodError> local2) {
        MethodId<T, Void> constructor = TypeId.get(AbstractMethodError.class).getConstructor(TypeId.STRING);
        code.loadConstant(local, "'" + method + "' cannot be called");
        code.newInstance(local2, constructor, local);
        code.throwValue(local2);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v1, types: [com.android.dx.DexMaker] */
    /* JADX WARN: Type inference failed for: r36v0, types: [com.android.dx.TypeId<G extends T>, com.android.dx.TypeId] */
    private static <T, G extends T> void generateCodeForAllMethods(DexMaker dexMaker, TypeId<G> typeId, Method[] methodArr, TypeId<T> typeId2) {
        TypeId typeId3;
        Class<?>[] clsArr;
        Local newLocal;
        Local newLocal2;
        Local local;
        Local[] localArr;
        TypeId typeId4;
        MethodId methodId;
        DexMaker dexMaker2 = dexMaker;
        TypeId<T> typeId5 = typeId;
        Method[] methodArr2 = methodArr;
        TypeId typeId6 = TypeId.get(InvocationHandler.class);
        TypeId typeId7 = TypeId.get(Method[].class);
        Object field = typeId5.getField(typeId6, FIELD_NAME_HANDLER);
        Object field2 = typeId5.getField(typeId7, FIELD_NAME_METHODS);
        TypeId<?> typeId8 = TypeId.get(Method.class);
        TypeId<?> typeId9 = TypeId.get(Object[].class);
        MethodId method = typeId6.getMethod(TypeId.OBJECT, "invoke", TypeId.OBJECT, typeId8, typeId9);
        int i = 0;
        TypeId typeId10 = typeId6;
        TypeId typeId11 = typeId7;
        while (i < methodArr2.length) {
            Method method2 = methodArr2[i];
            String name = method2.getName();
            Class<?>[] parameterTypes = method2.getParameterTypes();
            int length = parameterTypes.length;
            TypeId<?>[] typeIdArr = new TypeId[length];
            for (int i2 = 0; i2 < length; i2++) {
                typeIdArr[i2] = TypeId.get(parameterTypes[i2]);
            }
            Class<?> returnType = method2.getReturnType();
            MethodId methodId2 = method;
            TypeId typeId12 = TypeId.get(returnType);
            Object obj = field;
            MethodId method3 = typeId5.getMethod(typeId12, name, typeIdArr);
            TypeId typeId13 = TypeId.get(AbstractMethodError.class);
            Object obj2 = field2;
            Code declare = dexMaker2.declare(method3, 1);
            Local<T> local2 = declare.getThis(typeId5);
            Local<T> newLocal3 = declare.newLocal(typeId10);
            Local<T> newLocal4 = declare.newLocal(TypeId.OBJECT);
            Local newLocal5 = declare.newLocal(TypeId.INT);
            Local newLocal6 = declare.newLocal(typeId9);
            TypeId<?> typeId14 = typeId9;
            Local newLocal7 = declare.newLocal(TypeId.INT);
            Local newLocal8 = declare.newLocal(TypeId.OBJECT);
            Local newLocal9 = declare.newLocal(typeId12);
            Local newLocal10 = declare.newLocal(typeId11);
            TypeId typeId15 = typeId11;
            Local newLocal11 = declare.newLocal(typeId8);
            TypeId<?> typeId16 = typeId8;
            Local newLocal12 = declare.newLocal(TypeId.INT);
            Class<?> cls = PRIMITIVE_TO_BOXED.get(returnType);
            Local newLocal13 = cls != null ? declare.newLocal(TypeId.get(cls)) : null;
            Local newLocal14 = declare.newLocal(typeId10);
            TypeId typeId17 = typeId10;
            if ((method2.getModifiers() & 1024) == 0) {
                Local newLocal15 = declare.newLocal(typeId12);
                MethodId method4 = typeId2.getMethod(typeId12, name, typeIdArr);
                typeId3 = typeId12;
                local = newLocal15;
                newLocal = null;
                typeId4 = typeId13;
                methodId = method4;
                localArr = new Local[parameterTypes.length];
                clsArr = parameterTypes;
                newLocal2 = null;
            } else {
                typeId3 = typeId12;
                clsArr = parameterTypes;
                newLocal = declare.newLocal(TypeId.STRING);
                newLocal2 = declare.newLocal(typeId13);
                local = null;
                localArr = null;
                typeId4 = typeId13;
                methodId = null;
            }
            declare.loadConstant(newLocal12, Integer.valueOf(i));
            declare.sget(obj2, newLocal10);
            declare.aget(newLocal11, newLocal10, newLocal12);
            declare.loadConstant(newLocal7, Integer.valueOf(length));
            declare.newArray(newLocal6, newLocal7);
            declare.iget(obj, newLocal3, local2);
            declare.loadConstant(newLocal14, null);
            Label label = new Label();
            declare.compare(Comparison.EQ, label, newLocal14, newLocal3);
            int i3 = length;
            int i4 = 0;
            while (i4 < i3) {
                int i5 = i;
                Local local3 = newLocal5;
                declare.loadConstant(local3, Integer.valueOf(i4));
                declare.aput(newLocal6, local3, boxIfRequired(declare, declare.getParameter(i4, typeIdArr[i4]), newLocal8));
                i4++;
                i3 = i3;
                newLocal2 = newLocal2;
                newLocal5 = local3;
                i = i5;
            }
            int i6 = i;
            Local local4 = newLocal2;
            declare.invokeInterface(methodId2, newLocal4, newLocal3, local2, newLocal11, newLocal6);
            generateCodeForReturnStatement(declare, returnType, newLocal4, newLocal9, newLocal13);
            declare.mark(label);
            if ((method2.getModifiers() & 1024) == 0) {
                for (int i7 = 0; i7 < localArr.length; i7++) {
                    localArr[i7] = declare.getParameter(i7, typeIdArr[i7]);
                }
                if (Void.TYPE.equals(returnType)) {
                    declare.invokeSuper(methodId, null, local2, localArr);
                    declare.returnVoid();
                } else {
                    invokeSuper(methodId, declare, local2, localArr, local);
                    declare.returnValue(local);
                }
            } else {
                throwAbstractMethodError(declare, method2, newLocal, local4);
            }
            TypeId typeId18 = typeId3;
            Code declare2 = dexMaker.declare(typeId.getMethod(typeId18, superMethodName(method2), typeIdArr), 1);
            if ((method2.getModifiers() & 1024) == 0) {
                Local local5 = declare2.getThis(typeId);
                int length2 = clsArr.length;
                Local[] localArr2 = new Local[length2];
                for (int i8 = 0; i8 < length2; i8++) {
                    localArr2[i8] = declare2.getParameter(i8, typeIdArr[i8]);
                }
                if (Void.TYPE.equals(returnType)) {
                    declare2.invokeSuper(methodId, null, local5, localArr2);
                    declare2.returnVoid();
                } else {
                    Local newLocal16 = declare2.newLocal(typeId18);
                    invokeSuper(methodId, declare2, local5, localArr2, newLocal16);
                    declare2.returnValue(newLocal16);
                }
            } else {
                throwAbstractMethodError(declare2, method2, declare2.newLocal(TypeId.STRING), declare2.newLocal(typeId4));
            }
            methodArr2 = methodArr;
            method = methodId2;
            typeId5 = typeId;
            field = obj;
            field2 = obj2;
            typeId9 = typeId14;
            typeId11 = typeId15;
            typeId10 = typeId17;
            i = i6 + 1;
            dexMaker2 = dexMaker;
            typeId8 = typeId16;
        }
    }

    private static void invokeSuper(MethodId methodId, Code code, Local local, Local[] localArr, Local local2) {
        code.invokeSuper(methodId, local2, local, localArr);
    }

    private static Local<?> boxIfRequired(Code code, Local<?> local, Local<Object> local2) {
        MethodId<?, ?> methodId = PRIMITIVE_TYPE_TO_UNBOX_METHOD.get(local.getType());
        if (methodId == null) {
            return local;
        }
        code.invokeStatic(methodId, local2, local);
        return local2;
    }

    public static Object callSuper(Object obj, Method method, Object... objArr) throws Throwable {
        try {
            return obj.getClass().getMethod(superMethodName(method), method.getParameterTypes()).invoke(obj, objArr);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    private static String superMethodName(Method method) {
        return "super$" + method.getName() + "$" + method.getReturnType().getName().replace('.', '_').replace('[', '_').replace(';', '_');
    }

    private static void check(boolean z, String str) {
        if (!z) {
            throw new IllegalArgumentException(str);
        }
    }

    private static <T, G extends T> void generateConstructorsAndFields(DexMaker dexMaker, TypeId<G> typeId, TypeId<T> typeId2, Class<T> cls) {
        Constructor[] constructorsToOverwrite;
        TypeId<V> typeId3 = TypeId.get(InvocationHandler.class);
        TypeId<V> typeId4 = TypeId.get(Method[].class);
        dexMaker.declare(typeId.getField(typeId3, FIELD_NAME_HANDLER), 2, null);
        dexMaker.declare(typeId.getField(typeId4, FIELD_NAME_METHODS), 10, null);
        for (Constructor constructor : getConstructorsToOverwrite(cls)) {
            if (constructor.getModifiers() != 16) {
                TypeId<?>[] classArrayToTypeArray = classArrayToTypeArray(constructor.getParameterTypes());
                Code declare = dexMaker.declare(typeId.getConstructor(classArrayToTypeArray), 1);
                Local<T> local = declare.getThis(typeId);
                int length = classArrayToTypeArray.length;
                Local<?>[] localArr = new Local[length];
                for (int i = 0; i < length; i++) {
                    localArr[i] = declare.getParameter(i, classArrayToTypeArray[i]);
                }
                declare.invokeDirect(typeId2.getConstructor(classArrayToTypeArray), null, local, localArr);
                declare.returnVoid();
            }
        }
    }

    private static <T> Constructor<T>[] getConstructorsToOverwrite(Class<T> cls) {
        return (Constructor<T>[]) cls.getDeclaredConstructors();
    }

    private TypeId<?>[] getInterfacesAsTypeIds() {
        TypeId<?>[] typeIdArr = new TypeId[this.interfaces.size()];
        int i = 0;
        for (Class<?> cls : this.interfaces) {
            typeIdArr[i] = TypeId.get(cls);
            i++;
        }
        return typeIdArr;
    }

    private Method[] getMethodsToProxyRecursive() {
        int i;
        Set<MethodSetEntry> hashSet = new HashSet<>();
        Set<MethodSetEntry> hashSet2 = new HashSet<>();
        for (Class<T> cls = this.baseClass; cls != null; cls = cls.getSuperclass()) {
            getMethodsToProxy(hashSet, hashSet2, cls);
        }
        Class<T> cls2 = this.baseClass;
        while (true) {
            i = 0;
            if (cls2 == null) {
                break;
            }
            Class<?>[] interfaces = cls2.getInterfaces();
            int length = interfaces.length;
            while (i < length) {
                getMethodsToProxy(hashSet, hashSet2, interfaces[i]);
                i++;
            }
            cls2 = cls2.getSuperclass();
        }
        for (Class<?> cls3 : this.interfaces) {
            getMethodsToProxy(hashSet, hashSet2, cls3);
        }
        Method[] methodArr = new Method[hashSet.size()];
        for (MethodSetEntry methodSetEntry : hashSet) {
            methodArr[i] = methodSetEntry.originalMethod;
            i++;
        }
        return methodArr;
    }

    private void getMethodsToProxy(Set<MethodSetEntry> set, Set<MethodSetEntry> set2, Class<?> cls) {
        Method[] declaredMethods;
        for (Method method : cls.getDeclaredMethods()) {
            if ((method.getModifiers() & 16) != 0) {
                MethodSetEntry methodSetEntry = new MethodSetEntry(method);
                set2.add(methodSetEntry);
                set.remove(methodSetEntry);
            } else if ((method.getModifiers() & 8) == 0 && ((Modifier.isPublic(method.getModifiers()) || Modifier.isProtected(method.getModifiers()) || (this.sharedClassLoader && !Modifier.isPrivate(method.getModifiers()))) && (!method.getName().equals("finalize") || method.getParameterTypes().length != 0))) {
                MethodSetEntry methodSetEntry2 = new MethodSetEntry(method);
                if (!set2.contains(methodSetEntry2)) {
                    set.add(methodSetEntry2);
                }
            }
        }
        if (cls.isInterface()) {
            for (Class<?> cls2 : cls.getInterfaces()) {
                getMethodsToProxy(set, set2, cls2);
            }
        }
    }

    private static <T> String getMethodNameForProxyOf(Class<T> cls, List<Class<?>> list) {
        return cls.getName().replace(".", "/") + "_" + Integer.toHexString(list.hashCode()) + "_Proxy";
    }

    private static TypeId<?>[] classArrayToTypeArray(Class<?>[] clsArr) {
        TypeId<?>[] typeIdArr = new TypeId[clsArr.length];
        for (int i = 0; i < clsArr.length; i++) {
            typeIdArr[i] = TypeId.get(clsArr[i]);
        }
        return typeIdArr;
    }

    private static void generateCodeForReturnStatement(Code code, Class cls, Local local, Local local2, Local local3) {
        if (PRIMITIVE_TO_UNBOX_METHOD.containsKey(cls)) {
            code.cast(local3, local);
            code.invokeVirtual(getUnboxMethodForPrimitive(cls), local2, local3, new Local[0]);
            code.returnValue(local2);
        } else if (Void.TYPE.equals(cls)) {
            code.returnVoid();
        } else {
            code.cast(local2, local);
            code.returnValue(local2);
        }
    }

    private static MethodId<?, ?> getUnboxMethodForPrimitive(Class<?> cls) {
        return PRIMITIVE_TO_UNBOX_METHOD.get(cls);
    }

    /* loaded from: classes.dex */
    public static class MethodSetEntry {
        public final String name;
        public final Method originalMethod;
        public final Class<?>[] paramTypes;
        public final Class<?> returnType;

        public MethodSetEntry(Method method) {
            this.originalMethod = method;
            this.name = method.getName();
            this.paramTypes = method.getParameterTypes();
            this.returnType = method.getReturnType();
        }

        public boolean equals(Object obj) {
            if (obj instanceof MethodSetEntry) {
                MethodSetEntry methodSetEntry = (MethodSetEntry) obj;
                return this.name.equals(methodSetEntry.name) && this.returnType.equals(methodSetEntry.returnType) && Arrays.equals(this.paramTypes, methodSetEntry.paramTypes);
            }
            return false;
        }

        public int hashCode() {
            int hashCode = IInputController.KEYCODE_KNOB_TALKING_BOOK + this.name.hashCode() + 17;
            int hashCode2 = hashCode + (hashCode * 31) + this.returnType.hashCode();
            return hashCode2 + (hashCode2 * 31) + Arrays.hashCode(this.paramTypes);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ProxiedClass<U> {
        final Class<U> clazz;
        final List<Class<?>> interfaces;
        final ClassLoader requestedClassloader;
        final boolean sharedClassLoader;

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            ProxiedClass proxiedClass = (ProxiedClass) obj;
            return this.clazz == proxiedClass.clazz && this.interfaces.equals(proxiedClass.interfaces) && this.requestedClassloader == proxiedClass.requestedClassloader && this.sharedClassLoader == proxiedClass.sharedClassLoader;
        }

        public int hashCode() {
            return this.clazz.hashCode() + this.interfaces.hashCode() + this.requestedClassloader.hashCode() + (this.sharedClassLoader ? 1 : 0);
        }

        private ProxiedClass(Class<U> cls, List<Class<?>> list, ClassLoader classLoader, boolean z) {
            this.clazz = cls;
            this.interfaces = new ArrayList(list);
            this.requestedClassloader = classLoader;
            this.sharedClassLoader = z;
        }
    }
}

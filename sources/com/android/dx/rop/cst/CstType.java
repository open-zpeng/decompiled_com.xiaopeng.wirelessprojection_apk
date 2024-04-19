package com.android.dx.rop.cst;

import com.android.dx.rop.type.Type;
import com.android.multidex.ClassPathElement;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
/* loaded from: classes.dex */
public final class CstType extends TypedConstant {
    private CstString descriptor;
    private final Type type;
    private static final ConcurrentMap<Type, CstType> interns = new ConcurrentHashMap(1000, 0.75f);
    public static final CstType OBJECT = new CstType(Type.OBJECT);
    public static final CstType BOOLEAN = new CstType(Type.BOOLEAN_CLASS);
    public static final CstType BYTE = new CstType(Type.BYTE_CLASS);
    public static final CstType CHARACTER = new CstType(Type.CHARACTER_CLASS);
    public static final CstType DOUBLE = new CstType(Type.DOUBLE_CLASS);
    public static final CstType FLOAT = new CstType(Type.FLOAT_CLASS);
    public static final CstType LONG = new CstType(Type.LONG_CLASS);
    public static final CstType INTEGER = new CstType(Type.INTEGER_CLASS);
    public static final CstType SHORT = new CstType(Type.SHORT_CLASS);
    public static final CstType VOID = new CstType(Type.VOID_CLASS);
    public static final CstType BOOLEAN_ARRAY = new CstType(Type.BOOLEAN_ARRAY);
    public static final CstType BYTE_ARRAY = new CstType(Type.BYTE_ARRAY);
    public static final CstType CHAR_ARRAY = new CstType(Type.CHAR_ARRAY);
    public static final CstType DOUBLE_ARRAY = new CstType(Type.DOUBLE_ARRAY);
    public static final CstType FLOAT_ARRAY = new CstType(Type.FLOAT_ARRAY);
    public static final CstType LONG_ARRAY = new CstType(Type.LONG_ARRAY);
    public static final CstType INT_ARRAY = new CstType(Type.INT_ARRAY);
    public static final CstType SHORT_ARRAY = new CstType(Type.SHORT_ARRAY);
    public static final CstType METHOD_HANDLE = new CstType(Type.METHOD_HANDLE);
    public static final CstType VAR_HANDLE = new CstType(Type.VAR_HANDLE);

    @Override // com.android.dx.rop.cst.Constant
    public boolean isCategory2() {
        return false;
    }

    @Override // com.android.dx.rop.cst.Constant
    public String typeName() {
        return "type";
    }

    static {
        initInterns();
    }

    private static void initInterns() {
        internInitial(OBJECT);
        internInitial(BOOLEAN);
        internInitial(BYTE);
        internInitial(CHARACTER);
        internInitial(DOUBLE);
        internInitial(FLOAT);
        internInitial(LONG);
        internInitial(INTEGER);
        internInitial(SHORT);
        internInitial(VOID);
        internInitial(BOOLEAN_ARRAY);
        internInitial(BYTE_ARRAY);
        internInitial(CHAR_ARRAY);
        internInitial(DOUBLE_ARRAY);
        internInitial(FLOAT_ARRAY);
        internInitial(LONG_ARRAY);
        internInitial(INT_ARRAY);
        internInitial(SHORT_ARRAY);
        internInitial(METHOD_HANDLE);
    }

    private static void internInitial(CstType cstType) {
        if (interns.putIfAbsent(cstType.getClassType(), cstType) != null) {
            throw new IllegalStateException("Attempted re-init of " + cstType);
        }
    }

    public static CstType forBoxedPrimitiveType(Type type) {
        switch (type.getBasicType()) {
            case 0:
                return VOID;
            case 1:
                return BOOLEAN;
            case 2:
                return BYTE;
            case 3:
                return CHARACTER;
            case 4:
                return DOUBLE;
            case 5:
                return FLOAT;
            case 6:
                return INTEGER;
            case 7:
                return LONG;
            case 8:
                return SHORT;
            default:
                throw new IllegalArgumentException("not primitive: " + type);
        }
    }

    public static CstType intern(Type type) {
        CstType cstType = new CstType(type);
        CstType putIfAbsent = interns.putIfAbsent(type, cstType);
        return putIfAbsent != null ? putIfAbsent : cstType;
    }

    public CstType(Type type) {
        Objects.requireNonNull(type, "type == null");
        if (type == Type.KNOWN_NULL) {
            throw new UnsupportedOperationException("KNOWN_NULL is not representable");
        }
        this.type = type;
        this.descriptor = null;
    }

    public boolean equals(Object obj) {
        return (obj instanceof CstType) && this.type == ((CstType) obj).type;
    }

    public int hashCode() {
        return this.type.hashCode();
    }

    @Override // com.android.dx.rop.cst.Constant
    protected int compareTo0(Constant constant) {
        return this.type.getDescriptor().compareTo(((CstType) constant).type.getDescriptor());
    }

    public String toString() {
        return "type{" + toHuman() + '}';
    }

    @Override // com.android.dx.rop.type.TypeBearer
    public Type getType() {
        return Type.CLASS;
    }

    @Override // com.android.dx.util.ToHuman
    public String toHuman() {
        return this.type.toHuman();
    }

    public Type getClassType() {
        return this.type;
    }

    public CstString getDescriptor() {
        if (this.descriptor == null) {
            this.descriptor = new CstString(this.type.getDescriptor());
        }
        return this.descriptor;
    }

    public String getPackageName() {
        String string = getDescriptor().getString();
        int lastIndexOf = string.lastIndexOf(47);
        return lastIndexOf == -1 ? "default" : string.substring(string.lastIndexOf(91) + 2, lastIndexOf).replace(ClassPathElement.SEPARATOR_CHAR, '.');
    }

    public static void clearInternTable() {
        interns.clear();
        initInterns();
    }
}

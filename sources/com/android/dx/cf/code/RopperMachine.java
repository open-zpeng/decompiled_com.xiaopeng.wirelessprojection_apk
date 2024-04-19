package com.android.dx.cf.code;

import com.android.dx.cf.iface.Method;
import com.android.dx.cf.iface.MethodList;
import com.android.dx.rop.code.AccessFlags;
import com.android.dx.rop.code.Insn;
import com.android.dx.rop.code.InvokePolymorphicInsn;
import com.android.dx.rop.code.RegisterSpec;
import com.android.dx.rop.code.RegisterSpecList;
import com.android.dx.rop.code.Rop;
import com.android.dx.rop.code.SourcePosition;
import com.android.dx.rop.code.TranslationAdvice;
import com.android.dx.rop.cst.Constant;
import com.android.dx.rop.cst.CstMethodRef;
import com.android.dx.rop.cst.CstNat;
import com.android.dx.rop.cst.CstString;
import com.android.dx.rop.cst.CstType;
import com.android.dx.rop.type.Type;
import com.android.dx.rop.type.TypeList;
import java.util.ArrayList;
import java.util.Objects;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class RopperMachine extends ValueAwareMachine {
    private static final CstType ARRAY_REFLECT_TYPE;
    private static final CstMethodRef MULTIANEWARRAY_METHOD;
    private final TranslationAdvice advice;
    private boolean blockCanThrow;
    private TypeList catches;
    private boolean catchesUsed;
    private int extraBlockCount;
    private boolean hasJsr;
    private final ArrayList<Insn> insns;
    private final int maxLocals;
    private final ConcreteMethod method;
    private final MethodList methods;
    private int primarySuccessorIndex;
    private ReturnAddress returnAddress;
    private Rop returnOp;
    private SourcePosition returnPosition;
    private boolean returns;
    private final Ropper ropper;

    static {
        CstType cstType = new CstType(Type.internClassName("java/lang/reflect/Array"));
        ARRAY_REFLECT_TYPE = cstType;
        MULTIANEWARRAY_METHOD = new CstMethodRef(cstType, new CstNat(new CstString("newInstance"), new CstString("(Ljava/lang/Class;[I)Ljava/lang/Object;")));
    }

    public RopperMachine(Ropper ropper, ConcreteMethod concreteMethod, TranslationAdvice translationAdvice, MethodList methodList) {
        super(concreteMethod.getEffectiveDescriptor());
        Objects.requireNonNull(methodList, "methods == null");
        Objects.requireNonNull(ropper, "ropper == null");
        Objects.requireNonNull(translationAdvice, "advice == null");
        this.ropper = ropper;
        this.method = concreteMethod;
        this.methods = methodList;
        this.advice = translationAdvice;
        this.maxLocals = concreteMethod.getMaxLocals();
        this.insns = new ArrayList<>(25);
        this.catches = null;
        this.catchesUsed = false;
        this.returns = false;
        this.primarySuccessorIndex = -1;
        this.extraBlockCount = 0;
        this.blockCanThrow = false;
        this.returnOp = null;
        this.returnPosition = null;
    }

    public ArrayList<Insn> getInsns() {
        return this.insns;
    }

    public Rop getReturnOp() {
        return this.returnOp;
    }

    public SourcePosition getReturnPosition() {
        return this.returnPosition;
    }

    public void startBlock(TypeList typeList) {
        this.catches = typeList;
        this.insns.clear();
        this.catchesUsed = false;
        this.returns = false;
        this.primarySuccessorIndex = 0;
        this.extraBlockCount = 0;
        this.blockCanThrow = false;
        this.hasJsr = false;
        this.returnAddress = null;
    }

    public boolean wereCatchesUsed() {
        return this.catchesUsed;
    }

    public boolean returns() {
        return this.returns;
    }

    public int getPrimarySuccessorIndex() {
        return this.primarySuccessorIndex;
    }

    public int getExtraBlockCount() {
        return this.extraBlockCount;
    }

    public boolean canThrow() {
        return this.blockCanThrow;
    }

    public boolean hasJsr() {
        return this.hasJsr;
    }

    public boolean hasRet() {
        return this.returnAddress != null;
    }

    public ReturnAddress getReturnAddress() {
        return this.returnAddress;
    }

    /* JADX WARN: Code restructure failed: missing block: B:66:0x01f3, code lost:
        if (r2.isConstant() != false) goto L85;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:113:0x0331  */
    /* JADX WARN: Removed duplicated region for block: B:115:0x0338  */
    /* JADX WARN: Removed duplicated region for block: B:130:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:59:0x01ca  */
    /* JADX WARN: Removed duplicated region for block: B:60:0x01d4  */
    /* JADX WARN: Removed duplicated region for block: B:79:0x024c  */
    /* JADX WARN: Removed duplicated region for block: B:85:0x027b  */
    @Override // com.android.dx.cf.code.ValueAwareMachine, com.android.dx.cf.code.Machine
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void run(com.android.dx.cf.code.Frame r21, int r22, int r23) {
        /*
            Method dump skipped, instructions count: 951
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.dx.cf.code.RopperMachine.run(com.android.dx.cf.code.Frame, int, int):void");
    }

    private RegisterSpecList getSources(int i, int i2) {
        RegisterSpecList registerSpecList;
        int argCount = argCount();
        if (argCount == 0) {
            return RegisterSpecList.EMPTY;
        }
        int localIndex = getLocalIndex();
        if (localIndex >= 0) {
            registerSpecList = new RegisterSpecList(1);
            registerSpecList.set(0, RegisterSpec.make(localIndex, arg(0)));
        } else {
            RegisterSpecList registerSpecList2 = new RegisterSpecList(argCount);
            for (int i3 = 0; i3 < argCount; i3++) {
                RegisterSpec make = RegisterSpec.make(i2, arg(i3));
                registerSpecList2.set(i3, make);
                i2 += make.getCategory();
            }
            if (i != 79) {
                if (i == 181) {
                    if (argCount != 2) {
                        throw new RuntimeException("shouldn't happen");
                    }
                    RegisterSpec registerSpec = registerSpecList2.get(0);
                    registerSpecList2.set(0, registerSpecList2.get(1));
                    registerSpecList2.set(1, registerSpec);
                }
            } else if (argCount != 3) {
                throw new RuntimeException("shouldn't happen");
            } else {
                RegisterSpec registerSpec2 = registerSpecList2.get(0);
                RegisterSpec registerSpec3 = registerSpecList2.get(1);
                registerSpecList2.set(0, registerSpecList2.get(2));
                registerSpecList2.set(1, registerSpec2);
                registerSpecList2.set(2, registerSpec3);
            }
            registerSpecList = registerSpecList2;
        }
        registerSpecList.setImmutable();
        return registerSpecList;
    }

    private void updateReturnOp(Rop rop, SourcePosition sourcePosition) {
        Objects.requireNonNull(rop, "op == null");
        Objects.requireNonNull(sourcePosition, "pos == null");
        Rop rop2 = this.returnOp;
        if (rop2 == null) {
            this.returnOp = rop;
            this.returnPosition = sourcePosition;
        } else if (rop2 != rop) {
            throw new SimException("return op mismatch: " + rop + ", " + this.returnOp);
        } else {
            if (sourcePosition.getLine() > this.returnPosition.getLine()) {
                this.returnPosition = sourcePosition;
            }
        }
    }

    private int jopToRopOpcode(int i, Constant constant) {
        if (i != 0) {
            if (i != 20) {
                if (i != 21) {
                    if (i != 171) {
                        if (i != 172) {
                            if (i != 198) {
                                if (i != 199) {
                                    switch (i) {
                                        case 0:
                                            return 1;
                                        case 18:
                                            return 5;
                                        case 46:
                                            return 38;
                                        case 54:
                                            return 2;
                                        case 79:
                                            return 39;
                                        case 96:
                                            return 14;
                                        case 100:
                                            return 15;
                                        case 104:
                                            return 16;
                                        case 108:
                                            return 17;
                                        case 112:
                                            return 18;
                                        case 116:
                                            return 19;
                                        case 120:
                                            return 23;
                                        case 122:
                                            return 24;
                                        case 124:
                                            return 25;
                                        case 126:
                                            return 20;
                                        case 128:
                                            return 21;
                                        case 130:
                                            return 22;
                                        default:
                                            switch (i) {
                                                case 132:
                                                    return 14;
                                                case 133:
                                                case 134:
                                                case 135:
                                                case 136:
                                                case 137:
                                                case 138:
                                                case 139:
                                                case 140:
                                                case 141:
                                                case 142:
                                                case 143:
                                                case 144:
                                                    return 29;
                                                case 145:
                                                    return 30;
                                                case 146:
                                                    return 31;
                                                case 147:
                                                    return 32;
                                                case 148:
                                                case 149:
                                                case 151:
                                                    return 27;
                                                case 150:
                                                case 152:
                                                    return 28;
                                                case 153:
                                                case 159:
                                                case 165:
                                                    return 7;
                                                case 154:
                                                case 160:
                                                case 166:
                                                    return 8;
                                                case 155:
                                                case 161:
                                                    return 9;
                                                case 156:
                                                case 162:
                                                    return 10;
                                                case 157:
                                                case 163:
                                                    return 12;
                                                case 158:
                                                case 164:
                                                    return 11;
                                                case 167:
                                                    return 6;
                                                default:
                                                    switch (i) {
                                                        case 177:
                                                            return 33;
                                                        case 178:
                                                            return 46;
                                                        case 179:
                                                            return 48;
                                                        case 180:
                                                            return 45;
                                                        case 181:
                                                            return 47;
                                                        case 182:
                                                            CstMethodRef cstMethodRef = (CstMethodRef) constant;
                                                            if (cstMethodRef.getDefiningClass().equals(this.method.getDefiningClass())) {
                                                                for (int i2 = 0; i2 < this.methods.size(); i2++) {
                                                                    Method method = this.methods.get(i2);
                                                                    if (AccessFlags.isPrivate(method.getAccessFlags()) && cstMethodRef.getNat().equals(method.getNat())) {
                                                                        return 52;
                                                                    }
                                                                }
                                                            }
                                                            return cstMethodRef.isSignaturePolymorphic() ? 58 : 50;
                                                        case 183:
                                                            CstMethodRef cstMethodRef2 = (CstMethodRef) constant;
                                                            return (cstMethodRef2.isInstanceInit() || cstMethodRef2.getDefiningClass().equals(this.method.getDefiningClass())) ? 52 : 51;
                                                        case 184:
                                                            return 49;
                                                        case 185:
                                                            return 53;
                                                        case 186:
                                                            return 59;
                                                        case 187:
                                                            return 40;
                                                        case 188:
                                                        case 189:
                                                            return 41;
                                                        case 190:
                                                            return 34;
                                                        case 191:
                                                            return 35;
                                                        case 192:
                                                            return 43;
                                                        case 193:
                                                            return 44;
                                                        case 194:
                                                            return 36;
                                                        case 195:
                                                            return 37;
                                                        default:
                                                            throw new RuntimeException("shouldn't happen");
                                                    }
                                            }
                                    }
                                }
                                return 8;
                            }
                            return 7;
                        }
                        return 33;
                    }
                    return 13;
                }
                return 2;
            }
            return 5;
        }
        return 1;
    }

    private Insn makeInvokePolymorphicInsn(Rop rop, SourcePosition sourcePosition, RegisterSpecList registerSpecList, TypeList typeList, Constant constant) {
        return new InvokePolymorphicInsn(rop, sourcePosition, registerSpecList, typeList, (CstMethodRef) constant);
    }
}

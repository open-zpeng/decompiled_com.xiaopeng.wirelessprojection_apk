package com.android.dx;

import com.android.dx.rop.code.RegisterSpec;
/* loaded from: classes.dex */
public final class Local<T> {
    private final Code code;
    private int reg = -1;
    private RegisterSpec spec;
    final TypeId<T> type;

    private Local(Code code, TypeId<T> typeId) {
        this.code = code;
        this.type = typeId;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <T> Local<T> get(Code code, TypeId<T> typeId) {
        return new Local<>(code, typeId);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int initialize(int i) {
        this.reg = i;
        this.spec = RegisterSpec.make(i, this.type.ropType);
        return size();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int size() {
        return this.type.ropType.getCategory();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RegisterSpec spec() {
        if (this.spec == null) {
            this.code.initializeLocals();
            if (this.spec == null) {
                throw new AssertionError();
            }
        }
        return this.spec;
    }

    public TypeId getType() {
        return this.type;
    }

    public String toString() {
        return RegisterSpec.PREFIX + this.reg + "(" + this.type + ")";
    }
}

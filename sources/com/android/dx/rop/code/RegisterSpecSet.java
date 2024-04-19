package com.android.dx.rop.code;

import com.android.dx.util.MutabilityControl;
import java.util.Objects;
/* loaded from: classes.dex */
public final class RegisterSpecSet extends MutabilityControl {
    public static final RegisterSpecSet EMPTY = new RegisterSpecSet(0);
    private int size;
    private final RegisterSpec[] specs;

    public RegisterSpecSet(int i) {
        super(i != 0);
        this.specs = new RegisterSpec[i];
        this.size = 0;
    }

    public boolean equals(Object obj) {
        if (obj instanceof RegisterSpecSet) {
            RegisterSpecSet registerSpecSet = (RegisterSpecSet) obj;
            RegisterSpec[] registerSpecArr = registerSpecSet.specs;
            int length = this.specs.length;
            if (length == registerSpecArr.length && size() == registerSpecSet.size()) {
                for (int i = 0; i < length; i++) {
                    RegisterSpec registerSpec = this.specs[i];
                    RegisterSpec registerSpec2 = registerSpecArr[i];
                    if (registerSpec != registerSpec2 && (registerSpec == null || !registerSpec.equals(registerSpec2))) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
        return false;
    }

    public int hashCode() {
        int length = this.specs.length;
        int i = 0;
        for (int i2 = 0; i2 < length; i2++) {
            RegisterSpec registerSpec = this.specs[i2];
            i = (i * 31) + (registerSpec == null ? 0 : registerSpec.hashCode());
        }
        return i;
    }

    public String toString() {
        int length = this.specs.length;
        StringBuilder sb = new StringBuilder(length * 25);
        sb.append('{');
        boolean z = false;
        for (int i = 0; i < length; i++) {
            RegisterSpec registerSpec = this.specs[i];
            if (registerSpec != null) {
                if (z) {
                    sb.append(", ");
                } else {
                    z = true;
                }
                sb.append(registerSpec);
            }
        }
        sb.append('}');
        return sb.toString();
    }

    public int getMaxSize() {
        return this.specs.length;
    }

    public int size() {
        int i = this.size;
        if (i < 0) {
            int length = this.specs.length;
            int i2 = 0;
            for (int i3 = 0; i3 < length; i3++) {
                if (this.specs[i3] != null) {
                    i2++;
                }
            }
            this.size = i2;
            return i2;
        }
        return i;
    }

    public RegisterSpec get(int i) {
        try {
            return this.specs[i];
        } catch (ArrayIndexOutOfBoundsException unused) {
            throw new IllegalArgumentException("bogus reg");
        }
    }

    public RegisterSpec get(RegisterSpec registerSpec) {
        return get(registerSpec.getReg());
    }

    public RegisterSpec findMatchingLocal(RegisterSpec registerSpec) {
        int length = this.specs.length;
        for (int i = 0; i < length; i++) {
            RegisterSpec registerSpec2 = this.specs[i];
            if (registerSpec2 != null && registerSpec.matchesVariable(registerSpec2)) {
                return registerSpec2;
            }
        }
        return null;
    }

    public RegisterSpec localItemToSpec(LocalItem localItem) {
        int length = this.specs.length;
        for (int i = 0; i < length; i++) {
            RegisterSpec registerSpec = this.specs[i];
            if (registerSpec != null && localItem.equals(registerSpec.getLocalItem())) {
                return registerSpec;
            }
        }
        return null;
    }

    public void remove(RegisterSpec registerSpec) {
        try {
            this.specs[registerSpec.getReg()] = null;
            this.size = -1;
        } catch (ArrayIndexOutOfBoundsException unused) {
            throw new IllegalArgumentException("bogus reg");
        }
    }

    public void put(RegisterSpec registerSpec) {
        int i;
        RegisterSpec registerSpec2;
        throwIfImmutable();
        Objects.requireNonNull(registerSpec, "spec == null");
        this.size = -1;
        try {
            int reg = registerSpec.getReg();
            RegisterSpec[] registerSpecArr = this.specs;
            registerSpecArr[reg] = registerSpec;
            if (reg > 0 && (registerSpec2 = registerSpecArr[reg - 1]) != null && registerSpec2.getCategory() == 2) {
                this.specs[i] = null;
            }
            if (registerSpec.getCategory() == 2) {
                this.specs[reg + 1] = null;
            }
        } catch (ArrayIndexOutOfBoundsException unused) {
            throw new IllegalArgumentException("spec.getReg() out of range");
        }
    }

    public void putAll(RegisterSpecSet registerSpecSet) {
        int maxSize = registerSpecSet.getMaxSize();
        for (int i = 0; i < maxSize; i++) {
            RegisterSpec registerSpec = registerSpecSet.get(i);
            if (registerSpec != null) {
                put(registerSpec);
            }
        }
    }

    public void intersect(RegisterSpecSet registerSpecSet, boolean z) {
        RegisterSpec intersect;
        throwIfImmutable();
        RegisterSpec[] registerSpecArr = registerSpecSet.specs;
        int length = this.specs.length;
        int min = Math.min(length, registerSpecArr.length);
        this.size = -1;
        for (int i = 0; i < min; i++) {
            RegisterSpec registerSpec = this.specs[i];
            if (registerSpec != null && (intersect = registerSpec.intersect(registerSpecArr[i], z)) != registerSpec) {
                this.specs[i] = intersect;
            }
        }
        while (min < length) {
            this.specs[min] = null;
            min++;
        }
    }

    public RegisterSpecSet withOffset(int i) {
        int length = this.specs.length;
        RegisterSpecSet registerSpecSet = new RegisterSpecSet(length + i);
        for (int i2 = 0; i2 < length; i2++) {
            RegisterSpec registerSpec = this.specs[i2];
            if (registerSpec != null) {
                registerSpecSet.put(registerSpec.withOffset(i));
            }
        }
        registerSpecSet.size = this.size;
        if (isImmutable()) {
            registerSpecSet.setImmutable();
        }
        return registerSpecSet;
    }

    public RegisterSpecSet mutableCopy() {
        int length = this.specs.length;
        RegisterSpecSet registerSpecSet = new RegisterSpecSet(length);
        for (int i = 0; i < length; i++) {
            RegisterSpec registerSpec = this.specs[i];
            if (registerSpec != null) {
                registerSpecSet.put(registerSpec);
            }
        }
        registerSpecSet.size = this.size;
        return registerSpecSet;
    }
}

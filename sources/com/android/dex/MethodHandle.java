package com.android.dex;

import com.android.dex.Dex;
import com.android.dex.util.Unsigned;
import com.xiaopeng.wirelessprojection.dmr.RendererActivity;
/* loaded from: classes.dex */
public class MethodHandle implements Comparable<MethodHandle> {
    private final Dex dex;
    private final int fieldOrMethodId;
    private final MethodHandleType methodHandleType;
    private final int unused1;
    private final int unused2;

    /* loaded from: classes.dex */
    public enum MethodHandleType {
        METHOD_HANDLE_TYPE_STATIC_PUT(0),
        METHOD_HANDLE_TYPE_STATIC_GET(1),
        METHOD_HANDLE_TYPE_INSTANCE_PUT(2),
        METHOD_HANDLE_TYPE_INSTANCE_GET(3),
        METHOD_HANDLE_TYPE_INVOKE_STATIC(4),
        METHOD_HANDLE_TYPE_INVOKE_INSTANCE(5),
        METHOD_HANDLE_TYPE_INVOKE_DIRECT(6),
        METHOD_HANDLE_TYPE_INVOKE_CONSTRUCTOR(7),
        METHOD_HANDLE_TYPE_INVOKE_INTERFACE(8);
        
        private final int value;

        MethodHandleType(int i) {
            this.value = i;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static MethodHandleType fromValue(int i) {
            MethodHandleType[] values;
            for (MethodHandleType methodHandleType : values()) {
                if (methodHandleType.value == i) {
                    return methodHandleType;
                }
            }
            throw new IllegalArgumentException(String.valueOf(i));
        }

        public boolean isField() {
            int i = AnonymousClass1.$SwitchMap$com$android$dex$MethodHandle$MethodHandleType[ordinal()];
            return i == 1 || i == 2 || i == 3 || i == 4;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.dex.MethodHandle$1  reason: invalid class name */
    /* loaded from: classes.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$android$dex$MethodHandle$MethodHandleType;

        static {
            int[] iArr = new int[MethodHandleType.values().length];
            $SwitchMap$com$android$dex$MethodHandle$MethodHandleType = iArr;
            try {
                iArr[MethodHandleType.METHOD_HANDLE_TYPE_STATIC_PUT.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$android$dex$MethodHandle$MethodHandleType[MethodHandleType.METHOD_HANDLE_TYPE_STATIC_GET.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$android$dex$MethodHandle$MethodHandleType[MethodHandleType.METHOD_HANDLE_TYPE_INSTANCE_PUT.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$android$dex$MethodHandle$MethodHandleType[MethodHandleType.METHOD_HANDLE_TYPE_INSTANCE_GET.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
        }
    }

    public MethodHandle(Dex dex, MethodHandleType methodHandleType, int i, int i2, int i3) {
        this.dex = dex;
        this.methodHandleType = methodHandleType;
        this.unused1 = i;
        this.fieldOrMethodId = i2;
        this.unused2 = i3;
    }

    @Override // java.lang.Comparable
    public int compareTo(MethodHandle methodHandle) {
        MethodHandleType methodHandleType = this.methodHandleType;
        MethodHandleType methodHandleType2 = methodHandle.methodHandleType;
        if (methodHandleType != methodHandleType2) {
            return methodHandleType.compareTo(methodHandleType2);
        }
        return Unsigned.compare(this.fieldOrMethodId, methodHandle.fieldOrMethodId);
    }

    public MethodHandleType getMethodHandleType() {
        return this.methodHandleType;
    }

    public int getUnused1() {
        return this.unused1;
    }

    public int getFieldOrMethodId() {
        return this.fieldOrMethodId;
    }

    public int getUnused2() {
        return this.unused2;
    }

    public void writeTo(Dex.Section section) {
        section.writeUnsignedShort(this.methodHandleType.value);
        section.writeUnsignedShort(this.unused1);
        section.writeUnsignedShort(this.fieldOrMethodId);
        section.writeUnsignedShort(this.unused2);
    }

    public String toString() {
        MethodId methodId;
        if (this.dex == null) {
            return this.methodHandleType + RendererActivity.DEFAULT_TITLE + this.fieldOrMethodId;
        }
        StringBuilder append = new StringBuilder().append(this.methodHandleType).append(RendererActivity.DEFAULT_TITLE);
        if (this.methodHandleType.isField()) {
            methodId = this.dex.fieldIds().get(this.fieldOrMethodId);
        } else {
            methodId = this.dex.methodIds().get(this.fieldOrMethodId);
        }
        return append.append(methodId).toString();
    }
}

package com.android.dx.rop.cst;

import com.android.dx.rop.type.Prototype;
import com.android.dx.rop.type.Type;
/* loaded from: classes.dex */
public abstract class CstBaseMethodRef extends CstMemberRef {
    private Prototype instancePrototype;
    private final Prototype prototype;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CstBaseMethodRef(CstType cstType, CstNat cstNat) {
        super(cstType, cstNat);
        String string = getNat().getDescriptor().getString();
        if (isSignaturePolymorphic()) {
            this.prototype = Prototype.fromDescriptor(string);
        } else {
            this.prototype = Prototype.intern(string);
        }
        this.instancePrototype = null;
    }

    public final Prototype getPrototype() {
        return this.prototype;
    }

    public final Prototype getPrototype(boolean z) {
        if (z) {
            return this.prototype;
        }
        if (this.instancePrototype == null) {
            this.instancePrototype = this.prototype.withFirstParameter(getDefiningClass().getClassType());
        }
        return this.instancePrototype;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.dx.rop.cst.CstMemberRef, com.android.dx.rop.cst.Constant
    public final int compareTo0(Constant constant) {
        int compareTo0 = super.compareTo0(constant);
        return compareTo0 != 0 ? compareTo0 : this.prototype.compareTo(((CstBaseMethodRef) constant).prototype);
    }

    @Override // com.android.dx.rop.type.TypeBearer
    public final Type getType() {
        return this.prototype.getReturnType();
    }

    public final int getParameterWordCount(boolean z) {
        return getPrototype(z).getParameterTypes().getWordCount();
    }

    public final boolean isInstanceInit() {
        return getNat().isInstanceInit();
    }

    public final boolean isClassInit() {
        return getNat().isClassInit();
    }

    public final boolean isSignaturePolymorphic() {
        CstType definingClass = getDefiningClass();
        if (definingClass.equals(CstType.METHOD_HANDLE)) {
            String string = getNat().getName().getString();
            string.hashCode();
            if (string.equals("invoke") || string.equals("invokeExact")) {
                return true;
            }
        } else if (definingClass.equals(CstType.VAR_HANDLE)) {
            String string2 = getNat().getName().getString();
            string2.hashCode();
            char c = 65535;
            switch (string2.hashCode()) {
                case -1946504908:
                    if (string2.equals("getAndBitwiseOrRelease")) {
                        c = 0;
                        break;
                    }
                    break;
                case -1686727776:
                    if (string2.equals("getAndBitwiseAndRelease")) {
                        c = 1;
                        break;
                    }
                    break;
                case -1671098288:
                    if (string2.equals("compareAndSet")) {
                        c = 2;
                        break;
                    }
                    break;
                case -1292078254:
                    if (string2.equals("compareAndExchangeRelease")) {
                        c = 3;
                        break;
                    }
                    break;
                case -1117944904:
                    if (string2.equals("weakCompareAndSet")) {
                        c = 4;
                        break;
                    }
                    break;
                case -1103072857:
                    if (string2.equals("getAndAddRelease")) {
                        c = 5;
                        break;
                    }
                    break;
                case -1032914329:
                    if (string2.equals("getAndBitwiseAnd")) {
                        c = 6;
                        break;
                    }
                    break;
                case -1032892181:
                    if (string2.equals("getAndBitwiseXor")) {
                        c = 7;
                        break;
                    }
                    break;
                case -794517348:
                    if (string2.equals("getAndBitwiseXorRelease")) {
                        c = '\b';
                        break;
                    }
                    break;
                case -567150350:
                    if (string2.equals("weakCompareAndSetPlain")) {
                        c = '\t';
                        break;
                    }
                    break;
                case -240822786:
                    if (string2.equals("weakCompareAndSetAcquire")) {
                        c = '\n';
                        break;
                    }
                    break;
                case -230706875:
                    if (string2.equals("setRelease")) {
                        c = 11;
                        break;
                    }
                    break;
                case -127361888:
                    if (string2.equals("getAcquire")) {
                        c = '\f';
                        break;
                    }
                    break;
                case -37641530:
                    if (string2.equals("getAndSetRelease")) {
                        c = '\r';
                        break;
                    }
                    break;
                case 102230:
                    if (string2.equals("get")) {
                        c = 14;
                        break;
                    }
                    break;
                case 113762:
                    if (string2.equals("set")) {
                        c = 15;
                        break;
                    }
                    break;
                case 93645315:
                    if (string2.equals("getAndBitwiseOrAcquire")) {
                        c = 16;
                        break;
                    }
                    break;
                case 101293086:
                    if (string2.equals("setVolatile")) {
                        c = 17;
                        break;
                    }
                    break;
                case 189872914:
                    if (string2.equals("getVolatile")) {
                        c = 18;
                        break;
                    }
                    break;
                case 282707520:
                    if (string2.equals("getAndAdd")) {
                        c = 19;
                        break;
                    }
                    break;
                case 282724865:
                    if (string2.equals("getAndSet")) {
                        c = 20;
                        break;
                    }
                    break;
                case 353422447:
                    if (string2.equals("getAndBitwiseAndAcquire")) {
                        c = 21;
                        break;
                    }
                    break;
                case 470702883:
                    if (string2.equals("setOpaque")) {
                        c = 22;
                        break;
                    }
                    break;
                case 685319959:
                    if (string2.equals("getOpaque")) {
                        c = 23;
                        break;
                    }
                    break;
                case 748071969:
                    if (string2.equals("compareAndExchangeAcquire")) {
                        c = 24;
                        break;
                    }
                    break;
                case 937077366:
                    if (string2.equals("getAndAddAcquire")) {
                        c = 25;
                        break;
                    }
                    break;
                case 1245632875:
                    if (string2.equals("getAndBitwiseXorAcquire")) {
                        c = 26;
                        break;
                    }
                    break;
                case 1352153939:
                    if (string2.equals("getAndBitwiseOr")) {
                        c = 27;
                        break;
                    }
                    break;
                case 1483964149:
                    if (string2.equals("compareAndExchange")) {
                        c = 28;
                        break;
                    }
                    break;
                case 2002508693:
                    if (string2.equals("getAndSetAcquire")) {
                        c = 29;
                        break;
                    }
                    break;
                case 2013994287:
                    if (string2.equals("weakCompareAndSetRelease")) {
                        c = 30;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case '\b':
                case '\t':
                case '\n':
                case 11:
                case '\f':
                case '\r':
                case 14:
                case 15:
                case 16:
                case 17:
                case 18:
                case 19:
                case 20:
                case 21:
                case 22:
                case 23:
                case 24:
                case 25:
                case 26:
                case 27:
                case 28:
                case 29:
                case 30:
                    return true;
            }
        }
        return false;
    }
}

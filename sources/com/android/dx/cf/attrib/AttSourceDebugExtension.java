package com.android.dx.cf.attrib;

import com.android.dx.rop.cst.CstString;
import java.util.Objects;
/* loaded from: classes.dex */
public final class AttSourceDebugExtension extends BaseAttribute {
    public static final String ATTRIBUTE_NAME = "SourceDebugExtension";
    private final CstString smapString;

    public AttSourceDebugExtension(CstString cstString) {
        super(ATTRIBUTE_NAME);
        Objects.requireNonNull(cstString, "smapString == null");
        this.smapString = cstString;
    }

    @Override // com.android.dx.cf.iface.Attribute
    public int byteLength() {
        return this.smapString.getUtf8Size() + 6;
    }

    public CstString getSmapString() {
        return this.smapString;
    }
}

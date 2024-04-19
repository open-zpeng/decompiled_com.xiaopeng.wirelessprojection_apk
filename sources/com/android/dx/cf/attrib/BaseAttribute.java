package com.android.dx.cf.attrib;

import com.android.dx.cf.iface.Attribute;
import java.util.Objects;
/* loaded from: classes.dex */
public abstract class BaseAttribute implements Attribute {
    private final String name;

    public BaseAttribute(String str) {
        Objects.requireNonNull(str, "name == null");
        this.name = str;
    }

    @Override // com.android.dx.cf.iface.Attribute
    public String getName() {
        return this.name;
    }
}

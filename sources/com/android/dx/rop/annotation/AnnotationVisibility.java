package com.android.dx.rop.annotation;

import com.android.dx.util.ToHuman;
/* loaded from: classes.dex */
public enum AnnotationVisibility implements ToHuman {
    RUNTIME("runtime"),
    BUILD("build"),
    SYSTEM("system"),
    EMBEDDED("embedded");
    
    private final String human;

    AnnotationVisibility(String str) {
        this.human = str;
    }

    @Override // com.android.dx.util.ToHuman
    public String toHuman() {
        return this.human;
    }
}

package com.android.dx.rop.type;
/* loaded from: classes.dex */
public interface TypeList {
    Type getType(int i);

    int getWordCount();

    boolean isMutable();

    int size();

    TypeList withAddedType(Type type);
}

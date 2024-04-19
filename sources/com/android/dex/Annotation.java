package com.android.dex;

import com.android.dex.Dex;
import com.xiaopeng.wirelessprojection.dmr.RendererActivity;
/* loaded from: classes.dex */
public final class Annotation implements Comparable<Annotation> {
    private final Dex dex;
    private final EncodedValue encodedAnnotation;
    private final byte visibility;

    public Annotation(Dex dex, byte b, EncodedValue encodedValue) {
        this.dex = dex;
        this.visibility = b;
        this.encodedAnnotation = encodedValue;
    }

    public byte getVisibility() {
        return this.visibility;
    }

    public EncodedValueReader getReader() {
        return new EncodedValueReader(this.encodedAnnotation, 29);
    }

    public int getTypeIndex() {
        EncodedValueReader reader = getReader();
        reader.readAnnotation();
        return reader.getAnnotationType();
    }

    public void writeTo(Dex.Section section) {
        section.writeByte(this.visibility);
        this.encodedAnnotation.writeTo(section);
    }

    @Override // java.lang.Comparable
    public int compareTo(Annotation annotation) {
        return this.encodedAnnotation.compareTo(annotation.encodedAnnotation);
    }

    public String toString() {
        if (this.dex == null) {
            return ((int) this.visibility) + RendererActivity.DEFAULT_TITLE + getTypeIndex();
        }
        return ((int) this.visibility) + RendererActivity.DEFAULT_TITLE + this.dex.typeNames().get(getTypeIndex());
    }
}

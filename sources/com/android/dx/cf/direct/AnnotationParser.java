package com.android.dx.cf.direct;

import com.android.dx.cf.iface.ParseException;
import com.android.dx.cf.iface.ParseObserver;
import com.android.dx.rop.annotation.Annotation;
import com.android.dx.rop.annotation.AnnotationVisibility;
import com.android.dx.rop.annotation.Annotations;
import com.android.dx.rop.annotation.AnnotationsList;
import com.android.dx.rop.annotation.NameValuePair;
import com.android.dx.rop.cst.Constant;
import com.android.dx.rop.cst.ConstantPool;
import com.android.dx.rop.cst.CstAnnotation;
import com.android.dx.rop.cst.CstArray;
import com.android.dx.rop.cst.CstBoolean;
import com.android.dx.rop.cst.CstByte;
import com.android.dx.rop.cst.CstChar;
import com.android.dx.rop.cst.CstDouble;
import com.android.dx.rop.cst.CstEnumRef;
import com.android.dx.rop.cst.CstFloat;
import com.android.dx.rop.cst.CstInteger;
import com.android.dx.rop.cst.CstLong;
import com.android.dx.rop.cst.CstNat;
import com.android.dx.rop.cst.CstShort;
import com.android.dx.rop.cst.CstString;
import com.android.dx.rop.cst.CstType;
import com.android.dx.rop.type.Type;
import com.android.dx.util.ByteArray;
import com.android.dx.util.Hex;
import java.io.IOException;
import java.util.Objects;
/* loaded from: classes.dex */
public final class AnnotationParser {
    private final ByteArray bytes;
    private final DirectClassFile cf;
    private final ByteArray.MyDataInputStream input;
    private final ParseObserver observer;
    private int parseCursor;
    private final ConstantPool pool;

    public AnnotationParser(DirectClassFile directClassFile, int i, int i2, ParseObserver parseObserver) {
        Objects.requireNonNull(directClassFile, "cf == null");
        this.cf = directClassFile;
        this.pool = directClassFile.getConstantPool();
        this.observer = parseObserver;
        ByteArray slice = directClassFile.getBytes().slice(i, i2 + i);
        this.bytes = slice;
        this.input = slice.makeDataInputStream();
        this.parseCursor = 0;
    }

    public Constant parseValueAttribute() {
        try {
            Constant parseValue = parseValue();
            if (this.input.available() == 0) {
                return parseValue;
            }
            throw new ParseException("extra data in attribute");
        } catch (IOException e) {
            throw new RuntimeException("shouldn't happen", e);
        }
    }

    public AnnotationsList parseParameterAttribute(AnnotationVisibility annotationVisibility) {
        try {
            AnnotationsList parseAnnotationsList = parseAnnotationsList(annotationVisibility);
            if (this.input.available() == 0) {
                return parseAnnotationsList;
            }
            throw new ParseException("extra data in attribute");
        } catch (IOException e) {
            throw new RuntimeException("shouldn't happen", e);
        }
    }

    public Annotations parseAnnotationAttribute(AnnotationVisibility annotationVisibility) {
        try {
            Annotations parseAnnotations = parseAnnotations(annotationVisibility);
            if (this.input.available() == 0) {
                return parseAnnotations;
            }
            throw new ParseException("extra data in attribute");
        } catch (IOException e) {
            throw new RuntimeException("shouldn't happen", e);
        }
    }

    private AnnotationsList parseAnnotationsList(AnnotationVisibility annotationVisibility) throws IOException {
        int readUnsignedByte = this.input.readUnsignedByte();
        if (this.observer != null) {
            parsed(1, "num_parameters: " + Hex.u1(readUnsignedByte));
        }
        AnnotationsList annotationsList = new AnnotationsList(readUnsignedByte);
        for (int i = 0; i < readUnsignedByte; i++) {
            if (this.observer != null) {
                parsed(0, "parameter_annotations[" + i + "]:");
                changeIndent(1);
            }
            annotationsList.set(i, parseAnnotations(annotationVisibility));
            ParseObserver parseObserver = this.observer;
            if (parseObserver != null) {
                parseObserver.changeIndent(-1);
            }
        }
        annotationsList.setImmutable();
        return annotationsList;
    }

    private Annotations parseAnnotations(AnnotationVisibility annotationVisibility) throws IOException {
        int readUnsignedShort = this.input.readUnsignedShort();
        if (this.observer != null) {
            parsed(2, "num_annotations: " + Hex.u2(readUnsignedShort));
        }
        Annotations annotations = new Annotations();
        for (int i = 0; i < readUnsignedShort; i++) {
            if (this.observer != null) {
                parsed(0, "annotations[" + i + "]:");
                changeIndent(1);
            }
            annotations.add(parseAnnotation(annotationVisibility));
            ParseObserver parseObserver = this.observer;
            if (parseObserver != null) {
                parseObserver.changeIndent(-1);
            }
        }
        annotations.setImmutable();
        return annotations;
    }

    private Annotation parseAnnotation(AnnotationVisibility annotationVisibility) throws IOException {
        requireLength(4);
        int readUnsignedShort = this.input.readUnsignedShort();
        int readUnsignedShort2 = this.input.readUnsignedShort();
        CstType cstType = new CstType(Type.intern(((CstString) this.pool.get(readUnsignedShort)).getString()));
        if (this.observer != null) {
            parsed(2, "type: " + cstType.toHuman());
            parsed(2, "num_elements: " + readUnsignedShort2);
        }
        Annotation annotation = new Annotation(cstType, annotationVisibility);
        for (int i = 0; i < readUnsignedShort2; i++) {
            if (this.observer != null) {
                parsed(0, "elements[" + i + "]:");
                changeIndent(1);
            }
            annotation.add(parseElement());
            if (this.observer != null) {
                changeIndent(-1);
            }
        }
        annotation.setImmutable();
        return annotation;
    }

    private NameValuePair parseElement() throws IOException {
        requireLength(5);
        CstString cstString = (CstString) this.pool.get(this.input.readUnsignedShort());
        if (this.observer != null) {
            parsed(2, "element_name: " + cstString.toHuman());
            parsed(0, "value: ");
            changeIndent(1);
        }
        Constant parseValue = parseValue();
        if (this.observer != null) {
            changeIndent(-1);
        }
        return new NameValuePair(cstString, parseValue);
    }

    private Constant parseValue() throws IOException {
        int readUnsignedByte = this.input.readUnsignedByte();
        if (this.observer != null) {
            parsed(1, "tag: " + new CstString(Character.toString((char) readUnsignedByte)).toQuoted());
        }
        if (readUnsignedByte != 64) {
            if (readUnsignedByte != 70) {
                if (readUnsignedByte != 83) {
                    if (readUnsignedByte == 99) {
                        Type internReturnType = Type.internReturnType(((CstString) this.pool.get(this.input.readUnsignedShort())).getString());
                        if (this.observer != null) {
                            parsed(2, "class_info: " + internReturnType.toHuman());
                        }
                        return new CstType(internReturnType);
                    } else if (readUnsignedByte == 101) {
                        requireLength(4);
                        int readUnsignedShort = this.input.readUnsignedShort();
                        int readUnsignedShort2 = this.input.readUnsignedShort();
                        CstString cstString = (CstString) this.pool.get(readUnsignedShort);
                        CstString cstString2 = (CstString) this.pool.get(readUnsignedShort2);
                        if (this.observer != null) {
                            parsed(2, "type_name: " + cstString.toHuman());
                            parsed(2, "const_name: " + cstString2.toHuman());
                        }
                        return new CstEnumRef(new CstNat(cstString2, cstString));
                    } else if (readUnsignedByte != 115) {
                        if (readUnsignedByte != 73) {
                            if (readUnsignedByte != 74) {
                                if (readUnsignedByte != 90) {
                                    if (readUnsignedByte != 91) {
                                        switch (readUnsignedByte) {
                                            case 66:
                                                return CstByte.make(((CstInteger) parseConstant()).getValue());
                                            case 67:
                                                CstInteger cstInteger = (CstInteger) parseConstant();
                                                cstInteger.getValue();
                                                return CstChar.make(cstInteger.getValue());
                                            case 68:
                                                return (CstDouble) parseConstant();
                                            default:
                                                throw new ParseException("unknown annotation tag: " + Hex.u1(readUnsignedByte));
                                        }
                                    }
                                    requireLength(2);
                                    int readUnsignedShort3 = this.input.readUnsignedShort();
                                    CstArray.List list = new CstArray.List(readUnsignedShort3);
                                    if (this.observer != null) {
                                        parsed(2, "num_values: " + readUnsignedShort3);
                                        changeIndent(1);
                                    }
                                    for (int i = 0; i < readUnsignedShort3; i++) {
                                        if (this.observer != null) {
                                            changeIndent(-1);
                                            parsed(0, "element_value[" + i + "]:");
                                            changeIndent(1);
                                        }
                                        list.set(i, parseValue());
                                    }
                                    if (this.observer != null) {
                                        changeIndent(-1);
                                    }
                                    list.setImmutable();
                                    return new CstArray(list);
                                }
                                return CstBoolean.make(((CstInteger) parseConstant()).getValue());
                            }
                            return (CstLong) parseConstant();
                        }
                        return (CstInteger) parseConstant();
                    } else {
                        return parseConstant();
                    }
                }
                return CstShort.make(((CstInteger) parseConstant()).getValue());
            }
            return (CstFloat) parseConstant();
        }
        return new CstAnnotation(parseAnnotation(AnnotationVisibility.EMBEDDED));
    }

    private Constant parseConstant() throws IOException {
        String human;
        Constant constant = this.pool.get(this.input.readUnsignedShort());
        if (this.observer != null) {
            if (constant instanceof CstString) {
                human = ((CstString) constant).toQuoted();
            } else {
                human = constant.toHuman();
            }
            parsed(2, "constant_value: " + human);
        }
        return constant;
    }

    private void requireLength(int i) throws IOException {
        if (this.input.available() < i) {
            throw new ParseException("truncated annotation attribute");
        }
    }

    private void parsed(int i, String str) {
        this.observer.parsed(this.bytes, this.parseCursor, i, str);
        this.parseCursor += i;
    }

    private void changeIndent(int i) {
        this.observer.changeIndent(i);
    }
}

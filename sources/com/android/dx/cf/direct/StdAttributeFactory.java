package com.android.dx.cf.direct;

import com.android.dx.cf.attrib.AttAnnotationDefault;
import com.android.dx.cf.attrib.AttBootstrapMethods;
import com.android.dx.cf.attrib.AttCode;
import com.android.dx.cf.attrib.AttConstantValue;
import com.android.dx.cf.attrib.AttDeprecated;
import com.android.dx.cf.attrib.AttEnclosingMethod;
import com.android.dx.cf.attrib.AttExceptions;
import com.android.dx.cf.attrib.AttInnerClasses;
import com.android.dx.cf.attrib.AttLineNumberTable;
import com.android.dx.cf.attrib.AttLocalVariableTable;
import com.android.dx.cf.attrib.AttLocalVariableTypeTable;
import com.android.dx.cf.attrib.AttRuntimeInvisibleAnnotations;
import com.android.dx.cf.attrib.AttRuntimeInvisibleParameterAnnotations;
import com.android.dx.cf.attrib.AttRuntimeVisibleAnnotations;
import com.android.dx.cf.attrib.AttRuntimeVisibleParameterAnnotations;
import com.android.dx.cf.attrib.AttSignature;
import com.android.dx.cf.attrib.AttSourceDebugExtension;
import com.android.dx.cf.attrib.AttSourceFile;
import com.android.dx.cf.attrib.AttSynthetic;
import com.android.dx.cf.attrib.InnerClassList;
import com.android.dx.cf.code.BootstrapMethodArgumentsList;
import com.android.dx.cf.code.BootstrapMethodsList;
import com.android.dx.cf.code.ByteCatchList;
import com.android.dx.cf.code.BytecodeArray;
import com.android.dx.cf.code.LineNumberList;
import com.android.dx.cf.code.LocalVariableList;
import com.android.dx.cf.iface.Attribute;
import com.android.dx.cf.iface.ParseException;
import com.android.dx.cf.iface.ParseObserver;
import com.android.dx.cf.iface.StdAttributeList;
import com.android.dx.rop.annotation.AnnotationVisibility;
import com.android.dx.rop.code.AccessFlags;
import com.android.dx.rop.cst.ConstantPool;
import com.android.dx.rop.cst.CstMethodHandle;
import com.android.dx.rop.cst.CstNat;
import com.android.dx.rop.cst.CstString;
import com.android.dx.rop.cst.CstType;
import com.android.dx.rop.cst.TypedConstant;
import com.android.dx.util.ByteArray;
import com.android.dx.util.Hex;
import com.xiaopeng.wirelessprojection.dmr.RendererActivity;
import java.io.IOException;
/* loaded from: classes.dex */
public class StdAttributeFactory extends AttributeFactory {
    public static final StdAttributeFactory THE_ONE = new StdAttributeFactory();

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.dx.cf.direct.AttributeFactory
    public Attribute parse0(DirectClassFile directClassFile, int i, String str, int i2, int i3, ParseObserver parseObserver) {
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    if (i == 3) {
                        if (str == AttLineNumberTable.ATTRIBUTE_NAME) {
                            return lineNumberTable(directClassFile, i2, i3, parseObserver);
                        }
                        if (str == AttLocalVariableTable.ATTRIBUTE_NAME) {
                            return localVariableTable(directClassFile, i2, i3, parseObserver);
                        }
                        if (str == AttLocalVariableTypeTable.ATTRIBUTE_NAME) {
                            return localVariableTypeTable(directClassFile, i2, i3, parseObserver);
                        }
                    }
                } else if (str == AttAnnotationDefault.ATTRIBUTE_NAME) {
                    return annotationDefault(directClassFile, i2, i3, parseObserver);
                } else {
                    if (str == AttCode.ATTRIBUTE_NAME) {
                        return code(directClassFile, i2, i3, parseObserver);
                    }
                    if (str == AttDeprecated.ATTRIBUTE_NAME) {
                        return deprecated(directClassFile, i2, i3, parseObserver);
                    }
                    if (str == AttExceptions.ATTRIBUTE_NAME) {
                        return exceptions(directClassFile, i2, i3, parseObserver);
                    }
                    if (str == AttRuntimeInvisibleAnnotations.ATTRIBUTE_NAME) {
                        return runtimeInvisibleAnnotations(directClassFile, i2, i3, parseObserver);
                    }
                    if (str == AttRuntimeVisibleAnnotations.ATTRIBUTE_NAME) {
                        return runtimeVisibleAnnotations(directClassFile, i2, i3, parseObserver);
                    }
                    if (str == AttRuntimeInvisibleParameterAnnotations.ATTRIBUTE_NAME) {
                        return runtimeInvisibleParameterAnnotations(directClassFile, i2, i3, parseObserver);
                    }
                    if (str == AttRuntimeVisibleParameterAnnotations.ATTRIBUTE_NAME) {
                        return runtimeVisibleParameterAnnotations(directClassFile, i2, i3, parseObserver);
                    }
                    if (str == AttSignature.ATTRIBUTE_NAME) {
                        return signature(directClassFile, i2, i3, parseObserver);
                    }
                    if (str == AttSynthetic.ATTRIBUTE_NAME) {
                        return synthetic(directClassFile, i2, i3, parseObserver);
                    }
                }
            } else if (str == AttConstantValue.ATTRIBUTE_NAME) {
                return constantValue(directClassFile, i2, i3, parseObserver);
            } else {
                if (str == AttDeprecated.ATTRIBUTE_NAME) {
                    return deprecated(directClassFile, i2, i3, parseObserver);
                }
                if (str == AttRuntimeInvisibleAnnotations.ATTRIBUTE_NAME) {
                    return runtimeInvisibleAnnotations(directClassFile, i2, i3, parseObserver);
                }
                if (str == AttRuntimeVisibleAnnotations.ATTRIBUTE_NAME) {
                    return runtimeVisibleAnnotations(directClassFile, i2, i3, parseObserver);
                }
                if (str == AttSignature.ATTRIBUTE_NAME) {
                    return signature(directClassFile, i2, i3, parseObserver);
                }
                if (str == AttSynthetic.ATTRIBUTE_NAME) {
                    return synthetic(directClassFile, i2, i3, parseObserver);
                }
            }
        } else if (str == AttBootstrapMethods.ATTRIBUTE_NAME) {
            return bootstrapMethods(directClassFile, i2, i3, parseObserver);
        } else {
            if (str == AttDeprecated.ATTRIBUTE_NAME) {
                return deprecated(directClassFile, i2, i3, parseObserver);
            }
            if (str == AttEnclosingMethod.ATTRIBUTE_NAME) {
                return enclosingMethod(directClassFile, i2, i3, parseObserver);
            }
            if (str == AttInnerClasses.ATTRIBUTE_NAME) {
                return innerClasses(directClassFile, i2, i3, parseObserver);
            }
            if (str == AttRuntimeInvisibleAnnotations.ATTRIBUTE_NAME) {
                return runtimeInvisibleAnnotations(directClassFile, i2, i3, parseObserver);
            }
            if (str == AttRuntimeVisibleAnnotations.ATTRIBUTE_NAME) {
                return runtimeVisibleAnnotations(directClassFile, i2, i3, parseObserver);
            }
            if (str == AttSynthetic.ATTRIBUTE_NAME) {
                return synthetic(directClassFile, i2, i3, parseObserver);
            }
            if (str == AttSignature.ATTRIBUTE_NAME) {
                return signature(directClassFile, i2, i3, parseObserver);
            }
            if (str == AttSourceDebugExtension.ATTRIBUTE_NAME) {
                return sourceDebugExtension(directClassFile, i2, i3, parseObserver);
            }
            if (str == AttSourceFile.ATTRIBUTE_NAME) {
                return sourceFile(directClassFile, i2, i3, parseObserver);
            }
        }
        return super.parse0(directClassFile, i, str, i2, i3, parseObserver);
    }

    private Attribute annotationDefault(DirectClassFile directClassFile, int i, int i2, ParseObserver parseObserver) {
        if (i2 < 2) {
            throwSeverelyTruncated();
        }
        return new AttAnnotationDefault(new AnnotationParser(directClassFile, i, i2, parseObserver).parseValueAttribute(), i2);
    }

    private Attribute bootstrapMethods(DirectClassFile directClassFile, int i, int i2, ParseObserver parseObserver) {
        if (i2 < 2) {
            return throwSeverelyTruncated();
        }
        ByteArray bytes = directClassFile.getBytes();
        int unsignedShort = bytes.getUnsignedShort(i);
        if (parseObserver != null) {
            parseObserver.parsed(bytes, i, 2, "num_boostrap_methods: " + Hex.u2(unsignedShort));
        }
        return new AttBootstrapMethods(parseBootstrapMethods(bytes, directClassFile.getConstantPool(), directClassFile.getThisClass(), unsignedShort, i + 2, i2 - 2, parseObserver));
    }

    private Attribute code(DirectClassFile directClassFile, int i, int i2, ParseObserver parseObserver) {
        if (i2 < 12) {
            return throwSeverelyTruncated();
        }
        ByteArray bytes = directClassFile.getBytes();
        ConstantPool constantPool = directClassFile.getConstantPool();
        int unsignedShort = bytes.getUnsignedShort(i);
        int i3 = i + 2;
        int unsignedShort2 = bytes.getUnsignedShort(i3);
        int i4 = i + 4;
        int i5 = bytes.getInt(i4);
        if (parseObserver != null) {
            parseObserver.parsed(bytes, i, 2, "max_stack: " + Hex.u2(unsignedShort));
            parseObserver.parsed(bytes, i3, 2, "max_locals: " + Hex.u2(unsignedShort2));
            parseObserver.parsed(bytes, i4, 4, "code_length: " + Hex.u4(i5));
        }
        int i6 = i + 8;
        int i7 = i2 - 8;
        if (i7 < i5 + 4) {
            return throwTruncated();
        }
        int i8 = i6 + i5;
        int i9 = i7 - i5;
        BytecodeArray bytecodeArray = new BytecodeArray(bytes.slice(i6, i8), constantPool);
        if (parseObserver != null) {
            bytecodeArray.forEach(new CodeObserver(bytecodeArray.getBytes(), parseObserver));
        }
        int unsignedShort3 = bytes.getUnsignedShort(i8);
        ByteCatchList byteCatchList = unsignedShort3 == 0 ? ByteCatchList.EMPTY : new ByteCatchList(unsignedShort3);
        if (parseObserver != null) {
            parseObserver.parsed(bytes, i8, 2, "exception_table_length: " + Hex.u2(unsignedShort3));
        }
        int i10 = i8 + 2;
        int i11 = i9 - 2;
        if (i11 < (unsignedShort3 * 8) + 2) {
            return throwTruncated();
        }
        for (int i12 = 0; i12 < unsignedShort3; i12++) {
            if (parseObserver != null) {
                parseObserver.changeIndent(1);
            }
            int unsignedShort4 = bytes.getUnsignedShort(i10);
            int unsignedShort5 = bytes.getUnsignedShort(i10 + 2);
            int unsignedShort6 = bytes.getUnsignedShort(i10 + 4);
            CstType cstType = (CstType) constantPool.get0Ok(bytes.getUnsignedShort(i10 + 6));
            byteCatchList.set(i12, unsignedShort4, unsignedShort5, unsignedShort6, cstType);
            if (parseObserver != null) {
                parseObserver.parsed(bytes, i10, 8, Hex.u2(unsignedShort4) + ".." + Hex.u2(unsignedShort5) + " -> " + Hex.u2(unsignedShort6) + RendererActivity.DEFAULT_TITLE + (cstType == null ? "<any>" : cstType.toHuman()));
            }
            i10 += 8;
            i11 -= 8;
            if (parseObserver != null) {
                parseObserver.changeIndent(-1);
            }
        }
        byteCatchList.setImmutable();
        AttributeListParser attributeListParser = new AttributeListParser(directClassFile, 3, i10, this);
        attributeListParser.setObserver(parseObserver);
        StdAttributeList list = attributeListParser.getList();
        list.setImmutable();
        int endOffset = attributeListParser.getEndOffset() - i10;
        if (endOffset != i11) {
            return throwBadLength(endOffset + (i10 - i));
        }
        return new AttCode(unsignedShort, unsignedShort2, bytecodeArray, byteCatchList, list);
    }

    private Attribute constantValue(DirectClassFile directClassFile, int i, int i2, ParseObserver parseObserver) {
        if (i2 != 2) {
            return throwBadLength(2);
        }
        ByteArray bytes = directClassFile.getBytes();
        TypedConstant typedConstant = (TypedConstant) directClassFile.getConstantPool().get(bytes.getUnsignedShort(i));
        AttConstantValue attConstantValue = new AttConstantValue(typedConstant);
        if (parseObserver != null) {
            parseObserver.parsed(bytes, i, 2, "value: " + typedConstant);
        }
        return attConstantValue;
    }

    private Attribute deprecated(DirectClassFile directClassFile, int i, int i2, ParseObserver parseObserver) {
        if (i2 != 0) {
            return throwBadLength(0);
        }
        return new AttDeprecated();
    }

    private Attribute enclosingMethod(DirectClassFile directClassFile, int i, int i2, ParseObserver parseObserver) {
        if (i2 != 4) {
            throwBadLength(4);
        }
        ByteArray bytes = directClassFile.getBytes();
        ConstantPool constantPool = directClassFile.getConstantPool();
        CstType cstType = (CstType) constantPool.get(bytes.getUnsignedShort(i));
        int i3 = i + 2;
        CstNat cstNat = (CstNat) constantPool.get0Ok(bytes.getUnsignedShort(i3));
        AttEnclosingMethod attEnclosingMethod = new AttEnclosingMethod(cstType, cstNat);
        if (parseObserver != null) {
            parseObserver.parsed(bytes, i, 2, "class: " + cstType);
            parseObserver.parsed(bytes, i3, 2, "method: " + DirectClassFile.stringOrNone(cstNat));
        }
        return attEnclosingMethod;
    }

    private Attribute exceptions(DirectClassFile directClassFile, int i, int i2, ParseObserver parseObserver) {
        if (i2 < 2) {
            return throwSeverelyTruncated();
        }
        ByteArray bytes = directClassFile.getBytes();
        int unsignedShort = bytes.getUnsignedShort(i);
        if (parseObserver != null) {
            parseObserver.parsed(bytes, i, 2, "number_of_exceptions: " + Hex.u2(unsignedShort));
        }
        int i3 = i + 2;
        int i4 = unsignedShort * 2;
        if (i2 - 2 != i4) {
            throwBadLength(i4 + 2);
        }
        return new AttExceptions(directClassFile.makeTypeList(i3, unsignedShort));
    }

    private Attribute innerClasses(DirectClassFile directClassFile, int i, int i2, ParseObserver parseObserver) {
        if (i2 < 2) {
            return throwSeverelyTruncated();
        }
        ByteArray bytes = directClassFile.getBytes();
        ConstantPool constantPool = directClassFile.getConstantPool();
        int unsignedShort = bytes.getUnsignedShort(i);
        if (parseObserver != null) {
            parseObserver.parsed(bytes, i, 2, "number_of_classes: " + Hex.u2(unsignedShort));
        }
        int i3 = i + 2;
        int i4 = unsignedShort * 8;
        if (i2 - 2 != i4) {
            throwBadLength(i4 + 2);
        }
        InnerClassList innerClassList = new InnerClassList(unsignedShort);
        for (int i5 = 0; i5 < unsignedShort; i5++) {
            int unsignedShort2 = bytes.getUnsignedShort(i3);
            int i6 = i3 + 2;
            int unsignedShort3 = bytes.getUnsignedShort(i6);
            int i7 = i3 + 4;
            int unsignedShort4 = bytes.getUnsignedShort(i7);
            int i8 = i3 + 6;
            int unsignedShort5 = bytes.getUnsignedShort(i8);
            CstType cstType = (CstType) constantPool.get(unsignedShort2);
            CstType cstType2 = (CstType) constantPool.get0Ok(unsignedShort3);
            CstString cstString = (CstString) constantPool.get0Ok(unsignedShort4);
            innerClassList.set(i5, cstType, cstType2, cstString, unsignedShort5);
            if (parseObserver != null) {
                parseObserver.parsed(bytes, i3, 2, "inner_class: " + DirectClassFile.stringOrNone(cstType));
                parseObserver.parsed(bytes, i6, 2, "  outer_class: " + DirectClassFile.stringOrNone(cstType2));
                parseObserver.parsed(bytes, i7, 2, "  name: " + DirectClassFile.stringOrNone(cstString));
                parseObserver.parsed(bytes, i8, 2, "  access_flags: " + AccessFlags.innerClassString(unsignedShort5));
            }
            i3 += 8;
        }
        innerClassList.setImmutable();
        return new AttInnerClasses(innerClassList);
    }

    private Attribute lineNumberTable(DirectClassFile directClassFile, int i, int i2, ParseObserver parseObserver) {
        if (i2 < 2) {
            return throwSeverelyTruncated();
        }
        ByteArray bytes = directClassFile.getBytes();
        int unsignedShort = bytes.getUnsignedShort(i);
        if (parseObserver != null) {
            parseObserver.parsed(bytes, i, 2, "line_number_table_length: " + Hex.u2(unsignedShort));
        }
        int i3 = i + 2;
        int i4 = unsignedShort * 4;
        if (i2 - 2 != i4) {
            throwBadLength(i4 + 2);
        }
        LineNumberList lineNumberList = new LineNumberList(unsignedShort);
        for (int i5 = 0; i5 < unsignedShort; i5++) {
            int unsignedShort2 = bytes.getUnsignedShort(i3);
            int unsignedShort3 = bytes.getUnsignedShort(i3 + 2);
            lineNumberList.set(i5, unsignedShort2, unsignedShort3);
            if (parseObserver != null) {
                parseObserver.parsed(bytes, i3, 4, Hex.u2(unsignedShort2) + RendererActivity.DEFAULT_TITLE + unsignedShort3);
            }
            i3 += 4;
        }
        lineNumberList.setImmutable();
        return new AttLineNumberTable(lineNumberList);
    }

    private Attribute localVariableTable(DirectClassFile directClassFile, int i, int i2, ParseObserver parseObserver) {
        if (i2 < 2) {
            return throwSeverelyTruncated();
        }
        ByteArray bytes = directClassFile.getBytes();
        int unsignedShort = bytes.getUnsignedShort(i);
        if (parseObserver != null) {
            parseObserver.parsed(bytes, i, 2, "local_variable_table_length: " + Hex.u2(unsignedShort));
        }
        return new AttLocalVariableTable(parseLocalVariables(bytes.slice(i + 2, i + i2), directClassFile.getConstantPool(), parseObserver, unsignedShort, false));
    }

    private Attribute localVariableTypeTable(DirectClassFile directClassFile, int i, int i2, ParseObserver parseObserver) {
        if (i2 < 2) {
            return throwSeverelyTruncated();
        }
        ByteArray bytes = directClassFile.getBytes();
        int unsignedShort = bytes.getUnsignedShort(i);
        if (parseObserver != null) {
            parseObserver.parsed(bytes, i, 2, "local_variable_type_table_length: " + Hex.u2(unsignedShort));
        }
        return new AttLocalVariableTypeTable(parseLocalVariables(bytes.slice(i + 2, i + i2), directClassFile.getConstantPool(), parseObserver, unsignedShort, true));
    }

    private LocalVariableList parseLocalVariables(ByteArray byteArray, ConstantPool constantPool, ParseObserver parseObserver, int i, boolean z) {
        CstString cstString;
        CstString cstString2;
        int i2 = i * 10;
        if (byteArray.size() != i2) {
            throwBadLength(i2 + 2);
        }
        ByteArray.MyDataInputStream makeDataInputStream = byteArray.makeDataInputStream();
        LocalVariableList localVariableList = new LocalVariableList(i);
        for (int i3 = 0; i3 < i; i3++) {
            try {
                int readUnsignedShort = makeDataInputStream.readUnsignedShort();
                int readUnsignedShort2 = makeDataInputStream.readUnsignedShort();
                int readUnsignedShort3 = makeDataInputStream.readUnsignedShort();
                int readUnsignedShort4 = makeDataInputStream.readUnsignedShort();
                int readUnsignedShort5 = makeDataInputStream.readUnsignedShort();
                CstString cstString3 = (CstString) constantPool.get(readUnsignedShort3);
                CstString cstString4 = (CstString) constantPool.get(readUnsignedShort4);
                if (z) {
                    cstString2 = null;
                    cstString = cstString4;
                } else {
                    cstString = null;
                    cstString2 = cstString4;
                }
                localVariableList.set(i3, readUnsignedShort, readUnsignedShort2, cstString3, cstString2, cstString, readUnsignedShort5);
                if (parseObserver != null) {
                    parseObserver.parsed(byteArray, i3 * 10, 10, Hex.u2(readUnsignedShort) + ".." + Hex.u2(readUnsignedShort + readUnsignedShort2) + RendererActivity.DEFAULT_TITLE + Hex.u2(readUnsignedShort5) + RendererActivity.DEFAULT_TITLE + cstString3.toHuman() + RendererActivity.DEFAULT_TITLE + cstString4.toHuman());
                }
            } catch (IOException e) {
                throw new RuntimeException("shouldn't happen", e);
            }
        }
        localVariableList.setImmutable();
        return localVariableList;
    }

    private Attribute runtimeInvisibleAnnotations(DirectClassFile directClassFile, int i, int i2, ParseObserver parseObserver) {
        if (i2 < 2) {
            throwSeverelyTruncated();
        }
        return new AttRuntimeInvisibleAnnotations(new AnnotationParser(directClassFile, i, i2, parseObserver).parseAnnotationAttribute(AnnotationVisibility.BUILD), i2);
    }

    private Attribute runtimeVisibleAnnotations(DirectClassFile directClassFile, int i, int i2, ParseObserver parseObserver) {
        if (i2 < 2) {
            throwSeverelyTruncated();
        }
        return new AttRuntimeVisibleAnnotations(new AnnotationParser(directClassFile, i, i2, parseObserver).parseAnnotationAttribute(AnnotationVisibility.RUNTIME), i2);
    }

    private Attribute runtimeInvisibleParameterAnnotations(DirectClassFile directClassFile, int i, int i2, ParseObserver parseObserver) {
        if (i2 < 2) {
            throwSeverelyTruncated();
        }
        return new AttRuntimeInvisibleParameterAnnotations(new AnnotationParser(directClassFile, i, i2, parseObserver).parseParameterAttribute(AnnotationVisibility.BUILD), i2);
    }

    private Attribute runtimeVisibleParameterAnnotations(DirectClassFile directClassFile, int i, int i2, ParseObserver parseObserver) {
        if (i2 < 2) {
            throwSeverelyTruncated();
        }
        return new AttRuntimeVisibleParameterAnnotations(new AnnotationParser(directClassFile, i, i2, parseObserver).parseParameterAttribute(AnnotationVisibility.RUNTIME), i2);
    }

    private Attribute signature(DirectClassFile directClassFile, int i, int i2, ParseObserver parseObserver) {
        if (i2 != 2) {
            throwBadLength(2);
        }
        ByteArray bytes = directClassFile.getBytes();
        CstString cstString = (CstString) directClassFile.getConstantPool().get(bytes.getUnsignedShort(i));
        AttSignature attSignature = new AttSignature(cstString);
        if (parseObserver != null) {
            parseObserver.parsed(bytes, i, 2, "signature: " + cstString);
        }
        return attSignature;
    }

    private Attribute sourceDebugExtension(DirectClassFile directClassFile, int i, int i2, ParseObserver parseObserver) {
        ByteArray slice = directClassFile.getBytes().slice(i, i + i2);
        CstString cstString = new CstString(slice);
        AttSourceDebugExtension attSourceDebugExtension = new AttSourceDebugExtension(cstString);
        if (parseObserver != null) {
            parseObserver.parsed(slice, i, i2, "sourceDebugExtension: " + cstString.getString());
        }
        return attSourceDebugExtension;
    }

    private Attribute sourceFile(DirectClassFile directClassFile, int i, int i2, ParseObserver parseObserver) {
        if (i2 != 2) {
            throwBadLength(2);
        }
        ByteArray bytes = directClassFile.getBytes();
        CstString cstString = (CstString) directClassFile.getConstantPool().get(bytes.getUnsignedShort(i));
        AttSourceFile attSourceFile = new AttSourceFile(cstString);
        if (parseObserver != null) {
            parseObserver.parsed(bytes, i, 2, "source: " + cstString);
        }
        return attSourceFile;
    }

    private Attribute synthetic(DirectClassFile directClassFile, int i, int i2, ParseObserver parseObserver) {
        if (i2 != 0) {
            return throwBadLength(0);
        }
        return new AttSynthetic();
    }

    private static Attribute throwSeverelyTruncated() {
        throw new ParseException("severely truncated attribute");
    }

    private static Attribute throwTruncated() {
        throw new ParseException("truncated attribute");
    }

    private static Attribute throwBadLength(int i) {
        throw new ParseException("bad attribute length; expected length " + Hex.u4(i));
    }

    private BootstrapMethodsList parseBootstrapMethods(ByteArray byteArray, ConstantPool constantPool, CstType cstType, int i, int i2, int i3, ParseObserver parseObserver) throws ParseException {
        BootstrapMethodsList bootstrapMethodsList = new BootstrapMethodsList(i);
        int i4 = i2;
        int i5 = i3;
        for (int i6 = 0; i6 < i; i6++) {
            if (i5 < 4) {
                throwTruncated();
            }
            int unsignedShort = byteArray.getUnsignedShort(i4);
            int i7 = i4 + 2;
            int unsignedShort2 = byteArray.getUnsignedShort(i7);
            if (parseObserver != null) {
                parseObserver.parsed(byteArray, i4, 2, "bootstrap_method_ref: " + Hex.u2(unsignedShort));
                parseObserver.parsed(byteArray, i7, 2, "num_bootstrap_arguments: " + Hex.u2(unsignedShort2));
            }
            i4 += 4;
            i5 -= 4;
            if (i5 < unsignedShort2 * 2) {
                throwTruncated();
            }
            BootstrapMethodArgumentsList bootstrapMethodArgumentsList = new BootstrapMethodArgumentsList(unsignedShort2);
            int i8 = 0;
            while (i8 < unsignedShort2) {
                int unsignedShort3 = byteArray.getUnsignedShort(i4);
                if (parseObserver != null) {
                    parseObserver.parsed(byteArray, i4, 2, "bootstrap_arguments[" + i8 + "]" + Hex.u2(unsignedShort3));
                }
                bootstrapMethodArgumentsList.set(i8, constantPool.get(unsignedShort3));
                i8++;
                i4 += 2;
                i5 -= 2;
            }
            bootstrapMethodArgumentsList.setImmutable();
            bootstrapMethodsList.set(i6, cstType, (CstMethodHandle) constantPool.get(unsignedShort), bootstrapMethodArgumentsList);
        }
        bootstrapMethodsList.setImmutable();
        if (i5 != 0) {
            throwBadLength(i5);
        }
        return bootstrapMethodsList;
    }
}

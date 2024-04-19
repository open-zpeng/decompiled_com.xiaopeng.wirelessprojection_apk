package com.android.dx.command.annotool;

import com.android.dx.cf.attrib.AttRuntimeInvisibleAnnotations;
import com.android.dx.cf.attrib.AttRuntimeVisibleAnnotations;
import com.android.dx.cf.attrib.BaseAnnotations;
import com.android.dx.cf.direct.ClassPathOpener;
import com.android.dx.cf.direct.DirectClassFile;
import com.android.dx.cf.direct.StdAttributeFactory;
import com.android.dx.cf.iface.Attribute;
import com.android.dx.cf.iface.AttributeList;
import com.android.dx.command.annotool.Main;
import com.android.dx.rop.annotation.Annotation;
import com.android.dx.util.ByteArray;
import com.android.multidex.ClassPathElement;
import java.io.File;
import java.lang.annotation.ElementType;
import java.util.HashSet;
import java.util.Iterator;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class AnnotationLister {
    private static final String PACKAGE_INFO = "package-info";
    private final Main.Arguments args;
    HashSet<String> matchInnerClassesOf = new HashSet<>();
    HashSet<String> matchPackages = new HashSet<>();

    /* JADX INFO: Access modifiers changed from: package-private */
    public AnnotationLister(Main.Arguments arguments) {
        this.args = arguments;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void process() {
        for (String str : this.args.files) {
            new ClassPathOpener(str, true, new ClassPathOpener.Consumer() { // from class: com.android.dx.command.annotool.AnnotationLister.1
                @Override // com.android.dx.cf.direct.ClassPathOpener.Consumer
                public void onProcessArchiveStart(File file) {
                }

                @Override // com.android.dx.cf.direct.ClassPathOpener.Consumer
                public boolean processFileBytes(String str2, long j, byte[] bArr) {
                    if (str2.endsWith(".class")) {
                        DirectClassFile directClassFile = new DirectClassFile(new ByteArray(bArr), str2, true);
                        directClassFile.setAttributeFactory(StdAttributeFactory.THE_ONE);
                        AttributeList attributes = directClassFile.getAttributes();
                        String className = directClassFile.getThisClass().getClassType().getClassName();
                        if (!className.endsWith(AnnotationLister.PACKAGE_INFO)) {
                            if (AnnotationLister.this.isMatchingInnerClass(className) || AnnotationLister.this.isMatchingPackage(className)) {
                                AnnotationLister.this.printMatch(directClassFile);
                            } else {
                                for (Attribute findFirst = attributes.findFirst(AttRuntimeInvisibleAnnotations.ATTRIBUTE_NAME); findFirst != null; findFirst = attributes.findNext(findFirst)) {
                                    AnnotationLister.this.visitClassAnnotation(directClassFile, (BaseAnnotations) findFirst);
                                }
                                for (Attribute findFirst2 = attributes.findFirst(AttRuntimeVisibleAnnotations.ATTRIBUTE_NAME); findFirst2 != null; findFirst2 = attributes.findNext(findFirst2)) {
                                    AnnotationLister.this.visitClassAnnotation(directClassFile, (BaseAnnotations) findFirst2);
                                }
                            }
                        } else {
                            for (Attribute findFirst3 = attributes.findFirst(AttRuntimeInvisibleAnnotations.ATTRIBUTE_NAME); findFirst3 != null; findFirst3 = attributes.findNext(findFirst3)) {
                                AnnotationLister.this.visitPackageAnnotation(directClassFile, (BaseAnnotations) findFirst3);
                            }
                            for (Attribute findFirst4 = attributes.findFirst(AttRuntimeVisibleAnnotations.ATTRIBUTE_NAME); findFirst4 != null; findFirst4 = attributes.findNext(findFirst4)) {
                                AnnotationLister.this.visitPackageAnnotation(directClassFile, (BaseAnnotations) findFirst4);
                            }
                        }
                        return true;
                    }
                    return true;
                }

                @Override // com.android.dx.cf.direct.ClassPathOpener.Consumer
                public void onException(Exception exc) {
                    throw new RuntimeException(exc);
                }
            }).process();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void visitClassAnnotation(DirectClassFile directClassFile, BaseAnnotations baseAnnotations) {
        if (this.args.eTypes.contains(ElementType.TYPE)) {
            for (Annotation annotation : baseAnnotations.getAnnotations().getAnnotations()) {
                if (this.args.aclass.equals(annotation.getType().getClassType().getClassName())) {
                    printMatch(directClassFile);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void visitPackageAnnotation(DirectClassFile directClassFile, BaseAnnotations baseAnnotations) {
        if (this.args.eTypes.contains(ElementType.PACKAGE)) {
            String className = directClassFile.getThisClass().getClassType().getClassName();
            int lastIndexOf = className.lastIndexOf(47);
            String substring = lastIndexOf == -1 ? "" : className.substring(0, lastIndexOf);
            for (Annotation annotation : baseAnnotations.getAnnotations().getAnnotations()) {
                if (this.args.aclass.equals(annotation.getType().getClassType().getClassName())) {
                    printMatchPackage(substring);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.dx.command.annotool.AnnotationLister$2  reason: invalid class name */
    /* loaded from: classes.dex */
    public static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$com$android$dx$command$annotool$Main$PrintType;

        static {
            int[] iArr = new int[Main.PrintType.values().length];
            $SwitchMap$com$android$dx$command$annotool$Main$PrintType = iArr;
            try {
                iArr[Main.PrintType.CLASS.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$android$dx$command$annotool$Main$PrintType[Main.PrintType.INNERCLASS.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$android$dx$command$annotool$Main$PrintType[Main.PrintType.METHOD.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$android$dx$command$annotool$Main$PrintType[Main.PrintType.PACKAGE.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
        }
    }

    private void printMatchPackage(String str) {
        Iterator it = this.args.printTypes.iterator();
        while (it.hasNext()) {
            int i = AnonymousClass2.$SwitchMap$com$android$dx$command$annotool$Main$PrintType[((Main.PrintType) it.next()).ordinal()];
            if (i == 1 || i == 2 || i == 3) {
                this.matchPackages.add(str);
            } else if (i == 4) {
                System.out.println(str.replace(ClassPathElement.SEPARATOR_CHAR, '.'));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void printMatch(DirectClassFile directClassFile) {
        Iterator it = this.args.printTypes.iterator();
        while (it.hasNext()) {
            int i = AnonymousClass2.$SwitchMap$com$android$dx$command$annotool$Main$PrintType[((Main.PrintType) it.next()).ordinal()];
            if (i == 1) {
                System.out.println(directClassFile.getThisClass().getClassType().getClassName().replace(ClassPathElement.SEPARATOR_CHAR, '.'));
            } else if (i == 2) {
                this.matchInnerClassesOf.add(directClassFile.getThisClass().getClassType().getClassName());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isMatchingInnerClass(String str) {
        do {
            int lastIndexOf = str.lastIndexOf(36);
            if (lastIndexOf <= 0) {
                return false;
            }
            str = str.substring(0, lastIndexOf);
        } while (!this.matchInnerClassesOf.contains(str));
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isMatchingPackage(String str) {
        int lastIndexOf = str.lastIndexOf(47);
        return this.matchPackages.contains(lastIndexOf == -1 ? "" : str.substring(0, lastIndexOf));
    }
}

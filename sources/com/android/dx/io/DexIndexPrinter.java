package com.android.dx.io;

import com.android.dex.ClassDef;
import com.android.dex.Dex;
import com.android.dex.FieldId;
import com.android.dex.MethodId;
import com.android.dex.ProtoId;
import com.android.dex.TableOfContents;
import com.xiaopeng.wirelessprojection.dmr.RendererActivity;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
/* loaded from: classes.dex */
public final class DexIndexPrinter {
    private final Dex dex;
    private final TableOfContents tableOfContents;

    public DexIndexPrinter(File file) throws IOException {
        Dex dex = new Dex(file);
        this.dex = dex;
        this.tableOfContents = dex.getTableOfContents();
    }

    private void printMap() {
        TableOfContents.Section[] sectionArr;
        for (TableOfContents.Section section : this.tableOfContents.sections) {
            if (section.off != -1) {
                System.out.println("section " + Integer.toHexString(section.type) + " off=" + Integer.toHexString(section.off) + " size=" + Integer.toHexString(section.size) + " byteCount=" + Integer.toHexString(section.byteCount));
            }
        }
    }

    private void printStrings() throws IOException {
        Iterator<String> it = this.dex.strings().iterator();
        int i = 0;
        while (it.hasNext()) {
            System.out.println("string " + i + ": " + it.next());
            i++;
        }
    }

    private void printTypeIds() throws IOException {
        Iterator<Integer> it = this.dex.typeIds().iterator();
        int i = 0;
        while (it.hasNext()) {
            System.out.println("type " + i + ": " + this.dex.strings().get(it.next().intValue()));
            i++;
        }
    }

    private void printProtoIds() throws IOException {
        Iterator<ProtoId> it = this.dex.protoIds().iterator();
        int i = 0;
        while (it.hasNext()) {
            System.out.println("proto " + i + ": " + it.next());
            i++;
        }
    }

    private void printFieldIds() throws IOException {
        Iterator<FieldId> it = this.dex.fieldIds().iterator();
        int i = 0;
        while (it.hasNext()) {
            System.out.println("field " + i + ": " + it.next());
            i++;
        }
    }

    private void printMethodIds() throws IOException {
        Iterator<MethodId> it = this.dex.methodIds().iterator();
        int i = 0;
        while (it.hasNext()) {
            System.out.println("methodId " + i + ": " + it.next());
            i++;
        }
    }

    private void printTypeLists() throws IOException {
        if (this.tableOfContents.typeLists.off == -1) {
            System.out.println("No type lists");
            return;
        }
        Dex.Section open = this.dex.open(this.tableOfContents.typeLists.off);
        for (int i = 0; i < this.tableOfContents.typeLists.size; i++) {
            int readInt = open.readInt();
            System.out.print("Type list i=" + i + ", size=" + readInt + ", elements=");
            for (int i2 = 0; i2 < readInt; i2++) {
                System.out.print(RendererActivity.DEFAULT_TITLE + this.dex.typeNames().get(open.readShort()));
            }
            if (readInt % 2 == 1) {
                open.readShort();
            }
            System.out.println();
        }
    }

    private void printClassDefs() {
        Iterator<ClassDef> it = this.dex.classDefs().iterator();
        int i = 0;
        while (it.hasNext()) {
            System.out.println("class def " + i + ": " + it.next());
            i++;
        }
    }

    public static void main(String[] strArr) throws IOException {
        DexIndexPrinter dexIndexPrinter = new DexIndexPrinter(new File(strArr[0]));
        dexIndexPrinter.printMap();
        dexIndexPrinter.printStrings();
        dexIndexPrinter.printTypeIds();
        dexIndexPrinter.printProtoIds();
        dexIndexPrinter.printFieldIds();
        dexIndexPrinter.printMethodIds();
        dexIndexPrinter.printTypeLists();
        dexIndexPrinter.printClassDefs();
    }
}

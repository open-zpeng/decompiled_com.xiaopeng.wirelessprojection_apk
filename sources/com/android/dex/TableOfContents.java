package com.android.dex;

import androidx.core.view.InputDeviceCompat;
import androidx.fragment.app.FragmentTransaction;
import com.android.dex.Dex;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
/* loaded from: classes.dex */
public final class TableOfContents {
    public final Section annotationSetRefLists;
    public final Section annotationSets;
    public final Section annotations;
    public final Section annotationsDirectories;
    public int apiLevel;
    public final Section callSiteIds;
    public int checksum;
    public final Section classDatas;
    public final Section classDefs;
    public final Section codes;
    public int dataOff;
    public int dataSize;
    public final Section debugInfos;
    public final Section encodedArrays;
    public final Section fieldIds;
    public int fileSize;
    public final Section header;
    public int linkOff;
    public int linkSize;
    public final Section mapList;
    public final Section methodHandles;
    public final Section methodIds;
    public final Section protoIds;
    public final Section[] sections;
    public byte[] signature;
    public final Section stringDatas;
    public final Section stringIds;
    public final Section typeIds;
    public final Section typeLists;

    public TableOfContents() {
        Section section = new Section(0);
        this.header = section;
        Section section2 = new Section(1);
        this.stringIds = section2;
        Section section3 = new Section(2);
        this.typeIds = section3;
        Section section4 = new Section(3);
        this.protoIds = section4;
        Section section5 = new Section(4);
        this.fieldIds = section5;
        Section section6 = new Section(5);
        this.methodIds = section6;
        Section section7 = new Section(6);
        this.classDefs = section7;
        Section section8 = new Section(7);
        this.callSiteIds = section8;
        Section section9 = new Section(8);
        this.methodHandles = section9;
        Section section10 = new Section(4096);
        this.mapList = section10;
        Section section11 = new Section(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        this.typeLists = section11;
        Section section12 = new Section(InputDeviceCompat.SOURCE_TOUCHSCREEN);
        this.annotationSetRefLists = section12;
        Section section13 = new Section(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        this.annotationSets = section13;
        Section section14 = new Section(8192);
        this.classDatas = section14;
        Section section15 = new Section(8193);
        this.codes = section15;
        Section section16 = new Section(8194);
        this.stringDatas = section16;
        Section section17 = new Section(8195);
        this.debugInfos = section17;
        Section section18 = new Section(8196);
        this.annotations = section18;
        Section section19 = new Section(8197);
        this.encodedArrays = section19;
        Section section20 = new Section(8198);
        this.annotationsDirectories = section20;
        this.sections = new Section[]{section, section2, section3, section4, section5, section6, section7, section10, section8, section9, section11, section12, section13, section14, section15, section16, section17, section18, section19, section20};
        this.signature = new byte[20];
    }

    public void readFrom(Dex dex) throws IOException {
        readHeader(dex.open(0));
        readMap(dex.open(this.mapList.off));
        computeSizesFromOffsets();
    }

    private void readHeader(Dex.Section section) throws UnsupportedEncodingException {
        byte[] readByteArray = section.readByteArray(8);
        if (!DexFormat.isSupportedDexMagic(readByteArray)) {
            throw new DexException(String.format("Unexpected magic: [0x%02x, 0x%02x, 0x%02x, 0x%02x, 0x%02x, 0x%02x, 0x%02x, 0x%02x]", Byte.valueOf(readByteArray[0]), Byte.valueOf(readByteArray[1]), Byte.valueOf(readByteArray[2]), Byte.valueOf(readByteArray[3]), Byte.valueOf(readByteArray[4]), Byte.valueOf(readByteArray[5]), Byte.valueOf(readByteArray[6]), Byte.valueOf(readByteArray[7])));
        }
        this.apiLevel = DexFormat.magicToApi(readByteArray);
        this.checksum = section.readInt();
        this.signature = section.readByteArray(20);
        this.fileSize = section.readInt();
        int readInt = section.readInt();
        if (readInt != 112) {
            throw new DexException("Unexpected header: 0x" + Integer.toHexString(readInt));
        }
        int readInt2 = section.readInt();
        if (readInt2 != 305419896) {
            throw new DexException("Unexpected endian tag: 0x" + Integer.toHexString(readInt2));
        }
        this.linkSize = section.readInt();
        this.linkOff = section.readInt();
        this.mapList.off = section.readInt();
        if (this.mapList.off == 0) {
            throw new DexException("Cannot merge dex files that do not contain a map");
        }
        this.stringIds.size = section.readInt();
        this.stringIds.off = section.readInt();
        this.typeIds.size = section.readInt();
        this.typeIds.off = section.readInt();
        this.protoIds.size = section.readInt();
        this.protoIds.off = section.readInt();
        this.fieldIds.size = section.readInt();
        this.fieldIds.off = section.readInt();
        this.methodIds.size = section.readInt();
        this.methodIds.off = section.readInt();
        this.classDefs.size = section.readInt();
        this.classDefs.off = section.readInt();
        this.dataSize = section.readInt();
        this.dataOff = section.readInt();
    }

    private void readMap(Dex.Section section) throws IOException {
        int readInt = section.readInt();
        Section section2 = null;
        int i = 0;
        while (i < readInt) {
            short readShort = section.readShort();
            section.readShort();
            Section section3 = getSection(readShort);
            int readInt2 = section.readInt();
            int readInt3 = section.readInt();
            if ((section3.size != 0 && section3.size != readInt2) || (section3.off != -1 && section3.off != readInt3)) {
                throw new DexException("Unexpected map value for 0x" + Integer.toHexString(readShort));
            }
            section3.size = readInt2;
            section3.off = readInt3;
            if (section2 != null && section2.off > section3.off) {
                throw new DexException("Map is unsorted at " + section2 + ", " + section3);
            }
            i++;
            section2 = section3;
        }
        Arrays.sort(this.sections);
    }

    public void computeSizesFromOffsets() {
        int i = this.dataOff + this.dataSize;
        for (int length = this.sections.length - 1; length >= 0; length--) {
            Section section = this.sections[length];
            if (section.off != -1) {
                if (section.off > i) {
                    throw new DexException("Map is unsorted at " + section);
                }
                section.byteCount = i - section.off;
                i = section.off;
            }
        }
    }

    private Section getSection(short s) {
        Section[] sectionArr;
        for (Section section : this.sections) {
            if (section.type == s) {
                return section;
            }
        }
        throw new IllegalArgumentException("No such map item: " + ((int) s));
    }

    public void writeHeader(Dex.Section section, int i) throws IOException {
        section.write(DexFormat.apiToMagic(i).getBytes("UTF-8"));
        section.writeInt(this.checksum);
        section.write(this.signature);
        section.writeInt(this.fileSize);
        section.writeInt(112);
        section.writeInt(DexFormat.ENDIAN_TAG);
        section.writeInt(this.linkSize);
        section.writeInt(this.linkOff);
        section.writeInt(this.mapList.off);
        section.writeInt(this.stringIds.size);
        section.writeInt(this.stringIds.off);
        section.writeInt(this.typeIds.size);
        section.writeInt(this.typeIds.off);
        section.writeInt(this.protoIds.size);
        section.writeInt(this.protoIds.off);
        section.writeInt(this.fieldIds.size);
        section.writeInt(this.fieldIds.off);
        section.writeInt(this.methodIds.size);
        section.writeInt(this.methodIds.off);
        section.writeInt(this.classDefs.size);
        section.writeInt(this.classDefs.off);
        section.writeInt(this.dataSize);
        section.writeInt(this.dataOff);
    }

    public void writeMap(Dex.Section section) throws IOException {
        Section[] sectionArr;
        int i = 0;
        for (Section section2 : this.sections) {
            if (section2.exists()) {
                i++;
            }
        }
        section.writeInt(i);
        for (Section section3 : this.sections) {
            if (section3.exists()) {
                section.writeShort(section3.type);
                section.writeShort((short) 0);
                section.writeInt(section3.size);
                section.writeInt(section3.off);
            }
        }
    }

    /* loaded from: classes.dex */
    public static class Section implements Comparable<Section> {
        public final short type;
        public int size = 0;
        public int off = -1;
        public int byteCount = 0;

        public Section(int i) {
            this.type = (short) i;
        }

        public boolean exists() {
            return this.size > 0;
        }

        @Override // java.lang.Comparable
        public int compareTo(Section section) {
            int i = this.off;
            int i2 = section.off;
            if (i != i2) {
                return i < i2 ? -1 : 1;
            }
            return 0;
        }

        public String toString() {
            return String.format("Section[type=%#x,off=%#x,size=%#x]", Short.valueOf(this.type), Integer.valueOf(this.off), Integer.valueOf(this.size));
        }
    }
}

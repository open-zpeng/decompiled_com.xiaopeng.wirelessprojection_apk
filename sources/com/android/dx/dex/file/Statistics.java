package com.android.dx.dex.file;

import com.android.dx.util.AnnotatedOutput;
import java.util.HashMap;
import java.util.TreeMap;
/* loaded from: classes.dex */
public final class Statistics {
    private final HashMap<String, Data> dataMap = new HashMap<>(50);

    public void add(Item item) {
        String typeName = item.typeName();
        Data data = this.dataMap.get(typeName);
        if (data == null) {
            this.dataMap.put(typeName, new Data(item, typeName));
        } else {
            data.add(item);
        }
    }

    public void addAll(Section section) {
        for (Item item : section.items()) {
            add(item);
        }
    }

    public final void writeAnnotation(AnnotatedOutput annotatedOutput) {
        if (this.dataMap.size() == 0) {
            return;
        }
        annotatedOutput.annotate(0, "\nstatistics:\n");
        TreeMap treeMap = new TreeMap();
        for (Data data : this.dataMap.values()) {
            treeMap.put(data.name, data);
        }
        for (Data data2 : treeMap.values()) {
            data2.writeAnnotation(annotatedOutput);
        }
    }

    public String toHuman() {
        StringBuilder sb = new StringBuilder();
        sb.append("Statistics:\n");
        TreeMap treeMap = new TreeMap();
        for (Data data : this.dataMap.values()) {
            treeMap.put(data.name, data);
        }
        for (Data data2 : treeMap.values()) {
            sb.append(data2.toHuman());
        }
        return sb.toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class Data {
        private int count;
        private int largestSize;
        private final String name;
        private int smallestSize;
        private int totalSize;

        public Data(Item item, String str) {
            int writeSize = item.writeSize();
            this.name = str;
            this.count = 1;
            this.totalSize = writeSize;
            this.largestSize = writeSize;
            this.smallestSize = writeSize;
        }

        public void add(Item item) {
            int writeSize = item.writeSize();
            this.count++;
            this.totalSize += writeSize;
            if (writeSize > this.largestSize) {
                this.largestSize = writeSize;
            }
            if (writeSize < this.smallestSize) {
                this.smallestSize = writeSize;
            }
        }

        public void writeAnnotation(AnnotatedOutput annotatedOutput) {
            annotatedOutput.annotate(toHuman());
        }

        public String toHuman() {
            StringBuilder sb = new StringBuilder();
            sb.append("  " + this.name + ": " + this.count + " item" + (this.count == 1 ? "" : "s") + "; " + this.totalSize + " bytes total\n");
            if (this.smallestSize == this.largestSize) {
                sb.append("    " + this.smallestSize + " bytes/item\n");
            } else {
                sb.append("    " + this.smallestSize + ".." + this.largestSize + " bytes/item; average " + (this.totalSize / this.count) + "\n");
            }
            return sb.toString();
        }
    }
}

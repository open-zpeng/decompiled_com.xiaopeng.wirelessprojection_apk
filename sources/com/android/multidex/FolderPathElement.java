package com.android.multidex;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
/* loaded from: classes.dex */
class FolderPathElement implements ClassPathElement {
    private final File baseFolder;

    @Override // com.android.multidex.ClassPathElement
    public void close() {
    }

    public FolderPathElement(File file) {
        this.baseFolder = file;
    }

    @Override // com.android.multidex.ClassPathElement
    public InputStream open(String str) throws FileNotFoundException {
        return new FileInputStream(new File(this.baseFolder, str.replace(ClassPathElement.SEPARATOR_CHAR, File.separatorChar)));
    }

    @Override // com.android.multidex.ClassPathElement
    public Iterable<String> list() {
        ArrayList<String> arrayList = new ArrayList<>();
        collect(this.baseFolder, "", arrayList);
        return arrayList;
    }

    private void collect(File file, String str, ArrayList<String> arrayList) {
        File[] listFiles;
        for (File file2 : file.listFiles()) {
            if (file2.isDirectory()) {
                collect(file2, str + ClassPathElement.SEPARATOR_CHAR + file2.getName(), arrayList);
            } else {
                arrayList.add(str + ClassPathElement.SEPARATOR_CHAR + file2.getName());
            }
        }
    }
}

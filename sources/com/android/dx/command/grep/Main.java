package com.android.dx.command.grep;

import com.android.dex.Dex;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Pattern;
/* loaded from: classes.dex */
public final class Main {
    public static void main(String[] strArr) throws IOException {
        System.exit(new Grep(new Dex(new File(strArr[0])), Pattern.compile(strArr[1]), new PrintWriter(System.out)).grep() <= 0 ? 1 : 0);
    }
}

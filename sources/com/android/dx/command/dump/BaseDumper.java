package com.android.dx.command.dump;

import com.android.dx.cf.code.ConcreteMethod;
import com.android.dx.cf.iface.Member;
import com.android.dx.cf.iface.ParseObserver;
import com.android.dx.dex.DexOptions;
import com.android.dx.util.ByteArray;
import com.android.dx.util.Hex;
import com.android.dx.util.IndentingWriter;
import com.android.dx.util.TwoColumnOutput;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
/* loaded from: classes.dex */
public abstract class BaseDumper implements ParseObserver {
    protected Args args;
    private final byte[] bytes;
    protected final DexOptions dexOptions;
    private final String filePath;
    private final int hexCols;
    private int indent;
    private final PrintStream out;
    private final boolean rawBytes;
    private int readBytes;
    private String separator;
    private final boolean strictParse;
    private final int width;

    @Override // com.android.dx.cf.iface.ParseObserver
    public void endParsingMember(ByteArray byteArray, int i, String str, String str2, Member member) {
    }

    @Override // com.android.dx.cf.iface.ParseObserver
    public void startParsingMember(ByteArray byteArray, int i, String str, String str2) {
    }

    public BaseDumper(byte[] bArr, PrintStream printStream, String str, Args args) {
        this.bytes = bArr;
        boolean z = args.rawBytes;
        this.rawBytes = z;
        this.out = printStream;
        int i = args.width <= 0 ? 79 : args.width;
        this.width = i;
        this.filePath = str;
        this.strictParse = args.strictParse;
        this.indent = 0;
        this.separator = z ? "|" : "";
        this.readBytes = 0;
        this.args = args;
        this.dexOptions = new DexOptions();
        int i2 = (((i - 5) / 15) + 1) & (-2);
        if (i2 < 6) {
            i2 = 6;
        } else if (i2 > 10) {
            i2 = 10;
        }
        this.hexCols = i2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int computeParamWidth(ConcreteMethod concreteMethod, boolean z) {
        return concreteMethod.getEffectiveDescriptor().getParameterTypes().getWordCount();
    }

    @Override // com.android.dx.cf.iface.ParseObserver
    public void changeIndent(int i) {
        this.indent += i;
        this.separator = this.rawBytes ? "|" : "";
        for (int i2 = 0; i2 < this.indent; i2++) {
            this.separator += "  ";
        }
    }

    @Override // com.android.dx.cf.iface.ParseObserver
    public void parsed(ByteArray byteArray, int i, int i2, String str) {
        print(twoColumns(getRawBytes() ? hexDump(byteArray.underlyingOffset(i), i2) : "", str));
        this.readBytes += i2;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final int getReadBytes() {
        return this.readBytes;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final byte[] getBytes() {
        return this.bytes;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final String getFilePath() {
        return this.filePath;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final boolean getStrictParse() {
        return this.strictParse;
    }

    protected final void print(String str) {
        this.out.print(str);
    }

    protected final void println(String str) {
        this.out.println(str);
    }

    protected final boolean getRawBytes() {
        return this.rawBytes;
    }

    protected final int getWidth1() {
        if (this.rawBytes) {
            int i = this.hexCols;
            return (i * 2) + 5 + (i / 2);
        }
        return 0;
    }

    protected final int getWidth2() {
        return (this.width - (this.rawBytes ? getWidth1() + 1 : 0)) - (this.indent * 2);
    }

    protected final String hexDump(int i, int i2) {
        return Hex.dump(this.bytes, i, i2, i, this.hexCols, 4);
    }

    protected final String twoColumns(String str, String str2) {
        int width1 = getWidth1();
        int width2 = getWidth2();
        try {
            if (width1 == 0) {
                int length = str2.length();
                StringWriter stringWriter = new StringWriter(length * 2);
                IndentingWriter indentingWriter = new IndentingWriter(stringWriter, width2, this.separator);
                indentingWriter.write(str2);
                if (length == 0 || str2.charAt(length - 1) != '\n') {
                    indentingWriter.write(10);
                }
                indentingWriter.flush();
                return stringWriter.toString();
            }
            return TwoColumnOutput.toString(str, width1, this.separator, str2, width2);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

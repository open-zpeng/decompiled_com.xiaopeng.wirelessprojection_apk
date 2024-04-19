package com.android.dx.command.dexer;

import com.android.dx.dex.cf.CodeStatistics;
import com.android.dx.dex.cf.OptimizerOptions;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
/* loaded from: classes.dex */
public class DxContext {
    public final CodeStatistics codeStatistics;
    public final PrintStream err;
    final PrintStream noop;
    public final OptimizerOptions optimizerOptions;
    public final PrintStream out;

    public DxContext(OutputStream outputStream, OutputStream outputStream2) {
        this.codeStatistics = new CodeStatistics();
        this.optimizerOptions = new OptimizerOptions();
        this.noop = new PrintStream(new OutputStream() { // from class: com.android.dx.command.dexer.DxContext.1
            @Override // java.io.OutputStream
            public void write(int i) throws IOException {
            }
        });
        this.out = new PrintStream(outputStream);
        this.err = new PrintStream(outputStream2);
    }

    public DxContext() {
        this(System.out, System.err);
    }
}

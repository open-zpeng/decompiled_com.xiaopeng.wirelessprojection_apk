package com.android.dx.command.dump;

import com.android.dex.util.FileUtils;
import com.android.dx.cf.iface.ParseException;
import com.android.dx.util.HexParser;
import java.io.UnsupportedEncodingException;
/* loaded from: classes.dex */
public class Main {
    private final Args parsedArgs = new Args();

    private Main() {
    }

    public static void main(String[] strArr) {
        new Main().run(strArr);
    }

    private void run(String[] strArr) {
        int i = 0;
        while (i < strArr.length) {
            String str = strArr[i];
            if (str.equals("--") || !str.startsWith("--")) {
                break;
            }
            if (str.equals("--bytes")) {
                this.parsedArgs.rawBytes = true;
            } else if (str.equals("--basic-blocks")) {
                this.parsedArgs.basicBlocks = true;
            } else if (str.equals("--rop-blocks")) {
                this.parsedArgs.ropBlocks = true;
            } else if (str.equals("--optimize")) {
                this.parsedArgs.optimize = true;
            } else if (str.equals("--ssa-blocks")) {
                this.parsedArgs.ssaBlocks = true;
            } else if (str.startsWith("--ssa-step=")) {
                this.parsedArgs.ssaStep = str.substring(str.indexOf(61) + 1);
            } else if (str.equals("--debug")) {
                this.parsedArgs.debug = true;
            } else if (str.equals("--dot")) {
                this.parsedArgs.dotDump = true;
            } else if (str.equals("--strict")) {
                this.parsedArgs.strictParse = true;
            } else if (str.startsWith("--width=")) {
                String substring = str.substring(str.indexOf(61) + 1);
                this.parsedArgs.width = Integer.parseInt(substring);
            } else if (str.startsWith("--method=")) {
                this.parsedArgs.method = str.substring(str.indexOf(61) + 1);
            } else {
                System.err.println("unknown option: " + str);
                throw new RuntimeException("usage");
            }
            i++;
        }
        if (i == strArr.length) {
            System.err.println("no input files specified");
            throw new RuntimeException("usage");
        }
        while (i < strArr.length) {
            try {
                String str2 = strArr[i];
                System.out.println("reading " + str2 + "...");
                byte[] readFile = FileUtils.readFile(str2);
                if (!str2.endsWith(".class")) {
                    try {
                        readFile = HexParser.parse(new String(readFile, "utf-8"));
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException("shouldn't happen", e);
                        break;
                    }
                }
                processOne(str2, readFile);
            } catch (ParseException e2) {
                System.err.println("\ntrouble parsing:");
                if (this.parsedArgs.debug) {
                    e2.printStackTrace();
                } else {
                    e2.printContext(System.err);
                }
            }
            i++;
        }
    }

    private void processOne(String str, byte[] bArr) {
        if (this.parsedArgs.dotDump) {
            DotDumper.dump(bArr, str, this.parsedArgs);
        } else if (this.parsedArgs.basicBlocks) {
            BlockDumper.dump(bArr, System.out, str, false, this.parsedArgs);
        } else if (this.parsedArgs.ropBlocks) {
            BlockDumper.dump(bArr, System.out, str, true, this.parsedArgs);
        } else if (this.parsedArgs.ssaBlocks) {
            this.parsedArgs.optimize = false;
            SsaDumper.dump(bArr, System.out, str, this.parsedArgs);
        } else {
            ClassDumper.dump(bArr, System.out, str, this.parsedArgs);
        }
    }
}

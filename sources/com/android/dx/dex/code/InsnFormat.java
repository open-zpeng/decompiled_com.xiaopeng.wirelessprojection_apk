package com.android.dx.dex.code;

import com.android.dx.rop.code.RegisterSpec;
import com.android.dx.rop.code.RegisterSpecList;
import com.android.dx.rop.cst.CstInteger;
import com.android.dx.rop.cst.CstKnownNull;
import com.android.dx.rop.cst.CstLiteral64;
import com.android.dx.rop.cst.CstLiteralBits;
import com.android.dx.util.AnnotatedOutput;
import com.android.dx.util.Hex;
import java.util.BitSet;
/* loaded from: classes.dex */
public abstract class InsnFormat {
    public static final boolean ALLOW_EXTENDED_OPCODES = true;

    /* JADX INFO: Access modifiers changed from: protected */
    public static boolean signedFitsInByte(int i) {
        return ((byte) i) == i;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static boolean signedFitsInNibble(int i) {
        return i >= -8 && i <= 7;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static boolean signedFitsInShort(int i) {
        return ((short) i) == i;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static boolean unsignedFitsInByte(int i) {
        return i == (i & 255);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static boolean unsignedFitsInNibble(int i) {
        return i == (i & 15);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static boolean unsignedFitsInShort(int i) {
        return i == (65535 & i);
    }

    public boolean branchFits(TargetInsn targetInsn) {
        return false;
    }

    public abstract int codeSize();

    public abstract String insnArgString(DalvInsn dalvInsn);

    public abstract String insnCommentString(DalvInsn dalvInsn, boolean z);

    public abstract boolean isCompatible(DalvInsn dalvInsn);

    public abstract void writeTo(AnnotatedOutput annotatedOutput, DalvInsn dalvInsn);

    public final String listingString(DalvInsn dalvInsn, boolean z) {
        String name = dalvInsn.getOpcode().getName();
        String insnArgString = insnArgString(dalvInsn);
        String insnCommentString = insnCommentString(dalvInsn, z);
        StringBuilder sb = new StringBuilder(100);
        sb.append(name);
        if (insnArgString.length() != 0) {
            sb.append(' ');
            sb.append(insnArgString);
        }
        if (insnCommentString.length() != 0) {
            sb.append(" // ");
            sb.append(insnCommentString);
        }
        return sb.toString();
    }

    public BitSet compatibleRegs(DalvInsn dalvInsn) {
        return new BitSet();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static String regListString(RegisterSpecList registerSpecList) {
        int size = registerSpecList.size();
        StringBuilder sb = new StringBuilder((size * 5) + 2);
        sb.append('{');
        for (int i = 0; i < size; i++) {
            if (i != 0) {
                sb.append(", ");
            }
            sb.append(registerSpecList.get(i).regString());
        }
        sb.append('}');
        return sb.toString();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static String regRangeString(RegisterSpecList registerSpecList) {
        int size = registerSpecList.size();
        StringBuilder sb = new StringBuilder(30);
        sb.append("{");
        if (size != 0) {
            if (size == 1) {
                sb.append(registerSpecList.get(0).regString());
            } else {
                RegisterSpec registerSpec = registerSpecList.get(size - 1);
                if (registerSpec.getCategory() == 2) {
                    registerSpec = registerSpec.withOffset(1);
                }
                sb.append(registerSpecList.get(0).regString());
                sb.append("..");
                sb.append(registerSpec.regString());
            }
        }
        sb.append("}");
        return sb.toString();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static String literalBitsString(CstLiteralBits cstLiteralBits) {
        StringBuilder sb = new StringBuilder(100);
        sb.append('#');
        if (cstLiteralBits instanceof CstKnownNull) {
            sb.append("null");
        } else {
            sb.append(cstLiteralBits.typeName());
            sb.append(' ');
            sb.append(cstLiteralBits.toHuman());
        }
        return sb.toString();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static String literalBitsComment(CstLiteralBits cstLiteralBits, int i) {
        long intBits;
        StringBuilder sb = new StringBuilder(20);
        sb.append("#");
        if (cstLiteralBits instanceof CstLiteral64) {
            intBits = ((CstLiteral64) cstLiteralBits).getLongBits();
        } else {
            intBits = cstLiteralBits.getIntBits();
        }
        if (i == 4) {
            sb.append(Hex.uNibble((int) intBits));
        } else if (i == 8) {
            sb.append(Hex.u1((int) intBits));
        } else if (i == 16) {
            sb.append(Hex.u2((int) intBits));
        } else if (i == 32) {
            sb.append(Hex.u4((int) intBits));
        } else if (i == 64) {
            sb.append(Hex.u8(intBits));
        } else {
            throw new RuntimeException("shouldn't happen");
        }
        return sb.toString();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static String branchString(DalvInsn dalvInsn) {
        int targetAddress = ((TargetInsn) dalvInsn).getTargetAddress();
        return targetAddress == ((char) targetAddress) ? Hex.u2(targetAddress) : Hex.u4(targetAddress);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static String branchComment(DalvInsn dalvInsn) {
        int targetOffset = ((TargetInsn) dalvInsn).getTargetOffset();
        return targetOffset == ((short) targetOffset) ? Hex.s2(targetOffset) : Hex.s4(targetOffset);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static boolean isRegListSequential(RegisterSpecList registerSpecList) {
        int size = registerSpecList.size();
        if (size < 2) {
            return true;
        }
        int reg = registerSpecList.get(0).getReg();
        for (int i = 0; i < size; i++) {
            RegisterSpec registerSpec = registerSpecList.get(i);
            if (registerSpec.getReg() != reg) {
                return false;
            }
            reg += registerSpec.getCategory();
        }
        return true;
    }

    protected static int argIndex(DalvInsn dalvInsn) {
        int value = ((CstInteger) ((CstInsn) dalvInsn).getConstant()).getValue();
        if (value >= 0) {
            return value;
        }
        throw new IllegalArgumentException("bogus insn");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static short opcodeUnit(DalvInsn dalvInsn, int i) {
        if ((i & 255) != i) {
            throw new IllegalArgumentException("arg out of range 0..255");
        }
        int opcode = dalvInsn.getOpcode().getOpcode();
        if ((opcode & 255) == opcode) {
            return (short) (opcode | (i << 8));
        }
        throw new IllegalArgumentException("opcode out of range 0..255");
    }

    protected static short opcodeUnit(DalvInsn dalvInsn) {
        int opcode = dalvInsn.getOpcode().getOpcode();
        if (opcode < 256 || opcode > 65535) {
            throw new IllegalArgumentException("opcode out of range 0..65535");
        }
        return (short) opcode;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static short codeUnit(int i, int i2) {
        if ((i & 255) == i) {
            if ((i2 & 255) == i2) {
                return (short) (i | (i2 << 8));
            }
            throw new IllegalArgumentException("high out of range 0..255");
        }
        throw new IllegalArgumentException("low out of range 0..255");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static short codeUnit(int i, int i2, int i3, int i4) {
        if ((i & 15) == i) {
            if ((i2 & 15) == i2) {
                if ((i3 & 15) == i3) {
                    if ((i4 & 15) == i4) {
                        return (short) (i | (i2 << 4) | (i3 << 8) | (i4 << 12));
                    }
                    throw new IllegalArgumentException("n3 out of range 0..15");
                }
                throw new IllegalArgumentException("n2 out of range 0..15");
            }
            throw new IllegalArgumentException("n1 out of range 0..15");
        }
        throw new IllegalArgumentException("n0 out of range 0..15");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static int makeByte(int i, int i2) {
        if ((i & 15) == i) {
            if ((i2 & 15) == i2) {
                return i | (i2 << 4);
            }
            throw new IllegalArgumentException("high out of range 0..15");
        }
        throw new IllegalArgumentException("low out of range 0..15");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void write(AnnotatedOutput annotatedOutput, short s) {
        annotatedOutput.writeShort(s);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void write(AnnotatedOutput annotatedOutput, short s, short s2) {
        annotatedOutput.writeShort(s);
        annotatedOutput.writeShort(s2);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void write(AnnotatedOutput annotatedOutput, short s, short s2, short s3) {
        annotatedOutput.writeShort(s);
        annotatedOutput.writeShort(s2);
        annotatedOutput.writeShort(s3);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void write(AnnotatedOutput annotatedOutput, short s, short s2, short s3, short s4) {
        annotatedOutput.writeShort(s);
        annotatedOutput.writeShort(s2);
        annotatedOutput.writeShort(s3);
        annotatedOutput.writeShort(s4);
    }

    protected static void write(AnnotatedOutput annotatedOutput, short s, short s2, short s3, short s4, short s5) {
        annotatedOutput.writeShort(s);
        annotatedOutput.writeShort(s2);
        annotatedOutput.writeShort(s3);
        annotatedOutput.writeShort(s4);
        annotatedOutput.writeShort(s5);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void write(AnnotatedOutput annotatedOutput, short s, int i) {
        write(annotatedOutput, s, (short) i, (short) (i >> 16));
    }

    protected static void write(AnnotatedOutput annotatedOutput, short s, int i, short s2) {
        write(annotatedOutput, s, (short) i, (short) (i >> 16), s2);
    }

    protected static void write(AnnotatedOutput annotatedOutput, short s, int i, short s2, short s3) {
        write(annotatedOutput, s, (short) i, (short) (i >> 16), s2, s3);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void write(AnnotatedOutput annotatedOutput, short s, long j) {
        write(annotatedOutput, s, (short) j, (short) (j >> 16), (short) (j >> 32), (short) (j >> 48));
    }
}

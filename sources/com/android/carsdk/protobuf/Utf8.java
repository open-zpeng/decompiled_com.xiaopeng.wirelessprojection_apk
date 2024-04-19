package com.android.carsdk.protobuf;

import com.google.android.exoplayer2.extractor.ts.PsExtractor;
import com.google.common.base.Ascii;
import java.lang.reflect.Field;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.misc.Unsafe;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class Utf8 {
    private static final long ASCII_MASK_LONG = -9187201950435737472L;
    public static final int COMPLETE = 0;
    public static final int MALFORMED = -1;
    static final int MAX_BYTES_PER_CHAR = 3;
    private static final int UNSAFE_COUNT_ASCII_THRESHOLD = 16;
    private static final Logger logger = Logger.getLogger(Utf8.class.getName());
    private static final Processor processor;

    /* JADX INFO: Access modifiers changed from: private */
    public static int incompleteStateFor(int i) {
        if (i > -12) {
            return -1;
        }
        return i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int incompleteStateFor(int i, int i2) {
        if (i > -12 || i2 > -65) {
            return -1;
        }
        return i ^ (i2 << 8);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int incompleteStateFor(int i, int i2, int i3) {
        if (i > -12 || i2 > -65 || i3 > -65) {
            return -1;
        }
        return (i ^ (i2 << 8)) ^ (i3 << 16);
    }

    static {
        processor = UnsafeProcessor.isAvailable() ? new UnsafeProcessor() : new SafeProcessor();
    }

    public static boolean isValidUtf8(byte[] bArr) {
        return processor.isValidUtf8(bArr, 0, bArr.length);
    }

    public static boolean isValidUtf8(byte[] bArr, int i, int i2) {
        return processor.isValidUtf8(bArr, i, i2);
    }

    public static int partialIsValidUtf8(int i, byte[] bArr, int i2, int i3) {
        return processor.partialIsValidUtf8(i, bArr, i2, i3);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int incompleteStateFor(byte[] bArr, int i, int i2) {
        byte b = bArr[i - 1];
        int i3 = i2 - i;
        if (i3 != 0) {
            if (i3 != 1) {
                if (i3 == 2) {
                    return incompleteStateFor(b, bArr[i], bArr[i + 1]);
                }
                throw new AssertionError();
            }
            return incompleteStateFor(b, bArr[i]);
        }
        return incompleteStateFor(b);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int incompleteStateFor(ByteBuffer byteBuffer, int i, int i2, int i3) {
        if (i3 != 0) {
            if (i3 != 1) {
                if (i3 == 2) {
                    return incompleteStateFor(i, byteBuffer.get(i2), byteBuffer.get(i2 + 1));
                }
                throw new AssertionError();
            }
            return incompleteStateFor(i, byteBuffer.get(i2));
        }
        return incompleteStateFor(i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class UnpairedSurrogateException extends IllegalArgumentException {
        private UnpairedSurrogateException(int i, int i2) {
            super("Unpaired surrogate at index " + i + " of " + i2);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int encodedLength(CharSequence charSequence) {
        int length = charSequence.length();
        int i = 0;
        while (i < length && charSequence.charAt(i) < 128) {
            i++;
        }
        int i2 = length;
        while (true) {
            if (i < length) {
                char charAt = charSequence.charAt(i);
                if (charAt >= 2048) {
                    i2 += encodedLengthGeneral(charSequence, i);
                    break;
                }
                i2 += (127 - charAt) >>> 31;
                i++;
            } else {
                break;
            }
        }
        if (i2 >= length) {
            return i2;
        }
        throw new IllegalArgumentException("UTF-8 length does not fit in int: " + (i2 + 4294967296L));
    }

    private static int encodedLengthGeneral(CharSequence charSequence, int i) {
        int length = charSequence.length();
        int i2 = 0;
        while (i < length) {
            char charAt = charSequence.charAt(i);
            if (charAt < 2048) {
                i2 += (127 - charAt) >>> 31;
            } else {
                i2 += 2;
                if (55296 <= charAt && charAt <= 57343) {
                    if (Character.codePointAt(charSequence, i) < 65536) {
                        throw new UnpairedSurrogateException(i, length);
                    }
                    i++;
                }
            }
            i++;
        }
        return i2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int encode(CharSequence charSequence, byte[] bArr, int i, int i2) {
        return processor.encodeUtf8(charSequence, bArr, i, i2);
    }

    static boolean isValidUtf8(ByteBuffer byteBuffer) {
        return processor.isValidUtf8(byteBuffer, byteBuffer.position(), byteBuffer.remaining());
    }

    static int partialIsValidUtf8(int i, ByteBuffer byteBuffer, int i2, int i3) {
        return processor.partialIsValidUtf8(i, byteBuffer, i2, i3);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void encodeUtf8(CharSequence charSequence, ByteBuffer byteBuffer) {
        processor.encodeUtf8(charSequence, byteBuffer);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int estimateConsecutiveAscii(ByteBuffer byteBuffer, int i, int i2) {
        int i3 = i2 - 7;
        int i4 = i;
        while (i4 < i3 && (byteBuffer.getLong(i4) & ASCII_MASK_LONG) == 0) {
            i4 += 8;
        }
        return i4 - i;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static abstract class Processor {
        abstract int encodeUtf8(CharSequence charSequence, byte[] bArr, int i, int i2);

        abstract void encodeUtf8Direct(CharSequence charSequence, ByteBuffer byteBuffer);

        abstract int partialIsValidUtf8(int i, byte[] bArr, int i2, int i3);

        abstract int partialIsValidUtf8Direct(int i, ByteBuffer byteBuffer, int i2, int i3);

        Processor() {
        }

        final boolean isValidUtf8(byte[] bArr, int i, int i2) {
            return partialIsValidUtf8(0, bArr, i, i2) == 0;
        }

        final boolean isValidUtf8(ByteBuffer byteBuffer, int i, int i2) {
            return partialIsValidUtf8(0, byteBuffer, i, i2) == 0;
        }

        final int partialIsValidUtf8(int i, ByteBuffer byteBuffer, int i2, int i3) {
            if (byteBuffer.hasArray()) {
                int arrayOffset = byteBuffer.arrayOffset();
                return partialIsValidUtf8(i, byteBuffer.array(), i2 + arrayOffset, arrayOffset + i3);
            } else if (byteBuffer.isDirect()) {
                return partialIsValidUtf8Direct(i, byteBuffer, i2, i3);
            } else {
                return partialIsValidUtf8Default(i, byteBuffer, i2, i3);
            }
        }

        /* JADX WARN: Code restructure failed: missing block: B:10:0x0017, code lost:
            if (r8.get(r9) > (-65)) goto L12;
         */
        /* JADX WARN: Code restructure failed: missing block: B:31:0x004c, code lost:
            if (r8.get(r9) > (-65)) goto L31;
         */
        /* JADX WARN: Code restructure failed: missing block: B:52:0x008b, code lost:
            if (r8.get(r9) > (-65)) goto L51;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        final int partialIsValidUtf8Default(int r7, java.nio.ByteBuffer r8, int r9, int r10) {
            /*
                r6 = this;
                if (r7 == 0) goto L8e
                if (r9 < r10) goto L5
                return r7
            L5:
                byte r0 = (byte) r7
                r1 = -32
                r2 = -1
                r3 = -65
                if (r0 >= r1) goto L1e
                r7 = -62
                if (r0 < r7) goto L1d
                int r7 = r9 + 1
                byte r9 = r8.get(r9)
                if (r9 <= r3) goto L1a
                goto L1d
            L1a:
                r9 = r7
                goto L8e
            L1d:
                return r2
            L1e:
                r4 = -16
                if (r0 >= r4) goto L4f
                int r7 = r7 >> 8
                int r7 = ~r7
                byte r7 = (byte) r7
                if (r7 != 0) goto L38
                int r7 = r9 + 1
                byte r9 = r8.get(r9)
                if (r7 < r10) goto L35
                int r7 = com.android.carsdk.protobuf.Utf8.access$100(r0, r9)
                return r7
            L35:
                r5 = r9
                r9 = r7
                r7 = r5
            L38:
                if (r7 > r3) goto L4e
                r4 = -96
                if (r0 != r1) goto L40
                if (r7 < r4) goto L4e
            L40:
                r1 = -19
                if (r0 != r1) goto L46
                if (r7 >= r4) goto L4e
            L46:
                int r7 = r9 + 1
                byte r9 = r8.get(r9)
                if (r9 <= r3) goto L1a
            L4e:
                return r2
            L4f:
                int r1 = r7 >> 8
                int r1 = ~r1
                byte r1 = (byte) r1
                r4 = 0
                if (r1 != 0) goto L65
                int r7 = r9 + 1
                byte r1 = r8.get(r9)
                if (r7 < r10) goto L63
                int r7 = com.android.carsdk.protobuf.Utf8.access$100(r0, r1)
                return r7
            L63:
                r9 = r7
                goto L68
            L65:
                int r7 = r7 >> 16
                byte r4 = (byte) r7
            L68:
                if (r4 != 0) goto L78
                int r7 = r9 + 1
                byte r4 = r8.get(r9)
                if (r7 < r10) goto L77
                int r7 = com.android.carsdk.protobuf.Utf8.access$200(r0, r1, r4)
                return r7
            L77:
                r9 = r7
            L78:
                if (r1 > r3) goto L8d
                int r7 = r0 << 28
                int r1 = r1 + 112
                int r7 = r7 + r1
                int r7 = r7 >> 30
                if (r7 != 0) goto L8d
                if (r4 > r3) goto L8d
                int r7 = r9 + 1
                byte r9 = r8.get(r9)
                if (r9 <= r3) goto L1a
            L8d:
                return r2
            L8e:
                int r7 = partialIsValidUtf8(r8, r9, r10)
                return r7
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.carsdk.protobuf.Utf8.Processor.partialIsValidUtf8Default(int, java.nio.ByteBuffer, int, int):int");
        }

        private static int partialIsValidUtf8(ByteBuffer byteBuffer, int i, int i2) {
            int estimateConsecutiveAscii = i + Utf8.estimateConsecutiveAscii(byteBuffer, i, i2);
            while (estimateConsecutiveAscii < i2) {
                int i3 = estimateConsecutiveAscii + 1;
                byte b = byteBuffer.get(estimateConsecutiveAscii);
                if (b < 0) {
                    if (b < -32) {
                        if (i3 >= i2) {
                            return b;
                        }
                        if (b < -62 || byteBuffer.get(i3) > -65) {
                            return -1;
                        }
                        i3++;
                    } else if (b >= -16) {
                        if (i3 >= i2 - 2) {
                            return Utf8.incompleteStateFor(byteBuffer, b, i3, i2 - i3);
                        }
                        int i4 = i3 + 1;
                        byte b2 = byteBuffer.get(i3);
                        if (b2 <= -65 && (((b << Ascii.FS) + (b2 + 112)) >> 30) == 0) {
                            int i5 = i4 + 1;
                            if (byteBuffer.get(i4) <= -65) {
                                i3 = i5 + 1;
                                if (byteBuffer.get(i5) > -65) {
                                }
                            }
                        }
                        return -1;
                    } else if (i3 >= i2 - 1) {
                        return Utf8.incompleteStateFor(byteBuffer, b, i3, i2 - i3);
                    } else {
                        int i6 = i3 + 1;
                        byte b3 = byteBuffer.get(i3);
                        if (b3 > -65 || ((b == -32 && b3 < -96) || ((b == -19 && b3 >= -96) || byteBuffer.get(i6) > -65))) {
                            return -1;
                        }
                        estimateConsecutiveAscii = i6 + 1;
                    }
                }
                estimateConsecutiveAscii = i3;
            }
            return 0;
        }

        final void encodeUtf8(CharSequence charSequence, ByteBuffer byteBuffer) {
            if (byteBuffer.hasArray()) {
                int arrayOffset = byteBuffer.arrayOffset();
                byteBuffer.position(Utf8.encode(charSequence, byteBuffer.array(), byteBuffer.position() + arrayOffset, byteBuffer.remaining()) - arrayOffset);
            } else if (byteBuffer.isDirect()) {
                encodeUtf8Direct(charSequence, byteBuffer);
            } else {
                encodeUtf8Default(charSequence, byteBuffer);
            }
        }

        final void encodeUtf8Default(CharSequence charSequence, ByteBuffer byteBuffer) {
            int length = charSequence.length();
            int position = byteBuffer.position();
            int i = 0;
            while (i < length) {
                try {
                    char charAt = charSequence.charAt(i);
                    if (charAt >= 128) {
                        break;
                    }
                    byteBuffer.put(position + i, (byte) charAt);
                    i++;
                } catch (IndexOutOfBoundsException unused) {
                    throw new ArrayIndexOutOfBoundsException("Failed writing " + charSequence.charAt(i) + " at index " + (byteBuffer.position() + Math.max(i, (position - byteBuffer.position()) + 1)));
                }
            }
            if (i == length) {
                byteBuffer.position(position + i);
                return;
            }
            position += i;
            while (i < length) {
                char charAt2 = charSequence.charAt(i);
                if (charAt2 < 128) {
                    byteBuffer.put(position, (byte) charAt2);
                } else if (charAt2 < 2048) {
                    int i2 = position + 1;
                    try {
                        byteBuffer.put(position, (byte) ((charAt2 >>> 6) | 192));
                        byteBuffer.put(i2, (byte) ((charAt2 & '?') | 128));
                        position = i2;
                    } catch (IndexOutOfBoundsException unused2) {
                        position = i2;
                        throw new ArrayIndexOutOfBoundsException("Failed writing " + charSequence.charAt(i) + " at index " + (byteBuffer.position() + Math.max(i, (position - byteBuffer.position()) + 1)));
                    }
                } else if (charAt2 < 55296 || 57343 < charAt2) {
                    int i3 = position + 1;
                    byteBuffer.put(position, (byte) ((charAt2 >>> '\f') | 224));
                    position = i3 + 1;
                    byteBuffer.put(i3, (byte) (((charAt2 >>> 6) & 63) | 128));
                    byteBuffer.put(position, (byte) ((charAt2 & '?') | 128));
                } else {
                    int i4 = i + 1;
                    if (i4 != length) {
                        try {
                            char charAt3 = charSequence.charAt(i4);
                            if (Character.isSurrogatePair(charAt2, charAt3)) {
                                int codePoint = Character.toCodePoint(charAt2, charAt3);
                                int i5 = position + 1;
                                try {
                                    byteBuffer.put(position, (byte) ((codePoint >>> 18) | PsExtractor.VIDEO_STREAM_MASK));
                                    int i6 = i5 + 1;
                                    byteBuffer.put(i5, (byte) (((codePoint >>> 12) & 63) | 128));
                                    int i7 = i6 + 1;
                                    byteBuffer.put(i6, (byte) (((codePoint >>> 6) & 63) | 128));
                                    byteBuffer.put(i7, (byte) ((codePoint & 63) | 128));
                                    position = i7;
                                    i = i4;
                                } catch (IndexOutOfBoundsException unused3) {
                                    position = i5;
                                    i = i4;
                                    throw new ArrayIndexOutOfBoundsException("Failed writing " + charSequence.charAt(i) + " at index " + (byteBuffer.position() + Math.max(i, (position - byteBuffer.position()) + 1)));
                                }
                            } else {
                                i = i4;
                            }
                        } catch (IndexOutOfBoundsException unused4) {
                        }
                    }
                    throw new UnpairedSurrogateException(i, length);
                }
                i++;
                position++;
            }
            byteBuffer.position(position);
        }
    }

    /* loaded from: classes.dex */
    static final class SafeProcessor extends Processor {
        SafeProcessor() {
        }

        /* JADX WARN: Code restructure failed: missing block: B:10:0x0015, code lost:
            if (r8[r9] > (-65)) goto L12;
         */
        /* JADX WARN: Code restructure failed: missing block: B:31:0x0046, code lost:
            if (r8[r9] > (-65)) goto L31;
         */
        /* JADX WARN: Code restructure failed: missing block: B:52:0x007f, code lost:
            if (r8[r9] > (-65)) goto L51;
         */
        @Override // com.android.carsdk.protobuf.Utf8.Processor
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        int partialIsValidUtf8(int r7, byte[] r8, int r9, int r10) {
            /*
                r6 = this;
                if (r7 == 0) goto L82
                if (r9 < r10) goto L5
                return r7
            L5:
                byte r0 = (byte) r7
                r1 = -32
                r2 = -1
                r3 = -65
                if (r0 >= r1) goto L1c
                r7 = -62
                if (r0 < r7) goto L1b
                int r7 = r9 + 1
                r9 = r8[r9]
                if (r9 <= r3) goto L18
                goto L1b
            L18:
                r9 = r7
                goto L82
            L1b:
                return r2
            L1c:
                r4 = -16
                if (r0 >= r4) goto L49
                int r7 = r7 >> 8
                int r7 = ~r7
                byte r7 = (byte) r7
                if (r7 != 0) goto L34
                int r7 = r9 + 1
                r9 = r8[r9]
                if (r7 < r10) goto L31
                int r7 = com.android.carsdk.protobuf.Utf8.access$100(r0, r9)
                return r7
            L31:
                r5 = r9
                r9 = r7
                r7 = r5
            L34:
                if (r7 > r3) goto L48
                r4 = -96
                if (r0 != r1) goto L3c
                if (r7 < r4) goto L48
            L3c:
                r1 = -19
                if (r0 != r1) goto L42
                if (r7 >= r4) goto L48
            L42:
                int r7 = r9 + 1
                r9 = r8[r9]
                if (r9 <= r3) goto L18
            L48:
                return r2
            L49:
                int r1 = r7 >> 8
                int r1 = ~r1
                byte r1 = (byte) r1
                r4 = 0
                if (r1 != 0) goto L5d
                int r7 = r9 + 1
                r1 = r8[r9]
                if (r7 < r10) goto L5b
                int r7 = com.android.carsdk.protobuf.Utf8.access$100(r0, r1)
                return r7
            L5b:
                r9 = r7
                goto L60
            L5d:
                int r7 = r7 >> 16
                byte r4 = (byte) r7
            L60:
                if (r4 != 0) goto L6e
                int r7 = r9 + 1
                r4 = r8[r9]
                if (r7 < r10) goto L6d
                int r7 = com.android.carsdk.protobuf.Utf8.access$200(r0, r1, r4)
                return r7
            L6d:
                r9 = r7
            L6e:
                if (r1 > r3) goto L81
                int r7 = r0 << 28
                int r1 = r1 + 112
                int r7 = r7 + r1
                int r7 = r7 >> 30
                if (r7 != 0) goto L81
                if (r4 > r3) goto L81
                int r7 = r9 + 1
                r9 = r8[r9]
                if (r9 <= r3) goto L18
            L81:
                return r2
            L82:
                int r7 = partialIsValidUtf8(r8, r9, r10)
                return r7
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.carsdk.protobuf.Utf8.SafeProcessor.partialIsValidUtf8(int, byte[], int, int):int");
        }

        @Override // com.android.carsdk.protobuf.Utf8.Processor
        int partialIsValidUtf8Direct(int i, ByteBuffer byteBuffer, int i2, int i3) {
            return partialIsValidUtf8Default(i, byteBuffer, i2, i3);
        }

        /* JADX WARN: Code restructure failed: missing block: B:12:0x001d, code lost:
            return r11 + r0;
         */
        @Override // com.android.carsdk.protobuf.Utf8.Processor
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        int encodeUtf8(java.lang.CharSequence r9, byte[] r10, int r11, int r12) {
            /*
                Method dump skipped, instructions count: 259
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.carsdk.protobuf.Utf8.SafeProcessor.encodeUtf8(java.lang.CharSequence, byte[], int, int):int");
        }

        @Override // com.android.carsdk.protobuf.Utf8.Processor
        void encodeUtf8Direct(CharSequence charSequence, ByteBuffer byteBuffer) {
            encodeUtf8Default(charSequence, byteBuffer);
        }

        private static int partialIsValidUtf8(byte[] bArr, int i, int i2) {
            while (i < i2 && bArr[i] >= 0) {
                i++;
            }
            if (i >= i2) {
                return 0;
            }
            return partialIsValidUtf8NonAscii(bArr, i, i2);
        }

        private static int partialIsValidUtf8NonAscii(byte[] bArr, int i, int i2) {
            while (i < i2) {
                int i3 = i + 1;
                byte b = bArr[i];
                if (b < 0) {
                    if (b < -32) {
                        if (i3 >= i2) {
                            return b;
                        }
                        if (b >= -62) {
                            i = i3 + 1;
                            if (bArr[i3] > -65) {
                            }
                        }
                        return -1;
                    } else if (b >= -16) {
                        if (i3 >= i2 - 2) {
                            return Utf8.incompleteStateFor(bArr, i3, i2);
                        }
                        int i4 = i3 + 1;
                        byte b2 = bArr[i3];
                        if (b2 <= -65 && (((b << Ascii.FS) + (b2 + 112)) >> 30) == 0) {
                            int i5 = i4 + 1;
                            if (bArr[i4] <= -65) {
                                i3 = i5 + 1;
                                if (bArr[i5] > -65) {
                                }
                            }
                        }
                        return -1;
                    } else if (i3 >= i2 - 1) {
                        return Utf8.incompleteStateFor(bArr, i3, i2);
                    } else {
                        int i6 = i3 + 1;
                        byte b3 = bArr[i3];
                        if (b3 <= -65 && ((b != -32 || b3 >= -96) && (b != -19 || b3 < -96))) {
                            i = i6 + 1;
                            if (bArr[i6] > -65) {
                            }
                        }
                        return -1;
                    }
                }
                i = i3;
            }
            return 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class UnsafeProcessor extends Processor {
        private static final int ARRAY_BASE_OFFSET;
        private static final boolean AVAILABLE;
        private static final long BUFFER_ADDRESS_OFFSET;
        private static final Unsafe UNSAFE = getUnsafe();

        UnsafeProcessor() {
        }

        static {
            long fieldOffset = fieldOffset(field(Buffer.class, "address"));
            BUFFER_ADDRESS_OFFSET = fieldOffset;
            int byteArrayBaseOffset = byteArrayBaseOffset();
            ARRAY_BASE_OFFSET = byteArrayBaseOffset;
            AVAILABLE = fieldOffset != -1 && byteArrayBaseOffset % 8 == 0;
        }

        static boolean isAvailable() {
            return AVAILABLE;
        }

        /* JADX WARN: Code restructure failed: missing block: B:35:0x0063, code lost:
            if (com.android.carsdk.protobuf.Utf8.UnsafeProcessor.UNSAFE.getByte(r13, r2) > (-65)) goto L36;
         */
        /* JADX WARN: Code restructure failed: missing block: B:58:0x00ae, code lost:
            if (com.android.carsdk.protobuf.Utf8.UnsafeProcessor.UNSAFE.getByte(r13, r2) > (-65)) goto L56;
         */
        @Override // com.android.carsdk.protobuf.Utf8.Processor
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        int partialIsValidUtf8(int r12, byte[] r13, int r14, int r15) {
            /*
                Method dump skipped, instructions count: 220
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.carsdk.protobuf.Utf8.UnsafeProcessor.partialIsValidUtf8(int, byte[], int, int):int");
        }

        /* JADX WARN: Code restructure failed: missing block: B:35:0x0069, code lost:
            if (com.android.carsdk.protobuf.Utf8.UnsafeProcessor.UNSAFE.getByte(r2) > (-65)) goto L36;
         */
        /* JADX WARN: Code restructure failed: missing block: B:58:0x00b4, code lost:
            if (com.android.carsdk.protobuf.Utf8.UnsafeProcessor.UNSAFE.getByte(r2) > (-65)) goto L56;
         */
        @Override // com.android.carsdk.protobuf.Utf8.Processor
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        int partialIsValidUtf8Direct(int r11, java.nio.ByteBuffer r12, int r13, int r14) {
            /*
                Method dump skipped, instructions count: 229
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.carsdk.protobuf.Utf8.UnsafeProcessor.partialIsValidUtf8Direct(int, java.nio.ByteBuffer, int, int):int");
        }

        @Override // com.android.carsdk.protobuf.Utf8.Processor
        int encodeUtf8(CharSequence charSequence, byte[] bArr, int i, int i2) {
            char c;
            long j;
            int i3;
            String str;
            String str2;
            long j2;
            long j3;
            int i4;
            char c2;
            char charAt;
            long j4 = ARRAY_BASE_OFFSET + i;
            long j5 = i2 + j4;
            int length = charSequence.length();
            String str3 = " at index ";
            String str4 = "Failed writing ";
            if (length > i2 || bArr.length - i2 < i) {
                throw new ArrayIndexOutOfBoundsException("Failed writing " + charSequence.charAt(length - 1) + " at index " + (i + i2));
            }
            int i5 = 0;
            while (true) {
                c = 128;
                j = 1;
                if (i5 >= length || (charAt = charSequence.charAt(i5)) >= 128) {
                    break;
                }
                UNSAFE.putByte(bArr, j4, (byte) charAt);
                i5++;
                j4 = 1 + j4;
            }
            if (i5 == length) {
                i3 = ARRAY_BASE_OFFSET;
            } else {
                while (i5 < length) {
                    char charAt2 = charSequence.charAt(i5);
                    if (charAt2 >= c || j4 >= j5) {
                        if (charAt2 >= 2048 || j4 > j5 - 2) {
                            str = str3;
                            str2 = str4;
                            if ((charAt2 >= 55296 && 57343 >= charAt2) || j4 > j5 - 3) {
                                if (j4 <= j5 - 4) {
                                    int i6 = i5 + 1;
                                    if (i6 != length) {
                                        char charAt3 = charSequence.charAt(i6);
                                        if (Character.isSurrogatePair(charAt2, charAt3)) {
                                            int codePoint = Character.toCodePoint(charAt2, charAt3);
                                            Unsafe unsafe = UNSAFE;
                                            long j6 = j4 + 1;
                                            unsafe.putByte(bArr, j4, (byte) ((codePoint >>> 18) | PsExtractor.VIDEO_STREAM_MASK));
                                            long j7 = j6 + 1;
                                            c2 = 128;
                                            unsafe.putByte(bArr, j6, (byte) (((codePoint >>> 12) & 63) | 128));
                                            long j8 = j7 + 1;
                                            unsafe.putByte(bArr, j7, (byte) (((codePoint >>> 6) & 63) | 128));
                                            j3 = 1;
                                            j2 = j8 + 1;
                                            unsafe.putByte(bArr, j8, (byte) ((codePoint & 63) | 128));
                                            i5 = i6;
                                        } else {
                                            i5 = i6;
                                        }
                                    }
                                    throw new UnpairedSurrogateException(i5 - 1, length);
                                } else if (55296 <= charAt2 && charAt2 <= 57343 && ((i4 = i5 + 1) == length || !Character.isSurrogatePair(charAt2, charSequence.charAt(i4)))) {
                                    throw new UnpairedSurrogateException(i5, length);
                                } else {
                                    throw new ArrayIndexOutOfBoundsException(str2 + charAt2 + str + j4);
                                }
                            }
                            Unsafe unsafe2 = UNSAFE;
                            long j9 = j4 + j;
                            unsafe2.putByte(bArr, j4, (byte) ((charAt2 >>> '\f') | 480));
                            long j10 = j9 + j;
                            unsafe2.putByte(bArr, j9, (byte) (((charAt2 >>> 6) & 63) | 128));
                            unsafe2.putByte(bArr, j10, (byte) ((charAt2 & '?') | 128));
                            j2 = j10 + 1;
                            j3 = 1;
                        } else {
                            Unsafe unsafe3 = UNSAFE;
                            str = str3;
                            str2 = str4;
                            long j11 = j4 + j;
                            unsafe3.putByte(bArr, j4, (byte) ((charAt2 >>> 6) | 960));
                            unsafe3.putByte(bArr, j11, (byte) ((charAt2 & '?') | 128));
                            j2 = j11 + j;
                            j3 = j;
                        }
                        c2 = 128;
                    } else {
                        long j12 = j4 + j;
                        UNSAFE.putByte(bArr, j4, (byte) charAt2);
                        j3 = j;
                        j2 = j12;
                        c2 = c;
                        str = str3;
                        str2 = str4;
                    }
                    i5++;
                    c = c2;
                    str3 = str;
                    str4 = str2;
                    j = j3;
                    j4 = j2;
                }
                i3 = ARRAY_BASE_OFFSET;
            }
            return (int) (j4 - i3);
        }

        @Override // com.android.carsdk.protobuf.Utf8.Processor
        void encodeUtf8Direct(CharSequence charSequence, ByteBuffer byteBuffer) {
            char c;
            long j;
            long j2;
            long j3;
            int i;
            char c2;
            char charAt;
            long addressOffset = addressOffset(byteBuffer);
            long position = byteBuffer.position() + addressOffset;
            long limit = byteBuffer.limit() + addressOffset;
            int length = charSequence.length();
            if (length > limit - position) {
                throw new ArrayIndexOutOfBoundsException("Failed writing " + charSequence.charAt(length - 1) + " at index " + byteBuffer.limit());
            }
            int i2 = 0;
            while (true) {
                c = 128;
                j = 1;
                if (i2 >= length || (charAt = charSequence.charAt(i2)) >= 128) {
                    break;
                }
                UNSAFE.putByte(position, (byte) charAt);
                i2++;
                position = 1 + position;
            }
            if (i2 == length) {
                byteBuffer.position((int) (position - addressOffset));
                return;
            }
            while (i2 < length) {
                char charAt2 = charSequence.charAt(i2);
                if (charAt2 >= c || position >= limit) {
                    if (charAt2 >= 2048 || position > limit - 2) {
                        j2 = addressOffset;
                        if ((charAt2 >= 55296 && 57343 >= charAt2) || position > limit - 3) {
                            if (position <= limit - 4) {
                                int i3 = i2 + 1;
                                if (i3 != length) {
                                    char charAt3 = charSequence.charAt(i3);
                                    if (Character.isSurrogatePair(charAt2, charAt3)) {
                                        int codePoint = Character.toCodePoint(charAt2, charAt3);
                                        Unsafe unsafe = UNSAFE;
                                        j3 = limit;
                                        j = 1;
                                        long j4 = position + 1;
                                        unsafe.putByte(position, (byte) ((codePoint >>> 18) | PsExtractor.VIDEO_STREAM_MASK));
                                        long j5 = j4 + 1;
                                        c2 = 128;
                                        unsafe.putByte(j4, (byte) (((codePoint >>> 12) & 63) | 128));
                                        long j6 = j5 + 1;
                                        unsafe.putByte(j5, (byte) (((codePoint >>> 6) & 63) | 128));
                                        position = j6 + 1;
                                        unsafe.putByte(j6, (byte) ((codePoint & 63) | 128));
                                        i2 = i3;
                                    } else {
                                        i2 = i3;
                                    }
                                }
                                throw new UnpairedSurrogateException(i2 - 1, length);
                            } else if (55296 <= charAt2 && charAt2 <= 57343 && ((i = i2 + 1) == length || !Character.isSurrogatePair(charAt2, charSequence.charAt(i)))) {
                                throw new UnpairedSurrogateException(i2, length);
                            } else {
                                throw new ArrayIndexOutOfBoundsException("Failed writing " + charAt2 + " at index " + position);
                            }
                        }
                        Unsafe unsafe2 = UNSAFE;
                        long j7 = position + j;
                        unsafe2.putByte(position, (byte) ((charAt2 >>> '\f') | 480));
                        long j8 = j7 + j;
                        unsafe2.putByte(j7, (byte) (((charAt2 >>> 6) & 63) | 128));
                        unsafe2.putByte(j8, (byte) ((charAt2 & '?') | 128));
                        j3 = limit;
                        position = j8 + 1;
                        j = 1;
                    } else {
                        Unsafe unsafe3 = UNSAFE;
                        j2 = addressOffset;
                        long j9 = position + j;
                        unsafe3.putByte(position, (byte) ((charAt2 >>> 6) | 960));
                        unsafe3.putByte(j9, (byte) ((charAt2 & '?') | 128));
                        position = j9 + j;
                        j3 = limit;
                    }
                    c2 = 128;
                } else {
                    UNSAFE.putByte(position, (byte) charAt2);
                    j3 = limit;
                    position += j;
                    c2 = 128;
                    j2 = addressOffset;
                }
                i2++;
                c = c2;
                addressOffset = j2;
                limit = j3;
            }
            byteBuffer.position((int) (position - addressOffset));
        }

        private static int unsafeEstimateConsecutiveAscii(byte[] bArr, long j, int i) {
            if (i < 16) {
                return 0;
            }
            int i2 = ((int) j) & 7;
            int i3 = i2;
            while (i3 > 0) {
                long j2 = 1 + j;
                if (UNSAFE.getByte(bArr, j) < 0) {
                    return i2 - i3;
                }
                i3--;
                j = j2;
            }
            int i4 = i - i2;
            while (i4 >= 8 && (UNSAFE.getLong(bArr, j) & Utf8.ASCII_MASK_LONG) == 0) {
                j += 8;
                i4 -= 8;
            }
            return i - i4;
        }

        private static int unsafeEstimateConsecutiveAscii(long j, int i) {
            if (i < 16) {
                return 0;
            }
            int i2 = ((int) j) & 7;
            int i3 = i2;
            while (i3 > 0) {
                long j2 = 1 + j;
                if (UNSAFE.getByte(j) < 0) {
                    return i2 - i3;
                }
                i3--;
                j = j2;
            }
            int i4 = i - i2;
            while (i4 >= 8 && (UNSAFE.getLong(j) & Utf8.ASCII_MASK_LONG) == 0) {
                j += 8;
                i4 -= 8;
            }
            return i - i4;
        }

        /* JADX WARN: Code restructure failed: missing block: B:22:0x003d, code lost:
            return -1;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        private static int partialIsValidUtf8(byte[] r9, long r10, int r12) {
            /*
                int r0 = unsafeEstimateConsecutiveAscii(r9, r10, r12)
                int r12 = r12 - r0
                long r0 = (long) r0
                long r10 = r10 + r0
            L7:
                r0 = 0
                r1 = r0
            L9:
                r2 = 1
                if (r12 <= 0) goto L1c
                sun.misc.Unsafe r1 = com.android.carsdk.protobuf.Utf8.UnsafeProcessor.UNSAFE
                long r4 = r10 + r2
                byte r1 = r1.getByte(r9, r10)
                if (r1 < 0) goto L1b
                int r12 = r12 + (-1)
                r10 = r4
                goto L9
            L1b:
                r10 = r4
            L1c:
                if (r12 != 0) goto L1f
                return r0
            L1f:
                int r12 = r12 + (-1)
                r0 = -32
                r4 = -65
                r5 = -1
                if (r1 >= r0) goto L3e
                if (r12 != 0) goto L2b
                return r1
            L2b:
                int r12 = r12 + (-1)
                r0 = -62
                if (r1 < r0) goto L3d
                sun.misc.Unsafe r0 = com.android.carsdk.protobuf.Utf8.UnsafeProcessor.UNSAFE
                long r2 = r2 + r10
                byte r10 = r0.getByte(r9, r10)
                if (r10 <= r4) goto L3b
                goto L3d
            L3b:
                r10 = r2
                goto L7
            L3d:
                return r5
            L3e:
                r6 = -16
                if (r1 >= r6) goto L6a
                r6 = 2
                if (r12 >= r6) goto L4a
                int r9 = unsafeIncompleteStateFor(r9, r1, r10, r12)
                return r9
            L4a:
                int r12 = r12 + (-2)
                sun.misc.Unsafe r6 = com.android.carsdk.protobuf.Utf8.UnsafeProcessor.UNSAFE
                long r7 = r10 + r2
                byte r10 = r6.getByte(r9, r10)
                if (r10 > r4) goto L69
                r11 = -96
                if (r1 != r0) goto L5c
                if (r10 < r11) goto L69
            L5c:
                r0 = -19
                if (r1 != r0) goto L62
                if (r10 >= r11) goto L69
            L62:
                long r2 = r2 + r7
                byte r10 = r6.getByte(r9, r7)
                if (r10 <= r4) goto L3b
            L69:
                return r5
            L6a:
                r0 = 3
                if (r12 >= r0) goto L72
                int r9 = unsafeIncompleteStateFor(r9, r1, r10, r12)
                return r9
            L72:
                int r12 = r12 + (-3)
                sun.misc.Unsafe r0 = com.android.carsdk.protobuf.Utf8.UnsafeProcessor.UNSAFE
                long r6 = r10 + r2
                byte r10 = r0.getByte(r9, r10)
                if (r10 > r4) goto L96
                int r11 = r1 << 28
                int r10 = r10 + 112
                int r11 = r11 + r10
                int r10 = r11 >> 30
                if (r10 != 0) goto L96
                long r10 = r6 + r2
                byte r1 = r0.getByte(r9, r6)
                if (r1 > r4) goto L96
                long r2 = r2 + r10
                byte r10 = r0.getByte(r9, r10)
                if (r10 <= r4) goto L3b
            L96:
                return r5
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.carsdk.protobuf.Utf8.UnsafeProcessor.partialIsValidUtf8(byte[], long, int):int");
        }

        /* JADX WARN: Code restructure failed: missing block: B:22:0x003d, code lost:
            return -1;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        private static int partialIsValidUtf8(long r9, int r11) {
            /*
                int r0 = unsafeEstimateConsecutiveAscii(r9, r11)
                long r1 = (long) r0
                long r9 = r9 + r1
                int r11 = r11 - r0
            L7:
                r0 = 0
                r1 = r0
            L9:
                r2 = 1
                if (r11 <= 0) goto L1c
                sun.misc.Unsafe r1 = com.android.carsdk.protobuf.Utf8.UnsafeProcessor.UNSAFE
                long r4 = r9 + r2
                byte r1 = r1.getByte(r9)
                if (r1 < 0) goto L1b
                int r11 = r11 + (-1)
                r9 = r4
                goto L9
            L1b:
                r9 = r4
            L1c:
                if (r11 != 0) goto L1f
                return r0
            L1f:
                int r11 = r11 + (-1)
                r0 = -32
                r4 = -65
                r5 = -1
                if (r1 >= r0) goto L3e
                if (r11 != 0) goto L2b
                return r1
            L2b:
                int r11 = r11 + (-1)
                r0 = -62
                if (r1 < r0) goto L3d
                sun.misc.Unsafe r0 = com.android.carsdk.protobuf.Utf8.UnsafeProcessor.UNSAFE
                long r2 = r2 + r9
                byte r9 = r0.getByte(r9)
                if (r9 <= r4) goto L3b
                goto L3d
            L3b:
                r9 = r2
                goto L7
            L3d:
                return r5
            L3e:
                r6 = -16
                if (r1 >= r6) goto L6a
                r6 = 2
                if (r11 >= r6) goto L4a
                int r9 = unsafeIncompleteStateFor(r9, r1, r11)
                return r9
            L4a:
                int r11 = r11 + (-2)
                sun.misc.Unsafe r6 = com.android.carsdk.protobuf.Utf8.UnsafeProcessor.UNSAFE
                long r7 = r9 + r2
                byte r9 = r6.getByte(r9)
                if (r9 > r4) goto L69
                r10 = -96
                if (r1 != r0) goto L5c
                if (r9 < r10) goto L69
            L5c:
                r0 = -19
                if (r1 != r0) goto L62
                if (r9 >= r10) goto L69
            L62:
                long r2 = r2 + r7
                byte r9 = r6.getByte(r7)
                if (r9 <= r4) goto L3b
            L69:
                return r5
            L6a:
                r0 = 3
                if (r11 >= r0) goto L72
                int r9 = unsafeIncompleteStateFor(r9, r1, r11)
                return r9
            L72:
                int r11 = r11 + (-3)
                sun.misc.Unsafe r0 = com.android.carsdk.protobuf.Utf8.UnsafeProcessor.UNSAFE
                long r6 = r9 + r2
                byte r9 = r0.getByte(r9)
                if (r9 > r4) goto L96
                int r10 = r1 << 28
                int r9 = r9 + 112
                int r10 = r10 + r9
                int r9 = r10 >> 30
                if (r9 != 0) goto L96
                long r9 = r6 + r2
                byte r1 = r0.getByte(r6)
                if (r1 > r4) goto L96
                long r2 = r2 + r9
                byte r9 = r0.getByte(r9)
                if (r9 <= r4) goto L3b
            L96:
                return r5
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.carsdk.protobuf.Utf8.UnsafeProcessor.partialIsValidUtf8(long, int):int");
        }

        private static int unsafeIncompleteStateFor(byte[] bArr, int i, long j, int i2) {
            if (i2 != 0) {
                if (i2 != 1) {
                    if (i2 == 2) {
                        Unsafe unsafe = UNSAFE;
                        return Utf8.incompleteStateFor(i, unsafe.getByte(bArr, j), unsafe.getByte(bArr, j + 1));
                    }
                    throw new AssertionError();
                }
                return Utf8.incompleteStateFor(i, UNSAFE.getByte(bArr, j));
            }
            return Utf8.incompleteStateFor(i);
        }

        private static int unsafeIncompleteStateFor(long j, int i, int i2) {
            if (i2 != 0) {
                if (i2 != 1) {
                    if (i2 == 2) {
                        Unsafe unsafe = UNSAFE;
                        return Utf8.incompleteStateFor(i, unsafe.getByte(j), unsafe.getByte(j + 1));
                    }
                    throw new AssertionError();
                }
                return Utf8.incompleteStateFor(i, UNSAFE.getByte(j));
            }
            return Utf8.incompleteStateFor(i);
        }

        private static Field field(Class<?> cls, String str) {
            Field field;
            try {
                field = cls.getDeclaredField(str);
                field.setAccessible(true);
            } catch (Throwable unused) {
                field = null;
            }
            Logger logger = Utf8.logger;
            Level level = Level.FINEST;
            Object[] objArr = new Object[3];
            objArr[0] = cls.getName();
            objArr[1] = str;
            objArr[2] = field != null ? "available" : "unavailable";
            logger.log(level, "{0}.{1}: {2}", objArr);
            return field;
        }

        private static long fieldOffset(Field field) {
            Unsafe unsafe;
            if (field == null || (unsafe = UNSAFE) == null) {
                return -1L;
            }
            return unsafe.objectFieldOffset(field);
        }

        private static <T> int byteArrayBaseOffset() {
            Unsafe unsafe = UNSAFE;
            if (unsafe == null) {
                return -1;
            }
            return unsafe.arrayBaseOffset(byte[].class);
        }

        private static long addressOffset(ByteBuffer byteBuffer) {
            return UNSAFE.getLong(byteBuffer, BUFFER_ADDRESS_OFFSET);
        }

        private static Unsafe getUnsafe() {
            Unsafe unsafe;
            try {
                unsafe = (Unsafe) AccessController.doPrivileged(new PrivilegedExceptionAction<Unsafe>() { // from class: com.android.carsdk.protobuf.Utf8.UnsafeProcessor.1
                    @Override // java.security.PrivilegedExceptionAction
                    public Unsafe run() throws Exception {
                        Field[] declaredFields;
                        UnsafeProcessor.checkRequiredMethods(Unsafe.class);
                        for (Field field : Unsafe.class.getDeclaredFields()) {
                            field.setAccessible(true);
                            Object obj = field.get(null);
                            if (Unsafe.class.isInstance(obj)) {
                                return (Unsafe) Unsafe.class.cast(obj);
                            }
                        }
                        return null;
                    }
                });
            } catch (Throwable unused) {
                unsafe = null;
            }
            Utf8.logger.log(Level.FINEST, "sun.misc.Unsafe: {}", unsafe != null ? "available" : "unavailable");
            return unsafe;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static void checkRequiredMethods(Class<Unsafe> cls) throws NoSuchMethodException, SecurityException {
            cls.getMethod("arrayBaseOffset", Class.class);
            cls.getMethod("getByte", Object.class, Long.TYPE);
            cls.getMethod("putByte", Object.class, Long.TYPE, Byte.TYPE);
            cls.getMethod("getLong", Object.class, Long.TYPE);
            cls.getMethod("objectFieldOffset", Field.class);
            cls.getMethod("getByte", Long.TYPE);
            cls.getMethod("getLong", Object.class, Long.TYPE);
            cls.getMethod("putByte", Long.TYPE, Byte.TYPE);
            cls.getMethod("getLong", Long.TYPE);
        }
    }

    private Utf8() {
    }
}

package com.android.dx.dex.code;

import com.android.dx.rop.code.RegisterSpec;
import com.android.dx.rop.code.RegisterSpecSet;
import com.android.dx.rop.cst.CstString;
import com.android.dx.rop.cst.CstType;
import com.android.dx.rop.type.Type;
import com.android.dx.util.FixedSizeList;
import com.xiaopeng.wirelessprojection.dmr.RendererActivity;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
/* loaded from: classes.dex */
public final class LocalList extends FixedSizeList {
    private static final boolean DEBUG = false;
    public static final LocalList EMPTY = new LocalList(0);

    /* loaded from: classes.dex */
    public enum Disposition {
        START,
        END_SIMPLY,
        END_REPLACED,
        END_MOVED,
        END_CLOBBERED_BY_PREV,
        END_CLOBBERED_BY_NEXT
    }

    public LocalList(int i) {
        super(i);
    }

    public Entry get(int i) {
        return (Entry) get0(i);
    }

    public void set(int i, Entry entry) {
        set0(i, entry);
    }

    public void debugPrint(PrintStream printStream, String str) {
        int size = size();
        for (int i = 0; i < size; i++) {
            printStream.print(str);
            printStream.println(get(i));
        }
    }

    /* loaded from: classes.dex */
    public static class Entry implements Comparable<Entry> {
        private final int address;
        private final Disposition disposition;
        private final RegisterSpec spec;
        private final CstType type;

        public Entry(int i, Disposition disposition, RegisterSpec registerSpec) {
            if (i < 0) {
                throw new IllegalArgumentException("address < 0");
            }
            Objects.requireNonNull(disposition, "disposition == null");
            try {
                if (registerSpec.getLocalItem() == null) {
                    throw new NullPointerException("spec.getLocalItem() == null");
                }
                this.address = i;
                this.disposition = disposition;
                this.spec = registerSpec;
                this.type = CstType.intern(registerSpec.getType());
            } catch (NullPointerException unused) {
                throw new NullPointerException("spec == null");
            }
        }

        public String toString() {
            return Integer.toHexString(this.address) + RendererActivity.DEFAULT_TITLE + this.disposition + RendererActivity.DEFAULT_TITLE + this.spec;
        }

        public boolean equals(Object obj) {
            return (obj instanceof Entry) && compareTo((Entry) obj) == 0;
        }

        @Override // java.lang.Comparable
        public int compareTo(Entry entry) {
            int i = this.address;
            int i2 = entry.address;
            if (i < i2) {
                return -1;
            }
            if (i > i2) {
                return 1;
            }
            boolean isStart = isStart();
            if (isStart != entry.isStart()) {
                return isStart ? 1 : -1;
            }
            return this.spec.compareTo(entry.spec);
        }

        public int getAddress() {
            return this.address;
        }

        public Disposition getDisposition() {
            return this.disposition;
        }

        public boolean isStart() {
            return this.disposition == Disposition.START;
        }

        public CstString getName() {
            return this.spec.getLocalItem().getName();
        }

        public CstString getSignature() {
            return this.spec.getLocalItem().getSignature();
        }

        public CstType getType() {
            return this.type;
        }

        public int getRegister() {
            return this.spec.getReg();
        }

        public RegisterSpec getRegisterSpec() {
            return this.spec;
        }

        public boolean matches(RegisterSpec registerSpec) {
            return this.spec.equalsUsingSimpleType(registerSpec);
        }

        public boolean matches(Entry entry) {
            return matches(entry.spec);
        }

        public Entry withDisposition(Disposition disposition) {
            return disposition == this.disposition ? this : new Entry(this.address, disposition, this.spec);
        }
    }

    public static LocalList make(DalvInsnList dalvInsnList) {
        int size = dalvInsnList.size();
        MakeState makeState = new MakeState(size);
        for (int i = 0; i < size; i++) {
            DalvInsn dalvInsn = dalvInsnList.get(i);
            if (dalvInsn instanceof LocalSnapshot) {
                makeState.snapshot(dalvInsn.getAddress(), ((LocalSnapshot) dalvInsn).getLocals());
            } else if (dalvInsn instanceof LocalStart) {
                makeState.startLocal(dalvInsn.getAddress(), ((LocalStart) dalvInsn).getLocal());
            }
        }
        return makeState.finish();
    }

    private static void debugVerify(LocalList localList) {
        try {
            debugVerify0(localList);
        } catch (RuntimeException e) {
            int size = localList.size();
            for (int i = 0; i < size; i++) {
                System.err.println(localList.get(i));
            }
            throw e;
        }
    }

    private static void debugVerify0(LocalList localList) {
        int size = localList.size();
        Entry[] entryArr = new Entry[65536];
        for (int i = 0; i < size; i++) {
            Entry entry = localList.get(i);
            int register = entry.getRegister();
            if (entry.isStart()) {
                Entry entry2 = entryArr[register];
                if (entry2 != null && entry.matches(entry2)) {
                    throw new RuntimeException("redundant start at " + Integer.toHexString(entry.getAddress()) + ": got " + entry + "; had " + entry2);
                }
                entryArr[register] = entry;
            } else if (entryArr[register] == null) {
                throw new RuntimeException("redundant end at " + Integer.toHexString(entry.getAddress()));
            } else {
                int address = entry.getAddress();
                boolean z = false;
                for (int i2 = i + 1; i2 < size; i2++) {
                    Entry entry3 = localList.get(i2);
                    if (entry3.getAddress() != address) {
                        break;
                    }
                    if (entry3.getRegisterSpec().getReg() == register) {
                        if (entry3.isStart()) {
                            if (entry.getDisposition() != Disposition.END_REPLACED) {
                                throw new RuntimeException("improperly marked end at " + Integer.toHexString(address));
                            }
                            z = true;
                        } else {
                            throw new RuntimeException("redundant end at " + Integer.toHexString(address));
                        }
                    }
                }
                if (!z && entry.getDisposition() == Disposition.END_REPLACED) {
                    throw new RuntimeException("improper end replacement claim at " + Integer.toHexString(address));
                }
                entryArr[register] = null;
            }
        }
    }

    /* loaded from: classes.dex */
    public static class MakeState {
        private final ArrayList<Entry> result;
        private int nullResultCount = 0;
        private RegisterSpecSet regs = null;
        private int[] endIndices = null;
        private final int lastAddress = 0;

        public MakeState(int i) {
            this.result = new ArrayList<>(i);
        }

        private void aboutToProcess(int i, int i2) {
            int[] iArr = this.endIndices;
            boolean z = iArr == null;
            int i3 = this.lastAddress;
            if (i != i3 || z) {
                if (i < i3) {
                    throw new RuntimeException("shouldn't happen");
                }
                if (z || i2 >= iArr.length) {
                    int i4 = i2 + 1;
                    RegisterSpecSet registerSpecSet = new RegisterSpecSet(i4);
                    int[] iArr2 = new int[i4];
                    Arrays.fill(iArr2, -1);
                    if (!z) {
                        registerSpecSet.putAll(this.regs);
                        int[] iArr3 = this.endIndices;
                        System.arraycopy(iArr3, 0, iArr2, 0, iArr3.length);
                    }
                    this.regs = registerSpecSet;
                    this.endIndices = iArr2;
                }
            }
        }

        public void snapshot(int i, RegisterSpecSet registerSpecSet) {
            int maxSize = registerSpecSet.getMaxSize();
            aboutToProcess(i, maxSize - 1);
            for (int i2 = 0; i2 < maxSize; i2++) {
                RegisterSpec registerSpec = this.regs.get(i2);
                RegisterSpec filterSpec = filterSpec(registerSpecSet.get(i2));
                if (registerSpec == null) {
                    if (filterSpec != null) {
                        startLocal(i, filterSpec);
                    }
                } else if (filterSpec == null) {
                    endLocal(i, registerSpec);
                } else if (!filterSpec.equalsUsingSimpleType(registerSpec)) {
                    endLocal(i, registerSpec);
                    startLocal(i, filterSpec);
                }
            }
        }

        public void startLocal(int i, RegisterSpec registerSpec) {
            RegisterSpec registerSpec2;
            RegisterSpec registerSpec3;
            int reg = registerSpec.getReg();
            RegisterSpec filterSpec = filterSpec(registerSpec);
            aboutToProcess(i, reg);
            RegisterSpec registerSpec4 = this.regs.get(reg);
            if (filterSpec.equalsUsingSimpleType(registerSpec4)) {
                return;
            }
            RegisterSpec findMatchingLocal = this.regs.findMatchingLocal(filterSpec);
            if (findMatchingLocal != null) {
                addOrUpdateEnd(i, Disposition.END_MOVED, findMatchingLocal);
            }
            int i2 = this.endIndices[reg];
            if (registerSpec4 != null) {
                add(i, Disposition.END_REPLACED, registerSpec4);
            } else if (i2 >= 0) {
                Entry entry = this.result.get(i2);
                if (entry.getAddress() == i) {
                    if (entry.matches(filterSpec)) {
                        this.result.set(i2, null);
                        this.nullResultCount++;
                        this.regs.put(filterSpec);
                        this.endIndices[reg] = -1;
                        return;
                    }
                    this.result.set(i2, entry.withDisposition(Disposition.END_REPLACED));
                }
            }
            if (reg > 0 && (registerSpec3 = this.regs.get(reg - 1)) != null && registerSpec3.isCategory2()) {
                addOrUpdateEnd(i, Disposition.END_CLOBBERED_BY_NEXT, registerSpec3);
            }
            if (filterSpec.isCategory2() && (registerSpec2 = this.regs.get(reg + 1)) != null) {
                addOrUpdateEnd(i, Disposition.END_CLOBBERED_BY_PREV, registerSpec2);
            }
            add(i, Disposition.START, filterSpec);
        }

        public void endLocal(int i, RegisterSpec registerSpec) {
            endLocal(i, registerSpec, Disposition.END_SIMPLY);
        }

        public void endLocal(int i, RegisterSpec registerSpec, Disposition disposition) {
            int reg = registerSpec.getReg();
            RegisterSpec filterSpec = filterSpec(registerSpec);
            aboutToProcess(i, reg);
            if (this.endIndices[reg] < 0 && !checkForEmptyRange(i, filterSpec)) {
                add(i, disposition, filterSpec);
            }
        }

        /* JADX WARN: Code restructure failed: missing block: B:15:0x0027, code lost:
            r5.regs.remove(r7);
            r4 = null;
            r5.result.set(r0, null);
            r5.nullResultCount++;
            r7 = r7.getReg();
         */
        /* JADX WARN: Code restructure failed: missing block: B:16:0x003b, code lost:
            r0 = r0 - 1;
         */
        /* JADX WARN: Code restructure failed: missing block: B:17:0x003d, code lost:
            if (r0 < 0) goto L30;
         */
        /* JADX WARN: Code restructure failed: missing block: B:18:0x003f, code lost:
            r4 = r5.result.get(r0);
         */
        /* JADX WARN: Code restructure failed: missing block: B:19:0x0048, code lost:
            if (r4 != null) goto L16;
         */
        /* JADX WARN: Code restructure failed: missing block: B:22:0x0053, code lost:
            if (r4.getRegisterSpec().getReg() != r7) goto L27;
         */
        /* JADX WARN: Code restructure failed: missing block: B:23:0x0055, code lost:
            r2 = true;
         */
        /* JADX WARN: Code restructure failed: missing block: B:24:0x0056, code lost:
            if (r2 == false) goto L25;
         */
        /* JADX WARN: Code restructure failed: missing block: B:25:0x0058, code lost:
            r5.endIndices[r7] = r0;
         */
        /* JADX WARN: Code restructure failed: missing block: B:26:0x0060, code lost:
            if (r4.getAddress() != r6) goto L25;
         */
        /* JADX WARN: Code restructure failed: missing block: B:27:0x0062, code lost:
            r5.result.set(r0, r4.withDisposition(com.android.dx.dex.code.LocalList.Disposition.END_SIMPLY));
         */
        /* JADX WARN: Code restructure failed: missing block: B:28:0x006d, code lost:
            return true;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        private boolean checkForEmptyRange(int r6, com.android.dx.rop.code.RegisterSpec r7) {
            /*
                r5 = this;
                java.util.ArrayList<com.android.dx.dex.code.LocalList$Entry> r0 = r5.result
                int r0 = r0.size()
                r1 = 1
                int r0 = r0 - r1
            L8:
                r2 = 0
                if (r0 < 0) goto L27
                java.util.ArrayList<com.android.dx.dex.code.LocalList$Entry> r3 = r5.result
                java.lang.Object r3 = r3.get(r0)
                com.android.dx.dex.code.LocalList$Entry r3 = (com.android.dx.dex.code.LocalList.Entry) r3
                if (r3 != 0) goto L16
                goto L24
            L16:
                int r4 = r3.getAddress()
                if (r4 == r6) goto L1d
                return r2
            L1d:
                boolean r3 = r3.matches(r7)
                if (r3 == 0) goto L24
                goto L27
            L24:
                int r0 = r0 + (-1)
                goto L8
            L27:
                com.android.dx.rop.code.RegisterSpecSet r3 = r5.regs
                r3.remove(r7)
                java.util.ArrayList<com.android.dx.dex.code.LocalList$Entry> r3 = r5.result
                r4 = 0
                r3.set(r0, r4)
                int r3 = r5.nullResultCount
                int r3 = r3 + r1
                r5.nullResultCount = r3
                int r7 = r7.getReg()
            L3b:
                int r0 = r0 + (-1)
                if (r0 < 0) goto L56
                java.util.ArrayList<com.android.dx.dex.code.LocalList$Entry> r3 = r5.result
                java.lang.Object r3 = r3.get(r0)
                r4 = r3
                com.android.dx.dex.code.LocalList$Entry r4 = (com.android.dx.dex.code.LocalList.Entry) r4
                if (r4 != 0) goto L4b
                goto L3b
            L4b:
                com.android.dx.rop.code.RegisterSpec r3 = r4.getRegisterSpec()
                int r3 = r3.getReg()
                if (r3 != r7) goto L3b
                r2 = r1
            L56:
                if (r2 == 0) goto L6d
                int[] r2 = r5.endIndices
                r2[r7] = r0
                int r7 = r4.getAddress()
                if (r7 != r6) goto L6d
                java.util.ArrayList<com.android.dx.dex.code.LocalList$Entry> r6 = r5.result
                com.android.dx.dex.code.LocalList$Disposition r7 = com.android.dx.dex.code.LocalList.Disposition.END_SIMPLY
                com.android.dx.dex.code.LocalList$Entry r7 = r4.withDisposition(r7)
                r6.set(r0, r7)
            L6d:
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.dx.dex.code.LocalList.MakeState.checkForEmptyRange(int, com.android.dx.rop.code.RegisterSpec):boolean");
        }

        private static RegisterSpec filterSpec(RegisterSpec registerSpec) {
            return (registerSpec == null || registerSpec.getType() != Type.KNOWN_NULL) ? registerSpec : registerSpec.withType(Type.OBJECT);
        }

        private void add(int i, Disposition disposition, RegisterSpec registerSpec) {
            int reg = registerSpec.getReg();
            this.result.add(new Entry(i, disposition, registerSpec));
            if (disposition == Disposition.START) {
                this.regs.put(registerSpec);
                this.endIndices[reg] = -1;
                return;
            }
            this.regs.remove(registerSpec);
            this.endIndices[reg] = this.result.size() - 1;
        }

        private void addOrUpdateEnd(int i, Disposition disposition, RegisterSpec registerSpec) {
            if (disposition == Disposition.START) {
                throw new RuntimeException("shouldn't happen");
            }
            int i2 = this.endIndices[registerSpec.getReg()];
            if (i2 >= 0) {
                Entry entry = this.result.get(i2);
                if (entry.getAddress() == i && entry.getRegisterSpec().equals(registerSpec)) {
                    this.result.set(i2, entry.withDisposition(disposition));
                    this.regs.remove(registerSpec);
                    return;
                }
            }
            endLocal(i, registerSpec, disposition);
        }

        public LocalList finish() {
            aboutToProcess(Integer.MAX_VALUE, 0);
            int size = this.result.size();
            int i = size - this.nullResultCount;
            if (i == 0) {
                return LocalList.EMPTY;
            }
            Entry[] entryArr = new Entry[i];
            if (size == i) {
                this.result.toArray(entryArr);
            } else {
                Iterator<Entry> it = this.result.iterator();
                int i2 = 0;
                while (it.hasNext()) {
                    Entry next = it.next();
                    if (next != null) {
                        entryArr[i2] = next;
                        i2++;
                    }
                }
            }
            Arrays.sort(entryArr);
            LocalList localList = new LocalList(i);
            for (int i3 = 0; i3 < i; i3++) {
                localList.set(i3, entryArr[i3]);
            }
            localList.setImmutable();
            return localList;
        }
    }
}

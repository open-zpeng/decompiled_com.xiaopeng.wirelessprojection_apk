package com.android.dx.dex.code;

import com.android.dx.dex.code.CatchTable;
import com.android.dx.rop.code.BasicBlock;
import com.android.dx.rop.code.BasicBlockList;
import com.android.dx.rop.code.RopMethod;
import com.android.dx.rop.cst.CstType;
import com.android.dx.rop.type.Type;
import com.android.dx.rop.type.TypeList;
import com.android.dx.util.IntList;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
/* loaded from: classes.dex */
public final class StdCatchBuilder implements CatchBuilder {
    private static final int MAX_CATCH_RANGE = 65535;
    private final BlockAddresses addresses;
    private final RopMethod method;
    private final int[] order;

    public StdCatchBuilder(RopMethod ropMethod, int[] iArr, BlockAddresses blockAddresses) {
        Objects.requireNonNull(ropMethod, "method == null");
        Objects.requireNonNull(iArr, "order == null");
        Objects.requireNonNull(blockAddresses, "addresses == null");
        this.method = ropMethod;
        this.order = iArr;
        this.addresses = blockAddresses;
    }

    @Override // com.android.dx.dex.code.CatchBuilder
    public CatchTable build() {
        return build(this.method, this.order, this.addresses);
    }

    @Override // com.android.dx.dex.code.CatchBuilder
    public boolean hasAnyCatches() {
        BasicBlockList blocks = this.method.getBlocks();
        int size = blocks.size();
        for (int i = 0; i < size; i++) {
            if (blocks.get(i).getLastInsn().getCatches().size() != 0) {
                return true;
            }
        }
        return false;
    }

    @Override // com.android.dx.dex.code.CatchBuilder
    public HashSet<Type> getCatchTypes() {
        HashSet<Type> hashSet = new HashSet<>(20);
        BasicBlockList blocks = this.method.getBlocks();
        int size = blocks.size();
        for (int i = 0; i < size; i++) {
            TypeList catches = blocks.get(i).getLastInsn().getCatches();
            int size2 = catches.size();
            for (int i2 = 0; i2 < size2; i2++) {
                hashSet.add(catches.getType(i2));
            }
        }
        return hashSet;
    }

    public static CatchTable build(RopMethod ropMethod, int[] iArr, BlockAddresses blockAddresses) {
        int length = iArr.length;
        BasicBlockList blocks = ropMethod.getBlocks();
        ArrayList arrayList = new ArrayList(length);
        CatchHandlerList catchHandlerList = CatchHandlerList.EMPTY;
        BasicBlock basicBlock = null;
        BasicBlock basicBlock2 = null;
        for (int i : iArr) {
            BasicBlock labelToBlock = blocks.labelToBlock(i);
            if (labelToBlock.canThrow()) {
                CatchHandlerList handlersFor = handlersFor(labelToBlock, blockAddresses);
                if (catchHandlerList.size() != 0) {
                    if (catchHandlerList.equals(handlersFor) && rangeIsValid(basicBlock, labelToBlock, blockAddresses)) {
                        basicBlock2 = labelToBlock;
                    } else if (catchHandlerList.size() != 0) {
                        arrayList.add(makeEntry(basicBlock, basicBlock2, catchHandlerList, blockAddresses));
                    }
                }
                basicBlock = labelToBlock;
                basicBlock2 = basicBlock;
                catchHandlerList = handlersFor;
            }
        }
        if (catchHandlerList.size() != 0) {
            arrayList.add(makeEntry(basicBlock, basicBlock2, catchHandlerList, blockAddresses));
        }
        int size = arrayList.size();
        if (size == 0) {
            return CatchTable.EMPTY;
        }
        CatchTable catchTable = new CatchTable(size);
        for (int i2 = 0; i2 < size; i2++) {
            catchTable.set(i2, (CatchTable.Entry) arrayList.get(i2));
        }
        catchTable.setImmutable();
        return catchTable;
    }

    private static CatchHandlerList handlersFor(BasicBlock basicBlock, BlockAddresses blockAddresses) {
        IntList successors = basicBlock.getSuccessors();
        int size = successors.size();
        int primarySuccessor = basicBlock.getPrimarySuccessor();
        TypeList catches = basicBlock.getLastInsn().getCatches();
        int size2 = catches.size();
        if (size2 == 0) {
            return CatchHandlerList.EMPTY;
        }
        if ((primarySuccessor == -1 && size != size2) || (primarySuccessor != -1 && (size != size2 + 1 || primarySuccessor != successors.get(size2)))) {
            throw new RuntimeException("shouldn't happen: weird successors list");
        }
        int i = 0;
        while (true) {
            if (i >= size2) {
                break;
            } else if (catches.getType(i).equals(Type.OBJECT)) {
                size2 = i + 1;
                break;
            } else {
                i++;
            }
        }
        CatchHandlerList catchHandlerList = new CatchHandlerList(size2);
        for (int i2 = 0; i2 < size2; i2++) {
            catchHandlerList.set(i2, new CstType(catches.getType(i2)), blockAddresses.getStart(successors.get(i2)).getAddress());
        }
        catchHandlerList.setImmutable();
        return catchHandlerList;
    }

    private static CatchTable.Entry makeEntry(BasicBlock basicBlock, BasicBlock basicBlock2, CatchHandlerList catchHandlerList, BlockAddresses blockAddresses) {
        return new CatchTable.Entry(blockAddresses.getLast(basicBlock).getAddress(), blockAddresses.getEnd(basicBlock2).getAddress(), catchHandlerList);
    }

    private static boolean rangeIsValid(BasicBlock basicBlock, BasicBlock basicBlock2, BlockAddresses blockAddresses) {
        Objects.requireNonNull(basicBlock, "start == null");
        Objects.requireNonNull(basicBlock2, "end == null");
        return blockAddresses.getEnd(basicBlock2).getAddress() - blockAddresses.getLast(basicBlock).getAddress() <= 65535;
    }
}

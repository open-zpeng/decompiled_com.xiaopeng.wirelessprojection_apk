package com.android.dx;

import com.android.dx.rop.code.BasicBlock;
import com.android.dx.rop.code.Insn;
import com.android.dx.rop.code.InsnList;
import com.android.dx.util.IntList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/* loaded from: classes.dex */
public final class Label {
    Label alternateSuccessor;
    Code code;
    Label primarySuccessor;
    final List<Insn> instructions = new ArrayList();
    boolean marked = false;
    List<Label> catchLabels = Collections.emptyList();
    int id = -1;

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isEmpty() {
        return this.instructions.isEmpty();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void compact() {
        for (int i = 0; i < this.catchLabels.size(); i++) {
            while (this.catchLabels.get(i).isEmpty()) {
                List<Label> list = this.catchLabels;
                list.set(i, list.get(i).primarySuccessor);
            }
        }
        while (true) {
            Label label = this.primarySuccessor;
            if (label == null || !label.isEmpty()) {
                break;
            }
            this.primarySuccessor = this.primarySuccessor.primarySuccessor;
        }
        while (true) {
            Label label2 = this.alternateSuccessor;
            if (label2 == null || !label2.isEmpty()) {
                return;
            }
            this.alternateSuccessor = this.alternateSuccessor.primarySuccessor;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BasicBlock toBasicBlock() {
        InsnList insnList = new InsnList(this.instructions.size());
        for (int i = 0; i < this.instructions.size(); i++) {
            insnList.set(i, this.instructions.get(i));
        }
        insnList.setImmutable();
        int i2 = -1;
        IntList intList = new IntList();
        for (Label label : this.catchLabels) {
            intList.add(label.id);
        }
        Label label2 = this.primarySuccessor;
        if (label2 != null) {
            i2 = label2.id;
            intList.add(i2);
        }
        Label label3 = this.alternateSuccessor;
        if (label3 != null) {
            intList.add(label3.id);
        }
        intList.setImmutable();
        return new BasicBlock(this.id, insnList, intList, i2);
    }
}

package com.android.dx.merge;

import com.android.dex.DexException;
import com.android.dex.DexIndexOverflowException;
import com.android.dx.io.CodeReader;
import com.android.dx.io.instructions.DecodedInstruction;
import com.android.dx.io.instructions.ShortArrayCodeOutput;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class InstructionTransformer {
    private IndexMap indexMap;
    private int mappedAt;
    private DecodedInstruction[] mappedInstructions;
    private final CodeReader reader;

    static /* synthetic */ int access$808(InstructionTransformer instructionTransformer) {
        int i = instructionTransformer.mappedAt;
        instructionTransformer.mappedAt = i + 1;
        return i;
    }

    public InstructionTransformer() {
        CodeReader codeReader = new CodeReader();
        this.reader = codeReader;
        codeReader.setAllVisitors(new GenericVisitor());
        codeReader.setStringVisitor(new StringVisitor());
        codeReader.setTypeVisitor(new TypeVisitor());
        codeReader.setFieldVisitor(new FieldVisitor());
        codeReader.setMethodVisitor(new MethodVisitor());
        codeReader.setMethodAndProtoVisitor(new MethodAndProtoVisitor());
        codeReader.setCallSiteVisitor(new CallSiteVisitor());
    }

    public short[] transform(IndexMap indexMap, short[] sArr) throws DexException {
        DecodedInstruction[] decodedInstructionArr;
        DecodedInstruction[] decodeAll = DecodedInstruction.decodeAll(sArr);
        int length = decodeAll.length;
        this.indexMap = indexMap;
        this.mappedInstructions = new DecodedInstruction[length];
        this.mappedAt = 0;
        this.reader.visitAll(decodeAll);
        ShortArrayCodeOutput shortArrayCodeOutput = new ShortArrayCodeOutput(length);
        for (DecodedInstruction decodedInstruction : this.mappedInstructions) {
            if (decodedInstruction != null) {
                decodedInstruction.encode(shortArrayCodeOutput);
            }
        }
        this.indexMap = null;
        return shortArrayCodeOutput.getArray();
    }

    /* loaded from: classes.dex */
    private class GenericVisitor implements CodeReader.Visitor {
        private GenericVisitor() {
        }

        @Override // com.android.dx.io.CodeReader.Visitor
        public void visit(DecodedInstruction[] decodedInstructionArr, DecodedInstruction decodedInstruction) {
            InstructionTransformer.this.mappedInstructions[InstructionTransformer.access$808(InstructionTransformer.this)] = decodedInstruction;
        }
    }

    /* loaded from: classes.dex */
    private class StringVisitor implements CodeReader.Visitor {
        private StringVisitor() {
        }

        @Override // com.android.dx.io.CodeReader.Visitor
        public void visit(DecodedInstruction[] decodedInstructionArr, DecodedInstruction decodedInstruction) {
            int adjustString = InstructionTransformer.this.indexMap.adjustString(decodedInstruction.getIndex());
            InstructionTransformer.jumboCheck(decodedInstruction.getOpcode() == 27, adjustString);
            InstructionTransformer.this.mappedInstructions[InstructionTransformer.access$808(InstructionTransformer.this)] = decodedInstruction.withIndex(adjustString);
        }
    }

    /* loaded from: classes.dex */
    private class FieldVisitor implements CodeReader.Visitor {
        private FieldVisitor() {
        }

        @Override // com.android.dx.io.CodeReader.Visitor
        public void visit(DecodedInstruction[] decodedInstructionArr, DecodedInstruction decodedInstruction) {
            int adjustField = InstructionTransformer.this.indexMap.adjustField(decodedInstruction.getIndex());
            InstructionTransformer.jumboCheck(decodedInstruction.getOpcode() == 27, adjustField);
            InstructionTransformer.this.mappedInstructions[InstructionTransformer.access$808(InstructionTransformer.this)] = decodedInstruction.withIndex(adjustField);
        }
    }

    /* loaded from: classes.dex */
    private class TypeVisitor implements CodeReader.Visitor {
        private TypeVisitor() {
        }

        @Override // com.android.dx.io.CodeReader.Visitor
        public void visit(DecodedInstruction[] decodedInstructionArr, DecodedInstruction decodedInstruction) {
            int adjustType = InstructionTransformer.this.indexMap.adjustType(decodedInstruction.getIndex());
            InstructionTransformer.jumboCheck(decodedInstruction.getOpcode() == 27, adjustType);
            InstructionTransformer.this.mappedInstructions[InstructionTransformer.access$808(InstructionTransformer.this)] = decodedInstruction.withIndex(adjustType);
        }
    }

    /* loaded from: classes.dex */
    private class MethodVisitor implements CodeReader.Visitor {
        private MethodVisitor() {
        }

        @Override // com.android.dx.io.CodeReader.Visitor
        public void visit(DecodedInstruction[] decodedInstructionArr, DecodedInstruction decodedInstruction) {
            int adjustMethod = InstructionTransformer.this.indexMap.adjustMethod(decodedInstruction.getIndex());
            InstructionTransformer.jumboCheck(decodedInstruction.getOpcode() == 27, adjustMethod);
            InstructionTransformer.this.mappedInstructions[InstructionTransformer.access$808(InstructionTransformer.this)] = decodedInstruction.withIndex(adjustMethod);
        }
    }

    /* loaded from: classes.dex */
    private class MethodAndProtoVisitor implements CodeReader.Visitor {
        private MethodAndProtoVisitor() {
        }

        @Override // com.android.dx.io.CodeReader.Visitor
        public void visit(DecodedInstruction[] decodedInstructionArr, DecodedInstruction decodedInstruction) {
            InstructionTransformer.this.mappedInstructions[InstructionTransformer.access$808(InstructionTransformer.this)] = decodedInstruction.withProtoIndex(InstructionTransformer.this.indexMap.adjustMethod(decodedInstruction.getIndex()), InstructionTransformer.this.indexMap.adjustProto(decodedInstruction.getProtoIndex()));
        }
    }

    /* loaded from: classes.dex */
    private class CallSiteVisitor implements CodeReader.Visitor {
        private CallSiteVisitor() {
        }

        @Override // com.android.dx.io.CodeReader.Visitor
        public void visit(DecodedInstruction[] decodedInstructionArr, DecodedInstruction decodedInstruction) {
            InstructionTransformer.this.mappedInstructions[InstructionTransformer.access$808(InstructionTransformer.this)] = decodedInstruction.withIndex(InstructionTransformer.this.indexMap.adjustCallSite(decodedInstruction.getIndex()));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void jumboCheck(boolean z, int i) {
        if (!z && i > 65535) {
            throw new DexIndexOverflowException("Cannot merge new index " + i + " into a non-jumbo instruction!");
        }
    }
}

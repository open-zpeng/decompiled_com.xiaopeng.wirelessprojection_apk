package com.android.dx.cf.code;

import com.android.dex.util.ExceptionWithContext;
import com.android.dx.rop.cst.CstType;
import com.android.dx.rop.type.StdTypeList;
import com.android.dx.rop.type.Type;
import com.android.dx.util.IntList;
import java.util.Objects;
/* loaded from: classes.dex */
public final class Frame {
    private final LocalsArray locals;
    private final ExecutionStack stack;
    private final IntList subroutines;

    private Frame(LocalsArray localsArray, ExecutionStack executionStack) {
        this(localsArray, executionStack, IntList.EMPTY);
    }

    private Frame(LocalsArray localsArray, ExecutionStack executionStack, IntList intList) {
        Objects.requireNonNull(localsArray, "locals == null");
        Objects.requireNonNull(executionStack, "stack == null");
        intList.throwIfMutable();
        this.locals = localsArray;
        this.stack = executionStack;
        this.subroutines = intList;
    }

    public Frame(int i, int i2) {
        this(new OneLocalsArray(i), new ExecutionStack(i2));
    }

    public Frame copy() {
        return new Frame(this.locals.copy(), this.stack.copy(), this.subroutines);
    }

    public void setImmutable() {
        this.locals.setImmutable();
        this.stack.setImmutable();
    }

    public void makeInitialized(Type type) {
        this.locals.makeInitialized(type);
        this.stack.makeInitialized(type);
    }

    public LocalsArray getLocals() {
        return this.locals;
    }

    public ExecutionStack getStack() {
        return this.stack;
    }

    public IntList getSubroutines() {
        return this.subroutines;
    }

    public void initializeWithParameters(StdTypeList stdTypeList) {
        int size = stdTypeList.size();
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            Type type = stdTypeList.get(i2);
            this.locals.set(i, type);
            i += type.getCategory();
        }
    }

    public Frame subFrameForLabel(int i, int i2) {
        LocalsArray localsArray = this.locals;
        LocalsArray subArrayForLabel = localsArray instanceof LocalsArraySet ? ((LocalsArraySet) localsArray).subArrayForLabel(i2) : null;
        try {
            IntList mutableCopy = this.subroutines.mutableCopy();
            if (mutableCopy.pop() != i) {
                throw new RuntimeException("returning from invalid subroutine");
            }
            mutableCopy.setImmutable();
            if (subArrayForLabel == null) {
                return null;
            }
            return new Frame(subArrayForLabel, this.stack, mutableCopy);
        } catch (IndexOutOfBoundsException unused) {
            throw new RuntimeException("returning from invalid subroutine");
        } catch (NullPointerException unused2) {
            throw new NullPointerException("can't return from non-subroutine");
        }
    }

    public Frame mergeWith(Frame frame) {
        LocalsArray merge = getLocals().merge(frame.getLocals());
        ExecutionStack merge2 = getStack().merge(frame.getStack());
        IntList mergeSubroutineLists = mergeSubroutineLists(frame.subroutines);
        LocalsArray adjustLocalsForSubroutines = adjustLocalsForSubroutines(merge, mergeSubroutineLists);
        return (adjustLocalsForSubroutines == getLocals() && merge2 == getStack() && this.subroutines == mergeSubroutineLists) ? this : new Frame(adjustLocalsForSubroutines, merge2, mergeSubroutineLists);
    }

    private IntList mergeSubroutineLists(IntList intList) {
        if (this.subroutines.equals(intList)) {
            return this.subroutines;
        }
        IntList intList2 = new IntList();
        int size = this.subroutines.size();
        int size2 = intList.size();
        for (int i = 0; i < size && i < size2 && this.subroutines.get(i) == intList.get(i); i++) {
            intList2.add(i);
        }
        intList2.setImmutable();
        return intList2;
    }

    private static LocalsArray adjustLocalsForSubroutines(LocalsArray localsArray, IntList intList) {
        if (localsArray instanceof LocalsArraySet) {
            LocalsArraySet localsArraySet = (LocalsArraySet) localsArray;
            return intList.size() == 0 ? localsArraySet.getPrimary() : localsArraySet;
        }
        return localsArray;
    }

    public Frame mergeWithSubroutineCaller(Frame frame, int i, int i2) {
        IntList intList;
        LocalsArraySet mergeWithSubroutineCaller = getLocals().mergeWithSubroutineCaller(frame.getLocals(), i2);
        ExecutionStack merge = getStack().merge(frame.getStack());
        IntList mutableCopy = frame.subroutines.mutableCopy();
        mutableCopy.add(i);
        mutableCopy.setImmutable();
        if (mergeWithSubroutineCaller == getLocals() && merge == getStack() && this.subroutines.equals(mutableCopy)) {
            return this;
        }
        if (this.subroutines.equals(mutableCopy)) {
            mutableCopy = this.subroutines;
        } else {
            if (this.subroutines.size() > mutableCopy.size()) {
                intList = mutableCopy;
                mutableCopy = this.subroutines;
            } else {
                intList = this.subroutines;
            }
            int size = mutableCopy.size();
            int size2 = intList.size();
            for (int i3 = size2 - 1; i3 >= 0; i3--) {
                if (intList.get(i3) != mutableCopy.get((size - size2) + i3)) {
                    throw new RuntimeException("Incompatible merged subroutines");
                }
            }
        }
        return new Frame(mergeWithSubroutineCaller, merge, mutableCopy);
    }

    public Frame makeNewSubroutineStartFrame(int i, int i2) {
        this.subroutines.mutableCopy().add(i);
        return new Frame(this.locals.getPrimary(), this.stack, IntList.makeImmutable(i)).mergeWithSubroutineCaller(this, i, i2);
    }

    public Frame makeExceptionHandlerStartFrame(CstType cstType) {
        ExecutionStack copy = getStack().copy();
        copy.clear();
        copy.push(cstType);
        return new Frame(getLocals(), copy, this.subroutines);
    }

    public void annotate(ExceptionWithContext exceptionWithContext) {
        this.locals.annotate(exceptionWithContext);
        this.stack.annotate(exceptionWithContext);
    }
}

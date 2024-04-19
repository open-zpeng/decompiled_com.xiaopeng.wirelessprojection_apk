package com.android.carsdk.protobuf;
/* loaded from: classes.dex */
interface MutabilityOracle {
    public static final MutabilityOracle IMMUTABLE = new MutabilityOracle() { // from class: com.android.carsdk.protobuf.MutabilityOracle.1
        @Override // com.android.carsdk.protobuf.MutabilityOracle
        public void ensureMutable() {
            throw new UnsupportedOperationException();
        }
    };

    void ensureMutable();
}

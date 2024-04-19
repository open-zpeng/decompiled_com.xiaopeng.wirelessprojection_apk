package com.xiaopeng.lib.apirouter.server;

import com.xiaopeng.wirelessprojection.core.apirouter.IpcRouterService;
import java.util.HashSet;
import java.util.Set;
/* loaded from: classes2.dex */
public class IpcRouterService_Manifest {
    public static final String DESCRIPTOR = "com.xiaopeng.wirelessprojection.core.apirouter.IpcRouterService";
    public static final int TRANSACTION_onFlowRemainQueryResult = 0;
    public static final int TRANSACTION_onQuery = 1;

    public static String toJsonManifest() {
        return "{\"authority\":\"com.xiaopeng.wirelessprojection.core.apirouter.IpcRouterService\",\"DESCRIPTOR\":\"com.xiaopeng.wirelessprojection.core.apirouter.IpcRouterService\",\"TRANSACTION\":[{\"path\":\"onFlowRemainQueryResult\",\"METHOD\":\"onFlowRemainQueryResult\",\"ID\":0,\"parameter\":[{\"alias\":\"data\",\"name\":\"data\"}]},{\"path\":\"onQuery\",\"METHOD\":\"onQuery\",\"ID\":1,\"parameter\":[{\"alias\":\"event\",\"name\":\"event\"},{\"alias\":\"data\",\"name\":\"data\"},{\"alias\":\"callback\",\"name\":\"callback\"}]}]}";
    }

    public static Set<String> getKey() {
        HashSet hashSet = new HashSet(2);
        hashSet.add(IpcRouterService.TAG);
        return hashSet;
    }
}

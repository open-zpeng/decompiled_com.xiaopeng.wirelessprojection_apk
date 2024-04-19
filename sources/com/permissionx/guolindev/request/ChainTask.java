package com.permissionx.guolindev.request;

import java.util.List;
/* loaded from: classes2.dex */
public interface ChainTask {
    void finish();

    ExplainScope getExplainScope();

    ForwardScope getForwardScope();

    void request();

    void requestAgain(List<String> list);
}

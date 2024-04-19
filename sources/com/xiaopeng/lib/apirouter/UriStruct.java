package com.xiaopeng.lib.apirouter;
/* loaded from: classes2.dex */
public class UriStruct {
    public String applicationId;
    public String processTag;
    public String serviceName;
    public int targetUid;

    public String toString() {
        return this.targetUid + "@" + this.applicationId + "." + this.serviceName;
    }
}

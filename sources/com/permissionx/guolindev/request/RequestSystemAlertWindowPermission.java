package com.permissionx.guolindev.request;

import android.os.Build;
import android.provider.Settings;
import com.permissionx.guolindev.callback.ExplainReasonCallback;
import com.permissionx.guolindev.callback.ExplainReasonCallbackWithBeforeParam;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
/* compiled from: RequestSystemAlertWindowPermission.kt */
@Metadata(d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0000\b\u0000\u0018\u00002\u00020\u0001B\u000f\b\u0000\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\b\u0010\u0005\u001a\u00020\u0006H\u0016J\u0016\u0010\u0007\u001a\u00020\u00062\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\tH\u0016¨\u0006\u000b"}, d2 = {"Lcom/permissionx/guolindev/request/RequestSystemAlertWindowPermission;", "Lcom/permissionx/guolindev/request/BaseTask;", "permissionBuilder", "Lcom/permissionx/guolindev/request/PermissionBuilder;", "(Lcom/permissionx/guolindev/request/PermissionBuilder;)V", "request", "", "requestAgain", "permissions", "", "", "permissionx_release"}, k = 1, mv = {1, 5, 1}, xi = 48)
/* loaded from: classes2.dex */
public final class RequestSystemAlertWindowPermission extends BaseTask {
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public RequestSystemAlertWindowPermission(PermissionBuilder permissionBuilder) {
        super(permissionBuilder);
        Intrinsics.checkNotNullParameter(permissionBuilder, "permissionBuilder");
    }

    @Override // com.permissionx.guolindev.request.ChainTask
    public void request() {
        if (this.pb.shouldRequestSystemAlertWindowPermission()) {
            if (Build.VERSION.SDK_INT >= 23 && this.pb.getTargetSdkVersion() >= 23) {
                if (Settings.canDrawOverlays(this.pb.getActivity())) {
                    finish();
                    return;
                } else if (this.pb.explainReasonCallback != null || this.pb.explainReasonCallbackWithBeforeParam != null) {
                    List<String> mutableListOf = CollectionsKt.mutableListOf("android.permission.SYSTEM_ALERT_WINDOW");
                    if (this.pb.explainReasonCallbackWithBeforeParam != null) {
                        ExplainReasonCallbackWithBeforeParam explainReasonCallbackWithBeforeParam = this.pb.explainReasonCallbackWithBeforeParam;
                        Intrinsics.checkNotNull(explainReasonCallbackWithBeforeParam);
                        explainReasonCallbackWithBeforeParam.onExplainReason(getExplainScope(), mutableListOf, true);
                        return;
                    }
                    ExplainReasonCallback explainReasonCallback = this.pb.explainReasonCallback;
                    Intrinsics.checkNotNull(explainReasonCallback);
                    explainReasonCallback.onExplainReason(getExplainScope(), mutableListOf);
                    return;
                } else {
                    finish();
                    return;
                }
            }
            this.pb.grantedPermissions.add("android.permission.SYSTEM_ALERT_WINDOW");
            this.pb.specialPermissions.remove("android.permission.SYSTEM_ALERT_WINDOW");
            finish();
            return;
        }
        finish();
    }

    @Override // com.permissionx.guolindev.request.ChainTask
    public void requestAgain(List<String> permissions) {
        Intrinsics.checkNotNullParameter(permissions, "permissions");
        this.pb.requestSystemAlertWindowPermissionNow(this);
    }
}

package com.permissionx.guolindev.request;

import android.os.Build;
import com.permissionx.guolindev.callback.ExplainReasonCallback;
import com.permissionx.guolindev.callback.ExplainReasonCallbackWithBeforeParam;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
/* compiled from: RequestInstallPackagesPermission.kt */
@Metadata(d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0000\u0018\u0000 \u000b2\u00020\u0001:\u0001\u000bB\u000f\b\u0000\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\b\u0010\u0005\u001a\u00020\u0006H\u0016J\u0016\u0010\u0007\u001a\u00020\u00062\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\tH\u0016¨\u0006\f"}, d2 = {"Lcom/permissionx/guolindev/request/RequestInstallPackagesPermission;", "Lcom/permissionx/guolindev/request/BaseTask;", "permissionBuilder", "Lcom/permissionx/guolindev/request/PermissionBuilder;", "(Lcom/permissionx/guolindev/request/PermissionBuilder;)V", "request", "", "requestAgain", "permissions", "", "", "Companion", "permissionx_release"}, k = 1, mv = {1, 5, 1}, xi = 48)
/* loaded from: classes2.dex */
public final class RequestInstallPackagesPermission extends BaseTask {
    public static final Companion Companion = new Companion(null);
    public static final String REQUEST_INSTALL_PACKAGES = "android.permission.REQUEST_INSTALL_PACKAGES";

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public RequestInstallPackagesPermission(PermissionBuilder permissionBuilder) {
        super(permissionBuilder);
        Intrinsics.checkNotNullParameter(permissionBuilder, "permissionBuilder");
    }

    @Override // com.permissionx.guolindev.request.ChainTask
    public void request() {
        if (this.pb.shouldRequestInstallPackagesPermission() && Build.VERSION.SDK_INT >= 26 && this.pb.getTargetSdkVersion() >= 26) {
            if (this.pb.getActivity().getPackageManager().canRequestPackageInstalls()) {
                finish();
                return;
            } else if (this.pb.explainReasonCallback != null || this.pb.explainReasonCallbackWithBeforeParam != null) {
                List<String> mutableListOf = CollectionsKt.mutableListOf(REQUEST_INSTALL_PACKAGES);
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
        finish();
    }

    @Override // com.permissionx.guolindev.request.ChainTask
    public void requestAgain(List<String> permissions) {
        Intrinsics.checkNotNullParameter(permissions, "permissions");
        this.pb.requestInstallPackagePermissionNow(this);
    }

    /* compiled from: RequestInstallPackagesPermission.kt */
    @Metadata(d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T¢\u0006\u0002\n\u0000¨\u0006\u0005"}, d2 = {"Lcom/permissionx/guolindev/request/RequestInstallPackagesPermission$Companion;", "", "()V", "REQUEST_INSTALL_PACKAGES", "", "permissionx_release"}, k = 1, mv = {1, 5, 1}, xi = 48)
    /* loaded from: classes2.dex */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }
    }
}

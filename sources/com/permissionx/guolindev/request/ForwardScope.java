package com.permissionx.guolindev.request;

import com.permissionx.guolindev.dialog.RationaleDialog;
import com.permissionx.guolindev.dialog.RationaleDialogFragment;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
/* compiled from: ForwardScope.kt */
@Metadata(d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\u0004\u0018\u00002\u00020\u0001B\u0017\b\u0000\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006J\u000e\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nJ\u000e\u0010\u0007\u001a\u00020\b2\u0006\u0010\u000b\u001a\u00020\fJ2\u0010\u0007\u001a\u00020\b2\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000e2\u0006\u0010\u0010\u001a\u00020\u000f2\u0006\u0010\u0011\u001a\u00020\u000f2\n\b\u0002\u0010\u0012\u001a\u0004\u0018\u00010\u000fH\u0007R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0013"}, d2 = {"Lcom/permissionx/guolindev/request/ForwardScope;", "", "pb", "Lcom/permissionx/guolindev/request/PermissionBuilder;", "chainTask", "Lcom/permissionx/guolindev/request/ChainTask;", "(Lcom/permissionx/guolindev/request/PermissionBuilder;Lcom/permissionx/guolindev/request/ChainTask;)V", "showForwardToSettingsDialog", "", "dialog", "Lcom/permissionx/guolindev/dialog/RationaleDialog;", "dialogFragment", "Lcom/permissionx/guolindev/dialog/RationaleDialogFragment;", "permissions", "", "", "message", "positiveText", "negativeText", "permissionx_release"}, k = 1, mv = {1, 5, 1}, xi = 48)
/* loaded from: classes2.dex */
public final class ForwardScope {
    private final ChainTask chainTask;
    private final PermissionBuilder pb;

    public final void showForwardToSettingsDialog(List<String> permissions, String message, String positiveText) {
        Intrinsics.checkNotNullParameter(permissions, "permissions");
        Intrinsics.checkNotNullParameter(message, "message");
        Intrinsics.checkNotNullParameter(positiveText, "positiveText");
        showForwardToSettingsDialog$default(this, permissions, message, positiveText, null, 8, null);
    }

    public ForwardScope(PermissionBuilder pb, ChainTask chainTask) {
        Intrinsics.checkNotNullParameter(pb, "pb");
        Intrinsics.checkNotNullParameter(chainTask, "chainTask");
        this.pb = pb;
        this.chainTask = chainTask;
    }

    public static /* synthetic */ void showForwardToSettingsDialog$default(ForwardScope forwardScope, List list, String str, String str2, String str3, int i, Object obj) {
        if ((i & 8) != 0) {
            str3 = null;
        }
        forwardScope.showForwardToSettingsDialog(list, str, str2, str3);
    }

    public final void showForwardToSettingsDialog(List<String> permissions, String message, String positiveText, String str) {
        Intrinsics.checkNotNullParameter(permissions, "permissions");
        Intrinsics.checkNotNullParameter(message, "message");
        Intrinsics.checkNotNullParameter(positiveText, "positiveText");
        this.pb.showHandlePermissionDialog(this.chainTask, false, permissions, message, positiveText, str);
    }

    public final void showForwardToSettingsDialog(RationaleDialog dialog) {
        Intrinsics.checkNotNullParameter(dialog, "dialog");
        this.pb.showHandlePermissionDialog(this.chainTask, false, dialog);
    }

    public final void showForwardToSettingsDialog(RationaleDialogFragment dialogFragment) {
        Intrinsics.checkNotNullParameter(dialogFragment, "dialogFragment");
        this.pb.showHandlePermissionDialog(this.chainTask, false, dialogFragment);
    }
}

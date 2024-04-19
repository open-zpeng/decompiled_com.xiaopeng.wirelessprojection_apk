package com.permissionx.guolindev.request;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import com.permissionx.guolindev.callback.ExplainReasonCallback;
import com.permissionx.guolindev.callback.ExplainReasonCallbackWithBeforeParam;
import com.permissionx.guolindev.callback.ForwardToSettingsCallback;
import com.permissionx.guolindev.callback.RequestCallback;
import com.permissionx.guolindev.dialog.DefaultDialog;
import com.permissionx.guolindev.dialog.RationaleDialog;
import com.permissionx.guolindev.dialog.RationaleDialogFragment;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
/* compiled from: PermissionBuilder.kt */
@Metadata(d1 = {"\u0000\u0090\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010#\n\u0002\u0010\u000e\n\u0002\b\b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\"\n\u0002\b\f\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\u0018\u0000 X2\u00020\u0001:\u0001XB5\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007\u0012\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\b0\u0007¢\u0006\u0002\u0010\nJ\r\u00102\u001a\u000203H\u0000¢\u0006\u0002\b4J\u0006\u0010\u0015\u001a\u00020\u0000J\u0016\u00105\u001a\u0002032\f\u00106\u001a\b\u0012\u0004\u0012\u00020\b07H\u0002J\b\u00108\u001a\u000203H\u0003J\u0010\u00109\u001a\u00020\u00002\b\u0010:\u001a\u0004\u0018\u00010\u0018J\u0010\u00109\u001a\u00020\u00002\b\u0010:\u001a\u0004\u0018\u00010\u001aJ\u0010\u0010;\u001a\u00020\u00002\b\u0010:\u001a\u0004\u0018\u00010\u001dJ\b\u0010<\u001a\u000203H\u0002J\u0010\u0010=\u001a\u0002032\b\u0010:\u001a\u0004\u0018\u00010,J\u000e\u0010>\u001a\u0002032\u0006\u0010?\u001a\u00020@J\u000e\u0010A\u001a\u0002032\u0006\u0010?\u001a\u00020@J\u000e\u0010B\u001a\u0002032\u0006\u0010?\u001a\u00020@J\u001c\u0010C\u001a\u0002032\f\u00106\u001a\b\u0012\u0004\u0012\u00020\b0D2\u0006\u0010?\u001a\u00020@J\u000e\u0010E\u001a\u0002032\u0006\u0010?\u001a\u00020@J\u000e\u0010F\u001a\u0002032\u0006\u0010?\u001a\u00020@J\b\u0010G\u001a\u000203H\u0002J\u0016\u0010H\u001a\u00020\u00002\u0006\u0010'\u001a\u00020\u00132\u0006\u0010\u0012\u001a\u00020\u0013J\u0006\u0010I\u001a\u00020\u0016J\u0006\u0010J\u001a\u00020\u0016J\u0006\u0010K\u001a\u00020\u0016J\u0006\u0010L\u001a\u00020\u0016J\u0006\u0010M\u001a\u00020\u0016J\u001e\u0010N\u001a\u0002032\u0006\u0010?\u001a\u00020@2\u0006\u0010O\u001a\u00020\u00162\u0006\u0010P\u001a\u00020QJ\u001e\u0010N\u001a\u0002032\u0006\u0010?\u001a\u00020@2\u0006\u0010O\u001a\u00020\u00162\u0006\u0010R\u001a\u00020SJ>\u0010N\u001a\u0002032\u0006\u0010?\u001a\u00020@2\u0006\u0010O\u001a\u00020\u00162\f\u00106\u001a\b\u0012\u0004\u0012\u00020\b072\u0006\u0010T\u001a\u00020\b2\u0006\u0010U\u001a\u00020\b2\b\u0010V\u001a\u0004\u0018\u00010\bJ\b\u0010W\u001a\u000203H\u0002R\u001a\u0010\u000b\u001a\u00020\u0003X\u0086.¢\u0006\u000e\n\u0000\u001a\u0004\b\f\u0010\r\"\u0004\b\u000e\u0010\u000fR\u0014\u0010\u0010\u001a\u0004\u0018\u00010\u00118\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0013X\u0082\u000e¢\u0006\u0002\n\u0000R\u0018\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\b0\u00078\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0012\u0010\u0015\u001a\u00020\u00168\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u0017\u001a\u0004\u0018\u00010\u00188\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u0019\u001a\u0004\u0018\u00010\u001a8\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0018\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\b0\u00078\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u001c\u001a\u0004\u0018\u00010\u001d8\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0082\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u001e\u001a\u00020\u001f8BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b \u0010!R\u0018\u0010\"\u001a\b\u0012\u0004\u0012\u00020\b0\u00078\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0014\u0010#\u001a\u00020$8BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b%\u0010&R\u000e\u0010'\u001a\u00020\u0013X\u0082\u000e¢\u0006\u0002\n\u0000R\u0018\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u00078\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u000e\u0010(\u001a\u00020\u0013X\u0082\u000e¢\u0006\u0002\n\u0000R\u0018\u0010)\u001a\b\u0012\u0004\u0012\u00020\b0\u00078\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0018\u0010*\u001a\b\u0012\u0004\u0012\u00020\b0\u00078\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0014\u0010+\u001a\u0004\u0018\u00010,8\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0012\u0010-\u001a\u00020\u00168\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0018\u0010\t\u001a\b\u0012\u0004\u0012\u00020\b0\u00078\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0011\u0010.\u001a\u00020\u00138F¢\u0006\u0006\u001a\u0004\b/\u00100R\u0018\u00101\u001a\b\u0012\u0004\u0012\u00020\b0\u00078\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000¨\u0006Y"}, d2 = {"Lcom/permissionx/guolindev/request/PermissionBuilder;", "", "fragmentActivity", "Landroidx/fragment/app/FragmentActivity;", "fragment", "Landroidx/fragment/app/Fragment;", "normalPermissions", "", "", "specialPermissions", "(Landroidx/fragment/app/FragmentActivity;Landroidx/fragment/app/Fragment;Ljava/util/Set;Ljava/util/Set;)V", "activity", "getActivity", "()Landroidx/fragment/app/FragmentActivity;", "setActivity", "(Landroidx/fragment/app/FragmentActivity;)V", "currentDialog", "Landroid/app/Dialog;", "darkColor", "", "deniedPermissions", "explainReasonBeforeRequest", "", "explainReasonCallback", "Lcom/permissionx/guolindev/callback/ExplainReasonCallback;", "explainReasonCallbackWithBeforeParam", "Lcom/permissionx/guolindev/callback/ExplainReasonCallbackWithBeforeParam;", "forwardPermissions", "forwardToSettingsCallback", "Lcom/permissionx/guolindev/callback/ForwardToSettingsCallback;", "fragmentManager", "Landroidx/fragment/app/FragmentManager;", "getFragmentManager", "()Landroidx/fragment/app/FragmentManager;", "grantedPermissions", "invisibleFragment", "Lcom/permissionx/guolindev/request/InvisibleFragment;", "getInvisibleFragment", "()Lcom/permissionx/guolindev/request/InvisibleFragment;", "lightColor", "originRequestOrientation", "permanentDeniedPermissions", "permissionsWontRequest", "requestCallback", "Lcom/permissionx/guolindev/callback/RequestCallback;", "showDialogCalled", "targetSdkVersion", "getTargetSdkVersion", "()I", "tempPermanentDeniedPermissions", "endRequest", "", "endRequest$permissionx_release", "forwardToSettings", "permissions", "", "lockOrientation", "onExplainRequestReason", "callback", "onForwardToSettings", "removeInvisibleFragment", "request", "requestAccessBackgroundLocationNow", "chainTask", "Lcom/permissionx/guolindev/request/ChainTask;", "requestInstallPackagePermissionNow", "requestManageExternalStoragePermissionNow", "requestNow", "", "requestSystemAlertWindowPermissionNow", "requestWriteSettingsPermissionNow", "restoreOrientation", "setDialogTintColor", "shouldRequestBackgroundLocationPermission", "shouldRequestInstallPackagesPermission", "shouldRequestManageExternalStoragePermission", "shouldRequestSystemAlertWindowPermission", "shouldRequestWriteSettingsPermission", "showHandlePermissionDialog", "showReasonOrGoSettings", "dialog", "Lcom/permissionx/guolindev/dialog/RationaleDialog;", "dialogFragment", "Lcom/permissionx/guolindev/dialog/RationaleDialogFragment;", "message", "positiveText", "negativeText", "startRequest", "Companion", "permissionx_release"}, k = 1, mv = {1, 5, 1}, xi = 48)
/* loaded from: classes2.dex */
public final class PermissionBuilder {
    public static final Companion Companion = new Companion(null);
    private static final String FRAGMENT_TAG = "InvisibleFragment";
    private static boolean inRequestFlow;
    public FragmentActivity activity;
    public Dialog currentDialog;
    private int darkColor;
    public Set<String> deniedPermissions;
    public boolean explainReasonBeforeRequest;
    public ExplainReasonCallback explainReasonCallback;
    public ExplainReasonCallbackWithBeforeParam explainReasonCallbackWithBeforeParam;
    public Set<String> forwardPermissions;
    public ForwardToSettingsCallback forwardToSettingsCallback;
    private Fragment fragment;
    public Set<String> grantedPermissions;
    private int lightColor;
    public Set<String> normalPermissions;
    private int originRequestOrientation;
    public Set<String> permanentDeniedPermissions;
    public Set<String> permissionsWontRequest;
    public RequestCallback requestCallback;
    public boolean showDialogCalled;
    public Set<String> specialPermissions;
    public Set<String> tempPermanentDeniedPermissions;

    public PermissionBuilder(FragmentActivity fragmentActivity, Fragment fragment, Set<String> normalPermissions, Set<String> specialPermissions) {
        Intrinsics.checkNotNullParameter(normalPermissions, "normalPermissions");
        Intrinsics.checkNotNullParameter(specialPermissions, "specialPermissions");
        this.lightColor = -1;
        this.darkColor = -1;
        this.originRequestOrientation = -1;
        this.permissionsWontRequest = new LinkedHashSet();
        this.grantedPermissions = new LinkedHashSet();
        this.deniedPermissions = new LinkedHashSet();
        this.permanentDeniedPermissions = new LinkedHashSet();
        this.tempPermanentDeniedPermissions = new LinkedHashSet();
        this.forwardPermissions = new LinkedHashSet();
        if (fragmentActivity != null) {
            setActivity(fragmentActivity);
        }
        if (fragmentActivity == null && fragment != null) {
            FragmentActivity requireActivity = fragment.requireActivity();
            Intrinsics.checkNotNullExpressionValue(requireActivity, "fragment.requireActivity()");
            setActivity(requireActivity);
        }
        this.fragment = fragment;
        this.normalPermissions = normalPermissions;
        this.specialPermissions = specialPermissions;
    }

    public final FragmentActivity getActivity() {
        FragmentActivity fragmentActivity = this.activity;
        if (fragmentActivity != null) {
            return fragmentActivity;
        }
        Intrinsics.throwUninitializedPropertyAccessException("activity");
        return null;
    }

    public final void setActivity(FragmentActivity fragmentActivity) {
        Intrinsics.checkNotNullParameter(fragmentActivity, "<set-?>");
        this.activity = fragmentActivity;
    }

    private final FragmentManager getFragmentManager() {
        Fragment fragment = this.fragment;
        FragmentManager childFragmentManager = fragment == null ? null : fragment.getChildFragmentManager();
        if (childFragmentManager == null) {
            FragmentManager supportFragmentManager = getActivity().getSupportFragmentManager();
            Intrinsics.checkNotNullExpressionValue(supportFragmentManager, "activity.supportFragmentManager");
            return supportFragmentManager;
        }
        return childFragmentManager;
    }

    private final InvisibleFragment getInvisibleFragment() {
        Fragment findFragmentByTag = getFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        if (findFragmentByTag != null) {
            return (InvisibleFragment) findFragmentByTag;
        }
        InvisibleFragment invisibleFragment = new InvisibleFragment();
        getFragmentManager().beginTransaction().add(invisibleFragment, FRAGMENT_TAG).commitNowAllowingStateLoss();
        return invisibleFragment;
    }

    public final int getTargetSdkVersion() {
        return getActivity().getApplicationInfo().targetSdkVersion;
    }

    public final PermissionBuilder onExplainRequestReason(ExplainReasonCallback explainReasonCallback) {
        this.explainReasonCallback = explainReasonCallback;
        return this;
    }

    public final PermissionBuilder onExplainRequestReason(ExplainReasonCallbackWithBeforeParam explainReasonCallbackWithBeforeParam) {
        this.explainReasonCallbackWithBeforeParam = explainReasonCallbackWithBeforeParam;
        return this;
    }

    public final PermissionBuilder onForwardToSettings(ForwardToSettingsCallback forwardToSettingsCallback) {
        this.forwardToSettingsCallback = forwardToSettingsCallback;
        return this;
    }

    public final PermissionBuilder explainReasonBeforeRequest() {
        this.explainReasonBeforeRequest = true;
        return this;
    }

    public final PermissionBuilder setDialogTintColor(int i, int i2) {
        this.lightColor = i;
        this.darkColor = i2;
        return this;
    }

    public final void request(RequestCallback requestCallback) {
        this.requestCallback = requestCallback;
        startRequest();
    }

    public final void showHandlePermissionDialog(ChainTask chainTask, boolean z, List<String> permissions, String message, String positiveText, String str) {
        Intrinsics.checkNotNullParameter(chainTask, "chainTask");
        Intrinsics.checkNotNullParameter(permissions, "permissions");
        Intrinsics.checkNotNullParameter(message, "message");
        Intrinsics.checkNotNullParameter(positiveText, "positiveText");
        showHandlePermissionDialog(chainTask, z, new DefaultDialog(getActivity(), permissions, message, positiveText, str, this.lightColor, this.darkColor));
    }

    public final void showHandlePermissionDialog(final ChainTask chainTask, final boolean z, final RationaleDialog dialog) {
        Intrinsics.checkNotNullParameter(chainTask, "chainTask");
        Intrinsics.checkNotNullParameter(dialog, "dialog");
        this.showDialogCalled = true;
        final List<String> permissionsToRequest = dialog.getPermissionsToRequest();
        Intrinsics.checkNotNullExpressionValue(permissionsToRequest, "dialog.permissionsToRequest");
        if (permissionsToRequest.isEmpty()) {
            chainTask.finish();
            return;
        }
        this.currentDialog = dialog;
        dialog.show();
        if ((dialog instanceof DefaultDialog) && ((DefaultDialog) dialog).isPermissionLayoutEmpty$permissionx_release()) {
            dialog.dismiss();
            chainTask.finish();
        }
        View positiveButton = dialog.getPositiveButton();
        Intrinsics.checkNotNullExpressionValue(positiveButton, "dialog.positiveButton");
        View negativeButton = dialog.getNegativeButton();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        positiveButton.setClickable(true);
        positiveButton.setOnClickListener(new View.OnClickListener() { // from class: com.permissionx.guolindev.request.-$$Lambda$PermissionBuilder$uz5q09bKYdOmJDE8SO186N6Iikw
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PermissionBuilder.m45showHandlePermissionDialog$lambda0(RationaleDialog.this, z, chainTask, permissionsToRequest, this, view);
            }
        });
        if (negativeButton != null) {
            negativeButton.setClickable(true);
            negativeButton.setOnClickListener(new View.OnClickListener() { // from class: com.permissionx.guolindev.request.-$$Lambda$PermissionBuilder$2fM8AZBC3afP91Vb8dH7uD4M0DI
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    PermissionBuilder.m46showHandlePermissionDialog$lambda1(RationaleDialog.this, chainTask, view);
                }
            });
        }
        Dialog dialog2 = this.currentDialog;
        if (dialog2 == null) {
            return;
        }
        dialog2.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.permissionx.guolindev.request.-$$Lambda$PermissionBuilder$h5YLp2A-1FdRklk40ZOxzrjCsqk
            @Override // android.content.DialogInterface.OnDismissListener
            public final void onDismiss(DialogInterface dialogInterface) {
                PermissionBuilder.m47showHandlePermissionDialog$lambda2(PermissionBuilder.this, dialogInterface);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: showHandlePermissionDialog$lambda-0  reason: not valid java name */
    public static final void m45showHandlePermissionDialog$lambda0(RationaleDialog dialog, boolean z, ChainTask chainTask, List permissions, PermissionBuilder this$0, View view) {
        Intrinsics.checkNotNullParameter(dialog, "$dialog");
        Intrinsics.checkNotNullParameter(chainTask, "$chainTask");
        Intrinsics.checkNotNullParameter(permissions, "$permissions");
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        dialog.dismiss();
        if (z) {
            chainTask.requestAgain(permissions);
        } else {
            this$0.forwardToSettings(permissions);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: showHandlePermissionDialog$lambda-1  reason: not valid java name */
    public static final void m46showHandlePermissionDialog$lambda1(RationaleDialog dialog, ChainTask chainTask, View view) {
        Intrinsics.checkNotNullParameter(dialog, "$dialog");
        Intrinsics.checkNotNullParameter(chainTask, "$chainTask");
        dialog.dismiss();
        chainTask.finish();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: showHandlePermissionDialog$lambda-2  reason: not valid java name */
    public static final void m47showHandlePermissionDialog$lambda2(PermissionBuilder this$0, DialogInterface dialogInterface) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        this$0.currentDialog = null;
    }

    public final void showHandlePermissionDialog(final ChainTask chainTask, final boolean z, final RationaleDialogFragment dialogFragment) {
        Intrinsics.checkNotNullParameter(chainTask, "chainTask");
        Intrinsics.checkNotNullParameter(dialogFragment, "dialogFragment");
        this.showDialogCalled = true;
        final List<String> permissionsToRequest = dialogFragment.getPermissionsToRequest();
        Intrinsics.checkNotNullExpressionValue(permissionsToRequest, "dialogFragment.permissionsToRequest");
        if (permissionsToRequest.isEmpty()) {
            chainTask.finish();
            return;
        }
        dialogFragment.showNow(getFragmentManager(), "PermissionXRationaleDialogFragment");
        View positiveButton = dialogFragment.getPositiveButton();
        Intrinsics.checkNotNullExpressionValue(positiveButton, "dialogFragment.positiveButton");
        View negativeButton = dialogFragment.getNegativeButton();
        dialogFragment.setCancelable(false);
        positiveButton.setClickable(true);
        positiveButton.setOnClickListener(new View.OnClickListener() { // from class: com.permissionx.guolindev.request.-$$Lambda$PermissionBuilder$Lryy70HiIObb0uxPBzOJHCEctvI
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PermissionBuilder.m48showHandlePermissionDialog$lambda3(RationaleDialogFragment.this, z, chainTask, permissionsToRequest, this, view);
            }
        });
        if (negativeButton != null) {
            negativeButton.setClickable(true);
            negativeButton.setOnClickListener(new View.OnClickListener() { // from class: com.permissionx.guolindev.request.-$$Lambda$PermissionBuilder$O9Hp_-9YuEjwy3MT2jVR8bjTDnE
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    PermissionBuilder.m49showHandlePermissionDialog$lambda4(RationaleDialogFragment.this, chainTask, view);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: showHandlePermissionDialog$lambda-3  reason: not valid java name */
    public static final void m48showHandlePermissionDialog$lambda3(RationaleDialogFragment dialogFragment, boolean z, ChainTask chainTask, List permissions, PermissionBuilder this$0, View view) {
        Intrinsics.checkNotNullParameter(dialogFragment, "$dialogFragment");
        Intrinsics.checkNotNullParameter(chainTask, "$chainTask");
        Intrinsics.checkNotNullParameter(permissions, "$permissions");
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        dialogFragment.dismiss();
        if (z) {
            chainTask.requestAgain(permissions);
        } else {
            this$0.forwardToSettings(permissions);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: showHandlePermissionDialog$lambda-4  reason: not valid java name */
    public static final void m49showHandlePermissionDialog$lambda4(RationaleDialogFragment dialogFragment, ChainTask chainTask, View view) {
        Intrinsics.checkNotNullParameter(dialogFragment, "$dialogFragment");
        Intrinsics.checkNotNullParameter(chainTask, "$chainTask");
        dialogFragment.dismiss();
        chainTask.finish();
    }

    public final void requestNow(Set<String> permissions, ChainTask chainTask) {
        Intrinsics.checkNotNullParameter(permissions, "permissions");
        Intrinsics.checkNotNullParameter(chainTask, "chainTask");
        getInvisibleFragment().requestNow(this, permissions, chainTask);
    }

    public final void requestAccessBackgroundLocationNow(ChainTask chainTask) {
        Intrinsics.checkNotNullParameter(chainTask, "chainTask");
        getInvisibleFragment().requestAccessBackgroundLocationNow(this, chainTask);
    }

    public final void requestSystemAlertWindowPermissionNow(ChainTask chainTask) {
        Intrinsics.checkNotNullParameter(chainTask, "chainTask");
        getInvisibleFragment().requestSystemAlertWindowPermissionNow(this, chainTask);
    }

    public final void requestWriteSettingsPermissionNow(ChainTask chainTask) {
        Intrinsics.checkNotNullParameter(chainTask, "chainTask");
        getInvisibleFragment().requestWriteSettingsPermissionNow(this, chainTask);
    }

    public final void requestManageExternalStoragePermissionNow(ChainTask chainTask) {
        Intrinsics.checkNotNullParameter(chainTask, "chainTask");
        getInvisibleFragment().requestManageExternalStoragePermissionNow(this, chainTask);
    }

    public final void requestInstallPackagePermissionNow(ChainTask chainTask) {
        Intrinsics.checkNotNullParameter(chainTask, "chainTask");
        getInvisibleFragment().requestInstallPackagesPermissionNow(this, chainTask);
    }

    public final boolean shouldRequestBackgroundLocationPermission() {
        return this.specialPermissions.contains(RequestBackgroundLocationPermission.ACCESS_BACKGROUND_LOCATION);
    }

    public final boolean shouldRequestSystemAlertWindowPermission() {
        return this.specialPermissions.contains("android.permission.SYSTEM_ALERT_WINDOW");
    }

    public final boolean shouldRequestWriteSettingsPermission() {
        return this.specialPermissions.contains("android.permission.WRITE_SETTINGS");
    }

    public final boolean shouldRequestManageExternalStoragePermission() {
        return this.specialPermissions.contains(RequestManageExternalStoragePermission.MANAGE_EXTERNAL_STORAGE);
    }

    public final boolean shouldRequestInstallPackagesPermission() {
        return this.specialPermissions.contains(RequestInstallPackagesPermission.REQUEST_INSTALL_PACKAGES);
    }

    private final void startRequest() {
        if (inRequestFlow) {
            return;
        }
        inRequestFlow = true;
        lockOrientation();
        RequestChain requestChain = new RequestChain();
        requestChain.addTaskToChain$permissionx_release(new RequestNormalPermissions(this));
        requestChain.addTaskToChain$permissionx_release(new RequestBackgroundLocationPermission(this));
        requestChain.addTaskToChain$permissionx_release(new RequestSystemAlertWindowPermission(this));
        requestChain.addTaskToChain$permissionx_release(new RequestWriteSettingsPermission(this));
        requestChain.addTaskToChain$permissionx_release(new RequestManageExternalStoragePermission(this));
        requestChain.addTaskToChain$permissionx_release(new RequestInstallPackagesPermission(this));
        requestChain.runTask$permissionx_release();
    }

    private final void removeInvisibleFragment() {
        Fragment findFragmentByTag = getFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        if (findFragmentByTag != null) {
            getFragmentManager().beginTransaction().remove(findFragmentByTag).commitNowAllowingStateLoss();
        }
    }

    private final void restoreOrientation() {
        if (Build.VERSION.SDK_INT != 26) {
            getActivity().setRequestedOrientation(this.originRequestOrientation);
        }
    }

    private final void lockOrientation() {
        if (Build.VERSION.SDK_INT != 26) {
            this.originRequestOrientation = getActivity().getRequestedOrientation();
            int i = getActivity().getResources().getConfiguration().orientation;
            if (i == 1) {
                getActivity().setRequestedOrientation(7);
            } else if (i != 2) {
            } else {
                getActivity().setRequestedOrientation(6);
            }
        }
    }

    private final void forwardToSettings(List<String> list) {
        this.forwardPermissions.clear();
        this.forwardPermissions.addAll(list);
        getInvisibleFragment().forwardToSettings();
    }

    public final void endRequest$permissionx_release() {
        removeInvisibleFragment();
        restoreOrientation();
        inRequestFlow = false;
    }

    /* compiled from: PermissionBuilder.kt */
    @Metadata(d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u0007"}, d2 = {"Lcom/permissionx/guolindev/request/PermissionBuilder$Companion;", "", "()V", "FRAGMENT_TAG", "", "inRequestFlow", "", "permissionx_release"}, k = 1, mv = {1, 5, 1}, xi = 48)
    /* loaded from: classes2.dex */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }
    }
}

package com.permissionx.guolindev.request;

import android.app.Dialog;
import android.car.Car;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import com.permissionx.guolindev.callback.ExplainReasonCallback;
import com.permissionx.guolindev.callback.ExplainReasonCallbackWithBeforeParam;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
/* compiled from: InvisibleFragment.kt */
@Metadata(d1 = {"\u0000`\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u0011\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0007\n\u0002\u0010$\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\"\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\b\u0010\u0015\u001a\u00020\u0016H\u0002J\u0006\u0010\u0017\u001a\u00020\u0018J\b\u0010\u0019\u001a\u00020\u0018H\u0016J\u0010\u0010\u001a\u001a\u00020\u00182\u0006\u0010\u001b\u001a\u00020\u0016H\u0002J\b\u0010\u001c\u001a\u00020\u0018H\u0002J\b\u0010\u001d\u001a\u00020\u0018H\u0002J\u001c\u0010\u001e\u001a\u00020\u00182\u0012\u0010\u001f\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u00160 H\u0002J\b\u0010!\u001a\u00020\u0018H\u0002J\b\u0010\"\u001a\u00020\u0018H\u0002J\u0016\u0010#\u001a\u00020\u00182\f\u0010$\u001a\b\u0012\u0004\u0012\u00020\u00180%H\u0002J\u0016\u0010&\u001a\u00020\u00182\u0006\u0010'\u001a\u00020\n2\u0006\u0010(\u001a\u00020\u0014J\u0016\u0010)\u001a\u00020\u00182\u0006\u0010'\u001a\u00020\n2\u0006\u0010(\u001a\u00020\u0014J\u0016\u0010*\u001a\u00020\u00182\u0006\u0010'\u001a\u00020\n2\u0006\u0010(\u001a\u00020\u0014J$\u0010+\u001a\u00020\u00182\u0006\u0010'\u001a\u00020\n2\f\u0010,\u001a\b\u0012\u0004\u0012\u00020\f0-2\u0006\u0010(\u001a\u00020\u0014J\u0016\u0010.\u001a\u00020\u00182\u0006\u0010'\u001a\u00020\n2\u0006\u0010(\u001a\u00020\u0014J\u0016\u0010/\u001a\u00020\u00182\u0006\u0010'\u001a\u00020\n2\u0006\u0010(\u001a\u00020\u0014R\u001c\u0010\u0003\u001a\u0010\u0012\f\u0012\n \u0006*\u0004\u0018\u00010\u00050\u00050\u0004X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082.¢\u0006\u0002\n\u0000R\u001c\u0010\u000b\u001a\u0010\u0012\f\u0012\n \u0006*\u0004\u0018\u00010\f0\f0\u0004X\u0082\u0004¢\u0006\u0002\n\u0000R\u001c\u0010\r\u001a\u0010\u0012\f\u0012\n \u0006*\u0004\u0018\u00010\u00050\u00050\u0004X\u0082\u0004¢\u0006\u0002\n\u0000R\u001c\u0010\u000e\u001a\u0010\u0012\f\u0012\n \u0006*\u0004\u0018\u00010\u00050\u00050\u0004X\u0082\u0004¢\u0006\u0002\n\u0000R:\u0010\u000f\u001a.\u0012*\u0012(\u0012\f\u0012\n \u0006*\u0004\u0018\u00010\f0\f \u0006*\u0014\u0012\u000e\b\u0001\u0012\n \u0006*\u0004\u0018\u00010\f0\f\u0018\u00010\u00100\u00100\u0004X\u0082\u0004¢\u0006\u0002\n\u0000R\u001c\u0010\u0011\u001a\u0010\u0012\f\u0012\n \u0006*\u0004\u0018\u00010\u00050\u00050\u0004X\u0082\u0004¢\u0006\u0002\n\u0000R\u001c\u0010\u0012\u001a\u0010\u0012\f\u0012\n \u0006*\u0004\u0018\u00010\u00050\u00050\u0004X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0014X\u0082.¢\u0006\u0002\n\u0000¨\u00060"}, d2 = {"Lcom/permissionx/guolindev/request/InvisibleFragment;", "Landroidx/fragment/app/Fragment;", "()V", "forwardToSettingsLauncher", "Landroidx/activity/result/ActivityResultLauncher;", "Landroid/content/Intent;", "kotlin.jvm.PlatformType", "handler", "Landroid/os/Handler;", "pb", "Lcom/permissionx/guolindev/request/PermissionBuilder;", "requestBackgroundLocationLauncher", "", "requestInstallPackagesLauncher", "requestManageExternalStorageLauncher", "requestNormalPermissionLauncher", "", "requestSystemAlertWindowLauncher", "requestWriteSettingsLauncher", "task", "Lcom/permissionx/guolindev/request/ChainTask;", "checkForGC", "", "forwardToSettings", "", "onDestroy", "onRequestBackgroundLocationPermissionResult", "granted", "onRequestInstallPackagesPermissionResult", "onRequestManageExternalStoragePermissionResult", "onRequestNormalPermissionsResult", "grantResults", "", "onRequestSystemAlertWindowPermissionResult", "onRequestWriteSettingsPermissionResult", "postForResult", "callback", "Lkotlin/Function0;", "requestAccessBackgroundLocationNow", "permissionBuilder", "chainTask", "requestInstallPackagesPermissionNow", "requestManageExternalStoragePermissionNow", "requestNow", "permissions", "", "requestSystemAlertWindowPermissionNow", "requestWriteSettingsPermissionNow", "permissionx_release"}, k = 1, mv = {1, 5, 1}, xi = 48)
/* loaded from: classes2.dex */
public final class InvisibleFragment extends Fragment {
    private final ActivityResultLauncher<Intent> forwardToSettingsLauncher;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private PermissionBuilder pb;
    private final ActivityResultLauncher<String> requestBackgroundLocationLauncher;
    private final ActivityResultLauncher<Intent> requestInstallPackagesLauncher;
    private final ActivityResultLauncher<Intent> requestManageExternalStorageLauncher;
    private final ActivityResultLauncher<String[]> requestNormalPermissionLauncher;
    private final ActivityResultLauncher<Intent> requestSystemAlertWindowLauncher;
    private final ActivityResultLauncher<Intent> requestWriteSettingsLauncher;
    private ChainTask task;

    public InvisibleFragment() {
        ActivityResultLauncher<String[]> registerForActivityResult = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback() { // from class: com.permissionx.guolindev.request.-$$Lambda$InvisibleFragment$N68S7qVgI-LTwmw3MPog02_dGbo
            @Override // androidx.activity.result.ActivityResultCallback
            public final void onActivityResult(Object obj) {
                InvisibleFragment.m40requestNormalPermissionLauncher$lambda0(InvisibleFragment.this, (Map) obj);
            }
        });
        Intrinsics.checkNotNullExpressionValue(registerForActivityResult, "registerForActivityResul…)\n            }\n        }");
        this.requestNormalPermissionLauncher = registerForActivityResult;
        ActivityResultLauncher<String> registerForActivityResult2 = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback() { // from class: com.permissionx.guolindev.request.-$$Lambda$InvisibleFragment$nU7ee_GW92jxQglzcNdg5-6XLq8
            @Override // androidx.activity.result.ActivityResultCallback
            public final void onActivityResult(Object obj) {
                InvisibleFragment.m37requestBackgroundLocationLauncher$lambda1(InvisibleFragment.this, (Boolean) obj);
            }
        });
        Intrinsics.checkNotNullExpressionValue(registerForActivityResult2, "registerForActivityResul…)\n            }\n        }");
        this.requestBackgroundLocationLauncher = registerForActivityResult2;
        ActivityResultLauncher<Intent> registerForActivityResult3 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback() { // from class: com.permissionx.guolindev.request.-$$Lambda$InvisibleFragment$XCcOAK6buHHj5QnuNVd1utNrASw
            @Override // androidx.activity.result.ActivityResultCallback
            public final void onActivityResult(Object obj) {
                InvisibleFragment.m41requestSystemAlertWindowLauncher$lambda2(InvisibleFragment.this, (ActivityResult) obj);
            }
        });
        Intrinsics.checkNotNullExpressionValue(registerForActivityResult3, "registerForActivityResul…)\n            }\n        }");
        this.requestSystemAlertWindowLauncher = registerForActivityResult3;
        ActivityResultLauncher<Intent> registerForActivityResult4 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback() { // from class: com.permissionx.guolindev.request.-$$Lambda$InvisibleFragment$VPjEktZJvMaHa6R0rqR4VOmwcx4
            @Override // androidx.activity.result.ActivityResultCallback
            public final void onActivityResult(Object obj) {
                InvisibleFragment.m42requestWriteSettingsLauncher$lambda3(InvisibleFragment.this, (ActivityResult) obj);
            }
        });
        Intrinsics.checkNotNullExpressionValue(registerForActivityResult4, "registerForActivityResul…)\n            }\n        }");
        this.requestWriteSettingsLauncher = registerForActivityResult4;
        ActivityResultLauncher<Intent> registerForActivityResult5 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback() { // from class: com.permissionx.guolindev.request.-$$Lambda$InvisibleFragment$XtuD2zgSi2sNTn1-KHBU_KOc9no
            @Override // androidx.activity.result.ActivityResultCallback
            public final void onActivityResult(Object obj) {
                InvisibleFragment.m39requestManageExternalStorageLauncher$lambda4(InvisibleFragment.this, (ActivityResult) obj);
            }
        });
        Intrinsics.checkNotNullExpressionValue(registerForActivityResult5, "registerForActivityResul…)\n            }\n        }");
        this.requestManageExternalStorageLauncher = registerForActivityResult5;
        ActivityResultLauncher<Intent> registerForActivityResult6 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback() { // from class: com.permissionx.guolindev.request.-$$Lambda$InvisibleFragment$dJHFYY-aw74QFRQaldTPHZA0hfA
            @Override // androidx.activity.result.ActivityResultCallback
            public final void onActivityResult(Object obj) {
                InvisibleFragment.m38requestInstallPackagesLauncher$lambda5(InvisibleFragment.this, (ActivityResult) obj);
            }
        });
        Intrinsics.checkNotNullExpressionValue(registerForActivityResult6, "registerForActivityResul…)\n            }\n        }");
        this.requestInstallPackagesLauncher = registerForActivityResult6;
        ActivityResultLauncher<Intent> registerForActivityResult7 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback() { // from class: com.permissionx.guolindev.request.-$$Lambda$InvisibleFragment$Ied-m49zCABuAaqE-xwUtovEl9A
            @Override // androidx.activity.result.ActivityResultCallback
            public final void onActivityResult(Object obj) {
                InvisibleFragment.m30forwardToSettingsLauncher$lambda6(InvisibleFragment.this, (ActivityResult) obj);
            }
        });
        Intrinsics.checkNotNullExpressionValue(registerForActivityResult7, "registerForActivityResul…)\n            }\n        }");
        this.forwardToSettingsLauncher = registerForActivityResult7;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: requestNormalPermissionLauncher$lambda-0  reason: not valid java name */
    public static final void m40requestNormalPermissionLauncher$lambda0(final InvisibleFragment this$0, final Map map) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        this$0.postForResult(new Function0<Unit>() { // from class: com.permissionx.guolindev.request.InvisibleFragment$requestNormalPermissionLauncher$1$1
            /* JADX INFO: Access modifiers changed from: package-private */
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(0);
            }

            @Override // kotlin.jvm.functions.Function0
            public /* bridge */ /* synthetic */ Unit invoke() {
                invoke2();
                return Unit.INSTANCE;
            }

            /* renamed from: invoke  reason: avoid collision after fix types in other method */
            public final void invoke2() {
                InvisibleFragment invisibleFragment = InvisibleFragment.this;
                Map<String, Boolean> grantResults = map;
                Intrinsics.checkNotNullExpressionValue(grantResults, "grantResults");
                invisibleFragment.onRequestNormalPermissionsResult(grantResults);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: requestBackgroundLocationLauncher$lambda-1  reason: not valid java name */
    public static final void m37requestBackgroundLocationLauncher$lambda1(final InvisibleFragment this$0, final Boolean bool) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        this$0.postForResult(new Function0<Unit>() { // from class: com.permissionx.guolindev.request.InvisibleFragment$requestBackgroundLocationLauncher$1$1
            /* JADX INFO: Access modifiers changed from: package-private */
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(0);
            }

            @Override // kotlin.jvm.functions.Function0
            public /* bridge */ /* synthetic */ Unit invoke() {
                invoke2();
                return Unit.INSTANCE;
            }

            /* renamed from: invoke  reason: avoid collision after fix types in other method */
            public final void invoke2() {
                InvisibleFragment invisibleFragment = InvisibleFragment.this;
                Boolean granted = bool;
                Intrinsics.checkNotNullExpressionValue(granted, "granted");
                invisibleFragment.onRequestBackgroundLocationPermissionResult(granted.booleanValue());
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: requestSystemAlertWindowLauncher$lambda-2  reason: not valid java name */
    public static final void m41requestSystemAlertWindowLauncher$lambda2(final InvisibleFragment this$0, ActivityResult activityResult) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        this$0.postForResult(new Function0<Unit>() { // from class: com.permissionx.guolindev.request.InvisibleFragment$requestSystemAlertWindowLauncher$1$1
            /* JADX INFO: Access modifiers changed from: package-private */
            {
                super(0);
            }

            @Override // kotlin.jvm.functions.Function0
            public /* bridge */ /* synthetic */ Unit invoke() {
                invoke2();
                return Unit.INSTANCE;
            }

            /* renamed from: invoke  reason: avoid collision after fix types in other method */
            public final void invoke2() {
                InvisibleFragment.this.onRequestSystemAlertWindowPermissionResult();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: requestWriteSettingsLauncher$lambda-3  reason: not valid java name */
    public static final void m42requestWriteSettingsLauncher$lambda3(final InvisibleFragment this$0, ActivityResult activityResult) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        this$0.postForResult(new Function0<Unit>() { // from class: com.permissionx.guolindev.request.InvisibleFragment$requestWriteSettingsLauncher$1$1
            /* JADX INFO: Access modifiers changed from: package-private */
            {
                super(0);
            }

            @Override // kotlin.jvm.functions.Function0
            public /* bridge */ /* synthetic */ Unit invoke() {
                invoke2();
                return Unit.INSTANCE;
            }

            /* renamed from: invoke  reason: avoid collision after fix types in other method */
            public final void invoke2() {
                InvisibleFragment.this.onRequestWriteSettingsPermissionResult();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: requestManageExternalStorageLauncher$lambda-4  reason: not valid java name */
    public static final void m39requestManageExternalStorageLauncher$lambda4(final InvisibleFragment this$0, ActivityResult activityResult) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        this$0.postForResult(new Function0<Unit>() { // from class: com.permissionx.guolindev.request.InvisibleFragment$requestManageExternalStorageLauncher$1$1
            /* JADX INFO: Access modifiers changed from: package-private */
            {
                super(0);
            }

            @Override // kotlin.jvm.functions.Function0
            public /* bridge */ /* synthetic */ Unit invoke() {
                invoke2();
                return Unit.INSTANCE;
            }

            /* renamed from: invoke  reason: avoid collision after fix types in other method */
            public final void invoke2() {
                InvisibleFragment.this.onRequestManageExternalStoragePermissionResult();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: requestInstallPackagesLauncher$lambda-5  reason: not valid java name */
    public static final void m38requestInstallPackagesLauncher$lambda5(final InvisibleFragment this$0, ActivityResult activityResult) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        this$0.postForResult(new Function0<Unit>() { // from class: com.permissionx.guolindev.request.InvisibleFragment$requestInstallPackagesLauncher$1$1
            /* JADX INFO: Access modifiers changed from: package-private */
            {
                super(0);
            }

            @Override // kotlin.jvm.functions.Function0
            public /* bridge */ /* synthetic */ Unit invoke() {
                invoke2();
                return Unit.INSTANCE;
            }

            /* renamed from: invoke  reason: avoid collision after fix types in other method */
            public final void invoke2() {
                InvisibleFragment.this.onRequestInstallPackagesPermissionResult();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: forwardToSettingsLauncher$lambda-6  reason: not valid java name */
    public static final void m30forwardToSettingsLauncher$lambda6(InvisibleFragment this$0, ActivityResult activityResult) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        if (this$0.checkForGC()) {
            ChainTask chainTask = this$0.task;
            PermissionBuilder permissionBuilder = null;
            if (chainTask == null) {
                Intrinsics.throwUninitializedPropertyAccessException("task");
                chainTask = null;
            }
            PermissionBuilder permissionBuilder2 = this$0.pb;
            if (permissionBuilder2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("pb");
            } else {
                permissionBuilder = permissionBuilder2;
            }
            chainTask.requestAgain(new ArrayList(permissionBuilder.forwardPermissions));
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final void requestNow(PermissionBuilder permissionBuilder, Set<String> permissions, ChainTask chainTask) {
        Intrinsics.checkNotNullParameter(permissionBuilder, "permissionBuilder");
        Intrinsics.checkNotNullParameter(permissions, "permissions");
        Intrinsics.checkNotNullParameter(chainTask, "chainTask");
        this.pb = permissionBuilder;
        this.task = chainTask;
        ActivityResultLauncher<String[]> activityResultLauncher = this.requestNormalPermissionLauncher;
        Object[] array = permissions.toArray(new String[0]);
        Objects.requireNonNull(array, "null cannot be cast to non-null type kotlin.Array<T>");
        activityResultLauncher.launch(array);
    }

    public final void requestAccessBackgroundLocationNow(PermissionBuilder permissionBuilder, ChainTask chainTask) {
        Intrinsics.checkNotNullParameter(permissionBuilder, "permissionBuilder");
        Intrinsics.checkNotNullParameter(chainTask, "chainTask");
        this.pb = permissionBuilder;
        this.task = chainTask;
        this.requestBackgroundLocationLauncher.launch(RequestBackgroundLocationPermission.ACCESS_BACKGROUND_LOCATION);
    }

    public final void requestSystemAlertWindowPermissionNow(PermissionBuilder permissionBuilder, ChainTask chainTask) {
        Intrinsics.checkNotNullParameter(permissionBuilder, "permissionBuilder");
        Intrinsics.checkNotNullParameter(chainTask, "chainTask");
        this.pb = permissionBuilder;
        this.task = chainTask;
        if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(getContext())) {
            Intent intent = new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION");
            intent.setData(Uri.parse(Intrinsics.stringPlus("package:", requireActivity().getPackageName())));
            this.requestSystemAlertWindowLauncher.launch(intent);
            return;
        }
        onRequestSystemAlertWindowPermissionResult();
    }

    public final void requestWriteSettingsPermissionNow(PermissionBuilder permissionBuilder, ChainTask chainTask) {
        Intrinsics.checkNotNullParameter(permissionBuilder, "permissionBuilder");
        Intrinsics.checkNotNullParameter(chainTask, "chainTask");
        this.pb = permissionBuilder;
        this.task = chainTask;
        if (Build.VERSION.SDK_INT >= 23 && !Settings.System.canWrite(getContext())) {
            Intent intent = new Intent("android.settings.action.MANAGE_WRITE_SETTINGS");
            intent.setData(Uri.parse(Intrinsics.stringPlus("package:", requireActivity().getPackageName())));
            this.requestWriteSettingsLauncher.launch(intent);
            return;
        }
        onRequestWriteSettingsPermissionResult();
    }

    public final void requestManageExternalStoragePermissionNow(PermissionBuilder permissionBuilder, ChainTask chainTask) {
        Intrinsics.checkNotNullParameter(permissionBuilder, "permissionBuilder");
        Intrinsics.checkNotNullParameter(chainTask, "chainTask");
        this.pb = permissionBuilder;
        this.task = chainTask;
        if (Build.VERSION.SDK_INT >= 30 && !Environment.isExternalStorageManager()) {
            this.requestManageExternalStorageLauncher.launch(new Intent("android.settings.MANAGE_ALL_FILES_ACCESS_PERMISSION"));
            return;
        }
        onRequestManageExternalStoragePermissionResult();
    }

    public final void requestInstallPackagesPermissionNow(PermissionBuilder permissionBuilder, ChainTask chainTask) {
        Intrinsics.checkNotNullParameter(permissionBuilder, "permissionBuilder");
        Intrinsics.checkNotNullParameter(chainTask, "chainTask");
        this.pb = permissionBuilder;
        this.task = chainTask;
        if (Build.VERSION.SDK_INT >= 26) {
            Intent intent = new Intent("android.settings.MANAGE_UNKNOWN_APP_SOURCES");
            intent.setData(Uri.parse(Intrinsics.stringPlus("package:", requireActivity().getPackageName())));
            this.requestInstallPackagesLauncher.launch(intent);
            return;
        }
        onRequestInstallPackagesPermissionResult();
    }

    public final void forwardToSettings() {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts(Car.PACKAGE_SERVICE, requireActivity().getPackageName(), null));
        this.forwardToSettingsLauncher.launch(intent);
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        if (checkForGC()) {
            PermissionBuilder permissionBuilder = this.pb;
            if (permissionBuilder == null) {
                Intrinsics.throwUninitializedPropertyAccessException("pb");
                permissionBuilder = null;
            }
            Dialog dialog = permissionBuilder.currentDialog;
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code restructure failed: missing block: B:123:0x01f9, code lost:
        if ((!r9.tempPermanentDeniedPermissions.isEmpty()) != false) goto L106;
     */
    /* JADX WARN: Code restructure failed: missing block: B:142:0x0244, code lost:
        if (r9.showDialogCalled == false) goto L132;
     */
    /* JADX WARN: Code restructure failed: missing block: B:79:0x014c, code lost:
        if (r9.explainReasonCallbackWithBeforeParam != null) goto L136;
     */
    /* JADX WARN: Removed duplicated region for block: B:138:0x023a  */
    /* JADX WARN: Removed duplicated region for block: B:145:0x024a  */
    /* JADX WARN: Removed duplicated region for block: B:149:0x0255  */
    /* JADX WARN: Removed duplicated region for block: B:150:0x0259  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final void onRequestNormalPermissionsResult(java.util.Map<java.lang.String, java.lang.Boolean> r9) {
        /*
            Method dump skipped, instructions count: 605
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.permissionx.guolindev.request.InvisibleFragment.onRequestNormalPermissionsResult(java.util.Map):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void onRequestBackgroundLocationPermissionResult(final boolean z) {
        if (checkForGC()) {
            postForResult(new Function0<Unit>() { // from class: com.permissionx.guolindev.request.InvisibleFragment$onRequestBackgroundLocationPermissionResult$1
                /* JADX INFO: Access modifiers changed from: package-private */
                /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                {
                    super(0);
                }

                @Override // kotlin.jvm.functions.Function0
                public /* bridge */ /* synthetic */ Unit invoke() {
                    invoke2();
                    return Unit.INSTANCE;
                }

                /* JADX WARN: Code restructure failed: missing block: B:27:0x0076, code lost:
                    if (r6.explainReasonCallbackWithBeforeParam != null) goto L56;
                 */
                /* JADX WARN: Removed duplicated region for block: B:63:0x0122  */
                /* JADX WARN: Removed duplicated region for block: B:70:0x013a  */
                /* JADX WARN: Removed duplicated region for block: B:71:0x013e  */
                /* renamed from: invoke  reason: avoid collision after fix types in other method */
                /*
                    Code decompiled incorrectly, please refer to instructions dump.
                    To view partially-correct add '--show-bad-code' argument
                */
                public final void invoke2() {
                    /*
                        Method dump skipped, instructions count: 323
                        To view this dump add '--comments-level debug' option
                    */
                    throw new UnsupportedOperationException("Method not decompiled: com.permissionx.guolindev.request.InvisibleFragment$onRequestBackgroundLocationPermissionResult$1.invoke2():void");
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void onRequestSystemAlertWindowPermissionResult() {
        if (checkForGC()) {
            ChainTask chainTask = null;
            if (Build.VERSION.SDK_INT >= 23) {
                if (Settings.canDrawOverlays(getContext())) {
                    ChainTask chainTask2 = this.task;
                    if (chainTask2 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("task");
                    } else {
                        chainTask = chainTask2;
                    }
                    chainTask.finish();
                    return;
                }
                PermissionBuilder permissionBuilder = this.pb;
                if (permissionBuilder == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("pb");
                    permissionBuilder = null;
                }
                if (permissionBuilder.explainReasonCallback == null) {
                    PermissionBuilder permissionBuilder2 = this.pb;
                    if (permissionBuilder2 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("pb");
                        permissionBuilder2 = null;
                    }
                    if (permissionBuilder2.explainReasonCallbackWithBeforeParam == null) {
                        return;
                    }
                }
                PermissionBuilder permissionBuilder3 = this.pb;
                if (permissionBuilder3 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("pb");
                    permissionBuilder3 = null;
                }
                if (permissionBuilder3.explainReasonCallbackWithBeforeParam != null) {
                    PermissionBuilder permissionBuilder4 = this.pb;
                    if (permissionBuilder4 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("pb");
                        permissionBuilder4 = null;
                    }
                    ExplainReasonCallbackWithBeforeParam explainReasonCallbackWithBeforeParam = permissionBuilder4.explainReasonCallbackWithBeforeParam;
                    Intrinsics.checkNotNull(explainReasonCallbackWithBeforeParam);
                    ChainTask chainTask3 = this.task;
                    if (chainTask3 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("task");
                    } else {
                        chainTask = chainTask3;
                    }
                    explainReasonCallbackWithBeforeParam.onExplainReason(chainTask.getExplainScope(), CollectionsKt.listOf("android.permission.SYSTEM_ALERT_WINDOW"), false);
                    return;
                }
                PermissionBuilder permissionBuilder5 = this.pb;
                if (permissionBuilder5 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("pb");
                    permissionBuilder5 = null;
                }
                ExplainReasonCallback explainReasonCallback = permissionBuilder5.explainReasonCallback;
                Intrinsics.checkNotNull(explainReasonCallback);
                ChainTask chainTask4 = this.task;
                if (chainTask4 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("task");
                } else {
                    chainTask = chainTask4;
                }
                explainReasonCallback.onExplainReason(chainTask.getExplainScope(), CollectionsKt.listOf("android.permission.SYSTEM_ALERT_WINDOW"));
                return;
            }
            ChainTask chainTask5 = this.task;
            if (chainTask5 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("task");
            } else {
                chainTask = chainTask5;
            }
            chainTask.finish();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void onRequestWriteSettingsPermissionResult() {
        if (checkForGC()) {
            postForResult(new Function0<Unit>() { // from class: com.permissionx.guolindev.request.InvisibleFragment$onRequestWriteSettingsPermissionResult$1
                /* JADX INFO: Access modifiers changed from: package-private */
                {
                    super(0);
                }

                @Override // kotlin.jvm.functions.Function0
                public /* bridge */ /* synthetic */ Unit invoke() {
                    invoke2();
                    return Unit.INSTANCE;
                }

                /* renamed from: invoke  reason: avoid collision after fix types in other method */
                public final void invoke2() {
                    ChainTask chainTask;
                    PermissionBuilder permissionBuilder;
                    PermissionBuilder permissionBuilder2;
                    PermissionBuilder permissionBuilder3;
                    ChainTask chainTask2;
                    PermissionBuilder permissionBuilder4;
                    ChainTask chainTask3;
                    PermissionBuilder permissionBuilder5;
                    ChainTask chainTask4;
                    ChainTask chainTask5 = null;
                    if (Build.VERSION.SDK_INT < 23) {
                        chainTask = InvisibleFragment.this.task;
                        if (chainTask == null) {
                            Intrinsics.throwUninitializedPropertyAccessException("task");
                        } else {
                            chainTask5 = chainTask;
                        }
                        chainTask5.finish();
                    } else if (Settings.System.canWrite(InvisibleFragment.this.getContext())) {
                        chainTask4 = InvisibleFragment.this.task;
                        if (chainTask4 == null) {
                            Intrinsics.throwUninitializedPropertyAccessException("task");
                        } else {
                            chainTask5 = chainTask4;
                        }
                        chainTask5.finish();
                    } else {
                        permissionBuilder = InvisibleFragment.this.pb;
                        if (permissionBuilder == null) {
                            Intrinsics.throwUninitializedPropertyAccessException("pb");
                            permissionBuilder = null;
                        }
                        if (permissionBuilder.explainReasonCallback == null) {
                            permissionBuilder5 = InvisibleFragment.this.pb;
                            if (permissionBuilder5 == null) {
                                Intrinsics.throwUninitializedPropertyAccessException("pb");
                                permissionBuilder5 = null;
                            }
                            if (permissionBuilder5.explainReasonCallbackWithBeforeParam == null) {
                                return;
                            }
                        }
                        permissionBuilder2 = InvisibleFragment.this.pb;
                        if (permissionBuilder2 == null) {
                            Intrinsics.throwUninitializedPropertyAccessException("pb");
                            permissionBuilder2 = null;
                        }
                        if (permissionBuilder2.explainReasonCallbackWithBeforeParam != null) {
                            permissionBuilder4 = InvisibleFragment.this.pb;
                            if (permissionBuilder4 == null) {
                                Intrinsics.throwUninitializedPropertyAccessException("pb");
                                permissionBuilder4 = null;
                            }
                            ExplainReasonCallbackWithBeforeParam explainReasonCallbackWithBeforeParam = permissionBuilder4.explainReasonCallbackWithBeforeParam;
                            Intrinsics.checkNotNull(explainReasonCallbackWithBeforeParam);
                            chainTask3 = InvisibleFragment.this.task;
                            if (chainTask3 == null) {
                                Intrinsics.throwUninitializedPropertyAccessException("task");
                            } else {
                                chainTask5 = chainTask3;
                            }
                            explainReasonCallbackWithBeforeParam.onExplainReason(chainTask5.getExplainScope(), CollectionsKt.listOf("android.permission.WRITE_SETTINGS"), false);
                            return;
                        }
                        permissionBuilder3 = InvisibleFragment.this.pb;
                        if (permissionBuilder3 == null) {
                            Intrinsics.throwUninitializedPropertyAccessException("pb");
                            permissionBuilder3 = null;
                        }
                        ExplainReasonCallback explainReasonCallback = permissionBuilder3.explainReasonCallback;
                        Intrinsics.checkNotNull(explainReasonCallback);
                        chainTask2 = InvisibleFragment.this.task;
                        if (chainTask2 == null) {
                            Intrinsics.throwUninitializedPropertyAccessException("task");
                        } else {
                            chainTask5 = chainTask2;
                        }
                        explainReasonCallback.onExplainReason(chainTask5.getExplainScope(), CollectionsKt.listOf("android.permission.WRITE_SETTINGS"));
                    }
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void onRequestManageExternalStoragePermissionResult() {
        if (checkForGC()) {
            postForResult(new Function0<Unit>() { // from class: com.permissionx.guolindev.request.InvisibleFragment$onRequestManageExternalStoragePermissionResult$1
                /* JADX INFO: Access modifiers changed from: package-private */
                {
                    super(0);
                }

                @Override // kotlin.jvm.functions.Function0
                public /* bridge */ /* synthetic */ Unit invoke() {
                    invoke2();
                    return Unit.INSTANCE;
                }

                /* renamed from: invoke  reason: avoid collision after fix types in other method */
                public final void invoke2() {
                    ChainTask chainTask;
                    PermissionBuilder permissionBuilder;
                    PermissionBuilder permissionBuilder2;
                    PermissionBuilder permissionBuilder3;
                    ChainTask chainTask2;
                    PermissionBuilder permissionBuilder4;
                    ChainTask chainTask3;
                    PermissionBuilder permissionBuilder5;
                    ChainTask chainTask4;
                    ChainTask chainTask5 = null;
                    if (Build.VERSION.SDK_INT < 30) {
                        chainTask = InvisibleFragment.this.task;
                        if (chainTask == null) {
                            Intrinsics.throwUninitializedPropertyAccessException("task");
                        } else {
                            chainTask5 = chainTask;
                        }
                        chainTask5.finish();
                    } else if (Environment.isExternalStorageManager()) {
                        chainTask4 = InvisibleFragment.this.task;
                        if (chainTask4 == null) {
                            Intrinsics.throwUninitializedPropertyAccessException("task");
                        } else {
                            chainTask5 = chainTask4;
                        }
                        chainTask5.finish();
                    } else {
                        permissionBuilder = InvisibleFragment.this.pb;
                        if (permissionBuilder == null) {
                            Intrinsics.throwUninitializedPropertyAccessException("pb");
                            permissionBuilder = null;
                        }
                        if (permissionBuilder.explainReasonCallback == null) {
                            permissionBuilder5 = InvisibleFragment.this.pb;
                            if (permissionBuilder5 == null) {
                                Intrinsics.throwUninitializedPropertyAccessException("pb");
                                permissionBuilder5 = null;
                            }
                            if (permissionBuilder5.explainReasonCallbackWithBeforeParam == null) {
                                return;
                            }
                        }
                        permissionBuilder2 = InvisibleFragment.this.pb;
                        if (permissionBuilder2 == null) {
                            Intrinsics.throwUninitializedPropertyAccessException("pb");
                            permissionBuilder2 = null;
                        }
                        if (permissionBuilder2.explainReasonCallbackWithBeforeParam != null) {
                            permissionBuilder4 = InvisibleFragment.this.pb;
                            if (permissionBuilder4 == null) {
                                Intrinsics.throwUninitializedPropertyAccessException("pb");
                                permissionBuilder4 = null;
                            }
                            ExplainReasonCallbackWithBeforeParam explainReasonCallbackWithBeforeParam = permissionBuilder4.explainReasonCallbackWithBeforeParam;
                            Intrinsics.checkNotNull(explainReasonCallbackWithBeforeParam);
                            chainTask3 = InvisibleFragment.this.task;
                            if (chainTask3 == null) {
                                Intrinsics.throwUninitializedPropertyAccessException("task");
                            } else {
                                chainTask5 = chainTask3;
                            }
                            explainReasonCallbackWithBeforeParam.onExplainReason(chainTask5.getExplainScope(), CollectionsKt.listOf(RequestManageExternalStoragePermission.MANAGE_EXTERNAL_STORAGE), false);
                            return;
                        }
                        permissionBuilder3 = InvisibleFragment.this.pb;
                        if (permissionBuilder3 == null) {
                            Intrinsics.throwUninitializedPropertyAccessException("pb");
                            permissionBuilder3 = null;
                        }
                        ExplainReasonCallback explainReasonCallback = permissionBuilder3.explainReasonCallback;
                        Intrinsics.checkNotNull(explainReasonCallback);
                        chainTask2 = InvisibleFragment.this.task;
                        if (chainTask2 == null) {
                            Intrinsics.throwUninitializedPropertyAccessException("task");
                        } else {
                            chainTask5 = chainTask2;
                        }
                        explainReasonCallback.onExplainReason(chainTask5.getExplainScope(), CollectionsKt.listOf(RequestManageExternalStoragePermission.MANAGE_EXTERNAL_STORAGE));
                    }
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void onRequestInstallPackagesPermissionResult() {
        if (checkForGC()) {
            postForResult(new Function0<Unit>() { // from class: com.permissionx.guolindev.request.InvisibleFragment$onRequestInstallPackagesPermissionResult$1
                /* JADX INFO: Access modifiers changed from: package-private */
                {
                    super(0);
                }

                @Override // kotlin.jvm.functions.Function0
                public /* bridge */ /* synthetic */ Unit invoke() {
                    invoke2();
                    return Unit.INSTANCE;
                }

                /* renamed from: invoke  reason: avoid collision after fix types in other method */
                public final void invoke2() {
                    ChainTask chainTask;
                    PermissionBuilder permissionBuilder;
                    PermissionBuilder permissionBuilder2;
                    PermissionBuilder permissionBuilder3;
                    ChainTask chainTask2;
                    PermissionBuilder permissionBuilder4;
                    ChainTask chainTask3;
                    PermissionBuilder permissionBuilder5;
                    ChainTask chainTask4;
                    ChainTask chainTask5 = null;
                    if (Build.VERSION.SDK_INT < 26) {
                        chainTask = InvisibleFragment.this.task;
                        if (chainTask == null) {
                            Intrinsics.throwUninitializedPropertyAccessException("task");
                        } else {
                            chainTask5 = chainTask;
                        }
                        chainTask5.finish();
                    } else if (InvisibleFragment.this.requireActivity().getPackageManager().canRequestPackageInstalls()) {
                        chainTask4 = InvisibleFragment.this.task;
                        if (chainTask4 == null) {
                            Intrinsics.throwUninitializedPropertyAccessException("task");
                        } else {
                            chainTask5 = chainTask4;
                        }
                        chainTask5.finish();
                    } else {
                        permissionBuilder = InvisibleFragment.this.pb;
                        if (permissionBuilder == null) {
                            Intrinsics.throwUninitializedPropertyAccessException("pb");
                            permissionBuilder = null;
                        }
                        if (permissionBuilder.explainReasonCallback == null) {
                            permissionBuilder5 = InvisibleFragment.this.pb;
                            if (permissionBuilder5 == null) {
                                Intrinsics.throwUninitializedPropertyAccessException("pb");
                                permissionBuilder5 = null;
                            }
                            if (permissionBuilder5.explainReasonCallbackWithBeforeParam == null) {
                                return;
                            }
                        }
                        permissionBuilder2 = InvisibleFragment.this.pb;
                        if (permissionBuilder2 == null) {
                            Intrinsics.throwUninitializedPropertyAccessException("pb");
                            permissionBuilder2 = null;
                        }
                        if (permissionBuilder2.explainReasonCallbackWithBeforeParam != null) {
                            permissionBuilder4 = InvisibleFragment.this.pb;
                            if (permissionBuilder4 == null) {
                                Intrinsics.throwUninitializedPropertyAccessException("pb");
                                permissionBuilder4 = null;
                            }
                            ExplainReasonCallbackWithBeforeParam explainReasonCallbackWithBeforeParam = permissionBuilder4.explainReasonCallbackWithBeforeParam;
                            Intrinsics.checkNotNull(explainReasonCallbackWithBeforeParam);
                            chainTask3 = InvisibleFragment.this.task;
                            if (chainTask3 == null) {
                                Intrinsics.throwUninitializedPropertyAccessException("task");
                            } else {
                                chainTask5 = chainTask3;
                            }
                            explainReasonCallbackWithBeforeParam.onExplainReason(chainTask5.getExplainScope(), CollectionsKt.listOf(RequestInstallPackagesPermission.REQUEST_INSTALL_PACKAGES), false);
                            return;
                        }
                        permissionBuilder3 = InvisibleFragment.this.pb;
                        if (permissionBuilder3 == null) {
                            Intrinsics.throwUninitializedPropertyAccessException("pb");
                            permissionBuilder3 = null;
                        }
                        ExplainReasonCallback explainReasonCallback = permissionBuilder3.explainReasonCallback;
                        Intrinsics.checkNotNull(explainReasonCallback);
                        chainTask2 = InvisibleFragment.this.task;
                        if (chainTask2 == null) {
                            Intrinsics.throwUninitializedPropertyAccessException("task");
                        } else {
                            chainTask5 = chainTask2;
                        }
                        explainReasonCallback.onExplainReason(chainTask5.getExplainScope(), CollectionsKt.listOf(RequestInstallPackagesPermission.REQUEST_INSTALL_PACKAGES));
                    }
                }
            });
        }
    }

    private final boolean checkForGC() {
        if (this.pb == null || this.task == null) {
            Log.w("PermissionX", "PermissionBuilder and ChainTask should not be null at this time, so we can do nothing in this case.");
            return false;
        }
        return true;
    }

    private final void postForResult(final Function0<Unit> function0) {
        this.handler.post(new Runnable() { // from class: com.permissionx.guolindev.request.-$$Lambda$InvisibleFragment$5smUx0LTHxWnAIIE5s8Iv2EsV18
            @Override // java.lang.Runnable
            public final void run() {
                InvisibleFragment.m36postForResult$lambda8(Function0.this);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: postForResult$lambda-8  reason: not valid java name */
    public static final void m36postForResult$lambda8(Function0 callback) {
        Intrinsics.checkNotNullParameter(callback, "$callback");
        callback.invoke();
    }
}

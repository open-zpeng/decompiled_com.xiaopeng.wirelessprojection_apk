package com.permissionx.guolindev;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
/* loaded from: classes2.dex */
public class PermissionX {
    public static PermissionMediator init(FragmentActivity fragmentActivity) {
        return new PermissionMediator(fragmentActivity);
    }

    public static PermissionMediator init(Fragment fragment) {
        return new PermissionMediator(fragment);
    }

    public static boolean isGranted(Context context, String str) {
        return ContextCompat.checkSelfPermission(context, str) == 0;
    }
}

package com.permissionx.guolindev.dialog;

import android.os.Bundle;
import android.view.View;
import androidx.fragment.app.DialogFragment;
import java.util.List;
/* loaded from: classes2.dex */
public abstract class RationaleDialogFragment extends DialogFragment {
    public abstract View getNegativeButton();

    public abstract List<String> getPermissionsToRequest();

    public abstract View getPositiveButton();

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle != null) {
            dismiss();
        }
    }
}

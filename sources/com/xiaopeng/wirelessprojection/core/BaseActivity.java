package com.xiaopeng.wirelessprojection.core;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.xiaopeng.lib.utils.LogUtils;
import com.xiaopeng.wirelessprojection.core.manager.ActivityManager;
import com.xiaopeng.wirelessprojection.core.utils.AppUtils;
import com.xiaopeng.wirelessprojection.core.utils.CarHardwareHelper;
import com.xiaopeng.wirelessprojection.core.utils.ThreadUtils;
import com.xiaopeng.wirelessprojection.core.utils.ToastUtils;
/* loaded from: classes2.dex */
public class BaseActivity extends AppCompatActivity {
    private final String TAG = "BaseActivity";
    private final int WAIT_CAR_SERVICE_DELAY = 800;

    public int getRootLayoutId() {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ActivityManager.instance().addActivity(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        ActivityManager.instance().removeActivity(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStart() {
        super.onStart();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        ThreadUtils.postMainThread(new Runnable() { // from class: com.xiaopeng.wirelessprojection.core.-$$Lambda$BaseActivity$OhgrzwTZOG72RCKOs6fSvHBdf0c
            @Override // java.lang.Runnable
            public final void run() {
                BaseActivity.this.lambda$onResume$2$BaseActivity();
            }
        }, !CarHardwareHelper.instance().isServiceConnected() ? 800 : 0);
    }

    public /* synthetic */ void lambda$onResume$2$BaseActivity() {
        if (!AppUtils.isAllowUseApp()) {
            if (CarHardwareHelper.instance().isCarH93()) {
                runOnUiThread(new Runnable() { // from class: com.xiaopeng.wirelessprojection.core.-$$Lambda$BaseActivity$aMxU9RZi6b8IBmwLbfnX49IaiaI
                    @Override // java.lang.Runnable
                    public final void run() {
                        ToastUtils.showToast(R.string.toast_gear_limit_h93);
                    }
                });
            } else {
                runOnUiThread(new Runnable() { // from class: com.xiaopeng.wirelessprojection.core.-$$Lambda$BaseActivity$-tQ-ee4pcwpoTecc0K2VO4QI4uM
                    @Override // java.lang.Runnable
                    public final void run() {
                        ToastUtils.showToast(R.string.toast_gear_limit);
                    }
                });
            }
            ActivityManager.instance().finishAllActivity("BaseActivity");
            return;
        }
        LogUtils.i("BaseActivity", "app show permitted");
        makeActivityVisible();
    }

    @Override // android.app.Activity
    public void finish() {
        super.finish();
    }

    public void makeActivityVisible() {
        View findViewById;
        if (getRootLayoutId() == 0 || (findViewById = findViewById(getRootLayoutId())) == null) {
            return;
        }
        findViewById.setVisibility(0);
    }
}

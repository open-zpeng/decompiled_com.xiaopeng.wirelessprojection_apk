package com.xiaopeng.wirelessprojection.home.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.xiaopeng.wirelessprojection.home.R;
/* loaded from: classes2.dex */
public class CustomWifiDetailLayout extends LinearLayout {
    private static final String TAG = "CustomWifiDetailLayout";
    private Context mContext;

    public CustomWifiDetailLayout(Context context) {
        this(context, null);
    }

    public CustomWifiDetailLayout(Context context, ViewGroup viewGroup) {
        super(context);
        this.mContext = context;
        View inflate = LayoutInflater.from(context).inflate(R.layout.dialog_wifi_detail, viewGroup, false);
        setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
        addView(inflate);
    }
}

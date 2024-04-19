package com.xiaopeng.wirelessprojection.core.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatTextView;
import com.xiaopeng.lib.utils.LogUtils;
import com.xiaopeng.wirelessprojection.core.BaseApp;
import com.xiaopeng.wirelessprojection.core.R;
/* loaded from: classes2.dex */
public class CustomXTextView extends AppCompatTextView {
    private int mCurrTextColorRef;

    public CustomXTextView(Context context) {
        super(context);
    }

    public CustomXTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public CustomXTextView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override // android.widget.TextView
    public void setTextColor(int i) {
        super.setTextColor(i);
    }

    @Override // android.widget.TextView
    public void setTextColor(ColorStateList colorStateList) {
        if (this.mCurrTextColorRef != 0) {
            return;
        }
        super.setTextColor(colorStateList);
    }

    public void setTextColorRef(int i) {
        try {
            try {
                setTextColor(BaseApp.getContext().getResources().getColor(i));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } finally {
            this.mCurrTextColorRef = i;
        }
    }

    @Override // android.widget.TextView, android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        LogUtils.d("CustomXTextView", "onConfigurationChanged mCurrTextColorRef=" + this.mCurrTextColorRef);
        int i = this.mCurrTextColorRef;
        if (i != 0) {
            setTextColorRef(i);
        }
    }

    private int getColorId(String str) {
        try {
            return R.color.class.getField(str).getInt(null);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}

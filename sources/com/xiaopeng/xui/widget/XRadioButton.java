package com.xiaopeng.xui.widget;

import android.content.Context;
import android.util.AttributeSet;
/* loaded from: classes2.dex */
public class XRadioButton extends XAppCompatRadioButton {
    private boolean pressAnimEnable;

    public XRadioButton(Context context) {
        this(context, null);
    }

    public XRadioButton(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 16842878);
    }

    public XRadioButton(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.pressAnimEnable = true;
        setClickable(true);
    }

    public void setPressAnimEnable(boolean z) {
        this.pressAnimEnable = z;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.appcompat.widget.AppCompatRadioButton, android.widget.CompoundButton, android.widget.TextView, android.view.View
    public void drawableStateChanged() {
        super.drawableStateChanged();
        if (this.mXViewDelegate == null || !this.pressAnimEnable) {
            return;
        }
        this.mXViewDelegate.drawableStateChanged();
    }
}

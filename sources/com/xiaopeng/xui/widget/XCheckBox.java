package com.xiaopeng.xui.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import com.xiaopeng.xpui.R;
/* loaded from: classes2.dex */
public class XCheckBox extends XCompoundButton {
    private boolean pressAnimEnable;

    public XCheckBox(Context context) {
        this(context, null);
    }

    public XCheckBox(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public XCheckBox(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, R.style.XCheckBox);
    }

    public XCheckBox(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.pressAnimEnable = true;
        setClickable(true);
    }

    public void setPressAnimEnable(boolean z) {
        this.pressAnimEnable = z;
    }

    @Override // android.widget.CompoundButton, android.widget.TextView, android.view.View
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        Drawable background = getBackground();
        if (background != null && background.isStateful() && background.setState(getDrawableState())) {
            invalidateDrawable(background);
        }
        if (this.mXViewDelegate == null || !this.pressAnimEnable) {
            return;
        }
        this.mXViewDelegate.drawableStateChanged();
    }
}

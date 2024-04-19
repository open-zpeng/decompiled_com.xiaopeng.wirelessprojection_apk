package com.xiaopeng.xui.widget;

import android.content.Context;
import android.util.AttributeSet;
import xiaopeng.widget.SimpleSlider;
/* loaded from: classes2.dex */
public class XSliderNumberIndicator extends XTextView {
    private final SimpleSlider.OnSlideChangeListener mOnSlideChangeListener;
    private XSimpleSlider mSimpleSlider;

    public XSliderNumberIndicator(Context context) {
        this(context, null);
    }

    public XSliderNumberIndicator(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public XSliderNumberIndicator(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mOnSlideChangeListener = new SimpleSlider.OnSlideChangeListener() { // from class: com.xiaopeng.xui.widget.XSliderNumberIndicator.1
            @Override // xiaopeng.widget.SimpleSlider.OnSlideChangeListener
            public void onStartTrackingTouch(SimpleSlider simpleSlider) {
            }

            @Override // xiaopeng.widget.SimpleSlider.OnSlideChangeListener
            public void onStopTrackingTouch(SimpleSlider simpleSlider) {
            }

            @Override // xiaopeng.widget.SimpleSlider.OnSlideChangeListener
            public void onProgressChanged(SimpleSlider simpleSlider, int i2, boolean z) {
                XSliderNumberIndicator.this.setText(String.valueOf(i2));
            }
        };
    }

    public void setupWithSlider(XSimpleSlider xSimpleSlider) {
        this.mSimpleSlider = xSimpleSlider;
        xSimpleSlider.removeOnSliderChangeListener(this.mOnSlideChangeListener);
        xSimpleSlider.addOnSliderChangeListener(this.mOnSlideChangeListener);
        setText(String.valueOf(xSimpleSlider.getProgress()));
    }

    public void clear() {
        XSimpleSlider xSimpleSlider = this.mSimpleSlider;
        if (xSimpleSlider != null) {
            xSimpleSlider.removeOnSliderChangeListener(this.mOnSlideChangeListener);
        }
    }
}

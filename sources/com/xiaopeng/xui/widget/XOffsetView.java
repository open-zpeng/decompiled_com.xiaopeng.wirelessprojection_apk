package com.xiaopeng.xui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import androidx.core.content.res.ResourcesCompat;
import com.xiaopeng.libtheme.ThemeViewModel;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import com.xiaopeng.vui.commons.IVuiElementBuilder;
import com.xiaopeng.vui.commons.IVuiElementListener;
import com.xiaopeng.vui.commons.model.VuiElement;
import com.xiaopeng.vui.commons.model.VuiEvent;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.Xui;
import com.xiaopeng.xui.app.XToast;
import com.xiaopeng.xui.vui.floatinglayer.VuiFloatingLayerManager;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes2.dex */
public class XOffsetView extends XLinearLayout implements IVuiElementListener {
    private String exceedMaxText;
    private String exceedMinText;
    private int maxValue;
    private int minValue;
    private OnButtonClickListener onButtonClickListener;
    private int step;
    private String unit;
    private boolean useStep;
    private int value;
    private XImageButton x_btn_increase;
    private XImageButton x_btn_reduce;
    private XTextView x_text_content;

    /* loaded from: classes2.dex */
    public interface OnButtonClickListener {
        void onIncrease(int i);

        void onReduce(int i);
    }

    public XOffsetView(Context context) {
        this(context, null);
    }

    public XOffsetView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public XOffsetView(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public XOffsetView(final Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        setOrientation(0);
        LayoutInflater.from(context).inflate(R.layout.x_offset_view, this);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.XOffsetView);
        this.value = obtainStyledAttributes.getInt(R.styleable.XOffsetView_offset_view_value, 0);
        this.step = obtainStyledAttributes.getInt(R.styleable.XOffsetView_offset_view_step, -1);
        this.useStep = obtainStyledAttributes.getBoolean(R.styleable.XOffsetView_offset_view_use_step, false);
        this.maxValue = obtainStyledAttributes.getInt(R.styleable.XOffsetView_offset_view_max_value, 0);
        this.minValue = obtainStyledAttributes.getInt(R.styleable.XOffsetView_offset_view_min_value, 0);
        this.unit = obtainStyledAttributes.getString(R.styleable.XOffsetView_offset_view_unit);
        this.exceedMaxText = obtainStyledAttributes.getString(R.styleable.XOffsetView_offset_view_out_exceed_max_text);
        this.exceedMinText = obtainStyledAttributes.getString(R.styleable.XOffsetView_offset_view_out_exceed_min_text);
        if (this.unit == null) {
            this.unit = "";
        }
        obtainStyledAttributes.recycle();
        setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.x_btn_offset_selector, context.getTheme()));
        initView();
        refreshContent();
        initEvent();
        this.mXViewDelegate.getThemeViewModel().addCallback(new ThemeViewModel.OnCallback() { // from class: com.xiaopeng.xui.widget.XOffsetView.1
            @Override // com.xiaopeng.libtheme.ThemeViewModel.OnCallback
            public void onThemeChanged() {
                XOffsetView xOffsetView = XOffsetView.this;
                xOffsetView.setBackground(ResourcesCompat.getDrawable(xOffsetView.getResources(), R.drawable.x_btn_offset_selector, context.getTheme()));
            }
        });
    }

    private void initView() {
        this.x_btn_reduce = (XImageButton) findViewById(R.id.x_btn_reduce);
        this.x_btn_increase = (XImageButton) findViewById(R.id.x_btn_increase);
        this.x_text_content = (XTextView) findViewById(R.id.x_text_content);
    }

    private void refreshContent() {
        int i = this.value;
        int i2 = this.maxValue;
        if (i >= i2) {
            this.value = i2;
            this.x_btn_increase.setEnabled(false);
        } else {
            int i3 = this.minValue;
            if (i <= i3) {
                this.value = i3;
                this.x_btn_reduce.setEnabled(false);
            } else {
                this.x_btn_increase.setEnabled(true);
                this.x_btn_reduce.setEnabled(true);
            }
        }
        this.x_text_content.setText(this.value + this.unit);
    }

    public void setUnit(String str) {
        this.unit = str;
    }

    public String getUnit() {
        return this.unit;
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int i) {
        this.value = i;
        showExceedToast();
        refreshContent();
        updateVui(this);
    }

    public void setMaxValue(int i) {
        this.maxValue = i;
        refreshContent();
    }

    public int getMaxValue() {
        return this.maxValue;
    }

    public void setMinValue(int i) {
        this.minValue = i;
        refreshContent();
    }

    public int getMinValue() {
        return this.minValue;
    }

    public void setStep(int i) {
        this.step = i;
    }

    public void useStep(boolean z) {
        this.useStep = z;
    }

    private void initEvent() {
        this.x_btn_reduce.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.xui.widget.-$$Lambda$XOffsetView$BYrjxG7vKiaOdaXB9gO0ABFQlcU
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                XOffsetView.this.lambda$initEvent$0$XOffsetView(view);
            }
        });
        this.x_btn_increase.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.xui.widget.-$$Lambda$XOffsetView$hIikZ7QgrQ5kgub5HwLEMX5reI4
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                XOffsetView.this.lambda$initEvent$1$XOffsetView(view);
            }
        });
    }

    public /* synthetic */ void lambda$initEvent$0$XOffsetView(View view) {
        int i = this.step;
        if (i > 0 && this.useStep) {
            setValue(this.value - i);
        }
        OnButtonClickListener onButtonClickListener = this.onButtonClickListener;
        if (onButtonClickListener != null) {
            onButtonClickListener.onReduce(this.value);
        }
    }

    public /* synthetic */ void lambda$initEvent$1$XOffsetView(View view) {
        int i = this.step;
        if (i > 0 && this.useStep) {
            setValue(this.value + i);
        }
        OnButtonClickListener onButtonClickListener = this.onButtonClickListener;
        if (onButtonClickListener != null) {
            onButtonClickListener.onIncrease(this.value);
        }
    }

    private void showExceedToast() {
        int i = this.value;
        int i2 = this.maxValue;
        if (i >= i2) {
            this.value = i2;
            if (TextUtils.isEmpty(this.exceedMaxText)) {
                return;
            }
            XToast.show(this.exceedMaxText);
            return;
        }
        int i3 = this.minValue;
        if (i <= i3) {
            this.value = i3;
            if (TextUtils.isEmpty(this.exceedMinText)) {
                return;
            }
            XToast.show(this.exceedMinText);
        }
    }

    public void setOnButtonClickListener(OnButtonClickListener onButtonClickListener) {
        this.onButtonClickListener = onButtonClickListener;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElementListener
    public VuiElement onBuildVuiElement(String str, IVuiElementBuilder iVuiElementBuilder) {
        try {
            setVuiValue(Integer.valueOf(this.value));
            JSONObject vuiProps = getVuiProps();
            if (vuiProps == null) {
                vuiProps = new JSONObject();
            }
            vuiProps.put(VuiConstants.PROPS_MINVALUE, this.minValue);
            vuiProps.put(VuiConstants.PROPS_MAXVALUE, this.maxValue);
            vuiProps.put(VuiConstants.PROPS_INTERVAL, this.useStep ? this.step : 1);
            vuiProps.put(VuiConstants.PROPS_UNIT, this.unit);
            if (!TextUtils.isEmpty(this.exceedMaxText) || !TextUtils.isEmpty(this.exceedMinText)) {
                vuiProps.put(VuiConstants.PROPS_FEEDBACK, true);
            }
            setVuiProps(vuiProps);
            return null;
        } catch (JSONException unused) {
            return null;
        }
    }

    @Override // com.xiaopeng.vui.commons.IVuiElementListener
    public boolean onVuiElementEvent(View view, VuiEvent vuiEvent) {
        final Double d;
        logD("slider onVuiElementEvent");
        if (view == null || (d = (Double) vuiEvent.getEventValue(vuiEvent)) == null) {
            return false;
        }
        post(new Runnable() { // from class: com.xiaopeng.xui.widget.-$$Lambda$XOffsetView$0s1mJX6yHkkxlZ4QQjTyEyVX5Fg
            @Override // java.lang.Runnable
            public final void run() {
                XOffsetView.this.lambda$onVuiElementEvent$2$XOffsetView(d);
            }
        });
        return true;
    }

    public /* synthetic */ void lambda$onVuiElementEvent$2$XOffsetView(Double d) {
        int intValue = d.intValue();
        int i = this.maxValue;
        if (intValue > i || intValue < this.minValue) {
            if (intValue > i && !TextUtils.isEmpty(this.exceedMaxText) && Xui.getVuiEngine() != null) {
                Xui.getVuiEngine().vuiFeedback(this, this.exceedMaxText);
            }
            if (intValue < this.minValue && !TextUtils.isEmpty(this.exceedMinText) && Xui.getVuiEngine() != null) {
                Xui.getVuiEngine().vuiFeedback(this, this.exceedMinText);
            }
        } else {
            setPerformVuiAction(true);
            setValue(intValue);
            setPerformVuiAction(false);
            if (intValue == this.maxValue && !TextUtils.isEmpty(this.exceedMaxText) && Xui.getVuiEngine() != null) {
                Xui.getVuiEngine().vuiFeedback(this, this.exceedMaxText);
            }
            if (intValue == this.minValue && !TextUtils.isEmpty(this.exceedMinText) && Xui.getVuiEngine() != null) {
                Xui.getVuiEngine().vuiFeedback(this, this.exceedMinText);
            }
        }
        VuiFloatingLayerManager.show(this);
    }
}

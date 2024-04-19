package com.xiaopeng.xui.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.google.android.material.tabs.TabLayout;
import com.xiaopeng.vui.commons.IVuiElementBuilder;
import com.xiaopeng.vui.commons.IVuiElementListener;
import com.xiaopeng.vui.commons.model.VuiElement;
import com.xiaopeng.vui.commons.model.VuiEvent;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.Xui;
import com.xiaopeng.xui.utils.FlavorUtils;
import com.xiaopeng.xui.view.XViewDelegate;
import com.xiaopeng.xui.vui.VuiView;
import com.xiaopeng.xui.vui.floatinglayer.VuiFloatingLayerManager;
import org.json.JSONObject;
/* loaded from: classes2.dex */
public class XTabLayout_V5 extends TabLayout implements VuiView, IVuiElementListener {
    private String[] mTitles;
    protected XViewDelegate mXViewDelegate;

    public XTabLayout_V5(Context context) {
        this(context, null);
    }

    public XTabLayout_V5(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
        this.mXViewDelegate = XViewDelegate.create(this, attributeSet);
        initVui(this, attributeSet);
    }

    public XTabLayout_V5(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.XTabLayout_V5);
        int resourceId = obtainStyledAttributes.getResourceId(R.styleable.XTabLayout_V5_tab_text_array, 0);
        if (resourceId != 0) {
            this.mTitles = getResources().getStringArray(resourceId);
        }
        obtainStyledAttributes.recycle();
        initView();
        this.mXViewDelegate = XViewDelegate.create(this, attributeSet, i);
        initVui(this, attributeSet);
        addVuiListener();
    }

    private void addVuiListener() {
        if (Xui.isVuiEnable()) {
            addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() { // from class: com.xiaopeng.xui.widget.XTabLayout_V5.1
                @Override // com.google.android.material.tabs.TabLayout.BaseOnTabSelectedListener
                public void onTabReselected(TabLayout.Tab tab) {
                }

                @Override // com.google.android.material.tabs.TabLayout.BaseOnTabSelectedListener
                public void onTabUnselected(TabLayout.Tab tab) {
                }

                @Override // com.google.android.material.tabs.TabLayout.BaseOnTabSelectedListener
                public void onTabSelected(TabLayout.Tab tab) {
                    XTabLayout_V5 xTabLayout_V5 = XTabLayout_V5.this;
                    xTabLayout_V5.updateVui(xTabLayout_V5);
                }
            });
        }
    }

    private void initView() {
        String[] strArr = this.mTitles;
        if (strArr != null) {
            for (String str : strArr) {
                addTabItem(str);
            }
        }
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        XViewDelegate xViewDelegate = this.mXViewDelegate;
        if (xViewDelegate != null) {
            xViewDelegate.onConfigurationChanged(configuration);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.material.tabs.TabLayout, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        XViewDelegate xViewDelegate = this.mXViewDelegate;
        if (xViewDelegate != null) {
            xViewDelegate.onAttachedToWindow();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.material.tabs.TabLayout, android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        XViewDelegate xViewDelegate = this.mXViewDelegate;
        if (xViewDelegate != null) {
            xViewDelegate.onDetachedFromWindow();
        }
    }

    @Override // android.view.View
    public void setVisibility(int i) {
        super.setVisibility(i);
        setVuiVisibility(this, i);
    }

    @Override // android.view.View
    public void setSelected(boolean z) {
        super.setSelected(z);
        setVuiSelected(this, z);
    }

    protected void finalize() throws Throwable {
        super.finalize();
        releaseVui();
    }

    @Override // android.view.View
    public void setEnabled(boolean z) {
        super.setEnabled(z);
        if (FlavorUtils.isV5()) {
            if (z) {
                setAlpha(1.0f);
            } else {
                setAlpha(0.4f);
            }
        }
    }

    public void addTabItem(String str) {
        TabLayout.Tab newTab = newTab();
        newTab.setCustomView(R.layout.x_tab_item_v5);
        TextView textView = (TextView) newTab.getCustomView().findViewById(R.id.x_tab_text);
        if (str == null) {
            str = "";
        }
        textView.setText(str);
        addTab(newTab);
        updateVui(this);
    }

    public void addTabItem(String str, int i) {
        TabLayout.Tab newTab = newTab();
        newTab.setCustomView(R.layout.x_tab_item_v5);
        TextView textView = (TextView) newTab.getCustomView().findViewById(R.id.x_tab_text);
        if (str == null) {
            str = "";
        }
        textView.setText(str);
        addTab(newTab, i);
        updateVui(this);
    }

    @Override // com.xiaopeng.vui.commons.IVuiElementListener
    public VuiElement onBuildVuiElement(String str, IVuiElementBuilder iVuiElementBuilder) {
        try {
            setVuiValue(Integer.valueOf(getSelectedTabPosition()));
            for (int i = 0; i < getTabCount(); i++) {
                XTextView xTextView = (XTextView) getTabAt(i).view.findViewById(R.id.x_tab_text);
                xTextView.setVuiPosition(i);
                xTextView.setVuiElementId(str + "_" + i);
                JSONObject vuiProps = getVuiProps();
                if (vuiProps == null) {
                    vuiProps = new JSONObject();
                }
                vuiProps.put("isTabLayoutChild", true);
                setVuiProps(vuiProps);
            }
            logD("onBuildVuiElement:" + str);
            return null;
        } catch (Exception unused) {
            return null;
        }
    }

    @Override // com.xiaopeng.vui.commons.IVuiElementListener
    public boolean onVuiElementEvent(final View view, VuiEvent vuiEvent) {
        final Double d;
        logD("tablayout onVuiElementEvent");
        if (view == null || (d = (Double) vuiEvent.getEventValue(vuiEvent)) == null) {
            return false;
        }
        post(new Runnable() { // from class: com.xiaopeng.xui.widget.-$$Lambda$XTabLayout_V5$M8FrOImBLZl2Yi2WABFtLQ5w79Q
            @Override // java.lang.Runnable
            public final void run() {
                XTabLayout_V5.this.lambda$onVuiElementEvent$0$XTabLayout_V5(d, view);
            }
        });
        return true;
    }

    public /* synthetic */ void lambda$onVuiElementEvent$0$XTabLayout_V5(Double d, View view) {
        if (d.intValue() < getTabCount()) {
            VuiFloatingLayerManager.show(getTabAt(d.intValue()).view);
        } else {
            VuiFloatingLayerManager.show(view);
        }
        setPerformVuiAction(true);
        selectTab(getTabAt(d.intValue()));
        setPerformVuiAction(false);
    }
}

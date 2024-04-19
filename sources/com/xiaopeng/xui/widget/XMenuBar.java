package com.xiaopeng.xui.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.TextViewCompat;
import com.xiaopeng.libtheme.ThemeViewModel;
import com.xiaopeng.vui.commons.IVuiElementBuilder;
import com.xiaopeng.vui.commons.IVuiElementListener;
import com.xiaopeng.vui.commons.model.VuiElement;
import com.xiaopeng.vui.commons.model.VuiEvent;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.vui.floatinglayer.VuiFloatingLayerManager;
import org.json.JSONObject;
/* loaded from: classes2.dex */
public class XMenuBar extends XLinearLayout implements View.OnClickListener, IVuiElementListener {
    int iconTintColorId;
    private int[] mIcons;
    private String[] mNames;
    private OnItemClickListener onItemClickListener;

    /* loaded from: classes2.dex */
    public interface OnItemClickListener {
        void onItemChanged(View view, int i, boolean z);
    }

    public XMenuBar(Context context) {
        this(context, null);
    }

    public XMenuBar(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public XMenuBar(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public XMenuBar(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        setOrientation(1);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.XMenuBar);
        int integer = obtainStyledAttributes.getInteger(R.styleable.XMenuBar_menu_check_position, 0);
        int resourceId = obtainStyledAttributes.getResourceId(R.styleable.XMenuBar_menu_text_array, 0);
        if (resourceId != 0) {
            this.mNames = getResources().getStringArray(resourceId);
        }
        int resourceId2 = obtainStyledAttributes.getResourceId(R.styleable.XMenuBar_menu_icon_array, 0);
        this.iconTintColorId = obtainStyledAttributes.getResourceId(R.styleable.XMenuBar_menu_icon_tint, 0);
        if (resourceId2 != 0) {
            String[] stringArray = getResources().getStringArray(resourceId2);
            if (stringArray.length > 0) {
                this.mIcons = new int[stringArray.length];
                for (int i3 = 0; i3 < stringArray.length; i3++) {
                    this.mIcons[i3] = getResources().getIdentifier(stringArray[i3], "drawable", context.getPackageName());
                }
            }
        }
        obtainStyledAttributes.recycle();
        init(integer);
        this.mXViewDelegate.getThemeViewModel().addCallback(new ThemeViewModel.OnCallback() { // from class: com.xiaopeng.xui.widget.XMenuBar.1
            @Override // com.xiaopeng.libtheme.ThemeViewModel.OnCallback
            public void onThemeChanged() {
                XMenuBar xMenuBar = XMenuBar.this;
                xMenuBar.setIconTintColor(xMenuBar.iconTintColorId);
            }
        });
    }

    private void init(int i) {
        LayoutInflater from = LayoutInflater.from(getContext());
        if (this.mNames == null) {
            return;
        }
        for (int i2 = 0; i2 < this.mNames.length; i2++) {
            View inflate = from.inflate(R.layout.x_menu_item, (ViewGroup) this, false);
            TextView textView = (TextView) inflate.findViewById(R.id.x_text_menu_item);
            textView.setText(this.mNames[i2]);
            int[] iArr = this.mIcons;
            if (iArr != null && i2 < iArr.length) {
                textView.setCompoundDrawablesWithIntrinsicBounds(iArr[i2], 0, 0, 0);
            }
            addView(inflate);
            inflate.setOnClickListener(this);
            if (i == i2) {
                inflate.setSelected(true);
            }
        }
        setIconTintColor(this.iconTintColorId);
    }

    public void addTabItem(String str, int i) {
        addTabItem(str, i, -1);
    }

    public void addTabItem(CharSequence charSequence, int i, int i2) {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.x_menu_item, (ViewGroup) this, false);
        TextView textView = (TextView) inflate.findViewById(R.id.x_text_menu_item);
        textView.setText(charSequence);
        textView.setCompoundDrawablesWithIntrinsicBounds(i, 0, 0, 0);
        setIconTintColor(textView);
        addView(inflate, i2);
        inflate.setOnClickListener(this);
        updateVui(this);
    }

    public void setIconTintColor(TextView textView) {
        ColorStateList colorStateList;
        if (this.iconTintColorId == 0 || (colorStateList = ResourcesCompat.getColorStateList(getResources(), this.iconTintColorId, getContext().getTheme())) == null) {
            return;
        }
        TextViewCompat.setCompoundDrawableTintList(textView, colorStateList);
    }

    public void setIconTintColor(int i) {
        this.iconTintColorId = i;
        if (i == 0) {
            return;
        }
        ColorStateList colorStateList = ResourcesCompat.getColorStateList(getResources(), i, getContext().getTheme());
        if (colorStateList != null) {
            for (int i2 = 0; i2 < getChildCount(); i2++) {
                TextViewCompat.setCompoundDrawableTintList((TextView) getChildAt(i2).findViewById(R.id.x_text_menu_item), colorStateList);
            }
        }
        invalidate();
    }

    public void removeTabItem(int i) {
        if (i < 0 || i >= getChildCount()) {
            return;
        }
        removeViewAt(i);
        updateVui(this);
    }

    public View getTabView(int i) {
        if (i < 0 || i >= getChildCount()) {
            return null;
        }
        return getChildAt(i);
    }

    public void setPosition(int i) {
        if (i < 0 || i > getChildCount() - 1) {
            Log.e("XMenuBar", "position is out of range");
            return;
        }
        View view = null;
        for (int i2 = 0; i2 < getChildCount(); i2++) {
            TextView textView = (TextView) getChildAt(i2).findViewById(R.id.x_text_menu_item);
            if (i == i2) {
                view = getChildAt(i2);
                getChildAt(i2).setSelected(true);
                getChildAt(i2).findViewById(R.id.x_text_menu_item).setSelected(true);
            } else {
                getChildAt(i2).setSelected(false);
                getChildAt(i2).findViewById(R.id.x_text_menu_item).setSelected(false);
            }
            updateTextViewStyle(textView);
        }
        OnItemClickListener onItemClickListener = this.onItemClickListener;
        if (onItemClickListener != null && view != null) {
            onItemClickListener.onItemChanged(view, i, false);
        }
        updateVui(this);
    }

    public int getPosition() {
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i).isSelected()) {
                return i;
            }
        }
        return -1;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        int i = 0;
        for (int i2 = 0; i2 < getChildCount(); i2++) {
            TextView textView = (TextView) getChildAt(i2).findViewById(R.id.x_text_menu_item);
            if (view == getChildAt(i2)) {
                getChildAt(i2).setSelected(true);
                getChildAt(i2).findViewById(R.id.x_text_menu_item).setSelected(true);
                i = i2;
            } else {
                getChildAt(i2).setSelected(false);
                getChildAt(i2).findViewById(R.id.x_text_menu_item).setSelected(false);
            }
            updateTextViewStyle(textView);
        }
        OnItemClickListener onItemClickListener = this.onItemClickListener;
        if (onItemClickListener != null) {
            onItemClickListener.onItemChanged(view, i, true);
        }
        updateVui(this);
    }

    private void updateTextViewStyle(TextView textView) {
        if (textView.isSelected()) {
            textView.setTypeface(Typeface.create(getResources().getString(R.string.x_font_typeface_medium), R.style.XMenuItemText));
        } else {
            textView.setTypeface(Typeface.create(getResources().getString(R.string.x_font_typeface_regular), R.style.XMenuItemText));
        }
    }

    @Override // com.xiaopeng.vui.commons.IVuiElementListener
    public VuiElement onBuildVuiElement(String str, IVuiElementBuilder iVuiElementBuilder) {
        XTextView xTextView;
        try {
            setVuiValue(Integer.valueOf(getPosition()));
            for (int i = 0; i < getChildCount(); i++) {
                View childAt = getChildAt(i);
                if (childAt != null && (xTextView = (XTextView) childAt.findViewById(R.id.x_text_menu_item)) != null) {
                    xTextView.setVuiPosition(i);
                    xTextView.setVuiElementId(str + "_" + i);
                    JSONObject vuiProps = getVuiProps();
                    if (vuiProps == null) {
                        vuiProps = new JSONObject();
                    }
                    vuiProps.put("isTabLayoutChild", true);
                    setVuiProps(vuiProps);
                }
            }
            logD("onBuildVuiElement:" + str);
        } catch (Exception unused) {
        }
        logD("onBuildVuiElement:" + str);
        return null;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElementListener
    public boolean onVuiElementEvent(final View view, VuiEvent vuiEvent) {
        final Double d;
        logD("xMenuBar onVuiElementEvent");
        if (view == null || (d = (Double) vuiEvent.getEventValue(vuiEvent)) == null) {
            return false;
        }
        post(new Runnable() { // from class: com.xiaopeng.xui.widget.-$$Lambda$XMenuBar$IIbT91hjvPVhhCSrB_hmHTfpQiw
            @Override // java.lang.Runnable
            public final void run() {
                XMenuBar.this.lambda$onVuiElementEvent$0$XMenuBar(d, view);
            }
        });
        return true;
    }

    public /* synthetic */ void lambda$onVuiElementEvent$0$XMenuBar(Double d, View view) {
        if (d.intValue() < getChildCount()) {
            VuiFloatingLayerManager.show(getChildAt(d.intValue()));
        } else {
            VuiFloatingLayerManager.show(view);
        }
        setPerformVuiAction(true);
        setPosition(d.intValue());
        setPerformVuiAction(false);
    }
}

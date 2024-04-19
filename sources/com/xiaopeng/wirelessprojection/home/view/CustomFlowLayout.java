package com.xiaopeng.wirelessprojection.home.view;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import com.xiaopeng.lib.utils.LogUtils;
import com.xiaopeng.wirelessprojection.core.BaseApp;
import com.xiaopeng.wirelessprojection.core.event.DayNightChangeEvent;
import com.xiaopeng.wirelessprojection.core.utils.EventBusUtils;
import com.xiaopeng.wirelessprojection.home.R;
import com.xiaopeng.xui.theme.XThemeManager;
import com.xiaopeng.xui.widget.XTextView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/* loaded from: classes2.dex */
public class CustomFlowLayout extends ViewGroup {
    private final int CLIENT_MAX_NUM;
    private final String TAG;
    private String[] mClients;
    private List<String> mTags;

    public CustomFlowLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.TAG = "CustomFlowLayout";
        this.CLIENT_MAX_NUM = 5;
        this.mTags = new ArrayList();
        this.mClients = new String[0];
    }

    public CustomFlowLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.TAG = "CustomFlowLayout";
        this.CLIENT_MAX_NUM = 5;
        this.mTags = new ArrayList();
        this.mClients = new String[0];
    }

    public CustomFlowLayout(Context context) {
        super(context);
        this.TAG = "CustomFlowLayout";
        this.CLIENT_MAX_NUM = 5;
        this.mTags = new ArrayList();
        this.mClients = new String[0];
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        int i3;
        int mode = View.MeasureSpec.getMode(i);
        int size = View.MeasureSpec.getSize(i);
        int mode2 = View.MeasureSpec.getMode(i2);
        int size2 = View.MeasureSpec.getSize(i2);
        int childCount = getChildCount();
        int i4 = 0;
        int i5 = 0;
        int i6 = 0;
        int i7 = 0;
        int i8 = 0;
        while (i4 < childCount) {
            View childAt = getChildAt(i4);
            if (childAt.getVisibility() != 8) {
                measureChild(childAt, i, i2);
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) childAt.getLayoutParams();
                i3 = size2;
                int measuredWidth = childAt.getMeasuredWidth() + marginLayoutParams.leftMargin + marginLayoutParams.rightMargin;
                int measuredHeight = childAt.getMeasuredHeight() + marginLayoutParams.topMargin + marginLayoutParams.bottomMargin;
                int i9 = i6 + measuredWidth;
                if (i9 > size) {
                    i8 += i7;
                    if (i5 < i6) {
                        i5 = i6;
                    }
                    i7 = measuredHeight;
                    i6 = measuredWidth;
                } else {
                    if (i7 < measuredHeight) {
                        i7 = measuredHeight;
                    }
                    i6 = i9;
                }
            } else {
                i3 = size2;
            }
            i4++;
            size2 = i3;
        }
        int i10 = size2;
        if (mode != 1073741824) {
            size = i5;
        }
        setMeasuredDimension(size, mode2 == 1073741824 ? i10 : i8);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5;
        int i6;
        int i7;
        ArrayList arrayList = new ArrayList();
        int childCount = getChildCount();
        LogUtils.i("CustomFlowLayout", "onLayout count---->" + childCount);
        int i8 = 1;
        int i9 = 0;
        int i10 = 0;
        for (int i11 = 0; i11 < childCount; i11++) {
            View childAt = getChildAt(i11);
            if (childAt.getVisibility() != 8) {
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) childAt.getLayoutParams();
                int measuredWidth = childAt.getMeasuredWidth() + marginLayoutParams.leftMargin + marginLayoutParams.rightMargin;
                int measuredHeight = childAt.getMeasuredHeight() + marginLayoutParams.topMargin + marginLayoutParams.bottomMargin;
                LogUtils.i("CustomFlowLayout", "getWidth()---->" + getWidth());
                int i12 = i9 + measuredWidth;
                if (i12 > getWidth()) {
                    arrayList.add(Integer.valueOf(i10));
                    i8++;
                    i5 = marginLayoutParams.leftMargin;
                    if (i8 > 1) {
                        int i13 = 0;
                        for (int i14 = 0; i14 < i8 - 1; i14++) {
                            i13 += ((Integer) arrayList.get(i14)).intValue();
                        }
                        i7 = i13 + marginLayoutParams.topMargin;
                    } else {
                        i7 = marginLayoutParams.topMargin;
                    }
                    i10 = measuredHeight;
                } else {
                    i5 = i9 + marginLayoutParams.leftMargin;
                    if (i8 > 1) {
                        int i15 = 0;
                        for (int i16 = 0; i16 < i8 - 1; i16++) {
                            i15 += ((Integer) arrayList.get(i16)).intValue();
                        }
                        i6 = i15 + marginLayoutParams.topMargin;
                    } else {
                        i6 = marginLayoutParams.topMargin;
                    }
                    if (i10 < measuredHeight) {
                        i10 = measuredHeight;
                    }
                    measuredWidth = i12;
                    i7 = i6;
                }
                childAt.layout(i5, i7, childAt.getMeasuredWidth() + i5, childAt.getMeasuredHeight() + i7);
                i9 = measuredWidth;
            }
        }
    }

    @Override // android.view.ViewGroup
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new ViewGroup.MarginLayoutParams(getContext(), attributeSet);
    }

    @Override // android.view.View
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (XThemeManager.isThemeChanged(configuration)) {
            LogUtils.i("CustomFlowLayout", "onConfigurationChanged isNight=" + XThemeManager.isNight(configuration) + ", mClients=" + Arrays.toString(this.mClients));
            removeAllViews();
            addTags(this.mClients);
            EventBusUtils.post(new DayNightChangeEvent());
        }
    }

    public void addTags(String[] strArr) {
        this.mClients = strArr;
        addTags(Arrays.asList(strArr));
    }

    private void addTags(List<String> list) {
        if (list != null) {
            LogUtils.i("CustomFlowLayout", "setTags size=" + list.size());
            int min = Math.min(list.size(), 5);
            this.mTags.clear();
            this.mTags.addAll(list);
            int i = 1;
            for (int i2 = 0; i2 < min; i2++) {
                String str = this.mTags.get(i2);
                if ("unknown".equals(str)) {
                    str = BaseApp.getContext().getString(R.string.hotspot_name_unknown, Integer.valueOf(i));
                    i++;
                }
                XTextView xTextView = new XTextView(getContext());
                Resources resources = BaseApp.getContext().getResources();
                xTextView.setBackground(resources.getDrawable(R.drawable.bg_hotspot_device));
                xTextView.setTextSize(34.0f);
                xTextView.setText(str);
                xTextView.setTextColor(resources.getColorStateList(R.color.custom_flow_text_color));
                xTextView.setPadding(32, 8, 32, 8);
                ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(-2, -2);
                marginLayoutParams.setMargins(0, 2, 24, 24);
                xTextView.setLayoutParams(marginLayoutParams);
                addView(xTextView);
            }
            requestLayout();
        }
    }
}

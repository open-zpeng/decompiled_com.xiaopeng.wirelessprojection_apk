package com.xiaopeng.xui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.TextView;
import com.xiaopeng.vui.commons.IVuiElementBuilder;
import com.xiaopeng.vui.commons.IVuiElementListener;
import com.xiaopeng.vui.commons.model.VuiElement;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.utils.FlavorUtils;
import com.xiaopeng.xui.vui.VuiView;
import java.util.Map;
/* loaded from: classes2.dex */
public class XListMultiple extends XRelativeLayout implements IVuiElementListener {
    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_TW0 = 1;
    private static final int TYPE_WRAP = 2;
    private ViewStub mBottomViewStub;
    private String mBottomVuiLabel;
    private ViewStub mRightViewStub;
    private String mRightVuiLabel;
    private TextView mText;
    private TextView mTextSub;
    private ViewGroup mViewGroupBottom;
    private ViewGroup mViewGroupRight;

    public XListMultiple(Context context) {
        this(context, null);
    }

    public XListMultiple(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public XListMultiple(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public XListMultiple(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        int i3;
        int resourceId;
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.XListMultiple);
        int i4 = obtainStyledAttributes.getInt(R.styleable.XListMultiple_list_multiple_type, 0);
        if (FlavorUtils.isV5()) {
            i3 = R.layout.x_list_multiple_wrap;
        } else if (i4 == 1) {
            i3 = R.layout.x_list_multiple_two;
        } else if (i4 != 2) {
            i3 = R.layout.x_list_multiple;
        } else {
            i3 = R.layout.x_list_multiple_wrap;
        }
        LayoutInflater.from(context).inflate(i3, this);
        initView();
        this.mTextSub.setMaxLines(obtainStyledAttributes.getInt(R.styleable.XListMultiple_list_text_sub_lines, FlavorUtils.isV5() ? Integer.MAX_VALUE : 1));
        setText(obtainStyledAttributes.getString(R.styleable.XListMultiple_list_multiple_text));
        setTextSub(obtainStyledAttributes.getString(R.styleable.XListMultiple_list_multiple_text_sub));
        int resourceId2 = obtainStyledAttributes.getResourceId(R.styleable.XListMultiple_list_multiple_bottom, -1);
        if (resourceId2 != -1) {
            this.mBottomViewStub.setLayoutResource(resourceId2);
            View inflate = this.mBottomViewStub.inflate();
            if (inflate instanceof ViewGroup) {
                this.mViewGroupBottom = (ViewGroup) inflate;
            }
        }
        if (!FlavorUtils.isV5() && (resourceId = obtainStyledAttributes.getResourceId(R.styleable.XListMultiple_list_multiple_right, -1)) != -1) {
            this.mRightViewStub.setLayoutResource(resourceId);
            View inflate2 = this.mRightViewStub.inflate();
            if (inflate2 instanceof ViewGroup) {
                this.mViewGroupRight = (ViewGroup) inflate2;
            }
        }
        this.mRightVuiLabel = obtainStyledAttributes.getString(R.styleable.XListMultiple_list_multiple_right_vuiLabel);
        this.mBottomVuiLabel = obtainStyledAttributes.getString(R.styleable.XListMultiple_list_multiple_bottom_vuiLabel);
        obtainStyledAttributes.recycle();
    }

    private /* synthetic */ void lambda$new$0(View view) {
        logD("list-mul mBottomViewStub w " + view.getWidth() + " h " + view.getHeight());
    }

    private /* synthetic */ void lambda$new$1(int i, int i2) {
        logD("list-mul this w " + getWidth() + " h " + getHeight() + ", lines : " + i + ", type :" + i2);
    }

    private void initView() {
        this.mText = (TextView) findViewById(R.id.x_list_tv);
        this.mTextSub = (TextView) findViewById(R.id.x_list_tv_sub);
        this.mBottomViewStub = (ViewStub) findViewById(R.id.x_list_bottom);
        this.mRightViewStub = (ViewStub) findViewById(R.id.x_list_right);
    }

    public void setText(CharSequence charSequence) {
        this.mText.setText(charSequence);
    }

    public void setTextSub(CharSequence charSequence) {
        this.mTextSub.setText(charSequence);
    }

    @Override // com.xiaopeng.xui.widget.XRelativeLayout, android.view.View
    public void setEnabled(boolean z) {
        setEnabled(z, true);
    }

    public void setEnabled(boolean z, boolean z2) {
        super.setEnabled(z);
        if (z2) {
            setChildEnabled(this, z);
        }
    }

    private void setChildEnabled(ViewGroup viewGroup, boolean z) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt instanceof ViewGroup) {
                setChildEnabled((ViewGroup) childAt, z);
            }
            childAt.setEnabled(z);
            childAt.setAlpha(1.0f);
        }
    }

    @Override // com.xiaopeng.vui.commons.IVuiElementListener
    public VuiElement onBuildVuiElement(String str, IVuiElementBuilder iVuiElementBuilder) {
        if (this.mViewGroupRight != null && !TextUtils.isEmpty(this.mRightVuiLabel)) {
            for (int i = 0; i < this.mViewGroupRight.getChildCount(); i++) {
                View childAt = this.mViewGroupRight.getChildAt(i);
                if (isListNeedSetVuiLabelView(childAt)) {
                    VuiView vuiView = (VuiView) childAt;
                    vuiView.setVuiLabel(this.mRightVuiLabel);
                    vuiView.setVuiElementId(str + "_" + childAt.getId());
                }
            }
        }
        if (this.mViewGroupBottom == null || TextUtils.isEmpty(this.mBottomVuiLabel)) {
            return null;
        }
        for (int i2 = 0; i2 < this.mViewGroupBottom.getChildCount(); i2++) {
            View childAt2 = this.mViewGroupBottom.getChildAt(i2);
            if (isListNeedSetVuiLabelView(childAt2)) {
                VuiView vuiView2 = (VuiView) childAt2;
                vuiView2.setVuiLabel(this.mBottomVuiLabel);
                vuiView2.setVuiElementId(str + "_" + childAt2.getId());
            }
        }
        return null;
    }

    public void setVuiLabels(Map<Integer, String> map) {
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            int intValue = entry.getKey().intValue();
            if (intValue == 1) {
                this.mRightVuiLabel = entry.getValue();
            } else if (intValue == 2) {
                this.mBottomVuiLabel = entry.getValue();
            }
        }
        updateVui(this);
    }
}

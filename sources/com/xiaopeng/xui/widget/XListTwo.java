package com.xiaopeng.xui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;
import com.xiaopeng.vui.commons.IVuiElementBuilder;
import com.xiaopeng.vui.commons.IVuiElementListener;
import com.xiaopeng.vui.commons.model.VuiElement;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.utils.FlavorUtils;
import com.xiaopeng.xui.utils.XTouchAreaUtils;
import com.xiaopeng.xui.utils.XuiUtils;
import com.xiaopeng.xui.vui.VuiView;
import java.util.List;
import java.util.Map;
/* loaded from: classes2.dex */
public class XListTwo extends XRelativeLayout implements IVuiElementListener {
    private int mLeftRightTouchFull;
    private ViewGroup mLeftView;
    private ViewStub mLeftViewStub;
    private String mLeftVuiLabel;
    private TextView mNum;
    private ViewStub mRightViewStub;
    private String mRightVuiLabel;
    private View mTag;
    private ViewStub mTagCustomViewStub;
    private TextView mText;
    private TextView mTextSub;
    private ViewGroup mTvLay;
    private ViewGroup mViewGroupLeft;
    private ViewGroup mViewGroupRight;
    private ViewGroup mViewRightTouchArea;

    public XListTwo(Context context) {
        this(context, null);
    }

    public XListTwo(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public XListTwo(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public XListTwo(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        LayoutInflater.from(context).inflate(R.layout.x_list_two, this);
        initView();
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.XListTwo);
        setText(obtainStyledAttributes.getString(R.styleable.XListTwo_list_two_text));
        setTextSub(obtainStyledAttributes.getString(R.styleable.XListTwo_list_two_text_sub));
        if (FlavorUtils.isV5()) {
            Drawable drawable = obtainStyledAttributes.getDrawable(R.styleable.XListTwo_list_two_tag_background);
            if (drawable != null) {
                this.mTag.setBackground(drawable);
            }
            ((XTextView) this.mTag).setTextColor(obtainStyledAttributes.getColor(R.styleable.XListTwo_list_two_tag_text_color, getResources().getColor(R.color.x_color_text_1, context.getTheme())));
            if (drawable != null) {
                this.mTag.setBackground(drawable);
            }
            String string = obtainStyledAttributes.getString(R.styleable.XListTwo_list_two_tag_text);
            if (string != null) {
                ((XTextView) this.mTag).setText(string);
            }
        }
        this.mTextSub.setMaxLines(obtainStyledAttributes.getInt(R.styleable.XListTwo_list_text_sub_lines, FlavorUtils.isV5() ? Integer.MAX_VALUE : 1));
        int resourceId = obtainStyledAttributes.getResourceId(R.styleable.XListTwo_list_two_left, -1);
        if (resourceId != -1) {
            this.mLeftViewStub.setLayoutResource(resourceId);
            View inflate = this.mLeftViewStub.inflate();
            if (inflate instanceof ViewGroup) {
                this.mViewGroupLeft = (ViewGroup) inflate;
            }
        }
        int resourceId2 = obtainStyledAttributes.getResourceId(R.styleable.XListTwo_list_two_right, -1);
        if (resourceId2 != -1) {
            this.mRightViewStub.setLayoutResource(resourceId2);
            View inflate2 = this.mRightViewStub.inflate();
            if (inflate2 instanceof ViewGroup) {
                this.mViewGroupRight = (ViewGroup) inflate2;
            }
        }
        int resourceId3 = obtainStyledAttributes.getResourceId(R.styleable.XListTwo_list_two_tag_icon, -1);
        if (resourceId3 != -1) {
            setTagIcon(resourceId3);
        }
        showIconTag(obtainStyledAttributes.getBoolean(R.styleable.XListTwo_list_two_tag, false));
        int resourceId4 = obtainStyledAttributes.getResourceId(R.styleable.XListTwo_list_two_tag_custom, -1);
        if (resourceId4 != -1) {
            this.mTagCustomViewStub.setLayoutResource(resourceId4);
            this.mTagCustomViewStub.inflate();
        }
        this.mLeftRightTouchFull = obtainStyledAttributes.getInt(R.styleable.XListTwo_list_two_left_right_touch_full, 0);
        this.mLeftVuiLabel = obtainStyledAttributes.getString(R.styleable.XListTwo_list_two_left_vuiLabel);
        this.mRightVuiLabel = obtainStyledAttributes.getString(R.styleable.XListTwo_list_two_right_vuiLabel);
        obtainStyledAttributes.recycle();
        if (FlavorUtils.isV5() || resourceId2 == -1) {
            return;
        }
        View findViewById = findViewById(R.id.x_list_tv_lay);
        int dimension = (int) getResources().getDimension(R.dimen.x_list_tv_margin_end);
        findViewById.setPadding(findViewById.getPaddingLeft(), findViewById.getPaddingTop(), dimension, findViewById.getPaddingBottom());
        TextView textView = this.mTextSub;
        textView.setPadding(textView.getPaddingLeft(), this.mTextSub.getPaddingTop(), dimension, this.mTextSub.getPaddingBottom());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.widget.XRelativeLayout, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        extendTouchLeft();
        extendTouchRight();
    }

    private /* synthetic */ void lambda$onAttachedToWindow$0() {
        logD("list-two this w " + getWidth() + " h " + getHeight());
    }

    private void extendTouchLeft() {
        if (this.mViewGroupLeft != null) {
            if (this.mLeftRightTouchFull == 1) {
                for (Class cls : XTouchAreaUtils.CLASSES) {
                    List<View> findViewsByType = XuiUtils.findViewsByType(this.mViewGroupLeft, cls);
                    if (findViewsByType.size() > 0) {
                        View[] viewArr = new View[findViewsByType.size()];
                        findViewsByType.toArray(viewArr);
                        XTouchAreaUtils.extendTouchAreaAsParentSameSize(viewArr, this);
                    }
                }
            } else if (!FlavorUtils.isV5()) {
                XTouchAreaUtils.extendTouchAreaAsParentSameSize(this.mLeftView);
            } else {
                XTouchAreaUtils.extendTouchAreaAsParentSameSize(this.mViewGroupLeft);
            }
        }
    }

    private /* synthetic */ void lambda$extendTouchLeft$1() {
        logD("list-two mLeftView w " + this.mLeftView.getWidth() + " h " + this.mLeftView.getHeight());
    }

    private void extendTouchRight() {
        if (this.mViewGroupRight != null) {
            int i = 0;
            if (this.mLeftRightTouchFull == 2) {
                Class[] clsArr = XTouchAreaUtils.CLASSES;
                int length = clsArr.length;
                while (i < length) {
                    List<View> findViewsByType = XuiUtils.findViewsByType(this.mViewGroupRight, clsArr[i]);
                    if (findViewsByType.size() > 0) {
                        View[] viewArr = new View[findViewsByType.size()];
                        findViewsByType.toArray(viewArr);
                        XTouchAreaUtils.extendTouchAreaAsParentSameSize(viewArr, this);
                    }
                    i++;
                }
            } else if (FlavorUtils.isV5()) {
                Class[] clsArr2 = XTouchAreaUtils.CLASSES;
                int length2 = clsArr2.length;
                while (i < length2) {
                    List<View> findViewsByType2 = XuiUtils.findViewsByType(this.mViewGroupRight, clsArr2[i]);
                    if (findViewsByType2.size() > 0) {
                        View[] viewArr2 = new View[findViewsByType2.size()];
                        findViewsByType2.toArray(viewArr2);
                        XTouchAreaUtils.extendTouchAreaAsParentSameSize(viewArr2, this.mViewRightTouchArea);
                    }
                    i++;
                }
            } else {
                XTouchAreaUtils.extendTouchAreaAsParentSameSize(this.mViewGroupRight);
            }
        }
    }

    private /* synthetic */ void lambda$extendTouchRight$2() {
        logD("list-two mViewGroupRight w " + this.mViewGroupRight.getWidth() + " h " + this.mViewGroupRight.getHeight());
    }

    private void initView() {
        this.mText = (TextView) findViewById(R.id.x_list_tv);
        this.mNum = (TextView) findViewById(R.id.x_list_num);
        this.mLeftView = (ViewGroup) findViewById(R.id.x_list_left_lay);
        this.mTvLay = (ViewGroup) findViewById(R.id.x_list_tv_lay);
        this.mTextSub = (TextView) findViewById(R.id.x_list_tv_sub);
        this.mLeftViewStub = (ViewStub) findViewById(R.id.x_list_left);
        this.mRightViewStub = (ViewStub) findViewById(R.id.x_list_right);
        this.mTag = findViewById(R.id.x_list_tag);
        this.mTagCustomViewStub = (ViewStub) findViewById(R.id.x_list_tag_custom_lay);
        this.mViewRightTouchArea = (ViewGroup) findViewById(R.id.x_list_right_touch_area);
        if (FlavorUtils.isV5() || isInEditMode()) {
            return;
        }
        this.mNum.setTextAppearance(R.style.XFont_Number_Bold);
    }

    public void setNum(int i) {
        this.mNum.setText(String.valueOf(i));
    }

    public void showNum(boolean z) {
        this.mNum.setVisibility(z ? 0 : 8);
        if (FlavorUtils.isV5()) {
            return;
        }
        int dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.x_list_left_width_for_num);
        int dimensionPixelSize2 = getResources().getDimensionPixelSize(R.dimen.x_list_left_width);
        ViewGroup viewGroup = this.mLeftView;
        if (viewGroup != null) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) viewGroup.getLayoutParams();
            if (z) {
                marginLayoutParams.width = dimensionPixelSize;
            } else {
                marginLayoutParams.width = dimensionPixelSize2;
            }
            this.mLeftView.setLayoutParams(marginLayoutParams);
        }
        ViewGroup viewGroup2 = this.mViewGroupLeft;
        if (viewGroup2 != null) {
            ViewGroup.MarginLayoutParams marginLayoutParams2 = (ViewGroup.MarginLayoutParams) viewGroup2.getLayoutParams();
            if (z) {
                marginLayoutParams2.leftMargin = dimensionPixelSize - dimensionPixelSize2;
            } else {
                marginLayoutParams2.leftMargin = 0;
            }
            this.mViewGroupLeft.setLayoutParams(marginLayoutParams2);
        }
        ViewGroup viewGroup3 = this.mTvLay;
        if (viewGroup3 != null) {
            ViewGroup.MarginLayoutParams marginLayoutParams3 = (ViewGroup.MarginLayoutParams) viewGroup3.getLayoutParams();
            if (z) {
                marginLayoutParams3.leftMargin = dimensionPixelSize;
            } else {
                marginLayoutParams3.leftMargin = dimensionPixelSize2;
            }
            this.mTvLay.setLayoutParams(marginLayoutParams3);
        }
        extendTouchLeft();
    }

    public void setText(CharSequence charSequence) {
        this.mText.setText(charSequence);
        this.mText.requestLayout();
    }

    public void setTextSub(CharSequence charSequence) {
        this.mTextSub.setText(charSequence);
    }

    public void setTagIcon(int i) {
        if (FlavorUtils.isV5()) {
            return;
        }
        ((ImageView) this.mTag).setImageResource(i);
    }

    public XTextView getTagView() {
        if (FlavorUtils.isV5()) {
            return (XTextView) this.mTag;
        }
        return null;
    }

    public void showIconTag(boolean z) {
        this.mTag.setVisibility(z ? 0 : 8);
    }

    public boolean isShowIconTag() {
        return this.mTag.getVisibility() == 0;
    }

    public void setTagOnClickListener(View.OnClickListener onClickListener) {
        this.mTag.setOnClickListener(onClickListener);
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
            if (FlavorUtils.isV5()) {
                childAt.setStateListAnimator(null);
            }
            childAt.setEnabled(z);
            childAt.setAlpha(1.0f);
        }
    }

    @Override // com.xiaopeng.vui.commons.IVuiElementListener
    public VuiElement onBuildVuiElement(String str, IVuiElementBuilder iVuiElementBuilder) {
        if (this.mViewGroupLeft != null && !TextUtils.isEmpty(this.mLeftVuiLabel)) {
            for (int i = 0; i < this.mViewGroupLeft.getChildCount(); i++) {
                View childAt = this.mViewGroupLeft.getChildAt(i);
                if (isListNeedSetVuiLabelView(childAt)) {
                    VuiView vuiView = (VuiView) childAt;
                    vuiView.setVuiLabel(this.mLeftVuiLabel);
                    vuiView.setVuiElementId(str + "_" + childAt.getId());
                }
            }
        }
        if (this.mViewGroupRight == null || TextUtils.isEmpty(this.mRightVuiLabel)) {
            return null;
        }
        for (int i2 = 0; i2 < this.mViewGroupRight.getChildCount(); i2++) {
            View childAt2 = this.mViewGroupRight.getChildAt(i2);
            if (isListNeedSetVuiLabelView(childAt2)) {
                VuiView vuiView2 = (VuiView) childAt2;
                vuiView2.setVuiLabel(this.mRightVuiLabel);
                vuiView2.setVuiElementId(str + "_" + childAt2.getId());
            }
        }
        return null;
    }

    public void setVuiLabels(Map<Integer, String> map) {
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            int intValue = entry.getKey().intValue();
            if (intValue == 0) {
                this.mLeftVuiLabel = entry.getValue();
            } else if (intValue == 1) {
                this.mRightVuiLabel = entry.getValue();
            }
        }
        updateVui(this);
    }
}

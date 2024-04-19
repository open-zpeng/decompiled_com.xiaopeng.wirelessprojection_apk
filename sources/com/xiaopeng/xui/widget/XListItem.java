package com.xiaopeng.xui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
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
public class XListItem extends XConstraintLayout implements IVuiElementListener {
    Context context;
    private ViewGroup mBottomContent;
    private ViewStub mBottomViewStub;
    private String mBottomVuiLabel;
    private int mLeftRightTouchFull;
    private ViewStub mLeftViewStub;
    private String mLeftVuiLabel;
    private TextView mNum;
    private ViewStub mRightViewStub;
    private String mRightVuiLabel;
    private XTextView mTag;
    private ViewStub mTagCustomViewStub;
    private TextView mText;
    private ViewGroup mTextContent;
    private TextView mTextSub;
    private ViewGroup mViewGroupBottom;
    private ViewGroup mViewGroupLeft;
    private ViewGroup mViewGroupRight;
    private ViewGroup mViewRightTouchArea;
    String text;
    String textSub;

    public XListItem(Context context) {
        this(context, null);
    }

    public XListItem(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public XListItem(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public XListItem(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mLeftRightTouchFull = 0;
        this.context = context;
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.XListItem);
        String string = obtainStyledAttributes.getString(R.styleable.XListItem_list_item_text);
        String string2 = obtainStyledAttributes.getString(R.styleable.XListItem_list_item_text_sub);
        int resourceId = obtainStyledAttributes.getResourceId(R.styleable.XListItem_list_item_left, -1);
        int resourceId2 = obtainStyledAttributes.getResourceId(R.styleable.XListItem_list_item_right, -1);
        int resourceId3 = obtainStyledAttributes.getResourceId(R.styleable.XListItem_list_item_bottom, -1);
        this.mLeftRightTouchFull = obtainStyledAttributes.getInt(R.styleable.XListItem_list_item_left_right_touch_full, 0);
        LayoutInflater.from(context).inflate(R.layout.x_list_item, this);
        this.mLeftVuiLabel = obtainStyledAttributes.getString(R.styleable.XListItem_list_item_left_vuiLabel);
        this.mRightVuiLabel = obtainStyledAttributes.getString(R.styleable.XListItem_list_item_right_vuiLabel);
        this.mBottomVuiLabel = obtainStyledAttributes.getString(R.styleable.XListItem_list_item_bottom_vuiLabel);
        initView();
        setText(string);
        setTextSub(string2);
        initTag(obtainStyledAttributes);
        initLayout(resourceId, resourceId2, resourceId3);
        obtainStyledAttributes.recycle();
    }

    private void initView() {
        this.mText = (TextView) findViewById(R.id.x_list_tv);
        this.mTextSub = (TextView) findViewById(R.id.x_list_tv_sub);
        this.mNum = (TextView) findViewById(R.id.x_list_num);
        this.mLeftViewStub = (ViewStub) findViewById(R.id.x_list_left);
        this.mRightViewStub = (ViewStub) findViewById(R.id.x_list_right);
        this.mTagCustomViewStub = (ViewStub) findViewById(R.id.x_list_tag_custom_lay);
        this.mTag = (XTextView) findViewById(R.id.x_list_tag);
        this.mViewRightTouchArea = (ViewGroup) findViewById(R.id.x_list_right_touch_area);
        this.mBottomViewStub = (ViewStub) findViewById(R.id.x_list_bottom);
        this.mBottomContent = (ViewGroup) findViewById(R.id.x_bottom_content);
        this.mTextContent = (ViewGroup) findViewById(R.id.x_text_content);
    }

    private void initTag(TypedArray typedArray) {
        Drawable drawable = typedArray.getDrawable(R.styleable.XListItem_list_item_tag_background);
        if (drawable != null) {
            this.mTag.setBackground(drawable);
        }
        this.mTag.setTextColor(typedArray.getColor(R.styleable.XListItem_list_item_tag_text_color, getResources().getColor(R.color.x_color_text_1, this.context.getTheme())));
        if (drawable != null) {
            this.mTag.setBackground(drawable);
        }
        String string = typedArray.getString(R.styleable.XListItem_list_item_tag_text);
        if (string != null) {
            this.mTag.setText(string);
        }
        if (drawable != null || !TextUtils.isEmpty(string)) {
            this.mTag.setVisibility(0);
        } else {
            this.mTag.setVisibility(8);
        }
        int resourceId = typedArray.getResourceId(R.styleable.XListItem_list_item_tag_custom, -1);
        if (resourceId != -1) {
            this.mTagCustomViewStub.setLayoutResource(resourceId);
            this.mTagCustomViewStub.inflate();
        }
    }

    private void initLayout(int i, int i2, int i3) {
        if (i != -1) {
            this.mLeftViewStub.setLayoutResource(i);
            View inflate = this.mLeftViewStub.inflate();
            if (inflate instanceof ViewGroup) {
                this.mViewGroupLeft = (ViewGroup) inflate;
            }
        }
        if (i2 != -1) {
            this.mRightViewStub.setLayoutResource(i2);
            View inflate2 = this.mRightViewStub.inflate();
            if (inflate2 instanceof ViewGroup) {
                this.mViewGroupRight = (ViewGroup) inflate2;
            }
        }
        if (i3 != -1) {
            this.mBottomContent.setVisibility(0);
            this.mBottomViewStub.setLayoutResource(i3);
            View inflate3 = this.mBottomViewStub.inflate();
            if (inflate3 instanceof ViewGroup) {
                this.mViewGroupBottom = (ViewGroup) inflate3;
            }
        } else {
            this.mBottomContent.setVisibility(8);
        }
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) this.mTextContent.getLayoutParams();
        if (this.mViewGroupBottom != null) {
            ((ConstraintLayout) this.mTextContent).setMinHeight(0);
            marginLayoutParams.topMargin = (int) getResources().getDimension(R.dimen.x_list_item_padding1);
            marginLayoutParams.bottomMargin = (int) getResources().getDimension(R.dimen.x_list_item_padding3);
        } else {
            if (TextUtils.isEmpty(this.textSub)) {
                Log.e("XListItem", "initLayout: setMinWidth = " + getResources().getDimension(R.dimen.x_list_item_text_min_height1));
                ((ConstraintLayout) this.mTextContent).setMinHeight((int) getResources().getDimension(R.dimen.x_list_item_text_min_height1));
            } else {
                Log.e("XListItem", "initLayout: setMinWidth = " + getResources().getDimension(R.dimen.x_list_item_text_min_height2));
                ((ConstraintLayout) this.mTextContent).setMinHeight((int) getResources().getDimension(R.dimen.x_list_item_text_min_height2));
            }
            marginLayoutParams.topMargin = (int) getResources().getDimension(R.dimen.x_list_item_padding2);
            marginLayoutParams.bottomMargin = (int) getResources().getDimension(R.dimen.x_list_item_padding2);
        }
        invalidate();
    }

    public void setNum(int i) {
        this.mNum.setText(String.valueOf(i));
    }

    public void showNum(boolean z) {
        this.mNum.setVisibility(z ? 0 : 8);
    }

    public void setText(String str) {
        this.mText.setText(str);
        this.text = str;
    }

    public void setTextSub(String str) {
        if (TextUtils.isEmpty(str)) {
            this.mTextSub.setVisibility(8);
        } else {
            this.mTextSub.setVisibility(0);
            this.mTextSub.setText(str);
        }
        this.textSub = str;
    }

    public XTextView getTagView() {
        return this.mTag;
    }

    public void setTagVisible(boolean z) {
        this.mTag.setVisibility(z ? 0 : 8);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.widget.XConstraintLayout, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        extendTouchLeft();
        extendTouchRight();
    }

    private void extendTouchLeft() {
        ViewGroup viewGroup = this.mViewGroupLeft;
        if (viewGroup != null) {
            if (this.mLeftRightTouchFull == 1) {
                for (Class cls : XTouchAreaUtils.CLASSES) {
                    List<View> findViewsByType = XuiUtils.findViewsByType(this.mViewGroupLeft, cls);
                    if (findViewsByType.size() > 0) {
                        View[] viewArr = new View[findViewsByType.size()];
                        findViewsByType.toArray(viewArr);
                        XTouchAreaUtils.extendTouchAreaAsParentSameSize(viewArr, this);
                    }
                }
                return;
            }
            XTouchAreaUtils.extendTouchAreaAsParentSameSize(viewGroup);
        }
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
                return;
            }
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
        }
    }

    @Override // com.xiaopeng.xui.widget.XConstraintLayout, android.view.View
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
            int i = 0;
            while (true) {
                if (i >= this.mViewGroupLeft.getChildCount()) {
                    break;
                }
                View childAt = this.mViewGroupLeft.getChildAt(i);
                if (isListNeedSetVuiLabelView(childAt)) {
                    VuiView vuiView = (VuiView) childAt;
                    if (TextUtils.isEmpty(vuiView.getVuiLabel())) {
                        vuiView.setVuiLabel(this.mLeftVuiLabel);
                        vuiView.setVuiElementId(str + "_" + childAt.getId());
                        break;
                    }
                }
                i++;
            }
        }
        if (this.mViewGroupRight != null && !TextUtils.isEmpty(this.mRightVuiLabel)) {
            for (int i2 = 0; i2 < this.mViewGroupRight.getChildCount(); i2++) {
                View childAt2 = this.mViewGroupRight.getChildAt(i2);
                if (isListNeedSetVuiLabelView(childAt2)) {
                    VuiView vuiView2 = (VuiView) childAt2;
                    vuiView2.setVuiLabel(this.mRightVuiLabel);
                    vuiView2.setVuiElementId(str + "_" + childAt2.getId());
                }
            }
        }
        if (this.mViewGroupBottom == null || TextUtils.isEmpty(this.mBottomVuiLabel)) {
            return null;
        }
        for (int i3 = 0; i3 < this.mViewGroupBottom.getChildCount(); i3++) {
            View childAt3 = this.mViewGroupBottom.getChildAt(i3);
            if (isListNeedSetVuiLabelView(childAt3)) {
                VuiView vuiView3 = (VuiView) childAt3;
                vuiView3.setVuiLabel(this.mBottomVuiLabel);
                vuiView3.setVuiElementId(str + "_" + childAt3.getId());
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
            } else if (intValue == 2) {
                this.mBottomVuiLabel = entry.getValue();
            }
        }
        updateVui(this);
    }
}

package com.xiaopeng.xui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.core.content.res.ResourcesCompat;
import com.xiaopeng.vui.commons.IVuiElementBuilder;
import com.xiaopeng.vui.commons.IVuiElementListener;
import com.xiaopeng.vui.commons.model.VuiElement;
import com.xiaopeng.xpui.R;
/* loaded from: classes2.dex */
public class XJumpButton extends XConstraintLayout implements IVuiElementListener {
    private LinearLayout contentView;
    private Context context;
    private ImageView imageView;
    private View spaceView;
    private XTextView textSubView;
    private XTextView textView;
    private View xDrawablePaddingView;

    public XJumpButton(Context context) {
        this(context, null);
    }

    public XJumpButton(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public XJumpButton(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public XJumpButton(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.x_jump_ui_button, this);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.XJumpButton);
        int resourceId = obtainStyledAttributes.getResourceId(R.styleable.XJumpButton_jump_button_drawable, -1);
        int i3 = obtainStyledAttributes.getInt(R.styleable.XJumpButton_jump_button_drawable_direction, 0);
        String string = obtainStyledAttributes.getString(R.styleable.XJumpButton_jump_button_text);
        String string2 = obtainStyledAttributes.getString(R.styleable.XJumpButton_jump_button_text_sub);
        initView(i3, resourceId);
        setText(string);
        setTextSub(string2);
        setDrawable(resourceId);
        obtainStyledAttributes.recycle();
    }

    private void initView(int i, int i2) {
        this.contentView = (LinearLayout) findViewById(R.id.x_text_content);
        this.spaceView = findViewById(R.id.x_space);
        if (i == 2) {
            this.imageView = (ImageView) findViewById(R.id.x_button_image_right);
            this.spaceView.setVisibility(0);
            this.contentView.setGravity(19);
        } else {
            this.imageView = (ImageView) findViewById(R.id.x_text_image);
            this.spaceView.setVisibility(8);
            this.contentView.setGravity(17);
            if (i2 != -1) {
                View findViewById = findViewById(R.id.x_space_drawable_padding);
                this.xDrawablePaddingView = findViewById;
                findViewById.setVisibility(0);
            }
            if (i == 0) {
                this.contentView.setOrientation(0);
            } else {
                this.contentView.setOrientation(1);
            }
        }
        this.textView = (XTextView) findViewById(R.id.x_button_text);
        this.textSubView = (XTextView) findViewById(R.id.x_button_text_sub);
        setBackgroundResource(R.drawable.x_btn_real_secoundary_selector);
        setForeground(ResourcesCompat.getDrawable(getResources(), R.drawable.x_tint_press, this.context.getTheme()));
        setClipToOutline(true);
    }

    public void setText(String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        this.textView.setText(str);
    }

    public void setTextSub(String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        this.textSubView.setText(str);
        this.textSubView.setVisibility(0);
    }

    public void setDrawable(int i) {
        if (i == -1) {
            return;
        }
        this.imageView.setVisibility(0);
        this.imageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), i, this.context.getTheme()));
    }

    @Override // com.xiaopeng.vui.commons.IVuiElementListener
    public VuiElement onBuildVuiElement(String str, IVuiElementBuilder iVuiElementBuilder) {
        if (this.textView.getText() == null || !TextUtils.isEmpty(getVuiLabel())) {
            return null;
        }
        setVuiLabel(this.textView.getText().toString());
        return null;
    }
}

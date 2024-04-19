package com.xiaopeng.xui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.res.ResourcesCompat;
import com.xiaopeng.libtheme.ThemeViewModel;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.utils.XLogUtils;
import com.xiaopeng.xui.widget.toggle.XToggleLayout;
/* loaded from: classes2.dex */
public class XToggleButton_V5 extends XToggleLayout {
    private static final String TAG = "XToggleButton_V5";
    private Drawable mIconDr;
    private final int mIconRes;
    private ImageView mIconView;
    private View mIndicatorView;
    private CharSequence mSubtitleTextOff;
    private CharSequence mSubtitleTextOn;
    private TextView mSubtitleTextView;
    private CharSequence mTextOff;
    private CharSequence mTextOn;
    private TextView mTitleTextView;
    private boolean pressAnimEnable;

    public XToggleButton_V5(Context context) {
        this(context, null);
    }

    public XToggleButton_V5(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.toggleButtonV5Style);
    }

    public XToggleButton_V5(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public XToggleButton_V5(final Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.pressAnimEnable = true;
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.XToggleButton_V5, i, i2);
        int resourceId = obtainStyledAttributes.getResourceId(R.styleable.XToggleButton_V5_toggle_content_layout, 0);
        int resourceId2 = obtainStyledAttributes.getResourceId(R.styleable.XToggleButton_V5_toggle_icon, 0);
        this.mIconRes = resourceId2;
        this.mTextOn = obtainStyledAttributes.getText(R.styleable.XToggleButton_V5_android_textOn);
        this.mTextOff = obtainStyledAttributes.getText(R.styleable.XToggleButton_V5_android_textOff);
        this.mSubtitleTextOn = obtainStyledAttributes.getText(R.styleable.XToggleButton_V5_toggle_subtitleOn);
        this.mSubtitleTextOff = obtainStyledAttributes.getText(R.styleable.XToggleButton_V5_toggle_subtitleOff);
        obtainStyledAttributes.recycle();
        if (resourceId != 0) {
            LayoutInflater.from(context).inflate(resourceId, this);
            this.mTitleTextView = (TextView) findViewById(R.id.toggle_title);
            this.mSubtitleTextView = (TextView) findViewById(R.id.toggle_subtitle);
            this.mIconView = (ImageView) findViewById(R.id.toggle_ic);
            this.mIndicatorView = findViewById(R.id.toggle_indicator);
            if (this.mIconView != null && resourceId2 != 0) {
                setIcon(ResourcesCompat.getDrawable(getResources(), resourceId2, context.getTheme()));
            }
            syncTextState();
        } else {
            XLogUtils.w(TAG, "layout not set!");
        }
        this.mXViewDelegate.getThemeViewModel().addCallback(new ThemeViewModel.OnCallback() { // from class: com.xiaopeng.xui.widget.XToggleButton_V5.1
            @Override // com.xiaopeng.libtheme.ThemeViewModel.OnCallback
            public void onThemeChanged() {
                if (XToggleButton_V5.this.mIconView == null || XToggleButton_V5.this.mIconRes == 0) {
                    return;
                }
                XToggleButton_V5 xToggleButton_V5 = XToggleButton_V5.this;
                xToggleButton_V5.setIcon(ResourcesCompat.getDrawable(xToggleButton_V5.getResources(), XToggleButton_V5.this.mIconRes, context.getTheme()));
            }
        });
    }

    public void setTextOn(CharSequence charSequence) {
        this.mTextOn = charSequence;
        syncTextState();
    }

    public void setTextOff(CharSequence charSequence) {
        this.mTextOff = charSequence;
        syncTextState();
    }

    public void setSubtitleOn(CharSequence charSequence) {
        this.mSubtitleTextOn = charSequence;
        syncTextState();
    }

    public void setSubtitleOff(CharSequence charSequence) {
        this.mSubtitleTextOff = charSequence;
        syncTextState();
    }

    private void syncTextState() {
        boolean isChecked = isChecked();
        CharSequence charSequence = isChecked ? this.mTextOn : this.mTextOff;
        CharSequence charSequence2 = isChecked ? this.mSubtitleTextOn : this.mSubtitleTextOff;
        TextView textView = this.mTitleTextView;
        if (textView instanceof Checkable) {
            ((Checkable) textView).setChecked(isChecked);
        }
        TextView textView2 = this.mSubtitleTextView;
        if (textView2 instanceof Checkable) {
            ((Checkable) textView2).setChecked(isChecked);
        }
        if (this.mTitleTextView != null) {
            if (!TextUtils.isEmpty(charSequence)) {
                this.mTitleTextView.setVisibility(0);
                this.mTitleTextView.setText(charSequence);
            } else {
                this.mTitleTextView.setVisibility(8);
            }
        }
        if (this.mSubtitleTextView != null) {
            if (!TextUtils.isEmpty(charSequence2)) {
                this.mSubtitleTextView.setVisibility(0);
                this.mSubtitleTextView.setText(charSequence2);
                return;
            }
            this.mSubtitleTextView.setVisibility(8);
        }
    }

    public void setIcon(Drawable drawable) {
        Drawable drawable2 = this.mIconDr;
        if (drawable2 != null) {
            drawable2.setCallback(null);
        }
        this.mIconDr = drawable;
        ImageView imageView = this.mIconView;
        if (imageView == null || drawable == null) {
            return;
        }
        imageView.setImageDrawable(drawable);
    }

    public void setPressAnimEnable(boolean z) {
        this.pressAnimEnable = z;
    }

    @Override // com.xiaopeng.xui.widget.toggle.XToggleLayout, android.widget.Checkable
    public void setChecked(boolean z) {
        super.setChecked(z);
        syncTextState();
        View view = this.mIndicatorView;
        if (view != null) {
            view.setSelected(z);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.widget.toggle.XToggleLayout, android.view.ViewGroup, android.view.View
    public void drawableStateChanged() {
        super.drawableStateChanged();
        if (this.mXViewDelegate == null || !this.pressAnimEnable) {
            return;
        }
        this.mXViewDelegate.drawableStateChanged();
    }
}

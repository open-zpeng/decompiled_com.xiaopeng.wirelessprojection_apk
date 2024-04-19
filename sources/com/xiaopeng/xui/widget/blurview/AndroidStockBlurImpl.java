package com.xiaopeng.xui.widget.blurview;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RSRuntimeException;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
/* loaded from: classes2.dex */
public class AndroidStockBlurImpl implements BlurImpl {
    static Boolean DEBUG;
    private Allocation mBlurInput;
    private Allocation mBlurOutput;
    private ScriptIntrinsicBlur mBlurScript;
    private RenderScript mRenderScript;

    @Override // com.xiaopeng.xui.widget.blurview.BlurImpl
    public boolean prepare(Context context, Bitmap bitmap, float f) {
        if (this.mRenderScript == null) {
            try {
                RenderScript create = RenderScript.create(context);
                this.mRenderScript = create;
                this.mBlurScript = ScriptIntrinsicBlur.create(create, Element.U8_4(create));
            } catch (RSRuntimeException e) {
                if (isDebug(context)) {
                    throw e;
                }
                release();
                return false;
            }
        }
        this.mBlurScript.setRadius(f);
        Allocation createFromBitmap = Allocation.createFromBitmap(this.mRenderScript, bitmap, Allocation.MipmapControl.MIPMAP_NONE, 1);
        this.mBlurInput = createFromBitmap;
        this.mBlurOutput = Allocation.createTyped(this.mRenderScript, createFromBitmap.getType());
        return true;
    }

    @Override // com.xiaopeng.xui.widget.blurview.BlurImpl
    public void release() {
        Allocation allocation = this.mBlurInput;
        if (allocation != null) {
            allocation.destroy();
            this.mBlurInput = null;
        }
        Allocation allocation2 = this.mBlurOutput;
        if (allocation2 != null) {
            allocation2.destroy();
            this.mBlurOutput = null;
        }
        ScriptIntrinsicBlur scriptIntrinsicBlur = this.mBlurScript;
        if (scriptIntrinsicBlur != null) {
            scriptIntrinsicBlur.destroy();
            this.mBlurScript = null;
        }
        RenderScript renderScript = this.mRenderScript;
        if (renderScript != null) {
            renderScript.destroy();
            this.mRenderScript = null;
        }
    }

    @Override // com.xiaopeng.xui.widget.blurview.BlurImpl
    public void blur(Bitmap bitmap, Bitmap bitmap2) {
        this.mBlurInput.copyFrom(bitmap);
        this.mBlurScript.setInput(this.mBlurInput);
        this.mBlurScript.forEach(this.mBlurOutput);
        this.mBlurOutput.copyTo(bitmap2);
    }

    static boolean isDebug(Context context) {
        if (DEBUG == null && context != null) {
            DEBUG = Boolean.valueOf((context.getApplicationInfo().flags & 2) != 0);
        }
        return DEBUG == Boolean.TRUE;
    }
}

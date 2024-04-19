package com.xiaopeng.vui.commons;
/* loaded from: classes2.dex */
public enum VuiFeedbackType {
    SOUND("Sound"),
    TTS("Tts");
    
    private String type;

    VuiFeedbackType(String str) {
        this.type = str;
    }

    public String getType() {
        return this.type;
    }
}

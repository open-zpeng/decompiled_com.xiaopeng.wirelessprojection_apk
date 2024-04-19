package com.xiaopeng.speech.vui.event;

import android.view.View;
import android.widget.Checkable;
import com.google.gson.internal.LinkedTreeMap;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import com.xiaopeng.speech.vui.utils.LogUtils;
import com.xiaopeng.vui.commons.IVuiElement;
import com.xiaopeng.vui.commons.model.VuiElement;
import java.util.Map;
/* loaded from: classes2.dex */
public class SetCheckEvent extends BaseEvent {
    @Override // com.xiaopeng.speech.vui.event.IVuiEvent
    public <T extends View> T run(T t, VuiElement vuiElement) {
        Boolean bool;
        if (t == null) {
            return null;
        }
        if (vuiElement == null || vuiElement.getResultActions() == null || vuiElement.getResultActions().isEmpty() || (bool = (Boolean) getSetCheck(vuiElement)) == null || ((t instanceof Checkable) && (!bool.booleanValue()) != ((Checkable) t).isChecked())) {
            return t;
        }
        LogUtils.d("SetCheckEvent run on setSelected view");
        boolean z = t instanceof IVuiElement;
        if (z) {
            ((IVuiElement) t).setPerformVuiAction(true);
        }
        t.setPressed(true);
        t.performClick();
        t.setPressed(false);
        if (z) {
            ((IVuiElement) t).setPerformVuiAction(false);
        }
        return t;
    }

    private <T> T getSetCheck(VuiElement vuiElement) {
        Map map;
        if (vuiElement != null && vuiElement.getResultActions() != null && !vuiElement.getResultActions().isEmpty()) {
            Object obj = (String) vuiElement.getResultActions().get(0);
            if ((vuiElement.getValues() instanceof LinkedTreeMap) && (map = (Map) vuiElement.getValues()) != null) {
                if (map.get(obj) instanceof LinkedTreeMap) {
                    Map map2 = (Map) map.get(obj);
                    if (map2 == null || map2.get(VuiConstants.ELEMENT_VALUE) == null) {
                        return null;
                    }
                    return (T) map2.get(VuiConstants.ELEMENT_VALUE);
                } else if (map.get(map) != null) {
                    return (T) map.get(VuiConstants.ELEMENT_VALUE);
                }
            }
        }
        return null;
    }
}

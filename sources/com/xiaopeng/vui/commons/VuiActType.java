package com.xiaopeng.vui.commons;

import android.car.hardware.icm.CarIcmManager;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes2.dex */
public enum VuiActType {
    SEARCH("Search"),
    SELECT("Select"),
    EDIT("Edit"),
    OPEN(CarIcmManager.MSG_CONTENT_OPEN),
    DELETE("Delete"),
    DETAIL("Detail"),
    EXPANDFOLD("ExpandFold"),
    ROLL("Roll"),
    TAB("Tab"),
    SELECTTAB("SelectTab"),
    SLIDE("Slide"),
    UP("Up"),
    DOWN("Down"),
    LEFT("Left"),
    RIGHT("Right"),
    SET("Set"),
    SORT("Sort"),
    EXPAND("Expand"),
    ADD("Add"),
    PLAY(CarIcmManager.MSG_CONTENT_PLAY),
    NULL("Null");
    
    private String type;

    VuiActType(String str) {
        this.type = str;
    }

    public String getType() {
        return this.type;
    }

    public static List<String> getVuiActTypeList() {
        ArrayList arrayList = new ArrayList();
        for (VuiActType vuiActType : values()) {
            arrayList.add(vuiActType.getType());
        }
        return arrayList;
    }
}

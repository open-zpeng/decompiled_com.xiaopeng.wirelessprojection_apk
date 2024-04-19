package com.xiaopeng.xui.vui;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import com.xiaopeng.vui.commons.IVuiElement;
import com.xiaopeng.vui.commons.VuiElementType;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes2.dex */
public class StateFulButtonPlugin {
    private static StateFulButtonPlugin mInstance = new StateFulButtonPlugin();

    private StateFulButtonPlugin() {
    }

    public static StateFulButtonPlugin getInstance() {
        return mInstance;
    }

    public synchronized void setStatefulButtonAttr(VuiView vuiView, int i, String[] strArr, String str) {
        if (vuiView != null && strArr != null) {
            if (strArr.length > 0 && (i >= 0 || i < strArr.length)) {
                if (vuiView instanceof IVuiElement) {
                    if (vuiView instanceof View) {
                        JSONObject vuiProps = vuiView.getVuiProps();
                        if (vuiProps == null) {
                            vuiProps = new JSONObject();
                        }
                        if (i == -1 && (vuiView instanceof TextView)) {
                            TextView textView = (TextView) vuiView;
                            int indexByArrays = getIndexByArrays(strArr, textView.getText() == null ? "" : textView.getText().toString());
                            if (indexByArrays >= 0) {
                                i = indexByArrays;
                            }
                        }
                        JSONObject createStatefulButtonData = createStatefulButtonData(i, strArr, vuiProps, vuiView);
                        if (createStatefulButtonData != null && vuiView != null) {
                            vuiView.setVuiElementType(VuiElementType.STATEFULBUTTON);
                            if (TextUtils.isEmpty(str)) {
                                vuiView.setVuiAction("SetValue");
                            } else {
                                vuiView.setVuiAction(str);
                            }
                            vuiView.setVuiProps(createStatefulButtonData);
                        }
                    }
                }
            }
        }
    }

    private JSONObject createStatefulButtonData(int i, String[] strArr, JSONObject jSONObject, VuiView vuiView) {
        if (strArr == null || strArr.length == 0 || !(vuiView instanceof IVuiElement) || i < 0 || i > strArr.length - 1) {
            return null;
        }
        JSONArray jSONArray = new JSONArray();
        try {
            Object[] objArr = new String[strArr.length];
            int i2 = 0;
            while (i2 < strArr.length) {
                JSONObject jSONObject2 = new JSONObject();
                int i3 = i2 + 1;
                String str = "state_" + i3;
                objArr[i2] = str;
                jSONObject2.put(str, strArr[i2]);
                jSONArray.put(jSONObject2);
                i2 = i3;
            }
            jSONObject.put("states", jSONArray);
            if (vuiView instanceof View) {
                vuiView.setVuiValue(objArr[i], (View) vuiView);
            }
            return jSONObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:12:0x0021, code lost:
        if (r3 >= 0) goto L12;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public synchronized void setStatefulButtonValue(int r2, java.lang.String[] r3, int[] r4, com.xiaopeng.xui.vui.VuiView r5) {
        /*
            r1 = this;
            monitor-enter(r1)
            boolean r0 = r5 instanceof com.xiaopeng.vui.commons.IVuiElement     // Catch: java.lang.Throwable -> L73
            if (r0 == 0) goto L71
            boolean r0 = r5 instanceof android.widget.TextView     // Catch: java.lang.Throwable -> L73
            if (r0 == 0) goto L25
            r4 = r5
            android.widget.TextView r4 = (android.widget.TextView) r4     // Catch: java.lang.Throwable -> L73
            java.lang.CharSequence r0 = r4.getText()     // Catch: java.lang.Throwable -> L73
            if (r0 != 0) goto L15
            java.lang.String r4 = ""
            goto L1d
        L15:
            java.lang.CharSequence r4 = r4.getText()     // Catch: java.lang.Throwable -> L73
            java.lang.String r4 = r4.toString()     // Catch: java.lang.Throwable -> L73
        L1d:
            int r3 = r1.getIndexByArrays(r3, r4)     // Catch: java.lang.Throwable -> L73
            if (r3 < 0) goto L48
        L23:
            r2 = r3
            goto L48
        L25:
            boolean r3 = r5 instanceof android.widget.ImageView     // Catch: java.lang.Throwable -> L73
            if (r3 == 0) goto L30
            int r3 = r1.getIndexByArrays(r4, r2)     // Catch: java.lang.Throwable -> L73
            if (r3 < 0) goto L48
            goto L23
        L30:
            boolean r3 = r5 instanceof com.xiaopeng.xui.widget.slider.XSlider     // Catch: java.lang.Throwable -> L73
            if (r3 == 0) goto L3d
            r2 = r5
            com.xiaopeng.xui.widget.slider.XSlider r2 = (com.xiaopeng.xui.widget.slider.XSlider) r2     // Catch: java.lang.Throwable -> L73
            float r2 = r2.getVuiCurrentIndex()     // Catch: java.lang.Throwable -> L73
            int r2 = (int) r2     // Catch: java.lang.Throwable -> L73
            goto L48
        L3d:
            boolean r3 = r5 instanceof xiaopeng.widget.SimpleSlider     // Catch: java.lang.Throwable -> L73
            if (r3 == 0) goto L48
            r2 = r5
            xiaopeng.widget.SimpleSlider r2 = (xiaopeng.widget.SimpleSlider) r2     // Catch: java.lang.Throwable -> L73
            int r2 = r2.getProgress()     // Catch: java.lang.Throwable -> L73
        L48:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L73
            r3.<init>()     // Catch: java.lang.Throwable -> L73
            java.lang.String r4 = "state_"
            java.lang.StringBuilder r3 = r3.append(r4)     // Catch: java.lang.Throwable -> L73
            int r2 = r2 + 1
            java.lang.StringBuilder r2 = r3.append(r2)     // Catch: java.lang.Throwable -> L73
            java.lang.String r2 = r2.toString()     // Catch: java.lang.Throwable -> L73
            java.lang.Object r3 = r5.getVuiValue()     // Catch: java.lang.Throwable -> L73
            boolean r3 = r2.equals(r3)     // Catch: java.lang.Throwable -> L73
            if (r3 != 0) goto L71
            boolean r3 = r5 instanceof android.view.View     // Catch: java.lang.Throwable -> L73
            if (r3 == 0) goto L71
            r3 = r5
            android.view.View r3 = (android.view.View) r3     // Catch: java.lang.Throwable -> L73
            r5.setVuiValue(r2, r3)     // Catch: java.lang.Throwable -> L73
        L71:
            monitor-exit(r1)
            return
        L73:
            r2 = move-exception
            monitor-exit(r1)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaopeng.xui.vui.StateFulButtonPlugin.setStatefulButtonValue(int, java.lang.String[], int[], com.xiaopeng.xui.vui.VuiView):void");
    }

    private int getIndexByArrays(String[] strArr, String str) {
        if (strArr == null || strArr.length == 0 || str == null) {
            return -1;
        }
        for (int i = 0; i < strArr.length; i++) {
            if (!TextUtils.isEmpty(strArr[i]) && strArr[i].contains("-")) {
                String[] split = strArr[i].split("-");
                if (split != null) {
                    for (String str2 : split) {
                        if (str.equals(str2)) {
                            return i;
                        }
                    }
                    continue;
                } else {
                    continue;
                }
            } else if (str.equals(strArr[i])) {
                return i;
            }
        }
        return -1;
    }

    private int getIndexByArrays(int[] iArr, int i) {
        if (iArr == null || iArr.length == 0 || i < 0) {
            return -1;
        }
        for (int i2 = 0; i2 < iArr.length; i2++) {
            if (i == iArr[i2]) {
                return i2;
            }
        }
        return -1;
    }
}

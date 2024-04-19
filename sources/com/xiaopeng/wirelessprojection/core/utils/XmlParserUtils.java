package com.xiaopeng.wirelessprojection.core.utils;

import android.text.TextUtils;
import com.xiaopeng.lib.utils.LogUtils;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilderFactory;
import org.xml.sax.InputSource;
/* loaded from: classes2.dex */
public class XmlParserUtils {
    public static final String DC_TITLE = "dc:title";
    private static final String TAG = "XmlParserUtils";

    public static String getPropertyValue(String str, String str2) {
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(str2)) {
            LogUtils.e(TAG, "Null or empty xml/name");
            return null;
        }
        try {
            DocumentBuilderFactory newInstance = DocumentBuilderFactory.newInstance();
            newInstance.setNamespaceAware(true);
            String textContent = newInstance.newDocumentBuilder().parse(new InputSource(new StringReader(str.trim()))).getElementsByTagName(str2).item(0).getFirstChild().getTextContent();
            LogUtils.i(TAG, "getPropertyValue name=" + str2 + ", res=" + textContent);
            return textContent;
        } catch (Exception e) {
            LogUtils.e(TAG, "Could not parse xml: " + e.toString());
            return null;
        }
    }
}

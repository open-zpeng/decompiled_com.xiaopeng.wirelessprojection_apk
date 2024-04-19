package com.xiaopeng.lib.utils.config;

import android.text.TextUtils;
import android.util.Log;
import com.xiaopeng.lib.utils.FileUtils;
import com.xiaopeng.lib.utils.ThreadUtils;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
/* loaded from: classes2.dex */
public class EnvConfigHelper {
    static final int DEF_EXPIRE_INTERVAL_MILLIS = 43200000;
    static final String DEF_MAIN_HOST = "https://xmart.deploy-test.xiaopeng.com";
    static final String KEY_MAIN_HOST = "main_host";
    private static final String TAG = "EnvConfig";

    private static String getNextExpireTime() {
        return new SimpleDateFormat("yyyyMMdd HHmmss").format(new Date(System.currentTimeMillis() + 43200000));
    }

    static void saveToPath(Properties properties, String str) {
        FileOutputStream fileOutputStream;
        if (TextUtils.isEmpty(str)) {
            return;
        }
        BufferedOutputStream bufferedOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(new File(str));
            try {
                BufferedOutputStream bufferedOutputStream2 = new BufferedOutputStream(fileOutputStream);
                try {
                    properties.store(bufferedOutputStream2, "pre env file");
                    FileUtils.closeQuietly(bufferedOutputStream2);
                } catch (Throwable th) {
                    th = th;
                    bufferedOutputStream = bufferedOutputStream2;
                    try {
                        Log.e(TAG, "EnvConfigHelper.saveToPath error!");
                        th.printStackTrace();
                        FileUtils.closeQuietly(bufferedOutputStream);
                        FileUtils.closeQuietly(fileOutputStream);
                    } catch (Throwable th2) {
                        FileUtils.closeQuietly(bufferedOutputStream);
                        FileUtils.closeQuietly(fileOutputStream);
                        throw th2;
                    }
                }
            } catch (Throwable th3) {
                th = th3;
            }
        } catch (Throwable th4) {
            th = th4;
            fileOutputStream = null;
        }
        FileUtils.closeQuietly(fileOutputStream);
    }

    public static void saveFile() {
        final Properties cloneConfig = EnvConfig.cloneConfig();
        ThreadUtils.post(0, new Runnable() { // from class: com.xiaopeng.lib.utils.config.EnvConfigHelper.1
            @Override // java.lang.Runnable
            public void run() {
                EnvConfigHelper.saveToPath(cloneConfig, "/sdcard/pre_env.ini");
            }
        });
    }

    public static void setKey(String str, String str2, boolean z) {
        EnvConfig.setString(str, str2);
        if (z) {
            saveFile();
        }
    }

    public static void removeKey(String str, boolean z) {
        if (EnvConfig.hasKey(str)) {
            EnvConfig.removeKey(str);
            if (z) {
                saveFile();
            }
        }
    }

    public static void checkAndSetMainHost() {
        EnvConfig.setString(KEY_MAIN_HOST, DEF_MAIN_HOST);
        EnvConfig.setString("expired_time", getNextExpireTime());
        saveFile();
    }

    public static void removeMainHost() {
        removeKey(KEY_MAIN_HOST, true);
    }
}

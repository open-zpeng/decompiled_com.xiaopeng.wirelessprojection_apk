package com.permissionx.guolindev.dialog;

import com.permissionx.guolindev.request.RequestBackgroundLocationPermission;
import com.permissionx.guolindev.request.RequestInstallPackagesPermission;
import com.permissionx.guolindev.request.RequestManageExternalStoragePermission;
import java.util.Map;
import java.util.Set;
import kotlin.Metadata;
import kotlin.TuplesKt;
import kotlin.collections.MapsKt;
import kotlin.collections.SetsKt;
/* compiled from: PermissionMap.kt */
@Metadata(d1 = {"\u0000\u0016\n\u0000\n\u0002\u0010\"\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010$\n\u0002\b\u0007\"\u0017\u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\u00020\u0001¢\u0006\b\n\u0000\u001a\u0004\b\u0003\u0010\u0004\"\"\u0010\u0005\u001a\u000e\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00020\u00068\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\b\"\"\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00020\u00068\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\b\"\"\u0010\u000b\u001a\u000e\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00020\u00068\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\b¨\u0006\r"}, d2 = {"allSpecialPermissions", "", "", "getAllSpecialPermissions", "()Ljava/util/Set;", "permissionMapOnQ", "", "getPermissionMapOnQ", "()Ljava/util/Map;", "permissionMapOnR", "getPermissionMapOnR", "permissionMapOnS", "getPermissionMapOnS", "permissionx_release"}, k = 2, mv = {1, 5, 1}, xi = 48)
/* loaded from: classes2.dex */
public final class PermissionMapKt {
    private static final Set<String> allSpecialPermissions = SetsKt.setOf((Object[]) new String[]{RequestBackgroundLocationPermission.ACCESS_BACKGROUND_LOCATION, "android.permission.SYSTEM_ALERT_WINDOW", "android.permission.WRITE_SETTINGS", RequestManageExternalStoragePermission.MANAGE_EXTERNAL_STORAGE, RequestInstallPackagesPermission.REQUEST_INSTALL_PACKAGES});
    private static final Map<String, String> permissionMapOnQ;
    private static final Map<String, String> permissionMapOnR;
    private static final Map<String, String> permissionMapOnS;

    public static final Set<String> getAllSpecialPermissions() {
        return allSpecialPermissions;
    }

    static {
        Map<String, String> mapOf = MapsKt.mapOf(TuplesKt.to("android.permission.READ_CALENDAR", "android.permission-group.CALENDAR"), TuplesKt.to("android.permission.WRITE_CALENDAR", "android.permission-group.CALENDAR"), TuplesKt.to("android.permission.READ_CALL_LOG", "android.permission-group.CALL_LOG"), TuplesKt.to("android.permission.WRITE_CALL_LOG", "android.permission-group.CALL_LOG"), TuplesKt.to("android.permission.PROCESS_OUTGOING_CALLS", "android.permission-group.CALL_LOG"), TuplesKt.to("android.permission.CAMERA", "android.permission-group.CAMERA"), TuplesKt.to("android.permission.READ_CONTACTS", "android.permission-group.CONTACTS"), TuplesKt.to("android.permission.WRITE_CONTACTS", "android.permission-group.CONTACTS"), TuplesKt.to("android.permission.GET_ACCOUNTS", "android.permission-group.CONTACTS"), TuplesKt.to("android.permission.ACCESS_FINE_LOCATION", "android.permission-group.LOCATION"), TuplesKt.to("android.permission.ACCESS_COARSE_LOCATION", "android.permission-group.LOCATION"), TuplesKt.to(RequestBackgroundLocationPermission.ACCESS_BACKGROUND_LOCATION, "android.permission-group.LOCATION"), TuplesKt.to("android.permission.RECORD_AUDIO", "android.permission-group.MICROPHONE"), TuplesKt.to("android.permission.READ_PHONE_STATE", "android.permission-group.PHONE"), TuplesKt.to("android.permission.READ_PHONE_NUMBERS", "android.permission-group.PHONE"), TuplesKt.to("android.permission.CALL_PHONE", "android.permission-group.PHONE"), TuplesKt.to("android.permission.ANSWER_PHONE_CALLS", "android.permission-group.PHONE"), TuplesKt.to("com.android.voicemail.permission.ADD_VOICEMAIL", "android.permission-group.PHONE"), TuplesKt.to("android.permission.USE_SIP", "android.permission-group.PHONE"), TuplesKt.to("android.permission.ACCEPT_HANDOVER", "android.permission-group.PHONE"), TuplesKt.to("android.permission.BODY_SENSORS", "android.permission-group.SENSORS"), TuplesKt.to("android.permission.ACTIVITY_RECOGNITION", "android.permission-group.ACTIVITY_RECOGNITION"), TuplesKt.to("android.permission.SEND_SMS", "android.permission-group.SMS"), TuplesKt.to("android.permission.RECEIVE_SMS", "android.permission-group.SMS"), TuplesKt.to("android.permission.READ_SMS", "android.permission-group.SMS"), TuplesKt.to("android.permission.RECEIVE_WAP_PUSH", "android.permission-group.SMS"), TuplesKt.to("android.permission.RECEIVE_MMS", "android.permission-group.SMS"), TuplesKt.to("android.permission.READ_EXTERNAL_STORAGE", "android.permission-group.STORAGE"), TuplesKt.to("android.permission.WRITE_EXTERNAL_STORAGE", "android.permission-group.STORAGE"), TuplesKt.to("android.permission.ACCESS_MEDIA_LOCATION", "android.permission-group.STORAGE"));
        permissionMapOnQ = mapOf;
        permissionMapOnR = mapOf;
        Map mutableMap = MapsKt.toMutableMap(MapsKt.mapOf(TuplesKt.to("android.permission.BLUETOOTH_SCAN", "android.permission-group.NEARBY_DEVICES"), TuplesKt.to("android.permission.BLUETOOTH_ADVERTISE", "android.permission-group.NEARBY_DEVICES"), TuplesKt.to("android.permission.BLUETOOTH_CONNECT", "android.permission-group.NEARBY_DEVICES")));
        mutableMap.putAll(getPermissionMapOnR());
        permissionMapOnS = MapsKt.toMap(mutableMap);
    }

    public static final Map<String, String> getPermissionMapOnQ() {
        return permissionMapOnQ;
    }

    public static final Map<String, String> getPermissionMapOnR() {
        return permissionMapOnR;
    }

    public static final Map<String, String> getPermissionMapOnS() {
        return permissionMapOnS;
    }
}

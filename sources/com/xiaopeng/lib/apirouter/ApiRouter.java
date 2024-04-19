package com.xiaopeng.lib.apirouter;

import android.content.ContentProviderClient;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import com.xiaopeng.lib.apirouter.ClientConstants;
import com.xiaopeng.lib.apirouter.server.ApiPublisherProvider;
import java.util.List;
/* loaded from: classes2.dex */
public class ApiRouter {
    private static final String TAG = "ApiRouter";
    public static final int UID_IVI = 0;
    public static final int UID_RSE = 10;
    private static ApiMatcher mApiMatcher = new ApiMatcher();

    private ApiRouter() {
    }

    private static RemoteOperator publishModule(UriStruct uriStruct, IBinder iBinder, String str) {
        RemoteOperator fromJson = RemoteOperator.fromJson(iBinder, str);
        mApiMatcher.publishUri(uriStruct, fromJson);
        return fromJson;
    }

    private static RemoteOperator wakeRemoteService(UriStruct uriStruct) throws RemoteException {
        if (ApiPublisherProvider.CONTEXT == null) {
            throw new RemoteException("ApiRouter can not route. If it is an asynchronous thread, please check your Context first!");
        }
        Uri.Builder builder = new Uri.Builder();
        if (TextUtils.isEmpty(uriStruct.processTag)) {
            builder.scheme(ClientConstants.BINDER.SCHEME).authority(uriStruct.targetUid + "@" + uriStruct.applicationId + ClientConstants.BINDER.API_SUFFIX);
        } else {
            builder.scheme(ClientConstants.BINDER.SCHEME).authority(uriStruct.targetUid + "@" + uriStruct.applicationId + "." + uriStruct.processTag + ClientConstants.BINDER.API_SUFFIX);
        }
        ContentProviderClient acquireUnstableContentProviderClient = ApiPublisherProvider.CONTEXT.getContentResolver().acquireUnstableContentProviderClient(builder.build());
        if (acquireUnstableContentProviderClient == null) {
            throw new RemoteException("Unknown service " + uriStruct);
        }
        try {
            Bundle call = acquireUnstableContentProviderClient.call(uriStruct.serviceName, null, null);
            if (call == null) {
                throw new RemoteException("Server does not implement call");
            }
            IBinder binder = call.getBinder("binder");
            String string = call.getString("manifest");
            if (binder == null || TextUtils.isEmpty(string)) {
                throw new RemoteException("No matching method");
            }
            return publishModule(uriStruct, binder, string);
        } finally {
            acquireUnstableContentProviderClient.release();
        }
    }

    public static <T> T route(Builder builder) throws RemoteException {
        return (T) route(builder.uri, builder.withBlob, builder.blob, builder.isOneWay, builder.targetUid);
    }

    public static <T> T route(Uri uri) throws RemoteException {
        return (T) route(uri, false, null, false, 0);
    }

    public static <T> T route(Uri uri, boolean z) throws RemoteException {
        return (T) route(uri, false, null, z, 0);
    }

    public static <T> T route(Uri uri, byte[] bArr) throws RemoteException {
        return (T) route(uri, true, bArr, false, 0);
    }

    public static <T> T route(Uri uri, byte[] bArr, boolean z) throws RemoteException {
        return (T) route(uri, true, bArr, z, 0);
    }

    private static <T> T route(Uri uri, boolean z, byte[] bArr, boolean z2, int i) throws RemoteException {
        UriStruct uriStruct = toUriStruct(uri, i);
        RemoteOperator matchRemoteOperator = mApiMatcher.matchRemoteOperator(uriStruct);
        if (matchRemoteOperator == null) {
            matchRemoteOperator = wakeRemoteService(uriStruct);
        } else if (!matchRemoteOperator.isRemoteAlive()) {
            mApiMatcher.unpublishUri(uriStruct);
            matchRemoteOperator = wakeRemoteService(uriStruct);
        }
        return (T) matchRemoteOperator.call(uri, z, bArr, z2);
    }

    private static UriStruct toUriStruct(Uri uri, int i) throws RemoteException {
        String authority = uri.getAuthority();
        if (TextUtils.isEmpty(authority)) {
            throw new RemoteException("Can not find authority in uri");
        }
        int lastIndexOf = authority.lastIndexOf(".");
        if (lastIndexOf == -1) {
            throw new RemoteException("Illegal uri authority");
        }
        int indexOf = authority.indexOf("@");
        if (indexOf != -1) {
            try {
                int parseInt = Integer.parseInt(authority.substring(0, indexOf));
                if (parseInt != i) {
                    i = parseInt;
                }
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
        String substring = authority.substring(indexOf + 1, lastIndexOf);
        String substring2 = authority.substring(lastIndexOf + 1);
        UriStruct uriStruct = new UriStruct();
        uriStruct.applicationId = substring;
        uriStruct.serviceName = substring2;
        uriStruct.targetUid = i;
        List<String> pathSegments = uri.getPathSegments();
        if (pathSegments != null && pathSegments.size() > 1) {
            uriStruct.processTag = pathSegments.get(1);
        }
        return uriStruct;
    }

    /* loaded from: classes2.dex */
    public static class Builder {
        private Uri uri;
        private boolean withBlob = false;
        private byte[] blob = null;
        private boolean isOneWay = false;
        private int targetUid = 0;

        public Builder(Uri uri) {
            this.uri = uri;
        }

        public Builder setWithBlob(boolean z) {
            this.withBlob = z;
            return this;
        }

        public Builder setBlob(byte[] bArr) {
            this.blob = bArr;
            return this;
        }

        public Builder setOneWay(boolean z) {
            this.isOneWay = z;
            return this;
        }

        public Builder setTargetUid(int i) {
            this.targetUid = i;
            return this;
        }
    }
}

package com.linxcool.sdkface;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.TextUtils;

import com.linxcool.sdkface.feature.YmnPreferences;
import com.linxcool.sdkface.feature.YmnWarning;
import com.linxcool.sdkface.util.Logger;
import com.linxcool.sdkface.feature.YmnProperties;

/**
 * 应用上下文信息
 *
 * @author 胡昌海(linxcool.hu)
 */
public class AppConfig {

    private static final String TAG = "AppConfig";

    public static final String SIGN_APP_SECREAT = "NULL-NEED-SET";

    private static final String KEY_APP_ID = "SDK_APP_ID";
    private static final String KEY_CHANNEL_ID = "SDK_CHANNEL_ID";
    private static final String KEY_GROUP_ID = "SDK_GROUP_ID";
    private static final String KEY_AREA_ID = "SDK_AREA_ID";
    private static final String KEY_PRODUCT_ID = "SDK_PRODUCT_ID";
    private static final String KEY_CONFIG_ID = "SDK_CONFIG_ID";
    private static final String KEY_OS_TYPE = "SDK_CLIENT_TYPE";

    private static final String KEY_HOST_URL = "SDK_HOST_VER";

    private static final String KEY_MAIN_ACTIVITY = "MAIN_ACTIVITY";

    private static String pkgName;
    private static String verName;
    private static String verCode;

    private static String appId;
    private static String channelId;
    private static String groupId;
    private static String areaId;
    private static String productId;
    private static String configId;
    private static String hostUrl;

    private static String clientType;
    private static String mainActivity;
    private static boolean debug;

    private static Context context;
    private static Bundle metaData;

    private static boolean inited;

    public static Context getContext() {
        return context;
    }

    public static void init(Context context) {
        try {
            if (inited) {
                return;
            }
            inited = true;

            AppConfig.context = context.getApplicationContext();

            pkgName = context.getPackageName();

            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(pkgName, 0);
            verName = pi.versionName;
            verCode = String.valueOf(pi.versionCode);

            ApplicationInfo ai = pm.getApplicationInfo(pkgName, PackageManager.GET_META_DATA);
            metaData = ai.metaData;
            appId = getMetaDataValue(metaData, KEY_APP_ID);
            if(TextUtils.isEmpty(channelId)) {
                channelId = getMetaDataValue(metaData, KEY_CHANNEL_ID);
            }
            groupId = getMetaDataValue(metaData, KEY_GROUP_ID);
            clientType = getMetaDataValue(metaData, KEY_OS_TYPE);
            areaId = getMetaDataValue(metaData, KEY_AREA_ID);
            productId = getMetaDataValue(metaData, KEY_PRODUCT_ID);
            configId = getMetaDataValue(metaData, KEY_CONFIG_ID);

            hostUrl = getMetaDataValue(metaData, KEY_HOST_URL);

            mainActivity = getMetaDataValue(metaData, KEY_MAIN_ACTIVITY);

            check();
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void check() {
        if (pkgName == null) pkgName = "";
        if (verName == null) verName = "";
        if (verCode == null) verCode = "";
        if (channelId == null) channelId = "";
        if (groupId == null) groupId = "0";
        if (clientType == null) clientType = "1";
        if (areaId == null) areaId = "1";
        if (configId == null) configId = "";
        if (hostUrl == null) hostUrl = "";

        if (TextUtils.isEmpty(appId)) {
            YmnPreferences.adaptStrategy(new YmnWarning("未配置Sdkface：SDK_APP_ID")).burst();
        }
    }

    private static String getMetaDataValue(Bundle metaData, String key) {
        if (metaData == null || !metaData.containsKey(key)) return null;
        return String.valueOf(metaData.get(key));
    }

    public static String getMetaDataValue(String key) {
        if (metaData != null) {
            return getMetaDataValue(metaData, key);
        }
        return null;
    }

    public static String getAppId() {
        return appId;
    }

    public static void setAppId(String appId) {
        AppConfig.appId = appId;
    }

    public static String getChannelId() {
        return channelId;
    }

    public static void setChannelId(String channelId) {
        AppConfig.channelId = channelId;
    }

    public static String getGroupId() {
        return groupId;
    }

    public static void setGroupId(String groupId) {
        AppConfig.groupId = groupId;
    }

    public static String getAreaId() {
        return areaId;
    }

    public static void setAreaId(String areaId) {
        AppConfig.areaId = areaId;
    }

    public static String getProductId() {
        return productId;
    }

    public static void setProductId(String productId) {
        AppConfig.productId = productId;
    }

    public static String getConfigId() {
        return configId;
    }

    public static void setConfigId(String configId) {
        AppConfig.configId = configId;
    }

    public static String getHostUrl() {
        return hostUrl;
    }

    public static void setHostUrl(String hostUrl) {
        AppConfig.hostUrl = hostUrl;
    }

    public static String getClientType() {
        return clientType;
    }

    public static void setClientType(String clientType) {
        AppConfig.clientType = clientType;
    }

    public static String getPkgName() {
        return pkgName;
    }

    public static String getVerName() {
        return verName;
    }

    public static String getVerCode() {
        return verCode;
    }

    public static String getMainActivity() {
        return mainActivity;
    }

    public static void setDebug(boolean debug) {
        AppConfig.debug = debug;
    }

    public static boolean isDebug() {
        return debug;
    }
}

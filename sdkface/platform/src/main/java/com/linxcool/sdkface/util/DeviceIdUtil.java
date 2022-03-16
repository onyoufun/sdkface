package com.linxcool.sdkface.util;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import java.util.UUID;

public class DeviceIdUtil {

    private static final String KEY_MID_NAME = "device_id";
    private static String mid;

    /**
     * 获取设备码
     * @ return
     */
    public static String getDevcieId(Context context) {
        if (!TextUtils.isEmpty(mid)) {
            return mid;
        }
        mid = getLocalDeviceId(context);
        if (!TextUtils.isEmpty(mid)&&!"02:00:00:00:00:00".equalsIgnoreCase(mid)) {
            return mid;
        }
        mid = generateDeviceId(context);
        saveLocalDeviceId(context, mid);
        return mid;
    }

    /**
     * 获取本地缓存
     * @return
     */
    private static String getLocalDeviceId(Context context) {
        String mid = ResourceUtil.readPreferences(context, KEY_MID_NAME);
        if (!TextUtils.isEmpty(mid)) {
            return mid;
        }
        return null;
    }

    private static void saveLocalDeviceId(Context context, String deviceid) {
        ResourceUtil.savePreferences(context, KEY_MID_NAME, deviceid);
    }

    /**
     * 是否为模拟器
     * @return
     */
    private static boolean isEmulator() {
        try {
            return (Build.MODEL.equals("sdk")) || (Build.MODEL.equals("google_sdk"));
        } catch (Exception e) {
        }
        return false;
    }

    private static String generateDeviceId(Context context) {
        String deviceid = getAndroidId(context);
        if (TextUtils.isEmpty(deviceid)||"9774d56d682e549c".equals(deviceid)) {
            deviceid = getMac();
        }
        if (TextUtils.isEmpty(deviceid)||"02:00:00:00:00:00".equalsIgnoreCase(deviceid)) {
            deviceid = getDeviceUUID();
        }
        if (TextUtils.isEmpty(deviceid) || "unknown".equalsIgnoreCase(deviceid)) {
            deviceid = UUID.randomUUID().toString().replace("-", "");
        }
        if (TextUtils.isEmpty(deviceid)) {
            deviceid = "T" + System.currentTimeMillis();
        }
        return deviceid;
    }

    private static String getAndroidId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * android.permission.ACCESS_WIFI_STATE
     * @return
     */
    private static String getMac() {
        return null;
    }

    /**
     * 获得设备硬件uuid
     * 使用硬件信息，计算出一个随机数
     * @return 设备硬件uuid
     */
    private static String getDeviceUUID() {
        try {
            String dev = "LINX-" +
                    Build.BOARD.length() % 10 +
                    Build.BRAND.length() % 10 +
                    Build.DEVICE.length() % 10 +
                    Build.HARDWARE.length() % 10 +
                    Build.ID.length() % 10 +
                    Build.MODEL.length() % 10 +
                    Build.PRODUCT.length() % 10 +
                    Build.SERIAL.length() % 10;
            return new UUID(dev.hashCode(),
                    Build.SERIAL.hashCode()).toString();
        } catch (Exception ex) {
            return "";
        }
    }
}

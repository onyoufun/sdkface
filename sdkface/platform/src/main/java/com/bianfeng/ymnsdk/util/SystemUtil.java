package com.bianfeng.ymnsdk.util;

import java.security.MessageDigest;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Map.Entry;

//import org.apache.http.HttpHost;

import com.bianfeng.ymnsdk.AppConfig;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * 系统工具类
 *
 * @author 胡昌海(linxcool.hu)
 */
public class SystemUtil {

    public static void hideVirtualKey(Activity activity) {
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            activity.getWindow().getDecorView().setSystemUiVisibility(uiOptions);
        }
    }

    /**
     * 获取应用名称
     *
     * @param context
     * @return
     */
    public static String getAppName(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo appInfo = pm.getApplicationInfo(context.getPackageName(), 0);
            return pm.getApplicationLabel(appInfo).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isScreenLandscape(Activity activity) {
        return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE == SystemUtil.getScreenOrient(activity);
    }

    public static int getScreenOrient(Activity activity) {
        int orient = activity.getRequestedOrientation();
        if (orient != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE && orient != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            DisplayMetrics outSize = activity.getResources().getDisplayMetrics();
            orient = outSize.widthPixels < outSize.heightPixels ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        }
        return orient;
    }

    /**
     * 获取网络代理信息
     *
     * @param context
     * @return
     */
//    @SuppressWarnings("deprecation")
//    public static HttpHost getProxy(Context context) {
//        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//
//        NetworkInfo ni = cm.getActiveNetworkInfo();
//        if (ni == null || !ni.isAvailable() || ni.getType() != ConnectivityManager.TYPE_MOBILE)
//            return null;
//
//        String proxyHost = android.net.Proxy.getDefaultHost();
//        int port = android.net.Proxy.getDefaultPort();
//        if (proxyHost != null && port != -1) {
//            return new HttpHost(proxyHost, port);
//        }
//
//        return null;
//    }

    /**
     * md5加密
     *
     * @param input 16位或32位
     * @return
     */
    public static String md5(String input) {
        try {
            String algorithm = System.getProperty("MD5.algorithm", "MD5");
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] bs = md.digest(input.getBytes("utf-8"));
            return bytesToHexString(bs);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 字节数组转16进制字符
     *
     * @param src
     * @return
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder sb = new StringBuilder();
        if (src == null || src.length <= 0) return null;
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) sb.append(0);
            sb.append(hv);
        }
        return sb.toString();
    }

    public static String getSign(TreeMap<String, String> map) {
        return getSign(map, AppConfig.SIGN_APP_SECREAT);
    }

    /***
     * 获取签名参数
     *
     * @param map
     * @return
     */
    public static String getSign(TreeMap<String, String> map, String appSecret) {
        StringBuilder sb = new StringBuilder();
        Iterator<Entry<String, String>> iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<String, String> entry = iter.next();
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "&");
        }
        sb.append(appSecret);
        return SystemUtil.md5(sb.toString());
    }

    /***
     * 生成指定位数的随机数
     */
    public static String getRandom(int digit) {
        String strRand = "";
        for (int i = 0; i < digit; i++) {
            strRand += String.valueOf((int) (Math.random() * 10));
        }
        return strRand;
    }

    public static boolean isMainProcess(Context context) {
        try {
            int pid = android.os.Process.myPid();
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
                if (appProcess.pid == pid) {
                    return context.getApplicationInfo().packageName.equals(appProcess.processName);
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

}

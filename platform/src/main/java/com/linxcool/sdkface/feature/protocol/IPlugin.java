package com.linxcool.sdkface.feature.protocol;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public interface IPlugin extends Serializable {

    int STATE_EMPTY = -10;

    /**
     * 等待安装
     */
    int STATE_PENDING_INSTALL = -5;

    /**
     * 安装中（含下载）
     */
    int STATE_INSTALLING = -4;

    /**
     * 已安装，等待校验或运行或关闭
     */
    int STATE_INSTALLED = -3;

    /**
     * 等待检查状态
     */
    int STATE_PENDING_CHECK = -2;

    /**
     * 网络检查中
     */
    int STATE_CHECKING = -1;

    /**
     * 关闭状态
     */
    int STATE_CLOSED = 0;

    /**
     * 打开状态
     */
    int STATE_WORKING = 1;

    /**
     * 返回插件ID
     *
     * @return
     */
    String getPluginId();

    /**
     * 返回插件名
     *
     * @return
     */
    String getPluginName();

    /**
     * 返回插件版本（XxxInterface文件）
     *
     * @return
     */
    int getPluginVersion();

    /**
     * 返回SDK版本（三方SDK版本）
     *
     * @return
     */
    String getSdkVersion();

    boolean isSupportFunction(String functionName);

    void callFunction(String functionName, LinkedHashMap<String, String> data);

    String callFunctionWithResult(String functionName, LinkedHashMap<String, String> data);

    void callFunction(String functionName, String... data);

    String callFunctionWithResult(String functionName, String... data);

    void setDebugMode(boolean mode);

    boolean isDebugMode();

    void onInit(Context context);

    void onStart();

    void onRestart();

    void onPause();

    void onResume();

    void onStop();

    void onDestroy();

    void onNewIntent(Intent intent);

    void onActivityResult(int requestCode, int resultCode, Intent data);

    void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults);

    void onLogin(Map<String, String> data);

    void onPay(Map<String, String> data);

    public class STATE_NAME {
        static Map<Integer, String> names = new HashMap<>();

        public static String get(Integer state) {
            String name = names.get(state);
            if (TextUtils.isEmpty(name)) return "UNSET";
            return name;
        }

        static {
            Field[] fields = IPlugin.class.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                String name = field.getName();
                if (name.startsWith("STATE_")) {
                    try {
                        names.put(field.getInt(null), name);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

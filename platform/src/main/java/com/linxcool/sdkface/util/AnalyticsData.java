package com.linxcool.sdkface.util;

import android.content.Context;
import android.text.TextUtils;
import com.linxcool.sdkface.YmnCode;
import com.linxcool.sdkface.feature.YmnPluginWrapper;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class AnalyticsData {

    public static final int DATA_SUCCESS = 1;
    public static final int DATA_FAIL = -1;
    public static final int DATA_CANCEL = -2;

    public static final String KEY_TRANSACTIONID = "transactionId";

    private static Set<String> blackFunctions = new HashSet<String>();

    private static String transactionId;

    public static String getTransactionId() {
        if(TextUtils.isEmpty(transactionId))
            transactionId = "";
        return transactionId;
    }

    private static String createTransactionId(Context context) {
        return SystemUtil.md5(UUID.randomUUID().toString() + System.currentTimeMillis() + SystemUtil.getRandom(3));
    }

    public static void init(Context context) {
        DataFunAgent.init(context);
    }

    /***
     * 调用第三方登录事件
     * @param wrapper
     */
    public static void loginThirdEvent(YmnPluginWrapper wrapper) {
        Map<String, Object> customs = new HashMap<>();
        customs.put("platformId", wrapper.getPluginId());
        customs.put("sdkVersion", wrapper.getSdkVersion());
        customs.put(KEY_TRANSACTIONID, transactionId = createTransactionId(wrapper.getContext()));
        datafunOnEvent("1010101", "1", customs);
    }

    /***
     * 调用第三方登录返回事件
     * @param wrapper
     * @param code
     * @param msg
     */
    public static void loginThirdResEvent(YmnPluginWrapper wrapper, int code, String msg) {
        Map<String, Object> customs = new HashMap<>();
        customs.put("platformId", wrapper.getPluginId());
        customs.put("sdkVersion", wrapper.getSdkVersion());
        customs.put(KEY_TRANSACTIONID, transactionId);
        switch (code) {
            case YmnCode.ACTION_RET_LOGIN_SUCCESS:
                datafunOnEvent("1010101", "2", customs, msg);
                break;
            case YmnCode.ACTION_RET_LOGIN_CANCEL:
                datafunOnEvent("1010101", "4", customs, msg);
                break;
            case YmnCode.ACTION_RET_LOGIN_FAIL:
                datafunOnEvent("1010101", "3", customs, msg);
                break;
        }
    }

    /***
     * 服务器返回登录状态事件
     * @param wrapper
     * @param code
     * @param msg
     */
    public static void loginServerResEvent(YmnPluginWrapper wrapper, int code, String msg, String loginTransactionId) {
        Map<String, Object> customs = new HashMap<>();
        customs.put("platformId", wrapper.getPluginId());
        customs.put("sdkVersion", wrapper.getSdkVersion());
        customs.put(KEY_TRANSACTIONID, loginTransactionId);
        switch (code) {
            case DATA_SUCCESS:
                datafunOnEvent("1010103", "1", customs, msg);
                break;
            case DATA_FAIL:
                datafunOnEvent("1010103", "2", customs, msg);
                break;
        }
    }

    /***
     * 请求支付订单事件
     * @param wrapper
     */
    public static void payServerEvent(YmnPluginWrapper wrapper) {
        Map<String, Object> customs = new HashMap<>();
        customs.put("platformId", wrapper.getPluginId());
        customs.put("sdkVersion", wrapper.getSdkVersion());
        customs.put(KEY_TRANSACTIONID, transactionId = createTransactionId(wrapper.getContext()));
        datafunOnEvent("1010204", "1", customs);
    }

    /***
     * 服务器返回支付订单事件
     * @param wrapper
     * @param code
     * @param msg
     */
    public static void payServerResEvent(YmnPluginWrapper wrapper, int code, String msg, String paymentTransactionId) {
        Map<String, Object> customs = new HashMap<>();
        customs.put("platformId", wrapper.getPluginId());
        customs.put("sdkVersion", wrapper.getSdkVersion());
        customs.put(KEY_TRANSACTIONID, transactionId = paymentTransactionId);
        switch (code) {
            case DATA_SUCCESS:
                datafunOnEvent("1010203", "1", customs, msg);
                break;
            case DATA_FAIL:
               datafunOnEvent("1010203", "2", customs, msg);
                break;
        }
    }

    /***
     * 调用第三方支付事件
     * @param wrapper
     * @param code
     * @param msg
     */
    public static void payThirdResEvent(YmnPluginWrapper wrapper, int code, String msg) {
        Map<String, Object> customs = new HashMap<>();
        customs.put("platformId", wrapper.getPluginId());
        customs.put("sdkVersion", wrapper.getSdkVersion());
        customs.put(KEY_TRANSACTIONID, transactionId);
            switch (code) {
            case YmnCode.PAYRESULT_SUCCESS:
                datafunOnEvent("1010204", "2", customs, msg);
                break;
            case YmnCode.PAYRESULT_CANCEL:
                datafunOnEvent("1010204", "4", customs, msg);
                break;
            case YmnCode.PAYRESULT_FAIL:
                datafunOnEvent("1010204", "3", customs, msg);
                break;
        }
    }

    private static void datafunOnEvent(String eventId, String ext, Map<String, Object> map) {
        datafunOnEvent(eventId, ext, map, null);
    }

    private static void datafunOnEvent(String eventId, String ext, Map<String, Object> map, String msg) {
        Map<String, Object> dfMap = new HashMap<>(map);
        dfMap.putAll(jsonStringToMap(msg));
        DataFunAgent.onEvent(eventId, ext, dfMap);
    }

    public static Map<String, Object> jsonStringToMap(String jsonStr) {
        Map<String, Object> valueMap = new HashMap<String, Object>();
        if (TextUtils.isEmpty(jsonStr)) {
            Logger.i("AnalyticsData", "onCallback msg is null");
            return valueMap;
        }
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            Iterator<String> keyIter = jsonObject.keys();
            while (keyIter.hasNext()) {
                String key = keyIter.next();
                valueMap.put(key, jsonObject.get(key));
            }
            return valueMap;
        } catch (Exception e) {
            valueMap.put("msg", jsonStr);
            return valueMap;
        }
    }

    /**
     * 添加方法黑名单，不计入数据埋点
     *
     * @param functionName
     */
    public static void addBlackFunction(String functionName) {
        blackFunctions.add(functionName);
    }

    public static void callFunctionEvent(String functionName) {
        if (!blackFunctions.contains(functionName)) {
            DataFunAgent.testCallFunction(functionName);
        }
    }

    public static void callFunctionEvent(String functionName, String[] args) {
        if (!blackFunctions.contains(functionName)) {
            DataFunAgent.testCallFunction(functionName, args);
        }
    }
}

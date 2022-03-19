package com.linxcool.sdkface.template;

import android.text.TextUtils;

import com.linxcool.sdkface.feature.protocol.IPaymentFeature;
import com.linxcool.sdkface.feature.protocol.IUserFeature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by huchanghai on 2017/11/6.
 */
public class TemplateData {

    private static HashMap<String, Integer> functionMap = new LinkedHashMap<String, Integer>() {
        {
            put("onStart", 0);
            put("onRestart", 0);
            put("onPause", 0);
            put("onResume", 0);
            put("onStart", 0);
            put("onStop", 0);
            put("onDestroy", 0);
            put("onNewIntent", 0);
            put("onActivityResult", 0);

            put(IUserFeature.FUNCTION_IS_LOGINED, 0);
            put(IUserFeature.FUNCTION_LOGOUT, 0);
            put(IUserFeature.FUNCTION_SHOW_TOOLBAR, 0);
            put(IUserFeature.FUNCTION_HIDE_TOOLBAR, 0);
            put(IUserFeature.FUNCTION_ACCOUNT_SWITCH, 0);
            put(IUserFeature.FUNCTION_EXIT, 0);
            put(IUserFeature.FUNCTION_SUBMIT_USERINFO, 0);
            put(IUserFeature.FUNCTION_GET_USER_INFO, 0);
            put(IUserFeature.FUNCTION_ENTER_PLATFORM, 0);
        }
    };

    private static ArrayList<String> cpOrderKeys = new ArrayList<String>() {
        {
            add(IPaymentFeature.ARG_CP_ORDER_ID);
            add(IPaymentFeature.ARG_PRODUCT_ID);
            add(IPaymentFeature.ARG_PRODUCT_NAME);
            add(IPaymentFeature.ARG_PRODUCT_PRICE);
            add(IPaymentFeature.ARG_PRODUCT_COUNT);
            add(IPaymentFeature.ARG_ROLE_ID);
            add(IPaymentFeature.ARG_ROLE_NAME);
            add(IPaymentFeature.ARG_ROLE_GRADE);
            add(IPaymentFeature.ARG_ROLE_BALANCE);
            add(IPaymentFeature.ARG_SERVER_ID);
            add(IPaymentFeature.ARG_NOTIFY_URL);
            add(IPaymentFeature.ARG_EXT);
        }
    };

    public static HashMap<String, Integer> getFunctionMap() {
        return functionMap;
    }

    public static void onFunctionEvent(String functionName) {
        HashMap<String, Integer> map = getFunctionMap();
        if (map.containsKey(functionName)) {
            map.put(functionName, map.get(functionName) + 1);
        }
    }

    public static HashMap<String, String> getFunctionNotes() {
        HashMap<String, String> descMap = new HashMap<>();

        descMap.put("onStart", "*生命周期必调方法");
        descMap.put("onRestart", "*生命周期必调方法");
        descMap.put("onPause", "*生命周期必调方法");
        descMap.put("onResume", "*生命周期必调方法");
        descMap.put("onStop", "*生命周期必调方法");
        descMap.put("onDestroy", "*生命周期必调方法");
        descMap.put("onNewIntent", "*必调方法");
        descMap.put("onActivityResult", "*必调方法");

        descMap.put(IUserFeature.FUNCTION_SHOW_TOOLBAR, "*登录后必调方法");
        descMap.put(IUserFeature.FUNCTION_EXIT, "*游戏退出必调方法");
        descMap.put(IUserFeature.FUNCTION_SUBMIT_USERINFO, "*获取角色信息后必调方法");

        return descMap;
    }

    public static StringBuffer getLackedParams(Map<String, String> map) {
        StringBuffer lackedParams = new StringBuffer();
        for (String key : cpOrderKeys) {
            if (TextUtils.isEmpty(map.get(key))) {
                lackedParams.append("\n  " + key);
            }
        }
        if (!TextUtils.isEmpty(lackedParams)) {
            lackedParams = new StringBuffer("以下参数缺失(请务必补充，否者会导致支付失败)：" + lackedParams);
        }
        return lackedParams;
    }

    public static StringBuffer getOfferedParams(Map<String, String> map) {
        StringBuffer offeredParams = new StringBuffer();
        offeredParams.append("已传入参数：");
        for (String key : cpOrderKeys) {
            if (!TextUtils.isEmpty(map.get(key))) {
                offeredParams.append("\n  " + key + " = " + map.get(key));
            }
        }
        return offeredParams;
    }

    public static class FunctionEvent {

        String functionName;
        String status;
        Map<String, Object> map;

        public FunctionEvent(String functionName) {
            this.functionName = functionName;
        }

        public static FunctionEvent create(String functionName) {
            return new FunctionEvent(functionName);
        }

        public FunctionEvent status(String status) {
            this.status = status;
            return this;
        }

        public FunctionEvent parameters(String key, Object value) {
            if (map == null) {
                map = new HashMap<>();
            }
            map.put(key, value);
            return this;
        }

        public FunctionEvent parameters(Map<String, String> ps) {
            if (map == null) {
                map = new HashMap<>();
            }
            map.putAll(ps);
            return this;
        }

        public void send() {

        }
    }
}

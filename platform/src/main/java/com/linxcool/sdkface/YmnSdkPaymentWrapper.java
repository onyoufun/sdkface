package com.linxcool.sdkface;

import com.linxcool.sdkface.feature.YmnPluginWrapper;
import com.linxcool.sdkface.feature.plugin.PaymentFeatureWrapper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by huchanghai on 2017/8/31.
 */
public class YmnSdkPaymentWrapper extends YmnSdkWrapper {

    private static Set<PaymentFeatureWrapper> wrappers;
    private static Map<String, PaymentFeatureWrapper> autoFunctions;

    public static Set<PaymentFeatureWrapper> getPaymentWrappers() {
        return wrappers;
    }

    public static PaymentFeatureWrapper getPaymentDefault() {
        if (wrappers == null || wrappers.isEmpty()) {
            return null;
        }
        return wrappers.iterator().next();
    }

    public static void registPaymentFeatureWrapper(PaymentFeatureWrapper wrapper) {
        if (wrappers == null) {
            wrappers = new HashSet<>();
        }
        wrappers.add(wrapper);

        registPaymentFeatureAutoFunctions(wrapper);
    }

    private static void registPaymentFeatureAutoFunctions(PaymentFeatureWrapper wrapper) {
        if (autoFunctions == null) {
            autoFunctions = new HashMap<>();
        }
        YmnPluginWrapper plugin = wrapper.getPluginWrapper();
        String name = plugin.getPluginName();
        autoFunctions.put(name + "_pay", wrapper);
    }

    public static boolean isSupportFunction(String functionName) {
        if (autoFunctions != null && autoFunctions.containsKey(functionName)) {
            return true;
        }
        return YmnSdkWrapper.isSupportFunction(functionName);
    }

    public static void callFunction(String functionName, String... args) {
        if (autoFunctions != null && autoFunctions.containsKey(functionName) && YmnStrategy.isJsonParamers(args)) {
            autoFunctions.get(functionName).pay(YmnStrategy.arrayParamersAsMap(args));
        } else {
            YmnSdkWrapper.callFunction(functionName, args);
        }
    }

    public static void callFunction(String functionName, LinkedHashMap<String, String> data) {
        if (autoFunctions != null && autoFunctions.containsKey(functionName)) {
            autoFunctions.get(functionName).pay(data);
        } else {
            YmnSdkWrapper.callFunction(functionName, data);
        }
    }

    /**
     * 默认插件策略是否有效
     *
     * @return
     */
    private static boolean availableDefault() {
        if (wrappers == null || wrappers.isEmpty() || wrappers.size() > 1) {
            // Logger.e("not exist available PaymentFeature plugin");
            return false;
        }
        return true;
    }

    public static void pay(Map<String, String> order) {
        if (availableDefault()) {
            getPaymentDefault().pay(order);
        }
    }

    public static String getOrderId() {
        if (availableDefault()) {
            getPaymentDefault().getOrderId();
        }
        return null;
    }

    public static void checkOrder(String oderId, int orderType) {
        if (availableDefault()) {
            getPaymentDefault().checkOrder(oderId, orderType);
        }
    }

}

package com.linxcool.sdkface;

import com.linxcool.sdkface.feature.YmnPluginWrapper;
import com.linxcool.sdkface.feature.plugin.UserFeatureWrapper;
import com.linxcool.sdkface.feature.protocol.IUserFeature;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by huchanghai on 2017/8/31.
 */
public class YmnSdkUserWrapper extends YmnSdkPaymentWrapper {

    private static Set<UserFeatureWrapper> wrappers;
    private static Map<String, UserFeatureWrapper> autoFunctions;

    public static Set<UserFeatureWrapper> getUserWrappers() {
        return wrappers;
    }

    public static UserFeatureWrapper getUserDefault() {
        if (wrappers == null || wrappers.isEmpty()) {
            return null;
        }
        return wrappers.iterator().next();
    }

    public static void registUserFeatureWrapper(UserFeatureWrapper wrapper) {
        if (wrappers == null) {
            wrappers = new HashSet<>();
        }
        wrappers.add(wrapper);

        registUserFeatureAutoFunctions(wrapper);
    }

    private static void registUserFeatureAutoFunctions(UserFeatureWrapper wrapper) {
        if (autoFunctions == null) {
            autoFunctions = new HashMap<>();
        }
        YmnPluginWrapper plugin = wrapper.getPluginWrapper();
        String name = plugin.getPluginName();
        autoFunctions.put(name + "_login", wrapper);
    }

    public static boolean isSupportFunction(String functionName) {
        if (autoFunctions != null && autoFunctions.containsKey(functionName)) {
            return true;
        }
        return YmnSdkPaymentWrapper.isSupportFunction(functionName);
    }

    public static void callFunction(String functionName) {
        if (autoFunctions != null && autoFunctions.containsKey(functionName)) {
            autoFunctions.get(functionName).login();
        } else {
            YmnSdkPaymentWrapper.callFunction(functionName);
        }
    }

    /**
     * 默认插件策略是否有效
     *
     * @return
     */
    private static boolean availableDefault() {
        if (wrappers == null || wrappers.isEmpty() || wrappers.size() > 1) {
            // Logger.e("not exist available UserFeature plugin");
            return false;
        }
        return true;
    }

    public static void login() {
        if (availableDefault()) {
            getUserDefault().login();
        }
    }

    public static boolean isLogined() {
        if (availableDefault()) {
            return getUserDefault().isLogined();
        }
        return false;
    }

    public static void logout() {
        if (availableDefault()) {
            getUserDefault().logout();
        }
    }

    public static void showToolBar() {
        if (availableDefault()) {
            getUserDefault().showToolBar();
        }
    }

    public static void hideToolBar() {
        if (availableDefault()) {
            getUserDefault().hideToolBar();
        }
    }

    public static void switchAccount() {
        if (availableDefault()) {
            getUserDefault().switchAccount();
        }
    }

    public static void exit() {
        if (availableDefault()) {
            getUserDefault().exit();
        }
    }

    public static void submitUserInfo(LinkedHashMap<String, String> data) {
        if (availableDefault()) {
            getUserDefault().submitUserInfo(data);
        }
    }

    public static IUserFeature.UserInfo getUserInfo() {
        if (availableDefault()) {
            return getUserDefault().getUserInfo();
        }
        return null;
    }

    public static void enterPlatform() {
        if (availableDefault()) {
            getUserDefault().enterPlatform();
        }
    }
}

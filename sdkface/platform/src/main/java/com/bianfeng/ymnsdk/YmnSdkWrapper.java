package com.bianfeng.ymnsdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.bianfeng.ymnsdk.feature.YmnPluginManager;
import com.bianfeng.ymnsdk.feature.YmnPreferences;
import com.bianfeng.ymnsdk.feature.YmnProperties;
import com.bianfeng.ymnsdk.feature.YmnURLManager;
import com.bianfeng.ymnsdk.util.AnalyticsData;
import com.bianfeng.ymnsdk.util.Logger;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

public class YmnSdkWrapper {

    private static Activity activity;
    private static boolean inited;
    private static Set<YmnCallback> callbacks = new HashSet<>();
    private static YmnCallback dispatcher = new YmnCallback() {
        @Override
        public void onCallBack(int code, String msg) {
            Logger.d(String.format("dispatcher callbacks(%d) for result(%d | %s)", callbacks.size(), code, msg));
            for (YmnCallback callback : callbacks) {
                callback.onCallBack(code, msg);
            }
        }
    };

    public static void registCallback(YmnCallback callback) {
        callbacks.add(callback);
    }

    public static void removeCallback(YmnCallback callback) {
        callbacks.remove(callback);
    }

    public static void clearCallbacks() {
        callbacks.clear();
    }

    public static void dispatchMessage(int code, String msg) {
        dispatcher.onCallBack(code, msg);
    }

    public static void initialize(Activity activity) {
        YmnSdkWrapper.activity = activity;
        innerInit(activity);
    }

    public static void innerInit(Context base) {
        if (!inited) {
            Context context = base instanceof Activity ? base.getApplicationContext() : base;
            AnalyticsData.init(context);
            YmnPreferences.init(context);
            YmnProperties.init(context);
            AppConfig.init(context);
            YmnURLManager.init(context);
            YmnPluginManager.registCallback(dispatcher);
            inited = true;
        }
        YmnPluginManager.init(base);
    }

    public static boolean isSupportFunction(String functionName) {
        return YmnPluginManager.isSupportFunction(functionName);
    }

    public static void callFunction(final String functionName) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                YmnPluginManager.callFunction(functionName);
                AnalyticsData.callFunctionEvent(functionName);
            }
        });
    }

    public static void callFunction(final String functionName, final String... args) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (YmnStrategy.isJsonParamers(args)) {
                    YmnPluginManager.callFunction(functionName, YmnStrategy.arrayParamersAsMap(args));
                } else {
                    YmnPluginManager.callFunction(functionName, args);
                }
                AnalyticsData.callFunctionEvent(functionName, args);
            }
        });
    }

    public static void callFunction(final String functionName, final LinkedHashMap<String, String> data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                YmnPluginManager.callFunction(functionName, data);
            }
        });
    }

    public static String callFunctionWithResult(String functionName, String... args) {
        if (YmnStrategy.isJsonParamers(args)) {
            return YmnPluginManager.callFunctionWithResult(functionName, YmnStrategy.arrayParamersAsMap(args));
        } else {
            return YmnPluginManager.callFunctionWithResult(functionName, args);
        }
    }

    public static String callFunctionWithResult(String functionName, LinkedHashMap<String, String> data) {
        return YmnPluginManager.callFunctionWithResult(functionName, data);
    }

    public static void setDebugMode(boolean mode) {
        YmnPluginManager.setDebugMode(mode);
        Logger.showDebugLog(mode);
    }

    public static void onStart() {
        YmnPluginManager.onStart();
    }

    public static void onRestart() {
        YmnPluginManager.onRestart();
    }

    public static void onPause() {
        YmnPluginManager.onPause();
    }

    public static void onResume() {
        YmnPluginManager.onResume();
    }

    public static void onStop() {
        YmnPluginManager.onStop();
    }

    public static void onDestroy() {
        YmnPluginManager.onDestroy();
    }

    public static void onNewIntent(Intent intent) {
        YmnPluginManager.onNewIntent(intent);
    }

    public static void onActivityResult(int requestCode, int resultCode, Intent data) {
        YmnPluginManager.onActivityResult(requestCode, resultCode, data);
    }

    public static void runOnUiThread(Runnable runnable) {
        if (activity == null || activity.isFinishing()) {
            Logger.e("activity is null or finishing, ignore target to ui thread");
        } else {
            activity.runOnUiThread(runnable);
        }
    }

}

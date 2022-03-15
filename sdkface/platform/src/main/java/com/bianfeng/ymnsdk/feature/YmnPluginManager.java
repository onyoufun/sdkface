package com.bianfeng.ymnsdk.feature;

import android.content.Context;
import android.content.Intent;

import com.bianfeng.ymnsdk.YmnCallback;
import com.bianfeng.ymnsdk.YmnStrategy;
import com.bianfeng.ymnsdk.action.ActionObserver;
import com.bianfeng.ymnsdk.action.ActionSupport;
import com.bianfeng.ymnsdk.action.RequestPluginsStatusAction;
import com.bianfeng.ymnsdk.entity.PluginConfig;
import com.bianfeng.ymnsdk.entity.PluginLocalState;
import com.bianfeng.ymnsdk.util.Logger;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 插件管理类
 *
 * @author huchanghai
 */
public class YmnPluginManager {

    private static HashMap<String, YmnPluginWrapper> plugins = new HashMap<>();
    private static boolean inited = false;
    private static YmnCallback callback;

    public static void registCallback(YmnCallback callback) {
        YmnPluginManager.callback = callback;
    }

    public static void init(Context context) {
        if (!inited) {
            YmnPluginLoader.init(context);
            loadHostPlugins(context);
            loadAssetsPlugins(context);
            loadLocalPlugins(context);
            // 断开服务器请求，暂时仅适用本地控制
            // requestPluginsStateConfig(context);
            inited = true;
        }

        YmnPluginInjector.inject(context, plugins);
        checkPluginsLocalState(context);
        onInit(context);
    }

    private static void loadHostPlugins(Context context) {
        List<YmnPluginWrapper> list = YmnPluginLoader.loadHostPlugins(context);
        // FixExcutorInjector.inject(context, list);
        cachePlugins(list);
    }

    private static void loadAssetsPlugins(Context context) {
        // TODO 后期添加该功能
    }

    private static void loadLocalPlugins(Context context) {
        // TODO 后期添加该功能
    }

    private static void cachePlugins(List<YmnPluginWrapper> items) {
        if (items != null && !items.isEmpty()) {
            for (YmnPluginWrapper plugin : items) {
                plugins.put(YmnPlugin.YPluginKey.get(plugin), plugin);
            }
        }
    }

    private static void checkPluginsLocalState(Context context) {
        PluginLocalState local = YmnPreferences.loadPluginState(context);
        for (YmnPluginWrapper plugin : plugins.values()) {
            if (!plugin.isCheckedState()) {
                plugin.checkState(local);
                printPluginLocalState("AfterCheckState", plugin);
            }
        }
    }

    private static void requestPluginsStateConfig(final Context context) {
        RequestPluginsStatusAction action = new RequestPluginsStatusAction(context);
        action.putReqData(null);
        action.addObserver(new ActionObserver() {
            @Override
            public void onActionResult(ActionSupport.ResponseResult result) {
                updatePluginsLocalState(context, (PluginConfig) result.processedResult);
                checkPluginsLocalState(context);
                onInit(context);
            }
        });
        action.actionStart();
    }

    private static void updatePluginsLocalState(Context context, PluginConfig config) {
        PluginLocalState local = YmnPreferences.loadPluginState(context);
        local.setInteracted(true);
        local.setPluginConfig(config);
        YmnPreferences.savePluginState(context, local);
    }

    private static void printPluginLocalState(String step, YmnPluginWrapper plugin) {
        Logger.dRich(step + ":" + plugin.toString());
    }

    public static boolean isSupportFunction(String functionName) {
        Logger.d(String.format("isSupportFunction %s", functionName));
        for (YmnPluginWrapper plugin : plugins.values()) {
            if (plugin.isSupportFunction(functionName)) {
                if (plugin.isWorking()) {
                    return true;
                } else {
                    burstWarning(plugin);
                    return false;
                }
            }
        }
        return false;
    }

    public static void callFunction(String functionName, LinkedHashMap<String, String> data) {
        Logger.d(String.format("callFunction %s", functionName));
        for (YmnPluginWrapper plugin : plugins.values()) {
            if (plugin.isWorking()) {
                plugin.callFunction(functionName, data);
            } else if (plugin.isSupportFunction(functionName)) {
                burstWarning(plugin);
            }
        }
    }

    public static String callFunctionWithResult(String functionName, LinkedHashMap<String, String> data) {
        Logger.d(String.format("callFunctionWithResult %s", functionName));
        for (YmnPluginWrapper plugin : plugins.values()) {
            if (plugin.isWorking()) {
                String result = plugin.callFunctionWithResult(functionName, data);
                if (result != null) return result;
            } else if (plugin.isSupportFunction(functionName)) {
                burstWarning(plugin);
            }
        }
        return null;
    }

    public static void callFunction(String functionName, String... args) {
        Logger.d(String.format("callFunction %s", functionName));
        for (YmnPluginWrapper plugin : plugins.values()) {
            if (plugin.isWorking()) {
                plugin.callFunction(functionName, args);
            } else if (plugin.isSupportFunction(functionName)) {
                burstWarning(plugin);
            }
        }
    }

    public static String callFunctionWithResult(String functionName, String... args) {
        Logger.d(String.format("callFunctionWithResult %s", functionName));
        for (YmnPluginWrapper plugin : plugins.values()) {
            if (plugin.isWorking()) {
                String result = plugin.callFunctionWithResult(functionName, args);
                if (result != null) return result;
            } else if (plugin.isSupportFunction(functionName)) {
                burstWarning(plugin);
            }
        }
        return null;
    }

    public static void burstWarning(YmnPluginWrapper plugin) {
        String message = String.format("%s插件未开启，请检查网络及远程配置", plugin.getPluginName());
        YmnPreferences.adaptStrategy(new YmnWarning(message)).burst();
    }

    public static void setDebugMode(boolean mode) {
        for (YmnPluginWrapper plugin : plugins.values()) {
            if (plugin.isWorking()) {
                plugin.setDebugMode(mode);
            }
        }
    }

    private static void onInit(Context context) {
        PluginLocalState localState = YmnPreferences.loadPluginState(context);
        for (YmnPluginWrapper plugin : plugins.values()) {
            if (plugin.isWorking() && plugin.matchEntrance(context) && plugin.canDoInit()) {
                PluginConfig.PluginInfo localInfo = localState.getInfo(plugin);

                if (localInfo != null) {
                    plugin.setParams(localInfo.getParams());
                    plugin.setCfgs(localInfo.getCfg_detail());
                }

                plugin.registCallback(callback);
                plugin.onInit(context);
                printPluginLocalState("AfterDoInit", plugin);
            }
        }
    }

    public static void onStart() {
        for (YmnPluginWrapper plugin : plugins.values()) {
            if (plugin.isWorking()) {
                plugin.onStart();
            }
        }
    }

    public static void onRestart() {
        for (YmnPluginWrapper plugin : plugins.values()) {
            if (plugin.isWorking()) {
                plugin.onRestart();
            }
        }
    }

    public static void onPause() {
        for (YmnPluginWrapper plugin : plugins.values()) {
            if (plugin.isWorking()) {
                plugin.onPause();
            }
        }
    }

    public static void onResume() {
        for (YmnPluginWrapper plugin : plugins.values()) {
            if (plugin.isWorking()) {
                plugin.onResume();
            }
        }
    }

    public static void onStop() {
        for (YmnPluginWrapper plugin : plugins.values()) {
            if (plugin.isWorking()) {
                plugin.onStop();
            }
        }
    }

    public static void onDestroy() {
        for (YmnPluginWrapper plugin : plugins.values()) {
            if (plugin.isWorking()) {
                plugin.onDestroy();
            }
        }
    }

    public static void onNewIntent(Intent intent) {
        for (YmnPluginWrapper plugin : plugins.values()) {
            if (plugin.isWorking()) {
                plugin.onNewIntent(intent);
            }
        }
    }

    public static void onActivityResult(int requestCode, int resultCode, Intent data) {
        for (YmnPluginWrapper plugin : plugins.values()) {
            if (plugin.isWorking()) {
                plugin.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    public static void onLogin(Map<String, String> data) {
        for (YmnPluginWrapper plugin : plugins.values()) {
            if (plugin.isWorking()) {
                plugin.onLogin(data);
            }
        }
    }

    public static void onPay(Map<String, String> data) {
        for (YmnPluginWrapper plugin : plugins.values()) {
            if (plugin.isWorking()) {
                plugin.onPay(data);
            }
        }
    }

}

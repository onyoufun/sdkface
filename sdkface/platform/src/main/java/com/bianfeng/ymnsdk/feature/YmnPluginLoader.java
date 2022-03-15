package com.bianfeng.ymnsdk.feature;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;

import com.bianfeng.ymnsdk.feature.plugin.YmnBaseInterface;
import com.bianfeng.ymnsdk.feature.protocol.YPlugin;
import com.bianfeng.ymnsdk.util.Logger;
import com.bianfeng.ymnsdk.util.ResourceUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dalvik.system.DexFile;

/**
 * Created by huchanghai on 2017/8/25.
 */
public class YmnPluginLoader {

    private static List<Class<?>> pluginClasses = new ArrayList<>();

    public static void init(Context context) {
        try {
            pluginClasses.add(YmnBaseInterface.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void registPluginClass(Class<?> cls) {
        pluginClasses.add(cls);
    }

    /**
     * 加载打到代码内的插件
     * @param context
     * @return
     */
    public static List<YmnPluginWrapper> loadHostPlugins(Context context) {
        return loadPlugins(pluginClasses);
    }

    private static List<YmnPluginWrapper> loadPlugins(List<Class<?>> pluginClasses) {
        long startTime = System.currentTimeMillis();

        List<YmnPluginWrapper> plugins = new ArrayList<>();
        for (Class<?> cls : pluginClasses) {
            YmnPluginWrapper plugin = loadPlugin(cls);
            if (plugin != null) plugins.add(plugin);
        }

        long endTime = System.currentTimeMillis();
        Logger.dRich("load plugins(fast model) cost millis " + (endTime - startTime));

        return plugins;
    }

    private static YmnPluginWrapper loadPlugin(Class<?> cls) {
        try {
            if (isYmnPlugin(cls)) {
                YmnPluginWrapper plugin = (YmnPluginWrapper) cls.newInstance();
                Logger.i(String.format("load plugin %s success", YmnPlugin.YPluginKey.get(plugin)));
                return plugin;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private static boolean isYmnPlugin(Class<?> cls) {
        if (!YmnPluginWrapper.class.isAssignableFrom(cls)) {
            return false;
        }
        return cls.getAnnotation(YPlugin.class) != null;
    }
}

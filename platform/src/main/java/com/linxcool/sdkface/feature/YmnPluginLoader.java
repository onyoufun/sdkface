package com.linxcool.sdkface.feature;

import android.content.Context;

import com.linxcool.sdkface.feature.plugin.YmnBaseInterface;
import com.linxcool.sdkface.feature.protocol.YPlugin;
import com.linxcool.sdkface.util.Logger;

import java.util.ArrayList;
import java.util.List;

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

    public static void registPlugin(String className) {
        try {
            registPlugin(Class.forName(className));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void registPlugin(Class<?> cls) {
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

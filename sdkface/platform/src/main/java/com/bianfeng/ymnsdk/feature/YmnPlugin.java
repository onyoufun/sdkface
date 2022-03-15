package com.bianfeng.ymnsdk.feature;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;

import com.bianfeng.ymnsdk.feature.protocol.IPlugin;
import com.bianfeng.ymnsdk.feature.protocol.YFunction;
import com.bianfeng.ymnsdk.feature.protocol.YPlugin;
import com.bianfeng.ymnsdk.util.Logger;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 插件基础功能封装
 * Created by huchanghai
 */
public abstract class YmnPlugin implements IPlugin {

    private Map<YFunctionKey, Method> functions = new HashMap<>();
    private Map<String, Integer> functionNames = new HashMap<>();
    private Map<String, String> functionAliasNames = new HashMap<>();

    /**
     * 获取插件加载策略
     *
     * @return
     */
    public YPlugin.Policy getPolicy() {
        YPlugin tagPlugin = getClass().getAnnotation(YPlugin.class);
        if (tagPlugin != null) {
            return tagPlugin.strategy();
        }
        return null;
    }

    /**
     * 获取插件入口（Application or Activity）
     *
     * @return
     */
    public YPlugin.Entrance getEntrance() {
        YPlugin tagPlugin = getClass().getAnnotation(YPlugin.class);
        if (tagPlugin != null) {
            return tagPlugin.entrance();
        }
        return null;
    }

    /**
     * 是否匹配Activity做初始化
     *
     * @param context 初始化用的上下文对象
     * @return
     */
    public boolean matchEntrance(Context context) {
        return matchAcitityEntrance(context) || matchContextEntrance(context) || matchApplicationEntrance(context);
    }

    public boolean matchAcitityEntrance(Context context) {
        if (context instanceof Activity) {
            return isAcitityEntrance();
        }
        return false;
    }

    public boolean isAcitityEntrance() {
        YPlugin.Entrance tag = getEntrance();
        return tag != null && tag == YPlugin.Entrance.ACTIVITY;
    }

    public boolean matchContextEntrance(Context context) {
        YPlugin.Entrance tag = getEntrance();
        return tag != null && tag == YPlugin.Entrance.CONTEXT;
    }

    public boolean matchApplicationEntrance(Context context) {
        YPlugin.Entrance tag = getEntrance();
        return tag != null && tag == YPlugin.Entrance.APPLICATION;
    }

    public YmnPlugin() {
        Method[] methods = getClass().getDeclaredMethods();
        for (Method method : methods) {
            method.setAccessible(true);
            YFunction tagFunction = method.getAnnotation(YFunction.class);
            if (tagFunction != null) {
                loadYmnTagFunction(method, tagFunction);
            }
        }
    }

    private void loadYmnTagFunction(Method method, YFunction tagFunction) {
        String functionName = tagFunction.name();
        String functionAliasName = tagFunction.alias();

        // 注意以下加载顺序不能变更否者将影响方法唯一性

        // 加载 functionAliasNames
        if (!TextUtils.isEmpty(functionAliasName)) {
            functionAliasNames.put(functionAliasName, functionName);
        }

        // 加载 functionNames
        String functionNameKey = formatFunctionName(functionName);
        functionNames.put(functionNameKey, getFunctionNameValue(functionName));

        // 加载 functions
        YFunctionKey functionKey = getFunctionKey(functionName, method.getParameterTypes());
        functions.put(functionKey, method);
    }

    private YFunctionKey getFunctionKey(String functionName, Class<?>... types) {
        return new YFunctionKey(formatFunctionName(functionName), types);
    }

    private int getFunctionNameValue(String functionName) {
        int count = 0;
        if (functionNames.containsKey(functionName)) {
            count = functionNames.get(functionName);
        }
        return ++count;
    }

    /**
     * 转移方法名成为真实存储方法名
     *
     * @param functionName
     * @return
     */
    public String formatFunctionName(String functionName) {
        String realName = functionName;
        if (functionAliasNames.containsKey(functionName)) {
            realName = functionAliasNames.get(functionName);
        }
        return functionNameWithPluginPrefix(realName);
    }

    private String functionNameWithPluginPrefix(String functionName) {
        if (!functionName.startsWith(getPluginName())) {
            functionName = getPluginName() + "_" + functionName;
        }
        return functionName;
    }

    @Override
    public boolean isSupportFunction(String functionName) {
        return functionNames.containsKey(formatFunctionName(functionName));
    }

    @Override
    public void callFunction(String functionName, LinkedHashMap<String, String> data) {
        if (isSupportFunction(functionName)) {
            callFunctionWithResult(functionName, data);
        } else {
            Logger.d(String.format("%s not found function %s", getPluginName(), functionName));
        }
    }

    @Override
    public String callFunctionWithResult(String functionName, LinkedHashMap<String, String> data) {
        try {
            Pair<Boolean, Object> result = invokeFunction(functionName, new Object[]{data}, LinkedHashMap.class);
            if (result.first && result.second != null) {
                return result.second.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void callFunction(String functionName, String... data) {
        if (isSupportFunction(functionName)) {
            callFunctionWithResult(functionName, data);
        } else {
            Logger.d(String.format("%s not found function %s", getPluginName(), functionName));
        }
    }

    @Override
    public String callFunctionWithResult(String functionName, String... data) {
        try {
            Pair<Boolean, Object> result = null;

            if (data == null || data.length == 0) {
                result = invokeFunction(functionName);
            } else {
                /*
                Class<?>[] types = new Class<?>[data.length];
                for (int i = 0; i < types.length; i++) {
                    types[i] = String.class;
                }
                */
                Pair<Class<?>[], String[]> types = fixGameFrameworkTypes(functionName, data);
                if(types == null) {
                    result = invokeFunction(functionName);
                } else {
                    result = invokeFunction(functionName, types.second, types.first);
                }
                if (!result.first) {
                    result = invokeFunction(functionName, new Object[]{data}, String[].class);
                }
            }

            if (result.first && result.second != null) {
                return result.second.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Pair<Boolean, Object> invokeFunction(String functionName) throws Exception {
        YFunctionKey key = getFunctionKey(functionName);
        if (functions.containsKey(key)) {
            Method method = functions.get(key);
            Object result = method.invoke(this);
            return new Pair<>(true, result);
        }
        return new Pair<>(false, null);
    }

    private Pair<Boolean, Object> invokeFunction(String functionName, Object[] data, Class<?>... types) throws Exception {
        YFunctionKey key = getFunctionKey(functionName, types);
        if (functions.containsKey(key)) {
            Method method = functions.get(key);
            Object result = method.invoke(this, data);
            return new Pair<>(true, result);
        }
        return new Pair<>(false, null);
    }

    /**
     * 兼容游戏框架内不区分重载方法的情况，策略是：自动递减参数个数，找到匹配项
     * @param functionName
     * @param data
     * @return
     */
    private Pair<Class<?>[], String[]> fixGameFrameworkTypes(String functionName, String... data) {
        int length = data.length;

        Class<?>[] types = new Class<?>[length];
        for (int i = 0; i < length; i++) {
            types[i] = String.class;
        }

        YFunctionKey key = getFunctionKey(functionName, types);
        if (functions.containsKey(key)) {
            Logger.i(String.format("%s(%s) found match types, args lenth is %d", getPluginName(), functionName, length));
            return new Pair<>(types, data);
        }

        Logger.e(String.format("%s(%s) can't find match types, reset args lenth(%d -> %d)", getPluginName(), functionName, length, length - 1));

        if (length == 1) {
            return null;
        } else {
            String[] args = new String[length - 1];
            for (int i = 0; i < length - 1; i++) {
                args[i] = data[i];
            }
            return fixGameFrameworkTypes(functionName, args);
        }
    }

    /**
     * 方法标识（区分重载）
     *
     * @author huchanghai
     */
    public static class YFunctionKey {

        public final String functionName;
        public final Class<?>[] types;

        public YFunctionKey(String functionName, Class<?>[] types) {
            this.functionName = functionName;
            this.types = types;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((functionName == null) ? 0 : functionName.hashCode());
            if (types == null) {
                return result;
            }
            for (Class<?> type : types) {
                result = prime * result + type.getName().hashCode();
            }
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            YFunctionKey other = (YFunctionKey) obj;
            if (functionName == null) {
                if (other.functionName != null) return false;
            } else if (!functionName.equals(other.functionName)) return false;
            if (!Arrays.equals(types, other.types)) return false;
            return true;
        }
    }

    public static class YPluginKey {
        public static String get(IPlugin plugin) {
            Class<?>[] interfaces = plugin.getClass().getInterfaces();
            if (interfaces == null || interfaces.length == 0) {
                return plugin.getPluginName();
            }
            final int prime = 31;
            int result = 1;
            for (Class<?> type : interfaces) {
                result = prime * result + type.getName().hashCode();
            }
            return String.format("%s_%d", plugin.getPluginId(), result);
        }
    }

}

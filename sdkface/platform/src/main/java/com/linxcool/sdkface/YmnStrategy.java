package com.linxcool.sdkface;

import com.linxcool.sdkface.util.Logger;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.LinkedHashMap;

/**
 * Created by huchanghai on 2017/9/6.
 */
public class YmnStrategy {

    public static final int STRATEGY_NONE = 0x0000;
    public static final int STRATEGY_INNER_PROGRESS = 0x0001;
    public static final int STRATEGY_INNER_TOAST_WARN = 0x0002;
    public static final int STRATEGY_WITH_SERVER = 0x0004;

    private static int flags = STRATEGY_INNER_PROGRESS | STRATEGY_INNER_TOAST_WARN;

    public static void setStrategys(int strategys) {
        YmnStrategy.flags = strategys;
    }

    public static void addStrategy(int strategy) {
        YmnStrategy.flags |= strategy;
        Logger.d("add addStrategy " + strategy);
    }

    public static boolean withStrategy(int strategy) {
        return (flags & strategy) == strategy;
    }

    /**
     * 方法参数关系映射
     *
     * @param args
     * @return
     */
    protected static LinkedHashMap<String, String> arrayParamersAsMap(String... args) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(args[0], new TypeToken<LinkedHashMap<String, String>>() {
            }.getType());
        } catch (Exception e) {
            e.printStackTrace();
            return new LinkedHashMap<>();
        }
    }

    protected static boolean isJsonParamers(String... args) {
        if (args == null || args.length != 1) {
            return false;
        }
        try {
            new JSONObject(args[0]);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}

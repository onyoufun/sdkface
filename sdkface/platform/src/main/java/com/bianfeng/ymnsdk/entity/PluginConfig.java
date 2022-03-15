package com.bianfeng.ymnsdk.entity;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PluginConfig {

    private List<PluginInfo> cfgs;

    public List<PluginInfo> getPluginInfos() {
        return cfgs;
    }

    public class PluginInfo {
        public String name;
        public int status;
        private Object params;
        private Object cfg_detail;

        public Map<String, String> getParams() {
            Gson gson = new Gson();
            if (params instanceof String) {
                return gson.fromJson((String) params, Map.class);
            }
            if (params instanceof JsonElement) {
                return gson.fromJson((JsonElement) params, Map.class);
            }
            return new HashMap<>();
        }
        public Map<String, String> getCfg_detail() {
            Gson gson = new Gson();
            if (params instanceof String) {
                return gson.fromJson((String) cfg_detail, Map.class);
            }
            if (params instanceof JsonElement) {
                return gson.fromJson((JsonElement) cfg_detail, Map.class);
            }
            return new HashMap<>();
        }
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

}

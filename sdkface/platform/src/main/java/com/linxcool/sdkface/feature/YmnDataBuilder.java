package com.linxcool.sdkface.feature;

import org.json.JSONObject;

/**
 * Created by huchanghai on 2017/9/7.
 */

public class YmnDataBuilder {

    public static class JsonData {

        JSONObject json = new JSONObject();
        YmnPluginWrapper plugin;

        public JsonData(YmnPluginWrapper plugin) {
            this.plugin = plugin;
        }

        public JsonData append(String key, Object value) {
            try {
                json.put(key, value);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return this;
        }

        public void sendResult(int code) {
            plugin.sendResult(code, json.toString());
        }

        public void sendResult(int code, Object ext) {
            plugin.sendResult(code, json, ext);
        }

        public void sendResultWithoutInterceptors(int code) {
            plugin.sendResultWithoutInterceptors(code, json.toString());
        }

        @Override
        public String toString() {
            return json.toString();
        }
    }

    public static JsonData createJson(YmnPluginWrapper plugin) {
        return new JsonData(plugin);
    }

}

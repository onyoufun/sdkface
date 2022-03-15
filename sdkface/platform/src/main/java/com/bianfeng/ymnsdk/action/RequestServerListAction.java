package com.bianfeng.ymnsdk.action;

import android.content.Context;

import com.bianfeng.ymnsdk.entity.UrlConfig;
import com.bianfeng.ymnsdk.feature.protocol.IPlugin;
import com.google.gson.Gson;

import org.json.JSONObject;

public class RequestServerListAction extends ActionSupport<UrlConfig> {

    public RequestServerListAction(Context context) {
        super(context);
    }

    @Override
    public JSONObject onPrepareData(IPlugin plugin, Object... datas) throws Exception {
        JSONObject json = new JSONObject();

        json.put("platform_id", plugin.getPluginId());
        json.put("platform_name", plugin.getPluginName());
        json.put("platform_ver", plugin.getPluginVersion());
        json.put("isDebug", String.valueOf(plugin.isDebugMode() ? 1 : 0));

        JSONObject data = new JSONObject();
        data.put("pid", String.valueOf(datas[0]));
        data.put("gid", String.valueOf(datas[1]));
        json.put("data", data);

        return json;
    }

    @Override
    protected String getURL() {
        return formatUrl("server/getList");
    }

    @Override
    protected UrlConfig onSuccess(ResponseResult result) throws Exception {
        Gson gson = new Gson();
        return gson.fromJson(result.dataAsString(), UrlConfig.class);
    }

}

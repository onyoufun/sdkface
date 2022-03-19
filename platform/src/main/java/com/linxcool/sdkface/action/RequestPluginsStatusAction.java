package com.linxcool.sdkface.action;

import android.content.Context;

import com.linxcool.sdkface.entity.PluginConfig;
import com.linxcool.sdkface.feature.protocol.IPlugin;
import com.linxcool.sdkface.util.Logger;
import com.google.gson.Gson;

import org.json.JSONObject;

public class RequestPluginsStatusAction extends ActionSupport<PluginConfig> {

    public RequestPluginsStatusAction(Context context) {
        super(context);
    }

    @Override
    public JSONObject onPrepareData(IPlugin plugin, Object... datas) throws Exception {
        return new JSONObject();
    }

    @Override
    protected String getURL() {
        return formatUrl("plugin/status");
    }

    @Override
    protected PluginConfig onSuccess(ResponseResult result) throws Exception {
        Logger.i(TAG, "request plugins status success");
        Gson gson = new Gson();
        return gson.fromJson(result.dataAsString(), PluginConfig.class);
    }
}

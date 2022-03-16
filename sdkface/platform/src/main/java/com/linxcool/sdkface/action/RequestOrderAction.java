package com.linxcool.sdkface.action;

import android.content.Context;

import com.linxcool.sdkface.feature.protocol.IPaymentFeature;
import com.linxcool.sdkface.feature.protocol.IPlugin;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.Map;

public class RequestOrderAction extends ActionSupport<Map<String, String>> {

    private Map<String, String> order;
    private Map<String, String> loginedData;

    public RequestOrderAction(Context context) {
        super(context);
    }

    @Override
    public JSONObject onPrepareData(IPlugin plugin, Object... datas) throws Exception {
        order = (Map<String, String>) datas[0];
        loginedData = (Map<String, String>) datas[1];

        JSONObject json = new JSONObject();

        json.put("platform_id", plugin.getPluginId());
        json.put("platform_name", plugin.getPluginName());
        json.put("platform_ver", plugin.getPluginVersion());
        json.put("isDebug", String.valueOf(plugin.isDebugMode() ? 1 : 0));

        Gson gson = new Gson();
        JSONObject data = new JSONObject(gson.toJson(order));
        json.put("data", data);

        // TODO fix ext arg
        if (loginedData != null) {
            JSONObject ext = new JSONObject(gson.toJson(loginedData));
            json.put("ext", ext);
        }

        return json;
    }

    @Override
    protected String getURL() {
        return formatUrl("pay");
    }

    @Override
    protected Map<String, String> onSuccess(ResponseResult result) throws Exception {
        order.put(IPaymentFeature.ARG_TRADE_CODE, result.data.getString(IPaymentFeature.ARG_TRADE_CODE));
        order.put(IPaymentFeature.ARG_CLIENT_CALLBACK, result.data.getString(IPaymentFeature.ARG_CLIENT_CALLBACK));
        order.put(IPaymentFeature.ARG_THIRDPARTY_CALLBACK, result.data.getString(IPaymentFeature.ARG_THIRDPARTY_CALLBACK));
        order.put(IPaymentFeature.ARG_PLATFORM_NOTIFY_URL, result.data.getString(IPaymentFeature.ARG_PLATFORM_NOTIFY_URL));
        return order;
    }

}

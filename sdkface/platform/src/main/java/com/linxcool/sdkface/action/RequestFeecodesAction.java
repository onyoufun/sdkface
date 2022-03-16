package com.linxcool.sdkface.action;

import android.content.Context;
import android.util.Log;

import com.linxcool.sdkface.AppConfig;
import com.linxcool.sdkface.feature.protocol.IPlugin;

import org.json.JSONObject;

/**
 * Created by huchanghai on 2018/1/17.
 */
public class RequestFeecodesAction extends ActionSupport<JSONObject> {

    public RequestFeecodesAction(Context context) {
        super(context);
        httpHelper.setMethod(HttpHelper.HTTP_METHOD_GET);
    }

    @Override
    protected String getURL() {
        return formatUrl("shop/feecode");
    }

    @Override
    public JSONObject onPrepareData(IPlugin plugin, Object... datas) throws Exception {
        gContent.put("app_id", AppConfig.getAppId());
        gContent.put("package_id", AppConfig.getConfigId());
        gContent.put("platform_id", plugin.getPluginId());
        return null;
    }

    @Override
    protected JSONObject onSuccess(ResponseResult result) throws Exception {
        Log.i(TAG, "request feedcodes success : " + result.dataAsString());
        return result.data;
    }
}

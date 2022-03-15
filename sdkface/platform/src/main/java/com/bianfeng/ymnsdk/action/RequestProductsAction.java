package com.bianfeng.ymnsdk.action;

import android.content.Context;
import android.util.Log;

import com.bianfeng.ymnsdk.AppConfig;
import com.bianfeng.ymnsdk.feature.protocol.IPlugin;

import org.json.JSONObject;

/**
 * 请求商品列表
 * Created by huchanghai on 2018/1/5.
 */
public class RequestProductsAction extends ActionSupport<JSONObject> {

    public RequestProductsAction(Context context) {
        super(context);
        httpHelper.setMethod(HttpHelper.HTTP_METHOD_GET);
    }

    @Override
    protected String getURL() {
        return formatUrl("shop/goods");
    }

    @Override
    public JSONObject onPrepareData(IPlugin plugin, Object... datas) throws Exception {
        gContent.put("app_id", AppConfig.getSdkAppId());
        gContent.put("package_id", AppConfig.getConfigId());
        return null;
    }

    @Override
    protected JSONObject onSuccess(ResponseResult result) throws Exception {
        Log.i(TAG, "request products success : " + result.dataAsString());
        return result.data;
    }
}

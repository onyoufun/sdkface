package com.linxcool.sdkface.action;

import android.content.Context;
import android.util.Log;

import com.linxcool.sdkface.util.SystemUtil;
import com.linxcool.sdkface.feature.YmnURLManager;
import com.linxcool.sdkface.feature.protocol.IPlugin;

import org.json.JSONObject;

import java.net.URLEncoder;

public class RequestIdentityUpdateAction extends ActionSupport<JSONObject> {

    public static final String IDENTITY_APP_ID = "1148";
    public static final String IDENTITY_APP_SECREAT = "c986f7b40c468a3a3b1087d0eb08628b";

    public RequestIdentityUpdateAction(Context context) {
        super(context);
        httpHelper.setMethod(HttpHelper.HTTP_METHOD_GET);
    }

    @Override
    public JSONObject onPrepareData(IPlugin plugin, Object... datas) throws Exception {
        gContent.put("type", (String) datas[0]);
        gContent.put("area_id", (String) datas[1]);
        gContent.put("numid", (String) datas[2]);
        gContent.put("name", URLEncoder.encode((String) datas[3], "UTF-8"));
        gContent.put("identity_number", (String) datas[4]);

        gContent.put("appid", IDENTITY_APP_ID);
        gContent.put("time", String.valueOf(System.currentTimeMillis() / 1000));
        gContent.put("sign", SystemUtil.getSign(gContent, IDENTITY_APP_SECREAT));
        return null;
    }

    @Override
    protected String getURL() {
        return String.format("https://%s/%s/%s", YmnURLManager.URL_HOST_PUBLIC_V2, HttpHelper.VERSION_SERVER, "player/setRealName");
    }

    @Override
    protected JSONObject onSuccess(ResponseResult result) throws Exception {
        Log.i(TAG, "request identity status resource success : " + result.dataAsString());
        return result.data;
    }

}

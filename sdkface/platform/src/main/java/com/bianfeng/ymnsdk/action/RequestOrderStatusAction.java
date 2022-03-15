package com.bianfeng.ymnsdk.action;

import android.content.Context;

import com.bianfeng.ymnsdk.util.RSASignature;
import com.bianfeng.ymnsdk.feature.protocol.IPlugin;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

public class RequestOrderStatusAction extends ActionSupport<Boolean> {

    public static final String ORDER_CHECK_STATUS = "status";
    public static final String ORDER_RSA_CHECK_RESULT = "rsa_check_result";

    public RequestOrderStatusAction(Context context) {
        super(context);
    }

    @Override
    public JSONObject onPrepareData(IPlugin plugin, Object... datas) throws Exception {
        JSONObject json = new JSONObject();
        json.put("order_id", datas[0]);
        json.put("order_type", datas[1]);
        return json;
    }

    @Override
    protected String getURL() {
        return formatUrl("pay/orderQuery");
    }

    @Override
    protected Boolean onSuccess(ResponseResult result) throws Exception {
        Iterator<String> iterator = result.data.keys();
        List<String> keys = new ArrayList<>();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (!"sign".equals(key)) {
                keys.add(key);
            }
        }
        TreeMap<String, String> mapping = new TreeMap<>();
        for (String key : keys) {
            mapping.put(key, URLEncoder.encode(result.data.optString(key), "utf-8"));
        }

        StringBuilder sb = new StringBuilder();
        for (Entry<String, String> entry : mapping.entrySet()) {
            sb.append("&" + entry.getKey() + "=" + entry.getValue());
        }

        String content = sb.substring(1);
        String sign = result.data.optString("sign");

        boolean isLegal = RSASignature.doCheck(content, sign);
        if (!isLegal) {
            result.msg = "非法响应，请注意订单安全性";
        }

        return isLegal;
    }

}

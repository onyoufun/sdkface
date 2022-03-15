package com.bianfeng.ymnsdk.action;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.bianfeng.ymnsdk.AppConfig;
import com.bianfeng.ymnsdk.feature.YmnURLManager;
import com.bianfeng.ymnsdk.feature.protocol.IPlugin;
import com.bianfeng.ymnsdk.util.DataFunAgent;
import com.bianfeng.ymnsdk.util.Logger;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.TreeMap;

/**
 * 基础动作类
 *
 * @author: linxcool.hu
 */
public abstract class ActionSupport<T> extends Observable implements HttpListener {

    protected static final String TAG = "ActionSupport";

    //上下文对象
    protected Context context;
    protected HttpHelper httpHelper;
    protected TreeMap<String, String> gContent;
    protected String pContent;
    protected ActionAttachment attachment;

    public void setAttachment(ActionAttachment attachment) {
        this.attachment = attachment;
    }

    public static class ResponseResult<T> {
        // 原始数据
        public String srcRes;
        public JSONObject srcObj;
        // 结构解析
        public int code = HttpHelper.CODE_UNSET;
        public String msg;
        public JSONObject data;
        public Object ext;
        // 结果对象
        public T processedResult;

        public ResponseResult(String response) {
            this.srcRes = response;
        }

        public ResponseResult(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public boolean isResponsed() {
            return !TextUtils.isEmpty(srcRes);
        }

        public boolean isOk() {
            return code == HttpHelper.CODE_RES_SUCCESS;
        }

        public Map<String, String> processedResultAsMap() {
            Gson gson = new Gson();
            return gson.fromJson(gson.toJson(processedResult), Map.class);
        }

        public String dataAsString() {
            return data.toString();
        }

        public String messageFail() {
            return code + "|" + msg;
        }

        public String getExtData(String key) {
            if (ext != null && ext instanceof JSONObject) {
                JSONObject json = (JSONObject) ext;
                return json.optString(key);
            }
            return "unknow";
        }
    }

    public ActionSupport(Context context) {
        this.context = context;
        httpHelper = new HttpHelper(context);
        httpHelper.setMethod(HttpHelper.HTTP_METHOD_POST);
        gContent = new TreeMap<>();
    }

    protected abstract String getURL();

    public void actionStart() {
        try {
            doRequest(getURL());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 重写以传入基本信息 未被任何方法调用 仅用于功能性设计
     *
     * @param json
     * @throws JSONException
     */
    protected void putBasicData(JSONObject json) throws JSONException {
        json.put("appid", AppConfig.getSdkAppId());
        json.put("channel", AppConfig.getChannelId());
        json.put("package_id", AppConfig.getConfigId());
    }

    public void putReqData(IPlugin plugin, Object... datas) {
        try {
            JSONObject json = onPrepareData(plugin, datas);
            if (json != null) {
                putBasicData(json);
                pContent = json.toString();
            }
            gContent.put("os", "android");
            gContent.put("mid", DataFunAgent.getDeviceId(context));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public abstract JSONObject onPrepareData(IPlugin plugin, Object... datas) throws Exception;

    public void doRequest(String url) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        for (Entry<String, String> entry : gContent.entrySet()) {
            sb.append("&" + entry.getKey() + "=" + entry.getValue());
        }

        if (sb.length() > 0) {
            url += "?" + sb.substring(1);
        }
        HttpURLConnection connection = null;

        if (pContent != null) {
            httpHelper.createHttpRequest(url, URLEncoder.encode(pContent, "utf-8"),this);
        }

        if (attachment != null) {
            attachment.onStart(context);
        }
    }

    @Override
    public void onComplete(String response) {
        ResponseResult result = new ResponseResult(response);
        processResponseResult(result);
    }

    @Override
    public void onError(int code, String msg) {
        ResponseResult result = new ResponseResult(code, msg);
        processResponseResult(result);
    }

    public void processResponseResult(final ResponseResult result) {
        parseResponseResult(result);
        if (context instanceof Activity) ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyResponseResult(result);
            }
        });
        else notifyResponseResult(result);
    }

    private void parseResponseResult(ResponseResult result) {
        try {
            showDebugLog(result);

            if (TextUtils.isEmpty(result.srcRes)) {
                Logger.e(TAG, String.format("do action response error code = %d msg = %s", result.code, result.msg));
            } else {
                JSONObject obj = new JSONObject(result.srcRes);
                result.srcObj = obj;
                result.code = obj.getInt("code");
                result.msg = obj.optString("msg");
                result.data = obj.optJSONObject("data");
                result.ext = obj.opt("ext");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.code = HttpHelper.CODE_SERVER_ERROR;
            result.msg = "parse response json error";
        }
    }

    private void showDebugLog(ResponseResult result) {
        StringBuilder builder = new StringBuilder();
        builder.append("[CONTEXT] " + context);
        builder.append("\n");
        builder.append("[REQUEST] " + getURL());
        builder.append("\n");
        builder.append("[CONTENT] " + pContent);
        builder.append("\n");
        builder.append("[RESPONSE] " + result.srcRes);
        Logger.dRich(builder.toString());
    }

    private void notifyResponseResult(ResponseResult result) {
        this.setChanged();
        if (result.isResponsed()) {
            try {
                if (result.isOk()) {
                    result.processedResult = onSuccess(result);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            YmnURLManager.notifyRequestSuccess(context);
        } else {
            YmnURLManager.notifyRequestFailure(context);
        }

        if (attachment != null) {
            attachment.onEnd(context);
        }

        notifyObservers(result);
    }

    protected abstract T onSuccess(ResponseResult result) throws Exception;

    /**
     * 返回请求地址
     *
     * @param action
     * @return
     */
    protected String formatUrl(String action) {
        String urlHost = YmnURLManager.getHost(context);
        return String.format("%s/%s/%s", urlHost, HttpHelper.VERSION_SERVER, action);
    }

    protected Object formatType(Object obj) {
        if (obj instanceof String) {
            try {
                return new JSONObject(String.valueOf(obj));
            } catch (Exception e) {
                return obj;
            }
        }
        return obj;
    }
}

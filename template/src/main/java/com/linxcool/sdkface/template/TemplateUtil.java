package com.linxcool.sdkface.template;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

import com.linxcool.sdkface.AppConfig;
import com.linxcool.sdkface.action.HttpHelper;
import com.linxcool.sdkface.action.HttpListener;
import com.linxcool.sdkface.feature.plugin.YmnChannelInterface;
import com.linxcool.sdkface.util.Logger;

import org.json.JSONObject;

/**
 * Created by huchanghai on 2017/11/7.
 */

public class TemplateUtil {

    private static final String PAY_TEMPLATE_NOTIFY_URL = "http://mobile.bfun.cn/index.php/v1/notify/templatePayNotify";

    public static void notifyPayResult(YmnChannelInterface channelInterface, String orderId) {
        HttpHelper httpHelper = new HttpHelper(channelInterface.getContext());
        httpHelper.setMethod(HttpHelper.HTTP_METHOD_POST);
        httpHelper.createHttpRequest(PAY_TEMPLATE_NOTIFY_URL,getRequestData(channelInterface, orderId),new HttpListener() {
            @Override
            public void onError(int code, String msg) {
                Logger.d("template pay notify error: " + code + "|" + msg);
            }

            @Override
            public void onComplete(String response) {
                Logger.d("template pay notify response: " + response);
            }
        });
//        httpHelper.request(post, );
    }

    private static String getRequestData(YmnChannelInterface channelInterface, String orderId) {
        try {
            JSONObject json = new JSONObject();
            json.put("appid", AppConfig.getAppId());
            json.put("channel", AppConfig.getChannelId());
            json.put("platform_id", channelInterface.getPluginId());
            json.put("platform_name", channelInterface.getPluginName());
            if(channelInterface.getLoginedData() != null) {
                json.put("pid", channelInterface.getLoginedData().get("ymnUserIdInt"));
            } else {
                Logger.e("未检测到登录信息，请确认是否已调用SDK登录功能并登录成功！");
            }
            json.put("order_id", orderId);
            Logger.d("template pay notify data: " + json);
            return json.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static boolean isActivityTop(Activity activity){
        ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        String name = manager.getRunningTasks(1).get(0).topActivity.getClassName();
        return name.equals(activity.getClass().getName());
    }

}

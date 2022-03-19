package com.linxcool.sdkface.action;

import android.content.Context;

import com.linxcool.sdkface.feature.protocol.IPlugin;
import com.linxcool.sdkface.feature.protocol.IUserFeature;

import org.json.JSONObject;

public class RequestLoginAction extends ActionSupport<IUserFeature.UserInfo> {

    private String LOGIN_SUC_RS_UID = "uid";
    private String LOGIN_SUC_RS_PID = "pid";
    private String LOGIN_SUC_RS_GID = "gid";
    /**
     * SDK内部通信时发送给服务端的是uname，但响应回的是username
     */
    private String LOGIN_SUC_RS_USERNAME = "username";
    private String LOGIN_SUC_RS_SESSION = "session";
    private IPlugin plugin;

    public RequestLoginAction(Context context) {
        super(context);
    }

    @Override
    public JSONObject onPrepareData(IPlugin plugin, Object... datas) throws Exception {
        this.plugin = plugin;

        JSONObject json = new JSONObject();

        json.put("platform_id", plugin.getPluginId());
        json.put("platform_name", plugin.getPluginName());
        json.put("platform_ver", plugin.getPluginVersion());
        json.put("isDebug", String.valueOf(plugin.isDebugMode() ? 1 : 0));

        json.put("data", formatType(datas[0]));
        json.put("ext", formatType(datas[1]));

        return json;
    }

    @Override
    protected String getURL() {
        return formatUrl("login");
    }

    @Override
    protected IUserFeature.UserInfo onSuccess(ResponseResult result) throws Exception {
        result.data.put("platform_id", plugin.getPluginId());
        result.data.put("platform_name", plugin.getPluginName());
        result.data.put("thirdparty", plugin.getPluginName());

        IUserFeature.UserInfo userInfo = new IUserFeature.UserInfo();

        userInfo.setYmnLogined(true);
        userInfo.setYmnUserIdInt(result.data.optString(LOGIN_SUC_RS_PID));
        userInfo.setYmnUserId(result.data.optString(LOGIN_SUC_RS_UID));
        userInfo.setPlatformUserId(result.data.optString(LOGIN_SUC_RS_GID));
        userInfo.setYmnSession(result.data.optString(LOGIN_SUC_RS_SESSION));
        userInfo.setYmnUserName(result.data.optString(LOGIN_SUC_RS_USERNAME));

        userInfo.setResponseExt(result.ext.toString());

        return userInfo;
    }


}

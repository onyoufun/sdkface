package com.linxcool.sdkface.feature.plugin;

import android.content.Context;

import com.linxcool.sdkface.YmnCode;
import com.linxcool.sdkface.feature.YmnPluginWrapper;
import com.linxcool.sdkface.feature.protocol.IUserFeature;
import com.google.gson.Gson;

import java.util.LinkedHashMap;

/**
 * 该类仅作为渠道类插件扩展，简化操作
 * Created by huchanghai on 2017/9/7.
 */
public abstract class YmnUserInterface extends YmnPluginWrapper implements IUserFeature, YmnCode {

    @Override
    public void onInit(Context context) {
        super.onInit(context);
        setIniting(true);
    }

    @Override
    public void sendResult(int code, String msg) {
        setInitFlagsByReturnCode(code);
        super.sendResult(code, msg);
    }

    @Override
    public void sendResultWithoutInterceptors(int code, String msg) {
        setInitFlagsByReturnCode(code);
        super.sendResultWithoutInterceptors(code, msg);
    }

    /**
     * 改方法仅针对UserFeature的结果做处理，特殊情况请分开处理
     *
     * @param code
     */
    protected void setInitFlagsByReturnCode(int code) {
        if (code == ACTION_RET_INIT_SUCCESS) {
            setIniting(false);
            setInited(true);
        }
        if (code == ACTION_RET_INIT_FAIL) {
            setIniting(false);
            setInited(false);
        }
    }

    @Override
    public boolean isLogined() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void logout() {
        // TODO Auto-generated method stub
    }

    @Override
    public void showToolBar() {
        // TODO Auto-generated method stub
    }

    @Override
    public void hideToolBar() {
        // TODO Auto-generated method stub
    }

    @Override
    public void switchAccount() {
        // TODO Auto-generated method stub
    }

    @Override
    public void exit() {
        // TODO Auto-generated method stub
    }

    @Override
    public void submitUserInfo(LinkedHashMap<String, String> data) {
        // TODO Auto-generated method stub
    }

    @Override
    public UserInfo getUserInfo() {
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(getLoginedData()), UserInfo.class);
    }

    @Override
    public void enterPlatform() {
        // TODO Auto-generated method stub
    }

}

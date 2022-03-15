package com.bianfeng.ymnsdk.feature.plugin;

import android.content.Context;

import com.bianfeng.ymnsdk.AppConfig;
import com.bianfeng.ymnsdk.action.ActionObserver;
import com.bianfeng.ymnsdk.action.ActionSupport;
import com.bianfeng.ymnsdk.action.RequestIdentityStatusAction;
import com.bianfeng.ymnsdk.action.RequestIdentityUpdateAction;
import com.bianfeng.ymnsdk.action.RequestProductsAction;
import com.bianfeng.ymnsdk.feature.YmnPluginWrapper;
import com.bianfeng.ymnsdk.feature.protocol.YFunction;
import com.bianfeng.ymnsdk.feature.protocol.YPlugin;

/**
 * 有猫腻基础插件
 * Created by huchanghai on 2017/8/29.
 */
@YPlugin(strategy = YPlugin.Policy.FORCE, entrance = YPlugin.Entrance.ACTIVITY)
public class YmnBaseInterface extends YmnPluginWrapper {

    @Override
    public String getPluginId() {
        return "0";
    }

    @Override
    public String getPluginName() {
        return "ymnbase";
    }

    @Override
    public int getPluginVersion() {
        return 1;
    }

    @Override
    public String getSdkVersion() {
        return "1.0.0";
    }

    @Override
    public void onInit(Context context) {
        super.onInit(context);
        setInited(true);
    }

    @YFunction(name = "get_channel_id")
    public String getChannelId() {
        return AppConfig.getChannelId();
    }

    @YFunction(name = "get_metadata_value")
    public String getMetaDataValue(String key) {
        return AppConfig.getMetaDataValue(key);
    }
}

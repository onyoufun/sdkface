package com.linxcool.sdkface.feature.plugin;

import android.content.Context;

import com.linxcool.sdkface.AppConfig;
import com.linxcool.sdkface.feature.YmnPluginWrapper;
import com.linxcool.sdkface.feature.protocol.YFunction;
import com.linxcool.sdkface.feature.protocol.YPlugin;

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
        return "sdkbase";
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

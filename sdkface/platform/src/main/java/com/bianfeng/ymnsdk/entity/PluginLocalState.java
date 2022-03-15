package com.bianfeng.ymnsdk.entity;

import com.bianfeng.ymnsdk.feature.protocol.IPlugin;

import java.util.List;

/**
 * Created by huchanghai on 2017/8/28.
 */
public class PluginLocalState extends LocalState {

    private PluginConfig pluginConfig;

    public PluginConfig getPluginConfig() {
        return pluginConfig;
    }

    public void setPluginConfig(PluginConfig pluginConfig) {
        this.pluginConfig = pluginConfig;
    }

    public PluginLocalState() {
        // Empty
    }

    public PluginLocalState(PluginConfig pluginConfig) {
        this.pluginConfig = pluginConfig;
    }

    public int getState(IPlugin plugin) {
        PluginConfig.PluginInfo info = getInfo(plugin);
        if (info == null) return IPlugin.STATE_PENDING_CHECK;
        return info.status;
    }

    public PluginConfig.PluginInfo getInfo(IPlugin plugin) {
        if (pluginConfig == null) return null;
        List<PluginConfig.PluginInfo> infos = pluginConfig.getPluginInfos();
        if (infos == null) return null;
        for (PluginConfig.PluginInfo info : infos) {
            if (plugin.getClass().getName().equals(info.name)) {
                return info;
            }
            if (plugin.getPluginName().equals(info.name)) {
                return info;
            }
        }
        return null;
    }

}

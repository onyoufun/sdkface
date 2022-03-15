package com.bianfeng.ymnsdk.feature;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.bianfeng.ymnsdk.YmnStrategy;
import com.bianfeng.ymnsdk.action.ActionAttachment;
import com.bianfeng.ymnsdk.action.ActionSupport;
import com.bianfeng.ymnsdk.entity.PluginLocalState;
import com.bianfeng.ymnsdk.entity.UrlConfig;
import com.bianfeng.ymnsdk.entity.UrlLocalState;
import com.bianfeng.ymnsdk.util.Logger;
import com.bianfeng.ymnsdk.util.ResourceUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by huchanghai on 2017/8/25.
 */
public class YmnPreferences {

    public static final String PLUGIN_LOCAL_STATES = "ymn_plugin_local_states";
    public static final String URL_LOCAL_STATES = "ymn_url_local_states";
    public static final String URL_REMOTE_CONFIGS = "ymn_url_remote_configs";

    private static Gson gson = new Gson();
    private static Context context;

    public static void init(Context context) {
        YmnPreferences.context = context;
        Logger.updateState();
    }

    public static PluginLocalState loadPluginState(Context context) {
        try {
            return loadLocalState(context, PLUGIN_LOCAL_STATES, PluginLocalState.class);
        } catch (Exception e) {
            e.printStackTrace();
            return new PluginLocalState();
        }
    }

    public static void savePluginState(Context context, PluginLocalState state) {
        saveLoacalState(context, PLUGIN_LOCAL_STATES, state);
    }

    public static Map<String, UrlConfig> loadUrlRemoteState(Context context) {
        Map<String, UrlConfig> remoteConfigs = new HashMap<>();
        String configs = ResourceUtil.readPreferences(context, URL_REMOTE_CONFIGS);
        if (!TextUtils.isEmpty(configs)) {
            remoteConfigs = gson.fromJson(configs, new TypeToken<Map<String, UrlConfig>>() {
            }.getType());
        }
        return remoteConfigs;
    }

    public static void saveUrlRemoteConfig(Context context, UrlConfig config) {
        try {
            Map<String, UrlConfig> remoteConfigs = loadUrlRemoteState(context);
            remoteConfigs.put(config.getGid(), config);
            saveLoacalState(context, URL_REMOTE_CONFIGS, remoteConfigs);

            UrlLocalState localState = loadUrlLocalState(context);
            UrlConfig maxLevelConfig = getMaxLevelUrlConfig(context);
            localState.updateConfig(maxLevelConfig);
            saveUrlLoacalState(context, localState);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static UrlLocalState loadUrlLocalState(Context context) {
        UrlLocalState localState = null;
        String config = ResourceUtil.readPreferences(context, URL_LOCAL_STATES);
        if (TextUtils.isEmpty(config)) {
            UrlConfig urlConfig = getMaxLevelUrlConfig(context);
            if (urlConfig != null) {
                localState = new UrlLocalState(urlConfig);
                saveUrlLoacalState(context, localState);
            }
        } else {
            localState = gson.fromJson(config, UrlLocalState.class);
        }
        return localState;
    }

    private static UrlConfig getMaxLevelUrlConfig(Context context) {
        Map<String, UrlConfig> remoteConfigs = loadUrlRemoteState(context);
        Iterator<UrlConfig> iterator = remoteConfigs.values().iterator();
        UrlConfig maxLevel = null;
        while (iterator.hasNext()) {
            UrlConfig item = iterator.next();
            if (maxLevel == null || item.getLevel() > maxLevel.getLevel()) {
                maxLevel = item;
            }
        }
        return maxLevel;
    }

    public static void saveUrlLoacalState(Context context, UrlLocalState localState) {
        saveLoacalState(context, URL_LOCAL_STATES, localState);
    }

    public static void clearAllUrlConfigs(Context context) {
        ResourceUtil.removePreferences(context, URL_LOCAL_STATES);
        ResourceUtil.removePreferences(context, URL_REMOTE_CONFIGS);
    }

    public static <T> T loadLocalState(Context context, String cfgKey, Class<T> cfgClass) throws Exception {
        String config = ResourceUtil.readPreferences(context, cfgKey);
        if (!TextUtils.isEmpty(config)) {
            return gson.fromJson(config, cfgClass);
        }
        return cfgClass.newInstance();
    }

    public static void saveLoacalState(Context context, String cfgKey, Object localState) {
        if (localState != null) {
            ResourceUtil.savePreferences(context, cfgKey, gson.toJson(localState));
        }
    }

    public static <T extends ActionSupport> T adaptStrategy(T actionSupport) {
        if (YmnStrategy.withStrategy(YmnStrategy.STRATEGY_INNER_PROGRESS)) {
            actionSupport.setAttachment(new ActionAttachment.ProgressAttachment());
        }
        return actionSupport;
    }

    public static YmnWarning adaptStrategy(final YmnWarning warning) {
        if (YmnStrategy.withStrategy(YmnStrategy.STRATEGY_INNER_TOAST_WARN)) {
            warning.setAttachment(new YmnWarning.WarningAttachment() {
                @Override
                public void onBurst(String message) {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                }
            });
        }
        return warning;
    }

}

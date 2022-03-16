package com.linxcool.sdkface;

import com.linxcool.sdkface.feature.YmnPluginLoader;

/**
 * Created by huchanghai on 2017/8/31.
 */
public class YmnSdk extends YmnSdkUserWrapper implements YmnUserCode, YmnPaymentCode {

    private YmnSdk() {
        // Empty
    }

    /**
     * 注册要加载的插件，需要最早调用
     * @param cls
     */
    public static void registPlugin(Class<?> cls) {
        YmnPluginLoader.registPluginClass(cls);
    }

}

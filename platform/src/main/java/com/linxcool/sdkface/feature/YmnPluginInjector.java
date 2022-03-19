package com.linxcool.sdkface.feature;

import android.app.Activity;
import android.content.Context;

import com.linxcool.sdkface.YmnSdkPaymentWrapper;
import com.linxcool.sdkface.YmnSdkUserWrapper;
import com.linxcool.sdkface.feature.plugin.PaymentFeatureWrapper;
import com.linxcool.sdkface.feature.plugin.UserFeatureWrapper;
import com.linxcool.sdkface.feature.protocol.IPaymentFeature;
import com.linxcool.sdkface.feature.protocol.IUserFeature;
import com.linxcool.sdkface.util.Logger;

import java.util.HashMap;
import java.util.Set;

/**
 * 捕获插件的回调结果，进行新的处理，目前执行的是服务端中转处理
 * Created by huchanghai on 2017/8/31.
 */
public class YmnPluginInjector {

    public static void inject(Context context, HashMap<String, YmnPluginWrapper> plugins) {
        Logger.d("inject pugins on " + context);
        for (YmnPluginWrapper plugin : plugins.values()) {
            plugin.updateContext(context);
            if (plugin.isInjected()) {
                // TODO now it's empty
            } else if (context instanceof Activity) {
                injectWhenActivity((Activity) context, plugin);
            }
        }
        removeWrappersInCase();
    }

    /**
     * 捕获设置IUserFeature和IPaymentFeature的插件，将其回调进行内部中转，经服务端处理后再回调给发起方
     * @param activity
     * @param plugin
     */
    private static void injectWhenActivity(Activity activity, YmnPluginWrapper plugin) {
        if (plugin instanceof IUserFeature) {
            Logger.d("registUserFeatureWrapper " + plugin);
            UserFeatureWrapper wrapper = new UserFeatureWrapper((IUserFeature) plugin);
            YmnSdkUserWrapper.registUserFeatureWrapper(wrapper);
            plugin.setInjected(true);
        }
        if (plugin instanceof IPaymentFeature) {
            Logger.d("registPaymentFeatureWrapper " + plugin);
            PaymentFeatureWrapper wrapper = new PaymentFeatureWrapper((IPaymentFeature) plugin);
            YmnSdkPaymentWrapper.registPaymentFeatureWrapper(wrapper);
            plugin.setInjected(true);
        }
    }

    private static void removeWrappersInCase() {
        Set<UserFeatureWrapper> userSet = YmnSdkUserWrapper.getUserWrappers();
        if (userSet != null && userSet.size() > 1) {
            for (UserFeatureWrapper item : userSet) {
                if (isTemplate(item) || isDisableExecutor(item.getPluginWrapper())) {
                    userSet.remove(item);
                    break;
                }
            }
        }
        Set<PaymentFeatureWrapper> paymentSet = YmnSdkPaymentWrapper.getPaymentWrappers();
        if (paymentSet != null && paymentSet.size() > 1) {
            for (PaymentFeatureWrapper item : paymentSet) {
                if (isTemplate(item) || isDisableExecutor(item.getPluginWrapper())) {
                    paymentSet.remove(item);
                    break;
                }
            }
        }
    }

    private static boolean isDisableExecutor(YmnPluginWrapper wrapper) {

        return false;
    }

    private static boolean isTemplate(UserFeatureWrapper wrapper) {
        try {
            return "template".equals(wrapper.getPluginWrapper().getPluginName());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean isTemplate(PaymentFeatureWrapper wrapper) {
        try {
            return "template".equals(wrapper.getPluginWrapper().getPluginName());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

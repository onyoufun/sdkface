package com.linxcool.sdkface.feature;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.linxcool.sdkface.AppConfig;
import com.linxcool.sdkface.YmnCallback;
import com.linxcool.sdkface.entity.PluginLocalState;
import com.linxcool.sdkface.util.Logger;
import com.linxcool.sdkface.util.SystemUtil;
import com.linxcool.sdkface.feature.protocol.IPlugin;
import com.linxcool.sdkface.feature.protocol.YPlugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 插件扩展类，所有插件都要求继承该类
 * 如果是满足实现 IUserFeature 或 IPaymentFeature 的插件：
 *  . 使用sendResultWithoutInterceptors则不请求服务器
 *  . 使用sendResult则请求服务器
 * @author huchanghai
 */
public abstract class YmnPluginWrapper extends YmnPlugin {

    private Map<String, String> params;
    private Map<String, String> cfgs;

    private Map<String, String> loginedData;

    private int state = IPlugin.STATE_EMPTY;
    // 插件完成初始化
    private boolean inited;
    // 插件正在初始化
    private boolean initing;

    // 完成注入，注意只有经过有猫腻服务端的插件需要注入，并需要在核心项目中补充判断（当然也可以在插件中自行实现） YmnPluginInjector
    private boolean injected;

    // 完成插件加载触发条件，之后状态变更为服务中(STATE_WORKING)
    private boolean triggered;

    private Context context;
    private YmnCallback callback;
    private List<YmnCallbackInterceptor> interceptors;

    private boolean debugMode;

    public final void setCfgs(Map<String, String> cfgs) {
        this.cfgs = cfgs;
    }

    public final Map<String, String> getCfgs() {
        return cfgs;
    }
    public final void setParams(Map<String, String> params) {
        this.params = params;
    }

    public final Map<String, String> getParams() {
        return params;
    }
    public Map<String, String> getLoginedData() {
        return loginedData;
    }

    public void registCallback(YmnCallback callback) {
        this.callback = callback;
    }

    public void addCallbackInterceptor(YmnCallbackInterceptor interceptor) {
        if (interceptors == null) {
            interceptors = new ArrayList<>();
        }
        interceptors.add(interceptor);
    }

    public void checkState(PluginLocalState local) {
        switch (getPolicy()) {
            case LAZY:
                state = local.getState(this);
                break;
            case REMOTE:
                state = IPlugin.STATE_PENDING_CHECK;
                break;
            case FORCE:
                state = IPlugin.STATE_WORKING;
                break;
            case TRIGGER:
                if (triggered) state = IPlugin.STATE_WORKING;
                else state = IPlugin.STATE_CLOSED;
                break;
            default:
                break;
        }
    }

    public int getState() {
        return state;
    }

    public boolean isCheckedState() {
        return state != IPlugin.STATE_EMPTY && state != IPlugin.STATE_PENDING_CHECK;
    }

    public boolean isWorking() {
        return state == IPlugin.STATE_WORKING;
    }

    public boolean isInited() {
        return inited;
    }

    public void setInited(boolean inited) {
        this.inited = inited;
    }

    public boolean isIniting() {
        return initing;
    }

    public void setIniting(boolean initing) {
        this.initing = initing;
    }

    public boolean isInjected() {
        return injected;
    }

    public void setInjected(boolean injected) {
        this.injected = injected;
    }

    /**
     * 设置完成触发条件与否，设置为true则通过调用 {@link #checkState(PluginLocalState)} 方法插件状态变更为服务中
     *
     * @param triggered
     */
    public void setTriggered(boolean triggered) {
        this.triggered = triggered;
    }

    public <T extends Context> T getContext() {
        return (T) context;
    }

    public void updateContext(Context context) {
        this.context = context;
        onContextChanged(context);
    }

    public void tryRunOnUiThreadOrJustRun(Runnable runnable) {
        if (isAcitityEntrance()) ((Activity) context).runOnUiThread(runnable);
        else runnable.run();
    }

    /**
     * 返回SDK参数配置（ymn.cfg/usd.cfg）
     *
     * @param key
     * @return
     */
    public String getPropertie(String key) {
        return YmnProperties.getPluginValue(this, key);
    }

    /**
     * 返回项目MetaData参数
     *
     * @param key
     * @return
     */
    public String getMetaData(String key) {
        return AppConfig.getMetaDataValue(key);
    }

    /**
     * 发送方法处理结果，由注册的YmnCallback接收
     *
     * @param code
     * @param msg
     */
    public void sendResult(int code, String msg) {
        if (interceptors != null && !interceptors.isEmpty()) {
            Iterator<YmnCallbackInterceptor> it = interceptors.iterator();
            YmnCallbackInterceptor first = it.next();
            YmnCallbackInterceptor current = first;
            while (it.hasNext()) {
                YmnCallbackInterceptor next = it.next();
                current.setNext(next);
                current = next;
            }
            if (callback != null) {
                current.setNext(callback);
            }
            first.onCallBack(code, msg);
        } else if (callback != null) {
            callback.onCallBack(code, msg);
        }
    }

    public void sendResult(int code, Object data, Object ext) {
        YmnCallback.RichCallbackMessage message = new YmnCallback.RichCallbackMessage(data, ext);
        sendResult(code, message.toString());
    }

    /**
     * 如果登录支付不接入服务端则调用该方法
     * @param code
     * @param msg
     */
    public void sendResultWithoutInterceptors(int code, String msg) {
        if (callback != null) {
            callback.onCallBack(code, msg);
        }
    }

    @Override
    public String toString() {
        return String.format("%s {%s, inited = %b, initing = %b, class = %s}", getPluginName(), IPlugin.STATE_NAME.get(state), inited, initing, getClass().getName());
    }

    @Override
    public void setDebugMode(boolean mode) {
        this.debugMode = mode;
    }

    @Override
    public boolean isDebugMode() {
        return debugMode;
    }

    @Override
    public void onInit(Context context) {
        this.context = context;
    }

    public boolean canDoInit() {
        if (initing) {
            Logger.w(getPluginName() + " on initing, ignore invoke " + context);
            return false;
        }
        if (inited) {
            Logger.w(getPluginName() + " already inited, ignore invoke " + context);
            return false;
        }
        return true;
    }


    /**
     * 上下文环境变更时触发，一般情况为Application启动时触发一次，Activity启动时触发一次，
     * 该方法在onInit方法之前执行
     *
     * @param context
     */
    public void onContextChanged(Context context) {
        // TODO support for sub class
    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onRestart() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStop() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
    }

    @Override
    public void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onLogin(Map<String, String> data) {
        this.loginedData = data;
    }

    @Override
    public void onPay(Map<String, String> data) {
        // TODO Auto-generated method stub
    }

    /**
     * 是否是横向屏幕
     *
     * @return
     */
    public boolean isScreenLandscape() {
        if (getContext() instanceof Activity) {
            return SystemUtil.isScreenLandscape((Activity) getContext());
        }
        return true;
    }

    /**
     * 返回当前依赖的Activity对象，注意要求插件配置 entrance = YPlugin.Entrance.ACTIVITY
     * @return
     */
    public Activity getActivity() {
        return getContext();
    }

    /**
     * 返回服务主机域名
     * @return
     */
    public String getServerHost() {
        return YmnURLManager.getHost(getContext());
    }
}

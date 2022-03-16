package com.linxcool.sdkface.feature.plugin;

import com.linxcool.sdkface.YmnStrategy;
import com.linxcool.sdkface.YmnUserCode;
import com.linxcool.sdkface.action.ActionObserver;
import com.linxcool.sdkface.action.ActionSupport;
import com.linxcool.sdkface.action.HttpHelper;
import com.linxcool.sdkface.action.RequestLoginAction;
import com.linxcool.sdkface.action.RequestServerListAction;
import com.linxcool.sdkface.entity.UrlConfig;
import com.linxcool.sdkface.feature.YmnCallbackInterceptor;
import com.linxcool.sdkface.feature.YmnPluginManager;
import com.linxcool.sdkface.feature.YmnPluginWrapper;
import com.linxcool.sdkface.feature.YmnPreferences;
import com.linxcool.sdkface.feature.YmnURLManager;
import com.linxcool.sdkface.feature.protocol.IUserFeature;
import com.linxcool.sdkface.util.AnalyticsData;
import com.linxcool.sdkface.util.Logger;
import com.linxcool.sdkface.YmnCallback;

import org.json.JSONObject;

import java.util.LinkedHashMap;

/**
 * 此处不注册为插件，仅做登录插件调用工具
 * Created by huchanghai on 2017/8/29.
 */
public class UserFeatureWrapper extends ActionObserver implements IUserFeature, YmnUserCode {

    IUserFeature userFeature;
    YmnPluginWrapper pluginWrapper;
    UserInfo userInfo;

    public YmnPluginWrapper getPluginWrapper() {
        return pluginWrapper;
    }

    public UserFeatureWrapper(IUserFeature userFeature) {
        this.userFeature = userFeature;
        this.pluginWrapper = ((YmnPluginWrapper) userFeature);
        pluginWrapper.addCallbackInterceptor(interceptor);
    }

    @Override
    public void login() {
        AnalyticsData.loginThirdEvent(pluginWrapper);
        pluginWrapper.tryRunOnUiThreadOrJustRun(new Runnable() {
            @Override
            public void run() {
                userFeature.login();
            }
        });
    }

    /**
     * 作用是拦截三方返回结果，继而先去请求我方服务器，等请求返回后转发给外部调用
     */
    YmnCallbackInterceptor interceptor = new YmnCallbackInterceptor() {
        @Override
        public void onCallBack(int code, String msg) {
            AnalyticsData.loginThirdResEvent(pluginWrapper, code, msg);
            switch (code) {
                case ACTION_RET_LOGIN_SUCCESS:
                    Object data = msg;
                    Object ext = null;

                    if (YmnCallback.RichCallbackMessage.isRich(msg)) {
                        YmnCallback.RichCallbackMessage messageObj = YmnCallback.RichCallbackMessage.instance(msg);
                        data = messageObj.getData();
                        ext = messageObj.getExt();
                    }

                    if(YmnStrategy.withStrategy(YmnStrategy.STRATEGY_WITH_SERVER)) {
                        RequestLoginAction action = YmnPreferences.adaptStrategy(new RequestLoginAction(pluginWrapper.getContext()));
                        action.putReqData(pluginWrapper, data, ext);
                        action.addObserver(UserFeatureWrapper.this);
                        action.actionStart();
                    } else {
                        ActionSupport.ResponseResult result = new ActionSupport.ResponseResult<IUserFeature.UserInfo>(
                                HttpHelper.CODE_RES_SUCCESS, "login without server");
                        userInfo = new UserInfo();
                        userInfo.setYmnLogined(true);
                        userInfo.resExt = msg;
                        result.processedResult = userInfo;
                        result.data = new JSONObject();
                        onActionResult(result);
                    }
                    break;
                case ACTION_RET_LOGOUT_SUCCESS:
                case ACTION_RET_ACCOUNTSWITCH_SUCCESS:
                    userInfo = null;
                    super.onCallBack(code, msg);
                    break;
                default:
                    super.onCallBack(code, msg);
                    break;
            }
        }
    };

    @Override
    public void onActionResult(ActionSupport.ResponseResult result) {
        if (result.isOk()) {
            AnalyticsData.loginServerResEvent(pluginWrapper, AnalyticsData.DATA_SUCCESS, result.data.toString(), result.getExtData(AnalyticsData.KEY_TRANSACTIONID));
            this.userInfo = (UserInfo) result.processedResult;
            YmnPluginManager.onLogin(result.processedResultAsMap());
            // updateUrlConfig();
            interceptor.dispatchNext(ACTION_RET_LOGIN_SUCCESS, result.dataAsString());
        } else {
            AnalyticsData.loginServerResEvent(pluginWrapper, AnalyticsData.DATA_FAIL, result.messageFail(), result.getExtData(AnalyticsData.KEY_TRANSACTIONID));
            interceptor.dispatchNext(ACTION_RET_LOGIN_FAIL, result.messageFail());
        }
    }

    private void updateUrlConfig() {
        RequestServerListAction action = new RequestServerListAction(pluginWrapper.getContext());
        action.putReqData(pluginWrapper, userInfo.getYmnUserIdInt(), userInfo.getPlatformUserId());
        action.addObserver(new ActionObserver() {
            @Override
            public void onActionResult(ActionSupport.ResponseResult result) {
                if (result.isOk()) {
                    UrlConfig config = (UrlConfig) result.processedResult;
                    if (config.isEnable()) {
                        YmnURLManager.saveRemoteConfig(pluginWrapper.getContext(), config);
                    } else {
                        Logger.e("illegal remote url config " + result.dataAsString());
                    }
                }
            }
        });
        action.actionStart();
    }

    @Override
    public boolean isLogined() {
        if (userInfo == null) return false;
        return userInfo.isYmnLogined();
    }

    @Override
    public void logout() {
        if (userFeature != null) {
            pluginWrapper.tryRunOnUiThreadOrJustRun(new Runnable() {
                @Override
                public void run() {
                    userFeature.logout();
                }
            });
        }
    }

    @Override
    public void showToolBar() {
        if (userFeature != null) {
            pluginWrapper.tryRunOnUiThreadOrJustRun(new Runnable() {
                @Override
                public void run() {
                    userFeature.showToolBar();
                }
            });
        }
    }

    @Override
    public void hideToolBar() {
        if (userFeature != null) {
            userFeature.hideToolBar();
        }
    }

    @Override
    public void switchAccount() {
        if (userFeature != null) {
            pluginWrapper.tryRunOnUiThreadOrJustRun(new Runnable() {
                @Override
                public void run() {
                    userFeature.switchAccount();
                }
            });
        }
    }

    @Override
    public void exit() {
        if (userFeature != null) {
            pluginWrapper.tryRunOnUiThreadOrJustRun(new Runnable() {
                @Override
                public void run() {
                    userFeature.exit();
                }
            });
        }
    }

    @Override
    public void submitUserInfo(final LinkedHashMap<String, String> data) {
        if (userFeature != null) {
            pluginWrapper.tryRunOnUiThreadOrJustRun(new Runnable() {
                @Override
                public void run() {
                    userFeature.submitUserInfo(data);
                }
            });
        }
    }

    @Override
    public UserInfo getUserInfo() {
        return userInfo;
    }

    @Override
    public void enterPlatform() {
        if (userFeature != null) {
            userFeature.enterPlatform();
        }
    }
}

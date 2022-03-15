package com.bianfeng.ymnsdk.feature.plugin;

import com.bianfeng.ymnsdk.YmnSdk;
import com.bianfeng.ymnsdk.YmnStrategy;
import com.bianfeng.ymnsdk.YmnUserCode;
import com.bianfeng.ymnsdk.action.ActionObserver;
import com.bianfeng.ymnsdk.action.ActionSupport;
import com.bianfeng.ymnsdk.action.HttpHelper;
import com.bianfeng.ymnsdk.action.RequestLoginAction;
import com.bianfeng.ymnsdk.action.RequestServerListAction;
import com.bianfeng.ymnsdk.entity.UrlConfig;
import com.bianfeng.ymnsdk.feature.YmnCallbackInterceptor;
import com.bianfeng.ymnsdk.feature.YmnPluginManager;
import com.bianfeng.ymnsdk.feature.YmnPluginWrapper;
import com.bianfeng.ymnsdk.feature.YmnPreferences;
import com.bianfeng.ymnsdk.feature.YmnURLManager;
import com.bianfeng.ymnsdk.feature.protocol.IUserFeature;
import com.bianfeng.ymnsdk.util.AnalyticsData;
import com.bianfeng.ymnsdk.util.Logger;

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

                    if (RichCallbackMessage.isRich(msg)) {
                        RichCallbackMessage messageObj = RichCallbackMessage.instance(msg);
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

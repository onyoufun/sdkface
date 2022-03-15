package com.bianfeng.ymnsdk.template;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.bianfeng.ymnsdk.YmnSdk;
import com.bianfeng.ymnsdk.feature.YmnDataBuilder;
import com.bianfeng.ymnsdk.feature.plugin.YmnChannelInterface;
import com.bianfeng.ymnsdk.feature.protocol.YFunction;
import com.bianfeng.ymnsdk.feature.protocol.YPlugin;
import com.bianfeng.ymnsdk.util.DataFunAgent;
import com.bianfeng.ymnsdk.util.Logger;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by huchanghai on 2017/11/6.
 */
@YPlugin(strategy = YPlugin.Policy.FORCE, entrance = YPlugin.Entrance.ACTIVITY)
public class TemplateInterface extends YmnChannelInterface {
    @Override
    public String getPluginId() {
        return "10000";
    }

    @Override
    public String getPluginName() {
        return "template";
    }

    @Override
    public int getPluginVersion() {
        return 1;
    }

    @Override
    public String getSdkVersion() {
        return "2.0.0";
    }

    private FloatWindow floatWindow;
    private boolean logined;

    @Override
    public boolean isLogined() {
        return logined;
    }

    @Override
    public void onInit(Context context) {
        super.onInit(context);
        TemplateData.FunctionEvent.create("1050101").status("1").send();

        Logger.setLogToCache(true);
        TemplateUi.init(context);

        sendResult(ACTION_RET_INIT_SUCCESS, null);
        sendResult(PAYRESULT_INIT_SUCCESS, null);
    }

    @YFunction(name = "template_login")
    @Override
    public void login() {
        TemplateData.FunctionEvent.create("1050110").status("1").send();

        TemplateLoginView view = new TemplateLoginView(getContext());
        view.setOnClickListener(new TemplateLoginView.OnClickListener() {
            @Override
            public void onSuccess(TemplateUi view) {
                view.dismiss();

                logined = true;

                String session = "time" + System.currentTimeMillis();
                String userId = DataFunAgent.getDeviceId(getContext());
                String userName = ((TemplateLoginView) view).getUserName();
                if (TextUtils.isEmpty(userName)) userName = "ymn_template" + userId;
                else userId = userName;

                YmnDataBuilder
                        .createJson(TemplateInterface.this)
                        .append(LOGIN_SUC_RS_UID, userId)
                        .append(LOGIN_SUC_RS_UNAME, userName)
                        .append(LOGIN_SUC_RS_SESSION, session)
                        .sendResult(ACTION_RET_LOGIN_SUCCESS);
            }

            @Override
            public void onFailure(TemplateUi view) {
                view.dismiss();
                sendResultWithoutInterceptors(ACTION_RET_LOGIN_FAIL, "测试终端登录失败情况");
            }

            @Override
            public void onCancel(TemplateUi view) {
                view.dismiss();
                sendResultWithoutInterceptors(ACTION_RET_LOGIN_CANCEL, "用户取消登录");
            }
        });
        view.show(getActivity());
    }

    @Override
    public void pay(Map<String, String> map) {
        super.pay(map);

        // 可能未登录情况调用，修复该情况下的崩溃问题
        if(getLoginedData() != null) {
            Logger.d("login responsed data of resExt is " + getLoginedData().get("resExt"));
        } else {
            Logger.e("login responsed data is null, maybe not logined");
        }

        TemplateData.FunctionEvent.create("1050111").status(TextUtils.isEmpty(TemplateData.getLackedParams(map)) ? "1" : "2").parameters(map).send();

        TemplatePayView view = new TemplatePayView(getContext());
        view.setLackedParamsText(TemplateData.getLackedParams(map));
        view.setOfferedParamsText(TemplateData.getOfferedParams(map));
        view.setOnClickListener(new TemplateUi.OnClickListener() {
            @Override
            public void onSuccess(TemplateUi view) {
                view.dismiss();
                // TemplateUtil.notifyPayResult(TemplateInterface.this, getOrderId());
                sendResultWithoutInterceptors(PAYRESULT_SUCCESS, null);
            }

            @Override
            public void onFailure(TemplateUi view) {
                view.dismiss();
                sendResultWithoutInterceptors(PAYRESULT_FAIL, "测试终端支付失败情况");
            }

            @Override
            public void onCancel(TemplateUi view) {
                view.dismiss();
                sendResultWithoutInterceptors(PAYRESULT_CANCEL, "用户取消支付");
            }
        });
        view.show(getActivity());
    }

    @YFunction(name = FUNCTION_EXIT)
    @Override
    public void exit() {
        TemplateData.FunctionEvent.create("1050112").status("1").send();

        final TemplateExitView view = new TemplateExitView(getActivity());
        view.setPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.dismiss();
                sendResult(ACTION_RET_EXIT_PAGE, null);
            }
        });
        view.setNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.dismiss();
            }
        });
        view.show(getActivity());
    }

    @YFunction(name = FUNCTION_LOGOUT)
    @Override
    public void logout() {
        super.logout();
        TemplateData.FunctionEvent.create("1050116").status("1").send();

        final TemplateConfirmView view = new TemplateConfirmView(getContext());
        view.setTitle("注销账号");
        view.setMessage("你要注销账号吗？(要求游戏切换场景返回登录页)");
        view.setPositive("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.dismiss();
                logined = false;
                sendResult(ACTION_RET_LOGOUT_SUCCESS, "注销账号成功");
            }
        });
        view.setNegative("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.dismiss();
            }
        });
        view.show(getActivity());
    }

    @YFunction(name = FUNCTION_ACCOUNT_SWITCH)
    @Override
    public void switchAccount() {
        super.switchAccount();
        final TemplateConfirmView view = new TemplateConfirmView(getContext());
        view.setTitle("切换账号");
        view.setMessage("你要切换账号吗？(要求游戏切换场景返回登录页)");
        view.setPositive("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.dismiss();
                logined = false;
                sendResult(ACTION_RET_ACCOUNTSWITCH_SUCCESS, "切换账号成功");
            }
        });
        view.setNegative("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.dismiss();
            }
        });
        view.show(getActivity());
    }

    @YFunction(name = "resetPassword")
    public void resetPassword() {
        final TemplateConfirmView view = new TemplateConfirmView(this.getContext());
        view.setTitle("修改密码");
        view.setMessage("你要修改密码吗？(要求游戏切换场景返回登录页)");
        view.setPositive("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.dismiss();
                logined = false;
                sendResult(ACTION_RET_LOGIN_TIMEOUT, "密码修改成功");
            }
        });
        view.setNegative("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.dismiss();
            }
        });
        view.show(getActivity());
    }

    @YFunction(name = FUNCTION_SUBMIT_USERINFO)
    @Override
    public void submitUserInfo(LinkedHashMap<String, String> data) {
        super.submitUserInfo(data);
        TemplateData.FunctionEvent.create("1050113").status("1").parameters(data).send();

        Toast.makeText(getActivity(), "提交用户数据成功", Toast.LENGTH_SHORT).show();
    }

    @YFunction(name = FUNCTION_SHOW_TOOLBAR)
    @Override
    public void showToolBar() {
        super.showToolBar();
        TemplateData.FunctionEvent.create("1050114").status("1").send();

        if (floatWindow == null) {
            floatWindow = new FloatWindow(getActivity());
            floatWindow.show();
        }
    }

    @YFunction(name = FUNCTION_HIDE_TOOLBAR)
    @Override
    public void hideToolBar() {
        super.hideToolBar();
        TemplateData.FunctionEvent.create("1050115").status("1").send();

        if (floatWindow != null) {
            floatWindow.release();
            floatWindow = null;
        }
    }

    @Override
    public void callFunction(String s, String... strings) {
        super.callFunction(s, strings);
        TemplateData.onFunctionEvent(s);
    }

    @Override
    public void callFunction(String s, LinkedHashMap<String, String> linkedHashMap) {
        super.callFunction(s, linkedHashMap);
        TemplateData.onFunctionEvent(s);
    }

    @Override
    public String callFunctionWithResult(String s, String... strings) {
        TemplateData.onFunctionEvent(s);
        return super.callFunctionWithResult(s, strings);
    }

    @Override
    public String callFunctionWithResult(String s, LinkedHashMap<String, String> linkedHashMap) {
        TemplateData.onFunctionEvent(s);
        return super.callFunctionWithResult(s, linkedHashMap);
    }

    @Override
    public void onStart() {
        super.onStart();
        TemplateData.onFunctionEvent("onStart");
        TemplateData.FunctionEvent.create("1050102").status("1").send();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        TemplateData.onFunctionEvent("onRestart");
        TemplateData.FunctionEvent.create("1050103").status("1").send();
    }

    @Override
    public void onResume() {
        super.onResume();
        TemplateData.onFunctionEvent("onResume");
        TemplateData.FunctionEvent.create("1050105").status("1").send();
    }

    @Override
    public void onPause() {
        super.onPause();
        TemplateData.onFunctionEvent("onPause");
        TemplateData.FunctionEvent.create("1050104").status("1").send();
    }

    @Override
    public void onStop() {
        super.onStop();
        TemplateData.onFunctionEvent("onStop");
        TemplateData.FunctionEvent.create("1050106").status("1").send();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        TemplateData.onFunctionEvent("onDestroy");
        TemplateData.FunctionEvent.create("1050107").status("1").send();
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        TemplateData.onFunctionEvent("onNewIntent");
        TemplateData.FunctionEvent.create("1050108").status("1").send();
    }

    @Override
    public void onActivityResult(int i, int i1, Intent intent) {
        super.onActivityResult(i, i1, intent);
        TemplateData.onFunctionEvent("onActivityResult");
        TemplateData.FunctionEvent.create("1050109").status("1").send();
    }
}

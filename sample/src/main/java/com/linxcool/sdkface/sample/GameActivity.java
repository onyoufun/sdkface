package com.linxcool.sdkface.sample;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;

import com.linxcool.sdkface.YmnSdk;
import com.linxcool.sdkface.util.Logger;

import java.util.LinkedHashMap;

/**
 * Created by huchanghai on 2017/9/5.
 */
public class GameActivity extends SdkWrapperActivity implements Handler.Callback {

    private LinearLayout layoutPlugins;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        handler = new Handler(this);
        layoutPlugins = (LinearLayout) findViewById(R.id.layoutPlugins);
        refreshViews();
    }

    public void refreshViews() {
        layoutPlugins.removeAllViews();

        FunctionViewFactory.registAdapter(new FunctionViewAdapter() {
            @Override
            public String getFunctionText() {
                return "登录";
            }
            @Override
            public void onClick(View view) {
                //YmnSdk.callFunction("template_login");
                YmnSdk.login();
            }
        });

        FunctionViewFactory.registAdapter(new FunctionViewAdapter() {
            @Override
            public String getFunctionText() {
                return "支付";
            }
            @Override
            public void onClick(View view) {
                LinkedHashMap<String, String> order = new LinkedHashMap<>();
                YmnSdk.pay(order);
            }
        });

        for (FunctionViewAdapter adapter : FunctionViewFactory.getAdapters()) {
            View v = FunctionViewFactory.newView(this, adapter);
            layoutPlugins.addView(v);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        YmnSdk.hideToolBar();
    }

    @Override
    public boolean handleMessage(Message msg) {
        YmnSdk.showToolBar();
        return false;
    }

    @Override
    public void onCallBack(int code, String msg) {
        switch (code) {
            case YmnSdk.ACTION_RET_LOGIN_SUCCESS:
                Logger.d("登录成功");
                handler.sendEmptyMessageDelayed(0, 200);
                break;
            case YmnSdk.ACTION_RET_LOGIN_CANCEL:
                Logger.d("登录取消");
                break;
            case YmnSdk.ACTION_RET_LOGIN_FAIL:
                Logger.d("登录失败");
                break;
            case YmnSdk.PAYRESULT_SUCCESS:
                Logger.d("支付成功");
                break;
            case YmnSdk.PAYRESULT_CANCEL:
                Logger.d("支付取消");
                break;
            case YmnSdk.PAYRESULT_FAIL:
                Logger.d("支付失败");
                break;
            case YmnSdk.ACTION_RET_EXIT_PAGE:
                finish();
                break;
            case YmnSdk.ACTION_RET_LOGOUT_SUCCESS:
            case YmnSdk.ACTION_RET_LOGIN_TIMEOUT:
            case YmnSdk.ACTION_RET_ACCOUNTSWITCH_SUCCESS:
                Logger.d("此时游戏应当切换到登录场景");
                break;
        }
    }

}

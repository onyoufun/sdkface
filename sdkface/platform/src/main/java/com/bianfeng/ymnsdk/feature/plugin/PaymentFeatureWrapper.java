package com.bianfeng.ymnsdk.feature.plugin;

import com.bianfeng.ymnsdk.YmnPaymentCode;
import com.bianfeng.ymnsdk.YmnStrategy;
import com.bianfeng.ymnsdk.action.ActionObserver;
import com.bianfeng.ymnsdk.action.ActionSupport;
import com.bianfeng.ymnsdk.action.RequestOrderAction;
import com.bianfeng.ymnsdk.action.RequestOrderStatusAction;
import com.bianfeng.ymnsdk.feature.YmnCallbackInterceptor;
import com.bianfeng.ymnsdk.feature.YmnPluginManager;
import com.bianfeng.ymnsdk.feature.YmnPluginWrapper;
import com.bianfeng.ymnsdk.feature.YmnPreferences;
import com.bianfeng.ymnsdk.feature.protocol.IPaymentFeature;
import com.bianfeng.ymnsdk.util.AnalyticsData;

import java.util.Map;

/**
 * Created by huchanghai on 2017/8/30.
 */
public class PaymentFeatureWrapper extends ActionObserver implements IPaymentFeature, YmnPaymentCode {

    IPaymentFeature paymentFeature;
    YmnPluginWrapper pluginWrapper;

    public YmnPluginWrapper getPluginWrapper() {
        return pluginWrapper;
    }

    public PaymentFeatureWrapper(IPaymentFeature paymentFeature) {
        this.paymentFeature = paymentFeature;
        this.pluginWrapper = ((YmnPluginWrapper) paymentFeature);
        pluginWrapper.addCallbackInterceptor(interceptor);
    }

    @Override
    public void pay(Map<String, String> order) {
        PAYMENT_ARGS_CHECKER.check(order);
        if(YmnStrategy.withStrategy(YmnStrategy.STRATEGY_WITH_SERVER)) {
            AnalyticsData.payServerEvent(pluginWrapper);
            RequestOrderAction action = YmnPreferences.adaptStrategy(new RequestOrderAction(pluginWrapper.getContext()));
            action.putReqData(pluginWrapper, order, pluginWrapper.getLoginedData());
            action.addObserver(this);
            action.actionStart();
        } else {
            paymentFeature.pay(order);
            YmnPluginManager.onPay(order);
        }
    }

    @Override
    public void onActionResult(ActionSupport.ResponseResult result) {
        // onActionResult下的代码在主线程中执行，无需特殊处理
        if (result.isOk()) {
            AnalyticsData.payServerResEvent(pluginWrapper, AnalyticsData.DATA_SUCCESS, result.dataAsString(), result.getExtData(AnalyticsData.KEY_TRANSACTIONID));
            paymentFeature.pay((Map<String, String>) result.processedResult);
            YmnPluginManager.onPay(result.processedResultAsMap());
        } else {
            AnalyticsData.payServerResEvent(pluginWrapper, AnalyticsData.DATA_FAIL, result.messageFail(), result.getExtData(AnalyticsData.KEY_TRANSACTIONID));
            pluginWrapper.sendResult(PAYRESULT_FAIL, result.messageFail());
        }
    }

    YmnCallbackInterceptor interceptor = new YmnCallbackInterceptor() {
        @Override
        public void onCallBack(int code, String msg) {
            AnalyticsData.payThirdResEvent(pluginWrapper, code, msg);
            super.onCallBack(code, msg);
        }
    };

    @Override
    public String getOrderId() {
        if (paymentFeature != null) {
            return paymentFeature.getOrderId();
        }
        return null;
    }

    public void checkOrder(String oderId, int orderType) {
        checkOrder(pluginWrapper, oderId, orderType);
    }

    public static void checkOrder(final YmnPluginWrapper pluginWrapper, String oderId, int orderType) {
        RequestOrderStatusAction action = new RequestOrderStatusAction(pluginWrapper.getContext());
        action.putReqData(pluginWrapper, oderId, orderType);
        action.addObserver(new ActionObserver() {
            @Override
            public void onActionResult(ActionSupport.ResponseResult result) {
                if (result.isOk()) {
                    boolean isLegal = (boolean) result.processedResult;
                    if (isLegal) {
                        int status = result.data.optInt("status");
                        if (ARG_CHECK_ORDER_PAY_SUCCESS == status || ARG_CHECK_ORDER_DELIVERY_SUCCESS == status) {
                            pluginWrapper.sendResult(PAYRESULT_SUCCESS, result.dataAsString());
                        } else {
                            pluginWrapper.sendResult(PAYRESULT_FAIL, result.messageFail());
                        }
                    } else {
                        pluginWrapper.sendResult(PAYRESULT_FAIL, result.messageFail());
                    }
                } else {
                    pluginWrapper.sendResult(PAYRESULT_NETWORK_ERROR, result.messageFail());
                }
            }
        });
        action.actionStart();
    }
}

package com.bianfeng.ymnsdk.feature.plugin;

import android.content.Context;
import android.text.TextUtils;

import com.bianfeng.ymnsdk.YmnCode;
import com.bianfeng.ymnsdk.action.ActionObserver;
import com.bianfeng.ymnsdk.action.RequestFeecodesAction;
import com.bianfeng.ymnsdk.action.RequestProductsAction;
import com.bianfeng.ymnsdk.feature.YmnPluginWrapper;
import com.bianfeng.ymnsdk.feature.protocol.IPaymentFeature;
import com.bianfeng.ymnsdk.feature.protocol.IUserFeature;
import com.google.gson.Gson;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 该类仅作为渠道类插件扩展，简化操作
 * Created by huchanghai on 2017/9/7.
 */
public abstract class YmnChannelInterface extends YmnPluginWrapper implements IUserFeature, IPaymentFeature, YmnCode {

    private String cpOrderId;
    private String ymnOrderId;

    @Override
    public void onInit(Context context) {
        super.onInit(context);
        setIniting(true);
    }

    @Override
    public void sendResult(int code, String msg) {
        setInitFlagsByReturnCode(code);
        super.sendResult(code, msg);
    }

    @Override
    public void sendResultWithoutInterceptors(int code, String msg) {
        setInitFlagsByReturnCode(code);
        super.sendResultWithoutInterceptors(code, msg);
    }

    /**
     * 改方法仅针对UserFeature的结果做处理，特殊情况请分开处理
     *
     * @param code
     */
    protected void setInitFlagsByReturnCode(int code) {
        if (code == ACTION_RET_INIT_SUCCESS) {
            setIniting(false);
            setInited(true);
        }
        if (code == ACTION_RET_INIT_FAIL) {
            setIniting(false);
            setInited(false);
        }
    }

    @Override
    public boolean isLogined() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void logout() {
        // TODO Auto-generated method stub
    }

    @Override
    public void showToolBar() {
        // TODO Auto-generated method stub
    }

    @Override
    public void hideToolBar() {
        // TODO Auto-generated method stub
    }

    @Override
    public void switchAccount() {
        // TODO Auto-generated method stub
    }

    @Override
    public void exit() {
        // TODO Auto-generated method stub
    }

    @Override
    public void submitUserInfo(LinkedHashMap<String, String> data) {
        // TODO Auto-generated method stub
    }

    @Override
    public UserInfo getUserInfo() {
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(getLoginedData()), UserInfo.class);
    }

    @Override
    public void enterPlatform() {
        // TODO Auto-generated method stub
    }

    @Override
    public void pay(Map<String, String> order) {
        cpOrderId = order.get(ARG_CP_ORDER_ID);
        ymnOrderId = order.get(ARG_TRADE_CODE);
    }

    @Override
    public String getOrderId() {
        if (!TextUtils.isEmpty(ymnOrderId)) {
            return ymnOrderId;
        }
        return cpOrderId;
    }

    /**
     * 返回订单总金额
     *
     * @param order
     * @return
     */
    public float getOrderTotalPrice(Map<String, String> order) {
        try {
            float price = Float.parseFloat(order.get(ARG_PRODUCT_PRICE));
            float count = Float.parseFloat(order.get(ARG_PRODUCT_COUNT));
            return price * count;
        } catch (Exception e) {
            e.printStackTrace();
            return 999999999;
        }
    }

    public String getOrderNotifyUrl(Map<String, String> order) {
        String notifyUrl = order.get(ARG_PLATFORM_NOTIFY_URL);
        if (TextUtils.isEmpty(notifyUrl)) {
            notifyUrl = order.get(ARG_NOTIFY_URL);
        }
        return notifyUrl;
    }

    /**
     * 返回支付透传参数
     *
     * @param order
     * @return
     */
    public String getOrderExtArg(Map<String, String> order) {
        String extArg = order.get(ARG_THIRDPARTY_CALLBACK);
        if (TextUtils.isEmpty(extArg)) {
            extArg = order.get(ARG_EXT);
        }
        return extArg;
    }

    /**
     * 返回客户端订单回调信息
     *
     * @param order
     * @return
     */
    public String getOrdeCallbackMessage(Map<String, String> order) {
        String message = order.get(ARG_THIRDPARTY_CALLBACK);
        if (TextUtils.isEmpty(message)) {
            Gson gson = new Gson();
            message = gson.toJson(order);
        }
        return message;
    }

    public void checkOrder() {
        if (!TextUtils.isEmpty(ymnOrderId)) {
            PaymentFeatureWrapper.checkOrder(this, ymnOrderId, ORDER_TYPE_YMN);
        } else {
            PaymentFeatureWrapper.checkOrder(this, cpOrderId, ORDER_TYPE_CP);
        }
    }

    public void requestProducts(ActionObserver observer) {
        RequestProductsAction action = new RequestProductsAction(getContext());
        action.putReqData(this);
        action.addObserver(observer);
        action.actionStart();
    }

    public void requestFeedcodes(ActionObserver observer) {
        RequestFeecodesAction action = new RequestFeecodesAction(getContext());
        action.putReqData(this);
        action.addObserver(observer);
        action.actionStart();
    }
}

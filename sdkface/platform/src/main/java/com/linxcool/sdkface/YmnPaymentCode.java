package com.linxcool.sdkface;

import java.util.HashMap;
import java.util.Map;

public interface YmnPaymentCode {

    int PAYRESULT_SUCCESS = 200;
    int PAYRESULT_FAIL = 201;
    int PAYRESULT_CANCEL = 202;
    int PAYRESULT_NETWORK_ERROR = 203;
    int PAYRESULT_PRODUCTIONINFOR_INCOMPLETE = 204;
    int PAYRESULT_INIT_SUCCESS = 205;
    int PAYRESULT_INIT_FAIL = 206;
    int PAYRESULT_NOW_PAYING = 207;

    Map<Integer, String> ERROR_MESSAGE = new HashMap<Integer, String>() {
        {
            put(PAYRESULT_SUCCESS, "支付成功");
            put(PAYRESULT_FAIL, "支付失败");
            put(PAYRESULT_CANCEL, "取消支付");
            put(PAYRESULT_NETWORK_ERROR, "网络错误，请再次尝试");
            put(PAYRESULT_PRODUCTIONINFOR_INCOMPLETE, "订单信息不完整或非法");
            put(PAYRESULT_INIT_SUCCESS, "支付初始化成功");
            put(PAYRESULT_INIT_FAIL, "支付初始化失败");
            put(PAYRESULT_NOW_PAYING, "支付正在进行");
        }
    };

}

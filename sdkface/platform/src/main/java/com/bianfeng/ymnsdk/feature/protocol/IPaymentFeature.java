package com.bianfeng.ymnsdk.feature.protocol;

import android.text.TextUtils;

import com.bianfeng.ymnsdk.util.Logger;

import java.util.Map;

/**
 * 支付充值
 *
 * @author huchanghai
 */
public interface IPaymentFeature {

    // 游戏传入
    String ARG_CP_ORDER_ID = "order_id";
    String ARG_PRODUCT_ID = "product_id";
    String ARG_PRODUCT_NAME = "product_name";
    String ARG_PRODUCT_PRICE = "product_price";
    String ARG_PRODUCT_COUNT = "product_count";
    String ARG_ROLE_ID = "role_id";
    String ARG_ROLE_NAME = "role_name";
    String ARG_ROLE_GRADE = "role_grade";
    String ARG_ROLE_BALANCE = "role_balance";
    String ARG_SERVER_ID = "server_id";
    String ARG_SERVER_NAME = "server_name";
    String ARG_NOTIFY_URL = "notify_url";
    String ARG_EXT = "ext";

    // 平台返回
    String ARG_TRADE_CODE = "trade_code";
    String ARG_CLIENT_CALLBACK = "client_callback";
    String ARG_THIRDPARTY_CALLBACK = "platform_callback";
    String ARG_PLATFORM_NOTIFY_URL = "platform_notify_url";

    int ARG_CHECK_ORDER_PAY_SUCCESS = 2;
    int ARG_CHECK_ORDER_DELIVERY_SUCCESS = 3;

    /**
     * 订单类型 CP应用
     */
    int ORDER_TYPE_CP = 0;

    /**
     * 订单类型 有猫腻
     */
    int ORDER_TYPE_YMN = 1;

    void pay(Map<String, String> order);

    String getOrderId();

    class PAYMENT_ARGS_CHECKER {

        public static boolean check(Map<String, String> order) {
            StringBuilder builder = new StringBuilder();
            if (TextUtils.isEmpty(order.get(ARG_PRODUCT_ID))) {
                builder.append("\n错误：商品ID为空，请传入product_id");
            }
            if (TextUtils.isEmpty(order.get(ARG_PRODUCT_NAME))) {
                builder.append("\n错误：商品名称为空，请传入product_name");
                order.put(ARG_PRODUCT_NAME, "ymnDefValPro");
            }
            if (TextUtils.isEmpty(order.get(ARG_PRODUCT_PRICE))) {
                builder.append("\n错误：商品价格为空，请传入product_price");
            }
            if (TextUtils.isEmpty(order.get(ARG_PRODUCT_COUNT))) {
                builder.append("\n错误：商品数量为空，请传入product_count");
                order.put(ARG_PRODUCT_COUNT, "1");
            }
            if (TextUtils.isEmpty(order.get(ARG_ROLE_ID))) {
                builder.append("\n错误：角色ID为空，请传入role_id");
                order.put(ARG_ROLE_ID, "123");
            }
            if (TextUtils.isEmpty(order.get(ARG_ROLE_NAME))) {
                builder.append("\n错误：角色名称为空，请传入role_name");
                order.put(ARG_ROLE_NAME, "ymnDefValRole");
            }
            if (TextUtils.isEmpty(order.get(ARG_ROLE_GRADE))) {
                builder.append("\n错误：角色等级为空，请传入role_grade");
                order.put(ARG_ROLE_GRADE, "1");
            }
            if (TextUtils.isEmpty(order.get(ARG_ROLE_BALANCE))) {
                builder.append("\n错误：角色余额为空，请传入role_balance");
                order.put(ARG_ROLE_BALANCE, "0");
            }
            if (TextUtils.isEmpty(order.get(ARG_SERVER_ID))) {
                builder.append("\n错误：服务器ID为空，请传入server_id");
                order.put(ARG_SERVER_ID, "456");
            }
            if (TextUtils.isEmpty(order.get(ARG_SERVER_NAME))) {
                builder.append("\n错误：服务器名称为空，请传入server_name");
                order.put(ARG_SERVER_NAME, "ymnDefValSer");
            }
            if (TextUtils.isEmpty(order.get(ARG_NOTIFY_URL))) {
                builder.append("\n错误：通知地址为空，请传入notify_url");
            }

            if (builder.length() == 0) {
                return true;
            } else {
                Logger.eRich(builder.toString());
                return false;
            }
        }

    }
}

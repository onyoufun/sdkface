package com.linxcool.sdkface.googleplay;

import android.content.Context;
import android.content.Intent;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.linxcool.sdkface.YmnCallback;
import com.linxcool.sdkface.feature.plugin.YmnPaymentInterface;
import com.linxcool.sdkface.feature.protocol.YPlugin;
import com.sample.android.billing.BillingRepository;
import com.sample.android.billing.BillingDataSource;

import java.util.List;
import java.util.Map;


/**
 * Created by huchanghai on 2018/1/16.
 */
@YPlugin(strategy = YPlugin.Policy.FORCE, entrance = YPlugin.Entrance.ACTIVITY)
public class GooglePlayInterface extends YmnPaymentInterface {

    @Override
    public String getPluginId() {
        return "101";
    }

    @Override
    public String getPluginName() {
        return "googleplay";
    }

    @Override
    public int getPluginVersion() {
        return 1;
    }

    @Override
    public String getSdkVersion() {
        return "4.1.0";
    }

    private BillingDataSource billingDataSource;
    private BillingRepository gameRepository;

    @Override
    public void onInit(final Context context) {
        super.onInit(context);

        BillingDataSource.setYmnCallback(new YmnCallback() {
            @Override
            public void onCallBack(int code, String msg) {
                sendResult(code, msg);
            }
        });

        this.billingDataSource = BillingDataSource.getInstance(context,
                BillingRepository.INAPP_SKUS,
                BillingRepository.SUBSCRIPTION_SKUS,
                BillingRepository.AUTO_CONSUME_SKUS);
        this.gameRepository = new BillingRepository(billingDataSource);
    }

    @Override
    public void pay(final Map<String, String> map) {
        gameRepository.buySku(getActivity(), "105");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

}

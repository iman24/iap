package com.nxtra.iap;

import android.content.Context;

import androidx.annotation.NonNull;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;

public class CPD {
    static BillingClient billingClient;

    private String sku;
    private Boolean status = false;

    public void setSku(String s, Context context){
        sku = s;
        checkProducts(context);
    }

    private String getSku(){
        return sku;
    }

    public Boolean getStatus(){
        return status;
    }

    private void setStatus(Boolean s) {
        status = s;
    }

    private void checkProducts(Context context) {


        billingClient = BillingClient.newBuilder(context).enablePendingPurchases().setListener((billingResult, list) -> {
        }).build();

        final BillingClient finalBillingClient = billingClient;
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {

            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {

                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    finalBillingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP, (billingResult1, list) -> {
                        if (list.size() == 0) {
                            setStatus(false);
                        } else {
                            for (Purchase purchase : list) {
                                if (purchase.getSkus().get(0).equals(getSku())) {
                                    setStatus(true);
                                }
                            }
                        }
                    });
                }
            }
        });
    }
}

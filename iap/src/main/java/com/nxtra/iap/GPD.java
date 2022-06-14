package com.nxtra.iap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.ArrayList;
import java.util.List;

public class GPD {
    BillingClient billingClient;
    Boolean isVerified = false;
    String PRODUCT_SKU;
    Activity activity;
    private static final String TAG = "GPD";

    private void setVerified(Boolean verifiy){
        this.isVerified = verifiy;
    }

    public Boolean isVerified(){
        return this.isVerified;
    }

    public GPD(Context appContext, Activity activity, String sku) {
        PRODUCT_SKU = sku;
        this.activity = activity;

        billingClient = BillingClient.newBuilder(appContext)
                .setListener((billingResult, list) -> {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null) {
                        for (Purchase purchase : list) {
                            verifyPayment(purchase);
                        }
                    }

                })
                .enablePendingPurchases()
                .build();

        connectGooglePlayBilling();
    }




    void connectGooglePlayBilling() {
        Log.e(TAG, "connectGooglePlayBilling: ");
        billingClient.startConnection(new BillingClientStateListener() {

            @Override
            public void onBillingServiceDisconnected() {
                connectGooglePlayBilling();
            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    getProducts();
                }

            }
        });

    }



    // launch purchase flow
    void getProducts() {
        Log.e(TAG, "getProducts: ");

        List<String> skuList = new ArrayList<>();

        //replace these with your product IDs from google play console
        skuList.add(PRODUCT_SKU);
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);

        billingClient.querySkuDetailsAsync(params.build(),
                new SkuDetailsResponseListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSkuDetailsResponse(@NonNull BillingResult billingResult,
                                                     List<SkuDetails> skuDetailsList) {
                        // Process the result.
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {

                            Log.d("remove",skuDetailsList+"");

                            for (SkuDetails skuDetails: skuDetailsList){
                                if (skuDetails.getSku().equals(PRODUCT_SKU)){
                                        launchPurchaseFlow(skuDetails, activity);
                                }

                            }
                        }
                    }
                });
    }

    void launchPurchaseFlow(SkuDetails skuDetails, Context context) {
        Log.e(TAG, "launchPurchaseFlow: ");
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetails)
                .build();

        billingClient.launchBillingFlow((Activity) context, billingFlowParams);
    }


    void verifyPayment(Purchase purchase) {
        Log.e(TAG, "verifyPayment: ");
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, billingResult -> {

                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        setVerified(true);
                    } else {
                        setVerified(false);
                    }
                });
            }
        }
    }
}

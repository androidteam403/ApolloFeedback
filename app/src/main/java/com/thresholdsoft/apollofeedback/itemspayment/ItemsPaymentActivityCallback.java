package com.thresholdsoft.apollofeedback.itemspayment;

import com.thresholdsoft.apollofeedback.itemspayment.model.GetAdvertisementResponse;

public interface ItemsPaymentActivityCallback {
    void onClickContinuePayment();

    void onSuccessGetAdvertisementApi(GetAdvertisementResponse getAdvertisementResponse);

    void onFailureMessage(String message);
}

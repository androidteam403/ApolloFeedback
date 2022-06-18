package com.thresholdsoft.apollofeedback.ui.itemspayment;

import com.thresholdsoft.apollofeedback.ui.itemspayment.model.GetAdvertisementResponse;

public interface ItemsPaymentActivityCallback {
    void onClickContinuePayment();

    void onSuccessGetAdvertisementApi(GetAdvertisementResponse getAdvertisementResponse);

    void onFailureMessage(String message);
}

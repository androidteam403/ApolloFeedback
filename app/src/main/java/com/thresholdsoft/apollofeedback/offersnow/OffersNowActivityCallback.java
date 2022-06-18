package com.thresholdsoft.apollofeedback.offersnow;

import com.thresholdsoft.apollofeedback.offersnow.model.GetOffersNowResponse;

public interface OffersNowActivityCallback {
    void onClickSkip();

    void onSuccesGetOffersNowApi(GetOffersNowResponse getOffersNowResponse);

    void onFailureMessage(String message);
}

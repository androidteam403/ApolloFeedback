package com.thresholdsoft.apollofeedback.ui.offersnow;

import com.thresholdsoft.apollofeedback.ui.offersnow.model.GetOffersNowResponse;

public interface OffersNowActivityCallback {
    void onClickSkip();

    void onSuccesGetOffersNowApi(GetOffersNowResponse getOffersNowResponse);

    void onFailureMessage(String message);
}

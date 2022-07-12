package com.thresholdsoft.apollofeedback.ui.offersnow;

import com.thresholdsoft.apollofeedback.commonmodels.FeedbackSystemResponse;
import com.thresholdsoft.apollofeedback.ui.offersnow.model.GetOffersNowResponse;

public interface OffersNowActivityCallback {
    void onClickSkip();

    void onSuccesGetOffersNowApi(GetOffersNowResponse getOffersNowResponse);

    void onFailureMessage(String message);

    void onSuccessFeedbackSystemApiCall(FeedbackSystemResponse feedbackSystemResponse);

    void onClickRefreshIcon();

    void onClickSettingIcon();

    void onAccessDialogDismiss();
}

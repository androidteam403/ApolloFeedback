package com.thresholdsoft.apollofeedback.ui.itemspayment;

import com.thresholdsoft.apollofeedback.commonmodels.FeedbackSystemResponse;
import com.thresholdsoft.apollofeedback.ui.itemspayment.model.CrossShellResponse;
import com.thresholdsoft.apollofeedback.ui.itemspayment.model.GetAdvertisementResponse;

public interface ItemsPaymentActivityCallback {
    void onClickContinuePayment();

    void onSuccessGetAdvertisementApi(GetAdvertisementResponse getAdvertisementResponse);

    void onFailureMessage(String message);

    void onSuccessFeedbackSystemApiCall(FeedbackSystemResponse feedbackSystemResponse);

    void onClickRefreshIcon();

    void onSucessCrossShell(CrossShellResponse crossShellResponse);

    void onCLickStartRecord();

    void onClickPlayorStop();
}

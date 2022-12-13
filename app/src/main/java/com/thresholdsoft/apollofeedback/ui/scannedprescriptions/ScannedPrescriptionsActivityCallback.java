package com.thresholdsoft.apollofeedback.ui.scannedprescriptions;

import com.thresholdsoft.apollofeedback.commonmodels.FeedbackSystemResponse;

public interface ScannedPrescriptionsActivityCallback {
    void onSuccessFeedbackSystemApiCall(FeedbackSystemResponse feedbackSystemResponse);
    void onFailureMessage(String message);

    void onClickScanAgain();

    void onClickPrescription(String prescriptionPath);

    void onClickItemDelete(int position);
}

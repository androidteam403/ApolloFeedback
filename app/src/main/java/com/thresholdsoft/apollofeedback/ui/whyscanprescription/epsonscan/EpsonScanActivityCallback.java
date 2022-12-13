package com.thresholdsoft.apollofeedback.ui.whyscanprescription.epsonscan;

import com.thresholdsoft.apollofeedback.commonmodels.FeedbackSystemResponse;

public interface EpsonScanActivityCallback {
    void onSuccessFeedbackSystemApiCall(FeedbackSystemResponse feedbackSystemResponse);
    void onFailureMessage(String message);
}

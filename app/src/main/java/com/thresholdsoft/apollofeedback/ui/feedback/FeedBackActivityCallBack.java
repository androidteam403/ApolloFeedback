package com.thresholdsoft.apollofeedback.ui.feedback;

import com.thresholdsoft.apollofeedback.commonmodels.FeedbackSystemResponse;

public interface FeedBackActivityCallBack {

    void onFailureMessage(String message);

    void onSuccessFeedbackSystemApiCall(FeedbackSystemResponse feedbackSystemResponse);
}

package com.thresholdsoft.apollofeedback.ui.feedback;

import android.graphics.Bitmap;

import com.thresholdsoft.apollofeedback.commonmodels.FeedbackSystemResponse;
import com.thresholdsoft.apollofeedback.ui.offersnow.model.ZeroCodeApiModelResponse;

import java.io.File;

public interface FeedBackActivityCallBack {

    void onFailureMessage(String message);

    void onSuccessFeedbackSystemApiCall(FeedbackSystemResponse feedbackSystemResponse);

    void onSuccessFeedbackSystemApiContinousCall(FeedbackSystemResponse feedbackSystemResponse, int isFeedback);

    void onSuccessMultipartResponse(ZeroCodeApiModelResponse response, Bitmap image, File file);
}

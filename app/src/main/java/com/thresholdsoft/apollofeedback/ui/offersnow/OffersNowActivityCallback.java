package com.thresholdsoft.apollofeedback.ui.offersnow;

import android.graphics.Bitmap;

import com.thresholdsoft.apollofeedback.commonmodels.FeedbackSystemResponse;
import com.thresholdsoft.apollofeedback.ui.offersnow.model.DcOffersNowResponse;
import com.thresholdsoft.apollofeedback.ui.offersnow.model.GetOffersNowResponse;
import com.thresholdsoft.apollofeedback.ui.offersnow.model.OneApolloAPITransactionResponse;
import com.thresholdsoft.apollofeedback.ui.offersnow.model.ZeroCodeApiModelResponse;

import java.io.File;

public interface OffersNowActivityCallback {
    void onClickSkip();

    void onSuccesGetOffersNowApi(GetOffersNowResponse getOffersNowResponse);

    void onFailureMessage(String message);

    void onSuccessFeedbackSystemApiCall(FeedbackSystemResponse feedbackSystemResponse);

    void onClickRefreshIcon();

    void onClickSettingIcon();

    void onAccessDialogDismiss();

    void onSuccesDcOffersNowApi(DcOffersNowResponse body);

    void onFailureDcOffersNowApi();

    void onClickCapture();

    void onSuccessMultipartResponse(ZeroCodeApiModelResponse zeroCodeApiModelResponse, Bitmap image, File file);

    void onFailureMultipartResponse(String message);

    void onSuccessOneApolloApiTransaction(OneApolloAPITransactionResponse oneApolloAPITransactionResponse, Bitmap image, File file);

    void onFailureOneApolloApiTransaction(String message, Bitmap image, File file);

    void onCLickStartRecord();

    void onClickPlayorStop();
}

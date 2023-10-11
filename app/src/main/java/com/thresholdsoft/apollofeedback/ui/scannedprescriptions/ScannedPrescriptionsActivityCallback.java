package com.thresholdsoft.apollofeedback.ui.scannedprescriptions;

import com.thresholdsoft.apollofeedback.commonmodels.FeedbackSystemResponse;
import com.thresholdsoft.apollofeedback.ui.scannedprescriptions.model.KioskSelfCheckOutTransactionResponse;

public interface ScannedPrescriptionsActivityCallback {
    void onSuccessFeedbackSystemApiCall(FeedbackSystemResponse feedbackSystemResponse);

    void onFailureMessage(String message);

    void onClickScanAgain();

    void onClickPrescription(String prescriptionPath);

    void onClickItemDelete(int position);

    void onSuccessKioskSelfCheckOutTransactionApiCAll(KioskSelfCheckOutTransactionResponse kioskSelfCheckOutTransactionResponse, int prescriptionPos);

    void onClickUploadPrescriptions();
}

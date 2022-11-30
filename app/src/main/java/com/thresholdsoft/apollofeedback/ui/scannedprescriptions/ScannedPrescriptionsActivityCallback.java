package com.thresholdsoft.apollofeedback.ui.scannedprescriptions;

public interface ScannedPrescriptionsActivityCallback {

    void onClickScanAgain();

    void onClickPrescription(String prescriptionPath);

    void onClickItemDelete(int position);
}

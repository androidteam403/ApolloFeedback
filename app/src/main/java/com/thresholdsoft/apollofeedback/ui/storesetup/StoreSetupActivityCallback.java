package com.thresholdsoft.apollofeedback.ui.storesetup;

import com.thresholdsoft.apollofeedback.ui.model.DeviceRegistrationResponse;
import com.thresholdsoft.apollofeedback.ui.storesetup.model.StoreListResponseModel;

public interface StoreSetupActivityCallback {
    void onSelectStoreSearch();

    void setStoresList(StoreListResponseModel storesList);

    void onSelectStore(StoreListResponseModel.StoreListObj item);

    void dialogCloseListiner();

    void onCancelBtnClick();

    void onVerifyClick();

    String getDeviceId();

//    String getFcmKey();

    String getStoreId();

    String getStoreContactNum();

    StoreListResponseModel.StoreListObj getStoreDetails();

    String getTerminalId();

    String getUserId();

    String getDeviceType();

    String getRegisteredDate();

    String getLatitude();

    String getLongitude();

    String getEposURL();

    void getDeviceRegistrationDetails(DeviceRegistrationResponse body);

    void closeIcon();
}

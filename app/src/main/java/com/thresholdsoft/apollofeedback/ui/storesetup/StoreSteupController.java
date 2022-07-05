package com.thresholdsoft.apollofeedback.ui.storesetup;

import android.app.Activity;

import com.thresholdsoft.apollofeedback.network.ApiClient;
import com.thresholdsoft.apollofeedback.network.ApiInterface;
import com.thresholdsoft.apollofeedback.ui.model.DeviceRegistrationRequest;
import com.thresholdsoft.apollofeedback.ui.model.DeviceRegistrationResponse;
import com.thresholdsoft.apollofeedback.ui.storesetup.model.StoreListResponseModel;
import com.thresholdsoft.apollofeedback.utils.CommonUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StoreSteupController {

    Activity activity;
    StoreSetupActivityMvpView storeSetupActivityMvpView;

    public StoreSteupController(StoreSetupActivityMvpView storeSetupActivityMvpView, StoreSetupActivity activity) {
        this.activity = activity;
        this.storeSetupActivityMvpView = storeSetupActivityMvpView;
    }


    public void getStoreList() {

        CommonUtils.showDialog(activity, "Loading…");
        ApiInterface api = ApiClient.getApiService2();
        Call<StoreListResponseModel> call = api.GET_STORES_LIST();
        call.enqueue(new Callback<StoreListResponseModel>() {
            @Override
            public void onResponse(Call<StoreListResponseModel> call, Response<StoreListResponseModel> response) {
                CommonUtils.hideDialog();
                if (response.body() != null) {
                    storeSetupActivityMvpView.setStoresList(response.body());
                }
            }

            @Override
            public void onFailure(Call<StoreListResponseModel> call, Throwable t) {
                CommonUtils.hideDialog();
            }
        });
    }

    public void getDeviceRegistrationDetails(String date, String deviceType, String macId, double latitude, double longitude, String storeId, String terminalId, String admin) {
        CommonUtils.showDialog(activity, "Loading…");
        ApiInterface api = ApiClient.getApiService2();
        DeviceRegistrationRequest deviceRegistrationRequest = new DeviceRegistrationRequest();
        deviceRegistrationRequest.setDevicedate(storeSetupActivityMvpView.getRegisteredDate());
        deviceRegistrationRequest.setDevicetype(storeSetupActivityMvpView.getDeviceType());
        deviceRegistrationRequest.setFcmkey("test");
        deviceRegistrationRequest.setLatitude("test");
        deviceRegistrationRequest.setLogitude("test");
        deviceRegistrationRequest.setMacid(storeSetupActivityMvpView.getDeviceId());
        deviceRegistrationRequest.setStoreid(storeSetupActivityMvpView.getStoreId());
        deviceRegistrationRequest.setTerminalid(storeSetupActivityMvpView.getTerminalId());
        deviceRegistrationRequest.setUserid("admin");

        Call<DeviceRegistrationResponse> call = api.deviceRegistration(deviceRegistrationRequest);
        call.enqueue(new Callback<DeviceRegistrationResponse>() {
            @Override
            public void onResponse(Call<DeviceRegistrationResponse> call, Response<DeviceRegistrationResponse> response) {
                CommonUtils.hideDialog();
                if (response.body() != null) {
                    storeSetupActivityMvpView.getDeviceRegistrationDetails(response.body());
                }
            }

            @Override
            public void onFailure(Call<DeviceRegistrationResponse> call, Throwable t) {
                CommonUtils.hideDialog();
            }
        });
    }
}

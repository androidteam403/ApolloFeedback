package com.thresholdsoft.apollofeedback.ui.itemspayment;

import android.content.Context;

import com.thresholdsoft.apollofeedback.ui.itemspayment.model.GetAdvertisementResponse;
import com.thresholdsoft.apollofeedback.network.ApiClient;
import com.thresholdsoft.apollofeedback.network.ApiInterface;
import com.thresholdsoft.apollofeedback.utils.CommonUtils;
import com.thresholdsoft.apollofeedback.utils.NetworkUtils;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemsPaymentActivityController {
    private Context mContext;
    private ItemsPaymentActivityCallback mCallback;

    public ItemsPaymentActivityController(Context mContext, ItemsPaymentActivityCallback mCallback) {
        this.mContext = mContext;
        this.mCallback = mCallback;
    }

    public void getAdvertisementApiCall() {
        if (NetworkUtils.isNetworkConnected(mContext)) {
            CommonUtils.showDialog(mContext, "Please wait.");

            ApiInterface apiInterface = ApiClient.getApiService();
            Call<GetAdvertisementResponse> call = apiInterface.GET_ADVERTISEMENT_API_CALL();
            call.enqueue(new Callback<GetAdvertisementResponse>() {
                @Override
                public void onResponse(@NotNull Call<GetAdvertisementResponse> call, @NotNull Response<GetAdvertisementResponse> response) {
                    if (response.isSuccessful() && response.code() == 200) {
                        mCallback.onSuccessGetAdvertisementApi(response.body());
                    }
                    CommonUtils.hideDialog();
                }

                @Override
                public void onFailure(@NotNull Call<GetAdvertisementResponse> call, @NotNull Throwable t) {
                    CommonUtils.hideDialog();
                    mCallback.onFailureMessage(t.getMessage());
                }
            });
        } else {
            mCallback.onFailureMessage("Something went wrong.");
        }
    }
}

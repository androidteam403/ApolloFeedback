package com.thresholdsoft.apollofeedback.offersnow;

import android.content.Context;

import com.thresholdsoft.apollofeedback.network.ApiClient;
import com.thresholdsoft.apollofeedback.network.ApiInterface;
import com.thresholdsoft.apollofeedback.offersnow.model.GetOffersNowResponse;
import com.thresholdsoft.apollofeedback.utils.CommonUtils;
import com.thresholdsoft.apollofeedback.utils.NetworkUtils;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OffersNowActivityController {
    private Context mContext;
    private OffersNowActivityCallback mCallback;

    public OffersNowActivityController(Context mContext, OffersNowActivityCallback mCallback) {
        this.mContext = mContext;
        this.mCallback = mCallback;
    }

    public void getOffersNowApiCall() {
        if (NetworkUtils.isNetworkConnected(mContext)) {
            CommonUtils.showDialog(mContext, "Please wait.");

            ApiInterface apiInterface = ApiClient.getApiService();
            Call<GetOffersNowResponse> call = apiInterface.GET_OFFERS_NOW_API_CALL();
            call.enqueue(new Callback<GetOffersNowResponse>() {
                @Override
                public void onResponse(@NotNull Call<GetOffersNowResponse> call, @NotNull Response<GetOffersNowResponse> response) {
                    if (response.isSuccessful() && response.code() == 200) {
                        mCallback.onSuccesGetOffersNowApi(response.body());
                    }
                    CommonUtils.hideDialog();
                }

                @Override
                public void onFailure(@NotNull Call<GetOffersNowResponse> call, @NotNull Throwable t) {
                    CommonUtils.hideDialog();
                    mCallback.onFailureMessage(t.getMessage());
                }
            });
        } else {
            mCallback.onFailureMessage("Something went wrong.");
        }
    }
}

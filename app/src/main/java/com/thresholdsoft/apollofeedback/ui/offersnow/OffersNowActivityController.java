package com.thresholdsoft.apollofeedback.ui.offersnow;

import android.content.Context;

import com.thresholdsoft.apollofeedback.commonmodels.FeedbackSystemRequest;
import com.thresholdsoft.apollofeedback.commonmodels.FeedbackSystemResponse;
import com.thresholdsoft.apollofeedback.network.ApiClient;
import com.thresholdsoft.apollofeedback.network.ApiInterface;
import com.thresholdsoft.apollofeedback.ui.offersnow.model.GetOffersNowResponse;
import com.thresholdsoft.apollofeedback.utils.AppConstants;
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
            CommonUtils.showDialog(mContext, "Please wait...");

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

    public void feedbakSystemApiCall() {
        if (NetworkUtils.isNetworkConnected(mContext)) {
            FeedbackSystemRequest feedbackSystemRequest = new FeedbackSystemRequest();
            feedbackSystemRequest.setSiteId(AppConstants.SITE_ID);
            feedbackSystemRequest.setTerminalId(AppConstants.TERMINAL_ID);
            feedbackSystemRequest.setISFeedback(0);
            feedbackSystemRequest.setFeedbackRate("0");
            ApiInterface apiInterface = ApiClient.getApiService();
            Call<FeedbackSystemResponse> call = apiInterface.FEEDBACK_SYSTEM_API_CALL(feedbackSystemRequest);
            call.enqueue(new Callback<FeedbackSystemResponse>() {
                @Override
                public void onResponse(@NotNull Call<FeedbackSystemResponse> call, @NotNull Response<FeedbackSystemResponse> response) {
                    CommonUtils.hideDialog();
                    if (response.isSuccessful() && response.code() == 200) {
                        if (mCallback != null) {
                            mCallback.onSuccessFeedbackSystemApiCall(response.body());
                        }
                    }
                }

                @Override
                public void onFailure(@NotNull Call<FeedbackSystemResponse> call, @NotNull Throwable t) {
                    CommonUtils.hideDialog();
                    if (mCallback != null) {
                        mCallback.onFailureMessage(t.getMessage());
                    }
                }
            });
        } else {
            if (mCallback != null) {
                mCallback.onFailureMessage("Something went wrong.");
            }
        }
    }
}

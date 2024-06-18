package com.thresholdsoft.apollofeedback.ui.itemspayment;

import android.content.Context;

import com.thresholdsoft.apollofeedback.commonmodels.FeedbackSystemRequest;
import com.thresholdsoft.apollofeedback.commonmodels.FeedbackSystemResponse;
import com.thresholdsoft.apollofeedback.db.SessionManager;
import com.thresholdsoft.apollofeedback.network.ApiClient;
import com.thresholdsoft.apollofeedback.network.ApiInterface;
import com.thresholdsoft.apollofeedback.ui.itemspayment.model.CrossShellRequest;
import com.thresholdsoft.apollofeedback.ui.itemspayment.model.CrossShellResponse;
import com.thresholdsoft.apollofeedback.ui.itemspayment.model.GetAdvertisementResponse;
import com.thresholdsoft.apollofeedback.utils.CommonUtils;
import com.thresholdsoft.apollofeedback.utils.NetworkUtils;

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
            CommonUtils.showDialog(mContext, "Please wait...");

            ApiInterface apiInterface = ApiClient.getApiService(new SessionManager(mContext).getEposUrl());
            Call<GetAdvertisementResponse> call = apiInterface.GET_ADVERTISEMENT_API_CALL();
            call.enqueue(new Callback<GetAdvertisementResponse>() {
                @Override
                public void onResponse(Call<GetAdvertisementResponse> call, Response<GetAdvertisementResponse> response) {
                    if (response.isSuccessful() && response.code() == 200) {
                        mCallback.onSuccessGetAdvertisementApi(response.body());
                    }
                    CommonUtils.hideDialog();
                }

                @Override
                public void onFailure(Call<GetAdvertisementResponse> call, Throwable t) {
                    CommonUtils.hideDialog();
                    mCallback.onFailureMessage(t.getMessage());
                }
            });
        } else {
            mCallback.onFailureMessage("Something went wrong.");
        }
    }


    public void crossshellApiCall(String mobileNumber) {
        CrossShellRequest crossShellRequest = new CrossShellRequest();
        crossShellRequest.setStoreId(new SessionManager(mContext).getSiteId());
        crossShellRequest.setMobileno(mobileNumber);
        ApiInterface apiInterface = ApiClient.getApiService(new SessionManager(mContext).getEposUrl());
        Call<CrossShellResponse> call = apiInterface.Get_CROSSSHELL_API(crossShellRequest);
        call.enqueue(new Callback<CrossShellResponse>() {
            @Override
            public void onResponse(Call<CrossShellResponse> call, Response<CrossShellResponse> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    if (response.body() != null && response.body().getRequestStatus() == 0) {
                        if (mCallback != null) {
                            mCallback.onSucessCrossShell(response.body());
                        }
                    } else if (response.body() != null && response.body().getRequestStatus() == 1) {
                        if (mCallback != null) {
                            mCallback.onFailureMessage(response.body().getReturnMessage());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<CrossShellResponse> call, Throwable t) {
                if (mCallback != null) {
                    mCallback.onFailureMessage(t.getMessage());
                }
            }
        });


    }


    public void feedbakSystemApiCall() {
        if (NetworkUtils.isNetworkConnected(mContext)) {
            FeedbackSystemRequest feedbackSystemRequest = new FeedbackSystemRequest();
            feedbackSystemRequest.setSiteId(new SessionManager(mContext).getSiteId());
            feedbackSystemRequest.setTerminalId(new SessionManager(mContext).getTerminalId());
            feedbackSystemRequest.setISFeedback(0);
            feedbackSystemRequest.setFeedbackRate("0");
            ApiInterface apiInterface = ApiClient.getApiService(new SessionManager(mContext).getEposUrl());
            Call<FeedbackSystemResponse> call = apiInterface.FEEDBACK_SYSTEM_API_CALL();
//            Call<FeedbackSystemResponse> call = apiInterface.FEEDBACK_SYSTEM_API_CALL(feedbackSystemRequest);
            call.enqueue(new Callback<FeedbackSystemResponse>() {
                @Override
                public void onResponse(Call<FeedbackSystemResponse> call, Response<FeedbackSystemResponse> response) {
                    CommonUtils.hideDialog();
                    if (response.isSuccessful() && response.code() == 200) {
                        if (mCallback != null) {
                            mCallback.onSuccessFeedbackSystemApiCall(response.body());
                        }
                    }
                }

                @Override
                public void onFailure(Call<FeedbackSystemResponse> call, Throwable t) {
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
